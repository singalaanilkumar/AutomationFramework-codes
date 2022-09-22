package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.configuration.LocationService;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
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
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.macys.mst.DC2.EndToEnd.utilmethods.Constants.STORAGE_TYPE;
import static com.macys.mst.DC2.EndToEnd.utilmethods.Constants.STORAGE_TYPE_VALUE;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

@Slf4j
public class PackAwaySortPage extends PageObject{

	public static final String BIN_BARCODE = "binBarcode";
	public static final String LOCATION_TYPE = "locationType";
	public static final String BARCODE = "barcode";

	SeUiContextBase seUiContextBase = new SeUiContextBase();

	public PackAwaySortPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//b[contains(text(),'ICQA')]")
	private WebElement icqa;

	@FindBy(xpath = "//b[contains(text(),'Pack Away Sorting')]")
	private WebElement packAwaySorting;

	@FindBy(xpath = "//input[@type='text']")
	public WebElement scantext;

	@FindBy(xpath = "//strong[contains(text(),'Scan Location')]")
	private WebElement scanLocationLabel;

	@FindBy(xpath = "//input[@type='text']")
	private WebElement locationScanTextBox;

	@FindBy(xpath = "//input[@type='text']")
	private WebElement palletScanTextBox;

	@FindBy(xpath = "//strong[contains(text(),'Scan Pallet')]")
	private WebElement scanPalletLabel;

	@FindBy(xpath = "//strong[contains(text(),'Container')]/parent::div")
	private WebElement binBox;

	@FindBy(xpath = "//strong[contains(text(),'Container')]")
	private WebElement binBoxLabel;

	@FindBy(xpath = "//strong[contains(text(),'Scan Location')]/parent::div")
	private WebElement scanLocation;

	@FindBy(xpath = "//SPAN[text()='Back']" )
	WebElement backButton;

	@FindBy(xpath = "//SPAN[text()='Exit']")
	WebElement exitButton;

	@FindBy(xpath="//strong[contains(text(),'Scan Container')]")
	WebElement scanBinLabel;

	@FindBy(xpath = "//span[contains(text(),'Close Pallet')]")
	private WebElement closePALLETButton;

	@FindBy(xpath = "//strong[contains(text(),'Scan Pallet')]/parent::div")
	private WebElement scanPallet_Value;
	
	//strong[contains(text(),'Scan Location :')]
	@FindBy(xpath = "//strong[contains(text(),'Scan Location')]/parent::div")
	private WebElement scanLocation_Value;
	
	@FindBy(xpath = "//strong[starts-with(text(),'Location')]/parent::div")
	public WebElement Location_Value;

	@FindBy(xpath = "//strong[contains(text(),'Pallet')]/parent::div")
	private WebElement pallet_Value;

	@FindBy(xpath = "//strong[contains(text(),'Container')]/parent::div")
	private WebElement scannedBin;

	@FindBy(xpath = "//input[@type='text']")
	public WebElement scanLocation_Input;

	@FindBy(xpath = "//input[@type='text']")
	private WebElement scanLocn;

	@FindBy(xpath = "//*[starts-with(text(),'Do you')]")
	private WebElement pop_message1;

	@FindBy(xpath = "//*[starts-with(text(),'Do you')]")
	private WebElement pop_message2;

	@FindBy(xpath = "/html[1]/body[1]/div[3]/div[2]/div[1]/div[1]/p[1]")
	private WebElement autoclose;

	@FindBy(xpath = "//*[contains(text(), 'DC2.0 RF Options')]")
	WebElement rfOptions;

	public String ExpectedpopMessage1() {
		String Expected1 = seUiContextBase.storeText(pop_message1);
		return Expected1;
	}

	public String ExpectedpopMessage2() {
		String Expected2 = seUiContextBase.storeText(pop_message2);
		return Expected2;
	}

	public void buttonClick() {
		getWait(10).until(ExpectedConditions.visibilityOf(packAwaySorting));
		packAwaySorting.click();
	}

	public void scanBinBox(String BinBox) {
		SeUiContextBase.sendkeys(scantext,BinBox);
		scantext.sendKeys(Keys.ENTER);
	}

	public void scanExistPallet(String Pallet){
		SeUiContextBase.sendkeys(scantext,Pallet);
		scantext.sendKeys(Keys.ENTER);
	}

	public void scanLocationBarcode(String barcode) {
		getWait(10).until(ExpectedConditions.visibilityOf(scanLocationLabel));
		locationScanTextBox.clear();
		locationScanTextBox.sendKeys(barcode);
		locationScanTextBox.sendKeys(Keys.ENTER);
		StepDetail.addDetail("Scanned location barcode " + barcode, true);
	}

	public void scanHalfPalletLocation(String loc){
		getWait(10).until(ExpectedConditions.visibilityOf(scanLocationLabel));
		locationScanTextBox.clear();
		locationScanTextBox.sendKeys(loc);
		locationScanTextBox.sendKeys(Keys.ENTER);
		StepDetail.addDetail("Scanned half pallet location barcode " + loc, true);
	}

