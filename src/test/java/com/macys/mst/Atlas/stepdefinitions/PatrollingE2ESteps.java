package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.Atlas.utilmethods.PatrollingUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.Atlas.db.app.SQLQueriesMA;
import com.macys.mst.Atlas.utilmethods.PatrollingUtils;
import com.macys.wms.db.DBMethods;
import com.macys.wms.util.TALOSConnection;
import com.macys.wms.util.TalosUtils;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;

public class PatrollingE2ESteps {
	
	PatrollingUtils patrollingUtils = new PatrollingUtils();
	TalosUtils talos = new TalosUtils();
	private static Logger log = Logger.getLogger(PatrollingE2ESteps.class);
	static TALOSConnection conn = TALOSConnection.getInstance();
	
	@When("User uses $menu for patrolling Package $index and press $button :$examplesTable")
	public void patrollingSteps(String menu,int index,String button,ExamplesTable examplesTable) throws SQLException{
		if(menu.equalsIgnoreCase("Chute To Tote")){
			for (Map<String, String> row : examplesTable.getRows()) {
				String screenName1 = row.get("screenName1").trim();
				String packageStatus = row.get("packageStatus").trim();
				String screenName2 = row.get("screenName2").trim();
				int packageIndex ;
				if(row.containsKey("shipment index")){
					packageIndex = BeumerPackingE2ESteps.getPackageIndex(Integer.parseInt(row.get("shipment index").trim()));
				}else{
					packageIndex = index;
				}
				patrollingUtils.patrollingByChuteToTote(menu, packageIndex, screenName1, screenName2, packageStatus,button);
			}
		}else if(menu.equalsIgnoreCase("MSL Patrolling")){
			for (Map<String, String> row : examplesTable.getRows()) {
				int packageIndex ;
				if(row.containsKey("shipment index")){
					packageIndex = BeumerPackingE2ESteps.getPackageIndex(Integer.parseInt(row.get("shipment index").trim()));
				}else{
					packageIndex = index;
				}
				patrollingUtils.patrollingByMSL(menu,packageIndex,button,examplesTable);
			}
		}
	}
	
	@When("Scanning item_barcode,chute and validate the screen for Find Chute")
	public void scanItemForFindChute() throws Exception{
		List<Map<String, String>> packageInfo=new ArrayList<Map<String,String>>();
		packageInfo=DBMethods.getDBValuesAsListOfMaps(SQLQueriesMA.Talos.GETPACKAGEFORFINDCHUTE, "");
		String itemBarcode=packageInfo.get(0).get("ITEM_BARCODE");
		conn.postToServer("/"+conn.screen.responseURL+"?scanItem="+itemBarcode+"&wmscreen=false&noFirstTxtClear=false");
		log.info("Item scan is successful");
		StepDetail.addDetail("Item scan is successful for the item: "+itemBarcode, true);
		conn.postToServer("/"+conn.screen.responseURL+"&scanLocation="+packageInfo.get(0).get("CHUTE")+"&wmscreen=false&noFirstTxtClear=false");
		String totalQty="";
		String initialQty="";
		if(packageInfo.get(0).get("TOTAL_QUANTITY").length()<2){
			totalQty="0"+packageInfo.get(0).get("TOTAL_QUANTITY").length();
		}else{
			totalQty=String.valueOf(packageInfo.get(0).get("TOTAL_QUANTITY").length());
		}
		if(packageInfo.get(0).get("INITIAL_QUANTITY").length()<2){
			initialQty="0"+packageInfo.get(0).get("INITIAL_QUANTITY").length();
		}else{
			initialQty=String.valueOf(packageInfo.get(0).get("INITIAL_QUANTITY").length());
		}
		String actualElements=conn.screen.elements.get(8).toString().replaceAll("\\t", "").replaceAll(" ", "");
		String expectedScreenElements="<div><divstyle='position:absolute;'>"+packageInfo.get(0).get("CHUTE")+"&nbsp&nbsp&nbsp"+totalQty+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"+initialQty+"</div></div>";
		Assert.assertThat("Validating the Find chute screen elements: ", actualElements, equalTo(expectedScreenElements));
		StepDetail.addDetail("Find chute validation is successful: \n"+"Expected Screen Elements: "
		+expectedScreenElements+"\n"+"Actual Screen elements: "+conn.screen.elements.get(8), true);
	}
}
