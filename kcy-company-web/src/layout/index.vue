<template>
    <div class="app-layout">
    <!-- 左侧固定侧边栏 -->
    <div class="layout-sidebar">
        <div class="sidebar-logo-box">
        <div class="logo-circle">B</div>
        <span class="logo-title">企业招聘中心</span>
        </div>

        <!-- 动态路由导航菜单（支持多级折叠） -->
        <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#1e293b"
        text-color="#94a3b8"
        active-text-color="#ffffff"
        unique-opened
        router
        >
        <!-- 遍历主路由的子路由 -->
        <template v-for="menu in menuRoutes" :key="menu.path">
            
            <!-- 情况A：没有子菜单的单级菜单 -->
            <el-menu-item 
            v-if="!menu.children || menu.children.length === 0" 
            :index="resolvePath(menu.path)"
            >
            <el-icon v-if="menu.meta?.icon">
                <component :is="iconMap[menu.meta.icon as string]" />
            </el-icon>
            <span>{{ menu.meta?.title }}</span>
            </el-menu-item>

            <!-- 情况B：拥有二级菜单的折叠菜单 -->
            <el-sub-menu v-else :index="resolvePath(menu.path)">
            <template #title>
                <el-icon v-if="menu.meta?.icon">
                <component :is="iconMap[menu.meta.icon as string]" />
                </el-icon>
                <span>{{ menu.meta?.title }}</span>
            </template>
            
            <!-- 渲染二级子项 -->
            <el-menu-item 
                v-for="subMenu in menu.children" 
                :key="subMenu.path"
                :index="resolvePath(menu.path, subMenu.path)"
            >
                <span class="sub-menu-dot"></span>
                <span>{{ subMenu.meta?.title }}</span>
            </el-menu-item>
            </el-sub-menu>

        </template>
        </el-menu>
    </div>

    <!-- 右侧主体区域 -->
    <div class="layout-main-container">
        <!-- 顶部工作流导航条 -->
        <div class="layout-header">
        <div class="header-left-breadcrumb">
            <span>{{ currentTitle }}</span>
        </div>

        <div class="header-right-user">
            <div class="tenant-badge">
            <span class="dot"></span>
            <span>租户模式：极客互娱</span>
            </div>
            <el-dropdown trigger="click">
            <div class="user-dropdown-trigger">
                <el-avatar :size="32" src="https://picsum.photos/100/100?random=50" />
                <span class="hr-name">王经理</span>
                <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
                <el-dropdown-menu>
                <el-dropdown-item>企业资料</el-dropdown-item>
                <el-dropdown-item>账户安全</el-dropdown-item>
                <el-dropdown-item divided style="color: #f43f5e;" @click="handleLogout">
                    退出企业端
                </el-dropdown-item>
                </el-dropdown-menu>
            </template>
            </el-dropdown>
        </div>
        </div>

        <!-- 核心页面视图渲染视口 -->
        <div class="layout-page-content">
        <router-view />
        </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Odometer, Briefcase, Files, ChatLineRound, ArrowDown } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 图标映射哈希表，用于动态加载菜单图标
const iconMap: Record<string, any> = {
    Odometer,
    Briefcase,
    Files,
    ChatLineRound
}

// 提取当前需要渲染进左侧菜单的路由规则
const menuRoutes = computed(() => {
    const rootRoute = router.options.routes.find(r => r.path === '/')
    return rootRoute?.children || []
})

// 计算当前激活的高亮菜单项
const activeMenu = computed(() => route.path)

// 顶部面包屑文本（若是二级菜单，则显示父级 - 子级标题）
const currentTitle = computed(() => {
    return route.meta.title ? `${route.meta.title}` : '系统管理'
})

// 拼接标准的一级与二级路由路径
const resolvePath = (parentPath: string, childPath?: string) => {
    if (!childPath) return `/${parentPath}`
    return `/${parentPath}/${childPath}`
}

// 退出登录
const handleLogout = () => {
    ElMessageBox.confirm('确定要退出当前企业端招聘账号吗？', '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
    }).then(() => {
    ElMessage.success('已安全退出...')
    router.push('/login')
    }).catch(() => {})
}
</script>

<style scoped>
.app-layout {
    display: flex;
    width: 100vw;
    height: 100vh;
    overflow: hidden;
}

/* ================= 左侧侧边栏专属样式 ================= */
.layout-sidebar {
    width: 240px;
    height: 100%;
    background-color: #1e293b;
    display: flex;
    flex-direction: column;
    flex-shrink: 0;
}
.sidebar-logo-box {
    height: 64px;
    padding: 0 20px;
    display: flex;
    align-items: center;
    gap: 12px;
    border-bottom: 1px solid #334155;
}
.logo-circle {
    width: 32px;
    height: 32px;
    background: #2563eb;
    border-radius: 8px;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 16px;
}
.logo-title {
    color: #f8fafc;
    font-weight: 600;
    font-size: 16px;
    letter-spacing: 0.5px;
}
.sidebar-menu {
    border-right: none !important;
    flex: 1;
    padding-top: 16px;
}

/* 胶囊形选中特效 */
:deep(.el-menu-item), :deep(.el-sub-menu__title) {
    height: 50px;
    line-height: 50px;
    margin: 4px 12px;
    border-radius: 8px;
    color: #94a3b8 !important;
}
:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) {
    background-color: #334155 !important;
    color: #fff !important;
}
:deep(.el-menu-item.is-active) {
    background-color: #2563eb !important;
    color: #ffffff !important;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

/* 二级菜单专属缩进与点装饰 */
.sidebar-menu :deep(.el-menu) {
    background-color: #0f172a !important; /* 二级菜单背景色略微加深，体现层级 */
    padding: 4px 0;
}
.sub-menu-dot {
    width: 4px;
    height: 4px;
    background-color: currentColor;
    border-radius: 50%;
    margin-right: 12px;
    display: inline-block;
}

/* ================= 右侧容器部分 ================= */
.layout-main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}
.layout-header {
    height: 64px;
    background-color: #fff;
    border-bottom: 1px solid #eef0f5;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    flex-shrink: 0;
}
.header-left-breadcrumb {
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
}
.header-right-user {
    display: flex;
    align-items: center;
    gap: 20px;
}
.tenant-badge {
    display: flex;
    align-items: center;
    gap: 6px;
    background: #f0fdf4;
    border: 1px solid #bbf7d0;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    color: #16a34a;
    font-weight: 500;
}
.tenant-badge .dot {
    width: 6px;
    height: 6px;
    background: #16a34a;
    border-radius: 50%;
}
.user-dropdown-trigger {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 6px;
    transition: 0.2s;
}
.user-dropdown-trigger:hover {
    background-color: #f1f5f9;
}
.hr-name {
    font-size: 14px;
    color: #334155;
    font-weight: 500;
}
.layout-page-content {
    flex: 1;
    overflow-y: auto;
    background-color: #f8fafc;
}
</style>