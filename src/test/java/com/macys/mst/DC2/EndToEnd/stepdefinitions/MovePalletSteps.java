package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.ContainerInquiryPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.MovePalletPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MovePalletSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();

    private MovePalletPage movepalletPage = PageFactory.initElements(driver, MovePalletPage.class);
    private ContainerInquiryPage containerinquiryPage = PageFactory.initElements(driver, ContainerInquiryPage.class);

    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    String containerID;
    String totes;
    private List<String> toteIds;

    public MovePalletSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @SuppressWarnings("unchecked")
    @Then("move pallet to staging $parm location")
    public void splitAndMoveInventoryValidation(String StagingLocation) throws Exception {
        String ContainerId = (String) dataStorage.getStoredData().get("PalletCreatedFromBuildPallet");
        movepalletPage.clickNavOption("Move Pallet");
        movepalletPage.scanContainerID(ContainerId);

        String containerResponse = getPalletDetailsResponse(ContainerId);
        Map<String, String> containerAttributesAPI = getContainerAttributesMap(containerResponse);

        movepalletPage.validateMovePalletScreen(ContainerId, containerAttributesAPI);
        movepalletPage.movePalletToStagingLocation(StagingLocation);

    }


    private Map<String, String> getContainerAttributesMap(String response) {
        Map<String, String> containerAttributesMap = new HashMap<>();
        JSONObject containerDetails = new JSONObject(response);
        containerAttributesMap.put("Pallet ID", StringUtils.defaultString(containerDetails.getString("barCode")));
        JSONArray attributeList = new JSONObject(response).getJSONArray("attributeList");
        for (int i = 0; i < attributeList.length(); i++) {
            JsonPath attributes = new JsonPath(attributeList.get(i).toString());
            containerAttributesMap.put("PO", StringUtils.defaultString(attributes.getString("PO")));
            containerAttributesMap.put("POReceipt", StringUtils.defaultString(attributes.getString("POReceipt")));
            containerAttributesMap.put("Process Area", StringUtils.defaultString(attributes.getString("ProcessArea")));
            containerAttributesMap.put("PID", StringUtils.defaultString(attributes.getString("PID")));
            containerAttributesMap.put("Number Of containers", StringUtils.defaultString(attributes.getString("NumberOfContainers")));
        }
        return containerAttributesMap;
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


}


