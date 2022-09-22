package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import com.macys.mst.artemis.config.ConfigProperties;
import com.macys.mst.artemis.testNg.LocalDriverFactory;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;


public class BrowserCapabilities {
	
	public void setBrowserCapability () {
		
		try {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");
		options.addArguments("disable-extensions");
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--start-maximized");
		options.addArguments("--disable-notifications");

		// code for download file
		String downloadFilepath = "C:\\Users\\JF59468\\Documents\\WMS_LITE_JOPPA_DOCUMENTS\\WMS_LITe_New\\wms-lite-automation\\src\\test\\resources\\OrderExportDownloaded";
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		options.setExperimentalOption("prefs", chromePrefs);
        //download file

		options.setHeadless(Boolean.parseBoolean(ConfigProperties.getInstance("config.properties").getProperty("headlessBrowser")));
		options.setAcceptInsecureCerts(true);
		LocalDriverFactory.setChromeOptions(options);

		}catch(Exception e) {
			
			e.printStackTrace();
			throw new AssertionError(e.getMessage());
		}
		
	}
	
	

}
