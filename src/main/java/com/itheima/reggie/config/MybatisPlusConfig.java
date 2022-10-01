package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig{
    @Bean
    public MybatisPlusInterceptor mybatisPlusIntercetor(){
        MybatisPlusInterceptor myplus=new MybatisPlusInterceptor();
        myplus.addInnerInterceptor(new PaginationInnerInterceptor());
        return myplus;
    }
}
