package cn.kyc.dandanxia.module.member.service.resume;

import java.util.*;
import javax.validation.*;
import cn.kyc.dandanxia.module.member.controller.app.resume.vo.*;
import cn.kyc.dandanxia.module.member.dal.dataobject.resume.ResumeDO;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.common.pojo.PageParam;

/**
 * 用户简历档案 Service 接口
 *
 * @author 芋道源码
 */
public interface ResumeService {

    /**
     * 创建用户简历档案
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createResume(@Valid AppResumeSaveReqVO createReqVO);

    /**
     * 更新用户简历档案
     *
     * @param updateReqVO 更新信息
     */
    void updateResume(@Valid AppResumeSaveReqVO updateReqVO);

    /**
     * 删除用户简历档案
     *
     * @param id 编号
     */
    void deleteResume(Long id);

    /**
    * 批量删除用户简历档案
    *
    * @param ids 编号
    */
    void deleteResumeListByIds(List<Long> ids);

    /**
     * 获得用户简历档案
     *
     * @param id 编号
     * @return 用户简历档案
     */
    ResumeDO getResume(Long id);

    /**
     * 获得用户简历档案分页
     *
     * @param pageReqVO 分页查询
     * @return 用户简历档案分页
     */
    PageResult<ResumeDO> getResumePage(AppResumePageReqVO pageReqVO);

    void updateResumeActive(@Valid AppResumeUpdateActiveReqVO updateActiveReqVO);
}
