package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.execdrivers.ExecutionConfig;
import com.macys.mst.DC2.EndToEnd.pageobjects.ReleaseLanePage;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ReleaseLaneSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private ReleaseLanePage releaseLanePage = PageFactory.initElements(driver, ReleaseLanePage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }


    public ReleaseLaneSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @Given("user logs in to RF application and selects $menu menu")
    public void loginToRFoptions(String menu) throws Exception {

        //logintoApplication();
        //selectOptionFromMenumenu(menu);
    }

    @When("User scans Lane for Release Lane transaction $paramtable")
    public void completeReleaseLaneSteps(ExamplesTable paramtable) {
        List<Map<String, String>> exRows = paramtable.getRows();
        exRows.stream().forEach(m -> {
            m.forEach((k, v) -> {
                if (k.equals("Scan LaneID") && !v.equalsIgnoreCase("NA")) {
                    try {
                        releaseLanePage.whenUserScansLocationBarcode(v);
                    } catch (InterruptedException e) {
                        log.info(e.toString());
                    }
                    releaseLanePage.whenUserValidatesNextLocationBarcode(v);
                } else if (k.equals("Scan LaneID Again") && !v.equalsIgnoreCase("NA")) {
                    releaseLanePage.whenUserValidatesNextLocationBarcode(v);
                } else if (k.equals("CompleteAllPOAndQty") && v.equalsIgnoreCase("Yes")) {
                    String[] value = v.split("&");
                    releaseLanePage.thenUserCompletesAllActivityforPO(value[1]);
                }
            });
        });
    }

    @When("User releases the lane for the totes using RF Release Lane option")
    public void releaseLane() {
        if( ExecutionConfig.getExecEnv().equals("UAT"))
            releaseLanePage.buttonClick();
    }

    @Then("WSM tasks are completed to release the lane")
    public void validateReleaseLaneTasks() {
        if( ExecutionConfig.getExecEnv().equals("UAT")) {
            ListMultimap<String, String> stagingLocationMap = (ListMultimap<String, String>) stepsContext.get(Context.STAGING_LOCATION_TOTE_MAP.name());
            Multimaps.asMap(stagingLocationMap).forEach((stageLocation, totelist) -> {
                try {
                    releaseLanePage.whenUserScansLocationBarcode(stageLocation);
                    releaseLanePage.whenUserValidatesNextLocationBarcode(stageLocation);
                    releaseLanePage.thenUserCompletesAllActivityforPO(String.valueOf(totelist.size()));
                } catch (InterruptedException e) {
                    log.error("Release lane:", e);
                    Assert.fail("WSM tasks are completed to release the lane");
                }
            });
        }
    }

}
