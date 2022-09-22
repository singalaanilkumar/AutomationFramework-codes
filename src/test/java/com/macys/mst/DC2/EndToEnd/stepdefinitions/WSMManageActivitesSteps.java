package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLWsm;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODRRPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.WSMManageActivitiesPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class WSMManageActivitesSteps {

	public static WebDriver driver = LocalDriverManager.getInstance().getDriver();

	private WSMManageActivitiesPage wsmManageActivitiesPage = PageFactory.initElements(driver, WSMManageActivitiesPage.class);
	private StepsDataStore dataStorage = StepsDataStore.getInstance();
	private CommonUtils commonUtils = new CommonUtils();
	private ValidationUtil validationUtils = new ValidationUtil();
	PODRRPage drrPage = PageFactory.initElements(driver, PODRRPage.class);

	public long TestNGThreadID = Thread.currentThread().getId();
	private StepsContext stepsContext;
	BasePage basePage = new BasePage(driver);

	public WSMManageActivitesSteps(StepsContext stepsContext) {
		this.stepsContext = stepsContext;
	}

	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
		TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}

	@When("Validate WSM Activities on UI for $waveCount")
	public void validateManageActivities(String waveCount){
		String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");
	    wsmManageActivitiesPage.loginSCM();
		wsmManageActivitiesPage.clickNavOption("WSM");
		wsmManageActivitiesPage.clickNavOption("Manage Activities");
		wsmManageActivitiesPage.selectAttributeName("waveNumber");
		wsmManageActivitiesPage.selectAttributeValue(waveNumber);
		wsmManageActivitiesPage.clickSearchButton();  
		List<Map<String, String>> wsmActivitiesUI = wsmManageActivitiesPage.getGridElementsMapAllPages();
		log.info("wsmScreenvalues = {}",wsmActivitiesUI.toString()); 
		
		Map<String, Map<String, String>> activityIDAttributesUIMap = wsmActivitiesUI.stream().collect(Collectors.toMap(map -> map.get("Activity ID"), map -> map));
		log.info("activityIDAttributesUIMap: {}",activityIDAttributesUIMap);

		HashMap<String,String> testQueryParams = new HashMap<String,String>();
		testQueryParams.put("Wave Number",waveNumber);	
		List<Map<String, String>> wsmActivitiesDB = getResponseMapFromDB(testQueryParams);
		log.info("wsmDBvalues = {}",wsmActivitiesDB.toString());

		
		Map<String, Map<String, String>> activityIDAttributesDBMap = wsmActivitiesDB.stream().collect(Collectors.toMap(map -> map.get("Activity ID"), map -> map));
		log.info("activityIDAttributesDBMap: {}",activityIDAttributesDBMap);
		
    	CommonUtils.doJbehavereportConsolelogAndAssertion("Activity ID Count validated",
				"UI Activity IDs: "+activityIDAttributesUIMap.keySet()+" DB Activity IDs: "+activityIDAttributesDBMap.keySet(),
				activityIDAttributesUIMap.keySet().equals(activityIDAttributesDBMap.keySet()));
    	
    	for(String activityID_DB:activityIDAttributesDBMap.keySet()){
    			CommonUtils.doJbehavereportConsolelogAndAssertion("Activity Details for ID "+activityID_DB,
					" DB Details: " + activityIDAttributesDBMap.get(activityID_DB) + " UI Details: " + activityIDAttributesUIMap.get(activityID_DB),
					activityIDAttributesDBMap.get(activityID_DB).equals(activityIDAttributesUIMap.get(activityID_DB)));
    	}
	}


    @Then("On SCMUI Validate WSM Activities for $waveCount wave in $activityStatus Status")
    public void validateWSMActivitiesonSCMUI(String waveCount, String activityStatus) throws Exception {
        TimeUnit.SECONDS.sleep(10);
		HashMap<String,String> testQueryParams = new HashMap<String,String>();
		if (waveCount.contains("PO")) {
			wsmManageActivitiesPage.clickNavOption("WSM");
			wsmManageActivitiesPage.clickNavOption("Manage Activities");
			wsmManageActivitiesPage.clickSCMmenu();
			wsmManageActivitiesPage.selectStatus(activityStatus);
			wsmManageActivitiesPage.selectAttributeName("poNbr");
			String poNbr = (String) stepsContext.get(Context.PO_NBR.name());
			wsmManageActivitiesPage.selectAttributeValue(poNbr);
			testQueryParams.put("PO Number", poNbr);
		}
		else {
			wsmManageActivitiesPage.clickNavOption("WSM");
			wsmManageActivitiesPage.clickNavOption("Manage Activities");
			wsmManageActivitiesPage.clickSCMmenu();
			wsmManageActivitiesPage.selectStatus(activityStatus);
			wsmManageActivitiesPage.selectAttributeName("waveNumber");
			String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");
			wsmManageActivitiesPage.selectAttributeValue(waveNumber);
			testQueryParams.put("Wave Number", waveNumber);
		}
		wsmManageActivitiesPage.clickSearchButton();  
		List<Map<String, String>> wsmActivitiesUI = wsmManageActivitiesPage.getGridElementsMapAllPages();
		log.info("wsmScreenvalues = {}",wsmActivitiesUI.toString()); 
		
		Map<String, Map<String, String>> activityIDAttributesUIMap = wsmActivitiesUI.stream().collect(Collectors.toMap(map -> map.get("Activity ID"), map -> map));
		log.info("activityIDAttributesUIMap: {}",activityIDAttributesUIMap);

		testQueryParams.put("Status", activityStatus);
		List<Map<String, String>> wsmActivitiesDB = getResponseMapFromDB(testQueryParams);
		log.info("wsmDBvalues = {}",wsmActivitiesDB.toString());

		Map<String, Map<String, String>> activityIDAttributesDBMap = wsmActivitiesDB.stream().collect(Collectors.toMap(map -> map.get("Activity ID"), map -> map));
		log.info("activityIDAttributesDBMap: {}",activityIDAttributesDBMap);
		
    	CommonUtils.doJbehavereportConsolelogAndAssertion("WSM Manage Activities Screen Count validated",
				"UI Activity IDs: "+activityIDAttributesUIMap.keySet()+" DB Activity IDs: "+activityIDAttributesDBMap.keySet(),
				activityIDAttributesUIMap.keySet().equals(activityIDAttributesDBMap.keySet()));
    	
    	for(String activityID_DB:activityIDAttributesDBMap.keySet()){
    			CommonUtils.doJbehavereportConsolelogAndAssertion("Activity Details for ID "+activityID_DB,
					" DB Details: " + activityIDAttributesDBMap.get(activityID_DB) + " UI Details: " + activityIDAttributesUIMap.get(activityID_DB),
					activityIDAttributesDBMap.get(activityID_DB).equals(activityIDAttributesUIMap.get(activityID_DB)));  
    	
    	}
    }

	public List<Map<String, String>> getResponseMapFromDB(Map<String, String> queryParams) {
		try {
			String query = buildSearchQuery(queryParams);
			System.out.println(query);
		    List<Map<String, String>> queryResponseMap = DBMethods.getValuesFromDBAsStringListMap(query, "wsm");
			return queryResponseMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, String>> getDBResponseMap(Map<String, String> queryParams) {
		try {
			String wsmquery = buildSearchQuery(queryParams);
			System.out.println(wsmquery);
			String schema = "wsm";
			System.out.println(schema);
			List<Map<String, String>> wsmqueryResponseMap = DBMethods.getValuesFromDBAsStringListMap(wsmquery, schema);
			return wsmqueryResponseMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	@Then("Cancel WSM Activity in UI and Recreate for $waveCount")
	@Alias("Cancel WSM Activity in UI and Create activity for deleted activity for $waveCount")
	public void cancelWSMActivity(String waveCount) {		
		String deletedActivityId = wsmManageActivitiesPage.cancelFirstWSMActivity();
        dataStorage.getStoredData().put(waveCount+"AssignedActivityID", validateCancelledActivityandRecreate(waveCount,deletedActivityId));		
	}
		
	@Then("Unassign WSM Activity in UI for $waveCount wave")
	public void unAssignActivityinUI(String waveCount) {

		try {

			String assignedActivityID = (String) dataStorage.getStoredData().get(waveCount+"AssignedActivityID");
			log.info(assignedActivityID);
			wsmManageActivitiesPage.clickClearButton();
			wsmManageActivitiesPage.selectStatus("ASSIGNED");
			wsmManageActivitiesPage.selectActivityID(assignedActivityID);
			wsmManageActivitiesPage.clickSearchButton();
			wsmManageActivitiesPage.unAssignWSMActivity(assignedActivityID);

			StringBuilder queryArgs = new StringBuilder().append("id:").append(assignedActivityID);
			Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(commonUtils.getUrl("WSM.getActivities"), commonUtils.getParamsToMap(queryArgs.toString())).asResponse();

			if (200 == GETResponse.statusCode()) {
				JSONArray jsonArray = new JSONArray(GETResponse.asString());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					CommonUtils.doJbehavereportConsolelogAndAssertion("Activity unassigned as expected from WSM UI",
							assignedActivityID, "OPEN".equals(jsonObject.getString("status")));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			StepDetail.addDetail("Unable to validate the activity", false);
			Assert.fail("Unable to validate the Activity");
		}

	}

	public static String buildSearchQuery(Map<String, String> searchParams) {
		String searchQuery = SQLWsm.SEARCH_ACTIVITY;
		StringBuilder queryArgs = new StringBuilder();
		String searchValue;
		for (Map.Entry<String, String> searchParam : searchParams.entrySet()) {
			searchValue = CommonUtils.getQuotedString(searchParam.getValue());
			queryArgs.append("AND ");
			switch (searchParam.getKey()) {
			case "Activity ID":
				queryArgs.append("ACT.ID IN (").append(searchValue).append(") \n");
				break;
			case "Container ID/Barcode":
				queryArgs.append("ACT.CONTAINER IN (").append(searchValue).append(") \n");
				break;
			case "Container Type":
				queryArgs.append("ACT.CONTAINER_TYPE IN (").append(searchValue).append(") \n");
				break;
			case "Activity Type":
				queryArgs.append("ATYPE.DESCRIPTION IN (").append(searchValue).append(") \n");
				break;
			case "Status":
				queryArgs.append("STAT.STATUS_CD IN (").append(searchValue).append(") \n");
				break;
			case "SKU":
				queryArgs.append("ACT.UPC IN (").append(searchValue).append(") \n");
				break;
			case "PO Number":
				queryArgs.append("ATTR.PO_NBR IN (").append(searchValue).append(") \n");
				break;
			case "PO Receipt Number":
				queryArgs.append("ATTR.PO_RCPT_NBR IN (").append(searchValue).append(") \n");
				break;
			case "Wave Number":
				queryArgs.append("ATTR.WAVE_NUMBER IN (").append(searchValue).append(") \n");
				break;
			case "Attribute Value":
				queryArgs.append("ATTR.SEQUENCE IN (").append(searchValue).append(") \n");
				break;
			case "subType":
				queryArgs.append("ATTR.SUB_TYPE IN (").append(searchValue).append(") \n");
				break;
			case "pid":
				queryArgs.append("ATTR.PID IN (").append(searchValue).append(") \n");
				break;
			case "zone":
				queryArgs.append("ATTR.ZONE IN (").append(searchValue).append(") \n");
				break;
			case "Assigned User":
				queryArgs.append("LIFE.ASSIGNED_TO (").append(searchValue).append(") \n");
				break;
			default:
				throw new NoSuchElementException("No search key found");
			}
		}
		return String.format(searchQuery, queryArgs.toString());
	}
	
	public String validateCancelledActivityandRecreate(String waveCount,String cancelledActivityID) {
		
		try
		{
			String Endpoint = commonUtils.getUrl("WSM.getActivities");
			String waveNumber = (String) dataStorage.getStoredData().get(waveCount + "Number");
	        StringBuilder queryArgs = new StringBuilder();	
			queryArgs.append("id:");
			queryArgs.append(cancelledActivityID);
	        String GETQueryParams = queryArgs.toString();
	        JSONArray listOfActivityToCreate = new JSONArray();
	        Map<String, String> processedGetQP = commonUtils.getParamsToMap(GETQueryParams);

	        Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(Endpoint, processedGetQP).asResponse();
	       
	        if (200 == GETResponse.statusCode()) {
	            JSONArray jsonArray = new JSONArray(GETResponse.asString());
	            for (int i = 0; i < jsonArray.length(); i++) {
	                JSONObject jsonObject = jsonArray.getJSONObject(i);
	                Assert.assertTrue("Activity is not deleted", "DELETED".equals(jsonObject.getString("status")));
	                CommonUtils.doJbehavereportConsolelogAndAssertion("Activity cancelled using Cancel button",cancelledActivityID,"DELETED".equals(jsonObject.getString("status")));
	                jsonObject.remove("id");
	                jsonObject.remove("actor");
	                jsonObject.remove("status");
	                jsonObject.put("status", "OPEN");
	                listOfActivityToCreate.put(jsonObject);
                }
            }
	        
	        if (listOfActivityToCreate.length() != 0) {
	            Response postActivityResponse = WhmRestCoreAutomationUtils.postRequestResponse(Endpoint, listOfActivityToCreate.toString()).asResponse();
	            if (postActivityResponse != null) {
	                Assert.assertTrue("Activities " + listOfActivityToCreate + " not Created",validationUtils.validateResponseStatusCode(postActivityResponse, 201));
	                CommonUtils.doJbehavereportConsolelogAndAssertion("Created Inventory",
	                        "Create Activity Endpoint: " + Endpoint + "\n"
	                                + "Create Activity Request: " + listOfActivityToCreate.toString() + "\n"
	                                + "Create Activity Response Statuscode: " + postActivityResponse.getStatusCode(),
	                        validationUtils.validateResponseStatusCode(postActivityResponse, 201));
	            }
	        }	            
	        
	        StringBuilder waveNumqueryArgs = new StringBuilder();
	        waveNumqueryArgs.append("waveNumber:");
	        waveNumqueryArgs.append(waveNumber);
	        waveNumqueryArgs.append(",sortOrder:desc,sortBy:createdTime");
		    String GETWaveNumQueryParams = waveNumqueryArgs.toString();
		    JSONArray ActivityToAssign = new JSONArray();
	        Map<String, String> processedGetByWaveQP = commonUtils.getParamsToMap(GETWaveNumQueryParams);
	        Response GETByWaveNumResponse = WhmRestCoreAutomationUtils.getRequestResponse(Endpoint, processedGetByWaveQP).asResponse();
	        
	        if (200 == GETByWaveNumResponse.statusCode()) {
	            JSONArray jsonArray = new JSONArray(GETByWaveNumResponse.asString());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                jsonObject.remove("actor");
                jsonObject.put("actor", "B0$WHM2SUPERUSER");
                jsonObject.remove("status");
                jsonObject.put("status", "ASSIGNED");
                ActivityToAssign.put(jsonObject);
            }
	        
	        String assignedActivityID =  String.valueOf(ActivityToAssign.getJSONObject(0).get("id"));
	        if (ActivityToAssign.length() != 0) {
	            Response putResponse = WhmRestCoreAutomationUtils.putRequestResponse(Endpoint, ActivityToAssign.toString()).asResponse();
	            if (putResponse != null) {
	                Assert.assertTrue("Activities " + ActivityToAssign + " not Unassigned", validationUtils.validateResponseStatusCode(putResponse, 201));
	                CommonUtils.doJbehavereportConsolelogAndAssertion("Activities Assigned ",
	                        new JsonPath(ActivityToAssign.toString()).getList("id", Integer.class).toString(),
	                        validationUtils.validateResponseStatusCode(putResponse, 201));
	            }
	        }
	        
	        return assignedActivityID;
	        
	   } catch (Exception e) {
		   e.printStackTrace();
		   CommonUtils.doJbehavereportConsolelogAndAssertion("Unable to validate the activity",cancelledActivityID,false);
		   return "";
	  }
	}
}