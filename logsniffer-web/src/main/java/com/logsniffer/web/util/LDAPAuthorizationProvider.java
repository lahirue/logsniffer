package com.logsniffer.web.util;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.Authentication;

public class LDAPAuthorizationProvider implements Authentication {

    private List<GrantedAuthority> authorities;
    private Authentication wrapped;

    public LDAPAuthorizationProvider(Authentication wrapped, Collection<GrantedAuthority> Auths) {
        this.wrapped = wrapped;
        this.authorities = new ArrayList<GrantedAuthority>(wrapped.getAuthorities());
        this.authorities.addAll(Auths);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public Authentication getWrapped() {
        return this.wrapped;
    }

    public void setAuthenticated(boolean isAuthenticated){
        wrapped.setAuthenticated(isAuthenticated);
    }

    public boolean isAuthenticated() {
       return wrapped.isAuthenticated();
    }


    public Object getCredentials(){
       return wrapped.getCredentials();
    }

    public Object getDetails(){
       return wrapped.getDetails();
    }

    public Object getPrincipal(){
       return wrapped.getPrincipal();
    }

    public String getName(){
        return wrapped.getName();
    }

}

