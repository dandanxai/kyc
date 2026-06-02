package cn.kyc.dandanxia.module.report.dal.mysql.goview;

import cn.kyc.dandanxia.framework.common.pojo.PageParam;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.mybatis.core.mapper.BaseMapperX;
import cn.kyc.dandanxia.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kyc.dandanxia.module.report.dal.dataobject.goview.GoViewProjectDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoViewProjectMapper extends BaseMapperX<GoViewProjectDO> {

    default PageResult<GoViewProjectDO> selectPage(PageParam reqVO, Long userId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GoViewProjectDO>()
                .eq(GoViewProjectDO::getCreator, userId)
                .orderByDesc(GoViewProjectDO::getId));
    }

}
