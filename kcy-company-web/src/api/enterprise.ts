import request from '@/utils/request' // 对应你 index.ts 的导出路径

/**
 * 统一文件上传接口（对接芋道后端 infra 模块或自定义上传）
 * @param file 文件对象
 */
export const uploadFileApi = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.postOriginal({
    url: '/infra/file/upload', 
    data: formData,
    headersType: 'multipart/form-data'
  })
}

/**
 * 企业入驻申请（注册）
 * @param data 注册完整表单数据
 */
export const registerEnterpriseApi = (data: any) => {
  return request.post({
    url: '/enterprise/auth/create',
    data
  })
}

/**
 * 企业端用户登录
 * @param data 登录表单数据（手机号/账号 + 密码）
 */
export const loginEnterpriseApi = (data: any) => {
  return request.post({
    url: '/enterprise/auth/create', // 如果你后端测试非要走 create，这里可以改成 '/enterprise/auth/create'
    data
  })
}