package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.ContainerInquiryPage;
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
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ContainerInquirySteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();

    private ContainerInquiryPage containerInquiryPage = PageFactory.initElements(driver, ContainerInquiryPage.class);
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    String containerID;
    String totes;
    private List<String> toteIds;

    public ContainerInquirySteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    StepsDataStore dataStorage = StepsDataStore.getInstance();

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @SuppressWarnings("unchecked")
    @Then("click on Container Inquiry link to check totes details")
    public void verifyContainerInquiryUPCDetails() {
        String randomToteForPO = null;
        try {
            Map<String, List<String>> poLineToteMap = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
            randomToteForPO = poLineToteMap.values().stream().flatMap(List::stream).collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            randomToteForPO = (String) dataStorage.getStoredData().get("Tote_locataeContainer");
        }
        containerInquiryPage.clickNavOption("Container Inquiry");
        containerInquiryPage.scanContainerID(randomToteForPO);

        String containerResponse = commonUtils.getContainerDetailsbyBarcode(randomToteForPO);

        Map<String, String> containerInquiryUIData = containerInquiryPage.getScreenData("//*[@id='app']/div/div[2]/div/div/div[@class='MarginBottom']");
        Map<String, String> containerInquiryAPIData = getContainerDetails(containerResponse);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Container UI Details Validated for " + randomToteForPO,
                "UI Details " + containerInquiryUIData.toString() + " API Details " + containerInquiryAPIData.toString(),
                containerInquiryUIData.equals(containerInquiryAPIData));

        containerInquiryPage.clickDetailsButton();
        Map<String, String> skuPOLineMap = getUPCPOLineMap(containerResponse);

        for (String sku : skuPOLineMap.keySet()) {
            containerInquiryPage.clickUPCLink(sku);
            String poLineResponse = getPOLineResponse(skuPOLineMap.get(sku));
            Map<String, String> upcDetailsAPIData = getUPCDetails(poLineResponse);
            Map<String, String> upcDetailsUIData = containerInquiryPage.getScreenData("//*[text()='UPC Details']/parent::div/following-sibling::div//tr");
            // Need clarification on Carton Details to be displayed - Not validating QTY and status until then
            upcDetailsUIData.remove("Quantity");
            upcDetailsUIData.remove("Inventory Status");

            containerInquiryPage.clickOKButton();
            CommonUtils.doJbehavereportConsolelogAndAssertion("UPC Details Validated for SKU " + upcDetailsUIData,
                    "UI Details " + upcDetailsAPIData.toString() + " API Details " + upcDetailsAPIData.toString(),
                    upcDetailsUIData.equals(upcDetailsAPIData));
        }
    }

    private Map<String, String> getContainerDetails(String response) {
        Map<String, String> containerDetails = new HashMap<String, String>();
        JsonPath containerResponse = new JsonPath(response);
        containerDetails.put("Container ID", containerResponse.get("container.barCode"));
        containerDetails.put("Type", ExpectedDataProperties.getContainerType().get(StringUtils.defaultString(containerResponse.getString("container.containerType"))));
        containerDetails.put("Status", ExpectedDataProperties.getContainerStatus().get(StringUtils.defaultString(containerResponse.getString("container.containerStatusCode"))));
        containerDetails.put("Location", StringUtils.defaultString(containerResponse.getString("container.containerRelationshipList.findAll {containerRelationshipList -> containerRelationshipList.parentContainerType=='LCN'}.parentContainer[0]")));
        containerDetails.put("Last Action Timestamp", StringUtils.defaultString(containerResponse.getString("container.createdOn")));
        containerDetails.put("PO", StringUtils.defaultString(containerResponse.getString("container.attributeList.findAll {attributeList -> attributeList.key=='PO'}.values[0][0]")));
        containerDetails.put("POReceipt", StringUtils.defaultString(containerResponse.getString("container.attributeList.findAll {attributeList -> attributeList.key=='POReceipt'}.values[0][0]")));
        containerDetails.put("Process Area", StringUtils.defaultString(containerResponse.getString("container.attributeList.findAll {attributeList -> attributeList.key=='ProcessArea'}.values[0][0]")));
        return containerDetails;
    }

    private Map<String, String> getPalletContainerDetails(String response) {
        Map<String, String> containerDetails = new HashMap<String, String>();
        JsonPath containerResponse = new JsonPath(response);
        containerDetails.put("Container ID", containerResponse.get("barCode"));
        containerDetails.put("Type", ExpectedDataProperties.getContainerType().get(StringUtils.defaultString(containerResponse.getString("containerType"))));
        containerDetails.put("Status", ExpectedDataProperties.getContainerStatus().get(StringUtils.defaultString(containerResponse.getString("containerStatusCode"))));
        containerDetails.put("Location", StringUtils.defaultString(containerResponse.getString("containerRelationshipList.findAll {containerRelationshipList -> containerRelationshipList.parentContainerType=='LCN'}.parentContainer[0]")));
        //containerDetails.put("Process Area", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='ProcessArea'}.values[0][0]")));
        containerDetails.put("Last Action Timestamp", containerResponse.get("updatedOn"));
        containerDetails.put("PO", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='PO'}.values[0][0]")));
        containerDetails.put("PO Receipt", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='POReceipt'}.values[0][0]")));
        return containerDetails;
    }

    private Map<String, String> getUPCDetails(String response) {
        Map<String, String> upcDetails = new HashMap<String, String>();
        JsonPath poLineResponse = new JsonPath(response);
        upcDetails.put("POLineBarcode", StringUtils.defaultString(poLineResponse.getString("poLineBarCode")));
        upcDetails.put("Department", StringUtils.defaultString(poLineResponse.getString("deptNbr")));
        upcDetails.put("PID", StringUtils.defaultString(poLineResponse.getString("pid")));
        upcDetails.put("UPC", StringUtils.defaultString(poLineResponse.getString("skuUpc")));
        upcDetails.put("PO", StringUtils.defaultString(poLineResponse.getString("poNbr")));
        upcDetails.put("Color", StringUtils.defaultString(poLineResponse.getString("colorDesc")));
        upcDetails.put("PID Desc", StringUtils.defaultString(poLineResponse.getString("pidDesc")));
        upcDetails.put("Size", StringUtils.defaultString(poLineResponse.getString("sizeDesc")));
        upcDetails.put("Ticket Type", StringUtils.defaultString(poLineResponse.getString("ticketType")));
        return upcDetails;
    }

    private Map<String, String> getUPCPOLineMap(String response) {
        Map<String, String> upcPOLineMap = new HashMap<>();
        JSONArray inventorySnapshotList = new JSONObject(response).getJSONArray("inventorySnapshotList");
        for (int i = 0; i < inventorySnapshotList.length(); i++) {
            JsonPath inventorySnapshot = new JsonPath(inventorySnapshotList.get(i).toString());
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

    @Then("click on Container Inquiry link to check pallet details")
    public void verifyContainerInquiryPalletDetails(String ScreenType) {
        String containerId = "";
        switch (ScreenType) {
            case "SplitPallet":
                containerId = (String) dataStorage.getStoredData().get("PalletCreatedForSplitPallet");
                break;
            case "EndPallet":
                containerId = (String) dataStorage.getStoredData().get("PalletCreatedForEndPallet");
                break;
            default:
                containerId = (String) dataStorage.getStoredData().get("PalletCreatedFromBuildPallet");
                break;
        }
        containerInquiryPage.clickNavOption("Container Inquiry");
        containerInquiryPage.scanContainerID(containerId);
        String containerResponse = getPalletDetailsResponse(containerId);
        Map<String, String> containerInquiryUIData = containerInquiryPage.getScreenData("//*[@id='app']/div/div[2]/div/div/div[@class='MarginBottom']");
        Map<String, String> containerInquiryAPIData = getPalletContainerDetails(containerResponse);
        String Location_UI = containerInquiryUIData.get("Location");
        String Location_API = containerInquiryAPIData.get("Location");
        if ((Location_UI == null || Location_UI.isEmpty()) && Location_API.isEmpty()) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Container UI Details Validated for Location field " + containerId,
                    "UI Location " + Location_UI + " API Details " + Location_API,
                    true);
        } else {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Container UI Details Validated for Location field " + containerId,
                    "UI Location " + Location_UI + " API Details " + Location_API,
                    Location_UI.equals(Location_API));
        }
        containerInquiryUIData.remove("Location");
        containerInquiryUIData.remove("Process Area");
        containerInquiryUIData.remove("Last Action Timestamp");
        containerInquiryAPIData.remove("Last Action Timestamp");
        containerInquiryAPIData.remove("Location");
        CommonUtils.doJbehavereportConsolelogAndAssertion("Container UI Details Validated for " + containerId,
                "UI Details " + containerInquiryUIData.toString() + " API Details " + containerInquiryAPIData.toString(),
                containerInquiryUIData.equals(containerInquiryAPIData));

        containerInquiryPage.clickDetailsButton();
        Map<String, String> palletDtlMap = getPalletDetailsMap(containerResponse);
        Map<String, String> palletDetailsUIData = containerInquiryPage.getScreenData("//div[@class='MarginBottom']");

        // Need clarification on Carton Details to be displayed - Not validating QTY and status until then
        palletDetailsUIData.remove("Count");
        palletDetailsUIData.remove("ProcessArea");
        CommonUtils.doJbehavereportConsolelogAndAssertion("Pallet Details Validated for " + containerId,
                "UI Details " + palletDetailsUIData.toString() + " API Details " + palletDtlMap.toString(),
                containerInquiryUIData.equals(containerInquiryAPIData));
        containerInquiryPage.clickBackButton();
        containerInquiryPage.clickAttributeButton();
        Map<String, String> palletAttributesMap = getPalletAttributeMap(containerResponse);
        Map<String, String> palletAttributesUIData = containerInquiryPage.getScreenData("//div[@class='MarginBottom']");
        palletAttributesUIData.remove("Count");
        palletAttributesUIData.remove("ProcessArea");
        CommonUtils.doJbehavereportConsolelogAndAssertion("Pallet Attributes Validated for " + containerId,
                "UI Details " + palletAttributesUIData.toString() + " API Details " + palletAttributesMap.toString(),
                containerInquiryUIData.equals(containerInquiryAPIData));
        containerInquiryPage.clickExitButton();
    }

    public String getPalletDetailsResponse(String barcode) {
        try {
            String endpoint = commonUtils.getUrl("InventoryServices.ContainerInquiry").replace("#containerBarcode", barcode);
            Response response = WhmRestCoreAutomationUtils.getRequestResponse(endpoint).asResponse();
            return response.asString();
        } catch (Exception e) {
            log.info("unable to get inventory for barCode: " + barcode + " with error " + e.getMessage());
            return "";
        }
    }

    private Map<String, String> getPalletDetailsMap(String response) {
        Map<String, String> palletDetails = new HashMap<>();
        JsonPath containerResponse = new JsonPath(response);
        palletDetails.put("Container ID", containerResponse.get("barCode"));
        palletDetails.put("Type", ExpectedDataProperties.getContainerType().get(StringUtils.defaultString(containerResponse.getString("containerType"))));
        //   containerDetails.put("Process Area", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='ProcessArea'}.values[0][0]")));
        palletDetails.put("PO", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='PO'}.values[0][0]")));
        palletDetails.put("Receipt", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='POReceipt'}.values[0][0]")));
        palletDetails.put("PID", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='PID'}.values[0][0]")));
        palletDetails.put("NumberOfContainers", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='NumberOfContainers'}.values[0][0]")));
        return palletDetails;
    }

    private Map<String, String> getPalletAttributeMap(String response) {
        Map<String, String> palletAttributes = new HashMap<>();
        JsonPath containerResponse = new JsonPath(response);
        palletAttributes.put("Container ID", containerResponse.get("barCode"));
        palletAttributes.put("Type", ExpectedDataProperties.getContainerType().get(StringUtils.defaultString(containerResponse.getString("containerType"))));
        palletAttributes.put("PID", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='PID'}.values[0][0]")));
        palletAttributes.put("NumberOfContainers", StringUtils.defaultString(containerResponse.getString("attributeList.findAll {attributeList -> attributeList.key=='NumberOfContainers'}.values[0][0]")));
        return palletAttributes;
    }


    @Then("click on Container Inquiry link to check details after End Pallet Operation")
    public void EndPallet_Validation() {
        verifyContainerInquiryPalletDetails("EndPallet");
    }

    @Then("click on Container Inquiry link to check details after Split Pallet Operation")
    public void SplitPallet_Validation() {
        verifyContainerInquiryPalletDetails("SplitPallet");
    }

    @Then("click on Container Inquiry link to check details after Adjust Pallet Operation")
    public void AdjustPallet_Validation() {
        verifyContainerInquiryPalletDetails("AdjustPallet");
    }

    @Then("click on Container Inquiry link to check details after Move Pallet Operation")
    public void MovePallet_Validation() {
        verifyContainerInquiryPalletDetails("MovePallet");
    }

    @Then("click on Container Inquiry link to check details after Build Pallet Operation")
    public void BuildPallet_Validation() {
        verifyContainerInquiryPalletDetails("BuildPallet");
    }
}

