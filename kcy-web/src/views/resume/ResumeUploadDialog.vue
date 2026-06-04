<template>
    <el-dialog
    v-model="dialogVisible"
    title="批量上传附件简历"
    width="580px"
    destroy-on-close
    :close-on-click-modal="false"
    >
    <div class="upload-trigger-zone">
        <div class="zone-left">
        <p class="hint-text">支持批量多选导入，格式支持 .pdf, .doc, .docx (单文件最大 5MB)</p>
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
        选择简历文件 (支持多选)
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
        <p>也可以将文件夹中的多份简历直接拖拽到此区域暂存</p>
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
                <el-icon class="is-loading"><Loading /></el-icon> 上传解析中...
            </span>
            <span v-else-if="item.status === 'success'" class="status-txt success">
                <el-icon><CircleCheck /></el-icon> 成功
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
            已选 <strong>{{ localFiles.length }}</strong> 份简历
        </span>
        <el-button :disabled="isBatchUploading" @click="dialogVisible = false">取消</el-button>
        <el-button
            type="success"
            :loading="isBatchUploading"
            :disabled="localFiles.length === 0 || isAllSuccess"
            @click="startRealUploadAndParse"
        >
            开始上传解析
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
import { ResumeApi } from '@/api/resume/index' // 简历业务 API

// 定义内部暂存文件模型
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

// 计算属性：是否列表中所有文件都已经成功上传入库
const isAllSuccess = computed(() => {
    return localFiles.value.length > 0 && localFiles.value.every(f => f.status === 'success')
})

// 🌟 被父页面通过 ref 调用拉起
const open = () => {
    dialogVisible.value = true
    localFiles.value = []
    isBatchUploading.value = false
}

// 唤醒隐藏的系统原生多选文件框
const triggerChooseFile = () => {
    if (fileInputRef.value) {
    fileInputRef.value.click()
    }
}

// 核心文件过滤与暂存机制
const filterAndStageFiles = (files: FileList) => {
    for (let i = 0; i < files.length; i++) {
    const file = files[i]
    
    // 限制单张简历大小最大 5MB
    if (file.size / 1024 / 1024 > 5) {
        ElMessage.error(`文件 [${file.name}] 超过 5MB，已自动略过`)
        continue
    }

    // 防止暂存列表里出现同名同大小的重复文件
    const isDuplicate = localFiles.value.some(
        f => f.rawFile.name === file.name && f.rawFile.size === file.size
    )
    if (isDuplicate) continue

    // 🌟 仅仅推入暂存数组，状态设为 ready，绝不触发任何网络请求
    localFiles.value.push({
        rawFile: file,
        status: 'ready'
    })
    }
}

// 捕获点击多选后的文件
const handleFileChoose = (e: Event) => {
    const target = e.target as HTMLInputElement
    if (target.files && target.files.length > 0) {
    filterAndStageFiles(target.files)
    }
    if (target) target.value = '' // 清空以支持连续选相同文件
}

// 捕获从外部拖拽放开后的文件
const handleFileDrop = (e: DragEvent) => {
    isDragOver.value = false
    if (e.dataTransfer && e.dataTransfer.files.length > 0) {
    filterAndStageFiles(e.dataTransfer.files)
    }
}

// 从暂存列表中移除
const removeFile = (index: number) => {
    localFiles.value.splice(index, 1)
}

