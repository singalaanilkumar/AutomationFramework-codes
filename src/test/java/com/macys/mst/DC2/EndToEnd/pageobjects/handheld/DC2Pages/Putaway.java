package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.HAFSteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Putaway extends BasePage {

    CommonUtils commonUtils = new CommonUtils();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    
	@FindBy(xpath = "//b[contains(text(),'Putaway Pallet')]")
	private WebElement putawayPallet;

	@FindBy(xpath = "//*[contains(text(),'Pallet')]")
	private WebElement palletLinkText;
	
	@FindBy(xpath = "//*[contains(text(),'Binbox')]")
	private WebElement binboxLinkText;
	
	@FindBy(xpath = "//*[contains(text(),'GOH')]")
	private WebElement gohLinkText;
	
	@FindBy(xpath = "//*[contains(text(),'JWL')]")
	private WebElement jwlLinkText;
	
	@FindBy(xpath = "//*[contains(text(),'Case')]")
	private WebElement caseLinkText;

    @FindBy(xpath = "//*[@id='entryBox']")
    public WebElement scanContainer;

    @FindBy(xpath = "//*[text()='Storage Location :']")
    public WebElement storageLocation;
    
    @FindBy(xpath = "//span[text()='Exit']")
    public WebElement exitButton;
    
    @FindBy(xpath = "//span[text()='End']")
    public WebElement endButton;

    @FindBy(xpath = "//*[@type='text']")
    public WebElement scanputawayloc;

    @FindBy(xpath = "//span[text()='Back']")
    public WebElement backButton;
    
    @FindBy(xpath = "//*[text()='Scan Location :']")
    public WebElement scanLocationLabel;
    
    @FindBy(xpath = "//*[text()='Scan Case :']")
    public WebElement scanCaseLabel;

    public void scanContainerputAway(String putawayloc) throws InterruptedException {

        List<String> caseBarcodeList = (List) dataStorage.getStoredData().get("caseBarcodeList");

        Map<String, String> caseToLocationAllocation = commonUtils.getParamsToMap(putawayloc);
        dataStorage.getStoredData().put("pickLocationAndNumberOfCases",caseToLocationAllocation);

        int totalAllocatedCases = 0;

        for (String key : caseToLocationAllocation.keySet()) {
            totalAllocatedCases += Integer.parseInt(caseToLocationAllocation.get(key));
        }

        if (caseBarcodeList.size() == totalAllocatedCases) {
            int allocStartIndex = 0;
            for (String key : caseToLocationAllocation.keySet()) {
                int allocEndIndex = allocStartIndex + Integer.parseInt(caseToLocationAllocation.get(key)) - 1;
                String ptwLoc = key;
                boolean firstContainerFlag = true;
                for (; allocStartIndex < caseBarcodeList.size(); allocStartIndex++) {
                    String caseBarcode = caseBarcodeList.get(allocStartIndex);
                    pageLoadWait();
                    getWait().ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(scanCaseLabel));
                    scanContainer.clear();
                    scanContainer.sendKeys(caseBarcode, Keys.ENTER);
                    pageLoadWait();
                    if(firstContainerFlag){
                    	getWait().until(ExpectedConditions.visibilityOf(scanLocationLabel));
                    	clearText(scanputawayloc);
                    	scanputawayloc.sendKeys(ptwLoc, Keys.ENTER);
                    	firstContainerFlag = false;
                    }
                    //slight delay to allow the case update happen behind the screens
                    TimeUnit.SECONDS.sleep(2);
                    validateCasesAfterPutaway(caseBarcode,ptwLoc);
                    if (allocStartIndex == allocEndIndex) {
                        ++allocStartIndex;
                        endButton.click();
                        break;
                    }
                }
            }
            pageLoadWait();
            getWait().until(ExpectedConditions.elementToBeClickable(exitButton));
            exitButton.click();
        } else {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Num of cases in ASN and allocation for putaway are not matching : ", "No of cases = " + caseBarcodeList.size() + ", Total allocation = " + totalAllocatedCases, false);
        }
    }
    
    public void selectPutawaySubMenu(String putawaySubMenu) {
        switch (putawaySubMenu) {
            case "Pallet":
            	palletLinkText.click();
                getWait().until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[*[text()='Storage Location :']]"), putawaySubMenu));
                break;
            case "Binbox":
            	binboxLinkText.click();
            	getWait().until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[*[text()='Storage Location :']]"), putawaySubMenu));
                break;
            case "GOH":
            	gohLinkText.click();
            	getWait().until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[*[text()='Storage Location :']]"), putawaySubMenu));
                break;
            case "JWL":
            	jwlLinkText.click();
            	getWait().until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[*[text()='Storage Location :']]"), putawaySubMenu));
                break;
            case "Case":
            	caseLinkText.click();
            	getWait().until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//*[*[text()='Storage Location :']]"), putawaySubMenu));
                break;
            default:
                Assert.fail("Passed submenu value doesn't match, send correct value");
                break;
        }
    }

    public void validateCasesAfterPutaway(String caseBarcode, String ptwLoc) {
        Response caseAfterPutaway = HAFSteps.getInventoryDetailsWithBarcode(caseBarcode).asResponse();
        JsonPath jpath = new JsonPath(caseAfterPutaway.asString());
        boolean caseIsPutawayValid = "PTW".equals(jpath.getString("container.containerStatusCode")) &&
                !caseAfterPutaway.asString().contains("conditionCodeList") &&
                ptwLoc.equalsIgnoreCase(jpath.getString("container.containerRelationshipList[0].parentContainer"));

        CommonUtils.doJbehavereportConsolelogAndAssertion("Case is putaway valid  ", caseBarcode + " , " + ptwLoc, caseIsPutawayValid);
    }
}
