<template>
    <div class="resume-detail-page" v-loading="loading">
    <!-- 顶部控制悬浮栏 -->
    <div class="detail-action-bar">
        <div class="left-title">
        <el-button :icon="Back" circle @click="goBack"></el-button>
        <span class="resume-name">{{ formatFileName(resumeData.fileName) }}</span>
        </div>
        <div class="right-ops">
        <el-button type="info" plain :icon="Download" @click="exportPDF">导出 PDF</el-button>
        <el-button type="primary" :icon="Edit" @click="toEdit">编辑此简历</el-button>
        </div>
    </div>

    <!-- 主体两栏布局 -->
    <div class="detail-layout">
        <!-- ================= 左侧：全息简历核心内容 ================= -->
        <div class="layout-left">
        
        <!-- 1. 个人名片 -->
        <div class="detail-card hero-block">
            <div class="hero-main">
            <div class="hero-text">
                <div class="name-row">
                <h2>{{ resumeData.name || '未解析出姓名' }}</h2>
                <span class="status-tag">积极找工作</span>
                </div>
                <!-- 处理 expectedPosition 可能是数组或者字符串的情况 -->
                <p class="job-title">{{ formatArray(resumeData.expectedPosition) || '暂无期望岗位' }}</p>
                <div class="meta-grid">
                <span>
                    <el-icon><User /></el-icon> 
                    {{ resumeData.age ? resumeData.age + '岁' : '年龄未知' }} · 
                    {{ resumeData.yearsOfExperience ? resumeData.yearsOfExperience + '年经验' : '经验未知' }}
                </span>
                <span>
                    <el-icon><Location /></el-icon> 
                    {{ resumeData.currentCity || resumeData.expectedCity || '未知城市' }}
                </span>
                <span>
                    <el-icon><Document /></el-icon> 
                    {{ resumeData.highestEducation || '未知学历' }} · {{ resumeData.major || '未知专业' }}
                </span>
                </div>
            </div>
            </div>
        </div>

        <!-- 2. 🌟 新增：综合优势与核心成就 (对接 selfEvaluation 和 achievements) -->
        <div class="detail-card" v-if="resumeData.selfEvaluation || resumeData.achievements">
            <h3 class="block-title">综合优势与核心成就</h3>
            <div v-if="resumeData.achievements" class="mb-4">
            <h4 class="sub-title">🏆 核心成就</h4>
            <p class="desc-text highlight-text">{{ resumeData.achievements }}</p>
            </div>
            <div v-if="resumeData.selfEvaluation">
            <h4 class="sub-title">💡 自我评价</h4>
            <p class="desc-text">{{ resumeData.selfEvaluation }}</p>
            </div>
        </div>

        <!-- 3. 🌟 强化：全栈技术能级画像 (整合 skillTags 和 coreSkills) -->
        <div class="detail-card chart-block" v-if="(resumeData.coreSkills && resumeData.coreSkills.length) || (resumeData.skillTags && resumeData.skillTags.length)">
            <h3 class="block-title">全栈技术能级画像</h3>
            <div class="chart-content">
            <!-- 左侧：雷达图 -->
            <div ref="radarChartRef" class="radar-canvas"></div>
            <!-- 右侧：真实的核心技能点罗列 -->
            <div class="tech-narrative">
                <div class="proj-tags" style="margin-bottom: 16px;" v-if="resumeData.skillTags">
                <span v-for="tag in resumeData.skillTags" :key="tag" class="tech-tag highlight-tag">{{ tag }}</span>
                </div>
                <ul v-if="resumeData.coreSkills" class="core-skills-list">
                <li v-for="(skill, index) in resumeData.coreSkills" :key="index">{{ skill }}</li>
                </ul>
            </div>
            </div>
        </div>

        <!-- 4. 工作经历 (对接 workExperiences) -->
        <div class="detail-card project-block" v-if="resumeData.workExperiences && resumeData.workExperiences.length">
            <h3 class="block-title">工作经历</h3>
            <div class="project-timeline">
            <div class="project-item" v-for="(work, index) in resumeData.workExperiences" :key="index">
                <div class="proj-header">
                <div class="proj-title-row">
                    <h4>{{ work.company }}</h4>
                    <span class="proj-role">{{ work.position }}</span>
                </div>
                <span class="proj-time">{{ work.startTime }} - {{ work.endTime || '至今' }}</span>
                </div>
                <div class="proj-desc-box">
                <p><strong>工作内容：</strong>{{ work.description }}</p>
                </div>
            </div>
            </div>
        </div>

        <!-- 5. 项目经历 (对接 projectExperiences) -->
        <div class="detail-card project-block" v-if="resumeData.projectExperiences && resumeData.projectExperiences.length">
            <h3 class="block-title">项目经历</h3>
            <div class="project-timeline">
            <div class="project-item" v-for="(proj, index) in resumeData.projectExperiences" :key="index">
                <div class="proj-header">
                <div class="proj-title-row">
                    <h4>{{ proj.name }}</h4>
                    <span class="proj-role" v-if="proj.role">{{ proj.role }}</span>
                </div>
                </div>
                <!-- 技术栈展示 -->
                <div class="proj-tags" v-if="proj.technology">
                <span class="tech-tag tech-stack-tag">技术栈：{{ proj.technology }}</span>
                </div>
                <!-- 项目细节 -->
                <div class="proj-desc-box">
                <p><strong>项目描述与职责：</strong>{{ proj.description }}</p>
                </div>
            </div>
            </div>
        </div>

        <!-- 6. 🌟 新增：荣誉奖项与资格证书 (对接 awards 和 certifications) -->
        <div class="detail-card list-block" v-if="(resumeData.awards && resumeData.awards.length) || (resumeData.certifications && resumeData.certifications.length)">
            <h3 class="block-title">荣誉奖项与资格证书</h3>
            <div class="two-col-list">
            <!-- 奖项 -->
            <div class="list-col" v-if="resumeData.awards && resumeData.awards.length">
                <h4 class="sub-title"><el-icon><Trophy /></el-icon> 荣誉奖项</h4>
                <ul class="bullet-list">
                <li v-for="(award, index) in resumeData.awards" :key="index">{{ award }}</li>
                </ul>
            </div>
            <!-- 证书 -->
            <div class="list-col" v-if="resumeData.certifications && resumeData.certifications.length">
                <h4 class="sub-title"><el-icon><Medal /></el-icon> 资格证书</h4>
                <ul class="bullet-list">
                <li v-for="(cert, index) in resumeData.certifications" :key="index">{{ cert }}</li>
                </ul>
            </div>
            </div>
        </div>

        </div>

        <!-- ================= 右侧：求职意向与联系信道 ================= -->
        <div class="layout-right">
        <!-- 求职意向 -->
        <div class="sidebar-card">
            <h3 class="side-title">期望意向</h3>
            <div class="side-item">
            <span class="label">期望职业</span>
            <span class="val">{{ formatArray(resumeData.expectedPosition) || '面议' }}</span>
            </div>
            <div class="side-item">
            <span class="label">工作城市</span>
            <span class="val">{{ resumeData.expectedCity || '面议' }}</span>
            </div>
            <div class="side-item">
            <span class="label">薪资期望</span>
            <span class="val">{{ formatSalary(resumeData.expectedSalaryMin, resumeData.expectedSalaryMax) }}</span>
            </div>
            <div class="side-item" v-if="resumeData.availability">
            <span class="label">到岗时间</span>
            <span class="val">{{ resumeData.availability }}</span>
            </div>
        </div>

        <!-- 联系信道 -->
        <div class="sidebar-card">
            <h3 class="side-title">联系方式</h3>
            <div class="side-item">
            <span class="label">手机号码</span>
            <span class="val">{{ resumeData.phone || '未填写' }}</span>
            </div>
            <div class="side-item">
            <span class="label">电子邮箱</span>
            <span class="val">{{ resumeData.email || '未填写' }}</span>
            </div>
        </div>
        </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
