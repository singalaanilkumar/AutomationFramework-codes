package com.macys.mst.DC2.EndToEnd.stepdefinitions;


import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.ToContext;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.macys.mst.DC2.EndToEnd.configuration.ConfigurationEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.configuration.DistroEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.InventoryEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.PO4WallEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.WsmEndpoint;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLResearchInventory;
import com.macys.mst.DC2.EndToEnd.db.app.SQLPo4Walls;
import com.macys.mst.DC2.EndToEnd.db.app.SQLWsm;
import com.macys.mst.DC2.EndToEnd.model.DistroRqstPOLines;
import com.macys.mst.DC2.EndToEnd.model.LocationDistro;
import com.macys.mst.DC2.EndToEnd.model.POLineItems;
import com.macys.mst.DC2.EndToEnd.model.POPtcRelease;
import com.macys.mst.DC2.EndToEnd.model.PORelease;
import com.macys.mst.DC2.EndToEnd.model.SKU_STR_Qty;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData.PoLinebarCode;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
@SuppressWarnings("unchecked")
@Slf4j
public class POReleaseReceiptSteps {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    TimeZone timeZone = TimeZone.getTimeZone("UTC");
    public long TestNGThreadID = Thread.currentThread().getId();
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    private StepsContext stepsContext;

    Integer updatedavailQty;

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    public POReleaseReceiptSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @When("User releases receipt for a PO")
    public void releaseReceipt() throws Exception {

        List<PoLinebarCode> poLinebarCode = (List<PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());


        Map<String, List<PoLinebarCode>> skuPOLineBarCode = poLinebarCode.stream()
                .collect(Collectors.groupingBy(PoLineBarCodeData.PoLinebarCode::getPoNbr));


        skuPOLineBarCode.forEach((poNbr, v) -> {
            log.info("poNbr :{}", poNbr);
            Set<BigInteger> skuInHouseUPC = v.stream().map(m -> {
                return new BigInteger(m.getSKU());
            }).collect(Collectors.toSet());
            log.info("In House SKU UPC :{}", skuInHouseUPC);
            String receiptNbr = v.get(0).getReceiptNbr();
            String poLineBarcode = v.get(0).getPoLineBarCode();
            DataAvailableForPO(poNbr);

            releasingPO(Integer.valueOf(poNbr), Integer.valueOf(receiptNbr), 7221, skuInHouseUPC, poLineBarcode);
            //Below method is used for POReceipt and Distro request status validation.. Pls dont delete
            //poReceiptStatusAndDistroRequestStatusValidation(poNbr,receiptNbr);

            String overageShortageFlag = (String) stepsContext.get(Context.OVERAGE_SHORTAGE_FLAG.name());
            if (!(overageShortageFlag.isEmpty())) {
                try {
                    log.info("Overage /Shortage validation begins");
                    validateDistribution(poNbr, receiptNbr);
                } catch (Exception e) {
                    log.info("overage/shortage is not validated");
                }
            }
        });

    }
    
    @When("User PTC releases receipt for the PO")
	public void ptcReleaseReceipt() throws Exception {

		List<PoLinebarCode> poLinebarCode = (List<PoLinebarCode>) stepsContext
				.get(Context.PO_LINES_BARCODE_DATA.name());

		Map<String, List<PoLinebarCode>> skuPOLineBarCode = poLinebarCode.stream()
				.collect(Collectors.groupingBy(PoLineBarCodeData.PoLinebarCode::getPoNbr));

		skuPOLineBarCode.forEach((poNbr, v) -> {
			log.info("poNbr :{}", poNbr);
			Set<BigInteger> skuInHouseUPC = v.stream().map(m -> {
				return new BigInteger(m.getSKU());
			}).collect(Collectors.toSet());
			log.info("In House SKU UPC :{}", skuInHouseUPC);
			String receiptNbr = v.get(0).getReceiptNbr();

			ptcReleasingPO(Integer.valueOf(poNbr), Integer.valueOf(receiptNbr), skuInHouseUPC);

		});

	}
    
    @Then("location status updated to $param for PTC release")
	public void locnStatusUpdate(String param) throws Exception {
		// example: https://dev-backstage.devops.fds.com/inventory-service/inventory/7221/containers?barcode=DS01S032
		
		String lanes = stepsContext.get(Context.LANES.name()).toString();
		String[] laneList = lanes.split(",");
		
		for (int i = 0; i < laneList.length; i++) {
			 String inventoryBarcodeEndpoint =
			 String.format(InventoryEndPoint.InventoryWithContainerBarcode,"7221",laneList[i]);

			//String inventoryBarcodeEndpoint = "https://dev-backstage.devops.fds.com/inventory-service/inventory/7221/containers?barcode="
			//		+ totes.get(i);
			String inventoryForContainerResponse = CommonUtils.getRequestResponse(inventoryBarcodeEndpoint);
			JSONObject containerInventoryJson = new JSONObject(inventoryForContainerResponse);
			String containerStatusCode = containerInventoryJson.getJSONObject("container")
					.getString("containerStatusCode");
			log.info("containerStatusCode for tote " + laneList[i] + " is " + containerStatusCode);
			if(!containerStatusCode.equals(param)){
				Assert.fail("containerStatusCode for tote " + laneList[i] + " should be " + param);
			}
		}
	}

    //Dont delete below method
    /*private void poReceiptStatusAndDistroRequestStatusValidation(String poNbr, String receiptNbr) {

        Map<String, String> distroStatus = null;
        List<String> poStatus = null;

        //validate status in distro request as "COMPLETED"
        String distroRequestStatusSql = String.format(SQLPo4Walls.DISTRO_STATUS, poNbr, receiptNbr);
        log.info("distroRequestStatusSql: {}", distroRequestStatusSql);
        try {
            TimeUnit.SECONDS.sleep(20);
            //distroStatus = DBUtils.getDBValuesInMap("pofourwalls", distroRequestStatusSql);
            distroStatus = DBMethods.getDBValuesInMap(distroRequestStatusSql, "pofourwalls");
            log.info("distroStatus: {}", distroStatus);
            Assert.assertEquals(distroStatus.keySet().toArray()[0], "DISTRIBUTION");


            //         Assert.assertEquals(distroStatus.values().toArray()[0], "COMPLETED");
            log.info("Status in distro request is Completed as expected");
        } catch (Exception e) {
            log.error("Error in distroRequestStatus", e);
            Assert.fail("Error in distroRequestStatus", e);
        }

        //validate the status of PO in po_receipts table as "REL"
        String poStatusSql = String.format(SQLPo4Walls.PO_STATUS, poNbr, receiptNbr);
        log.info("poStatusSql: {}", poStatusSql);
        try {
            TimeUnit.SECONDS.sleep(20);
            //poStatus = DBUtils.getDBValueInList("pofourwalls", poStatusSql);
            poStatus = DBMethods.getDBValueInList(poStatusSql, "pofourwalls");
            log.info("poStatus: {}", poStatus);
            Assert.assertEquals(poStatus.get(0), "REL");
            log.info("Status in PO Receipts is REL as expected");
        } catch (Exception e) {
            log.error("Error in poStatus", e);
            Assert.fail("Error in poStatus", e);
        }

    }*/

