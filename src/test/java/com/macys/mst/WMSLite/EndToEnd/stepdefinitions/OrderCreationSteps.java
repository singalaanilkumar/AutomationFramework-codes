package com.macys.mst.WMSLite.EndToEnd.stepdefinitions;

import com.macys.mst.WMSLite.EndToEnd.pageobjects.CreateOrderPage;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.concurrent.ConcurrentHashMap;

public class OrderCreationSteps {

    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    private CreateOrderPage createOrderPage= PageFactory.initElements(driver, CreateOrderPage.class);
//    private StepsContext stepsContext;
//    public OrderCreationSteps(StepsContext stepsContext) {
//        this.stepsContext = stepsContext;
//    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("post single line order")
    public void postSingleOrder() {
        createOrderPage.sendQueueMessage();
    }
}
