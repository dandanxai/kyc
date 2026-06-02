package cn.kyc.dandanxia.module.crm.framework.web.config;

import cn.kyc.dandanxia.framework.swagger.config.KycSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * crm 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class CrmWebConfiguration {

    /**
     * crm 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi crmGroupedOpenApi() {
        return KycSwaggerAutoConfiguration.buildGroupedOpenApi("crm");
    }

}
