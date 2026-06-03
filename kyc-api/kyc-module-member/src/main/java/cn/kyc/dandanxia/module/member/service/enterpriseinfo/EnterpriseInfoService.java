package cn.kyc.dandanxia.module.member.service.enterpriseinfo;

import java.util.*;
import javax.validation.*;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.common.pojo.PageParam;
import cn.kyc.dandanxia.module.member.controller.app.auth.vo.*;
import cn.kyc.dandanxia.module.member.dal.dataobject.enterpriseinfo.EnterpriseInfoDO;

/**
 * 企业信息 Service 接口
 *
 * @author 芋道源码
 */
public interface EnterpriseInfoService {



    /**
     * 企业认证注册
     */
    AppAuthLoginRespVO register(@Valid AppEnterpriseInfoSaveReqVO reqVO);

    /**
     * 手机 + 密码登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AppAuthLoginRespVO login(@Valid AppAuthLoginReqVO reqVO);

    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 更新用户的最后登陆信息
     *
     * @param id      用户编号
     * @param loginIp 登陆 IP
     */
    void updateUserLogin(Long id, String loginIp);


    EnterpriseInfoDO createRegisterUser(AppEnterpriseInfoSaveReqVO reqVO, String registerIp, Integer terminal);

    /**
     * 创建企业信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEnterpriseInfo(@Valid AppEnterpriseInfoSaveReqVO createReqVO);

    /**
     * 更新企业信息
     *
     * @param updateReqVO 更新信息
     */
    void updateEnterpriseInfo(@Valid AppEnterpriseInfoSaveReqVO updateReqVO);

    /**
     * 删除企业信息
     *
     * @param id 编号
     */
    void deleteEnterpriseInfo(Long id);

    /**
    * 批量删除企业信息
    *
    * @param ids 编号
    */
    void deleteEnterpriseInfoListByIds(List<Long> ids);

    /**
     * 获得企业信息
     *
     * @param id 编号
     * @return 企业信息
     */
    EnterpriseInfoDO getEnterpriseInfo(Long id);

    /**
     * 获得企业信息分页
     *
     * @param pageReqVO 分页查询
     * @return 企业信息分页
     */
    PageResult<EnterpriseInfoDO> getEnterpriseInfoPage(AppEnterpriseInfoPageReqVO pageReqVO);

    /**
     * 基于 token 退出登录
     *
     * @param token token
     */
    void logout(String token);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AppAuthLoginRespVO refreshToken(String refreshToken);
}
