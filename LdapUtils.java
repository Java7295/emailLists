package com.email.autopdls;

import java.util.Vector;
import javax.naming.directory.*;
import javax.naming.*;

public class LdapUtils {

	/*
	 * 	To set the Distinguishable name
	 */
	public int insertAttributes(DirContext ctx, String dn, Attributes attrs) throws Exception {
		int i = 0;
		
		try {
			if (ctx != null) {
				ctx = ctx.createSubcontext(dn, attrs);
				i = EmailUtils.SUCCESS;
			} else {
				i = EmailUtils.CTX_NOT_FOUND;
				MessageLogger.writeLog("Not able to get Context......");
			}
		} catch (NameAlreadyBoundException nbe) {
			i = EmailUtils.NOT_INSERTED;
			MessageLogger.writeStackTrace(nbe); //if the name is already bound
			//throw nbe;
		} catch (InvalidAttributesException ie) {
			i = EmailUtils.NOT_INSERTED;
			MessageLogger.writeStackTrace(ie);
			//throw ie;
			//if attrs does not contain all the mandatory attributes required for creation
		} catch (InvalidNameException ine) {
			i = EmailUtils.NOT_INSERTED;
			MessageLogger.writeLog("Exception in insertAttributes(..):Dn In LdapUtils:" + dn);
			MessageLogger.writeStackTrace(ine); //if a naming exception is encountered
			//throw ine;
		} catch (NamingException ne) {
			i = EmailUtils.NOT_INSERTED;
			MessageLogger.writeStackTrace(ne);//if a naming exception is encountered
			//throw ne;
		} catch (Exception e) {
			i = EmailUtils.NOT_INSERTED;
			MessageLogger.writeStackTrace(e);
			//throw e;
		}
		return i;
	}
	
	public boolean modifingAttributes(DirContext ctx, String dn, ModificationItem[] mods)  throws Exception {
		boolean modified = false;
		int i=0;
		try {
			for(i=0;i<mods.length;i++)
			if(mods[i]==null) {
				MessageLogger.writeLog("One of the the Modification Item is null");
				return false;
			}
			ctx.modifyAttributes(dn, mods);
			modified = true;
		}catch(javax.naming.directory.InvalidAttributeValueException iave){
			MessageLogger.writeLog("Exception in modifingAttributes(..):InvalidAttributeValueException at "+i+" in "+mods[i]);
			MessageLogger.writeStackTrace(iave);
	
		}catch(NameNotFoundException ne){
			throw ne; //need to throw to calling class to force insertAddrGroup in ResetGroups.java
		}catch (Exception e) {
			MessageLogger.writeStackTrace(e);
			
		}
		return modified;
	}
	
	public boolean deleteAtrributes(DirContext ctx, String dn) throws Exception  {
		boolean deleted = false;
		
		try {
			ctx.destroySubcontext(dn);
			deleted = true;
		} catch (Exception e) {
			MessageLogger.writeStackTrace(e);
			//throw e;
		}
		
		return deleted;
	}

	public Vector getAllBindings(DirContext ctx, String searchCtx) throws Exception  {
		Vector v = new Vector();
	
		try {
			NamingEnumeration ne = ctx.listBindings(searchCtx);
			int i = 0;
			while (ne.hasMoreElements()) {
				String b = ((Binding) ne.nextElement()).getName();
				v.addElement(b);
			}
			
		} catch (Exception e) {
			MessageLogger.writeStackTrace(e);
		} finally {
		}
		return v;
	}
	

	public Attributes getAttributes(DirContext ctx, String dn)  throws Exception {
		Attributes attrs = null;
		
		try {
			attrs = ctx.getAttributes(dn);
		}catch(NameNotFoundException nnfe){
			MessageLogger.writeLog("NameNotFoundException: No entry found for:"+dn+" and attributes is:"+attrs); 
			attrs=null;
		}catch(NamingException ne){
			MessageLogger.writeLog("NamingException: NDS error: inconsistent database error"); 
			MessageLogger.writeStackTrace(ne);
			attrs=null;
			throw ne;  //throw exception to stop program processing due to inconsistent database error
		}catch (Exception e) {
			MessageLogger.writeStackTrace(e);
			attrs=null;
		}finally{
		}
		return attrs;
	}
	
