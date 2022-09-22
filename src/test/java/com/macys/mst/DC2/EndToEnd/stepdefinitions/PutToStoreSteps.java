package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.configuration.InventoryEndPoint;
import com.macys.mst.DC2.EndToEnd.constants.MHE_MessagingReverseJSON;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLPo4Walls;
import com.macys.mst.DC2.EndToEnd.model.Attribute;
import com.macys.mst.DC2.EndToEnd.model.CartonDetails;
import com.macys.mst.DC2.EndToEnd.model.ContainerRelation;
import com.macys.mst.DC2.EndToEnd.model.GenericMessagePayload;
import com.macys.mst.DC2.EndToEnd.model.InventoryContainer;
import com.macys.mst.DC2.EndToEnd.model.InventorySnapshot;
import com.macys.mst.DC2.EndToEnd.model.MessagePayload;
import com.macys.mst.DC2.EndToEnd.model.SKUDetails;
import com.macys.mst.DC2.EndToEnd.model.ToteAllocDetails;
import com.macys.mst.DC2.EndToEnd.model.Unitput;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.DC2.EndToEnd.utilmethods.RequestUtil;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.whm.coreautomation.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.ToContext;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.json.JSONObject;
import org.testng.Assert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.macys.mst.DC2.EndToEnd.db.app.SQLQueriesERS.SELECT_FLO_CNTR_BY_CNTR;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.removeEnd;

@Slf4j
public class PutToStoreSteps {

    private static final String UNIT_PUT = "UNITPUT";
    private static final String CONT_CLOSED = "CONTCLOSED";
    private static final String TOTE_CLOSE = "TOTECLOSE";
    private static final String NEW = "N";
    private static final String REGULAR = "R";
    private static final String SCHEMA = "LFCBIZ01";
    //public static Map<String, List<String>> cartonStoreDetail;
    private static RequestUtil requestUtil = new RequestUtil();
    ValidationUtil validationUtils = new ValidationUtil();
    CommonUtils commonUtils = new CommonUtils();
    private StepsContext stepsContext;
    private String format;
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    @Setter
    @Getter
    private List<InventoryContainer> inventoryContainers;
    public PutToStoreSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @Given("PO Details $param")
    public void poData(ExamplesTable table) {
        for (Iterator<Map<String, String>> iterator = table.getRows().iterator(); iterator.hasNext(); ) {
            Map<String, String> exRows = iterator.next();
            stepsContext.put(Context.PO_NBR.name(), exRows.get("PO_NBR"), ToContext.RetentionLevel.SCENARIO);
            stepsContext.put(Context.PO_RCPT_NBR.name(), exRows.get("RCPT_NBR"), ToContext.RetentionLevel.SCENARIO);
        }
    }

    @Then("Inventory is created for outbound cartons and decreased from original totes")
    public void verifyCartons() throws InterruptedException {
        List<CartonDetails> cartonDetails = (List<CartonDetails>)stepsContext.get(Context.CARTON_DETAILS.name());
        Map<String, List<String>> cartonIdToteMap = cartonDetails.stream().collect(groupingBy(CartonDetails::getCartonId, mapping(CartonDetails::getToteId, toList())));
        log.info("cartonIdToteMap : {}", cartonIdToteMap);
        stepsContext.put(Context.CARTON_TOTE_MAP.name(), cartonIdToteMap, ToContext.RetentionLevel.SCENARIO);
        Thread.sleep(50000);
        cartonIdToteMap.forEach((cartonId, toteIds) -> {
            String response = commonUtils.getContainerDetailsbyBarcode(cartonId);
            try {
                if (null != response) {
                    JsonPath cartondetail = new JsonPath(response);
                    if (CommonUtils.packageFlag) {
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as IPK for Carton: Carton ID", cartonId, "IPK".equals(cartondetail.getString("[0].status")));
                    } else {
                        CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as IPK for Carton: Carton ID", cartonId, "IPK".equals(cartondetail.getString("container.containerStatusCode")));
                    }
                } else {
                    log.info("P2S: Unable to create Carton: {}", cartonId);
                    Assert.assertTrue(false, "P2S: Unable to create Carton:" + cartonId);
                    StepDetail.addDetail(String.format("P2S: Unable to create Carton: %s", cartonId), true);
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            toteIds.forEach(toteId -> {
                InventoryContainer toteDetail = CommonUtils.getInventory(toteId);
                log.info("ToteDetail : {}", toteDetail);
                if (null != toteDetail) {
                    log.info("Tote is partially moved. Tote Id: {}", toteId);
                } else {
                    log.info("P2S: Tote [{}] is successfully moved into carton [{}]", toteId, cartonId);
                    StepDetail.addDetail(String.format("P2S: Tote [%s] is successfully moved into carton [%s]", toteId, cartonId), true);
                }
            });
        });
    }

    @Then("TOTECOMP message is sent by pyramid after totes are emptied")
    public void verifyToteCompMessage() {
        List<CartonDetails> cartonDetails = (List<CartonDetails>)stepsContext.get(Context.CARTON_DETAILS.name());
        Map<String, List<String>> cartonIdToteMap = cartonDetails.stream().collect(groupingBy(CartonDetails::getCartonId, mapping(CartonDetails::getToteId, toList())));

        List<String> toteIds = cartonIdToteMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        log.info("TOTECOMP toteIds : {}", toteIds);

        toteIds.forEach(toteId -> {
            int quantity = 0;
            InventoryContainer toteDetail = CommonUtils.getInventory(toteId);
            if (null != toteDetail) {
                quantity = toteDetail.getInventorySnapshotList().get(0).getQuantity();
            }
            String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String jsonResult = MHE_MessagingReverseJSON.TOTE_CLOSE_MESSAGE;
                jsonResult = jsonResult
                        .replace("#sequenceno#", seqId)
                        .replace("#toteNumber#", toteId)
                        .replace("#quantity#", String.valueOf(quantity));
                log.info("Tote CLose Message:{}", jsonResult);
                if (quantity == 0) {
                    CommonUtils.pyramidResponseValidation(jsonResult);
                }

                String trasName = String.format("%s:%s", seqId, TOTE_CLOSE);
                CommonUtils.verifyPutToStoreMsgResponse(trasName, TOTE_CLOSE, toteId);
            } else {
                String requestParams = "{#sequenceno:D-9,#container:" + toteId + ",#SkuQty:0" + "}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "TOTECLOSE.json");
                log.info("TOTECLOSE input JSON payload: {}", messageBody);
                for (String eachMessageBody : messageBody) {
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "TOTECLOSE");
                    JSONObject json = new JSONObject(eachMessageBody);
                    seqId = json.getJSONObject("payload").getString("sequenceNo");
                }

                String trasName = TOTE_CLOSE;
                CommonUtils.verifyPutToStoreMsgResponse(trasName, TOTE_CLOSE, toteId);
            }
        });

    }


