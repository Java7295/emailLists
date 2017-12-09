package com.email.autopdls;

import java.util.Vector;

import javax.naming.NameNotFoundException;
import javax.naming.directory.*;
//import javax.naming.directory.Attributes;
//import javax.naming.directory.BasicAttribute;
//import javax.naming.directory.BasicAttributes;
//import javax.naming.directory.ModificationItem;

public class EditGroups
{
	String cn,locality;
	String emailOnly="FALSE",hide="FALSE",cTime,cBy="AutoPDL",modifiedBy="AutoPDL";
	BasicAttribute attrMember,attrObjectClass,attrEmail,attrLoc,attrDesc,attrCn,attrName,attrEmailOnly,attrHide,acreatedBy,acreatedDate,attrAdminACL,amodBy,amodTime,aSendACL;
	LdapUtils ldapUtils;
	Attributes attrs;
	ModificationItem modMem,modBy,modTime;
	Attribute attrMem;
	int i,mi;	
	
	public EditGroups() {
		ldapUtils=new LdapUtils();
		attrObjectClass=new BasicAttribute(EmailUtils.OBJECT_CLASS);
		attrObjectClass.add(EmailUtils.TOP);
		attrObjectClass.add(EmailUtils.EGROUP_CLASS);
		attrEmailOnly=new BasicAttribute(EmailUtils.EGROUP_EMAIL_ONLY,emailOnly);
		attrHide=new BasicAttribute(EmailUtils.EGROUP_HIDE,hide);
		acreatedBy=new BasicAttribute(EmailUtils.CREATEDBY,cBy);
		amodBy=new BasicAttribute(EmailUtils.MODIFIEDBY,modifiedBy);
 	}

	boolean insertGroup(DirContext ctx, String name,String location,Vector vMembers)throws Exception
	{
		attrs=new BasicAttributes();
		i=0;
		boolean isInserted=false;
		try{
			cn=name+EmailUtils.SUFFIX;
			locality=location;
			attrAdminACL=new BasicAttribute(EmailUtils.EGROUP_ADMIN_ACL);
			aSendACL=new BasicAttribute(EmailUtils.EGROUP_SEND_ACL);
			if(name.startsWith("grpa-group1-")){
				attrAdminACL.add("cn=acl_group1_admin@company.com,ou=Groups,ou=Email,o=Company"); 
				aSendACL.add("cn=acl_group1_admin@company.com,ou=Groups,ou=Email,o=Company");
			}else if(name.startsWith("grpa-group2-")){
				attrAdminACL.add("cn=acl_group2_admin@company.com,ou=Groups,ou=Email,o=Company"); 
				aSendACL.add("cn=acl_group2_admin@company.com,ou=Groups,ou=Email,o=Company");
			}else if(name.startsWith("grpa-group3-")){
				attrAdminACL.add("cn=acl_group3_admin@company.com,ou=Groups,ou=Email,o=Company"); 
				aSendACL.add("cn=acl_group3_admin@company.com,ou=Groups,ou=Email,o=Company");
			}else if(name.startsWith("grpa-group4")){
				attrAdminACL.add("cn=acl_group4_admin@company.com,ou=Groups,ou=Email,o=Company"); 
				aSendACL.add("cn=acl_group4_admin@company.com,ou=Groups,ou=Email,o=Company");
			}
			attrCn=new BasicAttribute(EmailUtils.EGROUP_CN,cn);
			attrName=new BasicAttribute(EmailUtils.EGROUP_FULLNAME,name);
			attrEmail=new BasicAttribute(EmailUtils.EGROUP_EMAIL,cn);
			attrLoc=new BasicAttribute(EmailUtils.EGROUP_LOCALITY,locality);
			attrDesc=new BasicAttribute(EmailUtils.EGROUP_DESC,name);
			acreatedDate=new BasicAttribute(EmailUtils.CREATEDDATE,Utils.getCurrentDate());
			amodTime=new BasicAttribute(EmailUtils.MODIFIEDATE,Utils.getCurrentDate());
			attrMember = new BasicAttribute(EmailUtils.EGROUP_MEMBER);
			attrs.put(attrObjectClass);
			attrs.put(attrCn);
			attrs.put(attrName);
			attrs.put(attrEmail);
			attrs.put(attrLoc);
			attrs.put(attrDesc);

			if (!name.startsWith("grpa-group5"))
			{
			attrs.put(attrAdminACL);
			attrs.put(aSendACL);
			}
			attrs.put(attrHide);     //no hide on GAL by default 
			attrs.put(attrEmailOnly);
			attrs.put(acreatedBy);
			attrs.put(acreatedDate);
			attrs.put(amodBy);
			attrs.put(amodTime);
			for(mi=0;mi<vMembers.size();mi++)
				attrMember.add((String)vMembers.elementAt(mi));
			attrs.put(attrMember);

			i=ldapUtils.insertAttributes(ctx, Utils.setDn(cn,EmailUtils.EGROUP_CTX),attrs);
			if(i==1) isInserted=true;
			else MessageLogger.writeLog("Unable to create a group:"+cn);
		}catch(Exception e){
			e.printStackTrace();
			MessageLogger.writeLog("Exceptions in EditGroups.java insertGroups():"+cn);
			throw e;
		}
		return isInserted;
	}

