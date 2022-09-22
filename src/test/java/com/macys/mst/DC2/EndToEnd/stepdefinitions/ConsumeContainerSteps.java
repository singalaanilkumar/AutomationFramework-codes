package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.ConsumeContainerPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConsumeContainerSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();

    private ConsumeContainerPage consumecontainerPage = PageFactory.initElements(driver, ConsumeContainerPage.class);
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    String containerID;
    String totes;
    private List<String> toteIds;

    public ConsumeContainerSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    StepsDataStore dataStorage = StepsDataStore.getInstance();

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @SuppressWarnings("unchecked")
    @Then("consume pallet container")
    public void consumeContainerValidation() throws Exception {
        String containerId = (String) dataStorage.getStoredData().get("PalletCreatedFromBuildPallet");
        consumecontainerPage.clickNavOption("Consume Container");
        consumecontainerPage.scanContainerID(containerId);
        consumecontainerPage.validateConsumeContainerScreen(containerId);
        consumecontainerPage.validateConsumeContainerConfirmationMsg(containerId);
    }

    private Map<String, String> getQty(String containerResponse, String container) {
        Map<String, String> containerQtyAPI = new HashMap<>();
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


