package com.macys.mst.DC2.EndToEnd.utilmethods;

import com.github.javaparser.utils.Log;
import com.macys.mst.artemis.config.ConfigProperties;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class SnapShotUtils {
	private final static String snapShotsFolderPath = ConfigProperties.getInstance("config.properties").getProperty("Serenityreportspath");
	private static Integer count = 1;
	
	public static void takeScreenShot(WebDriver driver,String scenarioName,String snapShotName){
		File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(file, new File(snapShotsFolderPath+"/screenshots/"+scenarioName+"_"+snapShotName+".png"));
		} catch (Exception e) {
			Log.info("Unable to create screenshot for {} scenario. Error {}", scenarioName,e.getLocalizedMessage());
		}
	}
	
	public static void takeScreenShot(WebDriver driver){
		File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(file, new File(snapShotsFolderPath+"/screenshots/"+(count++)+".png"));
		} catch (Exception e) {
			Log.info("Unable to create screenshot for scenario. Error {}", e.getLocalizedMessage());
		}
	}
}
