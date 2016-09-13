package com.logsniffer.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.core.env.Environment;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.core.context.SecurityContextHolder;

import com.logsniffer.config.LDAPServerPropertiesProvider;

import javax.management.MXBean;
import java.util.HashMap;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth)
            throws Exception
    {
        LDAPServerPropertiesProvider.initialize();
        HashMap<String, String> propertyMap = LDAPServerPropertiesProvider.propertyMap();

        DefaultSpringSecurityContextSource context = new DefaultSpringSecurityContextSource(propertyMap.get("url"));
        context.afterPropertiesSet();

        auth.ldapAuthentication()
                .contextSource(context)
                .userSearchFilter(propertyMap.get("userSearchFilter"))
                .userSearchBase(propertyMap.get("userSearchBase"))
                .groupSearchBase(propertyMap.get("groupSearchBase"))
                .groupSearchFilter(propertyMap.get("groupSearchFilter"))
                .rolePrefix("");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/c/Reports").access("hasAuthority('APP_ADMIN')")
                .antMatchers("/c/System/**").access("hasAuthority('APP_ADMIN')")
                .antMatchers("/c/sources/**").access("hasAuthority('APP_ADMIN')")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .successHandler(customAuthenticationSuccessHandler())
                .permitAll();
         }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }
}