package com.h3w;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@EntityScan("com.h3w.entity")
@EnableTransactionManagement // 开启注解事务管理，等同于xml配置文件中的 <tx:annotation-driven />
@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
@Configuration
@EnableScheduling
//@EnableJpaRepositories("com.h3w.dao") // JPA扫描该包路径下的Repositorie
@ComponentScan({"com.h3w.*"}) //包名：扫描这个包下面的加了@Controller注解的类及其子包的加了@Controller注解的类，或者加了Service注解等其他组件注解的类
public class SpringBootDemoApplication extends SpringBootServletInitializer {
    @Bean
    public Object testBean(PlatformTransactionManager platformTransactionManager) {
        System.out.println(">>>>>>>>>>" + platformTransactionManager.getClass().getName());
        return new Object();
    }

    // 继承SpringBootServletInitializer 实现configure 方便打war 外部服务器部署。
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootDemoApplication.class);
    }

//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        // 单个数据大小
//        factory.setMaxFileSize("102400KB"); // KB,MB
//        // 总上传数据大小
//        factory.setMaxRequestSize("1024000KB");
//        return factory.createMultipartConfig();
//    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }
}
