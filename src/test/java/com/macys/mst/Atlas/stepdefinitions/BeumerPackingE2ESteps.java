package com.macys.mst.Atlas.stepdefinitions;

import com.macys.mst.artemis.selenium.WebDriverListener;
import com.macys.mst.wavefunction.utils.GetAndSetValues;
import com.macys.wms.DataGeneration.DataGeneration;
import com.macys.wms.db.DBMethods;
import com.macys.wms.systests.BeumerPackingSteps;
import com.macys.wms.systests.PickingSteps;
import com.macys.wms.systests.WMSASSteps;
import com.macys.wms.util.DBDataAccessHelp;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeumerPackingE2ESteps {
	public long TestNGThreadID = Thread.currentThread().getId();
//	private StepsContext stepsContext;
//	public BeumerPackingE2ESteps(StepsContext stepsContext) {
//		this.stepsContext = stepsContext;
//	}

	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = WebDriverListener.EnvMap.get(TestNGThreadID);
		WebDriverListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}
	@Given("Instantiate beumer packing $paramTable")
	public void instatiateBeumerPacking(ExamplesTable paramTable) throws SQLException, IOException {
		BeumerPackingSteps beumer=new BeumerPackingSteps();
		PickingSteps pickingSteps=new PickingSteps();
		beumer.getToteDetailBeforewaveActivation();
		pickingSteps.activateWaveForBeumerPacking();
		WMSASSteps wms=new WMSASSteps();
		DataGeneration.proteusWave=true;
		wms.setPickWaveNumber(GetAndSetValues.waveNBr);
		List<Map<String,String>> paramValues=paramTable.getRows();
		int packageIndex ;
		for(Map<String,String> map:paramValues){
			if(map.containsKey("shipment index")){
				packageIndex = getPackageIndex(Integer.parseInt(map.get("shipment index")));
			}else{
				packageIndex = Integer.parseInt(map.get("package index"));
			}
			try {
				beumer.packageStatus(map.get("sort type"), packageIndex,
						map.get("sorted quantity"));
				//beumer.validatePackageStatus(map.get("status"), map.get("sorted quantity"));	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
			/*paramValues.stream().forEach(m->{
				
				
				try {
					beumer.packageStatus(m.get("sort type"), Integer.parseInt(m.get("package index")),
							m.get("sorted quantity"));
					beumer.validatePackageStatus(m.get("status"), m.get("sorted quantity"));	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			
			});*/
					
	}
	
	@Given("user Activates the Pack Wave")
	public void activateWave() throws SQLException, IOException{
		BeumerPackingSteps beumer=new BeumerPackingSteps();
		PickingSteps pickingSteps=new PickingSteps();
		beumer.getToteDetailBeforewaveActivation();
		pickingSteps.activateWaveForBeumerPacking();
	}
	
	public static int getPackageIndex(int index){
		DBDataAccessHelp wsh = new DBDataAccessHelp();
		List<Map<String, String>> temp = new ArrayList();
		String sql = "select * from m_package where work_batch_nbr='#WORK_BATCH_NUMBER#' order by SHIPMENT_NUMBER asc "
				.replace("#WORK_BATCH_NUMBER#", GetAndSetValues.waveNBr+"001");
		System.out.println(sql);
		wsh.setSql(sql);
		temp = (List<Map<String, String>>) wsh.getData();
		String packNum = temp.get(index - 1).get("PACKAGE_NUMBER");
		sql = "select * from m_package where WORK_BATCH_NBR='#WORK_BATCH_NUMBER#' order by M_PACKAGE_ID asc"
				.replace("#WORK_BATCH_NUMBER#", GetAndSetValues.waveNBr+"001");
		temp = DBMethods.getDBValuesAsListOfMaps(sql,"");
		int packageIndex=1;
		for( Map e: temp){
			if(e.containsValue(packNum)){
				break;
			}else{
				packageIndex++;
			}
		}
		System.out.println(packageIndex);
		return packageIndex;
	}
}
