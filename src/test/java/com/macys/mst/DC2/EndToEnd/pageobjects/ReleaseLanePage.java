package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.mst.DC2.EndToEnd.configuration.InventoryEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.ReleaseLaneConfig;
import com.macys.mst.DC2.EndToEnd.configuration.WsmEndpoint;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLQueriesRL;
import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.selenium.PageObject;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import io.restassured.RestAssured;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Slf4j
public class ReleaseLanePage extends PageObject {

    public ReleaseLanePage(WebDriver driver) {
        super(driver);
    }

    public String currentLane = "NoLane";
    public String nextOpenLane = "NoLane";

    public String currentPO = "NoPO";
    public String nextPO = "NoPO";

    private String activityID = "0000";
    private String poNumber = "0000";
    private String poRcptNumber = "0000";

    private static ObjectMapper mapper = new ObjectMapper();

    @FindBy(xpath = "//*[contains(text(),'Scan Lane ID:')]")
    private WebElement scanLaneLabel;

    @FindBy(xpath = "//*[contains(text(),'Proceed to Lane :')]")
    private WebElement nextLaneLabel;

    @FindBy(xpath = "//*[*[contains(text(),'Proceed to Lane : ')]]/*[2]")
    private WebElement nextLaneValue;

    @FindBy(xpath = "//input[@type='text']")
    private WebElement scanLaneText;

    @FindBy(xpath = "//button[span[contains(text(),'Back')]]")
    private WebElement backButton;

    @FindBy(xpath = "//button[span[contains(text(),'Exit')]]")
    private WebElement exitButton;

    @FindBy(xpath = "//*[contains(text(),'Release Lane')]")
    private WebElement releaseLanepageHeader;

    @FindBy(xpath = "//*[contains(text(),'Alert')]")
    private WebElement alertHeader;

    @FindBy(xpath = "//*[*[contains(text(),'Alert')]]/*[2]")
    private WebElement alertMessageBox;

    @FindBy(xpath = "//*[contains(text(),'OK')]")
    private WebElement alertAcceptButton;


    @FindBy(xpath = "//*[contains(text(),'Release Lane')]")
    WebElement releaseLane;


    @FindBy(xpath = "//*[contains(text(),'Actual Totes: ')]")
    private WebElement actualToteQtyLabel;

    @FindBy(xpath = "//input[@type='number']")
    private WebElement actualToteQtyValue;


    @FindBy(xpath = "//*[contains(text(),'Release Lane')]")
    private WebElement releaseLanePageHeader;


    public void buttonClick() {
        getWait(10).until(ExpectedConditions.visibilityOf(releaseLane));
        releaseLane.click();
    }

    public void whenUserScansLocationBarcode(String locationBarcode) throws InterruptedException {
        //this.currentLane = getEarliestOpenRLActivity();
        this.currentPO = getEarliestOpenPO();

        validateScanLanePage();
        scanLaneandEnter(locationBarcode);
        setCurrentLane(locationBarcode);
        log.info("Scanning Given Lane: " + locationBarcode);
    }

    public String getEarliestOpenRLActivity() {
        try {
            String activityRequestResponse = RestAssured.given().get(String.format(ReleaseLaneConfig.GET_RL_ACTIVITY_STATUS_SORTED_COUNT, ReleaseLaneConfig.LOCN_NBR, "OPEN", "1", "asc")).body().asString();
            JSONArray activityGetArray = new JSONArray(activityRequestResponse);
            JSONObject tempJSONObject = activityGetArray.getJSONObject(0);
            return tempJSONObject.get("containerId").toString();

        } catch (Exception e) {
            return "NoLane";
        }
    }

    public static String getEarliestOpenPO() {
        try {
            String activityRequestResponse = RestAssured.given().get(String.format(ReleaseLaneConfig.GET_RL_ACTIVITY_STATUS_SORTED_COUNT, ReleaseLaneConfig.LOCN_NBR, "OPEN", "1", "asc")).body().asString();
            JSONArray activityGetArray = new JSONArray(activityRequestResponse);
            JSONObject tempJSONObject = activityGetArray.getJSONObject(0).getJSONObject("attributes");
            return tempJSONObject.get("poNbr").toString();
        } catch (Exception e) {
            return "NoPO";
        }
    }


