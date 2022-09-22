package com.macys.mst.Atlas.pageobjects;

import com.macys.mst.artemis.config.ConfigProperties;
import com.macys.mst.artemis.testNg.LocalDriverFactory;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;


public class BrowserCapabilities {
	
	public void setBrowserCapability () {
		
		try {
//		ChromeOptions options = new ChromeOptions();
//		options.addArguments("disable-infobars");
//		options.addArguments("disable-extensions");
//		options.addArguments("--window-size=1920,1080");
//		options.addArguments("--start-maximized");
//		options.addArguments("--disable-notifications");
//		options.setHeadless(Boolean.parseBoolean(ConfigProperties.getInstance("config.properties").getProperty("headlessBrowser")));
//		options.setAcceptInsecureCerts(true);
//		LocalDriverFactory.setChromeOptions(options);
//		InternetExplorerOptions options = new InternetExplorerOptions();
//		options.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
//		options.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
//		LocalDriverFactory.setInternetExplorerOptions(options);
//			LocalDriverFactory.getInternetExplorerOptions();
			LocalDriverFactory.getFirefoxOptions();
		}catch(Exception e) {
			e.printStackTrace();
			throw new AssertionError(e.getMessage());
		}
		
	}
	
	

}
