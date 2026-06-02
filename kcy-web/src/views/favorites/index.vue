<template>
    <div class="favorites-page">
    <div class="favorites-header-card">
        <div class="welcome-text">
        <h2>我的收藏中心</h2>
        <p>汇聚你心仪的精选岗位、实力企业与行业干货</p>
        </div>
        <div class="stats-group">
        <div class="stat-box">
            <span class="num blue">{{ jobFavorites.length }}</span>
            <span class="label">岗位收藏</span>
        </div>
        <div class="stat-box">
            <span class="num green">{{ companyFavorites.length }}</span>
            <span class="label">企业收藏</span>
        </div>
        <div class="stat-box">
            <span class="num purple">{{ infoFavorites.length }}</span>
            <span class="label">信息干货</span>
        </div>
        </div>
    </div>

    <div class="favorites-tabs-container">
        <el-tabs v-model="activeTab" class="custom-tabs">
        
        <el-tab-pane label="岗位收藏" name="jobs">
            <div v-if="jobFavorites.length > 0" class="list-layout-vertical">
            <div 
                class="collect-job-card" 
                v-for="job in jobFavorites" 
                :key="job.id"
                @click="toJobDetail(job.id)"
            >
                <div class="card-main">
                <div class="job-meta-row">
                    <div class="job-name-box">
                    <h4>{{ job.title }}</h4>
                    <span class="job-tags" v-for="tag in job.tags" :key="tag">{{ tag }}</span>
                    </div>
                    <span class="salary">{{ job.salary }}</span>
                </div>
                <div class="comp-meta-row">
                    <span class="comp-name">{{ job.companyName }}</span>
                    <span class="divider">|</span>
                    <span class="comp-desc">{{ job.industry }} · {{ job.scale }}</span>
                </div>
                </div>
                <div class="card-action-bar" @click.stop>
                <span class="time">收藏于：{{ job.collectTime }}</span>
                <el-button type="danger" link :icon="Delete" @click="cancelCollect('jobs', job.id)">取消收藏</el-button>
                </div>
            </div>
            </div>
            <el-empty v-else description="暂无收藏的岗位" />
        </el-tab-pane>

        <el-tab-pane label="企业收藏" name="companies">
            <div v-if="companyFavorites.length > 0" class="grid-layout-3col">
            <div 
                class="collect-comp-card" 
                v-for="comp in companyFavorites" 
                :key="comp.id"
                @click="toCompanyDetail(comp.id)"
            >
                <div class="comp-header">
                <img :src="comp.logo" class="comp-logo" />
                <div class="comp-info">
                    <h4>{{ comp.name }}</h4>
                    <p>{{ comp.industry }} · {{ comp.stage }}</p>
                </div>
                </div>
                <div class="comp-body">
                <div class="tag-row">
                    <span class="mini-border-tag" v-for="welfare in comp.welfares" :key="welfare">{{ welfare }}</span>
                </div>
                <p class="hot-job-snippet">在招热推：<span class="job-hl">{{ comp.hotJob }}</span></p>
                </div>
                <div class="comp-footer" @click.stop>
                <span class="loc">{{ comp.location }}</span>
                <el-button type="danger" link size="small" :icon="Delete" @click="cancelCollect('companies', comp.id)">取消</el-button>
                </div>
            </div>
            </div>
            <el-empty v-else description="暂无收藏的企业" />
        </el-tab-pane>

        <el-tab-pane label="信息收藏" name="info">
            <div v-if="infoFavorites.length > 0" class="list-layout-vertical">
            <div class="collect-info-card" v-for="info in infoFavorites" :key="info.id">
                <div class="info-body">
                <div class="info-content-left">
                    <span class="info-type-tag">{{ info.category }}</span>
                    <h4 class="info-title">{{ info.title }}</h4>
                    <p class="info-summary">{{ info.summary }}</p>
                </div>
                <img v-if="info.cover" :src="info.cover" class="info-cover" />
                </div>
                <div class="info-footer">
                <div class="meta-left">
                    <span>来源：{{ info.source }}</span>
                    <span class="dot">·</span>
                    <span>{{ info.collectTime }} 收藏</span>
                </div>
                <el-button type="danger" link :icon="Delete" @click="cancelCollect('info', info.id)">取消收藏</el-button>
                </div>
            </div>
            </div>
            <el-empty v-else description="暂无收藏的信息干货" />
        </el-tab-pane>

        </el-tabs>
    </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeTab = ref('jobs')

