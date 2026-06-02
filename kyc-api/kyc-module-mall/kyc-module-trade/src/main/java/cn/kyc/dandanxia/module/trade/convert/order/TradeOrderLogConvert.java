package cn.kyc.dandanxia.module.trade.convert.order;

import cn.kyc.dandanxia.module.trade.dal.dataobject.order.TradeOrderLogDO;
import cn.kyc.dandanxia.module.trade.service.order.bo.TradeOrderLogCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TradeOrderLogConvert {

    TradeOrderLogConvert INSTANCE = Mappers.getMapper(TradeOrderLogConvert.class);

    TradeOrderLogDO convert(TradeOrderLogCreateReqBO bean);

}
