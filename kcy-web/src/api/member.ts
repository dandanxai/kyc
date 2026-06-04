import request from '@/utils/request' 

// 个人认证总 API 形式
export const UserApi = {
    /**
     * 修改用户密码
     * @param data 新旧密码
     */
    updateUserPassword: async (data: any) => {
        return await request.put({
            url: '/member/user/update-password',
            data
        })
    },

    /**
     * 修改基本信息
     * @param data 修改基本信息
     */
    updateUser: async (data: any) => {
        return await request.put({
            url: '/member/user/update',
            data
        })
    },

    /**
     * 修改头像
     * @param data 修改头像
     */
    updateUserAvatar: async (params: any) => {
        return await request.put({
            url: '/member/user/update-avatar',
            params
        })
    },
}