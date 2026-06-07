package cn.kyc.dandanxia.module.member.service.resume;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.kyc.dandanxia.framework.common.exception.util.ServiceExceptionUtil;
import cn.kyc.dandanxia.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kyc.dandanxia.module.member.utils.mq.producer.ResumeGraphProducer;
import cn.kyc.dandanxia.module.member.utils.mq.producer.ResumeProducer;
import cn.kyc.dandanxia.module.member.utils.neo4j.Neo4jGraphUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.StrUtil;
import cn.kyc.dandanxia.framework.common.util.object.BeanUtils;

import java.util.*;
import cn.kyc.dandanxia.module.member.controller.app.resume.vo.*;
import cn.kyc.dandanxia.module.member.dal.dataobject.resume.ResumeDO;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;

import cn.kyc.dandanxia.module.member.dal.mysql.resume.ResumeMapper;

import static cn.kyc.dandanxia.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kyc.dandanxia.framework.common.util.collection.CollectionUtils.convertList;
import static cn.kyc.dandanxia.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.kyc.dandanxia.module.member.enums.ErrorCodeConstants.*;

/**
 * 用户简历档案 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j // 2. 🌟 重点：在这里加上这个注解，'log' 符号就不会再报错了！
public class ResumeServiceImpl implements ResumeService {

    @Resource
    private ResumeMapper resumeMapper;

    @Resource
    private ResumeProducer resumeProducer;

    @Resource
    private ResumeGraphProducer resumeGraphProducer;

    @Resource
    private Neo4jClient neo4jClient;

    @Override
    public Long createResume(AppResumeSaveReqVO createReqVO) {

        Long loginUserId = getLoginUserId();
        // 插入
        ResumeDO resume = BeanUtils.toBean(createReqVO, ResumeDO.class);
        resume.setUserId(loginUserId);
        resume.setParseStatus(1); // 初始化状态为：1-解析中
        resumeMapper.insert(resume);

        try {
            resumeProducer.sendResumeParseMessage(resume.getId(), resume.getFilePath(), loginUserId);
            log.info("【芋道规范】成功将简历消息压入 RabbitMQ 队列，等待 Python 消费。简历ID: {}", resume.getId());
        } catch (Exception e) {
            resumeMapper.updateById(new ResumeDO().setId(resume.getId()).setParseStatus(3));

            throw exception(RESUME_SENG_MQ_ERROR);
        }

        return resume.getId();
    }

    @Override
    public void updateResume(AppResumeSaveReqVO updateReqVO) {
        // 校验存在
        validateResumeExists(updateReqVO.getId());
        // 更新
        ResumeDO updateObj = BeanUtils.toBean(updateReqVO, ResumeDO.class);
        resumeMapper.updateById(updateObj);
    }

    @Override
    public void deleteResume(Long id) {
        // 校验存在
        validateResumeExists(id);
        // 删除
        resumeMapper.deleteById(id);
    }

    @Override
        public void deleteResumeListByIds(List<Long> ids) {
        // 删除
        resumeMapper.deleteByIds(ids);
        }


    private void validateResumeExists(Long id) {
        if (resumeMapper.selectById(id) == null) {
            throw exception(RESUME_NOT_EXISTS);
        }
    }

    @Override
    public ResumeDO getResume(Long id) {
        // 1. 从安全上下文中获取当前真正登录的用户 ID
        Long loginUserId = getLoginUserId();

        // 2. 构造查询条件：必须满足简历 id 等于前端传参，且所属 userId 必须是当前登录人
        return resumeMapper.selectOne(new LambdaQueryWrapperX<ResumeDO>()
                .eq(ResumeDO::getId, id)
                .eq(ResumeDO::getUserId, loginUserId) // 🔒 数据权限死锁
        );
    }

    @Override
    public PageResult<ResumeDO> getResumePage(AppResumePageReqVO pageReqVO) {
        Long loginUserId = getLoginUserId();
        pageReqVO.setUserId(loginUserId);
        return resumeMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 涉及多条更新，必须开启事务
    public void updateResumeActive(AppResumeUpdateActiveReqVO updateActiveReqVO) {
        // 🌟 核心安全机制：从安全上下文中获取当前真正合法的登录用户ID
        Long loginUserId = getLoginUserId();

        // 1. 校验并获取当前要操作的简历
        ResumeDO currentResume = resumeMapper.selectById(updateActiveReqVO.getId());
        if (currentResume == null) {
            throw exception(RESUME_NOT_EXISTS); // 简历不存在
        }

        // 🌟 核心防越权安全校验：
        // 检查这份简历的所属人(userId) 是不是 当前登录的用户(loginUserId)。如果不是，直接抛出异常拒绝操作！
        if (!Objects.equals(currentResume.getUserId(), loginUserId)) {
            throw exception(RESUME_NOT_EXISTS); // 或者抛出更精准的“无权操作该简历”的权限错误码
        }

        // 2. 核心逻辑判断（此时已经 100% 确认是操作者本人的数据）
        if (Objects.equals(currentResume.getIsActive(), 1)) {
            // 💡 原本就是默认简历 -> 取消默认 -> 仅将当前简历改成 0 结束
            ResumeDO cancelObj = new ResumeDO();
            cancelObj.setId(currentResume.getId());
            cancelObj.setIsActive(0);
            resumeMapper.updateById(cancelObj);
        } else {
            // 💡 原本不是默认简历 -> 启用默认

            // 2.1 排他性更新：【必须带上 loginUserId 限制条件】！
            // 只允许把【当前登录用户自己】的老默认简历踢成 0，绝对不影响任何其他用户
            resumeMapper.update(
                    new ResumeDO().setIsActive(0),
                    new LambdaQueryWrapperX<ResumeDO>()
                            .eq(ResumeDO::getUserId, loginUserId) // 🔒 严格锁定当前登录用户
                            .eq(ResumeDO::getIsActive, 1)
            );

            // 2.2 把当前这份简历改成 1
            ResumeDO activeObj = new ResumeDO();
            activeObj.setId(currentResume.getId());
            activeObj.setIsActive(1);
            resumeMapper.updateById(activeObj);
        }
    }

    @Override
    public AppResumeDetailRespVO getResumeDetail(Long id) {
        // 1. 安全数据权限审计
        Long loginUserId = getLoginUserId();
        ResumeDO resume = resumeMapper.selectOne(new LambdaQueryWrapperX<ResumeDO>()
                .eq(ResumeDO::getId, id)
                .eq(ResumeDO::getUserId, loginUserId)
        );

        if (resume == null) {
            return null;
        }

        // 2. 浅拷贝已有独立基础字段 (name, phone, skillTags, achievements 这一波直接同步)
        AppResumeDetailRespVO respVO = BeanUtils.toBean(resume, AppResumeDetailRespVO.class);

        // 3. 🚀 强力破局：深度熔炼大文本，补全所有前端渲染所需的黄金碎片
        if (resume.getParseStatus() == 2 && StrUtil.isNotEmpty(resume.getParsedJson())) {
            try {
                JSONObject jsonObject = JSONUtil.parseObj(resume.getParsedJson());

                // 🌟 【绝密补全】补充外层 VO 缺失的数据提取映射
                respVO.setAge(jsonObject.getInt("age"));                         // 补上你的 21 岁！
                respVO.setGender(jsonObject.getStr("gender"));                   // 补上性别 “男”
                respVO.setCurrentCity(jsonObject.getStr("current_city"));         // 当前城市

                // 补全其它可能属于 json 的非独立列字段
                respVO.setSelfEvaluation(jsonObject.getStr("self_evaluation"));
                respVO.setExpectedPosition(jsonObject.getStr("expected_position"));
                respVO.setSchool(jsonObject.getStr("school"));
                respVO.setMajor(jsonObject.getStr("major"));
                respVO.setAvailability(jsonObject.getStr("availability"));

                // 秒变基础字符串数组 List<String>
                respVO.setCoreSkills(jsonObject.getBeanList("core_skills", String.class));
                respVO.setCertifications(jsonObject.getBeanList("certifications", String.class));
                respVO.setAwards(jsonObject.getBeanList("awards", String.class));

                // 优雅转换复杂的履历对象数组
                respVO.setWorkExperiences(jsonObject.getBeanList("work_experience", AppResumeDetailRespVO.WorkExperienceVO.class));
                respVO.setProjectExperiences(jsonObject.getBeanList("project_experience", AppResumeDetailRespVO.ProjectExperienceVO.class));

            } catch (Exception e) {
                throw exception(RESUME_PARSE_ERROR);
//                log.error("⚠️ [ServiceImpl] 简历 id: {} 的 parsedJson 深度补全解构失败，原因: {}", id, e.getMessage());
            }
        }

        return respVO;
    }

    @Override
    public AppResumeGraphRespVO getResumeGraph(Long id) {
        // 1. 🌟 锁死独立简历端 RESUME_ 前缀防混淆钢印（像素级对齐 Python 新版主键隔离规约）
        String resumeGraphId = "RESUME_" + id;

        // 2. 🌟 满血升级版 Cypher 织网图捞取命令：
        // A. 先从独立的简历原点 (r:Resume) 出发，通过 HAS_TRACK 关系把【前端技术、后端技术、实战项目、重磅荣誉】4大树干捞出来。
        // B. 再利用 OPTIONAL MATCH 顺着 4 大树干把所有的 Skill、Project、Award 细节叶子节点抓上来。
        // C. 顺便利用 [r3:USING_TECH] 强行穿透【项目 ➔ 技术】的横向跨维度科技连线！
        String cypher = "MATCH (u:Resume {id: $resumeGraphId})-[r:HAS_TRACK]->(target:Category) " +
                "OPTIONAL MATCH (target)-[r2:CONTAINS_SKILL|CONTAINS_PROJ|CONTAINS_AWARD]->(subTarget) " +
                "OPTIONAL MATCH (subTarget)-[r3:USING_TECH]->(thirdTarget) " +
                "RETURN u, r, target, r2, subTarget, r3, thirdTarget";

        // 3. 轰炸图库抓取快照记录
        Collection<Map<String, Object>> rawResults = neo4jClient.query(cypher)
                .bind(resumeGraphId).to("resumeGraphId")
                .fetch()
                .all();

        // =================================================================
        // 🌟 核心突破：如果没查到，说明 Neo4j 里是一片虚无，火速丢给 MQ 异步编织规矩图
        // =================================================================
        if (rawResults == null || rawResults.isEmpty()) {
            log.warn("📡 [图谱引擎感知] 检测到 Neo4j 中暂无简历 ID: ({}) 的「双轨规矩网络」，启动异步合围计划...", id);

            // A. 从达梦数据库查出原本大模型解析好的全量 JSON 字段
            ResumeDO resumeDO = resumeMapper.selectById(id);

            if (resumeDO != null && resumeDO.getParsedJson() != null) {
                String parsedJsonStr = resumeDO.getParsedJson();

                // B. 动用生产者，一枪把弹药包射进 MQ 独立图谱同步队列！
                resumeGraphProducer.sendGraphSyncMessage(id, parsedJsonStr);
                log.info("🎉 [图谱引擎感知] 简历 ID: ({}) 已安全递交至 MQ 延迟编织队列，Python 消费者已接单...", id);
            } else {
                log.error("❌ [图谱引擎感知] 达梦数据库中竟然找不到该简历的 AI 解析结果！");
            }

            // 关键改动：没数据丢回 null，明确命令前端保持 Loading 动画开始大轮询
            return null;
        }

        // 4. 🌟 直接调用工具类，瞬间完成 ECharts 拓扑图清洗并直出！
        // 💡 提示：这里传入的中心 ID 必须是更新后的 resumeGraphId 啦
        return Neo4jGraphUtils.buildEchartsGraph(rawResults, resumeGraphId);
    }

}
