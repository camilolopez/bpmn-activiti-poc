package com.oc.pc.oauth2.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableGlobalAuthentication
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.ldapAuthentication()
			.userSearchFilter("(uid={0})")
			.userDnPatterns("uid={0}")
			.groupRoleAttribute("ou")
			.groupSearchBase("ou=groups")
			.groupSearchFilter("(&(objectClass=groupOfUniqueNames)(member={0}))")
			.userSearchBase("ou=people")
			.contextSource()
			.url("ldap://localhost:8389/dc=oc,dc=co");
	}

	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedMethods(
				List.of(
						HttpMethod.GET.name(), 
						HttpMethod.PUT.name(), 
						HttpMethod.POST.name(),
						HttpMethod.DELETE.name()));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues());
		
		return source;
	}

}
