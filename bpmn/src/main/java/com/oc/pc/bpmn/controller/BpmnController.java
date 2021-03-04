package com.oc.pc.bpmn.controller;

import static j2html.TagCreator.body;
import static j2html.TagCreator.br;
import static j2html.TagCreator.each;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("deprecation")
@RestController
public class BpmnController {
	private static final String VAR_NAME_APPROVED = "approved";
	 
	@Autowired private RuntimeService runtimeService;
	@Autowired private TaskService taskService;
	@Autowired private RepositoryService repositoryService;
	@Autowired private HistoryService historyService;
	@Autowired private ManagementService managementService;
	@Autowired private UserDetailsService userDetailsService;
	@Autowired private UserGroupManager userGroupManager;
	
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
		final UnaryOperator<String> up = (processDefinitionId) -> repositoryService.createProcessDefinitionQuery()
																.processDefinitionId(processDefinitionId)
																.list().stream()
																.findFirst().map(pd -> String.format("%s:%s", pd.getKey(), pd.getVersion()))
																.orElseThrow();
		
		
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
			.list();
		return body(
					table(thead(th("Process Instance Id"), th("Workflow Version")), 
							each(list, pi -> tr(td(pi.getId()), td(up.apply(pi.getProcessDefinitionId()))))
						) 
				)
				.render();
	}
	
	@GetMapping("/instance/{instance-id}")
	public String instanceDetails(@PathVariable("instance-id") final String instanceId) {
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(instanceId).list();
		
		List<Comment> comments = taskService.getProcessInstanceComments(instanceId);
		
		return body(
				table(thead(th("Task Id"), th("Name"), th("Assignee")),each(tasks, t -> tr(td(t.getId()), td(t.getName()), td(t.getAssignee())))),
				br(),
				table(thead(th("Date"), th("Comment")), each(comments, c -> tr(td(c.getTime().toLocaleString()), td(c.getFullMessage()))))
			)
			.render();
	}
	
	@GetMapping("/instance/delete/{instance-id}")
	public String deleteInstance(@PathVariable("instance-id") final String instanceId) {
		runtimeService.deleteProcessInstance(instanceId, "NA");
		
		return "Deleted " +  instanceId;
	}
	
	@GetMapping("/move/{task-id}")
	public String moveTask(@PathVariable("task-id") final String taskId) {
		Task task = taskService.createTaskQuery().includeProcessVariables()
				.taskId(taskId).singleResult();
		
		
		
		String comment = RandomStringUtils.randomAlphabetic(100);
		taskService.addComment(taskId, task.getProcessInstanceId(), comment);
		runtimeService.setVariable(task.getExecutionId(), VAR_NAME_APPROVED, RandomUtils.nextBoolean());
		taskService.complete(taskId);
		
		return comment;
	}
	
	
	@GetMapping("/mytasks")
	public String myTasks(final Principal principal) {
		String involvedUser = principal.getName();
		List<String> involvedGroups = userGroupManager.getUserGroups(involvedUser);
		
		List<Task> tasks = taskService.createTaskQuery()
				.taskCandidateOrAssigned(involvedUser)
				.taskCandidateGroupIn(involvedGroups)
				.list();
		
		return body(
				table(thead(th("Task Id"), th("Name"), th("Owner")),each(tasks, t -> tr(td(t.getId()), td(t.getName()), td(t.getOwner()))))			)
			.render();
	}
	
	@GetMapping("/tasks")
	public String tasks() {
		List<Task> tasks = taskService.createTaskQuery()
				.list();
		
		return body(
				table(thead(th("Task Id"), th("Name"), th("Owner") , th("Assignee")),
						each(tasks, t -> tr(td(t.getId()), td(t.getName()), td(t.getOwner()), td(t.getAssignee()))))			)
			.render();
	}
	
	
	
}
