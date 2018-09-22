package com.calderon.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import org.openqa.selenium.By;

/**
 * @author Johanna Calderon
 */
public class GooglePage {
    
    private WebDriver driver;
    
    public GooglePage(WebDriver driver) {
		this.driver = driver;
	}
    
    public WebElement getSearchBox() {
		return driver.findElement(By.name("q"));
	}
    
    public WebElement getSearchBtn() {
		return driver.findElement(By.name("btnK"));
	}
    
    public List<WebElement> getSearchResutls(String data) {
    	String xpath = "//a[contains(text(),'"+data+"')]";
		return driver.findElements(By.xpath(xpath));
	}
}
    