// 1. 模拟岗位收藏数据
const jobFavorites = ref([
    {
    id: 101,
    title: 'Java 全栈开发工程师（双休）',
    salary: '8K - 13K',
    companyName: '科大讯飞股份有限公司',
    industry: '人工智能/互联网',
    scale: '10000人以上',
    tags: ['Spring Boot', 'Vue3', 'MySQL'],
    collectTime: '2026-06-01'
    },
    {
    id: 102,
    title: 'Vue3 前端研发工程师（跨端方向）',
    salary: '7K - 11K',
    companyName: '极客互娱科技有限公司',
    industry: '游戏/移动互联网',
    scale: '100-499人',
    tags: ['Vue3', 'UniApp', 'TypeScript'],
    collectTime: '2026-05-28'
    }
])

// 2. 模拟企业收藏数据
const companyFavorites = ref([
    {
    id: 1,
    name: '字节跳动',
    logo: 'https://picsum.photos/100/100?random=1',
    industry: '互联网',
    stage: '已上市',
    location: '合肥研发中心',
    welfares: ['六险一金', '弹性工作', '免费三餐'],
    hotJob: 'Java架构师 / 资深跨端大前端'
    },
    {
    id: 2,
    name: '极客互娱',
    logo: 'https://picsum.photos/100/100?random=2',
    industry: '游戏',
    stage: 'B轮',
    location: '滁州高新区',
    welfares: ['年终多薪', '定期体检', '大牛带队'],
    hotJob: 'UniApp小程序全栈开发'
    }
])

// 3. 模拟信息资讯收藏数据
const infoFavorites = ref([
    {
    id: 301,
    category: '技术干货',
    title: '2026年全栈工程师核心武器库：Spring Boot 3.x 与 Vue 3 深度整合调优指南',
    summary: '本文深度探讨了在微服务与高性能单体架构下，如何利用持久层组件、多线程清洗以及前端响应式编排，实现复杂大数据列表的“秒级渲染”与高性能数据穿透。',
    source: '大厂架构师社区',
    collectTime: '2026-06-01',
    cover: 'https://picsum.photos/200/120?random=20'
    },
    {
    id: 302,
    category: '面试通关',
    title: '彻底搞懂 3D Web 渲染：Three.js 导入古建筑 glTF/GLB 模型内存泄漏优化痛点',
    summary: '针对数字化展馆项目，复盘如何优雅加载、缩减 Bundle 材质体积，处理复杂古建榫卯模型的动态交互与组件销毁处理机制。',
    source: '前端工程化专栏',
    collectTime: '2026-05-25',
    cover: 'https://picsum.photos/200/120?random=21'
    }
])

// 路由闭环：跳转至岗位详情页（假设路由为 /jobs/:id）
const toJobDetail = (id: number) => {
    router.push(`/jobs/${id}`)
}

// 路由闭环：跳转至企业详情页（对应你原有的配置 /companies/:id）
const toCompanyDetail = (id: number) => {
    router.push(`/companies/${id}`)
}

// 取消收藏交互处理机制
const cancelCollect = (type: 'jobs' | 'companies' | 'info', id: number) => {
    ElMessageBox.confirm('确认移出收藏夹吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
    }).then(() => {
    if (type === 'jobs') {
        jobFavorites.value = jobFavorites.value.filter(item => item.id !== id)
    } else if (type === 'companies') {
        companyFavorites.value = companyFavorites.value.filter(item => item.id !== id)
    } else if (type === 'info') {
        infoFavorites.value = infoFavorites.value.filter(item => item.id !== id)
    }
    ElMessage.success('已成功取消收藏')
    }).catch(() => {})
}
</script>

<style scoped>
.favorites-page { max-width: 1100px; margin: 88px auto 40px; padding: 0 20px; }

