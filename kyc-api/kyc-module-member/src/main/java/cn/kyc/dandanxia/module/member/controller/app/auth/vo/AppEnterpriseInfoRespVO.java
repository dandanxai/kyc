package cn.kyc.dandanxia.module.member.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "用户 APP - 企业信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppEnterpriseInfoRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16931")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("用户昵称")
    private String nickname;

    @Schema(description = "真实名字", example = "张三")
    @ExcelProperty("真实名字")
    private String name;

    @Schema(description = "性别")
    @ExcelProperty("性别")
    private Integer sex;

    @Schema(description = "出生日期")
    @ExcelProperty("出生日期")
    private LocalDateTime birthday;

    @Schema(description = "所在地", example = "18242")
    @ExcelProperty("所在地")
    private Integer areaId;

    @Schema(description = "用户备注")
    @ExcelProperty("用户备注")
    private String mark;

    @Schema(description = "头像", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("头像")
    private String avatar;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("手机号")
    private String mobile;

    @Schema(description = "邮箱")
    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("密码")
    private String password;

    @Schema(description = "注册 IP", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("注册 IP")
    private String registerIp;

    @Schema(description = "最后登录IP")
    @ExcelProperty("最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    @ExcelProperty("最后登录时间")
    private LocalDateTime loginDate;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "注册终端")
    @ExcelProperty("注册终端")
    private Long registerTerminal;

    @Schema(description = "公司营业执照名称", example = "芋艿")
    @ExcelProperty("公司营业执照名称")
    private String licenceName;

    @Schema(description = "所在省")
    @ExcelProperty("所在省")
    private String province;

    @Schema(description = "所在市")
    @ExcelProperty("所在市")
    private String city;

    @Schema(description = "所在区/县")
    @ExcelProperty("所在区/县")
    private String county;

    @Schema(description = "详细地址")
    @ExcelProperty("详细地址")
    private String addressDetail;

    @Schema(description = "纳税人识别号")
    @ExcelProperty("纳税人识别号")
    private String taxpayerCode;

    @Schema(description = "经营范围")
    @ExcelProperty("经营范围")
    private String businessScope;

    @Schema(description = "营业执照照片(URL)")
    @ExcelProperty("营业执照照片(URL)")
    private String licenceFile;

    @Schema(description = "身份证正面照片(URL)")
    @ExcelProperty("身份证正面照片(URL)")
    private String identityFront;

    @Schema(description = "身份证反面照片(URL)")
    @ExcelProperty("身份证反面照片(URL)")
    private String identityBack;

}
