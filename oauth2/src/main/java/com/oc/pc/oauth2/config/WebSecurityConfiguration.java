package com.oc.pc.oauth2.config;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
//		auth.ldapAuthentication()
//			.passwordEncoder(passwordEncoder())
//			.userSearchFilter("(uid={0})")
//			.userDnPatterns("uid={0}")
//			.groupRoleAttribute("ou")
//			.groupSearchBase("ou=groups")
//			.groupSearchFilter("(&(objectClass=groupOfUniqueNames)(member={0}))")
//			.userSearchBase("ou=people")
//			.contextSource()
//			.url("ldap://localhost:8389/dc=oc,dc=co");
		
		
		auth.authenticationProvider(ldapAuthenticationProvider());
	}
	
	
	@Bean
    public AuthenticationProvider ldapAuthenticationProvider() throws Exception {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource("ldap://localhost:8389/dc=oc,dc=co");
        contextSource.afterPropertiesSet();
        LdapUserSearch ldapUserSearch = new FilterBasedLdapUserSearch("ou=people", "(uid={0})", contextSource);
        BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);
        bindAuthenticator.setUserSearch(ldapUserSearch);
        LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator, ldapAuthoritiesPopulator());
//        LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator, new DefaultLdapAuthoritiesPopulator(contextSource, "ou=groups"));
        return ldapAuthenticationProvider;
    }
	
//	@Bean
//	public UserDetailsManager userDetailsManager(ContextSource contextSource) {
//		LdapUserDetailsManager ldapUserDetailsManager = new LdapUserDetailsManager(contextSource);
//		ldapUserDetailsManager.setGroupSearchBase("ou=groups,dc=oc,dc=co");
//		ldapUserDetailsManager.setUsernameMapper(new DefaultLdapUsernameToDnMapper("ou=people,dc=oc,dc=co", "uid"));
//
//		return ldapUserDetailsManager;
//	}
	
	@Bean
	public LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
		return new LdapAuthoritiesPopulator() {
			
			@Override
			public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData,
					String username) {
				// TODO Auto-generated method stub
				log.info("Getting authorities of {}", username);
				return Set.of(new SimpleGrantedAuthority("ROLE_LOQUESEA"), new SimpleGrantedAuthority("ROLE_ELQUETENGA"));
			}
		};
	}
	
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
	}
	
	@Bean
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
