package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class ASNReceiptsPage extends BasePage {

	public ASNReceiptsPage(WebDriver driver) {
		super(driver);
	}

	private SeUiContextBase seUiContextBase = new SeUiContextBase();

	CommonUtils commonUtils = new CommonUtils();
	BasePage basePage = new BasePage();

	@FindBy(xpath = "//*[@type='submit']")
	WebElement submitButton;

	@FindBy(xpath = "//button[span/text()='Cancel']")
	WebElement cancelButton;

	@FindBy(xpath = "//*[@row-id=0]/*[@col-id='0']")
	WebElement firstactivitycheckbox;

	@FindBy(xpath = "//*[@row-id=0]/*[@col-id='id']")
	WebElement activityid;

	@FindBy(xpath = "//*[span/text()='One or more activities successfully updated']")
	WebElement cancelAlert;

	@FindBy(xpath = "//*[span/text()='OK']")
	WebElement OKButton;

	@FindBy(xpath = "//*[@id='breadcrumbContainer']/div/button")
	WebElement breadcrumb;

	@FindBy(xpath = "//*[@id='activityId']")
	WebElement activityId;

	public By filterResultTable = By.id("gridSection");

	@FindBy(xpath = ".//div[contains(@ref, 'gridPanel')]")
	WebElement gridPanel;

	@FindBy(xpath = "//*[@id='searchBox']")
	private WebElement searchBox;

	@FindBy(xpath = "//*[contains(@id,'select')]")
	private WebElement selectDropdown;

	private By searchByInputs = By.xpath(".//*[@id='searchBox']/div/div/div[2]/div/div/div/div");

	@FindBy(id = "searchButton")
	private WebElement searchButton;

	@FindBy(id = "clearButton")
	private WebElement clearButton;

	@FindBy(xpath = "//*[contains(text(),'Are you sure you want to Close this Receipt?')]")
	private WebElement closeReceiptAlert;

	@FindBy(xpath = "//*[contains(text(),'CLOSE RECEIPT')]")
	private WebElement closeReceiptButton;

	@FindBy(xpath = "//*[contains(text(),'OK')]")
	private WebElement OkButton;

	@FindBy(xpath = "//*[contains(text(),'Close Receipt Initiated')]")
	private WebElement receiptClosedConfirmationMsg;
	
	@FindBy(xpath = "//*[@role='alertdialog']//span[contains(text(),'CLOSE')]")
	private WebElement AlertClose;
	
	@FindBy(xpath = "//*[@col-id='age']//div[@class='ag-cell-label-container ag-header-cell-sorted-none']")
	private WebElement ageOrder;
	
	public List<String> getPageBreadcrumb() {
		getWait(10).until(visibilityOf(breadcrumb));
		List<String> displayedButtons = new ArrayList<>();
		List<WebElement> breadcrumbTextList = driver.findElements((By) breadcrumb);
		for (WebElement item : breadcrumbTextList)
			displayedButtons.add(item.getText());
		return displayedButtons;
	}





	public WebDriverWait getWait(int secs) {
		WebDriverWait wait = new WebDriverWait(driver, secs);
		return wait;
	}

	public void sendEscape() {
		try {
			Actions action = new Actions(driver);
			action.sendKeys(Keys.ESCAPE).build().perform();
		} catch (Exception e) {
			log.info("Save alert not displayed");
		}

	}


	public void clickClearButton() {
		try {
			scrollElementIntoView(driver.findElement(By.xpath("//*[@id='searchBox']")));
			getWait(60).until(ExpectedConditions.elementToBeClickable(clearButton));
			clearButton.click();

			log.info("clearButton is clicked ");
			StepDetail.addDetail("Clear button is clicked ", true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("unable to click clear button", false);
			Assert.fail("unable to click clear button");

		}


	}

	protected void scrollElementIntoView(WebElement webElement) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView(true);", webElement);
	}

	public void closeReceipt(String receiptNumber) {
		getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']")));
		getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(ageOrder));
		ageOrder.click();
		List<WebElement> valueRows = driver.findElements(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']"));

		for(int i=0;i<valueRows.size();i++){
			try {
				getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']["+(i+1)+"]")));
				WebElement valueRow = driver.findElement(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']["+(i+1)+"]"));
				getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']")));
				WebElement receiptNbrUI = valueRow.findElement(By.xpath(".//*[@role='gridcell' and @col-id='receiptNbr']"));
				String rcptNbr = receiptNbrUI.getText();
				if(receiptNumber.equalsIgnoreCase(String.valueOf(receiptNbrUI.getText()))) {
					WebElement  closeButton = valueRow.findElement(By.xpath(".//*[@role='gridcell' and @col-id='closeReceipt']//button"));	   
					getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(closeButton));
					closeButton.click();
					getWait(270).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(closeReceiptAlert));
					log.info("Close receipt confirmation button is displayed");
					getWait(270).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(closeReceiptButton));
					closeReceiptButton.click();
					getWait(270).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(receiptClosedConfirmationMsg));
					AlertClose.click();
				}
	}catch (Exception e) {
				e.printStackTrace();
				StepDetail.addDetail("unable to click close receipt button", false);
				Assert.fail("unable to click close receipt button");
			}
		}
	}

	
}
