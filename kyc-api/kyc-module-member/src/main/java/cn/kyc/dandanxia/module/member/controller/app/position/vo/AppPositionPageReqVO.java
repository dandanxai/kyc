package cn.kyc.dandanxia.module.member.controller.app.position.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.kyc.dandanxia.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.kyc.dandanxia.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 APP - 企业岗位招聘档案分页 Request VO")
@Data
public class AppPositionPageReqVO extends PageParam {

    @Schema(description = "企业ID", example = "4822")
    private Long enterpriseId;

    @Schema(description = "文档解析后的JSON全量文本内容(包含职责、要求、加分项等)")
    private String parsedJson;

    @Schema(description = "解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败", example = "2")
    private Integer parseStatus;

    @Schema(description = "岗位招聘状态：0-关闭/下线，1-招聘中/激活", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "核心必选技能标签(逗号分隔，用于前端快速渲染)")
    private String requiredSkills;

    @Schema(description = "岗位关键字标签(逗号分隔，用于快速检索展示)")
    private String keywords;

    @Schema(description = "岗位职能大类(字典值，配对简历的job_family_bucket)")
    private String jobFamilyBucket;

    @Schema(description = "行业赛道大类(字典值，配对简历的industry_bucket)")
    private String industryBucket;

    @Schema(description = "工作城市(配对简历的expected_city)")
    private String city;

    @Schema(description = "学历要求生死线(配对简历的highest_education)")
    private String educationRequired;

    @Schema(description = "最低工作年限要求(配对简历的years_of_experience)")
    private BigDecimal yearsOfExperienceMin;

    @Schema(description = "岗位提供最低薪资(元/月，配对简历的expected_salary_min)")
    private Integer salaryMin;

    @Schema(description = "岗位提供最高薪资(元/月，配对简历的expected_salary_max)")
    private Integer salaryMax;

    @Schema(description = "岗位需求文件名", example = "张三")
    private String fileName;

    @Schema(description = "岗位需求文件存储路径/OSS URL")
    private String filePath;

    @Schema(description = "文件类型后缀(pdf/docx)", example = "2")
    private String fileType;

    @Schema(description = "文件大小(Byte)")
    private Long fileSize;

}
