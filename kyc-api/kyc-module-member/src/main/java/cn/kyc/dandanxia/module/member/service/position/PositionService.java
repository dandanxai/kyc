package cn.kyc.dandanxia.module.member.service.position;

import java.util.*;
import javax.validation.*;
import cn.kyc.dandanxia.module.member.controller.app.position.vo.*;
import cn.kyc.dandanxia.module.member.dal.dataobject.position.PositionDO;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.common.pojo.PageParam;

/**
 * 企业岗位招聘档案 Service 接口
 *
 * @author 芋道源码
 */
public interface PositionService {

    /**
     * 创建企业岗位招聘档案
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPosition(@Valid AppPositionSaveReqVO createReqVO);

    /**
     * 更新企业岗位招聘档案
     *
     * @param updateReqVO 更新信息
     */
    void updatePosition(@Valid AppPositionSaveReqVO updateReqVO);

    /**
     * 删除企业岗位招聘档案
     *
     * @param id 编号
     */
    void deletePosition(Long id);

    /**
    * 批量删除企业岗位招聘档案
    *
    * @param ids 编号
    */
    void deletePositionListByIds(List<Long> ids);

    /**
     * 获得企业岗位招聘档案
     *
     * @param id 编号
     * @return 企业岗位招聘档案
     */
    PositionDO getPosition(Long id);

    /**
     * 获得企业岗位招聘档案分页
     *
     * @param pageReqVO 分页查询
     * @return 企业岗位招聘档案分页
     */
    PageResult<PositionDO> getPositionPage(AppPositionPageReqVO pageReqVO);

}