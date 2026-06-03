import request from '@/utils/request' // 对应你 index.ts 的导出路径

// 基础设施文件总 API 形式
export const InfraFileApi = {
    /**
     * 统一文件上传接口（对接芋道后端 infra 模块或自定义上传）
     * @param file 文件对象
     */
    uploadFile: async (file: File) => {
        const formData = new FormData()
        formData.append('file', file)
        return await request.postOriginal({
        url: '/infra/file/upload',
        data: formData,
        headersType: 'multipart/form-data'
        })
    }
}