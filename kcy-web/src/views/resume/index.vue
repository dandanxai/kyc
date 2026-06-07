<template>
    <div class="resume-manager-page">
    <div class="page-header">
        <div class="welcome-info">
        <h2>我的简历管理</h2>
        <p>用心打磨每一份简历，大厂 Offer 近在咫尺</p>
        </div>
        <div class="action-buttons">
        <el-button type="info" plain :icon="Upload" @click="openUploadDialog">
            上传附件简历
        </el-button>
        <el-button type="primary" :icon="Plus" @click="createNewResume">
            创建在线简历
        </el-button>
        </div>
    </div>

    <div class="resume-stats">
        <div class="stat-item">
        <span class="num">{{ totalCount }}</span>
        <span class="label">简历总数</span>
        </div>
        <div class="stat-item">
        <span class="numColor">{{ 投递总数 }}</span>
        <span class="label">累计投递次</span>
        </div>
    </div>

    <div class="resume-list-container">
        <h3 class="sub-title">我的在线简历</h3>

        <div class="resume-list">
        <div 
            class="resume-box-card" 
            v-for="resume in sortedResumes" 
            :key="resume.id"
            @click="toDetail(resume.id)"
        >
            <div class="card-body">
            <div class="title-row">
                <div class="title-left">
                    <h4>{{ resume.fileName.replace(/\.(pdf|docx|doc|xlsx|xls)$/i, '') }}</h4>     
                    <el-tag v-if="resume.isActive === 1" size="small" type="success" effect="dark">默认投递</el-tag>
                </div>
                <span class="update-time">上传于：{{ formatDate(resume.createTime) }}</span>
            </div>

            <div v-if="resume.parseStatus === 0 || resume.parseStatus === 1" class="parsing-skeleton-state">
                <div class="tech-stack-preview">
                <span class="preview-label">技术栈聚焦：</span>
                <div class="parsing-loading-box">
                    <el-icon class="is-loading"><Loading /></el-icon>
                    <span class="loading-text">大模型正在深度提取核心技能点...</span>
                </div>
                </div>
                <div class="resume-summary italic-style">
                <span class="summary-label">核心项目：</span>
                <p class="summary-text processing-color">AI 正在提炼核心亮点，请稍候...</p>
                </div>
            </div>

            <div v-else-if="resume.parseStatus === 3" class="parsing-error-state">
                <div class="tech-stack-preview">
                <span class="preview-label">技术栈聚焦：</span>
                <span class="error-text">暂无（简历解析异常）</span>
                </div>
                <div class="resume-summary">
                <span class="summary-label">核心项目：</span>
                <p class="summary-text error-text">文档格式可能存在损坏，您可以尝试重新编辑或再次上传。</p>
                </div>
            </div>

            <div v-else>
                <div class="tech-stack-preview">
                <span class="preview-label">技术栈聚焦：</span>
                <div class="tags-wrapper">
                    <template v-if="resume.skillTags && resume.skillTags.length > 0">
                    <span v-for="tag in resume.skillTags" :key="tag" class="mini-tech-tag">{{ tag }}</span>
                    </template>
                    <span v-else class="empty-placeholder-text">暂无提取到技能标签</span>
                </div>
                </div>
                <div class="resume-summary">
                <span class="summary-label">核心项目：</span>
                <p class="summary-text">{{ resume.achievements || '未提取到核心项目亮点描述。' }}</p>
                </div>
            </div>
            </div>

            <div class="card-footer" @click.stop>
            <div class="status-badge-zone">
                <el-tag :type="getStatusTagType(resume.parseStatus)" size="small">
                {{ getStatusText(resume.parseStatus) }}
                </el-tag>
            </div>
            <div class="footer-actions">
                <el-button size="small" type="primary" link :icon="View" @click="toDetail(resume.id)">查看详情</el-button>
                <el-button size="small" type="primary" link :icon="Edit" @click="toEdit(resume.id)">编辑</el-button>
                <el-dropdown trigger="click" @command="(cmd: string) => handleCommand(cmd, resume)">
                <el-button size="small" type="info" link :icon="MoreFilled"></el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                    <el-dropdown-item command="setDefault" :disabled="resume.isActive === 1">设为默认简历</el-dropdown-item>
                    <el-dropdown-item command="delete" style="color: #f43f5e;">删除简历</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
                </el-dropdown>
            </div>
            </div>
        </div>
        </div>

        <div class="scroll-bottom-status">
        <p v-if="isLoading" class="status-tip">
            <el-icon class="is-loading"><Loading /></el-icon> 正在加载下一页简历数据...
        </p>
        <p v-else-if="isNoMore" class="status-tip finished-tip">
            已加载全部简历数据，大厂 Offer 正在向你招手！
        </p>
        </div>
    </div>

    <ResumeUploadDialog ref="resumeUploadDialogRef" @upload-success="refreshResumeList" />
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Upload, View, Edit, MoreFilled, Loading } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import dayjs from 'dayjs'
import ResumeUploadDialog from './ResumeUploadDialog.vue'
import { ResumeApi } from '@/api/resume/index'

