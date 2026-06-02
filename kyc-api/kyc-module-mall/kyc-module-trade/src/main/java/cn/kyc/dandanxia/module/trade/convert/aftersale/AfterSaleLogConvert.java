package cn.kyc.dandanxia.module.trade.convert.aftersale;

import cn.kyc.dandanxia.module.trade.dal.dataobject.aftersale.AfterSaleLogDO;
import cn.kyc.dandanxia.module.trade.service.aftersale.bo.AfterSaleLogCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AfterSaleLogConvert {

    AfterSaleLogConvert INSTANCE = Mappers.getMapper(AfterSaleLogConvert.class);

    AfterSaleLogDO convert(AfterSaleLogCreateReqBO bean);

}
