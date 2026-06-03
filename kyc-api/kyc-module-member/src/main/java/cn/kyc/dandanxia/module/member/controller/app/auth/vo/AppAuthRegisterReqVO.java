package cn.kyc.dandanxia.module.member.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @program: ruoyi-vue-pro-master
 * @description: 注册对象
 * @author: 黄胜
 * @create: 2026-03-03 14:07
 **/

@Schema(description = "用户 APP - 企业认证注册请求 VO")
@Data
public class AppAuthRegisterReqVO {



    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^[1][3-9][0-9]{9}$", message = "手机号格式有误")
    private String mobile;

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")  // 需要 import javax.validation.constraints.Email;
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度为 6-20 位")
    private String password;

//    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
//    @NotBlank(message = "昵称不能为空")
//    private String nickname;

    @Schema(description = "用户名(真实姓名)", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "头像")
//    @NotBlank(message = "头像不能为空")
    private String avatar;

    @Schema(description = "性别(1男 2女)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "性别不能为空")
    private Integer sex;

//    @Schema(description = "公司营业执照名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "阿里云计算有限公司")
//    @NotBlank(message = "公司营业执照名称不能为空")
//    private String licenceName;

//    @Schema(description = "所在省", requiredMode = Schema.RequiredMode.REQUIRED, example = "浙江省")
//    @NotBlank(message = "所在省不能为空")
//    private String province;
//
//    @Schema(description = "所在市", requiredMode = Schema.RequiredMode.REQUIRED, example = "杭州市")
//    @NotBlank(message = "所在市不能为空")
//    private String city;
//
//    @Schema(description = "所在区/县", requiredMode = Schema.RequiredMode.REQUIRED, example = "西湖区")
//    @NotBlank(message = "所在区/县不能为空")
//    private String county;

//    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "某某街道某某号")
//    @NotBlank(message = "详细地址不能为空")
//    private String addressDetail;
//
//    @Schema(description = "纳税人识别号", requiredMode = Schema.RequiredMode.REQUIRED, example = "91330000XXXXXXX")
//    @NotBlank(message = "纳税人识别号不能为空")
//    private String taxpayerCode;
//
//    @Schema(description = "经营范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "计算机软硬件...")
//    @NotBlank(message = "经营范围不能为空")
//    private String businessScope;
//
//    @Schema(description = "营业执照照片(URL或Base64)", requiredMode = Schema.RequiredMode.REQUIRED)
////    @NotBlank(message = "营业执照照片不能为空")
//    private String licenceFile;
//
//    @Schema(description = "身份证正面照片(URL或Base64)", requiredMode = Schema.RequiredMode.REQUIRED)
////    @NotBlank(message = "身份证正面照片不能为空")
//    private String identityFront;
//
//    @Schema(description = "身份证反面照片(URL或Base64)", requiredMode = Schema.RequiredMode.REQUIRED)
////    @NotBlank(message = "身份证反面照片不能为空")
//    private String identityBack;


}
