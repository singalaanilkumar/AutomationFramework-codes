package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.MagicTotePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.RequestUtil;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MagicToteSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    CommonUtils commonUtils = new CommonUtils();
    RandomUtil randomUtil = new RandomUtil();
    private RequestUtil requestUtil = new RequestUtil();
    ValidationUtil validationUtils = new ValidationUtil();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    private StepsContext stepsContext;

    public MagicToteSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    private MagicTotePage magicTotepage = PageFactory.initElements(driver, MagicTotePage.class);

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @When("select MagicTote")
    public void select_MagicTote() {
        magicTotepage.selectMagicTote();
    }

    @Then("scan a bad TOte and validate the activity of the tote")
    public void delete_badTotes() {
        magicTotepage.remove_badTote();
        magicTotepage.validateToteActivities();
    }

    @Given("wsm activities are created for Magic Tote")
    public void createInventory_MagicTote() {
        String Endpoint = commonUtils.getUrl("WSM.getActivities");
        String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
        String poRcptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());
        String ToteNbr = (String) dataStorage.getStoredData().get("Tote_locataeContainer");
        String SKUNbr = (String) dataStorage.getStoredData().get("item");
        int quantity = (int) dataStorage.getStoredData().get("QTY");
        String qty = String.valueOf(quantity);
        String reqParam = "{#PONBR:" + poNbr + ",#ReceiptNbr:" + poRcptNbr + ",#ToteNbr:" + ToteNbr + ",#SKUNbr:" + SKUNbr + ",#qty:" + qty + "}";
        List<String> filledRequest = requestUtil.getRequestBody(reqParam, "WSMActivityPayload.json");
        StepDetail.addDetail(String.format("The request posted is: " + filledRequest), true);
        Response createInvResponse = WhmRestCoreAutomationUtils.postRequestResponse(Endpoint, filledRequest.toString()).asResponse();
        CommonUtils.doJbehavereportConsolelogAndAssertion("WSM activity creation:" + Endpoint, filledRequest.toString(), validationUtils.validateResponseStatusCode(createInvResponse, 201));

    }

    @Given("the details of the Totes")
    public void getToteDetails() {
        HashSet<String> toteIds = (HashSet<String>) stepsContext.get(Context.Tote_List.name());
        String containerbarcode = "";
        for (String eachTote : toteIds) {
            containerbarcode = eachTote;
            break;
        }
        JsonPath binDetails = new JsonPath(commonUtils.getContainerDetailsbyBarcode(containerbarcode));
        Integer QTY = binDetails.getInt("inventorySnapshotList[0].quantity");
        String item = binDetails.get("inventorySnapshotList[0].item").toString();
        String ToteNbr = binDetails.get("inventorySnapshotList[0].container").toString();
        dataStorage.getStoredData().put("QTY", QTY);
        dataStorage.getStoredData().put("item", item);
        dataStorage.getStoredData().put("Tote_locataeContainer", ToteNbr);
    }

    @When("WSM activities are created for given totes")
    public void wsmActivites() {
        HashSet<String> toteIds = (HashSet<String>) stepsContext.get(Context.Tote_List.name());
        for (String eachTote : toteIds) {
            String Endpoint = commonUtils.getUrl("WSM.getActivities");
            String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
            String poRcptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());
            String ToteNbr = eachTote;
            String SKUNbr = (String) dataStorage.getStoredData().get("item");
            Integer quantity = (Integer) dataStorage.getStoredData().get("QTY");
            String qty = String.valueOf(quantity);
            String reqParam = "{#PONBR:" + poNbr + ",#ReceiptNbr:" + poRcptNbr + ",#ToteNbr:" + ToteNbr + ",#SKUNbr:" + SKUNbr + ",#qty:" + qty + "}";
            List<String> filledRequest = requestUtil.getRequestBody(reqParam, "WSMSplitActivityPayload.json");
            StepDetail.addDetail(String.format("The request posted is: " + filledRequest), true);
            Response createInvResponse = WhmRestCoreAutomationUtils.postRequestResponse(Endpoint, filledRequest.toString()).asResponse();
            CommonUtils.doJbehavereportConsolelogAndAssertion("WSM activity creation:" + Endpoint, filledRequest.toString(), validationUtils.validateResponseStatusCode(createInvResponse, 201));
        }
    }
}


