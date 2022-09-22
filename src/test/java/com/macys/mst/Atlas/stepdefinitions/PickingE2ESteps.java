package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.artemis.selenium.LocalDriverManager;
import com.macys.mst.artemis.selenium.WebDriverListener;
import com.macys.mst.wavefunction.utils.GetAndSetValues;
import com.macys.mst.Atlas.db.app.SQLQueriesMA;
import com.macys.mst.Atlas.utilmethods.PickingUtilImpl;
import com.macys.wms.DataGeneration.DataGeneration;
import com.macys.wms.db.DBMethods;
import com.macys.wms.db.XLSDBInitializer;
import com.macys.wms.systests.ConsumeToExceptionsSteps;
import com.macys.wms.systests.PickingSteps;
import com.macys.wms.systests.WMSASSteps;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PickingE2ESteps {

	public long TestNGThreadID = Thread.currentThread().getId();
	public static  WebDriver e2eDriver = LocalDriverManager.getInstance().getDriver();

	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = WebDriverListener.EnvMap.get(TestNGThreadID);
		WebDriverListener.EnvMap.put((Thread.currentThread().getId()), obj);
		WMSASSteps.setDriverForE2E(e2eDriver);
	}

	public static String waveNumber="";
	public static String workId="";
	
	
	@Given("$columnname is updated to $value in generic config for $configCode")
	public void updateGenericConfig(String column,String value,String configCode) {
		DataGeneration dataGen=new DataGeneration();
		dataGen.setGenConfigValues(column, value, configCode);
	}
	
	@Given("$columnname is updated  to $value in generic configuration for $configCode")
	public void updateGenericConfiguration(String column,String value,String configCode) {
		DataGeneration dataGen=new DataGeneration();
		dataGen.setGenConfigurationValues(column, value, configCode);
	}
	
	@Given("User complete picking for $transactionName transaction $paramtable")
	public void completePickByWorkId(String transactionName,ExamplesTable example) {
		PickingUtilImpl pickingUtil=new PickingUtilImpl();
		if(transactionName.equalsIgnoreCase("pick by work id")) {
			pickingUtil.completePickByWorkId(example);
		}else if(transactionName.equalsIgnoreCase("exception pick")) {
			pickingUtil.completeExceptionPick(example);
		}else if(transactionName.equalsIgnoreCase("BTY Pick to Cart")){
			pickingUtil.completeBeautyPickCart(example);
		}else if(transactionName.equalsIgnoreCase("Clear Pick Cart")){
			pickingUtil.completeClearPickCart(example);
		}
	}
	
	@Then("Login to handheld and select $menu menu")
	public void accessTalos(String menu) {
		WMSASSteps wmsStep=new WMSASSteps();
		wmsStep.loginToTalos();
		wmsStep.whenUserMenuOption(menu);
		wmsStep.menuOptionSuccessfullySelected(menu);
	}
	
	@Given("$callType: updating Other works for workGroup $workGrp to unique status")
	public void updateOtherWorksInWrkGrp(String callType,String workGrp){
		PickingSteps pickingStep=new PickingSteps();
		DataGeneration.proteusWave=true;
		WMSASSteps.pickWaveNumber=GetAndSetValues.waveNBr;
		pickingStep.updateOtherWorksInWrkGrp(callType, workGrp);
	}
	
	@Given("User click on Cancel Unassigned Pick for cancelling remaining works $paramtable")
	public void cancelUnassignedPicks(ExamplesTable example){
		PickingUtilImpl pickingUtil=new PickingUtilImpl();
		pickingUtil.cancellingUnassignedPicks(example);
	}
	
	@Given("Update Work Header status to $status")
	public void updateWork(String status){
		String workBatch = WMSASSteps.pickWaveNumber;
		if(workBatch.length()<15){
			workBatch += "001";
		}
		String sql = "update m_work_hdr set status='" + status
				+ "' where batch_number='" + workBatch + "' and status='30'";
		XLSDBInitializer.deleteOrUpdateDataBase(sql);
	}
	
	@Then("user scan tote and validate the update $exampleTable")
	public void consumeToExceptionPicking(ExamplesTable exampleTable){
		List<Map<String, String>> toteInfo=new ArrayList<Map<String,String>>();
		Map<String,String> exRows=exampleTable.getRow(0);
		toteInfo=DBMethods.getDBValuesAsListOfMaps(SQLQueriesMA.Talos.GETTOTEBASEDONSTATUSANDWORKBATCHSTATUS.replace("#TOTESTATUS#", "64").replace("#WORKBATCHSTATUS#", "90"), "");
		String workBatchNumber=toteInfo.get(0).get("WORK_BATCH_NUMBER");
		WMSASSteps wms=new WMSASSteps();
		wms.setPickWaveNumber(workBatchNumber.substring(0,workBatchNumber.length()-3));
		ConsumeToExceptionsSteps consumeEx=new ConsumeToExceptionsSteps();
		consumeEx.enterToteFromOurWorkBatch(Integer.parseInt(exRows.get("Status")),exRows.get("Message"));
		/*exRows.stream().forEach(m->{
			int status;
			String message;
			m.forEach((k,v)->{
				if(k.equalsIgnoreCase("status")){
					status=Integer.parseInt(v);
				}
				if(k.equalsIgnoreCase("message")){
					message=v;
				}
			});
			consumeEx.enterToteFromOurWorkBatch(status, message);
		});*/
		
	}
	
	@Given("user updates to_be_filled_qty to $qty")
	public void updateToBeFilled(String qty){
		Map<String,String> dbValue = new HashMap<String, String>();
		String query = SQLQueriesMA.getWorkDtlInfo.replace("#WORKBATCHNUMBER#", WMSASSteps.pickWaveNumber+"001");
		dbValue = DBMethods.getDBValuesInMap(query, "");
		String locnId = dbValue.get("");
		String itemId = dbValue.get("");
		query = SQLQueriesMA.UPDATETOBEFILLEDQTY.replace("#LOCATIONID#", locnId).replace("#ITEMID#", itemId).replace("$qty", qty);
        XLSDBInitializer.deleteOrUpdateDataBase(query);
	}
	
}
