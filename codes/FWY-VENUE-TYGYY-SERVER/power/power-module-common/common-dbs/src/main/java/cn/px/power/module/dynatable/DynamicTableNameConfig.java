package cn.px.power.module.dynatable;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import cn.px.power.module.config.MybatisPlusConfig;
import cn.px.power.module.dynatable.handler.DynamicTableNameHandler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态表名配置
 * @author 品讯科技
 * @date 2024-08
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({MybatisPlusConfig.class})
public class DynamicTableNameConfig {

    @Bean
    @ConditionalOnMissingBean({TableNameHandler.class})
    public TableNameHandler tableNameHandler() {
        return new DynamicTableNameHandler();
    }

    /**
     * 配置动态表名拦截器
     **/
    @Bean
    @ConditionalOnProperty(value = {"jpower.mybatis.dynamicTableName"}, matchIfMissing = false)
    @ConditionalOnBean(TableNameHandler.class)
    @ConditionalOnMissingBean({DynamicTableNameInnerInterceptor.class})
    public DynamicTableNameInnerInterceptor dataScopeQueryInterceptor(TableNameHandler tableNameHandler) {
        return new DynamicTableNameInnerInterceptor(tableNameHandler);
    }
}
