<template>
<el-dialog
    v-model="dialogVisible"
    title="智能导入岗位需求文档"
    width="580px"
    destroy-on-close
    :close-on-click-modal="false"
>
    <div class="upload-trigger-zone">
    <div class="zone-left">
        <p class="hint-text">支持批量导入岗位描述，格式支持 .pdf, .doc, .docx (单文件最大 5MB)</p>
    </div>
    <input
        ref="fileInputRef"
        type="file"
        multiple
        accept=".pdf,.doc,.docx"
        style="display: none"
        @change="handleFileChoose"
    />
    <el-button type="primary" plain @click="triggerChooseFile">
        选择岗位文件 (支持多选)
    </el-button>
    </div>

    <div 
    class="drop-box-area"
    :class="{ 'is-dragover': isDragOver }"
    @dragover.prevent="isDragOver = true"
    @dragleave.prevent="isDragOver = false"
    @drop.prevent="handleFileDrop"
    >
    <div v-if="localFiles.length === 0" class="empty-drop-placeholder">
        <el-icon class="cloud-icon"><UploadFilled /></el-icon>
        <p>可将文件夹中的多份岗位JD直接拖拽到此区域暂存</p>
    </div>

    <div v-else class="staged-file-list">
        <div v-for="(item, index) in localFiles" :key="index" class="staged-file-item">
        <div class="file-main-info">
            <el-icon class="doc-icon"><Document /></el-icon>
            <span class="file-name" :title="item.rawFile.name">{{ item.rawFile.name }}</span>
            <span class="file-size">({{ formatSize(item.rawFile.size) }})</span>
        </div>

        <div class="file-action-status">
            <el-button 
            v-if="item.status === 'ready'" 
            type="danger" 
            link 
            :icon="Delete" 
            @click="removeFile(index)"
            />
            <span v-else-if="item.status === 'uploading'" class="status-txt uploading">
            <el-icon class="is-loading"><Loading /></el-icon> 智能解构中...
            </span>
            <span v-else-if="item.status === 'success'" class="status-txt success">
            <el-icon><CircleCheck /></el-icon> 已压盘
            </span>
            <span v-else-if="item.status === 'error'" class="status-txt error">
            <el-icon><CircleClose /></el-icon> 失败
            </span>
        </div>
        </div>
    </div>
    </div>

    <template #footer>
    <span class="dialog-footer">
        <span class="left-count" v-if="localFiles.length > 0">
        已选 <strong>{{ localFiles.length }}</strong> 份需求文档
        </span>
        <el-button :disabled="isBatchUploading" @click="dialogVisible = false">取消</el-button>
        <el-button
        type="success"
        :loading="isBatchUploading"
        :disabled="localFiles.length === 0 || isAllSuccess"
        @click="startRealUploadAndParse"
        >
        开始智能解析
        </el-button>
    </span>
    </template>
</el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Document, Delete, CircleCheck, CircleClose, Loading } from '@element-plus/icons-vue'
import { InfraFileApi } from '@/api/file' // 基础设施文件上传 API
import { PositionApi } from '@/api/position/index' // 🌟 引入你的岗位业务 API

interface StagedFile {
rawFile: File
status: 'ready' | 'uploading' | 'success' | 'error'
}

const emit = defineEmits(['upload-success'])

const dialogVisible = ref(false)
const isBatchUploading = ref(false)
const isDragOver = ref(false)
const fileInputRef = ref<HTMLInputElement | null>(null)
const localFiles = ref<StagedFile[]>([])

const isAllSuccess = computed(() => {
return localFiles.value.length > 0 && localFiles.value.every(f => f.status === 'success')
})

const open = () => {
dialogVisible.value = true
localFiles.value = []
isBatchUploading.value = false
}

const triggerChooseFile = () => {
if (fileInputRef.value) fileInputRef.value.click()
}

const filterAndStageFiles = (files: FileList) => {
for (let i = 0; i < files.length; i++) {
    const file = files[i]
    if (file.size / 1024 / 1024 > 5) {
    ElMessage.error(`文件 [${file.name}] 超过 5MB，已自动略过`)
    continue
    }
    const isDuplicate = localFiles.value.some(
    f => f.rawFile.name === file.name && f.rawFile.size === file.size
    )
    if (isDuplicate) continue

    localFiles.value.push({ rawFile: file, status: 'ready' })
}
}

const handleFileChoose = (e: Event) => {
const target = e.target as HTMLInputElement
if (target.files && target.files.length > 0) {
    filterAndStageFiles(target.files)
}
if (target) target.value = ''
}

