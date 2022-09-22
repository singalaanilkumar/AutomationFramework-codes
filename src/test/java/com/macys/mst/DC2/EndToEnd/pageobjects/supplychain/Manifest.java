package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.ManifestSQL;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.ManifestSteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Manifest extends BasePage {
    private SeUiContextBase seUiContextBase = new SeUiContextBase();
    private static Logger log = Logger.getLogger(ManifestSteps.class);
    private StepsDataStore dataStorage = StepsDataStore.getInstance();

    @FindBy(xpath = "//*[@id='select-printerIP']")
    WebElement printerType;

    @FindBy(xpath = "//*[@id='menu-printerIP']//*[@role='listbox']")
    WebElement printerTypeMenu;

    @FindBy(xpath = "//*[@id='cartonNbr']")
    WebElement cartonNbr;

    @FindBy(xpath = "//span[text()='SEARCH']")
    WebElement searchButton;

    @FindBy(xpath = "//span[text()='MANIFEST']")
    WebElement manifestButton;

    @FindBy(xpath = "//*[@id='cartonStatus']")
    private WebElement crtStatus;

    @FindBy(xpath = "//*[@id='actualWeight']")
    WebElement actualWeight;

    @FindBy(xpath = "//*[@id='length']")
    WebElement length;

    @FindBy(xpath = "//*[@id='width']")
    WebElement width;

    @FindBy(xpath = "//*[@id='height']")
    WebElement height;

    @FindBy(xpath = "//div[@id='select-shipVia']")
    private WebElement Ship_ViaLabel;

    @FindBy(xpath = "//*[@id='trackingNumber']")
    WebElement trackingNumber;

    @FindBy(xpath = "//*[@id='baseAmount']")
    WebElement baseAmount;

    @FindBy(xpath = "//*[@id='totalShipmentAmount']")
    WebElement totalShipmentAmount;

    @FindBy(xpath = "//ul[@role='listbox']")
    private WebElement ShipViaMenu;

    public void selectPrinterType(String PrinterTypeName) {
        try {
            getWait().until(ExpectedConditions.elementToBeClickable(printerType));
            printerType.click();
            jsClick(driver.findElement(
                    By.xpath("//div[@role='document']//ul//li[contains(text(),'" + PrinterTypeName + "')]")));
            seUiContextBase.waitFor(2);
            log.info("Selected printerType: " + PrinterTypeName);
            StepDetail.addDetail("Selected printerType: " + PrinterTypeName, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select printerType", false);
            Assert.fail("Unable to select printerType");
        }

    }

    public void selectAttributeValue(String cartonInput) {

        try {
            getWait().until(ExpectedConditions.visibilityOf(cartonNbr));
            cartonNbr.sendKeys(cartonInput);
            log.info("Selected CartonNumber Value: " + cartonInput);
            StepDetail.addDetail("Selected CartonNumber Value: " + cartonInput, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select CartonNumber Value", false);
            Assert.fail("Unable to select CartonNumber Value");
        }

    }

    public static void sendkeys(WebElement element, String data) {
        try {
            element.clear();
            element.sendKeys(new CharSequence[]{data});
        } catch (Exception var3) {
            log.error(var3);
        }

    }

    public void enterActualWeight(String ActualWeight) {
        dataStorage.getStoredData().put("ActualWeightValue", ActualWeight);
        getWait().until(ExpectedConditions.visibilityOf(actualWeight));
        sendkeys(actualWeight, Keys.CONTROL + "a");
        seUiContextBase.waitFor(2);
        actualWeight.sendKeys(ActualWeight);
    }

    public void selectShipVia(String ShipVia) {
    try
    {
        scrollElementIntoView(driver.findElement(By.xpath("//div[@id='select-shipVia']")));
        getWait(60).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(Ship_ViaLabel));
        Ship_ViaLabel.click();
        getWait(60).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(ShipViaMenu));
        getWait(60);
        jsClick(driver.findElement(By.xpath("//*[@id='menu-shipVia']//li[contains(text(),'" + ShipVia + "')]")));
        seUiContextBase.waitFor(2);
        log.info("Selected ShipVia value: " + ShipVia);
    }
    catch(Exception e)
    {
        e.printStackTrace();
        StepDetail.addDetail("Unable to select ShipVia", false);
        Assert.fail("Unable to select ShipVia");
    }

}

    public void clickButton(String buttonType) {
        if (buttonType.equalsIgnoreCase("Search")) {
            seUiContextBase.waitFor(2);
            getWait().until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        } else if (buttonType.equalsIgnoreCase("Manifest")) {
            seUiContextBase.waitFor(2);
            getWait().until(ExpectedConditions.elementToBeClickable(manifestButton)).click();
        }
    }

    public void validateManifestScreen()
    {
        String cartonInput= (String)dataStorage.getStoredData().get("cartonInput");
        driver.navigate().refresh();
        seUiContextBase.waitFor(5);
        selectPrinterType("ZM2-T01");
        selectAttributeValue(cartonInput);
        clickButton("Search");
        seUiContextBase.waitFor(2);
        Map<String, String> Manifest = new HashMap<>();
        Manifest.put("Carton Status",getWait().until(ExpectedConditions.visibilityOf(crtStatus)).getAttribute("value"));
        Manifest.put("Actual Weight",getWait().until(ExpectedConditions.visibilityOf(actualWeight)).getAttribute("value"));
        Manifest.put("Length (Dimension)",getWait().until(ExpectedConditions.visibilityOf(length)).getAttribute("value"));
        Manifest.put("Width (Dimension)",getWait().until(ExpectedConditions.visibilityOf(width)).getAttribute("value"));
        Manifest.put("Height (Dimension)",getWait().until(ExpectedConditions.visibilityOf(height)).getAttribute("value"));
        WebElement shipvia=driver.findElement(By.id("shipVia"));
        Manifest.put("Ship Via",shipvia.getAttribute("value"));
        Manifest.put("Tracking Number",getWait().until(ExpectedConditions.visibilityOf(trackingNumber)).getAttribute("value"));
        Manifest.put("Base Amount",getWait().until(ExpectedConditions.visibilityOf(baseAmount)).getAttribute("value"));
        Manifest.put("Total Shipment Amount",getWait().until(ExpectedConditions.visibilityOf(totalShipmentAmount)).getAttribute("value"));
        List<Map<String, String>> ManifestUI=new ArrayList<Map<String, String>>();
        ManifestUI.add(Manifest);
        Map<String, Map<String, String>> ManifestUIMap = ManifestUI.stream().collect(Collectors.toMap(map -> map.get("Tracking Number"), map -> map));
        log.info("UI Map is" + ManifestUIMap.toString());
        List<Map<String, String>> ManifestUIDB = getDBData(cartonInput);
        Map<String, Map<String, String>> ManifestDBMap = ManifestUIDB.stream().collect(Collectors.toMap(map -> map.get("Tracking Number"), map -> map));
        log.info("DB Map is" + ManifestDBMap.toString());
        CommonUtils.doJbehavereportConsolelogAndAssertion("Manifest Attributes Count validated",
                "UI Attribute IDs: " + ManifestUIMap.keySet() + " DB Attribute IDs: " + ManifestDBMap.keySet(),
                ManifestUIMap.keySet().equals(ManifestDBMap.keySet()));
        for (String CartonDB : ManifestDBMap.keySet()) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Manifest Attributes for Carton " + CartonDB,
                    " DB Attributes: " + ManifestDBMap.get(CartonDB) + " UI Attributes: " + ManifestUIMap.get(CartonDB),
                    ManifestDBMap.get(CartonDB).equals(ManifestUIMap.get(CartonDB)));
        }
    }

    public List<Map<String, String>> getDBData(String Carton)
    {
        List<Map<String, String>> ManifestMap = ManifestMap(Carton);
        log.info(ManifestMap.toString());
        return ManifestMap;
    }


    public List<Map<String, String>> ManifestMap(String Carton) {
        try {
            String query = String.format(ManifestSQL.Manifest, Carton,Carton,Carton);
            List<Map<String, String>> ManifestMap = null;
            if(CommonUtils.packageFlag){
               ManifestMap = DBMethods.getValuesFromDBAsStringListMap(query, "Package");
            }else{
               ManifestMap = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            }
            return ManifestMap;
        }
        catch (Exception e)
        {
            log.error("Exception in Manifest", e);
            Assert.fail("Exception in Manifest", e);
            return null;
        }
    }

}
