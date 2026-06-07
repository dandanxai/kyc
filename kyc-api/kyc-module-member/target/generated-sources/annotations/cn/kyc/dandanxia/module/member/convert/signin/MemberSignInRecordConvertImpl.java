package cn.kyc.dandanxia.module.member.convert.signin;

import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.module.member.controller.admin.signin.vo.record.MemberSignInRecordRespVO;
import cn.kyc.dandanxia.module.member.controller.app.signin.vo.record.AppMemberSignInRecordRespVO;
import cn.kyc.dandanxia.module.member.dal.dataobject.signin.MemberSignInRecordDO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T19:31:56+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (Oracle Corporation)"
)
public class MemberSignInRecordConvertImpl implements MemberSignInRecordConvert {

    @Override
    public PageResult<MemberSignInRecordRespVO> convertPage(PageResult<MemberSignInRecordDO> pageResult) {
        if ( pageResult == null ) {
            return null;
        }

        PageResult<MemberSignInRecordRespVO> pageResult1 = new PageResult<MemberSignInRecordRespVO>();

        pageResult1.setTotal( pageResult.getTotal() );
        pageResult1.setList( memberSignInRecordDOListToMemberSignInRecordRespVOList( pageResult.getList() ) );

        return pageResult1;
    }

    @Override
    public PageResult<AppMemberSignInRecordRespVO> convertPage02(PageResult<MemberSignInRecordDO> pageResult) {
        if ( pageResult == null ) {
            return null;
        }

        PageResult<AppMemberSignInRecordRespVO> pageResult1 = new PageResult<AppMemberSignInRecordRespVO>();

        pageResult1.setTotal( pageResult.getTotal() );
        pageResult1.setList( memberSignInRecordDOListToAppMemberSignInRecordRespVOList( pageResult.getList() ) );

        return pageResult1;
    }

    @Override
    public AppMemberSignInRecordRespVO coverRecordToAppRecordVo(MemberSignInRecordDO memberSignInRecordDO) {
        if ( memberSignInRecordDO == null ) {
            return null;
        }

        AppMemberSignInRecordRespVO appMemberSignInRecordRespVO = new AppMemberSignInRecordRespVO();

        appMemberSignInRecordRespVO.setDay( memberSignInRecordDO.getDay() );
        appMemberSignInRecordRespVO.setPoint( memberSignInRecordDO.getPoint() );
        appMemberSignInRecordRespVO.setExperience( memberSignInRecordDO.getExperience() );
        appMemberSignInRecordRespVO.setCreateTime( memberSignInRecordDO.getCreateTime() );

        return appMemberSignInRecordRespVO;
    }

    protected MemberSignInRecordRespVO memberSignInRecordDOToMemberSignInRecordRespVO(MemberSignInRecordDO memberSignInRecordDO) {
        if ( memberSignInRecordDO == null ) {
            return null;
        }

        MemberSignInRecordRespVO memberSignInRecordRespVO = new MemberSignInRecordRespVO();

        memberSignInRecordRespVO.setId( memberSignInRecordDO.getId() );
        memberSignInRecordRespVO.setUserId( memberSignInRecordDO.getUserId() );
        memberSignInRecordRespVO.setDay( memberSignInRecordDO.getDay() );
        memberSignInRecordRespVO.setPoint( memberSignInRecordDO.getPoint() );
        memberSignInRecordRespVO.setCreateTime( memberSignInRecordDO.getCreateTime() );

        return memberSignInRecordRespVO;
    }

    protected List<MemberSignInRecordRespVO> memberSignInRecordDOListToMemberSignInRecordRespVOList(List<MemberSignInRecordDO> list) {
        if ( list == null ) {
            return null;
        }

        List<MemberSignInRecordRespVO> list1 = new ArrayList<MemberSignInRecordRespVO>( list.size() );
        for ( MemberSignInRecordDO memberSignInRecordDO : list ) {
            list1.add( memberSignInRecordDOToMemberSignInRecordRespVO( memberSignInRecordDO ) );
        }

        return list1;
    }

    protected List<AppMemberSignInRecordRespVO> memberSignInRecordDOListToAppMemberSignInRecordRespVOList(List<MemberSignInRecordDO> list) {
        if ( list == null ) {
            return null;
        }

        List<AppMemberSignInRecordRespVO> list1 = new ArrayList<AppMemberSignInRecordRespVO>( list.size() );
        for ( MemberSignInRecordDO memberSignInRecordDO : list ) {
            list1.add( coverRecordToAppRecordVo( memberSignInRecordDO ) );
        }

        return list1;
    }
}