    public void scanLaneandEnter(String laneID) throws InterruptedException {
        if ("empty".equals(laneID)) {
            scanLaneText.sendKeys(Keys.ENTER);
            log.info("Empty LaneID entered");
        } else {
            scanLaneText.sendKeys(laneID);
            TimeUnit.SECONDS.sleep(3);
            scanLaneText.sendKeys(Keys.ENTER);
            log.info("LaneID scanned {}", laneID);
        }
    }

    public void whenUserValidatesNextLocationBarcode(String locationBarcode) {
        if (locationBarcode.equals("firstOpen")) {
            validateNextLanePage(this.currentLane);
        } else {
            validateNextLanePage(locationBarcode);
        }
    }

    public void validateNextLanePage(String nextLane) {
        log.info("Validating the Next Lane page");
        getWait(2).until(ExpectedConditions.visibilityOf(backButton));
        getWait(2).until(ExpectedConditions.visibilityOf(exitButton));
        getWait(2).until(ExpectedConditions.visibilityOf(nextLaneLabel));
        getWait(2).until(ExpectedConditions.visibilityOf(nextLaneValue));
        getWait(2).until(ExpectedConditions.visibilityOf(scanLaneLabel));
        getWait(2).until(ExpectedConditions.visibilityOf(scanLaneText));
        getWait(2).until(ExpectedConditions.visibilityOf(releaseLanePageHeader));

        if (backButton.isDisplayed() && exitButton.isDisplayed()) {
            Assert.assertEquals(releaseLanePageHeader.getText(), "Release Lane");
            Assert.assertEquals(scanLaneLabel.getText(), "Scan Lane ID:");
            Assert.assertEquals(nextLaneValue.getText(), nextLane);
            Assert.assertTrue(scanLaneText.isDisplayed());
        } else {
            log.info("BackButton and ExitButton not visible on ReleaseLane --> ScanLane Page");
            Assert.assertFalse(true);
        }
        log.info("Validated Scan Lane page");
    }

    public void validateScanLanePage() {
        log.info("Validating the Scan Lane page");
        getWait(2).until(ExpectedConditions.visibilityOf(backButton));
        getWait(2).until(ExpectedConditions.visibilityOf(exitButton));
        getWait(2).until(ExpectedConditions.visibilityOf(scanLaneLabel));
        getWait(2).until(ExpectedConditions.visibilityOf(scanLaneText));
        getWait(2).until(ExpectedConditions.visibilityOf(releaseLanePageHeader));
        if (backButton.isDisplayed() && exitButton.isDisplayed()) {
            Assert.assertEquals(releaseLanePageHeader.getText(), "Release Lane");
            Assert.assertEquals(scanLaneLabel.getText(), "Scan Lane ID:");
            Assert.assertTrue(scanLaneText.isDisplayed());
        } else {
            log.info("BackButton and ExitButton not visible on ReleaseLane --> ScanLane Page");
            Assert.assertFalse(true);
        }
        log.info("Validated Scan Lane page");
    }

