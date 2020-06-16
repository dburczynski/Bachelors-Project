package com.forex.forexpredictor.config;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class PageConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/user-home").setViewName("user-home");
        registry.addViewController("/user-info").setViewName("user-edit");
        registry.addViewController("/user-edit").setViewName("user-edit");
        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/user-add-currency").setViewName("user-add-currency");
        registry.addViewController("/view-currency-pair").setViewName("view-currency-pair");
    }

}