	boolean modifyMembersofGroup(DirContext ctx,String dn,Vector vMembers)throws Exception
	{
		attrMem=new BasicAttribute(EmailUtils.EGROUP_MEMBER);
		for(mi=0;mi<vMembers.size();mi++)
			attrMem.add((String)vMembers.elementAt(mi));
		modMem=ldapUtils.getModificationItem(attrMem);
		modBy=ldapUtils.getModificationItem(EmailUtils.MODIFIEDBY,modifiedBy);
		modTime=ldapUtils.getModificationItem(EmailUtils.MODIFIEDATE,Utils.getCurrentDate());
		ModificationItem []mods={modMem,modBy,modTime};
		return ldapUtils.modifingAttributes(ctx, dn,mods);
	}
	
	
	boolean insertAddrGroup(DirContext ctx, String name,String location,Vector vMembers)throws Exception
	{
		attrs=new BasicAttributes();
		i=0;
		boolean isInserted=false;
		try{
			cn=name+EmailUtils.SUFFIX;
			locality=location;
			if ((name.indexOf("Manager")>0)||(name.indexOf("Division")>0)) {
				hide = "TRUE";
			}
			attrHide=new BasicAttribute(EmailUtils.EGROUP_HIDE,hide);
			attrCn=new BasicAttribute(EmailUtils.EGROUP_CN,cn);
			attrName=new BasicAttribute(EmailUtils.EGROUP_FULLNAME,name);
			attrEmail=new BasicAttribute(EmailUtils.EGROUP_EMAIL,cn);
			attrLoc=new BasicAttribute(EmailUtils.EGROUP_LOCALITY,locality);
			attrDesc=new BasicAttribute(EmailUtils.EGROUP_DESC,location+" Auto Distribution list");
			acreatedDate=new BasicAttribute(EmailUtils.CREATEDDATE,Utils.getCurrentDate());
			amodTime=new BasicAttribute(EmailUtils.MODIFIEDATE,Utils.getCurrentDate());
			attrMember = new BasicAttribute(EmailUtils.EGROUP_MEMBER);
			attrs.put(attrObjectClass);
			attrs.put(attrCn);
			attrs.put(attrName);
			attrs.put(attrEmail);
			attrs.put(attrLoc);
			attrs.put(attrDesc);
			attrs.put(attrHide);
			attrs.put(attrEmailOnly);
			attrs.put(acreatedBy);
			attrs.put(acreatedDate);
			attrs.put(amodBy);
			attrs.put(amodTime);
			for(mi=0;mi<vMembers.size();mi++)
				attrMember.add((String)vMembers.elementAt(mi));
			attrs.put(attrMember);

			i=ldapUtils.insertAttributes(ctx,Utils.setDn(cn,EmailUtils.EGROUP_CTX),attrs);
			if(i==1) isInserted=true;
			else MessageLogger.writeLog("Unable to create a group:"+cn);
		}catch(Exception e){
			MessageLogger.writeLog("##### Unable to create Email Group :"+cn);
			//throw e;
		}
		return isInserted;
	}

