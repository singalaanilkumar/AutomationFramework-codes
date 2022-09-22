package com.macys.mst.DC2.EndToEnd.utilmethods;

import com.macys.mst.artemis.config.ConfigProperties;

import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;

import java.util.*;

import static com.macys.mst.DC2.EndToEnd.utilmethods.Constants.*;

@Slf4j
public class ExpectedDataProperties {
    public static String storeAllocMessage = "\\u0002000000365|STOREALLOC|6330|492608024463|1|||||||||||||||||||||||||||||830|40|0|0|N000|0|P|4726842|4519230|||\\u0003";
    public static String TOTECONTMessage = "\\u0002000000389|TOTECONT|50000022000077000860|492608024432|1|||||||||||||||||||||||||||||830|50|0|N000|W|4726842|4519230|||\\u0003";
    public static Boolean pyramidJsonproperty =  Boolean.parseBoolean(ConfigProperties.getInstance("config.properties").getProperty("REVERSE_MESSAGE_AS_JSON"));

    public static Map<String, String> getHeaderProps() {
        Map<String, String> headerProps = new HashMap<>();
        headerProps.put("X-WHM-JWT", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJCMCRXSE1TVVBFUlVTRVIiLCJzdWJqZWN0IjoiQjAkV0hNU1VQRVJVU0VSIiwiZGlzcGxheU5hbWUiOiJiMCRXSE1TVVBFUlVTRVIiLCJuYW1lIjoiYjAkV0hNU1VQRVJVU0VSIiwiY24iOiJiMCRXSE1TVVBFUlVTRVIiLCJyb2xlcyI6WyItV0hNX1VTRVIiLCItV0hNX1BMQU5ORVIiLCItV0hNX0FSTyIsIi1XSE1fSUNRQSIsIi1XSE1fU1VQRVJWSVNPUiIsIi1XSE1fTUdSIiwiLVdITV9TVVBFUlVTRVIiXSwibG9jYXRpb25zIjpbIi1XSE1fTE9DQVRJT05fNzIyMSJdfQ.IjxPU3QE6C5TgJxvvK3mLSHpvKYXVEbp8ICl9expKKw");
        return headerProps;
    }

    public static Map<String, String> getJWTToken() {
        Map<String, String> headerProps = new HashMap<>();

        return headerProps;
    }

    public static Map<String, String> getMenuOptions() {
        Map<String, String> options = new HashMap<String, String>();
        options.put("Create Tote", "Create Tote");
        //options.put("Build Pallet", "Build Pallet");
        options.put("Print Ticket", "Print Ticket");
        options.put("Split Move", "Split Move");
        options.put("Stage Pallet", "Stage Pallet");
        options.put("Locate Pallet", "Locate Pallet");
        options.put("Adjust Container", "Adjust Container");
        // options.put("Putaway Pallet", "Putaway Pallet");
        options.put("Prep Options", "Prep Options");
        options.put("Consume Container", "Consume Container");
        return options;
    }

    public LinkedHashMap<String, String> expectedMessage(String messageType, String message, boolean prevPut, String orderType, boolean innerpack) {
        if (messageType.equals("STOREALLOC")) {
            LinkedHashMap<String, String> storeAllocMap = storeAllocMapConstruction(messageType, message, prevPut, orderType, innerpack);
            return storeAllocMap;
        } else if (messageType.equals("TOTECONT")) {
            LinkedHashMap<String, String> toteContMap = toteContMapConstruction(messageType, message, prevPut, orderType, innerpack);
            return toteContMap;
        } else
            return null;
    }


    public LinkedHashMap<String, String> toteContMapConstruction(String messageType, String message, boolean prevPut, String orderType, boolean innerpack) {
        if(!ExpectedDataProperties.pyramidJsonproperty){
            LinkedHashMap<String, String> toteContHashMap = new LinkedHashMap<String, String>();
            String[] expectedStoreAllocArray = ExpectedDataProperties.TOTECONTMessage.split("\\|");
            String[] toteContArray = message.split("\\|");
            log.info("toteCont Array: " + Arrays.asList(toteContArray).toString());
            Assert.assertTrue(expectedStoreAllocArray.length == toteContArray.length);
            toteContHashMap.put("messageType", toteContArray[1]);
            toteContHashMap.put("containerBarcode", toteContArray[2]);
            toteContHashMap.put("upc1", toteContArray[3]);
            toteContHashMap.put("upc1Qty", toteContArray[4]);
            if (innerpack) {
                toteContHashMap.put("upc2", toteContArray[5]);
                toteContHashMap.put("upc2Qty", toteContArray[6]);
                toteContHashMap.put("upc3", toteContArray[7]);
                toteContHashMap.put("upc3Qty", toteContArray[8]);
            }
            toteContHashMap.put("dept", toteContArray[33]);
            toteContHashMap.put("totalQty", toteContArray[34]);
            toteContHashMap.put("storeType", toteContArray[35]);
            toteContHashMap.put("innerPackPrefix", toteContArray[36]);
            toteContHashMap.put("orderType", toteContArray[37]);
            if (orderType.equalsIgnoreCase("PO")) {
                toteContHashMap.put("poNbr", toteContArray[38]);
                toteContHashMap.put("poReceipt", toteContArray[39]);
            }
            if (orderType.equalsIgnoreCase("WAVE")) {
                toteContHashMap.put("waveNbr", toteContArray[38]);
                toteContHashMap.put("waveNbrNextElement", toteContArray[39]);
            }

            toteContMessageValidations(orderType, innerpack, toteContHashMap);
            return toteContHashMap;
        }else{
            LinkedHashMap<String, String> toteContHashMap = new LinkedHashMap<String, String>();
            JsonPath toteContJSON = new JsonPath(message);
            toteContHashMap.put("messageType", StringUtils.defaultString(toteContJSON.getString("messageType")));
            toteContHashMap.put("containerBarcode", StringUtils.defaultString(toteContJSON.getString("container")));
            toteContHashMap.put("upc1", StringUtils.defaultString(toteContJSON.getString("items[0].sku")));
            toteContHashMap.put("upc1Qty", StringUtils.defaultString(toteContJSON.getString("items[0].quantity")));
            if (innerpack) {
            	toteContHashMap.put("upc2", StringUtils.defaultString(toteContJSON.getString("items[1].sku")));
            	toteContHashMap.put("upc2Qty", StringUtils.defaultString(toteContJSON.getString("items[1].quantity")));
            	toteContHashMap.put("upc3", StringUtils.defaultString(toteContJSON.getString("items[2].sku")));
            	toteContHashMap.put("upc3Qty", StringUtils.defaultString(toteContJSON.getString("items[2].quantity")));
            }
            toteContHashMap.put("dept", StringUtils.defaultString(toteContJSON.getString("dept")));
            toteContHashMap.put("totalQty", StringUtils.defaultString(toteContJSON.getString("quantity")));
            toteContHashMap.put("storeType", StringUtils.defaultString(toteContJSON.getString("newStore")));
            toteContHashMap.put("innerPackPrefix", StringUtils.defaultString(toteContJSON.getString("casePack")));
            toteContHashMap.put("orderType", StringUtils.defaultString(toteContJSON.getString("orderSource")));
            if (orderType.equalsIgnoreCase("PO")) {
            	toteContHashMap.put("poNbr", StringUtils.defaultString(toteContJSON.getString("orderNumber")));
                toteContHashMap.put("poReceipt", StringUtils.defaultString(toteContJSON.getString("receiptNumber")));
            }
            if (orderType.equalsIgnoreCase("WAVE")) {
            	toteContHashMap.put("waveNbr", StringUtils.defaultString(toteContJSON.getString("orderNumber")));
                toteContHashMap.put("waveNbrNextElement", StringUtils.defaultString(toteContJSON.getString("receiptNumber")));
            }
            toteContMessageValidations(orderType, innerpack, toteContHashMap);
            return toteContHashMap;
        }
    }

    public LinkedHashMap<String, String> storeAllocMapConstruction(String messageType, String message, boolean prevPut, String orderType, boolean innerpack) {
        if(!ExpectedDataProperties.pyramidJsonproperty){
        	String[] ExpectedStoreAllocArray = ExpectedDataProperties.storeAllocMessage.split("\\|");
        	String[] StoreAllocArray = message.split("\\|");
	        log.info("StoreAlloc Array: " + Arrays.asList(StoreAllocArray).toString());
	        Assert.assertTrue(ExpectedStoreAllocArray.length == StoreAllocArray.length);
	        LinkedHashMap<String, String> storeAllocHashMap = new LinkedHashMap<String, String>();
	        storeAllocHashMap.put("messageType", StoreAllocArray[1]);
	        storeAllocHashMap.put("store", StoreAllocArray[2]);
	        storeAllocHashMap.put("upc1", StoreAllocArray[3]);
	        storeAllocHashMap.put("upc1Qty", StoreAllocArray[4]);
	        if (innerpack) {
	            storeAllocHashMap.put("upc2", StoreAllocArray[5]);
	            storeAllocHashMap.put("upc2Qty", StoreAllocArray[6]);
	            storeAllocHashMap.put("upc3", StoreAllocArray[7]);
	            storeAllocHashMap.put("upc3Qty", StoreAllocArray[8]);
	        }
	        storeAllocHashMap.put("dept", StoreAllocArray[33]);
	        storeAllocHashMap.put("totalQty", StoreAllocArray[34]);
	        if (prevPut == true)
	            storeAllocHashMap.put("prevPutQTY", StoreAllocArray[35]);
	        else
	            storeAllocHashMap.put("prevPutQTY", "0");
	        storeAllocHashMap.put("storeType", StoreAllocArray[36]);
	        storeAllocHashMap.put("innerPackPrefix", StoreAllocArray[37]);
	        storeAllocHashMap.put("orderType", StoreAllocArray[39]);
	        if (orderType.equalsIgnoreCase("PO")) {
	            storeAllocHashMap.put("poNbr", StoreAllocArray[40]);
	            storeAllocHashMap.put("poReceipt", StoreAllocArray[41]);
	        }
	        if (orderType.equalsIgnoreCase("WAVE")) {
	            storeAllocHashMap.put("waveNbr", StoreAllocArray[40]);
	            storeAllocHashMap.put("waveNbrNextElement", StoreAllocArray[41]);
	        }
	
	        StoreAllocMessageValidations(orderType, innerpack, prevPut, storeAllocHashMap);
	        return storeAllocHashMap;
        }else{
            LinkedHashMap<String, String> storeAllocHashMap = new LinkedHashMap<String, String>();
            JsonPath storeAllocJSON = new JsonPath(message);
            storeAllocHashMap.put("messageType", StringUtils.defaultString(storeAllocJSON.getString("messageType")));
            storeAllocHashMap.put("store", StringUtils.defaultString(storeAllocJSON.getString("store")));
            storeAllocHashMap.put("upc1", StringUtils.defaultString(storeAllocJSON.getString("items[0].sku")));
            storeAllocHashMap.put("upc1Qty", StringUtils.defaultString(storeAllocJSON.getString("items[0].quantity")));
            if (innerpack) {
                storeAllocHashMap.put("upc2", StringUtils.defaultString(storeAllocJSON.getString("items[1].sku")));
                storeAllocHashMap.put("upc2Qty", StringUtils.defaultString(storeAllocJSON.getString("items[1].quantity")));
                storeAllocHashMap.put("upc3", StringUtils.defaultString(storeAllocJSON.getString("items[2].sku")));
                storeAllocHashMap.put("upc3Qty", StringUtils.defaultString(storeAllocJSON.getString("items[2].quantity")));
            }
            storeAllocHashMap.put("dept", StringUtils.defaultString(storeAllocJSON.getString("dept")));
            storeAllocHashMap.put("totalQty", StringUtils.defaultString(storeAllocJSON.getString("quantity")));
            if (prevPut == true)
                storeAllocHashMap.put("prevPutQTY", StringUtils.defaultString(storeAllocJSON.getString("prevPutQuantity")));
            else
                storeAllocHashMap.put("prevPutQTY", "0");
            storeAllocHashMap.put("storeType", StringUtils.defaultString(storeAllocJSON.getString("storeGroup")));
            storeAllocHashMap.put("innerPackPrefix", StringUtils.defaultString(storeAllocJSON.getString("casePack")));
            storeAllocHashMap.put("orderType", StringUtils.defaultString(storeAllocJSON.getString("orderSource")));
            if (orderType.equalsIgnoreCase("PO")) {
                storeAllocHashMap.put("poNbr", StringUtils.defaultString(storeAllocJSON.getString("orderNumber")));
                storeAllocHashMap.put("poReceipt", StringUtils.defaultString(storeAllocJSON.getString("receiptNumber")));
            }
            if (orderType.equalsIgnoreCase("WAVE")) {
                storeAllocHashMap.put("waveNbr", StringUtils.defaultString(storeAllocJSON.getString("orderNumber")));
                storeAllocHashMap.put("waveNbrNextElement", StringUtils.defaultString(storeAllocJSON.getString("receiptNumber")));
            }
            StoreAllocMessageValidations(orderType, innerpack, prevPut, storeAllocHashMap);
            return storeAllocHashMap;
        }
    }

    public void toteContMessageValidations(String orderType, boolean innerpack, LinkedHashMap<String, String> actualToteContMap) {
        if (!actualToteContMap.get("messageType").equals("TOTECONT"))
            Assert.assertTrue(false, "Message contains messageType as: " + actualToteContMap.get("messageType") + " but Expected messageType is: TOTECONT");
        if (StringUtils.isBlank(actualToteContMap.get("containerBarcode")))
            Assert.assertTrue(false, "Message contains containerBarcode as: " + actualToteContMap.get("containerBarcode") + " but Expected container barcode should not be blank");
        if (Long.parseLong(actualToteContMap.get("upc1")) <= 0)
            Assert.assertTrue(false, "Message contains upc1 as: " + actualToteContMap.get("upc1") + " but Expected upc1 barcode should be greater than zero");
        if (Integer.valueOf(actualToteContMap.get("upc1Qty")) <= 0)
            Assert.assertTrue(false, "Message contains upc1 qty as: " + actualToteContMap.get("upc1Qty") + " but Expected upc1 qty should be greater than zero");
        if (innerpack) {
            if (Long.parseLong(actualToteContMap.get("upc2")) <= 0)
                Assert.assertTrue(false, "Message contains upc2 as: " + actualToteContMap.get("upc2") + " but Expected upc2 barcode should be greater than zero");
            if (Integer.valueOf(actualToteContMap.get("upc2Qty")) <= 0)
                Assert.assertTrue(false, "Message contains upc2 qty as: " + actualToteContMap.get("upc2Qty") + " but Expected upc2 qty should be greater than zero");
            if (Long.parseLong(actualToteContMap.get("upc3")) <= 0)
                Assert.assertTrue(false, "Message contains upc3 as: " + actualToteContMap.get("upc3") + " but Expected upc3 barcode should be greater than zero");
            if (Integer.valueOf(actualToteContMap.get("upc3Qty")) <= 0)
                Assert.assertTrue(false, "Message contains upc3 qty as: " + actualToteContMap.get("upc3Qty") + " but Expected upc3 qty should be greater than zero");

        }
        if (Integer.valueOf(actualToteContMap.get("dept")) <= 0)
            Assert.assertTrue(false, "Message contains deptNbr as: " + StringUtils.defaultString(actualToteContMap.get("dept")) + " but Expected dept Nbr should be greater than zero");
        if (Integer.valueOf(actualToteContMap.get("totalQty")) <= 0)
            Assert.assertTrue(false, "Message contains totalQty as: " + actualToteContMap.get("totalQty") + " but Expected total QTY should be greater than zero");

        if (Integer.valueOf(actualToteContMap.get("storeType")) != 0 && Integer.valueOf(actualToteContMap.get("storeType")) != 1)
            Assert.assertTrue(false, "Message contains storeType as: " + actualToteContMap.get("storeType") + " but Expected storeType should be 1 or 0");
        if (innerpack) {
            if (actualToteContMap.get("innerPackPrefix").charAt(0) != 'Y')
                Assert.assertTrue(false, "Message contains innerPackPrefix as: " + actualToteContMap.get("innerPackPrefix") + " but Expected inner Pack Prefix should be Y");
        } else {
            if (actualToteContMap.get("innerPackPrefix").charAt(0) != 'N')
                Assert.assertTrue(false, "Message contains innerPackPrefix as: " + actualToteContMap.get("innerPackPrefix") + " but Expected inner Pack Prefix should be N");
        }
        if (orderType.equalsIgnoreCase("PO")) {
            if (!actualToteContMap.get("orderType").equalsIgnoreCase("P"))
                Assert.assertTrue(false, "Message contains orderType as: " + actualToteContMap.get("orderType") + " but Expected order Type should be P");
            if (Integer.valueOf(actualToteContMap.get("poNbr")) <= 0)
                Assert.assertTrue(false, "Message contains poNbr as: " + actualToteContMap.get("poNbr") + " but Expected PO NBR should be greater than zero");
            if (Integer.valueOf(actualToteContMap.get("poReceipt")) <= 0)
                Assert.assertTrue(false, "Message contains poReceipt as: " + actualToteContMap.get("poReceipt") + " but Expected PO Receipt NBR should be greater than zero");
        }
        if (orderType.equalsIgnoreCase("WAVE")) {
            if (!actualToteContMap.get("orderType").equalsIgnoreCase("W"))
                Assert.assertTrue(false, "Message contains orderType as: " + actualToteContMap.get("orderType") + " but Expected order Type should be W");
            if (Double.valueOf(actualToteContMap.get("waveNbr")) <= 0)
                Assert.assertTrue(false, "Message contains waveNbr as: " + actualToteContMap.get("waveNbr") + " but Expected WAVE NBR should be greater than zero");
            if (!StringUtils.isBlank(actualToteContMap.get("waveNbrNextElement")))
                Assert.assertTrue(false, "Message contains waveNbrNextElement as: " + actualToteContMap.get("waveNbrNextElement") + " but Expected waveNbrNextElement should be of empty value");
        }
    }

    public void StoreAllocMessageValidations(String orderType, boolean innerpack, boolean prevPut, LinkedHashMap<String, String> actualStoreAllocMap) {
        if (!actualStoreAllocMap.get("messageType").equals("STOREALLOC"))
            Assert.assertTrue(false, "Message contains messageType as: " + actualStoreAllocMap.get("messageType") + " but Expected messageType is: STOREALLOC");
        if (Integer.valueOf(actualStoreAllocMap.get("store")) <= 0)
            Assert.assertTrue(false, "Message contains storeNbr as: " + actualStoreAllocMap.get("messageType") + " but Expected store Nbr should be greater than zero");
        if (Long.parseLong(actualStoreAllocMap.get("upc1")) <= 0)
            Assert.assertTrue(false, "Message contains upc1 as: " + actualStoreAllocMap.get("upc1") + " but Expected upc1 barcode should be greater than zero");
        if (Integer.valueOf(actualStoreAllocMap.get("upc1Qty")) <= 0)
            Assert.assertTrue(false, "Message contains upc1 qty as: " + actualStoreAllocMap.get("upc1Qty") + " but Expected upc1 qty should be greater than zero");
        if (innerpack) {
            if (Long.parseLong(actualStoreAllocMap.get("upc2")) <= 0)
                Assert.assertTrue(false, "Message contains upc2 as: " + actualStoreAllocMap.get("upc2") + " but Expected upc2 barcode should be greater than zero");
            if (Integer.valueOf(actualStoreAllocMap.get("upc2Qty")) <= 0)
                Assert.assertTrue(false, "Message contains upc2 qty as: " + actualStoreAllocMap.get("upc2Qty") + " but Expected upc2 qty should be greater than zero");
            if (Long.parseLong(actualStoreAllocMap.get("upc3")) <= 0)
                Assert.assertTrue(false, "Message contains upc3 as: " + actualStoreAllocMap.get("upc3") + " but Expected upc3 barcode should be greater than zero");
            if (Integer.valueOf(actualStoreAllocMap.get("upc3Qty")) <= 0)
                Assert.assertTrue(false, "Message contains upc3 qty as: " + actualStoreAllocMap.get("upc3Qty") + " but Expected upc3 qty should be greater than zero");

        }
        if (Integer.valueOf(actualStoreAllocMap.get("dept")) <= 0)
            Assert.assertTrue(false, "Message contains deptNbr as: " + actualStoreAllocMap.get("dept") + " but Expected dept Nbr should be greater than zero");

        if (orderType.equalsIgnoreCase("PO")) {
            if (Integer.valueOf(actualStoreAllocMap.get("totalQty")) <= 0)
                Assert.assertTrue(false, "Message contains totalQty as: " + actualStoreAllocMap.get("totalQty") + " but Expected total QTY should be greater than zero");
        }
        if (orderType.equalsIgnoreCase("WAVE")) {
            if (Integer.valueOf(actualStoreAllocMap.get("totalQty")) < 0)
                Assert.assertTrue(false, "Message contains totalQty as: " + actualStoreAllocMap.get("totalQty") + " but Expected total QTY should be greater than or Equal zero");
        }
        if (prevPut == true) {
            if (Integer.valueOf(actualStoreAllocMap.get("prevPutQTY")) <= 0)
                Assert.assertTrue(false, "Message contains prevPutQTY as: " + actualStoreAllocMap.get("prevPutQTY") + " but Expected previous put QTY should be greater than zero");
        }
        if (Integer.valueOf(actualStoreAllocMap.get("storeType")) != 0 && Integer.valueOf(actualStoreAllocMap.get("storeType")) != 1)
            Assert.assertTrue(false, "Message contains storeType as: " + actualStoreAllocMap.get("storeType") + " but Expected storeType should be 1 or 0");
        if (innerpack) {
            if (actualStoreAllocMap.get("innerPackPrefix").charAt(0) != 'Y')
                Assert.assertTrue(false, "Message contains innerPackPrefix as: " + actualStoreAllocMap.get("innerPackPrefix") + " but Expected inner Pack Prefix should be Y");
        } else {
            if (actualStoreAllocMap.get("innerPackPrefix").charAt(0) != 'N')
                Assert.assertTrue(false, "Message contains innerPackPrefix as: " + actualStoreAllocMap.get("innerPackPrefix") + " but Expected inner Pack Prefix should be N");
        }
        if (orderType.equalsIgnoreCase("PO")) {
            if (!actualStoreAllocMap.get("orderType").equalsIgnoreCase("P"))
                Assert.assertTrue(false, "Message contains orderType as: " + actualStoreAllocMap.get("orderType") + " but Expected order Type should be P");
            if (Integer.valueOf(actualStoreAllocMap.get("poNbr")) <= 0)
                Assert.assertTrue(false, "Message contains poNbr as: " + actualStoreAllocMap.get("poNbr") + " but Expected PO NBR should be greater than zero");
            if (Integer.valueOf(actualStoreAllocMap.get("poReceipt")) <= 0)
                Assert.assertTrue(false, "Message contains poReceipt as: " + actualStoreAllocMap.get("poReceipt") + " but Expected PO Receipt NBR should be greater than zero");
        }
        if (orderType.equalsIgnoreCase("WAVE")) {
            if (!actualStoreAllocMap.get("orderType").equalsIgnoreCase("W"))
                Assert.assertTrue(false, "Message contains orderType as: " + actualStoreAllocMap.get("orderType") + " but Expected order Type should be W");
            if (Double.valueOf(actualStoreAllocMap.get("waveNbr")) <= 0)
                Assert.assertTrue(false, "Message contains waveNbr as: " + actualStoreAllocMap.get("waveNbr") + " but Expected WAVE NBR should be greater than zero");
            if (!StringUtils.isBlank(actualStoreAllocMap.get("waveNbrNextElement")))
                Assert.assertTrue(false, "Message contains waveNbrNextElement as: " + actualStoreAllocMap.get("waveNbrNextElement") + " but Expected waveNbrNextElement should be of empty value");
        }
    }

    public static List<String> getLocations() {
        List<String> locList = new ArrayList<String>();
        // locList.add("CS01A001"); Commenting this as this location should not get
        // listed in the UI
        locList.add("CS01A003");
        locList.add("CS01A006");
        locList.add("CS01A004");
        locList.add("CS01A002");
        locList.add("CS01A005");
        locList.add("CS01A008");
        return locList;
    }


    public static LinkedHashMap<String, String> getValidJewelryPoLine() {
        LinkedHashMap<String, String> poLineStatus = new LinkedHashMap<String, String>();
        poLineStatus.put("1231001", "open");
        poLineStatus.put("11001102", "open");
        poLineStatus.put("11001103", "open");
        poLineStatus.put("1001101", "closed");
        poLineStatus.put("110011019", "closed");
        poLineStatus.put("1151101", "nonexistent");
        poLineStatus.put("1012009", "wrongPA");

        return poLineStatus;
    }

    public static LinkedHashMap<String, String> getValidGourmetPoLine() {
        LinkedHashMap<String, String> poLineStatus = new LinkedHashMap<String, String>();
        poLineStatus.put("11001108", "Open");
        poLineStatus.put("11001109", "Open");
        poLineStatus.put("1171101", "nonexistent");
        poLineStatus.put("1001103", "wrongPA");

        return poLineStatus;
    }

    public static LinkedHashMap<String, String> getValidOSCPoLine() {
        LinkedHashMap<String, String> poLineStatus = new LinkedHashMap<String, String>();
        poLineStatus.put("110011013", "Open");
        poLineStatus.put("110011014", "Open");
        poLineStatus.put("110011015", "Open");
        poLineStatus.put("110011016", "Open");
        poLineStatus.put("110011017", "Open");
        poLineStatus.put("110011018", "Open");
        poLineStatus.put("1001108", "Open");
        poLineStatus.put("1001006", "Open");
        poLineStatus.put("1001107", "Open");
        poLineStatus.put("1001009", "Open");
        poLineStatus.put("1002107", "Open");
        poLineStatus.put("1002009", "Open");
        poLineStatus.put("1161101", "nonexistent");
        poLineStatus.put("1012009", "wrongPA");

        return poLineStatus;
    }

    public static List<Map<String, String>> getPutAwayLocationsMap() {
        List<Map<String, String>> locations = new ArrayList<>();

//        BINBOX
        Map<String, String> loc1 = new HashMap<>();
        loc1.put(STORAGE_TYPE, BINBOX);
        loc1.put(BARCODE, "PA43M023");
        Map<String, String> loc2 = new HashMap<>();
        loc2.put(STORAGE_TYPE, BINBOX);
        loc2.put(BARCODE, "VY5V02");
        Map<String, String> loc3 = new HashMap<>();
        loc3.put(STORAGE_TYPE, BINBOX);
        loc3.put(BARCODE, "PU01S20");
        Map<String, String> loc4 = new HashMap<>();
        loc4.put(STORAGE_TYPE, BINBOX);
        loc4.put(BARCODE, "BC04H01");
        Map<String, String> loc5 = new HashMap<>();
        loc5.put(STORAGE_TYPE, BINBOX);
        loc5.put(BARCODE, "BC05H01");
        Map<String, String> loc6 = new HashMap<>();
        loc6.put(STORAGE_TYPE, BINBOX);
        loc6.put(BARCODE, "BC06H01");
        Map<String, String> loc7 = new HashMap<>();
        loc7.put(STORAGE_TYPE, BINBOX);
        loc7.put(BARCODE, "BC07H01");
        Map<String, String> loc8 = new HashMap<>();
        loc8.put(STORAGE_TYPE, BINBOX);
        loc8.put(BARCODE, "BC08H01");

//        HP
        Map<String, String> loc9 = new HashMap<>();
        loc9.put(STORAGE_TYPE, HALF_PALLET);
        loc9.put(BARCODE, "PC35K056");
        Map<String, String> loc10 = new HashMap<>();
        loc10.put(STORAGE_TYPE, HALF_PALLET);
        loc10.put(BARCODE, "PC35K055");
        Map<String, String> loc11 = new HashMap<>();
        loc11.put(STORAGE_TYPE, HALF_PALLET);
        loc11.put(BARCODE, "PC35K054");
        Map<String, String> loc12 = new HashMap<>();
        loc12.put(STORAGE_TYPE, HALF_PALLET);
        loc12.put(BARCODE, "JJ11S11");
        Map<String, String> loc13 = new HashMap<>();
        loc13.put(STORAGE_TYPE, HALF_PALLET);
        loc13.put(BARCODE, "JJ12S12");
        Map<String, String> loc14 = new HashMap<>();
        loc14.put(STORAGE_TYPE, HALF_PALLET);
        loc14.put(BARCODE, "JJ14S14");
        Map<String, String> loc15 = new HashMap<>();
        loc15.put(STORAGE_TYPE, HALF_PALLET);
        loc15.put(BARCODE, "JJ15S15");
        Map<String, String> loc16 = new HashMap<>();
        loc16.put(STORAGE_TYPE, HALF_PALLET);
        loc16.put(BARCODE, "JJ16S16");

        //        FP
        Map<String, String> loc17 = new HashMap<>();
        loc17.put(STORAGE_TYPE, FULL_PALLET);
        loc17.put(BARCODE, "DB05S06");
        Map<String, String> loc18 = new HashMap<>();
        loc18.put(STORAGE_TYPE, FULL_PALLET);
        loc18.put(BARCODE, "GB05S06");
        Map<String, String> loc19 = new HashMap<>();
        loc19.put(STORAGE_TYPE, FULL_PALLET);
        loc19.put(BARCODE, "JB05S06");
        Map<String, String> loc20 = new HashMap<>();
        loc20.put(STORAGE_TYPE, FULL_PALLET);
        loc20.put(BARCODE, "JM05S01");
        Map<String, String> loc21 = new HashMap<>();
        loc21.put(STORAGE_TYPE, FULL_PALLET);
        loc21.put(BARCODE, "KM05S01");
        Map<String, String> loc22 = new HashMap<>();
        loc22.put(STORAGE_TYPE, FULL_PALLET);
        loc22.put(BARCODE, "LM05S01");
        Map<String, String> loc23 = new HashMap<>();
        loc23.put(STORAGE_TYPE, FULL_PALLET);
        loc23.put(BARCODE, "MM05S01");
        Map<String, String> loc24 = new HashMap<>();
        loc24.put(STORAGE_TYPE, FULL_PALLET);
        loc24.put(BARCODE, "NM05S01");

        locations.add(loc1);
        locations.add(loc2);
        locations.add(loc3);
        locations.add(loc4);
        locations.add(loc5);
        locations.add(loc6);
        locations.add(loc7);
        locations.add(loc8);
        locations.add(loc9);
        locations.add(loc10);
        locations.add(loc11);
        locations.add(loc12);
        locations.add(loc13);
        locations.add(loc14);
        locations.add(loc15);
        locations.add(loc16);
        locations.add(loc17);
        locations.add(loc18);
        locations.add(loc19);
        locations.add(loc20);
        locations.add(loc21);
        locations.add(loc22);
        locations.add(loc23);
        locations.add(loc24);

        return locations;
    }

    public static List<Map<String, String>> getLocationsMap() {
        List<Map<String, String>> locations = new ArrayList<>();

//        BINBOX
        Map<String, String> loc1 = new HashMap<>();
        loc1.put(STORAGE_TYPE, BINBOX);
        loc1.put(BARCODE, "PS02A021");
        loc1.put(SORT_ZONE, "001");
        Map<String, String> loc2 = new HashMap<>();
        loc2.put(STORAGE_TYPE, BINBOX);
        loc2.put(BARCODE, "PS07A039");
        loc2.put(SORT_ZONE, "003");
        Map<String, String> loc3 = new HashMap<>();
        loc3.put(STORAGE_TYPE, BINBOX);
        loc3.put(BARCODE, "PS07A038");
        loc3.put(SORT_ZONE, "001");
        Map<String, String> loc4 = new HashMap<>();
        loc4.put(STORAGE_TYPE, BINBOX);
        loc4.put(BARCODE, "DS01K04");
        loc4.put(SORT_ZONE, "001");
        Map<String, String> loc5 = new HashMap<>();
        loc5.put(STORAGE_TYPE, BINBOX);
        loc5.put(BARCODE, "DS01K05");
        loc5.put(SORT_ZONE, "001");
        Map<String, String> loc6 = new HashMap<>();
        loc6.put(STORAGE_TYPE, BINBOX);
        loc6.put(BARCODE, "DS01K06");
        loc6.put(SORT_ZONE, "001");
        Map<String, String> loc7 = new HashMap<>();
        loc7.put(STORAGE_TYPE, BINBOX);
        loc7.put(BARCODE, "DS01K07");
        loc7.put(SORT_ZONE, "001");
        Map<String, String> loc8 = new HashMap<>();
        loc8.put(STORAGE_TYPE, BINBOX);
        loc8.put(BARCODE, "DS01K08");
        loc8.put(SORT_ZONE, "001");

//        HP
        Map<String, String> loc9 = new HashMap<>();
        loc9.put(STORAGE_TYPE, HALF_PALLET);
        loc9.put(BARCODE, "PS02A024");
        loc9.put(SORT_ZONE, "001");
        Map<String, String> loc10 = new HashMap<>();
        loc10.put(STORAGE_TYPE, HALF_PALLET);
        loc10.put(BARCODE, "PS02A022");
        loc10.put(SORT_ZONE, "001");
        Map<String, String> loc11 = new HashMap<>();
        loc11.put(STORAGE_TYPE, HALF_PALLET);
        loc11.put(BARCODE, "PS02A020");
        loc11.put(SORT_ZONE, "001");
        Map<String, String> loc12 = new HashMap<>();
        loc12.put(STORAGE_TYPE, HALF_PALLET);
        loc12.put(BARCODE, "DS01M04");
        loc12.put(SORT_ZONE, "001");
        Map<String, String> loc13 = new HashMap<>();
        loc13.put(STORAGE_TYPE, HALF_PALLET);
        loc13.put(BARCODE, "DS01M05");
        loc13.put(SORT_ZONE, "001");
        Map<String, String> loc14 = new HashMap<>();
        loc14.put(STORAGE_TYPE, HALF_PALLET);
        loc14.put(BARCODE, "DS01M06");
        loc14.put(SORT_ZONE, "001");
        Map<String, String> loc15 = new HashMap<>();
        loc15.put(STORAGE_TYPE, HALF_PALLET);
        loc15.put(BARCODE, "DS01M07");
        loc15.put(SORT_ZONE, "001");
        Map<String, String> loc16 = new HashMap<>();
        loc16.put(STORAGE_TYPE, HALF_PALLET);
        loc16.put(BARCODE, "DS01M08");
        loc16.put(SORT_ZONE, "001");


        //        FP
        Map<String, String> loc17 = new HashMap<>();
        loc17.put(STORAGE_TYPE, FULL_PALLET);
        loc17.put(BARCODE, "DS01N01");
        loc17.put(SORT_ZONE, "001");
        Map<String, String> loc18 = new HashMap<>();
        loc18.put(STORAGE_TYPE, FULL_PALLET);
        loc18.put(BARCODE, "DS01N02");
        loc18.put(SORT_ZONE, "001");
        Map<String, String> loc19 = new HashMap<>();
        loc19.put(STORAGE_TYPE, FULL_PALLET);
        loc19.put(BARCODE, "DS01N03");
        loc19.put(SORT_ZONE, "001");
        Map<String, String> loc20 = new HashMap<>();
        loc20.put(STORAGE_TYPE, FULL_PALLET);
        loc20.put(BARCODE, "DS01N04");
        loc20.put(SORT_ZONE, "001");
        Map<String, String> loc21 = new HashMap<>();
        loc21.put(STORAGE_TYPE, FULL_PALLET);
        loc21.put(BARCODE, "DS01N05");
        loc21.put(SORT_ZONE, "001");
        Map<String, String> loc22 = new HashMap<>();
        loc22.put(STORAGE_TYPE, FULL_PALLET);
        loc22.put(BARCODE, "DS01N06");
        loc22.put(SORT_ZONE, "001");
        Map<String, String> loc23 = new HashMap<>();
        loc23.put(STORAGE_TYPE, FULL_PALLET);
        loc23.put(BARCODE, "DS01N07");
        loc23.put(SORT_ZONE, "001");
        Map<String, String> loc24 = new HashMap<>();
        loc24.put(STORAGE_TYPE, FULL_PALLET);
        loc24.put(BARCODE, "DS01N08");
        loc24.put(SORT_ZONE, "001");

        locations.add(loc1);
        locations.add(loc2);
        locations.add(loc3);
        locations.add(loc4);
        locations.add(loc5);
        locations.add(loc6);
        locations.add(loc7);
        locations.add(loc8);
        locations.add(loc9);
        locations.add(loc10);
        locations.add(loc11);
        locations.add(loc12);
        locations.add(loc13);
        locations.add(loc14);
        locations.add(loc15);
        locations.add(loc16);
        locations.add(loc17);
        locations.add(loc18);
        locations.add(loc19);
        locations.add(loc20);
        locations.add(loc21);
        locations.add(loc22);
        locations.add(loc23);
        locations.add(loc24);

        return locations;
    }

    public static List<Map<String, String>> getStagingLocationsMap() {
        List<Map<String, String>> locations = new ArrayList<>();

//        BINBOX
        Map<String, String> loc1 = new HashMap<>();
        loc1.put(STORAGE_TYPE, BINBOX);
        loc1.put(BARCODE, "PR02A040");
        Map<String, String> loc2 = new HashMap<>();
        loc2.put(STORAGE_TYPE, BINBOX);
        loc2.put(BARCODE, "PR02A039");
        Map<String, String> loc3 = new HashMap<>();
        loc3.put(STORAGE_TYPE, BINBOX);
        loc3.put(BARCODE, "PR02A038");
        Map<String, String> loc4 = new HashMap<>();
        loc4.put(STORAGE_TYPE, BINBOX);
        loc4.put(BARCODE, "AC02E09");

//        HP

        Map<String, String> loc5 = new HashMap<>();
        loc5.put(STORAGE_TYPE, HALF_PALLET);
        loc5.put(BARCODE, "PR01A002");
        Map<String, String> loc6 = new HashMap<>();
        loc6.put(STORAGE_TYPE, HALF_PALLET);
        loc6.put(BARCODE, "PC35X000");
        Map<String, String> loc7 = new HashMap<>();
        loc7.put(STORAGE_TYPE, HALF_PALLET);
        loc7.put(BARCODE, "PC26X000");
        Map<String, String> loc8 = new HashMap<>();
        loc8.put(STORAGE_TYPE, HALF_PALLET);
        loc8.put(BARCODE, "KB05S06");

//        FP

        Map<String, String> loc9 = new HashMap<>();
        loc9.put(STORAGE_TYPE, FULL_PALLET);
        loc9.put(BARCODE, "AM03S01");
        Map<String, String> loc10 = new HashMap<>();
        loc10.put(STORAGE_TYPE, FULL_PALLET);
        loc10.put(BARCODE, "BM03S01");
        Map<String, String> loc11 = new HashMap<>();
        loc11.put(STORAGE_TYPE, FULL_PALLET);
        loc11.put(BARCODE, "CM03S01");
        Map<String, String> loc12 = new HashMap<>();
        loc12.put(STORAGE_TYPE, FULL_PALLET);
        loc12.put(BARCODE, "DM03S01");

        locations.add(loc1);
        locations.add(loc2);
        locations.add(loc3);
        locations.add(loc4);
        locations.add(loc5);
        locations.add(loc6);
        locations.add(loc7);
        locations.add(loc8);
        locations.add(loc9);
        locations.add(loc10);
        locations.add(loc11);
        locations.add(loc12);

        return locations;
    }

    public static HashMap<String, String> getRcptStatus() {
        HashMap<String, String> rcptStatus = new HashMap<>();
        rcptStatus.put("Req", "Req");
        rcptStatus.put("REL", "Released");
        rcptStatus.put("TRB", "Release In Trouble");
        rcptStatus.put("RIP", "Release In Progress");
        rcptStatus.put("RLF", "Release Attempt Failed");
        rcptStatus.put("PREL", "Partial Released");
        rcptStatus.put("OPEN", "OPEN");
        rcptStatus.put("CIP", "Close In Progress");
        rcptStatus.put("CAF", "Close Attempt Failed");
        return rcptStatus;
    }

    public Map<String, String> getPurchaseOrderDetails(String templateName) {
        HashMap<String, String> poReceiptDetails = new HashMap<>();
        switch (templateName) {
            case "E2E_Shortage": {
                poReceiptDetails.put("PONbr", "996847141");
                poReceiptDetails.put("RcptNbr", "9127784");
                return poReceiptDetails;
            }
            case "E2E_ReRelease": {
                poReceiptDetails.put("PONbr", "997623196");
                poReceiptDetails.put("RcptNbr", "9186905");
                return poReceiptDetails;
            }
            case "E2E_UnitPutOverage": {
                poReceiptDetails.put("PONbr", "4757543");
                poReceiptDetails.put("RcptNbr", "4523736");
                return poReceiptDetails;
            }
            case "E2E_SingleSKU": {
                poReceiptDetails.put("PONbr", "4723740");
                poReceiptDetails.put("RcptNbr", "4518754");
                return poReceiptDetails;
            }
            case "E2E_PutToStoreAndPackaway": {
                poReceiptDetails.put("PONbr", "997903904");
                poReceiptDetails.put("RcptNbr", "9164326");
                return poReceiptDetails;
            }
            case "E2E_PartialRelease_UI": {
                poReceiptDetails.put("PONbr", "998764971");
                poReceiptDetails.put("RcptNbr", "9176703");
                return poReceiptDetails;
            }
            case "E2E_Overage": {
                poReceiptDetails.put("PONbr", "997521814");
                poReceiptDetails.put("RcptNbr", "9122046");
                return poReceiptDetails;
            }
            case "E2E_InnerPack": {
                poReceiptDetails.put("PONbr", "991257229");
                poReceiptDetails.put("RcptNbr", "9146214");
                return poReceiptDetails;
            }
            case "E2E_BulkSortToStore": {
                poReceiptDetails.put("PONbr", "996612401");
                poReceiptDetails.put("RcptNbr", "9159259");
                return poReceiptDetails;
            }
            case "E2E_MultiPleSKU_OSC": {
                poReceiptDetails.put("PONbr", "994390155");
                poReceiptDetails.put("RcptNbr", "9141783");
                return poReceiptDetails;
            }
            case "HAF_2SKU": {
                poReceiptDetails.put("PONbr", "992540100");
                poReceiptDetails.put("RcptNbr", "9186851");
                return poReceiptDetails;
            }
        }
        return null;
    }

    public static HashMap<String, String> getContainerStatus() {
        HashMap<String, String> containerStatus = new HashMap<>();
        containerStatus.put("SRT", "Sorted");
        containerStatus.put("CRE", "Created");
        containerStatus.put("LCT", "Located");
        containerStatus.put("STG", "Staged");
        // Add others
        return containerStatus;
    }

    public static HashMap<String, String> getContainerType() {
        HashMap<String, String> containerType = new HashMap<>();
        containerType.put("PLT", "Pallet");
        containerType.put("TOTE", "Tote");

        return containerType;
    }

}

