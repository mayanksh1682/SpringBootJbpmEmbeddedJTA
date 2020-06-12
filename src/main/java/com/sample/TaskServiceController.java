package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sample.client.JbpmService;
import com.sample.jbpm.UserGroupCallbackImpl;

@RestController
@RequestMapping("/taskService")
public class TaskServiceController {
	
	@Autowired
	@Qualifier("jbpmService")
	private JbpmService jbpmService;
	
	@Autowired
	@Qualifier("jbpmUserGroupCallback")
	private  UserGroupCallbackImpl userGrpCallBack;
	
	@RequestMapping("/start")
	@ResponseBody
	ProcessInstance startProcess() {
			
		Map<String, List<String>> userToGroupMap = new HashMap<>();
		userToGroupMap.put("mayank", Stream.of("maker-group") .collect(Collectors.toList()));
		userToGroupMap.put("parul", Stream.of("checker-group") .collect(Collectors.toList()));
		userGrpCallBack.updateEntitlementGroupMap(userToGroupMap);
		Map<String,Object> params = new HashMap<>();
		params.put("makerGrp", "maker-group");
		params.put("checkerGrp", "checker-group");
		ProcessInstance pInstance = jbpmService.startProcess("MakerChecker-4", params);
		
		return pInstance;
	}
	
	@RequestMapping("/complete")
	@ResponseBody
	List<TaskSummary> completeTask() {
		
	   List<TaskSummary>  tasklist = jbpmService.getTasksAssignedAsPotentialOwnerByDeploymentId("mayank", "jbpmXmlTest");
		
		for(TaskSummary task : tasklist) {
			Map<String,Object> results = new HashMap<>();
			results.put("makerState", "SUBMITTED");
			results.put("makerId", "mayank");
			jbpmService.completeTask("mayank", task.getId(), results);
		}
		
	   tasklist = jbpmService.getTasksAssignedAsPotentialOwnerByDeploymentId("parul", "jbpmXmlTest");
		
	   for(TaskSummary task : tasklist) {
			Map<String,Object> results = new HashMap<>();
			results.put("checkerState", "APPROVED");
			results.put("checkerId", "parul");
			jbpmService.completeTask("parul", task.getId(), results);
		}
	
		return tasklist;
		
	}

}
