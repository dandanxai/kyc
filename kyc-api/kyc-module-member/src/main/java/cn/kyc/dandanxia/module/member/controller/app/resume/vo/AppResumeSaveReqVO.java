package cn.kyc.dandanxia.module.member.controller.app.resume.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "用户 APP - 用户简历档案新增/修改 Request VO")
@Data
public class AppResumeSaveReqVO {

    @Schema(description = "简历档案主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27008")
    private Long id;

    @Schema(description = "简历文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "简历文件名不能为空")
    private String fileName;

    @Schema(description = "文件存储路径/OSS URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件存储路径/OSS URL不能为空")
    private String filePath;

    @Schema(description = "文件类型后缀(pdf/docx)", example = "2")
    private String fileType;

    @Schema(description = "文件大小(Byte)")
    private Long fileSize;

//    @Schema(description = "是否为默认/激活简历：0-否，1-是", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "是否为默认/激活简历：0-否，1-是不能为空")
//    private Integer isActive;

}