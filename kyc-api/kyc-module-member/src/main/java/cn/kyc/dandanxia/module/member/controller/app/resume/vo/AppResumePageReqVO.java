package cn.kyc.dandanxia.module.member.controller.app.resume.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.kyc.dandanxia.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.kyc.dandanxia.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 APP - 用户简历档案分页 Request VO")
@Data
public class AppResumePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "15347")
    private Long userId;

    @Schema(description = "简历文件名", example = "李四")
    private String fileName;

    @Schema(description = "文件存储路径/OSS URL")
    private String filePath;

    @Schema(description = "文件类型后缀(pdf/docx)", example = "2")
    private String fileType;

    @Schema(description = "文件大小(Byte)")
    private Long fileSize;

    @Schema(description = "是否为默认/激活简历：0-否，1-是")
    private Integer isActive;

    @Schema(description = "文档解析后的JSON文本内容")
    private String parsedJson;

    @Schema(description = "解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败", example = "2")
    private Integer parseStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}