package cn.kyc.dandanxia.module.member.service.position;

import cn.kyc.dandanxia.module.member.utils.mq.producer.PositionProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.kyc.dandanxia.module.member.controller.app.position.vo.*;
import cn.kyc.dandanxia.module.member.dal.dataobject.position.PositionDO;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.common.util.object.BeanUtils;

import cn.kyc.dandanxia.module.member.dal.mysql.position.PositionMapper;

import static cn.kyc.dandanxia.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kyc.dandanxia.framework.common.util.collection.CollectionUtils.convertList;
import static cn.kyc.dandanxia.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.kyc.dandanxia.module.member.enums.ErrorCodeConstants.*;

/**
 * 企业岗位招聘档案 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j // 2. 🌟 重点：在这里加上这个注解，'log' 符号就不会再报错了！
public class PositionServiceImpl implements PositionService {

    @Resource
    private PositionMapper positionMapper;

    @Resource
    private PositionProducer positionProducer;


    @Override
    public Long createPosition(AppPositionSaveReqVO createReqVO) {

        Long loginUserId = getLoginUserId();

        // 插入
        PositionDO position = BeanUtils.toBean(createReqVO, PositionDO.class);
        position.setParseStatus(1);
        position.setEnterpriseId(loginUserId);
        positionMapper.insert(position);


        try {
            positionProducer.sendPositionParseMessage(position.getId(), position.getFilePath(), loginUserId);
            log.info("【芋道规范】成功将岗位招聘消息压入 RabbitMQ 队列，等待 Python 消费。岗位ID: {}", position.getId());
        } catch (Exception e) {
            log.error("【MQ投递崩溃】岗位ID: {} 压入 RabbitMQ 队列失败，触发安全熔断机制！", position.getId(), e);

            // 🚨 触发安全降级：立刻同步将达梦数据库中的状态修改为 3 (解析失败)，防止前端卡死在初始化 Loading
            positionMapper.updateById(new PositionDO().setId(position.getId()).setParseStatus(3));

            // 抛出模块自定义的 B 端消息投递失败异常
            throw exception(POSITION_SEND_MQ_ERROR);
        }

        // 返回
        return position.getId();
    }

    @Override
    public void updatePosition(AppPositionSaveReqVO updateReqVO) {
        // 校验存在
        validatePositionExists(updateReqVO.getId());
        // 更新
        PositionDO updateObj = BeanUtils.toBean(updateReqVO, PositionDO.class);
        positionMapper.updateById(updateObj);
    }

    @Override
    public void deletePosition(Long id) {
        // 校验存在
        validatePositionExists(id);
        // 删除
        positionMapper.deleteById(id);
    }

    @Override
        public void deletePositionListByIds(List<Long> ids) {
        // 删除
        positionMapper.deleteByIds(ids);
        }


    private void validatePositionExists(Long id) {
        if (positionMapper.selectById(id) == null) {
            throw exception(POSITION_NOT_EXISTS);
        }
    }

    @Override
    public PositionDO getPosition(Long id) {
        return positionMapper.selectById(id);
    }

    @Override
    public PageResult<PositionDO> getPositionPage(AppPositionPageReqVO pageReqVO) {
        return positionMapper.selectPage(pageReqVO);
    }

}
