package com.macys.mst.DC2.EndToEnd.stepdefinitions;


import com.macys.mst.DC2.EndToEnd.configuration.PostDeployEndPoint;
import com.macys.mst.DC2.EndToEnd.pageobjects.*;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PostDeployValidationSteps {

    String hostname = "https://msc.gcp.cloudrts.net";
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    RFMenuPage rfMenuPage = PageFactory.initElements(driver, RFMenuPage.class);
    public static boolean hasInnerPack = false;
    SCMUIPage scmUIPage = new SCMUIPage(driver);
    SeUiContextBase seUiContextBase = new SeUiContextBase();
    BasePage basePage = new BasePage(driver);
    private StepsContext stepsContext;
    public By filterResultTable = By.id("gridSection");
    private SCMUIPage scmuiPage = PageFactory.initElements(driver, SCMUIPage.class);
    private PODashboardPage poDashboardPage = PageFactory.initElements(driver, PODashboardPage.class);

    PODistroPage poDistroPage = PageFactory.initElements(driver, PODistroPage.class);
    PODRRPage drrPage = PageFactory.initElements(driver, PODRRPage.class);


    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("user logs into PROD HH UI")
    public void prodHHLogin() {
        try {
            rfMenuPage.prodlogin();
        } catch (Exception e) {
            log.info("Login Failed" + e.getMessage());
            StepDetail.addDetail("Login Failed" + e.getMessage(), false);
        }
    }

    @Given("user logs into PROD SCM UI")
    public void prodSCMLogin() {
        try {
            scmUIPage.loginProdSCM();
            seUiContextBase.waitFor(5);
        } catch (Exception e) {
            log.info("Login Failed" + e.getMessage());
            StepDetail.addDetail("Login Failed" + e.getMessage(), false);
        }
    }

    @Given("Validate DRR Page")
    public void vaidateDRRPage(@Named("PO_NBR") String poNbr) throws Exception {
        clickHomeNavOption("PO Inquiry");
        scmUIPage.clickClearButton();
        scmUIPage.typeIntoInputField("PO *", poNbr);
        scmUIPage.clickSearchButton();

        log.info("Started to validate Report ID based on Po");

        scmUIPage.clickReportID();

        scmUIPage.verifyBarcodes();
        StepDetail.addDetail("Barcode is verified", true);
        scmUIPage.verifyPrintOptions();
        StepDetail.addDetail("Print is verified", true);
    }

    @Then("Validate PO Inquiry")
    public void vaidatePoInquiry(@Named("PO_NBR") String poNbr) throws Exception {
        clickHomeNavOption("PO Inquiry");
        scmUIPage.clickClearButton();
        scmUIPage.typeIntoInputField("PO *", poNbr);
        scmUIPage.clickSearchButton();
        scmUIPage.verifyInquiryPage();
        StepDetail.addDetail("PO Inquiry is verified", true);
    }

    @Then("Validate PO Detail")
    public void vaidatePoDetail(@Named("PO_NBR") String poNbr) throws Exception {

        scmUIPage.clickPO(poNbr);
        scmUIPage.verifyPODetails();
        StepDetail.addDetail("PO Detail is verified", true);
    }

    @Then("Validate PO Distro")
    public void vaidatedistro() throws Exception {

        scmUIPage.navigatetoDistro();
        scmUIPage.verifydistroPage();

        StepDetail.addDetail("PO Distro is verified", true);
    }

    @Then("Validate PO Dashboard")
    public void validatePoDashboard(@Named("PO_NBR") String poNbr) throws Exception {

        log.info("Validate PO availability in Dashboard Screen");

        clickHomeNavOption("PO Dashboard");
        scmUIPage.searchDashboard(poNbr);
        scmUIPage.verifyPODashboard(poNbr);
        scmUIPage.releasePOInDashboard();
        scmUIPage.verifyReleasePage();


    }

    @Given("Validate API Response Status Code and Details for Service $serviceName")
    @Then("Validate API Response Status Code and Details for Service $serviceName")
    public void ValidateInventoryCallbyPOandRCPT(ExamplesTable serviceName, @Named("SKUNUM") String skuNum,
                                                 @Named("BARCODE") String barcode,
                                                 @Named("REPORT_ID") String reportID,
                                                 @Named("PO_NBR") String poNbr,
                                                 @Named("RCPT_NBR") String poRpctNbr,
                                                 @Named("CONTAINER") String container,
                                                 @Named("CARTON") String cartonNo) {
        List<Map<String, String>> rows = serviceName.getRows();

        String containerId = "";
        String reportIdFromJenkins = System.getProperty("reportId");

        if (StringUtils.isNotBlank(reportIdFromJenkins)) {
            reportID = reportIdFromJenkins;
            String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.RCPT_DETAIL_BY_REPORT, reportIdFromJenkins), true);
            ApiResponse apiResponse = new ApiResponse(response);
            if (null != apiResponse) {
                poNbr = apiResponse.getString("poDetail.poDetailHdr.poNbr");
                poRpctNbr = apiResponse.getString("poDetail.poDetailHdr.rcptNbr");
                skuNum = apiResponse.getString("poDetail.poLineDetails[0].inhouseUpc");
                barcode = apiResponse.getString("poDetail.poLineDetails[0].polinebrcd");
            }
        }
        for (Map<String, String> service : rows) {
            switch (service.get("serviceName")) {
                case "INVENTORY_PO_RCPT":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.inventoryByPORPCTPath, poNbr, poRpctNbr), true);

                      String jsonPath = "container[0].attributeList.findAll {key = 'PO'}.values[0][0]";

                        if (StringUtils.isNotBlank(response)) {
                            String poNbrActual = getJSONValueByPath(response, jsonPath).replace("[","" ).replace("]","");
                            if (StringUtils.isNotBlank(reportIdFromJenkins)) {
                                containerId = getJSONValueByPath(response, "container[0].barCode");
                            }
                            compareValues(poNbr, poNbrActual, "Inventory Details by PO and PORcpt");
                        } else {
                            log.info("Inventory Details by PO and PORcpt Service Failed with 204 Status");
                            StepDetail.addDetail("Inventory Details by PO and PORcpt Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Inventory Details by PO and PORcpt Service Failed with exception " + e.getMessage());
                        StepDetail.addDetail("Inventory Details by PO and PORcpt Service Failed with exception" + e.getMessage(), false);
                    }
                    break;
                case "POLINE_BARCODE":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.PO4walls_POlineBarcode, barcode), true);
                        String jsonPath = "poLineBarCode";

                        if (StringUtils.isNotBlank(response)) {
                            String barcodeActual = getJSONValueByPath(response, jsonPath);
                            compareValues(barcode, barcodeActual, "POLine Barcode");
                        } else {
                            log.info("POLine Barcode Service Failed with 204 Status");
                            StepDetail.addDetail("POLine Barcode Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("POLine Barcode Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("POLine Barcode Service Failed with Exception " + e.getMessage(), false);
                    }

                    break;

                case "POLINE_DISTRO_INFO":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.OrderMngt_distro, poNbr), true);
                        String jsonPath = "[0].poNbr";

                        if (StringUtils.isNotBlank(response)) {
                            String poNbrActual = getJSONValueByPath(response, jsonPath);
                            compareValues(poNbr, poNbrActual, "POLine Distro");
                        } else {
                            log.info("POLine Distro Service Failed with 204 Status");
                            StepDetail.addDetail("POLine Distro Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("POLine Distro Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("POLine Distro Service Failed with Exception " + e.getMessage(), false);
                    }

                    break;
                case "DEPT_PID_BY_SKU":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.OrderMngt_skudeptNbr, skuNum), true);
                        String jsonPath = "deptNbr";

                        if (StringUtils.isNotBlank(response)) {
                            String deptNumAcutal = getJSONValueByPath(response, jsonPath);
                            compareValues(deptNumAcutal, deptNumAcutal, "Get DEPT by SKU");
                        } else {
                            log.info("Get DEPT by SKU Service Failed with 204 Status");
                            StepDetail.addDetail("Get DEPT by SKU Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Get DEPT by SKU Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Get DEPT by SKU Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "PO_DISTRIBUTION_INFO":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.PO4walls_distro_inventory, poNbr, poRpctNbr), true);
                        String jsonPath = "poLineItemList[0].poLocationDistroList[0].poNbr";

                        if (StringUtils.isNotBlank(response)) {
                            String deptNumAcutal = getJSONValueByPath(response, jsonPath);
                            compareValues(deptNumAcutal, deptNumAcutal, "Get DEPT by SKU");
                        } else {
                            log.info("Get PO Distribution Info Service Failed with 204 Status");
                            StepDetail.addDetail("Get PO Distribution Info Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Get PO Distribution Info Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Get PO Distribution Info Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "GET_WSM_ACTIVITY":
                    try {
                        String acivityType = "RELEASE";
                        String response = CommonUtils.getRequestResponse(PostDeployEndPoint.wsmReleaseLaneAcivityPath, true);
                        String jsonPath = "[0].type";

                        if (StringUtils.isNotBlank(response)) {
                            String acivityTypeAcutal = getJSONValueByPath(response, jsonPath);
                            compareValues(acivityType, acivityTypeAcutal, "Get WSM Activity");
                        } else {
                            log.info("Get WSM Activity Service Failed with 204 Status");
                            StepDetail.addDetail("Get WSM Activity Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Get WSM Activity Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Get WSM Activity Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "SINGLE_SKU_INFO":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.singleSKUDetailsPath, reportID, poNbr, skuNum), true);
                        String jsonPath = "[0].poNbr";

                        if (StringUtils.isNotBlank(response)) {
                            String poNbrActual = getJSONValueByPath(response, jsonPath);
                            compareValues(poNbr, poNbrActual, "Get Single SKU info");
                        } else {
                            log.info("Get Single SKU info Service Failed with 204 Status");
                            StepDetail.addDetail("Get Single SKU info Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Get Single SKU info Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Get Single SKU info Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "REPORTING":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.PO4walls_reporting, poNbr), true);
                        String jsonPath = "poDashboard[0].poNbr";

                        if (StringUtils.isNotBlank(response)) {
                            String poNbrActual = getJSONValueByPath(response, jsonPath);
                            compareValues(poNbr, poNbrActual, "Reporting");
                        } else {
                            log.info("Reporting Service Failed with 204 Status");
                            StepDetail.addDetail("Reporting Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Reporting Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Reporting Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "INVENTORY_SNAPSHOT_DETAIL":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.inventorySnapshotDetailsPath, skuNum), true);
                        String jsonPath = "[0].item";

                        if (StringUtils.isNotBlank(response)) {
                            String skuNumActual = getJSONValueByPath(response, jsonPath);
                            compareValues(skuNum, skuNumActual, "Inventory Snapshot Details");
                        } else {
                            log.info("Inventory Snapshot Details Service Failed with 204 Status");
                            StepDetail.addDetail("Inventory Snapshot Details Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Inventory Snapshot Details Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Inventory Snapshot Details Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "INVENTORY_RELATIONSHIP_DETAIL":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.inventoryRelationDetailsPath, skuNum), true);
                        String jsonPath = "[0].containerRelationshipList[0].locnNbr";

                        if (StringUtils.isNotBlank(response)) {
                            String containerTypeActual = getJSONValueByPath(response, jsonPath);
                            compareValues("7221", containerTypeActual, "INVENTORY_SNAPSHOT_DETAIL");
                        } else {
                            log.info("Inventory Relationship Details Service Failed with 204 Status");
                            StepDetail.addDetail("Inventory Relationship Details Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Inventory Relationship Details Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Inventory Relationship Details Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "SHIPINFO":
                    try {
                        String response = CommonUtils.getRequestResponse(PostDeployEndPoint.shipInfoPath, true);
                        String jsonPath = "MessageResponseDTO.messages[0].destinationId";

                        if (StringUtils.isNotBlank(response)) {
                            String messageTypeActual = getJSONValueByPath(response, jsonPath);
                            compareValues("SHIPINFO", messageTypeActual, "Inventory Relationship Details");
                        } else {
                            log.info("ShipInfo Details Service Failed with 204 Status");
                            StepDetail.addDetail("ShipInfo Details Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Inventory Relationship Details Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Inventory Relationship Details Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "CONTAINER_DETAILS":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.containerPath, containerId != "" ? containerId : container), true);
                        String jsonPath = "container.barCode";

                        if (StringUtils.isNotBlank(response)) {
                            String containerActual = getJSONValueByPath(response, jsonPath);
                            compareValues(container, containerActual, "Container Details");
                        } else {
                            log.info("Container Details Service Failed with 204 Status");
                            StepDetail.addDetail("Container Details Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Container Details Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Container Details Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;

                case "PACKAGE":
                    try {
                        String response = CommonUtils.getRequestResponse(String.format(PostDeployEndPoint.cartonPath, cartonNo), true);
                        String jsonPath = "barcode";

                        if (StringUtils.isNotBlank(response)) {
                            String cartonActual = getJSONValueByPath(response, jsonPath).replace("[","" ).replace("]","");
                            compareValues(cartonNo, cartonActual, "Carton Details");
                        } else {
                            log.info("Package Service Failed with 204 Status");
                            StepDetail.addDetail("Package Service Failed with 204 Status", false);
                        }
                    } catch (Exception e) {
                        log.info("Package Service Failed with Exception" + e.getMessage());
                        StepDetail.addDetail("Package Service Failed with Exception " + e.getMessage(), false);
                    }
                    break;
            }
        }

    }

    @When("user selects $projectOption")
    public void selectProjectOption(String projectOption){

        rfMenuPage.clickNavOption(projectOption);
    }

    @When("user hits $buttonName button")
    public void selectExitButton(String buttonName) {
        if ("EXIT".equalsIgnoreCase(buttonName))
            rfMenuPage.clickExitButton();
        else if ("BACK".equalsIgnoreCase(buttonName))
            rfMenuPage.clickBackButton();
    }


    @Given("Validate HH Menu Pages$table")
    @Then("Validate HH Menu Pages$table")
    public void getPOLineDetails(ExamplesTable table) throws Exception {
        menuloop:
        for (Iterator<Map<String, String>> iterator = table.getRows().iterator(); iterator.hasNext(); ) {
            Map<String, String> exRows = iterator.next();
            boolean isLabelDisplayed;

            String menuOption = exRows.get("MenuOption");
            String labelText = exRows.get("Label");
            String scanText = exRows.get("ScanText");
            String popUpText = exRows.get("PopupText");

            rfMenuPage.clickNavOption(menuOption);

            if (menuOption.contains("Create Tote")) {
                selectProcessArea("OSC");
                rfMenuPage.selectSingleOrInnerOption(hasInnerPack);
            } else if (menuOption.contains("Putaway Pallet")) {
                rfMenuPage.selectPallet();
            } else if  (menuOption.contains("ICQA")){
                rfMenuPage.clickNavOption("Create Bin");
            }
            if (menuOption.contains("Cycle Count"))
                isLabelDisplayed = rfMenuPage.isElementDisplayed(By.xpath("//label[contains(text(),'" + labelText + "')]"));
            else {
                isLabelDisplayed = rfMenuPage.isElementDisplayed(By.xpath("//*[contains(text(),'" + labelText + "')]"));
            }
            if (isLabelDisplayed) {
                log.info("Label: " + labelText + "displayed as expected");
                StepDetail.addDetail("Label: " + labelText + "displayed as expected", true);
            } else {
                log.info("Label: " + labelText + "is not displayed as expected");
                StepDetail.addDetail("Label: " + labelText + "is not displayed as expected", false);
            }

            if (menuOption.contains("Create Pack")) {
                rfMenuPage.clickNavOption("Inner Pack");
                rfMenuPage.scanRandomText(scanText);
            } else if (menuOption.contains("Manual ASN")) {
                rfMenuPage.validateManualASN();
                rfMenuPage.clickExitButton();
                break menuloop;
            }
            rfMenuPage.scanBarcode(scanText);
            TimeUnit.SECONDS.sleep(4);
            rfMenuPage.validatePopupMessageAndClose(popUpText);
            TimeUnit.SECONDS.sleep(2);
            if (menuOption.contains("Pick To Carton")) {
                rfMenuPage.clickEXITButton();
                rfMenuPage.clickBackButton();
            } else if (menuOption.contains("Cycle Count")) {
                rfMenuPage.clickBackButton();
            } else if (menuOption.contains("Create Pack")) {
                rfMenuPage.clickBackButton();
                rfMenuPage.clickBackButton();
            } else if (menuOption.contains("PreSort") || menuOption.contains("Case Induction")) {
                rfMenuPage.clickpresortBackButton();
            }else if (menuOption.contains("ICQA")) {
                rfMenuPage.clickBackButton();
                rfMenuPage.clickBackButton();
            }else if (menuOption.contains("Dock Scan")) {
                rfMenuPage.clickExitButton();
                rfMenuPage.clickBackButton();
            }else {
                rfMenuPage.clickExitButton();
            }

            TimeUnit.SECONDS.sleep(2);
            log.info("Validated " + menuOption + " Page");
            StepDetail.addDetail("Validated " + menuOption + " Page", true);
        }
    }

    private void selectProcessArea(String processArea) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        rfMenuPage.clickOnSelectProcessArea();
        TimeUnit.SECONDS.sleep(2);
        try {
            rfMenuPage.selectProcessArea(processArea);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }

    public String getJSONValueByPath(String response, String jsonPath) {
        try {
            JsonPath responseJSON = new JsonPath(response);
            String value = responseJSON.get(jsonPath).toString();
            return value;
        } catch (Exception e) {
            log.error("Error evaluating JsonPath", e);
            return "";
        }

    }

    public void compareValues(String actualValue, String expectedValue, String apiName) {
        if (StringUtils.isNotBlank(actualValue) && StringUtils.equals(actualValue, expectedValue)) {
            log.info("Validation Pass for " + apiName + " " + expectedValue);
            StepDetail.addDetail("Validation Pass for " + apiName + " " + expectedValue, true);
        } else {
            log.info("Validation Fail for " + apiName + " " + expectedValue);
            StepDetail.addDetail("Validation Fail for " + apiName + " " + expectedValue, false);
        }
    }

    public void clickHomeNavOption(String option) throws InterruptedException {
        Actions action = new Actions(driver);
        action.sendKeys(Keys.ESCAPE).build().perform();
        Assert.assertTrue(basePage.isNavMenuDisplayed());
        TimeUnit.SECONDS.sleep(20);
        scmuiPage.clickNavOption(option);
    }


}
