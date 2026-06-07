package cn.kyc.dandanxia.module.member.dal.dataobject.position;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cn.kyc.dandanxia.framework.mybatis.core.dataobject.BaseDO;

/**
 * 企业岗位招聘档案 DO
 *
 * @author 芋道源码
 */
@TableName(value = "kyc_position", autoResultMap = true)
@KeySequence("kyc_position_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionDO extends BaseDO {

    /**
     * 岗位档案主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 企业ID
     */
    private Long enterpriseId;
    /**
     * 文档解析后的JSON全量文本内容(包含职责、要求、加分项等)
     */
    private String parsedJson;
    /**
     * 解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败
     */
    private Integer parseStatus;
    /**
     * 岗位招聘状态：0-关闭/下线，1-招聘中/激活
     */
    private Integer status;
    /**
     * 核心必选技能标签(逗号分隔，用于前端快速渲染)
     */
    private String requiredSkills;
    /**
     * 岗位关键字标签(逗号分隔，用于快速检索展示)
     */
    private String keywords;
    /**
     * 岗位职能大类(字典值，配对简历的job_family_bucket)
     */
    private String jobFamilyBucket;
    /**
     * 行业赛道大类(字典值，配对简历的industry_bucket)
     */
    private String industryBucket;
    /**
     * 工作城市(配对简历的expected_city)
     */
    private String city;
    /**
     * 学历要求生死线(配对简历的highest_education)
     */
    private String educationRequired;
    /**
     * 最低工作年限要求(配对简历的years_of_experience)
     */
    private BigDecimal yearsOfExperienceMin;
    /**
     * 岗位提供最低薪资(元/月，配对简历的expected_salary_min)
     */
    private Integer salaryMin;
    /**
     * 岗位提供最高薪资(元/月，配对简历的expected_salary_max)
     */
    private Integer salaryMax;
    /**
     * 岗位需求文件名
     */
    private String fileName;
    /**
     * 岗位需求文件存储路径/OSS URL
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


}
