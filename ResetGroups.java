package com.email.autopdls;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

public class ResetGroups
{
	static String LogPath;
	static String  PropPath;
	static String flag;

	public static void main(String args[]) {
		LogPath=args[0];
		PropPath=args[1];
		flag=args[2];
		
		if("AUTOPDL".equals(flag)){
			MessageLogger logger=new MessageLogger(LogPath+"LogResetGroups"+Utils.getCurrentDate()+".log");	
		}else if ("MGRLEVEL".equals(flag)){
			MessageLogger logger=new MessageLogger(LogPath+"LogResetManagerGroups"+Utils.getCurrentDate()+".log");
		}
		
		MessageLogger.writeLog("----------------------------------------------------");
		MessageLogger.writeLog("Start Time:"+Utils.getCurrentDate());
		MessageLogger.writeLog("----------------------------------------------------");
		MessageLogger.writeLog("The flag passed is :"+flag);

		//load properties file
		Properties properties=new Properties();
		try{
			properties.load(new FileInputStream(PropPath+"creatingGroups.properties"));
			properties.load(new FileInputStream(PropPath+"divCodes.properties"));
		}catch(Exception e){
			MessageLogger.writeStackTrace(e);
		}
		//get properties
		Utils.HOST=properties.getProperty("HOST");
		Utils.BINDID=properties.getProperty("BINDID");
		Utils.BINDPW=properties.getProperty("BINDPW");
		
		System.out.println("Connecting to " + Utils.HOST);
		MessageLogger.writeLog("Connecting to " + Utils.HOST);
		
		ResetGroups rs=new ResetGroups();
		
		//get LDAP connection
		LdapConnection lc = new LdapConnection();  
		DirContext ctx = null;
		try {
			ctx = lc.getContextEnviroment();
			MessageLogger.writeLog("Got ctx Context Enviroment");
		} catch (Exception el) {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					MessageLogger.writeLog("Exception in closing the InitialContext: " + e.toString());
				}
			}
			MessageLogger.writeStackTrace(el);
		}
		
		//processing automatic groups
		try{
			if (ctx != null) {
				System.out.println("ctx is not null");
				if("AUTOPDL".equals(flag))
				{
					MessageLogger.writeLog("Processing AutoPDL groups....");
					rs.getCompAddrGroups(ctx);  	//update groups as CompnayAddress-all 
					rs.getEmplGrpGroups(ctx);   	//update groups as EmployeeGroup-all
					rs.getMgrLevelGroups(ctx);      //update groups as ManagerLevel-all
					rs.getDivisionCodeGroups(ctx);	//update groups as DivisionCode-all
					rs.getDivisionGroups(ctx,properties); 	//update groups for operational divisions ('56', '62', '66', '77')
				}
				else if ("MGRLEVEL".equals(flag))
				{
					MessageLogger.writeLog("Processing MGRLEVEL(1 to 5) groups....");
					rs.getMgrLevel5Groups(ctx);
				} else{
					MessageLogger.writeLog("No flag passed. Not processing any groups.");
				}
			} else {
				MessageLogger.writeLog("ctx is null");
			}
					
			//close connection
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException ne) {
					MessageLogger.writeLog("Exception in closing the InitialContext: " + ne.toString());
				}
			}
		}catch(Exception exe){
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException ne) {
					MessageLogger.writeLog("Exception in closing the InitialContext: " + ne.toString());
				}
			}
			MessageLogger.writeStackTrace(exe);
		}
		
		MessageLogger.writeLog("----------------------------------------------------");
		MessageLogger.writeLog("End Time:"+Utils.getCurrentDate());
		MessageLogger.writeLog("----------------------------------------------------");
	}
	
	public synchronized boolean getDivisionGroups(DirContext ctx, Properties properties){
		boolean isDivisionGroupsUpdated=false;
		LdapUtils ldapUtils=new LdapUtils();
		EditGroups ig=new EditGroups();
		MessageLogger.writeLog("----------------------------------------------------");
		try {
			String divCodes[]={"62","56","77","66"};
			String divCodeName="",divCode="",empGroup="",location="";
		
			Vector vEmpGroups=ldapUtils.getAllBindings(ctx, EmailUtils.EMPG_GRP);  // get all employee groups like IFN,FA,ADM
			Vector vLocations=ldapUtils.getAllBindings(ctx, EmailUtils.LOC_GRP);   // get all location like HKG, WHQ, SFO
			//System.out.println(" got all locations" + vLocations);
			Vector memDiv,memEmpGs,memLocs,memDiv_EmpG,memDiv_EmpG_Loc;
			Vector vD_E_L_Groups,vD_E_ALL_Groups;
			Vector exist_Mem_DEGL;
	
			Attributes attrs;
			int szD_E_L=0,szExD_E_L=0;
			String name="";
			String dn="";
			
			for(int x=0;x<divCodes.length;x++) { // 1st loop on divCode 77, 56, 62, 66
				divCode=divCodes[x];
				MessageLogger.writeLog("Processing Division Code: "+divCode + " from " + Utils.getCurrentDate());
				MessageLogger.writeLog("----------------------------------------------------");
				divCodeName=properties.getProperty(divCode);
				//System.out.println("divCodeName is" + divCodeName);
				if(Utils.isNull(divCodeName)){  
					divCodeName=divCode;
				}
				// get all members in divCode
				memDiv=ldapUtils.getAttributeVecValues((ldapUtils.getAttributes(ctx, Utils.setDn(divCode,EmailUtils.DIV_GRP))),EmailUtils.GROUPS_EQ);
				//MessageLogger.writeLog("09 Got "+ memDiv.size()+ " members in Division Code: "+divCode);
			
				vD_E_ALL_Groups=new Vector();
		
				if(memDiv.size()>0)
					//Loop for Empl Groups
					for(int y=0;y<vEmpGroups.size();y++) {
					//for(int y=0;y<1;y++) {
						empGroup=((String)vEmpGroups.elementAt(y)).substring(3);
						//empGroup = "ADM";
						memEmpGs=ldapUtils.getAttributeVecValues((ldapUtils.getAttributes(ctx, Utils.setDn(empGroup,EmailUtils.EMPG_GRP))),EmailUtils.GROUPS_EQ);
						//MessageLogger.writeLog("10 Got " +memEmpGs.size()+" members in Empl Group: "+empGroup+ " in Division Code: "+divCode);
						if(memEmpGs.size()>0) {
							memDiv_EmpG=new Vector();
							for(int a=0;a<memDiv.size();a++)
								if(memEmpGs.contains(memDiv.elementAt(a)))  //if there are common member in the Empl Grp and Division add to member list
									memDiv_EmpG.add(memDiv.elementAt(a));

							vD_E_L_Groups=new Vector();
							//Loop for locations
							for(int z=0;z<vLocations.size();z++) {
								location=((String)vLocations.elementAt(z)).substring(3);
								
								memLocs=ldapUtils.getAttributeVecValues((ldapUtils.getAttributes(ctx, Utils.setDn(location,EmailUtils.LOC_GRP))),EmailUtils.GROUPS_EQ);
								//MessageLogger.writeLog("12 Got "+memLocs.size()+" members in Location: "+location+ " in Empl Group: "+empGroup+ " in Division Code: "+divCode);
								if(memLocs.size()>0){
									memDiv_EmpG_Loc=new Vector();
									for(int a=0;a<memLocs.size();a++)
										if(memDiv_EmpG.contains(memLocs.elementAt(a)))
											memDiv_EmpG_Loc.add(memLocs.elementAt(a)); // get memebers in the loc, the empl group, and the division
									szD_E_L=memDiv_EmpG_Loc.size();
									name=""+divCodeName.toLowerCase()+"-"+empGroup.toLowerCase()+"-"+location.toLowerCase();
									
									//MessageLogger.writeLog("13 Group("+name+") has "+szD_E_L+" members");
									dn=Utils.setDn(name+EmailUtils.SUFFIX,EmailUtils.EGROUP_CTX); 
									attrs=ldapUtils.getAttributes(ctx, dn);
									
									if(attrs==null && szD_E_L>0)  {//Group does not exists & members are there --> insert a group
										//MessageLogger.writeLog("14 Group "+name+" does not exits in ou=Email but members are there: "+szD_E_L);
										if(ig.insertGroup(ctx, name,location,memDiv_EmpG_Loc)){
											vD_E_L_Groups.add(dn);
											MessageLogger.writeLog("D1A Group "+name+" inserted");
										}else {
											MessageLogger.writeLog("D1B #####Unable to insert group:"+name);
										}
									}else if(attrs!=null && (!(szD_E_L>0)) ){  // Group exists  & members are not there --> delete the existing group
										//MessageLogger.writeLog("17 Group "+name+" exists in ou=Email but members are not there: "+szD_E_L);
										if(ldapUtils.deleteAtrributes(ctx, dn)) {
											MessageLogger.writeLog("D1C Existing group "+name+" is deleted");
										} else {
											MessageLogger.writeLog("D1D #####Unable to delete group:"+name);
										}
									}else if(attrs!=null && (szD_E_L>0)){ //Group exists & Members exists  Verify & Modify the group
										exist_Mem_DEGL=ldapUtils.getAttributeVecValues(attrs,EmailUtils.EGROUP_MEMBER_ONLY);
										szExD_E_L=exist_Mem_DEGL.size();
										//MessageLogger.writeLog("21 Group("+name+ ") exists in ou=email and the member size is "+szExD_E_L);
										// To Verify & ModifyGroup
										if(szExD_E_L==szD_E_L){
											vD_E_L_Groups.add(dn);
											for(int a=0;a<szExD_E_L;a++) {
												if(! memDiv_EmpG_Loc.contains(exist_Mem_DEGL.elementAt(a))) { //Modify the members //"="+szExD_E_L+":"+szD_E_L+
													//MessageLogger.writeLog("22 There are differences in existing and new members.");  
													if(ig.modifyMembersofGroup(ctx, dn,memDiv_EmpG_Loc)) {
														MessageLogger.writeLog("D1E Members of the group:"+dn+" is modified.");
													} else {
														MessageLogger.writeLog("D1F #####Exception in Modifying the members of the group:"+dn);
													}
												}	
											}
										}else {
											vD_E_L_Groups.add(dn);
											if(ig.modifyMembersofGroup(ctx, dn,memDiv_EmpG_Loc)) {
												MessageLogger.writeLog("D1G Member size is different and group("+dn+") is modified" );
											}else {
												MessageLogger.writeLog("D1H #####Exception in Modifying the members of the group:"+dn);
											}
										}
									}else{
										MessageLogger.writeLog("D1I Group("+dn+") not found in ou=Email and no member in ou=Groups");
									} //end-if
								} //end for locations int z=0;z<vLocations.size();z++)
							}//end if on memEmpGs.size()>0
							if(vD_E_L_Groups.size()>0){
								vD_E_ALL_Groups=update_all_groups(ctx, vD_E_L_Groups,divCodeName,empGroup,vD_E_ALL_Groups);  //delete and insert grp-division-emplgroup-all
							}else {
								//MessageLogger.writeLog("D1J Group("+"grp-"+divCodeName.toLowerCase()+"-"+empGroup.toLowerCase()+"-all" + ") memeber size is 0");
								MessageLogger.writeLog("D1J Group("+""+divCodeName.toLowerCase()+"-"+empGroup.toLowerCase()+"-all" + ") memeber size is 0");
							}
						} //end for empl groups (int y=0;y<vEmpGroups.size();y++)and (memDiv.size()>0)
					} //end if (memDiv.size()>0)
					if(vD_E_ALL_Groups.size()>0){
						update_all_all_groups(ctx, divCodeName,vD_E_ALL_Groups);  //delete and insert grp-division-all
					} else { 
						MessageLogger.writeLog("D1K The no. of members in Location:ALL Emp Group:ALL For Div Code:"+divCode+"("+divCodeName+") are...0\n");
					} 
					isDivisionGroupsUpdated=true;
				}//end of divisions (int x=0;x<divCodes.length;x++) for loop
			}catch(Exception exe){
				MessageLogger.writeStackTrace(exe);
			}
		return isDivisionGroupsUpdated;
	}

	public void sendEmail(){
		
	}
	
	public synchronized Vector update_all_groups(DirContext ctx, Vector vD_E_L_Groups,String divCodeName,String empGroup,Vector vD_E_ALL_Groups)throws Exception
	{
		String name,dn,location;
		boolean isDeleted=false;
		LdapUtils ldapUtils=new LdapUtils();
		EditGroups ig=new EditGroups();

		name=""+divCodeName.toLowerCase()+"-"+empGroup.toLowerCase()+"-all";
		location="000";
		dn=Utils.setDn(name+EmailUtils.SUFFIX,EmailUtils.EGROUP_CTX);
		isDeleted=ldapUtils.deleteAtrributes(ctx, dn);
		this.wait(1800);
		if(isDeleted){
			if(ig.insertGroup(ctx, name,location,vD_E_L_Groups)){
				vD_E_ALL_Groups.add(Utils.setDn(name+EmailUtils.SUFFIX,EmailUtils.EGROUP_CTX));
				MessageLogger.writeLog("33 Group("+name+") has been deleted and inserted and member size is "+vD_E_L_Groups.size());
				
			}else MessageLogger.writeLog("34 #####Unable to Recreate group:"+name);
		}else MessageLogger.writeLog("35 #####Unable to delete group:"+name);

		return vD_E_ALL_Groups;
	}

	public synchronized void update_all_all_groups(DirContext ctx, String divCodeName,Vector vD_E_ALL_Groups)throws Exception
	{
		String name,dn,location;
		boolean isDeleted=false,isUpdated=false;
		LdapUtils ldapUtils=new LdapUtils();
		EditGroups ig=new EditGroups();
		name=""+divCodeName.toLowerCase()+"-all";
		location="000";
		dn=Utils.setDn(name+EmailUtils.SUFFIX,EmailUtils.EGROUP_CTX);
		isDeleted=ldapUtils.deleteAtrributes(ctx, dn);
		this.wait(1800);
		if(isDeleted){
			isUpdated=ig.insertGroup(ctx, name,location,vD_E_ALL_Groups);
			if(!isUpdated) MessageLogger.writeLog("36 #####Unable to Recreate group:"+name);
			else {
				MessageLogger.writeLog("37 Group("+name+") has been deleted and inserted and the member size is "+vD_E_ALL_Groups.size());
			}
		}else {
			MessageLogger.writeLog("38 #####Unable to delete group:"+name);
		}

	}

	boolean isMemberofList(String []list,String check){
		boolean isMember=false;
		for(int i=0;i<list.length;i++){
			if(check.equalsIgnoreCase(list[i]))		return true;
		}
		return isMember;
	}

	public synchronized boolean getCompAddrGroups(DirContext ctx){
		String name="";
		Vector memAddr;
		boolean isCompAddrUpdated=false;
		String dn="",addrCode="",location="";
		EditGroups eg=new EditGroups();
		LdapUtils ldapUtils=new LdapUtils();
		String []contractReqAttrs={"cn"};
		try{
			MessageLogger.writeLog("Processing CompanyAddress-ALL from " + Utils.getCurrentDate());
			MessageLogger.writeLog("----------------------------------------------------");
			//get all company addresses
			Vector vCompAddr=ldapUtils.getAllBindings(ctx, EmailUtils.COM_ADDR);
			
			if(vCompAddr.size()>0) {
				MessageLogger.writeLog("A1A Got "+vCompAddr.size()+" Company Addresses");
				for(int y=0;y<vCompAddr.size();y++){
					addrCode=((String)vCompAddr.elementAt(y)).substring(3);
						dn=Utils.setDn(addrCode,EmailUtils.COM_ADDR);
						memAddr=ldapUtils.getAttributeVecValues(ldapUtils.getAttributes(ctx, dn),EmailUtils.GROUPS_EQ);
						name=""+addrCode+"-all";
						location=addrCode;
						Vector contractorMem=ldapUtils.search_ONE_CN(ctx, contractReqAttrs,"Address="+addrCode,EmailUtils.affPeople);
						if(contractorMem.size()>0) {
							MessageLogger.writeLog("A1B Got contractor member for:" +addrCode + "and there are " + contractorMem.size());
							for(int z=0;z<contractorMem.size();z++) {
								memAddr.add(contractorMem.elementAt(z));
							}
						}
						if(memAddr.size()>0) {
							boolean isModify=false;
							try{
								isModify=eg.modifyAddrGroup(ctx, name,location,memAddr);
							}catch(NameNotFoundException ne){
								if(eg.insertAddrGroup(ctx,name,location,memAddr)){
									MessageLogger.writeLog("A1C Group :"+name+" inserted successfully ");
								}
							}
							if(isModify){
								MessageLogger.writeLog("A1D Group :"+name+" modified successfully ");
							}
						
						}else {
							if(eg.isGroupExists(ctx,name+"@company.com")){
								String grpToDel=Utils.setDn(name+"@company.com",EmailUtils.EGROUP_CTX);
								ldapUtils.deleteAtrributes(ctx, grpToDel);
								MessageLogger.writeLog("A1E Group :"+grpToDel+" is deleted successfully ");
							}
						}
				}// end of for loop
			}// end of if(vCompAddr.size()>0)
			isCompAddrUpdated=true;
		}catch(Exception exe){
			exe.printStackTrace();
		}
		return isCompAddrUpdated;
	}
	
	public synchronized void getEmplGrpGroups(DirContext ctx){
		String name="";
		Vector memAddr;
		String dn="",emplGrp="";
		EditGroups eg=new EditGroups();
		LdapUtils ldapUtils=new LdapUtils();
		String []excusionGrpList={"00"};
		
		try{
			MessageLogger.writeLog("Processing EmployeeGroup-ALL from " + Utils.getCurrentDate());
			MessageLogger.writeLog("----------------------------------------------------");
			//get all employee groups
			Vector vEmplGrp=ldapUtils.getAllBindings(ctx, EmailUtils.EMPL_GRP);
			
			if(vEmplGrp.size()>0) {
				MessageLogger.writeLog("E1A Got "+vEmplGrp.size()+" Employee Groups");
				for(int y=0;y<vEmplGrp.size();y++){
					emplGrp=((String)vEmplGrp.elementAt(y)).substring(3);
					if(!isMemberofList(excusionGrpList,emplGrp)){  
						dn=Utils.setDn(emplGrp,EmailUtils.EMPL_GRP);
						memAddr=ldapUtils.getAttributeVecValues(ldapUtils.getAttributes(ctx, dn),EmailUtils.GROUPS_EQ);
						name=""+emplGrp+"-all";
						
						System.out.println("The empl Group is  " + emplGrp);
						if(memAddr.size()>0) {
							boolean isModify=false;
							try{
								isModify=eg.modifyAddrGroup(ctx,name,emplGrp,memAddr);
							}catch(NameNotFoundException ne){
								MessageLogger.writeLog("E1B Exception: email group " + name + " does not exist in ou=Email");
								if(eg.insertAddrGroup(ctx,name,emplGrp,memAddr)){
									MessageLogger.writeLog("E1C Group: "+name+" inserted successfully ");
								}
							}
							if(isModify){
								MessageLogger.writeLog("E1D Group: "+name+" modified successfully ");
							}
						}else {
							if(eg.isGroupExists(ctx,name+"@company.com")){
								String grpToDel=Utils.setDn(name+"@company.com",EmailUtils.EGROUP_CTX);
								ldapUtils.deleteAtrributes(ctx, grpToDel);
								MessageLogger.writeLog("E1E Group: "+grpToDel+" is deleted successfully ");
							}
						} //end of memAddr.size()>0  
					}//end of !isMemberofList(excusionGrpList,addrCode)
				}// end of for loop
			}// end of if(vEmplGrp.size()>0)
		}catch(Exception exe){
			exe.printStackTrace();
		}
	}

	public synchronized void getMgrLevel5Groups(DirContext ctx){
		String name="";
		Vector memAddr;
		String dn="",mgrLvl="";
		EditGroups eg=new EditGroups();
		LdapUtils ldapUtils=new LdapUtils();
				
		try{
			MessageLogger.writeLog("----------------------------------------------------");
			Vector vMgrLvlGrp=ldapUtils.getAllBindings(ctx, EmailUtils.MGRLVL_GRP);
			ArrayList mgrLvlResetList = new ArrayList();
			mgrLvlResetList.add("1");
			mgrLvlResetList.add("1A");
			mgrLvlResetList.add("1B");
			mgrLvlResetList.add("1C");
			mgrLvlResetList.add("2");
			mgrLvlResetList.add("2A");
			mgrLvlResetList.add("3");
			mgrLvlResetList.add("3A");
			mgrLvlResetList.add("4");
			mgrLvlResetList.add("4A");
			mgrLvlResetList.add("4B");
			mgrLvlResetList.add("5");
			
			if(vMgrLvlGrp!=null && vMgrLvlGrp.size()>0) {
				MessageLogger.writeLog("Manager level groups: "+vMgrLvlGrp);
				MessageLogger.writeLog("M1A Got "+vMgrLvlGrp.size()+" Manager Level Groups");
		
				for(int y=0;y<vMgrLvlGrp.size();y++)
				{
					mgrLvl=((String)vMgrLvlGrp.elementAt(y)).substring(3);
					if(mgrLvlResetList.contains(mgrLvl)){
						dn=Utils.setDn(mgrLvl,EmailUtils.MGRLVL_GRP);
						memAddr=ldapUtils.getAttributeVecValues(ldapUtils.getAttributes(ctx, dn),EmailUtils.GROUPS_EQ);
						name="MgrLevel-"+mgrLvl+"-all";
						MessageLogger.writeLog("ManagerLevel Group "+name);
						System.out.println("ManagerLevel Group "+name);
						if(memAddr.size()>0) {
							boolean isModify=false;
							try{
								isModify=eg.modifyAddrGroup(ctx,name,mgrLvl,memAddr);
							}catch(NameNotFoundException ne){
								MessageLogger.writeLog("M1B Exception: email group " + name + " does not exist in ou=Email");
								if(eg.insertAddrGroup(ctx,name,mgrLvl,memAddr)){
									MessageLogger.writeLog("M1C Group :"+name+" inserted successfully ");
								}
							}
							if(isModify){
								MessageLogger.writeLog("M1D Group :"+name+" modified successfully ");
							}
						}else {
							if(eg.isGroupExists(ctx,name+"@company.com")){
								String grpToDel=Utils.setDn(name+"@company.com",EmailUtils.EGROUP_CTX);
								ldapUtils.deleteAtrributes(ctx, grpToDel);
								MessageLogger.writeLog("M1E Group :"+grpToDel+" is deleted successfully ");
							}
						} //end of memAddr.size()>0 
					}
				}// end of for loop
			}// end of if(vMgrLvlGrp.size()>0)
		}catch(Exception exe){
			exe.printStackTrace();
		}
	}

	public synchronized void getMgrLevelGroups(DirContext ctx){
		String name="";
		Vector memAddr;
		String dn="",mgrLvl="";
		EditGroups eg=new EditGroups();
		LdapUtils ldapUtils=new LdapUtils();
				
		try{
			MessageLogger.writeLog("Processing ManagerLevel-ALL from " + Utils.getCurrentDate());
			MessageLogger.writeLog("----------------------------------------------------");
			//get all manager level groups
			Vector vMgrLvlGrp=ldapUtils.getAllBindings(ctx, EmailUtils.MGRLVL_GRP);
			
			if(vMgrLvlGrp.size()>0) {
				MessageLogger.writeLog("M1A Got "+vMgrLvlGrp.size()+" Manager Level Groups");
		
				for(int y=0;y<vMgrLvlGrp.size();y++){
				//for(int y=0;y<5;y++){
					mgrLvl=((String)vMgrLvlGrp.elementAt(y)).substring(3);
					  
						dn=Utils.setDn(mgrLvl,EmailUtils.MGRLVL_GRP);
						memAddr=ldapUtils.getAttributeVecValues(ldapUtils.getAttributes(ctx, dn),EmailUtils.GROUPS_EQ);
						name="MgrLevel-"+mgrLvl+"-all";
						System.out.println("The empl Group is  " + name);
						if(memAddr.size()>0) {
							boolean isModify=false;
							try{
								isModify=eg.modifyAddrGroup(ctx,name,mgrLvl,memAddr);
							}catch(NameNotFoundException ne){
								MessageLogger.writeLog("M1B Exception: email group " + name + " does not exist in ou=Email");
								if(eg.insertAddrGroup(ctx,name,mgrLvl,memAddr)){
									MessageLogger.writeLog("M1C Group :"+name+" inserted successfully ");
								}
							}
							if(isModify){
								MessageLogger.writeLog("M1D Group :"+name+" modified successfully ");
							}
						}else {
							if(eg.isGroupExists(ctx,name+"@company.com")){
								String grpToDel=Utils.setDn(name+"@company.com",EmailUtils.EGROUP_CTX);
								ldapUtils.deleteAtrributes(ctx, grpToDel);
								MessageLogger.writeLog("M1E Group :"+grpToDel+" is deleted successfully ");
							}
						} //end of memAddr.size()>0 
				}// end of for loop
			}// end of if(vMgrLvlGrp.size()>0)
		}catch(Exception exe){
			exe.printStackTrace();
		}
	}


}
