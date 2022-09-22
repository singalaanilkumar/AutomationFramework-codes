package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.InventoryInquirySteps;
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

public class InventoryInquiry extends BasePage{
    Logger log = Logger.getLogger(InventoryInquirySteps.class);
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private SeUiContextBase seUiContextBase = new SeUiContextBase();
    @FindBy(xpath = "//*[@id='container']")
    WebElement containerbarcode;

    @FindBy(xpath = "//span[text()='search']")
    WebElement searchButton;

    @FindBy(xpath = "//*[@id=\"gridViewAttributes\"]")
    WebElement invattributes;

    @FindBy(xpath = "//*[@id=\"gridViewRelationships\"]")
    WebElement associations;

    @FindBy(xpath = "//*[@col-id='container']")
    private List<WebElement> containerList;

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
        seUiContextBase.waitFor(2);
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

    public void validateInventoryInquiryTabs(String selectedtab)
    {
        String Container= (String)dataStorage.getStoredData().get("Containervalue");
        switch(selectedtab)
        {
            case "ATTRIBUTES":
                selectContainer();
                invattributes.click();
                seUiContextBase.waitFor(2);
                List<Map<String, String>> inventoryattributesUI = getGridElementsMap(2,1);
                Map<String, Map<String, String>> attributesUIMap = inventoryattributesUI.stream().collect(Collectors.toMap(map -> map.get("PID"), map -> map));
                log.info("UI Map is" + attributesUIMap.toString());
                List<Map<String, String>> inventoryattributesDB = getinvattributesDBData(Container);
                Map<String, Map<String, String>> attributesDBMap = inventoryattributesDB.stream().collect(Collectors.toMap(map -> map.get("PID"), map -> map));
                log.info("DB Map is" + attributesDBMap.toString());
                CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Inquiry Attributes Count validated",
                        "UI Attribute IDs: " + attributesUIMap.keySet() + " DB Attribute IDs: " + attributesDBMap.keySet(),
                        attributesUIMap.keySet().equals(attributesDBMap.keySet()));
                for (String ContainerDB : attributesDBMap.keySet()) {
                    attributesUIMap.get(ContainerDB).remove("PO");
                    attributesUIMap.get(ContainerDB).remove("POReceipt");
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Inquiry Attributes for Container " + ContainerDB,
                            " DB Attributes: " + attributesDBMap.get(ContainerDB) + " UI Attributes: " + attributesUIMap.get(ContainerDB),
                            attributesDBMap.get(ContainerDB).equals(attributesUIMap.get(ContainerDB)));
                }
                break;
            case "ASSOCIATION":
                associations.click();
                seUiContextBase.waitFor(2);
                List<Map<String, String>> inventoryInquiryAssociationsUI = getGridElementsMap(2,1);
                Map<String, Map<String, String>> inventoryassociationsUIMap = inventoryInquiryAssociationsUI.stream().collect(Collectors.toMap(map -> map.get("Child Container"), map -> map));
                log.info("UI Map is" + inventoryassociationsUIMap.toString());
                List<Map<String, String>> inventoryInquiryassociationsDB = getinvassociationsDBData(Container);
                Map<String, Map<String, String>> invassociationsDBMap = inventoryInquiryassociationsDB.stream().collect(Collectors.toMap(map -> map.get("Child Container"), map -> map));
                log.info("DB Map is" + invassociationsDBMap.toString());
              CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Inquiry Associations Count validated",
                        "UI Association IDs: " + inventoryassociationsUIMap.keySet() + " DB Association IDs: " + invassociationsDBMap.keySet(),
                        inventoryassociationsUIMap.keySet().equals(invassociationsDBMap.keySet()));
                for (String ContainerDB : invassociationsDBMap.keySet()) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Inquiry Associations for Container " + ContainerDB,
                            " DB Details: " + invassociationsDBMap.get(ContainerDB) + " UI Details: " + inventoryassociationsUIMap.get(ContainerDB),
                            invassociationsDBMap.get(ContainerDB).equals(inventoryassociationsUIMap.get(ContainerDB)));

                }
                break;
        }

    }

    public List<Map<String, String>> getinvattributesDBData(String Container)
    {
        List<Map<String, String>> inventoryInquiryAttributes = inventoryInquiryAttributesMap(Container);
        log.info(inventoryInquiryAttributes.toString());
        return inventoryInquiryAttributes;
    }


    public List<Map<String, String>> inventoryInquiryAttributesMap(String Container) {
        try {
            String query = String.format(SQLResearchInventory.InventoryInquiryAttributes, Container);
            List<Map<String, String>> inventoryInquiryAttributes = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return inventoryInquiryAttributes;
        }
        catch (Exception e)
        {
            log.error("Exception in InventoryInquiryAttributes", e);
            Assert.fail("Exception in InventoryInquiryAttributes", e);
            return null;
        }
    }


    public List<Map<String, String>> getinvassociationsDBData(String Container)
    {
        List<Map<String, String>> inventoryInquiryAssociations = inventoryInquiryAssociationsMap(Container);
        log.info(inventoryInquiryAssociations.toString());
        return inventoryInquiryAssociations;
    }


    public List<Map<String, String>> inventoryInquiryAssociationsMap(String Container) {
        try {
            String query = String.format(SQLResearchInventory.ContainerInquiryassociations, Container);
            List<Map<String, String>> inventoryInquiryAssociations = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return inventoryInquiryAssociations;
        }
        catch (Exception e)
        {
            log.error("Exception in InventoryInquiryAssociations", e);
            Assert.fail("Exception in InventoryInquiryAssociations", e);
            return null;
        }
    }
}
