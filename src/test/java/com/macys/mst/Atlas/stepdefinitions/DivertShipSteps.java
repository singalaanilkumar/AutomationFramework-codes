package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.Atlas.pageobjects.DivertShipPage;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.concurrent.ConcurrentHashMap;

public class DivertShipSteps {
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    DivertShipPage manifestPackagePage = PageFactory.initElements(driver, DivertShipPage.class);
    public long TestNGThreadID = Thread.currentThread().getId();
    private StepsContext stepsContext;

    public DivertShipSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("User performs Divertship using API call $values")
    public void performDivertShip(ExamplesTable values){
        manifestPackagePage.performDivertShipUsingAPI(values);
    }

}
