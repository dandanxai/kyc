<template>
  <div class="position-list-container">
    <div class="position-mini-cards">
      <div class="mini-card">
        <div class="card-info">
          <span class="label">在招职位数</span>
          <span class="value">{{ totalCount }} / 10</span>
        </div>
        <el-progress type="circle" :percentage="Math.min(totalCount * 10, 100)" :width="48" stroke-width="5" />
      </div>
      <div class="mini-card">
        <div class="card-info">
          <span class="label">今日收到投递</span>
          <span class="value text-blue">24 份</span>
        </div>
        <div class="icon-box bg-blue"><el-icon><DocumentChecked /></el-icon></div>
      </div>
      <div class="mini-card">
        <div class="card-info">
          <span class="label">本月急聘消耗</span>
          <span class="value text-orange">2 / 5</span>
        </div>
        <div class="icon-box bg-orange"><el-icon><Lightning /></el-icon></div>
      </div>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" inline size="default" class="demo-form-inline">
        <el-form-item label="职位名称">
          <el-input v-model="filterForm.keyword" placeholder="搜索职位名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item class="filter-btns">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <div class="toolbar-right-btns">
          <el-button type="info" plain :icon="Upload" @click="openUploadDialog" style="margin-right: 8px">
            智能文档导入
          </el-button>
          <el-button type="primary" :icon="Plus" @click="handleCreate">发布新岗位</el-button>
        </div>
      </div>

      <el-table :data="positionList" style="width: 100%" v-loading="loading">
        <template #empty><el-empty description="暂无在招岗位数据" /></template>
        
        <el-table-column label="职位招聘信息" min-width="260">
          <template #default="scope">
            <div class="position-info-cell">
              <div class="title-row">
                <span class="p-title">{{ scope.row.title || formatJobTitle(scope.row.fileName) }}</span>
                
                <el-tag v-if="scope.row.parseStatus === 1" size="small" type="warning" class="status-tag">
                  <el-icon class="is-loading"><Loading /></el-icon> AI 深度解构中
                </el-tag>
                <el-tag v-else-if="scope.row.parseStatus === 3" size="small" type="danger" class="status-tag">
                  解析失败
                </el-tag>
                <el-tag v-else-if="scope.row.parseStatus === 0" size="small" type="info" class="status-tag">
                  未解析
                </el-tag>
              </div>
              
              <div class="meta-row">
                <span class="meta-item-city"><el-icon style="vertical-align: middle; margin-right: 2px;"><MapLocation /></el-icon>{{ scope.row.city || '待解构' }}</span>
                <el-divider direction="vertical" />
                <span>{{ formatExperience(scope.row.yearsOfExperienceMin) }}</span>
                <el-divider direction="vertical" />
                <span>{{ scope.row.educationRequired || '学历核对中' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="核心技能聚焦/关键字" min-width="200">
          <template #default="scope">
            <div class="tags-wrapper" v-if="scope.row.keywords">
              <el-tag 
                v-for="tag in parseKeywords(scope.row.keywords)" 
                :key="tag" 
                size="small" 
                class="mini-tech-tag"
                effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>
            <span v-else-if="scope.row.parseStatus === 1" class="loading-placeholder">AI 正在抓取技能树...</span>
            <span v-else class="empty-placeholder-text">暂无提取到关键字</span>
          </template>
        </el-table-column>
        
        <el-table-column label="薪资待遇" width="130">
          <template #default="scope">
            <span class="salary-text">{{ formatSalary(scope.row.salaryMin, scope.row.salaryMax) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="招聘漏斗数据（已投/面试）" width="200">
          <template #default="scope">
            <div class="funnel-data-box">
              <div class="funnel-stat">
                <span class="num blue">{{ scope.row.applyCount || 0 }}</span>
                <span class="lbl">新投递</span>
              </div>
              <div class="funnel-arrow">➔</div>
              <div class="funnel-stat">
                <span class="num orange">{{ scope.row.interviewCount || 0 }}</span>
                <span class="lbl">安排面试</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="发布时间" width="150">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="在招状态" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-text="开" inactive-text="关" inline-prompt />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" link type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" link type="success" :icon="RefreshRight" @click="handleRefresh(scope.row)">刷新曝光</el-button>
            <el-dropdown trigger="click">
              <el-button size="small" link type="info" :icon="More" style="margin-left: 12px">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :icon="Share">分享职位</el-dropdown-item>
                  <el-dropdown-item :icon="Delete" style="color: #f43f5e" @click="handleDelete(scope.row)">删除岗位</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNo"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalCount"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <PositionUploadDialog ref="positionUploadDialogRef" @upload-success="refreshPositionList" />
    <PositionEditForm ref="editDrawerRef" @success="handleFormSuccess" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Search, Refresh, Plus, Edit, RefreshRight, More, Delete, Share, DocumentChecked, Lightning, Upload, Loading, MapLocation } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import dayjs from 'dayjs'

import PositionEditForm from './edit.vue'
import PositionUploadDialog from './PositionUploadDialog.vue'

// 🌟 假设你已经在 @/api/position/index.ts 导出了你真实的 getPositionPage 和 deletePosition 接口
import { PositionApi } from '@/api/position/index'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

const loading = ref(false)
const totalCount = ref(0)
const positionList = ref<any[]>([])
const editDrawerRef = ref() 
const positionUploadDialogRef = ref<InstanceType<typeof PositionUploadDialog> | null>(null)

const filterForm = reactive({ keyword: '' })

// 统一分页查询模型参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10
})

// 🌟 B 端专属大一统通道雷达：捕获合并后通用 SSE 发射的专属通知
let sseSource: EventSource | null = null

const initPositionSse = () => {
  const loginUserId = userStore.userInfo?.id || 1
  console.log('📡 [SSE 准备发射] 此时计算出来的最终用户 ID 是:', loginUserId)
  
  if (sseSource) sseSource.close()

  // 🚀 直连 Java 升级合并后的大一统全局通道
  sseSource = new EventSource(`http://127.0.0.1:48080/app-api/member/sse/connect?userId=${loginUserId}`)

  // 🎯 精准捕捉大一统通道分发出来的 POSITION_PARSE_SUCCESS 枪声，彻底绝杀主键重叠隐患
  sseSource.addEventListener('POSITION_PARSE_SUCCESS', (event) => {
    const parsedPositionId = Number(event.data)
    
    ElNotification({
      title: '岗位需求全息智能解构成功',
      message: '大模型已完成文本全脱水，硬性过滤指标及技能树已成功同步至达梦数据库！',
      type: 'success',
      duration: 4000
    })

    // 局部极速秒爆刷新卡片转圈状态
    const targetJob = positionList.value.find(p => p.id === parsedPositionId)
    if (targetJob) {
      targetJob.parseStatus = 2
    }
    
    // 静默刷新当前页最新数据
    silentRefreshCurrentPage()
  })
}

// 核心加载数据层（对接真实后端分页接口）
const loadPositionData = async () => {
  loading.value = true
  try {
    const params = {
      pageNo: queryParams.pageNo,
      pageSize: queryParams.pageSize,
      keyword: filterForm.keyword || undefined
    }
    const res = await PositionApi.getPositionPage(params)
    const resData = (res && res.list !== undefined) ? res : res?.data
    
    if (resData && Array.isArray(resData.list)) {
      positionList.value = resData.list
      totalCount.value = resData.total
    }
  } catch (err) {
    console.error('拉取岗位分页数据溃败:', err)
    ElMessage.error('获取岗位列表失败')
  } finally {
    loading.value = false
  }
}

// 无感知静默刷新（大模型解析报喜专用，防止 Loading 闪烁破坏滚动感）
const silentRefreshCurrentPage = async () => {
  try {
    const params = {
      pageNo: queryParams.pageNo,
      pageSize: queryParams.pageSize,
      keyword: filterForm.keyword || undefined
    }
    const res = await PositionApi.getPositionPage(params)
    const resData = (res && res.list !== undefined) ? res : res?.data
    if (resData && Array.isArray(resData.list)) {
      positionList.value = resData.list
      totalCount.value = resData.total
    }
  } catch (e) {
    console.error('静默刷新异常:', e)
  }
}

// 刷新并重置页码回到第一页
const refreshPositionList = () => {
  queryParams.pageNo = 1
  loadPositionData()
}

const handleQuery = () => {
  refreshPositionList()
}

const resetQuery = () => {
  filterForm.keyword = ''
  refreshPositionList()
}

// 分页条动作拦截
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  loadPositionData()
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNo = val
  loadPositionData()
}

