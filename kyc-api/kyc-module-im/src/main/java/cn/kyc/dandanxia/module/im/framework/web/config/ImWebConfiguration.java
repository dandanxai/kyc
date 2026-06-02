package cn.kyc.dandanxia.module.im.framework.web.config;

import cn.kyc.dandanxia.framework.swagger.config.KycSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * im 模块的 web 组件的 Configuration
 */
@Configuration(proxyBeanMethods = false)
public class ImWebConfiguration {

    /**
     * im 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi imGroupedOpenApi() {
        return KycSwaggerAutoConfiguration.buildGroupedOpenApi("im");
    }

}
