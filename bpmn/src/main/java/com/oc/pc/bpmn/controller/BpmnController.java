package com.oc.pc.bpmn.controller;

import static j2html.TagCreator.body;
import static j2html.TagCreator.each;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.tr;

import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BpmnController {
	private final RuntimeService runtimeService;
	
	@Autowired
	public BpmnController(RuntimeService runtimeService) {
		super();
		this.runtimeService = runtimeService;
	}

	@GetMapping("/")
	public String index() {
		return "Welcome to the home page!";
	}
	
	@GetMapping("/start")
	public String start() {
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("pqr_nacional");
		
		return String.format("Process started %s. Number of currently running", processInstance.getId());
	}
	
	@GetMapping("/instances")
	public String instances() {
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
			.list();
		return body(
					table(each(list, pi -> tr(td(pi.getId()), td(pi.getDescription())))) 
				)
				.render();
	}
	
	
}
