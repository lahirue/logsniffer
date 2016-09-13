package com.logsniffer.web.util;

import org.springframework.security.core.GrantedAuthority;

public enum LDAPAuthority implements GrantedAuthority{

    APP_USER, APP_ADMIN; //roles used in application
    public String getAuthority() {
        return name();
    }
}
