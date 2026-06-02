<template>
    <div class="resume-manager-page">
    <div class="page-header">
        <div class="welcome-info">
        <h2>我的简历管理</h2>
        <p>用心打磨每一份简历，大厂 Offer 近在咫尺</p>
        </div>
        <div class="action-buttons">
        <el-upload
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            @change="handleUploadSuccess"
        >
            <el-button type="info" plain :icon="Upload">上传附件简历</el-button>
        </el-upload>
        <el-button type="primary" :icon="Plus" @click="createNewResume">创建在线简历</el-button>
        </div>
    </div>

    <div class="resume-stats">
        <div class="stat-item">
        <span class="num">{{ myResumes.length }}</span>
        <span class="label">简历总数</span>
        </div>
        <div class="stat-item">
        <span class="numColor">{{投递总数}}</span>
        <span class="label">累计投递次</span>
        </div>
    </div>

    <div class="resume-list-container">
        <h3 class="sub-title">我的在线简历</h3>
        
        <div class="resume-list">
        <div 
            class="resume-box-card" 
            v-for="resume in myResumes" 
            :key="resume.id"
            @click="toDetail(resume.id)"
        >
            <div class="card-body">
            <div class="title-row">
                <div class="title-left">
                <h4>{{ resume.title }}</h4>
                <el-tag v-if="resume.isDefault" size="small" type="success" effect="dark">默认投递</el-tag>
                </div>
                <span class="update-time">更新于：{{ resume.updateTime }}</span>
            </div>

            <div class="tech-stack-preview">
                <span class="preview-label">技术栈聚焦：</span>
                <div class="tags-wrapper">
                <span v-for="tag in resume.targetTags" :key="tag" class="mini-tech-tag">{{ tag }}</span>
                </div>
            </div>

            <div class="resume-summary">
                <span class="summary-label">核心项目：</span>
                <p class="summary-text">{{ resume.projectHighlight }}</p>
            </div>
            </div>

            <div class="card-footer" @click.stop>
            <div class="integrity-info">
                <span>简历完整度：</span>
                <el-progress :percentage="resume.integrity" :status="resume.integrity === 100 ? 'success' : ''" :stroke-width="6" style="width: 120px;" />
            </div>
            <div class="footer-actions">
                <el-button size="small" type="primary" link :icon="View" @click="toDetail(resume.id)">查看详情</el-button>
                <el-button size="small" type="primary" link :icon="Edit" @click="toEdit(resume.id)">编辑</el-button>
                <el-dropdown trigger="click" @command="(cmd: string) => handleCommand(cmd, resume.id)">
                <el-button size="small" type="info" link :icon="MoreFilled"></el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                    <el-dropdown-menu-item command="setDefault" :disabled="resume.isDefault">设为默认简历</el-dropdown-menu-item>
                    <el-dropdown-menu-item command="delete" sorted style="color: #f43f5e;">删除简历</el-dropdown-menu-item>
                    </el-dropdown-menu>
                </template>
                </el-dropdown>
            </div>
            </div>
        </div>
        </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Upload, View, Edit, MoreFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const 投递总数 = ref(24) // 模拟投递计数

// 模拟用户自己创建的“多方向”个性化简历列表
const myResumes = ref([
    {
    id: 1,
    title: '黄胜-Java全栈开发工程师简历',
    isDefault: true,
    updateTime: '2026-06-01',
    integrity: 100,
    targetTags: ['Spring Boot', 'Vue3', 'MyBatis-Plus', 'MySQL', 'UniApp'],
    projectHighlight: '包含《乐行旅途Mini-Program》及《凌云智控物联网平台》完整全栈研发经历。深度整合持久层架构，攻克高并发列表秒级渲染痛点。'
    },
    {
    id: 2,
    title: '黄胜-前端高级开发（移动跨端方向）',
    isDefault: false,
    updateTime: '2026-05-20',
    integrity: 85,
    targetTags: ['Vue3', 'TypeScript', 'UniApp', 'ECharts', 'Element Plus'],
    projectHighlight: '侧重于跨端多设备多终端组网混编开发。深度打磨复杂工业控大数据可视化大屏系统，极致缩减前端 bundle 渲染包体积。'
    }
])

// 路由跳转至简历详情页
const toDetail = (id: number) => {
    router.push(`/resume/${id}`)
}

// 路由跳转至编辑页
const toEdit = (id: number) => {
    router.push(`/resume/edit?id=${id}`)
}

const createNewResume = () => {
    ElMessage.success('正在初始化新在线简历模版...')
    router.push('/resume/edit')
}

const handleUploadSuccess = (file: any) => {
    ElMessage.success(`读取附件简历 [${file.name}] 成功，正在解析语义并导入...`)
}

// 更多菜单交互
const handleCommand = (command: string, id: number) => {
    const resume = myResumes.value.find(r => r.id === id)
    if (!resume) return

    if (command === 'setDefault') {
    myResumes.value.forEach(r => r.isDefault = false)
    resume.isDefault = true
    ElMessage.success(`已将 [${resume.title}] 设为默认投递简历`)
    } else if (command === 'delete') {
    ElMessageBox.confirm('简历删除后将无法恢复，确认删除吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        myResumes.value = myResumes.value.filter(r => r.id !== id)
        ElMessage.success('简历删除成功')
    }).catch(() => {})
    }
}
</script>

<style scoped>
.resume-manager-page { max-width: 1000px; margin: 88px auto 40px; padding: 0 20px; }

/* 头部样式 */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.welcome-info h2 { margin: 0 0 6px; font-size: 24px; color: #1e293b; }
.welcome-info p { margin: 0; font-size: 14px; color: #64748b; }
.action-buttons { display: flex; gap: 12px; }

/* 状态看板 */
.resume-stats { display: flex; gap: 24px; margin-bottom: 30px; }
.stat-item { background: #fff; border: 1px solid #eef0f5; padding: 16px 24px; border-radius: 12px; display: flex; flex-direction: column; min-width: 120px; }
.stat-item .num { font-size: 24px; font-weight: 700; color: #2563eb; }
.stat-item .numColor { font-size: 24px; font-weight: 700; color: #059669; }
.stat-item .label { font-size: 12px; color: #64748b; margin-top: 4px; }

/* 列表容器 */
.sub-title { font-size: 16px; color: #1e293b; margin-bottom: 16px; }
.resume-list { display: flex; flex-direction: column; gap: 16px; }

/* 简历专属盒子卡片 */
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

.resume-summary { background: #f8fafc; padding: 12px; border-radius: 8px; font-size: 12px; line-height: 1.6; }
.summary-label { color: #64748b; font-weight: 600; display: block; margin-bottom: 4px; }
.summary-text { margin: 0; color: #475569; }

/* 卡片尾部 */
.card-footer { border-top: 1px solid #f1f5f9; padding: 12px 20px; display: flex; justify-content: space-between; align-items: center; background: #fafbfc; border-radius: 0 0 12px 12px; }
.integrity-info { display: flex; align-items: center; gap: 8px; font-size: 12px; color: #64748b; }
.footer-actions { display: flex; align-items: center; gap: 16px; }
</style>