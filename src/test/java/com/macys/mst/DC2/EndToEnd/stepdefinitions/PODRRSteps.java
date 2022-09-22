package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODRRPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODashboardReleasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.POInquiryUIPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData.PoLinebarCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.context.StepsContext;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.PageFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PODRRSteps {

	private WebDriver driver = LocalDriverManager.getInstance().getDriver();
	private POInquiryUIPage poInquiryUIPage = PageFactory.initElements(driver, POInquiryUIPage.class);
	private static Logger LOGGER = Logger.getLogger(PODRRSteps.class.getName());
	
	private PODashboardReleasePage poDashboardReleasePage = PageFactory.initElements(driver, PODashboardReleasePage.class);
	private PODRRPage drrPage = PageFactory.initElements(driver, PODRRPage.class);

    private SeUiContextBase seUiContextBase = new SeUiContextBase();
	
	public long TestNGThreadID = Thread.currentThread().getId();

	private StepsContext stepsContext;

	public PODRRSteps(StepsContext stepsContext) {
		this.stepsContext = stepsContext;
	}

	public int editingCount=0;

	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
		TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}

	@Then("The $pagetitle page is displayed")
    public void checkPageTitle(String pagetitle){
        Assert.assertTrue(drrPage.getPageBreadcrumb().contains(pagetitle));
    }

	@When("PO number is passed as input in $page UI")
	public void clickPOBasedReportID(String page) throws InterruptedException {
		List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
	
		try {
			TimeUnit.SECONDS.sleep(20);
			poDashboardReleasePage.loginSCM();
		} catch (Exception e) {
			log.error("Error in login");
		}
		
		for (PoLinebarCode poLine : poLinebarCode) {
			String PO = poLine.getPoNbr();
			clickHomeNavOption(page);
			poInquiryUIPage.clickClearButton();
			poInquiryUIPage.typeIntoInputField("PO *",PO);
			poInquiryUIPage.clickSearchButton();
			TimeUnit.SECONDS.sleep(20);
			log.info("Started to validate Report ID based on Po");
			String reportID = drrPage.clickReportID();
		  	stepsContext.put(Context.REPORT_ID.name(), reportID, ToContext.RetentionLevel.SCENARIO);
	        break;		
		}
	}



	@Then("System displays reportId and its details for the corresponding PO")
	public void validatesDetailReceivingReportPage() throws SQLException, Exception {
		List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
		
		String reportID = (String) stepsContext.get(Context.REPORT_ID.name());
		HashMap<String,String> servicePOHeaderElements = drrPage.getPOHeaderMapFromAPITrans(reportID);
		log.info("Service Header Elements\n"+servicePOHeaderElements);
		HashMap<String, String> screenPOHeaderElements = drrPage.getPOHeaderMapFromScreen();
		screenPOHeaderElements.remove("Appt #");
		log.info("Screen HeaderElements\n"+screenPOHeaderElements);
		
		String rcptNbr = screenPOHeaderElements.get("Rcpt Nbr");

		String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
		drrPage.barcodeElementsvalidation(reportID,poNbr);
		Assert.assertEquals("Po Header attributes are not matching", servicePOHeaderElements, screenPOHeaderElements);
		log.info("Detail Receiving Report Barcode details have been validated");
		StepDetail.addDetail("Detail Receiving Report Barcode details have been validated", true);
		
		drrPage.validatesPOLineHeaderElementsforSKU(rcptNbr,reportID);
		log.info("Detail Receiving Report Header page has been validated");
		StepDetail.addDetail("Detail Receiving Report Header page has been validated", true);

	    drrPage.validateSkulist(poNbr,rcptNbr,reportID);
	    log.info("Detail Receiving Report Skulist and Edit have been validated");
	    StepDetail.addDetail("Detail Receiving Report Skulist and Edit have been validated", true);
	}
	

	@When("The user updates Editable attributes for SKU from examples")
	public void whenTheUserUpdatesEditableAttributes(@Named("skuNumber") String skuNumber, @Named("lineItemAttributes") String lineItemAttributes) {
		HashMap<String, String> attributesMap = new HashMap<String, String>();
		String[] attributesList = lineItemAttributes.split(",");

		for(String attributes:attributesList) {
			String[] attributesKeyVal = attributes.split("=");
			attributesMap.put(attributesKeyVal[0], attributesKeyVal[1]);
		}
		int retryCount = 0;
		boolean updateStaus = false;
		while((updateStaus==false)&&(retryCount<2))
		{
			updateStaus= drrPage.updateLineItemAttributes(attributesMap, skuNumber);
			retryCount++;
			drrPage.sendEscape();
		}
		StepDetail.addDetail("SkuNumber '"+skuNumber+"' updated with attributes: "+attributesMap, true);
		seUiContextBase.waitFor(4);

	}


	@When("User applies changes to current $element")
	@Alias("User $element save")
	public void whenTheUserAppliesChangestoSKU_PID(String element) {

		if(element.equals("SKU")){
			drrPage.clickButton("APPLY TO CURRENT");
			StepDetail.addDetail("User applies changes to entire PID", true);
		}else if(element.equals("PID")){
			drrPage.clickButton("APPLY TO ALL");
			StepDetail.addDetail("User applies changes to Single SKU", true);
		}else{
			drrPage.clickButton("CANCEL");
			StepDetail.addDetail("User cancels Save", true);
		}
		seUiContextBase.waitFor(4);

	}

	@When("The user updates Prep attributes for SKU from examples")
	public void whenTheUserUpdatesPrepAttributes(@Named("skuNumber") String skuNumber, @Named("prepAttributes") String prepAttributes) {
		HashMap<String, String> attributesMap = new HashMap<String, String>();
		String[] attributesList = prepAttributes.split(",");

		for(String attributes:attributesList) {
			String[] attributesKeyVal = attributes.split("=");
			attributesMap.put(attributesKeyVal[0], attributesKeyVal[1]);
		}
		int retryCount = 0;
		boolean updateStaus = false;
		while((updateStaus==false)&&(retryCount<2))
		{
			updateStaus= drrPage.updatesPrepAttributes(attributesMap, skuNumber);
			retryCount++;
			drrPage.sendEscape();
		}
		StepDetail.addDetail("SkuNumber '"+skuNumber+"' updated with Prep attributes: "+attributesMap, true);
		seUiContextBase.waitFor(4);

	}

	@Then("User validates font color as $fontColor for skuNumber")
	public void thenUserValidatesAttributesFontColor(@Named("skuNumber") String skuNumber,String fontColor) {

		drrPage.clickButton("Expand", skuNumber);

		switch(fontColor) {
			case "Red":{
				HashSet<String> fontColorListActual	  = new HashSet<String>();
				HashSet<String> fontColorListExpected = new HashSet<String>();
				fontColorListExpected.add("#ff0000");
				String rowColor;
				List<WebElement> editableRows = driver.findElements(By.xpath("//*[@id=\""+skuNumber+"-editPoField\"]/table/tbody/tr"));
				for(WebElement row:editableRows) {
					if(row.findElement(By.xpath("./td/div/div/div/select | ./td/div/div/div/input")).getTagName().equalsIgnoreCase("select")) {
						rowColor = row.findElement(By.xpath("./td/div/div/div/select")).getCssValue("color");
					}else {
						rowColor = row.findElement(By.xpath("./td/div/div/div/input")).getCssValue("color");
					}

					String hexRowColor = Color.fromString(rowColor).asHex();
					fontColorListActual.add(hexRowColor);
				}
				Assert.assertEquals("Overridden Attributes Font Color is not Red",fontColorListExpected,fontColorListActual);
				StepDetail.addDetail("Font color updated as Red for "+skuNumber, true);
				break;
			}
			case "Black":{
				HashSet<String> fontColorListActual	  = new HashSet<String>();
				HashSet<String> fontColorListExpected = new HashSet<String>();
				fontColorListExpected.add("#000000");
				String rowColor;
				List<WebElement> editableRows = driver.findElements(By.xpath("//*[@id=\""+skuNumber+"-editPoField\"]/table/tbody/tr"));
				for(WebElement row:editableRows) {
					if(row.findElement(By.xpath("./td/div/div/div/select | ./td/div/div/div/input")).getTagName().equalsIgnoreCase("select")) {
						rowColor = row.findElement(By.xpath("./td/div/div/div/select")).getCssValue("color");
					}else {
						rowColor = row.findElement(By.xpath("./td/div/div/div/input")).getCssValue("color");
					}

					String hexRowColor = Color.fromString(rowColor).asHex();
					fontColorListActual.add(hexRowColor);
				}
				Assert.assertEquals("Overridden Attributes Font Color is not Black",fontColorListExpected,fontColorListActual);
				StepDetail.addDetail("Font color is Black for "+skuNumber, true);
				break;
			}
			
		}

	}

	@Then("User clicks ReportId and update and save POLine Editable attributes for Single SKU")
	@Alias("User updated the edited values to its original values")
	public void thenUserStoresPOLineEditableAttributesforSingleSKU(){

		List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
		String skuNumber = poLinebarCode.get(0).getSKU();
		Map<String,String> fieldsTObeEditable = new HashMap<>();
		Map<String,String> prepoptionsTObeEditable = new HashMap<>();
		String reportID = poLinebarCode.get(0).getReportId();
		if(editingCount==0) {
			drrPage.clickReportID(reportID);
			seUiContextBase.waitFor(10);
			drrPage.clickButton("Expand", skuNumber);
			editingCount++;
		}
		HashMap<String,String> POLineItemElementsUI    = null;
		HashMap<String,String> POLineItemElementsDB	   = null;
		HashSet<String> PrepOptionsDB  = null;
		HashSet<String> PrepOptionsUI  = null;
		Map<String,String> prepwithoption_UI=new HashMap<>();

		POLineItemElementsUI  = drrPage.getPOLineItemMapFromUI("editableMinusPrep",skuNumber);
		prepwithoption_UI 		  = drrPage.getPrepOptionsforSKUFromUI(skuNumber);
		System.out.println("prepwithoption_UI:"+ prepwithoption_UI);
		log.info("prepwithoption_UI: {}",prepwithoption_UI);
		drrPage.clickButton("Edit", skuNumber);
     	if(!POLineItemElementsUI.isEmpty())
		{
			Iterator<Map.Entry<String, String>> iterator = POLineItemElementsUI.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				String editablefield_key= entry.getKey();
				String editablefield_value= entry.getValue();
				if(editablefield_key.equalsIgnoreCase("Process Area Conf")){
					fieldsTObeEditable.put(editablefield_key,editablefield_value);
					break;
				}
			}
			UpdatesEditableAttributes(skuNumber,fieldsTObeEditable);
		}
		if(!prepwithoption_UI.isEmpty())
		{
			Iterator<Map.Entry<String, String>> iterator = prepwithoption_UI.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				String editableperpfield_key= entry.getKey();
				String editableprepfield_value= entry.getValue();
					prepoptionsTObeEditable.put(editableperpfield_key,editableprepfield_value);
					break;
			}
			UpdatesPrepAttributes(skuNumber, prepoptionsTObeEditable);
		}

		drrPage.clickButton("Save", skuNumber);
		whenTheUserAppliesChangestoSKU_PID("SKU");
		Map<String,String> POLineItemElementsUIUpdated = new HashMap<>();
		POLineItemElementsUIUpdated  = drrPage.getPOLineItemMapFromUI("editableMinusPrep",skuNumber);
		POLineItemElementsDB= drrPage.getOverrideLineItemsDBTrans(reportID,skuNumber);
		LOGGER.info("poinedb: "+POLineItemElementsDB);
		Map<String,String> updatedPrepAttributesMap = new HashMap<String,String>();
		updatedPrepAttributesMap = drrPage.getPrepOptionsforSKUFromUI(skuNumber);
		PrepOptionsUI = new HashSet<>(updatedPrepAttributesMap.values());
		PrepOptionsDB = drrPage.getPrepOptionsforSKUFromDB(skuNumber,reportID);
		LOGGER.info("Modified Attributes saved in Override Tables for SKU: "+skuNumber);
		CommonUtils.compareValuesIgnoreEmpty(POLineItemElementsUIUpdated, POLineItemElementsDB);
		StepDetail.addDetail("Modified Attributes saved in Override Tables for SKU: "+skuNumber+" on ReportID: "+reportID, true);
		Assert.assertEquals("Prep Attributes not saved in DB for SKU: "+skuNumber,PrepOptionsUI,PrepOptionsDB);
		StepDetail.addDetail("Modified Prep Attributes saved in Override Tables for SKU: "+skuNumber+" on ReportID: "+reportID, true);
		LOGGER.info("Modified Prep Attributes saved in Override Tables for SKU: "+skuNumber);
		thenUserValidatesAttributesFontColor(skuNumber,"Red");
		LOGGER.info("Save and Updated validated for SKU: "+skuNumber);
	}

	@Given("Override tables are cleared for ReportID")
	public void givenOverrideTablesClearedForReportID() {
		String reportID = (String) stepsContext.get(Context.REPORT_ID.name());
		drrPage.clearOverrideTablesforReportID(reportID);
		StepDetail.addDetail("Override Attribute and Prep Attriutes cleared for reportID: "+reportID, true);
	}
		
	public void clickHomeNavOption(String option) throws InterruptedException {
		Actions action = new Actions(driver);
		action.sendKeys(Keys.ESCAPE).build().perform();
		Assert.assertTrue(poInquiryUIPage.isNavMenuDisplayed());
		TimeUnit.SECONDS.sleep(20);
		poInquiryUIPage.clickNavOption(option);
	}

	public void UpdatesEditableAttributes(String skuNumber, Map<String,String> editableattributes) {
		Map<String, String> attributesMap = new HashMap<>();
		attributesMap = editableattributes;
		int retryCount = 0;
		boolean updateStaus = false;
		while((updateStaus==false)&&(retryCount<2))
		{
			updateStaus= drrPage.updateLineItemAttributes(attributesMap, skuNumber);
			log.info("Updated Status :{}",updateStaus);
			retryCount++;
			drrPage.sendEscape();
		}
		StepDetail.addDetail("SkuNumber '"+skuNumber+"' updated with attributes: "+attributesMap, true);
		seUiContextBase.waitFor(4);

	}

	public void UpdatesPrepAttributes(String skuNumber, Map<String,String> editableprepOptions) {
		Map<String, String> prepOptionsMap = new HashMap<>();
		prepOptionsMap = editableprepOptions;
		int retryCount = 0;
		boolean updateStaus = false;
		while((updateStaus==false)&&(retryCount<2))
		{
			updateStaus= drrPage.updatesPrepAttributes(prepOptionsMap, skuNumber);
			retryCount++;
			drrPage.sendEscape();
		}
		StepDetail.addDetail("SkuNumber '"+skuNumber+"' updated with Prep attributes: "+prepOptionsMap, true);
		seUiContextBase.waitFor(4);

	}
}
