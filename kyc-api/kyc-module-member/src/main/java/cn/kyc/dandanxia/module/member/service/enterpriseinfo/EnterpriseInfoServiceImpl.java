package cn.kyc.dandanxia.module.member.service.enterpriseinfo;

import cn.hutool.core.collection.CollUtil;
import cn.kyc.dandanxia.module.member.controller.app.auth.vo.AppEnterpriseInfoSaveReqVO;
import cn.kyc.dandanxia.module.member.controller.app.auth.vo.AppEnterpriseInfoPageReqVO;
import cn.kyc.dandanxia.module.member.dal.dataobject.enterpriseinfo.EnterpriseInfoDO;
import cn.kyc.dandanxia.module.member.dal.mysql.enterpriseinfo.EnterpriseInfoMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.common.pojo.PageParam;
import cn.kyc.dandanxia.framework.common.util.object.BeanUtils;


import static cn.kyc.dandanxia.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kyc.dandanxia.framework.common.util.collection.CollectionUtils.convertList;
import static cn.kyc.dandanxia.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kyc.dandanxia.module.member.enums.ErrorCodeConstants.ENTERPRISE_INFO_NOT_EXISTS;

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
        return enterpriseInfoMapper.selectById(id);
    }

    @Override
    public PageResult<EnterpriseInfoDO> getEnterpriseInfoPage(AppEnterpriseInfoPageReqVO pageReqVO) {
        return enterpriseInfoMapper.selectPage(pageReqVO);
    }

}
