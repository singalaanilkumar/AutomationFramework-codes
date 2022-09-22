package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WorkloadPlanningUIPage extends BasePage {

    public WorkloadPlanningUIPage(WebDriver driver) {
        super(driver);
    }
    
    private SeUiContextBase seUiContextBase = new SeUiContextBase();
    
    private String CancelRequestConfirmationMsg = "Preview Cancel requested by user";
    private String RunWaveConfirmationMsg = "Wave Submitted for Processing successfully for wave number";
        
    // Wave Menu Elements
    @FindBy(xpath = "//*[div[@id='listItemGroup']][div/div/span[text()='Wave']]/li")
    private List<WebElement> waveMenuList;
    
    // Wave Select Option Elements
    @FindBy(xpath = "//*[@id='select-waveTypeName']")
    private WebElement waveType;
    
    @FindBy(xpath = "//*[@id='menu-waveTypeName']//*[@role='listbox']")
    private WebElement waveTypeMenu;
    
    @FindBy(xpath = "//*[@id='select-storeType']")
    private WebElement storeType;
    
    @FindBy(xpath = "//*[@id='menu-storeType']//*[@role='listbox']")
    private WebElement storeTypeMenu;
    
    @FindBy(xpath = "//*[@id='select-dept']")
    private WebElement waveDept;
    
    @FindBy(xpath = "//*[@id='menu-dept']//*[@role='listbox']")
    private WebElement waveDeptMenu;
    
    @FindBy(xpath = "//*[@id='shipOutDate']")
    private WebElement shipOutDate;

    @FindBy(xpath = "//div[div/label[@for='shipOutDate']]//div[@aria-roledescription='datepicker']")
    private WebElement shipOutDatePicker;
    
    @FindBy(xpath = "(//div[div/label[@for='shipOutDate']]//div[@data-visible='true']//strong)[1]")
    private WebElement shipOutStartMonth;
    
    @FindBy(xpath = "(//div[div/label[@for='shipOutDate']]//div[@data-visible='true']//strong)[2]")
    private WebElement shipOutEndMonth;
    
    @FindBy(xpath = "//div[div/label[@for='shipOutDate']]//div[contains(@aria-label,'previous month')]")
    private WebElement shipOutPrevMonthArrow;
    
    @FindBy(xpath = "//div[div/label[@for='shipOutDate']]//div[contains(@aria-label,'next month')]")
    private WebElement shipOutNextMonthArrow;

    @FindBy(xpath = "//*[@id='effectiveDate']")
    private WebElement effectiveDate;

    @FindBy(xpath = "//*[@id='effectiveDate']/parent::div/parent::div/following-sibling::div")
    private WebElement effectiveDatePicker;
    
    @FindBy(xpath = "(//div[div/label[@for='effectiveDate']]//div[@data-visible='true']//strong)[1]")
    private WebElement effectiveStartMonth;
    
    @FindBy(xpath = "(//div[div/label[@for='effectiveDate']]//div[@data-visible='true']//strong)[2]")
    private WebElement effectiveEndMonth;
    
    @FindBy(xpath = "//div[div/label[@for='effectiveDate']]//div[contains(@aria-label,'previous month')]")
    private WebElement effectivePrevMonthArrow;
    
    @FindBy(xpath = "//div[div/label[@for='effectiveDate']]//div[contains(@aria-label,'next month')]")
    private WebElement effectiveNextMonthArrow;

    @FindBy(xpath = "//*[(@id='nbrOfOrder')]")
    private WebElement nbrOfOrder;

    @FindBy(xpath = "//*[(@id='nbrOfUnitPick')]")
    private WebElement nbrOfUnitPick;
    
    @FindBy(xpath = "//*[(@id='division')]")
    private WebElement division;

    @FindBy(xpath = "//*[(@id='flowType')]")
    private WebElement flowType;
    
    // Graph elements
    @FindBy(xpath = "//div[div/div/h5[text()='Today']]")
    private WebElement todayGraph;
    
    @FindBy(xpath = "//div[div/div/h5[text()='Total']]")
    private WebElement totalGraph;
    
    // Grid elements
    @FindBy(xpath = "//*[@id='gridContainer']")
    private WebElement gridContainer;

    @FindBy(xpath = "//*[@col-id='id']")
    private List<WebElement> waveIDList;

    // Page Buttons
    @FindBy(xpath = "//*[@id='Wave SelectionButton']")
    private WebElement previewWaveButton;
  
    @FindBy(xpath = "//*[contains(text(),'Cancel Preview')]")
    private WebElement cancelPreviewButton;

    @FindBy(xpath = "//*[contains(text(),'Run Wave')]")
    private WebElement runWaveButton;
    
    @FindBy(xpath = "//*[@id='clearButton']")
    private WebElement clearButton;

    @FindBy(xpath = "//*[text()='OK']")
    private WebElement okButton;
    
    // PopUp Message elements
    @FindBy(xpath = "//*[@role='dialog']//*[@id='confirmation-dialog-title']/h5")
    private WebElement confirmationPopUpTitle;
    
    @FindBy(xpath = "//*[@role='dialog']//h6")
    private WebElement confirmationPopUpText;
    
    @FindBy(xpath = "//*[@role='dialog']//h6")
    private WebElement cancelConfirmationPopUpText;

    public void selectWaveType(String waveTypeName) {
        try {
        	getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(todayGraph));
        	getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(totalGraph));
        	seUiContextBase.waitFor(10);
            WebElement waveSelectionBox = driver.findElement(By.id("createBox"));
        	scrollElementIntoView(waveSelectionBox);
        	seUiContextBase.waitFor(5);
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(waveType));
            waveType.click();
            getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(waveTypeMenu));
            jsClick(driver.findElement(By.xpath("//*[@id='menu-waveTypeName']//li[@data-value='" + waveTypeName +"']")));
            waitForProcessing();     
            log.info("Selected WaveType: " + waveTypeName);
            StepDetail.addDetail("Selected WaveType: " + waveTypeName, true);
        } catch (Exception e) {
        	e.printStackTrace();
            StepDetail.addDetail("Unable to select WaveType", false);
            Assert.fail("Unable to select WaveType");
        }        
    }
    public void selectDept(List<String> depts) {
    	getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(waveDept));
    	seUiContextBase.waitFor(10);
        waveDept.click();
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(waveDeptMenu));
        List<String> deptsToBeSelected = new ArrayList<String>(depts);
        
        List<WebElement> deptCheckBoxElements = driver.findElements(By.xpath("//*[@id='menu-dept']/div[2]/ul/li//*[@type='checkbox']"));
        
        //Unchecks all pre-selected depts
        for(WebElement element: deptCheckBoxElements){
        	if(element.isSelected()){
        		jsClick(element);
        	}
        }
        //Selects required depts
        List<WebElement> deptElements = driver.findElements(By.xpath("//*[@id='menu-dept']/div[2]/ul/li"));
        for(WebElement deptElement: deptElements) {
        	if(depts.contains(deptElement.getAttribute("data-value"))){
        		getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(deptElement));
        		jsClick(deptElement);
        		deptsToBeSelected.remove(deptElement.getAttribute("data-value"));
        	}
        }    	
    	if(deptsToBeSelected.size()!=0){
    		Assert.assertTrue("Depts not selected/found: "+deptsToBeSelected,false);
    	}
        deptElements.get(0).sendKeys(Keys.ESCAPE);
        log.info("Departments selected: " + depts);
        StepDetail.addDetail("Departments selected: " + depts, true);
    }
    
    public void selectStoreType(String storeTypeName) {
        try {
        	seUiContextBase.waitFor(5);
            getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(storeType));
            storeType.click();
            getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(storeTypeMenu));
            jsClick(driver.findElement(By.xpath("//*[@id='menu-storeType']//*[@role='listbox']/li[@data-value='" + storeTypeName +"']")));
            log.info("Selected Store Type: " + storeTypeName);
            StepDetail.addDetail("Selected Store Type: " + storeTypeName, true);
        } catch (Exception e) {
        	e.printStackTrace();
            StepDetail.addDetail("Unable to select Store Type", false);
            Assert.fail("Unable to select Store Type");
        }

    }

    public void selectWaveID(String waveID) {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(gridContainer));
        scrollElementIntoView(driver.findElement(By.xpath("//*[span[text()='Run Wave']]")));
        boolean waveIDFound = false;
        for (WebElement waveIDrow : waveIDList) {
            if (waveID.equals(waveIDrow.getText())) {
            	String rowID = waveIDrow.findElement(By.xpath("./..")).getAttribute("row-id");
            	getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")));
                driver.findElement(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")).click();
                waveIDFound = true;
                break;
            } 
        }
        Assert.assertTrue("WaveID not displayed: "+waveID, waveIDFound);
    }

    public void cancelPreviewButton() {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(cancelPreviewButton));
        try {
            cancelPreviewButton.click();
        } catch (Exception e) {
            log.info("cancelPreview button is not enabled");
            Assert.assertTrue(false);
        }
    }

    public void cancelPreviewValidation(String waveID) {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
        try {
            Assert.assertEquals(CancelRequestConfirmationMsg, confirmationPopUpText.getText());
            StepDetail.addDetail("Cancel wave Confirmatoin is displayed as expected ", true);
            okButton.click();
            boolean iswaveIdRemoved = isWaveIDDisplayed(waveID);
            Assert.assertTrue(!iswaveIdRemoved);
            StepDetail.addDetail("WaveId got removed Successfully ", true);
        } catch (Exception e) {
            log.info("cancelPreview button is not working as expected");
            Assert.assertTrue(false);
        }
    }

    public String validateRunWaveMsg(String waveID) {
    	String waveNumber = "";
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
        try {
            String Runwave_confirmatation_txt = confirmationPopUpText.getText();
            if (Runwave_confirmatation_txt.contains(RunWaveConfirmationMsg)) {
                Assert.assertTrue(true);
                StepDetail.addDetail("Run wave Confirmatoin is displayed as expected ", true);
                waveNumber = Runwave_confirmatation_txt.split("number")[1].trim();
                StepDetail.addDetail("waveNumber: " + waveNumber, true);
                okButton.click();
                Assert.assertTrue("WaveID still displayed on the waveID List.",!isWaveIDDisplayed(waveID));
                StepDetail.addDetail("WaveId got removed Successfully ", true);
            } else {
                log.info("Run wave is not working as expected");
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            log.info("Exception occured while doing Run wave");
            Assert.assertTrue(false);
        }
        return waveNumber;
    }

    public boolean isWaveIDDisplayed(String waveID) {
        boolean flag = false;
        try {
            getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(gridContainer));
            for (WebElement waveIDrow : waveIDList) {
                if (waveID.equals(waveIDrow.getText())) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
        } catch (Exception e) {
            log.info("Exception occured while waveID validation");
            return flag;
        }
        return flag;
    }
    
    public String validatePreviewWaveMsg() {
    	String waveID = "";
        try {
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
            if (confirmationPopUpText.getText().contains("Preview wave has be created successfully for wave id")) {
                waveID = confirmationPopUpText.getText().split("id")[1].trim();
                //log.info("test"+waveID);
    			clickOKButton();
                StepDetail.addDetail("WaveID generated: " + waveID, true);
            }else{
            	Assert.assertTrue("Unable to Preview Wave: "+confirmationPopUpText.getText(),false);
            }
        } catch (Exception e) {
            log.info("Error while getting confirmation pop up text", e.getMessage());
            Assert.assertTrue("Unable to Preview Wave: "+e.getMessage(),false);
        }
		return waveID;
    }


	public void selectShipoutDateRange(String startDate, String endDate) {
		seUiContextBase.waitFor(5);
		getWait(20).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(shipOutDate));
		shipOutDate.click();
		getWait(20).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(shipOutDatePicker));		
		try {

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat monthYearFormat = new SimpleDateFormat("MMMMM yyyy");
			DateFormat dayFormat = new SimpleDateFormat("d");
			
			Assert.assertTrue("Start Date: "+startDate+" cannot be greater than EndDate: "+endDate, dateFormat.parse(startDate).equals(dateFormat.parse(endDate)) || dateFormat.parse(startDate).before(dateFormat.parse(endDate)));
					
			String startMonthYear = monthYearFormat.format(dateFormat.parse(startDate));
			String startDay = dayFormat.format(dateFormat.parse(startDate));
			
			int count = 0;
			if(monthYearFormat.parse(startMonthYear).before(monthYearFormat.parse(shipOutStartMonth.getText()))){
				while (!startMonthYear.equals(shipOutStartMonth.getText())) {
					count++;
					shipOutPrevMonthArrow.click();
					if (count > 20)
						break;
				}
			}else if(monthYearFormat.parse(startMonthYear).after(monthYearFormat.parse(shipOutStartMonth.getText()))){
				while (!startMonthYear.equals(shipOutStartMonth.getText())) {
					count++;
					shipOutNextMonthArrow.click();
					if (count > 20)
						break;
				}
			}
			seUiContextBase.waitFor(2);
			jsClick(driver.findElement(By.xpath("//div[div/label[@for='shipOutDate']]//div[div/strong[text()='" + startMonthYear
					+ "']]/table/tbody/tr/td[text()=" + startDay + "]")));
			seUiContextBase.waitFor(2);

			if (!startDate.equals(endDate)) {
				String endMonthYear = monthYearFormat.format(dateFormat.parse(endDate));
				String endDay = dayFormat.format(dateFormat.parse(endDate));
				count = 0;
				if(monthYearFormat.parse(endMonthYear).before(monthYearFormat.parse(shipOutEndMonth.getText()))){
					while (!endMonthYear.equals(shipOutEndMonth.getText())) {
						count++;
						shipOutPrevMonthArrow.click();
						if (count > 20)
							break;
					}
				}else if(monthYearFormat.parse(endMonthYear).after(monthYearFormat.parse(shipOutEndMonth.getText()))){
					while (!endMonthYear.equals(shipOutEndMonth.getText())) {
						count++;
						shipOutNextMonthArrow.click();
						if (count > 20)
							break;
					}
				}
				seUiContextBase.waitFor(2);
				jsClick(driver.findElement(By.xpath("//div[div/label[@for='shipOutDate']]//div[div/strong[text()='"
						+ endMonthYear + "']]/table/tbody/tr/td[text()=" + endDay + "]")));
				log.info("Selected shipoutDates: {},{}",startDate,endDate);
				StepDetail.addDetail("Selected shipoutDates: "+startDate+","+endDate, true);
			}else{
				nbrOfOrder.click();
			}
			seUiContextBase.waitFor(2);
		} catch (Exception e) {
			log.info("Unable to select shipoutDate",e.getStackTrace());
			StepDetail.addDetail("Unable to select shipoutDate", false);
			Assert.fail("Unable to select shipoutDate");
		}
	}

	public void selectEffectiveDateRange(String startDate, String endDate) {
		seUiContextBase.waitFor(5);
		getWait(20).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(effectiveDate));
		effectiveDate.click();
		getWait(20).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(effectiveDatePicker));		
		try {

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat monthYearFormat = new SimpleDateFormat("MMMMM yyyy");
			DateFormat dayFormat = new SimpleDateFormat("d");
			
			Assert.assertTrue("Start Date: "+startDate+" cannot be greater than EndDate: "+endDate, dateFormat.parse(startDate).equals(dateFormat.parse(endDate)) || dateFormat.parse(startDate).before(dateFormat.parse(endDate)));
					
			String startMonthYear = monthYearFormat.format(dateFormat.parse(startDate));
			String startDay = dayFormat.format(dateFormat.parse(startDate));
			
			int count = 0;
			if(monthYearFormat.parse(startMonthYear).before(monthYearFormat.parse(effectiveStartMonth.getText()))){
				while (!startMonthYear.equals(effectiveStartMonth.getText())) {
					count++;
					effectivePrevMonthArrow.click();
					if (count > 20)
						break;
				}
			}else if(monthYearFormat.parse(startMonthYear).after(monthYearFormat.parse(effectiveStartMonth.getText()))){
				while (!startMonthYear.equals(effectiveStartMonth.getText())) {
					count++;
					effectiveNextMonthArrow.click();
					if (count > 20)
						break;
				}
			}
			seUiContextBase.waitFor(2);
			jsClick(driver.findElement(By.xpath("//div[div/label[@for='effectiveDate']]//div[div/strong[text()='" + startMonthYear
					+ "']]/table/tbody/tr/td[text()=" + startDay + "]")));
			seUiContextBase.waitFor(2);

			if (!startDate.equals(endDate)) {
				String endMonthYear = monthYearFormat.format(dateFormat.parse(endDate));
				String endDay = dayFormat.format(dateFormat.parse(endDate));
				count = 0;
				if(monthYearFormat.parse(endMonthYear).before(monthYearFormat.parse(effectiveEndMonth.getText()))){
					while (!endMonthYear.equals(effectiveEndMonth.getText())) {
						count++;
						effectivePrevMonthArrow.click();
						if (count > 20)
							break;
					}
				}else if(monthYearFormat.parse(endMonthYear).after(monthYearFormat.parse(effectiveEndMonth.getText()))){
					while (!endMonthYear.equals(effectiveEndMonth.getText())) {
						count++;
						effectiveNextMonthArrow.click();
						if (count > 20)
							break;
					}
				}
				seUiContextBase.waitFor(2);
				jsClick(driver.findElement(By.xpath("//div[div/label[@for='effectiveDate']]//div[div/strong[text()='"
						+ endMonthYear + "']]/table/tbody/tr/td[text()=" + endDay + "]")));
				log.info("Selected holdDates: {},{}",startDate,endDate);
				StepDetail.addDetail("Selected holdDates: "+startDate+","+endDate, true);
			}else{
				nbrOfUnitPick.click();
			}
			seUiContextBase.waitFor(2);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Unable to select effectiveDate",e.getStackTrace());
			StepDetail.addDetail("Unable to select effectiveDate", false);
			Assert.fail("Unable to select effectiveDate");
		}
	}
	
    public void selectNoOfOrders(String NumberofOrders) {
        try {
        	seUiContextBase.waitFor(2);
        	getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(nbrOfOrder));
        	nbrOfOrder.sendKeys(Keys.CONTROL + "a");
        	nbrOfOrder.sendKeys(Keys.DELETE);
            nbrOfOrder.sendKeys(NumberofOrders);
            log.info("NumberofOrders selected is: " + NumberofOrders);
            StepDetail.addDetail("NumberofOrders selected is:" + NumberofOrders, true);
        } catch (Exception e) {
            StepDetail.addDetail("Unable to select NumberofOrders", false);
            Assert.fail("Unable to select NumberofOrders");
        }
    }

    public void selectNoOfUnitsPick(String NumberofUnitsPick) {
        try {
        	seUiContextBase.waitFor(2);
        	getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(nbrOfUnitPick));
        	nbrOfUnitPick.sendKeys(Keys.CONTROL + "a");
        	nbrOfUnitPick.sendKeys(Keys.DELETE);
        	nbrOfUnitPick.sendKeys(NumberofUnitsPick);
            log.info("NumberofUnitsPick selected is: " + NumberofUnitsPick);
            StepDetail.addDetail("NumberofUnitsPick selected is:" + NumberofUnitsPick, true);
        } catch (Exception e) {
            StepDetail.addDetail("Unable to select NumberofUnitsPick", false);
            Assert.fail("Unable to select NumberofUnitsPick");
        }
    }
    
    public void clickClearButton() {
    	scrollElementIntoView(driver.findElement(By.xpath("//*[@id='createBox']")));
        getWait(30).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(clearButton));
        clearButton.click();
        log.info("Clicked on Clear button");
        StepDetail.addDetail("Clicked on Clear button", true);
    }

    public void clickPreviewWaveButton() {
        getWait(30).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(previewWaveButton));
        previewWaveButton.click();
        waitForProcessing();
        log.info("Clicked on Preview Wave button");
        StepDetail.addDetail("Clicked on Preview Wave button", true);
    }

    public void clickRunWaveButton() {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(runWaveButton));
        runWaveButton.click();
        waitForProcessing();
        log.info("Clicked on Run Wave button is Successfull");
        StepDetail.addDetail("Clicked on Run Wave button is Successfull", true);
    }

    public void clickOKButton() {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(okButton));
        okButton.click();
        log.info("Clicked on Confirmation button");
        StepDetail.addDetail("Clicked on confirmation button", true);
    }
   
	public void validateCancelPreviewWaveMsg() {
		try {
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
            if (confirmationPopUpText.getText().contains("Preview Cancel requested by user")) {                
    			clickOKButton();
    			log.info("Preview Cancel is performed successfully");
                StepDetail.addDetail("Preview Cancel is performed successfully", true);
            }else{
            	Assert.assertTrue("Unable to Preview Wave: "+confirmationPopUpText.getText(),false);
            }
        } catch (Exception e) {
            log.info("Preview Cancel is not performed successfully", e.getMessage());            
        }
		
	}
}