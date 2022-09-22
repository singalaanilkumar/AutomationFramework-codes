package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.CreateBinPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.ToContext;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CreateBinSteps {
    private StepsContext stepsContext;
    public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    CreateBinPage createBinPage = PageFactory.initElements(driver, CreateBinPage.class);
    PrintTicketSteps printTicketSteps = new PrintTicketSteps(stepsContext);

    public CreateBinSteps(StepsContext stepsContext) {
        this.stepsContext = stepsContext;
    }

    @When("User creates bins with ICQA CreateBin Transaction having $reason ReasonCode$values")
    public void createBinUsingICQA(String reason,ExamplesTable values) throws Exception {
        createBinPage.navigateToCreateBin();
        List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
        //String receipt = stepsContext.get(Context.PO_RCPT_NBR.name()).toString();
        Map<String, List<PoLineBarCodeData.PoLinebarCode>> skuPOLineBarCode = poLinebarCode.stream()
                .collect(Collectors.groupingBy(PoLineBarCodeData.PoLinebarCode::getPoNbr));

        String poNbr = null;
        String rcptNbr = null;
        String upc = null;

        for (Map.Entry<String, List<PoLineBarCodeData.PoLinebarCode>> entry : skuPOLineBarCode.entrySet()) {
            poNbr = entry.getKey();
            List<PoLineBarCodeData.PoLinebarCode> v = entry.getValue();
            log.info("poNbr :{}", poNbr);
            rcptNbr = v.get(0).getReceiptNbr();
            log.info("rcptNbr :{}", rcptNbr);
            Set<BigInteger> skuInHouseUPC = v.stream().map(m -> {
                return new BigInteger(m.getSKU());
            }).collect(Collectors.toSet());
            log.info("In House SKU UPC :{}", skuInHouseUPC);
            upc = skuInHouseUPC.toArray()[0].toString();
            //poLineBarcode = v.get(0).getPoLineBarCode();
        }

        createBinPage.scanPoNbr(poNbr);
        createBinPage.scanRcptNbr(rcptNbr);
        createBinPage.selectReasonCode(reason);


        Table<String, String, Integer> binMapExpected = HashBasedTable.create();
        Map<String, Map<String, String>> binValues = new HashMap<>();
        Map<String, String> binData = new HashMap<>();
        List<String> binboxList = new ArrayList<>();
        List<String> caseBarcodeList = new ArrayList<>();
        if (values.getRows().size() >= 0) {
            for (int i = 0; i < values.getRows().size(); i++) {
                String binBox = createBinPage.createBinId();
                String skuUpc = values.getRow(i).get("SKU");
                int quantity = Integer.valueOf(values.getRow(i).get("Quantity"));
                binMapExpected.put(binBox,skuUpc, quantity);
                binData.put(Integer.toString(i),binBox);
                binboxList.add(binBox);
                caseBarcodeList.add(binBox);

                log.info("BinBox Id : {}" ,binBox);
                createBinPage.scanBin(binBox);
                createBinPage.scanUPC(skuUpc);
                createBinPage.scanQty(Integer.toString(quantity));
            }
        }
        binValues.put("BIN", binData);
        log.info("final BIN Info: {}", binValues);
        stepsContext.put(Context.BIN_ID_MAP.name(), binValues, ToContext.RetentionLevel.SCENARIO);
        dataStorage.getStoredData().put("ExpectedBinBoxMap", binMapExpected);
        dataStorage.getStoredData().put("BinBoxMap", binboxList);
        dataStorage.getStoredData().put("caseBarcodeList", caseBarcodeList);
        createBinPage.clickExit();
    }

    @Then("$msgType messages are validated for all the created bins sent to Pyramid for $transactionType Transaction")
    public void validateCONTROUTESentToPyramidForCreateBin(String msgType, String transactionType){
       Table<String, String, Integer> binMapExpected = (Table<String, String, Integer>) dataStorage.getStoredData().get("ExpectedBinBoxMap");
        for (Table.Cell<String, String, Integer> cell : binMapExpected.cellSet()) {
            log.info("BinBox :", cell.getRowKey());
            CommonUtils.verifyMsgServiceResponse(transactionType, msgType, cell.getRowKey());
            StepDetail.addDetail("Validated CONTROUTE for BinBox:"+cell.getRowKey() , true);
            //printTicketSteps.validateMHEmessageFromDB("CONTROUTE", binBox);
        }
    }



}
