package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.Atlas.utilmethods.PackingUtils;
import com.macys.mst.Atlas.utilmethods.PatrollingUtils;
import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.artemis.selenium.WebDriverListener;
import com.macys.mst.wavefunction.utils.GetAndSetValues;
import com.macys.mst.Atlas.db.app.SQLQueriesMA;
import com.macys.mst.Atlas.utilmethods.PackingUtils;
import com.macys.mst.Atlas.utilmethods.PatrollingUtils;
import com.macys.wms.db.DBMethods;
import com.macys.wms.db.XLSDBInitializer;
import com.macys.wms.systests.*;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PackingStep {
	public long TestNGThreadID = Thread.currentThread().getId();
	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = WebDriverListener.EnvMap.get(TestNGThreadID);
		WebDriverListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}
	static String packageNumber="";
	PackingUtils packingUtils = new PackingUtils();
	PatrollingUtils patrollingUtils = new PatrollingUtils();
	public static String manifestUrl = FileConfig.getInstance().getStringConfigValue("was.env.mainFeastUrl");
	static Logger log = Logger.getLogger(PackingStep.class.getName());


	@When("User uses $menu for packing Package $index :$examples")
	public void packingSteps(String menu,int index,ExamplesTable examples) throws Exception{
		packageNumber = PatrollingUtils.packageNumber==null?CustomPackingSteps.packageNumber:PatrollingUtils.packageNumber;
		int packageIndex = index ;
		for (Map<String, String> row : examples.getRows()) {
			if(row.containsKey("shipment index")){
				packageIndex = BeumerPackingE2ESteps.getPackageIndex(Integer.parseInt(row.get("shipment index").trim()));
			}
		}
		if(menu.equalsIgnoreCase("Pack and Print")){
			packingUtils.packingByPackAndPrint(menu,packageIndex, packageNumber,examples);
		}else if(menu.equalsIgnoreCase("Pack OB Package")){
			packingUtils.packingByPackOBPackage(menu, packageIndex,packageNumber,examples);
		}else if(menu.equalsIgnoreCase("Locate package")){
			packingUtils.packingByLocatePackage(packageIndex);

		}else if(menu.equalsIgnoreCase("Exceptions packing")){
			packingUtils.packingByExceptionPacking(menu,packageIndex, packageNumber);
		}else if(menu.equalsIgnoreCase("Exceptions Tote")){
			packingUtils.packingByExceptionTote(menu,packageIndex, packageNumber,examples);
		}
	}

	@When("User uses $menu for locating package $index :$examples")
	public void locatingPackageByLocatePackage(String menu,int index,ExamplesTable examples){
		//log.info("In method locatingPackageByLocatePackage");
		int packageIndex = index;
		for (Map<String, String> row : examples.getRows()) {
			if(row.containsKey("shipment index")){
				packageIndex = BeumerPackingE2ESteps.getPackageIndex(Integer.parseInt(row.get("shipment index").trim()));
			}
		}
		CustomPackingSteps customPackingSteps = new CustomPackingSteps();
		patrollingUtils.selectingMenu(menu);// input:Locate Package
		customPackingSteps.selectPackageToLocate(packageIndex);//input:1
		customPackingSteps.enterSelectedPackageToLocate();
		customPackingSteps.packageScanLocationSuccessfull();
		//WMSHF-15
		//customPackingSteps.selectLocation();
		customPackingSteps.selectExceptionLocationToLocatePackage();
		customPackingSteps.enterExceptionLocation();
		customPackingSteps.locationScanSuccessfull();
	}
	
	
	@Then("Sending package for manifesting $examples")
	public void sendingPackageForManifesting(ExamplesTable examples){
		RfManifestPackageSteps rfManifestPackageSteps = new RfManifestPackageSteps();
		for (Map<String, String> row : examples.getRows()){
			String menu = row.get("menu").trim();
			int index = Integer.parseInt(row.get("index").trim());
			String action = row.get("action").trim();
			patrollingUtils.selectingMenu(menu);
			rfManifestPackageSteps.scanPackages("valid", index);
			//rfManifestPackageSteps.validateMpackageShipDtl("Manifesting", index, index);
			//rfManifestPackageSteps.validatingMWorkLog(menu, action, index);
			
		}
	}
	
	@Then("user close wave by $ClosePackWave")
	public void userCloseWaveByClosePackWave(String Cron_or_ClosePackWave) throws IOException{
		PickingSteps picking=new PickingSteps();
		picking.userCloseWaveByClosePackWave(Cron_or_ClosePackWave);
	}
	
	@When("User uses $menu Singles transaction $examples")
	public void packingStepsForSingles(String menu,ExamplesTable example){
		packingUtils.packingByPackAndPrintSingles(menu, example);
	}
	
	@Then("validate Pack Wave released for $logicalGrp Logical_Grp")
	public void validateReleasedWaves(String logicalGrp){
		String packWaveNumber = GetAndSetValues.waveNBr;
		if(packWaveNumber.length()<15){
			packWaveNumber+="001";
		}
		PackageInquiryHandHeldSteps packSteps = new PackageInquiryHandHeldSteps();
	    String sql = SQLQueriesMA.getPackWavesForLogicalGroup.replace("#PACKWAVENBR#", packWaveNumber).replace("#LOGICALGROUP#", logicalGrp).replace("$colname", "PACK_WAVE_NBR,STATUS_CODE,STATUS");
	    Map<String,String> DBvalues= DBMethods.getDBValuesAsListOfMaps(sql, "").get(0);
	    Map<String,String> expectedValues = new HashMap<>();
	    expectedValues.put("PACK_WAVE_NBR", packWaveNumber);
	    expectedValues.put("STATUS_CODE", "90");
	    expectedValues.put("STATUS", "Released");
	    packSteps.compareValues(DBvalues, expectedValues);
	}
	
	@Given("update conveyable from $status1 to $status2 in Tote table")
	public void updateToteType(String status1,String status2){
		String workBatch = WMSASSteps.pickWaveNumber;
		if (workBatch.length() < 15) {
			workBatch = workBatch + "001";
		}
		String sql = "update m_tote set CONVEYABLE='" + status2 + "' where CONVEYABLE='" + status1 + "' and WORK_BATCH_NUMBER='"
				+ workBatch + "'";
		log.info(sql);
		XLSDBInitializer.deleteOrUpdateDataBase(sql);
	}
}
