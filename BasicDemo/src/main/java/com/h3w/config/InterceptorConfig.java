package com.h3w.config;

import com.h3w.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean  //创建一个拦截器 返回值是拦截器类型
    public LoginInterceptor createLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    //增加拦截器方法
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration addInterceptor = registry.addInterceptor(createLoginInterceptor());
        //接受所有的访问
        addInterceptor.addPathPatterns("/**");
        //排除一些访问,不拦截
//        addInterceptor.excludePathPatterns("/loginPost");
        addInterceptor.excludePathPatterns("/js/**/*.js");
        addInterceptor.excludePathPatterns("/css/**/*.css");
  /*addInterceptor.excludePathPatterns("/cookie");
  addInterceptor.excludePathPatterns("/getcookie");*/

    }

}
