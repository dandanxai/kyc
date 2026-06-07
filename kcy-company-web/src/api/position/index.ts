import request from '@/utils/request' // 对应你 index.ts 的导出路径

/** 企业岗位招聘档案信息 */
export interface Position {
          id: number; // 岗位档案主键ID
          enterpriseId?: number; // 企业ID
          jobTitle?: string; // 岗位名称
          parsedJson: string; // 文档解析后的JSON全量文本内容(包含职责、要求、加分项等)
          parseStatus?: number; // 解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败
          status?: number; // 岗位招聘状态：0-关闭/下线，1-招聘中/激活
          requiredSkills: string; // 核心必选技能标签(逗号分隔，用于前端快速渲染)
          keywords: string; // 岗位关键字标签(逗号分隔，用于快速检索展示)
          jobFamilyBucket: string; // 岗位职能大类(字典值，配对简历的job_family_bucket)
          industryBucket: string; // 行业赛道大类(字典值，配对简历的industry_bucket)
          city: string; // 工作城市(配对简历的expected_city)
          educationRequired: string; // 学历要求生死线(配对简历的highest_education)
          yearsOfExperienceMin: number; // 最低工作年限要求(配对简历的years_of_experience)
          salaryMin: number; // 岗位提供最低薪资(元/月，配对简历的expected_salary_min)
          salaryMax: number; // 岗位提供最高薪资(元/月，配对简历的expected_salary_max)
          fileName?: string; // 岗位需求文件名
          filePath?: string; // 岗位需求文件存储路径/OSS URL
          fileType: string; // 文件类型后缀(pdf/docx)
          fileSize: number; // 文件大小(Byte)
  }

// 企业岗位招聘档案 API
export const PositionApi = {
  // 查询企业岗位招聘档案分页
  getPositionPage: async (params: any) => {
    return await request.get({ url: `/member/position/page`, params })
  },

  // 查询企业岗位招聘档案详情
  getPosition: async (id: number) => {
    return await request.get({ url: `/member/position/get?id=` + id })
  },

  // 新增企业岗位招聘档案
  createPosition: async (data: Position) => {
    return await request.post({ url: `/member/position/create`, data })
  },

  // 修改企业岗位招聘档案
  updatePosition: async (data: Position) => {
    return await request.put({ url: `/member/position/update`, data })
  },

  // 删除企业岗位招聘档案
  deletePosition: async (id: number) => {
    return await request.delete({ url: `/member/position/delete?id=` + id })
  },

  /** 批量删除企业岗位招聘档案 */
  deletePositionList: async (ids: number[]) => {
    return await request.delete({ url: `/member/position/delete-list?ids=${ids.join(',')}` })
  },

  // 导出企业岗位招聘档案 Excel
  exportPosition: async (params) => {
    return await request.download({ url: `/member/position/export-excel`, params })
  },
}
