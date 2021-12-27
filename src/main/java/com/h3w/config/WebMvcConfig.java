package com.h3w.config;

import com.h3w.filter.XssAndSqlFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * Web配置
 *
 * @author hyyds
 * @date 2021/6/16
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("*")
                .allowedHeaders("*");///** 表示本应用的所有方法都会去处理跨域请求，allowedMethods 表示允许通过的请求数，allowedHeaders 则表示允许的请求头。
    }

    @Bean
    public FilterRegistrationBean xssFilterRegistrationBean() {
        // 创建一个自定义的 Filter
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new XssAndSqlFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        // 过滤指定请求地址
        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("excludes", "/login,/swagger-ui.html/**,/swagger-resources/**,/webjars/**,/v2/**,/upload/**,/file/img/**");
        initParameters.put("isIncludeRichText", "true");
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }
}
