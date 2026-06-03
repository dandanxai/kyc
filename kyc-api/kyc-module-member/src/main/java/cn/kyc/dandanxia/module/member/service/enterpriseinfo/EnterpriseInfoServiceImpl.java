package cn.kyc.dandanxia.module.member.service.enterpriseinfo;

import cn.hutool.core.lang.Assert;
import cn.kyc.dandanxia.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.kyc.dandanxia.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import cn.kyc.dandanxia.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.kyc.dandanxia.framework.common.enums.CommonStatusEnum;
import cn.kyc.dandanxia.framework.common.enums.UserTypeEnum;
import cn.kyc.dandanxia.framework.common.exception.ServiceException;
import cn.kyc.dandanxia.framework.common.util.monitor.TracerUtils;
import cn.kyc.dandanxia.framework.common.util.servlet.ServletUtils;
import cn.kyc.dandanxia.module.member.controller.app.auth.vo.*;
import cn.kyc.dandanxia.module.member.convert.auth.AuthConvert;
import cn.kyc.dandanxia.module.member.dal.dataobject.enterpriseinfo.EnterpriseInfoDO;
import cn.kyc.dandanxia.module.member.dal.dataobject.user.MemberUserDO;
import cn.kyc.dandanxia.module.member.dal.mysql.enterpriseinfo.EnterpriseInfoMapper;
import cn.kyc.dandanxia.module.system.api.logger.LoginLogApi;
import cn.kyc.dandanxia.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.kyc.dandanxia.module.system.api.social.SocialClientApi;
import cn.kyc.dandanxia.module.system.api.social.SocialUserApi;
import cn.kyc.dandanxia.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.kyc.dandanxia.module.system.enums.logger.LoginLogTypeEnum;
import cn.kyc.dandanxia.module.system.enums.logger.LoginResultEnum;
import cn.kyc.dandanxia.module.system.enums.oauth2.OAuth2ClientConstants;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.common.util.object.BeanUtils;


import static cn.kyc.dandanxia.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kyc.dandanxia.framework.common.util.collection.CollectionUtils.convertList;
import static cn.kyc.dandanxia.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.kyc.dandanxia.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.kyc.dandanxia.framework.web.core.util.WebFrameworkUtils.getTerminal;
import static cn.kyc.dandanxia.module.member.enums.ErrorCodeConstants.*;

