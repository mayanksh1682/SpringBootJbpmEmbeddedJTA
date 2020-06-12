package com.sample.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.google.common.collect.Lists;
import com.sample.dao.api.GenericDao;

/**
 * This class is exposing api for taskservice
 * 
 * @author ms99658
 */
@Service("jbpmService")
public class JbpmServiceImpl implements JbpmService {

    private static final Logger logger = Logger.getLogger(JbpmServiceImpl.class);

    private RuntimeEngine engine;

    @Autowired
    @Qualifier("sampleTransactionManager")
    private PlatformTransactionManager jbpmTxManager;

    private KieSession ksession;

    @Autowired
    @Qualifier("logService")
    private AuditLogService logService;

    @Autowired
    private RuntimeManager manager;

    @Autowired  
    private GenericDao genericDao;

    private TaskService taskService;

    private AuditService auditService;

    private Object lock = new Object();

    public void close() {
        synchronized (lock) {
            manager.disposeRuntimeEngine(engine);
            manager.close();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#delegateTask(java.lang.Long, java.lang.String, java.lang.String)
     */
    @Override
    public void delegateTask(Long taskId, String sourceUserId, String targetUserId) {
        taskService.delegate(taskId, sourceUserId, targetUserId);
        logger.info("Task [" + taskId + "] Delegated  from Source User[" + sourceUserId + "] to Target User[" + targetUserId + "].");
    }

    @Override
    public RuntimeEngine getEngine() {
        return engine;
    }

    public PlatformTransactionManager getJbpmTxManager() {
        return jbpmTxManager;
    }

    @Override
    public KieSession getKsession() {
        return ksession;
    }

    @Override
    public AuditLogService getLogService() {
        return logService;
    }

    @Override
    public RuntimeManager getManager() {
        return manager;
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#getTasksAssignedAsPotentialOwner (java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String locale) {
        synchronized (lock) {
            List<TaskSummary> taskList = null;
            try {
                taskList = taskService.getTasksAssignedAsPotentialOwner(userId, locale);
                logger.info("Found " + taskList.size() + " task(s) for user " + userId);
            }
            finally {
            }
            return taskList;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService# getTasksAssignedAsPotentialOwnerByProcessId(java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId(String userId, String processId) {
        synchronized (lock) {
            List<TaskSummary> taskList = new ArrayList<>();
            try {
                List<TaskSummary> tmpTaskList = taskService.getTasksAssignedAsPotentialOwner(userId, "en-US");
                // List<TaskSummary> tmpTaskList =
                // taskService.getTasksAssignedAsPotentialOwnerByProcessId(userId,
                // processId);
                for (TaskSummary tSumm : tmpTaskList) {
                    if (tSumm.getProcessId().equals(processId)) {
                        taskList.add(tSumm);
                    }
                }
                logger.info("Found " + taskList.size() + " task(s) for user " + userId);
            }
            finally {
            }
            return taskList;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService# getTasksAssignedAsPotentialOwnerByProcessId(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByProcessId(String userId, String processId, String deploymentId) {
        synchronized (lock) {
            List<TaskSummary> taskList = new ArrayList<>();
            try {
                List<TaskSummary> tmpTaskList = taskService.getTasksAssignedAsPotentialOwner(userId, "en-US");
                for (TaskSummary tSumm : tmpTaskList) {
                    if (StringUtils.startsWithIgnoreCase(tSumm.getProcessId(), processId) && tSumm.getDeploymentId().equalsIgnoreCase(deploymentId)) {
                        taskList.add(tSumm);
                    }
                }
                logger.info("Found " + taskList.size() + " task(s) for user " + userId);
            }
            finally {
            }
            return taskList;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#claimTask(java.lang.String, java.util.List)
     */
    @Override
    public void claimTask(String userId, List<TaskSummary> taskList) {
        boolean claimFailed = false;

        synchronized (lock) {

            for (TaskSummary taskLocal : taskList) {

                claimFailed = false;

                logger.info("Task Detail [" + taskLocal.getName() + "][" + taskLocal.getId() + "])");
                try {

                    if (taskLocal.getStatus().equals(Status.Reserved) || taskLocal.getStatus().equals(Status.Ready)) {
                        try {
                            taskService.claim(taskLocal.getId(), userId);
                            logger.info(taskLocal.getName() + " Claim action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");
                        }
                        catch (Exception ex) {
                            claimFailed = true;
                            ex.printStackTrace();
                        }
                    }
                    if (claimFailed) {
                        continue;
                    }

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#claimTask(java.lang.String, java.lang.Long)
     */
    @Override
    public void claimTask(String userId, Long taskId) {
        synchronized (lock) {
            try {
                logger.info("Claim on task [" + taskId + "]) requested");
                Task taskLocal = taskService.getTaskById(taskId);

                if (taskLocal.getTaskData().getStatus().equals(Status.Reserved) || taskLocal.getTaskData().getStatus().equals(Status.Ready)) {
                    taskService.claim(taskLocal.getId(), userId);
                    logger.info(taskLocal.getName() + " Claim action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");

                }
            }
            catch (Exception ex) {
                throw ex;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#completeTask(java.lang.String, java.lang.Long, java.util.Map)
     */
    @Override
    public void completeTask(String userId, Long taskId, Map<String, Object> results) {
        synchronized (lock) {
            try {
                logger.info("Complete on task [" + taskId + "]) requested");
                Task taskLocal = taskService.getTaskById(taskId);
                if (taskLocal.getTaskData().getStatus().equals(Status.Ready)) {
                    taskService.claim(taskLocal.getId(), userId);
                    logger.info(taskLocal.getName() + " Claim action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");
                }
                if (!taskLocal.getTaskData().getStatus().equals(Status.InProgress)) {
                    logger.info(taskLocal.getName() + " Start action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");
                    taskService.start(taskLocal.getId(), userId);
                }
                taskService.complete(taskLocal.getId(), userId, results);
                logger.info(taskLocal.getName() + " Complete action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");
            }
            catch (Exception ex) {
                throw ex;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#releaseTask(java.lang.String, java.lang.Long)
     */
    @Override
    public void releaseTask(String userId, Long taskId) {
        synchronized (lock) {
            logger.info("Release on task [" + taskId + "]) requested");
            Task taskLocal = taskService.getTaskById(taskId);
            if (taskLocal.getTaskData().getStatus().equals(Status.Reserved)) {
                try {
                    taskService.release(taskLocal.getId(), userId);
                    logger.info("Release on task [" + taskId + "]) done.");
                }
                catch (Exception ex) {
                    throw ex;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#releaseTask(java.lang.String, java.util.List, java.util.Map)
     */
    @Override
    public void releaseTask(String userId, List<TaskSummary> taskList, Map<String, Object> results) {
        boolean claimFailed = false;

        synchronized (lock) {

            for (TaskSummary taskLocal : taskList) {
                claimFailed = false;
                logger.info("Task Detail [" + taskLocal.getName() + "][" + taskLocal.getId() + "])");
                try {

                    if (taskLocal.getStatus().equals(Status.Reserved)) {
                        try {
                            taskService.release(taskLocal.getId(), userId);

                            logger.info(taskLocal.getName() + " Claim action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");
                        }
                        catch (Exception ex) {
                            claimFailed = true;
                            ex.printStackTrace();
                        }
                    }
                    if (claimFailed) {
                        continue;
                    }

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#completeTask(java.lang.String, java.util.List, java.util.Map)
     */
    @Override
    public void completeTask(String userId, List<TaskSummary> taskList, Map<String, Object> results) {

        boolean claimFailed = false;
        synchronized (lock) {

            for (TaskSummary taskLocal : taskList) {

                claimFailed = false;

                logger.info("Task Detail [" + taskLocal.getName() + "][" + taskLocal.getId() + "])");
                try {

                    if (taskLocal.getStatus().equals(Status.Ready)) {
                        try {
                            taskService.claim(taskLocal.getId(), userId);

                            logger.info(taskLocal.getName() + " Claim action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");
                        }
                        catch (Exception ex) {
                            claimFailed = true;
                            ex.printStackTrace();
                        }
                    }
                    if (claimFailed) {
                        continue;
                    }

                    try {
                        taskService.start(taskLocal.getId(), userId);
                        logger.info(taskLocal.getName() + " Start action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    taskService.complete(taskLocal.getId(), userId, results);
                    logger.info(taskLocal.getName() + " Complete action suceeded for userId [" + userId + "]on task [" + taskLocal.getId() + "]");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#getTasksByProcessInstanceId(java .lang.Long, java.lang.String)
     */
    @Override
    public List<TaskSummary> getTasksByProcessInstanceId(Long processInstanceId, String locale) {
        synchronized (lock) {
            List<Status> taskStatus = new ArrayList();
            taskStatus.add(Status.Completed);
            taskStatus.add(Status.InProgress);
            taskStatus.add(Status.Reserved);
            taskStatus.add(Status.Ready);
            taskStatus.add(Status.Created);
            List<TaskSummary> workFlowTaskList = taskService.getTasksByStatusByProcessInstanceId(processInstanceId, taskStatus, locale);

            return workFlowTaskList;
        }

    }

    @Override
    public TaskService getTaskService() {
        return taskService;
    }

    @PostConstruct
    private void init() {
        engine = manager.getRuntimeEngine(EmptyContext.get());
        auditService = engine.getAuditService();
        ksession = engine.getKieSession();
        taskService = engine.getTaskService();
        logger.info("Initialized Task Server " + manager.toString());
    }

    @Override
    public AuditService getAuditService() {
        return auditService;
    }

    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    public void setEngine(RuntimeEngine engine) {
        this.engine = engine;
    }

    public void setJbpmTxManager(AbstractPlatformTransactionManager jbpmTxManager) {
        this.jbpmTxManager = jbpmTxManager;
    }

    public void setKsession(KieSession ksession) {
        this.ksession = ksession;
    }

    public void setLogService(AuditLogService logService) {
        this.logService = logService;
    }

    public void setManager(RuntimeManager manager) {
        this.manager = manager;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public GenericDao getGenericDao() {
        return genericDao;
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#startProcess(java.lang.String, java.util.Map)
     */
    @Override
    public ProcessInstance startProcess(String processId, Map<String, Object> params) {
        ProcessInstance pInstance = null;
        try {
            synchronized (lock) {
                logger.info("Manager " + manager.getIdentifier());
                pInstance = ksession.startProcess(processId, params);
            }
        }
        finally {
        }
        return pInstance;
    }

    @Override
    public void abortProcess(Long processInstanceId) {
        // TODO Auto-generated method stub
        try {
            synchronized (lock) {
                logger.info("Abort a process instance [" + processInstanceId + "]) requested.");
                ksession.abortProcessInstance(processInstanceId);
            }
        }
        finally {
        }
    }

    /*
     * (non-Javadoc)
     * @see com.citi.dmt.workflow.client.JbpmService#nominateTask(java.lang.String, java.util.List, java.lang.Long)
     */
    @Override
    public void nominateTask(String userId, List<String> userList, Long taskId) {
        synchronized (lock) {
            logger.info("Nominate on task [" + taskId + "]) requested");
            Task taskLocal = taskService.getTaskById(taskId);
            try {
                List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
                for (String soeid : userList) {
                    User user = TaskModelProvider.getFactory().newUser();
                    ((InternalOrganizationalEntity) user).setId(soeid);
                    potentialOwners.add(user);
                }
                taskService.nominate(taskId, userId, potentialOwners);
                logger.info(taskLocal.getName() + " Nominate action suceeded for userList [" + userList + "]on task [" + taskLocal.getId() + "]");
            }
            catch (Exception ex) {
                throw ex;
            }

        }
    }

	

    @Override
    public List<TaskImpl> getTasksByProcessInstanceIdStatusFormName(Long processInstanceId, List<Status> statusList, List<String> formName) throws RuntimeException {
        Map<String, Object> params = new HashMap<>();
        params.put("processInstanceId", processInstanceId);
        params.put("status", statusList);
        params.put("formName", formName);
        List<TaskImpl> taskList;
        try {
            taskList = genericDao.executeNamedQuery("fetchTaskByProcessInstanceIdStatusFormName", params);
        }
        catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        return taskList;
    }

    @Override
    public boolean isWorkflowActive(Long processInstanceId) throws RuntimeException {
        Map<String, Object> params = new HashMap<>();
        params.put("processInstanceId", processInstanceId);
        List<ProcessInstanceLog> pLogList;
        try {
            pLogList = genericDao.executeNamedQuery("fetchProcessInstanceLog", params);
        }
        catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        if (pLogList != null && pLogList.size() == 1) {
            ProcessInstanceLog pLog = pLogList.get(0);
            if (pLog.getEnd() == null)
                return true;
            else return false;
        }
        return false;
    }

    @Override
    public Long completeTask(Long processInstanceId, String formName, String action, String approver, String state) {
        List<Status> statusList = new ArrayList<>();
        statusList.add(Status.Reserved);
        statusList.add(Status.Ready);
        List<String> formList = new ArrayList<>();
        formList.add(formName);

        List<TaskImpl> taskList = getTasksByProcessInstanceIdStatusFormName(processInstanceId, statusList, formList);
        // CCState
        // CCState
        Long taskId = null;
        if (!taskList.isEmpty() && taskList.size() == 1) {
            Map<String, Object> results = new HashMap<>();
            results.put(state, action);
            taskId = taskList.get(0).getId();
            completeTask(approver, taskId, results);
            logger.info("Approval Completed ");
        }
        else {
            logger.info("More than 1 task for a particular workflow and formName cannot exsist in ready or reserved state at a time");
        }
        return taskId;
    }

    @Override
    public List<TaskImpl> getTaskPendingByActor(Map<String, Object> params) throws RuntimeException {
        List<TaskImpl> taskList;
        try {
            taskList = genericDao.executeNamedQuery("fetchTaskPendingByActor", params);
        }
        catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        return taskList;
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByDeploymentId(String userId, String deploymentId) {
        synchronized (lock) {
            List<TaskSummary> taskList = Lists.newArrayList();
            try {
                List<TaskSummary> tmpTaskList = taskService.getTasksAssignedAsPotentialOwner(userId, "en-US");
                for (TaskSummary tSumm : tmpTaskList) {
                    if (tSumm.getDeploymentId().equals(deploymentId)) {
                        taskList.add(tSumm);
                    }
                }

            }
            finally {
                // logger.info("Found " + taskList.size() + " task(s) for user "
                // + userId);
            }
            return taskList;
        }
    }
}