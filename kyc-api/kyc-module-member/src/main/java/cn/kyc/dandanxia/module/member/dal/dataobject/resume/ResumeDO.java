package cn.kyc.dandanxia.module.member.dal.dataobject.resume;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.kyc.dandanxia.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户简历档案 DO
 *
 * @author 芋道源码
 */
@TableName(value = "kyc_resume", autoResultMap = true)
@KeySequence("kyc_resume_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDO extends BaseDO {

    /**
     * 简历档案主键ID
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 简历文件名
     */
    private String fileName;
    /**
     * 文件存储路径/OSS URL
     */
    private String filePath;
    /**
     * 文件类型后缀(pdf/docx)
     */
    private String fileType;
    /**
     * 文件大小(Byte)
     */
    private Long fileSize;
    /**
     * 是否为默认/激活简历：0-否，1-是
     */
    private Integer isActive;
    /**
     * 文档解析后的JSON文本内容
     */
    private String parsedJson;
    /**
     * 解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败
     */
    private Integer parseStatus;

    /**
     * 技能点
     * 🌟 重点：前端传过来的是 ["Vue3", "Spring Boot"]，
     * MyBatis-Plus 会自动通过 Jackson 将其转为 JSON 字符串或逗号格式存入达梦 VARCHAR 中
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> skills;

    /**
     * 简历重点核心
     */
    private String coreHighlight;


}
