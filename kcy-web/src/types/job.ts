// src/types/job.ts

// 每一项职位的简要信息（用于首页和列表页的卡片展示）
export interface JobItem {
    id: number | string;
    title: string;          // 职位名称，如：Java开发工程师
    salary: string;         // 薪资范围，如：8K-12K
    city: string;           // 城市，如：合肥
    experience: string;     // 经验要求、如：1-3年 / 应届生
    education: string;      // 学历要求，如：大专 / 本科
    companyName: string;    // 公司名称
    companyLogo: string;    // 公司Logo图片链接
    tags: string[];         // 职位标签，如：['Spring Boot', 'MySQL', '双休']
    publishTime: string;    // 发布时间，如：3天前
}