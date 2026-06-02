<template>
  <div class="position-list-container">
    <div class="position-mini-cards">
      <div class="mini-card">
        <div class="card-info">
          <span class="label">在招职位数</span>
          <span class="value">{{ positionList.length }} / 10</span>
        </div>
        <el-progress
          type="circle"
          :percentage="positionList.length * 10"
          :width="48"
          stroke-width="5"
        />
      </div>
      <div class="mini-card">
        <div class="card-info">
          <span class="label">今日收到投递</span>
          <span class="value text-blue">24 份</span>
        </div>
        <div class="icon-box bg-blue"
          ><el-icon><DocumentChecked /></el-icon
        ></div>
      </div>
      <div class="mini-card">
        <div class="card-info">
          <span class="label">本月急聘消耗</span>
          <span class="value text-orange">2 / 5</span>
        </div>
        <div class="icon-box bg-orange"
          ><el-icon><Lightning /></el-icon
        ></div>
      </div>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" inline size="default" class="demo-form-inline">
        <el-form-item label="职位名称">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索职位名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="发布部门">
          <el-select
            v-model="filterForm.department"
            placeholder="全部部门"
            clearable
            style="width: 160px"
          >
            <el-option label="技术研发部" value="技术研发部" />
            <el-option label="产品设计部" value="产品设计部" />
            <el-option label="运营市场部" value="运营市场部" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-btns">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <span class="table-tip">已选租户：极客互娱 ｜ 数据已实现多租户底层完全隔离</span>
        </div>
        <el-button type="primary" :icon="Plus" @click="handleCreate">发布新岗位</el-button>
      </div>

      <el-table :data="positionList" style="width: 100%" v-loading="loading">
        <template #empty><el-empty description="暂无在招岗位数据" /></template>

        <el-table-column label="职位信息" min-width="240">
          <template #default="scope">
            <div class="position-info-cell">
              <div class="title-row">
                <span class="p-title">{{ scope.row.title }}</span>
                <el-tag
                  v-if="scope.row.isUrgent"
                  size="small"
                  type="danger"
                  effect="plain"
                  class="urgent-tag"
                  >急聘</el-tag
                >
              </div>
              <div class="meta-row">
                <span>{{ scope.row.city }}</span>
                <el-divider direction="vertical" />
                <span>{{ scope.row.exp }}</span>
                <el-divider direction="vertical" />
                <span>{{ scope.row.edu }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="departmentName" label="所属部门" width="130" />
        <el-table-column prop="salaryStr" label="薪资待遇" width="130">
          <template #default="scope">
            <span class="salary-text">{{ scope.row.salaryStr }}</span>
          </template>
        </el-table-column>

        <el-table-column label="招聘漏斗数据（已投/面试）" width="220">
          <template #default="scope">
            <div class="funnel-data-box">
              <div class="funnel-stat"
                ><span class="num blue">{{ scope.row.applyCount }}</span
                ><span class="lbl">新投递</span></div
              >
              <div class="funnel-arrow">➔</div>
              <div class="funnel-stat"
                ><span class="num orange">{{ scope.row.interviewCount }}</span
                ><span class="lbl">安排面试</span></div
              >
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="发布时间" width="130" />

        <el-table-column label="在招状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="开"
              inactive-text="关"
              inline-prompt
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" link type="primary" :icon="Edit" @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-button
              size="small"
              link
              type="success"
              :icon="RefreshRight"
              @click="handleRefresh(scope.row)"
              >刷新曝光</el-button
            >
            <el-dropdown trigger="click">
              <el-button size="small" link type="info" :icon="More" style="margin-left: 12px"
                >更多</el-button
              >
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :icon="Share">分享职位</el-dropdown-item>
                  <el-dropdown-item
                    :icon="Delete"
                    style="color: #f43f5e"
                    @click="handleDelete(scope.row)"
                    >删除岗位</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <PositionEditForm ref="editDrawerRef" @success="handleFormSuccess" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  Search,
  Refresh,
  Plus,
  Edit,
  RefreshRight,
  More,
  Delete,
  Share,
  DocumentChecked,
  Lightning
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 引入刚刚建立的独立 Edit 抽屉组件
import PositionEditForm from './edit.vue'

const loading = ref(false)
const editDrawerRef = ref() // 挂载抽屉组件的 ref 实例

const filterForm = reactive({ keyword: '', department: '' })

const positionList = ref([
  {
    id: 1001,
    title: 'Vue3 前端研发工程师（跨端）',
    isUrgent: true,
    city: '合肥',
    exp: '应届生/1年以下',
    edu: '大专及以上',
    departmentName: '技术研发部',
    salaryStr: '7K-11K',
    applyCount: 18,
    interviewCount: 4,
    createTime: '2026-05-28',
    status: 1,
    requirement: '精通 Vue3 核心底层概念，会使用 Pinia, Axios。'
  },
  {
    id: 1002,
    title: 'Java 全栈开发工程师（Spring Boot）',
    isUrgent: false,
    city: '合肥',
    exp: '1-3年',
    edu: '本科',
    departmentName: '技术研发部',
    salaryStr: '12K-18K',
    applyCount: 35,
    interviewCount: 6,
    createTime: '2026-05-25',
    status: 1,
    requirement: '掌握 Spring Boot, MyBatis-Plus 开发闭环。'
  }
])

// 唤醒新增抽屉
const handleCreate = () => {
  editDrawerRef.value.open()
}

// 唤醒编辑抽屉并传入当前行数据
const handleEdit = (row: any) => {
  editDrawerRef.value.open(row)
}

// 接收来自抽屉组件提交成功后的回调数据
const handleFormSuccess = (formData: any) => {
  if (formData.id) {
    // 1. 编辑状态：同步定位本地列表中对应的行进行前端数据合并
    const index = positionList.value.findIndex((item) => item.id === formData.id)
    if (index !== -1) positionList.value[index] = formData
  } else {
    // 2. 新增状态：造一个临时 ID 压入列表头部
    formData.id = Math.floor(Math.random() * 1000) + 2000
    positionList.value.unshift(formData)
  }
}

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 300)
}

