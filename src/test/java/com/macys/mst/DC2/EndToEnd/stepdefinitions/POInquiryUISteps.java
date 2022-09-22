package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.configuration.PO4WallEndPoint;
import com.macys.mst.DC2.EndToEnd.model.POInquiryDetail;
import com.macys.mst.DC2.EndToEnd.model.POLineDetails;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.POInquiryUIPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData.PoLinebarCode;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class POInquiryUISteps {
	public static WebDriver driver =  LocalDriverManager.getInstance().getDriver();
	private POInquiryUIPage poInquiryUIPage = PageFactory.initElements(driver, POInquiryUIPage.class);
	public long TestNGThreadID = Thread.currentThread().getId();
	private StepsContext stepsContext;
	SeUiContextBase seUiContextBase = new SeUiContextBase();

	public POInquiryUISteps(StepsContext stepsContext) {
		this.stepsContext = stepsContext;
	}

	BasePage basePage = new BasePage(driver);

	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
		TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}

	@Then("User validates PO details in $page UI to confirm recieved and completed units")
	public void poInquirydetails(String page) throws Exception{
		loadMacysBackstageHomepage();
		List<PoLinebarCode> poLinebarCode = (List<PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
		for (PoLinebarCode poLine : poLinebarCode) {
			String PO = poLine.getPoNbr();
			String ReceiptNbr = poLine.getReceiptNbr();
			clickNavOption(page);
			checkPageTitle(page);
			clickOnClearButton();
			typeInInputFieldValuesFromExamples("PO *",PO);
			clickSearchButton();
			clickPONumber(PO,ReceiptNbr);
			checkPageTitle("PO Details");
			validatePOdetails(poLine.getReportId());
		}
	}


	public void loadMacysBackstageHomepage() throws Exception {
		basePage.loadHomepageURL();
		basePage.login();
		seUiContextBase.waitFor(5);
	}

	public void clickNavOption(String option) {
		Actions action = new Actions(driver);
		action.sendKeys(Keys.ESCAPE).build().perform();
		Assert.assertTrue(basePage.isNavMenuDisplayed());
		basePage.clickNavOption(option);
	}

	public void checkPageTitle(String pagetitle){
		Assert.assertTrue(basePage.getPageBreadcrumb().contains(pagetitle));
		StepDetail.addDetail("Validating page title:"+pagetitle, basePage.getPageBreadcrumb().contains(pagetitle));
	}

	public void typeInInputFieldValuesFromExamples(@Named("inputLabel")String inputLabel,@Named("value") String value) {
		poInquiryUIPage.typeIntoInputField(inputLabel, value);
	}

	public void clickSearchButton(){
		poInquiryUIPage.clickSearchButton();
		seUiContextBase.waitFor(3);
	}

	public void clickOnClearButton(){
		poInquiryUIPage.clickClearButton();
	}

	public void verifyDisplayedFilterResultTable(String validation) {
		if(validation.equals("displayed")){
			Assert.assertTrue(poInquiryUIPage.isSearchResultTableDisplayed());
		} else if(validation.equals("not displayed")){
			Assert.assertFalse(poInquiryUIPage.isSearchResultTableDisplayed());
		}
	}

	public void clickPONumber(String value1,String value2){
		poInquiryUIPage.clickGridCell("PO",value1,"Receipt #",value2);
	}

	public void validatePOdetails(String reportId){
		List<Map<String,String>> podetailsfromUI = new ArrayList<>();
		List<Map<String,String>> podetailsfromService = new ArrayList<>();
		String rcvUnits = "";
		podetailsfromUI = poInquiryUIPage.getColumnData();
		log.info("Values displayed in PO details Screen table: "+podetailsfromUI);
		StepDetail.addDetail("Values displayed in PO details Screen table: "+podetailsfromUI, true);
		podetailsfromService = fetchPODetailServiceDetails(reportId);
		for(int i = 0;i<podetailsfromService.size();i++){
			CommonUtils.compareValues(podetailsfromUI.get(i), podetailsfromService.get(i));
			rcvUnits = getRecvUnits(podetailsfromUI.get(i).get("Exp Units"),podetailsfromUI.get(i).get("Act Units"));
			if(podetailsfromUI.get(i).get("RCV +/-").equalsIgnoreCase(rcvUnits)){
				log.info("Actual Received units in UI :"+podetailsfromUI.get(i).get("RCV +/-"));
				log.info("Expected Received units :"+rcvUnits);
				log.info("Actual and Expected Received units are matched");
				StepDetail.addDetail("Actual Received units in UI :"+podetailsfromUI.get(i).get("RCV +/-")+" and Expected Received units :"+rcvUnits+" are matched", true);
			}else{
				log.info("Actual Received units in UI :"+podetailsfromUI.get(i).get("RCV +/-"));
				log.info("Expected Received units :"+rcvUnits);
				log.info("Actual and Expected Received units are not matched");
				StepDetail.addDetail("Actual Received units in UI :"+podetailsfromUI.get(i).get("RCV +/-")+" and Expected Received units :"+rcvUnits+" are not matched", false);
			}
		}
	}

	public List<Map<String,String>> fetchPODetailServiceDetails(String reportID) {
		List<Map<String,String>> podetails = new ArrayList<>();
		log.info("fetchPODetails inside ::");
		String query = String.format(PO4WallEndPoint.POSCREEN_GET_PODETAILS,reportID);
		String response = RestUtilities.getRequestResponse(query);
		log.info("fetchPODetails  ::"+ response);
		POInquiryDetail poDetail = CommonUtils.getClientResponse(response, new TypeReference<POInquiryDetail>() {});
		List<POLineDetails> poLineDetails = poDetail.getPoDetail().getPoLineDetails();
		poLineDetails.forEach(poline -> {
			Map<String,String> podetailservice = new HashMap<>();
			if(null != poline){
				podetailservice.put("In-House UPC", poline.getInhouseUpc());
				podetailservice.put("Vendor UPC", poline.getVendorUpc());
				podetailservice.put("MKSTYL", poline.getMkStyl());
				podetailservice.put("VNDSTYL", poline.getVndStyle());
				podetailservice.put("PID", poline.getPid());
				podetailservice.put("PID Desc", poline.getPidDesc());
				podetailservice.put("Col Desc", poline.getColDesc());
				podetailservice.put("Size Desc", poline.getSizeDesc());
				podetailservice.put("Compare @ Retail", poline.getCompareRetail());
				podetailservice.put("Exp Units", poline.getExpectedUnits());
				podetailservice.put("Act Units", poline.getActualUnits());
				if(null != poline.getRcv() && poline.getRcv().contains("-")){
					String rcv = poline.getRcv().replace("-", "");
					podetailservice.put("RCV +/-", "("+rcv+")");
				}else{
					podetailservice.put("RCV +/-", poline.getRcv());
				}
				podetailservice.put("Line Item Status", poline.getStatus());
				podetailservice.put("Process Area", poline.getProcessArea());
			}
			podetails.add(podetailservice);
		});
		log.info("Po details from service:"+podetails);
		StepDetail.addDetail("Po details from service:"+podetails, true);

		return podetails;
	}

	public String getRecvUnits(String expUnits,String actUnits){
		String rcvUnits = "";
		rcvUnits = String.valueOf(Integer.parseInt(actUnits)-Integer.parseInt(expUnits));
		rcvUnits = (rcvUnits.contains("-")) ? (rcvUnits.replace("-", "(").concat(")")) : rcvUnits;
		return rcvUnits;
	}
}
