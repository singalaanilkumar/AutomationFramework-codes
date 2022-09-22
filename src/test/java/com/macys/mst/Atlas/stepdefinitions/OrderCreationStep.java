package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.LocalDriverManager;
import com.macys.mst.artemis.selenium.WebDriverListener;
import com.macys.mst.talos.datasetup.DataCreateModule;
import com.macys.mst.wavefunction.datasetup.DataGeneration;
import com.macys.mst.wavefunction.db.app.DBMethods;
import com.macys.mst.wavefunction.pageobjects.RunWorkPlan;
import com.macys.mst.wavefunction.stepdefinitions.*;
import com.macys.mst.wavefunction.utils.GetAndSetValues;
import com.macys.mst.Atlas.db.app.SQLQueriesMA;
import com.macys.mst.Atlas.utilmethods.GenericConfigUtils;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class OrderCreationStep {
	private static Logger LOGGER = Logger.getLogger(OrderCreationStep.class.getName());
	public long TestNGThreadID = Thread.currentThread().getId();

	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = WebDriverListener.EnvMap.get(TestNGThreadID);
		WebDriverListener.EnvMap.put((Thread.currentThread().getId())	, obj);
		WMSASSteps.setDriverForE2E(e2eDriver);

	}
	public static  WebDriver e2eDriver = LocalDriverManager.getInstance().getDriver();
	
	public static List<Map<String,String>> osmOrderList;
	public static List<Map<String,String>> mstOrderList;

	@BeforeScenario(uponType=ScenarioType.ANY)
	@AfterScenario(uponType=ScenarioType.ANY)
	public void systemTime(){
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        StepDetail.addDetail("Starting time: " + sdf.format(cal.getTime()),true);
	}
	
	@Given("Get location details for prodline $prodlines with aisles as $noOfAisles with $locnNumber")
	public void getLocation(String prodlines, String noOfAisles, String locnNumber) throws Exception{
		DataGeneration dataGen=new DataGeneration();
		dataGen.getaisle(prodlines, noOfAisles, locnNumber);
	}
	
	@Given("reset the data for wave function for ITEMS $itemaction,Orders $orderaction,Waves $waveAction")
	public void resetData(String itemaction, String orderaction, String waveAction){
		WaveFunction dataGen=new WaveFunction();
		dataGen.resetdata(itemaction, orderaction, waveAction);
	}
	
	//Reset method for all the orders in scenario
	//Given HAF: Reset the data for wave function for ITEMS N,Orders Y,Waves Y
	@Given("HAF: Reset the data for wave function for ITEMS $itemaction,Orders $orderaction,Waves $waveAction")
	public void clearUpOrders(String itemaction, String orderaction, String waveAction)
	{
		WaveFunction dataGen=new WaveFunction();
		HashMap<String, String> ordernumber = GetAndSetValues.ordernumbers;
		for(int index=1;index<=ordernumber.size();index++)
		{
			System.setProperty("testData.orderPrefix", ordernumber.get("ORD"+index));
			dataGen.resetdata(itemaction, orderaction, waveAction);
		}
	}
		
	@Given("data is created for wave by posting orders using below parameter $paramtable")
	public void orderCreation(ExamplesTable example) throws Exception {
		DataGeneration dataGen = new DataGeneration();
		dataGen.createdataByPostingOrders(example);
	}
	
	@When("wave planner selects $waveType wave with $Exclude singles and SOT checkbox is $unchecked and $runOrPreview wave with Pickdensity as $pickCheck and date from $fromDate to $todate")
	public void runWave(String waveType, String ordersType, String sotCheck, String runOrPreview, String pickCheck,
			String fromDate, String todate){
		RunWorkPlanSteps runWorkPln=new RunWorkPlanSteps();
		System.out.println(e2eDriver);
		runWorkPln.runWave(waveType, ordersType, sotCheck, runOrPreview, pickCheck, fromDate, todate);
	}
	
	@Then("verify that the order $orders is selected for $action wave for batch number $batchseq")
	public void verifyUpcDensity(String orders, String action, int batchseq) throws SQLException, InterruptedException {
		VerificationOfValues verfication=new VerificationOfValues();
		try {
			verfication.verifyUpcDensity(orders, action, batchseq);
		}catch(Exception e){LOGGER.info("Exception : "+e.getMessage());}
	}
	
	@Then("verify that table $table is update with coloumns $coloumns as values $values for batch number $batchseq")
	public void ShipWaveVerification(String table,String coloumns,String values,int batchseq) throws SQLException, InterruptedException{
		VerificationOfValues verfication=new VerificationOfValues();
		try {
		verfication.ShipWaveVerification(table, coloumns, values, batchseq);
		}catch(Exception e){LOGGER.info("Exception : "+e.getMessage());}

	}
		
	@Given("Generic configuration set for $waveType in New Storage Type Mix Configuration with below parameters $parametersTable")
	public void setGenConfigMixConfiguration(String waveType, ExamplesTable parametersTable) throws Exception{
		RuleSimplification ruleSimpl=new RuleSimplification();
		ruleSimpl.setGenConfigMixConfiguration(waveType, parametersTable);
	}
	
	@Given("release wave for logical group $examplesTable")
        public void releaseWave(ExamplesTable examplesTable) throws Exception{
		//TimeUnit.SECONDS.sleep(120);
		for (Map<String, String> row : examplesTable.getRows()) {
			WMSASSteps wms=new WMSASSteps();
			wms.setPickWaveNumber(GetAndSetValues.waveNBr);
			wms.runparamWave(row.get("releaseParam").trim());
			//GetAndSetValues.waveNBr=WMSASSteps.getPickWaveNumber();//added wave number
			LOGGER.info("GetAndSetValues.waveNBr :"+GetAndSetValues.waveNBr);
			LOGGER.info("DataCreateModule.outputEntity.getDtWaveNumber() :"+DataCreateModule.outputEntity.getDtWaveNumber());
			if(null==DataCreateModule.outputEntity.getDtWaveNumber()||!DataCreateModule.outputEntity.getDtWaveNumber().equals(GetAndSetValues.waveNBr))
			{
				DataCreateModule.outputEntity.setDtWaveNumber(GetAndSetValues.waveNBr);
                                com.macys.wms.util.TalosUtils.setWorkBatchNumber(GetAndSetValues.waveNBr+"001");
				com.macys.wms.util.TalosUtils.workBatchNumber=GetAndSetValues.waveNBr+"001";
			}
		}
	}
	
	@Given("Generic configuration set for $waveType in New Wave Type Configuration with below parameters $parametersTable")
	public void setGenConfigWaveTypeConfiguration(String waveType,ExamplesTable parametersTable) throws Exception{
		RuleSimplification ruleSimpl=new RuleSimplification();
		ruleSimpl.setGenConfigWaveTypeConfiguration(waveType, parametersTable);
	}
	
	@When("The Wave planner selects $waveType wave with minRWF, maxRWF as $maxqty , $maxorders and  $Exclude singles and SOT checkbox is $unchecked and $runOrPreview wave with Pickdensity as $pickCheck and date from $fromDate to $todate")
	public void runwavewithmaxorder(String waveType, String maxqty, String maxOrders, String ordersType,
			String sotCheck, String runOrPreview, String pickCheck, String fromDate, String todate) {
		RunWorkPlanSteps runWrkPln= new RunWorkPlanSteps();
		runWrkPln.runwavewithmaxorder(waveType, maxqty, maxOrders, ordersType, sotCheck, runOrPreview, pickCheck, fromDate, todate);
	}
	
	@Given("insert data into M_ATLAS_SLM for orders $order with RWF as $RWF with days older as $date")
	public void insertDataAtlas(String order, String RWF, String date) {
		WaveFunction waveFn = new WaveFunction();
		try {
		waveFn.insertDataAtlas(order, RWF, date);
		}catch(Exception e){LOGGER.info("Exception : "+e.getMessage());}

	}
	
	@Given("remove records from M_ATLAS_SLM for orders $order")
	public void deleterecord(String order) {
		WaveFunction waveFn=new WaveFunction();
		try {
			waveFn.deleterecord(order);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Given("update the order(S) $Orders with ICW flag as $flag")
	public void updateICWflag(String order, String flag) {
		WaveFunction waveFn=new WaveFunction();
		try {
			waveFn.updateICWflag(order, flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Given("Generic configuration parameters for Maximize Full Tote are $paramtersTable")
	public void setGenConfigMaximizeFullTote(ExamplesTable parametersTable) {
		RuleSimplification ruleSimpl= new RuleSimplification();
		ruleSimpl.setGenConfigMaximizeFullTote(parametersTable);
	}
	
	@Given("reset Actual users and UPH data")
	public void resetactualUph() {
		WaveFunction waveFn=new WaveFunction();
		try {
			waveFn.resetactualUph();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Given("delete the GC for $configcode")
	public void deletGcforconfigcode(String configcode) {
		GenericCofigParamUpdate genConfig=new GenericCofigParamUpdate();
		genConfig.deletGcforconfigcode(configcode);
	}
	
	@Given("verify and insert Picker info Generic config $paramtable")
	public void insertpickerinfo(ExamplesTable paramtable) throws Exception {
		GenericCofigParamUpdate genConfig=new GenericCofigParamUpdate();
		genConfig.insertpickerinfo(paramtable);
	}
	
	@Given("verify and insert Auto Wave type Generic config $paramtable")
	public void insertAutowavetype(ExamplesTable paramtable) throws Exception {
		GenericCofigParamUpdate genConfig=new GenericCofigParamUpdate();
		genConfig.insertAutowavetype(paramtable);
	}
	
	
	@Given("Check elgible orders for order category $Ordercaegory and order type $OrderType")
	public void getorders(String Ordercaegory, String OrderType) {
		DataGeneration dataGen=new DataGeneration();
		dataGen.getorders(Ordercaegory, OrderType);
	}
	
	@When("post msg to request for recommendations")
	public void postmsgforautoWave() {
		WaveFunction waveFn=new WaveFunction();
		waveFn.postmsgforautoWave();
	}
	
	@Then("verify that M_PD_RECOMMENDED_WAVES table has values as below $parametersTable")
	public void verifyM_PD_RECOMMENDED_WAVES(ExamplesTable parametersTable) throws SQLException, InterruptedException {
		VerificationOfValues verify=new VerificationOfValues();
		try {
			verify.verifyM_PD_RECOMMENDED_WAVES(parametersTable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@When("wave $wavename is $action from the UI with message as $message")
	public void performActionforWave(String wavename, String action, String message) throws Exception {
		RunWorkPlanSteps runWork=new RunWorkPlanSteps();
		runWork.performActionforWave(wavename, action, message);
	}
	
	@Then("verify the batch in M_PD_RECOMMENDED_WAVES is updated with $parametersTable")
	public void verfiytheexistingbatchvalues(ExamplesTable parametersTable) throws SQLException, InterruptedException {
		VerificationOfValues verification =new VerificationOfValues();
		try {
			verification.verfiytheexistingbatchvalues(parametersTable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Then("verify the new batch created in M_PD_RECOMMENDED_WAVES with $parametersTable")
	public void verifyNewBatchvalues(ExamplesTable parametersTable) throws SQLException, InterruptedException {
		VerificationOfValues verification =new VerificationOfValues();
		try {
			verification.verifyNewBatchvalues(parametersTable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Given("Below parameter set in Run Replenishment screen $parametersTable")
	public void setReplenParameters(ExamplesTable parametersTable) {
		DemandReplen demandRepln=new DemandReplen();
		demandRepln.setReplenParameters(parametersTable);
	}
	
	@When("post a message with above parameters to the Replen Queue $previewOrRun")
	public void previewDemandReplenBatch(String previewOrRun) throws InterruptedException {
		DemandReplen demandRepln=new DemandReplen();
		try {
			demandRepln.previewDemandReplenBatch(previewOrRun);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Then("validate below fields in M_RPLNS_RCMND_BATCH table $parametersTable")
	public void validateReplenBatchTable(ExamplesTable parametersTable) throws InterruptedException {
		DemandReplen demandRepln=new DemandReplen();
		demandRepln.validateReplenBatchTable(parametersTable);
	}
	
	@Then("Verify the following in M_REPLEN_SELECTION table $parametersTable")
	public void validateReplenTableParameters(ExamplesTable parametersTable) {
		DemandReplen demandRepln=new DemandReplen();
		demandRepln.validateReplenTableParameters(parametersTable);
	}
	
	@Then("Verify the selected orders are for prodline $prodLine")
	public void verifyOrdersSelected(String prodLine) {
		DemandReplen demandRepln=new DemandReplen();
		demandRepln.verifyOrdersSelected(prodLine);
	}
	
	@Then("Verify that the destination_location in M_REPLEN_SELECTION for successful items are all permanent or dynamic active locations")
	public void verify_all_active_location(){
		Replenishment repln=new Replenishment();
		repln.verify_all_active_location();
	}
	
	@Then("Verify the orders selected are $orderKind")
	public void verifyOrderKind(String orderKind) {
		DemandReplen demandRepln=new DemandReplen();
		demandRepln.verifyOrderKind(orderKind);
	}
	
	@Then("Verify the selected orders are of type $orderType")
	public void verifyOrderType(String orderType) {
		DemandReplen demandRepln=new DemandReplen();
		demandRepln.verifyOrderType(orderType);
	}
	
	@Then("Cancel the batch from backend")
	public void cancelBatchFromBackEnd() throws Exception {
		DemandReplen demandRepln=new DemandReplen();
		demandRepln.cancelBatchFromBackEnd();
	}
	
	@Given("Generic configuration set for Work Type $workType, Work Group $Work_workGroup in Integrated Picking Parameters Configuration with below paramters $parametersTable")
	public void setGenConfigIntegratedPickingParmConfiguration(String workType,String Work_workGroup,ExamplesTable parametersTable) throws Exception{
		RuleSimplification ruleSimpl= new RuleSimplification();
		ruleSimpl.setGenConfigIntegratedPickingParmConfiguration(workType, Work_workGroup, parametersTable);
	}
		
	@Then("Verify that the packwave is created successfully")
	public void verifyPackWave(@Named("PackwaveResultParameters") String PackwaveResultParameters) throws Exception{
		WaveCreation waveCreation = new WaveCreation();
		waveCreation.verifyPackWave(PackwaveResultParameters);
	}
	
	@Given("release Wave for logical group")
	public void releasewave(@Named("ReleasePackwaveParameters") String ReleasePackwaveParameters) throws InterruptedException{
		WaveCreation waveCreation = new WaveCreation();
		waveCreation.releasewave(ReleasePackwaveParameters);
	}
	
	@Then("verify the wave is released successfully")
	public void verfiyReleasepacwave(@Named("ReleasePackwaveResultParameters") String ReleasePackwaveResultParameters) throws Exception{
		WaveCreation waveCreation = new WaveCreation();
		waveCreation.verfiyReleasepacwave(ReleasePackwaveResultParameters);
	}
	
	@When("Run by shipment option: Wave planner selects a waveType $stmt")
	public void runWave(String stmt,@Named("waveType") String waveType,@Named("runOrPreview") String runOrPreview,@Named("pickCheck") String pickCheck,
			@Named("fromDate") String fromDate,@Named("todate") String todate,@Named("Run By Shipment") String runByShipment,@Named("Include DO's") String DO) {
		RunWorkPlanSteps runWorkPlanSteps = new RunWorkPlanSteps();
		runWorkPlanSteps.runWave(stmt, waveType, runOrPreview, pickCheck, fromDate, todate, runByShipment, DO);
	}
	
    @Given("user creates excel file")
    public void createExcel() throws IOException {
    	Replenishment repln = new Replenishment();
    	repln.createExcel();
    }
    
    @Given("Generic configuration parameters for Enhanced Opportunity Moves are $paramtersTable")
    public void setGenConfigEnhancedOppMovesFlag(ExamplesTable parametersTable) {
    	Replenishment repln = new Replenishment();
    	repln.setGenConfigEnhancedOppMovesFlag(parametersTable);
    }
    
    @When("clear any existing UPC List file")
    public void resetdata(){
    	Replenishment repln = new Replenishment();
    	try {
			repln.resetdata();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @When("user Imports UPC Move List $parametersTable")
    public void importUpcMoveList(ExamplesTable parametersTable) throws Exception{
    	Replenishment repln = new Replenishment();
    	repln.importUpcMoveList(parametersTable);
    }
    
    @Given("Generic configuration parameters for Common replen rules are $parametersTable")
    public void genConfigurationforCommonreplenrules(ExamplesTable parametersTable) throws Exception{
    	Replenishment repln = new Replenishment();
    	repln.genConfigurationforCommonreplenrules(parametersTable);
	}
    
    @Given("Generic configuration parameters for New Order Select Rule are $parametersTable")
    public void genConfigurationOrderSelectRule(ExamplesTable parametersTable) throws Exception{
    	Replenishment repln = new Replenishment();
    	repln.genConfigurationOrderSelectRule(parametersTable);
    }
    
    @Given("Generic configuration parameters for New Demand Replenishment are $parametersTable")
    public void genConfigurationforDemandReplen(ExamplesTable parametersTable) throws Exception{
    	Replenishment repln = new Replenishment();
    	repln.genConfigurationforDemandReplen(parametersTable);
    }
    
    @When("user runs replenishment based on the rule $ruleName")
    public void runReplinBatch(String ruleName){
    	Replenishment repln = new Replenishment();
    	repln.runReplinBatch(ruleName);
    }
    
    @Given("user clicks on Preview Wave")
    public void previewWave() throws Exception{
    	Replenishment repln = new Replenishment();
    	repln.previewWave();
    }
    
    @When("user $action the previewed batch")
    public void runrcancelbatch(String action){
    	Replenishment repln = new Replenishment();
    	repln.runrcancelbatch(action);
    }
    
    @Then("verify the tables for repln")
    public void verifyRepln(@Named("VrfcnParameters") String VrfcnParameters){
    	Replenishment repln = new Replenishment();
    	repln.verifyRepln(VrfcnParameters);
    }
    
    @Then("verify allocation and task creation")
	public void verifyallocationandtaskcreation(@Named("allocTaskverif") String allocTaskverif) {
		Replenishment repln=new Replenishment();
		repln.verifyallocationandtaskcreation(allocTaskverif);
	}
    
    @When("post a message with the above parameters to the PassiveReplen Queue $PassiveRun")
    public void PassiveRunDemandReplenBatch(String PassiveRun) throws Exception {
    	DemandReplen demandRepln = new DemandReplen();
    	demandRepln.PassiveRunDemandReplenBatch(PassiveRun);
    }
    
    @Given("Below parameter set in Passive Replenishment screen $parametersTable")
	public void setPassiveReplenParameters(ExamplesTable parametersTable) {
    	DemandReplen demandRepln = new DemandReplen();
    	demandRepln.setPassiveReplenParameters(parametersTable);
    }
    
    @Given("Generic configuration set for $rule rule,$overRide override rule in Logical Group and Override UI configuration with below parameter $parametersTable")
    public void genericConfiguration(String rule,String overRide,ExamplesTable parametersTable) throws Exception{
    	GenericConfigUtils genConfig = new GenericConfigUtils();
    	genConfig.genericConfigLogicalGrp(rule, overRide, parametersTable);
    }
    
    @Given("Generic configuration set for $chuteType chuteType in Release Pack Wave configuration with below parameter $parametersTable")
    public void genericConfigurationForReleasePackWave(String chuteType,ExamplesTable parametersTable) throws Exception{
    	GenericConfigUtils genConfig = new GenericConfigUtils();
    	genConfig.genericConfigReleasePackWave(chuteType,parametersTable);
    }
    
    @Given("update $colName for $rule rule,$overRide override rule in Logical Group and Override UI configuration")
    public void genericConfigForLogicalGrp(String colName,String rule,String overRide) throws Exception{
    	GenericConfigUtils genConfig = new GenericConfigUtils();
    	genConfig.genConfigForLogicalGrp(colName,rule,overRide);
    }
    
    @Given("data and post orders")
    public void setUpDataAndPostOrder(@Named("orderParameters") String orderParameters) throws Exception {
    	DataGeneration dataGeneration = new DataGeneration();
    	dataGeneration.setUpDataAndPostOrder(orderParameters);
    }
    
    @Given("Task_rule_parm is updated for task_capacity with qty $qty")
    public void updateTaskRuleParm(int qty){
    	Replenishment repn = new Replenishment();
    	repn.updateTaskRuleParm(qty);
    }
    
    @Given("whse_Parameters is updated for ppick_repl_ctrl with value as $value")
    public void updateWhseParameters(int value){
    	Replenishment repn = new Replenishment();
    	repn.updateWhseParameters(value);
    }
    
    @Then("Verify that opportunity moves batch is triggered and completed after Manual Batch and values are as below $parametersTable")
    public void verify_opportunity_batch_triggered_after_Manual_Batch(ExamplesTable parametersTable) throws InterruptedException {
    	Replenishment repn = new Replenishment();
    	repn.verify_opportunity_batch_triggered_after_Manual_Batch(parametersTable);
    }
    
    /**
     * method validates the orders intregrated in both MST and OSM db for the given reservation number and will assigned 
     * TC_ORDER_ID/FULLFILLMENT_NBR to runb the wave
     * @param res_nbr
     */
    
    @Then("verify order in mst and osm tables")
    public void verifyOrderandAssigntoRunwave(@Named("res_nbr")String res_nbr){
    	int index=1;
    	osmOrderList= new ArrayList<>();
    	LOGGER.info("query "+SQLQueriesMA.getOsmOrder.replace("#RES_NBR#", res_nbr));
    	osmOrderList=DBMethods.getDBValuesAsListOfMaps(SQLQueriesMA.getOsmOrder.replace("#RES_NBR#", res_nbr), ICWStepsE2E.getOSMdbSchema());
    	if(!osmOrderList.isEmpty()){
    		for(Map<String,String> osmorderMap:osmOrderList){
    			if(null!=osmorderMap.get("FULLFILLMENT_NBR")){
    				mstOrderList= new ArrayList<>();
    				LOGGER.info("query "+SQLQueriesMA.getMstOrder.replace("#TC_ORDER_ID#", osmorderMap.get("FULLFILLMENT_NBR")));
    				mstOrderList=DBMethods.getDBValuesAsListOfMaps(SQLQueriesMA.getMstOrder.replace("#TC_ORDER_ID#", osmorderMap.get("FULLFILLMENT_NBR")), "talosuser");
    				if(!mstOrderList.isEmpty()){
    					for(Map<String,String> mstorderMap:mstOrderList){
    						if(mstorderMap.get("TC_ORDER_ID").equalsIgnoreCase(osmorderMap.get("FULLFILLMENT_NBR"))){
    							LOGGER.info("Orders integrated to both OSM and MST tables");
    							StepDetail.addDetail("Orders integrated to both OSM and MST tables", true);
    						}else{
    							LOGGER.info("TC_ORDER_ID is not mathing with FULLFILLMENT_NBR");
    							StepDetail.addDetail("TC_ORDER_ID is not mathing with FULLFILLMENT_NBR", true);
    							Assert.assertTrue(false);
    						}
    					}
    				}else{
    					LOGGER.info("Order is not integrated into MST tables for TC_ORDER_ID "+osmorderMap.get("FULLFILLMENT_NBR"));
    					StepDetail.addDetail("Order is not integrated into MST tables for TC_ORDER_ID "+osmorderMap.get("FULLFILLMENT_NBR"), true);
    					Assert.assertTrue(false);
    				}
    				LOGGER.info("Setting the orders to run wave "+osmorderMap.get("FULLFILLMENT_NBR"));
    				GetAndSetValues.ordernumbers= new HashMap<>();
    				GetAndSetValues.ordernumbers.put("ORD"+index, osmorderMap.get("FULLFILLMENT_NBR"));
    				LOGGER.info("Orders "+GetAndSetValues.ordernumbers);
    				index++;
    			}
    		}
    		index=1;
    	}else{
    		LOGGER.info("Order is not integrated into OSM tables for Res_NBR "+res_nbr);
    		StepDetail.addDetail("Order is not integrated into OSM tables for Res_NBR "+res_nbr, true);
    		Assert.assertTrue(false);
    	}
    }
    
    /*
     * Start: WMSHF-15: Run by shipment method
     */
    //When HAF Run by shipment: Wave planner selects a Beumer WF wave to Run with PD checked and date range from 2 to 2 days all
	@When("HAF Run by shipment: Wave planner selects a $waveType wave to $runOrPreview with PD $pickCheck and date range from $fromDate to $todate days $stmt")
	public void runBySHipmentHAF(String stmt, @Named("waveType") String waveType,@Named("runOrPreview") String runOrPreview,@Named("pickCheck") String pickCheck,
			@Named("fromDate") String fromDate,@Named("todate") String todate,@Named("Run By Shipment") String runByShipment,@Named("Include DO's") String DO) 
	{
		RunWorkPlanSteps runWorkPlanSteps = new RunWorkPlanSteps();
		RunWorkPlan.nonPD=(pickCheck.equalsIgnoreCase("checked"))?false:true;
		runWorkPlanSteps.runWave(stmt, waveType, runOrPreview, pickCheck, fromDate, todate, runByShipment, DO);
	}
    /*
     * End:: WMSHF-15: Run by shipment method
     */
}
