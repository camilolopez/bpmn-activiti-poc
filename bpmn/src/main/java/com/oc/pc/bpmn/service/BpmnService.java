package com.oc.pc.bpmn.service;

import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BpmnService {
	private final LdapTemplate ldapTemplate;
	private final SessionRegistry sessionRegistry;
	
	@Autowired
	public BpmnService(final LdapTemplate ldapTemplate, final SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
		this.ldapTemplate = ldapTemplate;
	}

	public void assign(final Execution execution) {
		log.info("Assigning user to process");
	}
}
