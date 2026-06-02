interface CompanyDetail {
  id: string;
  name: string;
  logo: string;
  slogan: string;
  desc: string;          // 公司简介
  tags: string[];        // 公司亮点/福利
  industry: string;      // 所属行业
  stage: string;         // 融资阶段
  scale: string;         // 人员规模
  website: string;       // 公司官网
  jobCount: number;      // 在招职位总数
  jobs: JobItem[];       // 关联的职位列表
}