// 🌟 新引入 Trophy 和 Medal 图标
import { Back, Download, Edit, User, Location, Document, Trophy, Medal } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { ResumeApi } from '@/api/resume/index'

const route = useRoute()
const router = useRouter()
const radarChartRef = ref<HTMLDivElement | null>(null)
let myChart: echarts.ECharts | null = null

const loading = ref(true)
const resumeId = route.params.id || 24
const resumeData = ref<any>({})

// ======================= 工具函数 =======================

const formatFileName = (name: string) => {
    if (!name) return '未知简历文件'
    return name.replace(/\.(pdf|docx|doc|xlsx|xls)$/i, '')
}

const formatSalary = (min: number, max: number) => {
    if (!min && !max) return '薪资面议'
    if (min && !max) return `${min}K 以上`
    if (!min && max) return `${max}K 以下`
    return `${min}K - ${max}K`
}

// 防止某些大模型把 expected_position 吐成数组 ["java", "前端"]
const formatArray = (val: any) => {
    if (!val) return ''
    if (Array.isArray(val)) return val.join(' / ')
    try {
    const arr = JSON.parse(val)
    if (Array.isArray(arr)) return arr.join(' / ')
    } catch(e) {}
    return val
}

// ======================= 网络请求 =======================

const getResume = async () => {
    try {
    loading.value = true
    const res = await ResumeApi.getResume(resumeId)
    console.log("🎉 获取到的全量简历数据：", res)
    if (res) {
        resumeData.value = res
    }
    } catch (error) {
    console.error("💥 简历详情获取失败:", error)
    ElMessage.error("获取简历详情失败，请检查网络或权限")
    } finally {
    loading.value = false
    initRadar()
    }
}

