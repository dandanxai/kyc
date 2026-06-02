<template>
  <div class="pipeline-container">
    <el-card class="pipeline-filter-card" shadow="never">
      <div class="filter-flex-box">
        <div class="search-left">
          <el-input
            v-model="searchKeyword"
            placeholder="输入候选人姓名 / 期望职位"
            :prefix-icon="Search"
            clearable
            style="width: 280px"
            @input="handleFilter"
          />
          <el-select v-model="filterJob" placeholder="按在招职位筛选" clearable style="width: 220px" @change="handleFilter">
            <el-option label="Vue3 前端研发工程师（跨端）" value="vue3" />
            <el-option label="Java 全栈开发工程师" value="java" />
          </el-select>
        </div>
        <div class="summary-right">
          <span class="summary-text">当前多租户活跃漏斗人才：<strong>{{ talentPipelines.reduce((acc, col) => acc + col.list.length, 0) }}</strong> 人</span>
        </div>
      </div>
    </el-card>

    <div class="pipeline-board-layout">
      
      <div 
        v-for="(column, colIdx) in talentPipelines" 
        :key="column.id" 
        class="pipeline-column"
        :class="column.id"
      >
        <div class="column-header">
          <div class="title-left">
            <span class="status-dot"></span>
            <span class="column-title">{{ column.name }}</span>
          </div>
          <el-badge :value="column.list.length" :type="column.badgeType" class="col-count-badge" />
        </div>

        <div class="column-card-scroller">
          <template v-if="column.list.length > 0">
            
            <div 
              v-for="candidate in column.list" 
              :key="candidate.id" 
              class="candidate-pipeline-card"
            >
              <div class="card-top-row">
                <div class="user-meta">
                  <el-avatar :size="32" :src="candidate.avatar" />
                  <div class="name-box">
                    <span class="c-name">{{ candidate.name }}</span>
                    <span class="c-tags">{{ candidate.exp }} ｜ {{ candidate.edu }}</span>
                  </div>
                </div>
                <el-tag 
                  size="small" 
                  :type="candidate.matchScore >= 85 ? 'success' : 'warning'" 
                  effect="plain"
                  class="score-tag"
                >
                  匹配度 {{ candidate.matchScore }}%
                </el-tag>
              </div>

              <div class="card-job-row">
                <span class="job-lbl">投递岗位：</span>
                <span class="job-val">{{ candidate.targetJob }}</span>
              </div>

              <div class="card-time-row">
                <span>投递/更新于：{{ candidate.updateTime }}</span>
              </div>

              <div class="card-actions-row">
                <el-button 
                  size="small" 
                  type="danger" 
                  plain 
                  :icon="CircleClose"
                  @click="handleTalentAction(candidate, 'reject', column.id)"
                >
                  淘汰
                </el-button>
                
                <el-button 
                  v-if="colIdx < talentPipelines.length - 1"
                  size="small" 
                  type="primary" 
                  :icon="CircleCheck"
                  @click="handleTalentAction(candidate, 'advance', column.id, colIdx)"
                >
                  晋级下阶段
                </el-button>
                <el-button
                  v-else
                  size="small"
                  type="success"
                  :icon="Checked"
                  @click="handleTalentAction(candidate, 'offer', column.id)"
                >
                  发 Offer
                </el-button>
              </div>
            </div>

          </template>
          <template v-else>
            <el-empty :image-size="60" description="暂无该阶段人才" class="col-empty" />
          </template>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search, CircleCheck, CircleClose, Checked } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索检索绑定键
const searchKeyword = ref('')
const filterJob = ref('')

// 模拟极其核心的四大漏斗管道完整数据集 (对接多租户简历表)
const rawPipelines = [
  {
    id: 'col-new',
    name: '新投递简历',
    badgeType: 'primary' as const,
    list: [
      { id: 201, name: '黄胜', exp: '应届生', edu: '大专', targetJob: 'Vue3 前端研发工程师（跨端）', matchScore: 92, avatar: 'https://picsum.photos/60/60?random=21', updateTime: '10分钟前' },
      { id: 202, name: '李强', exp: '3年经验', edu: '本科', targetJob: 'Java 全栈开发工程师', matchScore: 88, avatar: 'https://picsum.photos/60/60?random=22', updateTime: '1小时前' }
    ]
  },
  {
    id: 'col-screen',
    name: '简历筛选中',
    badgeType: 'warning' as const,
    list: [
      { id: 203, name: '张悦', exp: '1年经验', edu: '本科', targetJob: 'Vue3 前端研发工程师（跨端）', matchScore: 84, avatar: 'https://picsum.photos/60/60?random=23', updateTime: '昨天' }
    ]
  },
  {
    id: 'col-interview',
    name: '约聊与面试',
    badgeType: 'success' as const,
    list: [
      { id: 204, name: '赵六', exp: '5年经验', edu: '硕士', targetJob: 'Java 全栈开发工程师', matchScore: 95, avatar: 'https://picsum.photos/60/60?random=24', updateTime: '2天前' }
    ]
  },
  {
    id: 'col-offer',
    name: '录用待下发',
    badgeType: 'danger' as const,
    list: [
      { id: 205, name: '孙七', exp: '2年经验', edu: '大专', targetJob: '跨端 UI/UX 设计师', matchScore: 89, avatar: 'https://picsum.photos/60/60?random=25', updateTime: '5天前' }
    ]
  }
]

const talentPipelines = ref(JSON.parse(JSON.stringify(rawPipelines)))

