package com.macys.mst.Atlas.utilmethods;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.macys.mst.artemis.config.ConfigProperties;
import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.macys.mst.artemis.db.DBUtils.readClob;
import static org.apache.commons.lang.StringUtils.substringAfterLast;

@Slf4j
public class CommonUtils {

    public static String warehouseLocNbr;
    public static Boolean packageFlag = Boolean.parseBoolean(ConfigProperties.getInstance("config.properties").getProperty("packageService"));
    private static ObjectMapper mapper = new ObjectMapper();
    private static RequestUtil requestUtil = new RequestUtil();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    /*
     * Function to get List of Maps from json String
     *
     */
    public static List<Map<String, String>> getListOfMapsFromJsonArray(JSONArray jsonArray) {

        List<Map<String, String>> responseMap = new ArrayList<>();
        try {

            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                Map<String, String> map = getMapFromJson(jsonObject.toString());
                responseMap.add(map);
            }

        } catch (Exception e) {
            log.error("Exception while converting json String to Map", e);
            Assert.fail("Exception while converting json String to Map");
        }
        log.info("Map from the response {}", responseMap);
        return responseMap;
    }

    /*
     * Function to get Map from json String
     *
     */
    public static Map<String, String> getMapFromJson(String response) {
        Map<String, String> responseMap = new HashMap<String, String>();
        try {
            if (response != JSONObject.NULL) {
                // JSONObject jsonObject = new JSONObject(response);
                responseMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
                });
            } else {
                Assert.fail("response String is null");
            }
        } catch (IOException e) {
            log.error("Exception while converting json String to Map", e);
            Assert.fail("Exception while converting json String to Map");
        }
        log.info("Map from the response {}", responseMap);
        return responseMap;
    }

    public static Map<String, String> getScreenElementsMap(WebDriver aDriver, String xpath) {
        Map<String, String> screenMap = new HashMap<String, String>();
        List<WebElement> allElements = aDriver.findElements(By.xpath(xpath));
        String screenElements = "";
        String key = null;
        String val = null;
        int size = allElements.size();
        for (int i = 0; i < size; i++) {
            String menuName = allElements.get(i).getText();

            if (i == size - 1) {
                screenMap.put(menuName.trim(), menuName.trim());

            } else {
                if (menuName.contains(":")) {
                    int index = menuName.indexOf(":");
                    if (index == menuName.length() - 1) {
                        key = StringUtils.chop(menuName);
                        val = StringUtils.chop(menuName);
                    } else {
                        key = menuName.substring(0, index);
                        val = menuName.substring(index, menuName.length() - 1);
                    }
                } else {
                    val = menuName;
                    key = menuName;
                }
                screenMap.put(key.trim(), val.trim());
            }

        }

        log.info("screenMap: {}", screenMap);
        return screenMap;
    }

    public static Map<String, String> getScreenElementData(WebDriver driver, String xpath) {
        Map<String, String> screenMap = new HashMap<String, String>();
        List<WebElement> allFields = driver.findElements(By.xpath(xpath));
        for (WebElement element : allFields) {
            String text = element.getText().trim();
            if (StringUtils.isNotEmpty(text)) {
                if (text.contains(":")) {
                    String[] keyValue = text.split(":");
                    screenMap.put(keyValue[0].trim(), keyValue.length > 1 ? keyValue[1].trim() : "");
                } else {
                    screenMap.put(text, text);
                }
            }
        }
        log.info("Get the Screen Elements data:{}", screenMap);
        return screenMap;
    }

    public static void compareValues1(Map<String, String> screenValue, Map<String, Object> dbValue) {
        String validation = "";

        for (Map.Entry<String, String> entry : screenValue.entrySet()) {
            try {

                if (commonValidation1(dbValue, entry.getKey(), entry.getValue())) {
                    validation = validation + " " + entry.getKey() + " " + entry.getValue() + " true ";
                } else {
                    validation = validation + " " + entry.getKey() + " " + entry.getValue() + " false ";
                }

            } catch (Exception e) {
                log.info("Exception not executed, key:[{}], value:[{}] ", entry.getKey(), entry.getValue());
                Assert.assertFalse(true);
            }
        }
        StepDetail.addDetail("Validation value string : " + validation, true);
        log.info("Validation value string : {}", validation);
        if (validation.contains("false")) {
            Assert.assertTrue(false);
        }

    }

    public static boolean commonValidation1(Map<String, Object> actual, String key, String value) {
        if (null != actual && !actual.isEmpty()) {
            if (null != value && !value.isEmpty() && null != key && !key.isEmpty()) {
                if (actual.containsKey(key)) {
                    Object object = actual.get(key);
                    return value.equalsIgnoreCase(object.toString());
                }
            }
        }
        return false;
    }

    /*
     * function two validate list1 values with list2
     */
    public static void validateLists(List<String> list1, List<String> list2) {

        if (null != list1 && null != list2 && !list1.isEmpty() && !list2.isEmpty()) {
            for (String value : list1) {
                if (org.apache.commons.lang.StringUtils.isNotBlank(value.trim()) && !list2.contains(value.trim())) {
                    String infoMsg = String.format("List are not matching with value: %s", value);
                    log.info(infoMsg);
                    StepDetail.addDetail(infoMsg, true);
                    Assert.fail(infoMsg);
                } else {
                    log.info("list mathching with value: {}", value);
                }
            }
        } else {
            Assert.fail("Any one of the list is Empty");
        }
    }

    public static String getRandomItem(String ContainerPrefix) {
        String prefix = ContainerPrefix.isEmpty() ? "98765" : ContainerPrefix;
        return prefix + (long) (Math.random() * 10000000L);
    }

    public static List<String> getPoLine(String processArea, String status, long lineCount) {
        List<String> poline = null;
        switch (processArea.toLowerCase()) {
            case "jewelry":
                poline = getKeyBasedOnvalueInaMap(ExpectedDataProperties.getValidJewelryPoLine(), status, lineCount);
                break;
            case "gourmet":
                poline = getKeyBasedOnvalueInaMap(ExpectedDataProperties.getValidGourmetPoLine(), status, lineCount);
                break;
            case "open sort count":
                poline = getKeyBasedOnvalueInaMap(ExpectedDataProperties.getValidOSCPoLine(), status, lineCount);
                break;
        }

        return poline;
    }

    public static List<String> getKeyBasedOnvalueInaMap(Map<String, String> testMap, String value, long lineCount) {

        return testMap.keySet().stream().filter(k -> value.equals(testMap.get(k))).limit(lineCount)
                .collect(Collectors.toList());

    }

    public static void compareValues(Map<String, String> screenValue, Map<String, String> dbValue) {
        String validation = "";
        log.info("Comparing hashMap screen values: [{}] and dbValue [{}]", screenValue, dbValue);
        for (Map.Entry<String, String> entry : screenValue.entrySet()) {
            try {

                if (commonValidation(dbValue, entry.getKey(), entry.getValue())) {
                    validation = validation + String.format("%1$s%2$s%3$s%4$s%5$s", " ", entry.getKey(), " ",
                            entry.getValue(), " true");
                } else {
                    validation = validation + String.format("%1$s%2$s%3$s%4$s%5$s", " ", entry.getKey(), " ",
                            entry.getValue(), " false");
                }

            } catch (Exception e) {
                log.info("Exception not executed, key:[{}], value:[{}] ", entry.getKey(), entry.getValue());
                Assert.assertFalse(true);
            }
        }
        StepDetail.addDetail("Validation value string : " + validation, true);
        log.info("Validation value string : {}", validation);
        if (validation.contains("false")) {
            Assert.assertTrue(false);
        }

    }

    public static void compareValueswithNullString(Map<String, String> screenValue, Map<String, String> dbValue) {
        String validation = "";
        log.info("Comparing hashMap screen values: [{}] and dbValue [{}]", screenValue, dbValue);
        for (Map.Entry<String, String> entry : screenValue.entrySet()) {
            try {

                if (commonValidationwithNullString(dbValue, entry.getKey(), entry.getValue())) {
                    validation = validation + String.format("%1$s%2$s%3$s%4$s%5$s", " ", entry.getKey(), " ",
                            entry.getValue(), " true");
                } else {
                    validation = validation + String.format("%1$s%2$s%3$s%4$s%5$s", " ", entry.getKey(), " ",
                            entry.getValue(), " false");
                }

            } catch (Exception e) {
                log.info("Exception not executed, key:[{}], value:[{}] ", entry.getKey(), entry.getValue());
                Assert.assertFalse(true);
            }
        }
        StepDetail.addDetail("Validation value string : " + validation, true);
        log.info("Validation value string : {}", validation);
        if (validation.contains("false")) {
            Assert.assertTrue(false);
        }

    }

    public static void compareValuesIgnoreEmpty(Map<String, String> screenValue, Map<String, String> dbValue) {
        String validation = "";
        log.info("Comparing hashMap screen values" + screenValue + " and dbValue " + dbValue);
        for (Map.Entry<String, String> entry : screenValue.entrySet()) {
            try {

                if (commonValidationIgnoreEmpty(dbValue, entry.getKey(), entry.getValue())) {
                    validation = validation + " " + entry.getKey() + " " + entry.getValue() + " true ";
                } else {
                    validation = validation + " " + entry.getKey() + " " + entry.getValue() + " false ";
                }

            } catch (Exception e) {
                log.info("Exception not executed, key:" + entry.getKey() + " ,value:" + entry.getValue());
                Assert.assertFalse(true);
            }
        }
        StepDetail.addDetail("Validation value string : " + validation, true);
        log.info("Validation value string : " + validation);
        if (validation.contains("false")) {
            Assert.assertTrue(false);
        }

    }

    public static boolean commonValidationIgnoreEmpty(Map<String, String> actual, String key, String value) {
        if (null != actual) {
            if (null != value && null != key && !key.isEmpty()) {
                if (actual.containsKey(key)) {
                    Object object = actual.get(key);
                    if (value.equalsIgnoreCase(object.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean commonValidation(Map<String, String> actual, String key, String value) {
        if (null != actual && !actual.isEmpty()) {
            if (null != value && !value.isEmpty() && null != key && !key.isEmpty()) {
                if (actual.containsKey(key)) {
                    Object object = actual.get(key);
                    return value.equalsIgnoreCase(object.toString());
                }
                return false;
            }
            return true;
        }
        return true;

    }

    public static boolean commonValidationwithNullString(Map<String, String> actual, String key, String value) {
        if (actual.containsKey(key)) {
            Object object = actual.get(key);
            return value.equalsIgnoreCase(object.toString());
        }
        return false;
    }

    public static String getRequestResponse(String path) {
        log.info("request path : {}", path);
        Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).get(path);
        if (response.getStatusCode() == 204)
            return "";
        else if (response == null || response.getStatusCode() != 200 && response.getStatusCode() != 201) {
            Assert.assertTrue("Response is null or response code is !=200 ", false);
            return null;
        } else {
            return response.asString();
        }

    }


    public static String getRequestResponse(String path, boolean prodEndpointFlag) {
        log.info("request path : {}", path);
        Response response = RestAssured.given().relaxedHTTPSValidation().headers(ExpectedDataProperties.getHeaderProps()).get(path);
        if (response.getStatusCode() == 204)
            return "";
        else if (response == null || response.getStatusCode() != 200 && response.getStatusCode() != 201) {
            Assert.assertTrue("Response is null or response code is !=200 ", false);
            return null;
        } else {
            return response.asString();
        }

    }

    public static int getRandomNumber(int size) {
        Random rand = new Random();
        return rand.nextInt(size);
    }

    public static void deleteRequest(String path, int statusCode) {
        log.info("request path : {} and status code: {}", path, statusCode);
        StepDetail.addDetail("request path :  " + path, true);
        Response response = null;
        response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).contentType(ContentType.JSON).when().delete(path);
        log.info("response.getStatusCode(): {}", response.getStatusCode());
        Assert.assertTrue("Status Code for response : " + response.getStatusCode(),
                response.getStatusCode() == statusCode);
    }

    public static <T> T getClientResponse(String actualJson, TypeReference<T> returnType) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            mapper.setDateFormat(df);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(actualJson, returnType);
        } catch (Exception e) {
            log.error("GetClientResponse:", e);
            return null;
        }
    }

//    public static List<POLineItems> POOrderDetails(String PO) {
//        String orderEndpoint = OrderEndPoint.ORDER_SERVICE + "/" + PO;
//        String orderGetResponse = RestUtilities.getRequestResponse(orderEndpoint);
//        log.info("orderGetResponse :{}", orderGetResponse);
//        POLineItems[] lineItems = CommonUtils.getClientResponse(orderGetResponse, new TypeReference<POLineItems[]>() {
//        });
//
//        return Arrays.asList(lineItems);
//
//    }
//
//    public static PODetails getPODetails(String poNbr, String receiptNbr) {
//        String poEndPoint = String.format(PO4WallEndPoint.PO4WALL_GET_PO_DETAILS, poNbr, receiptNbr);
//        PODetails lineItems = CommonUtils.getClientResponse(RestUtilities.getRequestResponse(poEndPoint), new TypeReference<PODetails>() {
//        });
//        return lineItems;
//    }

    public static Map<String, List<Object>> resultSetToMap(ResultSet rs) throws SQLException, IOException {
        if (rs != null) {
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            Map<String, List<Object>> map = new HashMap<>(columns);
            for (int i = 1; i <= columns; ++i) {
                map.put(md.getColumnLabel(i), new ArrayList<>());
            }
            while (rs.next()) {
                for (int i = 1; i <= columns; ++i) {
                    if (rs.getObject(i) != null) {
                        if (md.getColumnClassName(i).contains("CLOB")) {
                            String clobToString = readClob((Clob) rs.getObject(i));
                            map.get(md.getColumnLabel(i)).add(clobToString);
                        } else {
                            map.get(md.getColumnLabel(i)).add(rs.getObject(i));
                        }
                    } else {
                        map.get(md.getColumnLabel(i)).add(JSONObject.NULL);
                    }
                }
            }

            // LOGGER.info("RESULT SET : " + map);
            // dbIntializer.closeDBResources();
            return map;
        } else {
            return null;
        }
    }

    public static List<Map<?, ?>> resultSetToList(ResultSet rs) throws SQLException, IOException {
        if (rs != null) {
            List<Map<?, ?>> result = new ArrayList<Map<?, ?>>();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            int index = 0;
            while (rs.next()) {
                Map resultMap = new LinkedHashMap();
                for (int i = 1; i <= columns; ++i) {
                    if (rs.getObject(i) != null) {
                        // resultMap.put(md.getColumnLabel(i), rs.getObject(i));

                        if (md.getColumnClassName(i).contains("CLOB")) {
                            String clobToString = readClob((Clob) rs.getObject(i));
                            resultMap.put(md.getColumnLabel(i), clobToString);
                        } else {
                            resultMap.put(md.getColumnLabel(i), rs.getObject(i));
                        }

                    } else {
                        resultMap.put(md.getColumnLabel(i), null);
                    }
                }
                result.add(index, resultMap);
                index = index + 1;
            }
            return result;
        } else {
            return null;
        }
    }

    public static List<Map<Object, Object>> resultSetToStringList(ResultSet rs) throws SQLException, IOException {
        if (rs != null) {
            List<Map<Object, Object>> result = new ArrayList<Map<Object, Object>>();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            int index = 0;
            while (rs.next()) {
                Map resultMap = new LinkedHashMap();
                for (int i = 1; i <= columns; ++i) {
                    if (rs.getObject(i) != null) {
                        // resultMap.put(md.getColumnLabel(i), rs.getObject(i));

                        if (md.getColumnClassName(i).contains("CLOB")) {
                            String clobToString = readClob((Clob) rs.getObject(i));
                            resultMap.put(md.getColumnLabel(i), clobToString);
                        } else {
                            resultMap.put(md.getColumnLabel(i), rs.getObject(i));
                        }

                    } else {
                        resultMap.put(md.getColumnLabel(i), null);
                    }
                }
                result.add(index, resultMap);
                index = index + 1;
            }
            return result;
        } else {
            return null;
        }

    }

    public static List<Map<String, String>> resultSetToStringListMap(ResultSet rs) throws SQLException, IOException {
        if (rs != null) {
            List<Map<String, String>> result = new ArrayList<Map<String, String>>();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            int index = 0;
            while (rs.next()) {
                Map<String, String> resultMap = new LinkedHashMap<String, String>();
                for (int i = 1; i <= columns; ++i) {
                    if (rs.getObject(i) != null) {
                        if (md.getColumnClassName(i).contains("CLOB")) {
                            String clobToString = readClob((Clob) rs.getObject(i));
                            resultMap.put(String.valueOf(md.getColumnLabel(i)).trim(), String.valueOf(clobToString).trim());
                        } else {
                            resultMap.put(String.valueOf(md.getColumnLabel(i)).trim(), String.valueOf(rs.getObject(i)).trim());
                        }

                    } else {
                        resultMap.put(String.valueOf(md.getColumnLabel(i)), "");
                    }
                }
                result.add(index, resultMap);
                index = index + 1;
            }
            return result;
        } else {
            return null;
        }

    }


    public static String getRandomCartonNumber(String prefix, String length) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String thirteenDigits = String.valueOf(timestamp.getTime());
        String sevenDigits = String.format("%-7s", prefix).replace(' ', '0');
        String nextContainer = sevenDigits + thirteenDigits;
        nextContainer = nextContainer.substring(0, Integer.parseInt(length));
        return nextContainer;
    }

    public static <T> T getMatchedObject(Collection<T> objects, String matchedValue) {
        for (T object : objects) {
            if (object.toString().contains(matchedValue))
                return object;
        }
        return null;
    }

    public static void publishMessage(String projectId, String topic, List<String> messages) {
        log.info("Project id:[{}]", projectId);
        log.info("Topic :[{}]", topic);

        PubSubUtil pubSubUtil = new PubSubUtil();
        try {
            pubSubUtil.publishMessage(projectId, topic, messages);
        } catch (Exception e) {
            log.error("Failed to publish message", e);
        }
    }

    public static String convertListStringToSingleQuotes(List<String> listObject) {
        return listObject.stream().collect(Collectors.joining("','", "'", "'"));
    }

//    public static Map<String, String> loadConfig(String appName, String moduleName, String configKey, String configValueKey, String value) {
//        String response = RestUtilities.getRequestResponse(String.format(ConfigurationEndPoint.CONFIG_URL, appName, moduleName, configKey, configValueKey));
//        JSONArray jsonArray = new JSONArray(response);
//        Map<String, String> configMap = null;
//        if (null != jsonArray && jsonArray.length() > 0) {
//            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
//            JSONArray configValueArray = new JSONArray(jsonObject.getString("configValue"));
//            configMap = getListOfMapsFromJsonArray(configValueArray).stream().filter(filter -> {
//                return value.equalsIgnoreCase(filter.get(configValueKey));
//            }).findAny().orElse(null);
//        }
//        return configMap;
//    }
//
//    public static List<Map<String, String>> loadConfig(String appName, String moduleName, String configKey) {
//
//        String response = RestUtilities.getRequestResponse(String.format(ConfigurationEndPoint.CONFIG_URL, appName, moduleName, configKey));
//        JSONArray jsonArray = new JSONArray(response);
//        Map<String, String> configMap = null;
//        if (null != jsonArray && jsonArray.length() > 0) {
//            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
//            JSONArray configValueArray = new JSONArray(jsonObject.getString("configValue"));
//            return getListOfMapsFromJsonArray(configValueArray);
//        }
//        return null;
//    }


//    public static JSONObject verifyPutToStoreMsgResponse(String trasName, String destinationId, String toteId) {
//
//        JSONObject jsonObject = getMessageResponse(trasName, destinationId, toteId);
//        if (null != jsonObject) {
//            //  JSONObject trasSts = jsonObject.getJSONObject("transmissionStatus");
//            String trasSts = jsonObject.getString("transmissionStatus");
//            //  String trasID = String.valueOf(jsonObject.getString("id"));
//            String destID = jsonObject.getString("destinationId");
//            String transName = jsonObject.getString("transactionName");
//            //  log.info("Tranmission Status id validated: {}", trasSts.get("id"));
//            //  log.info("Tranmission Status  validated: {}", trasSts.get("status"));
//            log.info("Tranmission Status  validated: {}", trasSts);
//            log.info("Destination ID: {}", destID);
//            log.info("TransactionName: {}", transName);
//            //  log.info("TransactionId: {}", trasID);
//
//        } else {
//            Assert.fail("MHE not generated for expected container " + toteId + " with the transaction name: " + trasName);
//        }
//        return jsonObject;
//    }

//    public static JSONObject getMessageResponse(String trasName, String destinationId, String toteId) {
//        String response = RestUtilities.getRequestResponse(ReadHostConfiguration.GET_MESSAGE_SERVICE_URL.value() + toteId);
//        log.info("Response is:" + response);
//        if (org.apache.commons.lang.StringUtils.isNotEmpty(response) && response != JSONObject.NULL) {
//            boolean found = false;
//            JSONObject messageObject = new JSONObject(response);
//            JSONObject messageResponseDTO = (JSONObject) messageObject.get("MessageResponseDTO");
//            JSONArray messageList = (JSONArray) messageResponseDTO.get("messages");
//            for (Object data : messageList) {
//                JSONObject jsonObject = (JSONObject) data;
//                if (destinationId.equals(jsonObject.getString("destinationId")) && trasName.equals(jsonObject.getString("transactionName"))) {
//                    return jsonObject;
//                }
//            }
//        }
//        return null;
//    }

//    public static void verifyMsgServiceResponse(String trasName, String destinationId, String toteId) {
//
//        JSONObject jsonObject = getMessageResponse(trasName, destinationId, toteId);
//        if (null != jsonObject) {
//            //    JSONObject trasSts = jsonObject.getJSONObject("transmissionStatus");
//            String trasSts = jsonObject.getString("transmissionStatus");
//            //  String trasID = String.valueOf(jsonObject.getString("id"));
//            String destID = jsonObject.getString("destinationId");
//            String transName = jsonObject.getString("transactionName");
//            //  log.info("Tranmission Status code validated: {}", trasSts.get("id"));
//            //  log.info("Tranmission Status code validated: {}", trasID);
////            Assert.assertTrue("Tranmission Status in validated: ", "4".equals(trasSts.get("id").toString()));
//            //  log.info("Tranmission Status in validated: {}", trasSts.get("status"));
//            log.info("Tranmission Status in validated: {}", trasSts);
//            log.info("Tranmission Status in validated: {}", destID);
//            log.info("Tranmission Status in validated: {}", transName);
//            //          Assert.assertEquals("SENT_TO_DEST", trasSts.get("status"));
//            JSONObject incmgpayload = new JSONObject(jsonObject.getString("incomingPayload"));
//            JSONObject payload = new JSONObject(jsonObject.getString("incomingPayload")).getJSONObject("payload");
//
//            if (payload.getString("container").equals(toteId)) {
//                Assert.assertTrue("Container is validated", true);
//                log.info("Container is validated: {}", payload.getString("container"));
//
//                //Assert.assertTrue("destination in validated: ", payload.getString("destination").equals("V"));
//                //log.info("destination in validated: {}", payload.getString("destination"));
//
//                Assert.assertTrue("Transaction Name is validated: ", incmgpayload.getString("tname").equals(trasName));
//                log.info("Transaction Name is validated: {}", incmgpayload.getString("tname"));
//
//                Assert.assertTrue("clientId is validated: ", incmgpayload.getString("clientId").equals("HandHeld"));
//                log.info("clientId is validated: {}", incmgpayload.getString("clientId"));
//            }
//
//        } else {
//            Assert.fail("MHE not generated for expected container " + toteId + " with the transaction name: " + trasName);
//        }
//    }
//
//    public static InventoryContainer getInventory(String containerId) {
//        String inventoryResponse = CommonUtils.getRequestResponse(ReadHostConfiguration.GET_INVENTORY_SERVICE_URL.value().replace("{totebarcode}", containerId));
//        if (org.apache.commons.lang.StringUtils.isNotBlank(inventoryResponse)) {
//            return CommonUtils.getClientResponse(inventoryResponse, new TypeReference<InventoryContainer>() {
//            });
//        }
//        return null;
//    }
//
//    public static void pyramidResponseValidation(String jsonResult) {
//        log.info("pyramid simulation url: {}", MHEmessagingServiceURLS.pyramidSimulatorPostReverse_URL);
//        Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).contentType(ContentType.TEXT).body(jsonResult).when().post(MHEmessagingServiceURLS.pyramidSimulatorPostReverse_URL);
//        log.info("Pyramid Response status: {}", response.statusCode());
//        org.testng.Assert.assertTrue(response.getStatusCode() == 200);
//        StepDetail.addDetail("Pyramid Response status: " + response.statusCode(), true);
//        JSONObject messageObject = new JSONObject(response.body().asString());
//        JSONObject responseEntity = (JSONObject) messageObject.get("ResponseEntity");
//        String responseBody = responseEntity.getString("body");
//        log.info("Body : {}", responseBody);
//        StepDetail.addDetail("Pyramid Response body: " + responseBody, true);
//        org.testng.Assert.assertEquals(substringAfterLast(responseBody, "|"), "ACK\u0003");
//    }
//
//    public static void pyramidJSONResponseValidation(String jsonpayload, String transName) {
//        String pyramid_JSON_url = MHEmessagingServiceURLS.pyramidSimulatorPostJSON_URL + "/" + transName;
//        log.info("pyramid JSON url: {}", pyramid_JSON_url);
//        Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).contentType(ContentType.JSON).body(jsonpayload).when().post(pyramid_JSON_url);
//        log.info("Pyramid Response status: {}", response.statusCode());
//        org.testng.Assert.assertTrue(response.getStatusCode() == 200);
//        StepDetail.addDetail("Pyramid Response status: " + response.statusCode(), true);
//        String responseBody = response.getBody().asString();
//        log.info("Body : {}", responseBody);
//        StepDetail.addDetail("Pyramid Response body: " + responseBody, true);
//        Assert.assertEquals("Pyramid Response is not as expected", "ACK", substringAfterLast(responseBody, "|").substring(0, 3));
//    }
//
//    public static String decryptBase64(String encryptString) {
//        return new String(Base64.getDecoder().decode(encryptString));
//    }
//
//    public static JSONObject getInventoryDetails(String tote) {
//        JSONObject inventoryObj = null;
//        String response = RestUtilities.getRequestResponse(
//                ReadHostConfiguration.CREATE_INVENTORY_URL.value() + ReadHostConfiguration.LOCATION_NUMBER.value() + "/containers?barcode=" + tote);
//
//        if (response != JSONObject.NULL) {
//            JSONObject jsonObject = new JSONObject(response);
//
//            JSONArray inventorySnapshotList = (JSONArray) jsonObject.get("inventorySnapshotList");
//            if (null != inventorySnapshotList && 0 < inventorySnapshotList.length()) {
//                for (int i = 0; i < inventorySnapshotList.length(); i++) {
//                    inventoryObj = inventorySnapshotList.getJSONObject(i);
//                    log.info("inventoryObj : " + inventoryObj);
//                }
//            } else {
//                Assert.fail("response String is null");
//            }
//        } else {
//            Assert.fail("response String is null");
//        }
//        return inventoryObj;
//    }
//
//    public static void sendContDivertMessage(String containerId, String divertLane) {
//        if (!ExpectedDataProperties.pyramidJsonproperty) {
//            String jsonResult = MHE_MessagingReverseJSON.CONT_DIVERT;
//            jsonResult = jsonResult
//                    .replace("#sequenceno#", String.valueOf(System.currentTimeMillis()).substring(4, 13))
//                    .replace("#lpnNumber#", containerId)
//                    .replace("#divertedLane#", divertLane);
//            log.info("CONTDIVERT input payload: {}", jsonResult);
//            pyramidResponseValidation(jsonResult);
//        } else {
//            String requestParams = "{#sequenceno:D-9,#lpnNumber:" + containerId + ",#divertedLane:" + divertLane + "}";
//            List<String> messageBody = requestUtil.getRequestBody(requestParams, "CONTDIVERT.json");
//            log.info("CONTDIVERT input JSON payload: {}", messageBody);
//            for (String eachMessageBody : messageBody) {
//                pyramidJSONResponseValidation(eachMessageBody, "CONTDIVERT");
//            }
//        }
//    }

    public static String getQueryString(Map<String, String> params) {
        StringBuilder builder = new StringBuilder("?");
        params.forEach((key, value) -> builder.append(key + "=" + value + "&"));

        return builder.substring(0, builder.length() - 1);
    }

//    public static String getInventoryAdjHistory(String containerId) {
//        String inventoryResponse = CommonUtils.getResponseCode(ReadHostConfiguration.GET_INVENTORY_ADJUSTMENT_HISTORY.value().replace("{totebarcode}", containerId));
//        return inventoryResponse;
//    }

    public static String getResponseCode(String path) {
        log.info("request path : {}", path);
        Response response = RestAssured.given().headers(ExpectedDataProperties.getHeaderProps()).get(path);
        if (response.getStatusCode() == 200)
            return "Inventory Adjustment History Found" + response.asString();
        else if (response == null || response.getStatusCode() != 200 && response.getStatusCode() != 201) {
            Assert.assertTrue("Inventory Adjustment History Not Found ", false);
            return null;
        } else {
            return response.asString();
        }
    }

    public static String getWarehouseLocNbr() {
        if (null == warehouseLocNbr || warehouseLocNbr.isEmpty()) {
            warehouseLocNbr = FileConfig.getInstance().getStringConfigValue("warehouseLocNbr");
        }
        return warehouseLocNbr;
    }

    public static void doJbehavereportConsolelogAndAssertion(String info, String content, boolean condition) {
        log.info(info + " = " + content);
        StepDetail.addDetail(info + " = " + content, condition);
        Assert.assertTrue(info + " = " + content, condition);
    }

    public static String getQuotedString(String str) {
        List<String> strList = Arrays.asList(str.split(","));
        return String.join(",", strList.stream().map(str1 -> "'" + str1 + "'").collect(Collectors.toList()));
    }

    public static String createString(String charSeq, int length) {
        String requiredString = "\n";
        int i = 1;
        while (i != length) {
            requiredString += charSeq;
            i++;
        }
        return requiredString;
    }

    public String getCurrentDateTime(int minutes) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, minutes);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(cal.getTime());

    }

    public String getUrl(String tagVal) {
        return FileConfig.getInstance().getStringConfigValue("services." + tagVal).replace("#locNbr", getWarehouseLocNbr());
    }

    public String getEnvConfigValue(String tagVal) {
        return FileConfig.getInstance().getStringConfigValue(tagVal);
    }

    public Map<String, String> getParamsToMap(String params) {
        Map<String, String> requestParamsMap = Maps.newHashMap(Splitter.on(",").withKeyValueSeparator(":").split(params));
        requestParamsMap.replaceAll((key, value) -> value.replaceAll(";", ","));
        return requestParamsMap;
    }

    public String getContainerDetailsbyBarcode(String barcode) {
        if (packageFlag && "15".equals(barcode.substring(0, 2))) {
            try {
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put("barcode", barcode);
                String endpoint = getUrl("packageService.createPackage");
                Response response = WhmRestCoreAutomationUtils.getRequestResponse(endpoint, queryParams).asResponse();
                return response.asString();
            } catch (Exception e) {
                log.info("unable to get inventory for barCode: " + barcode + " with error " + e.getMessage());
                return "";
            }
        } else {
            try {
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put("barcode", barcode);
                String endpoint = getUrl("InventoryServices.CreateInventory");
                Response response = WhmRestCoreAutomationUtils.getRequestResponse(endpoint, queryParams).asResponse();
                return response.asString();
            } catch (Exception e) {
                log.info("unable to get inventory for barCode: " + barcode + " with error " + e.getMessage());
                return "";
            }
        }
    }

    public boolean consumeContainersbyBarcode(String barcode) {
        if (packageFlag && "15".equals(barcode.substring(0, 2))) {
            try {
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put("reason", "RR");
                String endpoint = getUrl("packageService.deletePackage");
                Response response = WhmRestCoreAutomationUtils.deleteRequestResponse(endpoint.replace("#sourceBarcode", barcode), queryParams).asResponse();
                doJbehavereportConsolelogAndAssertion("Container not deleted for Barcode ", barcode, 204 == response.getStatusCode());
                return true;
            } catch (Exception e) {
                log.info("unable to get inventory for barCode: " + barcode + " with error " + e.getMessage());
                return false;
            }
        } else {
            try {
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put("reasonCode", "RR");
                String endpoint = getUrl("InventoryServices.DeleteInventory");
                Response response = WhmRestCoreAutomationUtils.deleteRequestResponse(endpoint.replace("#sourceBarcode", barcode), queryParams).asResponse();
                doJbehavereportConsolelogAndAssertion("Container not deleted for Barcode ", barcode, 204 == response.getStatusCode());
                return true;
            } catch (Exception e) {
                log.info("unable to get inventory for barCode: " + barcode + " with error " + e.getMessage());
                return false;
            }
        }
    }

    public static void waitSec(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            log.error("InterruptedException:", e);
        }
    }

    public String getPackageDetailByBarcode(String barcode) {
        return RestUtilities.getRequestResponse(String.format(getUrl("packageService.getPackage"), barcode));
    }
    public static String getUTCDatetimeAsString() {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());

    }