// ======================= 图表渲染 =======================

const initRadar = () => {
    if (!radarChartRef.value) return
    if (myChart) myChart.dispose()
    
    myChart = echarts.init(radarChartRef.value)
    myChart.setOption({
    radar: {
        indicator: [
        { name: '后端开发\n(Java/Spring)', max: 100 },
        { name: '前端开发\n(Vue/跨端)', max: 100 },
        { name: '数据库\n(MySQL/Redis)', max: 100 },
        { name: '架构/部署', max: 100 },
        { name: '竞赛/科研', max: 100 }
        ],
        shape: 'circle',
        axisName: { color: '#64748b', fontSize: 11, fontWeight: 'bold' },
        splitArea: { color: ['#f8fafc', '#f1f5f9', '#e2e8f0'] },
        splitLine: { lineStyle: { color: '#e2e8f0' } }
    },
    series: [{
        type: 'radar',
        data: [{
        value: [92, 88, 95, 80, 96], // 针对你的豪华简历稍微调高了默认分
        areaStyle: { color: 'rgba(37, 99, 235, 0.2)' },
        lineStyle: { color: '#2563eb', width: 2 },
        itemStyle: { color: '#2563eb' }
        }]
    }]
    })
}

const handleResize = () => myChart?.resize()
const goBack = () => router.push('/resume')
const toEdit = () => router.push(`/resume/edit?id=${resumeId}`)
const exportPDF = () => ElMessage.success('导出功能开发中...')

onMounted(() => {
    window.addEventListener('resize', handleResize)
    getResume()
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    myChart?.dispose()
})
</script>

