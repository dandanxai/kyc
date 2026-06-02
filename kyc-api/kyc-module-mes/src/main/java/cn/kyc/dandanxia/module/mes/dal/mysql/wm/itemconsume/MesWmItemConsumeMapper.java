package cn.kyc.dandanxia.module.mes.dal.mysql.wm.itemconsume;

import cn.kyc.dandanxia.framework.mybatis.core.mapper.BaseMapperX;
import cn.kyc.dandanxia.module.mes.dal.dataobject.wm.itemconsume.MesWmItemConsumeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 物料消耗记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesWmItemConsumeMapper extends BaseMapperX<MesWmItemConsumeDO> {

    default MesWmItemConsumeDO selectByFeedbackId(Long feedbackId) {
        return selectOne(MesWmItemConsumeDO::getFeedbackId, feedbackId);
    }

}
