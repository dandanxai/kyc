<template>
    <div class="error-container">
    <div class="error-content">
        <!-- 动画数字宇宙 -->
        <div class="error-code">
        <span>4</span>
        <div class="zero-space">
            <div class="radar-pulse"></div>
            <div class="planet">0</div>
        </div>
        <span>4</span>
        </div>

        <!-- 提示文案 -->
        <h2 class="error-title">灵之彼岸，触碰虚无</h2>
        <p class="error-desc">
        抱歉，你访问的页面似乎在闪聘的智慧矩阵中迷失了，或者已被系统平滑重构。
        </p>

        <!-- 操作按钮群 -->
        <div class="action-group">
        <el-button type="primary" size="large" round class="back-btn" @click="goHome">
            <el-icon><HomeFilled /></el-icon> 返回首页
        </el-button>
        <el-button size="large" round class="prev-btn" @click="goBack">
            <el-icon><Back /></el-icon> 返回上一页
        </el-button>
        </div>
    </div>

    <!-- 背景装饰科技流光 -->
    <div class="bg-glow glow-1"></div>
    <div class="bg-glow glow-2"></div>
    </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { HomeFilled, Back } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
    router.push('/')
}

const goBack = () => {
    router.back()
}
</script>

<style scoped>
.error-container {
    position: relative;
    width: 100%;
    height: 100vh;
    background-color: #0a1020; /* 科技深邃黑 */
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
}

.error-content {
    position: relative;
    z-index: 10;
    text-align: center;
    padding: 40px;
    max-width: 560px;
}

/* 404 炫酷数字特效 */
.error-code {
    font-size: 140px;
    font-weight: 900;
    color: #ffffff;
    line-height: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    letter-spacing: -4px;
    margin-bottom: 24px;
    user-select: none;
}

.error-code span {
    background: linear-gradient(135deg, #ffffff 30%, #a0aab5 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.zero-space {
    position: relative;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    margin: 0 10px;
}

.planet {
    background: linear-gradient(135deg, #409eff 0%, #00d2ff 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    animation: float 4s ease-in-out infinite;
}

.radar-pulse {
    position: absolute;
    width: 120px;
    height: 120px;
    border: 2px solid rgba(64, 158, 255, 0.3);
    border-radius: 50%;
    animation: pulse 2.5s cubic-bezier(0.215, 0.610, 0.355, 1) infinite;
    pointer-events: none;
}

/* 文案设计 */
.error-title {
    font-size: 24px;
    color: #ffffff;
    font-weight: 600;
    margin-bottom: 16px;
    letter-spacing: 1px;
}

.error-desc {
    font-size: 14px;
    color: #8c9ba5;
    line-height: 1.6;
    margin-bottom: 40px;
}

/* 按钮优雅过渡 */
.action-group {
    display: flex;
    gap: 16px;
    justify-content: center;
}

.back-btn {
    background: linear-gradient(135deg, #409eff 0%, #207eff 100%) !important;
    border: none !important;
    box-shadow: 0 4px 14px rgba(64, 158, 255, 0.4);
    transition: all 0.3s !important;
}

.back-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(64, 158, 255, 0.6);
}

.prev-btn {
    background: rgba(255, 255, 255, 0.08) !important;
    border: 1px solid rgba(255, 255, 255, 0.15) !important;
    color: #ffffff !important;
    transition: all 0.3s !important;
}

.prev-btn:hover {
    background: rgba(255, 255, 255, 0.15) !important;
    transform: translateY(-2px);
}

/* 霓虹极光背景装饰 */
.bg-glow {
    position: absolute;
    border-radius: 50%;
    filter: blur(140px);
    opacity: 0.15;
    pointer-events: none;
}

.glow-1 {
    width: 400px;
    height: 400px;
    background: #409eff;
    top: -100px;
    right: -100px;
}

.glow-2 {
    width: 500px;
    height: 500px;
    background: #00d2ff;
    bottom: -150px;
    left: -150px;
}

/* 动效 */
@keyframes float {
    0%, 100% { transform: translateY(0) rotate(0deg); }
    50% { transform: translateY(-10px) rotate(3deg); }
}

@keyframes pulse {
    0% { transform: scale(0.6); opacity: 0.8; }
    100% { transform: scale(1.4); opacity: 0; }
}
</style>