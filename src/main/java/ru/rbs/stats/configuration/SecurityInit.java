package ru.rbs.stats.configuration;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInit extends AbstractSecurityWebApplicationInitializer {

    public SecurityInit() {
        super(AppConfiguration.class);
    }
}
