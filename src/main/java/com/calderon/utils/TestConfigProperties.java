package com.calderon.utils;

import java.io.File;

import com.calderon.utils.MyLogger;

/**
 * Handles the properties file to be used by the tests
 * 
 * @author Johanna Calderon
 *
 */
public class TestConfigProperties extends PropertyFileReader {

	private static final MyLogger logger = new MyLogger(TestConfigProperties.class.getName());
	
	/**
	 * location of the file to load
	 */
	private String file = ""; 
	
	/**
	 * Constructor:
	 * Loads the properties file
	 */
	public TestConfigProperties(String fileName){
		
		super();
		this.file = fileName;
		File file;
		try {
			file = new File(this.file);
			if (file.exists()){
				this.setPropertiesFile(this.file);
				return;
			}
		} catch (Exception e) {
			logger.error("Unable to load file: " + this.file + e.getMessage());
		}
	}
	
	/**
	 * set the properties filename to use
	 * @param fileName
	 */
	public void setFile(String fileName){
		file = fileName;
	}
	
}
