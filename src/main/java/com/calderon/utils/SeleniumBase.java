package com.calderon.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.security.UserAndPassword;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.google.common.base.Function;

/**
 * Contains all basic selenium commands to perform actions against web elements.
 * Handles support for multiple browsers: IE, Chrome, Firefox
 * 
 * @author Johanna Calderon
 */
public class SeleniumBase {

	private static final MyLogger logger = new MyLogger(SeleniumBase.class.getName());

	public static WebDriverWait customWait;
	private WebDriver driver;
	private WebDriverWait wait;
	private JavascriptExecutor js;
	private Alert alert = null;


	/*
	 * constructor
	 */
	public SeleniumBase(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, GlobalUtils.waitTime);
		js = (JavascriptExecutor) driver;
	}

  
    // ===================================================================//
    //  Elements Interactions
    // ===================================================================//

	/**
	 * perform a click on the element
	 * 
	 * @param element
	 */
	public void clickElement(WebElement element) {
		GlobalUtils.customWait(1); // require or it will go to fast
		try {
			element.click();
		} catch (StaleElementReferenceException e) {
			// Ignore
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", element);
		}

	}
	
	public void clickElementJS(WebElement element) {
		js.executeScript("arguments[0].click();", element);
	}

	/**
	 * perform click two times on some elements that requires
	 * 
	 * @param element
	 * 
	 */
	public void clickElementTwice(WebElement element) {
		GlobalUtils.customWait(1); // require or it will go to fast
		try {
			element.click();
			GlobalUtils.customWait(1);
			element.click();
		} catch (Exception e) {
			// no body here, code will throw an exception on the next
			// line if not on the correct page
		}

	}

	/**
	 * perform a fast click on the element, with no wait time to mimic double
	 * click
	 * 
	 * @param element
	 */
	public void fastClickElement(WebElement element) {
		try {
			element.click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", element);
		}
	}

	/**
	 * Submit a form
	 * 
	 * @param element
	 */
	public void submitForm(WebElement element) {
		GlobalUtils.customWait(1); // require or it will go to fast
		try {
			element.submit();
		} catch (StaleElementReferenceException e) {
			// Ignore
		} catch (Exception e) {
			js.executeScript("arguments[0].submit();", element);
		}
	}

	/**
	 * set a value in an element. usually text fields, input tag if value is not
	 * correct it will re-try
	 * 
	 * @param element
	 * @param value
	 *            to set
	 */
	public void setValue(WebElement element, String value) {
		GlobalUtils.customWait(1); // require or it will go to fast :(

		try {
			element.click();
		} catch (Exception e) {
			// Ignore ElementNotClickableException, not necessary for set a value
		}

		element.sendKeys(value);

		if (!this.getValue(element).equalsIgnoreCase(value)) {
			GlobalUtils.customWait(1);
			element.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE);
			GlobalUtils.customWait(1);
			js.executeScript("arguments[0].value = '" + value + "';", element);
			js.executeScript("arguments[0].focus(); arguments[0].blur(); return true", element);
		}

	}

	/**
	 * clear the value, works for input and textarea
	 * 
	 * @param element
	 */
	public void clearValue(WebElement element) {
		GlobalUtils.customWait(1); // :( required
		try {
			element.clear();
		} catch (Exception e) {
			// Ignore
		}
	}

	/**
	 * return the value of the attribute VALUE in the element common for input
	 * fields
	 * 
	 * @param element
	 * @return the text of the value property
	 */
	public String getValue(WebElement element) {
		return element.getAttribute("value");
	}

	/**
	 * return the value of an specified attribute name in the element
	 * 
	 * @param element
	 * @param attributeName-
	 *            name of the attribute you want to get the value of
	 */
	public String getAttributeValue(WebElement element, String attributeName) {
		return element.getAttribute(attributeName);
	}

	/**
	 * return the text of that element
	 * 
	 * @param element
	 */
	public String getText(WebElement element) {
		String text = element.getText();

		if (text.equals(""))
			text = (String) js.executeScript("return arguments[0].value", element);

		if (text.equals(""))
			text = (String) js.executeScript("return arguments[0].textContent", element);

		return text;
	}
	
	/**
	 * return if the checkbox is checked
	 * 
	 * @param element
	 */
	public boolean isChecked(WebElement element) {
		return (Boolean) js.executeScript("return arguments[0].checked", element);
	}

	/**
	 * select an item in a drop down element by the value
	 * 
	 * @param element
	 * @param value-
	 *            name of the item, value of the item
	 */
	public void selectDropDown(WebElement element, String value) {
		new Select(element).selectByValue(value);
	}

	/**
	 * select an item in a drop down element by the value
	 * 
	 * @param element
	 * @param value-
	 *            name of the item, value of the item
	 */
	public void selectDropDownJs(WebElement element, String value) {
		js.executeScript("arguments[0].value = arguments[1]", element, value);
	}

	/**
	 * select an item in a drop down element by the index
	 * 
	 * @param element
	 * @param value-
	 *            index of the item
	 */
	public void selectDropDown(WebElement element, int value) {
		new Select(element).selectByIndex(value);
	}

	/**
	 * select an item in a drop down element by the text visible
	 * 
	 * @param element
	 * @param text
	 */
	public void selectDropDownByText(WebElement element, String text) {
		new Select(element).selectByVisibleText(text);
	}

	/**
	 * Returns the first visible element on a list of Elements
	 */
	public WebElement getVisibleElement(List<WebElement> elements) {
		for (WebElement element : elements) {
			if (element.isDisplayed())
				return element;
		}
		return null;
	}

   /**
	 * Choose a select option containing desire text value
	 * 
	 * @param element
	 *            select element
	 * @param text
	 *            containing text
	 */
	public void selectTextContains(WebElement element, String text) {

		Select select = new Select(element);
		List<WebElement> options = select.getOptions();

		for (WebElement option : options) {
			if (option.getText().contains(text)) {
				select.selectByIndex(options.indexOf(option));
				break;
			}
		}
	}
    
	/**
	 * scroll the current windows to a certain element
	 * 
	 * @param element
	 */
	public void scrollToElement(WebElement element) {
		GlobalUtils.customWait(3);
		try {
			js.executeScript("arguments[0].scrollIntoView(true);", element);
			waitVisibilityOf(element);
		} catch (Exception e) {
			logger.error("---> Could not scroll to the element");
		}
	}

	/**
	 * Use the Robot class to perform keypress actions -NOTE: this do not work
	 * on Virtual Machines
	 * 
	 * @param keyCode
	 *            - int value of the key
	 */
	public void pressKey(int keyCode) {
		try {
			Robot robot = new Robot();
			robot.keyPress(keyCode); // Press Key
			robot.keyRelease(keyCode); // Release the Key

		} catch (AWTException e) {
			logger.error("--> Cannot press key using Robot" + e.getMessage());
		}

	}

	// ===================================================================//
    //  Waits
    // ===================================================================//
    
	/**
	 * @param time
	 *            max time set for the element to be found
	 * @return the wait instance to be use in custom situations
	 */
	public static WebDriverWait customWait(WebDriver driver, int time) {
		customWait = new WebDriverWait(driver, time);
		return customWait;
	}

	/**
	 * wait for the HTML DOM to be ready, all elements loaded
	 * 
	 * note: Does not guarantee the full page has loaded, sometimes DOM can
	 * return complete, but some elements are still loading dynamically.
	 */
	public void waitForDOMready() {

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};

		try {
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.assertFalse(true, "Timeout waiting for Page Load Request to complete.");
		}

	}

	/**
	 * wait for the visibility of the element alternative to wait for page load
	 * 
	 * @param element
	 * 
	 */
	public void waitVisibilityOf(WebElement element) {
		waitVisibilityOf(element, 60);
	}

	public void waitVisibilityOf(WebElement element, int time) {
		WebDriverWait waiting = new WebDriverWait(driver, time);

		try {
			waiting.until(ExpectedConditions.visibilityOf(element));
		} catch (StaleElementReferenceException e) {
			// Ignore
		} catch (Exception ex) {
			// Ignore
		}

	}
	
	public void waitAttributeToBeNotEmpty(WebElement element, String value) {
		WebDriverWait waiting = new WebDriverWait(driver, 15);
		try {
			waiting.until(ExpectedConditions.attributeToBeNotEmpty(element, value));
		} catch (StaleElementReferenceException e) {
			// Ignore
		} catch (Exception ex) {
			// Ignore
		}

	}
	

	public void waitAttributeValueToBePresent(WebElement element,String attribute, String value) {
		WebDriverWait waiting = new WebDriverWait(driver, 15);
		try {
			waiting.until(ExpectedConditions.attributeToBe(element, attribute, value));
		} catch (StaleElementReferenceException e) {
			// Ignore
		} catch (Exception ex) {
			// Ignore
		}

	}
	
	
	public void waitVisibilityOfAllElements(List<WebElement> elements) {
		waitVisibilityOfAllElements(elements, 60);
	}

	public void waitVisibilityOfAllElements(List<WebElement> elements, int time) {
		WebDriverWait waiting = new WebDriverWait(driver, time);

		try {
			waiting.until(ExpectedConditions.visibilityOfAllElements(elements));
		} catch (StaleElementReferenceException e) {
			// Ignore
		} catch (Exception ex) {
			// Ignore
		}

	}

	/**
	 * wait for the element to be clickable
	 * 
	 * @param element
	 * @param time
	 */
	public void waitToBeClickable(WebElement element, int time) {
		WebDriverWait waiting = new WebDriverWait(driver, time);
		waiting.until(ExpectedConditions.elementToBeClickable(element));

	}

	/**
	 * wait for the invisibility of some element find by xpath
	 */
	public void waitInvisibilityOf(String xpath, int time) {
		WebDriverWait waiting = new WebDriverWait(driver, time);
		waiting.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
	}


	/**
	 * wait for a desire amount of minutes for an element polling every desire
	 * amount of minutes. returns the element if is found
	 * 
	 * @param locator
	 *            A webdriver locator, like By.Id
	 * @param timeOut
	 *            the total time to wait for the element in Minutes
	 * @param pollTime
	 *            the time for polling in Minutes
	 */
	public WebElement fluentWait(final By locator, long timeOut, long pollTime) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(5, TimeUnit.MINUTES)
				.pollingEvery(1, TimeUnit.MINUTES).ignoring(NoSuchElementException.class);

		WebElement element = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(locator);
			}
		});

		return element;
	};

    
	// ===================================================================//
    //  Windows / Frames
    // ===================================================================//
    
	/**
	 * Switch to another Window make sure to store and pass the current window
	 * handler, before using this method. it handles multiple windows.
	 */
	public void switchToWindow(Set<String> allWin) {

		if (driver instanceof InternetExplorerDriver) {
			int i = 0;
			while (i < 40) {
				driver.getWindowHandles();
				if (driver.getWindowHandles().size() > 1) {
					break;
				}
				i++;
			}
		}

		driver.getWindowHandles();

		if (driver.getWindowHandles().size() > 1) {
			Set<String> allHandles = driver.getWindowHandles();
			for (String currentHandle : allHandles) {
				if (!allWin.contains(currentHandle)) {
					driver.switchTo().window(currentHandle);
					break;
				}
			}

		} else {
			logger.warn("---> The driver has not detected more than one windows open");
		}

	}

	/**
	 * Switch back to the main window Note: this only switch to one window,
	 * assuming allWin has a size of 1 if not, it will not make the switch.
	 */
	public void switchBackToMainWindow(Set<String> allWin) {
		if (allWin.size() == 1) {
			for (String currentWin : allWin) {
				driver.switchTo().window(currentWin);
			}
		}
	}

	/**
	 * Switch to frame
	 */
	public void switchToFrame(String frameName) {
		GlobalUtils.customWait(5);
		try {
			driver.switchTo().frame(frameName);
		} catch (Exception e2) {
			driver.switchTo().frame(driver.findElement(By.id(frameName)));
		}

	}

	// Handling iFrame encapsulation
	/**
	 * 
	 * @param fragment1
	 *            - name/ID outer fragemnt
	 * @param attr
	 *            - Attribute for searching purpose
	 * @param value
	 *            - value of attribute
	 */
	public void switchToFrame(String fragment1, String attr, String value) {

		driver.switchTo().defaultContent();
		switchToFrame(fragment1);
		List<WebElement> iframes = driver.findElements(By.cssSelector("iframe"));

		WebElement myFrame = null;

		for (WebElement iframe : iframes) {
			if (iframe.getAttribute(attr).contains(value))
				myFrame = iframe;
		}

		if (myFrame != null)
			driver.switchTo().frame(myFrame);
	}

	/**
	 * Get Out of the Frames
	 */
	public void getOutFrames() {
		driver.switchTo().defaultContent();

		if (driver instanceof InternetExplorerDriver) {
			String parentHandle = driver.getWindowHandle();
			driver.switchTo().window(parentHandle);
		}

	}

	// ===================================================================//
    //   Alerts
    // ===================================================================//

	/**
	 * Alerts handlers - verify if alert is present
	 */
	public boolean isAlertPresent() {
		try {
			GlobalUtils.customWait(4);
			alert = driver.switchTo().alert();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * accept the alert - click on OK / YES
	 */
	public void acceptAlert() {
		GlobalUtils.customWait(5);
		alert = driver.switchTo().alert();
		alert.accept();
	}

	/**
	 * Dismiss the alert - click CANCEL / NO
	 */
	public void dismissAlert() {
		GlobalUtils.customWait(5);
		try {
			alert = driver.switchTo().alert();
			alert.dismiss();
		} catch (Exception e) {

			js.executeScript("window.alert = function(msg){};");
		}
	}

	/**
	 * @return the text on the alert box as a string
	 */
	public String getAlertText() {
		GlobalUtils.customWait(5);
		return driver.switchTo().alert().getText().trim();
	}

	/**
	 * authenticate an alert by passing the credentials
	 * 
	 * BASIC HTTP authentication is not supported at the moment, 
	 * WebDriver specification doesn't tell drivers to support 
	 * HTTP authentication. Since Selenium implements the spec, 
	 * it doesn't support auth as well.
	 * @param userName
	 * @param password
	 */
	public void authenticateAlert(String userName, String password) {
		alert = driver.switchTo().alert();
		if (alert != null) {
			alert.authenticateUsing(new UserAndPassword(userName, password));
		}
	}

	/**
	 * show an alert with a specific message
	 * 
	 * @param message
	 */
	public void showAlert(String message) {
		try {
			js.executeScript("alert('" + message + "')");
		} catch (Exception ex) {
			logger.error("---> Could not show alert.");
		}
	}

	// ===================================================================//
    //   Validations
    // ===================================================================//

	/**
	 * validate the page title contains the expected
	 * 
	 * @param current
	 * @param expected
	 */
	public void validatePageTitleContains(String expected) {
		GlobalUtils.customWait(4);
		wait.until(ExpectedConditions.titleContains(expected));
		// Assert.assertTrue(current.contains(expected));
	}

	/**
	 * validate the page using the url
	 * 
	 * @param expected
	 */
	public void validateUrlContains(String expected) {
		GlobalUtils.customWait(8);
		Assert.assertTrue(driver.getCurrentUrl().contains(expected), "URL does not match the expected");
	}

	/**
	 * validate the screen title contains the expected
	 * 
	 * @param current
	 * @param expected
	 */
	public void validateScreenTitleContains(String current, String expected) {
		GlobalUtils.customWait(5);
		Assert.assertTrue(current.contains(expected));
	}


	/**
	 * Checks the attribute value or text value for each element of the page
	 * method, must contains keyword _Validate. If the element is a dropdown
	 * xpath must return the "SELECTED" option as the following example:
	 * 
	 * Select sel = new Select(base_driver.getElementByID(ID)); return
	 * sel.getFirstSelectedOption();
	 * 
	 * 
	 * @param pageObject
	 *            Page object instance already initiaded with driver.
	 * 
	 */
	public void validateFields(Object pageObject) {
		boolean found = false;
		Class<?> c = pageObject.getClass();
		Method[] methods = c.getDeclaredMethods();
		for (Method currentMethod : methods) {
			try {

				/*
				 * invoking each method with current object instance of the
				 * class as parameter, returns method and has to be cast to the
				 * return method of the class
				 */
				if (currentMethod.getName().contains("_Validate")) {
					logger.info("---> Current element: " + currentMethod.getName());
					// evaluates each webelement to check either its attribrute
					// or
					// text value
					WebElement element = (WebElement) currentMethod.invoke(pageObject);
					found = false;
					if (element != null) {
						if (element.getAttribute("value") == null || element.getAttribute("value").isEmpty()) {
							found = true;
							Assert.fail("---> Unable to validate: " + currentMethod.getName());

						} else if (!element.getTagName().equals("option")) {
							found = true;
							logger.info("---> " + element.getAttribute("value") + " Validated");
						}

						if (element.getText() == null || element.getText().isEmpty() && found == false) {

							Assert.fail("---> Unable to validate: " + currentMethod.getName());
						} else if (!found) {
							logger.info("---> " + element.getText() + " Validated");
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}

	/**
	 * Validates a single webElement method by value or text If the element is a
	 * dropdown xpath must return the "SELECTED" option as the following
	 * example: Select sel = new Select(base_driver.getElementByID(ID)); return
	 * sel.getFirstSelectedOption();
	 * 
	 * @param element
	 *            current element
	 * @return true if element has either a valid value or text attribute
	 */
	public boolean validateFields(WebElement element) {

		// evaluates each webelement to check either its attribrute
		// or
		// text value

		if (element.getAttribute("value") == null || element.getAttribute("value").isEmpty()) {
			return false;

		} else if (!element.getTagName().equals("option")) {
			return true;
		}

		if (element.getText() == null || element.getText().isEmpty()) {
			return false;
		} else {
			return true;
		}

	}
    

	/**
	 * Validates the value attribute for a input type element and that is disable
	 * @param inputName name to represent input
	 * @param element WebElement to evaluate input.
	 */
	public void validateInputsValue(String inputName, WebElement element, WebDriver driver){
		new SeleniumBase(driver).waitAttributeToBeNotEmpty(element, "value");
		new SeleniumBase(driver).waitAttributeValueToBePresent(element, "aria-disabled", "true");
		
		String atrriValue = element.getAttribute("value");
		String isElementEnable = element.getAttribute("aria-disabled");
	
		if (atrriValue == null || atrriValue.isEmpty())
			Assert.fail(inputName + " input is empty!");
		else if(isElementEnable == null || !isElementEnable.equals("true"))
			Assert.fail(inputName + " input is enable!");
		else
			logger.info("---> "+inputName+": " + atrriValue);
	}
	

	
	/**
	 * Validates a radio btn is checked  and that is disable
	 * @param inputName name to represent radio input
	 * @param noElement Weblement of Yes radio btn
	 * @param noElement Weblement of No radio btn
	 */
	public void validateRadioBtnssValue(String inputName, WebElement yesElement, WebElement noElement){
		String yesRadioBtn = yesElement.getAttribute("aria-checked");
		String noRadioBtn = noElement.getAttribute("aria-checked");
		
	
		if (!yesRadioBtn.equals("true") && !noRadioBtn.equals("true"))
			Assert.fail(inputName + " is not checked!");
		else
			logger.info("---> "+inputName+": " + "is checked");
	}

	
	

}
