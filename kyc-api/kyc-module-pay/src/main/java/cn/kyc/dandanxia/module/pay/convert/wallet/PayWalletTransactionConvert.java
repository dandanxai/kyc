package cn.kyc.dandanxia.module.pay.convert.wallet;

import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.module.pay.controller.admin.wallet.vo.transaction.PayWalletTransactionRespVO;
import cn.kyc.dandanxia.module.pay.controller.app.wallet.vo.transaction.AppPayWalletTransactionRespVO;
import cn.kyc.dandanxia.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;
import cn.kyc.dandanxia.module.pay.service.wallet.bo.WalletTransactionCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayWalletTransactionConvert {

    PayWalletTransactionConvert INSTANCE = Mappers.getMapper(PayWalletTransactionConvert.class);

    PageResult<PayWalletTransactionRespVO> convertPage2(PageResult<PayWalletTransactionDO> page);

    PayWalletTransactionDO convert(WalletTransactionCreateReqBO bean);

}
