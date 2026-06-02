package cn.kyc.dandanxia.module.member.dal.dataobject.enterpriseinfo;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.kyc.dandanxia.framework.mybatis.core.dataobject.BaseDO;

/**
 * 企业信息 DO
 *
 * @author 芋道源码
 */
@TableName("kyc_enterprise_info")
@KeySequence("kyc_enterprise_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseInfoDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 真实名字
     */
    private String name;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 出生日期
     */
    private LocalDateTime birthday;
    /**
     * 所在地
     */
    private Integer areaId;
    /**
     * 用户备注
     */
    private String mark;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;
    /**
     * 注册 IP
     */
    private String registerIp;
    /**
     * 最后登录IP
     */
    private String loginIp;
    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;
    /**
     * 注册终端
     */
    private Long registerTerminal;
    /**
     * 公司营业执照名称
     */
    private String licenceName;
    /**
     * 所在省
     */
    private String province;
    /**
     * 所在市
     */
    private String city;
    /**
     * 所在区/县
     */
    private String county;
    /**
     * 详细地址
     */
    private String addressDetail;
    /**
     * 纳税人识别号
     */
    private String taxpayerCode;
    /**
     * 经营范围
     */
    private String businessScope;
    /**
     * 营业执照照片(URL)
     */
    private String licenceFile;
    /**
     * 身份证正面照片(URL)
     */
    private String identityFront;
    /**
     * 身份证反面照片(URL)
     */
    private String identityBack;


}
