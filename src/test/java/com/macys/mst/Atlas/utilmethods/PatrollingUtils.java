package com.macys.mst.Atlas.utilmethods;

import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.Atlas.db.app.SQLQueriesMA;
import com.macys.wms.DataGeneration.DataGeneration;
import com.macys.wms.db.DBMethods;
import com.macys.wms.db.XLSDBInitializer;
import com.macys.wms.db.dao.PackageDao;
import com.macys.wms.jbehave.reports.StepDetail;
import com.macys.wms.systests.ChuteToToteSteps;
import com.macys.wms.systests.FlexInventorySteps;
import com.macys.wms.systests.MSLpatrollingSteps;
import com.macys.wms.systests.WMSASSteps;
import org.apache.log4j.Logger;
import org.jbehave.core.model.ExamplesTable;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatrollingUtils {

	public static String packageNumber;
	public static String defaultLocationId;
	private static Logger log = Logger.getLogger(PatrollingUtils.class.getName());
	public static String mslArea = FileConfig.getInstance().getStringConfigValue("reservedMslArea");

	/**
	 * 
	 * @param menu
	 * @param index
	 * @throws SQLException
	 */
	public void patrollingByChuteToTote(String menu,int index,String screenName1,String screenName2,String packageStatus,String button) throws SQLException{
		ChuteToToteSteps chuteToToteSteps = new ChuteToToteSteps();
		selectingMenu(menu);
		chuteToToteSteps.userGetTheWaveNumber();
		chuteToToteSteps.chutePromt(index);
		chuteToToteSteps.whenUserEnterChute();
		chuteToToteSteps.successfullyChuteScanN(screenName1);
		packageNumber = ChuteToToteSteps.packageNumber;
		if(button.equalsIgnoreCase("No button")){
			enteringTote(screenName2, index);
		}else if(button.equalsIgnoreCase("Empty Chute")){
			if(DataGeneration.defaultLocation!=null && DataGeneration.defaultLocation.equalsIgnoreCase("XP99A999")){
				defaultLocationId = "2851906";
			}
			chuteToToteSteps.clickEmptyChute();
			if(WMSASSteps.flexOnly){
				validatingPackageUpdates("LOCATION_BARCODE", "default location", packageNumber);
			}else{
				validatingPackageUpdates("LOCATION_ID", defaultLocationId, packageNumber);
			}
		}else if(button.equalsIgnoreCase("F4")){
			chuteToToteSteps.hitsF4();
			chuteToToteSteps.pressingF4("Send to Exception");
			enteringTote(screenName2, index);
		}
		//chuteToToteSteps.validateChuteToToteMsgGeneration(); 
	}
	
	/**
	 * Common code for selecting transaction menu
	 * @param menu
	 */
	public void selectingMenu(String menu){
		WMSASSteps wmsSteps = new WMSASSteps();
		wmsSteps.loginToTalos();
		wmsSteps.whenUserMenuOption(menu);
		wmsSteps.menuOptionSuccessfullySelected(menu);
	}
	
	public void enteringTote(String screenName,int index) throws SQLException{
		ChuteToToteSteps chuteToToteSteps = new ChuteToToteSteps();
		chuteToToteSteps.whenUserEnterTote(index);
		chuteToToteSteps.ToteScan(screenName);
	}
	
	public void validatingPackageUpdates(String colname,String colNameExpected,String packageNumber){
		String sql="";
		String expectedValue="";
		log.info("Validating "+colname+" in M_Package table");
		StepDetail.addDetail("Validating "+colname+" in M_Package table", true);
		sql=SQLQueriesMA.getPackageInfo.replace("$colname", colname).replace("#PACKAGENUMBER#", packageNumber);
		log.info("Query:"+sql);
		String dbValue=DBMethods.getDBValueInString(sql, "");
		if(colNameExpected.equalsIgnoreCase("default location")){
			expectedValue=DataGeneration.defaultLocation;
		}else{
			expectedValue=defaultLocationId;
		}
		log.info("Expected value:"+expectedValue);
		log.info("Actual value:"+dbValue);
		StepDetail.addDetail("Actual value" + dbValue, true);
		StepDetail.addDetail("Expected value" + expectedValue, true);
		Assert.assertEquals("Verify "+colname, expectedValue, dbValue);

	}
	
	public void patrollingByMSL(String menu,int index,String button,ExamplesTable examples){
		MSLpatrollingSteps mslPatrollingSteps = new MSLpatrollingSteps();
		PackageDao packageDao = new PackageDao();
		selectingMenu(menu);
		/*
		 * Start:HAF
		 */
	/*	if(examples.getRow(0).containsKey("packageWithStatus"))
		{
		*/
		/*List<Map<String,String>> exampleMap=examples.getRows();
		exampleMap.stream().forEach(mp ->{
			mp.forEach((k,v)->{
				if(k.equalsIgnoreCase("packageWithStatus"))
				{
					String wavNum=DataCreateModule.outputEntity.getDtWaveNumber()+"001";
					String pkgStatus=examples.getRow(1).;
					int pkgIndex=;
					String pkgNum=packageDao.getPackagesWithSpecificStatus(wavNum,pkgStatus,pkgIndex);
					MSLpatrollingSteps.locnBrcd = packageDao.getlocnBrcd(pkgNum);
				}
			});
		});*/
		//}
		/*
		 * End:HAF
		 */
		
		mslPatrollingSteps.ScanMsl(index);
		packageNumber = ChuteToToteSteps.packageNumber;
		if(button.equalsIgnoreCase("No button")){
			List<Map<String,String>> exRows=examples.getRows();
			exRows.stream().forEach(m ->{
				m.forEach((k,v)->{
					if(k.equals("Scan Tote")) {
						mslPatrollingSteps.patrolLocatnBarcodeScanSucessfull(k);
						mslPatrollingSteps.givenTote(v);
						mslPatrollingSteps.UseScanTote();
						mslPatrollingSteps.toteNoBarcodeScanSucessfull(k);
						restorePackageStatus(packageNumber);
					}else if(k.equals("Scan MSL")) {
						updatePackageStatus(packageNumber);
						mslPatrollingSteps.whenUserEntersMSLbrcd();
						mslPatrollingSteps.locatnBarcodeScanSucessfull();
						mslPatrollingSteps.whenUseEnterMSLbarcode();					}
				});
			}
					);
		}else if(button.equalsIgnoreCase("Empty Location")){
			List<Map<String,String>> exRows=examples.getRows();
			exRows.stream().forEach(m ->{
				m.forEach((k,v)->{
					if(k.equals("Scan Tote")) {
						mslPatrollingSteps.patrolLocatnBarcodeScanSucessfull(k);
						restorePackageStatus(packageNumber);
					}else if(k.equals("Scan MSL")) {
						updatePackageStatus(packageNumber);
						mslPatrollingSteps.whenUserEntersMSLbrcd();
						mslPatrollingSteps.locatnBarcodeScanSucessfull();
						mslPatrollingSteps.whenUseEnterMSLbarcode();
					}else if(k.equals("validate flexLocation")) {
						String[] values = v.split("&");
						FlexInventorySteps flexInvsteps = new FlexInventorySteps();
						flexInvsteps.validateFlexLocation(values[0], values[1], values[2]);//-- /location_services/1.4/location/barcode/{barcode}&XP99A999&Exceptions
					}else if(k.equals("Empty Location button")){
						if(DataGeneration.defaultLocation!=null && DataGeneration.defaultLocation.equalsIgnoreCase("XP99A999")){
							defaultLocationId = "2851906";
						}
						mslPatrollingSteps.checkForEmptyLocationButton(v);//-- displayed
						mslPatrollingSteps.clickEmptyLocation();
						if(WMSASSteps.flexOnly){
							validatingPackageUpdates("LOCATION_BARCODE", "default location", packageNumber);
						}else{
							validatingPackageUpdates("LOCATION_ID", defaultLocationId, packageNumber);
						}
					}
				});
			}
					);
		}
		
	}
	
	public void updatePackageStatus(String packageNumber) {
		List<Map<String, String>> temp = new ArrayList();
		String sql1 = SQLQueriesMA.update_Package_Status_11.replace("#PACKAGE_NUMBER#", packageNumber)
				.replace("#WORKGRP#", mslArea);
		System.out.println(sql1);
		XLSDBInitializer.deleteOrUpdateDataBase(sql1);

		String sql2 = SQLQueriesMA.update_Package_Status_12.replace("#PACKAGE_NUMBER#", packageNumber)
				.replace("#WORKGRP#", mslArea);
		System.out.println(sql2);
		XLSDBInitializer.deleteOrUpdateDataBase(sql2);
	}
	
	public void restorePackageStatus(String packageNumber) {
		List<Map<String, String>> temp = new ArrayList();
		String sql1 = SQLQueriesMA.restore_Package_Status_11.replace("#PACKAGE_NUMBER#", packageNumber)
				.replace("#WORKGRP#", mslArea);
		System.out.println(sql1);
		XLSDBInitializer.deleteOrUpdateDataBase(sql1);

		String sql2 = SQLQueriesMA.restore_Package_Status_12.replace("#PACKAGE_NUMBER#", packageNumber)
				.replace("#WORKGRP#", mslArea);
		System.out.println(sql2);
		XLSDBInitializer.deleteOrUpdateDataBase(sql2);


	}
	
}
