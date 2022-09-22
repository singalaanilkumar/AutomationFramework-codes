package com.macys.mst.Atlas.utilmethods;

import com.macys.mst.artemis.selenium.LocalDriverManager;
import com.macys.mst.artemis.selenium.WebDriverListener;
import com.macys.mst.wavefunction.utils.GetAndSetValues;
import com.macys.wms.selenium.util.ApolloUtils;
import com.macys.wms.systests.*;
import com.macys.wms.util.TalosUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.model.ExamplesTable;
import org.openqa.selenium.WebDriver;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PickingUtilImpl implements PickingUtil{
	public long TestNGThreadID = Thread.currentThread().getId();
	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = WebDriverListener.EnvMap.get(TestNGThreadID);
		WebDriverListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}
	public static  WebDriver e2eDriver = LocalDriverManager.getInstance().getDriver();
	

	@Override
	public void completePickByWorkId(ExamplesTable example) {
		PickingSteps pickingSteps=new PickingSteps();
		List<Map<String,String>> exRows=example.getRows();
		exRows.stream().forEach(m ->{
			m.forEach((k,v)->{
				if(k.equals("Scan Work") && !v.equalsIgnoreCase("Same")) {
					WMSASSteps.pickWaveNumber=GetAndSetValues.waveNBr;
					pickingSteps.userEnteredSpecifiedWorkId(Integer.parseInt(v));
					 pickingSteps.thenSuccessfullWorkIdScanNTotePrompt();
				}else if(k.equals("Scan Tote") && !v.equalsIgnoreCase("NA")) {
					 try {
						pickingSteps.givenTote(v);
						pickingSteps.thenSuccessfullToteScanNItemPrompt();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}else if(k.equals("Scan Item") && !v.equalsIgnoreCase("NA")) {
					pickingSteps.givenItem(v);
					
				}else if(k.equals("Scan Qty") && !v.equalsIgnoreCase("NA")) {
					pickingSteps.whenUserEntersQuantity(v);
				}else if(k.equals("Not In Location") && !v.equalsIgnoreCase("NA")) {
					pickingSteps.userPressNotInLocation(v);
				}else if(k.equals("End tote") && !v.equalsIgnoreCase("NA")){
					pickingSteps.userEndsTote(v);// input: Item Prompt or Qty Prompt
				}else if(k.equals("Skip Qty/Item") && !v.equalsIgnoreCase("NA")){
					pickingSteps.whenUserPressesF2AndSkipsTheItem(v); //input: Qty or Item
				}
			});
		 }
		);
	}

	@Override
	public void completeExceptionPick(ExamplesTable example) {
		PickingSteps pickingSteps=new PickingSteps();
		List<Map<String,String>> exRows=example.getRows();
				
		exRows.stream().forEach(m ->{
			m.forEach((k,v)->{
				if(k.equals("Work Index")) {
					WMSASSteps.indexWorkId=1;
					WMSASSteps.pickWaveNumber=GetAndSetValues.waveNBr;
				}else if(k.equals("Scan Tote") && !v.equalsIgnoreCase("NA")) {
					 try {
							pickingSteps.givenTote(v);
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}else if(k.equals("Scan Item")) {
					pickingSteps.givenItem(v);
				}else if(k.equals("Scan Qty")) {
					pickingSteps.whenUserEntersQuantity(v);
				}else if(k.equals("Scan Location")) {
					//pickingSteps.setIndex();
					pickingSteps.enterLocnForPickToTote();
					//wms.indexWorkId=2;
				}
				
			});
		 }
		);
	}

	//Modified for Hold And Flow
	@Override
	public void completePickToTote(ExamplesTable example) 
	{
		PickingSteps pickingSteps=new PickingSteps();
		PickToToteMultipleWorks pickToToteMultipleWorks=new PickToToteMultipleWorks();
		List<Map<String,String>> exRows=example.getRows();
		exRows.stream().forEach(m ->
		{
			//|Work	|Detail	|Scan location|Pop-Up|Scan Tote|Scan Item|Scan Qty|NIL|
			m.forEach((k,v)->
			{
				int workNum=1,workDtlNum=1;
				String scanType="Qty";
				if(k.equals("Work"))
				{
					workNum=Integer.parseInt(v);
				}
				else if(k.equals("Detail"))
				{
					workDtlNum=Integer.parseInt(v);
				}
				else if(k.equals("Scan Tote")) 
				{
					try {
						pickToToteMultipleWorks.validateScreenInP2T(k, workNum, workDtlNum);
						pickingSteps.givenTote(v);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else if(k.equals("Scan Item")) 
				{
					scanType=v.trim();
					pickToToteMultipleWorks.validateScreenInP2T(k, workNum, workDtlNum);
				}
				else if(k.equals("Scan Qty")) 
				{
					pickToToteMultipleWorks.validateScreenInP2T("Enter Quantity", workNum, workDtlNum);
					pickToToteMultipleWorks.pickSpecificNumberOfUnits(Integer.parseInt(v.trim()),scanType);
				}
				else if(k.equals("Scan Location")&& !v.equalsIgnoreCase("NA")) 
				{
					pickToToteMultipleWorks.enterLocationBasedOnWorkAndWorkDetail("Pick to Tote", workNum, workDtlNum);
				}
				else if(k.equals("NIL"))
				{
					pickingSteps.userPressNotInLocation(v.trim());
				}
				else if(k.equals("Pop-Up")&& !v.equalsIgnoreCase("NA"))
				{
					String str[]=v.trim().split("-");
					String action=str[0],msg=str[1];
					pickToToteMultipleWorks.handlePopUpInPickToTote(action, msg);
				}
			});
		 }
		);
		
	}
	
	/**
	 * Splitting string based on break point
	 * @param <E>
	 * @param mainString
	 * @param breakPoint
	 * @return
	 */
	public static <E> List<E> breakValuesBasedOnChar(String mainString,String breakPoint) {
		List<E> str=(List<E>) Arrays.asList(mainString.split(breakPoint));
		return str;
	}
	
	
	public <T> T createAcc() {
		return null;
		
	}

	public void cancellingUnassignedPicks(ExamplesTable example) {
		for (Map<String, String> row : example.getRows()){
			String mainMenu = row.get("mainMenu").trim();
			String subMenu = row.get("subMenu").trim();
			String button =row.get("CancelUnassignedPicksButton").trim();
			System.out.println(e2eDriver);
			ApolloUtils apolloUtils = new ApolloUtils();
			apolloUtils.loginAtlas(mainMenu);
			apolloUtils.selectSubMenu(subMenu);
			apolloUtils.fiterOutResults();
			if(button.equalsIgnoreCase("Click")){
				apolloUtils.buttonDisabled("enabled", "visible");
				apolloUtils.clickCancelUnassignButton();
			}
		}
	}

	@Override
	public void completeBeautyPickCart(ExamplesTable example) {
		
		PickToToteNew pickCart=new PickToToteNew();
		PickCartSteps pickCartStep = new PickCartSteps();
		WMSASSteps.pickWaveNumber=GetAndSetValues.waveNBr;
		List<Map<String,String>> exRows=example.getRows();
		exRows.stream().forEach(m ->{
			m.forEach((k,v)->{
				if(k.equals("Scan Work") && !v.equalsIgnoreCase("NA")) {
					pickCart.userEntersF4ScanWork();
					pickCart.userEnterworkId();
				}else if(k.equals("Scan Cart") && !v.equalsIgnoreCase("NA")) {
					if(v.isEmpty()){
						pickCartStep.scanCartBtyCart();
						pickCartStep.validateScanCart();
					}else{
						pickCart.scanCart();
						pickCart.validateMtoteStatus(v);
					}
				}else if((k.equals("Scan Location1") || k.equals("Scan Location2")) && !v.equalsIgnoreCase("NA")) {
					if(v.equalsIgnoreCase("From_Picking")){
						 pickCart.enterLocnForPickCart();
					}else if(v.equalsIgnoreCase("Again")){
						 pickCart.enterLocation();
					}
				}else if(k.equals("Scan Qty") && !v.equalsIgnoreCase("NA")) {
					     pickCart.enterQty(v);
				}else if(k.equals("Enter Slot") && !v.equalsIgnoreCase("NA")){
					if(v.isEmpty()){
						pickCart.enterPickCartSlot();
					}else{
						pickCart.enterSlot(v);
					}
				}else if(k.equals("Work_Hdr Status") && !v.equalsIgnoreCase("NA")){
					TalosUtils.setWorkBatchNumber(GetAndSetValues.waveNBr+"001");
					BeumerPackingSteps beumerPacking=new BeumerPackingSteps();
					beumerPacking.validateMWorkHdr(v);
				}else if(k.equals("Pressing F2") && v.equalsIgnoreCase("shorts UPC")){
					pickCart.itemShorts();
				}
				else if(k.equals("Scan Item") && !v.equalsIgnoreCase("NA")){
					pickCart.enterItemInPickCart(Integer.parseInt(v));
				}else if(k.equals("Pressing F8") && v.equalsIgnoreCase("scan Cart")){
					pickCart.userPressesF8();
					pickCart.scanCartonF8();
				}else if(k.equals("validate msg") && !v.equalsIgnoreCase("NA")){
					pickCart.validatePopUpMessages(v);//Remove Incomplete Slot From Cart
				}else if(k.equals("Scan Printer") && !v.equalsIgnoreCase("NA")){
					pickCart.scanPrinterInPickCart(v);
				}else if(k.equals("Scan Exception slot") && !v.equalsIgnoreCase("NA")){
					pickCart.scanExceptionSlot();
				}else if(k.equals("Scan Exception Locn") && !v.equalsIgnoreCase("NA")){
					pickCart.scanExceptionLocation();
				}
			});
		 }
		);
	}
	
	public void completeClearPickCart(ExamplesTable example) {
		PickCartSteps pickCart = new PickCartSteps();
		List<Map<String,String>> exRows=example.getRows();
		exRows.stream().forEach(m ->{
			m.forEach((k,v)->{
				if(k.equals("Scan Cart") && !v.equalsIgnoreCase("NA")) {
					//pickCart.validateCartIsValid();
					pickCart.scanPickcart();
				}else if(k.equals("Validate Work_Hdr") && !v.equalsIgnoreCase("NA")){
					String[] value = v.split("&");
					pickCart.validateMWorkHdrClearCart(value[0], value[1]);
				}else if(k.equals("Validate Cart Status") && !v.equalsIgnoreCase("NA")){
					pickCart.validatecartStatus(Integer.parseInt(v));
				}else if(k.equals("Validate Work_log") && !v.equalsIgnoreCase("NA")){
					String[] value = v.split("&");
					pickCart.validateWorkLog(value[0], value[1]);
				}
			});
		});
	}
}
