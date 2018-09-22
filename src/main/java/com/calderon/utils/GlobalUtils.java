package com.calderon.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


/**
 * contains all common utilities used across the framework
 * 
 * @author Johanna Calderon
 */
public class GlobalUtils {

	private GlobalUtils() { }
	
	
	/**
	 * waiting time in seconds, for elements
	 */
	public static long waitTime = Long.valueOf(DriverBase.getConfigPropertyData().getValue("maxWait.time")).longValue();
    
	public static long shortWaitTime = Long.valueOf(DriverBase.getConfigPropertyData().getValue("shortWait.time"))
			.longValue();


	/**
	 * custom wait time
	 */
	public static void customWait(int seconds) {

		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 
	}
	
	
	/* ****************************************
	 *  ENCONDER / DECODER  FUNCTIONS
	 */
	
	
	/**
	 * decode the data from a string in Base64
	 */
	public static String decode(String data){
		byte[] valueDecoded= Base64.decodeBase64(data);
		return new String(valueDecoded);
	}
	
	/**
	 * encode the data from a string in BAse64
	 */
	public static String encode(String data){
		byte[] bytesEncoded = Base64.encodeBase64(data.getBytes());
		return new String(bytesEncoded);
	}
	
	
	/* ****************************************
	 *  LOGGING 
	 */
	
	/**
	 * Return last ERROR stored in log file
	 */
	public static String getErrorLog() {

		BufferedReader br = null;
		FileReader fr = null;
		String FILENAME = "./log/output.log";
		String result = "";

		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("ERROR")) {
					result = sCurrentLine;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		try {
			br.close();
			fr.close();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

    /* *******************************************************************
    FILE SYSTEM *******/
	
	/**
	 * Read all files in a folder and return a list
	 * @param folderPath - full path of the folder location
	 */
	public static ArrayList<String> readFilesFromFolder(String folderPath) {

		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> finalList = new ArrayList<String>();
		
		for (File file : listOfFiles) {
			if (file.isFile()) {
				//save it
				finalList.add(file.getName());
			}
		}
		
		return finalList;
		    
	}
    
	/**
	 * Delete all files on a dir including the root dir
	 * 
	 * @param file
	 *            current file with path
	 */
	public static void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				f.delete();
				deleteDir(f);
			}
		}
		file.delete();
	}
	
	/**
	 * Delete a file from project
	 */
	public static void deleteFile(String filePath){
		
		File file = new File(filePath);
		try {
			file.delete();
		} catch (Exception e) {
			System.out.println("Error deleting. No such file exists");
		}
	}
    
    
	/* *******************************************************************
     EXCEL SHEET *******/
	
	/**
	 * read the excel sheet and return the data in a multidimentional array
	 */
	public static String[][] getAllExcelData(String fileName, String sheetName) {
		String[][] arrayExcelData = null;
		try {
			FileInputStream fs = new FileInputStream(fileName);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(sheetName);

			int totalNoOfCols = sh.getColumns();
			int totalNoOfRows = sh.getRows();

			arrayExcelData = new String[totalNoOfRows - 1][totalNoOfCols];

			for (int i = 1; i < totalNoOfRows; i++) {

				for (int j = 0; j < totalNoOfCols; j++) {
					arrayExcelData[i - 1][j] = sh.getCell(j, i).getContents();
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}
		return arrayExcelData;
	}
	
	

}
