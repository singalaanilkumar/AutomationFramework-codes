package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class POInquiryPage extends BasePage {

    Logger log = Logger.getLogger(POInquiryPage.class);
    CommonUtils commonUtils = new CommonUtils();

    public static boolean inHouseUPCClicked = false;

    @FindBy(xpath = "//*[text()='Retrieve PO']")
    public WebElement retrievePOButton;

    @FindBy(xpath = "//button[contains(text(),'PO Inquiry')]")
    public WebElement poinquiry;

    @FindBy(xpath = "//input[@name='reportId']")
    public WebElement ReportId;

    @FindBy(xpath = "//div[@id='select-divisions']")
    public WebElement DIV;

    @FindBy(xpath = "//*[@id='gridPoDistro']")
    public WebElement distro;

    @FindBy(xpath = "//input[@id='apptDateRange']")
    public WebElement ApptDateRange;

    @FindBy(xpath = "//input[@name='department']")
    public WebElement Department;

    @FindBy(xpath = "//div[@id='select-newStore']")
    public WebElement Newstore;

    @FindBy(xpath = "//div[@id='select-apptStatus']")
    public WebElement ApptStatus;

    @FindBy(xpath = "//input[@id='apptNbr']")
    public WebElement Appt;

    @FindBy(xpath = "//div[@id='select-processArea']")
    public WebElement Processarea;

    @FindBy(xpath = "//input[@id='poNbr']")
    public WebElement PO;
    
    @FindBy(xpath = "//*[@col-id='poNbr']/div/button")
    private List<WebElement> poNbrList;

    @FindBy(xpath = "//div[@id='select-genType']")
    public WebElement Gentype;

    @FindBy(xpath = "//div[@id='select-poStatus']")
    public WebElement PoStatus;

    @FindBy(xpath = "//div[@id='select-preTicket']")
    public WebElement Preticket;

    @FindBy(xpath = "//button[@id='searchButton']/span")
    public WebElement Search;

    @FindBy(xpath = "//span[text()='CLEAR']")
    public WebElement Clear;

    @FindBy(xpath = "//span[text()='DETAIL RECEIVING REPORT (QUICK PRINT)']")
    public WebElement DRR;

    @FindBy(xpath = "//*[@col-id='0']")
    public WebElement Checkbox;

    @FindBy(xpath = "//*[@col-id='poNbr']/div/button")
    public WebElement POlink;

    @FindBy(xpath = "//span[text()='EDIT']")
    public WebElement Edit;

    @FindBy(xpath = "//input[@id='filled-name']")
    public WebElement enterPO;

    @FindBy(xpath = "//span[text()='CLOSE']")
    public WebElement close;
    
    @FindBy(xpath = "//*[text()='OK']")
    public WebElement OK;

    @FindBy(xpath = "/html/body/div[2]/div[2]/div/div[2]/h6")
    public WebElement success;

    @FindBy(xpath = "//div[@role='presentation']/div[2]/div/p")
    public WebElement Grid;

    @FindBy(xpath = "/html/body/div[2]/div[2]/div/div[2]/p/p")
    public WebElement message;

    private By QuickPrintButton = By.id("gridQuickPrint");

    public String GridRecordCount = "//div[@ref='eLeftContainer']/div";
    public String ReportIdLink = "//div[@ref='eLeftContainer']/div[*]/div/div/span[2]/div/button";
    public String ERSStatus = "//div[@ref='eCenterContainer']/div[*]/div[@col-id='ersRcptStatus']";
    public String POStatus = "//div[@ref='eCenterContainer']/div[*]/div[@col-id='poStatus']";
    public String CheckedCheckBox = "//div[@ref='eLeftContainer']/div[*]/div/div/span/span[contains(@class,'checkbox-checked')]";
    public String CheckBox = "//div[@ref='eLeftContainer']/div[*]/div/div/span[contains(@class,'checkbox')]";
    public String ScrollBar = "//div[@ref='eBodyHorizontalScrollViewport']";
    public String ColIndex = "//div[@aria-colindex='*']";

    public void typeIntoInputField(String fieldname, String fieldvalue) {
        if (fieldname.equalsIgnoreCase("PO")) {
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(PO));
            PO.sendKeys(fieldvalue);
        }
    }

    public void clickSearchButton() {
        getWait().until(ExpectedConditions.visibilityOf(Search));
        Search.click();
    }

    public void clickPO(String PONumber) {
    	getWait(30).until(ExpectedConditions.visibilityOfAllElements(poNbrList));
		for (WebElement poNbr : poNbrList) {         	
    		if(PONumber.equals(poNbr.getText())){
    		    try {
                    poNbr.click();
                }catch(Exception e){
                    poNbr.click();
                }
    			getWait(30).until(ExpectedConditions.visibilityOf(distro));
    			break;	
    		}
    	}
	}

    public void retievePO(String fieldvalue)
    {
        getWait().until(ExpectedConditions.urlContains("inquiry"));
        scrollToTop();
        getWait().until(ExpectedConditions.elementToBeClickable(retrievePOButton));
        retrievePOButton.click();
        enterPO.sendKeys(fieldvalue);
        List<WebElement> Retrievepos= driver.findElements(By.xpath("//span[text()='Retrieve PO']"));
        Retrievepos.get(2).click();
        getWait().until(ExpectedConditions.visibilityOf(success));
        getWait().until(ExpectedConditions.visibilityOf(message));
        if(success.getText().equalsIgnoreCase("Success!")&& message.getText().equalsIgnoreCase("PO already exists."))
        {
            log.info("Validated PO successfully in Retrieve PO screen");
            close.click();
        }
        else
            log.info("Invalid PO");

	}

    public boolean ReportIdHyperLinkValidation()
    {
        int RecordCount = 0,i,count=0;
        boolean Result = false;
        String POStatusValue = null,ERSStatusValue = null,ReportIdAttribute=null;
        List<String> RestrictedERSStatus = FetchRestrictedERSStatus();
        List<String> RestrictedPOStatus = FetchRestrictedPOStatus();
        RecordCount = driver.findElements(By.xpath(GridRecordCount)).size();
        if(RecordCount!=0) {
            for (i = 1; i <= RecordCount; i++) {
                if(i>1)
                {
                    for(int x=19;x>2;x--)
                    {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(ColIndex.replace("*",String.valueOf(x)))));

                    }
                }
                ReportIdAttribute = driver.findElement(By.xpath(ReportIdLink.replace("*".toString(),String.valueOf(i)))).getAttribute("class");
                ERSStatusValue = driver.findElement(By.xpath(ERSStatus.replace("*".toString(),String.valueOf(i)))).getText();
                for(int x=2;x<20;x++)
                {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(ColIndex.replace("*",String.valueOf(x)))));

                }
                POStatusValue = driver.findElement(By.xpath(POStatus.replace("*".toString(),String.valueOf(i)))).getText();
                if(RestrictedERSStatus.contains(ERSStatusValue) && RestrictedPOStatus.contains(POStatusValue))
                {
                    if(ReportIdAttribute.endsWith("cUCYEh"))
                    {
                        count = count +1;
                    }
                }
                else if(RestrictedERSStatus.contains(ERSStatusValue) && !RestrictedPOStatus.contains(POStatusValue))
                {
                    if(ReportIdAttribute.endsWith("cUCYEh"))
                    {
                        count = count +1;
                    }
                }
                else if (!RestrictedERSStatus.contains(ERSStatusValue) && RestrictedPOStatus.contains(POStatusValue))
                {
                    if(ReportIdAttribute.endsWith("gTyisB"))
                    {
                        count = count +1;
                    }
                }
                else if (!RestrictedERSStatus.contains(ERSStatusValue) && !RestrictedPOStatus.contains(POStatusValue))
                {
                    if(ReportIdAttribute.endsWith("gTyisB"))
                    {
                        count = count +1;
                    }
                }
            }
        }
        if(RecordCount==count)
        {
            Result=true;
        }
        else
        {
            Result=false;
        }
        return Result;
    }

    public boolean QuickPrintButtonValidation()
    {
        int RecordCount = 0,i,count=0,checkedcount=0;
        boolean Result = false;
        String POStatusValue = null,ERSStatusValue = null,ReportIdAttribute=null;
        List<String> RestrictedERSStatus = FetchRestrictedERSStatus();
        List<String> RestrictedPOStatus = FetchRestrictedPOStatus();
        JavascriptExecutor javascriptDriver = (JavascriptExecutor) driver;
        RecordCount = driver.findElements(By.xpath(GridRecordCount)).size();
        if(RecordCount!=0) {
            for (i = 1; i <= RecordCount; i++) {
                if (i < RecordCount && RecordCount >= 1)
                {
                    driver.findElement(By.xpath(CheckBox.replace("*".toString(),String.valueOf(i)))).click();
                    checkedcount = checkedcount +1;
                }
                if (RecordCount == 1) {
                    driver.findElement(By.xpath(CheckBox.replace("*".toString(),String.valueOf(i)))).click();
                    checkedcount = checkedcount +1;
                }
            }
            for (i = 1; i <= RecordCount; i++)
            {
                if(!driver.findElement(By.xpath(CheckedCheckBox.replace("*".toString(),String.valueOf(i)))).getAttribute("class").contains("ag-hidden"))
                {
                    if(i>1)
                    {
                        for(int x=2;x<20;x++)
                        {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(ColIndex.replace("*",String.valueOf(x)))));

                        }
                    }
                    POStatusValue = driver.findElement(By.xpath(POStatus.replace("*".toString(),String.valueOf(i)))).getText();
                    for(int x=19;x>2;x--)
                    {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(ColIndex.replace("*",String.valueOf(x)))));

                    }
                    ERSStatusValue = driver.findElement(By.xpath(ERSStatus.replace("*".toString(),String.valueOf(i)))).getText();
                    if(RestrictedERSStatus.contains(ERSStatusValue) && RestrictedPOStatus.contains(POStatusValue))
                    {
                        count=count +1;
                    }
                    else if(RestrictedERSStatus.contains(ERSStatusValue) && !RestrictedPOStatus.contains(POStatusValue))
                    {
                        count=count +1;
                    }
                    else if (!RestrictedERSStatus.contains(ERSStatusValue) && RestrictedPOStatus.contains(POStatusValue))
                    {
                        count=count +1;
                        Result = true;
                    }
                    else if (!RestrictedERSStatus.contains(ERSStatusValue) && !RestrictedPOStatus.contains(POStatusValue))
                    {
                        count=count +1;
                        Result = true;
                    }
                }
            }
        }
        Map<String, Object> attributes = (Map<String, Object>) javascriptDriver.executeScript("var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;", driver.findElement(QuickPrintButton));
        if(Result)
        {
            if(attributes.containsKey("disabled"))
            {
                Result = false;
            }
            else
            {
                Result = true;
            }
        }
        return Result;
    }

    public List<String> FetchRestrictedERSStatus()
    {
        String RestrictionType = "",RestrictedStatus = "";
        List<String> RestrictedERStatus = new ArrayList<String>();
        String GETRestrictedStatusEndpoint = commonUtils.getUrl("configurationServices.GetRestrictedERSStatus");
        Response response = WhmRestCoreAutomationUtils.getRequestResponse(GETRestrictedStatusEndpoint.replace("{AppName}", "offpricedc").replace("{MName}", "handheld").replace("{CName}", "ConfigRcptRestricted")).asResponse();
        JsonPath Jpath = new JsonPath(response.asString());
        String configValue = Jpath.getString("configValue");
        JsonPath Jpath1 = new JsonPath(configValue);
        for(int i=0;i<=1;i++)
        {
            RestrictionType = Jpath1.getString("[0]["+i+"].restrictionType");
            if(RestrictionType.equals("ERSStatus"))
            {
                RestrictedStatus = Jpath1.getString("[0]["+i+"].restrictedStatus");
                break;
            }
        }
        if(RestrictedStatus!="")
        {
            String[] status = RestrictedStatus.split(",");
            for(int j=0;j<status.length;j++)
            {
                RestrictedERStatus.add(status[j]);
            }
        }
        CommonUtils.doJbehavereportConsolelogAndAssertion("Fetching Restricted ER Status",RestrictedStatus,true);
        return RestrictedERStatus;
    }

    public List<String> FetchRestrictedPOStatus()
    {
        String RestrictionType = "",RestrictedStatus = "";
        List<String> RestrictedPOStatus = new ArrayList<String>();
        String GETRestrictedStatusEndpoint = commonUtils.getUrl("configurationServices.GetRestrictedERSStatus");
        Response response = WhmRestCoreAutomationUtils.getRequestResponse(GETRestrictedStatusEndpoint.replace("{AppName}", "offpricedc").replace("{MName}", "handheld").replace("{CName}", "ConfigRcptRestricted")).asResponse();
        JsonPath Jpath = new JsonPath(response.asString());
        String configValue = Jpath.getString("configValue");
        JsonPath Jpath1 = new JsonPath(configValue);
        for(int i=0;i<=1;i++)
        {
            RestrictionType = Jpath1.getString("[0]["+i+"].restrictionType");
            if(RestrictionType.equals("POReceiptStatus"))
            {
                RestrictedStatus = Jpath1.getString("[0]["+i+"].restrictedStatus");
                break;
            }
        }
       if(RestrictedStatus!="")
       {
           String[] status = RestrictedStatus.split(",");
           for(int j=0;j<status.length;j++)
           {
               RestrictedPOStatus.add(status[j]);
           }
       }
       CommonUtils.doJbehavereportConsolelogAndAssertion("Fetching Restricted POS Status",RestrictedStatus,true);
       return RestrictedPOStatus;
    }
    
    public List<Map<String, String>> getGridElementsMap(){
    	getWait(30).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@ref='eBodyViewport']")));
		scrollElementIntoView(driver.findElement(By.xpath("//*[@ref='eBodyViewport']")));
		Map<String,String> colIDnameMap = new HashMap<String,String>(); 
		Map<String,String> colIDvalueMap = new HashMap<String,String>(); 
		Map<String,Map<String, String>> fixedColNameValueMapList = new HashMap<String,Map<String,String>>();
		Map<String,Map<String, String>> colNameValueMapList = new HashMap<String,Map<String,String>>();
	
		try {
			int scrollCount = 0;
    		while(scrollCount<scrollCountMax){
    			List<WebElement> headerRowCells = driver.findElements(By.xpath("//*[@ref='headerRoot']//*[@col-id!='0']"));
    			
    			for(int i=0;i<headerRowCells.size();i++){
    				try {
    					String headerColID = headerRowCells.get(i).getAttribute("col-id");
						String headerName  = StringUtils.normalizeSpace(headerRowCells.get(i).getText());
						if(StringUtils.isNotBlank(headerColID) && StringUtils.isNotBlank(headerName))
							colIDnameMap.put(headerColID,headerName);
					} catch (StaleElementReferenceException e) {
						//ignore
					}
    			}
    			
				getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@ref='eLeftContainer']//*[@role='row']")));
				List<WebElement> fixedRows = driver.findElements(By.xpath("//*[@ref='eLeftContainer']//*[@role='row']"));
				
				for(int i=0;i<fixedRows.size();i++){
					try {
						String rowID = String.valueOf(fixedRows.get(i).getAttribute("row-id"));
						colIDvalueMap = fixedColNameValueMapList.containsKey(rowID)?fixedColNameValueMapList.get(rowID):(new HashMap<String,String>());
						List<WebElement> valueCells = fixedRows.get(i).findElements(By.xpath("./div"));
						for(int j=0;j<valueCells.size();j++){
							try {
								String colID = valueCells.get(j).getAttribute("col-id");
								String colText = (StringUtils.isEmpty(valueCells.get(j).getText()))?"":valueCells.get(j).getText();
								if(StringUtils.isNotBlank(colID) && colIDnameMap.containsKey(colID)){
									colIDvalueMap.put(colIDnameMap.get(colID), StringUtils.normalizeSpace(colText.trim()));
								}
							} catch (StaleElementReferenceException e) {
								//ignore
							}
						}
						fixedColNameValueMapList.put(rowID,colIDvalueMap);
					} catch (StaleElementReferenceException e) {
						//ignore
					}
				}
    			
				getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']")));
				List<WebElement> valueRows = driver.findElements(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']"));
				for(int i=0;i<valueRows.size();i++){
					try {
						String rowID = String.valueOf(valueRows.get(i).getAttribute("row-id"));
						colIDvalueMap = colNameValueMapList.containsKey(rowID)?colNameValueMapList.get(rowID):(new HashMap<String,String>());
						List<WebElement> valueCells = valueRows.get(i).findElements(By.xpath(".//*[@role='gridcell' and @col-id!='0']"));
						for(int j=0;j<valueCells.size();j++){
							try {
								String colID = valueCells.get(j).getAttribute("col-id");
								String colText = (StringUtils.isEmpty(valueCells.get(j).getText()))?"":valueCells.get(j).getText();
								if(StringUtils.isNotBlank(colID) && colIDnameMap.containsKey(colID) && colID.equals("actInfo")){
									valueCells.get(j).findElement(By.xpath(".//*[@role='presentation']")).click();
									getWait(20).ignoring(Exception.class).until((ExpectedConditions.visibilityOf(OK)));
									String value = driver.findElement(By.xpath("//*[@role='document']//p[3]")).getText();
									if(value.equals("No Data")){
										colText="0";
										OK.click();
									}else{
										String actualCartonText = driver.findElement(By.xpath("//div[@role='document']//p[1]")).getText();
										colIDvalueMap.put(StringUtils.normalizeSpace(actualCartonText.split(":")[0].trim()), StringUtils.normalizeSpace(actualCartonText.split(":")[1].trim()));
										String actualUnitText = driver.findElement(By.xpath("//div[@role='document']//p[2]")).getText();
										colIDvalueMap.put(StringUtils.normalizeSpace(actualUnitText.split(":")[0].trim()), StringUtils.normalizeSpace(actualUnitText.split(":")[1].trim()));
										OK.click();
									}
								}else if(StringUtils.isNotBlank(colID) && colIDnameMap.containsKey(colID)){
									colIDvalueMap.put(colIDnameMap.get(colID), StringUtils.normalizeSpace(colText.trim()));
								}
							} catch (StaleElementReferenceException e) {
								//ignore
							}
						}
						if(fixedColNameValueMapList.containsKey(rowID))
							colIDvalueMap.putAll(fixedColNameValueMapList.get(rowID));
						colNameValueMapList.put(rowID,colIDvalueMap);
					} catch (StaleElementReferenceException e) {
						//ignore
					}
				}
				
				
			scrollGridTableRight(1);
			scrollCount++;
    		}
			log.info(colIDnameMap.toString());
			scrollCount = 0;
			while(scrollCount<scrollCountMax){
				scrollGridTableLeft(1);
				scrollCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error occured when fetcing GridElements: "+e.getLocalizedMessage());
		}    	
		return colNameValueMapList.values().stream().collect(Collectors.toList());
    }

}
