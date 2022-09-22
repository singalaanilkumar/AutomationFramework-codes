package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLPo4Walls;
import com.macys.mst.DC2.EndToEnd.model.LocationDistro;

import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.ContainerInquiryPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.SplitMovePage;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData.PoLinebarCode;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.RequestUtil;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.vavr.collection.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplitMoveSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();

    private SplitMovePage splitmovePage = PageFactory.initElements(driver, SplitMovePage.class);
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    RandomUtil randomUtil = new RandomUtil();
    private RequestUtil requestUtil = new RequestUtil();
    ValidationUtil validationUtils = new ValidationUtil();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    String containerID;
    String totes;
    private List<String> toteIds;

    public SplitMoveSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @SuppressWarnings("unchecked")
    
    
    @Then("inventory is created for Split Move")
    public void createInventory_SplitMove() {
        String Endpoint = commonUtils.getUrl("InventoryServices.CreateInventory");
        String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
        String poRcptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());
        String ToteNbr = randomUtil.getRandomValue("5000-D-16");
        dataStorage.getStoredData().put("Tote_Container", ToteNbr);
        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        String SKUNbr = poLinebarCode.stream().findFirst().orElse(null).getSKU();
        String barcode = poLinebarCode.stream().findFirst().orElse(null).getPoLineBarCode();
        String reqParams = "{#contNum:" + ToteNbr +",#contTyp:TOTE,#contStat:CRE,#PO:" + poNbr + ",#Receipt:" + poRcptNbr + ",#item:"+ SKUNbr + ",#qty:50,#itmStat:AVL,#toteNum:" + ToteNbr +",#lineBarcod:" + barcode + ",#dept:803}";
  //      String reqParam = "{#PONBR:" + poNbr + ",#ReceiptNbr:" + poRcptNbr + ",#ToteNbr:"+ToteNbr+",#SKUNbr:"+ SKUNbr+"`}";
        List<String> filledRequest = requestUtil.getRequestBody(reqParams, "CreateTote.json");
        String payload = filledRequest.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        StepDetail.addDetail(String.format("The request posted is: " + filledRequest), true);
        Response createInvResponse = WhmRestCoreAutomationUtils.postRequestResponse(Endpoint, payload).asResponse();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Tote creation:" + Endpoint , filledRequest.toString(), validationUtils.validateResponseStatusCode(createInvResponse, 201));

    }
    
    @Then("source container quantity is split and moved to target container")
    public void splitAndMoveInventoryValidation() throws Exception {
    	String sourceContainer =  (String) dataStorage.getStoredData().get("Tote_Container");
    	int sourceQty = 0;
    	Map<String,String>	containerQtyAPI = new HashMap<>();
    	Map<String,String> containerQtyUI = new HashMap<>();
        splitmovePage.clickNavOption("Split Move");
        splitmovePage.scansourceContainerID(sourceContainer);

        String containerResponse = commonUtils.getContainerDetailsbyBarcode(sourceContainer);
        Map<String, String> skuPOLineMap = getUPCPOLineMap(containerResponse);
        StepDetail.addDetail(String.valueOf(skuPOLineMap),true);
        sourceQty = Integer.parseInt(skuPOLineMap.get("quantity"));
        Integer firsttargetQty = sourceQty/2;
        Integer secondtargetQty = sourceQty-firsttargetQty;
        Integer count = 1;

        for (String sku : skuPOLineMap.keySet()) {
        	if(sku.equalsIgnoreCase("quantity"))
        	continue;
            String poLineResponse = getPOLineResponse(skuPOLineMap.get(sku));
            CommonUtils.doJbehavereportConsolelogAndAssertion("POline response :",poLineResponse,true);
            Map<String, String> upcDetailsAPIData = getUPCDetails(poLineResponse, skuPOLineMap.get("quantity"));
            Map<String, String> upcDetailsUIData = splitmovePage.getScreenData("//tr");

            CommonUtils.doJbehavereportConsolelogAndAssertion("UPC Details Validated for SKU " + upcDetailsUIData,
                    "UI Details " + upcDetailsAPIData.toString() + " API Details " + upcDetailsAPIData.toString(),
                    upcDetailsUIData.equals(upcDetailsAPIData));
            
            	splitmovePage.scanUnits(firsttargetQty);
            	String targetToteId = createToteId();
            	splitmovePage.scanTargetContainer(targetToteId);
            	splitmovePage.validateSplitMoveConfirmationMsg(targetToteId,firsttargetQty);
            	containerQtyUI.put(targetToteId, String.valueOf(firsttargetQty));
            	splitmovePage.scanUnits(secondtargetQty);
            	targetToteId = createToteId();
            	splitmovePage.scanTargetContainer(targetToteId);
            	splitmovePage.validateEmptyContainerConfirmationMsg();
            	containerQtyUI.put(targetToteId, String.valueOf(secondtargetQty));
            	
            	for (String container : containerQtyUI.keySet()) {
            		containerResponse = commonUtils.getContainerDetailsbyBarcode(container);
            		containerQtyAPI = getQty(containerQtyAPI,containerResponse,container);
            	}

                CommonUtils.doJbehavereportConsolelogAndAssertion("Qty split and moved from container: " + sourceContainer,
                        "Qty split and moved to target containers in UI" + containerQtyUI.toString() + " Inventory Target Container Details in API: " + containerQtyAPI.toString(),
                        containerQtyUI.equals(containerQtyAPI));
            
        }
        
        
    }

    private Map<String, String> getQty(Map<String,String> containerQtyAPI,String containerResponse,String container) {
    	
         JSONArray inventorySnapshotList = new JSONObject(containerResponse).getJSONArray("inventorySnapshotList");
         for (int i = 0; i < inventorySnapshotList.length(); i++) {
             JsonPath inventorySnapshot = new JsonPath(inventorySnapshotList.get(i).toString());
             containerQtyAPI.put(container, StringUtils.defaultString(inventorySnapshot.getString("quantity")));
         }
         return containerQtyAPI;
	}

	private Map<String, String> getContainerDetails(String response) {
        Map<String, String> containerDetails = new HashMap<String, String>();
        JsonPath containerResponse = new JsonPath(response);
        containerDetails.put("Container ID", containerResponse.get("container.barCode"));
        containerDetails.put("Type", ExpectedDataProperties.getContainerType().get(StringUtils.defaultString(containerResponse.getString("container.containerType"))));
        containerDetails.put("Status", ExpectedDataProperties.getContainerStatus().get(StringUtils.defaultString(containerResponse.getString("container.containerStatusCode"))));
        containerDetails.put("Location", StringUtils.defaultString(containerResponse.getString("container.containerRelationshipList.findAll {containerRelationshipList -> containerRelationshipList.parentContainerType=='LCN'}.parentContainer[0]")));
        containerDetails.put("Divert Destination", StringUtils.defaultString(containerResponse.getString("container.Destination")));
        return containerDetails;
    }

    private Map<String, String> getUPCDetails(String response, String quantity) {
        Map<String, String> upcDetails = new HashMap<String, String>();
        JsonPath poLineResponse = new JsonPath(response);
        upcDetails.put("UPC", StringUtils.defaultString(poLineResponse.getString("skuUpc")));
        upcDetails.put("Color", StringUtils.defaultString(poLineResponse.getString("colorDesc")));
        upcDetails.put("Size", StringUtils.defaultString(poLineResponse.getString("sizeDesc")));
        upcDetails.put("Units", quantity);
        return upcDetails;
    }

    private Map<String, String> getUPCPOLineMap(String response) {
        Map<String, String> upcPOLineMap = new HashMap<>();
        JSONArray inventorySnapshotList = new JSONObject(response).getJSONArray("inventorySnapshotList");
        for (int i = 0; i < inventorySnapshotList.length(); i++) {
            JsonPath inventorySnapshot = new JsonPath(inventorySnapshotList.get(i).toString());
            upcPOLineMap.put("quantity", StringUtils.defaultString(inventorySnapshot.getString("quantity")));
            upcPOLineMap.put(inventorySnapshot.get("item"), inventorySnapshot.get("attributeList.findAll {attributeList -> attributeList.key=='POLineBarcode'}.values[0][0]"));
        }
        return upcPOLineMap;
    }

    private String getPOLineResponse(String barcode) {
        try {
            String endpoint = commonUtils.getUrl("pofourwalls.poLineService").replace("#poLineBarcode", barcode);
            Response response = WhmRestCoreAutomationUtils.getRequestResponse(endpoint).asResponse();
            return response.asString();
        } catch (Exception e) {
            log.info("unable to get inventory for barCode: " + barcode + " with error " + e.getMessage());
            return "";
        }
    }
    
    private String createToteId() throws Exception {
        return new DataCreateModule().generateContainer("empty", "Tote");
    }

}


