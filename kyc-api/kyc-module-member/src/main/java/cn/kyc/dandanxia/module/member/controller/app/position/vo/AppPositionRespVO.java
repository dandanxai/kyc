package cn.kyc.dandanxia.module.member.controller.app.position.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "用户 APP - 企业岗位招聘档案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppPositionRespVO {

    @Schema(description = "岗位档案主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27678")
    @ExcelProperty("岗位档案主键ID")
    private Long id;

    @Schema(description = "解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败")
    private Integer parseStatus;

    @Schema(description = "岗位招聘状态：0-关闭/下线，1-招聘中/激活", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("岗位招聘状态：0-关闭/下线，1-招聘中/激活")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "岗位关键字标签(逗号分隔，用于快速检索展示)")
    @ExcelProperty("岗位关键字标签(逗号分隔，用于快速检索展示)")
    private String keywords;

    @Schema(description = "工作城市(配对简历的expected_city)")
    @ExcelProperty("工作城市(配对简历的expected_city)")
    private String city;

    @Schema(description = "学历要求生死线(配对简历的highest_education)")
    @ExcelProperty("学历要求生死线(配对简历的highest_education)")
    private String educationRequired;

    @Schema(description = "最低工作年限要求(配对简历的years_of_experience)")
    @ExcelProperty("最低工作年限要求(配对简历的years_of_experience)")
    private BigDecimal yearsOfExperienceMin;

    @Schema(description = "岗位提供最低薪资(元/月，配对简历的expected_salary_min)")
    @ExcelProperty("岗位提供最低薪资(元/月，配对简历的expected_salary_min)")
    private Integer salaryMin;

    @Schema(description = "岗位提供最高薪资(元/月，配对简历的expected_salary_max)")
    @ExcelProperty("岗位提供最高薪资(元/月，配对简历的expected_salary_max)")
    private Integer salaryMax;

    @Schema(description = "岗位需求文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("岗位需求文件名")
    private String fileName;
}
