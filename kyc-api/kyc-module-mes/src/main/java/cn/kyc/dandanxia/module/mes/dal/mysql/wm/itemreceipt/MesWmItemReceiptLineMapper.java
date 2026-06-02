package cn.kyc.dandanxia.module.mes.dal.mysql.wm.itemreceipt;

import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.mybatis.core.mapper.BaseMapperX;
import cn.kyc.dandanxia.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kyc.dandanxia.module.mes.controller.admin.wm.itemreceipt.vo.line.MesWmItemReceiptLinePageReqVO;
import cn.kyc.dandanxia.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 采购入库单行 Mapper
 */
@Mapper
public interface MesWmItemReceiptLineMapper extends BaseMapperX<MesWmItemReceiptLineDO> {

    default PageResult<MesWmItemReceiptLineDO> selectPage(MesWmItemReceiptLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmItemReceiptLineDO>()
                .eqIfPresent(MesWmItemReceiptLineDO::getReceiptId, reqVO.getReceiptId())
                .inIfPresent(MesWmItemReceiptLineDO::getReceiptId, reqVO.getReceiptIds())
                .orderByDesc(MesWmItemReceiptLineDO::getId));
    }

    default List<MesWmItemReceiptLineDO> selectListByReceiptId(Long receiptId) {
        return selectList(MesWmItemReceiptLineDO::getReceiptId, receiptId);
    }

    default void deleteByReceiptId(Long receiptId) {
        delete(MesWmItemReceiptLineDO::getReceiptId, receiptId);
    }

    default List<MesWmItemReceiptLineDO> selectListByReceiptIds(java.util.Collection<Long> receiptIds) {
        return selectList(MesWmItemReceiptLineDO::getReceiptId, receiptIds);
    }

}
