package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.PageObject;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public class PutAwayScanlocationPage extends PageObject{
	private SeUiContextBase seUiContextBase = new SeUiContextBase();

	public PutAwayScanlocationPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//b[contains(text(),'Putaway Pallet')]")
	private WebElement putawayPallet;

	@FindBy(xpath = "//ol/li/a/b[contains(text(),'Pallet')]")
	private WebElement PalletLinkText;


	@FindBy(xpath = "//span[text()='Back']")
	WebElement backButton;

	@FindBy(xpath = "//span[text()='Exit']")
	WebElement exitButton;

	@FindBy(xpath = "//span[contains(text(),'Back')]")
	WebElement palletBackButton;

	@FindBy(xpath = "//span[contains(text(),'Exit')]")
	WebElement palletExitButton;

	@FindBy(xpath = "//input[@type='text']")
	WebElement scanPallet;

	@FindBy(xpath = "//input[@type='text']")
	private WebElement scanLocn;

	public void buttonClick() {
		getWait(10).until(ExpectedConditions.visibilityOf(putawayPallet));
		putawayPallet.click();
	}

	public WebDriverWait getWait(int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		return wait;
	}

	public void selectStorageLcn(WebDriver driver) {
		seUiContextBase.waitFor(5);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, backButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, exitButton);
		getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(PalletLinkText));
		PalletLinkText.click();


	}
	
	public void selectStorageLcn(String locationType) {
		seUiContextBase.waitFor(5);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, backButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, exitButton);
    	By locationTypeLocator = By.linkText(locationType);
    	getWait(20).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(driver.findElement(locationTypeLocator)));
        driver.findElement(locationTypeLocator).click();
        log.info("Selected Location Type: "+locationType);
        StepDetail.addDetail("Selected Location Type: " + locationType, true);

	}

	public void scanPallet(String pallet) {
		seUiContextBase.waitFor(3);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, palletExitButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, palletBackButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, scanPallet);
		scanPallet.clear();
		StepDetail.addDetail("Barcode: "+ pallet, true);
		SeUiContextBase.sendkeys(scanPallet, pallet);
		scanPallet.sendKeys(Keys.ENTER);
	}

	public void scanPutAwayLocn(String location){
		seUiContextBase.waitFor(3);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, exitButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, backButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, scanLocn);
		scanLocn.sendKeys(location);
		scanPallet.sendKeys(Keys.ENTER);
		StepDetail.addDetail("Location: "+ location, true);
	}

	public void clickButton(String button) {
		log.info("Clicking on button  :" + button);
		if (button.equalsIgnoreCase("Exit"))
			palletExitButton.click();
	}
}
