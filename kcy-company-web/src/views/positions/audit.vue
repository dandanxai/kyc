<template>
  <div class="audit-container">
    <!-- 顶部状态引导 -->
    <div class="audit-header-tip">
      <div class="tip-content">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
        <div class="text-group">
          <span class="title">职位审核机制说明</span>
          <span class="desc">为保障平台求职者权益，新发布或重大修改的岗位需经合规审核。审核通过后将自动转入「在招职位列表」并公开曝光。</span>
        </div>
      </div>
    </div>

    <!-- 核心标签页切换 -->
    <el-tabs v-model="activeTab" class="audit-tabs" type="card">
      
      <!-- 栏目一：审核中 -->
      <el-tab-pane name="submitting">
        <template #label>
          <div class="tab-label-item">
            <span>审核中</span>
            <el-badge :value="auditList.filter(i => i.auditStatus === 0).length" type="primary" class="badge-dot" />
          </div>
        </template>
        <el-table :data="filterList(0)" style="width: 100%" v-loading="loading">
          <el-table-column label="申请岗位" min-width="200">
            <template #default="scope">
              <div class="position-cell">
                <span class="p-name">{{ scope.row.title }}</span>
                <div class="p-tags">
                  <el-tag size="small" type="info">{{ scope.row.departmentName }}</el-tag>
                  <el-tag size="small" type="warning" effect="plain">{{ scope.row.salaryStr }}</el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="city" label="办公城市" width="120" />
          <el-table-column prop="submitTime" label="提交时间" width="160" />
          <el-table-column label="当前状态" width="150">
            <template #default>
              <el-tag type="primary" effect="light" class="status-tag">
                <span class="dot pulse-blue"></span>
                平台审核中
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button size="small" link type="primary" @click="handleView(scope.row)">查看详情</el-button>
              <el-button size="small" link type="info" @click="handleWithdraw(scope.row)">撤回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 栏目二：已驳回 -->
      <el-tab-pane name="rejected">
        <template #label>
          <div class="tab-label-item">
            <span>未通过</span>
            <el-badge :value="auditList.filter(i => i.auditStatus === 2).length" type="danger" class="badge-dot" />
          </div>
        </template>
        <el-table :data="filterList(2)" style="width: 100%" v-loading="loading">
          <el-table-column label="驳回岗位" min-width="200">
            <template #default="scope">
              <div class="position-cell">
                <span class="p-name text-muted">{{ scope.row.title }}</span>
                <div class="p-tags">
                  <el-tag size="small" type="info">{{ scope.row.departmentName }}</el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="拒绝时间" width="160" />
          <el-table-column label="驳回原因" min-width="220">
            <template #default="scope">
              <el-tooltip :content="scope.row.rejectReason" placement="top" effect="dark">
                <div class="reject-reason-box">
                  <el-icon type="danger"><Warning /></el-icon>
                  <span class="reason-text">{{ scope.row.rejectReason }}</span>
                </div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button size="small" link type="danger" @click="handleReEdit(scope.row)">重新编辑</el-button>
              <el-button size="small" link type="info" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 栏目三：草稿箱 -->
      <el-tab-pane name="drafts">
        <template #label>
          <div class="tab-label-item">
            <span>草稿箱</span>
            <el-badge :value="auditList.filter(i => i.auditStatus === 3).length" type="info" class="badge-dot" />
          </div>
        </template>
        <el-table :data="filterList(3)" style="width: 100%" v-loading="loading">
          <el-table-column label="草稿名称" min-width="200">
            <template #default="scope">
              <div class="position-cell">
                <span class="p-name">{{ scope.row.title || '（未命名职位草稿）' }}</span>
                <span class="p-desc">{{ scope.row.requirement ? scope.row.requirement.substring(0, 30) + '...' : '暂无任职描述' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="departmentName" label="拟定部门" width="140" />
          <el-table-column prop="submitTime" label="最后保存" width="160" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button size="small" link type="primary" :icon="Edit" @click="handleResumeDraft(scope.row)">继续编辑</el-button>
              <el-button size="small" link type="danger" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请快照详情" width="500px" append-to-body>
      <div v-if="selectedRow" class="audit-detail-md">
        <p><strong>岗位名称：</strong>{{ selectedRow.title }}</p>
        <p><strong>归属部门：</strong>{{ selectedRow.departmentName }}</p>
        <p><strong>薪资待遇：</strong>{{ selectedRow.salaryStr }}</p>
        <p><strong>工作城市：</strong>{{ selectedRow.city }}</p>
        <p><strong>职责描述：</strong></p>
        <div class="req-block">{{ selectedRow.requirement }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { InfoFilled, Warning, Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('submitting')
const loading = ref(false)
const detailVisible = ref(false)
const selectedRow = ref<any>(null)

// 模拟后端职位审核流大盘模型 (auditStatus: 0-审核中, 2-被驳回, 3-草稿箱)
const auditList = ref([
  {
    id: 5001,
    title: '高级 Java 后端架构师（多租户 SaaS 方向）',
    departmentName: '技术研发部',
    city: '合肥',
    salaryStr: '20K-30K',
    requirement: '负责多租户底层数据隔离机制的设计与核心中间件的优化，要求精通 Spring Boot 架构。',
    submitTime: '2026-06-01',
    auditStatus: 0,
    rejectReason: ''
  },
  {
    id: 5002,
    title: '小红书/抖音信息流海外运营',
    departmentName: '运营市场部',
    city: '远程/合肥',
    salaryStr: '5K-8K',
    requirement: '负责海外媒体矩阵的搭建和引流。',
    submitTime: '2026-05-28',
    auditStatus: 2,
    rejectReason: '依据最新互联网合规条例，职位描述中包含模糊的“引流”敏感词汇，请明确合规运营范围。'
  },
  {
    id: 5003,
    title: '前端实习生（Vue2/Vue3 熟手）',
    departmentName: '技术研发部',
    city: '合肥',
    salaryStr: '3K-5K',
    requirement: '协助参与企业端组件化改造和 ECharts 数字化看板的日常维护工作。',
    submitTime: '2026-06-01',
    auditStatus: 3,
    rejectReason: ''
  }
])

// 过滤对应状态的数据
const filterList = (status: number) => {
  return auditList.value.filter(item => item.auditStatus === status)
}

// 业务交互逻辑
const handleView = (row: any) => {
  selectedRow.value = row
  detailVisible.value = true
}

const handleWithdraw = (row: any) => {
  ElMessageBox.confirm(`确定要撤回职位【${row.title}】的发布申请吗？撤回后它将退回到草稿箱。`, '系统提示', {
    confirmButtonText: '确定撤回',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.auditStatus = 3 // 变回草稿
    ElMessage.success('已成功撤回至草稿箱')
  }).catch(() => {})
}

const handleReEdit = (row: any) => {
  ElMessage.info(`正在将已被驳回的 [${row.title}] 数据重新载入编辑工作流...`)
}

const handleResumeDraft = (row: any) => {
  ElMessage.success(`已唤醒草稿箱，正在恢复 [${row.title}] 的编辑进度`)
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要永久删除此条审核记录/草稿吗？此操作不可逆。', '警示', {
    confirmButtonText: '删除',
    cancelButtonText: '点错了',
    type: 'error'
  }).then(() => {
    auditList.value = auditList.value.filter(item => item.id !== row.id)
    ElMessage.success('数据已彻底清除')
  }).catch(() => {})
}
</script>

<style scoped>
.audit-container {
  padding: 24px;
  background-color: #f8fafc;
  min-height: calc(100vh - 64px);
}

/* 顶部指引框 */
.audit-header-tip {
  background-color: #fff;
  border: 1px solid #eef0f5;
  border-left: 4px solid #2563eb;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 24px;
}
.tip-content {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}
.info-icon {
  font-size: 20px;
  color: #2563eb;
  margin-top: 2px;
}
.text-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.text-group .title {
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
}
.text-group .desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

/* 选项卡与徽标 */
.audit-tabs {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #eef0f5;
}
.tab-label-item {
  display: flex;
  align-items: center;
  gap: 6px;
}
.badge-dot :deep(.el-badge__content) {
  top: -2px;
  padding: 0 5px;
  height: 16px;
  line-height: 16px;
}

/* 列表内部复合单元格 */
.position-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.position-cell .p-name {
  font-weight: 600;
  color: #1e293b;
}
.position-cell .p-desc {
  font-size: 12px;
  color: #94a3b8;
}
.text-muted {
  color: #94a3b8 !important;
  text-decoration: line-through;
}
.p-tags {
  display: flex;
  gap: 8px;
}

/* 带闪烁点的状态标签样式 */
.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}
.status-tag .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}
.pulse-blue {
  background-color: #2563eb;
  box-shadow: 0 0 0 0 rgba(37, 99, 235, 0.4);
  animation: pulse 1.6s infinite;
}

/* 驳回原因美化描述 */
.reject-reason-box {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #f43f5e;
  background: #fff5f5;
  border: 1px dashed #fecdd3;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  max-width: 320px;
}
.reason-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.req-block {
  background: #f8fafc;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  font-size: 13px;
  color: #475569;
  line-height: 1.6;
}

@keyframes pulse {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(37, 99, 235, 0.7); }
  70% { transform: scale(1); box-shadow: 0 0 0 6px rgba(37, 99, 235, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(37, 99, 235, 0); }
}

/* 微调 Element 表头 */
:deep(.el-table th) {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 600;
}
</style>