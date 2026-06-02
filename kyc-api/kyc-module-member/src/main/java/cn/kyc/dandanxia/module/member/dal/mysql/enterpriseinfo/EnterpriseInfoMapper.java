package cn.kyc.dandanxia.module.member.dal.mysql.enterpriseinfo;

import java.util.*;

import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kyc.dandanxia.framework.mybatis.core.mapper.BaseMapperX;
import cn.kyc.dandanxia.module.member.controller.app.auth.vo.AppEnterpriseInfoPageReqVO;
import cn.kyc.dandanxia.module.member.dal.dataobject.enterpriseinfo.EnterpriseInfoDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EnterpriseInfoMapper extends BaseMapperX<EnterpriseInfoDO> {

    default PageResult<EnterpriseInfoDO> selectPage(AppEnterpriseInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EnterpriseInfoDO>()
                .likeIfPresent(EnterpriseInfoDO::getNickname, reqVO.getNickname())
                .likeIfPresent(EnterpriseInfoDO::getName, reqVO.getName())
                .eqIfPresent(EnterpriseInfoDO::getSex, reqVO.getSex())
                .eqIfPresent(EnterpriseInfoDO::getBirthday, reqVO.getBirthday())
                .eqIfPresent(EnterpriseInfoDO::getAreaId, reqVO.getAreaId())
                .eqIfPresent(EnterpriseInfoDO::getMark, reqVO.getMark())
                .eqIfPresent(EnterpriseInfoDO::getAvatar, reqVO.getAvatar())
                .eqIfPresent(EnterpriseInfoDO::getStatus, reqVO.getStatus())
                .eqIfPresent(EnterpriseInfoDO::getMobile, reqVO.getMobile())
                .eqIfPresent(EnterpriseInfoDO::getEmail, reqVO.getEmail())
                .eqIfPresent(EnterpriseInfoDO::getPassword, reqVO.getPassword())
                .eqIfPresent(EnterpriseInfoDO::getRegisterIp, reqVO.getRegisterIp())
                .eqIfPresent(EnterpriseInfoDO::getLoginIp, reqVO.getLoginIp())
                .betweenIfPresent(EnterpriseInfoDO::getLoginDate, reqVO.getLoginDate())
                .betweenIfPresent(EnterpriseInfoDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(EnterpriseInfoDO::getRegisterTerminal, reqVO.getRegisterTerminal())
                .likeIfPresent(EnterpriseInfoDO::getLicenceName, reqVO.getLicenceName())
                .eqIfPresent(EnterpriseInfoDO::getProvince, reqVO.getProvince())
                .eqIfPresent(EnterpriseInfoDO::getCity, reqVO.getCity())
                .eqIfPresent(EnterpriseInfoDO::getCounty, reqVO.getCounty())
                .eqIfPresent(EnterpriseInfoDO::getAddressDetail, reqVO.getAddressDetail())
                .eqIfPresent(EnterpriseInfoDO::getTaxpayerCode, reqVO.getTaxpayerCode())
                .eqIfPresent(EnterpriseInfoDO::getBusinessScope, reqVO.getBusinessScope())
                .eqIfPresent(EnterpriseInfoDO::getLicenceFile, reqVO.getLicenceFile())
                .eqIfPresent(EnterpriseInfoDO::getIdentityFront, reqVO.getIdentityFront())
                .eqIfPresent(EnterpriseInfoDO::getIdentityBack, reqVO.getIdentityBack())
                .orderByDesc(EnterpriseInfoDO::getId));
    }

}