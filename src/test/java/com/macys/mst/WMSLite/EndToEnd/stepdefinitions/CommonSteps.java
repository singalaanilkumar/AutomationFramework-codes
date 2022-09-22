package com.macys.mst.WMSLite.EndToEnd.stepdefinitions;

import com.macys.mst.WMSLite.EndToEnd.db.app.SQLQueries;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.BasePage;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.FetchDBDataPage;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.Home;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.Menu;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.SnapShotUtils;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.utils.PubSubUtil;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import com.macys.mst.whm.coreautomation.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

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
    private FetchDBDataPage fetchOracleData = PageFactory.initElements(driver, FetchDBDataPage.class);
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    
    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
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

    @Given("user logs in WMS LITE application")
    public void loginwmsLiteApplication() throws Exception {
        basePage.loginWmsLite();
    }

    @When("user click menu item in WMS LITE application")
    public void clickMenu() throws Exception {
        basePage.clickWMsLitemenu();
    }
    @Then("Menu Item's popup will display")
    public void popupDisplay() throws Exception{
        basePage.displayPopupBox();
    }

    @Then("user validates data from OSM DB using fullfillmentnbr")
    public void validateOSMDBData(){
        fetchOracleData.getOSMDetailsUsingFullfillmentNbr("F011002561");
    }

    @Then("user validates data from Joppa DB using fullfillmentnbr")
    public void validateJoppaDBData(){
        fetchOracleData.getJoppaDbDetailsUsingFullfillmentNbr("F477083141");
    }

    @Then("user will validate data in $osmDB")
    public void getStatus(String dbType) throws Exception{
           String resNumber=dataStorage.getStoredData().get("RES_NBR").toString();
        String query = String.format(SQLQueries.GET_OSM_SHP_LI_DATA, resNumber);
        List<Map<Object, Object>> dbResults = fetchOracleData.getDbDetailsUsingResNumber(query,dbType);


        // String results=dbResults;
        if (dbResults.size() > 0)
            for (Map<Object, Object> storeAllocMap : dbResults) {
                StepDetail.addDetail("RES_NBR : "+storeAllocMap.get("RES_NBR").toString()+" RES_LN_STAT : "+storeAllocMap.get("RES_LN_STAT").toString() ,true);

            }
    }

    @Then("the user will validate upc in $dbType")
    public void getNilStatus(String dbType) throws Exception{
            String PKT_CTRL_NBR=dataStorage.getStoredData().get("PKT_CTRL_NBR").toString();

        String query = String.format(SQLQueries.GET_INPT_PKT_DTL, PKT_CTRL_NBR);
        List<Map<Object, Object>> dbResults =fetchOracleData.getDbDetailsUsingResNumber(query,dbType);


        // String results=dbResults;
        if (dbResults.size() > 0)
            for (Map<Object, Object> storeAllocMap : dbResults) {

                try {
                    String nilStatus=storeAllocMap.get("NIL_STATUS").toString();
                    StepDetail.addDetail("NIL_STATUS : "+nilStatus+" SIZE_DESC : "+storeAllocMap.get("SIZE_DESC").toString()+" PKT_CTRL_NBR : "+storeAllocMap.get("PKT_CTRL_NBR").toString()
                            +"CANCEL_QTY : "+storeAllocMap.get("CANCEL_QTY").toString()+" ORIG_ORD_QTY : "+storeAllocMap.get("ORIG_ORD_QTY").toString(),true);


                }catch (NullPointerException e){
                    String nilStatus="null";
                    StepDetail.addDetail("NIL_STATUS : "+nilStatus+" SIZE_DESC : "+storeAllocMap.get("SIZE_DESC").toString()+" PKT_CTRL_NBR : "+storeAllocMap.get("PKT_CTRL_NBR").toString()
                            +"CANCEL_QTY : "+storeAllocMap.get("CANCEL_QTY").toString()+" ORIG_ORD_QTY : "+storeAllocMap.get("ORIG_ORD_QTY").toString(),true);

                }


            }
    }


    @Then("user will validate wave status code as $statusCode in $DB $beforepreview wave")
    public void dbTest(String beforepreview,String DB,String statusCode)
            throws Exception {
        Thread.sleep(3000);
        // dbTest();

        if(beforepreview.contains("Submit")){

            List<String> listOfResNumber = (List<String>) dataStorage.getStoredData().get("list");
            if(listOfResNumber!=null){

                for (String resNumber : listOfResNumber){
                   String query = String.format(SQLQueries.GET_WAVE_STATUS, resNumber);
                    List<Map<Object, Object>> dbResults =fetchOracleData.getDbDetailsUsingResNumber(query,DB);

                    // String results=dbResults;
                    if (dbResults.size() > 0)
                        for (Map<Object, Object> storeAllocMap : dbResults) {
                            StepDetail.addDetail("WAVE_STAT_CODE : "+storeAllocMap.get("WAVE_STAT_CODE").toString()+" STATUS : "+storeAllocMap.get("STATUS").toString() +
                                    " INPT_PKT_HDR_ID : "+storeAllocMap.get("INPT_PKT_HDR_ID").toString()+"  PKT_NBR : "+
                                    storeAllocMap.get("PKT_NBR").toString(),true);

                            String reservationNumber=storeAllocMap.get("PKT_NBR").toString();
                            dataStorage.getStoredData().put("RES_NBR",reservationNumber);

                        }
                }
            }else{
                String resNumber=dataStorage.getStoredData().get("search_value").toString();
                String query = String.format(SQLQueries.GET_WAVE_STATUS, resNumber);
                List<Map<Object, Object>> dbResults =fetchOracleData.getDbDetailsUsingResNumber(query,DB);

                // String results=dbResults;
                if (dbResults.size() > 0)
                    for (Map<Object, Object> storeAllocMap : dbResults) {
                        StepDetail.addDetail("WAVE_STAT_CODE : "+storeAllocMap.get("WAVE_STAT_CODE").toString()+" STATUS : "+storeAllocMap.get("STATUS").toString() +
                                " INPT_PKT_HDR_ID : "+storeAllocMap.get("INPT_PKT_HDR_ID").toString()+"  PKT_NBR : "+
                                storeAllocMap.get("PKT_NBR").toString(),true);

                        String reservationNumber=storeAllocMap.get("PKT_NBR").toString();
                        dataStorage.getStoredData().put("RES_NBR",reservationNumber);

                    }
            }

        }


        if(beforepreview.contains("preview")){

            String resNumber=dataStorage.getStoredData().get("search_value").toString();
            // String resNumber="110001725";
            String query = String.format(SQLQueries.GET_WAVE_STATUS, resNumber);
            List<Map<Object, Object>> dbResults =fetchOracleData.getDbDetailsUsingResNumber(query,DB);


            // String results=dbResults;
            if (dbResults.size() > 0)
                for (Map<Object, Object> storeAllocMap : dbResults) {
                    StepDetail.addDetail("WAVE_STAT_CODE : "+storeAllocMap.get("WAVE_STAT_CODE").toString()+" STATUS : "+storeAllocMap.get("STATUS").toString() +
                            " INPT_PKT_HDR_ID : "+storeAllocMap.get("INPT_PKT_HDR_ID").toString()+"  PKT_NBR : "+
                            storeAllocMap.get("PKT_NBR").toString(),true);

                    String reservationNumber=storeAllocMap.get("PKT_NBR").toString();
                    dataStorage.getStoredData().put("PKT_CTRL_NBR",storeAllocMap.get("PKT_CTRL_NBR").toString());
                    dataStorage.getStoredData().put("RES_NBR",reservationNumber);
                    if(beforepreview.contains("before")){
                        //  Assert.assertTrue("Wave status code is expecting after wave",storeAllocMap.get("WAVE_STAT_CODE").toString().equalsIgnoreCase(statusCode));

                    } else{
                        //  Assert.assertTrue("Wave status code is expecting after wave",storeAllocMap.get("WAVE_STAT_CODE").toString().equalsIgnoreCase(statusCode));

                    }
                }
        }
        if(beforepreview.contains("Undo")){
            String resNumber=dataStorage.getStoredData().get("RES_NBR").toString();

             String query = String.format(SQLQueries.GET_WAVE_STATUS, resNumber);
            List<Map<Object, Object>> dbResults =fetchOracleData.getDbDetailsUsingResNumber(query,DB);
            if (dbResults.size() > 0)
                for (Map<Object, Object> storeAllocMap : dbResults) {
                    StepDetail.addDetail("WAVE_STAT_CODE : "+storeAllocMap.get("WAVE_STAT_CODE").toString()+" STATUS : "+storeAllocMap.get("STATUS").toString() +
                            " INPT_PKT_HDR_ID : "+storeAllocMap.get("INPT_PKT_HDR_ID").toString()+"  PKT_NBR : "+
                            storeAllocMap.get("PKT_NBR").toString(),true);

                    String reservationNumber=storeAllocMap.get("PKT_NBR").toString();
                    dataStorage.getStoredData().put("RES_NBR",reservationNumber);
                    org.junit.Assert.assertTrue("Wave status code is expecting after wave",storeAllocMap.get("WAVE_STAT_CODE").toString().equalsIgnoreCase(statusCode));

                }
        }

    }
}