    public void thenUserCompletesAllActivityforPO(String toteCount) {
        try {
            log.info("FirstLane :{}", this.currentLane);
            scanLaneandEnter(this.currentLane);

            this.nextOpenLane = getEarliestOpenRLActivity();
            this.nextPO = getEarliestOpenPO();
           // validatePOActivityAssignedtoUser(this.currentPO, "ASSIGNED", ReleaseLaneConfig.CURRENT_USER, ReleaseLaneConfig.LOCN_NBR);

            validateActivityPage(currentLane, true);
            editToteQtyandSubmit(toteCount);

            Assert.assertEquals("Activity: " + getActivityID() + " not Completed in WSM", true, validateWSMActivtyAfterUpdate());
            log.info("Activity: " + getActivityID() + " Completed in WSM");
            Assert.assertEquals("Tote for: " + getPoRcptNumber() + " not moved in Inventory", true, validateInventoryAfterUpdate());
            log.info("Tote for: " + getPoRcptNumber() + "moved in Inventory");
            this.currentLane = getNextLanebyPO(getPoNumber());
            if (!this.currentLane.equals("NoLane")) {
                setCurrentLane(this.currentLane);
                validateActivityPageAlerts("nextlane");
            } else if (this.currentLane.equals("NoLane") && (!this.nextOpenLane.equals("NoLane"))) {
                this.currentLane = this.nextOpenLane;
                this.nextOpenLane = "NoLane";

                setCurrentLane(this.currentLane);
                validateActivityPageAlerts("nextPO");

                this.currentPO = this.nextPO;
                validatePOActivityAssignedtoUser(this.currentPO, "ASSIGNED", ReleaseLaneConfig.CURRENT_USER, ReleaseLaneConfig.LOCN_NBR);
                this.nextPO = getEarliestOpenPO();
                log.info("Next available Open Lane: " + this.nextOpenLane);
            } else if (this.currentLane.equals("NoLane") && (this.nextOpenLane.equals("NoLane"))) {
                validateActivityPageAlerts("poComplete");
                log.info("Next available Open Lane: " + this.nextOpenLane);
            } else {
                validateActivityPageAlerts("generic");
            }
            while (!this.currentLane.equals("NoLane")) {
                scanLaneandEnter(this.currentLane);
                log.info("User Scanned Lane: " + this.currentLane);
                validateActivityPage(this.nextOpenLane, true);
                this.nextOpenLane = getEarliestOpenRLActivity();
                this.nextPO = getEarliestOpenPO();
                editToteQtyandSubmit(toteCount);
                Assert.assertEquals("Activity: " + getActivityID() + "not Completed in WSM", true, validateWSMActivtyAfterUpdate());
                log.info("Tote for PoRcptNumber: " + getPoRcptNumber() + ": moved in Inventory");
                Assert.assertEquals("Tote for: " + getPoRcptNumber() + "not moved in Inventory", true, validateInventoryAfterUpdate());
                log.info("Validated Activity Completion for Lane: " + this.currentLane);
                this.currentLane = getNextLanebyPO(getPoNumber());

                if (!this.currentLane.equals("NoLane")) {
                    setCurrentLane(this.currentLane);
                    validateActivityPageAlerts("nextlane");
                } else if (this.currentLane.equals("NoLane") && (!this.nextOpenLane.equals("NoLane"))) {
                    this.currentLane = this.nextOpenLane;
                    this.nextOpenLane = "NoLane";
                    setCurrentLane(this.currentLane);
                    validateActivityPageAlerts("nextPO");
                    this.currentPO = this.nextPO;
                    validatePOActivityAssignedtoUser(this.currentPO, "ASSIGNED", ReleaseLaneConfig.CURRENT_USER, ReleaseLaneConfig.LOCN_NBR);
                    this.nextPO = getEarliestOpenPO();
                } else if (this.currentLane.equals("NoLane") && (this.nextOpenLane.equals("NoLane"))) {
                    validateActivityPageAlerts("poComplete");
                    log.info("Next available Open Lane: " + this.nextOpenLane);
                } else {
                    validateActivityPageAlerts("generic");
                    log.info("Unknown Alert Accepted");
                }
            }
            //Thread.sleep(5000); // Hard wait
            TimeUnit.SECONDS.sleep(5);
            validateActivityPageAlerts("noTasks");
        } catch (InterruptedException e) {
            log.info(e.toString());
        }
    }

