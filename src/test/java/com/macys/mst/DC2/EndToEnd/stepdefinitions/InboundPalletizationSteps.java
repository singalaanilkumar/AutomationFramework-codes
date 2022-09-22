package com.macys.mst.DC2.EndToEnd.stepdefinitions;


import com.macys.mst.DC2.EndToEnd.pageobjects.PODRRPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.BuildPalletPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.DC2SubMenu;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InboundPalletizationSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();

    private BuildPalletPage buildPalletPage = PageFactory.initElements(driver, BuildPalletPage.class);
    private DC2SubMenu dc2SubMenu = PageFactory.initElements(driver, DC2SubMenu.class);
    private PODRRPage podrrPage = PageFactory.initElements(driver, PODRRPage.class);
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    RandomUtil randomUtil = new RandomUtil();

    public InboundPalletizationSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Then("user start building pallets and locate to $loc location")
    public void verifyBuildPalletDetails(String loc) throws Exception {
        Map<String, String> BuildPalletUIElements = new HashMap<>();
        dataStorage.getStoredData().put("ProcessingStageLocNbr", loc);
        dc2SubMenu.clickOnGivenSubMenu("Build_Pallet");
        String poreport_Id = (String) dataStorage.getStoredData().get("po_report_id");
        buildPalletPage.scanBuildPalletReportID(poreport_Id);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Report ID Entered for Build Pallet : ", poreport_Id, true);
        String randomPalletID = randomUtil.getRandomValue("PLT-D-17");
        buildPalletPage.scanPalletNumber(randomPalletID);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Pallet Number Entered in Build Pallet : ", randomPalletID, true);
        dataStorage.getStoredData().put("PalletCreatedFromBuildPallet", randomPalletID);
        buildPalletPage.isPalletEmptyYesBtn();
        String randomNumber = randomUtil.getRandomValue("D-1");
        buildPalletPage.selectNumOfContainers(randomNumber);
        buildPalletPage.clickButton("Enter");
        CommonUtils.doJbehavereportConsolelogAndAssertion("Number of Containers Entered in Build Pallet : ", randomNumber, true);
        String locNbrPresentInScreen = buildPalletPage.getLocateIdOnScreen();
        log.info("Validated Location Number " + locNbrPresentInScreen);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Suggested location ID in Build Pallet : ", locNbrPresentInScreen, true);
        if (!locNbrPresentInScreen.equals("")) {
            buildPalletPage.scanLocationID(locNbrPresentInScreen);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Entered location ID in Build Pallet : ", locNbrPresentInScreen, true);
        } else {
            buildPalletPage.scanLocationID(loc);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Entered location ID in Build Pallet : ", loc, true);
        }
    }

    @Then("user start building pallets and click on end Pallet Btn")
    public void clickEndPalletBtn() {
        dc2SubMenu.clickOnGivenSubMenu("Build_Pallet");
        String poreport_Id = (String) dataStorage.getStoredData().get("po_report_id");
        buildPalletPage.scanBuildPalletReportID(poreport_Id);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Report ID Entered for End Pallet : ", poreport_Id, true);
        String randomPalletID = randomUtil.getRandomValue("PLT-D-17");
        buildPalletPage.scanPalletNumber(randomPalletID);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Pallet Number Entered for End Pallet validation: ", randomPalletID, true);
        dataStorage.getStoredData().put("PalletCreatedForEndPallet", randomPalletID);
        buildPalletPage.isPalletEmptyYesBtn();
        String randomNumber = randomUtil.getRandomValue("D-1");
        buildPalletPage.selectNumOfContainers(randomNumber);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Number of Containers Entered for EndPallet Validation: ", randomNumber, true);
        buildPalletPage.endPallet();
    }

    private Map<String, String> getBuildPalletDetails(String response) {
        Map<String, String> buildPalletDetails = new HashMap<String, String>();
        JsonPath poLineResponse = new JsonPath(response);
        buildPalletDetails.put("PO", StringUtils.defaultString(poLineResponse.getString("poNbr")));
        buildPalletDetails.put("Receipt", StringUtils.defaultString(poLineResponse.getString("receiptNbr")));
        buildPalletDetails.put("Pallet ID", StringUtils.defaultString(poLineResponse.getString("palletId")));
        buildPalletDetails.put("Process Area", StringUtils.defaultString(poLineResponse.getString("processArea")));
        buildPalletDetails.put("PID", StringUtils.defaultString(poLineResponse.getString("pid")));
        buildPalletDetails.put("Num# of Containers", StringUtils.defaultString(poLineResponse.getString("nbrOfContainers")));
        return buildPalletDetails;
    }

    //Reading value from Postman services
    private Map<String, String> getbuildPalletAPIDetails(String response) {
        Map<String, String> buildPalletDetails = new HashMap<String, String>();
        JsonPath buildPalletResponse = new JsonPath(response);
        buildPalletDetails.put("Container ID", buildPalletResponse.get("container.barCode"));
        buildPalletDetails.put("Type", ExpectedDataProperties.getContainerType().get(StringUtils.defaultString(buildPalletResponse.getString("container.containerType"))));
        return buildPalletDetails;
    }

    private Map<String, String> getLocatePageDetails(String locate) {
        Map<String, String> locatebtnDetails = new HashMap<>();
        JsonPath locatePgResponse = new JsonPath(locate);
        locatebtnDetails.put("Pallet ID", StringUtils.defaultString(locatePgResponse.getString("palletId")));
        locatebtnDetails.put("PO", StringUtils.defaultString(locatePgResponse.getString("poNbr")));
        locatebtnDetails.put("POReceipt", StringUtils.defaultString(locatePgResponse.getString("receiptNbr")));
        locatebtnDetails.put("Process Area", StringUtils.defaultString(locatePgResponse.getString("processArea")));
        locatebtnDetails.put("Locate To", StringUtils.defaultString(locatePgResponse.getString("locateTo")));
        locatebtnDetails.put("Scan Location ID", StringUtils.defaultString(locatePgResponse.getString("scanLocationID")));
        return locatebtnDetails;
    }

    @When("user navigate to Homescreen")
    public void homeScreen_Navigation() {
        buildPalletPage.clickButton("back");
        buildPalletPage.clickButton("exit");
    }

    @When("user navigate to Menuscreen")
    public void menuScreen_navigation() {
        buildPalletPage.clickButton("exit");
    }

}


