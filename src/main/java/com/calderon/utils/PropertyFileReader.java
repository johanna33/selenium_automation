package com.calderon.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.calderon.utils.MyLogger;


/**
 * Handles basic functionality to work with properties files
 *  
 * @author Johanna Calderon
 *
 */
public class PropertyFileReader {

	protected Properties properties = null;
	protected String propertiesFile = null;

	private static final MyLogger logger = new MyLogger(PropertyFileReader.class.getName());
	
	/**
	 * default constructor
	 */
	public PropertyFileReader(){
	}
	
	/**
	 * load the property file
	 * @param propertiesFile
	 */
	public PropertyFileReader(String propertiesFile){
		this.initializePropertiesFile(propertiesFile);
	}
	
	public void setPropertiesFile(String propertiesFile){
		this.initializePropertiesFile(propertiesFile);
	}
	
	private void initializePropertiesFile(String propertiesFile){
		try {
			this.propertiesFile = propertiesFile;
			this.properties = new Properties();
			FileInputStream in = new FileInputStream(propertiesFile);
			this.properties.load(in);
			in.close();
			logger.info("Properties file: " + propertiesFile + " loaded !");
		} catch (Exception e) {
			logger.error("Unable to load properties file: " + propertiesFile + e.getMessage());
		}
		
	}
	
	/**
	 * get a value from the properties file
	 * @param key
	 * @return
	 */
	public String getValue(String key){
		return this.properties.getProperty(key);
	}
	
	/**
	 *  get a value from the properties file, returns a default value if not present
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getValue(String key, String defaultValue){
		return this.properties.getProperty(key, defaultValue);
	}
	
	/**
	 * set a value in the properties file
	 * @param key - property where to set the value
	 * @param data - value to set
	 */
	public void setValue(String key, String data){
		this.properties.setProperty(key, data);
		try {
			FileOutputStream out = new FileOutputStream(this.propertiesFile);
			this.properties.store(out, "");
			out.close();
			logger.info("Properties file: " + this.propertiesFile + " updated successfully");
		} catch (Exception e) {
			logger.error("Unable to write to properties file: " + this.propertiesFile + e.getMessage());
		}
		
	}
	
}