	boolean modifyAddrGroup(DirContext ctx, String name,String location,Vector vMembers)throws NameNotFoundException,Exception
	{
		attrs=new BasicAttributes();
		LdapUtils utils = new LdapUtils();
		i=0;
		boolean isInserted=false;
		try{
			cn=name+EmailUtils.SUFFIX;
		
			amodTime=new BasicAttribute(EmailUtils.MODIFIEDATE,Utils.getCurrentDate());
			attrMember = new BasicAttribute(EmailUtils.EGROUP_MEMBER);
			for(mi=0;mi<vMembers.size();mi++)
				attrMember.add((String)vMembers.elementAt(mi));
			
			ModificationItem modmembers =	utils.getModificationItem(attrMember);
			ModificationItem modBy =	utils.getModificationItem(amodBy);
			ModificationItem modDate =utils.getModificationItem(amodTime);

			ModificationItem[] mods = new ModificationItem[3];
			mods[0]=modmembers;
			mods[1]=modBy;
			mods[2]=modDate;
			if( utils.modifingAttributes(ctx, Utils.setDn(cn,EmailUtils.EGROUP_CTX), mods))
			 isInserted=true;
			
		}catch(NameNotFoundException ne){
			throw ne;  //need to throw ne to calling class to force insertAddrGroup in ResetGroups.java
		}
		catch(Exception e){
			MessageLogger.writeLog("Exception: ##### Unable to modify Email Group :"+cn);
			throw e;
		}
		return isInserted;
	}

	boolean insertAddrGroup_ACL(DirContext ctx, String name,String location,Vector vMembers)throws Exception
	{
		attrs=new BasicAttributes();
		i=0;
		boolean isInserted=false;
		try{
			cn=name+EmailUtils.SUFFIX;
			locality=location;
			attrAdminACL=new BasicAttribute(EmailUtils.EGROUP_ADMIN_ACL);
			aSendACL=new BasicAttribute(EmailUtils.EGROUP_SEND_ACL);
			String aclName="acl_"+location+"_admin";
			String aclCn=aclName+EmailUtils.SUFFIX;
			String aclDn="cn=acl_"+location+"_admin@company.com,ou=Groups,ou=Email,o=Company";
			MessageLogger.writeLog("Trying to verify isGroupExists: "+aclCn+" Result:"+isGroupExists(ctx,aclCn));
			if(isGroupExists(ctx,aclCn)){
				attrAdminACL.add(aclDn); 
				aSendACL.add(aclDn);
			}else	MessageLogger.writeLog("Sending mail to Email Admin");
			attrCn=new BasicAttribute(EmailUtils.EGROUP_CN,cn);
			attrName=new BasicAttribute(EmailUtils.EGROUP_FULLNAME,name);
			attrEmail=new BasicAttribute(EmailUtils.EGROUP_EMAIL,cn);
			attrLoc=new BasicAttribute(EmailUtils.EGROUP_LOCALITY,locality);
			attrDesc=new BasicAttribute(EmailUtils.EGROUP_DESC,location+" Auto Distribution list");
			acreatedDate=new BasicAttribute(EmailUtils.CREATEDDATE,Utils.getCurrentDate());
			amodTime=new BasicAttribute(EmailUtils.MODIFIEDATE,Utils.getCurrentDate());
			attrMember = new BasicAttribute(EmailUtils.EGROUP_MEMBER);
			attrs.put(attrObjectClass);
			attrs.put(attrCn);
			attrs.put(attrName);
			attrs.put(attrEmail);
			attrs.put(attrLoc);
			attrs.put(attrDesc);
			attrs.put(attrAdminACL);
			attrs.put(aSendACL);
			attrs.put(attrHide);
			attrs.put(attrEmailOnly);
			attrs.put(acreatedBy);
			attrs.put(acreatedDate);
			attrs.put(amodBy);
			attrs.put(amodTime);
			for(mi=0;mi<vMembers.size();mi++)
				attrMember.add((String)vMembers.elementAt(mi));
			attrs.put(attrMember);

			i=ldapUtils.insertAttributes(ctx, Utils.setDn(cn,EmailUtils.EGROUP_CTX),attrs);
			if(i==1) isInserted=true;
			else MessageLogger.writeLog("Unable to create a group:"+cn);
		}catch(Exception e){
			throw e;
		}
		return isInserted;
	}

	
	
boolean isGroupExists(DirContext ctx,String email)throws Exception
{
	boolean isGroup=false;
	LdapUtils ldapUtils=new LdapUtils();
	String []reqAttr={EmailUtils.EGROUP_FULLNAME,EmailUtils.EGROUP_EMAIL};
	Vector vec=ldapUtils.search_ONE(ctx,reqAttr,"mail="+email,EmailUtils.EGROUP_CTX);
	if(vec.size()>0) isGroup=true;
	return isGroup;
}
}