//    public static JSONObject getContainerDetails(String tote) {
//        JSONObject containerObj = null;
//        String response = RestUtilities.getRequestResponse(
//                ReadHostConfiguration.CREATE_INVENTORY_URL.value() + ReadHostConfiguration.LOCATION_NUMBER.value() + "/containers?barcode=" + tote);
//
//        if (response != JSONObject.NULL) {
//            JSONObject jsonObject = new JSONObject(response);
//
//            JSONArray container = (JSONArray) jsonObject.get("container");
//            if (null != container && 0 < container.length()) {
//                for (int i = 0; i < container.length(); i++) {
//                    containerObj = container.getJSONObject(i);
//                    log.info("containerObj : " + containerObj);
//                }
//            } else {
//                Assert.fail("response String is null");
//            }
//        } else {
//            Assert.fail("response String is null");
//        }
//        return containerObj;
//    }

    public HashMap<String,String> getContainerRelationShipList(String response) {
        String containerObj = null;
        HashMap<String, String> map = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        if (response != JSONObject.NULL) {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject containerObject = (JSONObject) jsonObject.get("container");
            JSONArray container = (JSONArray) containerObject.get("containerRelationshipList");
            if (null != container && 0 < container.length()) {
                for (int i = 0; i < container.length(); i++) {
                    containerObj = container.getJSONObject(i).toString();
                    log.info("containerObj : " + containerObj);
                }
            } else {
                org.junit.Assert.fail("response String is null");
            }
        } else {
            org.junit.Assert.fail("response String is null");
        }
        try
        {
            map = mapper.readValue(containerObj, new TypeReference<Map<String, String>>(){});
            System.out.println(map);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

}
