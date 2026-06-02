package cn.kyc.dandanxia.module.member.service.enterpriseinfo;

import java.util.*;
import javax.validation.*;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.common.pojo.PageParam;
import cn.kyc.dandanxia.module.member.controller.app.auth.vo.AppEnterpriseInfoPageReqVO;
import cn.kyc.dandanxia.module.member.controller.app.auth.vo.AppEnterpriseInfoSaveReqVO;
import cn.kyc.dandanxia.module.member.dal.dataobject.enterpriseinfo.EnterpriseInfoDO;

/**
 * 企业信息 Service 接口
 *
 * @author 芋道源码
 */
public interface EnterpriseInfoService {

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

}
