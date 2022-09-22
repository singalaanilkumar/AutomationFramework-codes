package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.model.LocationDistro;
import com.macys.mst.DC2.EndToEnd.model.POLineDetails;
import com.macys.mst.DC2.EndToEnd.model.POLineItems;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.TestNGListener;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.steps.context.StepsContext;
import org.testng.Assert;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.macys.mst.DC2.EndToEnd.db.app.SQLQueriesERS.SELECT_RCPT_LI_BY_RCPT;

@Slf4j
public class CloseReceiptSteps {
    private static final String SCHEMA = "LFCBIZ01";
    private static final String DB_TYPE = "onPremDB";
    private static final String CLOSE_RCPT_STATUS = "30";
    private static final String POSTED_RCPT_STATUS = "20";
    private static StepsContext stepsContext;
    public long TestNGThreadID = Thread.currentThread().getId();

    StepsDataStore dataStorage = StepsDataStore.getInstance();
    CommonUtils commonUtils = new CommonUtils();


    public CloseReceiptSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
        try {
            DBMethods.establishDBConnections(DB_TYPE, SCHEMA);
        } catch (Exception e) {
            log.error("Error connecting to onPremDB", e);
        }
    }

    @BeforeStory
    public void beforeStory() {
        ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
        TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
    }

    @Then("validate CloseReceipt from ERS")
    public void validateCloseReceipt() {
        CommonUtils.waitSec(60);
        String PO = (String) stepsContext.get(Context.PO_NBR.name());
        String rcptNbr = (String) stepsContext.get(Context.PO_RCPT_NBR.name());

        log.info("Close PO:{}, rcptNbr: {}", PO, rcptNbr);
        List<POLineItems> poLineItems = CommonUtils.POOrderDetails(PO);
        List<LocationDistro> locationsDistro = poLineItems.stream().map(POLineItems::getPoLocationDistroList)
                .flatMap(List::stream).collect(Collectors.toList());
        Map<Integer, Map<BigInteger, Integer>> locationSkuQty = locationsDistro.stream()
                .collect(Collectors.groupingBy(LocationDistro::getLocationNbr, Collectors.groupingBy(LocationDistro::getSkuUpcNbr, Collectors.summingInt(LocationDistro::getOrderQty))));


        log.info("locationSkuQty: {}", locationSkuQty);

        boolean onpremRecordFound;
        List<Map<?, ?>> values;
        int retryCount = 0;
        do {
            values = getRcptLineItem(rcptNbr);
            if(values == null || values.isEmpty()) {
                CommonUtils.waitSec(10);
                log.info("Retry::{}", retryCount);
                onpremRecordFound = (retryCount++ < 5) ? true : false;
            } else {
                onpremRecordFound = false;
            }

        }while (onpremRecordFound);



        List<POLineDetails> polineItems = values.stream().map(v -> {
            POLineDetails lineDetail = new POLineDetails();
            lineDetail.setInhouseUpc(v.get("SKU_UPC_NBR").toString());
            lineDetail.setStatus(v.get("RCPT_STATUS").toString());
            if (!(CLOSE_RCPT_STATUS.equals(lineDetail.getStatus()) || POSTED_RCPT_STATUS.equals(lineDetail.getStatus()))) {
                StepDetail.addDetail("Not a Valid Close Receipt status:" + lineDetail.getStatus(), false);
                Assert.fail("Not a Valid Close Receipt status:" + lineDetail.getStatus());
            }
            lineDetail.setExpectedUnits(v.get("SUM_EXP_QTY").toString());
            lineDetail.setActualUnits(v.get("SUM_ACTL_QTY").toString());
            lineDetail.setPid(v.get("INV_LOC_NBR").toString());
            return lineDetail;
        }).collect(Collectors.toList());

        log.info("polineItems: {}", polineItems);

        Map<Integer, Map<BigInteger, Integer>> onPremLocationSkuQty = polineItems.stream().collect(Collectors.groupingBy(id -> new Integer(id.getPid()), Collectors.groupingBy(id -> new BigInteger(id.getInhouseUpc()), Collectors.summingInt(poline -> new Integer(poline.getExpectedUnits())))));
        log.info("onPremLocationSkuQty: {}", onPremLocationSkuQty);

        StepDetail.addDetail("Compare total location count GCP vs ERS", locationSkuQty.size() == onPremLocationSkuQty.size());
        org.junit.Assert.assertTrue("Compare total location count GCP vs ERS", locationSkuQty.size() == onPremLocationSkuQty.size());

        locationSkuQty.forEach((location, skuQtyDetail) -> {
            log.info("location: {}", location);
            if(getNewErsLocationNumbers().containsKey(location)){
                location = getNewErsLocationNumbers().get(location);
                log.info("New ERS location: {}", location);
            }
            StepDetail.addDetail(String.format("%d Location found in ERS", location), onPremLocationSkuQty.containsKey(location));
            org.junit.Assert.assertTrue(String.format("%d Location found in ERS", location), onPremLocationSkuQty.containsKey(location));
            Map<BigInteger, Integer> onPremSkuQty = onPremLocationSkuQty.get(location);
            log.info("skuQtyDetail: {}", skuQtyDetail);
            log.info("onPremSkuQty: {}", onPremSkuQty);
            StepDetail.addDetail("compare total no of sku's GCP vs ERS for that particular location:" + location, skuQtyDetail.size() == onPremSkuQty.size());
            org.junit.Assert.assertTrue("compare total no of sku's GCP vs ERS for that particular location:" + location, skuQtyDetail.size() == onPremSkuQty.size());
            skuQtyDetail.forEach((sku, qty) -> {
                StepDetail.addDetail(String.format("%d sku found in ERS", sku), onPremSkuQty.containsKey(sku));
                org.junit.Assert.assertTrue(String.format("%d sku found in ERS", sku), onPremSkuQty.containsKey(sku));
                StepDetail.addDetail("compare qty GCP vs ERS for that sku:" + sku, qty == onPremSkuQty.get(sku));
                org.junit.Assert.assertTrue("compare qty GCP vs ERS for that sku:" + sku, qty == onPremSkuQty.get(sku));
            });
        });
    }
    public Map<Integer, Integer> getNewErsLocationNumbers(){
        HashMap<Integer, Integer> newERSLocNbrs = new HashMap<Integer, Integer>();
        newERSLocNbrs.put(6322,7273);   newERSLocNbrs.put(6324,7274);
        newERSLocNbrs.put(6325,7275);   newERSLocNbrs.put(6326,7276);
        newERSLocNbrs.put(6329,7277);   newERSLocNbrs.put(6330,7278);

        return newERSLocNbrs;
    }

    private List<Map<?, ?>> getRcptLineItem(String rcptNbr) {
        try {
            return DBMethods.getValuesFromDBAsList(String.format(SELECT_RCPT_LI_BY_RCPT, rcptNbr), SCHEMA);
        } catch (Exception e) {
            log.error("unable to retrieve data.", e);
        }
        return null;
    }

}

