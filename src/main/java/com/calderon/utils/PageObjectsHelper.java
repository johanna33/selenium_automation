package com.calderon.utils;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageObjectsHelper {
    
	private static final MyLogger logger = new MyLogger(PageObjectsHelper.class.getName());
	private WebDriver driver;   	
	private final static int MAX = 3;
	private final static String XPATH = "xpath";
	private final static String ID = "id";
	private final static String NAME = "name";
	private final static String LINK = "link";
    

	/**
	 * Return an element located by xpath specified
	 */
	public WebElement getElementByXpath(String xpath) {
		return getElementByXpath(xpath, 10, true);
	}

	/**
	 * Return an element located by name specified
	 */
	public WebElement getElementByName(String name) {
		return getElementByName(name, 10, true);
	}

	/**
	 * Return an element located by id specified
	 */
	public WebElement getElementByID(String ID) {
		return getElementByID(ID, 10, true);
	}

	/**
	 * Return a list of elements located by xpath specified
	 */
	public List<WebElement> getListElementByXpath(String xpath) {
		return getListElementByXpath(xpath, 10, true);
	}

	/**
	 * Return a list of elements located by name specified
	 */
	public List<WebElement> getListElementByName(String name) {
		return getListElementByName(name, 10, true);
	}

	/**
	 * Return an element located by Link text
	 */
	public WebElement getElementByLinkText(String linkText) {
		return getElementByLinkText(linkText, 10, true);
	}

	/**
	 * Return an element located by xpath specified
	 * 
	 * @param xpath
	 *            - xpath locator
	 * @param time
	 *            - maximum waiting time for element
	 * @param printError
	 *            - flag to allow logging the error message if element is null
	 */
	public WebElement getElementByXpath(String xpath, long time, boolean printError) {
		return this.getElement(xpath, time, printError, XPATH);
	}

	/**
	 * Return an element with the name specified
	 * 
	 * @param name
	 *            - name locator
	 * @param time
	 *            - maximum waiting time for element
	 * @param printError
	 *            - flag to allow logging the error message if element is null
	 */
	public WebElement getElementByName(String name, long time, boolean printError) {
		return this.getElement(name, time, printError, NAME);
	}

	/**
	 * Return an element with the id specified
	 * 
	 * @param ID
	 *            - id locator
	 * @param time
	 *            - maximum waiting time for element
	 * @param printError
	 *            - flag to allow logging the error message if element is null
	 */
	public WebElement getElementByID(String id, long time, boolean printError) {
		return this.getElement(id, time, printError, ID);
	}

	/**
	 * Return an element with the link text specified
	 * 
	 * @param linkText
	 *            - linkText locator
	 * @param time
	 *            - maximum waiting time for element
	 * @param printError
	 *            - flag to allow logging the error message if element is null
	 */
	public WebElement getElementByLinkText(String link, long time, boolean printError) {
		return this.getElement(link, time, printError, LINK);
	}

	/**
	 * Return a list of elements located by xpath specified
	 * 
	 * @param xpath
	 *            - xpath locator
	 * @param time
	 *            - maximum waiting time for element
	 * @param printError
	 *            - flag to allow logging the error message if element is null
	 */
	public List<WebElement> getListElementByXpath(String xpath, long time, boolean printError) {
		return this.getListOfElements(xpath, time, printError, XPATH);
	}

	/**
	 * Return a list of elements with name specified
	 * 
	 * @param name
	 *            - name locator
	 * @param time
	 *            - maximum waiting time for element
	 * @param printError
	 *            - flag to allow logging the error message if element is null
	 */
	public List<WebElement> getListElementByName(String name, long time, boolean printError) {
		return this.getListOfElements(name, time, printError, NAME);
	}

    
    
	private List<WebElement> getListOfElements(String element, long time, boolean printError, String by) {
		WebDriverWait wait = new WebDriverWait(driver, time);
		List<WebElement> elements = null;
		// TODO: Need to create a medium wait about --> 10 secs
		
		for (int i = 0; i < MAX && (elements == null || elements.size() < 1); ++i) {
			try {
				if (by.toLowerCase().equals(NAME))
					elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(element)));
				else if (by.toLowerCase().equals(XPATH))
					elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(element)));

			} catch (Exception ex) {
				// Nothing expected
			}
		}

		if (elements != null && elements.size() > 0)
			return elements;

		else {
			if (printError) {
				logger.error("---> Unable to find List of elements with "+by+": " + element);
			}
			return null;
		}
	}

	private WebElement getElement(String element, long time, boolean printError, String by) {
		WebDriverWait wait = new WebDriverWait(driver, time);
		List<WebElement> elements = null;
		// TODO: Need to create a medium wait about --> 10 secs

		for (int i = 0; i < MAX && (elements == null || elements.size() < 1); ++i) {
			try {
				if (by.toLowerCase().equals(ID))
					elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(element)));
				else if (by.toLowerCase().equals(NAME))
					elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(element)));
				if (by.toLowerCase().equals(XPATH))
					elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(element)));
				else if (by.toLowerCase().equals(LINK))
					try {
						elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText(element)));
					} catch (Exception e) {
						elements = wait
								.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.partialLinkText(element)));
					}

			} catch (Exception ex) {
				if (i == 1 && by.toLowerCase().equals(XPATH)) {
					element = "//*" + element.substring(element.split("\\[")[0].length());
					// xpath = "//*["+ xpath.split("\\[")[1];
				}
			}
		}

		if (elements != null && elements.size() > 0)
			return elements.get(0);

		else {
			if (printError) {
				logger.error("---> Unable to find element with "+by+": " + element);
			}
			return null;
		}
	}
	
    
}