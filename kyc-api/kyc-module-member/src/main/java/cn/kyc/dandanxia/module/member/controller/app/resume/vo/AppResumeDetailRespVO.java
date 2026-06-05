package cn.kyc.dandanxia.module.member.controller.app.resume.vo;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: kyc-api
 * @description:
 * @author: 黄胜
 * @create: 2026-06-05 15:24
 **/

@Data // 🌟 绝密修复点：必须加上这个，才会自动生成外层类的 setSelfEvaluation、setExpectedPosition 等方法！
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppResumeDetailRespVO {

    // ==========================================
    // 1. 简历基础与管理元数据
    // ==========================================
    private Long id;              // 简历档案主键ID
    private Long userId;          // 用户ID
    private String fileName;      // 原始文件名
    private String filePath;      // 文件的 OSS 下载链接
    private String fileType;      // 文件类型 (pdf/docx)
    private Long fileSize;        // 文件大小 (Byte)
    private Integer isActive;     // 是否为默认/激活简历：0-否，1-是
    private Integer parseStatus;  // 解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败
    private LocalDateTime createTime; // 上传时间

    // ==========================================
    // 2. 从大模型提纯出来的“硬过滤”基础信息
    // ==========================================
    private String name;               // 求职者姓名
    private String phone;              // 手机号
    private String email;              // 邮箱
    private String gender;             // 性别
    private Integer age;               // 年龄
    private String currentCity;        // 当前城市
    private String expectedCity;       // 期望城市
    private String expectedPosition;   // 期望岗位
    private Integer expectedSalaryMin; // 期望最低薪资
    private Integer expectedSalaryMax; // 期望最高薪资
    private String availability;       // 到岗时间
    private String highestEducation;   // 最高学历
    private String school;             // 毕业学校
    private String major;              // 专业
    private Integer graduationYear;    // 毕业年份
    private BigDecimal yearsOfExperience; // 工作/项目经验年限

    // ==========================================
    // 3. 高频展示与亮点标签 (已自动解构)
    // ==========================================
    private List<String> skillTags;    // 技能点框架与工具标签数组 (Vue3, Spring Boot等)
    private List<String> coreSkills;   // 核心高频主修技能 (Java, MySQL等)
    private String achievements;       // 重大成果/核心亮点
    private String selfEvaluation;     // 自我评价文本

    // ==========================================
    // 4. 🚀 核心看点：解构出来的复杂履历数组
    // ==========================================
    private List<WorkExperienceVO> workExperiences;       // 核心工作履历
    private List<ProjectExperienceVO> projectExperiences; // 核心项目实战履历

    private List<String> certifications; // 证书列表
    private List<String> awards;         // 荣誉奖项列表

    // ==========================================
    // 5. 行业岗位大类标签
    // ==========================================
    private String jobFamilyBucket; // 岗位职能大类KEY
    private String industryBucket;  // 行业赛道大类KEY

    // =========================================================================
    // 嵌套内部类：工作履历 VO
    // =========================================================================
    @Data
    public static class WorkExperienceVO {
        private String company;     // 公司名称
        private String position;    // 担任职位
        private String startTime;   // 入职时间 (如: 2025-10)
        private String endTime;     // 离职时间
        private String description; // 职责描述
    }

    // =========================================================================
    // 嵌套内部类：项目履历 VO
    // =========================================================================
    @Data
    public static class ProjectExperienceVO {
        private String name;        // 项目名称
        private String role;        // 担任角色
        private String technology;  // 技术栈
        private String description; // 项目简介及产出
    }

}
