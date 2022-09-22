package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.Home;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.Menu;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.SnapShotUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.utils.PubSubUtil;
import com.macys.mst.foundationalServices.utils.TearDownMethods;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CommonSteps {

	public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    public long TestNGThreadID = Thread.currentThread().getId();
    CommonUtils commonUtils = new CommonUtils();
    RequestUtil requestUtil = new RequestUtil();
    BasePage basePage = new BasePage();
    Home home = new Home();
    Menu menu=new Menu();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    
    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("Clean up data $param")
    public void cleanup(ExamplesTable table) {
        TearDownMethods tearDownMethods = new TearDownMethods();
        for (Iterator<Map<String, String>> iterator = table.getRows().iterator(); iterator.hasNext(); ) {
            Map<String, String> exRows = iterator.next();
            String poNbr = exRows.get("PO_NBR");
            String rcptNbr = exRows.get("RCPT_NBR");

            log.info("Test Data PONumber:{} Receipt nbr: {}", poNbr, rcptNbr);
            tearDownMethods.cleanUpAnyExistingTotes(poNbr, rcptNbr);
            tearDownMethods.POFourWallDataCleanup(rcptNbr);
            tearDownMethods.tearDownForEndToEnd(poNbr, rcptNbr,true);

        }
    }

    @Given("publish LogD message $param")
    public void publish_logD(ExamplesTable values) {
        if (values.getRowCount() > 0) {
            for (int i = 0; i < values.getRowCount(); i++) {
                String cartonNbr = values.getRow(i).get("cartonNbr");
                String trackingNbr = values.getRow(i).get("TrackingNbr");
                if (trackingNbr.isEmpty() || trackingNbr == null) {
                    RandomUtil randomUtil = new RandomUtil();
                    trackingNbr = randomUtil.getRandomValue("1S-D-18");
                }
                String reqParam = "{#CartonNbr:" + cartonNbr + ",#TrackingNbr:" + trackingNbr + "}";
                List<String> filledRequest = requestUtil.getRequestBody(reqParam, "LOGD.json");
                StepDetail.addDetail(String.format("The request posted is: " + filledRequest), true);
                String projectId = commonUtils.getEnvConfigValue("GCPProjId");
                String topic = commonUtils.getEnvConfigValue("pubSub.topics.LogD");
                PubSubUtil pubSubUtil = new PubSubUtil();
                try {
                    pubSubUtil.publishMessage(projectId, topic, filledRequest);
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Publish LOGD", projectId + "\n" + topic, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Assert.fail("Publishing RTF is failed");
                }
            }
        }
    }
    
    @Then("take screenshot for $scenarioName")
    public void afterFailedScenario(String scenarioName) {
	       SnapShotUtils.takeScreenShot(driver, scenarioName ,"Failure");
	       driver.quit();
    }
    
    
	@Given("User logs in SCM application")
	public void loginSCMApplication() throws Exception {
		basePage.loginSCM();		
	}

    @Given("SupplyChain home page")
    public void logedInHome() {
            home.signIn();
            menu = new Menu();
            CommonUtils.doJbehavereportConsolelogAndAssertion("Logged in ", "Success", true);
    }
    
    @Given("Clean Rediscache by PoRecptNum")
    public void rediscacheByPoRecptNum(){
        String poNbr = (String)dataStorage.getStoredData().get("poNbr");
        String rcptNbr = (String)dataStorage.getStoredData().get("rcptNbr");
        String finalUrl = commonUtils.getUrl("redisCache.deleteByRcptNum").replace("{poRecptNbr}",rcptNbr);
        int deleteStatus = WhmRestCoreAutomationUtils.deleteRequestResponse(finalUrl).getStatusCode();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Redis Cache cleared for : ",finalUrl,200==deleteStatus);
        String deleteCachePo =  commonUtils.getUrl("redisCache.deleteByPo").replace("{poNbr}",poNbr);
        deleteStatus = WhmRestCoreAutomationUtils.deleteRequestResponse(deleteCachePo).getStatusCode();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Redis Cache cleared for : ",deleteCachePo,200==deleteStatus);
        String updateCacheEndpoint = commonUtils.getUrl("redisCache.updateByPo").replace("{poNbr}",poNbr);
        RestUtilities.putRequestResponse(updateCacheEndpoint,"");
    }
}
