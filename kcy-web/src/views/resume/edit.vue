<template>
    <div class="resume-edit-page">
    <!-- 顶部动作栏 -->
    <div class="edit-action-bar">
        <div class="left-info">
        <el-button :icon="Back" circle @click="goBack"></el-button>
        <span class="page-title">{{ isEditMode ? '编辑在线简历' : '创建新在线简历' }}</span>
        </div>
        <div class="right-ops">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" :icon="Check" @click="saveResume">保存简历</el-button>
        </div>
    </div>

    <!-- 主体两栏布局 -->
    <div class="edit-layout">
        <!-- 左侧：表单主体 -->
        <div class="layout-left">
        <el-form :model="form" label-position="top" ref="formRef">
            
            <!-- 1. 简历管理名称 -->
            <div class="form-card" id="module-title">
            <h3 class="module-title">简历管理设置</h3>
            <el-form-item label="简历名称（仅自己可见，用于区分投递方向）" required>
                <el-input v-model="form.title" placeholder="例如：黄胜-Java全栈开发工程师简历" />
            </el-form-item>
            </div>

            <!-- 2. 基本信息 -->
            <div class="form-card" id="module-base">
            <h3 class="module-title">基本信息</h3>
            <div class="form-grid-2">
                <el-form-item label="姓名" required>
                <el-input v-model="form.name" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="年龄" required>
                <el-input-number v-model="form.age" :min="16" :max="60" style="width: 100%;" />
                </el-form-item>
            </div>
            <div class="form-grid-2">
                <el-form-item label="现居城市" required>
                <el-input v-model="form.location" placeholder="例如：安徽合肥" />
                </el-form-item>
                <el-form-item label="最高学历" required>
                <el-select v-model="form.education" placeholder="请选择学历">
                    <el-option label="大专" value="大专" />
                    <el-option label="本科" value="本科" />
                    <el-option label="硕士" value="硕士" />
                    <el-option label="其他" value="其他" />
                </el-select>
                </el-form-item>
            </div>
            <div class="form-grid-2">
                <el-form-item label="手机号码">
                <el-input v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="电子邮箱">
                <el-input v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
            </div>
            </div>

            <!-- 3. 求职意向 -->
            <div class="form-card" id="module-intent">
            <h3 class="module-title">求职意向</h3>
            <div class="form-grid-2">
                <el-form-item label="期望职位" required>
                <el-input v-model="form.expectedJob" placeholder="例如：Java全栈开发工程师" />
                </el-form-item>
                <el-form-item label="核心技能标签（回车添加）">
                <el-select
                    v-model="form.targetTags"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    placeholder="请输入并按回车创建标签"
                >
                    <el-option label="Spring Boot" value="Spring Boot" />
                    <el-option label="Vue3" value="Vue3" />
                    <el-option label="TypeScript" value="TypeScript" />
                    <el-option label="UniApp" value="UniApp" />
                    <el-option label="MySQL" value="MySQL" />
                </el-select>
                </el-form-item>
            </div>
            </div>

            <!-- 4. 核心项目经历（动态增删面板） -->
            <div class="form-card" id="module-project">
            <div class="module-header-row">
                <h3 class="module-title">核心项目经历</h3>
                <el-button type="primary" link :icon="Plus" @click="addProject">添加项目经历</el-button>
            </div>

            <div v-for="(proj, index) in form.projects" :key="index" class="project-dynamic-item">
                <div class="item-header">
                <span>项目 #{{ index + 1 }}</span>
                <el-button type="danger" link :icon="Delete" @click="removeProject(index)" v-if="form.projects.length > 1">删除此项</el-button>
                </div>
                
                <div class="form-grid-2">
                <el-form-item label="项目名称" required>
                    <el-input v-model="proj.name" placeholder="请输入项目名称" />
                </el-form-item>
                <el-form-item label="担任角色" required>
                    <el-input v-model="proj.role" placeholder="例如：全栈独立开发 / 前端负责人" />
                </el-form-item>
                </div>
                <div class="form-grid-2">
                <el-form-item label="起止时间" required>
                    <el-input v-model="proj.period" placeholder="例如：2025.10 - 2026.02" />
                </el-form-item>
                <el-form-item label="涉及技术 (逗号隔开)">
                    <el-input v-model="proj.techsString" placeholder="例如：Vue3, Spring Boot, MySQL" @blur="syncTechs(proj)" />
                </el-form-item>
                </div>
                <el-form-item label="项目描述" required>
                <el-input v-model="proj.description" type="textarea" :rows="2" placeholder="简述这是一个什么样的系统..." />
                </el-form-item>
                <el-form-item label="核心职责与技术攻关" required>
                <el-input v-model="proj.duty" type="textarea" :rows="3" placeholder="你在项目中做了什么？解决了什么痛点？（如：攻克高并发列表秒级渲染痛点）" />
                </el-form-item>
            </div>
            </div>

        </el-form>
        </div>

        <!-- 右侧：快捷锚点导航 -->
        <div class="layout-right">
        <div class="anchor-card">
            <h4 class="anchor-title">简历模块导航</h4>
            <div class="anchor-list">
            <a href="#module-title" class="anchor-item">📌 简历设置</a>
            <a href="#module-base" class="anchor-item">👤 基本信息</a>
            <a href="#module-intent" class="anchor-item">🎯 求职意向</a>
            <a href="#module-project" class="anchor-item">💻 项目经历</a>
            </div>
        </div>
        </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Back, Check, Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 检查是编辑已有简历，还是新建简历