import { useUserStore } from '@/store/modules/user'
const userStore = useUserStore()

const router = useRouter()
const resumeUploadDialogRef = ref<InstanceType<typeof ResumeUploadDialog> | null>(null)

// 业务变量
const myResumes = ref<any[]>([])
const totalCount = ref(0)
const 投递总数 = ref(24)

// 分页控制
const currentPage = ref(1)
const pageSize = ref(5) 
const isLoading = ref(false)
const isNoMore = ref(false)

// 🌟 SSE 长连接实例对象
let sseSource: EventSource | null = null

// 核心初始化：建立长连接通道
const initResumeSse = () => {
    
    if (sseSource) sseSource.close()
    
    // 🚀 一剑穿透：连上咱们在 Java 里写好的 SSE 管道
    sseSource = new EventSource(`http://127.0.0.1:48080/app-api/member/sse/connect?userId=${userStore.userInfo?.id}`)

    // 监听 Python -> Java 转发过来的大模型解析成功喜报
    sseSource.addEventListener('RESUME_PARSE_SUCCESS', (event) => {
        const parsedId = Number(event.data)
        
        // 1. 右上角弹出高大上的智能通报
        ElNotification({
            title: 'AI 全息档案构建成功',
            message: '大模型已成功榨取文本并完成落库，正在为您局部刷新视图...',
            type: 'success',
            duration: 3500
        })

        // 2. 🌟 极速局部高能刷新：直接在前端遍历当前列表，把正在转圈的状态秒变成功
        const targetResume = myResumes.value.find(r => r.id === parsedId)
        if (targetResume) {
            // 原地改变状态，骨架屏瞬间消失，真实数据惊艳亮相
            targetResume.parseStatus = 2
        }
        
        // 3. 稳妥起见，静默同步一次当前页的数据（不破坏用户的滚动位置）
        silentRefreshCurrentPage()
    })
}

// 静默同步当前页数据（无感知刷新）
const silentRefreshCurrentPage = async () => {
    try {
        const res = await ResumeApi.getResumePage({ pageNo: 1, pageSize: myResumes.value.length })
        const resData = (res && res.list !== undefined) ? res : res?.data
        if (resData && Array.isArray(resData.list)) {
            // 精准覆盖现有列表里的最新大模型标签
            myResumes.value = resData.list
            totalCount.value = resData.total
        }
    } catch (e) {
        console.error('静默同步异常:', e)
    }
}

// 🌟 原生触底监听
const handleWindowScroll = () => {
    if (isLoading.value || isNoMore.value) return
    const clientHeight = document.documentElement.clientHeight
    const scrollHeight = document.documentElement.scrollHeight
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
    if (scrollHeight - (scrollTop + clientHeight) < 50) {
        loadMoreResume()
    }
}

// 适配后端数据结构（含1000ms丝滑等待）
const loadMoreResume = async () => {
    if (isLoading.value || isNoMore.value) return
    isLoading.value = true
    const minLoadingPromise = new Promise(resolve => setTimeout(resolve, 1000))

    try {
        const queryParams = { pageNo: currentPage.value, pageSize: pageSize.value }
        const [res] = await Promise.all([
            ResumeApi.getResumePage(queryParams),
            minLoadingPromise
        ])
        
        const resData = (res && res.list !== undefined) ? res : res?.data

        if (resData && Array.isArray(resData.list)) {
            totalCount.value = resData.total
            if (resData.list.length > 0) {
                myResumes.value = [...myResumes.value, ...resData.list]
                if (myResumes.value.length >= resData.total) {
                    isNoMore.value = true
                } else {
                    currentPage.value++ 
                }
            } else {
                isNoMore.value = true
            }
        } else {
            if (res && res.code !== undefined && res.code !== 0) {
                ElMessage.error(res.msg || '获取简历列表异常')
            } else {
                isNoMore.value = true
            }
        }
    } catch (err) {
        console.error('拉取分页简历异常:', err)
    } finally {
        isLoading.value = false
    }
}

// 刷新并重置
const refreshResumeList = () => {
    myResumes.value = []
    currentPage.value = 1
    isNoMore.value = false
    isLoading.value = false
    nextTick(() => {
        loadMoreResume()
    })
}

// 🌟 生命周期闭环
onMounted(() => {
    refreshResumeList()
    window.addEventListener('scroll', handleWindowScroll)
    // 🚀 挂载时率先启动 SSE 监听雷达
    initResumeSse()
})

onBeforeUnmount(() => {
    window.removeEventListener('scroll', handleWindowScroll)
    // 🌟 终极护航：卸载时切断长连接，防止内存和连接数暴涨
    if (sseSource) {
        sseSource.close()
    }
})

// 默认简历置顶规则
const sortedResumes = computed(() => {
    return [...myResumes.value].sort((a, b) => {
        if (a.isActive === 1 && b.isActive !== 1) return -1
        if (a.isActive !== 1 && b.isActive === 1) return 1
        return b.id - a.id
    })
})