	public String getAttributeValue(Attributes attrs, String name)  throws Exception {
		String value = "";
		try {
			Attribute attr = attrs.get(name);
			if (attr == null)
				MessageLogger.writeLog(name + ":Attribute not found!");
			else
				value = (String) attr.get();
		} catch (NamingException ne) {
			MessageLogger.writeStackTrace(ne);
		} finally {
		}
		return value;
	}


public String [] getAttributeValues(Attributes attrs,String name) throws Exception 
		{
			String values[]=null;
			int i=0;
			try{
				Attribute attr=attrs.get(name);
				if (attr == null){
					values[i]="";
				}else if(attr.size()>0){
					values=new String[attr.size()];
					NamingEnumeration e=attr.getAll();
					while(e.hasMore()){
						values[i]=(String)e.nextElement();
						i++;
					}
				}
			}catch(NamingException ne){
				MessageLogger.writeStackTrace(ne);
			}finally{
			}
			return values;
		}
		
	public Vector getAttributeVecValues(Attributes attrs,String name) throws Exception 
			{
				Vector values=new Vector();
				try{
					Attribute attr=attrs.get(name);
					if (attr == null){
					}else if(attr.size()>0){
						NamingEnumeration e=attr.getAll();
						while(e.hasMore()){
							values.add((String)e.nextElement());
						}
					}
				}catch(NamingException ne){
					MessageLogger.writeStackTrace(ne);
				}catch(Exception e){
					MessageLogger.writeStackTrace(e);
				}finally{
				}
				return values;
			}
		
	public ModificationItem getModificationItem(String name, String value)  throws Exception {
		if(value!=null){
		ModificationItem modDesc =
			new ModificationItem(
				DirContext.REPLACE_ATTRIBUTE,
				new BasicAttribute(name, value));
		return modDesc;
		}else{
			MessageLogger.writeLog("The Value of "+name+" is null");
			return null; 
		}
	}

	public ModificationItem getModificationItem(String name, boolean value)  throws Exception {
		ModificationItem modDesc =
			new ModificationItem(
				DirContext.REPLACE_ATTRIBUTE,
				new BasicAttribute(name, value));
		return modDesc;
		
	}

	public ModificationItem getModificationItem(Attribute attr) throws Exception  {
		if(attr!=null)
		return new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
		else{
			MessageLogger.writeLog("The Attribute is null");
			return null; 
		}
	}

	public ModificationItem getModificationItemADD(String name, String value)  throws Exception {
		if(value!=null){
			ModificationItem mod =
				new ModificationItem(
					DirContext.ADD_ATTRIBUTE,
					new BasicAttribute(name, value));
			return mod;
		}else return null;
	}

	
	public ModificationItem getModificationItemADD(Attribute attr) {
			if(attr!=null){
				ModificationItem mod =
					new ModificationItem(
						DirContext.ADD_ATTRIBUTE,attr);
				return mod;
			}else return null;
		}
	
	public ModificationItem getDeletionItem(String name) {
		ModificationItem modDel =
		new ModificationItem(
			DirContext.REMOVE_ATTRIBUTE,
			new BasicAttribute(name));
	return modDel;
	}
	
	public synchronized Vector search_SUB(
		DirContext ctx,
		String[] reqAttr,
		String filter,
		String searchBase)  throws Exception {
		LdapConnection la = new LdapConnection();
		
		Vector values=new Vector();
		try {
			SearchResult sr;
			Attributes as;
			SearchControls sc = la.getSearchSubtreeControls(reqAttr);
			NamingEnumeration nEnum = ctx.search(searchBase, filter, sc);
			while (nEnum.hasMore()) {
				sr = (SearchResult) nEnum.next();
				as = sr.getAttributes();
				if (as == null)	continue;
				else values.add(as);
			}
		} catch (NamingException ne) {
			MessageLogger.writeLog("Exception in search_SUB(..):When Getting attribute: " + ne);
			MessageLogger.writeStackTrace(ne);
		} catch (Exception ex) {
			MessageLogger.writeStackTrace(ex);
		}finally{
		}
		return values;
	}

