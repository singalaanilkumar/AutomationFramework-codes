package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.InventoryAdjustmentInquiry;
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

public class InventoryAdjustmentInquirySteps {

    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private InventoryAdjustmentInquiry adjustmentinquirypage = PageFactory.initElements(driver, InventoryAdjustmentInquiry.class);
    Logger log = Logger.getLogger(InventoryInquirySteps.class);
    private StepsContext stepsContext;
    public InventoryAdjustmentInquirySteps(StepsContext stepsContext) {
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

    @Then("user Validate InventoryAdjustmentInquiry")
    public void validateinvAdjustmentInquiry() {
        getContainer();
        String Container = (String) dataStorage.getStoredData().get("Containervalue");
        adjustmentinquirypage.selectAttributeValue(Container);
        adjustmentinquirypage.clickSearchButton();
        List<Map<String, String>> InventoryInquiryAdjustmentUI = adjustmentinquirypage.getGridElementsMap();
        Map<String, Map<String, String>> adjustmentUIMap = InventoryInquiryAdjustmentUI.stream().collect(Collectors.toMap(map -> map.get("Container"), map -> map));
        log.info("UI Map is" + adjustmentUIMap.toString());
        List<Map<String, String>> invAdjustmentDB = getDBData(Container);
        Map<String, Map<String, String>> adjustmentDBMap = invAdjustmentDB.stream().collect(Collectors.toMap(map -> map.get("Container"), map -> map));
        log.info("DB Map is" + adjustmentDBMap.toString());
        CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Inquiry Adjustment Count validated",
                "UI Container IDs: " + adjustmentUIMap.keySet() + " DB Container IDs: " + adjustmentDBMap.keySet(),
                adjustmentUIMap.keySet().equals(adjustmentDBMap.keySet()));
        for (String ContainerDB : adjustmentDBMap.keySet()) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("Inventory Adjustment Inquiry for Container " + ContainerDB,
                    " DB Details: " + adjustmentDBMap.get(ContainerDB) + " UI Details: " + adjustmentUIMap.get(ContainerDB),
                    adjustmentDBMap.get(ContainerDB).equals(adjustmentUIMap.get(ContainerDB)));

        }
    }

    @Then("user validates InventoryAdjustmentInquiry $selectedtab tab")
    public void inventoryAdjustmentInquiryTabs(String selectedtab)
    {
        adjustmentinquirypage.validateInventoryAdjustmentTabs(selectedtab);
    }


    public List<Map<String, String>> getDBData(String Container)
    {
        List<Map<String, String>> inventoryAdjustment = inventoryAdjustmentMap(Container);
        log.info(inventoryAdjustment.toString());
        return inventoryAdjustment;
    }


    public List<Map<String, String>> inventoryAdjustmentMap(String Container) {
        try {
            String query = String.format(SQLResearchInventory.InventoryAdjustmentInquiry, Container);
            List<Map<String, String>> inventoryAdjustment = DBMethods.getValuesFromDBAsStringListMap(query, "inventory");
            return inventoryAdjustment;
        }
        catch (Exception e)
        {
            log.error("Exception in InventoryAdjustmentInquiry", e);
            Assert.fail("Exception in InventoryAdjustmentInquiry", e);
            return null;
        }
    }

}
