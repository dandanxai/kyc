package cn.kyc.dandanxia.module.member.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Schema(description = "用户 APP - 企业信息新增/修改 Request VO")
@Data
public class AppEnterpriseInfoSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16931")
    private Long id;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "用户昵称不能为空")
    private String nickname;

    @Schema(description = "真实名字", example = "张三")
    private String name;

    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "出生日期")
    private LocalDateTime birthday;

    @Schema(description = "所在地", example = "18242")
    private Integer areaId;

    @Schema(description = "用户备注")
    private String mark;

    @Schema(description = "头像", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "头像不能为空")
    private String avatar;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "注册 IP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "注册 IP不能为空")
    private String registerIp;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;

    @Schema(description = "注册终端")
    private Long registerTerminal;

    @Schema(description = "公司营业执照名称", example = "芋艿")
    private String licenceName;

    @Schema(description = "所在省")
    private String province;

    @Schema(description = "所在市")
    private String city;

    @Schema(description = "所在区/县")
    private String county;

    @Schema(description = "详细地址")
    private String addressDetail;

    @Schema(description = "纳税人识别号")
    private String taxpayerCode;

    @Schema(description = "经营范围")
    private String businessScope;

    @Schema(description = "营业执照照片(URL)")
    private String licenceFile;

    @Schema(description = "身份证正面照片(URL)")
    private String identityFront;

    @Schema(description = "身份证反面照片(URL)")
    private String identityBack;

}