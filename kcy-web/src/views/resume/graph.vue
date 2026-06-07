<template>
<div class="full-graph-page" v-loading="pageLoading" element-loading-text="🌌 正在激活全息知识图谱引擎，AI 正在全力以赴进行高维双轨对称织网中..." element-loading-background="rgba(248, 250, 252, 0.90)">
    <div class="graph-top-nav">
    <div class="nav-left">
        <el-button :icon="Back" circle @click="backToDetail"></el-button>
        <span class="nav-title">🌌 AI 人才高维全息能力知识图谱</span>
        <el-tag size="small" type="success" effect="dark" round v-if="candidateName">
        {{ candidateName }} · 全栈能力图谱
        </el-tag>
    </div>
    <div class="nav-right">
        <el-button type="primary" plain @click="fetchGraphData">重新计算拓扑</el-button>
    </div>
    </div>

    <div class="graph-main-content">
    <div ref="fullGraphRef" class="full-canvas-container"></div>
    </div>

    <div class="graph-bottom-bar" v-if="!pageLoading">
    <span class="tip-item">💡 操作指引：支持鼠标滚轮对画面进行无限缩放，可任意拖拽节点。</span>
    <span class="tip-item">🔥 能级权重：节点辐射圈越大、代表候选人在该技术或项目的控盘能级越强。</span>
    </div>
</div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Back } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { ResumeApi } from '@/api/resume/index'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

const pageLoading = ref(true)
const fullGraphRef = ref<HTMLDivElement | null>(null)
let graphInstance: echarts.ECharts | null = null

const resumeId = ref<number>(Number(route.params.id))
const candidateName = ref<string>('') // 用于接收后端动态清洗出来的姓名

// SSE 全局长连接流实例与兜底定时器
let sseSource: EventSource | null = null
let backupTimer: any = null

const backToDetail = () => {
router.push(`/resume/detail/${resumeId.value}`)
}

// ======================= 🌟 核心空降：图谱页就地部署 SSE 哨兵线 =======================
const initGraphSse = () => {
if (sseSource) return // 防止重复挂载

const loginUserId = userStore.userInfo?.id || 2
const sseUrl = `http://127.0.0.1:48080/app-api/member/sse/connect?userId=${loginUserId}`
sseSource = new EventSource(sseUrl)

sseSource.addEventListener('INIT', (e) => {
    console.log('📡 [图谱页SSE] 长连接握手成功:', e.data)
})

// 🌟 核心哨兵：一旦捕获到当前简历图谱已经异步同步成功，立刻唤醒重新打捞！
sseSource.addEventListener('RESUME_GRAPH_SUCCESS', (e) => {
    const dataId = e.data
    if (Number(dataId) === resumeId.value) {
    console.log(`🎉 [图谱页SSE] 捷报！收到当前简历图谱同步成功喜报，开始秒级全自动冲锋！`)
    
    // 清除长连接与兜底定时器
    destroySseAndTimer()
    // 重新打捞渲染数据
    fetchGraphData()
    }
})
}

// 🌟 2. 对接 Java 实战接口流，承担按需延迟等待策略
const fetchGraphData = async () => {
try {
    pageLoading.value = true
    
    // 🚀 顺着你给出的标准请求，隔空炮轰 Java 业务实现层
    const res = await ResumeApi.getResumeGraph(resumeId.value)
    console.log("📡 [图谱同步] 后端接口直出载荷:", res)
    
    // 兼容可能带有 .data 包装的统一响应体结构
    const graphData = res?.data ? res.data : res
    
    // 🌟 判断核心：后端有图，直出渲染
    if (graphData && Array.isArray(graphData.nodes) && graphData.nodes.length > 0) {
    
    // 🎯 核心对齐：提取我们换完前缀后全新升级的 RESUME_ 中心节点姓名
    const centerNode = graphData.nodes.find((n: any) => n.id && n.id.startsWith('RESUME_'))
    if (centerNode) {
        candidateName.value = centerNode.name
    }

    // 数据质量检测通过，直接喂满 ECharts 仿真力学引力布局模型
    initFullGraph(graphData)
    
    // 只有拿到真实图数据并渲染了，才关闭全屏 Loading
    pageLoading.value = false

    } else {
    // 🌟 🌟 核心高能：如果查出是 null 或者是空的，说明 Python 正在火速织网！
    // 此时坚绝不让 pageLoading 变为 false，保持转圈圈加载状态！
    console.log('⏳ [图谱页感知] 现成图谱为空，后端已触发异步MQ。就地挂起长连接与2秒轮询兜底机制...');
    
    // 1. 激活长连接监听喜报
    initGraphSse()
    
    // 2. 挂载一个2秒钟的轮询作为长连接偶尔断连时的完美备用胎（双保险架构）
    if (!backupTimer) {
        backupTimer = setInterval(() => {
        fetchGraphData()
        }, 2000)
    }
    }
} catch (error) {
    console.error('拉取全景能力图谱触发业务崩溃:', error)
    ElMessage.error('加载全景能力图谱失败，请检查后端解构日志')
    pageLoading.value = false // 崩溃时允许释放，防止页面假死
    destroySseAndTimer()
}
}

