<template>
    <div class="job-card" @click="goToDetail">
    <div class="card-header">
        <div class="job-title-box">
        <h3 class="job-title">{{ job.title }}</h3>
        <span class="job-props">{{ job.city }} · {{ job.experience }} · {{ job.education }}</span>
        </div>
        <span class="job-salary">{{ job.salary }}</span>
    </div>

    <div class="card-tags">
        <el-tag v-for="tag in job.tags" :key="tag" size="small" type="info" class="tag-item">
        {{ tag }}
        </el-tag>
    </div>

    <div class="card-footer">
        <div class="company-info">
        <img :src="job.companyLogo" alt="logo" class="company-logo" />
        <span class="company-name">{{ job.companyName }}</span>
        </div>
        <span class="publish-time">{{ job.publishTime }}</span>
    </div>
    </div>
</template>

<script setup lang="ts">
import type { JobItem } from '@/types/job'

// 接收父组件传过来的 job 数据，并绑定上面定义好的 TS 类型
const props = defineProps<{
    job: JobItem
}>()

const goToDetail = () => {
    // 这里写点击卡片跳转到详情页的逻辑，比如：router.push(`/job/${props.job.id}`)
    console.log('跳转到职位详情，ID为:', props.job.id)
}
</script>

<style scoped>
.job-card {
    background: #fff;
    border: 1px solid #eef0f5;
    border-radius: 8px;
    padding: 20px;
    cursor: pointer;
    transition: all 0.2s linear;
}
.job-card:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    border-color: #c0c4cc;
    transform: translateY(-2px);
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
}
.job-title {
    margin: 0 0 6px 0;
    font-size: 16px;
    color: #222;
    font-weight: 600;
}
.job-props {
    font-size: 13px;
    color: #666;
}
.job-salary {
    font-size: 16px;
    color: #fe5722;
    font-weight: 600;
}
.card-tags {
    margin: 14px 0;
}
.tag-item {
    margin-right: 8px;
}
.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px inset #f8f9fa;
    padding-top: 12px;
    margin-top: 12px;
}
.company-info {
    display: flex;
    align-items: center;
}
.company-logo {
    width: 28px;
    height: 28px;
    border-radius: 4px;
    margin-right: 8px;
    object-fit: cover;
}
.company-name {
    font-size: 14px;
    color: #333;
}
.publish-time {
    font-size: 12px;
    color: #999;
}
</style>