package com.email.autopdls;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class MessageLogger
{
		private static PrintWriter pw;
		public MessageLogger() {
		}
		public MessageLogger(String logFile) {
			try{
				pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile,true)));
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		public static void writeLog(String _s) {
			pw.println(_s);
			pw.flush();
		}
		public static void writeStackTrace(Exception e){
			e.printStackTrace(pw);
			pw.flush();
		}
		public void close() {
			pw.flush();
			pw.close();
		}
}
