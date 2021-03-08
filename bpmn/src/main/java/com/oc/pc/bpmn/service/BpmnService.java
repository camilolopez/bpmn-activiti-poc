package com.oc.pc.bpmn.service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.asyncexecutor.JobManager;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BpmnService {
	public static final CronTrigger EVERY_TEN_SECONDS = new CronTrigger("0/10 * * * * *");
	
	@Autowired private LdapTemplate ldapTemplate;
	@Autowired private SessionRegistry sessionRegistry;
	@Autowired private RuntimeService runtimeService;
	@Autowired private TaskService taskService;
	@Autowired private UserGroupManager userGroupManager;
	@Autowired private JobManager jobManager;
	@Autowired private ManagementService managementService;
	
	@Autowired(required = false) private TaskScheduler taskScheduler;
	@Value("${com.oc.bpmn.scheduling.enabled}") private boolean schedulingEnabled;
	
	
	@PostConstruct
	public void initJob() 
	{
		if(schedulingEnabled)
			taskScheduler.schedule(() -> log.info("Time: " + Instant.now().toEpochMilli()), EVERY_TEN_SECONDS);
	}
	
	
	public String assign(final Execution execution) {
		log.info("Assigning user to process" );
		
		List<String> usersList = List.of("juan", "julio");
		
		String user = usersList.get(new Random().nextInt(usersList.size()));
		
		log.info("Task {} assigned to {}", execution.getId(), user);
		
		return user;
	}	
}
