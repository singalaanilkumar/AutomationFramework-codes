package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.InventoryAdjustmentInquirySteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryAdjustmentInquiry extends BasePage {
    private SeUiContextBase seUiContextBase = new SeUiContextBase();
    Logger log = Logger.getLogger(InventoryAdjustmentInquirySteps.class);
    private StepsDataStore dataStorage = StepsDataStore.getInstance();

    @FindBy(xpath = "//*[@id='container']")
    WebElement containerbarcode;

    @FindBy(xpath = "//span[text()='search']")
    WebElement searchButton;

    @FindBy(xpath = "//*[@col-id='container']")
    private List<WebElement> containerList;

    @FindBy(xpath = "//*[@id=\"gridViewAttributes\"]")
    WebElement adjustmentattributes;

    public void selectAttributeValue(String containerbarcodeInput) {

        try {

            getWait(60).until(ExpectedConditions.visibilityOf(containerbarcode));
            containerbarcode.sendKeys(containerbarcodeInput);
            log.info("Selected containerbarcode Value: " + containerbarcodeInput);
            StepDetail.addDetail("Selected containerbarcode Value: " + containerbarcodeInput, true);
        } catch (Exception e) {
            e.printStackTrace();
            StepDetail.addDetail("Unable to select containerbarcode Value", false);
            Assert.fail("Unable to select containerbarcode Value");
        }

    }

    public void clickSearchButton() {
        seUiContextBase.waitFor(10);
        getWait().until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    public void selectContainer()
    {
        String Container= (String)dataStorage.getStoredData().get("Containervalue");
        boolean containerFound = false;
        for (WebElement containerRow : containerList) {
            if (Container.equals(containerRow.getText())) {
                String rowID = containerRow.findElement(By.xpath("./..")).getAttribute("row-id");
                getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")));
                driver.findElement(By.xpath("//*[@row-id='"+rowID+"']/*[@col-id='0']")).click();
                containerFound = true;
                break;
            }
        }
        org.junit.Assert.assertTrue("ContainerID not displayed: "+Container, containerFound);
    }


   public void validateInventoryAdjustmentTabs(String selectedtab) {
       String Container = (String) dataStorage.getStoredData().get("Containervalue");
       switch (selectedtab) {
           case "ATTRIBUTES":
               selectContainer();
               adjustmentattributes.click();
               seUiContextBase.waitFor(2);
               List<Map<String, String>> adjustmentattributesUI = getGridElementsMap(2,1);
               Map<String, Map<String, String>> attributesUIMap = adjustmentattributesUI.stream().collect(Collectors.toMap(map -> map.get("PO"), map -> map));
               log.info("UI Map is" + attributesUIMap.toString());
               List<Map<String, String>> adjustmentAttributesDB = getAdjustmentAttributesDBData(Container);
               Map<String, Map<String, String>> attributesDBMap = adjustmentAttributesDB.stream().collect(Collectors.toMap(map -> map.get("PO"), map -> map));
               log.info("DB Map is" + attributesDBMap.toString());
               CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Adjustment Inquiry Attributes Count validated",
                       "UI Attribute IDs: " + attributesUIMap.keySet() + " DB Attribute IDs: " + attributesDBMap.keySet(),
                       attributesUIMap.keySet().equals(attributesDBMap.keySet()));
               for (String ContainerDB : attributesDBMap.keySet()) {
                   CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Adjustment Inquiry Attributes for Container " + ContainerDB,
                           " DB Attributes: " + attributesDBMap.get(ContainerDB) + " UI Attributes: " + attributesUIMap.get(ContainerDB),
                           attributesDBMap.get(ContainerDB).equals(attributesUIMap.get(ContainerDB)));

               }
               break;
       }
   }


    public List<Map<String, String>> getAdjustmentAttributesDBData(String Container)
    {
        List<Map<String, String>> inventoryAdjustmentAttributes = inventoryAdjustmentAttributesMap(Container);
        log.info(inventoryAdjustmentAttributes.toString());
        return inventoryAdjustmentAttributes;
    }


    public List<Map<String, String>> inventoryAdjustmentAttributesMap(String Container) {
        try {
            String query = String.format(SQLResearchInventory.InventoryAdjustmentAttributes, Container,Container);
            List<Map<String, String>> inventoryAdjustmentAttributes = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return inventoryAdjustmentAttributes;
        }
        catch (Exception e)
        {
            log.error("Exception in inventoryAdjustmentAttributes", e);
            Assert.fail("Exception in inventoryAdjustmentAttributes", e);
            return null;
        }
    }

}