	public Map<String, String> getDetailsFromPage() {
		getWait(30);
		getWait(30).until(ExpectedConditions.visibilityOf(scanLocationLabel));

		Map<String, String> pageDetails = new HashMap<>();
		pageDetails.put(BIN_BARCODE, binBox.getText().replace(binBoxLabel.getText(), "").trim());
		pageDetails.put(LOCATION_TYPE, scanLocation.getText().replace(scanLocationLabel.getText(), "").trim());
		log.info("Details from page : " + pageDetails);
		StepDetail.addDetail("Fetched details from page", true);

		return pageDetails;
	}
	public WebDriverWait getWait(int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		return wait;
	}

	public void scanPalletBarcode(String barcode) {
		getWait(10).until(ExpectedConditions.visibilityOf(scanPalletLabel));
		palletScanTextBox.clear();
		palletScanTextBox.sendKeys(barcode);
		palletScanTextBox.sendKeys(Keys.ENTER);
		StepDetail.addDetail("Scanned Pallet barcode " + barcode, true);
	}

	public void validateScreenComponents() throws Exception {
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, scanBinLabel);
		Assert.assertEquals(seUiContextBase.storeText(scanBinLabel), "Scan Container :");
		StepDetail.addDetail("Validated screen labels", true);
		Assert.assertTrue(SeUiContextBase.isElementDisplayed(backButton), "Back button not displayed");
		Assert.assertTrue(SeUiContextBase.isElementDisplayed(exitButton), "Exit button not displayed");
		StepDetail.addDetail("Validated Back and Exit buttons", true);
	}

	public void NavigateToScanPallet() throws Exception {
		getWait(20);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, exitButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, backButton);
		getWait(20);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, closePALLETButton);
		if (SeUiContextBase.isElementDisplayed(scanPallet_Value) && SeUiContextBase.isElementDisplayed(Location_Value) && SeUiContextBase.isElementDisplayed(pallet_Value)
				&& SeUiContextBase.isElementDisplayed(scannedBin)) {
			Assert.assertTrue(true);
			StepDetail.addDetail("Displayed  And Validated PackAwaySort: ", true);
		} else
			Assert.assertTrue(false);
	}
	
	public void NavigateToScanLocation() throws Exception {
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, exitButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, backButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, closePALLETButton);
		if (SeUiContextBase.isElementDisplayed(scantext) && SeUiContextBase.isElementDisplayed(Location_Value) && SeUiContextBase.isElementDisplayed(pallet_Value)
				&& SeUiContextBase.isElementDisplayed(scannedBin)) {
			Assert.assertTrue(true);
			StepDetail.addDetail("Displayed  And Validated PackAwaySort: ", true);
		} else
			Assert.assertTrue(false);
	}

	public void ScanPallet() {
		ScanPalletWithOutEnter();
		scanLocation_Input.sendKeys(Keys.ENTER);
	}

	public void ScanPalletWithOutEnter() {
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, Location_Value);
		String palletValue=seUiContextBase.storeText(pallet_Value).substring(9);
		log.info("OPEN pallet:" + palletValue);
		StepDetail.addDetail("Displayed OPEN pallet: " + palletValue, true);
		SeUiContextBase.sendkeys(scanLocation_Input,palletValue);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, scanLocation_Input);
	}
	
	public void ScanLocation() {
		ScanLocationWithOutEnter();
		scanLocation_Input.sendKeys(Keys.ENTER);
	}

	public void ScanLocationWithOutEnter() {
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, Location_Value);
		String LocationValue=seUiContextBase.storeText(Location_Value).substring(11);
		log.info("OPEN Location:" + LocationValue);
		StepDetail.addDetail("Displayed OPEN Location: " + LocationValue, true);
		SeUiContextBase.sendkeys(scanLocation_Input,LocationValue);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, scanLocation_Input);
	}

	public void validateErrorPopup(String type, String expMessage) {
		FluentWait<WebDriver> wait = getWait(5).withTimeout(Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[text()='" + type + "']")));

		Assert.assertEquals(true, driver.findElement(By.xpath("//h6[text()='" + type + "']")).isDisplayed());
		Assert.assertEquals(true, driver.findElement(By.xpath("//h5[text()='" + expMessage + "']")).isDisplayed());
		Assert.assertEquals(true, driver.findElement(By.xpath("//span[contains(text(), 'CLOSE')]")).isDisplayed());
		log.info("expMessage Displayed : " + expMessage);
		StepDetail.addDetail("expMessage Displayed : " + expMessage, true);
		driver.findElement(By.xpath("//span[contains(text(), 'CLOSE')]")).click();
	}

	public void autoClose(String expMessage, String YES) {

		if (expMessage.equalsIgnoreCase("Do you want to close the pallet?") && YES.equalsIgnoreCase("yes")) {
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='YES']")));
			Assert.assertEquals(ExpectedpopMessage1(), "Do you want to close the pallet?");
			log.info("expMessage Displayed : " + ExpectedpopMessage1());
			StepDetail.addDetail("expMessage Displayed : " + ExpectedpopMessage1(), true);
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='YES']"))).click();
		}

		if (expMessage.equalsIgnoreCase("Do you want to locate the Pallet?") && YES.equalsIgnoreCase("NO")) {
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='NO']")));
			Assert.assertEquals(ExpectedpopMessage2(), "Do you want to locate the Pallet?");
			log.info("expMessage Displayed : " + ExpectedpopMessage2());
			StepDetail.addDetail("expMessage Displayed : " + ExpectedpopMessage2(), true);
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='NO']"))).click();
		}

		if (expMessage.equalsIgnoreCase("Do you want to locate the Pallet?") && YES.equalsIgnoreCase("YES")) {
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='YES']")));
			Assert.assertEquals(ExpectedpopMessage2(), "Do you want to locate the Pallet?");
			log.info("expMessage Displayed : " + ExpectedpopMessage2());
			StepDetail.addDetail("expMessage Displayed : " + ExpectedpopMessage2(), true);
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='YES']"))).click();
		}

		if (expMessage.equalsIgnoreCase("Do you want to close the pallet") && YES.equalsIgnoreCase("No")) {
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[2]/div[2]/div[1]/div[1]/p[1]")));
			Assert.assertEquals(ExpectedpopMessage1(), "Do you want to close the pallet");
			log.info("expMessage Displayed : " + ExpectedpopMessage1());
			StepDetail.addDetail("expMessage Displayed : " + ExpectedpopMessage1(), true);
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[2]/div[2]/div[1]/div[2]/button[2]/span[1]"))).click();
		}

		if (expMessage.equalsIgnoreCase("Auto closing the Pallet") && YES.equalsIgnoreCase("Yes")){
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[3]/div[2]/div[1]/div[1]/p[1]")));
			Assert.assertEquals(autoclose.getText(), "Auto closing the Pallet");
			log.info("expMessage Displayed : " + ExpectedpopMessage1());
			StepDetail.addDetail("expMessage Displayed : " + autoclose.getText(), true);
			getWait(5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/div[3]/div[2]/div[1]/div[2]/button[1]/span[1]"))).click();
		}
	}

	public void validateAndScanPutAwayLocn(String fromLocation) {
		seUiContextBase.waitFor(3);

		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, exitButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, backButton);
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, scanLocn);

		LocationService locationService = new LocationService();
		HashMap<String, String> responseMap;
		String toLocation = null;

		if (fromLocation != null) {
			// Validating location is available in the service.
			responseMap = locationService.callLocationService(fromLocation);
			String stType = responseMap.get(STORAGE_TYPE_VALUE);
			if (responseMap.get("description").equals("SUCCESS")) {
				log.info("valid Location" + fromLocation);
				Assert.assertTrue(true);
			} else {
				log.info("invalid Location" + fromLocation);
				Assert.assertTrue(true);
			}
			toLocation = getStageLocation(stType).get(BARCODE);
			scanLocn.sendKeys(toLocation);
			String stType1 = locationService.callLocationService(toLocation).get(STORAGE_TYPE_VALUE);
			if (stType1.equals(stType)) {
				Assert.assertTrue(true);
				scanLocn.sendKeys(Keys.ENTER);
			} else {
				log.info("Mismatch Storage type for " + fromLocation + "and "+ toLocation);
				StepDetail.addDetail("Mismatch Storage type" + fromLocation, true);
				scanLocn.sendKeys(Keys.ENTER);
				Assert.assertTrue(false);
			}
			StepDetail.addDetail("location" + fromLocation, true);
		}
	}

	public void ClickOnClosePallet() {
		SeUiContextBase.Wait_Until_Element_Is_Visible(driver, closePALLETButton);
		seUiContextBase.clickElement(closePALLETButton);
	}

	public void clickButton(String button) {
		log.info("Clicking on button  :" + button);
		if (button.equalsIgnoreCase("Exit"))
			exitButton.click();
	}

	public Map<String, String> getStageLocation(String type) {
		Optional<Map<String, String>> result = ExpectedDataProperties.getStagingLocationsMap().stream().filter(loc -> {
			return type.equalsIgnoreCase(loc.get(STORAGE_TYPE));
		}).findAny();
		return result.isPresent() ? result.get() : null;
	}

	public void selectPackAwaySortingFromMainMenu(){
		rfOptions.click();
		getWait(20).ignoring(Exception.class).until(elementToBeClickable(packAwaySorting));
		log.info("driver = " + driver.getCurrentUrl());
		log.info("driver = " + driver.getTitle());
	}
}
