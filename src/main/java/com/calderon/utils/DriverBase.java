package com.calderon.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.calderon.utils.TestConfigProperties;


/**
 * 
 * Initialize the browser instance to be used
 * To be inherit in all web based test classes. 
 * 
 * @author Johanna Calderon
 *
 */
public class DriverBase {

	private static final MyLogger logger = new MyLogger(DriverBase.class.getName());
	
	/**
	 * webdriver instance to be used
	 */
	private static WebDriver driver;
	private static RemoteWebDriver remoteDriver;
		
	/**
	 * to setup desire settings to the driver instance
	 */
	private DesiredCapabilities caps;
	
	/**
	 * to extract browser information
	 */
	private Capabilities capability;
		
	/**
	 * holds the selenium grid server URL
	 */
	private URL serverURL;
		
	/**
	 * load the property file
	 */
	private static TestConfigProperties test_config = new TestConfigProperties("./src/test/resources/config.properties");
	
	/**
	 * load the browser to run the tests
	 */
	public static String browser = test_config.getValue("browser.name");
	
	/**
	 * Custom setting to set browser position ::: change according your monitors coordinates
	 */
	private Point coordinates = new Point(1942, 16);

	
	/**
	 * @return the config property file data
	 */
	public static TestConfigProperties getConfigPropertyData(){
		return test_config;
	}
	
	//=====================================================================//
	
	/**
	 * initialize the browser - webdriver instance
	 */
	@BeforeMethod(alwaysRun = true)
	public void setup() {
							
		logger.info("---> Starting WebDriver for: " + browser);
		
		if (browser.equals("ie")) {			
				setupIE();
		} else if (browser.equals("firefox")) {							
			   	setupFF();					
		} else if (browser.equals("chrome")) {							
				setupCHROME();		
		}
			   
	}
	
