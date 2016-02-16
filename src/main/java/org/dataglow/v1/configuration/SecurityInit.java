package org.dataglow.v1.configuration;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInit extends AbstractSecurityWebApplicationInitializer {

    public SecurityInit() {
        super(AppConfiguration.class);
    }
}
