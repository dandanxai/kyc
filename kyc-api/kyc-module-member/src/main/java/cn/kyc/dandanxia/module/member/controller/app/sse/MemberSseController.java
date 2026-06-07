package cn.kyc.dandanxia.module.member.controller.app.sse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Tag(name = "用户 App - 全局智能化异步明细 SSE 推送")
@RestController
@RequestMapping("/member/sse") // 🌟 统一大路由
@Slf4j
public class MemberSseController {

    // 统一管理 B/C 全端长连接通道的大池子
    private static final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * 1. 统一建立长连接通道点 (求职端、企业端均连此接口)
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PermitAll
    @Operation(summary = "全端通用 - 建立长连接通道")
    public SseEmitter connect(@RequestParam("userId") Long userId) {
        // 设置超时时间 10 分钟
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);

        emitterMap.put(userId, emitter);

        emitter.onCompletion(() -> emitterMap.remove(userId));
        emitter.onTimeout(() -> emitterMap.remove(userId));
        emitter.onError((e) -> emitterMap.remove(userId));

        try {
            // 必须发送标准规范的 INIT 握手信号
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .id("0")
                    .comment("connection established")
                    .data("全栈智能数据回传信道已挂载成功"));
        } catch (IOException e) {
            emitterMap.remove(userId);
        }

        // 🌟 核心点：这里直接 return emitter，绝对不能写成 return success(emitter);
        return emitter;
    }

    /**
     * 2. 🌟 核心枢纽：供 Python 内部调用的全自动任务分类分流通知接口
     */
    @PostMapping("/notify-success")
    @PermitAll
    @Operation(summary = "Python内部专用 - 大模型解构成骨分流回传")
    public SseNotificationResult notifySuccess(@RequestBody Map<String, Object> payload) {
        log.info("[GlobalSSE][接收到 Python 原始报喜广播] 载荷参数: {}", payload);

        // 1. 安全校验基本防空机制
        if (payload == null || !payload.containsKey("userId") || !payload.containsKey("dataId") || !payload.containsKey("taskType")) {
            log.error("[GlobalSSE][流转熔断] 载荷缺少必要元素 (userId / dataId / taskType)");
            return new SseNotificationResult(500, "参数残缺，拒绝接收");
        }

        Long userId = Long.valueOf(payload.get("userId").toString());
        String dataId = payload.get("dataId").toString();          // 对应的表自增主键ID
        String taskType = payload.get("taskType").toString();      // 钢印标识符：RESUME 或 POSITION

        // 2. 从共享大池子抓取该用户的活动网络流
        SseEmitter emitter = emitterMap.get(userId);

        if (emitter != null) {
            try {
                if ("RESUME".equals(taskType)) {
                    emitter.send(SseEmitter.event().name("RESUME_PARSE_SUCCESS").data(dataId)); //
                } else if ("POSITION".equals(taskType)) {
                    emitter.send(SseEmitter.event().name("POSITION_PARSE_SUCCESS").data(dataId)); //
                } else if ("RESUME_GRAPH".equals(taskType)) {
                    // 🌟 核心新增：通知前端简历高维规整图谱已经织网完毕，可以立刻刷新页面关闭 loading 轮询！
                    emitter.send(SseEmitter.event().name("RESUME_GRAPH_SUCCESS").data(dataId));
                    log.info("[GlobalSSE] 成功向用户: {} 推送【简历图谱】同步成功信号, ID: {}", userId, dataId);
                } else if ("POSITION_GRAPH".equals(taskType)) {
                    // 🌟 核心预留：通知前端招聘岗位图谱同步成功
                    emitter.send(SseEmitter.event().name("POSITION_GRAPH_SUCCESS").data(dataId));
                    log.info("[GlobalSSE] 成功向用户: {} 推送【岗位图谱】同步成功信号, ID: {}", userId, dataId);
                } else {
                    log.warn("[GlobalSSE][非法任务阻断] 无法识别的 taskType 标志: {}", taskType);
                    return new SseNotificationResult(400, "未知任务标识");
                }

                return new SseNotificationResult(0, "推送大成功");

            } catch (IOException e) {
                log.error("[GlobalSSE][推流瘫痪] 通道异常，强制移除不活跃通道: {}", userId);
                emitterMap.remove(userId);
            }
        }

        log.warn("[GlobalSSE][推流流产] 用户 {} 掉线或不在通道，无法通知", userId);
        return new SseNotificationResult(404, "通道离线或通道已被垃圾回收");
    }

    /**
     * 开放式静态池获取
     */
    public static Map<Long, SseEmitter> getEmitterMap() {
        return emitterMap;
    }

    /**
     * 内部通用结果状态包装类
     */
    public static class SseNotificationResult {
        public int code;
        public String msg;
        public SseNotificationResult(int code, String msg) { this.code = code; this.msg = msg; }
    }
}
