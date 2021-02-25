package com.oc.pc.bpmn.security;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        .groupSearchBase("ou=groups")
        .contextSource()
    	.url("ldap://localhost:8389/dc=oc,dc=co");        
  }
  
  @Bean
  public SessionRegistry sessionRegistry() {
      return new SessionRegistryImpl();
  }

  @Bean
  public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
      return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
  }

}
