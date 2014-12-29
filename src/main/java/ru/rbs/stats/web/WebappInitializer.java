package ru.rbs.stats.web;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import ru.rbs.stats.configuration.AppConfiguration;
import ru.rbs.stats.web.filter.SimpleCORSFilter;

import javax.servlet.Filter;

public class WebappInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/*"};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{AppConfiguration.class};
    }

    @Override
    protected Filter[] getServletFilters() {
        Filter[] servletFilters = super.getServletFilters();
        return new SimpleCORSFilter[]{new SimpleCORSFilter()};
    }
}