const isEditMode = computed(() => !!route.query.id)

// 定义统一的数据模型
const form = ref({
    title: '',
    name: '',
    avatar: 'https://picsum.photos/150/150?random=10',
    age: 21,
    location: '',
    education: '大专',
    phone: '138 **** 8888',
    email: 'huangsheng@example.com',
    expectedJob: '',
    targetTags: [] as string[],
    projects: [
    {
        name: '',
        period: '',
        role: '',
        techsString: '',
        techs: [] as string[],
        description: '',
        duty: ''
    }
    ]
})

// 挂载时如果带有 ID，则执行数据回显（模拟 API 请求）
onMounted(() => {
    if (isEditMode.value) {
    // 模拟从后端拉取到了之前保存的数据进行赋值
    form.value = {
        title: '黄胜-Java全栈开发工程师简历',
        name: '黄胜',
        avatar: 'https://picsum.photos/150/150?random=10',
        age: 21,
        location: '安徽',
        education: '大专',
        phone: '138 **** 8888',
        email: 'huangsheng@example.com',
        expectedJob: 'Java全栈开发工程师',
        targetTags: ['Spring Boot', 'Vue3', 'UniApp', 'MySQL'],
        projects: [
        {
            name: '乐行旅途（一站式跨端直达旅游平台）',
            period: '2025.10 - 2026.02',
            role: '全栈独立开发 / 负责人',
            techsString: 'UniApp, Vue3, Spring Boot, MySQL',
            techs: ['UniApp', 'Vue3', 'Spring Boot', 'MySQL'],
            description: '面向旅游出行生态的一站式轻量级跨端直达旅游服务平台。',
            duty: '全权负责客户端UniApp UI重构设计，基于Spring Boot完成高性能单体架构开发，封装MyBatis-Plus通用持久层，实现核心列表秒级渲染与数据穿透。'
        }
        ]
    }
    }
})

// 增删项目经历的逻辑
const addProject = () => {
    form.value.projects.push({
    name: '',
    period: '',
    role: '',
    techsString: '',
    techs: [],
    description: '',
    duty: ''
    })
}

const removeProject = (index: number) => {
    form.value.projects.splice(index, 1)
}

// 辅助函数：将输入的逗号隔开的字符串同步转换为数组
const syncTechs = (proj: any) => {
    if (proj.techsString) {
    proj.techs = proj.techsString.split(',').map((t: string) => t.trim()).filter(Boolean)
    }
}

// 保存逻辑
const saveResume = () => {
    // 确保所有项目的技术栈都同步好了
    form.value.projects.forEach(syncTechs)

    console.log('提交给后端的数据模型：', form.value)
    ElMessage.success('简历内容已成功同步至云端数据库！')
    
    // 保存成功后跳回列表页
    router.push('/resume')
}

const goBack = () => {
    router.push('/resume')
}
</script>

<style scoped>
.resume-edit-page { max-width: 1100px; margin: 88px auto 40px; padding: 0 20px; }

/* 吸顶动作栏 */
.edit-action-bar { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 14px 24px; border-radius: 12px; border: 1px solid #eef0f5; margin-bottom: 24px; position: sticky; top: 20px; z-index: 10; box-shadow: 0 4px 12px rgba(0,0,0,0.02); }
.left-info { display: flex; align-items: center; gap: 14px; }
.page-title { font-weight: 600; color: #1e293b; font-size: 16px; }

/* 页面主布局 */
.edit-layout { display: grid; grid-template-columns: 1fr 260px; gap: 24px; align-items: start; }
.layout-left { display: flex; flex-direction: column; gap: 24px; }
.form-card { background: #fff; border-radius: 12px; border: 1px solid #eef0f5; padding: 24px; margin-bottom: 24px; scroll-margin-top: 100px; }

/* 模块标题 */
.module-title { margin: 0 0 20px; font-size: 16px; color: #1e293b; border-left: 4px solid #2563eb; padding-left: 10px; }
.module-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.module-header-row .module-title { margin-bottom: 0; }

/* 表单栅格结构 */
.form-grid-2 { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }

/* 动态项目模块样式 */
.project-dynamic-item { border: 1px solid #f1f5f9; background: #fafbfc; padding: 16px; border-radius: 8px; margin-bottom: 20px; }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; border-bottom: 1px solid #f1f5f9; padding-bottom: 8px; font-size: 13px; font-weight: 600; color: #64748b; }

/* 右侧导航锚点卡片 */
.anchor-card { background: #fff; border-radius: 12px; border: 1px solid #eef0f5; padding: 20px; position: sticky; top: 100px; }
.anchor-title { margin: 0 0 14px; font-size: 14px; color: #1e293b; }
.anchor-list { display: flex; flex-direction: column; gap: 12px; }
.anchor-item { font-size: 13px; color: #475569; text-decoration: none; padding: 6px 10px; border-radius: 6px; transition: 0.2s; }
.anchor-item:hover { background: #f0f7ff; color: #2563eb; font-weight: 600; }

/* 深度重写Element内部间距，让表单排版更精致 */
:deep(.el-form-item__label) { font-weight: 600; color: #475569; font-size: 13px; margin-bottom: 6px !important; }
</style>