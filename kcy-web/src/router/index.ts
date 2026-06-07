// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import BaseLayout from '@/layout/BaseLayout.vue'
import { getAccessToken } from '@/utils/auth'

const routes: Array<RouteRecordRaw> = [
{
    path: '/',
    component: BaseLayout,
    children: [
    {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页 - 招聘网' }
    },
    {
        path: 'jobs',
        name: 'JobList',
        component: () => import('@/views/jobs/index.vue'),
        meta: { title: '职位搜索' }
    },
    {
        path: 'jobs/:id',
        name: 'JobDetail',
        component: () => import('@/views/jobs/detail.vue'),
        meta: { title: '职位详情' }
    },
    {
        path: 'companies',
        name: 'CompanyList',
        component: () => import('@/views/companies/index.vue'),
        meta: { title: '企业列表' }
    },
    {
        path: 'companies/:id',
        name: 'CompanyDetail',
        component: () => import('@/views/companies/detail.vue'),
        meta: { title: '企业详情' }
    },
    {
        path: 'resume',
        name: 'MyResume',
        component: () => import('@/views/resume/index.vue'),
        meta: { 
        title: '我的简历', 
        requiresAuth: true 
        }
    },
    {
        path: 'resume/:id',
        name: 'ResumeDetail',
        component: () => import('@/views/resume/detail.vue'),
        meta: { title: '企业详情' }
    },
    {
        path: '/resume/graph/:id',
        name: 'ResumeGraphPage',
        component: () => import('@/views/resume/graph.vue'), // 🌟 指向我们新建的独立图谱页面
        meta: { title: '全息能力图谱', hidden: true }
    },
    {
        path: 'resume/edit',
        name: 'ResumeEdit',
        component: () => import('@/views/resume/edit.vue'),
        meta: { 
        title: '编辑在线简历', 
        requiresAuth: true 
        }
    },
    {
        path: 'favorites',
        name: 'MyFavorites',
        component: () => import('@/views/favorites/index.vue'),
        meta: { 
        title: '我的收藏', 
        requiresAuth: true 
        }
    },
    {
        path: 'deliveries',
        name: 'MyDeliveries',
        component: () => import('@/views/deliveries/index.vue'),
        meta: { 
        title: '投递动态与沟通', 
        requiresAuth: true 
        }
    },
    {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/profile/index.vue'),
        meta: { 
        title: '个人中心', 
        requiresAuth: true 
        }
    }
    ]
},
// 🌟 核心调整：将不需要 Header/Footer 布局的公共页面独立为一级路由
{
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/login.vue'),
    meta: { title: '安全登录 - 闪聘' }
},
{
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/register.vue'),
    meta: { title: '新用户注册 - 闪聘' }
},
// 🌟 404 路由，同样作为独立的一级路由平铺，确保渲染时全屏无布局干扰
{ 
    path: '/:pathMatch(.*)*', 
    name: 'NotFound', 
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面未找到' }
}
]

const router = createRouter({
history: createWebHistory(),
routes
})

// 路由守卫：动态修改网页标题与权限拦截
router.beforeEach((to, from, next) => {
// 修改标题
if (to.meta.title) {
    document.title = to.meta.title as string
}

// 权限校验
if (to.meta.requiresAuth && !getAccessToken()) {
    // 未登录，跳转到登录页，并把当前想去的页面传过去（方便登录后回跳）
    next({ path: '/login', query: { redirect: to.fullPath } })
} else {
    next()
}
})

export default router