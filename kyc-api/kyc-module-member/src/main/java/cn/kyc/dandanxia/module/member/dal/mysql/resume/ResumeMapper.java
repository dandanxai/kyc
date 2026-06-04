package cn.kyc.dandanxia.module.member.dal.mysql.resume;

import java.util.*;

import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kyc.dandanxia.framework.mybatis.core.mapper.BaseMapperX;
import cn.kyc.dandanxia.module.member.dal.dataobject.resume.ResumeDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kyc.dandanxia.module.member.controller.app.resume.vo.*;

/**
 * 用户简历档案 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ResumeMapper extends BaseMapperX<ResumeDO> {

    default PageResult<ResumeDO> selectPage(AppResumePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ResumeDO>()
                .eqIfPresent(ResumeDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ResumeDO::getFileName, reqVO.getFileName())
                .eqIfPresent(ResumeDO::getFilePath, reqVO.getFilePath())
                .eqIfPresent(ResumeDO::getFileType, reqVO.getFileType())
                .eqIfPresent(ResumeDO::getFileSize, reqVO.getFileSize())
                .eqIfPresent(ResumeDO::getIsActive, reqVO.getIsActive())
                .eqIfPresent(ResumeDO::getParsedJson, reqVO.getParsedJson())
                .eqIfPresent(ResumeDO::getParseStatus, reqVO.getParseStatus())
                .betweenIfPresent(ResumeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ResumeDO::getIsActive)
                .orderByDesc(ResumeDO::getId));
    }

}