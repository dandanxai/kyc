import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getAccessToken, removeToken } from '@/utils/auth' // 🌟 引入对齐的授权工具
import { useUserStore } from '@/store/modules/user'         // 🌟 引入用户状态 Store

const routes: RouteRecordRaw[] = [
// 1. 重定向入口
{ path: '/', redirect: '/dashboard' },

// 2. 认证模块（分离登录与注册）
{
    path: '/auth',
    component: () => import('../layout/blank.vue'), // 空白布局组件
    children: [
    {
        path: 'login',
        name: 'Login',
        component: () => import('../views/auth/login.vue'),
        meta: { title: '登录' }
    },
    {
        path: 'register',
        name: 'Register',
        component: () => import('../views/auth/register.vue'),
        meta: { title: '企业入驻' }
    }
    ]
},

// 3. 核心业务模块
{
    path: '/',
    component: () => import('../layout/index.vue'),
    children: [
    {
        path: 'dashboard',
        name: 'CompanyDashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '招聘工作台', icon: 'Odometer', requiresAuth: true }
    },
    {
        path: 'positions',
        name: 'PositionManagement',
        meta: { title: '职位管理', icon: 'Briefcase', requiresAuth: true },
        redirect: '/positions/list',
        children: [
        { path: 'list', name: 'PositionList', component: () => import('../views/positions/list.vue'), meta: { title: '在招职位列表' } },
        { path: 'audit', name: 'PositionAudit', component: () => import('../views/positions/audit.vue'), meta: { title: '草稿与审核' } }
        ]
    },
    {
        path: 'talents',
        name: 'TalentPool',
        meta: { title: '简历处理夹', icon: 'Files', requiresAuth: true },
        redirect: '/talents/pipeline',
        children: [
        { path: 'pipeline', name: 'TalentPipeline', component: () => import('../views/talents/pipeline.vue'), meta: { title: '招聘漏斗推进' } },
        { path: 'archive', name: 'TalentArchive', component: () => import('../views/talents/archive.vue'), meta: { title: '企业人才库' } }
        ]
    },
    {
        path: 'chat',
        name: 'BndChat',
        component: () => import('../views/chat/index.vue'),
        meta: { title: '即时沟通', icon: 'ChatLineRound', requiresAuth: true }
    },
    {
        path: 'enterprise/profile',
        name: 'EnterpriseProfile',
        component: () => import('../views/enterprise/profile.vue'), // 请确保在此路径创建对应的 vue 文件
        meta: { title: '企业资料', requiresAuth: true } // 🌟 不写 icon，左侧 el-menu 渲染时就会自动忽略它，不会污染左侧菜单！
    },
    
    // 🌟 核心追加点 2：账户安全路由
    {
        path: 'enterprise/security',
        name: 'EnterpriseSecurity',
        component: () => import('../views/enterprise/security.vue'), // 请确保在此路径创建对应的 vue 文件
        meta: { title: '账户安全', requiresAuth: true }
    }
    ]
},
// 404 处理
{ path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

export const router = createRouter({
    history: createWebHistory(),
    routes
})

// 🌟 核心修复点：千万不要在这里写 const userStore = useUserStore() ❌

const whiteList = ['/auth/login', '/auth/register']

/**
 * 路由全局守卫
 */
router.beforeEach(async (to, from, next) => {
    const hasToken = getAccessToken()
    
    // 🌟 正确做法：把 Store 的实例化塞进守卫函数体内！
    // 当守卫真正运行时，app.use(pinia) 早就完事了，绝对不会报错！
    const userStore = useUserStore() 

    if (to.meta?.title) {
    document.title = `${to.meta.title} - 极客互娱`
    }

    if (hasToken) {
    if (whiteList.includes(to.path)) {
        next({ path: '/dashboard' })
    } else {
        if (userStore.isSetUser) {
        next()
        } else {
        try {
            await userStore.setUserInfoAction()
            next({ ...to, replace: true })
        } catch (error) {
            console.error('路由守卫加载用户信息异常:', error)
            userStore.resetToken()
            next(`/auth/login?redirect=${to.path}`)
        }
        }
    }
    } else {
    if (whiteList.includes(to.path)) {
        next()
    } else {
        next(`/auth/login?redirect=${to.path}`)
    }
    }
})

export default router