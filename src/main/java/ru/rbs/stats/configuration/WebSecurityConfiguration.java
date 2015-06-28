package ru.rbs.stats.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.rbs.stats.web.filter.CsrfHeaderFilter;

@Configuration
@EnableWebSecurity
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("tom").password("123456").roles("USER");
        auth.inMemoryAuthentication().withUser("bill").password("123456").roles("DATA_ENGINEER", "USER");
                //.authorities("EDIT_REPORTS");
        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN", "USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /*http.authorizeRequests()
                //.anyRequest().hasRole("USER")
                .antMatchers("/mvc/reports*").hasRole("DATA_ENGINEER")
                .and().formLogin();*/

        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/index.html", "/home.html", "/login.html", "/", "/parts/**", "/js/**", "/css/**", "/app/**", "/fonts/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                //.deleteCookies("remove")
                .invalidateHttpSession(true)
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .and()
                .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .csrf().csrfTokenRepository(csrfTokenRepository());
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

}