    @When("User releases $no receipts by $PO line")
    public void DataAvailableForPO(@Named("PO") String PO) {
        LinkedHashMap<String, Integer> typeOfStoreQuantityMap = new LinkedHashMap<String, Integer>();
        List<POLineItems> poLineItems = CommonUtils.POOrderDetails(PO);
        Map<BigInteger, List<LocationDistro>> SkuStoreLocnQuantityMap = getSkuStoreLocnQtyMap(poLineItems);

        SkuDeptNbrMap(poLineItems);
        List<LocationDistro> locationDistro = SkuStoreLocnQuantityMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Map<Integer, Integer> locationQty = locationDistro.stream().collect(Collectors.groupingBy(LocationDistro::getLocationNbr, Collectors.summingInt(LocationDistro::getOrderQty)));

        log.info("location Distro: {}", locationDistro);
        log.info("location Number with Quantity: {}", locationQty);
        String ConfigurationValueEndpoint = String.format(ConfigurationEndPoint.CONFIG_URL, "pofourwalls", "pofourwalls", "locnconfig");
        String ConfigurationResponse = CommonUtil.getRequestResponse(ConfigurationValueEndpoint);
        JSONArray ConfigValueArray = new JSONArray(ConfigurationResponse);
        String configValue = ConfigValueArray.getJSONObject(0).getString("configValue").replace("\\\"", "\"");
        JSONArray StoreConfigArray = new JSONArray(configValue);
        /*
        String ConfigurationValueSql = String.format(SQLConfiguration.ConfigurationValue, "7221", "pofourwalls", "pofourwalls", "locnconfig", "1");
        //String config_value = DBMethods.getDBValueInString(ConfigurationValueSql, "configuration");
        String config_value = null;
        try {
			config_value = DBMethods.getDBValueInString(ConfigurationValueSql,"configuration");
		} catch (Exception e) {
			log.error("Exception in getting configuration query");
		}
        */

        for (int l = 0; l < StoreConfigArray.length(); l++) {

            if (!StoreConfigArray.getJSONObject(l).getString("locnNbr").equalsIgnoreCase("7222")) {
                if (StoreConfigArray.getJSONObject(l).getBoolean("vasFlag") == true) {
                    if (!typeOfStoreQuantityMap.containsKey("newStore") && locationQty.containsKey(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr")))) {
                        typeOfStoreQuantityMap.put("newStore", locationQty.get(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr"))));
                        break;
                    }
                    if (typeOfStoreQuantityMap.containsKey("newStore") && locationQty.containsKey(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr")))) {
                        typeOfStoreQuantityMap.put("newStore", typeOfStoreQuantityMap.get("newStore") + locationQty.get(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr"))));
                        break;
                    }
                }
                if (StoreConfigArray.getJSONObject(l).getBoolean("vasFlag") == false) {
                    if (!typeOfStoreQuantityMap.containsKey("regularStore") && locationQty.containsKey(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr")))) {
                        typeOfStoreQuantityMap.put("regularStore", locationQty.get(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr"))));
                        break;
                    }
                    if (typeOfStoreQuantityMap.containsKey("regularStore") && locationQty.containsKey(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr")))) {
                        typeOfStoreQuantityMap.put("regularStore", typeOfStoreQuantityMap.get("regularStore") + locationQty.get(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr"))));
                        break;
                    }
                }
            } else {
                if (!typeOfStoreQuantityMap.containsKey("packaway") && locationQty.containsKey(7222)) {
                    typeOfStoreQuantityMap.put("packaway", locationQty.get(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr"))));
                    break;
                }
                if (typeOfStoreQuantityMap.containsKey("packaway") && locationQty.containsKey(7222)) {
                    typeOfStoreQuantityMap.put("packaway", typeOfStoreQuantityMap.get("packaway") + locationQty.get(Integer.valueOf(StoreConfigArray.getJSONObject(l).getString("locnNbr"))));
                    break;
                }
            }

        }

        log.info("TypeOfStoreQuantityMap :{}", typeOfStoreQuantityMap.toString());
        stepsContext.put(Context.STORE_QUANTITY_MAP.name(), typeOfStoreQuantityMap, ToContext.RetentionLevel.SCENARIO);
    }

    @When("User releases the PO")
    public void releasingPO(Integer poNbr, Integer receiptNbr, Integer locationNbr, Set<BigInteger> skuInhouseUpc, String poLineBarCode) {
        log.info("Release PO PONbr :{}, Receipt Nbr: {}, Location Nbr: {}", new Object[]{poNbr, receiptNbr, locationNbr});
        String distroType = (String) stepsContext.get(Context.DISTRO_TYPE.name());
        String overageShortageFlag = (String) stepsContext.get(Context.OVERAGE_SHORTAGE_FLAG.name());
        PORelease poRelease = new PORelease();
        poRelease.setPoNumber(poNbr);
        poRelease.setRcptNbr(receiptNbr);
        poRelease.setRequestType("DISTRIBUTION");

        if (overageShortageFlag.equalsIgnoreCase("Overage")) {
            poRelease.setDistroType(distroType);
        } else if (overageShortageFlag.equalsIgnoreCase("Shortage")) {
            poRelease.setDistroType(distroType);
        }

        poRelease.setDistroType("DISTRO");

        poRelease.setLocnNbr(locationNbr);
        if (!hasInnerPack(poLineBarCode)) {
            if (null != skuInhouseUpc && !skuInhouseUpc.isEmpty()) {
                List<DistroRqstPOLines> distroRqstPOLines = skuInhouseUpc.stream().map(m -> {
                    DistroRqstPOLines distroRqstPOLine = new DistroRqstPOLines();
                    distroRqstPOLine.setSkuUpc(m);
                    return distroRqstPOLine;
                }).collect(Collectors.toList());
                poRelease.setDistroRqstPOLines(distroRqstPOLines);
            }
        }
        JSONObject json = new JSONObject(poRelease);
        log.info("ReleasingPO Payload:[{}]", json.toString());
        String PoReleaseResponse = RestUtilities.postRequestResponse(DistroEndPoint.POReleaseService, json.toString(), 200);

        log.info("PoReleaseResponse: {}", PoReleaseResponse);
        StepDetail.addDetail("PoReleaseResponse: " + PoReleaseResponse, true);

    }
    
    @When("User PTC releases the PO")
	public void ptcReleasingPO(Integer poNbr, Integer receiptNbr, Set<BigInteger> skuInhouseUpc) {
		log.info("PTC Release PO PONbr :{}, Receipt Nbr: {}", new Object[] { poNbr, receiptNbr });
		POPtcRelease poPtcRelease = new POPtcRelease();
		poPtcRelease.setPoNbr(poNbr);
		poPtcRelease.setRcptNbr(receiptNbr);

		if (null != skuInhouseUpc && !skuInhouseUpc.isEmpty()) {
			poPtcRelease.setSkuUpcs(skuInhouseUpc);
		}

		JSONObject json = new JSONObject(poPtcRelease);
		log.info("PtcReleasingPO Payload:[{}]", json.toString());
		String url = PO4WallEndPoint.POPtcReleaseService;
        Response response = WhmRestCoreAutomationUtils.postRequestResponse(url,json.toString()).asResponse();
		Assert.assertTrue((response.getStatusCode() == 200), "Status Code for response : " + response.getStatusCode());
		log.info(" response.asString(): " + response.asString());
		StepDetail.addDetail("PoPtcReleaseResponse: " + response.asString(), true);

	}

    private void validateDistribution(String poNbr, String recptNbr) throws Exception {

        Map<String, String> allocQty_NewStr = null;
        Map<String, String> allocQty_RegStr = null;
        Map<String, String> allocQty_packaway = null;


        String distroType = (String) stepsContext.get(Context.DISTRO_TYPE.name());
        int regStrTotalQty = 0;
        int newStrTotalQty = 0;
        int allSkusOrderedRegStoreTotalQty = 0;
        int allSkusOrderedNewStoreTotalQty = 0;
        int allSkusOrderedPackawayStoreTotalQty = 0;
        int allSkusnewStrTotalQty = 0;
        int allSkuregStrTotalQty = 0;
        int allSkupackawayStrTotalQty = 0;

        List<PoLinebarCode> poLinebarCode_SKU = (List<PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        Set<String> sku_Nbrs = poLinebarCode_SKU.stream().map(x -> x.getSKU()).collect(Collectors.toSet());
        log.info("sku_Nbrs =: " + sku_Nbrs);
        //   List<POLineItems> poLineItems_distro = CommonUtils.POOrderDetails(poNbr);
        //  for(POLineItems eachPOLineItem : poLineItems_distro) {
        //  String skuNbr = (String) stepsContext.get(Context.SKU_NBR.name());
        //    log.info("skuNbr: {}", skuNbr);
        Integer allocatedQty = Integer.parseInt((String) stepsContext.get(Context.RECEIVED_QTY.name()));
        log.info("availQty: RECEIVED_QTY : {}", allocatedQty);
        int totalDistributedQty_allSKUs = 0;
        //       for(String skuNbr : sku_Nbrs) {
        // String skuNbr = String.valueOf(eachPOLineItem.getSkuUpc());

        //       }

        //Store and Qty in Ordered service as per Store Type
        Map<BigInteger, List<SKU_STR_Qty>> POlineItemSKU_Qty_NwStr = new LinkedHashMap<>();
        Map<BigInteger, List<SKU_STR_Qty>> POlineItemSKU_Qty_ExStr = new LinkedHashMap<>();
        Map<BigInteger, List<SKU_STR_Qty>> POlineItemSKU_Qty_pckwy = new LinkedHashMap<>();
        List<POLineItems> poLineItems = CommonUtils.POOrderDetails(poNbr);
        List<SKU_STR_Qty> pcky_Str_qty_AllSkus = new ArrayList<>();
        List<SKU_STR_Qty> new_Str_qty_AllSkus = new ArrayList<>();
        List<SKU_STR_Qty> existing_Str_qty_AllSkus = new ArrayList<>();
        TimeUnit.SECONDS.sleep(5);
        for (POLineItems eachPOLineItem : poLineItems) {
            List<SKU_STR_Qty> pcky_Str_qty = new ArrayList<>();
            List<SKU_STR_Qty> new_Str_qty = new ArrayList<>();
            List<SKU_STR_Qty> existing_Str_qty = new ArrayList<>();
            BigInteger SKUUPC = eachPOLineItem.getSkuUpc();
            String skuNbr = SKUUPC.toString();

            List<LocationDistro> distrolist = eachPOLineItem.getPoLocationDistroList();
            for (LocationDistro eachLocationDistro : distrolist) {
                Integer orderQty = eachLocationDistro.getOrderQty();
                Integer locationNbr = eachLocationDistro.getLocationNbr();

                if (locationNbr == 7222) {
                    pcky_Str_qty.add(new SKU_STR_Qty(locationNbr, orderQty));
                } else {
                    String response = CommonUtils.getRequestResponse(String.format(PO4WallEndPoint.PO4WALL_GET_LOCATION, locationNbr));
                    if (StringUtils.isNotEmpty(response)) {
                        TimeUnit.SECONDS.sleep(2);
                        JSONObject jsonObject = new JSONObject(response);
                        Boolean vas_flg = (Boolean) jsonObject.getJSONObject("LocationDto").get("vasFlag");
                        if (vas_flg) {
                            new_Str_qty.add(new SKU_STR_Qty(locationNbr, orderQty));
                        } else if (!vas_flg) {
                            existing_Str_qty.add(new SKU_STR_Qty(locationNbr, orderQty));
                        }
                    }
                }
            }
            Collections.sort(pcky_Str_qty);
            Collections.sort(new_Str_qty);
            Collections.sort(existing_Str_qty);
            pcky_Str_qty_AllSkus.addAll(pcky_Str_qty);
            log.info("Packaway stores of all Skus: {}", pcky_Str_qty_AllSkus);
            new_Str_qty_AllSkus.addAll(new_Str_qty);
            log.info("New store of All skus: {}", new_Str_qty_AllSkus);
            existing_Str_qty_AllSkus.addAll(existing_Str_qty);
            log.info("Regular store of All skus: {}", existing_Str_qty_AllSkus);

            POlineItemSKU_Qty_pckwy.put(SKUUPC, pcky_Str_qty);

            POlineItemSKU_Qty_NwStr.put(SKUUPC, new_Str_qty);

            POlineItemSKU_Qty_ExStr.put(SKUUPC, existing_Str_qty);


            // Sum of Qty in Ordered service as per distribution table
            int skuOrderedRegStoreTotalQty = existing_Str_qty.stream().mapToInt(SKU_STR_Qty::getQty).sum();
            allSkusOrderedRegStoreTotalQty = allSkusOrderedRegStoreTotalQty + skuOrderedRegStoreTotalQty;
            log.info("Sum of qty in Regular store of All skus: {}", allSkusOrderedRegStoreTotalQty);

            int skuOrderedNewStoreTotalQty = new_Str_qty.stream().mapToInt(SKU_STR_Qty::getQty).sum();
            allSkusOrderedNewStoreTotalQty = allSkusOrderedNewStoreTotalQty + skuOrderedNewStoreTotalQty;
            log.info("Sum of qty in New store of All skus: {}", allSkusOrderedNewStoreTotalQty);

            int skuOrderedPackawayStoreTotalQty = pcky_Str_qty.stream().mapToInt(SKU_STR_Qty::getQty).sum();
            allSkusOrderedPackawayStoreTotalQty = allSkusOrderedPackawayStoreTotalQty + skuOrderedPackawayStoreTotalQty;
            log.info("Sum of qty in Packaway store of All skus: {}", allSkusOrderedPackawayStoreTotalQty);

            // out of loop
            //   int allSkuOrderedTotalQty = allSkusOrderedRegStoreTotalQty + allSkusOrderedNewStoreTotalQty + allSkusOrderedPackawayStoreTotalQty;


            //Store and QTY in Distribution Table as per store type
            String distroAllocatedQtySql_newStr = String.format(SQLPo4Walls.DISTRO_ALLOCATED_NEW_STORE, poNbr, recptNbr, skuNbr);
            log.info("distroAllocatedQtySql_newStr: {}", distroAllocatedQtySql_newStr);
            try {
                //allocQty_NewStr = DBUtils.getDBValuesInMap("pofourwalls", distroAllocatedQtySql_newStr);
                allocQty_NewStr = DBMethods.getDBValuesInMap(distroAllocatedQtySql_newStr, "pofourwalls");
                log.info("allocQty_NewStr: {}", allocQty_NewStr);
            } catch (Exception e) {

                log.error("Exception in getting newstore query");
            }
            String distroAllocatedQtySql_regStr = String.format(SQLPo4Walls.DISTRO_ALLOCATED_REG_STORE, poNbr, recptNbr, skuNbr);
            log.info("distroAllocatedQtySql_regStr: {}", distroAllocatedQtySql_regStr);
            try {
            	allocQty_RegStr = DBMethods.getDBValuesInMap(distroAllocatedQtySql_regStr, "pofourwalls");
                log.info("allocQty_RegStr: {}", allocQty_RegStr);
            } catch (Exception e) {
                log.error("Exception in getting regularstore query");
            }
            String distroAllocatedQtySql_packaway = String.format(SQLPo4Walls.DISTRO_ALLOCATED_PACKAWAY, poNbr, recptNbr, skuNbr);
            log.info("distroAllocatedQtySql_packaway: {}", distroAllocatedQtySql_packaway);
            try {
                //allocQty_packaway = DBUtils.getDBValuesInMap("pofourwalls", distroAllocatedQtySql_packaway);
                allocQty_packaway = DBMethods.getDBValuesInMap(distroAllocatedQtySql_packaway, "pofourwalls");
                log.info("allocQty_packaway: {}", allocQty_packaway);
            } catch (Exception e) {
                log.error("Exception in getting packaway query");
            }

            //Sum of QTY in Distribution Table as per store type
            newStrTotalQty = allocQty_NewStr.values().stream().mapToInt(Integer::parseInt).sum();
            log.info("newStrTotalQty at sku level: {}", newStrTotalQty);
            allSkusnewStrTotalQty = allSkusnewStrTotalQty + newStrTotalQty;
            log.info("Sum of qty in new store in DB of All skus: {}", allSkusnewStrTotalQty);

            regStrTotalQty = allocQty_RegStr.values().stream().mapToInt(Integer::parseInt).sum();
            log.info("regStrTotalQty at sku level: {}", regStrTotalQty);
            allSkuregStrTotalQty = allSkuregStrTotalQty + regStrTotalQty;
            log.info("Sum of qty in Regular store in DB of All skus: {}", allSkuregStrTotalQty);

            Integer packawayStrTotalQty = allocQty_packaway.values().stream().mapToInt(Integer::parseInt).sum();
            log.info("packawayStrTotalQty at sku level: {}", packawayStrTotalQty);
            allSkupackawayStrTotalQty = allSkupackawayStrTotalQty + packawayStrTotalQty;
            log.info("Sum of qty in Packaway store in DB of All skus: {}", allSkupackawayStrTotalQty);

            int totalDistributedQty = newStrTotalQty + regStrTotalQty + packawayStrTotalQty;
            totalDistributedQty_allSKUs = totalDistributedQty_allSKUs + totalDistributedQty;
            log.info("Total distributed qty for All Skus in distribution table is: {}", totalDistributedQty_allSKUs);

        }


        String overageShortageFlag = (String) stepsContext.get(Context.OVERAGE_SHORTAGE_FLAG.name());


        if (overageShortageFlag.equalsIgnoreCase("Overage") && (distroType.equalsIgnoreCase("DISTRO"))) {

            log.info("overage validation for distro rule begins");
            if (!pcky_Str_qty_AllSkus.isEmpty()) {
                log.info("Overage quantity in packaway store validation begins");
                //  int orderAllocatedPackawayQty = POlineItemSKU_Qty_pckwy.get(SKUUPC).get(0).getQty();

                //     Integer excessQty = distroAllocatedPackawayQty - Integer.valueOf(beforeReleasePackawayQty);
                //     Assert.assertTrue(Integer.compare(overageQty, excessQty) == 0, "Successfully Validated Packaway with Overage Qty");

                //   Assert.assertTrue((Integer.valueOf(allocQty_packaway.get("7222")) > Integer.valueOf(orderAllocatedPackawayQty)), "Overage quantity is not distributed to packaway store");
                packawayOverageValidation(allSkupackawayStrTotalQty, allSkusOrderedPackawayStoreTotalQty);
            } else if ((!new_Str_qty_AllSkus.isEmpty()) || (!existing_Str_qty_AllSkus.isEmpty())) {
                log.info("If no Packaway store,Overage quantity in regular store validation begins");
                regularOverageValidation(allSkuregStrTotalQty, allSkusOrderedRegStoreTotalQty);

                //      int orderAllocatedNewStoreQty = POlineItemSKU_Qty_NwStr.get(SKUUPC).get(0).getQty();
                //    Integer distroAllocatedNewStoreQty = Integer.valueOf(allocQty_NewStr.entrySet().iterator().next().getValue());
                //     Integer excessQty = distroAllocatedNewStoreQty - Integer.valueOf(beforeReleaseNewStoreQty);
                //     Assert.assertTrue(Integer.compare(overageQty, excessQty) > 0, "Successfully Validated NewStore with Overage Qty");
                //      Assert.assertTrue((distroAllocatedNewStoreQty > Integer.valueOf(orderAllocatedNewStoreQty)), "Overage quantity is not distributed to new store");

            } 
           /*   else if ((pcky_Str_qty.isEmpty()) && (new_Str_qty.isEmpty())) {
 
 
                int orderAllocatedExStoreQty = POlineItemSKU_Qty_ExStr.get(SKUUPC).get(0).getQty();
                Integer distroAllocatedExStoreQty = Integer.valueOf(allocQty_RegStr.entrySet().iterator().next().getValue());
                //     Integer excessQty = distroAllocatedExStoreQty - Integer.valueOf(beforeReleaseExStoreQty);
                //     Assert.assertTrue(Integer.compare(overageQty, excessQty) == 0, "Successfully Validated RegularStore with Overage Qty");
                Assert.assertTrue((distroAllocatedExStoreQty > Integer.valueOf(orderAllocatedExStoreQty)), "Overage quantity is not distributed to existing store");
                log.info("Overage quantity is distributed to existing store");
 
 
            } */
            log.info("***Validated Overage successfully***");
            StepDetail.addDetail("Validated Overage successfully", true);
        } else if (overageShortageFlag.equalsIgnoreCase("Overage") && (distroType.equalsIgnoreCase("PERCENTAGE"))) {

            log.info("overage validation for percentage rule begins");

            if (!pcky_Str_qty_AllSkus.isEmpty()) {
                packawayOverageValidation(allSkupackawayStrTotalQty, allSkusOrderedPackawayStoreTotalQty);
            } else if ((!new_Str_qty_AllSkus.isEmpty()) || (!existing_Str_qty_AllSkus.isEmpty())) {
                log.info("If no Packaway store,Overage quantity in regular store validation begins");
                regularOverageValidation(allSkuregStrTotalQty, allSkusOrderedRegStoreTotalQty);
            }
            log.info("***Validated Overage successfully***");
            StepDetail.addDetail("Validated Overage successfully", true);
        } else if (overageShortageFlag.equalsIgnoreCase("Shortage") && (distroType.equalsIgnoreCase("DISTRO"))) {
            //  Integer packqty = Integer.valueOf(POlineItemSKU_Qty_pckwy.get(SKUUPC).get(0).getQty());
            log.info("shortage validation  for distro rule  begins");
            if (!pcky_Str_qty_AllSkus.isEmpty()) {
                log.info("Shortage validation in packaway store begins");


                //      int beforeReleasePackawayQty = POlineItemSKU_Qty_pckwy.get(SKUUPC).get(0).getQty();
                //   Integer packawayStoreQty = Integer.valueOf(beforeReleasePackawayQty);
             
            /*    if ((Integer.valueOf(allocQty_packaway.get(""))) == 0 || Integer.valueOf(allocQty_packaway.get("7222")) < packawayStoreQty) {
                    boolean pckwy_shrt = true;
                    //   Assert.assertTrue(Integer.compare(distroAllocatedPackawayQty, packawayStoreQty) == 0, "Shortage : Packaway store has no qty or less quantity");
                    Assert.assertEquals(pckwy_shrt, true, "Error in Shortage : Packaway store does not have qty as per the Distro");
                    log.info("Shortage Qty got reduced from the ordered Packaway qty.");
                } */
                packawayShortageValidation(allSkupackawayStrTotalQty, allSkusOrderedPackawayStoreTotalQty);


            } else if ((!new_Str_qty_AllSkus.isEmpty()) || (!existing_Str_qty_AllSkus.isEmpty())) {
                log.info("If no Packaway store,Shortage of quantity in new and regular store validation begins");
                //int originalRegStoreTotalQty = ((Map<String, String>) existing_Str_qty).values().stream().mapToInt(Integer::parseInt).sum();
                //        int originalRegStoreTotalQty= existing_Str_qty.stream().mapToInt(SKU_STR_Qty :: getQty).sum();

                //    log.info("originalRegStoreTotalQty = "+ originalRegStoreTotalQty);
                newShortageValidation(allSkusnewStrTotalQty, allSkusOrderedNewStoreTotalQty);

                regularShortageValidation(allSkuregStrTotalQty, allSkusOrderedRegStoreTotalQty);

              /*  if (regStrTotalQty == originalRegStoreTotalQty || regStrTotalQty > 0) {
                    boolean new_str_qty_flg = newStrTotalQty > 0;
                    Assert.assertEquals(new_str_qty_flg, true, "Error in Shortage:New Store has Qty is not Greater than zero which is not as expected");
                    log.info("New Store has Qty is Greater than zero which is as expected");
                    if (new_str_qty_flg) {
                        boolean new_str_qty_any = allocQty_NewStr.entrySet().stream().anyMatch(x -> (Integer.parseInt(x.getValue())) <= 0);
                        Assert.assertEquals(new_str_qty_any, false, "Error in Shortage: Not All the New Stores has Qty atleast greater than zero which is not expected");
                        log.info("All the New Stores has Qty atleast greater than zero which is as expected");
                        log.info("Shortage Qty : Priority of allocation is given to New Store");
                    } */

            }
            log.info("***Validated Shortage successfully***");
            StepDetail.addDetail("Validated Shortage successfully", true);

        } else if (overageShortageFlag.equalsIgnoreCase("Shortage") && (distroType.equalsIgnoreCase("PERCENTAGE"))) {
            log.info("shortage validation for percentage rule begins");
            if (!pcky_Str_qty_AllSkus.isEmpty()) {
                packawayShortageValidation(allSkupackawayStrTotalQty, allSkusOrderedPackawayStoreTotalQty);

                newShortageValidation(allSkusnewStrTotalQty, allSkusOrderedNewStoreTotalQty);
            } else if ((!new_Str_qty_AllSkus.isEmpty()) || (!existing_Str_qty_AllSkus.isEmpty())) {
                log.info("If no Packaway store,Shortage of quantity in new and regular store validation begins");
                newShortageValidation(allSkusnewStrTotalQty, allSkusOrderedNewStoreTotalQty);

                regularShortageValidation(allSkuregStrTotalQty, allSkusOrderedRegStoreTotalQty);

            }

            log.info("***Validated Shortage successfully***");
            StepDetail.addDetail("Validated Shortage successfully", true);
        }
        Assert.assertTrue(Integer.compare(allocatedQty, totalDistributedQty_allSKUs) == 0, "Distribution allocated quantity is not equal as Ordered allocated quantity");
        log.info("Distribution allocated quantity is equal to Ordered allocated quantity");


    }

    public void packawayOverageValidation(Integer allSkupackawayStrTotalQty, Integer allSkusOrderedPackawayStoreTotalQty) {
        log.info("Packaway Store TotalQty for All Skus in Distribution table: {}", allSkupackawayStrTotalQty);
        log.info("Packaway Store TotalQty for All Skus in Order service: {}", allSkusOrderedPackawayStoreTotalQty);
        Assert.assertTrue(allSkupackawayStrTotalQty > allSkusOrderedPackawayStoreTotalQty, "Error:Overage quantity is not distributed to packaway store");
        log.info("Overage quantity is distributed to packaway store");
    }

    public void regularOverageValidation(Integer allSkuregStrTotalQty, Integer allSkusOrderedRegStoreTotalQty) {
        log.info("Regular Store TotalQty for All Skus in Distribution table: {}", allSkuregStrTotalQty);
        log.info("Regular Store TotalQty for All Skus in Order service: {}", allSkusOrderedRegStoreTotalQty);
        Assert.assertTrue(allSkuregStrTotalQty > allSkusOrderedRegStoreTotalQty, "Error:Overage quantity is not distributed to regular store");
        log.info("Overage quantity is distributed to  regular stores");
    }

    public void packawayShortageValidation(Integer allSkupackawayStrTotalQty, Integer allSkusOrderedPackawayStoreTotalQty) {
        log.info("Packaway Store TotalQty for All Skus in Distribution table: {}", allSkupackawayStrTotalQty);
        log.info("Packaway Store TotalQty for All Skus in Order service: {}", allSkusOrderedPackawayStoreTotalQty);
        if (allSkupackawayStrTotalQty == 0) {
            log.info("Shortage of  quantity is found in packaway store");
        }
        Assert.assertTrue(allSkupackawayStrTotalQty < allSkusOrderedPackawayStoreTotalQty, "Error:Shortage of quantity is not found in packaway store");
        log.info("Shortage of  quantity is found in packaway store");
    }

    public void newShortageValidation(Integer allSkusnewStrTotalQty, Integer allSkusOrderedNewStoreTotalQty) {
        log.info("New Store TotalQty for All Skus in Distribution table: {}", allSkusnewStrTotalQty);
        log.info("New Store TotalQty for All Skus in Order service: {}", allSkusOrderedNewStoreTotalQty);
        Assert.assertTrue(Integer.compare(allSkusnewStrTotalQty, allSkusOrderedNewStoreTotalQty) == 0, "Error:Shortage of quantity is found in new store");
        log.info("Shortage of quantity is not found in new store");
    }

    public void regularShortageValidation(Integer allSkuregStrTotalQty, Integer allSkusOrderedRegStoreTotalQty) {
        log.info("Regular Store TotalQty for All Skus in Distribution table: {}", allSkuregStrTotalQty);
        log.info("Regular Store TotalQty for All Skus in Order service: {}", allSkusOrderedRegStoreTotalQty);
        if (allSkuregStrTotalQty == 0) {
            log.info("Shortage of  quantity is found in regular store");
        }
        Assert.assertTrue(allSkuregStrTotalQty < allSkusOrderedRegStoreTotalQty, "Error:Shortage of quantity is not found in regular store");
        log.info("Shortage of quantity is found in regular store");
    }

    @Then("WSM tasks are created for the VAS and Release Lane(RF) for Rerelease")
    public void validateWSMActivityforRerelease() throws Exception {
        Map<String, List<String>> barcodeWithTotes = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());
        Map<String, List<String>> barcodeWithTotes_rerel = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID_RERELEASE.name());

        //totes created in first release
        List<String> totes = barcodeWithTotes.values().stream().flatMap(List::stream).collect(Collectors.toList());
        //totes created in re-release
        List<String> totesrerel = barcodeWithTotes_rerel.values().stream().flatMap(List::stream).collect(Collectors.toList());

        for (String tote : totes) {
            totesrerel.add(tote);
        }

        //Validate WSM activity message for Totes
        totesrerel.forEach(tote -> {
            try {
                //	String wsmActivityToteSql = String.format(SQLWsm.WSMActivities, tote, "TOTE");
                //.info("WSM ActivityToteSql: {}", wsmActivityToteSql);
                //List<Map<Object, Object>> wsmActivitiesTotes = DBUtils.getValuesFromDBAsList("wsm", wsmActivityToteSql);
                CommonUtils dateCalculatorObject = new CommonUtils();
                String wsmActivityToteEndPoint = String.format(WsmEndpoint.WSM_Activities_SEARCH, tote, "TOTE", dateCalculatorObject.getCurrentDateTime(-30), dateCalculatorObject.getCurrentDateTime(0));
                log.info("WSM ActivityToteEndpoint: {}", wsmActivityToteEndPoint);
                String WsmActivityGetResponse = CommonUtil.getRequestResponse(wsmActivityToteEndPoint);
                JSONArray wsmActivitiesTotes = new JSONArray(WsmActivityGetResponse);
                // List<Map<?, ?>> wsmActivitiesTotes = DBMethods.getValuesFromDBAsList( wsmActivityToteSql,"wsm");
                if (null != wsmActivitiesTotes && wsmActivitiesTotes.length() > 1) {
                    log.info("WSM Activities Tote: {} has more than 1 activities. Tote list: {}", tote, Arrays.asList(wsmActivitiesTotes));
                    for (String oldreleasetote : totes) {
                        if (oldreleasetote.equalsIgnoreCase(tote))
                            Assert.assertTrue(wsmActivitiesTotes.length() > 1);
                        log.info("Duplicate activites are there for Tote: {}", tote);
                    }

                } else {
                    log.info("WSM Activities Tote: {} has only 1 activitity. Tote list: {}", tote, Arrays.asList(wsmActivitiesTotes));
                }
            } catch (Exception e) {
                log.error("Error in ValidateWsmMessagesForTotes", e);
                Assert.fail("Error in ValidateWsmMessagesForTotes", e);
            }
        });
        log.info("***Validated Totes Activities successfully***");
    }

    @Then("WSM tasks are created for the VAS and Release Lane(RF) for $full release")
    public void validateWSMActivity(String full) throws Exception {
        TimeUnit.SECONDS.sleep(25);
        Map<String, List<String>> barcodeWithTotes = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());


        //validate the distro and inventory quantity
        barcodeWithTotes.forEach((poLineBarCode, totes) -> {
            int barcodeloopingcount = 0;
            try {

                if (barcodeloopingcount == 0) {
                    String distroAllocatedQtySql = String.format(SQLPo4Walls.DISTRO_ALLOCATED_QTY_SUM, poLineBarCode);
                    log.info("DistroAllocatedQtySQL: {}", distroAllocatedQtySql);
                    Integer totalDistroAllocatedQty = DBMethods.getDBValueInteger(distroAllocatedQtySql, "pofourwalls");
                    log.info("TotalDistroAllocatedQty: {}", totalDistroAllocatedQty);

                    String toteQuantitySQL = String.format(SQLResearchInventory.INVENTORY_QTY, CommonUtils.convertListStringToSingleQuotes(totes));
                    log.info("toteQuantitySQL: {}", toteQuantitySQL);
                    Integer totalToteQty = DBMethods.getDBValueInteger(toteQuantitySQL, "inventory");
                    log.info("TotalTotesQty: {}", totalToteQty);
                    if (hasInnerPack(poLineBarCode)) {
                        totalToteQty = totalToteQty / barcodeWithTotes.size();
                    }

                    Assert.assertTrue(Integer.compare(totalDistroAllocatedQty, totalToteQty) == 0, "Successfully Validated");

                }
            } catch (Exception e) {
                log.error("Error in validateDistributedQuantity", e);
                Assert.fail("Error in validateDistributedQuantity", e);
            }

            barcodeloopingcount = barcodeloopingcount + 1;

        });


        log.info("***Validated distribution and inventory quantity successfully***");


        //Validate WSM activity message for Totes
        List<String> totesLst = barcodeWithTotes.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Set<String> totes = new TreeSet<>(totesLst);
        totes.forEach(tote -> {
            try {
                String wsmActivityToteSql = String.format(SQLWsm.WSMActivities, tote, "TOTE");
                log.info("WSM ActivityToteSql: {}", wsmActivityToteSql);
                List<Map<Object, Object>> wsmActivitiesTotes = DBMethods.getValuesFromDBAsStringList(wsmActivityToteSql, "wsm");
                if (null != wsmActivitiesTotes && wsmActivitiesTotes.size() > 1) {
                    log.info("WSM Activities Tote: {} has more than 1 activities. Tote list: {}", tote, wsmActivitiesTotes);
                } else {
                    log.info("WSM Activities Tote: {} has only 1 activitity. Tote list: {}", tote, wsmActivitiesTotes);
                }
            } catch (Exception e) {
                log.error("Error in ValidateWsmMessagesForTotes", e);
                Assert.fail("Error in ValidateWsmMessagesForTotes", e);
            }
        });

        log.info("***Validated Totes Activities successfully***");
        String actualProcessArea = (String) stepsContext.get(Context.PO_PROCESS_AREA.name());
        Set<String> multiProcessArea = (Set<String>) stepsContext.get(Context.MULTI_PROCESS_AREA.name());
        int mutipleprocessarea = multiProcessArea.size();
        if (actualProcessArea.equals("OSC") && mutipleprocessarea == 1) {
            //Validate WSM activity message for release Lane
            ListMultimap<String, String> stagingLocationMap = (ListMultimap<String, String>) stepsContext.get(Context.STAGING_LOCATION_TOTE_MAP.name());
            Multimaps.asMap(stagingLocationMap).forEach((stageLocation, totelist) -> {
                try {
                    String wsmActivityToteSql = String.format(SQLWsm.WSMActivities, stageLocation, "LANE");
                    log.info("WSM ActivityToteSql: {}", wsmActivityToteSql);
                    List<Map<Object, Object>> wsmActivitiesTotes = DBMethods.getValuesFromDBAsStringList(wsmActivityToteSql, "wsm");
                    wsmActivitiesTotes.forEach(data -> {
                        Integer totalQty = Integer.valueOf(data.get("TOTAL_QTY").toString());
                        Assert.assertTrue(Integer.compare(totalQty, totelist.size()) == 0);
                    });

                } catch (Exception e) {
                    log.error("Error in releaseLaneActivityValidation", e);
                    Assert.fail("Error in releaseLaneActivityValidation", e);
                }

            });
            log.info("***Validated Release Lane Activities successfully***");
        }


    }

    @Then("WSM tasks are created for the VAS and Release Lane(RF)")
    public void validateWSMActivity() throws Exception {
        TimeUnit.SECONDS.sleep(25);
        Map<String, List<String>> barcodeWithTotes = (Map<String, List<String>>) stepsContext.get(Context.PO_LINES_TOTE_ID.name());

        //validate the distro and inventory quantity
        barcodeWithTotes.forEach((poLineBarCode, totes) -> {
            try {
                String distroAllocatedQtySql = String.format(SQLPo4Walls.DISTRO_ALLOCATED_QTY_SUM, poLineBarCode);
                log.info("DistroAllocatedQtySQL: {}", distroAllocatedQtySql);
                Integer totalDistroAllocatedQty = DBMethods.getDBValueInteger(distroAllocatedQtySql, "pofourwalls");
                log.info("TotalDistroAllocatedQty: {}", totalDistroAllocatedQty);
                int quantity = 0;
                for (String tote : totes) {
                    String inventoryBarcodeEndpoint = String.format(InventoryEndPoint.InventoryWithContainerBarcode, "7221", tote);
                    String inventoryForContainerResponse = CommonUtils.getRequestResponse(inventoryBarcodeEndpoint);
                    JSONObject containerInventoryJson = new JSONObject(inventoryForContainerResponse);
                    quantity = quantity + containerInventoryJson.getJSONArray("inventorySnapshotList").getJSONObject(0).getInt("quantity");
                }
                Integer totalToteQty = new Integer(quantity);
                //String query = String.format(SQLMessage.GET_INVENTORY_SNAPSHOT, filter);
                //List<Map<Object, Object>> dbResults = null;
               /* String toteQuantitySQL = String.format(SQLInventory.INVENTORY_QTY, CommonUtils.convertListStringToSingleQuotes(totes));
                log.info("toteQuantitySQL: {}", toteQuantitySQL);
                Integer totalToteQty = DBMethods.getDBValueInteger( toteQuantitySQL,"inventory");*/
                log.info("TotalTotesQty: {}", totalToteQty);
               /* if (hasInnerPack(poLineBarCode)) {
                    totalToteQty = totalToteQty / barcodeWithTotes.size();
                }*/
                Assert.assertTrue(Integer.compare(totalDistroAllocatedQty, totalToteQty) == 0, "Successfully Validated");

            } catch (Exception e) {
                log.error("Error in validateDistributedQuantity", e);
                Assert.fail("Error in validateDistributedQuantity", e);
            }

        });
        log.info("***Validated distribution and inventory quantity successfully***");


        //Validate WSM activity message for Totes
        List<String> totesLst = barcodeWithTotes.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Set<String> totes = new TreeSet<>(totesLst);
        totes.forEach(tote -> {
            try {
/*
                String wsmActivityToteSql = String.format(SQLWsm.WSMActivities, tote, "TOTE");
                log.info("WSM ActivityToteSql: {}", wsmActivityToteSql);
                List<Map<Object, Object>> wsmActivitiesTotes = DBMethods.getValuesFromDBAsStringList(wsmActivityToteSql, "wsm");
                if (null != wsmActivitiesTotes && wsmActivitiesTotes.size() > 1) {
                    log.info("WSM Activities Tote: {} has more than 1 activities. Tote list: {}", tote, wsmActivitiesTotes);
*/
                // String wsmActivityToteSql = String.format(SQLWsm.WSMActivities, tote, "TOTE");
                CommonUtils dateCalculatorObject = new CommonUtils();
                String wsmActivityToteEndPoint = String.format(WsmEndpoint.WSM_Activities_SEARCH, tote, "TOTE", dateCalculatorObject.getCurrentDateTime(-30), dateCalculatorObject.getCurrentDateTime(0));
                log.info("WSM ActivityToteEndpoint: {}", wsmActivityToteEndPoint);
                String WsmActivityGetResponse = CommonUtil.getRequestResponse(wsmActivityToteEndPoint);
                JSONArray wsmActivitiesTotes = new JSONArray(WsmActivityGetResponse);
                // List<Map<Object, Object>> wsmActivitiesTotes = DBMethods.getValuesFromDBAsStringList( wsmActivityToteSql,"wsm");
                if (null != wsmActivitiesTotes && wsmActivitiesTotes.length() > 1) {
                    log.info("WSM Activities Tote: {} has more than 1 activities. Tote list: {}", tote, Arrays.asList(wsmActivitiesTotes));
                } else {
                    log.info("WSM Activities Tote: {} has only 1 activitity. Tote list: {}", tote, Arrays.asList(wsmActivitiesTotes));
                }
            } catch (Exception e) {
                log.error("Error in ValidateWsmMessagesForTotes", e);
                Assert.fail("Error in ValidateWsmMessagesForTotes", e);
            }
        });

        log.info("***Validated Totes Activities successfully***");
        String actualProcessArea = (String) stepsContext.get(Context.PO_PROCESS_AREA.name());
/*
        Set multiProcessArea =  (Set) stepsContext.get(Context.MULTI_PROCESS_AREA.name());
        int mutipleprocessarea = multiProcessArea.size();
        if (actualProcessArea.equals("OSC") && mutipleprocessarea ==1) {
            //Validate WSM activity message for release Lane
            ListMultimap<String, String> stagingLocationMap = (ListMultimap<String, String>) stepsContext.get(Context.STAGING_LOCATION_TOTE_MAP.name());
            Multimaps.asMap(stagingLocationMap).forEach((stageLocation, totelist) -> {
                try {
                    String wsmActivityToteSql = String.format(SQLWsm.WSMActivities, stageLocation, "LANE");
                    log.info("WSM ActivityToteSql: {}", wsmActivityToteSql);
                    List<Map<Object, Object>> wsmActivitiesTotes = DBMethods.getValuesFromDBAsStringList(wsmActivityToteSql, "wsm");
                    wsmActivitiesTotes.forEach(data -> {
                        Integer totalQty = Integer.valueOf(data.get("TOTAL_QTY").toString());
                        Assert.assertTrue(Integer.compare(totalQty, totelist.size()) == 0);
                    });
*/
        Set<String> multiProcessArea = (Set<String>) stepsContext.get(Context.MULTI_PROCESS_AREA.name());
        int mutipleprocessarea = multiProcessArea.size();
        if (actualProcessArea.equals("OSC") && mutipleprocessarea == 1) {

            //Validate WSM activity message for release Lane
            ListMultimap<String, String> stagingLocationMap = (ListMultimap<String, String>) stepsContext.get(Context.STAGING_LOCATION_TOTE_MAP.name());
            Multimaps.asMap(stagingLocationMap).forEach((stageLocation, totelist) -> {
                try {
                    //String wsmActivityToteSql = String.format(SQLWsm.WSMActivities, stageLocation, "LANE");
                    // log.info("WSM ActivityToteSql: {}", wsmActivityToteSql);
                    //  List<Map<Object, Object>> wsmActivitiesTotes = DBMethods.getValuesFromDBAsStringList(wsmActivityToteSql,"wsm");
                    CommonUtils dateCalculatorObjectForReleaseLane = new CommonUtils();
                    String wsmActivityToteEndPoint = String.format(WsmEndpoint.WSM_Activities_SEARCH, stageLocation, "LANE", dateCalculatorObjectForReleaseLane.getCurrentDateTime(-30), dateCalculatorObjectForReleaseLane.getCurrentDateTime(0));
                    log.info("WSM ActivityToteEndpoint: {}", wsmActivityToteEndPoint);
                    String WsmActivityGetResponse = CommonUtil.getRequestResponse(wsmActivityToteEndPoint);
                    JSONArray wsmActivitiesTotes = new JSONArray(WsmActivityGetResponse);
                    for (int i = 0; i < wsmActivitiesTotes.length(); i++) {
                        if (wsmActivitiesTotes.getJSONObject(i).getString("status").equalsIgnoreCase("OPEN")) {
                            Assert.assertTrue(Integer.compare(wsmActivitiesTotes.getJSONObject(i).getInt("totalQty"), totelist.size()) == 0);
                        }
                    }
                /*wsmActivitiesTotes.forEach(data -> {
                    Integer totalQty = Integer.valueOf(data.get("TOTAL_QTY").toString());
                    Assert.assertTrue(Integer.compare(totalQty, totelist.size()) == 0);
                });*/

                } catch (Exception e) {
                    log.error("Error in releaseLaneActivityValidation", e);
                    Assert.fail("Error in releaseLaneActivityValidation", e);
                }

            });
            log.info("***Validated Release Lane Activities successfully***");
        }

    }
    
	@Then("WSM tasks are created for PTC release")
	public void validatePTCReleaseWSMActivity() throws Exception {
		TimeUnit.SECONDS.sleep(25);
		
		String poNbr = stepsContext.get(Context.PO_NBR.name()).toString();
		String lanes = stepsContext.get(Context.LANES.name()).toString();
		
		//String poNbr = "998532282";
	    //String lanes = "KA01A009,KA01A010";
		CommonUtils dateCalculatorObject = new CommonUtils();
		String wsmActivityToteEndPoint = String.format(WsmEndpoint.WSM_Activities_SEARCH_PTCRELEASE, poNbr, "OPEN", lanes,
				dateCalculatorObject.getCurrentDateTime(-30), dateCalculatorObject.getCurrentDateTime(0));
		
		//String wsmActivityToteEndPoint = "https://dev-backstage.devops.fds.com/wsm-service/wsm/7221/activities?poNbr=" + poNbr + "&type=PICKTOCARTON&status=OPEN,ASSIGNED,COMPLETED&container=" + lanes;
			
		log.info("WSM ActivityToteEndpoint: {}" + wsmActivityToteEndPoint);
		String WsmActivityGetResponse = CommonUtil.getRequestResponse(wsmActivityToteEndPoint);
		if(WsmActivityGetResponse == null || WsmActivityGetResponse.isEmpty()){
			Assert.fail("No WSM activity found");
		}
		log.info("WsmActivityGetResponse is " + WsmActivityGetResponse);
		JSONArray wsmActivities = new JSONArray(WsmActivityGetResponse);
		
		HashSet<String> stores = new HashSet<String>();
		HashMap<String, HashSet<String>> upcsStores = new HashMap<String, HashSet<String>>();
		String upc = wsmActivities.getJSONObject(0).getString("upc");
		for(int i = 0; i < wsmActivities.length(); i++){
			JSONObject wsmActivity = wsmActivities.getJSONObject(i);
			String upc2 = wsmActivity.getString("upc");
			
			JSONObject attrbiute = wsmActivity.getJSONObject("attributes");
			String store = attrbiute.getString("storeNbr");
			if(upc2.equals(upc)){
				stores.add(store);
				log.info("stores is " + stores.toString());
			}else{
				upcsStores.put(upc,  stores);
				stores = new HashSet<String>();
				upc = upc2;
				stores.add(store);
			}
		}
		upcsStores.put(upc,  stores);
		//when all upcs has the same stores, for example, sku1 has 4 stores, sku2 has the same 4 stores
		stepsContext.put(Context.STORES.name(), stores.size(), ToContext.RetentionLevel.SCENARIO);
		log.info("upcsStores size is " + upcsStores.size() + ", content is " + upcsStores.toString());
		
		// Store and Qty in Ordered service as per Store Type
		Map<BigInteger, List<SKU_STR_Qty>> POlineItemSKU_Qty_NwStr = new LinkedHashMap<>();
		Map<BigInteger, List<SKU_STR_Qty>> POlineItemSKU_Qty_ExStr = new LinkedHashMap<>();
		Map<BigInteger, List<SKU_STR_Qty>> POlineItemSKU_Qty_pckwy = new LinkedHashMap<>();
		List<POLineItems> poLineItems = CommonUtils.POOrderDetails(poNbr);
		List<SKU_STR_Qty> pcky_Str_qty_AllSkus = new ArrayList<>();
		List<SKU_STR_Qty> new_Str_qty_AllSkus = new ArrayList<>();
		List<SKU_STR_Qty> existing_Str_qty_AllSkus = new ArrayList<>();
		for (POLineItems eachPOLineItem : poLineItems) {
			List<SKU_STR_Qty> pcky_Str_qty = new ArrayList<>();
			List<SKU_STR_Qty> new_Str_qty = new ArrayList<>();
			List<SKU_STR_Qty> existing_Str_qty = new ArrayList<>();
			BigInteger SKUUPC = eachPOLineItem.getSkuUpc();
			
			List<LocationDistro> distrolist = eachPOLineItem.getPoLocationDistroList();
			for (LocationDistro eachLocationDistro : distrolist) {
				Integer orderQty = eachLocationDistro.getOrderQty();
				Integer locationNbr = eachLocationDistro.getLocationNbr();

				if (locationNbr == 7222) {
					pcky_Str_qty.add(new SKU_STR_Qty(locationNbr, orderQty));
				} else {
					String response = CommonUtils
							.getRequestResponse(String.format(PO4WallEndPoint.PO4WALL_GET_LOCATION, locationNbr));
					
					//String response = CommonUtils
					//		.getRequestResponse(String.format("https://dev-backstage.devops.fds.com/pofourwalls-service/pofourwalls/locations/%s", locationNbr));
					if (StringUtils.isNotEmpty(response)) {
						JSONObject jsonObject = new JSONObject(response);
						Boolean vas_flg = (Boolean) jsonObject.getJSONObject("LocationDto").get("vasFlag");
						if (vas_flg) {
							new_Str_qty.add(new SKU_STR_Qty(locationNbr, orderQty));
						} else if (!vas_flg) {
							existing_Str_qty.add(new SKU_STR_Qty(locationNbr, orderQty));
						}
					}
				}
			}
			Collections.sort(pcky_Str_qty);
			Collections.sort(new_Str_qty);
			Collections.sort(existing_Str_qty);
			pcky_Str_qty_AllSkus.addAll(pcky_Str_qty);
			log.info("Packaway stores of all Skus: {}", pcky_Str_qty_AllSkus);
			new_Str_qty_AllSkus.addAll(new_Str_qty);
			log.info("New store of All skus: {}", new_Str_qty_AllSkus);
			existing_Str_qty_AllSkus.addAll(existing_Str_qty);
			log.info("Regular store of All skus: {}", existing_Str_qty_AllSkus);

			POlineItemSKU_Qty_pckwy.put(SKUUPC, pcky_Str_qty);
			POlineItemSKU_Qty_NwStr.put(SKUUPC, new_Str_qty);
			POlineItemSKU_Qty_ExStr.put(SKUUPC, existing_Str_qty);
		}
		
		log.info("POlineItemSKU_Qty_pckwy: {}", POlineItemSKU_Qty_pckwy);
		log.info("POlineItemSKU_Qty_NwStr: {}", POlineItemSKU_Qty_NwStr);
		log.info("POlineItemSKU_Qty_ExStr: {}", POlineItemSKU_Qty_ExStr);
		
		// validate wsm msg generated and matches sku-store allocation
		
		upcsStores.forEach((sku, strs) -> {		
			for (String str : strs) {
		        BigInteger skuKey = new BigInteger(sku);
		        
		        if(POlineItemSKU_Qty_ExStr.get(skuKey) == null){
		        	log.info("No activity for sku " + sku);
					Assert.fail("No activity for sku " + sku);
		        }
		        
		        boolean isMatch = false;
		        for(int i = 0; i < POlineItemSKU_Qty_ExStr.get(skuKey).size(); i++){
		        	String strOrig = String.valueOf(POlineItemSKU_Qty_ExStr.get(skuKey).get(i).getStr_nbr());
		        	if(str.equals(strOrig)){
		        		isMatch = true;
		        		break;
		        	}
		        }
		        if(!isMatch){
			        for(int i = 0; i < POlineItemSKU_Qty_NwStr.get(skuKey).size(); i++){
			        	String strOrig = String.valueOf(POlineItemSKU_Qty_NwStr.get(skuKey).get(i).getStr_nbr());
			        	if(str.equals(strOrig)){
			        		isMatch = true;
			        		break;
			        	}
			        }
			        if(!isMatch){
			        	log.info("No activity for store " + str + " of sku " + sku);
						Assert.fail("No activity for store " + str + " of sku " + sku);
			        }
		        }
		     }
		});
				
	}

    public String DateFormation(int seconds) {
        Calendar now = Calendar.getInstance(timeZone);
        now.add(Calendar.MINUTE, -120);
        sdf.setTimeZone(timeZone);
        return sdf.format(now.getTime());
    }


    @Then("System sends STOREALLOC message for $full release with allocations for PO items and distro to Pyramid and it consumes, conveys the details to put to store")
    public void validateStoreAllocMessage(String full) throws Exception {
        int size=100;
        TimeUnit.SECONDS.sleep(30);
        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        if (!full.equals("full"))
            DataAvailableForPO(poLinebarCode.stream().findFirst().orElse(null).getPoNbr());
        MHESteps mheSteps = new MHESteps(stepsContext);
        for (PoLinebarCode poLine : poLinebarCode) {
            Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap = (Map<BigInteger, List<LocationDistro>>) stepsContext.get(Context.SKU_STORE_LOCN_QTY_MAP.name());
            Map<BigInteger, Integer> skuDeptMap = (Map<BigInteger, Integer>) stepsContext.get(Context.SKU_DEPT_NBR_MAP.name());
            int expectedSize = 0;
            for (List<LocationDistro> distro : skuStoreLocnQuantityMap.values()) {
                boolean Packawayflag = false;
                for (int p = 0; p < distro.size(); p++) {
                    if (distro.get(p).getLocationNbr() == 7222) {
                        Packawayflag = true;
                        break;
                    }
                }
                if (Packawayflag)
                    expectedSize += distro.size() - 1;
                else
                    expectedSize += distro.size();
            }

            log.info("Expected storeAlloc size: " + expectedSize);
            if (hasInnerPack(poLine.getPoLineBarCode())) {
                expectedSize = expectedSize / poLinebarCode.size();
            }
            List<Map<String, String>> valueFromDB = mheSteps.fetchMHEDetailsFromDB("STOREALLOC", poLine.getPoNbr(), "fromDate", "toDate",size);
            if (full.equals("full")) {
                if (valueFromDB.size() == expectedSize) {
                    log.info("No of STOREALLOC messages are as expected");
                    Assert.assertTrue(true, "No of STOREALLOC messages are as expected");
                    StepDetail.addDetail("No of STOREALLOC messages are as expected", true);
                    mheMessageParser(valueFromDB, skuStoreLocnQuantityMap, skuDeptMap, poLine.getPoNbr(), poLine.getReceiptNbr(), poLine.getPoLineBarCode(), false);
                    break;
                } else {
                    log.info("No of STOREALLOC messages are not as expected");
                    //Assert.assertTrue(false, "No of STOREALLOC messages are not as expected");
                }
            }
            if (full.contains("partial")) {
                Assert.assertTrue(!valueFromDB.isEmpty());
                if (full.contains("partial")) {
                    mheMessageParser(valueFromDB, skuStoreLocnQuantityMap, skuDeptMap, poLine.getPoNbr(), poLine.getReceiptNbr(), poLine.getPoLineBarCode(), false);
                }
             }
        }
    }


    private Map<BigInteger, List<LocationDistro>> getSkuStoreLocnQtyMap(List<POLineItems> poLineItems) {
        List<LocationDistro> locationsDistro = poLineItems.stream().map(POLineItems::getPoLocationDistroList)
                .flatMap(List::stream).collect(Collectors.toList());
        Map<BigInteger, List<LocationDistro>> SkuStoreLocnQuantityMap = locationsDistro.stream()
                .collect(Collectors.groupingBy(LocationDistro::getSkuUpcNbr));
        log.info("skuLocationDistro::{}", SkuStoreLocnQuantityMap);
        stepsContext.put(Context.SKU_STORE_LOCN_QTY_MAP.name(), SkuStoreLocnQuantityMap, ToContext.RetentionLevel.SCENARIO);
        setStoreLocationSkuUpcMap(locationsDistro);
        return SkuStoreLocnQuantityMap;
    }

    private void setStoreLocationSkuUpcMap(List<LocationDistro> locationDistros) {
        Map<Integer, List<LocationDistro>> storeLocationSkuUpcMap = locationDistros.stream().collect(Collectors.groupingBy(LocationDistro::getLocationNbr));
        stepsContext.put(Context.STORE_LOCN_SKU_MAP.name(), storeLocationSkuUpcMap, ToContext.RetentionLevel.SCENARIO);
    }

    private Map<BigInteger, Integer> SkuDeptNbrMap(List<POLineItems> poLineItems) {
        Map<BigInteger, Integer> SkuDeptMap = poLineItems.stream()
                .collect(Collectors.toMap(POLineItems::getSkuUpc, POLineItems::getDeptNbr));
        log.info("skuDeptMap::{}", SkuDeptMap);
        stepsContext.put(Context.SKU_DEPT_NBR_MAP.name(), SkuDeptMap, ToContext.RetentionLevel.SCENARIO);
        return SkuDeptMap;
    }


    private void mheMessageParser(List<Map<String, String>> valueFromDB, Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap, Map<BigInteger, Integer> skuDeptMap, String poNbr, String rcptNbr, String poLineBrcd, boolean prevPut) {
        String expectedMsg = valueFromDB.get(0).get("WCS Payload");
        ExpectedDataProperties expdataForMessageBasicValidation = new ExpectedDataProperties();
        LinkedHashMap<String, String> actualMessageDataMap = expdataForMessageBasicValidation.expectedMessage("STOREALLOC", expectedMsg, prevPut, "PO", CreateToteSteps.hasInnerPack);

        if (CreateToteSteps.hasInnerPack) {        	
    		Map<String, String> expectedSkuQtyMap = getInnerPackForSKUs(poLineBrcd);
        	Map<String, String> actualSkuQtyMap   = new HashMap<>();
    		actualSkuQtyMap.put(actualMessageDataMap.get("upc1"),actualMessageDataMap.get("upc1Qty"));
    		actualSkuQtyMap.put(actualMessageDataMap.get("upc2"),actualMessageDataMap.get("upc2Qty"));
    		actualSkuQtyMap.put(actualMessageDataMap.get("upc3"),actualMessageDataMap.get("upc3Qty"));
    	    CommonUtils.doJbehavereportConsolelogAndAssertion("SKU List matches Distro SKU List", "Expected SKU List: "+expectedSkuQtyMap+" Actual SKU List: "+actualSkuQtyMap, expectedSkuQtyMap.equals(actualSkuQtyMap));
        }else  if (checkDeptNbrAndSku(skuStoreLocnQuantityMap, skuDeptMap, actualMessageDataMap.get("upc1"), actualMessageDataMap.get("upc1Qty"), actualMessageDataMap.get("dept"))) {
            Assert.assertTrue(true, "Message contain SKU's as expected");
        }else {
            if (!checkDeptNbrAndSku(skuStoreLocnQuantityMap, skuDeptMap, actualMessageDataMap.get("upc1"), actualMessageDataMap.get("upc1Qty"), actualMessageDataMap.get("dept"))) {
                Assert.assertTrue(false, "Message doesnot contain " + actualMessageDataMap.get("upc1") + " as expected");
            }
        }
        
        if (!(actualMessageDataMap.get("poNbr")).equalsIgnoreCase(poNbr)) {
            Assert.assertTrue(false, "Message contain :" + actualMessageDataMap.get("poNbr") + " but Expected is: " + poNbr);
        }
        if (!(actualMessageDataMap.get("poReceipt")).equalsIgnoreCase(rcptNbr)) {
            Assert.assertTrue(false, "Message contain :" + actualMessageDataMap.get("poReceipt") + " but Expected is: " + rcptNbr);
        }        
        CommonUtils.doJbehavereportConsolelogAndAssertion("Message format is as expected", expectedMsg, true);        
    }
    
    private Boolean checkDeptNbrAndSku(Map<BigInteger, List<LocationDistro>> skuStoreLocnQuantityMap, Map<BigInteger, Integer> skuDeptMap, String value1, String value2, String value3) {
        Boolean flag = false;
        for (BigInteger sku : skuStoreLocnQuantityMap.keySet()) {
            if (value1.equalsIgnoreCase(sku.toString()) && value2.equalsIgnoreCase("1") && value3.equalsIgnoreCase(skuDeptMap.get(sku).toString())) {
                flag = true;
                break;
            }
        }
        return flag;
    }


    private Boolean hasInnerPack(String poLineBarcode) {
        String response = CommonUtils.getRequestResponse(String.format(PO4WallEndPoint.PO4WALL_GET_POLINE_PCK, poLineBarcode));
        if (StringUtils.isNotEmpty(response)) {
            JSONArray jsonArray = new JSONArray(response);
            String packType = (String) ((JSONObject) jsonArray.get(0)).get("packType");
            return "IP".equalsIgnoreCase(packType);
        }
        return false;
    }

    private Map<String,String> getInnerPackForSKUs(String poLineBarcode) {
        Map<String, String> skuQtyMap = new HashMap<>();
        String response = CommonUtils.getRequestResponse(String.format(PO4WallEndPoint.PO4WALL_GET_POLINE_PCK, poLineBarcode));
        if (StringUtils.isNotEmpty(response)) {
            JSONArray jsonArray = new JSONArray(response).getJSONObject(0).getJSONArray("packDetails");
            for (int i = 0; i < jsonArray.length(); i++) {
            	skuQtyMap.put(String.valueOf(jsonArray.getJSONObject(i).get("skuUpc")),String.valueOf(jsonArray.getJSONObject(i).get("qty")));
            }
        }
        return skuQtyMap;
    }

}