    @Then("CONTCLOSED message is sent after the carton is closed")
    public void verifyCartonCloseMessage() {
        List<CartonDetails> cartonDetails = (List<CartonDetails>)stepsContext.get(Context.CARTON_DETAILS.name());
        Map<String, String> cartonIdStoreLocationMap = cartonDetails.stream().collect(
                Collectors.toMap(CartonDetails::getCartonId, CartonDetails::getStoreLocationNumber,
                        (storeLocationNumber1, storeLocationNumber2) -> {
                            if (storeLocationNumber1.equals(storeLocationNumber2))
                                return storeLocationNumber1;
                            else
                                return storeLocationNumber1 + "," + storeLocationNumber2;
                        }));

        stepsContext.put(Context.CARTON_STORE_MAP.name(), cartonIdStoreLocationMap, ToContext.RetentionLevel.SCENARIO);
        log.info("cartonIdStoreLocationMap  : {}", cartonIdStoreLocationMap);
        cartonIdStoreLocationMap.forEach((cartonId, storeLocation) -> {
            String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
            if (!ExpectedDataProperties.pyramidJsonproperty) {
                String jsonResult = MHE_MessagingReverseJSON.CONT_CLOSE_MESSAGE;
                jsonResult = jsonResult
                        .replace("#sequenceno#", seqId)
                        .replace("#cartonNumber#", cartonId)
                        .replace("#storeLocNbr#", storeLocation);
                log.info("Cont close message :{}", jsonResult);
                CommonUtils.pyramidResponseValidation(jsonResult);
                String trasName = String.format("%s:%s", seqId, CONT_CLOSED);
                CommonUtils.verifyPutToStoreMsgResponse(trasName, CONT_CLOSED, cartonId);
            } else {
                String requestParams = "{#sequenceno:D-9,#container:" + cartonId + ",#storeNbr:" + storeLocation + "}";
                List<String> messageBody = requestUtil.getRequestBody(requestParams, "CONTCLOSED.json");
                log.info("CONTCLOSED input JSON payload: {}", messageBody);
                for (String eachMessageBody : messageBody) {
                    CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "CONTCLOSED");
                    JSONObject json = new JSONObject(eachMessageBody);
                    seqId = json.getJSONObject("payload").getString("sequenceNo");
                }
                String trasName = CONT_CLOSED;
                CommonUtils.verifyPutToStoreMsgResponse(trasName, CONT_CLOSED, cartonId);

            }
        });