<style scoped>
/* 保持原有的基础样式不变，增加新增模块的细节优化 */
.resume-detail-page { max-width: 1100px; margin: 88px auto 40px; padding: 0 20px; }
.detail-action-bar { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 14px 24px; border-radius: 12px; border: 1px solid #eef0f5; margin-bottom: 24px; position: sticky; top: 20px; z-index: 10; box-shadow: 0 4px 12px rgba(0,0,0,0.02); }
.left-title { display: flex; align-items: center; gap: 14px; }
.resume-name { font-weight: 600; color: #1e293b; font-size: 16px; }

.detail-layout { display: grid; grid-template-columns: 1fr 300px; gap: 24px; }
.layout-left { display: flex; flex-direction: column; gap: 24px; }
.detail-card { background: #fff; border-radius: 12px; border: 1px solid #eef0f5; padding: 24px; transition: all 0.3s ease; }
.detail-card:hover { box-shadow: 0 8px 24px rgba(0,0,0,0.04); }

.hero-main { display: flex; gap: 20px; align-items: center; }
.hero-text { width: 100%; }
.name-row { display: flex; align-items: center; gap: 12px; }
.name-row h2 { margin: 0; font-size: 26px; color: #1e293b; letter-spacing: 1px; }
.status-tag { background: #f0fdf4; color: #16a34a; font-size: 12px; padding: 3px 10px; border-radius: 6px; font-weight: 600; }
.job-title { margin: 10px 0 16px; color: #2563eb; font-size: 16px; font-weight: 600; }
.meta-grid { display: flex; flex-wrap: wrap; gap: 24px; color: #475569; font-size: 14px; }
.meta-grid span { display: flex; align-items: center; gap: 6px; }

.block-title { margin: 0 0 20px; font-size: 17px; font-weight: 600; color: #1e293b; border-left: 4px solid #2563eb; padding-left: 12px; }
.sub-title { font-size: 15px; color: #334155; margin: 0 0 8px; display: flex; align-items: center; gap: 6px; }

/* 长文本展示优化 */
.desc-text { font-size: 14px; color: #475569; line-height: 1.7; margin: 0; white-space: pre-wrap; }
.highlight-text { color: #1e293b; font-weight: 500; }
.mb-4 { margin-bottom: 20px; }

/* 雷达与长串技能点优化 */
.chart-content { display: grid; grid-template-columns: 280px 1fr; gap: 20px; align-items: center; }
.radar-canvas { width: 100%; height: 240px; }
.core-skills-list { margin: 0; padding-left: 18px; color: #475569; font-size: 13.5px; line-height: 1.8; }
.core-skills-list li { margin-bottom: 4px; }

/* 双列列表结构（证书/奖项） */
.two-col-list { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.list-col { background: #f8fafc; padding: 16px; border-radius: 8px; }
.bullet-list { margin: 0; padding-left: 20px; color: #475569; font-size: 13.5px; line-height: 1.8; }
.bullet-list li { margin-bottom: 6px; }

.project-timeline { display: flex; flex-direction: column; gap: 24px; }
.project-item { border-bottom: 1px dashed #e2e8f0; padding-bottom: 24px; }
.project-item:last-child { border-bottom: none; padding-bottom: 0; }
.proj-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.proj-title-row { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.proj-title-row h4 { margin: 0; font-size: 16px; color: #1e293b; font-weight: 600; }
.proj-role { font-size: 12px; background: #eff6ff; color: #2563eb; padding: 3px 10px; border-radius: 4px; font-weight: 600; }
.proj-time { font-size: 13px; color: #94a3b8; font-weight: 500; white-space: nowrap; }
.proj-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 14px; }
.tech-tag { font-size: 12px; background: #f1f5f9; color: #475569; padding: 4px 10px; border-radius: 6px; }
.tech-stack-tag { background: #fdf4ff; color: #c026d3; } 
.highlight-tag { background: #e0e7ff; color: #4338ca; } 
.proj-desc-box { background: #f8fafc; padding: 16px; border-radius: 8px; font-size: 14px; line-height: 1.7; color: #334155; white-space: pre-wrap; }
.proj-desc-box p { margin: 0; }

.sidebar-card { background: #fff; border-radius: 12px; border: 1px solid #eef0f5; padding: 20px; margin-bottom: 20px; }
.side-title { margin: 0 0 16px; font-size: 15px; font-weight: 600; color: #1e293b; padding-bottom: 10px; border-bottom: 1px solid #f1f5f9; }
.side-item { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; font-size: 14px; }
.side-item:last-child { margin-bottom: 0; }
.side-item .label { color: #64748b; font-size: 13px; }
.side-item .val { color: #1e293b; font-weight: 600; }

@media (max-width: 768px) {
    .detail-layout { grid-template-columns: 1fr; }
    .two-col-list { grid-template-columns: 1fr; }
    .chart-content { grid-template-columns: 1fr; }
}
</style>