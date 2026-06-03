import axios, { AxiosError } from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import qs from 'qs'
import { config } from './config'
import errorCode from './errorCode'
import { EnterpriseAuthApi } from '@/api/enterprise'
// 这里的 auth 方法你需要根据自己的项目（比如 localStorage 或 Pinia）来实现
// 假设你写在 src/utils/auth.ts 中：
import { getAccessToken, getRefreshToken, getTenantId, setToken, removeToken } from '../../utils/auth'

const { result_code, base_url, request_timeout } = config

const ignoreMsgs = ['无效的刷新令牌', '刷新令牌已过期']
export const isRelogin = { show: false }
let requestList: any[] = []
let isRefreshToken = false
const whiteList: string[] = ['/login', '/refresh-token', '/enterprise/auth/create']

const service: AxiosInstance = axios.create({
baseURL: base_url,
timeout: request_timeout,
withCredentials: false,
paramsSerializer: (params) => {
    return qs.stringify(params, { allowDots: true })
}
})

// Request 拦截器
service.interceptors.request.use(
(config: InternalAxiosRequestConfig) => {
    let isToken = (config!.headers || {}).isToken !== false
    if (isToken && whiteList.some((v) => config.url?.includes(v))) {
    isToken = false
    }
    if (getAccessToken() && isToken) {
    config.headers.Authorization = 'Bearer ' + getAccessToken()
    }
    
    // 注入租户ID (对接芋道后端必须)
    const tenantId = getTenantId()
    if (tenantId) {
    config.headers['tenant-id'] = tenantId
    }

    const method = config.method?.toUpperCase()
    if (method === 'GET') {
    config.headers['Cache-Control'] = 'no-cache'
    config.headers['Pragma'] = 'no-cache'
    } else if (method === 'POST') {
    const contentType = config.headers['Content-Type'] || config.headers['content-type']
    if (contentType === 'application/x-www-form-urlencoded') {
        if (config.data && typeof config.data !== 'string') {
        config.data = qs.stringify(config.data)
        }
    }
    }
    return config
},
(error: AxiosError) => {
    return Promise.reject(error)
}
)

// Response 拦截器
service.interceptors.response.use(
async (response: AxiosResponse<any>) => {
    let { data } = response
    const currentConfig = response.config
    if (!data) {
    throw new Error('HTTP请求没有返回值')
    }

    // 处理二进制文件流（如导出 Excel）
    if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
    if (response.data.type !== 'application/json') {
        return response.data
    }
    data = await new Response(response.data).json()
    }

    const code = data.code ?? result_code
    const msg = data.msg || errorCode[code] || errorCode['default']

    if (ignoreMsgs.indexOf(msg) !== -1) {
    return Promise.reject(msg)
    } 
    
    // 核心：401 Token 过期无感刷新
    // 核心：401 Token 过期无感刷新
    else if (code === 401) {
        if (!isRefreshToken) {
        isRefreshToken = true
        
        if (!getRefreshToken()) {
            return handleAuthorized()
        }
        
        try {
            // 1. 偷偷去后端换取新牌（注意：EnterpriseAuthApi.refreshToken 已经被剥壳或未剥壳）
            const refreshTokenRes = await refreshToken()
            
            // 🌟 芋道底层适配核心点：
            // 因为你使用的是封装好的 API 或原生 axios，必须要保证拿到的是含有 accessToken 的实体对象
            // 如果你的 API 返回的是剥壳后的数据，那就是 refreshTokenRes；如果是原生响应，则是 refreshTokenRes.data.data
            const tokenData = refreshTokenRes.data?.data || refreshTokenRes.data || refreshTokenRes
            
            if (!tokenData || !tokenData.accessToken) {
            throw new Error('刷新令牌返回的结构不正确，无法读取新Token')
            }

            // 2. 将全新的双 Token 拍进本地缓存（localStorage）
            setToken(tokenData)
            
            // 3. 🌟 核心修复：更新当前请求的 Authorization 头（确保是最新的 AccessToken）
            const newAccessToken = getAccessToken()
            currentConfig.headers!.Authorization = 'Bearer ' + newAccessToken
            
            // 4. 🌟 核心修复：依次执行并回放队列里的请求，且必须把最新的 Token 强行注入进去！
            requestList.forEach((cb: any) => cb(newAccessToken))
            requestList = []
            
            // 5. 回放当前最先触发 401 的这个请求
            return service(currentConfig)
        } catch (e) {
            console.error('无感刷新彻底失败，强制清空凭证重定向', e)
            requestList = []
            return handleAuthorized()
        } finally {
            isRefreshToken = false
        }
        } else {
        // 🌟 核心修复：当处于“正在刷新”状态时，把请求挂起加入队列
        // 必须通过形参把最新的 token 传进来，动态赋给 headers，不能用闭包去捞旧配置！
        return new Promise((resolve) => {
            requestList.push((token: string) => {
            currentConfig.headers!.Authorization = 'Bearer ' + token
            resolve(service(currentConfig))
            })
        })
        }
    } else if (code === 500) {
    ElMessage.error('系统接口异常')
    return Promise.reject(new Error(msg))
    } else if (code !== 0 && code !== 200) {
    if (msg === '无效的刷新令牌') {
        return handleAuthorized()
    } else {
        ElNotification.error({ title: msg })
    }
    return Promise.reject('error')
    } else {
    return data // 业务直接拿到的就是后端的实体数据
    }
},
(error: AxiosError) => {
    let { message } = error
    if (message === 'Network Error') {
    message = '后端网络连接异常'
    } else if (message.includes('timeout')) {
    message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
    message = '系统接口请求失败，状态码：' + message.substr(message.length - 3)
    }
    ElMessage.error(message)
    return Promise.reject(error)
}
)

const refreshToken = async () => {
    // 1. 注入租户ID (对接后端必须)
    axios.defaults.headers.common['tenant-id'] = getTenantId()
    
    // 2. 🌟 核心修改点：改为你的企业端刷新令牌路径，并通过 Query 参数拼入 refreshToken
    // 如果你的 Controller 统一前缀是 /enterprise/auth，那就改成 /enterprise/auth/refresh-token
    const refreshTokenStr = getRefreshToken() || ''
    return await EnterpriseAuthApi.refreshToken(refreshTokenStr)
}

const handleAuthorized = () => {
if (!isRelogin.show) {
    if (window.location.href.includes('login')) {
    return
    }
    isRelogin.show = true
    ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
    showCancelButton: false,
    closeOnClickModal: false,
    showClose: false,
    closeOnPressEscape: false,
    confirmButtonText: '重新登录',
    type: 'warning'
    }).then(() => {
    removeToken() // 清理本地缓存的Token
    isRelogin.show = false
    window.location.href = '/auth/login'
    })
}
return Promise.reject('登录超时')
}

export { service }