package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.ContainerInquirySteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocationView extends BasePage {
    Logger log = Logger.getLogger(ContainerInquirySteps.class);
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private SeUiContextBase seUiContextBase = new SeUiContextBase();

    @FindBy(xpath = "//*[@id='select-locationType']")
    WebElement locationType;

    @FindBy(xpath = "//*[@id='menu-locationType']//*[@role='listbox']")
    WebElement locationTypeMenu;

    @FindBy(xpath = "//*[@name='locationBarCode']")
    WebElement locationBarCode;

    @FindBy(xpath = "//span[text()='SEARCH']")
    WebElement searchButton;

    @FindBy(xpath = "//span[text()='SAVE']")
    WebElement saveButton;

    @FindBy(xpath = "//span[text()='CLOSE']")
    WebElement closeButton;

    @FindBy(xpath="//li[@role='option']")
    public List<WebElement> ScheduledAreaList;

    @FindBy(xpath = "//*[@id='menu-scheduled-area']//*[@role='listbox']")
    WebElement ScheduledAreaMenu;

    public void selectLocationType(String LocationTypeButtonPosition)
    {

        try {
        getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(locationType));
        locationType.click();
        getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(locationTypeMenu));
        getWait(60);
        jsClick(driver.findElement(
                By.xpath("//*[@id='menu-locationType']//li[@data-value='" + LocationTypeButtonPosition + "']")));
            seUiContextBase.waitFor(2);
        log.info("LocationType Selected");
            } catch (Exception e) {
        e.printStackTrace();
        StepDetail.addDetail("Unable to select LocationType", false);
        Assert.fail("Unable to select LocationType");
    }

    }

    public void selectAttributeValue(String locationbarcodeInput) {
        try {

            getWait(60).until(ExpectedConditions.visibilityOf(locationBarCode));
            locationBarCode.sendKeys(locationbarcodeInput);
            log.info("Selected locationBarCode Value: " + locationbarcodeInput);
            StepDetail.addDetail("Selected locationBarCode Value: " + locationbarcodeInput, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select locationBarCode Value", false);
            Assert.fail("Unable to select locationBarCode Value");
        }

    }

    public void clickSearchButton() {
        seUiContextBase.waitFor(2);
        getWait().until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    public void userClicksButton(String buttonName)
    {
        seUiContextBase.waitFor(2);
        List<WebElement> editButtons = driver.findElements(By.xpath("//span[contains(text(),'" + buttonName + "')]"));
        editButtons.get(0).click();
    }



    public void userSelectsAttributesValueFromDropdown(int optionNumber,String dropdown) {
        WebElement Element = driver.findElement(By.xpath("//input[@name='" + dropdown + "']/parent::div"));
        String ScheduledAreaValue=getWait().until(ExpectedConditions.visibilityOf(Element)).getText();
        dataStorage.getStoredData().put("OriginalScheduledAreaValue",ScheduledAreaValue);
        seUiContextBase.waitFor(2);
        Element.click();
        seUiContextBase.waitFor(1);
        ScheduledAreaList.get(optionNumber).click();
        seUiContextBase.waitFor(1);
        saveButton.click();
        waitForProcessing();
        seUiContextBase.waitFor(1);
        closeButton.click();
    }


    public void userUpdatesAttributesValueFromDropdown(String dropdown) {
        WebElement Element = driver.findElement(By.xpath("//input[@name='" + dropdown + "']/parent::div"));
        seUiContextBase.waitFor(2);
        Element.click();
        seUiContextBase.waitFor(1);
        String OriginalScheduledAreaValue= (String)dataStorage.getStoredData().get("OriginalScheduledAreaValue");
            jsClick(driver.findElement(
                    By.xpath("//*[@id='menu-scheduled-area']//li[@data-value='" + OriginalScheduledAreaValue + "']")));
        seUiContextBase.waitFor(1);
        saveButton.click();
        seUiContextBase.waitFor(1);
        closeButton.click();
    }

    public void validateLocationHeaderView( ) {
        String groupid="6";
        List<String> headerFieldsDB = new ArrayList<>();
        List<String> headerFieldsUI = new ArrayList<>();
        List<String> newheaderFieldsUI = new ArrayList<>();
        List<Map<String, String>> LocationViewUI = getGridElementsMap();
        for (Map<String, String> map : LocationViewUI) {
            map.remove("Item");
            map.remove("Location");
            map.remove("Container");
            map.remove("Units");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                headerFieldsUI.add(entry.getKey());
               newheaderFieldsUI =headerFieldsUI.stream().distinct().collect(Collectors.toList());
               Collections.sort(newheaderFieldsUI);
            }
        }
        log.info("Location View Header UI" + newheaderFieldsUI.toString());
        List<Map<String, String>> LocationViewHeadersDB = getLocationViewHeaders(groupid);
        for (Map<String, String> map1 : LocationViewHeadersDB) {
              map1.get("DESCRIPTION");
                    for (Map.Entry<String, String> entry : map1.entrySet()) {
                        headerFieldsDB.add(entry.getValue());
                        Collections.sort(headerFieldsDB);
            }
        }
        log.info("Location View Header DB" + headerFieldsDB.toString());
        if(newheaderFieldsUI.equals(headerFieldsDB))
        log.info("Location view Headers are same in UI and DB");
    }

    public void validateLocationView(String locationvalue) {
        List<Map<String, String>> LocationViewUI = getGridElementsMap();
        for (Map<String, String> map : LocationViewUI) {
            map.entrySet().removeIf(ent -> ent.getValue().isEmpty());
        }
        Map<String, Map<String, String>> locationUIMap = LocationViewUI.stream().collect(Collectors.toMap(map -> map.get("UPC"), map -> map));
        log.info("UI Map is" + locationUIMap.toString());
        List<Map<String, String>> LocationViewDB = getDBData(locationvalue);
        for (Map<String, String> map : LocationViewDB) {
            String ATTRIBUTEVALUES = map.get("ATTRIBUTEVALUES");
            JSONArray jsonArray = new JSONArray(ATTRIBUTEVALUES);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                String key = object.get("key").toString();
                JSONArray valArray = (JSONArray) object.get("values");
                String valArray1=valArray.get(0).toString();
                switch (key) {
                    case "Section":
                    case "Processing Area":
                        if (valArray1.equalsIgnoreCase("OSC")) {
                            valArray1 = valArray1.replace("OSC", "Open Sort Count");
                            map.put(key, valArray1);
                        } else if (valArray1.equalsIgnoreCase("BLK")) {
                            valArray1 = valArray1.replace("BLK", "Bulk");
                            map.put(key, valArray1);
                        } else if (valArray1.equalsIgnoreCase("JWL")) {
                            valArray1 = valArray1.replace("JWL", "Jewelry");
                            map.put(key, valArray1);
                        } else if (valArray1.equalsIgnoreCase("OVR")) {
                            valArray1 = valArray1.replace("OVR", "Oversize");
                            map.put(key, valArray1);
                        } else if (valArray1.equalsIgnoreCase("PTC")) {
                            valArray1 = valArray1.replace("PTC", "Pick To Carton");
                            map.put(key, valArray1);
                        }
                        break;
                    case "Scheduled Area":
                        map.put(key, valArray1);break;
                    case "Max Nbr OfÂ Skus":
                        map.put(key, valArray1);break;
                }
            }
            map.remove("ATTRIBUTEVALUES");
        }
        Map<String, Map<String, String>> locationviewDBMap = LocationViewDB.stream().collect(Collectors.toMap(map -> map.get("UPC"), map -> map));
        log.info("DB Map is" + locationviewDBMap.toString());
        CommonUtils.doJbehavereportConsolelogAndAssertion("Location View Screen Count validated",
                "UI Item IDs: "+locationUIMap.keySet()+" DB Report IDs: "+locationviewDBMap.keySet(),
                locationUIMap.keySet().equals(locationviewDBMap.keySet()));
        for(String Item:locationviewDBMap.keySet()){
            CommonUtils.doJbehavereportConsolelogAndAssertion("Location View Screen for Item "+Item,
                    " DB Details: " + locationviewDBMap.get(Item) + " UI Details: " + locationUIMap.get(Item),
                    locationviewDBMap.get(Item).equals(locationUIMap.get(Item)));
        }
    }



    public List<Map<String, String>> getDBData(String locationvalue)
    {
        List<Map<String, String>> LocationView = locationViewMap(locationvalue);
        log.info(LocationView.toString());
        return LocationView;
    }


    public List<Map<String, String>> locationViewMap(String locationvalue) {
        try {
            String query = String.format(SQLResearchInventory.LocationView, locationvalue);
            List<Map<String, String>> LocationView = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return LocationView;
        }
        catch (Exception e)
        {
            log.error("Exception in LocationView", e);
            Assert.fail("Exception in LocationView", e);
            return null;
        }
    }


    public List<Map<String, String>> getLocationViewHeaders(String groupid)
    {
        List<Map<String, String>> LocationViewHeaders = locationViewHeadersMap(groupid);
        log.info(LocationViewHeaders.toString());
        return LocationViewHeaders;
    }


    public List<Map<String, String>> locationViewHeadersMap(String groupid) {
        try {
            String query = String.format(SQLResearchInventory.LocationViewHeaders, groupid);
            List<Map<String, String>> LocationViewHeaders = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return LocationViewHeaders;
        }
        catch (Exception e)
        {
            log.error("Exception in LocationViewHeaders", e);
            Assert.fail("Exception in LocationViewHeaders", e);
            return null;
        }
    }


    }



