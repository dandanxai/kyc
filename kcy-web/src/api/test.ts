import request from '@/config/axios'

// 1. 测试公开接口（不需要 Token 的接口，比如芋道的登录）
export const loginTest = (data: any) => {
return request.post({
    url: '/member/auth/login',
    data
})
}

// 2. 测试受保护接口（需要 Token 的接口，比如获取当前登录用户信息）
export const getUserProfile = () => {
return request.get({
    url: '/member/user/profile/get'
})
}