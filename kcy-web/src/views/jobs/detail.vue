<template>
    <div class="job-detail-container">
    
    <div class="job-header-banner">
        <div class="banner-inner">
        <div class="job-brief">
            <div class="status-tags">
            <span class="status-badge recruitment">🔥 热招中</span>
            <span class="status-badge feedback">⚡ 实时反馈率 99.4%</span>
            </div>
            <h1 class="job-title-text">{{ jobInfo.title }}</h1>
            <div class="job-meta-props">
            <span class="salary-txt">{{ jobInfo.salary }}</span>
            <span class="prop-divider">|</span>
            <span><el-icon><Location /></el-icon> {{ jobInfo.city }}</span>
            <span><el-icon><Calendar /></el-icon> {{ jobInfo.experience }}</span>
            <span><el-icon><Opportunity /></el-icon> {{ jobInfo.education }}</span>
            </div>
            <div class="job-tags-row">
            <el-tag v-for="tag in jobInfo.tags" :key="tag" class="geek-tag" size="small">
                {{ tag }}
            </el-tag>
            </div>
        </div>
        </div>
    </div>

    <div class="job-detail-body">
        <div class="body-inner-grid">
        
        <div class="detail-left-main">
            
            <div class="info-section">
            <h3 class="section-title-line">📋 岗位职责</h3>
            <div class="rich-text-box">
                <p v-for="(item, idx) in jobInfo.responsibilities" :key="idx">
                {{ idx + 1 }}. {{ item }}
                </p>
            </div>
            </div>

            <div class="info-section">
            <h3 class="section-title-line">🛠️ 任职要求</h3>
            <div class="rich-text-box">
                <p v-for="(item, idx) in jobInfo.requirements" :key="idx">
                {{ idx + 1 }}. {{ item }}
                </p>
            </div>
            </div>

            <div class="info-section">
            <h3 class="section-title-line">🎁 专属福利</h3>
            <div class="welfare-grid">
                <div v-for="w in jobInfo.welfares" :key="w" class="welfare-item">
                <span class="welfare-icon">✨</span> {{ w }}
                </div>
            </div>
            </div>

        </div>

        <div class="detail-right-sidebar">
            
            <div class="action-console-card">
            <h4 class="console-title">🎯 求职极速协同</h4>
            <p class="console-tip">简历将直接投递至技术负责人，预计 24 小时内反馈。</p>
            
            <div class="btn-group-vertical">
                <button class="action-btn chat-btn" @click="handleChat">
                <el-icon><ChatLineRound /></el-icon> 立即沟通
                </button>
                
                <button 
                :class="['action-btn', 'delivery-btn', { 'delivered': isDelivered }]" 
                @click="handleDelivery"
                :disabled="isDelivered"
                >
                <el-icon><Position v-if="!isDelivered" /><CircleCheck v-else /></el-icon> 
                {{ isDelivered ? '已成功投递简历' : '一键投递简历' }}
                </button>
            </div>

            <div class="action-footer-btns">
                <span class="sub-act-btn" @click="toggleCollect">
                <el-icon :class="{ 'collected-icon': isCollected }">
                    <StarFilled v-if="isCollected" /><Star v-else />
                </el-icon>
                {{ isCollected ? '已收藏' : '加入收藏' }}
                </span>
                <span class="sub-act-btn-divider"></span>
                <span class="sub-act-btn" @click="handleShare">
                <el-icon><Share /></el-icon> 分享职位
                </span>
            </div>
            </div>

            <div class="company-profile-card">
            <div class="comp-header">
                <img :src="jobInfo.companyLogo" class="sidebar-comp-logo" />
                <div class="comp-title-box">
                <h4 class="sidebar-comp-name">{{ jobInfo.companyName }}</h4>
                <p class="sidebar-comp-meta">{{ jobInfo.industry }} · {{ jobInfo.scale }}</p>
                </div>
            </div>
            <div class="comp-intro-short">
                {{ jobInfo.companyDesc }}
            </div>
            <button class="view-comp-btn" @click="goToCompany">
                查看公司详情 <el-icon><ArrowRight /></el-icon>
            </button>
            </div>

        </div>

        </div>
    </div>

    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
    Location, Calendar, Opportunity, ChatLineRound, 
    Position, CircleCheck, Star, StarFilled, Share, ArrowRight 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const isDelivered = ref(false)
const isCollected = ref(false)