// 格式化文件大小展示
const formatSize = (bytes: number) => {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 🌟 🌟 🌟 修改后的 startRealUploadAndParse 方法
const startRealUploadAndParse = async () => {
    const uploadTargets = localFiles.value.filter(f => f.status === 'ready' || f.status === 'error')
    if (uploadTargets.length === 0) return

    isBatchUploading.value = true
    let successCounter = 0

    await Promise.all(
        uploadTargets.map(async (item) => {
        item.status = 'uploading'
        try {
            // 🚀 1. 上传 OSS
            const fileRes = await InfraFileApi.uploadFile(item.rawFile)
            
            // 通常 uploadFile 返回的是原始封装或者直接返回 URL 字符串
            // 这里兼容两种情况：fileRes 本身是字符串，或者 fileRes.data 是字符串
            const ossFilePath = typeof fileRes === 'string' ? fileRes : fileRes?.data
            
            if (ossFilePath) {
            const fileExtension = item.rawFile.name.split('.').pop() || ''

            const resumeForm = {
                fileName: item.rawFile.name,       
                filePath: ossFilePath,              
                fileType: fileExtension,           
                fileSize: item.rawFile.size,       
                parseStatus: 0                     
            }

            // 🚀 2. 调用创建简历接口
            const dbRes = await ResumeApi.createResume(resumeForm)
            
            // 💡 核心修正：兼容芋道两种返回格式
            // 情况A：框架自动解包了，dbRes 直接就是生成的 ID 数字 (例如: 2)
            // 情况B：未解包，包含标准的封装对象形式 (dbRes.code === 0)
            if (
                (typeof dbRes === 'number' && dbRes > 0) || 
                (dbRes && (dbRes.code === 0 || dbRes.data))
            ) {
                item.status = 'success' // 完美标记成功！
                successCounter++
            } else {
                item.status = 'error'
                console.error(`[${item.rawFile.name}] 后端返回格式不匹配或落库失败:`, dbRes)
            }

            } else {
            item.status = 'error'
            console.error(`[${item.rawFile.name}] 未获取到有效的 OSS 路径`)
            }
        } catch (error) {
            console.error(`[${item.rawFile.name}] 流程异常:`, error)
            item.status = 'error'
        }
        })
    )

    isBatchUploading.value = false

    // 🏁 最终检查批量成果
    if (successCounter === uploadTargets.length) {
        ElMessage.success('所有简历已成功上传并生成档案！')
        emit('upload-success') 
        setTimeout(() => {
        dialogVisible.value = false
        }, 1000)
    } else {
        ElMessage.warning(`批量处理完毕：成功 ${successCounter} 份，失败 ${uploadTargets.length - successCounter} 份`)
    }
}

// 向外暴露唯一控制打开的 open 方法
defineExpose({ open })
</script>

<style scoped>
/* 头部操作栏 */
.upload-trigger-zone { display: flex; justify-content: space-between; align-items: center; background: #f8fafc; border: 1px solid #e2e8f0; padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; }
.hint-text { margin: 0; font-size: 12px; color: #64748b; line-height: 1.4; }

/* 拖拽及暂存视窗大框 */
.drop-box-area { border: 2px dashed #cbd5e1; border-radius: 10px; background: #fff; min-height: 240px; max-height: 340px; overflow-y: auto; transition: all 0.2s ease; position: relative; padding: 12px; }
.drop-box-area.is-dragover { border-color: #2563eb; background: #f0f7ff; }

/* 缺省占位图 */
.empty-drop-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 220px; color: #94a3b8; }
.cloud-icon { font-size: 40px; margin-bottom: 8px; color: #cbd5e1; }
.empty-drop-placeholder p { font-size: 13px; margin: 0; }

/* 暂存列表列表项 */
.staged-file-list { display: flex; flex-direction: column; gap: 8px; }
.staged-file-item { display: flex; justify-content: space-between; align-items: center; background: #f8fafc; border: 1px solid #e2e8f0; padding: 10px 14px; border-radius: 6px; }
.staged-file-item:hover { background: #f1f5f9; }

.file-main-info { display: flex; align-items: center; font-size: 13px; color: #1e293b; max-width: 75%; }
.doc-icon { color: #64748b; margin-right: 8px; font-size: 18px; flex-shrink: 0; }
.file-name { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-weight: 500; margin-right: 8px; }
.file-size { color: #94a3b8; font-size: 12px; flex-shrink: 0; }

/* 状态徽章 */
.status-txt { font-size: 12px; display: flex; align-items: center; gap: 4px; font-weight: 500; }
.status-txt.uploading { color: #2563eb; }
.status-txt.success { color: #10b981; }
.status-txt.error { color: #ef4444; }

/* 底部底座布局 */
.dialog-footer { display: flex; align-items: center; justify-content: flex-end; width: 100%; }
.left-count { margin-right: auto; font-size: 13px; color: #475569; }
.left-count strong { color: #2563eb; margin: 0 2px; }
</style>