// 前端快速条件联合交叉检索过滤器
const handleFilter = () => {
  // 深拷贝恢复基底
  const base = JSON.parse(JSON.stringify(rawPipelines))
  
  talentPipelines.value = base.map((column: any) => {
    column.list = column.list.filter((c: any) => {
      const matchKey = c.name.includes(searchKeyword.value) || c.targetJob.includes(searchKeyword.value)
      let matchJob = true
      if (filterJob.value === 'vue3') matchJob = c.targetJob.includes('Vue3')
      if (filterJob.value === 'java') matchJob = c.targetJob.includes('Java')
      return matchKey && matchJob
    })
    return column
  })
}

// 核心业务函数：晋级、淘汰、发Offer的全自动化流转机制
const handleTalentAction = (candidate: any, action: 'advance' | 'reject' | 'offer', currentColId: string, currentColIdx?: number) => {
  if (action === 'advance' && typeof currentColIdx === 'number') {
    // 晋级下个阶段
    const nextCol = talentPipelines.value[currentColIdx + 1]
    
    // 1. 从当前列摘除
    const curCol = talentPipelines.value.find(c => c.id === currentColId)
    if (curCol) curCol.list = curCol.list.filter(i => i.id !== candidate.id)
    
    // 2. 压入下一列头部，同步修正更新时间
    candidate.updateTime = '刚刚'
    nextCol.list.unshift(candidate)
    
    ElMessage.success(`候选人【${candidate.name}】已成功晋级至「${nextCol.name}」阶段！`)
  } 
  
  else if (action === 'reject') {
    // 淘汰流程拦截
    ElMessageBox.confirm(`确定要终止候选人【${candidate.name}】的本次岗位投递流程并归入淘汰库吗？系统将自动下发多租户通用拒绝信。`, '淘汰确认', {
      confirmButtonText: '确认淘汰',
      cancelButtonText: '留着看看',
      type: 'error'
    }).then(() => {
      const curCol = talentPipelines.value.find(c => c.id === currentColId)
      if (curCol) curCol.list = curCol.list.filter(i => i.id !== candidate.id)
      ElMessage.warning(`已将 ${candidate.name} 移入企业端淘汰人才夹。`)
    }).catch(() => {})
  } 
  
  else if (action === 'offer') {
    ElMessage.success(`🎉 正在为【${candidate.name}】生成由极客互娱签发的标准 SaaS 电子录用 Offer 报到通知...`)
  }
}
</script>

<style scoped>
.pipeline-container {
  padding: 24px;
  background-color: #f1f5f9; /* 稍微深一点的背景，更好地衬托出亮白色的看板列 */
  min-height: calc(100vh - 64px);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 顶部搜索 */
.pipeline-filter-card {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}
.filter-flex-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-left {
  display: flex;
  gap: 12px;
}
.summary-text {
  font-size: 13px;
  color: #64748b;
}
.summary-text strong {
  color: #2563eb;
  font-size: 15px;
}

/* 漏斗看板四列网格平衡布局 */
.pipeline-board-layout {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  align-items: start;
}

/* 单个大列的基础美化结构 */
.pipeline-column {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 180px);
}

/* 为不同的列头部赋予个性化彩条主题，防止HR发生视觉疲劳 */
.col-new { border-top: 4px solid #2563eb; }
.col-screen { border-top: 4px solid #eab308; }
.col-interview { border-top: 4px solid #10b981; }
.col-offer { border-top: 4px solid #ef4444; }

.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #edf2f7;
  margin-bottom: 12px;
}
.title-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}
.col-new .status-dot { background-color: #2563eb; }
.col-screen .status-dot { background-color: #eab308; }
.col-interview .status-dot { background-color: #10b981; }
.col-offer .status-dot { background-color: #ef4444; }

.column-title {
  font-weight: 700;
  color: #1e293b;
  font-size: 14px;
}

/* 看板列内容独立垂直滚动条（防止大盘卡片爆页拉垮整个浏览器） */
.column-card-scroller {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-right: 2px;
}

/* 💡 极其精致的单一求职者简历卡片样式设计 */
.candidate-pipeline-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);
  transition: all 0.2s ease-in-out;
}
.candidate-pipeline-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -4px rgba(0, 0, 0, 0.05);
  border-color: #cbd5e1;
}

.card-top-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.user-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}
.name-box {
  display: flex;
  flex-direction: column;
}
.c-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}
.c-tags {
  font-size: 11px;
  color: #64748b;
  margin-top: 1px;
}
.score-tag {
  font-weight: 600;
  border-radius: 4px;
}

.card-job-row {
  margin-top: 10px;
  font-size: 12px;
  line-height: 1.4;
}
.job-lbl { color: #94a3b8; }
.job-val { color: #475569; font-weight: 500; }

.card-time-row {
  margin-top: 8px;
  font-size: 11px;
  color: #94a3b8;
}

/* 卡片行内交互按钮区 */
.card-actions-row {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed #f1f5f9;
  display: flex;
  justify-content: space-between;
}
.card-actions-row :deep(.el-button) {
  padding: 4px 8px;
  height: 28px;
  font-size: 11px;
}

.col-empty {
  padding: 40px 0;
  background: transparent;
}

/* 美化自制滚动条组件 */
.column-card-scroller::-webkit-scrollbar { width: 4px; }
.column-card-scroller::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
.column-card-scroller::-webkit-scrollbar-track { background: transparent; }
</style>