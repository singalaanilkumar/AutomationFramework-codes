package com.macys.mst.Atlas.utilmethods;

import com.macys.mst.Atlas.stepdefinitions.PackingStep;
import com.macys.mst.Atlas.db.app.SQLQueriesMA;
import com.macys.mst.Atlas.stepdefinitions.PackingStep;
import com.macys.wms.DataGeneration.DataGeneration;
import com.macys.wms.db.DBMethods;
import com.macys.wms.db.TalosPackingDbUtil;
import com.macys.wms.db.dao.PackageDao;
import com.macys.wms.jbehave.reports.StepDetail;
import com.macys.wms.systests.*;
import com.macys.wms.util.TALOSConnection;
import flex.messaging.util.StringUtils;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.testng.Assert;

import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.IntStream;
//import junit.framework.Assert;

public class PackingUtils {

	PatrollingUtils patrollingUtils = new PatrollingUtils();
	TALOSConnection conn = TALOSConnection.getInstance();
	static Logger log = Logger.getLogger(PackingStep.class.getName());
	
	/**
	 * Complete Pack and Print transaction
	 * @param menu
	 * @param examples
	 * @param index
	 * @param packageNumber
	 * @throws SQLException
	 */
	public void packingByPackAndPrint(String menu,int index,String packageNumber,ExamplesTable examples) throws SQLException{
		PackAndPrintSteps packAndPrintSteps = new PackAndPrintSteps();
		if(packageNumber==null){
			PackageDao packageDao = new PackageDao();
			packageNumber = packageDao.getPackageWithSpecifiedIndex(WMSASSteps.pickWaveNumber+"001", index);
		}
		String query = SQLQueriesMA.getPackageInfo.replace("#PACKAGENUMBER#", packageNumber).replace("$colname", "TOTAL_QUANTITY");
		String totalquantity = DBMethods.getDBValueInString(query, "");
		patrollingUtils.selectingMenu(menu);// input:Pack and Print
		for (Map<String, String> row : examples.getRows()) {
			String printer = row.get("printer").trim();
			String tote_package = row.get("tote_package").trim();
			String screenName = row.get("screenName").trim();
			String partialQty ="";
			if(row.containsKey("PartialQty")){
				partialQty = row.get("PartialQty").trim();
			}
			packAndPrintSteps.packageNprintScreen(printer);
			packAndPrintSteps.whenUserEnterToteOrPackage(tote_package);
			packAndPrintSteps.successfullyPrinterScanN("Scan Tote or Package or Resv #");// input:Scan Tote or Package
			if(screenName.equalsIgnoreCase("Scan Tote")){
				packAndPrintSteps.whenUserEnterTote(1);// input:1
				packAndPrintSteps.successfullyToteScan("Scan item");
				scaningItemInPNP(packageNumber,partialQty);
				if(StringUtils.isEmpty(partialQty)){
					packAndPrintSteps.successfullyItemScan("Scan Tote or Package or Resv #");// input:Scan Tote or Package
				}else{
					packAndPrintSteps.whenUserPressesF4AndAcceptsInfoMsg();
				}
			}else if(screenName.equalsIgnoreCase("Scan Package")){
				DataGeneration.packAndPrintWorkLog = true;
				packAndPrintSteps.whenUserEnterToteOrPackage("Package", totalquantity, "", "");
			}else if(screenName.equalsIgnoreCase("Package")){
				packAndPrintSteps.whenUserEnterToteOrPackage(screenName);
			}else if(screenName.equalsIgnoreCase("Scan_Tote and End_Tote")){
				packAndPrintSteps.whenUserEnterTote(1);// input:1
				packAndPrintSteps.successfullyToteScan("Scan item");
				packAndPrintSteps.sendToException("Cancelling "+totalquantity);
			}
		}
		
	}

