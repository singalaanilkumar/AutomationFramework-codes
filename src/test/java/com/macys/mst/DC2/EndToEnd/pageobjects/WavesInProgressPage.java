package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.foundationalServices.utils.TearDownMethods;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class WavesInProgressPage extends BasePage {

    TearDownMethods tearDownMethods = new TearDownMethods();
    public WavesInProgressPage(WebDriver driver) {
        super(driver);
    }
    
    private SeUiContextBase seUiContextBase = new SeUiContextBase();
    
    private String UndoWaveAlertMsg = "Are you sure you want to undo the wave?";
    private String UndoWaveConfirmationMsg = "Wave Release Cancelled Successfully";
    private String releaseWaveConfirmationMsg = "Wave Release to Picking Successfully";
    private String closeWaveConfirmationMsg = "Wave closed for wave number ";
    public String reportingResponse = null;
    
    // Wave Menu Elements
    @FindBy(xpath = "//*[div[@id='listItemGroup']][div/div/span[text()='Wave']]/li")
    private List<WebElement> waveMenuList;
    
    // Grid elements
    @FindBy(xpath = "(//*[@id='gridContainer'])[2]")
    private WebElement gridContainer;

    @FindBy(xpath = "//*[@col-id='waveNbr']")
    private List<WebElement> waveNumberList;

    // Page Buttons
    @FindBy(xpath = "//*[@type='button'][*[contains(text(),'Undo Wave')]]")
    private WebElement undoWaveButton;
    
    @FindBy(xpath = "//*[@type='button'][*[contains(text(),'Release to Pick')]]")
    private WebElement releaseToPick;

    @FindBy(xpath = "//*[@id='clearButton']")
    private WebElement clearButton;

    @FindBy(xpath = "//*[text()='OK']")
    private WebElement okButton;
    
    @FindBy(xpath = "//*[text()='Yes']")
    private WebElement yesButton;
    
    @FindBy(xpath = "//*[text()='No']")
    private WebElement noButton;
    
    @FindBy(xpath = "//*[@type='button'][*[contains(text(),'Close')]]")
    private WebElement closeWave;
    
    @FindBy(xpath = "//*[@id='waveNbr']")
    private WebElement waveNbrBox;
 
    @FindBy(xpath = "//*[@id='Search WaveButton']")
    private WebElement searchButton;
    
    // PopUp Message elements
    @FindBy(xpath = "//*[@role='dialog']//*[@id='confirmation-dialog-title']/h5")
    private WebElement confirmationPopUpTitle;
    
    @FindBy(xpath = "//*[@role='dialog']//h6")
    private WebElement confirmationPopUpText;
    
    // Alert Message elements
    @FindBy(xpath = "//*[@role='dialog']//*[@id='alert-dialog-title']/h6")
    private WebElement alertPopUpTitle;
    
    @FindBy(xpath = "//*[@role='dialog']//p")
    private WebElement alertPopUpText;

    @FindBy(xpath = "//span[contains(text(),'Waves in Progress')]")
    WebElement waveDashboard;

    @FindBy(xpath = "//Input[@name='poNbr']")
    private WebElement poNbrBox;

    @FindBy(xpath = "//button[span/text()='Close']")
    private WebElement closeButton;

    @FindBy(xpath = "//button[span/text()='Release']")
    private WebElement releaseButton;

    @FindBy(xpath = "div[(@col-id='poRcptStatus')]")
    private WebElement poRcptStatus;

    @FindBy(xpath = "//*[@ref='lbTotal']")
    private WebElement totalPages;

    @FindBy(xpath = "(//*[@ref='eBodyHorizontalScrollViewport'])[2]")
    WebElement horizontalScrollBar;

    @FindBy(xpath = "//*[@id='waveNbr']")
    WebElement waveNbr;

    @FindBy(xpath = "//*[@id='Search WaveButton']")
    WebElement SearchWave;

    @FindBy(xpath = "//button[contains(@class,'GenerateWaveDashDetailsPath__StyledButton-hn3rx9-0')]")
    WebElement clickWave;


    public void selectWaveNumber(String waveNumber) {
    	seUiContextBase.waitFor(2);
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(gridContainer));
        scrollElementIntoView(driver.findElement(By.xpath("//*[span[text()='Undo Wave']]")));
    	boolean waveNumberFound = false;
        for (WebElement waveNumberRow : waveNumberList) {
            if (waveNumber.equals(waveNumberRow.getText())) {
            	String rowID = waveNumberRow.findElement(By.xpath("./..")).getAttribute("row-id");
            	getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")));
                driver.findElement(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")).click();
            	waveNumberFound = true;
            	break;
            } 
        }
        Assert.assertTrue("WaveID not displayed: "+waveNumber, waveNumberFound);
    }
   
    public void clickUndoWaveButton() {
            getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(undoWaveButton));
            undoWaveButton.click();
            waitForProcessing();
            log.info("Clicked on Undo Wave button");
            StepDetail.addDetail("Clicked on Undo Wave button", true);
    }
    
    public void clickReleaseToPickWaveButton() {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(releaseToPick));
        releaseToPick.click();
        waitForProcessing();
        log.info("Clicked on ReleaseToPick Wave button");
        StepDetail.addDetail("Clicked on ReleaseToPick Wave button", true);
}

    public void UndoWaveValidation(String waveNumber) {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
        try {
            Assert.assertTrue(confirmationPopUpText.getText().contains(UndoWaveConfirmationMsg));
            StepDetail.addDetail("Undo wave message displayed for Wave Number: "+waveNumber, true);
            okButton.click();
            waitForProcessing();
            Assert.assertTrue(!isWaveNumberDisplayed(waveNumber));
            StepDetail.addDetail("Wave Number got removed Successfully "+waveNumber, true);
        } catch (Exception e) {
            log.info("Exception occured while doing Undo wave: "+e.getMessage());
            Assert.assertTrue(false);
        }
    }
    
    public void releaseWaveValidation(String waveNumber) {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
        try {
            Assert.assertTrue(confirmationPopUpText.getText().contains(releaseWaveConfirmationMsg));
            StepDetail.addDetail("Release wave message displayed for Wave Number: "+waveNumber, true);
            okButton.click();
            waitForProcessing();
            StepDetail.addDetail("Wave Number got Released Successfully "+waveNumber, true);
        } catch (Exception e) {
            log.info("Exception occured while doing Releasing wave: "+e.getMessage());
            Assert.assertTrue(false);
        }
    }
    
    public void UndoWaveAlertValidation(String option) {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(alertPopUpTitle));
        try {
            Assert.assertTrue(alertPopUpText.getText().contains(UndoWaveAlertMsg));
            if("Yes".equalsIgnoreCase(option))
            	yesButton.click();
            else
            	noButton.click();
            waitForProcessing();
        } catch (Exception e) {
            log.info("Exception occured while doing Undo wave: "+e.getMessage());
            Assert.assertTrue(false);
        }
    }

    public boolean isWaveNumberDisplayed(String waveNumber) {
        boolean flag = false;
        try {
            getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(gridContainer));
            for (WebElement waveNumberRow : waveNumberList) {
                if (waveNumber.equals(waveNumberRow.getText())) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }
            }
        } catch (Exception e) {
            log.info("Exception occured while waveNbr validation in Undo wave");
            return flag;
        }
        return flag;
    }

    public void clickOKButton() {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(okButton));
        okButton.click();
        log.info("Clicking on Confirmation button");
        StepDetail.addDetail("Clicking on confirmation button", true);
    }
	
    public void clickCloseWaveButton() {
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(closeWave));
        closeWave.click();
        waitForProcessing();
        log.info("Clicked on Close Wave button");
        StepDetail.addDetail("Clicked on Close Wave button", true);
        
    }
 
    public void closeWaveValidation(String waveNumber) {
        seUiContextBase.waitFor(5);
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
        try {
            Assert.assertTrue(confirmationPopUpText.getText().contains(closeWaveConfirmationMsg));
            StepDetail.addDetail("Close wave message displayed for Wave Number: "+waveNumber, true);
            okButton.click();
            waitForProcessing();
            StepDetail.addDetail("Wave Number got Closed Successfully "+waveNumber, true);
        } catch (Exception e) {
            log.info("Exception occured while closing wave: "+e.getMessage());
            Assert.assertTrue(false);
        }
        
    }
 
    public void verifyWaveStatus(String status,String waveNumber) {
        seUiContextBase.waitFor(2);
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(gridContainer));
        scrollElementIntoView(driver.findElement(By.xpath("//*[span[text()='Close']]")));
        boolean waveNumberFound = false;
        for (WebElement waveNumberRow : waveNumberList) {
            if (waveNumber.equals(waveNumberRow.getText())) {
                String rowID = waveNumberRow.findElement(By.xpath("./..")).getAttribute("row-id");
                getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")));
                String waveStatus = driver.findElement(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='waveLifeCycleStatusDesc']")).getText();
                CommonUtils.doJbehavereportConsolelogAndAssertion("Wave Status is Closed. WaveNumber:", waveNumber, waveStatus.equalsIgnoreCase("Closed"));
                waveNumberFound = true;
                break;
            }       
        }
        Assert.assertTrue("WaveID not displayed: "+waveNumber, waveNumberFound);
        
    }
 
    public void searchwithWaveNumber(String waveNumber) {
                try {
                    getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(gridContainer));
                    seUiContextBase.waitFor(10);
                    WebElement waveSelectionBox = driver.findElement(By.id("searchBox"));
                    scrollElementIntoView(waveSelectionBox);
                    seUiContextBase.waitFor(5);
                    getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(waveNbrBox));
                    waveNbrBox.click();
                    waveNbrBox.sendKeys(waveNumber);
                    getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(searchButton));
                    searchButton.click();
                    waitForProcessing();     
                    log.info("Searched with WaveNumber: " + waveNumber);
                    StepDetail.addDetail("Searched with WaveNumber: " + waveNumber, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    StepDetail.addDetail("Unable to Search with WaveNumber", false);
                    Assert.fail("Unable to Search with WaveNumber");
                }        
        
    }

    public List<Map<String, String>> getGridElementsMapAllPages() {
        List<Map<String, String>> dbGridMapList = new ArrayList<Map<String, String>>();
        try {
            selectRowsPerPage("10");
            Integer currentPgeNbr = 1;
            Integer totalPageCount = getTotalPageCountforWave();
            for (int k = 1; k <= totalPageCount; k++) {
                currentPgeNbr = getCurrentPageNumber();
                if (currentPgeNbr <= totalPageCount) {
                    dbGridMapList.addAll(getGridElementsMapforWave(3));
                    if (currentPgeNbr < totalPageCount)
                        break;
                    else if (currentPgeNbr == totalPageCount) {
                        navigateToFirstPage();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getLocalizedMessage());
        }
        return dbGridMapList;
    }

    public Integer getTotalPageCountforWave(){
        try {
            List<WebElement> totalpagecount=driver.findElements(By.xpath("//*[@ref='lbTotal']"));
            return Integer.valueOf(totalpagecount.get(1).getText());
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return 0;
        }
    }

    public Integer getCurrentPageNumber(){
        try {
            List<WebElement> currentpagecount=driver.findElements(By.xpath("//*[@ref='lbCurrent']"));
            return Integer.valueOf(currentpagecount.get(1).getText());
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return 0;
        }
    }


    public boolean navigateToNextPage(){
        try {
            List<WebElement> nextpagecount=driver.findElements(By.xpath("//*[text()='Next']"));
            nextpagecount.get(1).click();
            return true;
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return false;
        }
    }

    public List<Map<String, String>> getGridElementsMapforWave(Integer scrollCountMax){
        getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//*[@ref='eBodyViewport'])[2]")));
        scrollElementIntoView(driver.findElement(By.xpath("(//*[@ref='eBodyViewport'])[2]")));
        Map<String,String> colIDnameMap = new HashMap<String,String>();
        Map<String,String> colIDvalueMap = new HashMap<String,String>();
        Map<String,Map<String, String>> colNameValueMapList = new HashMap<String,Map<String,String>>();

        try {
            int scrollCount = 0;
            while(scrollCount<scrollCountMax){
                System.out.println("scrollCount: "+scrollCount);
                List<WebElement> headerSecondRowCells = driver.findElements(By.xpath("((//*[@role='presentation' and @ref='headerRoot']//*[@ref='eHeaderContainer'])[2]//div[@class='ag-header-row'])[2]/*[@col-id!='0']"));
                for(int i=0;i<headerSecondRowCells.size();i++){
                    try {
                        getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("((//*[@role='presentation' and @ref='headerRoot']//*[@ref='eHeaderContainer'])[2]//div[@class='ag-header-row'])[2]/*[@col-id!='0']["+(i+1)+"]")));
                        WebElement headerSecondRowCell = driver.findElement(By.xpath("((//*[@role='presentation' and @ref='headerRoot']//*[@ref='eHeaderContainer'])[2]//div[@class='ag-header-row'])[2]/*[@col-id!='0']["+(i+1)+"]"));
                        String headerColID = headerSecondRowCell.getAttribute("col-id");
                        String headerName = "";
                        switch(headerColID){
                            case "units.sorted":
                                headerName = "Sorted Units";
                                break;
                            case "units.containerToBeSort":
                                headerName = "Sorted Remaining containers";
                                break;
                            case "units.put":
                                headerName = "Put Units";
                                break;
                            case "units.containerToBePut":
                                headerName = "PUT Remaining containers";
                                break;
                            default:
                                headerName = headerSecondRowCell.getText();
                        }
                        if(StringUtils.isNotBlank(headerColID) && StringUtils.isNotBlank(headerName) && !headerColID.equals("waveStatus"))
                            colIDnameMap.put(headerColID,headerName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("(//*[@ref='eCenterContainer'])[2]//*[@role='row']")));
                List<WebElement> valueRows = driver.findElements(By.xpath("(//*[@ref='eCenterContainer'])[2]//*[@role='row']"));
                for(int i=0;i<valueRows.size();i++){
                    try {
                        getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//*[@ref='eCenterContainer'])[2]//*[@role='row']["+(i+1)+"]")));
                        WebElement valueRow = driver.findElement(By.xpath("(//*[@ref='eCenterContainer'])[2]//*[@role='row']["+(i+1)+"]"));
                        String rowID = valueRow.getAttribute("row-id");
                        colIDvalueMap = colNameValueMapList.containsKey(rowID)?colNameValueMapList.get(rowID):(new HashMap<String,String>());
                        List<WebElement> valueCells = valueRow.findElements(By.xpath(".//*[@role='gridcell' and @col-id!='0']"));
                        for(int j=0;j<valueCells.size();j++){
                            try {
                                getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//*[@ref='eCenterContainer'])[2]//*[@role='row']["+(i+1)+"]//*[@role='gridcell' and @col-id!='0']["+(j+1)+"]")));
                                WebElement valueCell = driver.findElement(By.xpath("(//*[@ref='eCenterContainer'])[2]//*[@role='row']["+(i+1)+"]//*[@role='gridcell' and @col-id!='0']["+(j+1)+"]"));
                                String colID = valueCell.getAttribute("col-id");
                                String colText = (StringUtils.isEmpty(valueCell.getText()))?"":valueCell.getText();
                                if(StringUtils.isNotBlank(colID) && colIDnameMap.containsKey(colID)){
                                    colIDvalueMap.put(colIDnameMap.get(colID), StringUtils.normalizeSpace(colText.trim()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        colNameValueMapList.put(rowID,colIDvalueMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                scrollGridTableRight();
                scrollCount++;
            }
            log.info(colIDnameMap.toString());
            scrollCount = 0;
            while(scrollCount<scrollCountMax){
                scrollGridTableLeft();
                scrollCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colNameValueMapList.values().stream().collect(Collectors.toList());
    }

    public void scrollGridTableLeft(){
        try {
            scrollElementIntoView(driver.findElement(By.xpath("(//*[@ref='eBodyHorizontalScrollViewport'])[2]")));
            getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@ref='eBodyHorizontalScrollViewport'])[2]")));
            Rectangle horizontalScrollBarRect = horizontalScrollBar.getRect();
            new Actions(driver).moveToElement(horizontalScrollBar, -(horizontalScrollBarRect.width/2)+22, (horizontalScrollBarRect.height/2)).click().build().perform();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrollGridTableRight(){
        try {
            scrollElementIntoView(driver.findElement(By.xpath("(//*[@ref='eBodyHorizontalScrollViewport'])[2]")));
            getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@ref='eBodyHorizontalScrollViewport'])[2]")));
            Rectangle horizontalScrollBarRect = horizontalScrollBar.getRect();
            new Actions(driver).moveToElement(horizontalScrollBar,  (horizontalScrollBarRect.width/2)-22, (horizontalScrollBarRect.height/2)).click().build().perform();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectAttributeValue(String WAVE_NBR) {
        try {
            getWait(60).until(ExpectedConditions.visibilityOf(waveNbr));
            waveNbr.sendKeys(WAVE_NBR);
            log.info("Selected waveNumber Value: " + WAVE_NBR);
            StepDetail.addDetail("Selected waveNumber Value: " + WAVE_NBR, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select waveNumber Value", false);
            org.testng.Assert.fail("Unable to select waveNumber Value");
        }

    }

    public void searchWave() {
        seUiContextBase.waitFor(2);
        scrollToTop();
        getWait(60).until(ExpectedConditions.elementToBeClickable(SearchWave)).click();
    }

    public void clickWave()
    {
        getWait().until(ExpectedConditions.visibilityOf(clickWave)).click();
        getWait().until(ExpectedConditions.urlContains("waveDetails"));

    }
}