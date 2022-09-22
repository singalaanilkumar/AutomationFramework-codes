package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.InventoryInquiry;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryInquirySteps {
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private InventoryInquiry InventoryInquirypage = PageFactory.initElements(driver, InventoryInquiry.class);
    Logger log = Logger.getLogger(InventoryInquirySteps.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;
    public InventoryInquirySteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    public void getContainer() {
        Map<String, List<String>> poLinesToteId = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
        if (!poLinesToteId.isEmpty()) {
            List<String> toteIds = poLinesToteId.values().stream().flatMap(List::stream).collect(Collectors.toList());
            String Container=toteIds.get(0);
            dataStorage.getStoredData().put("Containervalue",Container);
        }
    }

    @Then("user validate InventoryInquiry")
    public void validateInventoryInquiry() {
        getContainer();
        String Container= (String)dataStorage.getStoredData().get("Containervalue");
        InventoryInquirypage.selectAttributeValue(Container);
        InventoryInquirypage.clickSearchButton();
        List<Map<String, String>> InventoryInquiryUI = InventoryInquirypage.getGridElementsMap();
        Map<String, Map<String, String>> attributesUIMap = InventoryInquiryUI.stream().collect(Collectors.toMap(map -> map.get("Container"), map -> map));
        log.info("UI Map is" + attributesUIMap.toString());
        List<Map<String, String>> inventoryInquiryDB = getDBData(Container);
        Map<String, Map<String, String>> attributesDBMap = inventoryInquiryDB.stream().collect(Collectors.toMap(map -> map.get("Container"), map -> map));
        log.info("DB Map is" + attributesDBMap.toString());
        CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Inquiry Count validated",
                "UI Container IDs: " + attributesUIMap.keySet() + " DB Container IDs: " + attributesDBMap.keySet(),
                attributesUIMap.keySet().equals(attributesDBMap.keySet()));
        for (String ContainerDB : attributesDBMap.keySet()) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Inquiry Details for Container " + ContainerDB,
                    " DB Details: " + attributesDBMap.get(ContainerDB) + " UI Details: " + attributesUIMap.get(ContainerDB),
                    attributesDBMap.get(ContainerDB).equals(attributesUIMap.get(ContainerDB)));

        }
    }

    public List<Map<String, String>> getDBData(String Container)
    {
        List<Map<String, String>> inventoryInquiry = inventoryInquiryMap(Container);
        log.info(inventoryInquiry.toString());
        return inventoryInquiry;
    }


    public List<Map<String, String>> inventoryInquiryMap(String Container) {
        try {
            String query = String.format(SQLResearchInventory.InventoryInquiry, Container);
            List<Map<String, String>> inventoryInquiry = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return inventoryInquiry;
        }
        catch (Exception e)
        {
            log.error("Exception in inventoryInquiry", e);
            Assert.fail("Exception in inventoryInquiry", e);
            return null;
        }
    }

    @Then("user validates InventoryInquiry $selectedtab tab")
    public void inventoryInquiryTabs(String selectedtab)
    {
        InventoryInquirypage.validateInventoryInquiryTabs(selectedtab);
    }

}