// 辅助方法
const openUploadDialog = () => { resumeUploadDialogRef.value?.open() }
const getStatusText = (status: number) => { return { 0: '未解析', 1: '解析中', 2: '解析成功', 3: '解析失败' }[status] ?? '未知' }
const getStatusTagType = (status: number) => { return { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[status] ?? 'info' }
const formatDate = (dateStr: any) => { return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm') : '未知时间' }
const toDetail = (id: number) => { router.push(`/resume/${id}`) }
const toEdit = (id: number) => { router.push(`/resume/edit?id=${id}`) }
const createNewResume = () => { router.push('/resume/edit') }

const handleCommand = async (command: string, resume: any) => {
    if (command === 'setDefault') {
        const res = await ResumeApi.updateResumeActive(resume.id)
        if (res && (res.code === 0 || res === true)) {
            const msg = resume.isActive === 1 ? `已取消 [${resume.fileName}] 的默认投递状态` : `已成功设置 [${resume.fileName}] 为默认简历`
            ElMessage.success(msg)
            refreshResumeList()
        }
    } else if (command === 'delete') {
        ElMessageBox.confirm(`确认删除简历 [${resume.fileName}] 吗？`, '警告', { type: 'warning' }).then(async () => {
            const res = await ResumeApi.deleteResume(resume.id)
            if (res && (res.code === 0 || res === true)) {
                ElMessage.success('删除成功')
                refreshResumeList()
            }
        })
    }
}
</script>

<style scoped>
.resume-manager-page { max-width: 1000px; margin: 100px auto; padding: 0 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.welcome-info h2 { margin: 0 0 6px; font-size: 24px; color: #1e293b; }
.welcome-info p { margin: 0; font-size: 14px; color: #64748b; }
.action-buttons { display: flex; gap: 12px; }

.resume-stats { display: flex; gap: 24px; margin-bottom: 30px; }
.stat-item { background: #fff; border: 1px solid #eef0f5; padding: 16px 24px; border-radius: 12px; display: flex; flex-direction: column; min-width: 120px; }
.stat-item .num { font-size: 24px; font-weight: 700; color: #2563eb; }
.stat-item .numColor { font-size: 24px; font-weight: 700; color: #059669; }
.stat-item .label { font-size: 12px; color: #64748b; margin-top: 4px; }

.sub-title { font-size: 16px; color: #1e293b; margin-bottom: 16px; }

/* 🌟 去掉了多余高度与局部滚动，直接自然平铺 */
.resume-list { display: flex; flex-direction: column; gap: 16px; }
.resume-box-card { background: #fff; border-radius: 12px; border: 1px solid #eef0f5; transition: 0.25s; cursor: pointer; display: flex; flex-direction: column; }
.resume-box-card:hover { border-color: #2563eb; box-shadow: 0 6px 16px rgba(37,99,235,0.05); }

.card-body { padding: 20px; }
.title-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.title-left { display: flex; align-items: center; gap: 12px; }
.title-left h4 { margin: 0; font-size: 16px; color: #1e293b; transition: 0.2s; }
.resume-box-card:hover .title-left h4 { color: #2563eb; }
.update-time { font-size: 12px; color: #94a3b8; }

.tech-stack-preview { display: flex; align-items: center; font-size: 13px; margin-bottom: 12px; }
.preview-label { color: #64748b; font-weight: 600; white-space: nowrap; }
.tags-wrapper { display: flex; flex-wrap: wrap; gap: 6px; }
.mini-tech-tag { font-size: 11px; background: #eff6ff; color: #2563eb; padding: 2px 8px; border-radius: 4px; font-weight: 500; }
.empty-placeholder-text { font-size: 12px; color: #94a3b8; font-style: italic; }

.resume-summary { background: #f8fafc; padding: 12px; border-radius: 8px; font-size: 12px; line-height: 1.6; }
.summary-label { color: #64748b; font-weight: 600; display: block; margin-bottom: 4px; }
.summary-text { margin: 0; color: #475569; }

.parsing-loading-box { display: flex; align-items: center; gap: 6px; color: #e6a23c; font-size: 12px; font-weight: 500; }
.processing-color { color: #94a3b8 !important; }
.italic-style { border: 1px dashed #e6a23c; background: #fffdf9; }
.error-text { color: #f56c6c !important; font-style: italic; }

.card-footer { border-top: 1px solid #f1f5f9; padding: 12px 20px; display: flex; justify-content: space-between; align-items: center; background: #fafbfc; border-radius: 0 0 12px 12px; }
.footer-actions { display: flex; align-items: center; gap: 16px; }

.scroll-bottom-status { padding: 24px 0 40px; text-align: center; }
.status-tip { font-size: 13px; color: #64748b; display: flex; align-items: center; justify-content: center; gap: 6px; }
.finished-tip { color: #94a3b8; font-weight: 500; }
</style>