/* 头部面板 */
.favorites-header-card { background: #fff; padding: 24px; border-radius: 12px; border: 1px solid #eef0f5; display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.welcome-text h2 { margin: 0 0 6px; font-size: 22px; color: #1e293b; }
.welcome-text p { margin: 0; font-size: 14px; color: #64748b; }
.stats-group { display: flex; gap: 20px; }
.stat-box { display: flex; flex-direction: column; align-items: center; background: #f8fafc; padding: 12px 20px; border-radius: 8px; min-width: 80px; border: 1px solid #f1f5f9; }
.stat-box .num { font-size: 20px; font-weight: 700; }
.stat-box .num.blue { color: #2563eb; }
.stat-box .num.green { color: #16a34a; }
.stat-box .num.purple { color: #7c3aed; }
.stat-box .label { font-size: 11px; color: #64748b; margin-top: 4px; }

/* 标签页框体 */
.favorites-tabs-container { background: #fff; padding: 24px; border-radius: 12px; border: 1px solid #eef0f5; min-height: 450px; }

/* 垂直排版列表风格（岗位、信息） */
.list-layout-vertical { display: flex; flex-direction: column; gap: 16px; }

/* 岗位收藏卡片 */
.collect-job-card { border: 1px solid #eef0f5; border-radius: 8px; transition: 0.25s; cursor: pointer; background: #fff; }
.collect-job-card:hover { border-color: #2563eb; box-shadow: 0 4px 12px rgba(37,99,235,0.04); }
.card-main { padding: 16px 20px; }
.job-meta-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.job-name-box { display: flex; align-items: center; gap: 10px; }
.job-name-box h4 { margin: 0; font-size: 16px; color: #1e293b; }
.job-tags { font-size: 11px; color: #475569; background: #f1f5f9; padding: 2px 8px; border-radius: 4px; }
.salary { font-size: 15px; color: #f43f5e; font-weight: 600; }
.comp-meta-row { font-size: 13px; color: #64748b; display: flex; align-items: center; gap: 8px; }
.comp-name { color: #334155; font-weight: 500; }
.card-action-bar { border-top: 1px solid #f8fafc; background: #fafbfc; padding: 10px 20px; border-radius: 0 0 8px 8px; display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: #94a3b8; }

/* 企业网格布局 (一行3列) */
.grid-layout-3col { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.collect-comp-card { border: 1px solid #eef0f5; border-radius: 10px; background: #fff; cursor: pointer; transition: 0.25s; display: flex; flex-direction: column; justify-content: space-between; overflow: hidden; }
.collect-comp-card:hover { border-color: #16a34a; box-shadow: 0 4px 12px rgba(22,163,74,0.05); }
.comp-header { padding: 16px; display: flex; gap: 12px; align-items: center; border-bottom: 1px solid #f8fafc; }
.comp-logo { width: 44px; height: 44px; border-radius: 6px; object-fit: cover; border: 1px solid #f1f5f9; }
.comp-info h4 { margin: 0 0 4px; font-size: 15px; color: #1e293b; }
.comp-info p { margin: 0; font-size: 12px; color: #94a3b8; }
.comp-body { padding: 14px 16px; flex: 1; }
.tag-row { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 12px; }
.mini-border-tag { font-size: 11px; color: #16a34a; border: 1px solid #bbf7d0; background: #f0fdf4; padding: 1px 6px; border-radius: 4px; }
.hot-job-snippet { margin: 0; font-size: 12px; color: #64748b; }
.job-hl { color: #334155; font-weight: 600; }
.comp-footer { background: #fafbfc; padding: 10px 16px; font-size: 12px; display: flex; justify-content: space-between; align-items: center; color: #94a3b8; border-top: 1px solid #f1f5f9; }

/* 信息干货卡片 */
.collect-info-card { border: 1px solid #eef0f5; border-radius: 8px; background: #fff; padding: 16px 20px; display: flex; flex-direction: column; gap: 12px; }
.info-body { display: flex; justify-content: space-between; gap: 20px; }
.info-content-left { flex: 1; }
.info-type-tag { font-size: 11px; color: #7c3aed; background: #f3e8ff; padding: 2px 8px; border-radius: 4px; font-weight: 600; display: inline-block; margin-bottom: 8px; }
.info-title { margin: 0 0 6px; font-size: 16px; color: #1e293b; line-height: 1.4; }
.info-summary { margin: 0; font-size: 13px; color: #64748b; line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.info-cover { width: 140px; height: 84px; border-radius: 6px; object-fit: cover; background: #f1f5f9; }
.info-footer { border-top: 1px dotted #e2e8f0; padding-top: 10px; display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: #94a3b8; }
.meta-left { display: flex; align-items: center; gap: 6px; }

/* 自定义Tabs微调 */
:deep(.el-tabs__item) { font-size: 15px; font-weight: 500; color: #64748b; height: 48px; }
:deep(.el-tabs__item.is-active) { color: #2563eb; font-weight: 600; }
:deep(.el-tabs__active-bar) { background-color: #2563eb; height: 3px; border-radius: 2px; }
</style>