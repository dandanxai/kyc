package cn.kyc.dandanxia.module.member.dal.mysql.position;

import java.util.*;

import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kyc.dandanxia.framework.mybatis.core.mapper.BaseMapperX;
import cn.kyc.dandanxia.module.member.dal.dataobject.position.PositionDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kyc.dandanxia.module.member.controller.app.position.vo.*;

/**
 * 企业岗位招聘档案 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface PositionMapper extends BaseMapperX<PositionDO> {

    default PageResult<PositionDO> selectPage(AppPositionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PositionDO>()
                .eqIfPresent(PositionDO::getEnterpriseId, reqVO.getEnterpriseId())
                .eqIfPresent(PositionDO::getParsedJson, reqVO.getParsedJson())
                .eqIfPresent(PositionDO::getParseStatus, reqVO.getParseStatus())
                .eqIfPresent(PositionDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PositionDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(PositionDO::getRequiredSkills, reqVO.getRequiredSkills())
                .eqIfPresent(PositionDO::getKeywords, reqVO.getKeywords())
                .eqIfPresent(PositionDO::getJobFamilyBucket, reqVO.getJobFamilyBucket())
                .eqIfPresent(PositionDO::getIndustryBucket, reqVO.getIndustryBucket())
                .eqIfPresent(PositionDO::getCity, reqVO.getCity())
                .eqIfPresent(PositionDO::getEducationRequired, reqVO.getEducationRequired())
                .eqIfPresent(PositionDO::getYearsOfExperienceMin, reqVO.getYearsOfExperienceMin())
                .eqIfPresent(PositionDO::getSalaryMin, reqVO.getSalaryMin())
                .eqIfPresent(PositionDO::getSalaryMax, reqVO.getSalaryMax())
                .likeIfPresent(PositionDO::getFileName, reqVO.getFileName())
                .eqIfPresent(PositionDO::getFilePath, reqVO.getFilePath())
                .eqIfPresent(PositionDO::getFileType, reqVO.getFileType())
                .eqIfPresent(PositionDO::getFileSize, reqVO.getFileSize())
                .orderByDesc(PositionDO::getId));
    }

}
