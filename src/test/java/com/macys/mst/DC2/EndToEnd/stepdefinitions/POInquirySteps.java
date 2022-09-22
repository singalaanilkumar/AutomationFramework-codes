package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLOrders;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.PODetailsPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.PODistroPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.POInquiryPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
public class POInquirySteps {

	Logger log = Logger.getLogger(POInquirySteps.class);
	public static WebDriver driver = LocalDriverManager.getInstance().getDriver();

	private POInquiryPage poinquiry = PageFactory.initElements(driver, POInquiryPage.class);
	private PODetailsPage podetails = PageFactory.initElements(driver, PODetailsPage.class);
	private PODistroPage  podistro  = PageFactory.initElements(driver, PODistroPage.class);

	private StepsDataStore dataStorage = StepsDataStore.getInstance();
	private StepsContext stepsContext;

	public WebDriverWait getWait() {
		WebDriverWait wait = new WebDriverWait(driver, 120);
		return wait;
	}

	public POInquirySteps(StepsContext stepsContext) {
		this.stepsContext = stepsContext;
	}


	@When("user enters fieldvalue into fieldname from examples")
	public void typeInInputFieldValuesFromExamples(@Named("fieldname")String fieldname,@Named("fieldvalue") String fieldvalue) {
		dataStorage.getStoredData().put(fieldname,fieldvalue);
		poinquiry.typeIntoInputField(fieldname, fieldvalue);
	}

	@When("user enters PO Number from $Template and search PO")
	public void typeInInputFieldValuesFromTemplate() {
		String poNbr = (String) dataStorage.getStoredData().get("poNbr");
		poinquiry.typeIntoInputField("PO", poNbr);
	}

	@When("required Inputs are provided")
	public void typeInInputFieldValues() {
		StepsContext stepsContext = new StepsContext();
		String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
		dataStorage.getStoredData().put("fieldname",poNbr);
		poinquiry.typeIntoInputField("PO", poNbr);
		poinquiry.clickSearchButton();
		poinquiry.waitForProcessing();
	}

	@When("the user clicks SEARCH button")
	public void clickSearchButton(){
		poinquiry.clickSearchButton();
	}

