import { AxiosHeaders } from 'axios'

const config: {
    base_url: string
    result_code: number | string
    default_headers: AxiosHeaders
    request_timeout: number
} = {
    // 读取你的 Vite 环境变量，拼接后端的接口地址
    base_url: import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL,
    // 芋道后端成功的状态码
    result_code: 0, 
    // 请求超时时间
    request_timeout: 30000,
    // 默认请求头
    default_headers: 'application/json' as unknown as AxiosHeaders
}

export { config }