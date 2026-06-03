import request from '@/utils/request' // 对应你 index.ts 的导出路径

// 声明登录成功后的标准返回数据结构
export interface LoginRespVO {
  userId: number
  accessToken: string
  refreshToken: string
  expiresTime: number
}

// 个人认证总 API 形式
export const UserAuthApi = {
  /**
   * 个人入驻申请（注册）
   * @param data 注册完整表单数据
   */
  registerUser: async (data: any) => {
    return await request.post({
      url: '/member/auth/register',
      data
    })
  },

  /**
   * 个人端用户登录
   * @param data 登录表单数据（账号 + 密码）
   */
  loginUser: async (data: any) => {
    return await request.post<LoginRespVO>({
      url: '/member/auth/login',
      data
    })
  },

  /**
   * 获取个人信息
   */
  getUserInfo: async () => {
    return await request.get({
      url: '/member/user/get' 
    })
  },

  /**
   * 🌟 核心新增：个人端用户登出系统
   */
  logout: async () => {
    return await request.post({
      url: '/member/auth/logout'
    })
  },

  /**
   * 🌟 规范封装：刷新令牌接口
   * @param refreshToken 刷新令牌
   * @returns 返回最新的双 Token 实体数据 (AppAuthLoginRespVO)
   */
  refreshToken: (refreshToken: string) => {
    return request.postOriginal({
      url: '/member/auth/refresh-token',
      // 🌟 使用 params 传参，Axios 会自动将其拼接到 URL 后面变为 ?refreshToken=xxx
      // 这样就彻底告别了难看的字符串加号拼接！
      params: {
        refreshToken
      },
      // 标记位：刷新 Token 的请求本身不要再触发 token 校验或拦截
      headers: {
        isToken: false
      }
    })
  }
}