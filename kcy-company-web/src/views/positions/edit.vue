<template>
    <el-drawer
    v-model="visible"
    :title="isEdit ? '编辑在招职位' : '发布全新职位岗位'"
    size="640px"
    destroy-on-close
    @close="handleClose"
    >
    <div class="drawer-tip">
        <el-icon><InfoFilled /></el-icon>
        <span>当前所发布岗位将归属于当前登录租户。通过平台审核后将实时同步至 C 端小程序大厅。</span>
    </div>

    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="position-drawer-form"
    >
        <el-form-item label="职位名称" prop="title">
        <el-input v-model="form.title" placeholder="如：Vue3前端研发工程师" maxlength="30" show-word-limit />
        </el-form-item>

        <div class="form-row">
        <el-form-item label="发布部门" prop="departmentName" style="flex: 1;">
            <el-select v-model="form.departmentName" placeholder="请选择所属部门" style="width: 100%;">
            <el-option label="技术研发部" value="技术研发部" />
            <el-option label="产品设计部" value="产品设计部" />
            <el-option label="运营市场部" value="运营市场部" />
            </el-select>
        </el-form-item>

        <el-form-item label="是否急聘" prop="isUrgent" style="width: 120px;">
            <el-switch
            v-model="form.isUrgent"
            active-text="是"
            inactive-text="否"
            inline-prompt
            active-color="#f43f5e"
            />
        </el-form-item>
        </div>

        <div class="form-row">
        <el-form-item label="工作城市" prop="city" style="flex: 1;">
            <el-input v-model="form.city" placeholder="如：合肥" />
        </el-form-item>
        <el-form-item label="经验要求" prop="exp" style="flex: 1;">
            <el-select v-model="form.exp" placeholder="请选择经验" style="width: 100%;">
            <el-option label="无要求" value="无要求" />
            <el-option label="应届生/1年以下" value="应届生/1年以下" />
            <el-option label="1-3年" value="1-3年" />
            <el-option label="3-5年" value="3-5年" />
            </el-select>
        </el-form-item>
        <el-form-item label="学历要求" prop="edu" style="flex: 1;">
            <el-select v-model="form.edu" placeholder="请选择学历" style="width: 100%;">
            <el-option label="不限" value="不限" />
            <el-option label="大专" value="大专" />
            <el-option label="大专及以上" value="大专及以上" />
            <el-option label="本科" value="本科" />
            </el-select>
        </el-form-item>
        </div>

        <el-form-item label="薪资待遇（如：12K - 18K）" prop="salaryStr">
        <el-input v-model="form.salaryStr" placeholder="请输入薪资范围，示例：8K - 12K" />
        </el-form-item>

        <el-form-item label="任职要求 & 核心职责" prop="requirement">
        <el-input
            v-model="form.requirement"
            type="textarea"
            :rows="6"
            placeholder="请输入清晰的岗位职责、技术栈要求（如 Spring Boot, MyBatis-Plus 或 Vue3 框架开发经验等...）"
        />
        </el-form-item>
    </el-form>

    <template #footer>
        <div class="drawer-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            提交保存
        </el-button>
        </div>
    </template>
    </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance } from 'element-plus'

// 定义需要向父组件通报的事件（保存成功后通知父列表刷新）
const emit = defineEmits(['success'])

const visible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 表单响应式载荷基底
const defaultForm = {
    id: null,
    title: '',
    isUrgent: false,
    city: '合肥',
    exp: '',
    edu: '',
    departmentName: '',
    salaryStr: '',
    requirement: '',
    applyCount: 0,
    interviewCount: 0,
    createTime: '',
    status: 1
}

const form = reactive({ ...defaultForm })

// 严格的大厂 B 端表单校验规则
const rules = reactive({
    title: [{ required: true, message: '请输入职位岗位名称', trigger: 'blur' }],
    departmentName: [{ required: true, message: '请选择职位所属部门', trigger: 'change' }],
    city: [{ required: true, message: '请输入办公城市', trigger: 'blur' }],
    exp: [{ required: true, message: '请指定经验年限要求', trigger: 'change' }],
    edu: [{ required: true, message: '请指定学历门槛要求', trigger: 'change' }],
    salaryStr: [
    { required: true, message: '请输入对外薪资待遇', trigger: 'blur' },
    { pattern: /^[0-9]+[kK]-[0-9]+[kK]$/, message: '请输入标准的薪资格式，如：8K-12K', trigger: 'blur' }
    ]
})

// 暴露给父组件调用的方法（唤醒抽屉）
const open = (row?: any) => {
    visible.value = true
    if (row) {
    isEdit.value = true
    // 回显数据深度克隆，防止未点击保存就污染外层列表
    Object.assign(form, row)
    } else {
    isEdit.value = false
    Object.assign(form, defaultForm)
    form.createTime = new Date().toISOString().split('T')[0] // 模拟今天日期
    }
}

const handleClose = () => {
    formRef.value?.resetFields()
}

// 模拟提交对接
const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate((valid) => {
    if (valid) {
        submitLoading.value = true
        // 模拟请求延迟响应
        setTimeout(() => {
        submitLoading.value = false
        visible.value = false
        ElMessage.success(isEdit.value ? '职位更改成功，多租户缓存已刷新' : '新职位已提交发布，等待系统审核')
        // 带着组装好的最新数据单传回给列表页更新
        emit('success', { ...form })
        }, 500)
    } else {
        ElMessage.error('表单仍有校验项未通过，请检查红标字段')
    }
    })
}

// 主动向外部暴露出 open 方法
defineExpose({ open })
</script>

<style scoped>
.drawer-tip {
    background-color: #eff6ff;
    border: 1px solid #bfdbfe;
    color: #1e40af;
    padding: 10px 14px;
    border-radius: 8px;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 24px;
    line-height: 1.4;
}
.drawer-tip .el-icon { font-size: 16px; flex-shrink: 0; }
.form-row {
    display: flex;
    gap: 16px;
}
.drawer-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 10px;
}
/* 深度微调表单标签厚度 */
:deep(.el-form-item__label) {
    font-weight: 600;
    color: #334155;
    padding-bottom: 4px !important;
}
</style>