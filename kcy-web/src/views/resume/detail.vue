<template>
    <div class="resume-detail-page">
    <!-- 顶部控制悬浮栏 -->
    <div class="detail-action-bar">
        <div class="left-title">
        <el-button :icon="Back" circle @click="goBack"></el-button>
        <span class="resume-name">{{ resumeData.title }}</span>
        </div>
        <div class="right-ops">
        <el-button type="info" plain :icon="Download" @click="exportPDF">导出 PDF</el-button>
        <el-button type="primary" :icon="Edit" @click="toEdit">编辑此简历</el-button>
        </div>
    </div>

    <!-- 主体两栏布局 -->
    <div class="detail-layout">
        <!-- 左侧：全息简历核心内容 -->
        <div class="layout-left">
        <!-- 1. 个人名片 -->
        <div class="detail-card hero-block">
            <div class="hero-main">
            <img :src="resumeData.avatar" class="detail-avatar" />
            <div class="hero-text">
                <div class="name-row">
                <h2>{{ resumeData.name }}</h2>
                <span class="status-tag">积极找工作</span>
                </div>
                <p class="job-title">{{ resumeData.expectedJob }}</p>
                <div class="meta-grid">
                <span><el-icon><User /></el-icon> {{ resumeData.age }}岁</span>
                <span><el-icon><Location /></el-icon> {{ resumeData.location }}</span>
                <span><el-icon><Document /></el-icon> {{ resumeData.education }} · 软件技术</span>
                </div>
            </div>
            </div>
        </div>

        <!-- 2. 核心技术能级评估 (ECharts 可视化) -->
        <div class="detail-card chart-block">
            <h3 class="block-title">全栈技术能级画像</h3>
            <div class="chart-content">
            <div ref="radarChartRef" class="radar-canvas"></div>
            <div class="tech-narrative">
                <h4>技术栈核心梯队：</h4>
                <ul>
                <li><strong>后端开发：</strong>深度运用 Spring Boot + MyBatis-Plus，精通持久层高内聚设计与 MySQL 关联查询调优。</li>
                <li><strong>前端生态：</strong>熟练掌握 Vue3 组合式 API、TypeScript，擅长运用 Element Plus 与 Axios 构建中后台数据闭环。</li>
                <li><strong>移动跨端：</strong>具备 UniApp 独立研发混编能力，打通多端数据穿透与秒级渲染。</li>
                </ul>
            </div>
            </div>
        </div>

        <!-- 3. 硬核项目深钻经历 -->
        <div class="detail-card project-block">
            <h3 class="block-title">核心项目经历</h3>
            <div class="project-timeline">
            <div class="project-item" v-for="proj in resumeData.projects" :key="proj.id">
                <div class="proj-header">
                <div class="proj-title-row">
                    <h4>{{ proj.name }}</h4>
                    <span class="proj-role">{{ proj.role }}</span>
                </div>
                <span class="proj-time">{{ proj.period }}</span>
                </div>
                
                <!-- 技术图谱 -->
                <div class="proj-tags">
                <span v-for="tech in proj.techs" :key="tech" class="tech-tag">{{ tech }}</span>
                </div>

                <!-- 项目细节 -->
                <div class="proj-desc-box">
                <p><strong>项目描述：</strong>{{ proj.description }}</p>
                <p><strong>核心职责与技术攻关：</strong>{{ proj.duty }}</p>
                </div>
            </div>
            </div>
        </div>
        </div>

        <!-- 右侧：求职意向与联系信道 -->
        <div class="layout-right">
        <!-- 求职意向 -->
        <div class="sidebar-card">
            <h3 class="side-title">期望意向</h3>
            <div class="side-item">
            <span class="label">期望职业</span>
            <span class="val">{{ resumeData.expectedJob }}</span>
            </div>
            <div class="side-item">
            <span class="label">工作城市</span>
            <span class="val">安徽合肥</span>
            </div>
            <div class="side-item">
            <span class="label">薪资期望</span>
            <span class="val">薪资面议</span>
            </div>
        </div>

        <!-- 联系信道 -->
        <div class="sidebar-card">
            <h3 class="side-title">联系方式</h3>
            <div class="side-item">
            <span class="label">手机号码</span>
            <span class="val">138 **** 8888</span>
            </div>
            <div class="side-item">
            <span class="label">电子邮箱</span>
            <span class="val">huangsheng@example.com</span>
            </div>
        </div>
        </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Back, Download, Edit, User, Location, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()
const radarChartRef = ref<HTMLDivElement | null>(null)
let myChart: echarts.ECharts | null = null

// 动态接收路由传参：route.params.id
const resumeId = route.params.id

// 模拟根据 ID 从后端异步拉取到的完整简历对象
const resumeData = reactive({
    title: '黄胜-Java全栈开发工程师简历',
    name: '黄胜',
    avatar: 'https://picsum.photos/150/150?random=10',
    age: 21,
    location: '安徽',
    education: '大专',
    expectedJob: 'Java全栈开发工程师',
    projects: [
    {
        id: 1,
        name: '乐行旅途（一站式跨端直达旅游平台）',
        period: '2025.10 - 2026.02',
        role: '全栈独立开发 / 负责人',
        techs: ['UniApp', 'Vue3', 'Spring Boot', 'MyBatis-Plus', 'MySQL'],
        description: '面向旅游出行生态的一站式轻量级跨端直达旅游服务平台。',
        duty: '全权负责客户端UniApp UI重构设计，基于Spring Boot完成高性能单体架构开发，封装MyBatis-Plus通用持久层，实现核心列表秒级渲染与数据穿透。'
    },
    {
        id: 2,
        name: '凌云智控物联网管理平台',
        period: '2025.05 - 2025.09',
        role: '架构设计 & 核心全栈',
        techs: ['Spring Boot', 'Vue2', 'Element UI', 'ECharts', 'Axios'],
        description: '实现多终端无缝接入的软硬件联动数字化物联网工业管控大屏系统。',
        duty: '攻克多传感器复杂数据汇聚痛点，利用后端多线程完成数据清洗归池，前端深度结合ECharts构建动态实时渲染折线与热力拓扑图。'
    }
    ]
})

