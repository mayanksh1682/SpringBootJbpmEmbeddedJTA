package com.sample.jbpm;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;

import org.apache.log4j.Logger;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.UserInfo;

/**
 * @author ms99658
 */
public class CustomLDAPUserInfoImpl implements UserInfo {

    private static final Logger logger = Logger.getLogger(CustomLDAPUserInfoImpl.class);

    @PostConstruct
    public void init() {
        logger.info("CustomLDAPUserInfoImpl Initialized");
    }

    public CustomLDAPUserInfoImpl(boolean activate) {
        logger.info("UserInfo properties loaded...");
    }

    protected String extractUserId(String userDN, OrganizationalEntity entity) throws NamingException {
        return null;
    }

    public synchronized String getDisplayName(OrganizationalEntity entity) {
        return "systmta";
    }

    public synchronized String getEmailForGroupMembers(String adGroup) throws NamingException {
        StringBuilder emailBuilder = new StringBuilder();
        return emailBuilder.toString();
    }

    public synchronized String getLanguageForEntity(OrganizationalEntity entity) {
        long start = System.currentTimeMillis();
        String result = "en-UK";
        logger.info("Time Taken getLanguageForEntity[ms]" + (System.currentTimeMillis() - start));
        return result;
    }

    public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {

        long start = System.currentTimeMillis();
        InitialLdapContext ctx = null;

        ArrayList members = new ArrayList<OrganizationalEntity>();

        return members.iterator();
    }

    public boolean hasEmail(Group group) {
        return true;
    }

	@Override
	public String getEmailForEntity(OrganizationalEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityForEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

   
}
