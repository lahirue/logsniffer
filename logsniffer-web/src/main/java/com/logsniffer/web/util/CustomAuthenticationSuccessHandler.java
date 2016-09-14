package com.logsniffer.web.util;

import java.io.IOException;
import java.util.Set;
import java.util.List;
import com.logsniffer.config.RoleContextProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.apache.log4j.Logger;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.AttributesMapper;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import java.util.Collection;
import java.util.HashMap;
import com.logsniffer.config.LDAPServerPropertiesProvider;

public class CustomAuthenticationSuccessHandler implements
        AuthenticationSuccessHandler {

    static Logger log = Logger.getLogger(CustomAuthenticationSuccessHandler.class.getName());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException,
            ServletException {

        try {

            System.out.println("Initializing Authorization Information...");
            setAuthorizations();
            redirectStrategy.sendRedirect(request, response, "/");
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void setAuthorizations(){
        try{

            LdapContextSource ctxSrc = new LdapContextSource();
            ctxSrc.setUrl((String) (LDAPServerPropertiesProvider.propertyMap()).get("url"));
            ctxSrc.setUserDn( (String)(LDAPServerPropertiesProvider.propertyMap()).get("managerDn"));
            ctxSrc.setPassword( (String)(LDAPServerPropertiesProvider.propertyMap()).get("managerPassword"));
            ctxSrc.afterPropertiesSet();
            LdapTemplate lt = new LdapTemplate(ctxSrc);

            Authentication currentAuthentication =
                    SecurityContextHolder.getContext().getAuthentication();
            String currentPrincipalName = currentAuthentication.getName();

            List<String> groupIDList = lt.search(
                    query().where("objectClass").is("inetOrgPerson").and("uid").is(currentPrincipalName),
                    new AttributesMapper<String>() {
                        public String mapFromAttributes(Attributes attrs)
                                throws NamingException {
                            return (String) attrs.get("gidNumber").get();
                        }
                    });

            RoleContextProvider.initialize();
            HashMap<String,String> roleMap = RoleContextProvider.getRoleMap();
            LDAPAuthorityMapper ldapAuthorityMapper =  new LDAPAuthorityMapper();


            for (String groupId : groupIDList) {
                String role = roleMap.get(groupId);
                if(!role.isEmpty()){
                    ldapAuthorityMapper.addAuthority(role);
                }
            }

            Collection<GrantedAuthority> authorities = ldapAuthorityMapper.getAuthorities();

            LDAPAuthorizationProvider modifiedAuthentication = new LDAPAuthorizationProvider(currentAuthentication,authorities);
            SecurityContextHolder.getContext().setAuthentication(modifiedAuthentication);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
}