package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.configuration.DistroEndPoint;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.foundationalServices.utils.TearDownMethods;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.HashMap;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class PODashboardPage extends BasePage {

	public String reportingResponse = null;

	@FindBy(xpath = "//span[contains(text(),'PO Dashboard')]")
	WebElement poDashboard;
	
	@FindBy(xpath = "//button[(@id = 'clearButton')]")
	private WebElement clearButton;

	@FindBy(xpath = "//Input[@name='poNbr']")
	private WebElement poNbrBox;
	
	@FindBy(xpath = "//button[(@id = 'searchButton')]")
	private WebElement searchButton;

	@FindBy(xpath = "//button[span/text()='Close']")
	public WebElement closeButton;

	@FindBy(xpath = "//button[span/text()='OK']")
	public WebElement OK;

	@FindBy(xpath = "//button[span/text()='CLOSE RECEIPT']")
	public WebElement closeConfirmationButton;

	@FindBy(tagName = "h6")
	public WebElement popupTitle;

	@FindBy(xpath = "//button[span/text()='Release']")
	private WebElement releaseButton;
	
	@FindBy(xpath = "div[(@col-id='poRcptStatus')]")
	private WebElement poRcptStatus;
	
	public void selectPODashboard() {
		getWait(10).until(visibilityOf(poDashboard));
		poDashboard.click();
		log.info("PO Dashboard selected");
	}

	public void searchDashboard(String poNbr) {
		getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(clearButton));
		jsClick(driver.findElement(By.xpath("//button[(@id = 'clearButton')]")));
		log.info("search Dashboard");
		getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(poNbrBox));
		poNbrBox.clear();		
		poNbrBox.sendKeys(poNbr);
		getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(searchButton));
		jsClick(driver.findElement(By.xpath("//button[(@id = 'searchButton')]")));
	}
	
	public void validateReleaseAndClose(String rcptNbr, String release, String close) {
		List<WebElement> gridRowsLocator = driver.findElements(By.xpath("//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[3]/div[2]/div/div/div"));
		for (WebElement row : gridRowsLocator) {
			String poRcptNbr = row.findElement(By.xpath("div[(@col-id='poRcptNbr')]")).getText().toString();
			if(poRcptNbr.equalsIgnoreCase(rcptNbr)) { 
	 			String rlsButtonColor = releaseButton.getCssValue("color");
	 			//String clsButtonColor = closeButton.getCssValue("color");	 			
	 			Assert.assertTrue("Release button not displayed as expected" , release.equalsIgnoreCase("enabled") ? rlsButtonColor.contains("rgba(255, 255, 255,") : rlsButtonColor.contains("rgba(128, 128, 128,"));
	 			log.info("Release button displayed as : " + release);
				break;
			}
		}
	}

	public void validatesPOStatusOnPODashboard(String poNbr, String rcptNbr) {
		HashMap<String, HashMap<String, String>> poDetailsList = getReportingServiceResponse(poNbr,rcptNbr);
		List<WebElement> gridRowsLocator = driver
				.findElements(By.xpath("//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[3]/div[2]/div/div/div"));
		for (WebElement row : gridRowsLocator) {
			String poRcptNbr = row.findElement(By.xpath("div[(@col-id='poRcptNbr')]")).getText().toString();
			if(poRcptNbr.equalsIgnoreCase(rcptNbr)) {
				String poRcptStatus = row.findElement(By.xpath("div[(@col-id='poRcptStatus')]")).getText().toString();
				HashMap<String, String> attributesVal = poDetailsList.get(poRcptNbr);
                if(attributesVal!=null && !attributesVal.isEmpty())
                {
    				Assert.assertEquals(attributesVal.get("PO Receipt NBR").toString(), poRcptNbr);
    				Assert.assertEquals(attributesVal.get("PO NBR").toString(),
    						row.findElement(By.xpath("div[(@col-id='poNbr')]")).getText().toString());
    				Assert.assertEquals(attributesVal.get("Area Flow").toString(),
    						row.findElement(By.xpath("div[(@col-id='processArea')]")).getText().toString());
    				Assert.assertEquals(attributesVal.get("Status").toString(), poRcptStatus);            	
                }
			}
		}

	}     
       
	public HashMap<String, HashMap<String, String>> getReportingServiceResponse(String poNbr,String receiptNbr) {
		HashMap<String, HashMap<String, String>> poReceiptAttributeMap = new HashMap<String, HashMap<String, String>>();
		log.info("poNbr  : \n" + poNbr);		
		String dashboardRequest = DistroEndPoint.DASHBOARDREPORTINGSERVICE + poNbr;
		log.info("dashboardRequest  : \n" + dashboardRequest);
		String reportingResponse = RestUtilities.getRequestResponse(dashboardRequest);
		this.reportingResponse = reportingResponse;
		log.info("reportingResponse  : \n" + reportingResponse);
		if (reportingResponse != null) {
			JSONArray jsonObjects = new JSONObject(reportingResponse).getJSONArray("poDashboard");
			for (int i = 0; i < jsonObjects.length(); i++) {
				HashMap<String, String> attibMap = new HashMap<String, String>();
				JSONObject jsonObject = jsonObjects.getJSONObject(i);
				if(String.valueOf(jsonObject.get("poRcptNbr")).equalsIgnoreCase(receiptNbr)) {					
					attibMap.put("PO Receipt NBR", String.valueOf(jsonObject.get("poRcptNbr")));
					attibMap.put("PO NBR", String.valueOf(jsonObject.get("poNbr")));
					attibMap.put("Area Flow", jsonObject.getJSONArray("processArea").getString(0));
					HashMap<String, String> recpStatus = ExpectedDataProperties.getRcptStatus();
					String status = recpStatus.get(String.valueOf(jsonObject.get("poRcptStatus")));				
					attibMap.put("Status", status);
					poReceiptAttributeMap.put(String.valueOf(jsonObject.get("poRcptNbr")), attibMap);				
				}

				}
		}

		log.info("POReceiptAttributeMap  : \n" + poReceiptAttributeMap);
		return poReceiptAttributeMap;
	}
}