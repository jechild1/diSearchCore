package utilities;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

/**
 * This class is the configuration class that is used by projects and is meant
 * to be inherited by them. Code that is at the CORE level is meant to be
 * non-specific to a project and able to be successfully shared with others.
 * 
 * @author jesse.childress
 *
 */
public class AutomationHelper extends diCoreConfig.CoreConfig {

	/**
	 * Basic @AfterClass method for staging a test
	 */
	public static void finishTest() {

		if (driver != null) {
			if (!driver.toString().contains("(null)")) {
				driver.quit();
			}
		}
	}

	/**
	 * Method to print the current method name.
	 */
	public static void printMethodName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		// 0 = getStackTrace
		// 1 = this method name
		// 2 = method that calls this one

		StackTraceElement e = stacktrace[2];
		String methodName = e.getMethodName();

		Reporter.log("Method: " + methodName, true);
	}

	/**
	 * Sets a text field with the passed in text.
	 * <p>
	 * Note: This method also clears any text that may be in the existing field. if
	 * the clear() method doesn't work, it enters a while loop and uses Keys at
	 * select all and delete.
	 * 
	 * @param element
	 * @param text
	 */
	public static void setTextField(WebElement element, String text) {
		element.clear();

		while (!element.getAttribute(("value")).equals("")) {
			element.sendKeys(Keys.CONTROL + "A");
			element.sendKeys(Keys.DELETE);
		}

		element.sendKeys(text);

	}

	/**
	 * Returns the text of a given WebElement.
	 * <p>
	 * If the default Selenium getText() method doesn't work, this method will
	 * attempt to get the "value" attribute of the element.
	 * 
	 * @param element
	 * @return String
	 */
	public static String getText(WebElement element) {
		String text = null;
		text = element.getText();

		// Sometimes the getText() method doesn't pull back a value. When this happens,
		// lets try the .value property of the object.
		if (text.equals("")) {
			text = element.getAttribute("value");
		}
		return text;

	}

	/**
	 * Hits the <i>Enter</i> key on the keyboard on the passed in object.
	 * 
	 * @param element
	 */
	public static void hitEnter(WebElement element) {
		element.sendKeys(Keys.ENTER);
	}

	/**
	 * Simulates an Escape key press and release on the keyboard.
	 */
	public static void hitEscape() {

		Robot robot = null;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ESCAPE);
			robot.keyRelease(KeyEvent.VK_ESCAPE);
		} catch (AWTException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Simulates a Tab key press and release on the keyboard.
	 */
	public static void hitTab() {

		Robot robot = null;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
		} catch (AWTException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Simulates an Arrow Down key press and release on the keyboard.
	 */
	public static void hitArrowDown() {

		Robot robot = null;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyRelease(KeyEvent.VK_DOWN);
		} catch (AWTException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Returns the Page Title.
	 * 
	 * @return String
	 */
	public static String getPageTitle() {
		return driver.getTitle();
	}

	/**
	 * Moves the mouse to the passed in element
	 * 
	 * @param element
	 */
	public static void hoverOverElement(WebElement element) {
		Actions action = new Actions(driver);
		action.moveToElement(element).perform();
	}

	/**
	 * Method to cause the script to pause for a given amount of time, as indicated
	 * by the passed in parameter.
	 * 
	 * @paramtimeInSeconds
	 */
	public static void waitSeconds(int timeInSeconds) {
		// Convert to milliseconds
		timeInSeconds = timeInSeconds * 1000;

		try {
			Thread.sleep(timeInSeconds);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to cause the script to pause for a given amount of time, as indicated
	 * by the passed in parameter.
	 * 
	 * @paramtimeInMilliSeconds
	 */
	public static void waitMillis(int timeInMilliSeconds) {

		try {
			Thread.sleep(timeInMilliSeconds);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adjusts a wait time to a very short time, e.g. 100 milli seconds.
	 */
	public static void adjustWaitTimeToVeryShort() {
		setTimeout(driver, MICRO_TIMEOUT_MILLIS);
	}

	/**
	 * Adjusts a wait time to user specified time in seconds
	 */
	public static void adjustWaitTimeToUserSpecified(int seconds) {
		setTimeout(driver, seconds);
	}

	/**
	 * Adjusts a wait time to a very short time, e.g. 3 seconds.
	 */
	public static void adjustWaitTimeToShort() {
		setTimeout(driver, SHORT_TIMEOUT);
	}

	/**
	 * Adjusts a wait time to a very short time, e.g. 20 seconds.
	 */
	public static void adjustWaitTimeToNormal() {
		setTimeout(driver, NORMAL_TIMEOUT);

	}

	/**
	 * Removes instances of tab, new line, return and space in the string
	 * 
	 * @param originalString
	 * @return String
	 */
	public static String removeMarkupFromString(String originalString) {
		return originalString.replaceAll("[\\t\\n\\r\\s\\u00A0]+", " ").trim();
	}

	/**
	 * Method that accepts a locator to find a WebElement. If it finds at least one,
	 * it returns true.
	 * 
	 * @param locator e.g. By.xpath("//table")
	 * @return boolean.
	 */
	public static boolean isWebElementPresent(By locator) {

		boolean itemFound = false;

		AutomationHelper.adjustWaitTimeToShort();
		List<WebElement> theWebElement = driver.findElements(locator);
		AutomationHelper.adjustWaitTimeToNormal();

		if (theWebElement.size() > 0) {
			itemFound = true;
		}

		return itemFound;
	}

	/**
	 * Method that accepts a locator to find a WebElement. If it finds at least one,
	 * it returns true. This method also accepts the length of time that it will
	 * wait for the presence of the element in seconds.
	 * 
	 * 
	 * @param locator e.g. By.xpath("//table")
	 * @param seconds
	 * @return boolean.
	 */
	public static boolean isWebElementPresent(By locator, int seconds) {
		AutomationHelper.printMethodName();

		boolean itemFound = false;

		AutomationHelper.adjustWaitTimeToUserSpecified(seconds);
		List<WebElement> theWebElement = driver.findElements(locator);
		AutomationHelper.adjustWaitTimeToNormal();

		if (theWebElement.size() > 0) {
			itemFound = true;
		}

		return itemFound;
	}

	/**
	 * Utility method to wait for an element to be PRESENT and VISIBLE on the DOM. A
	 * Hidden element is NOT good enough to return.
	 * 
	 * @param locator
	 * @param seconds
	 */
	public static void waitForElementToBePresent(By locator, int seconds) {

		AutomationHelper.printMethodName();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Utility method to wait for an element to NOT be PRESENT and VISIBLE on the
	 * DOM. A Hidden element is NOT good enough to return.
	 * 
	 * @param locator
	 * @param seconds
	 */
	public static void waitForElementToNotBePresent(By locator, int seconds) {

		AutomationHelper.printMethodName();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOfElementLocated(locator)));
	}

	/**
	 * Returns if a WebElement is Enabled or not. The WebElement can exist in the
	 * DOM, but if it is not interactable and enabled, it will return false.
	 * 
	 * @param locator
	 * @return boolean
	 */
	public static boolean isWebElementEnabled(By locator) {
		AutomationHelper.printMethodName();

		WebElement currentWebElement = driver.findElement(locator);

		return currentWebElement.isEnabled() ? true : false;

	}

	/**
	 * Waits for an object to disappear on the page.
	 * 
	 * @param locator
	 * @param waitTimeInSeconds
	 * @param throwEx
	 */
	public static void waitForObjectToDisappear(By locator, long waitTimeInSeconds, boolean throwEx) {

		AutomationHelper.printMethodName();

		// Convert seconds to millis
		long waitTimeInMillis = waitTimeInSeconds * 1000;

		long startTime = System.currentTimeMillis();
		List<WebElement> objectList = new ArrayList<WebElement>();
		long currentElapsedTime;
		boolean objectPresent = true;

		do {

			AutomationHelper.adjustWaitTimeToShort();
			objectList = driver.findElements(locator);
			AutomationHelper.adjustWaitTimeToNormal();
			currentElapsedTime = (System.currentTimeMillis() - startTime);

			// If we don't find the object, it's gone
			if (objectList.size() == 0) {
				objectPresent = false;
				break;
			}

		} while ((currentElapsedTime < waitTimeInMillis));

		if (throwEx && objectPresent) {
			throw new NoSuchElementException("Waited for the element " + locator + " to disappear, but it did not.");

		}
	}
	
	/**
	 * Waits for an object to disappear on the page. This looks for a message like
	 * "File Deleted!" or "Your files will be available soon". You must have the
	 * case and spelling exactly correct.
	 * 
	 * @param notificationMessage
	 * @param waitTimeInSeconds
	 * @param throwEx
	 */
	public static void waitForNotificationToDisappear(String notificationMessage, long waitTimeInSeconds, boolean throwEx) {
		//We will call another method to do the work.
		//We only need to pass a locator into the other method.
		waitForObjectToDisappear(By.xpath("//span[text() = '"+ notificationMessage +"']"), waitTimeInSeconds, throwEx);	
	}
}
