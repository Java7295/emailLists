package com.email.autopdls;

public interface EmailUtils {

//	Specific to Apllication
	  public static final String DIV_GRP="ou=Division,ou=Groups,o=Company";
	  public static final String EMPG_GRP="ou=EmployeeGroup,ou=Groups,o=Company";
	  public static final String LOC_GRP="ou=Location,ou=Groups,o=Company";
	  public static final String GROUPS_EQ="equivalentToMe";
	  public static final String SUFFIX="@company.com";
	  public static final String affPeople="ou=AffiliatedPeople, o=Company"; 
	  public static final String activePeople="ou=People, o=Company"; 

/* @@@@@ COMMON ATTRIBUTES @@@@ */
		public static final String TOP="top";
		public static final String OBJECT_CLASS="objectclass";
		public static final String CREATEDBY="EmailCreatedBy";
		public static final String CREATEDDATE="EmailCreatedTime";
		public static final String MODIFIEDBY="EmailLastModifiedBy";
		public static final String MODIFIEDATE="EmailLastModifiedTime";
		
/* Auto Distribution list of Company Address Code */
		public static final String COM_ADDR="ou=CompanyAddress, ou=Groups, o=Company";

/* Auto Distribution list of Department Code */
		public static final String COST_CENTER="ou=CostCenter,ou=Groups,o=Company";
		
/* Auto Distribution list of Employee Group Code */
		public static final String EMPL_GRP="ou=EmployeeGroup, ou=Groups, o=Company";
		public static final String MGRLVL_GRP="ou=ManagerLevel, ou=Groups, o=Company";
		public static final String DIVCD_GRP="ou=DivisionCode, ou=Groups, o=Company";
		
//	EmailAsset ATTRIBUTES
	public static final String ASSET_CTX="ou=Assets,ou=Email,o=Company";
	public static final String ASSET_CLASS="EmailAsset";
	public static final String ASSET_CN="cn";
	public static final String ASSET_NAME="fullName";
	public static final String ASSET_DESC="description";
	public static final String ASSET_EMAIL="mail";
	public static final String ASSET_LOCATION="EmailAssetLocation";
	public static final String ASSET_PASSWORD="EmailPassword";
	public static final String ASSET_HIDE="EmailHideFromAddressList";
	public static final String ASSET_SEND_ACL="EmailSendACL";
	public static final String ASSET_VIEW_ACL="EmailViewACL";
	
//	EmailGroup ATTRIBUTES
	public static final String EGROUP_CTX="ou=Groups,ou=Email,o=Company";
	public static final String EGROUP_CLASS="EmailGroup";
	public static final String EGROUP_CN="cn";
	public static final String EGROUP_FULLNAME="fullName";
	public static final String EGROUP_EMAIL="mail";
	public static final String EGROUP_DESC="description";
	public static final String EGROUP_LOCALITY="l";
	public static final String EGROUP_MEMBER="uniqueMember";
	//public static final String EGROUP_MEMBER="member";
	public static final String EGROUP_MEMBER_ONLY="member";
	public static final String EGROUP_EMAIL_ONLY="EmailOnly";
	public static final String EGROUP_HIDE="EmailHideFromAddressList";
	public static final String EGROUP_SEND_ACL="EmailSendACL";
	public static final String EGROUP_ADMIN_ACL="EmailAdminACL";

//	EmailBox ATTRIBUTES
	public static final String EBOX_CTX="ou=Mailboxes,ou=Email,o=Company";
	public static final String EBOX_CLASS="EmailBox";
	public static final String EBOX_CN="cn";
	public static final String EBOX_NAME="fullName";
	public static final String EBOX_EMAIL="mail";
	public static final String EBOX_DESC="description";
	public static final String EBOX_ADDRCODE="CompanyAddrCode";
//to be added to mailbox	
  	public static final String EBOX_HIDE="EmailHideFromAddressList";
	public static final String EBOX_PASSWORD="EmailPassword";
	public static final String EBOX_DISABLED="EmailAccountDisabledFlag";
	public static final String EBOX_LOCK="EmailAccountLockFlag";
	public static final String EBOX_SIZE="EmailBoxSize";
	public static final String EBOX_TIER="EmailBoxTier";
	public static final String EBOX_PROTECTED="EmailProtectedUserFlag";
	public static final String EBOX_WIRELESS="EmailWireless";
	public static final String EBOX_X400="EmailX400Address";
	public static final String EBOX_PLATFORM="EmailPlatform";
	public static final String EBOX_MIGRATION="EmailMigrationData";
	

// EmailContact ATTRIBUTES
  public static final String ECONTACT_CTX="ou=Contacts,ou=Email,o=Company";
  public static final String ECONTACT_CLASS="EmailContact";
	public static final String ECONTACT_FULLNAME="fullName";
	public static final String ECONTACT_GIVENNAME="givenName";
	public static final String ECONTACT_CN="cn";
	public static final String ECONTACT_EMAIL="mail";
	public static final String ECONTACT_INITIALS="initials";
	public static final String ECONTACT_SN="sn";
	public static final String ECONTACT_UID="uid";
	public static final String ECONTACT_GENERATION_QIFIER="generationQifier";
	public static final String ECONTACT_TYPE="EmailContactType";
	public static final String ECONTACT_LOGIN_DISABLED="loginDisabled";
	public static final String ECONTACT_HIDE="EmailHideFromAddressList";

//End of Ldap Schema

	public static int SUCCESS=1;
	public static int NOT_INSERTED=-1;
	public static int CTX_NOT_FOUND=-100;
	
//EmailPerson
  public static final String EPERSON_CN="cn";
  public static final String EPERSON_ADDRCODE="CompanyAddrCode";
  public static final String EPERSON_JOBDESC="JobCodeDesc";
  public static final String EPERSON_CTX="ou=EmailPerson,ou=Email,o=Company";
  public static final String EPERSON_EMAIL="mail";
  public static final String EPERSON_GIVENNAME="givenName";
  public static final String EPERSON_FULLNAME="fullName";
  public static final String EPERSON_HIDE="EmailHideFromAddressList";
  public static final String EPERSON_PASSWORD="EmailPassword";
  public static final String EPERSON_DISABLED="EmailAccountDisabledFlag";
  public static final String EPERSON_LOCK="EmailAccountLockFlag";
  public static final String EPERSON_SIZE="EmailBoxSize";
  public static final String EPERSON_TIER="EmailBoxTier";
  public static final String EPERSON_PROTECTED="EmailPortectedUserFlag";
  public static final String EPERSON_WIRELESS="EmailWireless";
  public static final String EPERSON_X400="EmailX400Address";
  	 
}