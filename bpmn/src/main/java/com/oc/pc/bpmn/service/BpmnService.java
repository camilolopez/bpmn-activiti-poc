package com.oc.pc.bpmn.service;

import java.util.List;
import java.util.Random;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BpmnService {
	@Autowired private LdapTemplate ldapTemplate;
	@Autowired private SessionRegistry sessionRegistry;
	@Autowired private RuntimeService runtimeService;
	@Autowired private TaskService taskService;
	@Autowired private UserGroupManager userGroupManager;
	
	public String assign(final Execution execution) {
		log.info("Assigning user to process" );
		
		List<String> usersList = List.of("juan", "julio");
		
		String user = usersList.get(new Random().nextInt(usersList.size()));
		
		log.info("Task {} assigned to {}", execution.getId(), user);
		
		return user;
	}
	
}
