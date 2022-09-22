package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.WavesInProgressPage;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLWave;

import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;

@Slf4j
public class WaveDashboardSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();

    WavesInProgressPage waveDashboardPage = PageFactory.initElements(driver, WavesInProgressPage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    SeUiContextBase seUiContextBase = new SeUiContextBase();

    private StepsContext stepsContext;
    BasePage basePage = new BasePage(driver);

    public WaveDashboardSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @Then("User searches and validates WAVE Dashboard Results for $searchType")
    public void waveDashboardValidation(String searchType) throws Exception {
        String WAVE_NBR = "20200305005";
        waveDashboardPage.waitForProcessing(60);
        HashMap<String, String> testQueryParams = new HashMap<String, String>();
        if (searchType.equals("DEFAULT")) {
            testQueryParams.clear();
        }
        List<Map<String, String>> gridData = waveDashboardPage.getGridElementsMapAllPages();
        Map<String, Map<String, String>> waveUIMap = gridData.stream().collect(Collectors.toMap(map -> map.get("Wave No"), map -> map));
        log.info("UI Grid" + waveUIMap.toString());
        List<Map<String, String>> DBdata = defaultWaveDashboard();
        Map<String, Map<String, String>> waveDBMap = DBdata.stream().collect(Collectors.toMap(map -> map.get("Wave No"), map -> map));
        log.info("DB Grid" + waveDBMap.toString());
        CommonUtils.doJbehavereportConsolelogAndAssertion("Wave Dashboard Count validated",
                "UI Wave IDs: " + waveUIMap.keySet() + " DB Wave IDs: " + waveDBMap.keySet(),
                waveUIMap.keySet().equals(waveDBMap.keySet()));
        waveDashboardPage.selectAttributeValue(WAVE_NBR);
        waveDashboardPage.searchWave();
        waveDashboardPage.waitForProcessing(150);
        List<Map<String, String>> waveUIData = waveDashboardPage.getGridElementsMapforWave(2);
        Map<String, Map<String, String>> waveUIdataMap = waveUIData.stream().collect(Collectors.toMap(map -> map.get("Wave No"), map -> map));
        List<Map<String, String>> waveDashboardDBdata = WAVE_DASHBOARD(WAVE_NBR);
        Set<String> WaveMap = waveDashboardDBdata.stream().map(map -> map.get("Wave No")).collect(Collectors.toSet());
        Table<String, String, Map<String, String>> dbTable = HashBasedTable.create();
        for (Map<String, String> dbTableMap : waveDashboardDBdata) {
            Map<String, String> reportingDataMap = dbTableMap;
            reportingDataMap.put("Wave Activation", String.valueOf(dbTableMap.get("Wave Activation") + ".0"));
            if (dbTableMap.get("Demand").equals(dbTableMap.get("Pickedvalue"))) {
                dbTableMap.remove("Pickedvalue");
                reportingDataMap.put("Picked", String.valueOf((dbTableMap.get("Picked") + "%")));
            } else {
                reportingDataMap.put("Picked", dbTableMap.get("Picked") + "%" + " " +String.valueOf(Integer.valueOf(dbTableMap.get("Demand")) - Integer.valueOf(dbTableMap.get("Pickedvalue"))));
                dbTableMap.remove("Pickedvalue");
            }
            if (dbTableMap.get("Demand").equals(dbTableMap.get("Stagedvalue"))) {
                dbTableMap.remove("Stagedvalue");
                reportingDataMap.put("Staged", String.valueOf((dbTableMap.get("Staged") + "%")));
            } else {
                reportingDataMap.put("Staged", dbTableMap.get("Staged") + "%" + " " +String.valueOf(Integer.valueOf(dbTableMap.get("Demand")) - Integer.valueOf(dbTableMap.get("Stagedvalue"))));
                dbTableMap.remove("Stagedvalue");
            }
            if (dbTableMap.get("Demand").equals(dbTableMap.get("SortedUnitsvalue"))) {
                dbTableMap.remove("SortedUnitsvalue");
                reportingDataMap.put("Sorted Units", String.valueOf((dbTableMap.get("Sorted Units") + "%")));
            } else {
                reportingDataMap.put("Sorted Units", dbTableMap.get("Sorted Units") + "%" + " " +String.valueOf(Integer.valueOf(dbTableMap.get("Demand")) - Integer.valueOf(dbTableMap.get("SortedUnitsvalue"))));
                dbTableMap.remove("SortedUnitsvalue");
            }
            if (dbTableMap.get("Demand").equals(dbTableMap.get("PutUnitsvalue"))) {
                dbTableMap.remove("PutUnitsvalue");
                reportingDataMap.put("Put Units", String.valueOf((dbTableMap.get("Put Units") + "%")));
            } else {
                reportingDataMap.put("Put Units", dbTableMap.get("Put Units") + "%" + " " +String.valueOf(Integer.valueOf(dbTableMap.get("Demand")) - Integer.valueOf(dbTableMap.get("PutUnitsvalue"))));
                dbTableMap.remove("PutUnitsvalue");
            }
            if (dbTableMap.get("Demand").equals(dbTableMap.get("Packedvalue"))) {
                dbTableMap.remove("Packedvalue");
                reportingDataMap.put("Packed", String.valueOf((dbTableMap.get("Packed") + "%")));
            } else {
                reportingDataMap.put("Packed", dbTableMap.get("Packed") + "%" + " " +String.valueOf(Integer.valueOf(dbTableMap.get("Demand")) - Integer.valueOf(dbTableMap.get("Packedvalue"))));
                dbTableMap.remove("Packedvalue");
            }
            if (dbTableMap.get("Demand").equals(dbTableMap.get("Shippedvalue"))) {
                dbTableMap.remove("Shippedvalue");
                reportingDataMap.put("Shipped", String.valueOf((dbTableMap.get("Shipped") + "%")));
            } else {
                reportingDataMap.put("Shipped", dbTableMap.get("Shipped") + "%" + " " + String.valueOf(Integer.valueOf(dbTableMap.get("Demand")) - Integer.valueOf(dbTableMap.get("Shippedvalue"))));
                dbTableMap.remove("Shippedvalue");
            }
            dbTable.put(dbTableMap.get("Wave No"), dbTableMap.get("Wave Type"), reportingDataMap);
            log.info(dbTableMap.toString());
        }
        Map<String, Map<String, String>> waveDBMapresult = waveDashboardDBdata.stream().collect(Collectors.toMap(map -> map.get("Wave No"), map -> map));
        CommonUtils.doJbehavereportConsolelogAndAssertion("Wave No Count validated",
                "Wave IDs: " + waveUIdataMap.keySet() + " DB Wave IDs: " + waveDBMapresult.keySet(),
                waveUIdataMap.keySet().equals(waveDBMapresult.keySet()));

        for (String waveId : waveDBMapresult.keySet()) {
            Map<String, String> dbvalue = waveDBMapresult.get(waveId);
            Map<String, String> uiValue = waveUIdataMap.get(waveId);
            String dbreturn = null, uiReturn = null;
            for (String value : dbvalue.keySet()) {
                if (dbvalue.get(value).equals(uiValue.get(value))) {
                    dbreturn = dbvalue.get(value);
                    uiReturn = uiValue.get(value);
                        CommonUtils.doJbehavereportConsolelogAndAssertion("wave details in wave dashboard: " + waveId,
                                " | Parameter to Compare: "+value+" | DB Details: " + dbreturn + " | UI Details: " + uiReturn, dbreturn.equals(uiReturn)
                        );

                }
            }
        }
    }

    public List<Map<String, String>> defaultWaveDashboard() {
        try {
            String query = String.format(SQLWave.WAVEDashboard_Default);
            List<Map<String, String>> defaultWaveDashboard = DBMethods.getValuesFromDBAsStringListMap(query, "orderselection");
            return defaultWaveDashboard;
        } catch (Exception e) {
            log.error("Exception in defaultWaveDashboard results", e);
            Assert.fail("Exception in defaultWaveDashboard results", e);
            return null;
        }
    }


    public List<Map<String, String>> WAVE_DASHBOARD(String WAVE_NBR) {
        try {
            String query = String.format(SQLWave.WAVE_DASHBOARD,WAVE_NBR);
            List<Map<String, String>> WAVE_DASHBOARD = DBMethods.getValuesFromDBAsStringListMap(query, "orderselection");
            return WAVE_DASHBOARD;
        } catch (Exception e) {
            log.error("Exception in defaultWaveDashboard results", e);
            Assert.fail("Exception in defaultWaveDashboard results", e);
            return null;
        }
    }

    @When("User click on WaveNo")
    public void clickWave()
    {
        waveDashboardPage.clickWave();
    }

}

