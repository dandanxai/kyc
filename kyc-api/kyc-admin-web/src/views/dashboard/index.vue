<template>
  <div class="company-dashboard">
    <!-- 欢迎栏 -->
    <div class="welcome-bar">
      <div class="welcome-left">
        <h2>早安，招聘官 ✨</h2>
        <p>今天是 2026年6月1日，您在招的岗位上周末收获了不错的投递量，快去看看吧！</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="router.push('/positions')"
        >发布新职位</el-button
      >
    </div>

    <!-- 1. 核心招聘漏斗数据看板 -->
    <div class="data-funnel-grid">
      <div class="funnel-card">
        <span class="card-label">今日新投递</span>
        <div class="card-bottom">
          <span class="count-num blue">18</span>
          <span class="trend up">▲ 12% 较昨日</span>
        </div>
      </div>
      <div class="funnel-card">
        <span class="card-label">待筛选简历</span>
        <div class="card-bottom">
          <span class="count-num orange">34</span>
          <span class="trend">需加急处理</span>
        </div>
      </div>
      <div class="funnel-card">
        <span class="card-label">今日面试</span>
        <div class="card-bottom">
          <span class="count-num green">4</span>
          <span class="trend">2场视频/2场线下面试</span>
        </div>
      </div>
      <div class="funnel-card">
        <span class="card-label">在招职位</span>
        <div class="card-bottom">
          <span class="count-num purple">6</span>
          <span class="trend">3个急聘渠道已开通</span>
        </div>
      </div>
    </div>

    <!-- 2. 下方两栏核心业务布局 -->
    <div class="dashboard-body-layout">
      <!-- 左侧：加急待办与最新投递动态 -->
      <div class="body-left">
        <div class="business-card">
          <h3 class="card-title">🚨 加急处理任务</h3>
          <div class="todo-list">
            <div class="todo-item" @click="router.push('/talents')">
              <el-badge is-dot class="badge-item">
                <span class="todo-txt"
                  >【极客互娱】有 5 份针对「Vue3前端工程师」的新投递超过 24 小时未处理</span
                >
              </el-badge>
              <el-button size="small" link type="primary">去筛选</el-button>
            </div>
            <div class="todo-item" @click="router.push('/chat')">
              <span class="todo-txt"
                >求职者「黄胜」给您发了一条新消息，正等待您确认周五面试时间</span
              >
              <el-button size="small" link type="primary">去直聊</el-button>
            </div>
          </div>
        </div>

        <!-- 最新投递的简历简表 -->
        <div class="business-card margin-top-20">
          <h3 class="card-title">📥 最新投递动态</h3>
          <el-table :data="recentDeliveries" style="width: 100%" size="default">
            <el-table-column prop="candidate" label="候选人" width="120">
              <template #default="scope">
                <div class="candidate-cell">
                  <el-avatar :size="24" src="https://picsum.photos/50/50?random=10" />
                  <span class="c-name">{{ scope.row.candidate }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="targetJob" label="投递职位" />
            <el-table-column prop="experience" label="工作年限" width="100" />
            <el-table-column prop="education" label="学历" width="90" />
            <el-table-column prop="time" label="投递时间" width="140" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button
                  size="small"
                  type="primary"
                  plain
                  @click="handleAction(scope.row.id, 'view')"
                  >查看简历</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 右侧：招聘小助手 / 平台公告 -->
      <div class="body-right">
        <div class="business-card helper-box">
          <h4 class="helper-title">💡 智能化招人建议</h4>
          <div class="advice-item">
            <h5>优化职位关键词</h5>
            <p>将您的职位描述加上“双休”、“提供全栈平台”，简历曝光度能提高 40%。</p>
          </div>
          <div class="advice-item">
            <h5>多租户资源提醒</h5>
            <p>您当前所属的企业套餐为【黄金招聘会员】，本月还可免费下载 120 份精选在线简历。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 模拟最新投递的数据流
const recentDeliveries = ref([
  {
    id: 1,
    candidate: '黄胜',
    targetJob: 'Vue3 前端研发工程师（跨端）',
    experience: '应届生',
    education: '大专',
    time: '10分钟前'
  },
  {
    id: 2,
    candidate: '张三',
    targetJob: 'Java 全栈开发工程师',
    experience: '3年经验',
    education: '本科',
    time: '半小时前'
  },
  {
    id: 3,
    candidate: '李四',
    targetJob: 'Java 全栈开发工程师',
    experience: '1年经验',
    education: '本科',
    time: '1小时前'
  }
])

const handleAction = (id: number, type: string) => {
  if (type === 'view') {
    ElMessage.success('正在为您调取求职者在线简历详情视图...')
    router.push('/talents')
  }
}
</script>

<style scoped>
.company-dashboard {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 48px);
}

/* 顶部欢迎 */
.welcome-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.welcome-left h2 {
  margin: 0 0 4px;
  font-size: 22px;
  color: #1e293b;
}
.welcome-left p {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

/* 数据漏斗网格 */
.data-funnel-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.funnel-card {
  background: #fff;
  border: 1px solid #eef0f5;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.01);
}
.card-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}
.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 12px;
}
.count-num {
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}
.count-num.blue {
  color: #2563eb;
}
.count-num.orange {
  color: #ea580c;
}
.count-num.green {
  color: #16a34a;
}
.count-num.purple {
  color: #7c3aed;
}
.trend {
  font-size: 11px;
  color: #94a3b8;
}
.trend.up {
  color: #16a34a;
  font-weight: 600;
}

/* 下方主布局 */
.dashboard-body-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 20px;
  align-items: start;
}
.business-card {
  background: #fff;
  border: 1px solid #eef0f5;
  border-radius: 12px;
  padding: 20px;
}
.margin-top-20 {
  margin-top: 20px;
}
.card-title {
  margin: 0 0 16px;
  font-size: 15px;
  color: #1e293b;
}

/* 待办流 */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff1f2;
  border: 1px solid #ffe4e6;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}
.todo-item:hover {
  border-color: #f43f5e;
  background: #fff5f5;
}
.todo-txt {
  font-size: 13px;
  color: #991b1b;
  line-height: 1.4;
  padding-right: 10px;
}

/* 候选人表格单元格 */
.candidate-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.c-name {
  font-weight: 600;
  color: #334155;
  font-size: 13px;
}

/* 右侧小助手 */
.helper-box {
  background: #fff7ed;
  border-color: #ffedd5;
}
.helper-title {
  margin: 0 0 16px;
  font-size: 14px;
  color: #ea580c;
}
.advice-item {
  margin-bottom: 16px;
}
.advice-item:last-child {
  margin-bottom: 0;
}
.advice-item h5 {
  margin: 0 0 4px;
  font-size: 13px;
  color: #c2410c;
}
.advice-item p {
  margin: 0;
  font-size: 12px;
  color: #7c2d12;
  line-height: 1.6;
}

/* 微调 Element 表格样式 */
:deep(.el-table th) {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 600;
  font-size: 13px;
}
</style>