const resetQuery = () => {
  filterForm.keyword = ''
  filterForm.department = ''
  handleQuery()
}

const handleRefresh = (row: any) => {
  ElMessage.success(`已刷新职位【${row.title}】的线上曝光度！`)
}
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要彻底删除【${row.title}】这个在招职位吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  })
    .then(() => {
      positionList.value = positionList.value.filter((item) => item.id !== row.id)
      ElMessage.success('职位已成功移除')
    })
    .catch(() => {})
}
</script>

<style scoped>
/* 此处包含之前的表格美化样式，保持不变 */
.position-list-container {
  padding: 24px;
  background-color: #f8fafc;
  min-height: calc(100vh - 64px);
}
.position-mini-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.mini-card {
  background: #fff;
  border: 1px solid #eef0f5;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.card-info .label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}
.card-info .value {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}
.text-blue {
  color: #2563eb !important;
}
.text-orange {
  color: #ea580c !important;
}
.icon-box {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
.bg-blue {
  background: #eff6ff;
  color: #2563eb;
}
.bg-orange {
  background: #fff7ed;
  color: #ea580c;
}
.filter-card {
  border-radius: 12px;
  border: 1px solid #eef0f5;
  margin-bottom: 20px;
}
.demo-form-inline {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
:deep(.el-form-item) {
  margin-bottom: 0 !important;
  margin-right: 24px;
}
.filter-btns {
  margin-left: auto;
  margin-right: 0;
}
.table-card {
  border-radius: 12px;
  border: 1px solid #eef0f5;
}
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.table-tip {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}
.position-info-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.p-title {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}
.urgent-tag {
  border-radius: 4px;
  padding: 0 4px;
}
.meta-row {
  font-size: 12px;
  color: #94a3b8;
}
.salary-text {
  color: #ef4444;
  font-weight: 600;
  font-size: 14px;
}
.funnel-data-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8fafc;
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px dashed #e2e8f0;
  width: fit-content;
}
.funnel-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.funnel-stat .num {
  font-weight: 700;
  font-size: 14px;
}
.funnel-stat .num.blue {
  color: #2563eb;
}
.funnel-stat .num.orange {
  color: #ea580c;
}
.funnel-stat .lbl {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 2px;
}
.funnel-arrow {
  color: #cbd5e1;
  font-size: 12px;
}
:deep(.el-table th) {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 600;
}
</style>