	public synchronized Vector search_ONE(
			DirContext ctx,
			String[] reqAttr,
			String filter,
			String searchBase)  throws Exception {
			LdapConnection la = new LdapConnection();
			Vector values=new Vector();
			try {
				SearchResult sr;
				Attributes as;
				SearchControls sc = la.getSearchOnelevelControls(reqAttr);
				NamingEnumeration nEnum = ctx.search(searchBase, filter, sc);
				while (nEnum.hasMore()) {
					sr = (SearchResult) nEnum.next();
					as = sr.getAttributes();
					if (as == null)	continue;
					else values.add(as);
				}
			} catch (NamingException ne) {
				MessageLogger.writeLog("Exception in search_ONE(..):When getting attribute: " + ne);
				MessageLogger.writeStackTrace(ne);
			} catch (Exception ex) {
				MessageLogger.writeStackTrace(ex);
			}finally{
			}
			return values;
		}

	public synchronized Vector search_ONE_CN(
			DirContext ctx,
			String[] reqAttr,
			String filter,
			String searchBase)  throws Exception {
			LdapConnection la = new LdapConnection();
			
			Vector values=new Vector();
			try {
				SearchResult sr;
				Attributes as;
				SearchControls sc = la.getSearchOnelevelControls(reqAttr);
				NamingEnumeration nEnum = ctx.search(searchBase, filter, sc);
				String cn="";
				Attribute attr;
				while (nEnum.hasMore()) {
					sr = (SearchResult) nEnum.next();
					as = sr.getAttributes();
					if (as == null)	continue;
					else {
						attr = as.get("cn");
						if (attr == null) cn = null;
						else cn="cn=" + (String) attr.get() + "," + searchBase;
						values.add(cn);
					} 
				}
				
			} catch (NamingException ne) {
				MessageLogger.writeLog("Exception in search_ONE(..):When getting attribute: " + ne);
				MessageLogger.writeStackTrace(ne);
			} catch (Exception ex) {
				MessageLogger.writeStackTrace(ex);
			}finally{
			}
			return values;
		}

	public boolean isExists(DirContext ctx,String cn,String searchCtx,String attrName)throws Exception
	{
		boolean exists=false;
		LdapConnection la = new LdapConnection();
		
		NamingEnumeration nEnum;
		try{
			SearchResult sr;
			String []reqAttr={attrName};
			String filter="(&(cn="+cn.trim()+"))";
			Attributes as;
			String value[]=null;
			SearchControls sc = la.getSearchSubtreeControls_OneLevel(reqAttr);
			nEnum = ctx.search(searchCtx, filter, sc);
			if(nEnum!=null)
			while (nEnum.hasMore()) {
				sr = (SearchResult) nEnum.next();
				as = sr.getAttributes();
				if (as == null)
					return false; 
				else {
					value=getAttributeValues(as,attrName);
					if(!Utils.isNull(value))  
					return true; 
				}
			}
		}catch(Exception exe){
			MessageLogger.writeStackTrace(exe);
			throw exe;
		}
		nEnum.close();
		return exists;
	}

	public synchronized Vector search(
			DirContext ctx,
			String[] reqAttr,
			String filter,
			String searchBase) throws Exception {
			LdapConnection la = new LdapConnection();
			Vector values=new Vector();
			try {
				SearchResult sr;
				Attributes as;
				if(ctx!=null){
					SearchControls sc = la.getSearchSubtreeControls_OneLevel(reqAttr);
					NamingEnumeration nEnum = ctx.search(searchBase, filter, sc);
					int i=0;
					if(nEnum!=null)
					while (nEnum.hasMore()) {
						sr = (SearchResult) nEnum.next();
						as = sr.getAttributes();
						if (as == null)	continue;
						else values.add(as);
						i++;
					}
				}else System.err.println("Not able to get Context......");
			} catch (NamingException ne) {
				MessageLogger.writeStackTrace(ne);
			} catch (Exception ex) {
				MessageLogger.writeStackTrace(ex);
			}finally{
			}
			return values;
		}
}
