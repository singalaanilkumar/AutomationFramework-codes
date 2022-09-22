package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLASNReceipts;
import com.macys.mst.DC2.EndToEnd.pageobjects.ASNReceiptsPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.RequestUtil;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ASNReceiptsSteps {
	public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
	public long TestNGThreadID = Thread.currentThread().getId();
	private StepsContext stepsContext;
	SeUiContextBase seUiContextBase = new SeUiContextBase();
	private ASNReceiptsPage asnReceiptsPage = new ASNReceiptsPage(driver);
	private RequestUtil requestUtil = new RequestUtil();
	
	public ASNReceiptsSteps(StepsContext stepsContext) {
		this.stepsContext = stepsContext;
	}
	
	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
		TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}

	@Then("User navigates to ASN Receipts and validates the messages")
	public void selectASNReceipt() {
	//	String rcptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());
		String rcptNbr = "9186851";
		List<Map<String, String>> asnReceiptsUI = asnReceiptsPage.getGridElementsMap();
		log.info("asnReceiptsUI = {}",asnReceiptsUI.toString()); 
		Map<String, Map<String, String>> asnReceiptsUIMap = asnReceiptsUI.stream().collect(Collectors.toMap(map -> map.get("Receipt NBR"), map -> map));
		log.info("asnReceiptsUIMap: {}",asnReceiptsUIMap);
		asnReceiptsUIMap.get(rcptNbr).remove("Manual Close");
		log.info("asnReceiptsUIMap: {}",asnReceiptsUIMap);
				
		try {
			
			List<Map<String, String>> asnReceiptsFromDB = fetchASNReceiptsDetailsFromDB(rcptNbr);
			log.info("asnReceiptsDBvalues = {}",asnReceiptsFromDB.toString()); 
			Map<String, Map<String, String>> asnReceiptsAttributesFromDBMap = asnReceiptsFromDB.stream().collect(Collectors.toMap(map -> map.get("Receipt NBR"), map -> map));
			log.info("asnReceiptsAttributesFromDBMap: {}",asnReceiptsAttributesFromDBMap);

			CommonUtils.doJbehavereportConsolelogAndAssertion("ASN Receipts Screen Count validated",
					"UI Activity IDs: "+asnReceiptsUIMap.get(rcptNbr).keySet()+" DB Activity IDs: "+asnReceiptsAttributesFromDBMap.keySet(),
					asnReceiptsUIMap.get(rcptNbr).keySet().equals(asnReceiptsAttributesFromDBMap.get(rcptNbr).keySet())); 
			
			CommonUtils.doJbehavereportConsolelogAndAssertion("ASN Receipts for Receipt Number"+rcptNbr,
						" DB Details: " + asnReceiptsAttributesFromDBMap.get(rcptNbr) + " UI Details: " + asnReceiptsUIMap.get(rcptNbr),
						asnReceiptsAttributesFromDBMap.get(rcptNbr).equals(asnReceiptsUIMap.get(rcptNbr))); 
			 
		}
		catch (Exception e) {
			log.error("Error in validation of UI ASN Receipts attributes with ASN Receipts attributes"+e);
		}
		asnReceiptsPage.closeReceipt(rcptNbr);
    }

	private List<Map<String, String>> fetchASNReceiptsDetailsFromDB(String receiptNbr) {
		try {
			String query = String.format(SQLASNReceipts.GET_ASN_RECEIPTNBR_DETAILS,receiptNbr,receiptNbr);
			System.out.println(query);
			List<Map<String, String>> queryResponseMap = DBMethods.getValuesFromDBAsStringListMap(query, "orders");
			return queryResponseMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
