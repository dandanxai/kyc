import request from '@/utils/request' 
/** 用户简历档案信息 */
export interface Resume {
          id: number; // 简历档案主键ID
          userId?: number; // 用户ID
          fileName?: string; // 简历文件名
          filePath?: string; // 文件存储路径/OSS URL
          fileType: string; // 文件类型后缀(pdf/docx)
          fileSize: number; // 文件大小(Byte)
          isActive?: number; // 是否为默认/激活简历：0-否，1-是
          parsedJson: string; // 文档解析后的JSON文本内容
          parseStatus?: number; // 解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败
  }

// 用户简历档案 API
export const ResumeApi = {
  // 查询用户简历档案分页
  getResumePage: async (params: any) => {
    return await request.get({ url: `/member/resume/page`, params })
  },

  // 查询用户简历档案详情
  getResume: async (id: number) => {
    return await request.get({ url: `/member/resume/get?id=` + id })
  },

  // 查询用户简历档案详情
  getResumeGraph: async (id: number) => {
    return await request.get({ url: `/member/resume/get-graph?id=` + id })
  },

  // 新增用户简历档案
  createResume: async (data: Resume) => {
    return await request.post({ url: `/member/resume/create`, data })
  },

  // 修改用户简历档案
  updateResume: async (data: Resume) => {
    return await request.put({ url: `/member/resume/update`, data })
  },

  // 修改简历默认
  updateResumeActive: async (id: number) => {
    return await request.put({ url: `/member/resume/update-active`, data: { id } })
  },

  // 删除用户简历档案
  deleteResume: async (id: number) => {
    return await request.delete({ url: `/member/resume/delete?id=` + id })
  },

  /** 批量删除用户简历档案 */
  deleteResumeList: async (ids: number[]) => {
    return await request.delete({ url: `/member/resume/delete-list?ids=${ids.join(',')}` })
  },

  // 导出用户简历档案 Excel
  exportResume: async (params) => {
    return await request.download({ url: `/member/resume/export-excel`, params })
  },
}