	@Then("User validates $screenName screen with DB")
	public void validateUIwithDB(String screenName) {
		String poNbr = (String) dataStorage.getStoredData().get("poNbr");
		String poRcptNbr = (String) dataStorage.getStoredData().get("rcptNbr");

		switch (screenName) {

			case "POInquiry":

				List<Map<String, String>> dataUIMapList = poinquiry.getGridElementsMap();
				log.info(dataUIMapList);
				Map<String, Map<String, String>> poInquriyUIMap = new HashMap<>();
				for(Map<String, String> dataUIMap : dataUIMapList){
					dataUIMap.remove("DIV");
					dataUIMap.remove("Destination Intent");
					dataUIMap.remove("Exp. Cartons");
					dataUIMap.remove("Act. Cartons");
					poInquriyUIMap.put(dataUIMap.get("Report ID"), dataUIMap);
				}

				Map<String, String> testPO = new HashMap<String,String>();
				testPO.put("PO", poNbr);
				List<Map<String, String>> dataDBMapList = getDBDataForPOInquiry(testPO);
				Map<String, Map<String, String>> poInquriyDBMap = dataDBMapList.stream().collect(Collectors.toMap(map -> map.get("Report ID"), map -> map));

				log.info(poInquriyDBMap);

				CommonUtils.doJbehavereportConsolelogAndAssertion("PO Inquiry Screen Count validated",
						"UI Report IDs: "+poInquriyUIMap.keySet()+" DB Report IDs: "+poInquriyDBMap.keySet(),
						poInquriyUIMap.keySet().equals(poInquriyDBMap.keySet()));
				for(String reportID_DB:poInquriyDBMap.keySet()){
					poInquriyDBMap.get(reportID_DB).remove("Act. Units");
					poInquriyUIMap.get(reportID_DB).remove("Actual Units");
					poInquriyDBMap.get(reportID_DB).remove("Appt #");
					poInquriyUIMap.get(reportID_DB).remove("Appt #");
					poInquriyUIMap.get(reportID_DB).remove("Actual Cartons");
					poInquriyDBMap.get(reportID_DB).remove("Exp. Units");
					poInquriyUIMap.get(reportID_DB).remove("Exp. Units");
					poInquriyDBMap.get(reportID_DB).remove("Multiple Appts");
					poInquriyUIMap.get(reportID_DB).remove("Multiple Appts");
					poInquriyDBMap.get(reportID_DB).remove("PO Status");
					poInquriyUIMap.get(reportID_DB).remove("PO Status");
					poInquriyDBMap.get(reportID_DB).remove("New Store");
					poInquriyUIMap.get(reportID_DB).remove("New Store");
					CommonUtils.doJbehavereportConsolelogAndAssertion("PO Inquiry Screen Details for ReportID "+reportID_DB,
							" DB Details: " + poInquriyDBMap.get(reportID_DB) + " UI Details: " + poInquriyUIMap.get(reportID_DB),
							poInquriyDBMap.get(reportID_DB).equals(poInquriyUIMap.get(reportID_DB))
					);
				}

				break;

			case "PODetails":

				List<Map<String, String>> detailsUIMap = podetails.getGridElementsMap();
				for (Map<String, String> map : detailsUIMap) {
					map.remove("Line Item Status");
					map.remove("RCV +/-");
				}

				Map<String, Map<String, String>> attributesUIMap = detailsUIMap.stream()
						.collect(Collectors.toMap(map -> map.get("In-House UPC"), map -> map));
				log.info("UI Map is" + attributesUIMap.toString());

				List<Map<String, String>> detailsDBMap = getDBDataForPODetails(poNbr,poRcptNbr);
				Map<String, Map<String, String>> attributesDBMap = detailsDBMap.stream()
						.collect(Collectors.toMap(map -> map.get("In-House UPC"), map -> map));

				log.info("DB Map is" + attributesDBMap.toString());

				CommonUtils.doJbehavereportConsolelogAndAssertion("PO Details Screen Count validated",
						"UI SKUs: " + attributesUIMap.keySet() + " DB SKUs: " + attributesDBMap.keySet(),
						attributesUIMap.keySet().equals(attributesDBMap.keySet()));



				for (String InHouseUPCDB : attributesDBMap.keySet()) {
					attributesUIMap.get(InHouseUPCDB).remove("Act Units");
					attributesDBMap.get(InHouseUPCDB).remove("Act Units");
					CommonUtils.doJbehavereportConsolelogAndAssertion(
							"PO Details Screen Details for SKUUPC " + InHouseUPCDB,
							" DB Details: " + attributesDBMap.get(InHouseUPCDB) + " UI Details: "
									+ attributesUIMap.get(InHouseUPCDB),
							attributesDBMap.get(InHouseUPCDB).equals(attributesUIMap.get(InHouseUPCDB)));
				}

				break;

			case "PODistro":
				String SKUNumber = "";
				List<Map<String, String>> distroUIMap = podistro.getGridElementsMap();
				for (Map<String, String> map : distroUIMap) {
					SKUNumber = map.get("In-House UPC");
					map.remove("Vendor UPC");
					map.remove("MKSTYL");
					map.remove("VNDSTYL");
					map.remove("PID");
					map.remove("PID Desc");
					map.remove("Col Desc");
					map.remove("Size Desc");
					String actualqty = map.get("Actual");
					if(actualqty.isEmpty()){
						map.put("Actual","0");
					}
				}
				Map<String, Map<String, String>> attDistroUIMap = distroUIMap.stream()
						.collect(Collectors.toMap(map -> map.get("Store #"), map -> map));
				log.info("UI Map is" + attDistroUIMap.toString());
				List<Map<String, String>> distroDBMap = getDBDataForPODistro(poNbr,poRcptNbr,SKUNumber);
				Map<String, Map<String, String>> attDistroDBMap = distroDBMap.stream()
						.collect(Collectors.toMap(map -> map.get("Store #"), map -> map));
				log.info("DB Map is" + attDistroDBMap.toString());

				CommonUtils.doJbehavereportConsolelogAndAssertion("PO Distro Screen Count validated",
						"UI Store #: " + attDistroUIMap.keySet() + "DB Store #: " + attDistroDBMap.keySet(),
						attDistroUIMap.keySet().equals(attDistroDBMap.keySet()));
				for (String InHouseUPCDB : attDistroDBMap.keySet()) {
					CommonUtils.doJbehavereportConsolelogAndAssertion("PO Distro Screen Details for Store #: " + InHouseUPCDB,
							" DB Details: " + attDistroDBMap.get(InHouseUPCDB) + " UI Details: "
									+ attDistroUIMap.get(InHouseUPCDB),
							attDistroDBMap.get(InHouseUPCDB).equals(attDistroUIMap.get(InHouseUPCDB)));
				}
				break;
		}
	}

