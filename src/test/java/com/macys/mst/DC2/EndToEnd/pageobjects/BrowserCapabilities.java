package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.artemis.config.ConfigProperties;
import com.macys.mst.artemis.testNg.LocalDriverFactory;
import org.openqa.selenium.chrome.ChromeOptions;


public class BrowserCapabilities {
	
	public void setBrowserCapability () {
		
		try {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");
		options.addArguments("disable-extensions");
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--start-maximized");
		options.addArguments("--disable-notifications");
		options.setHeadless(Boolean.parseBoolean(ConfigProperties.getInstance("config.properties").getProperty("headlessBrowser")));

		options.setHeadless(false);

		options.setAcceptInsecureCerts(true);
		LocalDriverFactory.setChromeOptions(options);

		}catch(Exception e) {
			
			e.printStackTrace();
			throw new AssertionError(e.getMessage());
		}
		
	}
	
	

}
