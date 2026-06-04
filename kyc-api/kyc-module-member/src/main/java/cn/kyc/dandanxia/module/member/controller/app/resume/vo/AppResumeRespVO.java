package cn.kyc.dandanxia.module.member.controller.app.resume.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "用户 APP - 用户简历档案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppResumeRespVO {

    @Schema(description = "简历档案主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27008")
    @ExcelProperty("简历档案主键ID")
    private Long id;

    @Schema(description = "简历文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("简历文件名")
    private String fileName;

    @Schema(description = "是否为默认/激活简历：0-否，1-是", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否为默认/激活简历：0-否，1-是")
    private Integer isActive;

    @Schema(description = "解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败")
    private Integer parseStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "技能点", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "[\"Spring Boot\", \"Vue3\", \"MyBatis-Plus\", \"MySQL\", \"UniApp\"]")
    @ExcelProperty("技能点")
    private List<String> skills;

    @Schema(description = "简历重点核心", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "包含《乐行旅途Mini-Program》及《凌云智控物联网平台》完整全栈研发经历。")
    @ExcelProperty("简历重点核心")
    private String coreHighlight;

}
