<template>
  <div class="archive-container">
    <!-- 1. 全局人才资产轻看板 -->
    <div class="archive-summary-cards">
      <div class="summary-card">
        <div class="sc-info">
          <span class="lbl">库内人才总数</span>
          <span class="val text-blue">1,248 位</span>
        </div>
        <div class="sc-icon bg-blue"><el-icon><User /></el-icon></div>
      </div>
      <div class="summary-card">
        <div class="sc-info">
          <span class="lbl">本周新入库</span>
          <span class="val text-green">+42 位</span>
        </div>
        <div class="sc-icon bg-green"><el-icon><TrendCharts /></el-icon></div>
      </div>
      <div class="summary-card">
        <div class="sc-info">
          <span class="lbl">高匹配度人才(>90%)</span>
          <span class="val text-purple">156 位</span>
        </div>
        <div class="sc-icon bg-purple"><el-icon><Star /></el-icon></div>
      </div>
    </div>

    <!-- 2. 多维高密搜索卡片 -->
    <el-card class="archive-filter-card" shadow="never">
      <el-form :model="filterForm" inline size="default" class="archive-filter-form">
        <el-form-item label="关键字">
          <el-input v-model="filterForm.keyword" placeholder="姓名 / 技术栈 / 院校" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="学历门槛">
          <el-select v-model="filterForm.edu" placeholder="不限学历" clearable style="width: 140px">
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士/博士" value="研究生" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类标记">
          <el-select v-model="filterForm.tag" placeholder="不限标签" clearable style="width: 140px">
            <el-option label="Java全栈" value="Java全栈" />
            <el-option label="Vue3精通" value="Vue3精通" />
            <el-option label="高潜应届生" value="高潜应届生" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-btns">
          <el-button type="primary" :icon="Search" @click="handleQuery">智能检索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 3. 人才高规表格大盘 -->
    <el-card class="archive-table-card" shadow="never">
      <!-- 表格上方控制工具链 -->
      <div class="archive-toolbar">
        <div class="toolbar-left">
          <el-button type="primary" plain :icon="Download" :disabled="selectedIds.length === 0">批量导出简历</el-button>
          <el-button type="warning" plain :icon="CollectionTag" :disabled="selectedIds.length === 0" @click="handleBatchTag">批量贴标签</el-button>
        </div>
        <div class="toolbar-right">
          <span class="tenant-info-text">当前隔离池：极客互娱专属私有库</span>
        </div>
      </div>

      <!-- 主数据表格 -->
      <el-table 
        :data="archiveList" 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        
        <!-- 人才画像复合列 -->
        <el-table-column label="人才基础画像" min-width="220">
          <template #default="scope">
            <div class="talent-profile-cell">
              <el-avatar :size="36" :src="scope.row.avatar" />
              <div class="profile-meta">
                <div class="name-line">
                  <span class="t-name">{{ scope.row.name }}</span>
                  <el-tag size="small" type="success" effect="light" class="match-badge">AI 评分 {{ scope.row.aiScore }}</el-tag>
                </div>
                <span class="t-sub">{{ scope.row.gender }} ｜ {{ scope.row.age }}岁 ｜ {{ scope.row.city }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 教育及工作背景 -->
        <el-table-column label="教育/经验资历" min-width="200">
          <template #default="scope">
            <div class="exp-cell">
              <span class="edu-text">🎓 {{ scope.row.school }} · {{ scope.row.edu }}</span>
              <span class="work-text">💼 资历：{{ scope.row.expYear }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 核心技能/标签库 -->
        <el-table-column label="标签画像" min-width="180">
          <template #default="scope">
            <div class="tag-cell-flex">
              <el-tag 
                v-for="(t, idx) in scope.row.tags" 
                :key="idx" 
                size="small" 
                :type="idx % 2 === 0 ? 'primary' : 'warning'"
                effect="plain"
              >
                {{ t }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="archiveDate" label="归档时间" width="120" />

        <!-- 悬浮精细化单项管理 -->
        <el-table-column label="资产管理" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" link type="primary" :icon="VideoPlay" @click="handleActivate(scope.row)">激活至流程</el-button>
            <el-button size="small" link type="info" :icon="Document" @click="handleViewResume(scope.row)">看简历</el-button>
            <el-dropdown trigger="click">
              <el-button size="small" link type="info" :icon="More" style="margin-left: 12px">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :icon="EditPen">修改评语</el-dropdown-item>
                  <el-dropdown-item :icon="Delete" style="color: #f43f5e" @click="handleRemove(scope.row)">移出人才库</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页控制 -->
      <div class="archive-pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="1248"
          :page-size="10"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Search, Refresh, Download, CollectionTag, User, TrendCharts, Star, VideoPlay, Document, More, EditPen, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const selectedIds = ref<number[]>([])

const filterForm = reactive({
  keyword: '',
  edu: '',
  tag: ''
})

// 模拟极其标准的数字化人才资产池大盘数据
const archiveList = ref([
  {
    id: 9001,
    name: '黄胜',
    avatar: 'https://picsum.photos/60/60?random=11',
    aiScore: 94,
    gender: '男',
    age: 21,
    city: '合肥',
    school: '滁州职业技术学院',
    edu: '大专',
    expYear: '应届生（Java全栈/跨端）',
    tags: ['Java全栈', 'Vue3精通', 'UniApp', '算法一等奖'],
    archiveDate: '2026-05-30'
  },
  {
    id: 9002,
    name: '王小丫',
    avatar: 'https://picsum.photos/60/60?random=12',
    aiScore: 89,
    gender: '女',
    age: 24,
    city: '合肥',
    school: '安徽大学',
    edu: '本科',
    expYear: '2年经验',
    tags: ['前端开发', 'ECharts大屏', 'ElementPlus'],
    archiveDate: '2026-05-28'
  },
  {
    id: 9003,
    name: '张铁柱',
    avatar: 'https://picsum.photos/60/60?random=13',
    aiScore: 91,
    gender: '男',
    age: 28,
    city: '上海/合肥',
    school: '中国科学技术大学',
    edu: '研究生',
    expYear: '5年经验',
    tags: ['SpringCloud', '高并发中间件', 'MySQL优化'],
    archiveDate: '2026-05-25'
  }
])

// 业务流操作
const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('智能筛选成功，已锁定最匹配的专属人才')
  }, 300)
}

