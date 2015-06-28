package ru.rbs.stats.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.Resource;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.rbs")
public class MvcConfiguration {


    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;



}
