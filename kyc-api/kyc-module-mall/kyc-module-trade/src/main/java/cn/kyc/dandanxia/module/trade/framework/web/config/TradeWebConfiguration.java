package cn.kyc.dandanxia.module.trade.framework.web.config;

import cn.kyc.dandanxia.framework.swagger.config.KycSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * trade 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class TradeWebConfiguration {

    /**
     * trade 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi tradeGroupedOpenApi() {
        return KycSwaggerAutoConfiguration.buildGroupedOpenApi("trade");
    }

}
