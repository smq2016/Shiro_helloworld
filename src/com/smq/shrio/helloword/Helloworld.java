package com.smq.shrio.helloword;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helloworld {
	
	private static final transient Logger log = LoggerFactory.getLogger(Helloworld.class);
	
	public static void main(String[] args) {
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
	    SecurityManager securityManager = factory.getInstance();

	    SecurityUtils.setSecurityManager(securityManager);

	    // ��ȡ��ǰ�� Subject. ���� SecurityUtils.getSubject();
	    Subject currentUser = SecurityUtils.getSubject();

	    // ����ʹ�� Session 
	    // ��ȡ Session: Subject#getSession()
	    Session session = currentUser.getSession();
	    session.setAttribute("someKey", "aValue");
	    String value = (String) session.getAttribute("someKey");
	    if (value.equals("aValue")) {
	        log.info("---> ŶŶŶ Retrieved the correct value! [" + value + "]");
	    }

	    // ���Ե�ǰ���û��Ƿ��Ѿ�����֤. ���Ƿ��Ѿ���¼. 
	    // ���� Subject �� isAuthenticated() 
	    if (!currentUser.isAuthenticated()) {
	    	// ���û����������װΪ UsernamePasswordToken ����
	        UsernamePasswordToken token = new UsernamePasswordToken("smq", "123");
	        // rememberme
	        token.setRememberMe(true);
	        try {
	        	// ִ�е�¼. 
	            currentUser.login(token);
	        } 
	        // ��û��ָ�����˻�, �� shiro �����׳� UnknownAccountException �쳣. 
	        catch (UnknownAccountException uae) {
	            log.info("----> There is no user with username of " + token.getPrincipal());
	            return; 
	        } 
	        // ���˻�����, �����벻ƥ��, �� shiro ���׳� IncorrectCredentialsException �쳣�� 
	        catch (IncorrectCredentialsException ice) {
	            log.info("----> Password for account " + token.getPrincipal() + " was incorrect!");
	            return; 
	        } 
	        // �û����������쳣 LockedAccountException
	        catch (LockedAccountException lae) {
	            log.info("The account for username " + token.getPrincipal() + " is locked.  " +
	                    "Please contact your administrator to unlock it.");
	        }
	        // ... catch more exceptions here (maybe custom ones specific to your application?
	        // ������֤ʱ�쳣�ĸ���. 
	        catch (AuthenticationException ae) {
	            //unexpected condition?  error?
	        }
	    }

	    log.info("----> User [" + currentUser.getPrincipal() + "] logged in successfully.");

	    // �����Ƿ���ĳһ����ɫ. ���� Subject �� hasRole ����. 
	    if (currentUser.hasRole("schwartz")) {
	        log.info("----> May the Schwartz be with you!");
	    } else {
	        log.info("----> Hello, mere mortal.");
	        return; 
	    }

	    // �����û��Ƿ�߱�ĳһ����Ϊ. ���� Subject �� isPermitted() ������ 
	    if (currentUser.isPermitted("lightsaber:weild")) {
	        log.info("----> You may use a lightsaber ring.  Use it wisely.");
	    } else {
	        log.info("Sorry, lightsaber rings are for schwartz masters only.");
	    }
	    
	    if (currentUser.isPermitted("admin:delete:zhangsan")) {
	        log.info("----> yesyesyesyes");
	    } else {
	        log.info("----> nonononono");
	    }

	    // �����û��Ƿ�߱�ĳһ����Ϊ. 
	    if (currentUser.isPermitted("user:delete:zhangsan")) {
	        log.info("----> You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  " +
	                "Here are the keys - have fun!");
	    } else {
	        log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
	    }

	    // ִ�еǳ�. ���� Subject �� Logout() ����. 
	    System.out.println("---->" + currentUser.isAuthenticated());
	    
	    currentUser.logout();
	    
	    System.out.println("---->" + currentUser.isAuthenticated());

	    //System.exit(0);
	}

}
