package com.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Extractor 
  { 
    private static String DATA_PATH = "C:/numbers.dat";

    public static void main (String args[])
      {
        Extractor extractor = new Extractor();

        DirContext eDirContext = extractor.getContext();

        Vector dataVector = new Vector();
        dataVector = extractor.getList(DATA_PATH);

        boolean found = false;
        String temp = null;
        String temp2 = null;
        String dn = null;
        String outTemp = null;
        String[] reqAttr = {"dn","givenName","sn","l","Department","mail"};
        String[] allCabs = {",ou=People,o=Company",
                            ",ou=Affiliates,o=Company"
                           };
        Attributes attrs = null;

        for (int i = 0; i < dataVector.size(); i++)
          {
            int h = 0;
            
            temp = (String) dataVector.elementAt(i);
            temp.trim();
            temp2 = temp;

            found = false;
            for (h = 0; found == false && h < allCabs.length; h++)
              {
                try
                  {
                    dn = "cn=" + temp + allCabs[h];
                    attrs = eDirContext.getAttributes (dn, reqAttr);
                    found = true;
                  }
                catch (Exception e)
                  {
                    found = false;
                  }
              }

            if (found)
              {
                outTemp = temp2 + ",";
                for (int j = 0; j < reqAttr.length; j++)
                  {
                    try
                      {
                        Enumeration eNum = attrs.get (reqAttr [j]).getAll();
                        int attrNdx = 0;
                        while (eNum.hasMoreElements()) {
                        	if (attrNdx > 0) outTemp += "|";
                        	attrNdx++;
                        	outTemp += eNum.nextElement() + "";
                        }
                        outTemp += ",";
                      }
                    catch (Exception e)
                      {
                        outTemp += ",";
                      }
                  }
                System.out.println (outTemp);
              }
            else
              {
                System.out.println (temp + ",*NOTFOUND");
              }
          }
      }

    private Vector getList (String filePath)
      {
        Vector dataVector = new Vector();
        BufferedReader reader;

        try
          {
            reader= new BufferedReader (new FileReader(filePath));
            String currentRecord = reader.readLine();
            while (currentRecord != null)
              {
                currentRecord = currentRecord.trim();
                if (!(currentRecord.equals("")))
                  {
                    dataVector.add(currentRecord);
                  }
                currentRecord = reader.readLine();
              }
          }
        catch (Exception e) {}
        return dataVector;
	  }

    private DirContext getContext ()
      {
        DirContext ctx = null;
        try
          {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://directory.company.com:389/");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.REFERRAL, "follow");
            env.put(Context.SECURITY_PRINCIPAL, "cn=User,ou=Management,ou=services,o=Company");
            env.put(Context.SECURITY_CREDENTIALS, "s3cur3P@s$code");
            ctx = new InitialDirContext(env);
          }
        catch (Exception e) {}
        return ctx;
      }
  }