const handleFileDrop = (e: DragEvent) => {
isDragOver.value = false
if (e.dataTransfer && e.dataTransfer.files.length > 0) {
    filterAndStageFiles(e.dataTransfer.files)
}
}

const removeFile = (index: number) => { localFiles.value.splice(index, 1) }

const formatSize = (bytes: number) => {
if (bytes === 0) return '0 B'
const k = 1024
const sizes = ['B', 'KB', 'MB']
const i = Math.floor(Math.log(bytes) / Math.log(k))
return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 🌟 核心高能逻辑：两段式级联创建（OSS -> 压入 Java 岗位 MQ 队列）
const startRealUploadAndParse = async () => {
const uploadTargets = localFiles.value.filter(f => f.status === 'ready' || f.status === 'error')
if (uploadTargets.length === 0) return

isBatchUploading.value = true
let successCounter = 0

await Promise.all(
    uploadTargets.map(async (item) => {
    item.status = 'uploading'
    try {
        // 🚀 1. 上传物理文件到 OSS 获得下载 URL
        const fileRes = await InfraFileApi.uploadFile(item.rawFile)
        const ossFilePath = typeof fileRes === 'string' ? fileRes : fileRes?.data
        
        if (ossFilePath) {
        const fileExtension = item.rawFile.name.split('.').pop() || ''

        // 🌟 对齐刚刚为你在 Java 补齐的物理文件 VO 模型字段
        const positionForm = {
            fileName: item.rawFile.name,       
            filePath: ossFilePath,              
            fileType: fileExtension,           
            fileSize: item.rawFile.size,
        }

        // 🚀 2. 调 Java 业务层：插入达梦库并异步轰向 RabbitMQ
        const dbRes = await PositionApi.createPosition(positionForm)
        
        if (
            (typeof dbRes === 'number' && dbRes > 0) || 
            (dbRes && (dbRes.code === 0 || dbRes.data))
        ) {
            item.status = 'success' 
            successCounter++
        } else {
            item.status = 'error'
        }
        } else {
        item.status = 'error'
        }
    } catch (error) {
        console.error(`岗位 [${item.rawFile.name}] 上传解析流程流产:`, error)
        item.status = 'error'
    }
    })
)

isBatchUploading.value = false

if (successCounter === uploadTargets.length) {
    ElMessage.success('所有岗位描述已成功入库并进入 AI 深度提取队列！')
    emit('upload-success') 
    setTimeout(() => { dialogVisible.value = false }, 1000)
} else {
    ElMessage.warning(`批量处理完毕：成功 ${successCounter} 个，失败 ${uploadTargets.length - successCounter} 个`)
}
}

defineExpose({ open })
</script>

<style scoped>
.upload-trigger-zone { display: flex; justify-content: space-between; align-items: center; background: #f8fafc; border: 1px solid #e2e8f0; padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; }
.hint-text { margin: 0; font-size: 12px; color: #64748b; line-height: 1.4; }
.drop-box-area { border: 2px dashed #cbd5e1; border-radius: 10px; background: #fff; min-height: 240px; max-height: 340px; overflow-y: auto; transition: all 0.2s ease; position: relative; padding: 12px; }
.drop-box-area.is-dragover { border-color: #2563eb; background: #f0f7ff; }
.empty-drop-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 220px; color: #94a3b8; }
.cloud-icon { font-size: 40px; margin-bottom: 8px; color: #cbd5e1; }
.empty-drop-placeholder p { font-size: 13px; margin: 0; }
.staged-file-list { display: flex; flex-direction: column; gap: 8px; }
.staged-file-item { display: flex; justify-content: space-between; align-items: center; background: #f8fafc; border: 1px solid #e2e8f0; padding: 10px 14px; border-radius: 6px; }
.staged-file-item:hover { background: #f1f5f9; }
.file-main-info { display: flex; align-items: center; font-size: 13px; color: #1e293b; max-width: 75%; }
.doc-icon { color: #64748b; margin-right: 8px; font-size: 18px; flex-shrink: 0; }
.file-name { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-weight: 500; margin-right: 8px; }
.file-size { color: #94a3b8; font-size: 12px; flex-shrink: 0; }
.status-txt { font-size: 12px; display: flex; align-items: center; gap: 4px; font-weight: 500; }
.status-txt.uploading { color: #2563eb; }
.status-txt.success { color: #10b981; }
.status-txt.error { color: #ef4444; }
.dialog-footer { display: flex; align-items: center; justify-content: flex-end; width: 100%; }
.left-count { margin-right: auto; font-size: 13px; color: #475569; }
.left-count strong { color: #2563eb; margin: 0 2px; }
</style>