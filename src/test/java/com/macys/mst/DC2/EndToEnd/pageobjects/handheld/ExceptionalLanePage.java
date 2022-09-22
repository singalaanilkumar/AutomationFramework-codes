package com.macys.mst.DC2.EndToEnd.pageobjects.handheld;

import com.macys.mst.DC2.EndToEnd.configuration.WsmEndpoint;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class ExceptionalLanePage extends BasePage {
    CommonUtils commonUtils = new CommonUtils();

    public ExceptionalLanePage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "entryBox")
    WebElement scanContainertxtbox;

    @FindBy(xpath = "//*[text()='Locate Container']")
    WebElement locateContainer;

    @FindBy(xpath = "//*[@type='text']")
    WebElement statingLoctionEntrybox;

    @FindBy(xpath = "//SPAN[text()='Exit']")
    WebElement exitButton;

    @FindBy(xpath = "//*[text()='Enter']")
    WebElement enterButton;

    @FindBy(xpath = "//*[text()='OK']")
    WebElement OKButton;

    @FindBy(xpath = "//*[text()='Expected Totes: ']/following-sibling::label")
    WebElement ExpectedToteqty;

    @FindBy(xpath = "//*[text()='Actual Totes: ']/parent::div/following-sibling::div")
    WebElement ActualToteqtyTxtBox;

    @FindBy(xpath = "//*[@id='app']/div/div[2]/div/div[3]/div/div")
    WebElement exceptionalLaneScreenElements;

    @FindBy(xpath = "//*[@type='number']")
    WebElement actualTotesInputbox;

    public void navigateTolocateContainer() {
        getWait(10).until(ExpectedConditions.elementToBeClickable(locateContainer));
        locateContainer.click();
    }

    public void scanContainerBarcode(String containerbarcode) {
        getWait(10).until(ExpectedConditions.elementToBeClickable(scanContainertxtbox));
        scanContainertxtbox.click();
        scanContainertxtbox.sendKeys(containerbarcode);
        scanContainertxtbox.sendKeys(Keys.ENTER);
    }

    public Map<String,String> getWSMActivitiesforLanes(String ContainerId) {
        Map<String, String> exceptionalLaneActivityDetailsAPI = null;
        String containerGetActivity = String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH_RELEASELANE_ByContainerId, "Lane", "ASSIGNED", ContainerId);
        log.info("ActivitiesEndpoint :" + containerGetActivity);
        String OpenActivitiesJson = CommonUtil.getRequestResponse(containerGetActivity);
        List<Integer> openActivityIdList = new ArrayList<Integer>();
        if (!StringUtils.isBlank(OpenActivitiesJson)) {
            JSONArray ActivitiesArray = new JSONArray(OpenActivitiesJson);
            for (int i = 0; i < ActivitiesArray.length(); i++) {
                openActivityIdList.add(ActivitiesArray.getJSONObject(i).getInt("id"));
            }
            for (int i = 0; i < openActivityIdList.size(); i++) {
                String activityEndpoint = String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH_By_ID, openActivityIdList.get(i));
                String activityGetResponse = CommonUtil.getRequestResponse(activityEndpoint);
                exceptionalLaneActivityDetailsAPI = getexceptionalLaneDetails(activityGetResponse);
                exceptionalLaneActivityDetailsAPI.remove("Actual Totes");
                break;
            }
        }
        return exceptionalLaneActivityDetailsAPI;
    }

    public void clickButton(String button) {
        if (button.equalsIgnoreCase("exit")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(exitButton));
            exitButton.click();
        }
        else if (button.equalsIgnoreCase("Enter")) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(enterButton));
            enterButton.click();
        }
        else if(button.equalsIgnoreCase("OK")){
            getWait(5).ignoring(Exception.class).until(visibilityOf(OKButton));
            OKButton.click();
        }
    }

    public String validateExceptionallaneStatus(String LaneId) {
        String lane_status = null;
        try {
            String activityEndpoint = String.format(WsmEndpoint.WSM_ACTIVITY_SEARCH_By_ID, LaneId);
            String activityGetResponse = CommonUtil.getRequestResponse(activityEndpoint);
            JsonPath activityResponse = new JsonPath(activityGetResponse);
            lane_status = activityResponse.getString("[0].status");
        } catch(Exception e){
            return null;
        }
        return lane_status;
    }

    private Map<String, String> getexceptionalLaneDetails(String response) {
        Map<String, String> activityDetails = new HashMap<String, String>();
        JsonPath activityResponse = new JsonPath(response);
        activityDetails.put("Activity ID", activityResponse.getString("[0].id"));
        activityDetails.put("Lane ID", activityResponse.getString("[0].containerId"));
        activityDetails.put("PO Number", activityResponse.getString("[0].attributes.poNbr"));
        activityDetails.put("PO Receipt Number", activityResponse.getString("[0].attributes.poRcptNbr"));
        activityDetails.put("Expected Totes", activityResponse.getString("[0].totalQty"));
        activityDetails.put("Actual Totes", activityResponse.getString("[0].qty"));
        return activityDetails;
    }
    public void waitForexceptionalLaneScreenElemnts(){
        getWait(15).until(ExpectedConditions.visibilityOf(exceptionalLaneScreenElements));
    }

    public void enterQty(String qty){
        getWait(5).until(ExpectedConditions.visibilityOf(actualTotesInputbox));
        actualTotesInputbox.clear();
        actualTotesInputbox.sendKeys(qty);
    }
}
