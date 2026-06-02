import axios, { AxiosError } from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import qs from 'qs'
import { config } from './config'
import errorCode from './errorCode'

// 这里的 auth 方法你需要根据自己的项目（比如 localStorage 或 Pinia）来实现
// 假设你写在 src/utils/auth.ts 中：
import { getAccessToken, getRefreshToken, getTenantId, setToken, removeToken } from '@/utils/auth'

const { result_code, base_url, request_timeout } = config

const ignoreMsgs = ['无效的刷新令牌', '刷新令牌已过期']
export const isRelogin = { show: false }
let requestList: any[] = []
let isRefreshToken = false
const whiteList: string[] = ['/login', '/refresh-token']

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
    else if (code === 401) {
    if (!isRefreshToken) {
        isRefreshToken = true
        if (!getRefreshToken()) {
        return handleAuthorized()
        }
        try {
        const refreshTokenRes = await refreshToken()
        // 刷新成功，保存新 Token
        setToken(refreshTokenRes.data.data)
        currentConfig.headers!.Authorization = 'Bearer ' + getAccessToken()
        
        // 回放队列
        requestList.forEach((cb: any) => cb())
        requestList = []
        return service(currentConfig)
        } catch (e) {
        requestList.forEach((cb: any) => cb())
        return handleAuthorized()
        } finally {
        requestList = []
        isRefreshToken = false
        }
    } else {
        // 正在刷新中，把请求挂起加入队列
        return new Promise((resolve) => {
        requestList.push(() => {
            currentConfig.headers!.Authorization = 'Bearer ' + getAccessToken()
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
axios.defaults.headers.common['tenant-id'] = getTenantId()
return await axios.post(base_url + '/system/auth/refresh-token?refreshToken=' + getRefreshToken())
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
    window.location.href = '/login' // 直接跳转回你自己的登录页
    })
}
return Promise.reject('登录超时')
}

export { service }