// 初始化高能雷达图
const initRadar = () => {
    if (!radarChartRef.value) return
    myChart = echarts.init(radarChartRef.value)
    myChart.setOption({
    radar: {
        indicator: [
        { name: 'Java后端\n(Spring Boot)', max: 100 },
        { name: '前端生态\n(Vue3/TS)', max: 100 },
        { name: '跨端混编\n(UniApp)', max: 100 },
        { name: '数据可视化\n(ECharts)', max: 100 },
        { name: '数据库设计\n(MySQL)', max: 100 }
        ],
        shape: 'circle',
        axisName: { color: '#64748b', fontSize: 11, fontWeight: 'bold' },
        splitArea: { color: ['#f8fafc', '#f1f5f9', '#e2e8f0'] },
        splitLine: { lineStyle: { color: '#e2e8f0' } }
    },
    series: [{
        type: 'radar',
        data: [{
        value: [92, 88, 85, 90, 88],
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
const exportPDF = () => ElMessage.success('正在调用浏览器打印信道生成标准 PDF 简历...')

onMounted(() => {
    initRadar()
    window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    myChart?.dispose()
})
</script>

<style scoped>
.resume-detail-page { max-width: 1100px; margin: 88px auto 40px; padding: 0 20px; }

/* 悬浮操作栏 */
.detail-action-bar { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 14px 24px; border-radius: 12px; border: 1px solid #eef0f5; margin-bottom: 24px; position: sticky; top: 20px; z-index: 10; box-shadow: 0 4px 12px rgba(0,0,0,0.02); }
.left-title { display: flex; align-items: center; gap: 14px; }
.resume-name { font-weight: 600; color: #1e293b; font-size: 16px; }

/* 两栏响应式 */
.detail-layout { display: grid; grid-template-columns: 1fr 300px; gap: 24px; }
.layout-left { display: flex; flex-direction: column; gap: 24px; }
.detail-card { background: #fff; border-radius: 12px; border: 1px solid #eef0f5; padding: 24px; }

/* 个人信息 */
.hero-main { display: flex; gap: 20px; align-items: center; }
.detail-avatar { width: 80px; height: 80px; border-radius: 50%; object-fit: cover; border: 3px solid #f0f7ff; }
.name-row { display: flex; align-items: center; gap: 12px; }
.name-row h2 { margin: 0; font-size: 24px; color: #1e293b; }
.status-tag { background: #f0fdf4; color: #16a34a; font-size: 11px; padding: 2px 8px; border-radius: 4px; font-weight: 600; }
.job-title { margin: 6px 0 12px; color: #475569; font-size: 15px; font-weight: 500; }
.meta-grid { display: flex; gap: 20px; color: #64748b; font-size: 13px; }
.meta-grid span { display: flex; align-items: center; gap: 6px; }

/* 模块通用标题 */
.block-title { margin: 0 0 20px; font-size: 16px; color: #1e293b; border-left: 4px solid #2563eb; padding-left: 10px; }

/* ECharts 图表 */
.chart-content { display: grid; grid-template-columns: 280px 1fr; gap: 20px; align-items: center; }
.radar-canvas { width: 100%; height: 240px; }
.tech-narrative h4 { margin: 0 0 10px; font-size: 14px; color: #1e293b; }
.tech-narrative ul { margin: 0; padding-left: 18px; font-size: 13px; color: #475569; line-height: 1.7; }
.tech-narrative li { margin-bottom: 6px; }

/* 项目经历细化 */
.project-timeline { display: flex; flex-direction: column; gap: 24px; }
.project-item { border-bottom: 1px dashed #e2e8f0; padding-bottom: 24px; }
.project-item:last-child { border-bottom: none; padding-bottom: 0; }
.proj-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.proj-title-row { display: flex; align-items: center; gap: 12px; }
.proj-title-row h4 { margin: 0; font-size: 16px; color: #1e293b; }
.proj-role { font-size: 12px; background: #eff6ff; color: #2563eb; padding: 2px 8px; border-radius: 4px; font-weight: 600; }
.proj-time { font-size: 13px; color: #94a3b8; }
.proj-tags { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 12px; }
.tech-tag { font-size: 11px; background: #f1f5f9; color: #475569; padding: 2px 8px; border-radius: 4px; }
.proj-desc-box { background: #f8fafc; padding: 14px; border-radius: 8px; font-size: 13px; line-height: 1.6; color: #334155; }
.proj-desc-box p { margin: 4px 0; }

/* 右侧侧边栏卡片 */
.sidebar-card { background: #fff; border-radius: 12px; border: 1px solid #eef0f5; padding: 20px; margin-bottom: 20px; }
.side-title { margin: 0 0 16px; font-size: 14px; color: #1e293b; padding-bottom: 8px; border-bottom: 1px solid #f1f5f9; }
.side-item { display: flex; flex-direction: column; gap: 4px; margin-bottom: 12px; font-size: 13px; }
.side-item:last-child { margin-bottom: 0; }
.side-item .label { color: #94a3b8; }
.side-item .val { color: #334155; font-weight: 600; }
</style>