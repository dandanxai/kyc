package cn.kyc.dandanxia.module.member.controller.app.resume.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "用户 APP - 用户简历档案新增/修改 Request VO")
@Data
public class AppResumeUpdateActiveReqVO {

    @Schema(description = "简历档案主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27008")
    private Long id;
}