        //validateCartonAndToteInventory();
        validateFLOContainerDetails();

    }

    private void validateCartonAndToteInventory() {

        List<InventorySnapshot> inventorySnapshots = getInventoryContainers().stream().flatMap(container -> container.getInventorySnapshotList().stream()).collect(Collectors.toList());
        Map<String, List<InventorySnapshot>> toteInventoryShapshotMap = inventorySnapshots.stream().collect(groupingBy(InventorySnapshot::getContainer));
        List<CartonDetails> cartonDetails = (List<CartonDetails>)stepsContext.get(Context.CARTON_DETAILS.name());
        Map<String, List<String>> cartonIdToteMap = cartonDetails.stream().collect(groupingBy(CartonDetails::getCartonId, mapping(CartonDetails::getToteId, toList())));
        cartonIdToteMap.forEach((cartonId, toteIds) -> {
            InventoryContainer cartonDetail = CommonUtils.getInventory(cartonId);
            if (null != cartonDetail) {
                Assert.assertEquals(cartonDetail.getContainer().getContainerType(), "CRT");
                StepDetail.addDetail(String.format("CONT CLOSE: Carton ID: %s and Carton Type: %s validated", cartonId, cartonDetail.getContainer().getContainerType()), true);
                Assert.assertEquals(cartonDetail.getContainer().getContainerStatusCode(), "PCK");
                StepDetail.addDetail(String.format("CONT CLOSE: Carton ID: %s and Carton status: %s validated", cartonId, cartonDetail.getContainer().getContainerStatusCode()), true);
                Map<String, List<InventorySnapshot>> cartonInventoryShapshotMap = cartonDetail.getInventorySnapshotList().stream().collect(groupingBy(m -> StringUtils.substringBefore(m.getReferenceContainer(), "-")));

                toteIds.forEach(toteid -> {
                    List<InventorySnapshot> cartonInventorySnapshot = cartonInventoryShapshotMap.get(toteid);
                    InventorySnapshot cartonInventory = cartonInventorySnapshot.get(0);
                    List<InventorySnapshot> toteInventorySnapshot = toteInventoryShapshotMap.get(toteid);
                    InventorySnapshot toteInventory = toteInventorySnapshot.get(0);

                    Assert.assertEquals(StringUtils.substringBefore(cartonInventory.getReferenceContainer(), "-"), toteid);
                    StepDetail.addDetail("CONT CLOSE: Inventory shapshot reference id validated", true);

                    Assert.assertEquals(cartonInventory.getQuantity(), toteInventory.getQuantity());
                    StepDetail.addDetail(String.format("CONT CLOSE: Tote ID : %s and Quantity: %s validated ", toteid, cartonInventory.getQuantity()), true);

                    Assert.assertEquals(cartonInventory.getStatusCode(), "PCK");
                    StepDetail.addDetail(String.format("CONT CLOSE: Tote ID: %s and Inventory status: %s validated", cartonId, cartonInventory.getStatusCode()), true);

                    Assert.assertEquals(cartonInventory.getItem(), toteInventory.getItem());
                    StepDetail.addDetail(String.format("CONT CLOSE: Tote ID : %s Sku UPC: %s validated ", toteid, cartonInventory.getItem()), true);

                    List<List<String>> cartonAttribute = cartonInventory.getAttributeList().stream()
                            .filter(f -> "Department".equals(f.getKey()) || "PID".equals(f.getKey()) || "POLineBarcode".equals(f.getKey()))
                            .sorted(Comparator.comparing(Attribute::getKey))
                            .map(Attribute::getValues)
                            .collect(Collectors.toList());

                    List<List<String>> inventoryAttribute = toteInventory.getAttributeList().stream()
                            .filter(f -> "Department".equals(f.getKey()) || "PID".equals(f.getKey()) || "POLineBarcode".equals(f.getKey()))
                            .sorted(Comparator.comparing(Attribute::getKey))
                            .map(Attribute::getValues)
                            .collect(Collectors.toList());

                    log.info("Carton Attribute List: {}", cartonInventory.getAttributeList());
                    log.info("Tote Attribute List: {}", toteInventory.getAttributeList());

                    Assert.assertEquals(cartonAttribute.toString(), inventoryAttribute.toString());
                    StepDetail.addDetail("CONT CLOSE: Carton and Totes attributes validated", true);


                });


            }
        });
    }


    @When("Totes are diverted to PUT to store and UNITPUT message is sent by Pyramid for moving inventory to carton")
    public void putToStoreSimulation() throws Exception {
        boolean featureFlag = true;
        Map<String, String> cartonConfig = CommonUtils.loadConfig("Carton", "Attributes", "Carton_Dimensions", "Carton", "P2C");
        String prefix = cartonConfig.get("StartsWith");
        Map<String, String> storeLocationCartonIds = new HashMap<>();
        List<CartonDetails> cartonDetailsList = new ArrayList<>(5);
        List<InventoryContainer> inventoryContainerList = new ArrayList<>(5);
        try {
            List<Unitput> unitPutList = getUnitPutMessageDetails((String) stepsContext.get(Context.PO_NBR.name()), (String) stepsContext.get(Context.PO_RCPT_NBR.name()));
            if (!unitPutList.isEmpty()) {
                unitPutList.forEach(unitPut -> {
                    String storeLocNbr = String.valueOf(unitPut.getStoreLocationNbr());
                    String carton;

                    if (storeLocationCartonIds.containsKey(storeLocNbr)) {
                        carton = storeLocationCartonIds.get(storeLocNbr);
                    } else {
                        storeLocationCartonIds.put(storeLocNbr, CommonUtils.getRandomCartonNumber(prefix, "20"));
                        carton = storeLocationCartonIds.get(storeLocNbr);
                    }
                    String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
                    String toteId = unitPut.getContainerId();
                    String quantity = String.valueOf(unitPut.getQuantity());

                    String upc = unitPut.getSkuDetails().stream().map(m -> {
                        return String.format("%s|%s", m.getSku(), m.getQuantity());
                    }).collect(Collectors.joining("|"));


                    InventoryContainer inventoryContainer = CommonUtils.getInventory(toteId);
                    List<ContainerRelation> containerRelations = inventoryContainer.getContainer().getContainerRelationshipList();
                    String parentContainer = null, parentContainerType = null, poNbr = null, poReceiptNbr = null;

                    if (!containerRelations.isEmpty()) {
                        parentContainer = containerRelations.get(0).getParentContainer();
                        parentContainerType = containerRelations.get(0).getParentContainerType();
                    } else {
                        parentContainer = "EOC123T";
                    }

                    for (Attribute attribute : inventoryContainer.getContainer().getAttributeList()) {
                        if ("PO".equals(attribute.getKey())) {
                            poNbr = attribute.getValues().get(0);
                        } else if ("POReceipt".equals(attribute.getKey())) {
                            poReceiptNbr = attribute.getValues().get(0);
                        }
                    }


                    // Construct the UNITPUT Message.
                    if (!ExpectedDataProperties.pyramidJsonproperty) {
                        List<String> messageData = new LinkedList<>();
                        messageData.add(seqId);
                        messageData.add("UNITPUT");
                        messageData.add(toteId);
                        messageData.add(carton);

                        for (SKUDetails detail : unitPut.getSkuDetails()) {
                            messageData.add(detail.getSku().toString());
                            messageData.add(String.valueOf(detail.getQuantity()));
                        }
                        StringBuilder message = new StringBuilder("\u0002");

                        message.append(StringUtils.join(messageData, "|"));
                        message.append(addPipe(messageData.size()));

                        messageData = new LinkedList<>();
                        messageData.add(storeLocNbr);
                        messageData.add(String.valueOf(unitPut.getDeptNbr()));
                        messageData.add(quantity);
                        messageData.add("0");
                        messageData.add(unitPut.getCasePack());
                        messageData.add("P");
                        messageData.add(poNbr);
                        messageData.add(poReceiptNbr);
                        messageData.add(parentContainer);

                        message.append(StringUtils.join(messageData, "|")).append("||\u0003");


                        cartonDetailsList.add(new CartonDetails(seqId, carton, Integer.valueOf(quantity), upc, storeLocNbr, toteId));
                        inventoryContainerList.add(inventoryContainer);


                        log.info("Unit put message: {}", message);
                        CommonUtils.pyramidResponseValidation(message.toString());
                        String trasName = String.format("%s:%s", seqId, UNIT_PUT);
                        CommonUtils.verifyPutToStoreMsgResponse(trasName, UNIT_PUT, toteId);
                    } else {
                        String lineItmRqstParams = "";
                        String finallineItmRqstParams = "";
                        List<String> lstlineItmRqstParams = new ArrayList<>();
                        for (SKUDetails detail : unitPut.getSkuDetails()) {
                            String SkuNbr = detail.getSku().toString();
                            String Skuqty = detail.getQuantity().toString();
                            lineItmRqstParams = "{#skuNbr:" + SkuNbr + ",#skuQty:" + Skuqty + "}";
                            List<String> messageBody = requestUtil.getRequestBody(lineItmRqstParams, "UNITPUTItemList.json");
                            lstlineItmRqstParams.addAll(messageBody);
                        }
                        for (String eachLineItemLst : lstlineItmRqstParams) {
                            finallineItmRqstParams = (finallineItmRqstParams + eachLineItemLst).trim() + ",";
                        }
                        finallineItmRqstParams = "[" + removeEnd(finallineItmRqstParams, ",") + "]";
                        log.info("finallineItmRqstParams: {}", finallineItmRqstParams);
                        String requestParams = "{#sequenceno:D-9,#sourceContainer:" + toteId + ",#targetContainer:" + carton + ",#StrNbr:" + storeLocNbr + ",#DeptNbr:" + String.valueOf(unitPut.getDeptNbr()) + ",#SKUqty:" + quantity + ",#CasePack:" + unitPut.getCasePack() + ",#PONbr:" + poNbr + ",#RcptNbr:" + poReceiptNbr + "}";
                        List<String> messageBody = requestUtil.getRequestBody(requestParams, "UNITPUT.json");
                        log.info("Unitput input JSON payload: {}", messageBody);
                        for (String eachMessageBody : messageBody) {
                            eachMessageBody = eachMessageBody.replace("\"#lineItem\"", finallineItmRqstParams);
                            log.info("Unitput input JSON payload with filled values: {}", eachMessageBody);
                            CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "UNITPUT");
                            JSONObject json = new JSONObject(eachMessageBody);
                            seqId = json.getJSONObject("payload").getString("sequenceNo");
                            cartonDetailsList.add(new CartonDetails(seqId, carton, Integer.valueOf(quantity), upc, storeLocNbr, toteId));
                            inventoryContainerList.add(inventoryContainer);
                            String trasName = UNIT_PUT;
                            CommonUtils.verifyPutToStoreMsgResponse(trasName, UNIT_PUT, toteId);
                        }
                    }
                });

            } else {
                Assert.fail("Unitput message is empty");
            }

        } catch (Exception e) {
            log.error("Error in creating unitput message", e);
            Assert.fail("Error in creating unitput message", e);
        }

        stepsContext.put(Context.CARTON_DETAILS.name(), cartonDetailsList, ToContext.RetentionLevel.SCENARIO);
        dataStorage.getStoredData().put("cartonDetailsList", cartonDetailsList);
        setInventoryContainers(inventoryContainerList);
    }


    private List<Unitput> getUnitPutMessageDetails(String poNbr, String receiptNbr) {
        List<Unitput> finalUnitputList = new ArrayList<>();

        String inventoryResponse = CommonUtils.getRequestResponse(String.format(InventoryEndPoint.GET_INVENTORY_PORECEIPT_PONBR, poNbr, receiptNbr));
        InventoryContainer[] inventoryContainer = CommonUtils.getClientResponse(inventoryResponse, new TypeReference<InventoryContainer[]>() {
        });
        List<InventoryContainer> InventoryContainers = Arrays.asList(inventoryContainer);
        InventoryContainer inventory = InventoryContainers.get(0);

        List<Attribute> containerAttribute = inventory.getContainer().getAttributeList();
        Attribute packAttribute = containerAttribute.stream().filter(f -> "Pack".equalsIgnoreCase(f.getKey())).findAny().orElse(null);

        List<ToteAllocDetails> toteAllocDetails = getToteAllocDetails(poNbr, receiptNbr);

        if (null != packAttribute && packAttribute.getValues().contains("IP")) {
            log.info("inner pack flow");
            Attribute unitPackAttribute = containerAttribute.stream().filter(f -> "UnitsPerPack".equalsIgnoreCase(f.getKey())).findAny().orElse(null);
            final Integer innerSKUQuantity = (null != unitPackAttribute) ? Integer.parseInt(unitPackAttribute.getValues().get(0)) : null;
            log.info("Unit pack quantity: {}", innerSKUQuantity);
            if (null == innerSKUQuantity) {
                Assert.fail("Unit Per Pack Attribute is NULL");
            }

            // Get the no sku count
            long skuCount = toteAllocDetails.stream().filter(distinctByKey(f -> f.getSku())).count();
            log.info("Total Number of sku: {}", skuCount);

            // update the quantity
            Map<String, Long> storeLocationQuantityMap = toteAllocDetails.stream().collect(groupingBy(ToteAllocDetails::getStoreLocNbr, summingLong(ToteAllocDetails::getQuantity)));
            storeLocationQuantityMap.forEach((k, v) -> {
                storeLocationQuantityMap.replace(k, v, (v / skuCount) * innerSKUQuantity);
            });

            log.info("storeLocationQuantityMap: {}", storeLocationQuantityMap);

            // Get all the inventories, which has only NEW and Regular, skip BINBOX
            List<String> inventorySnapshotList = InventoryContainers.stream().filter(f -> {
                long count = f.getContainer().getAttributeList().stream().filter(f1 -> f1.getKey().equalsIgnoreCase("NewStore")).count();
                return count == 1;
            }).map(m -> m.getContainer().getBarCode()).collect(Collectors.toList());


            // Get all the TOTECONT Message based on the TOTEID
            List<GenericMessagePayload> toteContMessages = new ArrayList<>();
            for (String inventorySnapshot : inventorySnapshotList) {
                JSONObject jsonObject = CommonUtils.getMessageResponse("PrepComplete", "TOTECONT", inventorySnapshot);
                if (null != jsonObject) {
                    JSONObject payload = new JSONObject(jsonObject.getString("incomingPayload")).getJSONObject("payload");
                    GenericMessagePayload messagePayload = new GenericMessagePayload();
                    messagePayload.setContainer(payload.getString("container"));
                    List<SKUDetails> skuDetails = new ArrayList<>(3);
                    CommonUtils.getListOfMapsFromJsonArray(payload.getJSONArray("items"))
                            .forEach(m -> {
                                skuDetails.add(new SKUDetails(new BigInteger(m.get("sku")), Integer.valueOf(((Object) m.get("quantity")).toString())));
                            });
                    messagePayload.setItems(skuDetails);
                    messagePayload.setQuantity(payload.getInt("quantity"));
                    messagePayload.setDept(Integer.valueOf(payload.getString("dept")));
                    messagePayload.setNewStore("1".equals(payload.getString("newStore")));
                    messagePayload.setMessageType(payload.getString("messageType"));
                    toteContMessages.add(messagePayload);
                }

            }
            toteContMessages.sort(Comparator.comparing(GenericMessagePayload::getNewStore, Comparator.reverseOrder()));
            log.info("Tote Cont message: {}", toteContMessages);

            //   String casePack = String.format("N%s", StringUtils.leftPad(String.valueOf(innerSKUQuantity), 3, "0"));
            String casePack = String.format("Y%s", StringUtils.leftPad(String.valueOf(innerSKUQuantity), 3, "0"));
            storeLocationQuantityMap.forEach((storeLocNbr, quantity) -> {
                List<Unitput> unitputs = toteSplitInnerPack(toteContMessages, quantity.intValue(), Integer.valueOf(storeLocNbr), casePack);
                finalUnitputList.addAll(unitputs);
            });

        } else {
            log.info("single sku flow");
            // filter it based on new store, groupby sku, storelocationnbr, quantity
            Map<String, Map<String, Integer>> skuNewStoreLocn = toteAllocDetails.stream().filter(f -> NEW.equalsIgnoreCase(f.getStoreType())).collect(groupingBy(ToteAllocDetails::getSku, groupingBy(ToteAllocDetails::getStoreLocNbr, summingInt(ToteAllocDetails::getQuantity))));

            Map<String, Map<String, Integer>> skuRegularStoreLocn = toteAllocDetails.stream().filter(f -> REGULAR.equalsIgnoreCase(f.getStoreType())).collect(groupingBy(ToteAllocDetails::getSku, groupingBy(ToteAllocDetails::getStoreLocNbr, summingInt(ToteAllocDetails::getQuantity))));


            // Get all the inventories, which has only NEW and Regular, skip BINBOX
            List<InventorySnapshot> inventorySnapshotList = InventoryContainers.stream().filter(f -> {
                long count = f.getContainer().getAttributeList().stream().filter(f1 -> f1.getKey().equalsIgnoreCase("NewStore")).count();
                return count == 1;
            }).map(InventoryContainer::getInventorySnapshotList).flatMap(List::stream).collect(Collectors.toList());


            // Get all the TOTECONT Message based on the TOTEID
            List<MessagePayload> toteContMessages = new ArrayList<>();
            for (InventorySnapshot inventorySnapshot : inventorySnapshotList) {
                JSONObject jsonObject = CommonUtils.getMessageResponse("PrepComplete", "TOTECONT", inventorySnapshot.getContainer());
                if (null != jsonObject) {
                    JSONObject payload = new JSONObject(jsonObject.getString("incomingPayload")).getJSONObject("payload");
                    toteContMessages.add(new MessagePayload(payload.getString("container"),
                            inventorySnapshot.getItem(),
                            payload.getInt("quantity"),
                            payload.getString("dept"),
                            payload.getString("newStore"), payload.getString("messageType")));
                }

            }

            // Seperate  message based on NEW and REGULAR Store
            Map<String, List<MessagePayload>> skuNewStoreMessagePayload = toteContMessages.stream().filter(f -> "1".equalsIgnoreCase(f.getNewStore())).collect(groupingBy(MessagePayload::getItems));
            Map<String, List<MessagePayload>> skuStoreMessagePayload = toteContMessages.stream().filter(f -> !"1".equalsIgnoreCase(f.getNewStore())).collect(groupingBy(MessagePayload::getItems));

            // split the Totes based on the New store
            skuNewStoreLocn.forEach((sku, locationQuantityMap) -> {
                List<MessagePayload> toteContmsg = skuNewStoreMessagePayload.get(sku);
                locationQuantityMap.forEach((storeLocNbr, quantity) -> {
                    finalUnitputList.addAll(toteSplit(toteContmsg, quantity, Integer.valueOf(storeLocNbr)));
                });
            });

            // split the Totes based on the Regular store
            skuRegularStoreLocn.forEach((sku, locationQuantityMap) -> {
                List<MessagePayload> toteContmsg = skuStoreMessagePayload.get(sku);
                locationQuantityMap.forEach((storeLocNbr, quantity) -> {
                    finalUnitputList.addAll(toteSplit(toteContmsg, quantity, Integer.valueOf(storeLocNbr)));
                });
            });
        }


        log.info("finalUnitputList: {}", finalUnitputList);

        return finalUnitputList;
    }


    /**
     * Allocate totes based on the StoreLocation
     *
     * @param toteCont
     * @param quantity
     * @param storeLocNbr
     * @return
     */
    private List<Unitput> toteSplit(List<MessagePayload> toteCont, Integer quantity, Integer storeLocNbr) {
        List<Unitput> unitputList = new ArrayList<>();
        for (MessagePayload messagePayload : toteCont) {
            if (messagePayload.getQuantity() == 0) {
                continue;
            }
            List<SKUDetails> skuDetails = new ArrayList<>(2);
            skuDetails.add(new SKUDetails(new BigInteger(messagePayload.getItems()), 1));

            Integer value = messagePayload.getQuantity() - quantity;
            if (value == 0) {
                log.info("container11:{},Quantity:{},Value:{}, quantity:{}", new Object[]{messagePayload.getContainerId(), messagePayload.getQuantity(), value, quantity});
                unitputList.add(new Unitput(messagePayload.getContainerId(), storeLocNbr, messagePayload.getQuantity(), skuDetails, messagePayload.getDept(), "N000"));
                messagePayload.setQuantity(0);
                return unitputList;
            } else if (value < 0) {
                quantity = Math.abs(value);
                log.info("container22:{},Quantity:{},Value:{}, quantity:{}", new Object[]{messagePayload.getContainerId(), messagePayload.getQuantity(), value, quantity});
                unitputList.add(new Unitput(messagePayload.getContainerId(), storeLocNbr, messagePayload.getQuantity(), skuDetails, messagePayload.getDept(), "N000"));
                messagePayload.setQuantity(0);

                continue;
            } else {
                log.info("container33:{},Quantity:{},Value:{}, quantity:{}", new Object[]{messagePayload.getContainerId(), messagePayload.getQuantity(), value, quantity});
                unitputList.add(new Unitput(messagePayload.getContainerId(), storeLocNbr, quantity, skuDetails, messagePayload.getDept(), "N000"));
                messagePayload.setQuantity(value);
                return unitputList;
            }

        }

        return unitputList;


    }

    private List<ToteAllocDetails> getToteAllocDetails(String poNbr, String receiptNbr) {
        List<ToteAllocDetails> toteAllocDetails = new ArrayList<>();
        try {
            String toteAllocSql = String.format(SQLPo4Walls.TOTE_ALLOC_SQL, poNbr, receiptNbr);
            log.info("GET toteAllocSql  query: {}", toteAllocSql);
            //Below commented code is alterante way to get the ToteAlloc detials.. Pls Dont delete
           /* List<Map<Object, Object>> toteAlloc = DBUtils.getValuesFromDBAsList("pofourwalls", toteAllocSql);
            String getCallEndPoint = String.format(PO4WallEndPoint.toteAlloc, poNbr, receiptNbr);
            log.info("get toteAlloc end point withour query parms :", getCallEndPoint);
            Map<String, String> getCallqueryParams = new HashMap<>();
            List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
            Map<String, List<PoLineBarCodeData.PoLinebarCode>> skuPOLineBarCode = poLinebarCode.stream()
                    .collect(Collectors.groupingBy(PoLineBarCodeData.PoLinebarCode::getPoNbr));
            skuPOLineBarCode.forEach((poNbr1, v) -> {
                log.info("poNbr :{}", poNbr);
                Set<String> skuInHouseUPC1 = v.stream().map(m -> {
                    return new String(m.getSKU());
                }).collect(Collectors.toSet());
                Set<String> toteIds = (Set<String>)stepsContext.get(Context.Tote_List.name());
                for (String eachTote : toteIds) {
                    for (String eachSKU : skuInHouseUPC1) {
                        getCallqueryParams.put("skuUPC", eachSKU);
                        getCallqueryParams.put("toteId", eachTote);
                        String getCallEndPoint = String.format(PO4WallEndPoint.toteAlloc, poNbr, receiptNbr,eachSKU,eachTote);
                        String toteAlloc_Details = CommonUtils.getRequestResponse(getCallEndPoint);
                        log.info("ToteAllocDetails service response {}", toteAlloc_Details);
                        if(!toteAlloc_Details.isEmpty()){
                            JSONArray jsonArray = new JSONArray(toteAlloc_Details);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String toteid = jsonArray.getJSONObject(i).getString("toteId");
                                String quantity = String.valueOf(jsonArray.getJSONObject(i).get("quantity"));
                                String locnNbr = String.valueOf(jsonArray.getJSONObject(i).getJSONObject("distributions").get("locnNbr"));
                                String storeType = String.valueOf(jsonArray.getJSONObject(i).getJSONObject("distributions").get("storeType"));
                                String SkuUPC = eachSKU;
                                String SKUBarcode = "";
                                List<PoLineBarCodeData.PoLinebarCode> poLinebarCode1 = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
                                for (PoLineBarCodeData.PoLinebarCode poLine2 : poLinebarCode1) {
                                    if (SkuUPC.equalsIgnoreCase(poLine2.getSKU())) {
                                        SKUBarcode = poLine2.getPoLineBarCode();
                                    }
                                }
                                if (NEW.equalsIgnoreCase(storeType) || REGULAR.equalsIgnoreCase(storeType)) {
                                    toteAllocDetails.add(new ToteAllocDetails(toteid, SkuUPC, locnNbr,
                                            Integer.valueOf(quantity), storeType, SKUBarcode));
                                }
                            }
                        }
                    }
                }
            });*/
            List<Map<?, ?>> toteAlloc = DBMethods.getValuesFromDBAsList(toteAllocSql, "pofourwalls");
            toteAlloc.forEach(data -> {
                if (NEW.equalsIgnoreCase(data.get("STORE_TYPE").toString()) || REGULAR.equalsIgnoreCase(data.get("STORE_TYPE").toString())) {
                    toteAllocDetails.add(new ToteAllocDetails(data.get("TOTE_ID").toString(), data.get("SKU_UPC").toString(), data.get("LOCN_NBR").toString(),
                            Integer.valueOf(data.get("QTY").toString()), data.get("STORE_TYPE").toString(), data.get("BARCODE").toString()));
                }
            });
            toteAllocDetails.sort(Comparator.comparing(ToteAllocDetails::getStoreType));

        } catch (Exception e) {
            log.error("Error in Tote alloc message", e);
            Assert.fail("Error in Tote alloc message", e);
        }
        log.info("toteAllocDetails size :", toteAllocDetails.size());
        return toteAllocDetails;
    }


    private List<Unitput> toteSplitInnerPack(List<GenericMessagePayload> toteCont, Integer quantity, Integer storeLocNbr, String casePack) {
        List<Unitput> unitputList = new ArrayList<>();
        for (GenericMessagePayload messagePayload : toteCont) {
            if (messagePayload.getQuantity() == 0) {
                continue;
            }
            Integer value = messagePayload.getQuantity() - quantity;
            if (value == 0) {
                log.info("container11:{},Quantity:{},Value:{}, quantity:{}", new Object[]{messagePayload.getContainer(), messagePayload.getQuantity(), value, quantity});
                unitputList.add(new Unitput(messagePayload.getContainer(), storeLocNbr, messagePayload.getQuantity(), messagePayload.getItems(), messagePayload.getDept().toString(), casePack));
                messagePayload.setQuantity(0);
                return unitputList;
            } else if (value < 0) {
                quantity = Math.abs(value);
                log.info("container22:{},Quantity:{},Value:{}, quantity:{}", new Object[]{messagePayload.getContainer(), messagePayload.getQuantity(), value, quantity});
                unitputList.add(new Unitput(messagePayload.getContainer(), storeLocNbr, messagePayload.getQuantity(), messagePayload.getItems(), messagePayload.getDept().toString(), casePack));
                messagePayload.setQuantity(0);
                continue;
            } else {
                log.info("container33:{},Quantity:{},Value:{}, quantity:{}", new Object[]{messagePayload.getContainer(), messagePayload.getQuantity(), value, quantity});
                unitputList.add(new Unitput(messagePayload.getContainer(), storeLocNbr, quantity, messagePayload.getItems(), messagePayload.getDept().toString(), casePack));
                messagePayload.setQuantity(value);
                return unitputList;
            }
        }
        return unitputList;
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private String addPipe(int size) {
        return String.format("%" + (35 - size) + "s", " ").replace(" ", "|");
    }

    @When("Totes are diverted to PUT to store and overgae UNITPUT message is sent by Pyramid for moving inventory to carton")
    public void putToStoreUnitPutOverageSimulation() throws Exception {
        Map<String, String> cartonConfig = CommonUtils.loadConfig("Carton", "Attributes", "Carton_Dimensions", "Carton", "P2C");
        String prefix = cartonConfig.get("StartsWith");
        Map<String, String> storeLocationCartonIds = new HashMap<>();
        List<CartonDetails> cartonDetailsList = new ArrayList<>(5);
        List<InventoryContainer> inventoryContainerList = new ArrayList<>(5);
        try {
            boolean unitputOverageflg = true;
            List<Unitput> unitPutList = getUnitPutMessageDetails((String) stepsContext.get(Context.PO_NBR.name()), (String) stepsContext.get(Context.PO_RCPT_NBR.name()));
            String parentContainer = null, poNbr = null, poReceiptNbr = null;
            InventoryContainer inventoryContainer = null;
            if (!unitPutList.isEmpty()) {
                for (Unitput unitPut : unitPutList) {
                    String storeLocNbr = String.valueOf(unitPut.getStoreLocationNbr());
                    String carton;

                    if (storeLocationCartonIds.containsKey(storeLocNbr)) {
                        carton = storeLocationCartonIds.get(storeLocNbr);
                    } else {
                        storeLocationCartonIds.put(storeLocNbr, CommonUtils.getRandomCartonNumber(prefix, "20"));
                        carton = storeLocationCartonIds.get(storeLocNbr);
                    }
                    String seqId = String.valueOf(System.currentTimeMillis()).substring(4, 13);
                    String toteId = unitPut.getContainerId();
                    String quantity = String.valueOf(unitPut.getQuantity());
                    int overageQuantity = Integer.parseInt(quantity) + 2;

                    String upc = unitPut.getSkuDetails().stream().map(m -> {
                        return String.format("%s|%s", m.getSku(), m.getQuantity());
                    }).collect(Collectors.joining("|"));

                    if (unitputOverageflg) {
                        inventoryContainer = CommonUtils.getInventory(toteId);
                        List<ContainerRelation> containerRelations = inventoryContainer.getContainer().getContainerRelationshipList();

                        if (!containerRelations.isEmpty()) {
                            parentContainer = containerRelations.get(0).getParentContainer();
                        } else {
                            parentContainer = "EOC123T";
                        }

                        for (Attribute attribute : inventoryContainer.getContainer().getAttributeList()) {
                            log.info("Attributes of each Container {} :", attribute);
                            if ("PO".equals(attribute.getKey())) {
                                poNbr = attribute.getValues().get(0);
                            } else if ("POReceipt".equals(attribute.getKey())) {
                                poReceiptNbr = attribute.getValues().get(0);
                            }
                        }
                    }
                    unitputOverageflg = false;
                    // Construct the UNITPUT Message.
                    if (!ExpectedDataProperties.pyramidJsonproperty) {
                        List<String> messageData = new LinkedList<>();
                        messageData.add(seqId);
                        messageData.add("UNITPUT");
                        messageData.add(toteId);
                        messageData.add(carton);

                        for (SKUDetails detail : unitPut.getSkuDetails()) {
                            messageData.add(detail.getSku().toString());
                            messageData.add(String.valueOf(detail.getQuantity()));
                        }
                        StringBuilder message = new StringBuilder("\u0002");

                        message.append(StringUtils.join(messageData, "|"));
                        message.append(addPipe(messageData.size()));

                        messageData = new LinkedList<>();
                        messageData.add(storeLocNbr);
                        messageData.add(String.valueOf(unitPut.getDeptNbr()));
                        messageData.add(String.valueOf(overageQuantity));
                        messageData.add("0");
                        messageData.add(unitPut.getCasePack());
                        messageData.add("P");
                        messageData.add(poNbr);
                        messageData.add(poReceiptNbr);
                        messageData.add(parentContainer);

                        message.append(StringUtils.join(messageData, "|")).append("||\u0003");


                        cartonDetailsList.add(new CartonDetails(seqId, carton, Integer.valueOf(quantity), upc, storeLocNbr, toteId));
                        inventoryContainerList.add(inventoryContainer);

                        log.info("cartonDetailsList {} :", cartonDetailsList);
                        log.info("Unit put message: {}", message);
                        CommonUtils.pyramidResponseValidation(message.toString());
                        String trasName = String.format("%s:%s", seqId, UNIT_PUT);
                        CommonUtils.verifyPutToStoreMsgResponse(trasName, UNIT_PUT, toteId);
                    } else {
                        String lineItmRqstParams = "";
                        String finallineItmRqstParams = "";
                        List<String> lstlineItmRqstParams = new ArrayList<>();
                        for (SKUDetails detail : unitPut.getSkuDetails()) {
                            String SkuNbr = detail.getSku().toString();
                            String Skuqty = detail.getQuantity().toString();
                            lineItmRqstParams = "{#skuNbr:" + SkuNbr + ",#skuQty:" + String.valueOf(overageQuantity) + "}";
                            List<String> messageBody = requestUtil.getRequestBody(lineItmRqstParams, "UNITPUTItemList.json");
                            lstlineItmRqstParams.addAll(messageBody);
                        }
                        for (String eachLineItemLst : lstlineItmRqstParams) {
                            finallineItmRqstParams = (finallineItmRqstParams + eachLineItemLst).trim() + ",";
                        }
                        finallineItmRqstParams = "[" + removeEnd(finallineItmRqstParams, ",") + "]";
                        log.info("finallineItmRqstParams: {}", finallineItmRqstParams);
                        String requestParams = "{#sequenceno:D-9,#sourceContainer:" + toteId + ",#targetContainer:" + carton + ",#StrNbr:" + storeLocNbr + ",#DeptNbr:" + String.valueOf(unitPut.getDeptNbr()) + ",#SKUqty:" + quantity + ",#CasePack:" + unitPut.getCasePack() + ",#PONbr:" + poNbr + ",#RcptNbr:" + poReceiptNbr + "}";
                        List<String> messageBody = requestUtil.getRequestBody(requestParams, "UNITPUT.json");
                        log.info("Unitput input JSON payload: {}", messageBody);
                        for (String eachMessageBody : messageBody) {
                            eachMessageBody = eachMessageBody.replace("\"#lineItem\"", finallineItmRqstParams);
                            log.info("Unitput input JSON payload with filled values: {}", eachMessageBody);
                            CommonUtils.pyramidJSONResponseValidation(eachMessageBody, "UNITPUT");
                            JSONObject json = new JSONObject(eachMessageBody);
                            seqId = json.getJSONObject("payload").getString("sequenceNo");
                        }
                        cartonDetailsList.add(new CartonDetails(seqId, carton, Integer.valueOf(quantity), upc, storeLocNbr, toteId));
                        inventoryContainerList.add(inventoryContainer);
                        String trasName = UNIT_PUT;
                        CommonUtils.verifyPutToStoreMsgResponse(trasName, UNIT_PUT, toteId);
                    }
                }

            } else {
                Assert.fail("Unitput message is empty");
            }

        } catch (Exception e) {
            log.error("Error in creating unitput message", e);
            Assert.fail("Error in creating unitput message", e);
        }


        stepsContext.put(Context.CARTON_DETAILS.name(), cartonDetailsList, ToContext.RetentionLevel.SCENARIO);
        setInventoryContainers(inventoryContainerList);
    }


    @Then("Inventory is created for overage quantity outbound cartons and decreased from original totes")
    public void verifyUpdatedCartons() {
        List<CartonDetails> cartonDetails = (List<CartonDetails>)stepsContext.get(Context.CARTON_DETAILS.name());
        Map<String, List<String>> cartonIdToteMap = cartonDetails.stream().collect(groupingBy(CartonDetails::getCartonId, mapping(CartonDetails::getToteId, toList())));
        log.info("cartonIdToteMap : {}", cartonIdToteMap);
        stepsContext.put(Context.CARTON_TOTE_MAP.name(), cartonIdToteMap, ToContext.RetentionLevel.SCENARIO);
        try {
            Thread.sleep(30000);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        cartonIdToteMap.forEach((cartonId, toteIds) -> {
            String cartonResponse = commonUtils.getContainerDetailsbyBarcode(cartonId);
            if (null != cartonResponse) {
                JsonPath cartondetail = new JsonPath(cartonResponse);
                if (CommonUtils.packageFlag) {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as IPK for Carton: Carton ID", cartonId, "IPK".equals(cartondetail.getString("[0].status")));
                } else {
                    CommonUtils.doJbehavereportConsolelogAndAssertion("Shipping: Scan Weigh Status validated as IPK for Carton: Carton ID", cartonId, "IPK".equals(cartondetail.getString("container.containerStatusCode")));
                }
            } else {
                CommonUtils.doJbehavereportConsolelogAndAssertion("Unable to get Details for Carton: Carton ID", cartonId, false);
            }

            toteIds.forEach(toteId -> {
                InventoryContainer toteDetail = CommonUtils.getInventory(toteId);
                log.info("ToteDetail : {}", toteDetail);
                if (null != toteDetail) {
                    log.info("Tote is partially moved. Tote Id: {}", toteId);
                } else {
                    log.info("P2S: Tote [{}] is successfully moved into carton [{}]", toteId, cartonId);
                    StepDetail.addDetail(String.format("P2S: Tote [%s] is successfully moved into carton [%s]", toteId, cartonId), true);
                }

                String toteinventoryAdjustment = CommonUtils.getInventoryAdjHistory(toteId);
                log.info("toteinventoryAdjustment Response : {}", toteinventoryAdjustment);
                if (toteinventoryAdjustment != null) {
                    log.info("toteinventory successfully adjusted");
                } else log.info("Inventory Adjustment History Not Found");
            });
        });
    }

    public void validateFLOContainerDetails() {
        if (CommonUtils.packageFlag) {
            CommonUtils.waitSec(5);
            Map<String, String> cartonIdStoreLocationMap = (Map<String, String>) stepsContext.get(Context.CARTON_STORE_MAP.name());
            cartonIdStoreLocationMap.forEach((cartonId, location) -> {
                String response = commonUtils.getPackageDetailByBarcode(cartonId);
                JsonPath jpath = new JsonPath(response);
                List<?> qtyList = jpath.getList("packageDetails.qty");
                if (null != qtyList && !qtyList.isEmpty()) {
                    List<Integer> qty = (ArrayList<Integer>) qtyList.get(0);
                    Integer totalQty = qty.stream().reduce(0, Integer::sum);
                    log.info("Total Carton Quantity:{}", totalQty);
                    // Retry to find cartonid on-prem DB
                    boolean onpremRecordFound;
                    List<Map<?, ?>> floCntrs;
                    int retryCount = 0;
                    do {
                        floCntrs = getFLOContainer(cartonId);
                        if(floCntrs == null || floCntrs.isEmpty()) {
                            CommonUtils.waitSec(10);
                            log.info("Retry::{}", retryCount);
                            onpremRecordFound = (retryCount++ < 5) ? true : false;
                        } else {
                            onpremRecordFound = false;
                        }

                    }while (onpremRecordFound);

                    if(null != floCntrs && !floCntrs.isEmpty()) {
                        Map<?, ?> floCntr = floCntrs.get(0);
                        log.info("FLO CNTR Details found. CNTR_STAT_NBRr:{}", floCntr.get("CNTR_STAT_NBR"));
                        StepDetail.addDetail("FLO CNTR Details found. Carton Nbr:"+cartonId, true);
                        StepDetail.addDetail("compare Expected qty"+cartonId, totalQty.compareTo(Integer.valueOf(floCntr.get("XPCTD_UNIT_QTY").toString())) == 0);
                        org.junit.Assert.assertTrue("compare Expected qty"+cartonId, totalQty.compareTo(Integer.valueOf(floCntr.get("XPCTD_UNIT_QTY").toString())) == 0);
                        StepDetail.addDetail("compare Actual qty"+cartonId, totalQty.compareTo(Integer.valueOf(floCntr.get("ACTL_UNIT_QTY").toString())) == 0);
                        org.junit.Assert.assertTrue("compare Actual qty"+cartonId, totalQty.compareTo(Integer.valueOf(floCntr.get("ACTL_UNIT_QTY").toString())) == 0);
                    } else {
                        StepDetail.addDetail("FLO CNTR Details not found. Carton Nbr:"+cartonId, false);
                        org.junit.Assert.fail("FLO CNTR Details not found. Carton Nbr:"+cartonId);
                    }
                }
            });
        }
    }

    private List<Map<?, ?>> getFLOContainer(String cntrNbr) {
        try {
            return DBMethods.getValuesFromDBAsList(String.format(SELECT_FLO_CNTR_BY_CNTR, cntrNbr), SCHEMA);
        } catch (Exception e) {
            log.error("unable to retrieve FLO CNTR data.", e);
        }
        return null;
    }
}

