package com.macys.mst.DC2.EndToEnd.stepdefinitions;


import com.macys.mst.DC2.EndToEnd.pageobjects.PODRRPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.BuildPalletPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.DC2SubMenu;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages.SplitAdjustPalletPage;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SplitAdjustPalletSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();

    private BuildPalletPage buildPalletPage = PageFactory.initElements(driver, BuildPalletPage.class);
    private SplitAdjustPalletPage splitAdjustPalletPage = PageFactory.initElements(driver, SplitAdjustPalletPage.class);
    private DC2SubMenu dc2SubMenu = PageFactory.initElements(driver, DC2SubMenu.class);
    private PODRRPage podrrPage = PageFactory.initElements(driver, PODRRPage.class);
    private StepsContext stepsContext;
    private CommonUtils commonUtils = new CommonUtils();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    RandomUtil randomUtil = new RandomUtil();

    public SplitAdjustPalletSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Then("user start building pallets and locate to $loc location")
    public void verifyBuildPalletDetails(String loc) throws Exception {
        dc2SubMenu.clickOnGivenSubMenu("Build_Pallet");
        String poreport_Id = (String) dataStorage.getStoredData().get("po_report_id");
        buildPalletPage.scanBuildPalletReportID(poreport_Id);
        log.info("Report ID: {}", poreport_Id);

        //Click on Scan Pallet Id textbox and enter PLT number
        String randomPalletID = randomUtil.getRandomValue("PLT-D-17");
        buildPalletPage.scanPalletNumber(randomPalletID);
        log.info("Pallet Number: {}", randomPalletID);
        buildPalletPage.isPalletEmptyYesBtn();

        //Enter Random Container Number in between 1-9
        String randomNumber = randomUtil.getRandomValue("D-1");
        buildPalletPage.selectNumOfContainers(randomNumber);
        buildPalletPage.clickButton("Enter");

        String locNbrPresentInScreen = buildPalletPage.getLocateIdOnScreen();
        log.info("Validated Location Number " + locNbrPresentInScreen);
        if (!locNbrPresentInScreen.equals("")) {
            buildPalletPage.scanLocationID(locNbrPresentInScreen);
        } else {
            buildPalletPage.scanLocationID(loc);
        }

    }

    @Then("user start building pallets and click on end Pallet Btn")
    public void clickEndPalletBtn() {
        String randomPalletID = randomUtil.getRandomValue("PLT-D-17");
        buildPalletPage.scanPalletNumber(randomPalletID);
        log.info("Pallet Number: {}", randomPalletID);

        buildPalletPage.isPalletEmptyYesBtn();

        //Enter Random Container Number in between 1-9
        String randomNumber = randomUtil.getRandomValue("D-1");
        buildPalletPage.selectNumOfContainers(randomNumber);
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

    @When("navigate to Split Adjust Pallet screen")
    public void Split_Adjust_Pallet_navigation() {
        dc2SubMenu.clickOnGivenSubMenu("Split_Adjust_pallet");
    }

    @Then("scan pallet and validate Split Adjust screen")
    public void split_AdjustScreen_Validation() {
        String PalletID = (String) dataStorage.getStoredData().get("PalletCreatedFromBuildPallet");
        splitAdjustPalletPage.scanPalletNumber(PalletID);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Existing Pallet Number for SPLIT created from BuildPallet : ", PalletID, true);
        Map<String, String> splitAdjustScreenUIData = splitAdjustPalletPage.getSplitAdjusscreenElements();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Split/Adjust Screen UIData : ", splitAdjustScreenUIData.toString(), true);
        String number_Of_Containers = "";
        for (Map.Entry entry : splitAdjustScreenUIData.entrySet()) {
            if (entry.getKey().equals("Num# of Containers")) {
                number_Of_Containers = entry.getValue().toString();
            }
        }
        dataStorage.getStoredData().put("Number of Containers present in the pallet", number_Of_Containers);
    }

    @Then("fetch the pallets from the created Inventory")
    public void palletDetails() {
        List<String> casesAndBinsList = (List<String>) dataStorage.getStoredData().get("listOfcasesAndBinboxes");
        List<String> createdpalletList = new ArrayList<>();
        for (String eachcaseAndBin : casesAndBinsList) {
            if (eachcaseAndBin.startsWith("PL")) {
                createdpalletList.add(eachcaseAndBin);
            }
            log.info("CreatedPalletList : {}", createdpalletList);
            dataStorage.getStoredData().put("createdpalletList", createdpalletList);
        }
    }

    @Then("scan pallet and validate Split pallet screen")
    public void split_Screen_Validation() {
        String PalletID = randomUtil.getRandomValue("PLT-D-17");
        splitAdjustPalletPage.scanToPalletNumber(PalletID);
        dataStorage.getStoredData().put("PalletCreatedForSplitPallet", PalletID);
        CommonUtils.doJbehavereportConsolelogAndAssertion("New Pallet Number for SPLIT : ", PalletID, true);
        splitAdjustPalletPage.selectPID("Split");
        String NumberofContainerForSplit = (String) dataStorage.getStoredData().get("Number of Containers present in the pallet");
        int containerstoEnterforSplit = Integer.valueOf(NumberofContainerForSplit) / 2;
        String NoOfContainers = String.valueOf(containerstoEnterforSplit);
        splitAdjustPalletPage.selectNumOfContainers(NoOfContainers);
    }

    @Then("locate the Pallet to $parm location and validate the status of the pallet")
    public void pallet_Validation_after_assigning_location(String loc) {
        String Processing_Stage_LocNbr = (String) dataStorage.getStoredData().get("ProcessingStageLocNbr");
        splitAdjustPalletPage.clickButton("Locate");
        String locNbrPresentInScreen = (buildPalletPage.getLocateIdOnScreen()).split(",")[0].trim();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Suggested location : ", locNbrPresentInScreen, true);
        if (!locNbrPresentInScreen.equals("")) {
            splitAdjustPalletPage.scanLocationID(locNbrPresentInScreen);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Entered location in split screen: ", locNbrPresentInScreen, true);
        } else {
            splitAdjustPalletPage.scanLocationID(Processing_Stage_LocNbr);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Entered location in split screen: ", Processing_Stage_LocNbr, true);
        }
    }

    @When("user click on Split button")
    public void Split_screen_navigation() {
        splitAdjustPalletPage.clickButton("Split");
    }

    @When("user click on Adjust button")
    public void adjust_screen_navigation() {
        splitAdjustPalletPage.clickButton("Adjust");
    }

    @Then("edit containers and validate Adjust pallet screen")
    public void adjust_Screen_Validation() {
        String NumberofContainerForAdjust = (String) dataStorage.getStoredData().get("Number of Containers present in the pallet");
        int containerstoEnterforAdjust = Integer.valueOf(NumberofContainerForAdjust);
        if(containerstoEnterforAdjust>1){
            containerstoEnterforAdjust = containerstoEnterforAdjust/2;
        }else{
            ++containerstoEnterforAdjust;
        }
        String NoOfContainers = String.valueOf(containerstoEnterforAdjust);
        splitAdjustPalletPage.selectNumOfContainers(NoOfContainers);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Number of containers entered in Adjust screen: ", NoOfContainers, true);
    }

    public void getPalletDetails() {
        String PalletForSplit = "";
        String PalletForAdjust = "";
        List<String> palletList = (List<String>) dataStorage.getStoredData().get("createdpalletList");
        if (palletList.size() == 1) {
            PalletForSplit = palletList.get(0);
            PalletForAdjust = palletList.get(0);
        } else if (palletList.size() > 1) {
            PalletForSplit = palletList.get(0);
            PalletForAdjust = palletList.get(1);
        } else {
            PalletForSplit = randomUtil.getRandomValue("PLT-D-17");
            PalletForAdjust = randomUtil.getRandomValue("PLT-D-17");
        }
        dataStorage.getStoredData().put("PalletForSplit", PalletForSplit);
        dataStorage.getStoredData().put("PalletForAdjust", PalletForAdjust);
    }
}


