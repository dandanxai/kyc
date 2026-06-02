// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import BaseLayout from '@/layout/BaseLayout.vue'

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
        component: () => import('@/views/jobs/index.vue'), // 先占位，后面开发
        meta: { title: '职位搜索' }
    },
    {
        path: 'jobs/:id',
        name: 'JobDetail',
        component: () => import('@/views/jobs/detail.vue'), // 对应你的详情页组件
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
            requiresAuth: true // 标注该页面需要登录权限
        }
    },
    {
        path: 'resume/:id',
        name: 'ResumeDetail',
        component: () => import('@/views/resume/detail.vue'),
        meta: { title: '企业详情' }
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
    // {
    //     path: 'user',
    //     name: 'UserCenter',
    //     component: () => import('@/views/user/resume.vue'), // 先占位，后面开发
    //     meta: { title: '个人中心' }
    // }
    ]
},
// {
//     path: '/login',
//     name: 'Login',
//     component: () => import('@/views/common/login.vue'), // 先占位
//     meta: { title: '登录/注册' }
// },
// {
//     path: '/:pathMatch(.*)*',
//     name: 'NotFound',
//     component: () => import('@/views/common/404.vue') // 先占位
// }
]

const router = createRouter({
history: createWebHistory(),
routes
})

// 路由守卫：动态修改网页标题
router.beforeEach((to, from, next) => {
if (to.meta.title) {
    document.title = to.meta.title as string
}
next()
})

export default router