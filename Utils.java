package com.email.autopdls;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	
	static final int PORT = 389;  // Network port used to connect to eDirectory
	static String HOST ;
	static String BINDID ;
	static String BINDPW;
	static String mailServer;

//To set the Distinguishable name
	public static String setDn(String fileNo, String searchContext) {
		if( (!isNull(fileNo)) && (!isNull(searchContext)) )
			return "cn=" + fileNo + "," + searchContext;
		else return null;
	}

//	To check for Null String & Blank String
	 public static boolean isNull(String str){
			 boolean isNullString=false;
			 String str1="";
			 if(str==null) return true;
			 else if(str.trim().equals(str1)) return true;
			 else return isNullString;
	 }
	 
	private void wrtStr(BufferedOutputStream os,String str){
		byte[] dat = str.getBytes();
		try{
		 os.write(dat,0,dat.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getCurrentDate(){
		String ldapDate=null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		ldapDate=dateFormat.format(date)+"Z";
		return ldapDate;
	}

	//	Open a BufferedOutputStream based on the name passed to the function
	static BufferedOutputStream openFile(String name,boolean isOpen)
	{
		FileOutputStream fos = null;
		try{
		 fos = new FileOutputStream(name,isOpen);
		} catch (FileNotFoundException fnfe) {
			MessageLogger.writeLog("Caught a FileNotFoundException "+fnfe.toString());
			fnfe.printStackTrace();
		}
		BufferedOutputStream output = new BufferedOutputStream(fos);
		return output;
	}
	public static boolean isNull(String[] str)
	{
		boolean b=true;
		if(str==null || (str.length == 0)) return true;
		else{
			for(int i=0;i<str.length;i++) 
				if(!isNull(str[i]))	return false;
		}
		return b;
	}
	
}
