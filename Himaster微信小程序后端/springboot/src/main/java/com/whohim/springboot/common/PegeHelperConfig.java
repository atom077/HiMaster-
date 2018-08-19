package com.whohim.springboot.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.pagehelper.PageHelper;

import java.util.Properties;

public class PegeHelperConfig {
    /**
     * 注解@Configuration 表示PageHelperConfig 这个类是用来做配置的。
     注解@Bean 表示启动PageHelper这个拦截器。
     */
    @Configuration
    public class PageHelperConfig {

        @Bean
        public PageHelper pageHelper() {
            PageHelper pageHelper = new PageHelper();
            Properties p = new Properties();
            p.setProperty("offsetAsPageNum", "true");
            p.setProperty("rowBoundsWithCount", "true");
            p.setProperty("reasonable", "true");
            pageHelper.setProperties(p);
            return pageHelper;
        }
    }

}
