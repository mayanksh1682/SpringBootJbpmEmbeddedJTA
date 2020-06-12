package com.sample.client;

import java.util.List;
import java.util.Map;

import org.jbpm.process.audit.AuditLogService;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;


/**
 * This interfaces exposes common functions around Jbpm taskservice .
 * 
 * @author ms99658
 */
public interface JbpmService {

    /**
     * Delegate a task owned by sourceUserId to targetUserId
     * 
     * @param taskId
     * @param sourceUserId
     * @param targetUserId
     */
    void delegateTask(Long taskId, String sourceUserId, String targetUserId);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String locale);

    /**
     * Gets all tasks assigned to a particular userid filtered by processId(which is same as Task.PROCESSID column or bpmn file process identifier)
     * 
     * @param userId
     * @param processId
     * @return
     */
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId(String userId, String processId);

    /**
     * @param userId
     * @param processId
     * @param deploymentId
     * @return
     */
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId(String userId, String processId, String deploymentId) throws RuntimeException;

    /**
     * Claim a taskList by user
     * 
     * @param userId
     * @param taskList
     * @return
     */
    void claimTask(String userId, List<TaskSummary> taskList);

    /**
     * This Claims a task with taskId by userId
     * 
     * @param userId
     * @param taskId
     */
    void claimTask(String userId, Long taskId);

    /**
     * This moves to state of task to Complete state.(Reserved->InProgress->Completed)
     * 
     * @param userId
     * @param taskId
     * @param results
     */
    void completeTask(String userId, Long taskId, Map<String, Object> results);

    /**
     * This api releases the claim by userId on taskid
     * 
     * @param userId
     * @param taskId
     */
    void releaseTask(String userId, Long taskId);

    /**
     * This api releases the claim by userId on taskList
     * 
     * @param userId
     * @param taskList
     * @param results
     */
    void releaseTask(String userId, List<TaskSummary> taskList, Map<String, Object> results);

    /**
     * This moves to state of taskList to Complete state.(Reserved->InProgress->Completed)
     * 
     * @param userId
     * @param taskList
     * @param results
     * @throws Exception
     */
    void completeTask(String userId, List<TaskSummary> taskList, Map<String, Object> results);

    /**
     * Gets all tasks assigned to a particular userid
     * 
     * @param processInstanceId
     * @param locale
     *            , eg: en-UK or en-US based on task configured in workflow
     * @return
     */
    List<TaskSummary> getTasksByProcessInstanceId(Long processInstanceId, String locale);

    /**
     * Start a new process instance. The process (definition) that should be used is referenced by the given process id. Parameters can be passed to the process instance (as name-value pairs), and
     * these will be set as variables of the process instance.
     * 
     * @param processId
     *            the id of the process that should be started
     * @param parameters
     *            the process variables that should be set when starting the process instance
     * @return the ProcessInstance that represents the instance of the process that was started
     * @throws Exception
     */
    ProcessInstance startProcess(String processId, Map<String, Object> params);

    /**
     * Nominate a task with taskId to userlist , this is operator only when task is in created state
     * 
     * @param userId
     * @param userList
     * @param taskId
     */
    void nominateTask(String userId, List<String> userList, Long taskId);

   

    /**
     * Gets all tasks assigned to a particular processInstanceId ,statusList and formNameList
     * 
     * @param processInstanceId
     * @param locale
     *            , eg: en-UK or en-US based on task configured in workflow
     * @return
     */
    List<TaskImpl> getTasksByProcessInstanceIdStatusFormName(Long processInstanceId, List<Status> statusList, List<String> formName);

    /**
     * Check if workflow is active or completed.
     * 
     * @param processInstanceId
     * @return
     */
    boolean isWorkflowActive(Long processInstanceId);

    /**
     * @param processInstanceId
     * @param formName
     * @param action
     * @param approver
     * @param state
     * @throws Exception
     */
    Long completeTask(Long processInstanceId, String formName, String action, String approver, String state);

    /**
     * Abort a running process instance.
     * 
     * @param processInstanceId
     *            the id of the process that already in run.
     * @throws Exception
     */
    void abortProcess(Long processInstanceId);

    List<TaskImpl> getTaskPendingByActor(Map<String, Object> params);

    Task getTaskById(Long taskId);

    /***
     * Gets all tasks assigned to a userId deploymentId
     * 
     * @param userId
     * @param deploymentId
     * @return
     * @throws Exception
     */
    List<TaskSummary> getTasksAssignedAsPotentialOwnerByDeploymentId(String userId, String deploymentId);

    TaskService getTaskService();

    AuditService getAuditService();

    RuntimeEngine getEngine();

    KieSession getKsession();

    AuditLogService getLogService();

    RuntimeManager getManager();

}