	/**
	 * @return the webdriver instance
	 */
	public WebDriver getDriver() {
		WebDriver d = null;		
		
		if (test_config.getValue("environment").equals("local")){
			d = driver;
		}
		if (test_config.getValue("environment").equals("remote")){
			d = (WebDriver) remoteDriver;
		}
				
		return d;		
	}

	
	/**
	 * close the browser and webdriver instance
	 */
	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		logger.info("---> Closing WebDriver");
		if (getDriver() != null) {  
			GlobalUtils.customWait(3); //required to fully quit chromeDriver
			getDriver().quit();	
			
			try {
				getDriver().quit();	 //required to fully quit chromeDriver
			} catch (Exception e) {}
						
			driver = null;
			remoteDriver = null;
		}
	}
	
	
	/**
	 * setup IE browser
	 */
	private void setupIE(){		
		//set capability settings
		caps = DesiredCapabilities.internetExplorer();
		caps.setCapability("ignoreZoomSetting", true);
		caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true); //To Enable Protected Mode settings
		caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);  //to pass ssl certificate
		   	
		//set proxy settings
		//if(test_config.getValue("proxy.enable").equals("true")){
			//TODO set proxy
	   	//}
		
		//initialize driver
		if (test_config.getValue("environment").equals("local")){
			
			System.setProperty("webdriver.ie.driver", test_config.getValue("ie.path"));
			
			try {
				driver = new InternetExplorerDriver(caps);

			} catch (Exception e) {
				logger.error("---> Current webDriver not compatible with the browser version...");
			}
			
			if (driver == null) {
				Assert.fail("--> None of the webDrivers are supported for this browser");
			}
		
			capability = ((RemoteWebDriver)driver).getCapabilities();		
			logger.info("---> Initializing Local WebDriver for Internet Explorer " + capability.getVersion());
			//Windows Settings
			//driver.manage().window().setPosition(coordinates);	//remove set position to maximize to work IE only
			driver.manage().window().maximize();   
			
		} else if (test_config.getValue("environment").equals("remote")){
			caps.setPlatform(getPlatform(test_config.getValue("system.platform"))); 				
			try {
				serverURL = new URL(test_config.getValue("remote.url"));
			} catch (MalformedURLException e) {
				logger.error("---> The remote url is incorrect, please check");
			}
					
			try {
				remoteDriver = new RemoteWebDriver(serverURL, caps);
			} catch (Exception e) {
				logger.error("--->" + e.getMessage());
			}
			logger.info("---> Initializing Remote WebDriver for Internet Explorer");
			remoteDriver.manage().window().maximize();
		}
	}
	
	/**
	 * setup Chrome browser
	 */
	private void setupCHROME(){
		Proxy proxy= new Proxy();
		ChromeOptions options = new ChromeOptions();
		caps = DesiredCapabilities.chrome();
		
		//Downloading setup
	   	File file = new File(test_config.getValue("chrome.downloadPath"));
		Map<String, Object> prefs = new HashMap<String, Object>();			
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("download.default_directory", file.getAbsolutePath());
		options.setExperimentalOption("prefs", prefs);

		//set proxy settings
		if(test_config.getValue("proxy.enable").equals("true")){					
			proxy.setHttpProxy(test_config.getValue("proxy.host") +":"+test_config.getValue("proxy.port"));
			caps.setCapability("proxy", proxy);
		}
		
		//set some options
		options.addArguments("--disable-popup-blocking"); //To disable automatic popup blocking 
		options.addArguments("--disable-extensions");
				
		//set capability settings
		caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		caps.setCapability(ChromeOptions.CAPABILITY, options);
		caps.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
		//caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
		
		//initialize driver
		if (test_config.getValue("environment").equals("local")){

			// USING DRIVERS FROM FOLDER
			ArrayList<String> fileName = GlobalUtils.readFilesFromFolder(test_config.getValue("chrome.root.path"));

			RETRY_DRIVER: for (String name : fileName) {

				String path = test_config.getValue("chrome.root.path") + name;
				logger.info(path);
				System.setProperty("webdriver.chrome.driver", path);

				try {
					driver = new ChromeDriver(caps);

				} catch (Exception ex) {
					logger.error("---> Current webDriver not compatible with the browser version...");
					continue RETRY_DRIVER;
				}

				if (driver == null) {
					Assert.fail("--> None of the webDrivers are supported for this browser");
				}

				capability = ((RemoteWebDriver)driver).getCapabilities();	
				logger.info("---> Initializing Local WebDriver for Chrome " + capability.getVersion());
				try {
					driver.manage().window().setPosition(coordinates);
					driver.manage().window().maximize();
					break;

				} catch (WebDriverException e) {
					logger.error("---> Current webDriver not compatible with the browser version...");
					tearDown();
					continue RETRY_DRIVER;
				}

			}
			
			
		} else if (test_config.getValue("environment").equals("remote")){
			caps.setPlatform(getPlatform(test_config.getValue("system.platform"))); 
			
			try {
			serverURL = new URL(test_config.getValue("remote.url"));
			} catch (MalformedURLException e) {
				logger.error("---> The remote url is incorrect, please check");
			}
			
			try {
				remoteDriver = new RemoteWebDriver(serverURL, caps);
			} catch (Exception e) {
				logger.error("--->" + e.getMessage());
			}
			logger.info("---> Initializing Remote WebDriver for Chrome");
			remoteDriver.manage().window().maximize();
			
		}
	}
	
	/**
	 * setup Firefox browser
	 */
	private void setupFF(){
		FirefoxProfile fp = new FirefoxProfile();
	   	caps = DesiredCapabilities.firefox();
	   		
	    //set proxy settings
	   	if(test_config.getValue("proxy.enable").equals("true")){		   	   
		   	fp.setPreference("network.proxy.type", 1);  //(ProxyType.MANUAL, Manual proxy settings)
		    fp.setPreference("network.proxy.http", test_config.getValue("proxy.host"));
		    fp.setPreference("network.proxy.http_port", test_config.getValue("proxy.port"));
	   	}
	   				   	
	   	//Downloading setup
	   	File file = new File(test_config.getValue("firefox.downloadPath"));			   	
	   	fp.setPreference("browser.download.folderList", 2);
	   	fp.setPreference("browser.download.manager.showWhenStarting", false);
	   	fp.setPreference("browser.download.dir", file.getAbsolutePath());
	   	fp.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/msword");
	   	//fp.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
	   	
	    //set capability settings
	   	caps.setCapability(FirefoxDriver.PROFILE, fp);
	   	//caps.setCapability("marionette", true);
	   	
	   	//initialize driver
		if (test_config.getValue("environment").equals("local")){
			
		//BINARY	
			if (test_config.getValue("firefox.binary").equals("true")) {
				System.setProperty("webdriver.firefox.bin", test_config.getValue("firefox.binary.path"));
			}
		   		
		//DEFAULT - embedded
			//selenium 2.53.1 with firefox version less than 47.0.1
			try {
				driver = new FirefoxDriver(caps); 
				
			} catch (Exception ex) {
				logger.error("---> Current webDriver not compatible with the browser version...");
				//retry again.
				driver = new FirefoxDriver(caps); 
			}
												
		//DRIVER - for firefox version greater than 47.0.1 (and selenium 3)	
			//TODO - using gecko driver might not work with selenium 2
			
			if (driver == null) {
				Assert.fail("--> None of the webDrivers are supported for this browser");
			}
		
			capability = ((RemoteWebDriver)driver).getCapabilities();					
			logger.info("---> Initializing Local WebDriver for Firefox " + capability.getVersion()); 
			//Windows Settings
			driver.manage().window().setPosition(coordinates);	
			driver.manage().window().maximize();
			
		} else if (test_config.getValue("environment").equals("remote")){
			//caps.setVersion(p.getValue("browser.version"));
			caps.setPlatform(getPlatform(test_config.getValue("system.platform"))); 
		
			try {
				serverURL = new URL(test_config.getValue("remote.url"));
			} catch (MalformedURLException e) {
				logger.error("---> The remote url is incorrect, please check");
			}
		
			try {
				remoteDriver = new RemoteWebDriver(serverURL, caps);
			} catch (Exception e) {
				logger.error("--->" + e.getMessage());
			}
			logger.info("---> Initializing Remote WebDriver for Firefox");
			remoteDriver.manage().window().maximize();
		}
		
	}
	
	
	/**
	 * returns the platform to be set in capabilities for the RemoteWebDriver
	 */
	private Platform getPlatform(String key){
		
		Platform os = null;
		
		if (key.equals("win7")){  //WINDOWS 7 (Windows Server 2008 R2)
			os = Platform.VISTA;
		} else if (key.equals("win8")){
			os = Platform.WIN8;
		} else if (key.equals("win10")){
			os = Platform.WIN10;
		}
		
		return os;
	}
	



	
}
