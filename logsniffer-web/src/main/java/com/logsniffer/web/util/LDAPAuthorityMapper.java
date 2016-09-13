package com.logsniffer.web.util;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import java.util.ArrayList;

public class LDAPAuthorityMapper{

    private ArrayList<String> Authorities = new ArrayList<>();

    public void addAuthority(String authority){
        Authorities.add(authority);
    }

    public Collection<GrantedAuthority> getAuthorities() {
        Set roles = EnumSet.noneOf(LDAPAuthority.class); //empty EnumSet

        for (String authority : Authorities) {
            if(authority.equals("admin")){
                roles.add(LDAPAuthority.APP_ADMIN);
            }else{
                roles.add(LDAPAuthority.APP_USER);
            }
        }

        return roles;
    }
}

