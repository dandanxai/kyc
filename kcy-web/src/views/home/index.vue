<template>
    <div class="home-container">
    
    <!-- 1. 全屏巨幕区 -->
    <div class="hero-section">
        <el-carousel 
        height="100vh" 
        class="hero-carousel" 
        :autoplay="true" 
        :interval="5000"
        arrow="always"
        trigger="click"
        >
        <el-carousel-item v-for="(img, idx) in bannerImages" :key="idx">
            <div class="full-bg-image" :style="{ backgroundImage: `url(${img.url})` }"></div>
        </el-carousel-item>
        </el-carousel>

        <div class="hero-overlay"></div>
        
        <!-- 【修复】：卸载了 JS 动态监听，回归纯 CSS 稳定布局，实现“直上直下”的硬核平滑滚动 -->
        <div class="hero-foreground-content">
        <div class="hero-content-panel">
            <div class="text-group">
            <span class="welcome-tag">✨ 闪聘 Recruitment 全栈对接平台</span>
            <h1 class="main-slogan">精选全栈岗位 · 开启未来蓝图</h1>
            <p class="main-sub-slogan">
                海量高薪岗位，一键简历投递，提供最高效、最便捷的 Java / Vue 全栈人才智慧对接 platform
            </p>
            </div>
            
            <div class="hero-search-box">
            <SearchBar @search="onSearch" />
            </div>
        </div>
        </div>
    </div>

    <!-- ================== 【调校】极客流光岗位广告组合区 ================== -->
    <div class="promo-section-wrapper">
        <div class="promo-grid-container">
        <!-- 左侧：今日主推·战略大厂直聘位 -->
        <div class="promo-main-card" @click="fastSearch('字节跳动')">
            <div class="promo-badge hot">🔥 今日急聘</div>
            <div class="promo-main-info">
            <span class="promo-comp">字节跳动 · 架构组</span>
            <h3 class="promo-title">Java 高级全栈架构师</h3>
            <div class="promo-meta">
                <span class="promo-salary">35K - 50K</span>
                <span class="promo-city">北京/合肥</span>
            </div>
            </div>
            <div class="aurora-bg"></div>
        </div>

        <!-- 右侧：4宫格高精尖技术岗位（预留后期热搜榜） -->
        <div class="promo-sub-grid">
            <div class="promo-mini-card" @click="fastSearch('Spring Boot')">
            <div class="mini-card-header">
                <span class="tech-tag java">Java</span>
                <span class="heat-index">⚡ 2.4k热度</span>
            </div>
            <h4 class="mini-job-title">Spring Boot 后端核心研发</h4>
            <div class="mini-job-footer">
                <span class="mini-salary">15K-25K</span>
                <span class="mini-comp">科大讯飞</span>
            </div>
            </div>

            <div class="promo-mini-card" @click="fastSearch('Vue3')">
            <div class="mini-card-header">
                <span class="tech-tag vue">Vue3</span>
                <span class="heat-index">⚡ 1.9k热度</span>
            </div>
            <h4 class="mini-job-title">Vue3 + TS 前端可视化专家</h4>
            <div class="mini-job-footer">
                <span class="mini-salary">12K-20K</span>
                <span class="mini-comp">蔚来汽车</span>
            </div>
            </div>

            <div class="promo-mini-card" @click="fastSearch('UniApp')">
            <div class="mini-card-header">
                <span class="tech-tag uniapp">UniApp</span>
                <span class="heat-index">⚡ 1.5k热度</span>
            </div>
            <h4 class="mini-job-title">跨端小程序 Full-Stack 负责人</h4>
            <div class="mini-job-footer">
                <span class="mini-salary">10K-18K</span>
                <span class="mini-comp">阳光电源</span>
            </div>
            </div>

            <div class="promo-mini-card" @click="fastSearch('AI')">
            <div class="mini-card-header">
                <span class="tech-tag ai">RAG / AI</span>
                <span class="heat-index">⚡ 3.2k热度</span>
            </div>
            <h4 class="mini-job-title">DeepSeek + Ollama 局部大模型专家</h4>
            <div class="mini-job-footer">
                <span class="mini-salary">25K-40K</span>
                <span class="mini-comp">阿里集团</span>
            </div>
            </div>
        </div>
        </div>
    </div>

    <!-- 2. 悬浮快捷菜单 -->
    <div class="data-board-wrapper">
        <div class="data-board">
            <div class="board-content">
                <!-- 快捷项 1：简历制作 -->
                <div class="data-item quick-menu-item" @click="router.push('/resume')">
                    <el-icon class="menu-icon icon-blue"><DocumentChecked /></el-icon>
                    <div class="menu-info">
                        <span class="data-num">简历制作</span>
                        <span class="data-label">在线打磨高光简历</span>
                    </div>
                </div>
                
                <div class="divider"></div>
                
                <!-- 快捷项 2：我的投递 -->
                <div class="data-item quick-menu-item" @click="router.push('/deliveries')">
                    <el-icon class="menu-icon icon-green"><ChatDotRound /></el-icon>
                    <div class="menu-info">
                        <span class="data-num">我的投递</span>
                        <span class="data-label">进度跟踪与HR直聊</span>
                    </div>
                </div>
                
                <div class="divider"></div>
                
                <!-- 快捷项 3：模拟面试 -->
                <div class="data-item quick-menu-item" @click="handleMockInterview">
                    <el-icon class="menu-icon icon-purple"><Cpu /></el-icon>
                    <div class="menu-info">
                        <span class="data-num">模拟面试</span>
                        <span class="data-label">AI 智能通关演练</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 3. 优质名企专区 -->
    <div class="home-content brand-companies">
        <div class="section-header">
        <h2 class="section-title">🏢 优质名企专区</h2>
        <span class="more-btn" @click="viewAllCompanies">查看更多大厂 ></span>
        </div>
        <div class="company-grid">
        <div v-for="comp in hotCompanies" :key="comp.id" class="company-card">
            <img :src="comp.logo" class="comp-logo" />
            <h4 class="comp-name">{{ comp.name }}</h4>
            <p class="comp-desc">{{ comp.industry }} · {{ comp.scale }}</p>
            <div class="comp-jobs-count">
            在招岗位 <span class="count-num">{{ comp.jobCount }}</span> 个
            </div>
        </div>
        </div>
    </div>

    <!-- 4. 精选推荐岗位 -->
    <div class="home-content">
        <div class="section-header">
        <div class="title-area">
            <h2 class="section-title">🌟 精选推荐岗位</h2>
            <div class="category-tabs">
            <span 
                v-for="tab in categories" 
                :key="tab.key"
                :class="['tab-item', { active: currentTab === tab.key }]"
                @click="currentTab = tab.key"
            >
                {{ tab.name }}
            </span>
            </div>
        </div>
        <span class="more-btn" @click="viewAllJobs">查看全部职位 ></span>
        </div>

        <div class="job-grid" v-if="filteredJobList.length > 0">
        <JobCard v-for="item in filteredJobList" :key="item.id" :job="item" />
        </div>
        <el-empty v-else description="该方向暂无推荐岗位" />
    </div>

    <!-- 5. 求职加油站 -->
    <div class="home-content career-guide">
        <div class="section-header">
        <h2 class="section-title">💡 求职加油站 & 面试干货</h2>
        </div>
        <div class="guide-grid">
        <div v-for="article in guideArticles" :key="article.id" class="article-card">
            <div class="article-tag">{{ article.tag }}</div>
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-summary">{{ article.summary }}</p>
            <div class="article-footer">
            <span>浏览 {{ article.views }}</span>
            <span class="read-more">阅读全文 →</span>
            </div>
        </div>
        </div>
    </div>

    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import SearchBar from '@/components/SearchBar.vue'
