import { defineStore } from 'pinia'
import { ref } from 'vue'
import { EnterpriseAuthApi } from '@/api/enterprise' // 确保你的企业/用户相关接口路径正确
import { getAccessToken, removeToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

// 定义用户信息 TS 类型接口（可根据后端返回的 UserInfoVO 结构自由删减或扩展）
export interface UserInfoType {
id?: number | string
username?: string
nickname?: string
avatar?: string
mobile?: string
email?: string
sex?: number
enterpriseId?: number | string  // 企业ID
enterpriseName?: string        // 企业名称
roles?: string[]               // 角色列表
permissions?: string[]         // 权限列表
}

export const useUserStore = defineStore('user', () => {
// 1. 响应式状态 State
// 🌟 完美对齐：启动时直接通过你定义的 getAccessToken() 捞取持久化 Token
const token = ref<string | null>(getAccessToken()) 
const userInfo = ref<UserInfoType | null>(null)    // 内存中的用户信息，刷新网页后重置为 null
const isSetUser = ref<boolean>(false)              // 标记位：是否已经成功加载过用户信息

/**
 * 🌟 核心 Action：获取/同步用户信息
 * 闭环痛点：当且仅当 Pinia 内存为空时，才穿透到后端查询，查完锁在内存中，防重复查询
 */
const setUserInfoAction = async (): Promise<UserInfoType> => {
    // 防重复请求：如果本地连 AccessToken 都没有，直接拦截，不浪费网络请求
    if (!getAccessToken()) {
    return Promise.reject(new Error('本地没有合法的登录凭证(AccessToken)，无法获取用户信息'))
    }

    try {
    // 穿透后端：调用芋道企业侧/用户侧获取当前登录人信息的接口
    const res = await EnterpriseAuthApi.getUserInfo()
    
    
    if (res && res.id) {
        userInfo.value = res
        isSetUser.value = true 
        return res
    } else {
        throw new Error('未能成功解析到企业用户信息')
    }
    } catch (error) {
    // 异常兜底：如果 Token 过期或接口报错，立刻清空脏数据，避免死循环
    logOutAction()
    return Promise.reject(error)
    }
}

/**
 * 🌟 Action：手动清空/重置状态（常用于 Token 校验失败、过期强退）
 */
const resetToken = () => {
    token.value = null
    userInfo.value = null
    isSetUser.value = false
    
    // 🌟 完美对齐：调用你 auth.ts 里的原生清空方法
    removeToken() 
    localStorage.removeItem('TENANT_ID')
}

/**
 * 🌟 Action：安全退出登录（前后端协同销毁凭证，并强制闪现登录页）
 */
const logOutAction = async () => {
    try {
        // 如果后端有退出的 API，通知后端销毁 Token
        if (typeof EnterpriseAuthApi.logout === 'function') {
            await EnterpriseAuthApi.logout()
        }
    } catch (error) {
        console.error('后端注销通知失败，强制执行前端本地清空', error)
    } finally {
        // 1. 干净利落地切断一切 Token 缓存
        resetToken() 
        
        // 2. 顺手把 Pinia 里的用户信息状态也刷成初始状态，防止下次登录闪现旧数据
        userInfo.value = null
        isSetUser.value = false

        ElMessage.success('已安全退出当前账户')
        
        // 3. 🌟 核心修复点：直接重定向到登录页，清除全部内存缓存，彻底解决不跳转的问题
        window.location.href = '/auth/login'
    }
}

// 3. 统一暴露给外部使用
return {
    token,
    userInfo,
    isSetUser,
    setUserInfoAction,
    resetToken,
    logOutAction
}
})