const resetQuery = () => {
  filterForm.keyword = ''
  filterForm.edu = ''
  filterForm.tag = ''
  handleQuery()
}

const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleBatchTag = () => {
  ElMessage.success(`已成功为选中的 ${selectedIds.value.length} 位人才批量唤醒标签注入面板...`)
}

const handleActivate = (row: any) => {
  ElMessageBox.confirm(`确定要直接重置并激活候选人【${row.name}】的投递流程吗？激活后他将直接重返「新投递简历」首位。`, '流程激活提示', {
    confirmButtonText: '立即重召入队',
    cancelButtonText: '再想想',
    type: 'success'
  }).then(() => {
    ElMessage.success(`人才【${row.name}】流程已重新被激活，短信通知已下发！`)
  }).catch(() => {})
}

const handleViewResume = (row: any) => {
  ElMessage.info(`正在加载【${row.name}】附加的企业加密 PDF/图片 格式底册简历大图...`)
}

const handleRemove = (row: any) => {
  ElMessageBox.confirm(`警告：该操作将把【${row.name}】彻底从极客互娱的企业内部库移除。`, '移除资产确认', {
    confirmButtonText: '确定移除',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    archiveList.value = archiveList.value.filter(item => item.id !== row.id)
    ElMessage.warning('该候选人信息已从企业内部库彻底注销')
  }).catch(() => {})
}
</script>

<style scoped>
.archive-container {
  padding: 24px;
  background-color: #f8fafc;
  min-height: calc(100vh - 64px);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 顶部三大轻量宏观统计面板 */
.archive-summary-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.summary-card {
  background: #fff;
  border: 1px solid #eef0f5;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.sc-info { display: flex; flex-direction: column; gap: 4px; }
.sc-info .lbl { font-size: 13px; color: #64748b; font-weight: 500; }
.sc-info .val { font-size: 22px; font-weight: 700; color: #1e293b; }
.text-blue { color: #2563eb !important; }
.text-green { color: #16a34a !important; }
.text-purple { color: #9333ea !important; }

.sc-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}
.bg-blue { background: #eff6ff; color: #2563eb; }
.bg-green { background: #f0fdf4; color: #16a34a; }
.bg-purple { background: #faf5ff; color: #9333ea; }

/* 过滤框 */
.archive-filter-card {
  border-radius: 12px;
  border: 1px solid #eef0f5;
}
.archive-filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
:deep(.el-form-item) {
  margin-bottom: 0 !important;
  margin-right: 24px;
}
.filter-btns { margin-left: auto; margin-right: 0; }

/* 表格主体卡片 */
.archive-table-card {
  border-radius: 12px;
  border: 1px solid #eef0f5;
}
.archive-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.tenant-info-text {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

/* 复合人才单元格画像 */
.talent-profile-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.profile-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.name-line {
  display: flex;
  align-items: center;
  gap: 8px;
}
.t-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}
.match-badge {
  font-size: 10px;
  border-radius: 4px;
  padding: 0 4px;
  height: 18px;
  line-height: 18px;
}
.t-sub {
  font-size: 12px;
  color: #64748b;
}

/* 教育与履历单元格 */
.exp-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.edu-text {
  font-size: 13px;
  font-weight: 500;
  color: #334155;
}
.work-text {
  font-size: 12px;
  color: #64748b;
}

/* 标签云弹性流 */
.tag-cell-flex {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  max-width: 240px;
}
.tag-cell-flex :deep(.el-tag) {
  border-radius: 4px;
}

.archive-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-table th) {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 600;
}
</style>