    private static boolean validatePOActivityAssignedtoUser(String poNbr, String expectedStatus, String expectedUser, String locationNbr) {
        log.info("expectedStatus : {} expectedUser : {}", expectedStatus, expectedUser);
        String query = String.format(SQLQueriesRL.GET_RL_ACTIVITY_BY_PORCPT_SQL, ReleaseLaneConfig.LOCN_NBR, poNbr);
        ArrayList<String> missedActivityList = new ArrayList<String>();
        try {
            List<Map<Object, Object>> resultSet = DBMethods.getValuesFromDBAsStringList(query, ReleaseLaneConfig.WSM_SCHEMA);
            for (Map<Object, Object> tempMap : resultSet) {
                if (!expectedStatus.equalsIgnoreCase(tempMap.get("STATUS_CD").toString())) {
                    //missedActivityList.add(tempMap.get("ID").toString());
                }
            }
            for (Map<Object, Object> tempMap : resultSet) {
                if (!expectedUser.equalsIgnoreCase((tempMap.get("ASSIGNED_TO").toString()))) {
                    missedActivityList.add(tempMap.get("ID").toString());
                }
            }
            Assert.assertEquals("Activity not assigned to current user for Lanes " + missedActivityList, 0, missedActivityList.size());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @SuppressWarnings("unchecked")
    public void validateActivityPage(String locationBarCode, boolean validateActualQty) {

        //Assert.assertEquals("Activity Page Title is invalid", activityPageTitle , RecvUtils.getDriver().getTitle());
        getWait(2).until(ExpectedConditions.visibilityOf(actualToteQtyLabel));
        getWait(2).until(ExpectedConditions.visibilityOf(backButton));
        getWait(2).until(ExpectedConditions.visibilityOf(exitButton));

        if (backButton.isDisplayed() && exitButton.isDisplayed()) {
            Map<String, String> activityWEBelements = getScreenElementsMap(driver, "//div");
            Assert.assertEquals("User redirected to incorrectLane", locationBarCode, activityWEBelements.get("Lane ID"));
            log.info("User redirected Correct Lane: {}", activityWEBelements.get("Lane ID"));
            Map<String, Object> jsonRespElments = getMapFromJsonArray(RestUtilities.getRequestResponse(String.format(ReleaseLaneConfig.GET_RL_ACTIVITY_ID, ReleaseLaneConfig.LOCN_NBR, activityWEBelements.get("Activity ID"))));
            HashMap<String, String> activityJSONelements = new HashMap<String, String>();
            activityJSONelements.put("Activity ID", jsonRespElments.get("id").toString());
            activityJSONelements.put("Expected Totes", jsonRespElments.get("qty").toString());
            activityJSONelements.put("Lane ID", jsonRespElments.get("containerId").toString());
            activityJSONelements.put("PO Number", ((HashMap<String, String>) jsonRespElments.get("attributes")).get("poNbr"));
            activityJSONelements.put("PO Receipt Number", ((HashMap<String, String>) jsonRespElments.get("attributes")).get("poRcptNbr"));
            CommonUtils.compareValues(activityWEBelements, activityJSONelements);
            Assert.assertTrue(actualToteQtyValue.isDisplayed() && actualToteQtyValue.isEnabled());
            if (validateActualQty) {
                Assert.assertTrue(actualToteQtyValue.getAttribute("value").toString().equals(activityJSONelements.get("Expected Totes")));
            }
            this.activityID = activityWEBelements.get("Activity ID");
            this.currentLane = activityWEBelements.get("Lane ID");
            this.poNumber = activityWEBelements.get("PO Number");
            this.poRcptNumber = activityWEBelements.get("PO Receipt Number");
        } else {
            log.info("BackButton and ExitButton not visible on ReleaseLane --> Activity Page");
            Assert.assertFalse(true);
        }
    }

    public static Map<String, String> getScreenElementsMap(WebDriver aDriver, String xpath) {
        Map<String, String> screenMap = new HashMap<String, String>();
        List<WebElement> allElements = aDriver.findElements(By.xpath(xpath));
        String val = null;
        String key = null;
        int size = allElements.size();
        for (int i = 0; i < size; i++) {
            try {
                val = allElements.get(i).findElement(By.tagName("label")).getText();
                key = allElements.get(i).findElement(By.tagName("strong")).getText();
            } catch (NoSuchElementException e) {
                val = null;
                key = null;
            }
            if ((key != null) && (val != null)) {
                if (key.contains(":")) {
                    key = StringUtils.chop(allElements.get(i).findElement(By.tagName("strong")).getText());
                }
                val = allElements.get(i).findElement(By.tagName("label")).getText();
                screenMap.put(key, val);
            }
        }
        return screenMap;
    }

    public static HashMap<String, Object> getMapFromJsonArray(String response) {
        HashMap<String, Object> responseMap = new HashMap<String, Object>();
        try {
            if (response != JSONObject.NULL) {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObj = (JSONObject) jsonArray.get(0);
                responseMap = mapper.readValue(jsonObj.toString(), new TypeReference<Map<String, Object>>() {
                });
            } else {
                Assert.fail("response String is null");
            }
        } catch (IOException e) {
            Assert.fail("Exception while converting json String to Map");
        }
        return responseMap;
    }


    public void editToteQtyandSubmit(String toteQuantity) {
        actualToteQtyValue.clear();
        if (actualToteQtyValue.equals("blank")) {
            actualToteQtyValue.sendKeys(Keys.ENTER);
        } else {
            actualToteQtyValue.sendKeys(toteQuantity);
            actualToteQtyValue.sendKeys(Keys.ENTER);
        }
    }

    public String getNextLanebyPO(String PONumber) {
        String nextLane = "NoLane";
        NavigableSet<String> returnLaneSet = new TreeSet<String>();
        HashMap<String, Object> responseMap = new HashMap<String, Object>();
        String activityUrl = String.format(ReleaseLaneConfig.CREATE_MODIFY_ACTIVITY, ReleaseLaneConfig.LOCN_NBR) + "?containerType=LANE&poNbr=" + PONumber + "&status=ASSIGNED";
        log.info("Activity url: {}", activityUrl);
        String activityResponseForPO = RestAssured.given().get(activityUrl).body().asString();
        log.info("ActivityResponseForPO: {}", activityResponseForPO);

        try {
            if (org.apache.commons.lang.StringUtils.isNotBlank(activityResponseForPO)) {
                JSONArray jsonArr = new JSONArray(activityResponseForPO);
                for (int i = 0; i < jsonArr.length(); i++) {
                    ObjectMapper mapper = new ObjectMapper();
                    responseMap = mapper.readValue(jsonArr.get(i).toString(), new TypeReference<Map<String, Object>>() {
                    });
                    returnLaneSet.add(responseMap.get("containerId").toString());
                }
            } else {
                log.info("No Existing activities");
            }
        } catch (Exception e) {
            log.error("Error getting PO Activities >> ", e);
        }
        if (returnLaneSet.size() == 1) {
            nextLane = returnLaneSet.first();
        } else if (returnLaneSet.size() > 1) {
            nextLane = returnLaneSet.higher(currentLane);
            if (nextLane == null) nextLane = returnLaneSet.first();
        } else {
            nextLane = "NoLane";
        }
        return nextLane;
    }

    public boolean validateWSMActivtyAfterUpdate() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        String activityEndpoint = String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH_By_ID,this.activityID);
        //String query = String.format(SQLQueriesRL.GET_RL_ACTIVITYSTATUS_BY_ID_SQL, ReleaseLaneConfig.LOCN_NBR, this.activityID);
        String activityGetResponse = CommonUtil.getRequestResponse(activityEndpoint);
        if (!org.apache.commons.lang.StringUtils.isBlank(activityGetResponse)) {
            JSONArray activityArray = new JSONArray(activityGetResponse);
            String activityStatus = activityArray.getJSONObject(0).getString("status");
            Assert.assertEquals("Activity not Completed", "COMPLETED", activityStatus);
            log.info("Activity " + this.activityID + " moved to Completion");
            return activityStatus.equals("COMPLETED");
        }
        else{
            return false;
        }
    }

