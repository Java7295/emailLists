package com.email.autopdls;

import java.util.Hashtable;
import javax.naming.directory.*;
import javax.naming.*;

public class LdapConnection {
	private  Hashtable env = new Hashtable();
	private  boolean INITIALIZED = false;
	final long	MAX_OBJECT_COUNT	=	600000L;
	final int 	MAX_QUERY_TIME		=	50000000;
	private  String ldapHost, ldapLogin, ldapPwd;
	public  DirContext ctx;
	public LdapConnection() {
		ldapHost = Utils.HOST;
		ldapLogin = Utils.BINDID;
		ldapPwd = Utils.BINDPW;
	}

	public DirContext getConnection()throws Exception{
		try {
			getContextEnviroment();
		}catch(Exception e){
			throw e;
		}
		return ctx;
	}
	
	private void setContextEnv() {
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":389/");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, ldapLogin);
		env.put(Context.SECURITY_CREDENTIALS, ldapPwd);
	}

	private  void getDefaultInitContext() throws Exception{
		try {
			setContextEnv();
			ctx = new InitialDirContext(env);
			INITIALIZED = true;
		} catch (NamingException ex) {
			ex.printStackTrace();
			INITIALIZED = false;
			throw ex;
		}
	}

	public DirContext getContextEnviroment() throws Exception{
		try{
		getDefaultInitContext();
		}catch(Exception e){
			throw e;
		}
		return ctx;
	}
	
	public SearchControls getSearchSubtreeControls(String[] reqAttr)
	{
		SearchControls sc = new SearchControls();
		sc.setCountLimit(MAX_OBJECT_COUNT);
		sc.setTimeLimit(MAX_QUERY_TIME);
		sc.setReturningAttributes(reqAttr);
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		return sc;
	}
	
	public SearchControls getSearchOnelevelControls(String[] reqAttr){
		SearchControls sc = new SearchControls();
		sc.setCountLimit(MAX_OBJECT_COUNT);
		sc.setTimeLimit(MAX_QUERY_TIME);
		sc.setReturningAttributes(reqAttr);
		sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		return sc;
	}
	
	public SearchControls getSearchSubtreeControls_OneLevel(String[] reqAttr) {
		SearchControls sc = new SearchControls();
		sc.setCountLimit(MAX_OBJECT_COUNT);
		sc.setTimeLimit(MAX_QUERY_TIME);
		sc.setReturningAttributes(reqAttr);
		sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		return sc;
	}
	
} // End of LdapConnection