/**
 * 企业信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class EnterpriseInfoServiceImpl implements EnterpriseInfoService {

    @Resource
    private EnterpriseInfoMapper enterpriseInfoMapper;

    @Resource
    private LoginLogApi loginLogApi;


    @Resource
    private OAuth2TokenCommonApi oauth2TokenApi;
    @Resource
    private SocialUserApi socialUserApi;
    @Resource
    private SocialClientApi socialClientApi;


    @Override
    public AppAuthLoginRespVO register(AppEnterpriseInfoSaveReqVO reqVO) {
        String userIp = getClientIP();

        // 1. 调用专用的企业注册逻辑
        EnterpriseInfoDO user = createRegisterUser(reqVO, userIp, getTerminal());
        Assert.notNull(user, "获取用户失败，结果为空");

        // 2. 完美复用老代码：创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, user.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE, null);
    }

    @Override
    public AppAuthLoginRespVO login(AppAuthLoginReqVO reqVO) {
        // 使用账号（手机、邮箱、用户名） + 密码，进行登录。
        EnterpriseInfoDO user = login0(reqVO.getMobile(), reqVO.getPassword());

        // 如果 socialType 非空，说明需要绑定社交用户
        String openid = null;
        if (reqVO.getSocialType() != null) {
            openid = socialUserApi.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
                    reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
        }

        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, reqVO.getMobile(), LoginLogTypeEnum.LOGIN_MOBILE, openid);
    }

    private EnterpriseInfoDO login0(String username, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_MOBILE;

        // 校验账号是否存在
        EnterpriseInfoDO user = enterpriseInfoMapper.selectByAccount(username);

        if (user == null) {
            createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private AppAuthLoginRespVO createTokenAfterLoginSuccess(EnterpriseInfoDO user, String mobile,
                                                            LoginLogTypeEnum logType, String openid) {
        // 插入登陆日志
        createLoginLog(user.getId(), mobile, logType, LoginResultEnum.SUCCESS);
        // 创建 Token 令牌
        OAuth2AccessTokenRespDTO accessTokenRespDTO = oauth2TokenApi.createAccessToken(new OAuth2AccessTokenCreateReqDTO()
                .setUserId(user.getId()).setUserType(getUserType().getValue())
                .setClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT));
        // 构建返回结果
        return AuthConvert.INSTANCE.convert(accessTokenRespDTO, openid);
    }

    private void createLoginLog(Long userId, String mobile, LoginLogTypeEnum logType, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(mobile);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogApi.createLoginLog(reqDTO);
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            updateUserLogin(userId, getClientIP());
        }
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.MEMBER;
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        enterpriseInfoMapper.updateById(new EnterpriseInfoDO().setId(id)
                .setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public EnterpriseInfoDO createRegisterUser(AppEnterpriseInfoSaveReqVO reqVO, String registerIp, Integer terminal) {
        // 1. 校验手机号是否已经存在
        EnterpriseInfoDO existingUser = enterpriseInfoMapper.selectByMobile(reqVO.getMobile());
        if (existingUser != null) {
            // 这里换成你们项目统一的异常抛出方式
            throw new ServiceException(400, "该手机号已被注册，请直接登录");
        }
        // 2. 构建新用户对象
        EnterpriseInfoDO user = new EnterpriseInfoDO();
        user.setMobile(reqVO.getMobile());
        user.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 默认开启
        user.setRegisterIp(registerIp);
        user.setRegisterTerminal(Long.valueOf(terminal));

        // 3. 密码加密 (复用你代码里现有的 encodePassword 方法)
        user.setPassword(encodePassword(reqVO.getPassword()));

        // 4. 赋值企业认证新增的各个字段
        user.setNickname(reqVO.getNickname());
        user.setName(reqVO.getUsername());
        user.setSex(reqVO.getSex());
        user.setLicenceName(reqVO.getLicenceName());
        user.setProvince(reqVO.getProvince());
        user.setCity(reqVO.getCity());
        user.setCounty(reqVO.getCounty());
        user.setAddressDetail(reqVO.getAddressDetail());
        user.setTaxpayerCode(reqVO.getTaxpayerCode());
        user.setBusinessScope(reqVO.getBusinessScope());
        user.setLicenceFile(reqVO.getLicenceFile());
        user.setIdentityFront(reqVO.getIdentityFront());
        user.setIdentityBack(reqVO.getIdentityBack());
        user.setEmail(reqVO.getEmail());

        // 5. 插入数据库
        enterpriseInfoMapper.insert(user);

        return user;
    }


    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public Long createEnterpriseInfo(AppEnterpriseInfoSaveReqVO createReqVO) {
        // 插入
        EnterpriseInfoDO enterpriseInfo = BeanUtils.toBean(createReqVO, EnterpriseInfoDO.class);
        enterpriseInfoMapper.insert(enterpriseInfo);

        // 返回
        return enterpriseInfo.getId();
    }

    @Override
    public void updateEnterpriseInfo(AppEnterpriseInfoSaveReqVO updateReqVO) {
        // 校验存在
//        validateEnterpriseInfoExists(updateReqVO.getId());
        // 更新
        EnterpriseInfoDO updateObj = BeanUtils.toBean(updateReqVO, EnterpriseInfoDO.class);
        enterpriseInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteEnterpriseInfo(Long id) {
        // 校验存在
        validateEnterpriseInfoExists(id);
        // 删除
        enterpriseInfoMapper.deleteById(id);
    }

    @Override
        public void deleteEnterpriseInfoListByIds(List<Long> ids) {
        // 删除
        enterpriseInfoMapper.deleteByIds(ids);
        }


    private void validateEnterpriseInfoExists(Long id) {
        if (enterpriseInfoMapper.selectById(id) == null) {
            throw exception(ENTERPRISE_INFO_NOT_EXISTS);
        }
    }

    @Override
    public EnterpriseInfoDO getEnterpriseInfo(Long id) {
        Long targetId;

        if (id != null) {
            // 如果传了 id，就用指定的 id
            targetId = id;
        } else {
            // 如果没传，去安全上下文中捞取当前登录用户的 ID
            targetId = getLoginUserId();
        }

        if (targetId == null) {
            throw exception(ENTERPRISE_NOT_EXISTS);
        }

        // 最终执行数据库查询
        return enterpriseInfoMapper.selectById(targetId);
    }

    @Override
    public PageResult<EnterpriseInfoDO> getEnterpriseInfoPage(AppEnterpriseInfoPageReqVO pageReqVO) {
        return enterpriseInfoMapper.selectPage(pageReqVO);
    }

    @Override
    public void logout(String token) {
        // 删除访问令牌
        OAuth2AccessTokenRespDTO accessTokenRespDTO = oauth2TokenApi.removeAccessToken(token);
        if (accessTokenRespDTO == null) {
            return;
        }
        // 删除成功，则记录登出日志
        createLogoutLog(accessTokenRespDTO.getUserId());
    }

    @Override
    public AppAuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenRespDTO accessTokenDO = oauth2TokenApi.refreshAccessToken(refreshToken,
                OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return AuthConvert.INSTANCE.convert(accessTokenDO, null);
    }

    private void createLogoutLog(Long userId) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(LoginLogTypeEnum.LOGOUT_SELF.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(getMobile(userId));
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
        loginLogApi.createLoginLog(reqDTO);
    }

    private String getMobile(Long userId) {
        if (userId == null) {
            return null;
        }
        EnterpriseInfoDO user = enterpriseInfoMapper.selectById(userId);
        return user != null ? user.getMobile() : null;
    }

}