    public boolean validateInventoryAfterUpdate() {
        //Currently creating and validating movement of one Tote only. Not validating 'RLS' status due to last minute contract change.  Need to update this logic.
        String inventoryResponse = CommonUtils.getRequestResponse(String.format(InventoryEndPoint.GET_INVENTORY_PORECEIPT_PONBR, this.poNumber, this.poRcptNumber));
        InventoryContainer[] inventoryContainer = CommonUtils.getClientResponse(inventoryResponse, new TypeReference<InventoryContainer[]>() {
        });
        List<InventoryContainer> InventoryContainers = Arrays.asList(inventoryContainer);
        InventoryContainers.forEach(container -> {
            Assert.assertEquals("Tote for receipt No: " + this.poRcptNumber + " not moved to Conveyor", "EOC123T", container.getContainer().getContainerRelationshipList().get(0).getParentContainer());
        });

        return true;

    }

    public void validateActivityPageAlerts(String param) {
        log.info("ValidateActivityPageAlerts param: {}", param);
        getWait(2).until(ExpectedConditions.visibilityOf(alertHeader));
        getWait(2).until(ExpectedConditions.visibilityOf(alertMessageBox));
        getWait(2).until(ExpectedConditions.visibilityOf(alertAcceptButton));

        switch (param) {
            case "invalidcount":
                Assert.assertEquals("Alert title is invalid", "Alert", alertHeader.getText().toString());
                Assert.assertEquals("Alert message is invalid", "Please enter valid totes", alertMessageBox.getText().toString());
                alertAcceptButton.click();
                log.info("Invalid totecount alert for ActivityPage validated and accepted");
                break;

            case "nextlane":
                Assert.assertEquals("Alert title invalid", "Alert", alertHeader.getText().toString());
                Assert.assertEquals("Alert message invalid", "PO is completely released. Please proceed with next available lane", alertMessageBox.getText().toString());
                alertAcceptButton.click();
                log.info("Next Lane alert on ActivityPage validated and accepted");
                break;

            case "cancelTask":
                Assert.assertEquals("Alert title invalid", "Alert", alertHeader.getText().toString());
                //Assert.assertEquals("Alert message invalid","Activity ID "+activityID+" is Reassigned to Open Status", alertMessageBox.getText().toString());
                alertAcceptButton.click();
                log.info("Next Lane alert on ActivityPage validated and accepted");
                break;

            case "poComplete":
                //PO is completely released. Please proceed with next available lane
                Assert.assertEquals("Alert title invalid", "Alert", alertHeader.getText().toString());
                Assert.assertEquals("Alert message invalid", "PO is completely released. Please proceed with next available lane", alertMessageBox.getText().toString());
                alertAcceptButton.click();
                log.info("PO Complete alert on ActivityPage validated and accepted");
                break;

            case "generic":
                Assert.assertEquals("Alert title invalid", "Alert", alertHeader.getText().toString());
                //Assert.assertEquals("Alert message invalid","PO is completely released. Please proceed with next available lane", alertMessageBox.getText().toString());
                alertAcceptButton.click();
                log.info("Unknown Alert accepted");
                break;

            case "nextPO":
                Assert.assertEquals("Alert title invalid", "Alert", alertHeader.getText().toString());
                Assert.assertEquals("Alert message invalid", "PO is completely released. Please proceed with next available lane " + currentLane, alertMessageBox.getText().toString());
                alertAcceptButton.click();
                log.info("Next PO alert on ActivityPage validated and accepted");
                break;

            case "noTasks":
                Assert.assertEquals("Alert title invalid", "Alert", alertHeader.getText().toString());
                Assert.assertEquals("Alert message invalid", "No Task assigned for the Release lane process", alertMessageBox.getText().toString());
                alertAcceptButton.click();
                log.info("No Open Tasks alert on ActivityPage validated and accepted");
                break;
        }
    }

    private WebDriverWait getWait(int secs) {
        WebDriverWait wait = new WebDriverWait(driver, secs);
        return wait;
    }
}
