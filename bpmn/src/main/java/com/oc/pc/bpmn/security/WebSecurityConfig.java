package com.oc.pc.bpmn.security;

import java.util.List;
import java.util.stream.Collectors;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.core.common.spring.identity.ActivitiUserGroupManagerImpl;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.DefaultLdapUsernameToDnMapper;
import org.springframework.security.ldap.userdetails.LdapUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http
//      .authorizeRequests()
//        .anyRequest().fullyAuthenticated()
//        .and()
//      .formLogin();
//  }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.sessionManagement()
//			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.maximumSessions(1)
			.sessionRegistry(sessionRegistry())
			.and();
		http.authorizeRequests()
			.anyRequest().authenticated()
			.and()
		.formLogin();
		// @formatter:on
	}

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      	.ldapAuthentication()
      	.userSearchFilter("(uid={0})")
      	.userDnPatterns("uid={0}")
        .groupRoleAttribute("ou")
        .groupSearchBase("ou=groups")
        .groupSearchFilter("(&(objectClass=groupOfUniqueNames)(member={0}))")
        .userSearchBase("ou=people")
        .contextSource()
    	.url("ldap://localhost:8389/dc=oc,dc=co");
    	
  }
  
  /**
   * User details from LDAP, it's used by UserDetailsService 
   * @param contextSource
   * @return
   */
  @Bean
  public UserDetailsManager userDetailsManager(ContextSource contextSource) {
	  LdapUserDetailsManager ldapUserDetailsManager = new LdapUserDetailsManager(contextSource);
	  ldapUserDetailsManager.setGroupSearchBase("ou=groups,dc=oc,dc=co");
	  ldapUserDetailsManager.setUsernameMapper(new DefaultLdapUsernameToDnMapper("ou=people,dc=oc,dc=co","uid"));
	  
	return ldapUserDetailsManager;
  }
  
  /**
   * UserGroup Manager is used by several services for querying process instances, task, history entries etc..
   * @param userDetailsService
   * @return
   */
  @Bean
  public UserGroupManager userGroupManager(UserDetailsService userDetailsService) {
      return new UserGroupManager() {
		
		@Override
		public List<String> getUsers() {
			// TODO Get list of users;
			return List.of("juan", "julio", "leon", "rafael");
		}
		
		@Override
		public List<String> getUserRoles(String username) {
			return userDetailsService.loadUserByUsername(username).getAuthorities().stream()
	                .filter((GrantedAuthority a) -> a.getAuthority().startsWith("ROLE_"))
	                .map((GrantedAuthority a) -> a.getAuthority().substring(5))
	                .collect(Collectors.toList());
		}
		
		@Override
		public List<String> getUserGroups(String username) {
			return userDetailsService.loadUserByUsername(username).getAuthorities().stream()
	                .filter((GrantedAuthority a) -> a.getAuthority().startsWith("ROLE_"))
	                .map((GrantedAuthority a) -> a.getAuthority().substring(5))
	                .collect(Collectors.toList());
		}
		
		@Override
		public List<String> getGroups() {
			// TODO Get List of Groups
			return List.of("ASESORES", "OPERACIONES");
		}
	};
  }
  
  /**
   * Check who is logged
   * @return
   */
  @Bean
  public SessionRegistry sessionRegistry() {
      return new SessionRegistryImpl();
  }

  @Bean
  public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
      return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
  }

}
