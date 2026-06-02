package cn.kyc.dandanxia.module.mes.dal.mysql.pro.workrecord;

import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.mybatis.core.mapper.BaseMapperX;
import cn.kyc.dandanxia.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kyc.dandanxia.module.mes.controller.admin.pro.workrecord.vo.MesProWorkRecordLogPageReqVO;
import cn.kyc.dandanxia.module.mes.dal.dataobject.pro.workrecord.MesProWorkRecordLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 上下工记录流水 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesProWorkRecordLogMapper extends BaseMapperX<MesProWorkRecordLogDO> {

    default PageResult<MesProWorkRecordLogDO> selectPage(MesProWorkRecordLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesProWorkRecordLogDO>()
                .eqIfPresent(MesProWorkRecordLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MesProWorkRecordLogDO::getWorkstationId, reqVO.getWorkstationId())
                .eqIfPresent(MesProWorkRecordLogDO::getType, reqVO.getType())
                .betweenIfPresent(MesProWorkRecordLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MesProWorkRecordLogDO::getId));
    }

}
