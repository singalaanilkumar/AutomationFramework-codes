package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.macys.mst.DC2.EndToEnd.pageobjects.WavesInProgressPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.WorkloadPlanningUIPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.IncorrectDataException;
import com.macys.mst.DC2.EndToEnd.utilmethods.RequestUtil;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class WorkloadPlanningUISteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();

    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    private WorkloadPlanningUIPage workloadPlanningUIPage = new WorkloadPlanningUIPage(driver);
    private WavesInProgressPage wavesInProgressPage = new WavesInProgressPage(driver);
    private RequestUtil requestUtil = new RequestUtil();

    @SuppressWarnings("unchecked")
    @Then("Preview Wave,Cancel Preview and Run for $waveCount wave on SCMUI$value")
    public void previewCancelPreviewAndRunWaveUndoWaveonUI(String waveCount, ExamplesTable value) throws Exception {
        if (1 == value.getRows().size()) {
            Map<String, String> processedGetQP = requestUtil.getRandomParamsfromMap(value.getRow(0).get("WaveDetails"));

            Table<String, String, String> rtfsPublished = (Table<String, String, String>) dataStorage.getStoredData().get("verifiedRTFs");
            Table<String, String, String> toBeWavedRTFs = HashBasedTable.create();
            List<String> shipDates = new ArrayList<>();
            List<String> shipLocations = new ArrayList<>();
            Integer shipLocationsCount = 0;
            Integer totalunits = 0;
            List<String> listofBinBoxes = (List<String>) dataStorage.getStoredData().get("inventoryContainerList");
            Integer containers = listofBinBoxes.size();

            String listOfHoldDates = joinList(getListofDates(processedGetQP.get("#efctStartDt"), processedGetQP.get("#efctEndDt")));
            String listOfShipDates = joinList(getListofDates(processedGetQP.get("#startShpDt"), processedGetQP.get("#endShpDt")));

            for (Cell<String, String, String> cell : rtfsPublished.cellSet()) {
                Integer units = 0;
                JsonPath rtfPath = new JsonPath(cell.getValue());
                if (listOfHoldDates.contains(rtfPath.getString("shipment.holdDate")) && listOfShipDates.contains(rtfPath.getString("shipment.expectedShipDate")))
                    toBeWavedRTFs.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
                shipDates.add(rtfPath.getString("shipment.expectedShipDate"));
                shipLocations.add(rtfPath.getString("shipment.shipToLocationNbr"));
                if (StringUtils.isNotEmpty(rtfPath.getString("shipment.shipToLocationNbr")))
                    shipLocationsCount = shipLocationsCount + 1;
                else
                    shipLocationsCount = shipLocationsCount + 0;
                JSONArray lineItemsArray;

                lineItemsArray = new JSONObject(cell.getValue()).getJSONArray("lineItem");
                for (int i = 0; i < lineItemsArray.length(); i++) {
                    JsonPath lineItemPublished = new JsonPath(lineItemsArray.get(i).toString());
                    units = units + Integer.valueOf(lineItemPublished.getInt("itemQuantity"));
                }
                totalunits = totalunits + units;
                Collections.sort(shipDates);
            }

            dataStorage.getStoredData().put(waveCount + "RTFs", toBeWavedRTFs);

            workloadPlanningUIPage.clickNavOption("Wave");
            workloadPlanningUIPage.clickNavOption("Workload Planning");

            workloadPlanningUIPage.selectWaveType(processedGetQP.get("#waveType"));
            workloadPlanningUIPage.selectDept(new ArrayList<String>(Arrays.asList(StringUtils.split(processedGetQP.get("#dept"), ","))));
            workloadPlanningUIPage.selectStoreType(processedGetQP.get("#storeType"));
            workloadPlanningUIPage.selectShipoutDateRange(processedGetQP.get("#startShpDt"), processedGetQP.get("#endShpDt"));
            workloadPlanningUIPage.selectEffectiveDateRange(processedGetQP.get("#efctStartDt"), processedGetQP.get("#efctEndDt"));
            workloadPlanningUIPage.selectNoOfOrders(processedGetQP.get("#noOfOrdersLimit"));
            workloadPlanningUIPage.selectNoOfUnitsPick(processedGetQP.get("#noOfUnits"));

            workloadPlanningUIPage.clickPreviewWaveButton();

            String waveID = workloadPlanningUIPage.validatePreviewWaveMsg();
            dataStorage.getStoredData().put(waveCount + "ID", waveID);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Preview Wave successful. WaveID:", waveID, true);

            workloadPlanningUIPage.selectWaveID(waveID);
            workloadPlanningUIPage.cancelPreviewButton();
            workloadPlanningUIPage.validateCancelPreviewWaveMsg();

            CommonUtils.doJbehavereportConsolelogAndAssertion("Cancel Preview Wave successful. WaveID:", waveID, true);
            
            workloadPlanningUIPage.clickNavOption("Workload Planning");
            workloadPlanningUIPage.selectWaveType(processedGetQP.get("#waveType"));
            workloadPlanningUIPage.selectDept(new ArrayList<String>(Arrays.asList(StringUtils.split(processedGetQP.get("#dept"), ","))));
            workloadPlanningUIPage.selectStoreType(processedGetQP.get("#storeType"));
            workloadPlanningUIPage.selectShipoutDateRange(processedGetQP.get("#startShpDt"), processedGetQP.get("#endShpDt"));
            workloadPlanningUIPage.selectEffectiveDateRange(processedGetQP.get("#efctStartDt"), processedGetQP.get("#efctEndDt"));
            workloadPlanningUIPage.selectNoOfOrders(processedGetQP.get("#noOfOrdersLimit"));
            workloadPlanningUIPage.selectNoOfUnitsPick(processedGetQP.get("#noOfUnits"));

            workloadPlanningUIPage.clickPreviewWaveButton();

            waveID = workloadPlanningUIPage.validatePreviewWaveMsg();
            dataStorage.getStoredData().put(waveCount + "ID", waveID);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Preview Wave successful. WaveID:", waveID, true);

            workloadPlanningUIPage.selectWaveID(waveID);
            List<Map<String, String>> previewWaveUI = workloadPlanningUIPage.getGridElementsMap();
            log.info("previewWaveUIvalues = {}", previewWaveUI.toString());

            Map<String, Map<String, String>> previewWaveUIMap = previewWaveUI.stream().collect(Collectors.toMap(map -> map.get("Batch #"), map -> map));
            log.info("previewWaveUIMap: {}", previewWaveUIMap);

            String ShipDate = previewWaveUIMap.get(waveID).get("Oldest Ship by Date");
            Integer unit = Integer.parseInt(previewWaveUIMap.get(waveID).get("Units"));
            Integer ShipLocations = Integer.parseInt(previewWaveUIMap.get(waveID).get("Ship To Locations"));
            Integer containersinUI = Integer.parseInt(previewWaveUIMap.get(waveID).get("Containers"));

            CommonUtils.doJbehavereportConsolelogAndAssertion("Ship Date is validated and ShipDate is :", ShipDate, ShipDate.equalsIgnoreCase(shipDates.get(0)));
            CommonUtils.doJbehavereportConsolelogAndAssertion("Units is validated and Unit value is :", unit.toString(), totalunits.equals(unit));
            CommonUtils.doJbehavereportConsolelogAndAssertion("Ship Locations is validated and number of Shiplocations are :", ShipLocations.toString(), shipLocationsCount.equals(ShipLocations));
            CommonUtils.doJbehavereportConsolelogAndAssertion("Container is validated and number of container are :", containersinUI.toString(), containersinUI.equals(containers));
            CommonUtils.doJbehavereportConsolelogAndAssertion("Container is validated and number of container are :", previewWaveUIMap.get(waveID).get("Wave Template"), previewWaveUIMap.get(waveID).get("Wave Template").equals(processedGetQP.get("#waveType")));

            workloadPlanningUIPage.clickRunWaveButton();

            String waveNumber = workloadPlanningUIPage.validateRunWaveMsg(waveID);
            dataStorage.getStoredData().put(waveCount + "Number", waveNumber);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Run Wave successful. WaveNumber:", waveNumber, true);

        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @Then("Preview Run and Validate $waveCount wave on SCMUI$value")
    public void previewAndRunWaveonUI(String waveCount, ExamplesTable value) throws Exception {
        if (1 == value.getRows().size()) {
            Map<String, String> processedGetQP = requestUtil.getRandomParamsfromMap(value.getRow(0).get("WaveDetails"));

            Table<String, String, String> rtfsPublished = (Table<String, String, String>) dataStorage.getStoredData().get("verifiedRTFs");
            Table<String, String, String> toBeWavedRTFs = HashBasedTable.create();

            String listOfHoldDates = joinList(getListofDates(processedGetQP.get("#efctStartDt"), processedGetQP.get("#efctEndDt")));
            String listOfShipDates = joinList(getListofDates(processedGetQP.get("#startShpDt"), processedGetQP.get("#endShpDt")));

            for (Cell<String, String, String> cell : rtfsPublished.cellSet()) {
                JsonPath rtfPath = new JsonPath(cell.getValue());
                if (listOfHoldDates.contains(rtfPath.getString("shipment.holdDate")) && listOfShipDates.contains(rtfPath.getString("shipment.expectedShipDate")))
                    toBeWavedRTFs.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
            }

            dataStorage.getStoredData().put(waveCount + "RTFs", toBeWavedRTFs);

            workloadPlanningUIPage.clickNavOption("Wave");
            workloadPlanningUIPage.clickNavOption("Workload Planning");

            workloadPlanningUIPage.selectWaveType(processedGetQP.get("#waveType"));
            workloadPlanningUIPage.selectDept(new ArrayList<String>(Arrays.asList(StringUtils.split(processedGetQP.get("#dept"), ","))));
            workloadPlanningUIPage.selectStoreType(processedGetQP.get("#storeType"));
            workloadPlanningUIPage.selectShipoutDateRange(processedGetQP.get("#startShpDt"), processedGetQP.get("#endShpDt"));
            workloadPlanningUIPage.selectEffectiveDateRange(processedGetQP.get("#efctStartDt"), processedGetQP.get("#efctEndDt"));
            workloadPlanningUIPage.selectNoOfOrders(processedGetQP.get("#noOfOrdersLimit"));
            workloadPlanningUIPage.selectNoOfUnitsPick(processedGetQP.get("#noOfUnits"));

            workloadPlanningUIPage.clickPreviewWaveButton();

            String waveID = workloadPlanningUIPage.validatePreviewWaveMsg();
            dataStorage.getStoredData().put(waveCount + "ID", waveID);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Preview Wave successful. WaveID:", waveID, true);

            workloadPlanningUIPage.selectWaveID(waveID);
            workloadPlanningUIPage.clickRunWaveButton();

            String waveNumber = workloadPlanningUIPage.validateRunWaveMsg(waveID);
            dataStorage.getStoredData().put(waveCount + "Number", waveNumber);
            CommonUtils.doJbehavereportConsolelogAndAssertion("Run Wave successful. WaveNumber:", waveNumber, true);

        } else {
            throw new IncorrectDataException("Supports only one row of data");
        }
    }

    @Then("release $waveCount wave on SCMUI$value")
    public void releaseWaveonUI(String waveCount) throws Exception {
        String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");

        wavesInProgressPage.clickNavOption("Wave");
        wavesInProgressPage.clickNavOption("Waves in Progress");
        wavesInProgressPage.selectWaveNumber(waveNumber);
        wavesInProgressPage.clickReleaseToPickWaveButton();

        wavesInProgressPage.releaseWaveValidation(waveNumber);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Release Wave successful. WaveNumber:", waveNumber, true);
    }

    @Then("Undo $waveCount wave on SCMUI")
    public void undoWaveonUI(String waveCount) throws Exception {
        String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");

        wavesInProgressPage.clickNavOption("Waves in Progress");
        wavesInProgressPage.waitForProcessing();
        wavesInProgressPage.selectWaveNumber(waveNumber);
        wavesInProgressPage.clickUndoWaveButton();
        wavesInProgressPage.UndoWaveAlertValidation("Yes");
        wavesInProgressPage.UndoWaveValidation(waveNumber);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Undo Wave successful. WaveNumber:", waveNumber, true);
    }

    @Then("close $waveCount wave on SCMUI")
    public void closeWaveonUI(String waveCount) throws Exception {
        String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");
        
        wavesInProgressPage.clickNavOption("Wave");
        wavesInProgressPage.clickNavOption("Waves in Progress");
        wavesInProgressPage.selectWaveNumber(waveNumber);
        wavesInProgressPage.clickCloseWaveButton();
        wavesInProgressPage.closeWaveValidation(waveNumber);

        wavesInProgressPage.searchwithWaveNumber(waveNumber);
        wavesInProgressPage.selectWaveNumber(waveNumber);
        wavesInProgressPage.verifyWaveStatus("Closed", waveNumber);

        CommonUtils.doJbehavereportConsolelogAndAssertion("Close Wave successful. WaveNumber:", waveNumber, true);
    }

    private ArrayList<String> getListofDates(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        ArrayList<String> totalDates = new ArrayList<>();
        while (!start.isAfter(end)) {
            totalDates.add(start.toString());
            start = start.plusDays(1);
        }
        return totalDates;
    }

    private String joinList(List<String> listofStrings) {
        return String.join(",", listofStrings.stream().map(stringValue -> ("'" + stringValue + "'")).collect(Collectors.toList()));
    }


}
