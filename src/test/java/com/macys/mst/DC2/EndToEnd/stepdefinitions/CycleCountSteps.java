package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.testng.Assert;

import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.HoldAndFlowSQL;
import com.macys.mst.DC2.EndToEnd.model.ContainerRelation;
import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.IncorrectDataException;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import com.macys.mst.whm.coreautomation.utils.ApiResponse;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CycleCountSteps {

    public long TestNGThreadID = Thread.currentThread().getId();
    private CommonUtils commonUtils = new CommonUtils();
    private ValidationUtil validationUtils = new ValidationUtil();
    List<String> validCycleCountStatuses = Arrays.asList(new String[]{"VSC","PTW"});
    
    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }   
    
    @Given("inventory is cleared for the given locations $values")
    public void findAndDeleteInventory(ExamplesTable values) {
        if (values.getRows().size() > 0) {
            for (Map<String, String> row : values.getRows()) {
                String GETCallEndpoint = commonUtils.getUrl(row.get("getRequestUrl"));
                String DELETECallEndpoint = commonUtils.getUrl(row.get("deleteRequestUrl"));
                Map<String, String> processedGetQP = commonUtils.getParamsToMap(row.get("GETQueryParams"));
                Map<String, String> processedDeleteQP = commonUtils.getParamsToMap(row.get("DELETEQueryParams"));

                Response GETResponse = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                if (200 == GETResponse.statusCode()) {
                    List<String> listOfContainerToDelete = new JsonPath(GETResponse.asString()).getList("inventorySnapshotList.container");

                    for (String barcode : listOfContainerToDelete) {
                        Response deleteResponse = WhmRestCoreAutomationUtils.deleteRequestResponse(DELETECallEndpoint.replace("#sourceBarcode", barcode), processedDeleteQP).asResponse();
                        Assert.assertTrue(validationUtils.validateResponseStatusCode(deleteResponse, 200) || validationUtils.validateResponseStatusCode(deleteResponse, 204), barcode + " not DELETED");
                    }

                    Response getRespAfterDelete = WhmRestCoreAutomationUtils.getRequestResponse(GETCallEndpoint, processedGetQP).asResponse();
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Deleted all existing Inventory ",
                            listOfContainerToDelete.toString(),
                            validationUtils.validateResponseStatusCode(getRespAfterDelete, 204));
                } else {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("No Inventory available or Unable to get inventory",
                            "NA",
                            validationUtils.validateResponseStatusCode(GETResponse, 204));
                }
            }
        } else {
            throw new IncorrectDataException("Require atleast row of data");
        }
    }

    // Method validates containers that are modified/updated (Containers is PTW, VSC etc)
    public void validateUpdatedContainersAfterCC(List<InventoryContainer> containerList, String scannedLocation) {
    	containerList.forEach(inventoryContainer -> {
    		String containerBarcode = inventoryContainer.getContainer().getBarCode();
    		String containerType = inventoryContainer.getContainer().getContainerType();
    		String systemLocationForContainer = "";
    		if(inventoryContainer.getContainer().getContainerRelationshipList().size()!=0)
    			systemLocationForContainer = inventoryContainer.getContainer().getContainerRelationshipList().get(0).getParentContainer(); 
    		InventoryContainer updatedInventoryContainer = CommonUtils.getInventory(containerBarcode);
    		if(scannedLocation.equalsIgnoreCase(systemLocationForContainer)){
    			String updatedParentContainer = updatedInventoryContainer.getContainer().getContainerRelationshipList().get(0).getParentContainer();
    			String updatedStatus = updatedInventoryContainer.getContainer().getContainerStatusCode();
    			List<String> conditionCodes = updatedInventoryContainer.getInventorySnapshotList().get(0).getConditionCodeList();
    			if(conditionCodes!=null){
    				CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" has no LW condition after cyclecount", updatedInventoryContainer.toString(), !conditionCodes.contains("LW"));
    			}else{
    				CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" has no LW condition after cyclecount", updatedInventoryContainer.toString(), true);
    			}
    			CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" status as expected after cyclecount", updatedInventoryContainer.toString(), "PTW".equalsIgnoreCase(updatedStatus));
    			CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" Location as expected after cyclecount", updatedInventoryContainer.toString(), scannedLocation.equalsIgnoreCase(updatedParentContainer));
    			
    			Map<Object, Object> dbResults = null;
    	        try {
    	            String cyclecount_UI_query = HoldAndFlowSQL.cycle_count_ui_data.replace("#CHILD", containerBarcode);
    	            log.info("getcyclecount details query: {}", cyclecount_UI_query);
    	            dbResults = DBMethods.getValuesFromDBAsStringList(cyclecount_UI_query, "receiving").get(0);

                    Object AvailableStatus = dbResults.get("AVAILABLE");
                    if(AvailableStatus !=null){
                        AvailableStatus = AvailableStatus.toString();
                    }
                    Object SYSTEM_PARENT = dbResults.get("SYSTEM_PARENT");
                    if(SYSTEM_PARENT !=null){
                        SYSTEM_PARENT = SYSTEM_PARENT.toString();
                    }
                    Object PARENT = dbResults.get("PARENT");
                    if(PARENT !=null){
                        PARENT = PARENT.toString();
                    }
                    Object REASON = dbResults.get("REASON");
                    if(REASON !=null){
                        REASON = REASON.toString();
                    }
                    Object CYCLE_COUNT_STATUS = dbResults.get("CYCLE_COUNT_STATUS");
                    if(CYCLE_COUNT_STATUS !=null){
                        CYCLE_COUNT_STATUS = CYCLE_COUNT_STATUS.toString();
                    }
                    Assert.assertEquals(PARENT,scannedLocation,  "Scanned Location and Container Location are same But the DB value is not correct");
                    Assert.assertEquals(AvailableStatus,"Y",  "Scanned Location and Container Location are same But the DB value is not correct");
                   	Assert.assertNull(SYSTEM_PARENT, "Scanned Location and Container Location are same But the DB SYSTEM_PARENT value is showing " + SYSTEM_PARENT);
                   	Assert.assertNull(REASON, "Scanned Location and Container Location are same But the DB DB Reason Code value is showing " + REASON);
                    Assert.assertEquals(CYCLE_COUNT_STATUS,"C",  "Container status should be Completed But the DB value is not correct");
                    ApiResponse locresponse = HAFSteps.getlocationDetails(updatedParentContainer);
                    if (locresponse.getStatusCode() != 200) {
                    	Assert.assertTrue(false,"Container has a invalid location");
                    }
                    CommonUtils.doJbehavereportConsolelogAndAssertion("The DB values of the Valid container with correct location: " + dbResults.get("CHILD").toString(), "AvailableStatus :" + AvailableStatus + "\t SYSTEM_PARENT :" + SYSTEM_PARENT + "\t REASON :" + REASON + "\t CYCLE_COUNT_STATUS :" + CYCLE_COUNT_STATUS, true);                    
    	        }catch(Exception e){
    	        	e.printStackTrace();
    	        }
    			
    		}else{
    			String updatedParentContainer = updatedInventoryContainer.getContainer().getContainerRelationshipList().get(0).getParentContainer();
    			String updatedStatus = updatedInventoryContainer.getContainer().getContainerStatusCode();
    			List<String> conditionCodes = updatedInventoryContainer.getInventorySnapshotList().get(0).getConditionCodeList();
    			if(conditionCodes!=null){
    				CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" has no LW condition after cyclecount", updatedInventoryContainer.toString(), !conditionCodes.contains("LW"));
    			}else{
    				CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" has no LW condition after cyclecount", updatedInventoryContainer.toString(), true);
    			}
    			CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" status as expected after cyclecount", updatedInventoryContainer.toString(), "PTW".equalsIgnoreCase(updatedStatus));
    			CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" Location as expected after cyclecount", updatedInventoryContainer.toString(), scannedLocation.equalsIgnoreCase(updatedParentContainer));
    			    			
    			Map<Object, Object> dbResults = null;
    	        try {
    	            String cyclecount_UI_query = HoldAndFlowSQL.cycle_count_ui_data.replace("#CHILD", containerBarcode);
    	            log.info("getcyclecount details query: {}", cyclecount_UI_query);
    	            dbResults = DBMethods.getValuesFromDBAsStringList(cyclecount_UI_query, "receiving").get(0);

                    Object AvailableStatus = dbResults.get("AVAILABLE");
                    if(AvailableStatus !=null){
                        AvailableStatus = AvailableStatus.toString();
                    }
                    Object SYSTEM_PARENT = dbResults.get("SYSTEM_PARENT");
                    if(SYSTEM_PARENT !=null){
                        SYSTEM_PARENT = SYSTEM_PARENT.toString();
                    }
                    Object PARENT = dbResults.get("PARENT");
                    if(PARENT !=null){
                        PARENT = PARENT.toString();
                    }
                    Object REASON = dbResults.get("REASON");
                    if(REASON !=null){
                        REASON = REASON.toString();
                    }
                    Object CYCLE_COUNT_STATUS = dbResults.get("CYCLE_COUNT_STATUS");
                    if(CYCLE_COUNT_STATUS !=null){
                        CYCLE_COUNT_STATUS = CYCLE_COUNT_STATUS.toString();
                    }
                    Assert.assertEquals(PARENT,scannedLocation,  "Scanned Location and Container Location are same But the DB value is not correct");
                    Assert.assertEquals(AvailableStatus,"Y",  "Scanned Location and Container Location are same But the DB value is not correct");
                    Assert.assertEquals(SYSTEM_PARENT,scannedLocation, "Scanned Location and Container Location are different But the DB SYSTEM_PARENT value is showing as " + SYSTEM_PARENT);
                	Assert.assertEquals(REASON,"Location Updated", "Scanned Location and Container Location are different But the DB Reason Code is showing as " + REASON);
                    Assert.assertEquals(CYCLE_COUNT_STATUS,"C",  "Container status should be Completed But the DB value is not correct");
                    ApiResponse locresponse = HAFSteps.getlocationDetails(updatedParentContainer);
                    if (locresponse.getStatusCode() != 200) {
                    	Assert.assertTrue(false,"Container has a invalid location");
                    }
                    CommonUtils.doJbehavereportConsolelogAndAssertion("The DB values of the Valid container with correct location: " + dbResults.get("CHILD").toString(), "AvailableStatus :" + AvailableStatus + "\t SYSTEM_PARENT :" + SYSTEM_PARENT + "\t REASON :" + REASON + "\t CYCLE_COUNT_STATUS :" + CYCLE_COUNT_STATUS, true);
                }catch(Exception e){
    	        	e.printStackTrace();
    	        }
    			
    		}
    		// Update orginalInventoryContainer details to expected state and compare with latestInventoryContainer details
        	inventoryContainer.getInventorySnapshotList().get(0).setConditionCodeList(null);
        	if(StringUtils.isNotBlank(systemLocationForContainer))
        		inventoryContainer.getContainer().getContainerRelationshipList().get(0).setParentContainer(scannedLocation);
        	else{
        		ContainerRelation containerRelation = new ContainerRelation();
        		containerRelation.setChildContainer(containerBarcode);
        		containerRelation.setChildContainerType(containerType);
        		containerRelation.setParentContainer(scannedLocation);
        		containerRelation.setParentContainerType("LCN");
        		containerRelation.setDepth(1);
        		List<ContainerRelation> containerRelationList = new ArrayList<>();
        		containerRelationList.add(containerRelation);
        		inventoryContainer.getContainer().setContainerRelationshipList(containerRelationList);
        	}
        	inventoryContainer.getContainer().setContainerStatusCode("PTW");
        	CommonUtils.doJbehavereportConsolelogAndAssertion("Container Details are updated as expected for "+containerBarcode ,updatedInventoryContainer.toString(), inventoryContainer.equals(updatedInventoryContainer));
    	});
    }
	
	 // Method validates container that are not modified after Cycle count
    public void validateUnmodifiedContainersAfterCC(List<InventoryContainer> containerList, String scannedLocation) {
    	containerList.forEach(inventoryContainer -> {
    		String containerBarcode = inventoryContainer.getContainer().getBarCode();
			InventoryContainer afterCCInventoryContainer = CommonUtils.getInventory(containerBarcode);
			String systemLocationForContainer = "";
    		if(inventoryContainer.getContainer().getContainerRelationshipList().size()!=0)
    			systemLocationForContainer = inventoryContainer.getContainer().getContainerRelationshipList().get(0).getParentContainer(); 
    		
    		Map<Object, Object> dbResults = null;
	        try {
	            String cyclecount_UI_query = HoldAndFlowSQL.cycle_count_ui_data.replace("#CHILD", containerBarcode);
	            log.info("getcyclecount details query: {}", cyclecount_UI_query);
	            dbResults = DBMethods.getValuesFromDBAsStringList(cyclecount_UI_query, "receiving").get(0);

                Object AvailableStatus = dbResults.get("AVAILABLE");
                if(AvailableStatus !=null){
                    AvailableStatus = AvailableStatus.toString();
                }
                Object SYSTEM_PARENT = dbResults.get("SYSTEM_PARENT");
                if(SYSTEM_PARENT !=null){
                    SYSTEM_PARENT = SYSTEM_PARENT.toString();
                }
                Object PARENT = dbResults.get("PARENT");
                if(PARENT !=null){
                    PARENT = PARENT.toString();
                }
                Object REASON = dbResults.get("REASON");
                if(REASON !=null){
                    REASON = REASON.toString();
                }
                Object CYCLE_COUNT_STATUS = dbResults.get("CYCLE_COUNT_STATUS");
                if(CYCLE_COUNT_STATUS !=null){
                    CYCLE_COUNT_STATUS = CYCLE_COUNT_STATUS.toString();
                }
                Assert.assertEquals(PARENT,scannedLocation,  "Scanned Location and Container Location are same But the DB value is not correct");
        		if(scannedLocation.equalsIgnoreCase(systemLocationForContainer)){
                	Assert.assertEquals(AvailableStatus,"Y",  "Scanned Location and Container Location are same But the DB value is not correct");
                	Assert.assertNull(REASON, "Scanned Location and Container Location are same But the DB Reason Code value is showing " + REASON);
                	Assert.assertNull(SYSTEM_PARENT, "Scanned Location and Container Location are same But the DB SYSTEM_PARENT value is showing " + SYSTEM_PARENT);
                }else{
                	Assert.assertEquals(AvailableStatus,"N",  "Scanned Location and Container Location are same But the DB value is not correct");
                	Assert.assertEquals(REASON,"Incorrect Location", "Scanned Location and Container Location are same But the DB Reason Code value is showing " + REASON);
                	if(!"".equals(systemLocationForContainer))
                		Assert.assertEquals(SYSTEM_PARENT,systemLocationForContainer, "Scanned Location and Container Location are same But the DB SYSTEM_PARENT value is showing " + SYSTEM_PARENT);
                	else
                		Assert.assertNull(SYSTEM_PARENT, "Scanned Location and Container Location are same But the DB SYSTEM_PARENT value is showing " + SYSTEM_PARENT);
                 }
               	Assert.assertEquals(CYCLE_COUNT_STATUS,"C",  "Container status should be Completed But the DB value is not correct");
                CommonUtils.doJbehavereportConsolelogAndAssertion("The DB values of the Valid container with correct location: " + dbResults.get("CHILD").toString(), "AvailableStatus :" + AvailableStatus + "\t SYSTEM_PARENT :" + SYSTEM_PARENT + "\t REASON :" + REASON + "\t CYCLE_COUNT_STATUS :" + CYCLE_COUNT_STATUS, true);                    
	        }catch(Exception e){
	        	e.printStackTrace();
	        }	        
			CommonUtils.doJbehavereportConsolelogAndAssertion("Container Details are unmodified as expected for "+containerBarcode ,afterCCInventoryContainer.toString(), inventoryContainer.equals(afterCCInventoryContainer));
    	});
    }
    
    
    // Method validates container that are updated as Lost in Warehouse(LW)
    public void validateLWContainersAfterCC(List<InventoryContainer> containerList, String scannedLocation) {
    	containerList.forEach(inventoryContainer -> {   		
    		String containerBarcode = inventoryContainer.getContainer().getBarCode();
			String systemParentContainer = inventoryContainer.getContainer().getContainerRelationshipList().get(0).getParentContainer();
			InventoryContainer afterCCInventoryContainer = CommonUtils.getInventory(containerBarcode);
			
    		if(validCycleCountStatuses.contains(inventoryContainer.getContainer().getContainerStatusCode())){
    			String updatedParentContainer = afterCCInventoryContainer.getContainer().getContainerRelationshipList().get(0).getParentContainer();
    			CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" has updated Location after cyclecount", afterCCInventoryContainer.toString(), "IC02A001".equalsIgnoreCase(updatedParentContainer));
    			List<String> conditionCodes = afterCCInventoryContainer.getInventorySnapshotList().get(0).getConditionCodeList();
    			if(conditionCodes!=null){
    				CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" has LW condition after cyclecount", afterCCInventoryContainer.toString(), conditionCodes.contains("LW"));
    			}else{
    				CommonUtils.doJbehavereportConsolelogAndAssertion("Container "+containerBarcode+" has LW condition after cyclecount", afterCCInventoryContainer.toString(), false);
    			}
    			// Update orginalInventoryContainer details to expected state and compare with latestInventoryContainer details
                List<String> ccList = inventoryContainer.getInventorySnapshotList().get(0).getConditionCodeList();
            	if(ccList==null || ccList.size()==0){
            		ccList = new ArrayList<>();
            	}
          		ccList.add("LW");
            	inventoryContainer.getInventorySnapshotList().get(0).setConditionCodeList(ccList);
            	inventoryContainer.getContainer().getContainerRelationshipList().get(0).setParentContainer("IC02A001");
            	inventoryContainer.getInventorySnapshotList().get(0).setStatusCode("NVL");
            	inventoryContainer.getInventorySnapshotList().get(0).setStatusDescription("Not Available");
        	}
			Map<Object, Object> dbResults = null;
	        try {
	            String cyclecount_UI_query = HoldAndFlowSQL.cycle_count_ui_data.replace("#CHILD", containerBarcode);
	            log.info("getcyclecount details query: {}", cyclecount_UI_query);
	            dbResults = DBMethods.getValuesFromDBAsStringList(cyclecount_UI_query, "receiving").get(0);

                Object AvailableStatus = dbResults.get("AVAILABLE");
                if(AvailableStatus !=null){
                    AvailableStatus = AvailableStatus.toString();
                }
                Object SYSTEM_PARENT = dbResults.get("SYSTEM_PARENT");
                if(SYSTEM_PARENT !=null){
                    SYSTEM_PARENT = SYSTEM_PARENT.toString();
                }
                Object PARENT = dbResults.get("PARENT");
                if(PARENT !=null){
                    PARENT = PARENT.toString();
                }
                Object REASON = dbResults.get("REASON");
                if(REASON !=null){
                    REASON = REASON.toString();
                }
                Object CYCLE_COUNT_STATUS = dbResults.get("CYCLE_COUNT_STATUS");
                if(CYCLE_COUNT_STATUS !=null){
                    CYCLE_COUNT_STATUS = CYCLE_COUNT_STATUS.toString();
                }
                Assert.assertEquals(PARENT,scannedLocation,  "Scanned Location and Container Location are same But the DB value is not correct");
                Assert.assertEquals(AvailableStatus,"N",  "Scanned Location and Container Location are same But the DB value is not correct");
                if(validCycleCountStatuses.contains(inventoryContainer.getContainer().getContainerStatusCode()))
        			Assert.assertEquals(SYSTEM_PARENT,"IC02A001", "Scanned Location and Container Location are same But the DB SYSTEM_PARENT value is showing " + SYSTEM_PARENT);
    			else
    				Assert.assertEquals(SYSTEM_PARENT,systemParentContainer, "Scanned Location and Container Location are same But the DB SYSTEM_PARENT value is showing " + SYSTEM_PARENT);
        		Assert.assertEquals(REASON,"Not In Location", "Scanned Location and Container Location are same But the DB DB Reason Code value is showing " + REASON);
                Assert.assertEquals(CYCLE_COUNT_STATUS,"C",  "Container status should be Completed But the DB value is not correct");
                CommonUtils.doJbehavereportConsolelogAndAssertion("The DB values of the Valid container with correct location: " + dbResults.get("CHILD").toString(), "AvailableStatus :" + AvailableStatus + "\t SYSTEM_PARENT :" + SYSTEM_PARENT + "\t REASON :" + REASON + "\t CYCLE_COUNT_STATUS :" + CYCLE_COUNT_STATUS, true);
        		
	        }catch(Exception e){
    	        	e.printStackTrace();
	        }	        
			CommonUtils.doJbehavereportConsolelogAndAssertion("Container Details are updated as expected for "+containerBarcode ,afterCCInventoryContainer.toString(), inventoryContainer.equals(afterCCInventoryContainer));
    	});
    }
    
    public void validateContainersNotInventoried(List<String> containerList, String scannedLocation){
    	containerList.forEach(containerBarcode -> {
    			Map<Object, Object> dbResults = null;
    	        try {
    	            String cyclecount_UI_query = HoldAndFlowSQL.cycle_count_ui_data.replace("#CHILD", containerBarcode);
    	            log.info("getcyclecount details query: {}", cyclecount_UI_query);
    	            dbResults = DBMethods.getValuesFromDBAsStringList(cyclecount_UI_query, "receiving").get(0);

                    Object AvailableStatus = dbResults.get("AVAILABLE");
                    if(AvailableStatus !=null){
                        AvailableStatus = AvailableStatus.toString();
                    }
                    Object SYSTEM_PARENT = dbResults.get("SYSTEM_PARENT");
                    if(SYSTEM_PARENT !=null){
                        SYSTEM_PARENT = SYSTEM_PARENT.toString();
                    }
                    Object PARENT = dbResults.get("PARENT");
                    if(PARENT !=null){
                        PARENT = PARENT.toString();
                    }
                    Object REASON = dbResults.get("REASON");
                    if(REASON !=null){
                        REASON = REASON.toString();
                    }
                    Object CYCLE_COUNT_STATUS = dbResults.get("CYCLE_COUNT_STATUS");
                    if(CYCLE_COUNT_STATUS !=null){
                        CYCLE_COUNT_STATUS = CYCLE_COUNT_STATUS.toString();
                    }
                    Assert.assertEquals(PARENT,scannedLocation,  "Scanned Location and Container Location are same But the DB value is not correct");
                    Assert.assertEquals(AvailableStatus,"N",  "Scanned Location and Container Location are same But the DB value is not correct");
                    Assert.assertNull(SYSTEM_PARENT,"Scanned Location and Container Location are different But the DB SYSTEM_PARENT value is showing as " + SYSTEM_PARENT);
                	Assert.assertEquals(REASON,"No Inventory", "Scanned Location and Container Location are different But the DB Reason Code is showing as " + REASON);
                    Assert.assertEquals(CYCLE_COUNT_STATUS,"C",  "Container status should be Completed But the DB value is not correct");
                    CommonUtils.doJbehavereportConsolelogAndAssertion("The DB values of the Valid container with correct location: " + dbResults.get("CHILD").toString(), "AvailableStatus :" + AvailableStatus + "\t SYSTEM_PARENT :" + SYSTEM_PARENT + "\t REASON :" + REASON + "\t CYCLE_COUNT_STATUS :" + CYCLE_COUNT_STATUS, true);
                }catch(Exception e){
    	        	e.printStackTrace();
    	        }    
    	});
    }
}
