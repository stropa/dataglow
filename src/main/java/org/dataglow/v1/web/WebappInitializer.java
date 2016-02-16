package org.dataglow.v1.web;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebappInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/mvc/*"};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    /*@Override
    protected Filter[] getServletFilters() {
        Filter[] servletFilters = super.getServletFilters();
        return new SimpleCORSFilter[]{new SimpleCORSFilter()};
    }*/
}