	@When("user click on $sellink link")
	public void clickLink(String sellink) throws InterruptedException {
		String PO_NBR =  (String) dataStorage.getStoredData().get("poNbr");
		switch (sellink) {
			case "PO":
				poinquiry.clickPO(PO_NBR);
				break;
			case "InHouseUPC":
				List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
				String skuNumber = poLinebarCode.get(0).getSKU();
				podetails.clickSKU(skuNumber);
				break;
			case "RetrievePO":
				podistro.navigateToInquiry();
				poinquiry.retievePO(PO_NBR);
				break;
		}
		TimeUnit.SECONDS.sleep(10);
	}

	public List<Map<String, String>> getDBDataForPOInquiry(Map<String, String> searchParams) {
		try {
			String query = buildSearchQuery(searchParams);
			System.out.println(query);
			List<Map<String, String>> queryResponseMap = DBMethods.getValuesFromDBAsStringListMap(query, "orders");
			return queryResponseMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, String>> getDBDataForPODetails(String PONumber,String poRcptNbr) {
		try {
			String query = String.format(SQLOrders.PODetails, PONumber,poRcptNbr);
			List<Map<String, String>> PODetails = DBMethods.getValuesFromDBAsStringListMap(query, "orders");
			log.info(PODetails.toString());
			return PODetails;
		} catch (Exception e) {
			log.error("Exception in getPODeatils", e);
			Assert.fail("Exception in getPODeatils", e);
			return null;
		}
	}


	public List<Map<String, String>> getDBDataForPODistro(String PONumber,String poRcptNbr,String SKUNumber) {
		try {
			String query = String.format(SQLOrders.PODistro, PONumber,PONumber,poRcptNbr,SKUNumber);
			List<Map<String, String>> PODistro = DBMethods.getValuesFromDBAsStringListMap(query, "orders");
			log.info(PODistro.toString());
			return PODistro;
		} catch (Exception e) {
			log.error("Exception in getPODistro", e);
			Assert.fail("Exception in getPODistro", e);
			return null;
		}
	}

	public static String buildSearchQuery(Map<String, String> searchParams) {
		String searchQuery = SQLOrders.POInquiry;
		StringBuilder queryArgs = new StringBuilder();
		String searchValue;
		for (Map.Entry<String, String> searchParam : searchParams.entrySet()) {
			searchValue = CommonUtils.getQuotedString(searchParam.getValue());
			queryArgs.append("AND ");
			switch (searchParam.getKey()) {
				case "PO":
					queryArgs.append("po_rcpt_xref.PO_NBR = ").append(searchValue).append(" \n");
					break;
				case "Distro":
					queryArgs.append("IF(DISTRO_CMPLT_TS IS NOT NULL,'Y','N') = ").append(searchValue).append(" \n");
					break;
				case "Appt #":
					queryArgs.append("TRIM(po_hdr.PO_GEN_DESC) = ").append(searchValue).append(" \n");
					break;
				default:
					throw new NoSuchElementException("No search key found");
			}
		}
		return String.format(searchQuery, queryArgs.toString());
	}

	@Then("User validates Report ID for Po displayed in PO Inquiry page")
	public void ValidateReportIDHyperLink()
	{
		try
		{
			boolean ValidationResult = poinquiry.ReportIdHyperLinkValidation();
			if(ValidationResult==false)
			{
				Assert.fail();
			}
		}
		catch (Exception e) {
			CommonUtils.doJbehavereportConsolelogAndAssertion("Exception in Report Id Validation",e.toString(),true);
		}
	}

	@Then("User validates Quick Print Button displayed in Inquiry page")
	public void ValidateQuickPrintButton()
	{
		try
		{
			boolean ValidationResult = poinquiry.QuickPrintButtonValidation();
			if(ValidationResult==false)
			{
				Assert.fail();
			}
		}
		catch (Exception e) {
			CommonUtils.doJbehavereportConsolelogAndAssertion("Exception in Quick print button Validation",e.toString(),true);
		}
	}

}
