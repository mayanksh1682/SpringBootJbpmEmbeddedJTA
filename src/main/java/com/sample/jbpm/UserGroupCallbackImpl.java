package com.sample.jbpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.kie.api.task.UserGroupCallback;

/**
 * @author ms99658
 */

public class UserGroupCallbackImpl implements UserGroupCallback {

    private static final Logger logger = Logger.getLogger(UserGroupCallbackImpl.class);

    private Properties config;

    private Map<String, List<String>> userIdToGroupListMap;
    private String userGrpMapStr;

    public String getUserGrpMapStr() {
        return userGrpMapStr;
    }

    public void setUserGrpMapStr(String userGrpMapStr) {
        this.userGrpMapStr = userGrpMapStr;
    }

    public UserGroupCallbackImpl(boolean activate) {
        logger.debug("Callback properties loaded...");
    }

    public boolean existsGroup(String groupId) {
        return true;
    }

    public synchronized boolean existsUser(String userId) {
        return true;
    }

    public synchronized List<String> getGroupsForUser(String userId) {
        long start = System.currentTimeMillis();
        List<String> userGroups = null;

        if (userIdToGroupListMap == null) {
            logger.info("userIdToGroupListMap found null, hence initializing");
            userIdToGroupListMap = new ConcurrentHashMap<>();
            userIdToGroupListMap.put("Administrator", new ArrayList<String>());
            List<String> userGroupsLocal = new ArrayList<>();
            userGroupsLocal.add("fGLBTMMaintenance");
            userIdToGroupListMap.put("systmta", userGroupsLocal);
        }

        if (userId != null && userIdToGroupListMap.containsKey(userId)) {
            logger.info("Returning groups from local cache for user " + userId);
            logger.info("Time Taken existsGroup[ms]" + (System.currentTimeMillis() - start));
            return userIdToGroupListMap.get(userId);
        }
        logger.info("Time Taken getGroupsForUser[ms]" + (System.currentTimeMillis() - start));
        return userGroups;
    }

    private void populateGroupForUser() {
        List<String> userGroupList = new ArrayList<>();
        String userGroupInfo = userGrpMapStr;// this.config.getProperty("user.group.map");
        String[] usrGroupArr = userGroupInfo.split(",");

        for (String info : usrGroupArr) {
            String user = info.split(":")[0];
            String group = info.split(":")[1];
            List<String> userGroupsLocal = null;
            if (userIdToGroupListMap.containsKey(user)) {
                userGroupsLocal = userIdToGroupListMap.get(user);
            }
            else userGroupsLocal = new ArrayList<>();
            userGroupsLocal.add(group);
            userIdToGroupListMap.put(user, userGroupsLocal);
        }
    }

    @PostConstruct
    public void init() {
        if (userIdToGroupListMap == null) userIdToGroupListMap = new ConcurrentHashMap<>();

        List<String> list = new ArrayList<>();
        list.add("Administrators");
        userIdToGroupListMap.put("Administrator", list);
        populateGroupForUser();
        logger.info("CustomLDAPUserGroupCallbackImpl initialized");
    }

    public void updateEntitlementGroupMap(Map<String, List<String>> userToGroupMap) {
        userIdToGroupListMap.putAll(userToGroupMap);
        logger.info("Updated userIdToGroupListMap in UserGroupCallbackImpl");
    }
}