// 🌟 3. 驱动 100% 满盘炫酷 Force 拓扑依赖网络
const initFullGraph = (graphData: any) => {
if (!fullGraphRef.value) return
if (graphInstance) graphInstance.dispose()

graphInstance = echarts.init(fullGraphRef.value)

// 🌟 4大视觉规整核心大分类
const defaultCategories = [
    { name: 'Resume', keyword: '人才简历' }, // 像素级对齐后端更新后的实体名
    { name: 'Category', keyword: '双轨骨架' }, 
    { name: 'Skill', keyword: '核心技术' },
    { name: 'Project', keyword: '实战项目' },
    { name: 'Award', keyword: '重磅荣誉' }
]

const option = {
    // 升级极客色矩阵，依次映射不同实体的霓虹气泡色
    color: ['#3b82f6', '#6366f1', '#10b981', '#f59e0b', '#ec4899'], 
    title: {
    text: `${candidateName.value || '候选人'} - 核心能力知识依赖拓扑网`,
    subtext: 'Data automatically structured by Yudao AI parser engine',
    top: '3%',
    left: '3%',
    textStyle: { color: '#0f172a', fontSize: 18, fontWeight: 'bold' }
    },
    tooltip: {
    trigger: 'item',
    backgroundColor: 'rgba(15, 23, 42, 0.9)', 
    borderWidth: 0,
    textStyle: { color: '#fff', fontSize: 13 },
    formatter: (params: any) => {
        if (params.dataType === 'edge') {
        return `<div style="padding:4px 8px;"><b>拓扑链路：</b>${params.data.value || '强相关纽带'}</div>`
        }
        return `<div style="padding:6px 12px; max-width:300px; white-space:pre-line;">
                <b style="font-size:14px;color:#3b82f6;">${params.name}</b><br/>
                <span style="color:#cbd5e1;font-size:12px;margin-top:4px;display:block;">分类特征：${params.data.category || '核心组件'}</span>
                </div>`
    }
    },
    legend: [{
    data: defaultCategories.map((a: any) => a.name),
    top: '3%',
    right: '4%',
    textStyle: { color: '#475569', fontWeight: 600 }
    }],
    animationDuration: 1800,
    animationEasingUpdate: 'exponentialOut',
    series: [
    {
        type: 'graph',
        layout: 'force', 
        data: graphData.nodes.map((node: any) => {
        const catIndex = defaultCategories.findIndex(c => c.name === node.category);
        return {
            ...node,
            category: catIndex >= 0 ? catIndex : 2 
        }
        }),
        links: graphData.links,
        categories: defaultCategories, 
        roam: true,               
        draggable: true,          
        focusNodeAdjacency: true, 
        label: {
        show: true,
        position: 'right',     
        formatter: '{b}',
        fontSize: 12,
        fontWeight: '500',
        color: '#334155'
        },
        labelLayout: {
        hideOverlap: true 
        },
        force: {
        repulsion: 400,        // 针对规整布局调优排斥力，让左右轨道铺开得更加端正
        edgeLength: [60, 130], 
        gravity: 0.05           
        },
        lineStyle: {
        color: 'source',       
        width: 2.2,
        curveness: 0.08,       // 规整排版给予稍微更直一点的曲率，显得横平竖直
        opacity: 0.7
        },
        emphasis: {
        scale: true,
        lineStyle: { width: 5, opacity: 1 }
        }
    }
    ]
}

graphInstance.setOption(option)
}

// 清除监听资源专门工具
const destroySseAndTimer = () => {
if (sseSource) {
    sseSource.close()
    sseSource = null
    console.log('📡 [图谱页SSE] 连接已安全断开')
}
if (backupTimer) {
    clearInterval(backupTimer)
    backupTimer = null
    console.log('⏱️ [图谱页定时器] 轮询已安全注销')
}
}

const handleResize = () => {
graphInstance?.resize()
}

onMounted(() => {
window.addEventListener('resize', handleResize)
fetchGraphData() 
})

onUnmounted(() => {
window.removeEventListener('resize', handleResize)
graphInstance?.dispose() 
destroySseAndTimer() // 离开页面干净卸载，绝不发生内存常驻
})
</script>

<style scoped>
.full-graph-page {
width: 100vw;
height: 100vh;
display: flex;
flex-direction: column;
background: #f8fafc;
overflow: hidden;
position: fixed;
top: 0;
left: 0;
z-index: 999;
}
.graph-top-nav {
height: 64px;
background: #ffffff;
border-bottom: 1px solid #e2e8f0;
padding: 0 24px;
display: flex;
justify-content: space-between;
align-items: center;
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}
.nav-left {
display: flex;
align-items: center;
gap: 16px;
}
.nav-title {
font-size: 16px;
font-weight: 600;
color: #1e293b;
letter-spacing: 0.5px;
}
.graph-main-content {
flex: 1;
width: 100%;
padding: 20px;
box-sizing: border-box;
}
.full-canvas-container {
width: 100%;
height: 100%;
background: #ffffff;
border-radius: 12px;
border: 1px solid #eef0f5;
box-shadow: 0 4px 18px rgba(0, 0, 0, 0.01);
}
.graph-bottom-bar {
height: 40px;
background: #ffffff;
border-top: 1px solid #e2e8f0;
padding: 0 24px;
display: flex;
align-items: center;
gap: 40px;
}
.tip-item {
font-size: 12px;
color: #64748b;
font-weight: 500;
}
</style>