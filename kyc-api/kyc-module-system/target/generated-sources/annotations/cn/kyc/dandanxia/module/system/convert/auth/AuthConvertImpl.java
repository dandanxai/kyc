package cn.kyc.dandanxia.module.system.convert.auth;

import cn.kyc.dandanxia.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import cn.kyc.dandanxia.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.kyc.dandanxia.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.kyc.dandanxia.module.system.controller.admin.auth.vo.AuthSmsLoginReqVO;
import cn.kyc.dandanxia.module.system.controller.admin.auth.vo.AuthSmsSendReqVO;
import cn.kyc.dandanxia.module.system.controller.admin.auth.vo.AuthSocialLoginReqVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T00:41:49+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (Oracle Corporation)"
)
public class AuthConvertImpl implements AuthConvert {

    @Override
    public SocialUserBindReqDTO convert(Long userId, Integer userType, AuthSocialLoginReqVO reqVO) {
        if ( userId == null && userType == null && reqVO == null ) {
            return null;
        }

        SocialUserBindReqDTO socialUserBindReqDTO = new SocialUserBindReqDTO();

        if ( reqVO != null ) {
            socialUserBindReqDTO.setCode( reqVO.getCode() );
            socialUserBindReqDTO.setState( reqVO.getState() );
        }
        socialUserBindReqDTO.setUserId( userId );
        socialUserBindReqDTO.setUserType( userType );

        return socialUserBindReqDTO;
    }

    @Override
    public SmsCodeSendReqDTO convert(AuthSmsSendReqVO reqVO) {
        if ( reqVO == null ) {
            return null;
        }

        SmsCodeSendReqDTO smsCodeSendReqDTO = new SmsCodeSendReqDTO();

        smsCodeSendReqDTO.setMobile( reqVO.getMobile() );
        smsCodeSendReqDTO.setScene( reqVO.getScene() );

        return smsCodeSendReqDTO;
    }

    @Override
    public SmsCodeUseReqDTO convert(AuthSmsLoginReqVO reqVO, Integer scene, String usedIp) {
        if ( reqVO == null && scene == null && usedIp == null ) {
            return null;
        }

        SmsCodeUseReqDTO smsCodeUseReqDTO = new SmsCodeUseReqDTO();

        if ( reqVO != null ) {
            smsCodeUseReqDTO.setMobile( reqVO.getMobile() );
            smsCodeUseReqDTO.setCode( reqVO.getCode() );
        }
        smsCodeUseReqDTO.setScene( scene );
        smsCodeUseReqDTO.setUsedIp( usedIp );

        return smsCodeUseReqDTO;
    }
}
