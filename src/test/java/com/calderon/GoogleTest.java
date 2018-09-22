package com.calderon;

import com.calderon.utils.SeleniumBase;
import com.calderon.pageobjects.GooglePage;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;
import com.calderon.utils.MyLogger;
import com.calderon.utils.DriverBase;

/**
 * Test Case suite - contains related test cases
 * @author Johanna Calderon
 */
public class GoogleTest extends DriverBase{
    
	private static final MyLogger logger = new MyLogger(GoogleTest.class.getName());
    
     /**
      * sample test case  
      */
    @Test(description="open google and search for a word")
    public void test1(){
        
        SeleniumBase selenium = new SeleniumBase(getDriver());
        GooglePage page = new GooglePage(getDriver());
        String url = "https://www.google.com/";
        String title = "google";
        String searchData = "software engineer";
        
        logger.info("opening Google page");
        getDriver().get(url);
        selenium.waitForDOMready();
        Assert.assertTrue(getDriver().getTitle().toLowerCase().contains(title), 
        		"The page requested is not correct");
        logger.info("Page validated!"); 

        logger.info("typing the data for search");  
        selenium.waitVisibilityOf(page.getSearchBox());
        selenium.clearValue(page.getSearchBox());
		selenium.setValue(page.getSearchBox(), searchData);
        selenium.clickElement(page.getSearchBtn());
        
        selenium.waitForDOMready();
        List<WebElement> results = new ArrayList<WebElement>();
        results = page.getSearchResutls(searchData);
        if(!results.isEmpty()) {
        	Assert.assertTrue(results.size()>0, "No results were found");
        } else {
        	Assert.fail("No results were found");
        }       
        logger.info("Search output validated!");  
    }
    
    
}