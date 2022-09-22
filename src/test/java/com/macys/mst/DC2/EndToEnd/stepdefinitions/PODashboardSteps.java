package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLPODashboard;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODashboardPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.artemis.testNg.TestNGListener;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.junit.Assert;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class PODashboardSteps {
    PODashboardPage poDashboardPage = new PODashboardPage();
    public long TestNGThreadID = Thread.currentThread().getId();
    SeUiContextBase seUiContextBase = new SeUiContextBase();
    StepsDataStore dataStorage = StepsDataStore.getInstance();

    private static StepsContext stepsContext;

    public PODashboardSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    CommonUtils commonUtils = new CommonUtils();

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("PO is available for the existing store for end to end processing")
    public void validatePO() throws Exception {
        log.info("Validate PO availability in Dashboard Screen");
        String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
        String rcptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());
        loadMacysBackstageHomepage();
        poDashboardPage.selectPODashboard();
        poDashboardPage.searchDashboard(poNbr);
        poDashboardPage.validatesPOStatusOnPODashboard(poNbr, rcptNbr);
        log.info("Dashboard grid validation completed");
    }


    public void loadMacysBackstageHomepage() throws Exception {
        poDashboardPage.loadHomepageURL();
        poDashboardPage.login();
        seUiContextBase.waitFor(5);
    }

    @Then("Release Button is $releaseButtonStatus and Close button is $closeButtonStatus after $POProcess")
    public void validateReleaseAndCloseButton(String releaseButtonStatus, String closeButtonStatus, String POProcess) throws Exception {
        String recptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());
        poDashboardPage.validateReleaseAndClose(recptNbr, releaseButtonStatus, closeButtonStatus);
        log.info("Release Button and Close button validation completed for {}", POProcess);
    }


    @Then("User searches and validates PO Dashboard Results for $searchType")
    public void poDashboardValidation(String searchType) throws Exception {
        poDashboardPage.selectPODashboard();
        poDashboardPage.waitForProcessing(60);
        HashMap<String, String> testQueryParams = new HashMap<String, String>();
        if (searchType.equals("PO")) {
            String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
            poDashboardPage.searchDashboard(poNbr);
            testQueryParams.put("PO NBR", poNbr);
        } else if (searchType.equals("DEFAULT")) {
            testQueryParams.clear();
        }
        List<Map<String, String>> gridData = poDashboardPage.getGridElementsMapAllPages();
        log.info(gridData.toString());
        Table<String, String, Map<String, String>> dataUIMapList = HashBasedTable.create();
        for (Map<String, String> dataUIMap : gridData) {
            dataUIMap.remove("Release Receipt");
            dataUIMap.remove("Close Receipt");
            if (dataUIMap.containsKey("VAS Units")) {
                dataUIMap.put("PREP Units", dataUIMap.remove("VAS Units"));
            }
            dataUIMapList.put(dataUIMap.get("PO NBR"), dataUIMap.get("PO Receipt NBR"), dataUIMap);
        }
        log.info(dataUIMapList.toString());
        Table<String, String, Map<String, String>> dataDBMapList = getResponseMapFromDB(testQueryParams);
        log.info(dataDBMapList.toString());
        CommonUtils.doJbehavereportConsolelogAndAssertion("Dashboard PO Number Count validated",
                "UI PO Numbers: " + dataUIMapList.rowKeySet() + " DB PO Numbers: " + dataDBMapList.rowKeySet(),
                dataUIMapList.rowKeySet().equals(dataDBMapList.rowKeySet()));

        CommonUtils.doJbehavereportConsolelogAndAssertion("Dashboard PO Receipt Number Count validated",
                "UI PO Receipt Numbers: " + dataUIMapList.columnKeySet() + " DB PO Receipt Numbers: " + dataDBMapList.columnKeySet(),
                dataUIMapList.columnKeySet().equals(dataDBMapList.columnKeySet()));

        for (Cell<String, String, Map<String, String>> cell : dataDBMapList.cellSet()) {
            Map<String, String> DBValues = cell.getValue();
            Map<String, String> UIValues = dataUIMapList.get(cell.getRowKey(), cell.getColumnKey());
            for (String key : DBValues.keySet()) {
                if (key.equals("Area Flow")) {
                    String[] dbArea = DBValues.get(key).trim().split(",");
                    String[] uiArea = UIValues.get(key).trim().split(",");
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Dashboard Details for PO Number =  " + cell.getRowKey() + " and Receipt Number = " + cell.getColumnKey(),
                            " | Value to be compared: " + key + " | DB Details: " + Arrays.toString(dbArea) + " | UI Details: " + Arrays.toString(uiArea),
                            Arrays.equals(dbArea, uiArea));
                } else {
/*                   CommonUtils.doJbehavereportConsolelogAndAssertion("Dashboard Details for PO Number =  "+cell.getRowKey() + " and Receipt Number =  "+cell.getColumnKey(),
                            " | Value to be compared: "+ key +" | DB Details: " + DBValues.get(key) + " | UI Details: " + UIValues.get(key),
                            DBValues.get(key).equals(UIValues.get(key))); */
                }
            }
            break;
        }
    }


    public Table<String, String, Map<String, String>> getResponseMapFromDB(Map<String, String> queryParams) {

        try {
            String pofourwallsQuery = buildSearchQuery(queryParams);
            log.info("PO4Walls query {}", pofourwallsQuery);
            List<Map<String, String>> pofourwallsDataMap = DBMethods.getValuesFromDBAsStringListMap(pofourwallsQuery, "pofourwalls");

            Set<String> rcptSet = pofourwallsDataMap.stream().map(map -> map.get("PO Receipt NBR")).collect(Collectors.toSet());
            Set<String> poSet = pofourwallsDataMap.stream().map(map -> map.get("PO NBR")).collect(Collectors.toSet());

            Table<String, String, Map<String, String>> dbTable = HashBasedTable.create();
            String inventoryQuery = String.format(SQLPODashboard.PODashboard_inventory, String.join(",", poSet), String.join(",", rcptSet));
            log.info("inventoryQuery {}", inventoryQuery);
            List<Map<String, String>> inventoryDataMapList = DBMethods.getValuesFromDBAsStringListMap(inventoryQuery, "inventory");

            Map<String, Map<String, String>> rcptPOMap = inventoryDataMapList.stream().collect(Collectors.toMap(map -> map.get("RCPT_NBR"), map -> map));

            for (Map<String, String> dbTableMap : pofourwallsDataMap) {

                Map<String, String> reportingDataMap = dbTableMap;
                Map<String, String> inventoryDataMap = rcptPOMap.get(dbTableMap.get("PO Receipt NBR"));

                if (inventoryDataMap != null && inventoryDataMap.size() != 0) {
                    Integer toteSummary = Integer.valueOf(inventoryDataMap.get("tote_summary"));

                    Integer totePrinted = Integer.valueOf(inventoryDataMap.get("tote_printed"))
                            + Integer.valueOf(inventoryDataMap.get("binbox_summary"))
                            + Integer.valueOf(inventoryDataMap.get("binbox_putaway_summary"))
                            + Integer.valueOf(inventoryDataMap.get("carton_put_summary"))
                            + Integer.valueOf(inventoryDataMap.get("carton_ship_summary"));

                    Integer totePreped = Integer.valueOf(inventoryDataMap.get("tote_vased"))
                            + Integer.valueOf(inventoryDataMap.get("binbox_summary"))
                            + Integer.valueOf(inventoryDataMap.get("binbox_putaway_summary"))
                            + Integer.valueOf(inventoryDataMap.get("carton_put_summary"))
                            + Integer.valueOf(inventoryDataMap.get("carton_ship_summary"));

                    Integer binboxPutSummary = Integer.valueOf(inventoryDataMap.get("binbox_summary"))
                            + Integer.valueOf(inventoryDataMap.get("binbox_putaway_summary"));

                    Integer binboxShipSummary = Integer.valueOf(inventoryDataMap.get("binbox_putaway_summary"));

                    Integer cartonPutSummary = Integer.valueOf(inventoryDataMap.get("carton_put_summary"))
                            + Integer.valueOf(inventoryDataMap.get("carton_ship_summary"));

                    Integer cartonShipSummary = Integer.valueOf(inventoryDataMap.get("carton_ship_summary"));

                    Integer totalUnitsCounted = toteSummary + binboxPutSummary + cartonPutSummary;

                    if (totalUnitsCounted > 0) {
                        reportingDataMap.put("Units Counted", String.valueOf(totalUnitsCounted));
                        reportingDataMap.put("Printed Tickets", String.valueOf((totePrinted * 100) / totalUnitsCounted + "%" + ((totalUnitsCounted - totePrinted != 0) ? " " + String.valueOf(totalUnitsCounted - totePrinted) : "")));
                        reportingDataMap.put("PREP Units", String.valueOf((totePreped * 100) / totalUnitsCounted + "%" + ((totalUnitsCounted - totePreped != 0) ? " " + String.valueOf(totalUnitsCounted - totePreped) : "")));
                        reportingDataMap.put("Put Units/ Packaway", String.valueOf(((cartonPutSummary + binboxPutSummary) * 100) / totalUnitsCounted + "%" + ((totalUnitsCounted - (cartonPutSummary + binboxPutSummary) != 0) ? " " + String.valueOf(totalUnitsCounted - (cartonPutSummary + binboxPutSummary)) : "")));
                        reportingDataMap.put("Shipped/ Located", String.valueOf(((cartonShipSummary + binboxShipSummary) * 100) / totalUnitsCounted + "%" + ((totalUnitsCounted - (cartonShipSummary + binboxShipSummary) != 0) ? " " + String.valueOf(totalUnitsCounted - (cartonShipSummary + binboxShipSummary)) : "")));
                    } else {
                        reportingDataMap.put("Units Counted", "0");
                        reportingDataMap.put("Printed Tickets", "0");
                        reportingDataMap.put("PREP Units", "0");
                        reportingDataMap.put("Put Units/ Packaway", "0");
                        reportingDataMap.put("Shipped/ Located", "0");
                    }
                } else {
                    reportingDataMap.put("Units Counted", "0");
                    reportingDataMap.put("Printed Tickets", "0");
                    reportingDataMap.put("PREP Units", "0");
                    reportingDataMap.put("Put Units/ Packaway", "0");
                    reportingDataMap.put("Shipped/ Located", "0");
                }

                if (queryParams == null || queryParams.size() == 0) {
                    if (Integer.valueOf(reportingDataMap.get("Units Counted")) > 0) {
                        dbTable.put(dbTableMap.get("PO NBR"), dbTableMap.get("PO Receipt NBR"), reportingDataMap);
                    }
                } else {
                    dbTable.put(dbTableMap.get("PO NBR"), dbTableMap.get("PO Receipt NBR"), reportingDataMap);
                }
            }

            return dbTable;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String buildSearchQuery(Map<String, String> searchParams) {
        String searchQuery = SQLPODashboard.PODashboard_4walls;
        StringBuilder queryArgs = new StringBuilder();
        String searchValue;
        if (searchParams == null || searchParams.size() == 0) {
            return String.format(searchQuery, "AND RC.STATUS not in ('Close', 'CIP') AND DATE(ORDERRCPT.DELIV_TS) > date_sub(CURDATE() ,INTERVAL 99 DAY) LIMIT 100");
        }

        for (Map.Entry<String, String> searchParam : searchParams.entrySet()) {
            searchValue = CommonUtils.getQuotedString(searchParam.getValue());
            queryArgs.append("AND ");
            switch (searchParam.getKey()) {
                case "PO NBR":
                    queryArgs.append("PO.PO_NBR = (").append(searchValue).append(") \n");
                    break;
                case "Status":
                    queryArgs.append("RC.STATUS IN (").append(searchValue).append(") \n");
                    break;
                case "Area Flow":
                    queryArgs.append("POL.PROCESS_AREA IN (").append(searchValue).append(") \n");
                    break;
                default:
                    throw new NoSuchElementException("No search key found");
            }
        }
        return String.format(searchQuery, queryArgs.toString());
    }

    @Then("receipt is closed from PODashboardUI")
    public void closeReceipt() {
        poDashboardPage.loginSCM();
        poDashboardPage.selectPODashboard();
        poDashboardPage.waitForProcessing(30);

        String poNbr = (String) dataStorage.getStoredData().get("poNbr");
        String rcptNbr = (String) dataStorage.getStoredData().get("rcptNbr");

        poDashboardPage.searchDashboard(poNbr);

        List<Map<String, String>> gridDataBeforeClose = poDashboardPage.getGridElementsMapAllPages();
        List<Map<String, String>> gridDataAfterClose;

        for (int i = 0; i < gridDataBeforeClose.size(); i++) {
            if (gridDataBeforeClose.get(i).get("PO Receipt NBR").equalsIgnoreCase(rcptNbr)) {
                String receiptStatusBeforClose = gridDataBeforeClose.get(i).get("Status");
                log.info("Receipt status Before close in po4walls {}", receiptStatusBeforClose);

                try {
                    TimeUnit.SECONDS.sleep(3);
                    if (poDashboardPage.horizontalScrollBar.isDisplayed()) {
                        poDashboardPage.scrollGridTableRight(1);
                    }
                    TimeUnit.SECONDS.sleep(3);
                    if (poDashboardPage.closeButton.isEnabled()) {

                        closeReceiptFromPODashboardUI();

                        //reading the result grid data after receipt close
                        gridDataAfterClose = poDashboardPage.getGridElementsMapAllPages();

                        //validates the receipt status after close
                        if (gridDataAfterClose.size() > 0) {
                            String receiptSatatusAfterClose = gridDataAfterClose.get(i).get("Status");
                            log.info("receiptSatatusAfterClose:{}", receiptSatatusAfterClose);

                            if (receiptSatatusAfterClose.equalsIgnoreCase("Close Attempt Failed")) {
                                for (int r = 0; r < 2; r++) {
                                    if (receiptSatatusAfterClose.equalsIgnoreCase("Close Attempt Failed")) {
                                        closeReceiptFromPODashboardUI();
                                        receiptSatatusAfterClose = gridDataAfterClose.get(i).get("Status");
                                    } else {
                                        break;
                                    }
                                }
                                if (receiptSatatusAfterClose.equalsIgnoreCase("Close Attempt Failed")) {
                                    log.info("Re-deploy OMS service");
                                    Assert.fail();
                                }
                            }
                            CommonUtils.doJbehavereportConsolelogAndAssertion("Receipt status After close in po4walls : ", receiptSatatusAfterClose, ("Closed".equalsIgnoreCase(receiptSatatusAfterClose) || "Close In Progress".equalsIgnoreCase(receiptSatatusAfterClose)));
                        }
                    }
                } catch (Exception ex) {
                    log.info("Exception occurred {} :: ", ex.getMessage());
                    Assert.fail();
                }
            }
        }
    }

    public void closeReceiptFromPODashboardUI() throws Exception {
        //click on close button present in main result grid
        poDashboardPage.closeButton.click();
        TimeUnit.SECONDS.sleep(3);

        //confirm that user want to close the receipt
        poDashboardPage.getWait(30).until(ExpectedConditions.elementToBeClickable(poDashboardPage.closeConfirmationButton));
        log.info("Displayed pop-up with title :: {}", poDashboardPage.popupTitle.getText());
        poDashboardPage.closeConfirmationButton.click();
        poDashboardPage.waitForProcessing(30);
        TimeUnit.SECONDS.sleep(3);

        //reading the closure confirmation and closing
        poDashboardPage.getWait(30).until(ExpectedConditions.elementToBeClickable(poDashboardPage.OK));
        log.info("Displayed pop-up with title :: {}", poDashboardPage.popupTitle.getText());
        poDashboardPage.OK.click();
        poDashboardPage.waitForProcessing(30);

        //time to load the result grid with changed status
        TimeUnit.SECONDS.sleep(5);

        if (poDashboardPage.horizontalScrollBar.isDisplayed()) {
            poDashboardPage.scrollGridTableLeft(1);
        }
    }
}
