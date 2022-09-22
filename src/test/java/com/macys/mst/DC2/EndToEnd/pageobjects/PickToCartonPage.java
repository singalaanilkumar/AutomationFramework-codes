package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.github.javaparser.utils.Log;
import com.macys.mst.artemis.selenium.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;


public class PickToCartonPage extends PageObject{

	public PickToCartonPage(WebDriver driver) {
		super(driver);
	}

	// Scan Location/Activity Id
	@FindBy(xpath = "//strong[contains(text(),'Scan Location/Activity ID')]")
	WebElement scanLocActLbl;
	
	@FindBy(xpath = "//strong[contains(text(),'Scan Location Id')]")
	WebElement scanLocLbl;
	
	@FindBy(xpath = "//INPUT[@type='text']")
	WebElement scanLocationIdTxtfield;
	
	@FindBy(xpath = "//span[contains(text(),'EXIT')]")
	WebElement exitButton;

	// Scan carton Id
	@FindBy(xpath = "//strong[contains(text(),'Activity ID')]")
	
	WebElement activityId;
	
	@FindBy(xpath = "//STRONG[text()='Activity Desc']/parent::div[1]")
	WebElement activityDesc;
	
	@FindBy(xpath = "//STRONG[text()='Store']/parent::div[1]")
	WebElement store;
	
	@FindBy(xpath = "//strong[contains(text(),'Location Id')]")
	WebElement locationId;
	
	@FindBy(xpath = "//STRONG[text()='SKU']/parent::div[1]")
	WebElement sku;
	
	@FindBy(xpath = "//span[text()='SKIP']")
	WebElement skipButton;

	@FindBy(xpath = "//INPUT[@type='text']")
	WebElement scanCartonTxtfield;
	
	@FindBy(xpath = "//strong[contains(text(),'Scan Carton')]")
	WebElement scanCrtLbl;
	
	@FindBy(xpath = "//span[text()='BACK']")
	WebElement backButton;

	// Scan Units
	@FindBy(xpath = "//strong[contains(text(),'Qty')]")
	WebElement qty;
	
	@FindBy(xpath = "//div[text()='Enter Qty:']")
	WebElement enterQtyLbl;
	
	@FindBy(id = "entryBox")
	WebElement enterQtyTxtfield;
	
	@FindBy(xpath = "//span[text()='CLOSE CARTON']")
	WebElement closeCartonButton;

	@FindBy(xpath = "//button[@type='button' and span='CLOSE']")
	WebElement closeButton;
	
	public ArrayList<String> activityIds = new ArrayList<String>();
	
	private WebDriverWait getWait(int secs) {
	    WebDriverWait wait = new WebDriverWait(driver, secs);
	    return wait;
	}

	public void scanLocationActionId(String locationId) throws InterruptedException {
		getWait(5).until(ExpectedConditions.visibilityOf(scanLocActLbl));
		scanLocationIdTxtfield.clear();
		Thread.sleep(3000);
		scanLocationIdTxtfield.sendKeys(locationId);
		scanLocationIdTxtfield.sendKeys(Keys.ENTER);
	}
	
	public void scanLocationId() throws InterruptedException {
		getWait(5).until(ExpectedConditions.visibilityOf(scanLocLbl));
		scanLocationIdTxtfield.clear();
		Thread.sleep(5000);
		String nextLocationId = locationId.findElement(By.xpath("./..")).getText().split(":")[1].trim();
		Log.info("Next location id on screen is " + nextLocationId);
		scanLocationIdTxtfield.sendKeys(nextLocationId);
		Thread.sleep(3000);
		scanLocationIdTxtfield.sendKeys(Keys.ENTER);
	}
	
	public void scanCarton(String cartonId) throws InterruptedException {
		getWait(5).until(ExpectedConditions.visibilityOf(scanCrtLbl));
		scanCartonTxtfield.clear();
		Thread.sleep(5000);
		scanCartonTxtfield.sendKeys(cartonId);
		Thread.sleep(3000);
		scanCartonTxtfield.sendKeys(Keys.ENTER);
	}
	
	public void scanFullQty() throws InterruptedException {
		getWait(5).until(ExpectedConditions.visibilityOf(enterQtyTxtfield));
		enterQtyTxtfield.clear();
		Thread.sleep(5000);
		String currActivityId = activityId.findElement(By.xpath("./..")).getText().split(":")[1].trim();
		Log.info("current Activity Id is " + currActivityId);
		activityIds.add(currActivityId);
		String fullQty = qty.findElement(By.xpath("./..")).getText().split(":")[1].trim();
		Log.info("full qty is " + fullQty);
		enterQtyTxtfield.sendKeys(fullQty);
		Thread.sleep(3000);
		enterQtyTxtfield.sendKeys(Keys.ENTER);
	}

	public void clickButton(String button) throws InterruptedException {
		if (button.equalsIgnoreCase("back"))
			backButton.click();
		else if (button.equalsIgnoreCase("skip"))
			skipButton.click();
		else if (button.equalsIgnoreCase("exit")){
			getWait(5).until(ExpectedConditions.visibilityOf(exitButton));
			exitButton.click();
		}
		else if (button.equalsIgnoreCase("close carton"))
			closeCartonButton.click();
		else if (button.equalsIgnoreCase("close")){
			getWait(5).until(ExpectedConditions.visibilityOf(closeButton));
			closeButton.click();
		}
	}
}
