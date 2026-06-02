<template>
    <div class="custom-search-container">
    <div class="search-capsule">
        <!-- 输入框部分 -->
        <div class="input-wrapper">
        <el-icon class="prefix-search-icon"><Search /></el-icon>
        <el-input
            v-model="searchKeyword"
            placeholder="搜索职位、大厂、技术方向..."
            class="pure-input"
            clearable
            @keyup.enter="handleSearch"
        />
        </div>
        
        <!-- 自定义高级极光感按钮 -->
        <button class="aurora-btn" @click="handleSearch">
        <span>搜索</span>
        <el-icon class="btn-arrow"><Position /></el-icon>
        </button>
    </div>
    
    <!-- 热门搜索气泡 -->
    <div class="hot-tags-tray">
        <span class="label-txt">热搜：</span>
        <span 
        v-for="word in hotWords" 
        :key="word" 
        class="glass-tag"
        @click="clickHotWord(word)"
        >
        {{ word }}
        </span>
    </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search, Position } from '@element-plus/icons-vue'

const emit = defineEmits<{
    (e: 'search', keyword: string): void
}>()

const searchKeyword = ref('')
const hotWords = ref(['Java', 'Vue3', 'Spring Boot', '前端开发', '人工智能'])

const handleSearch = () => {
    if (!searchKeyword.value.trim()) return
    emit('search', searchKeyword.value.trim())
}

const clickHotWord = (word: string) => {
    searchKeyword.value = word
    handleSearch()
}
</script>

<style scoped>
.custom-search-container {
    width: 100%;
}

/* 胶囊一体化高拟真框体 */
.search-capsule {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.25);
    border-radius: 100px; /* 极致圆角胶囊形态 */
    padding: 6px 6px 6px 24px;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 聚焦时的外层微光呼吸效果 */
.search-capsule:focus-within {
    border-color: rgba(64, 158, 255, 0.6);
    background: rgba(255, 255, 255, 0.2);
    box-shadow: 0 12px 40px rgba(64, 158, 255, 0.25);
}

.input-wrapper {
    display: flex;
    align-items: center;
    flex: 1;
}

.prefix-search-icon {
    color: rgba(255, 255, 255, 0.75);
    font-size: 20px;
    margin-right: 12px;
}

/* 彻底剥离 Element Plus 丑陋的默认边框和背景 */
.pure-input :deep(.el-input__wrapper) {
    background-color: transparent !important;
    box-shadow: none !important;
    padding: 0 !important;
    height: 48px;
}

.pure-input :deep(.el-input__inner) {
    color: #ffffff !important;
    font-size: 16px;
    font-weight: 400;
}

/* 改变 Placeholder（提示词）颜色为优雅的半透明白 */
.pure-input :deep(.el-input__inner::placeholder) {
    color: rgba(255, 255, 255, 0.55);
}

/* 隐藏清空按钮的原生生硬样式并美化 */
.pure-input :deep(.el-input__clear) {
    color: rgba(255, 255, 255, 0.6);
    font-size: 16px;
}
.pure-input :deep(.el-input__clear:hover) {
    color: #ffffff;
}

/* 自定义极光感、充满爆发力的按钮 */
.aurora-btn {
    height: 48px;
    padding: 0 28px;
    border-radius: 100px;
    border: none;
    background: linear-gradient(90deg, #409eff 0%, #007fff 100%);
    color: #ffffff;
    font-size: 16px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    box-shadow: 0 4px 15px rgba(0, 127, 255, 0.3);
    transition: all 0.3s ease;
}

.aurora-btn:hover {
    background: linear-gradient(90deg, #66b1ff 0%, #409eff 100%);
    box-shadow: 0 6px 20px rgba(0, 127, 255, 0.5);
    transform: translateY(-1px);
}

.aurora-btn:active {
    transform: translateY(1px);
}

/* 按钮内小箭头的交互微动效 */
.btn-arrow {
    font-size: 16px;
    transition: transform 0.3s ease;
}
.aurora-btn:hover .btn-arrow {
    transform: translate(2px, -2px);
}

/* 热门搜索磨砂流线标签 */
.hot-tags-tray {
    margin-top: 16px;
    font-size: 13px;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
    padding-left: 12px;
}

.label-txt {
    color: rgba(255, 255, 255, 0.5);
    font-weight: 500;
}

.glass-tag {
    color: rgba(255, 255, 255, 0.85);
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.1);
    padding: 4px 14px;
    border-radius: 30px;
    cursor: pointer;
    transition: all 0.25s ease;
}

.glass-tag:hover {
    background: rgba(64, 158, 255, 0.25);
    border-color: rgba(64, 158, 255, 0.4);
    color: #ffffff;
    transform: translateY(-1px);
}
</style>