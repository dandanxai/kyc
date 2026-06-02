import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
// 1. 重定向入口
{ path: '/', redirect: '/dashboard' },

// 2. 认证模块（分离登录与注册）
{
    path: '/auth',
    component: () => import('../layout/blank.vue'), // 需要一个空白布局组件
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
    // ... 保持你原有的 positions, talents, chat 路由不变 ...
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

// 路由守卫：这是企业级系统的防线
router.beforeEach((to, from, next) => {
const isAuth = !!localStorage.getItem('token') // 假设使用 token 验证

if (to.meta.requiresAuth && !isAuth) {
    next('/auth/login')
} else if (to.path === '/auth/login' && isAuth) {
    next('/dashboard')
} else {
    next()
}
})