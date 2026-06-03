// src/utils/auth.ts
const AccessTokenKey = 'ACCESS_TOKEN'
const RefreshTokenKey = 'REFRESH_TOKEN'
const TenantIdKey = 'TENANT_ID'

export const getAccessToken = () => localStorage.getItem(AccessTokenKey)
export const getRefreshToken = () => localStorage.getItem(RefreshTokenKey)
export const getTenantId = () => localStorage.getItem(TenantIdKey) || '1' // 如果你关了多租户，这里默认给 '1' 即可

/**
 * 🌟 设置/更新 Token 
 * 适配后端返回的 AppAuthLoginRespVO 结构
 */
export function setToken(data: any) {
    if (!data) return
    
    // 如果传进来的是一个对象 (例如：{ accessToken: 'xxx', refreshToken: 'xxx' })
    if (typeof data === 'object') {
    if (data.accessToken) localStorage.setItem(AccessTokenKey, data.accessToken)
    if (data.refreshToken) localStorage.setItem(RefreshTokenKey, data.refreshToken)
    } else {
    // 兼容只传字符串的情况
    localStorage.setItem(AccessTokenKey, data)
    }
}

export const removeToken = () => {
localStorage.removeItem(AccessTokenKey)
localStorage.removeItem(RefreshTokenKey)
}