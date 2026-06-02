const config: {
    base_url: string
    result_code: number | string
    default_headers: string // 改为 string
    request_timeout: number
} = {
    base_url: import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL,
    result_code: 0, 
    request_timeout: 30000,
    default_headers: 'application/json' // 干净的字符串
}

export { config }