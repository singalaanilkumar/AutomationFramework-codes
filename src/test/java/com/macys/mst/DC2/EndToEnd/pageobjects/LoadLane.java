package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.artemis.selenium.PageObject;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoadLane extends PageObject{

	//Scan Qty screen
	
	 public LoadLane(WebDriver driver) {
	        super(driver);
	    }
	
	@FindBy(xpath = "//STRONG[text()='Location Id :']/parent::div[1]")
    WebElement LocationId;
	@FindBy(xpath = "//STRONG[text()='Tote ID :']/parent::div[1]")
    WebElement toteId;
	@FindBy(xpath = "//STRONG[text()='SKU']/parent::div[1]")
    WebElement SKU;
	@FindBy(xpath = "//STRONG[text()='Color']/parent::div[1]")
    WebElement color;
	@FindBy(xpath = "//STRONG[text()='PID']/parent::div[1]")
    WebElement PID;
	@FindBy(xpath = "//STRONG[text()='Size']/parent::div[1]")
    WebElement Size;
	@FindBy(xpath = "//STRONG[text()='Qty']/parent::td[1]")
    WebElement Qty;
	@FindBy(xpath = "//STRONG[text()='PO']/parent::div[1]")
    WebElement PO;
	@FindBy(xpath = "//INPUT[@type='text']")
    WebElement ScanQtyTxtfield;
	@FindBy(xpath = "//span[contains(text(),'BACK')]")
    WebElement BackButton;
	@FindBy(xpath = "//span[contains(text(),'EXIT')]")
    WebElement exitButton;
	@FindBy(xpath = "//span[text()='END TOTE']")
    WebElement endToteButton;
	@FindBy(xpath = "//span[text()='END LOCATION']")
    WebElement endLocationButton;
	
//Scan Loacation
	

	@FindBy(xpath = "//STRONG[text()='Tote ID :']/parent::div[1]")
	private WebElement ToteId;
	@FindBy(xpath = "//INPUT[@type='text']")
    WebElement ScanLocation;
	
	//Scan tote Id
	


	@FindBy(xpath = "//strong[text()='Scan Tote ID :']")
    WebElement ScanToteId;
	@FindBy(xpath = "//INPUT[@type='text']")
    WebElement ScanToteTxtfield;

	
	 public WebDriverWait getWait(int waitTime) {
	        WebDriverWait wait = new WebDriverWait(driver, waitTime);
	        return wait;
	    }

	
	public void scanTote(String tote) throws InterruptedException {
		getWait(5).until(ExpectedConditions.visibilityOf(ScanToteTxtfield));
		ScanToteTxtfield.clear();
		Thread.sleep(5000);
		ScanToteTxtfield.sendKeys(tote);
		Thread.sleep(3000);
		ScanToteTxtfield.sendKeys(Keys.ENTER);
	}
public void scanTotepressEntr() {
		
	ScanToteTxtfield.sendKeys(Keys.ENTER);
	}
	public void scannullTote() {
		getWait(5).until(ExpectedConditions.visibilityOf(ScanToteTxtfield));
		ScanToteTxtfield.clear();
		ScanToteTxtfield.sendKeys(Keys.ENTER);
	}
	 public void clickButton(String button) {
	        if(button.equalsIgnoreCase("back")){
	        	getWait(5).until(ExpectedConditions.visibilityOf(BackButton));
	        	BackButton.click();
	        }
	        else if (button.equalsIgnoreCase("exit")){
	        	getWait(5).until(ExpectedConditions.visibilityOf(exitButton));
				exitButton.click();
	        }
	    }
	public void scanLocation(String location) throws InterruptedException {
		getWait(5).until(ExpectedConditions.visibilityOf(ScanLocation));
		ScanLocation.clear();
		Thread.sleep(5000);
		ScanLocation.sendKeys(location);
		Thread.sleep(5000);
		ScanLocation.sendKeys(Keys.ENTER);
	}
	public void scanCompleteQty() throws InterruptedException {
		getWait(5).until(ExpectedConditions.visibilityOf(Qty));
		String qty = Qty.getText().split(":")[1];
		ScanQtyTxtfield.clear();
		Thread.sleep(5000);
		ScanLocation.sendKeys(qty);
		Thread.sleep(5000);
		ScanLocation.sendKeys(Keys.ENTER);
	}
	
}
