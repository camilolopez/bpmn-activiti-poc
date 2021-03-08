package com.oc.pc.bpmn.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock.InterceptMode;

/**
 * Shedlock configuration. 
 * 
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m", interceptMode = InterceptMode.PROXY_SCHEDULER)
@ConditionalOnProperty(prefix = "com.oc.bpmn.scheduling", name="enabled", havingValue="true", matchIfMissing = true)
public class SchedulerConfig {
	
	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		
	            return new JdbcTemplateLockProvider(
	                JdbcTemplateLockProvider.Configuration.builder()
	                .withJdbcTemplate(new JdbcTemplate(dataSource))
	                .build()
	            );
	}
	
	@Bean
	public TaskScheduler taskScheduler() {
	    return new ThreadPoolTaskScheduler();
	}
}
