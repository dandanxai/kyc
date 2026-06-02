package cn.kyc.dandanxia.module.member.controller.app.auth.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.kyc.dandanxia.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.kyc.dandanxia.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 APP - 企业信息分页 Request VO")
@Data
public class AppEnterpriseInfoPageReqVO extends PageParam {

    @Schema(description = "用户昵称", example = "王五")
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

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "注册 IP")
    private String registerIp;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] loginDate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

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