// 模拟完整职位详情数据 (深度契合 Java / Vue 研发线生态)
const jobInfo = ref({
    id: 1,
    title: 'Java 后端架构开发专家',
    salary: '15K - 25K',
    city: '合肥 · 蜀山区',
    experience: '1-3年',
    education: '大专及以上',
    companyName: '科大智讯数字软件股份有限公司',
    companyLogo: 'https://picsum.photos/120/120?random=42',
    industry: '大数据 / 云原生',
    scale: '500-999人',
    companyDesc: '国家级高新技术企业，专注于物联网管理中台、智慧园区及局部AI大模型中台底层架构研发，为全行业提供高并发多端技术生态底座支撑。',
    tags: ['Spring Boot', 'MyBatis-Plus', 'Redis', 'MySQL', '双休', '五险一金'],
    responsibilities: [
    '负责公司闪聘系统 C 端与 B 端企业协同工作台后端核心业务系统的重构优化与日常演进。',
    '基于 Spring Boot 与 MyBatis-Plus 架构体系进行高性能、高复用性 monolithic 业务中台设计与研发。',
    '深度参与数据库（MySQL）底层建模、索引精细化调优，确保核心查询组件在千万级数据量下具备毫秒级响应响应速度。',
    '配合前端开发专家（Vue3 / TS / UniApp）完成高标准、规范化的 RESTful API 协议设计与高效端到端对接。'
    ],
    requirements: [
    '计算机科学、软件技术相关专业毕业，具备扎实的 Java 编程功底与面向对象设计思想。',
    '精通 Spring Boot 生态及 MyBatis-Plus 组件，熟悉主流拦截器、多租户及动态数据源切换机制。',
    '熟练掌握 Redis 缓存策略，拥有大并发削峰填谷、防击穿击穿、防雪崩实战改造经验者优先。',
    '加分项：具备校赛、全国大型软件设计竞赛（如“中国软件杯”、AI算法及创新创业大赛）获奖经验，或有线上全栈大型重构作品者优先考虑。'
    ],
    welfares: ['年终丰厚奖金', '高端极客弹性工作制', '不限量咖啡下午茶', '全额五险一金', '定期大厂技术沙龙交流']
})

onMounted(() => {
    // 实际开发中，在这里通过路由参数获取 ID 请求后端接口
    const jobId = route.params.id
    console.log('当前装载的职位ID: ', jobId)
    window.scrollTo({ top: 0, behavior: 'smooth' })
})

// 交互逻辑
const handleChat = () => {
    ElMessage.success('正在为您唤醒在线即时通讯协同面板...')
}

const handleDelivery = () => {
    isDelivered.value = true
    ElMessage({
    message: '🚀 简历投递成功！已为您锁定该岗位，请保持手机畅通。',
    type: 'success',
    })
}

const toggleCollect = () => {
    isCollected.value = !isCollected.value
    ElMessage({
    message: isCollected.value ? '⭐ 职位收藏成功' : '已取消收藏',
    type: 'info'
    })
}

const handleShare = () => {
    ElMessage.success('职位动态分享链接已复制到剪贴板！')
}

const goToCompany = () => {
    router.push('/companies')
}
</script>

<style scoped>
.job-detail-container {
    width: 100%;
    background-color: #f7f9fb;
    padding-bottom: 80px;
}