const openUploadDialog = () => {
  positionUploadDialogRef.value?.open()
}

const handleCreate = () => {
  editDrawerRef.value.open()
}

const handleEdit = (row: any) => {
  editDrawerRef.value.open(row)
}

const handleFormSuccess = () => {
  loadPositionData()
}

const handleRefresh = (row: any) => { 
  ElMessage.success(`已刷新职位【${row.title || formatJobTitle(row.fileName)}】的线上曝光度！`) 
}

const handleDelete = (row: any) => {
  const titleName = row.title || formatJobTitle(row.fileName)
  ElMessageBox.confirm(`确定要彻底删除【${titleName}】这个在招职位吗？`, '警告', { 
    confirmButtonText: '确定', 
    cancelButtonText: '取消', 
    type: 'error' 
  }).then(async () => {
    try {
      await PositionApi.deletePosition(row.id)
      ElMessage.success('职位已成功从达梦数据库移除')
      loadPositionData()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// ==================== 🛠️ 高能辅助转换清洗函数 ====================

// 1. 去掉后缀提取纯文件名称当主职位
const formatJobTitle = (fileName: string) => {
  if (!fileName) return '未命名岗位需求'
  return fileName.replace(/\.(pdf|docx|doc|xlsx|xls)$/i, '')
}

// 2. 将后端的纯元/月转换成标准 K 为单位的区间字符串
const formatSalary = (min: number, max: number) => {
  if (!min && !max) return '薪资面议'
  const minK = min ? `${Math.round(min / 1000)}K` : '?'
  const maxK = max ? `${Math.round(max / 1000)}K` : '?'
  return `${minK}-${maxK}`
}

// 3. 将后端的最低年限经验纯数字美化输出
const formatExperience = (exp: any) => {
  if (exp === undefined || exp === null || Number(exp) === 0) return '经验不限'
  return `满 ${exp} 年工作经验`
}

// 4. 将后端的 keywords 字符串（如 "["Java","MySQL"]"）解析为真正的数组
const parseKeywords = (keywordsStr: string) => {
  if (!keywordsStr) return []
  try {
    // 如果后端传过来本身就是 JSON 串格式
    if (keywordsStr.startsWith('[')) {
      return JSON.parse(keywordsStr)
    }
    // 如果是以逗号分隔的普通字符串，进行切割兼容
    return keywordsStr.split(',').filter(v => v.trim())
  } catch (e) {
    return keywordsStr.split(',').filter(v => v.trim())
  }
}

// 5. 格式化日期时间
const formatDateTime = (dateStr: any) => {
  return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm') : '未知时间'
}

// ==================== 🌟 生命周期闭环保障 ====================
onMounted(() => {
  loadPositionData()
  initPositionSse() // 启动专属防撞车雷达
})

onBeforeUnmount(() => {
  if (sseSource) sseSource.close() // 安全卸载连接
})
</script>

<style scoped>
.position-list-container { padding: 24px; background-color: #f8fafc; min-height: calc(100vh - 64px); }
.position-mini-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 20px; }
.mini-card { background: #fff; border: 1px solid #eef0f5; border-radius: 12px; padding: 16px 20px; display: flex; justify-content: space-between; align-items: center; }
.card-info { display: flex; flex-direction: column; gap: 6px; }
.card-info .label { font-size: 13px; color: #64748b; font-weight: 500; }
.card-info .value { font-size: 22px; font-weight: 700; color: #1e293b; }
.text-blue { color: #2563eb !important; }
.text-orange { color: #ea580c !important; }
.icon-box { width: 42px; height: 42px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 20px; }
.bg-blue { background: #eff6ff; color: #2563eb; }
.bg-orange { background: #fff7ed; color: #ea580c; }
.filter-card { border-radius: 12px; border: 1px solid #eef0f5; margin-bottom: 20px; }
.demo-form-inline { display: flex; flex-wrap: wrap; align-items: center; }
:deep(.el-form-item) { margin-bottom: 0 !important; margin-right: 24px; }
.filter-btns { margin-left: auto; margin-right: 0; }
.table-card { border-radius: 12px; border: 1px solid #eef0f5; }
.table-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.toolbar-right-btns { display: flex; align-items: center; }
.table-tip { font-size: 13px; color: #64748b; font-weight: 500; }

.position-info-cell { display: flex; flex-direction: column; gap: 6px; }
.title-row { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.p-title { font-weight: 600; color: #1e293b; font-size: 14px; }
.status-tag { border-radius: 4px; padding: 0 6px; display: inline-flex; align-items: center; gap: 4px; }
.meta-row { font-size: 12px; color: #94a3b8; display: flex; align-items: center; }
.meta-item-city { color: #475569; font-weight: 500; }

.tags-wrapper { display: flex; flex-wrap: wrap; gap: 4px; max-width: 280px; }
.mini-tech-tag { font-size: 11px; font-weight: 500; border-radius: 4px; }
.loading-placeholder { font-size: 12px; color: #e6a23c; font-style: italic; }
.empty-placeholder-text { font-size: 12px; color: #94a3b8; font-style: italic; }

.salary-text { color: #ef4444; font-weight: 600; font-size: 14px; }
.funnel-data-box { display: flex; align-items: center; gap: 12px; background: #f8fafc; padding: 6px 12px; border-radius: 8px; border: 1px dashed #e2e8f0; width: fit-content; }
.funnel-stat { display: flex; flex-direction: column; align-items: center; }
.funnel-stat .num { font-weight: 700; font-size: 14px; }
.funnel-stat .num.blue { color: #2563eb; }
.funnel-stat .num.orange { color: #ea580c; }
.funnel-stat .lbl { font-size: 11px; color: #94a3b8; margin-top: 2px; }
.funnel-arrow { color: #cbd5e1; font-size: 12px; }

.pagination-container { display: flex; justify-content: flex-end; margin-top: 20px; }
:deep(.el-table th) { background-color: #f8fafc !important; color: #475569; font-weight: 600; }
</style>