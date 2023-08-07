package diCoreConfig;

import java.time.Duration;
import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.support.PageFactory;
import org.testng.ITestContext;
import org.testng.Reporter;

import io.github.bonigarcia.wdm.WebDriverManager;
import utilities.AutomationHelper;

/**
 * Class containing methods to set up browser types and configurations
 * 
 * @author jesse.childress
 *
 */
public abstract class CoreConfig {

	// Project URL
	private static String BASE_URL = ""; // Usually, project specific and will override this in lower level project.

	protected static long MICRO_TIMEOUT_MILLIS = 100;
	protected static int SHORT_TIMEOUT = 3;
	protected static int NORMAL_TIMEOUT = 20;
	protected static int LONG_TIMEOUT = 50;

	protected static WebDriver driver;

	/**
	 * Abstract Config Constructor
	 */
	public CoreConfig() {

	}

	/**
	 * Abstract Config Constructor accepts the address from the page inheriting this
	 * abstract config.
	 * 
	 * @param project_URL
	 */
	public CoreConfig(String project_URL) {
		BASE_URL = project_URL;
	}

	/**
	 * Loads the given page in a new browser instance
	 */
	public void loadPage() {
		driver = this.setDriver();
		launchAndConfigureBrowser(driver);

		waitForPageToLoad();
		
		//TODO - Remove this after AS-182 is corrected.
//		AutomationHelper.waitSeconds(3);
		
		PageFactory.initElements(driver, this);
	}

	private void launchAndConfigureBrowser(WebDriver driver) {
		// Softer Asserter can go here
		// Timeout can go here
		setBrowserProperties(driver);
		setTimeout(driver, NORMAL_TIMEOUT);
		loadPage(driver);
	}

	/**
	 * Loads a browser with the base URL as outlined in the
	 * 
	 * @param driver
	 */
	private static void loadPage(WebDriver driver) {
		driver.get(BASE_URL + "login");
	}

	/**
	 * Sets properties in the browser, such as maximizing the screen size.
	 * 
	 * @param driver
	 */
	private static void setBrowserProperties(WebDriver driver) {
		driver.manage().window().maximize();
	}

	/**
	 * This method instantiates a WebDriver for use in the application.
	 * 
	 * @return WebDriver
	 */
	private WebDriver setDriver() {

		// Get the user-specified browser from the test suite xml file. If none listed,
		// we will go with the default
		String selectedBrowser = getSelectedBrowser();

		// Default browser in string if not user selected from xml test suite file.
		selectedBrowser = selectedBrowser != null ? selectedBrowser : "firefox";

		// Switch statement to go through each browser and set properties, if need be.
		// See archived core classes for options, if need be.
		switch (selectedBrowser.toLowerCase()) {
		case "ie":
			WebDriverManager.iedriver().arch32().setup();

			// Ignoring Zoom Settings
			InternetExplorerOptions options = new InternetExplorerOptions();
			options.ignoreZoomSettings();
			driver = new InternetExplorerDriver(options);
			break;

		case "edge":
			
			
			HashMap<String, Object> edgePrefs = new HashMap<String, Object>();
			edgePrefs.put("profile.default_content_settings.popups",0);
			edgePrefs.put("profile.default_content_setting_values.notifications",2);		
			edgePrefs.put("profile.default_content_setting_values.automatic_downloads" ,1);		
			edgePrefs.put("profile.content_settings.pattern_pairs.*,*.multiple-automatic-downloads",1);
			
			EdgeOptions egdeOptions = new EdgeOptions();
			egdeOptions.setExperimentalOption("prefs",edgePrefs);
			
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver(egdeOptions);
			break;

		case "chrome":
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			break;

		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			break;
			
		default:
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
		}
		return driver;
	}

	/**
	 * Sets the length of time before timing out when looking for an object on the
	 * page. This method uses seconds.
	 * 
	 * @param driver
	 * @param timeOutLengthInSeconds
	 */
	protected static void setTimeout(WebDriver driver, int timeOutLengthInSeconds) {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeOutLengthInSeconds));
	}

	/**
	 * Sets the length of time before timing out when looking for an object on the
	 * page. This method uses Milliseconds
	 * 
	 * @param driver
	 * @param timeoutLengthInMillis
	 */
	protected static void setTimeout(WebDriver driver, long timeoutLengthInMillis) {
		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(timeoutLengthInMillis));
	}

	/**
	 * Gets the selected browser from the XML text file, e.g. FireFox, Chrome, Edge,
	 * etc. It uses it for picking a browser to test with.
	 * 
	 * @return String
	 */
	protected String getSelectedBrowser() {

		ITestContext testContext = Reporter.getCurrentTestResult().getTestContext();

		HashMap<String, String> parameters = new HashMap<String, String>(
				testContext.getCurrentXmlTest().getAllParameters());
		return parameters.get("selectedBrowser");

	}

	/**
	 * Waits for a given page to be fully loaded.
	 */
	public void waitForPageToLoad() {
		Reporter.log("Waiting for page to load completely...", true);

		JavascriptExecutor js = (JavascriptExecutor) driver;

		boolean initiallyLoaded = false;

		// Perform an initial check for the page to be loaded.
		if (js.executeScript("return document.readyState").toString().equals("complete")) {

			Reporter.log("The page '" + AutomationHelper.getPageTitle() + "' is fully loaded now.", true);

			initiallyLoaded = true;
		}

		if (!initiallyLoaded) {
			for (int i = 0; i < NORMAL_TIMEOUT; i++) {
				AutomationHelper.waitSeconds(1);

				if (js.executeScript("return document.readyState").toString().equals("complete")) {
					Reporter.log("The page '" + AutomationHelper.getPageTitle() + "' is fully loaded now. /n"
							+ "It took " + i + " seconds to load.", true);

//TODO - This needs a second look because it prints an empty string
					Reporter.log("The page '" + driver.getCurrentUrl() + "' is fully loaded now. /n" + "It took " + i
							+ " seconds to load.", true);
					break;

				}
			}
		}

	}

}