/* ================== 1. 职位头饰巨幕区样式 ================== */
.job-header-banner {
    background: linear-gradient(135deg, #161e35 0%, #111827 100%);
    padding: 100px 20px 48px 20px; /* 留出 100px 承接顶部 fixed 毛玻璃导航 */
    color: #ffffff;
    text-align: left;
}
.banner-inner {
    max-width: 1120px;
    margin: 0 auto;
}
.status-tags {
    display: flex;
    gap: 10px;
    margin-bottom: 16px;
}
.status-badge {
    font-size: 11px;
    font-weight: 600;
    padding: 4px 10px;
    border-radius: 4px;
}
.status-badge.recruitment { background: rgba(64, 158, 255, 0.2); color: #409eff; border: 1px solid rgba(64, 158, 255, 0.3); }
.status-badge.feedback { background: rgba(82, 196, 26, 0.15); color: #52c41a; border: 1px solid rgba(82, 196, 26, 0.2); }

.job-title-text {
    font-size: 32px;
    font-weight: 700;
    margin: 0 0 16px 0;
    letter-spacing: 0.5px;
}

.job-meta-props {
    display: flex;
    align-items: center;
    gap: 16px;
    font-size: 15px;
    color: rgba(255, 255, 255, 0.85);
    margin-bottom: 20px;
}
.job-meta-props .salary-txt {
    font-size: 24px;
    font-weight: 700;
    color: #ff7875;
}
.prop-divider {
    color: rgba(255, 255, 255, 0.2);
}
.job-meta-props .el-icon {
    vertical-align: -1px;
    margin-right: 2px;
}

.job-tags-row {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}
.geek-tag {
    background: rgba(255, 255, 255, 0.08) !important;
    color: rgba(255, 255, 255, 0.8) !important;
    border: 1px solid rgba(255, 255, 255, 0.15) !important;
    border-radius: 4px;
}

/* ================== 2. 双栏主网格布局样式 ================== */
.job-detail-body {
    max-width: 1120px;
    margin: 32px auto 0 auto;
    padding: 0 20px;
    box-sizing: border-box;
}
.body-inner-grid {
    display: grid;
    grid-template-columns: 2.1fr 0.9fr;
    gap: 24px;
    align-items: start;
}

/* 左侧详情区基础板 */
.detail-left-main {
    background: #ffffff;
    border-radius: 16px;
    padding: 36px;
    border: 1px solid rgba(232, 236, 243, 0.7);
    box-shadow: 0 8px 24px rgba(10, 16, 32, 0.02);
    text-align: left;
}

.info-section {
    margin-bottom: 40px;
}
.info-section:last-child {
    margin-bottom: 0;
}

.section-title-line {
    font-size: 18px;
    color: #1c2541;
    font-weight: 600;
    margin: 0 0 20px 0;
    padding-bottom: 12px;
    border-bottom: 1px dashed #e8ecf3;
}

.rich-text-box p {
    font-size: 15px;
    color: #4c5a70;
    line-height: 1.8;
    margin: 0 0 12px 0;
}
.rich-text-box p:last-child {
    margin-bottom: 0;
}

/* 福利格子 */
.welfare-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 14px;
}
.welfare-item {
    background: #f8fafc;
    border-radius: 8px;
    padding: 12px 16px;
    font-size: 14px;
    color: #5c677d;
    border: 1px solid #edf2f7;
    display: flex;
    align-items: center;
    gap: 8px;
}
.welfare-icon {
    color: #409eff;
}

/* ================== 右侧边栏控制面板 ================== */
.detail-right-sidebar {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

/* 求职控制台卡片 */
.action-console-card {
    background: #ffffff;
    border-radius: 16px;
    padding: 24px;
    border: 1px solid rgba(232, 236, 243, 0.7);
    box-shadow: 0 8px 24px rgba(10, 16, 32, 0.02);
    text-align: left;
}
.console-title {
    font-size: 16px;
    color: #1c2541;
    font-weight: 600;
    margin: 0 0 8px 0;
}
.console-tip {
    font-size: 13px;
    color: #7f8c8d;
    line-height: 1.5;
    margin: 0 0 20px 0;
}

.btn-group-vertical {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 18px;
}

.action-btn {
    width: 100%;
    height: 44px;
    border-radius: 8px;
    border: none;
    font-size: 15px;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    cursor: pointer;
    transition: all 0.25s ease;
}

.chat-btn {
    background: #f0f4ff;
    color: #409eff;
    border: 1px solid rgba(64, 158, 255, 0.2);
}
.chat-btn:hover {
    background: #409eff;
    color: #ffffff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.delivery-btn {
    background: linear-gradient(90deg, #409eff 0%, #007fff 100%);
    color: #ffffff;
    box-shadow: 0 4px 14px rgba(0, 127, 255, 0.25);
}
.delivery-btn:hover {
    opacity: 0.95;
    transform: translateY(-1px);
    box-shadow: 0 6px 18px rgba(0, 127, 255, 0.35);
}
.delivery-btn:active {
    transform: translateY(1px);
}

/* 已投递置灰状态 */
.delivery-btn.delivered {
    background: #52c41a !important;
    color: #ffffff !important;
    box-shadow: none !important;
    cursor: not-allowed;
}

/* 点赞分享附属控制区 */
.action-footer-btns {
    display: flex;
    justify-content: space-around;
    align-items: center;
    border-top: 1px solid #f0f2f5;
    padding-top: 14px;
}
.sub-act-btn {
    font-size: 13px;
    color: #606266;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: color 0.2s;
}
.sub-act-btn:hover {
    color: #409eff;
}
.sub-act-btn-divider {
    width: 1px;
    height: 14px;
    background-color: #e8ecf3;
}
.collected-icon {
    color: #fadb14 !important;
    animation: scalePop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
@keyframes scalePop {
    0% { transform: scale(0.8); }
    50% { transform: scale(1.2); }
    100% { transform: scale(1); }
}

/* 企业资料名片 */
.company-profile-card {
    background: #ffffff;
    border-radius: 16px;
    padding: 24px;
    border: 1px solid rgba(232, 236, 243, 0.7);
    box-shadow: 0 8px 24px rgba(10, 16, 32, 0.02);
    text-align: left;
}
.comp-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 14px;
}
.sidebar-comp-logo {
    width: 48px;
    height: 48px;
    border-radius: 10px;
    object-fit: cover;
    border: 1px solid #edf2f7;
}
.sidebar-comp-name {
    font-size: 15px;
    color: #2c3e50;
    font-weight: 600;
    margin: 0 0 4px 0;
}
.sidebar-comp-meta {
    font-size: 12px;
    color: #909399;
    margin: 0;
}
.comp-intro-short {
    font-size: 13px;
    color: #606266;
    line-height: 1.6;
    margin-bottom: 18px;
}

.view-comp-btn {
    width: 100%;
    height: 36px;
    background: #ffffff;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    font-size: 13px;
    color: #606266;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    transition: all 0.2s;
}
.view-comp-btn:hover {
    color: #409eff;
    border-color: #b3d8ff;
    background: #f5f7fa;
}
</style>