package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.datasetup.SmokeTestData;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SmokeTestServicesSteps{

    public long TestNGThreadID = Thread.currentThread().getId();

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Given("Validation method for all services from $environmentsToExecute")
    public void givenAllServices(String environmentsToExecute){
        String environments = StringUtils.isNotBlank(System.getProperty("environmentsToExecute"))?
                System.getProperty("environmentsToExecute"):environmentsToExecute;

        log.info("User Passed Environments :{}",environments);

        String[] listOfEnvironments = environments.split(",");

        for(String env:listOfEnvironments){
            env = env.toUpperCase();
            String hostId = SmokeTestData.getHostMap().get(env);
            if(null==hostId){
                Assert.fail("No such environment as : "+env);
            }
            testGETServices(env,hostId);
            testPOSTServices(hostId);
        }
    }

    private void testGETServices(String env, String hostId){
        StepDetail.addDetail(CommonUtils.createString("***"+env+"***",110),StringUtils.isNotBlank(hostId));
        for(String url:getFinalURLsList(hostId,"GET")){
            invokeGivenGETService(url);
        }
        if(!"PERF".equals(env) && !"PROD".equals(env)){
            unleashGET(hostId);
        }
    }

    private void testPOSTServices(String hostId){
        for(String url:getFinalURLsList(hostId,"POST")){
            invokeGivenPostService(url);
        }
    }

    public List<String> getFinalURLsList(String hostId, String method) {
        List<String> finalUrls = new ArrayList<>();
        Map<String, String> serviceUrls = null;
        if("GET".equalsIgnoreCase(method)){
            serviceUrls = SmokeTestData.getServiceUrls();
        }else if ("POST".equalsIgnoreCase(method)){
            serviceUrls = SmokeTestData.getPOSTServiceUrls();
        }else{
            Assert.fail("Unsupported method");
        }
        for (String key : serviceUrls.keySet()) {
            StringBuilder finalUrl = new StringBuilder(hostId).append(serviceUrls.get(key));
            finalUrls.add(finalUrl.toString());
        }
        return finalUrls;
    }

    public void invokeGivenGETService(String url){
        if(StringUtils.isNotBlank(url)){
            CommonUtils.doJbehavereportConsolelogAndAssertion("$$$ REQUEST $$$",url,StringUtils.isNotBlank(url));
            Response response;
            if(url.contains("msc.gcp.cloudrts.net")){
                response = RestAssured.given().relaxedHTTPSValidation().headers(ExpectedDataProperties.getHeaderProps()).get(url);
            }else{
                ContentType contentType= ContentType.JSON;
                response = RestAssured.given().contentType(contentType).
                        headers(ExpectedDataProperties.getHeaderProps()).get(url);
            }

            int statusCode = response.getStatusCode();
            if( response.contentType()==null ||  response.contentType().isEmpty() ||  response.contentType().equals("")){
                CommonUtils.doJbehavereportConsolelogAndAssertion("### RESPONSE ###", response.asString(), 204 == statusCode );

            }else {
                CommonUtils.doJbehavereportConsolelogAndAssertion("### RESPONSE ###", response.asString(), ( 200 == statusCode || 417 == statusCode) && response.contentType().contains("application/json"));
            }
            StepDetail.addDetail(CommonUtils.createString("__",500),true);
        }
    }

    private void unleashGET(String env){
        if(env.contains("dev") || env.contains("qa") || env.contains("uat")){
            env = env.replace("//","//ul-");
        }else{
            env = env.replace("//","//ul.");
        }
        String finalUrl = env+"/unleash/api/admin/features";
        invokeGivenGETService(finalUrl);
    }

    private void invokeGivenPostService(String url) {
        if (StringUtils.isNotBlank(url)) {
            CommonUtils.doJbehavereportConsolelogAndAssertion("$$$ REQUEST $$$", url, StringUtils.isNotBlank(url));
            Response response;

            //default contenttype and body
            ContentType contentType= ContentType.JSON;
            String body = "{}";
            //only for pyramid-communication
            if(url.contains("pyramid-communication")){
                contentType= ContentType.TEXT;
                body = "||";
            }


            if (url.contains("msc.gcp.cloudrts.net")) {
                response = RestAssured.given().relaxedHTTPSValidation().contentType(contentType).
                        headers(ExpectedDataProperties.getHeaderProps()).body(body).post(url);
            } else {
                response = RestAssured.given().contentType(contentType).
                        headers(ExpectedDataProperties.getHeaderProps()).body(body).post(url);
            }
            int statusCode = response.getStatusCode();
            CommonUtils.doJbehavereportConsolelogAndAssertion("### RESPONSE ###", response.asString(), (400 == statusCode || 412 == statusCode || 200 == statusCode));
            StepDetail.addDetail(CommonUtils.createString("__", 500), true);
        }
    }

}
