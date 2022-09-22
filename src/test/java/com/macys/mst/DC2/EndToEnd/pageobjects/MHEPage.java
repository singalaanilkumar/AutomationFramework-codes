package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLMessage;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class MHEPage extends BasePage {

	public MHEPage(WebDriver driver) {
		super(driver);
	}

	private SeUiContextBase seUiContextBase = new SeUiContextBase();
	
	CommonUtils commonUtils = new CommonUtils();
	BasePage basePage = new BasePage();

	@FindBy(xpath = "//*[@id='breadcrumbContainer']/div/button")
	WebElement breadcrumb;

	@FindBy(xpath = "//*[@id='searchBox']")
	private WebElement searchBox;

	@FindBy(xpath = "//span[contains(text(),'MHE Search')]")
	private WebElement mheSearch;

	@FindBy(xpath = "//*[contains(@id,'select')]")
	private WebElement selectDropdown;

	private By searchByInputs = By.xpath(".//*[@id='searchBox']/div/div/div[2]/div/div/div/div");

	@FindBy(id = "searchButton")
	private WebElement searchButton;
	
	@FindBy(id = "clearButton")
	private WebElement clearButton;
	
	@FindBy(xpath = "//*[@id='select-messageType']")
	WebElement messageType;
	
	@FindBy(xpath = "//*[@id='menu-messageType']//*[@role='listbox']")
	private WebElement messageTypeMenu;
	
	@FindBy(xpath = "//*[@id='transactionName']")
	WebElement transactionName;
	
	@FindBy(xpath = "//*[@id='select-status']")
	WebElement statusType;
	
	@FindBy(xpath = "//*[@id='menu-status']//*[@role='listbox']")
	WebElement statusTypeMenu;
	
	@FindBy(xpath = "//*[@id='textFilter']")
	WebElement textFilter;
	
	@FindBy(xpath = "//*[@id='sequenceNumber']")
	WebElement sequenceNumber;
	
	@FindBy(xpath = "//*[@id='trxRange']")
    private WebElement trxRange;
	
	@FindBy(xpath = "//div[div/label[@for='trxRange']]//div[@aria-roledescription='datepicker']")
    private WebElement trxRangeDatePicker;
   
    @FindBy(xpath = "(//div[div/label[@for='trxRange']]//div[@data-visible='true']//strong)[1]")
    private WebElement trxRangeStartMonth;	
    
    @FindBy(xpath = "(//div[div/label[@for='trxRange']]//div[@data-visible='true']//strong)[2]")
    private WebElement trxRangeEndMonth;
    
    @FindBy(xpath = "//div[div/label[@for='trxRange']]//div[contains(@aria-label,'previous month')]")
    private WebElement trxRangePrevMonthArrow;	
    
    @FindBy(xpath = "//div[div/label[@for='trxRange']]//div[contains(@aria-label,'next month')]")
    private WebElement trxRangeNextMonthArrow;
    
    @FindBy(xpath = "//*[@row-index='0']/*[@col-id='0']")
    private WebElement firstCheckBox;
  
    @FindBy(xpath = "//*[@row-index='0']/*[@col-id='sequenceNumber']")
    private WebElement  firstSequenceID;
  
    @FindBy(xpath = "//button[not(@disabled)][span/text()='DETAILS']")
    private WebElement  detailsButton;
   
    @FindBy(xpath = "//button[not(@disabled)][span/text()='REPROCESS']")
    private WebElement  reprocessButton;
   
    @FindBy(xpath = "//button[span/text()='EDIT']")
    private WebElement  editButton;
 
    @FindBy(xpath = "//button[span/text()='SAVE']")
    private WebElement  saveButton;
  
    @FindBy(xpath = "//textarea[@name='jsonFilter']")
    private WebElement  whmPayload;
  
    @FindBy(xpath = "//textarea[@name='textFilter']")
    private WebElement  wcsPayload;
  
	public void clickSearchButton() {
		waitForProcessing(5);
		waitForElement(By.xpath("//button[(@id = 'searchButton') and not(@disabled)]"), 10);
		searchButton.click();
	}

	public List<String> getPageBreadcrumb() {
		getWait(10).until(visibilityOf(breadcrumb));
		List<String> displayedButtons = new ArrayList<>();
		List<WebElement> breadcrumbTextList = driver.findElements((By) breadcrumb);
		for (WebElement item : breadcrumbTextList)
			displayedButtons.add(item.getText());
		return displayedButtons;
	}

	public void typeIntoInputField(String fieldName, String input) {
		log.info("Entering typeIntoInputField ");
		List<WebElement> searchInputsLocators = driver.findElements(searchByInputs);
		for (WebElement inputField : searchInputsLocators) {
			try {
				String pathToLabel;
				if (fieldName.equals("Department")) {
					pathToLabel = "div/label";
				} else {
					pathToLabel = "label";
				}
				WebElement inputLabel = inputField.findElement(By.xpath(pathToLabel));
				if (inputLabel.getText().equals(fieldName)) {
					log.info("Entering data into field {} ", inputLabel.getText());
					WebElement inputFieldLocator = inputField
							.findElement(By.xpath(".//label[text()='" + fieldName + "']/following-sibling::div/input"));
					inputFieldLocator.click();
					inputFieldLocator.clear();
					inputFieldLocator.sendKeys(input);
					break;
				}
			} catch (Exception ignored) {
			}
		}
	}

	public void setOptionInDropdownnew(String dropDownName, String option) {
		List<WebElement> searchInputsLocators = driver.findElements((By) searchByInputs);
		for (WebElement inputField : searchInputsLocators) {
			try {
				WebElement inputLabel = inputField.findElement(By.xpath("label"));
				if (inputLabel.getText().equals(dropDownName)) {
					getWait(5).until(visibilityOf(selectDropdown));
					selectDropdown.click();
					log.info("Selecting option {}" + option);
					List<WebElement> options = driver.findElements(By.xpath("//*[@id='menu-areaFlow']//li"));
					for (WebElement option1 : options) {
						String optionText = option1.getAttribute("data-value");
						if (option.equals(optionText)) {
							option1.click();
							break;
						}
					}
					break;
				}
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}
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

	public void selectMessageType(String messagetypeName) {
		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(messageType));
			messageType.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(messageTypeMenu));
			jsClick(driver.findElement(By.xpath("//*[@id='menu-messageType']//li[@data-value='" + messagetypeName + "']")));
			waitForProcessing();
			log.info("Selected MessageType: " + messagetypeName);
			StepDetail.addDetail("Selected MessageType: " + messagetypeName, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select MessageType", false);
			Assert.fail("Unable to select MessageType");
		}

	}

	public void selectTransactionName(String transactionNameInput) {

		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(transactionName));
			transactionName.sendKeys(transactionNameInput);
			log.info("Selected transactionName: " + transactionNameInput);
			StepDetail.addDetail("Selected transactionName: " + transactionNameInput, true);

		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select transactionName", false);
			Assert.fail("Unable to select transactionName");
		}

	}

	public void selectTextFilter(String textFilterInput) {

		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(textFilter));
			textFilter.sendKeys(textFilterInput);
			log.info("Selected textFilter: " + textFilterInput);
			StepDetail.addDetail("Selected textFilter: " + textFilterInput, true);

		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select textFilter", false);
			Assert.fail("Unable to select textFilter");
		}

	}

	public void selectStatus(String statusTypeName) {
		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(statusType));
			statusType.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(statusTypeMenu));
			jsClick(driver.findElement(By.xpath("//*[@id='menu-status']//li[@data-value='" + statusTypeName + "']")));
			waitForProcessing();
			List<WebElement> statusElements = driver.findElements(By.xpath("//*[@id='menu-status']/div[2]/ul/li"));
			statusElements.get(0).sendKeys(Keys.ESCAPE);
			log.info("Selected Status: " + statusTypeName);
			StepDetail.addDetail("Selected Status: " + statusTypeName, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select Status", false);
			Assert.fail("Unable to select Status");
		}

	}

	

	public void selectSequenceNum(String sequenceNumInput) {
		try {

			getWait(60).until(ExpectedConditions.visibilityOf(sequenceNumber));
			sequenceNumber.sendKeys(sequenceNumInput);

			log.info("Selected Sequence Number: " + sequenceNumInput);
			StepDetail.addDetail("Selected Sequence Number: " + sequenceNumInput, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select Sequence Number", false);
			Assert.fail("Unable to select Sequence Number");
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
	
	public void selectTRXDateRange(String startDate, String endDate) {
		seUiContextBase.waitFor(5);
		getWait(20).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(trxRange));
		trxRange.click();
		getWait(20).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(trxRangeDatePicker));		
		try {

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat monthYearFormat = new SimpleDateFormat("MMMMM yyyy");
			DateFormat dayFormat = new SimpleDateFormat("d");
			
			Assert.assertTrue(dateFormat.parse(startDate).equals(dateFormat.parse(endDate)) || dateFormat.parse(startDate).before(dateFormat.parse(endDate)), "Start Date: "+startDate+" cannot be greater than EndDate: "+endDate);
					
			String startMonthYear = monthYearFormat.format(dateFormat.parse(startDate));
			String startDay = dayFormat.format(dateFormat.parse(startDate));
			
			int count = 0;
			if(monthYearFormat.parse(startMonthYear).before(monthYearFormat.parse(trxRangeStartMonth.getText()))){
				while (!startMonthYear.equals(trxRangeStartMonth.getText())) {
					count++;
					trxRangePrevMonthArrow.click();
					if (count > 20)
						break;
				}
			}else if(monthYearFormat.parse(startMonthYear).after(monthYearFormat.parse(trxRangeStartMonth.getText()))){
				while (!startMonthYear.equals(trxRangeStartMonth.getText())) {
					count++;
					trxRangeNextMonthArrow.click();
					if (count > 20)
						break;
				}
			}
			seUiContextBase.waitFor(3);
			jsClick(driver.findElement(By.xpath("//div[div/label[@for='trxRange']]//div[div/strong[text()='" + startMonthYear
					+ "']]/table/tbody/tr/td[text()=" + startDay + "]")));
			seUiContextBase.waitFor(3);

			
				String endMonthYear = monthYearFormat.format(dateFormat.parse(endDate));
				String endDay = dayFormat.format(dateFormat.parse(endDate));
				count = 0;
				if(monthYearFormat.parse(endMonthYear).before(monthYearFormat.parse(trxRangeEndMonth.getText()))){
					while (!endMonthYear.equals(trxRangeEndMonth.getText())) {
						count++;
						trxRangePrevMonthArrow.click();
						if (count > 20)
							break;
					}
				}else if(monthYearFormat.parse(endMonthYear).after(monthYearFormat.parse(trxRangeEndMonth.getText()))){
					while (!endMonthYear.equals(trxRangeEndMonth.getText())) {
						count++;
						trxRangeNextMonthArrow.click();
						if (count > 20)
							break;
					}
				}
				seUiContextBase.waitFor(2);
				jsClick(driver.findElement(By.xpath("//div[div/label[@for='trxRange']]//div[div/strong[text()='"
						+ endMonthYear + "']]/table/tbody/tr/td[text()=" + endDay + "]")));
				log.info("Selected trxRange Dates: {},{}",startDate,endDate);
				StepDetail.addDetail("Selected trxRange Dates: "+startDate+","+endDate, true);
			
			seUiContextBase.waitFor(2);
		} catch (Exception e) {
			log.info("Unable to select trxRange Date",e.getStackTrace());
			StepDetail.addDetail("Unable to select trxRange Date", false);
			Assert.fail("Unable to select trxRange Date");
		}
	}

	public String selectSequenceandVerifyDetails() {
		String selectedSequenceId = null;
		try {
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(firstCheckBox));
			firstCheckBox.click();
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(detailsButton));
            detailsButton.click();
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(whmPayload));
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(wcsPayload));
            selectedSequenceId = firstSequenceID.getText();
        	log.info("Details of sequence ID: {} is displayed",selectedSequenceId);
			StepDetail.addDetail("Details displayed for selected sequenceID: "+selectedSequenceId, true);         
            
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to edit and save", false);
			Assert.fail("Unable to edit and save");
		}
		return selectedSequenceId;
			
	}

	public void editAndSaveforPayload() {
		try {
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(editButton));
			editButton.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(whmPayload));
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(saveButton));
            saveButton.click();
        
        	log.info("selected sequence is edited and saved");
			StepDetail.addDetail("selected sequence is edited and saved", true);         
            
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select sequenceID", false);
			Assert.fail("Unable to select sequenceID");
		}
		
	}

	public void reProcessSequence() {
		try {
		getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(firstCheckBox));
		firstCheckBox.click();
		getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(reprocessButton));
		reprocessButton.click();
		log.info("selected sequence is reprocessed");
		StepDetail.addDetail("selected sequence is reprocessed", true);
	  }
		catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select reprocess", false);
			Assert.fail("Unable to select reprocess");
		}
	}

	public String updateSelectedMessagetoFailed() {
		String selectedSequenceId = null;
		try {
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(firstCheckBox));
			firstCheckBox.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(firstSequenceID));
			selectedSequenceId = firstSequenceID.getText();

			TimeUnit.SECONDS.sleep(5);
			String query = String.format(SQLMessage.UPDATE_MESSAGES_TO_FAILED.replace("{SEQUENCEID}", selectedSequenceId));
			log.info("UpdateMessagetoFailed query: {}", query);
			DBMethods.deleteOrUpdateDataBase(query, "messaging");
			log.info("Update Message to Failed status for sequence ID: {}",selectedSequenceId);

		} catch (Exception e) {
			log.error("Update messaging table", e);
		}
		return selectedSequenceId;
	}
	public void selectMHESearch(){
		mheSearch.click();
	}

}
