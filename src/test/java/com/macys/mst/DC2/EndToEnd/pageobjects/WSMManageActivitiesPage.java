package com.macys.mst.DC2.EndToEnd.pageobjects;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WSMManageActivitiesPage extends BasePage {

	public WSMManageActivitiesPage(WebDriver driver) {
		super(driver);
	}
    
	private SeUiContextBase seUiContextBase = new SeUiContextBase();
	
	private String cancelConfirmationMsg = "One or more activities successfully updated";
	@FindBy(xpath = "//*[div[@id='listItemGroup']][div/div/span[text()='WSM']]/li")
	private List<WebElement> wsmMenuList;

	public String reportingResponse = null;

	@FindBy(xpath = "//button[span/text()='Cancel']")
	WebElement cancelButton;
	
	@FindBy(xpath = "//*[@row-id=0]/*[@col-id='0']")
	WebElement firstactivitycheckbox;
	
	@FindBy(xpath = "//*[@row-id=0]/*[@col-id='id']")
	WebElement activityid;
	
	@FindBy(xpath = "//*[span/text()='OK']")
	WebElement OKButton;
	
	@FindBy(xpath = "//*[span/text()='UnAssign']")
	WebElement UnAssignButton;
	
	@FindBy(xpath = "//*[span/text()='Selected activities have been released']")
	WebElement unassignAlert;
	
	@FindBy(xpath = "//span[contains(text(),'PO Dashboard')]")
	WebElement poDashboard;

	@FindBy(xpath = "//*[@id='containerId']")
	WebElement containerId;

	@FindBy(xpath = "//*[@id='activityId']")
	WebElement activityId;

	@FindBy(xpath = "//*[@id='upc']")
	WebElement SKU;

	@FindBy(xpath = "//*[@id='attributeValue']")
	WebElement attributeValue;

	@FindBy(xpath = "//*[@id='select-status']")
	WebElement statusType;

	@FindBy(xpath = "//*[@id='select-type']")
	WebElement activityType;

	@FindBy(xpath = "//*[@id='select-attributeName']")
	WebElement attributeName;

	@FindBy(xpath = "//*[@id='menu-status']//*[@role='listbox']")
	WebElement statusTypeMenu;

	@FindBy(xpath = "//*[@id='menu-type']//*[@role='listbox']")
	WebElement activityTypeMenu;

	@FindBy(xpath = "//*[@id='menu-attributeName']//*[@role='listbox']")
	WebElement attributeNameMenu;

	@FindBy(xpath = "//*[@id='assignedTo']")
	WebElement assignedUser;

	@FindBy(xpath = "//*[@id='breadcrumbContainer']/div/button")
	WebElement breadcrumb;

	@FindBy(xpath = "//*[@id='menu-containerType']//*[@role='listbox']")
	private WebElement containerTypeMenu;

	@FindBy(xpath = ".//div[contains(@ref, 'gridPanel')]")
	WebElement gridPanel;

	@FindBy(id = "gridToolbarContainer")
	WebElement gridToolbar;

	@FindBy(xpath = "//*[@id='searchBox']")
	private WebElement searchBox;

	@FindBy(xpath = "//*[contains(@id,'select')]")
	private WebElement selectDropdown;

	@FindBy(xpath = "div[(@col-id='oscUnits')]//div[@class='div-percent-bar']")
	private WebElement oscUnits;

	@FindBy(xpath = "//button[(@ref='btNext')]")
	private WebElement btNextButton;

	private By searchByInputs = By.xpath(".//*[@id='searchBox']/div/div/div[2]/div/div/div/div");

	@FindBy(id = "searchButton")
	private WebElement searchButton;
	
	@FindBy(id = "clearButton")
	private WebElement clearButton;

	@FindBy(xpath = "//*[@id='select-containerType']")
	WebElement containerType;

    @FindBy(xpath = "//*[@role='dialog']//*[@id='confirmation-dialog-title']/h5")
    private WebElement confirmationPopUpTitle;

    @FindBy(xpath = "//*[@role='dialog']//h6")
    private WebElement confirmationPopUpText;

	public void clickSearchButton() {
		waitForProcessing(5);
		waitForElement(By.xpath("//button[(@id = 'searchButton') and not(@disabled)]"), 5);
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

	public void selectContainerType(String containertypeName) {
		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(containerType));
			containerType.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(containerTypeMenu));
			jsClick(driver.findElement(
					By.xpath("//*[@id='menu-containerType']//li[@data-value='" + containertypeName + "']")));
			waitForProcessing();
			log.info("Selected ContainerType: " + containertypeName);
			StepDetail.addDetail("Selected ContainerType: " + containertypeName, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select ContainerType", false);
			Assert.fail("Unable to select ContainerType");
		}

	}

	public void selectContainerID(String containerIDInput) {

		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(containerId));
			containerId.sendKeys(containerIDInput);
			log.info("Selected ContainerId: " + containerIDInput);
			StepDetail.addDetail("Selected ContainerId: " + containerIDInput, true);

		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select ContainerId", false);
			Assert.fail("Unable to select ContainerId");
		}

	}

	public void selectAssignedUser(String assignedUserName) {

		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(assignedUser));
			assignedUser.sendKeys(assignedUserName);
			log.info("Selected AssignedUser: " + assignedUserName);
			StepDetail.addDetail("Selected AssignedUser: " + assignedUserName, true);

		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select AssignedUser", false);
			Assert.fail("Unable to select AssignedUser");
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
			seUiContextBase.waitFor(5);
			log.info("Selected Status: " + statusTypeName);
			StepDetail.addDetail("Selected Status: " + statusTypeName, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select Status", false);
			Assert.fail("Unable to select Status");
		}

	}

	public void selectActivityType(String ActivityTypeInput) {
		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(activityType));
			activityType.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(activityTypeMenu));
			jsClick(driver.findElement(By.xpath("//*[@id='menu-type']//li[@data-value='" + ActivityTypeInput + "']")));
			waitForProcessing();
			log.info("Selected Activity Type: " + ActivityTypeInput);
			StepDetail.addDetail("Selected Activity Type: " + ActivityTypeInput, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select Activity Type", false);
			Assert.fail("Unable to select Activity Type");
		}

	}

	public void selectActivityID(String activityIDType) {
		try {

			getWait(60).until(ExpectedConditions.visibilityOf(activityId));
			activityId.sendKeys(activityIDType);

			log.info("Selected ActivityID: " + activityIDType);
			StepDetail.addDetail("Selected ActivityID: " + activityIDType, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select ActivityID", false);
			Assert.fail("Unable to select ActivityID");
		}

	}

	public void selectSKU(String SkuInput) {
		try {

			getWait(60).until(ExpectedConditions.visibilityOf(SKU));
			SKU.sendKeys(SkuInput);

			log.info("Selected SKU: " + SkuInput);
			StepDetail.addDetail("Selected SKU: " + SkuInput, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select SKU", false);
			Assert.fail("Unable to select SKU");
		}

	}

	public void selectAttributeName(String AttributeNameInput) {

		try {

			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(attributeName));
			attributeName.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(attributeNameMenu));
			jsClick(driver.findElement(
					By.xpath("//*[@id='menu-attributeName']//li[@data-value='" + AttributeNameInput + "']")));
			waitForProcessing();
			log.info("Selected Attribute Name: " + AttributeNameInput);
			StepDetail.addDetail("Selected AttributeName: " + AttributeNameInput, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select AttributeName", false);
			Assert.fail("Unable to select Attribute Name");
		}

	}

	public void selectAttributeValue(String AttributeValueInput) {

		try {

			getWait(60).until(ExpectedConditions.visibilityOf(attributeValue));
			attributeValue.sendKeys(AttributeValueInput);

			log.info("Selected Attribute Value: " + AttributeValueInput);
			StepDetail.addDetail("Selected Attribute Value: " + AttributeValueInput, true);
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to select Attribute Value", false);
			Assert.fail("Unable to select attribute Value");
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

	public void unAssignWSMActivity(String assignedActivityID) {
		try {
			scrollElementIntoView(driver.findElement(By.xpath("//*[@id='searchBox']")));
			
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(firstactivitycheckbox));
			firstactivitycheckbox.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(UnAssignButton));
			UnAssignButton.click();
			getWait(60).until(ExpectedConditions.visibilityOf(unassignAlert));
		    getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(OKButton));
			OKButton.click();
		
		    log.info("Unassigned  activity ID is {}",assignedActivityID);
			StepDetail.addDetail("Unassigned activity ID is"+assignedActivityID, true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("unable to click clear button", false);
			Assert.fail("unable to click clear button");
		    
		}
	}

	public String cancelFirstWSMActivity() {
		String activityID = null;
		try {
			scrollElementIntoView(driver.findElement(By.xpath("//*[@class='ag-theme-material']")));
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(firstactivitycheckbox));
			firstactivitycheckbox.click();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(activityid));
			activityID = activityid.getText();
			getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(cancelButton));
			cancelButton.click();
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
		    Assert.assertTrue(confirmationPopUpText.getText().contains(cancelConfirmationMsg));
		    getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(OKButton));
			OKButton.click();			
			CommonUtils.doJbehavereportConsolelogAndAssertion("Activity ID is cancelled: ", activityID, true);
			
		} catch (Exception e) {
			e.printStackTrace();
			CommonUtils.doJbehavereportConsolelogAndAssertion("Activity ID is cancelled: ", activityID, false);
		}
		
		return activityID;
	}

	public Integer validateUIactivitycount(Integer activityCount,String activityType) {
		
		List<WebElement> activityIDsList = driver.findElements(By.xpath("//*[@col-id='id'][@role='gridcell']"));
		Integer activityIDsCount = 0;
		try{
		activityIDsCount = activityIDsList.size();
		}
		
		catch (Exception e) {
				e.printStackTrace();
				StepDetail.addDetail("unable to retreive activityIDs", false);
				Assert.fail("unable to retreive activityIDs");
			}
		return activityIDsCount;
		}

}
