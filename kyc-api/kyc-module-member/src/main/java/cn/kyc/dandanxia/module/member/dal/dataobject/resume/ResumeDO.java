package cn.kyc.dandanxia.module.member.dal.dataobject.resume;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import java.util.*;
import java.math.BigDecimal; // 🌟 引入 BigDecimal 对应达梦的 DECIMAL(4,1)
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.kyc.dandanxia.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户简历档案 DO
 *
 * @author 芋道源码
 */
@TableName(value = "kyc_resume", autoResultMap = true)
@KeySequence("kyc_resume_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2、达梦数据库的主键自增
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDO extends BaseDO {

    /**
     * 简历档案主键ID
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 简历文件名
     */
    private String fileName;
    /**
     * 文件存储路径/OSS URL
     */
    private String filePath;
    /**
     * 文件类型后缀(pdf/docx)
     */
    private String fileType;
    /**
     * 文件大小(Byte)
     */
    private Long fileSize;
    /**
     * 是否为默认/激活简历：0-否，1-是
     */
    private Integer isActive;
    /**
     * 文档解析后的JSON文本内容
     */
    private String parsedJson;
    /**
     * 解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败
     */
    private Integer parseStatus;

    /**
     * 技能点
     * 🌟 重点：前端传过来的是 ["Vue3", "Spring Boot"]，
     * MyBatis-Plus 会自动通过 Jackson 将其转为 JSON 字符串或逗号格式存入达梦 VARCHAR 中
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> skillTags;

    /**
     * 简历重点核心/重大成果 (对应原本的 core_highlight)
     */
    private String achievements;

    // ==========================================
    // 🚀 🌟 绝密补全：达梦数据库全新升级的硬过滤与路由字段 🌟
    // ==========================================

    /**
     * 求职者姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 岗位职能大类(如: TECH_DEV)
     */
    private String jobFamilyBucket;

    /**
     * 行业赛道大类(如: INTERNET_AI_SOFTWARE)
     */
    private String industryBucket;

    /**
     * 期望城市
     */
    private String expectedCity;

    /**
     * 最高学历(如: 大专、本科、硕士)
     */
    private String highestEducation;

    /**
     * 工作/项目经验年限
     * 🌟 注意：对应达梦的 DECIMAL(4,1)，在 Java 中用 BigDecimal 接收最稳妥，方便做精确评分
     */
    private BigDecimal yearsOfExperience;

    /**
     * 毕业年份 (如: 2026)
     */
    private Integer graduationYear;

    /**
     * 期望最低薪资(元/月)
     */
    private Integer expectedSalaryMin;

    /**
     * 期望最高薪资(元/月)
     */
    private Integer expectedSalaryMax;

}