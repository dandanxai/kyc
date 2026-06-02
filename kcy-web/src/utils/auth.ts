// src/utils/auth.ts
const AccessTokenKey = 'ACCESS_TOKEN'
const RefreshTokenKey = 'REFRESH_TOKEN'
const TenantIdKey = 'TENANT_ID'

export const getAccessToken = () => localStorage.getItem(AccessTokenKey)
export const getRefreshToken = () => localStorage.getItem(RefreshTokenKey)
export const getTenantId = () => localStorage.getItem(TenantIdKey) || '1' // 如果你关了多租户，这里默认给 '1' 即可

export const setToken = (data: { accessToken: string; refreshToken: string }) => {
localStorage.setItem(AccessTokenKey, data.accessToken)
localStorage.setItem(RefreshTokenKey, data.refreshToken)
}

export const removeToken = () => {
localStorage.removeItem(AccessTokenKey)
localStorage.removeItem(RefreshTokenKey)
}