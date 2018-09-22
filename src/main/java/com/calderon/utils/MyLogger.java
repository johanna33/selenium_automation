package com.calderon.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * wrapper class for custom logging
 * uses log4j library
 * 
 * @author Johanna Calderon
 */
public class MyLogger{

	private static final Logger logger = LogManager.getLogger(MyLogger.class);	
	private static String testName;  
	private String className;
	
	
	public MyLogger(String className){
		this.className = className;
	}
	
	public void error(String str){
		logger.error(className+":"+testName+": "+str);
	}
	
	public void info(String str){
		logger.info(className+":"+testName+": "+str);
	}
	
	public void warn(String str){
		logger.warn(className+":"+testName+": "+str);
	}
	
	//adds a test name
	public static void setName(String name){
		testName=name;
	}
	
	public static String getName(){
		return testName;
	}
	
}