import JobCard from '@/components/JobCard.vue'
import type { JobItem } from '@/types/job'
import { DocumentChecked, ChatDotRound, Cpu } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const currentTab = ref('all')

const fastSearch = (keyword: string) => {
    router.push({ path: '/jobs', query: { keyword } })
}

const bannerImages = [
    { url: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1920&q=80' },
    { url: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1920&q=80' },
    { url: 'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?auto=format&fit=crop&w=1920&q=80' }
]

const hotCompanies = ref([
    { id: 1, name: '腾旭数字科技', logo: 'https://picsum.photos/80/80?random=21', industry: '互联网/软件', scale: '1000-5000人', jobCount: 42 },
    { id: 2, name: '微讯互联智控', logo: 'https://picsum.photos/80/80?random=22', industry: '物联网/AI', scale: '100-499人', jobCount: 15 },
    { id: 3, name: '盛大网络工作室', logo: 'https://picsum.photos/80/80?random=23', industry: '游戏开发/前端', scale: '20-99人', jobCount: 9 },
    { id: 4, name: '科大智讯软件', logo: 'https://picsum.photos/80/80?random=24', industry: '大数据/云原生', scale: '500-999人', jobCount: 24 }
])

const categories = [
    { name: '全部岗位', key: 'all' },
    { name: '后端开发', key: 'backend' },
    { name: '前端开发', key: 'frontend' }
]

interface ExtendedJobItem extends JobItem { category: string }
const mockJobList = ref<ExtendedJobItem[]>([
    { id: 1, title: 'Java 后端开发工程师', salary: '10K-15K', city: '合肥', experience: '1-3年', education: '大专', companyName: '科大智讯软件', companyLogo: 'https://picsum.photos/100/100?random=1', tags: ['Spring Boot', 'MyBatis-Plus', 'MySQL', '双休'], publishTime: '刚刚', category: 'backend' },
    { id: 2, title: 'Vue3 前端开发专家', salary: '12K-18K', city: '合肥', experience: '3-5年', education: '本科', companyName: '微讯互联智控', companyLogo: 'https://picsum.photos/100/100?random=2', tags: ['Vue3', 'TypeScript', 'ECharts'], publishTime: '1天前', category: 'frontend' },
    { id: 3, title: '全栈开发工程师（实习生）', salary: '4K-6K', city: '合肥', experience: '应届生/实习', education: '大专', companyName: '盛大网络工作室', companyLogo: 'https://picsum.photos/100/100?random=3', tags: ['Java', 'Vue', 'UniApp', '有转正机会'], publishTime: '3天前', category: 'backend' }
])

const filteredJobList = computed(() => {
    if (currentTab.value === 'all') return mockJobList.value
    return mockJobList.value.filter(job => job.category === currentTab.value)
})

const guideArticles = ref([
    { id: 1, tag: '面试突破', title: '2026年 Java 后端核心面试高频题深度解析', summary: '收录了从 Spring Boot 自动装配、MyBatis-Plus 插件原理到 MySQL 索引优化、B+树底层结构的硬核讲解。', views: '2.4k' },
    { id: 2, tag: '前端进阶', title: 'Vue2 迁移 Vue3 + TypeScript 实战避坑指南', summary: '手把手教你如何平滑重构项目。梳理了 Composition API 逻辑复用、Vite构建优化以及自定义 Hooks 的规范写法。', views: '1.8k' },
    { id: 3, tag: '简历优化', title: '如何写出一份让技术面试官眼前一亮的精美简历？', summary: '拒绝流水账！教你用“STAR法则”凸显你在校赛、软件设计大赛中的亮点、项目架构及复杂逻辑攻克过程。', views: '3.1k' }
])

const onSearch = (keyword: string) => { router.push({ path: '/jobs', query: { keyword } }) }
const viewAllJobs = () => { router.push('/jobs') }
const viewAllCompanies = () => { router.push('/companies') }
// 模拟面试快捷处理（可以先弹个提示，或者跳转到你未来的 AI 面试路由）
const handleMockInterview = () => {
    ElMessage.success('正在为您唤醒 AI 模拟面试官，真题沙箱加载中...')
    // router.push('/mock-interview') 
}
</script>

<style scoped>
.home-container { width: 100%; padding-bottom: 60px; }

.hero-section { position: relative; width: 100%; height: 100vh; overflow: hidden; }
.hero-carousel :deep(.el-carousel__container) { height: 100vh !important; }
.full-bg-image { width: 100%; height: 100vh; background-size: cover; background-position: center; }

.hero-overlay {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background: linear-gradient(135deg, rgba(11, 19, 43, 0.75) 0%, rgba(28, 37, 65, 0.45) 60%, rgba(11, 19, 43, 0.9) 100%);
    z-index: 1;
}

/* 轮播图指示器与控制 */
.hero-carousel :deep(.el-carousel__arrow) {
    background-color: rgba(255, 255, 255, 0.08) !important;
    backdrop-filter: blur(12px) !important;
    -webkit-backdrop-filter: blur(12px) !important;
    border: 1px solid rgba(255, 255, 255, 0.15) !important;
    color: #ffffff !important;
    width: 46px; height: 46px; font-size: 18px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.hero-carousel :deep(.el-carousel__arrow:hover) { background-color: #409eff !important; border-color: #409eff !important; box-shadow: 0 0 16px rgba(64, 158, 255, 0.4); transform: scale(1.05); }
.hero-carousel :deep(.el-carousel__arrow--left) { left: 30px; }
.hero-carousel :deep(.el-carousel__arrow--right) { right: 30px; }
.hero-carousel :deep(.el-carousel__indicators--horizontal) { bottom: 140px; z-index: 3; }
.hero-carousel :deep(.el-carousel__button) { width: 8px; height: 8px; border-radius: 50%; background-color: rgba(255, 255, 255, 0.4); opacity: 1; transition: all 0.3s ease; }
.hero-carousel :deep(.el-carousel__indicator.is-active .el-carousel__button) { width: 24px; border-radius: 4px; background-color: #409eff !important; box-shadow: 0 0 8px rgba(64, 158, 255, 0.6); }

/* 【纯CSS回归】：去除动效滞后性，完美跟随滚动条直接上下滚动 */
.hero-foreground-content { 
    position: absolute; 
    top: 0; left: 0; width: 100%; height: 100%; 
    z-index: 2; 
    display: flex; 
    align-items: center; 
}
.hero-content-panel { max-width: 1200px; width: 100%; margin: 0 auto; padding: 0 40px; box-sizing: border-box; text-align: left; }
.text-group { max-width: 750px; margin-bottom: 40px; }
.welcome-tag { display: inline-block; font-size: 13px; font-weight: 600; color: #409eff; background: rgba(64, 158, 255, 0.12); padding: 6px 16px; border-radius: 50px; margin-bottom: 18px; letter-spacing: 0.5px; }
.main-slogan { color: #ffffff; font-size: 46px; font-weight: 700; margin: 0 0 16px 0; letter-spacing: 1.5px; text-shadow: 0 4px 20px rgba(0, 0, 0, 0.35); }
.main-sub-slogan { color: rgba(255, 255, 255, 0.8); font-size: 17px; margin: 0; text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3); line-height: 1.7; }
.hero-search-box { max-width: 700px; }

/* ================== 【精细校调】极客流光岗位广告组合区样式 ================== */
.promo-section-wrapper {
    max-width: 1120px;
    /* 【修改】：将上间距扩大到 90px。为100vh巨幕完美留白，消除紧凑压迫感 */
    margin: 90px auto 0 auto; 
    padding: 0 20px;
    position: relative;
    z-index: 20;
    box-sizing: border-box;
}

.promo-grid-container { display: grid; grid-template-columns: 1.2fr 2fr; gap: 20px; width: 100%; }

/* 左侧主广告卡片 */
.promo-main-card {
    position: relative;
    background: linear-gradient(135deg, #11192b 0%, #0d1b3e 100%);
    border-radius: 16px;
    padding: 30px;
    min-height: 180px;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
    transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
}
.promo-main-card:hover { transform: translateY(-6px); box-shadow: 0 24px 50px rgba(64, 158, 255, 0.25); border-color: rgba(64, 158, 255, 0.4); }
.promo-badge { position: absolute; top: 16px; left: 16px; font-size: 11px; font-weight: 600; padding: 4px 10px; border-radius: 4px; z-index: 2; }
.promo-badge.hot { background: linear-gradient(90deg, #ff4d4f, #ff7875); color: #fff; box-shadow: 0 4px 10px rgba(255, 77, 79, 0.3); }
.promo-main-info { position: relative; z-index: 2; }
.promo-comp { color: rgba(255, 255, 255, 0.6); font-size: 13px; font-weight: 500; }
.promo-title { color: #ffffff; font-size: 20px; margin: 6px 0 14px 0; font-weight: 700; }
.promo-meta { display: flex; justify-content: space-between; align-items: center; }
.promo-salary { color: #409eff; font-size: 18px; font-weight: 700; }
.promo-city { color: rgba(255, 255, 255, 0.5); font-size: 13px; }

.aurora-bg {
    position: absolute;
    top: -50%; left: -50%; width: 200%; height: 200%;
    background: radial-gradient(circle, rgba(64, 158, 255, 0.15) 0%, transparent 60%);
    animation: rotateAurora 12s linear infinite;
    pointer-events: none;
}
@keyframes rotateAurora { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* 右侧4宫格 */
.promo-sub-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.promo-mini-card { background: #ffffff; border-radius: 14px; padding: 18px; border: 1px solid rgba(232, 236, 243, 0.7); box-shadow: 0 10px 25px rgba(10, 16, 32, 0.03); display: flex; flex-direction: column; justify-content: space-between; cursor: pointer; transition: all 0.3s ease; }
.promo-mini-card:hover { transform: translateY(-4px); border-color: #b3d8ff; box-shadow: 0 12px 30px rgba(64, 158, 255, 0.08); }
.mini-card-header { display: flex; justify-content: space-between; align-items: center; }
.tech-tag { font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 4px; }
.tech-tag.java { background: #e6f7ff; color: #1890ff; }
.tech-tag.vue { background: #f6ffed; color: #52c41a; }
.tech-tag.uniapp { background: #f9f0ff; color: #722ed1; }
.tech-tag.ai { background: #fff7e6; color: #fa8c16; }
.heat-index { font-size: 11px; color: #909399; font-weight: 500; }
.mini-job-title { font-size: 14px; color: #2c3e50; margin: 12px 0; font-weight: 600; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.mini-job-footer { display: flex; justify-content: space-between; align-items: center; font-size: 12px; }
.mini-salary { color: #ff7875; font-weight: 700; }
.mini-comp { color: #7f8c8d; }

/* ================== 悬浮数据看板区域布局 ================== */
.data-board-wrapper {
    width: 100%;
    position: relative;
    z-index: 10;
    margin-top: 40px; /* 在广告位和看板之间维持合理的透气间距 */
    padding: 0 20px;
    box-sizing: border-box;
}

.data-board { 
    background: #ffffff; 
    margin: 0 auto;
    max-width: 1120px; 
    border-radius: 16px; 
    box-shadow: 0 12px 40px rgba(10, 16, 32, 0.06); 
    border: 1px solid rgba(232, 236, 243, 0.6); 
}
.board-content { display: flex; justify-content: space-around; padding: 24px 0; }
.data-item { display: flex; flex-direction: column; align-items: center; }
.data-num { font-size: 28px; font-weight: 700; color: #409eff; font-family: 'Helvetica Neue', Arial, sans-serif; }
.data-label { font-size: 13px; color: #7f8c8d; margin-top: 6px; font-weight: 500; }
.divider { width: 1px; height: 42px; background-color: #ebeef5; }

/* 业务列表区域 */
.home-content { max-width: 1200px; margin: 56px auto 0 auto; padding: 0 20px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.title-area { display: flex; align-items: center; gap: 32px; }
.section-title { font-size: 22px; color: #1c2541; font-weight: 600; margin: 0; }
.category-tabs { display: flex; gap: 12px; }
.tab-item { font-size: 14px; color: #5c677d; cursor: pointer; padding: 6px 18px; border-radius: 20px; background: #f0f2f5; transition: all 0.25s; }
.tab-item.active { color: #fff; background-color: #409eff; font-weight: 500; box-shadow: 0 4px 12px rgba(64,158,255,0.3); }
.more-btn { font-size: 13px; color: #909399; cursor: pointer; transition: color 0.2s; }
.more-btn:hover { color: #409eff; }

.job-grid, .guide-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 26px; }
.company-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 26px; }
.company-card { background: #fff; border: 1px solid #eef0f5; border-radius: 14px; padding: 26px 18px; text-align: center; transition: all 0.3s ease; }
.company-card:hover { transform: translateY(-5px); box-shadow: 0 12px 32px rgba(0,0,0,0.05); border-color: #dcdfe6; }
.comp-logo { width: 64px; height: 64px; border-radius: 16px; margin-bottom: 12px; object-fit: cover; }
.comp-name { margin: 0 0 6px 0; font-size: 16px; color: #333; font-weight: 600; }
.comp-desc { margin: 0 0 16px 0; font-size: 12px; color: #909399; }
.comp-jobs-count { font-size: 13px; color: #606266; border-top: 1px dashed #e4e7ed; padding-top: 14px; }
.count-num { color: #409eff; font-weight: bold; }

.article-card { background: #fff; border-radius: 14px; padding: 26px; border: 1px solid #eef0f5; display: flex; flex-direction: column; justify-content: space-between; transition: all 0.25s; }
.article-card:hover { border-color: #b3d8ff; box-shadow: 0 8px 24px rgba(64,158,255,0.04); }
.article-tag { align-self: flex-start; background: #fdf6ec; color: #e6a23c; font-size: 11px; padding: 2px 10px; border-radius: 4px; margin-bottom: 14px; font-weight: 500; }
.article-title { font-size: 16px; margin: 0 0 12px 0; color: #303133; line-height: 1.4; font-weight: 600; }
.article-summary { font-size: 13px; color: #606266; line-height: 1.6; margin: 0 0 18px 0; }
.article-footer { display: flex; justify-content: space-between; font-size: 12px; color: #909399; }
.read-more { color: #409eff; cursor: pointer; font-weight: 500; }
/* 悬浮菜单外壳 */
.data-board-wrapper {
max-width: 1000px;
margin: 60px auto 30px; /* 向上微调，压在 Banner 下方（根据你原有的改动调整） */
padding: 0 20px;
position: relative;
z-index: 5;
}

.data-board {
background: #fff;
border-radius: 16px;
box-shadow: 0 10px 30px rgba(37, 99, 235, 0.08); /* 更柔和的大厂风阴影 */
border: 1px solid #eef0f5;
padding: 20px 30px;
}

.board-content {
display: flex;
justify-content: space-between;
align-items: center;
}

/* 改造后的可点击菜单项 */
.quick-menu-item {
flex: 1;
display: flex;
align-items: center;
gap: 16px; /* 图标与文字的间距 */
padding: 10px 20px;
border-radius: 12px;
cursor: pointer;
transition: all 0.25s ease;
}

/* 悬浮特效：背景变色、整体微微上移 */
.quick-menu-item:hover {
background: #f8fafc;
transform: translateY(-2px);
}

/* 增强点击反馈 */
.quick-menu-item:active {
transform: translateY(0);
}

/* 图标封装圈 */
.menu-icon {
font-size: 24px;
padding: 12px;
border-radius: 10px;
flex-shrink: 0;
}
.icon-blue { background: #eff6ff; color: #2563eb; }
.icon-green { background: #f0fdf4; color: #16a34a; }
.icon-purple { background: #f5f3ff; color: #7c3aed; }

/* 复合文字排版 */
.menu-info {
display: flex;
flex-direction: column;
gap: 4px;
}

.data-num {
font-size: 16px;
font-weight: 600;
color: #1e293b;
transition: color 0.2s;
}
/* 鼠标滑过时标题变色 */
.quick-menu-item:hover .data-num {
color: #2563eb;
}

.data-label {
font-size: 12px;
color: #94a3b8;
}

/* 分割线 */
.divider {
width: 1px;
height: 40px;
background: #e2e8f0;
margin: 0 10px;
}
</style>