	/**
	 * Complete Pack OB Package transaction
	 * @param menu
	 * @param examples
	 * @param index
	 * @param packageNumber
	 * @throws Exception
	 */
	public void packingByPackOBPackage(String menu,int index,String packageNumber,ExamplesTable examples) throws Exception{
		CustomPackingSteps customPackingSteps = new CustomPackingSteps();
		patrollingUtils.selectingMenu(menu);//input:Pack OB Package
		customPackingSteps.selectPackageToLocate(index);// input:Index=1
		if(packageNumber==null){
			packageNumber=CustomPackingSteps.packageNumber;
		}
		ChuteToToteSteps.packageNumber=packageNumber;
		customPackingSteps.whenUserEntersThePackage();
		customPackingSteps.packageScanSuccessfullPromptedForItem();
		customPackingSteps.userpressessF4atitemscreen();
		for (Map<String, String> row : examples.getRows()) {
			String shorted = row.get("shorted").trim();
			String packing = row.get("packing").trim();
			String errorMsg = "";
			String screenName = "";
			String packageDtlIndex = "1";
			if(row.containsKey("screenName")){
				screenName = row.get("screenName").trim();
			}
			if(row.containsKey("F8_Error_Msg")){
				errorMsg = row.get("F8_Error_Msg").trim();
			}else if(row.containsKey("ERROR")){
				errorMsg = row.get("ERROR").trim();
			}
			if(row.containsKey("Item_From_Package_Detail_Index")){
				packageDtlIndex = row.get("Item_From_Package_Detail_Index").trim();
			}
			customPackingSteps.userAtF4ScreenNFlow(shorted);//input:without shorting
			if(packing.equalsIgnoreCase("completePacking")){ 
				scanningItemInPackOBPackage(packageNumber, screenName,index,0,packageDtlIndex);

			}else if(packing.equalsIgnoreCase("partialPacking")){
				if(row.containsKey("ERROR")){
					customPackingSteps.userAtItemPrompt("Item From Package Detail "+packageDtlIndex);
					customPackingSteps.userEntersItem();
					userMessage("ERROR", "TALOS", errorMsg);
				}else{
					customPackingSteps.userAtItemPrompt("Item From Package Detail "+packageDtlIndex);
					customPackingSteps.userEntersItem();
					customPackingSteps.itemScanSuccessfullForCustomPacking(screenName);//input: Package	
					shortingPackagesInPackOBPackage("valid item", packing,packageNumber,index,errorMsg);
					scanningItemInPackOBPackage(packageNumber, screenName,index,1,packageDtlIndex);	
				}
			}else if(packing.equalsIgnoreCase("noPacking")){
				shortingPackagesInPackOBPackage("valid item", packing,packageNumber,index,errorMsg);
			}
		}
	}

	/**
	 * Complete Locate Package transaction
	 * @param packageIndex
	 */
	public void packingByLocatePackage(int packageIndex){
		CustomPackingSteps customPackingSteps = new CustomPackingSteps();
		TalosPackingDbUtil talosPackingDB = new TalosPackingDbUtil();
		PatrollingUtils.packageNumber = talosPackingDB.getValidPackageValues(WMSASSteps.getPickWaveNumber()+"001").get(packageIndex-1).get("PACKAGE_NUMBER");
		String packageNumber = PatrollingUtils.packageNumber;
		customPackingSteps.selectPackageToLocate(packageIndex);
		String query = SQLQueriesMA.getPackageDetailInfo.replace("#PACKAGENUMBER#", packageNumber).replace("$colname", "INITIAL_QUANTITY");
		List<String> listOfPackageDetailQty = DBMethods.getDBValuesInList(query, "");
		String param = "Item From Package Detail ";
		int index = 1;
		ListIterator itr = listOfPackageDetailQty.listIterator();
		while(itr.hasNext()){
			String quantity = (String) itr.next();
			customPackingSteps.userAtItemPrompt(param+index);
			IntStream.range(0, Integer.parseInt(quantity)).forEach(i ->{
				try {
					customPackingSteps.userEntersItemAtLocatePackage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			index++;
		}
		ChuteToToteSteps.packageNumber=PatrollingUtils.packageNumber;
	}

	/**
	 * Complete Exception Packing transaction
	 * @param menu
	 * @param packageNumber
	 */
	public void packingByExceptionPacking(String menu,int index,String packageNumber){

		scanningItemInExceptionPacking(menu,packageNumber,index);
	}

	/**
	 * Scanning all the items in Pack and Print for particular package
	 * @param packageNumbr
	 */
	public void scaningItemInPNP(String packageNumbr,String partialPackQty){
		PackAndPrintSteps packAndPrintSteps = new PackAndPrintSteps();
		String query = SQLQueriesMA.getPackageDetailInfo.replace("#PACKAGENUMBER#", packageNumbr).replace("$colname", "INITIAL_QUANTITY");
		List<String> listOfPackageDetailQty = DBMethods.getDBValuesInList(query, "");
		query = SQLQueriesMA.getPackageInfo.replace("#PACKAGENUMBER#", packageNumbr).replace("$colname", "TOTAL_QUANTITY");
		String totalquantity = DBMethods.getDBValueInString(query, "");
		String param ="item from package detail index-";
		String row ="FROM_LOCATION_ID:NULL,PREV_FROM_CONTAINER_STATUS:NULL,CURR_FROM_CONTAINER_STATUS:NULL,UNITS:1,PREV_TO_CONTAINER_STATUS:$prevToContainerStatus,CURR_TO_CONTAINER_STATUS:$currToContainerStatus";
		String prevToContainerStatus = "15";
		String currToContainerStatus = "15";
		String status = "15";
		String pckgDtlStatus = "90";
		String toteNumber = "NOT-NULL";
		String refToteNumber = "NULL";
		int index = 1;
		ListIterator itr = listOfPackageDetailQty.listIterator();
		if(totalquantity.equals("1")){
			prevToContainerStatus = "12";
		}
		log.info("List :"+listOfPackageDetailQty+" ; List size"+listOfPackageDetailQty.size());
		while(itr.hasNext()){
			String quantity="";
			if(!StringUtils.isEmpty(partialPackQty)){
				quantity=partialPackQty;
				itr.next();
			}else{
				quantity = (String) itr.next();
			}
			for( int i=0 ; i< Integer.parseInt(quantity);i++){
				packAndPrintSteps.givenUserAtItemPromptScreenForPacking(param+index);
				packAndPrintSteps.whenUserEnterItem();
			}
			if(itr.nextIndex() == listOfPackageDetailQty.size() && StringUtils.isEmpty(partialPackQty)){
				currToContainerStatus = "20";
				status = "20";
				toteNumber = "NULL";
				refToteNumber = "NOT-NULL";
			}
			packAndPrintSteps.ValidateWorklog(row.replace("$prevToContainerStatus",prevToContainerStatus ).replace("$currToContainerStatus", currToContainerStatus));
			packAndPrintSteps.validatePackageUpdates(status, totalquantity, toteNumber, refToteNumber, pckgDtlStatus, quantity);
			index++;
		}
	}


	/**
	 * Scanning all the items in Pack OB Package for particular package
	 * @param packageNumbr
	 * @param screenName
	 * @throws Exception
	 */
	public void scanningItemInPackOBPackage(String packageNumbr,String screenName,int packageIndex,int packagedQuantity,String packageDtlIndex) throws Exception{
		CustomPackingSteps customPackingSteps = new CustomPackingSteps();
		customPackingSteps.selectPackageToLocate(packageIndex);
		String sql = SQLQueriesMA.getPackedQty.replace("#PACKAGENUMBER#", packageNumbr).replace("$colname", "PACKED_QUANTITY");
		int packedQty = DBMethods.getDBValueInteger(sql, "");
		String query = SQLQueriesMA.getPackageDetailInfo.replace("#PACKAGENUMBER#", packageNumbr).replace("$colname", "INITIAL_QUANTITY");
		List<String> listOfPackageDetailQty = DBMethods.getDBValuesInList(query, "");
		if(packedQty!=0){
			String query1 = SQLQueriesMA.getPackageDetailInfo.replace("#PACKAGENUMBER#",packageNumbr ).replace("$colname", "INITIAL_QUANTITY-QUANTITY");
			listOfPackageDetailQty = DBMethods.getDBValuesInList(query1, "");
		}
		String param = "Item From Package Detail ";
		int index = 1;
		ListIterator itr = listOfPackageDetailQty.listIterator();
		while(itr.hasNext()){
			String quantity = (String) itr.next();
			for( int i=0 ; i< Integer.parseInt(quantity);i++){
				customPackingSteps.userAtItemPrompt(param+index);
				customPackingSteps.userEntersItem();
				customPackingSteps.itemScanSuccessfullForCustomPacking(screenName);//input: Package
			}
			index++;
		}

	}
	

	/**
	 * Shorting item in Pack OB package for particular package
	 * @param param
	 * @param packing
	 * @throws Exception
	 */
	public void shortingPackagesInPackOBPackage(String param,String packing,String packageNumber,int index,String errorMsg) throws Exception{
		CustomPackingSteps customPackingSteps = new CustomPackingSteps();
		customPackingSteps.selectPackageToLocate(index);
		String query = SQLQueriesMA.getPackageInfo.replace("#PACKAGENUMBER#",packageNumber ).replace("$colname", "TOTAL_QUANTITY");
		int totalQuantity = DBMethods.getDBValueInteger(query, "");
		customPackingSteps.userAtItemPrompt(param);//input: valid item
		customPackingSteps.closePackWave();
		customPackingSteps.userpressessF8atitemlist();
		if(packing.equalsIgnoreCase("partialPacking")){
			totalQuantity--;
		}
		/*if(errorMsg.equalsIgnoreCase("Cancelling units")){
			customPackingSteps.thenUserGetsAnErrorWhileEnteringTote("Cancelling "+totalQuantity);
		}else if(errorMsg.equalsIgnoreCase("Complete/Short")){
			customPackingSteps.thenUserGetsAnErrorWhileEnteringTote("Complete/Short Work for [workBatchNumber]");
		}*/
	}

	/**
	 * Scanning all the items in Exception Packing for particular package
	 * @param packageNumbr
	 */
	public void scanningItemInExceptionPacking(String menu,String packageNumbr,int index){
		CustomPackingSteps customPackingSteps = new CustomPackingSteps();
		customPackingSteps.selectPackageToLocate(index);
		patrollingUtils.selectingMenu(menu);
		String query = SQLQueriesMA.getPackageDetailInfo.replace("#PACKAGENUMBER#", packageNumbr).replace("$colname", "INITIAL_QUANTITY-QUANTITY");
		List<String> listOfPackageDetailQty = DBMethods.getDBValuesInList(query, "");
		String param = "Item From Package Detail ";
		String status = "In-packing";
		int packageDtlIndex = 1;
		ListIterator itr = listOfPackageDetailQty.listIterator();
		while(itr.hasNext()){
			String quantity = (String) itr.next();
			customPackingSteps.userAtItemPrompt(param+packageDtlIndex);
			IntStream.range(0, Integer.parseInt(quantity)).forEach(i->{
				customPackingSteps.enterItemForExceptionsPacking();
				customPackingSteps.itemScanSuccessfullForExceptionPacking();
				customPackingSteps.selectPacakageForExceptionPacking();
				customPackingSteps.enterPacakageForExceptionPackingAsDisplayed();
				customPackingSteps.packageScanSuccessfullForExceptionsPacking();
			});
			if(itr.nextIndex() == listOfPackageDetailQty.size()){
				status = "packed";
			}
			packageDtlIndex++;
		}
	}

	public void packingByExceptionTote(String menu, int index, String packageNumber, ExamplesTable examples) {
		PackAndPrintSteps packAndPrintSteps = new PackAndPrintSteps();
		ExceptionsToteSteps exceptionToteSteps = new ExceptionsToteSteps();
		patrollingUtils.selectingMenu(menu);// input:Exceptions Tote
		for (Map<String, String> row : examples.getRows()) {
			String printer = row.get("printer").trim();
			String screenName = row.get("screenName").trim();
			String partialQty ="";
			if(! StringUtils.isEmpty(row.get("PartialQty").trim())){
				partialQty = row.get("PartialQty").trim();
			}
			exceptionToteSteps.userIsAtPrinterScreenWith(printer);
			exceptionToteSteps.whenUserEnterPrinter();
			exceptionToteSteps.printerScanSuccessful("Scan Tote");
			if(screenName.equalsIgnoreCase("Scan Tote")){
				exceptionToteSteps.whenUserEnterTote(1);
				exceptionToteSteps.toteScanSuccessful();
				scaningItemInExceptionTote(packageNumber,partialQty);
				if(StringUtils.isEmpty(partialQty)){
					packAndPrintSteps.whenUserPressesF4AndAcceptsInfoMsg();//Pressing end tote
				}
			}
		}
	}

	public void scaningItemInExceptionTote(String packageNumbr, String partialPackQty) {
		PackAndPrintSteps packAndPrintSteps = new PackAndPrintSteps();
		String query = SQLQueriesMA.getPackageDetailInfo.replace("#PACKAGENUMBER#", packageNumbr).replace("$colname", "INITIAL_QUANTITY");
		List<String> listOfPackageDetailQty = DBMethods.getDBValuesInList(query, "");
		String param ="item from package detail index-";
		int index = 1;
		ListIterator itr = listOfPackageDetailQty.listIterator();
		
		while(itr.hasNext()){
			String quantity="";
			if(!StringUtils.isEmpty(partialPackQty)){
				quantity=partialPackQty;
				itr.next();
			}else{
				quantity = (String) itr.next();
			}
			for( int i=0 ; i< Integer.parseInt(quantity);i++){
				packAndPrintSteps.givenUserAtItemPromptScreenForPacking(param+index);
				packAndPrintSteps.whenUserEnterItem();
			}
			index++;
		}
	
		
	}

	/**
	 * Complete Pack and Print Singles transaction 
	 * @param menu
	 * @param example
	 */
	public void packingByPackAndPrintSingles(String menu, ExamplesTable example) {
		PackAndPrintSteps packAndPrintSteps = new PackAndPrintSteps();
		patrollingUtils.selectingMenu(menu);
		List<Map<String,String>> exRows=example.getRows();
		exRows.stream().forEach(m ->{
			m.forEach((k,v)->{
				if(k.equals("Printer")  && !v.equalsIgnoreCase("NA")) {
					try {
						packAndPrintSteps.packageNprintScreen(v);
						packAndPrintSteps.whenUserEnterToteOrPackage(k);
						packAndPrintSteps.successfullyPrinterScanN("Scan Tote or Package or Resv #");// input:Scan Tote or Package
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}else if(k.equals("Tote") && !v.equalsIgnoreCase("NA")) {
					try {
						packAndPrintSteps.thenUserGetstotefromPicking(v);
						packAndPrintSteps.whenUserEnterToteOrPackageInPackAandPrint(k);
						packAndPrintSteps.successfullyToteScan("Scan Item");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(k.equals("Item") && !v.equalsIgnoreCase("NA")) {
					if(v.equalsIgnoreCase("Same")){
						packAndPrintSteps.whenUserEnterItem();
					}else if(v.equalsIgnoreCase("End Tote")){
						packAndPrintSteps.whenUserPressesF4AndAcceptsInfoMsg();
					}else{
						packAndPrintSteps.givenUserAtItemPromptScreenForPacking(v);//input:item from tote detail index-1
						packAndPrintSteps.whenUserEnterItem();
					}
				}else if(k.equals("ScreeName") && !v.equalsIgnoreCase("NA")) {
						try {
							
							packAndPrintSteps.itemScanSuccessfullPromptedForEndToteOrScanItem(v);//input:End of Tote
							DataGeneration.SinglesEndToteFlag = false;
						} catch (Exception e) {
							e.printStackTrace();
						}
				}else if(k.equals("Scan Package") && !v.equalsIgnoreCase("NA")) {
					try {
						packAndPrintSteps.givenUserAtItemPromptScreenForPacking(v);
						packAndPrintSteps.whenUserEnterToteOrPackage("Package", "1", "", "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(k.equals("PackNPrintEndToteFlag")&& !v.equalsIgnoreCase("NA")){
					PackAndPrintSteps.endToteFlag = Boolean.parseBoolean(v);
				}else if(k.equals("SinglesEndToteFlag")&& !v.equalsIgnoreCase("NA")){
					DataGeneration.SinglesEndToteFlag = Boolean.parseBoolean(v);
				}
			});
		 }
		);
		
	}
	
	
	/**
	* This method implemented for validating error message
	* @param type
	* @param application
	* @param key
	* @throws SQLException
	*/
	@Then("Display user message for type $type, appltn $application and key $key")
	public void userMessage(String type, String application, String key) throws SQLException{
		String sql = "select text from user_message where type = '"+type+"' and application = '"+application+"' and key = '"+key+"'";
		String message = DBMethods.getDBValueInString(sql, "").trim();
		String screenMessage = conn.screen.message.toString().trim();
		if(key.equalsIgnoreCase("NO_ITEM_INVENTORY")){
			message = message.replace(message.substring(message.length()-1), "");
		}if(message.equalsIgnoreCase(screenMessage)){
			log.info("Expected message: "+message+" ; Actual message: "+screenMessage);
			Assert.assertTrue(true, "Display user message "+message);
			StepDetail.addDetail("Display user message "+message, true);
		}else{
			log.info("Expected message "+message+" ; Actual message"+screenMessage);
			Assert.assertTrue(false, "Display user message "+message);
			StepDetail.addDetail("Display user message "+message, false);
		}
	}

}
