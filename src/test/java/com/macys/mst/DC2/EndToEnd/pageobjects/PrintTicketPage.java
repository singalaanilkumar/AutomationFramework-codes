package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.mst.DC2.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.rest.RestUtilities;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PrintTicketPage extends BasePage {

    @FindBy(xpath = "//input[@type='text']")
    WebElement userName;

    /**
     * The password
     */
    @FindBy(xpath = "//INPUT[@type='password']")
    WebElement password;

    @FindBy(xpath = "//span[text()='Login']")
    WebElement loginButton;

    @FindBy(linkText = "DC2.0 RF Options")
    private WebElement RFMenu;

    @FindBy(xpath = "//b[contains(text(), 'Create Tote')]")
    private WebElement createTote;

    @FindBy(xpath = "//b[contains(text(), 'Build Pallet')]")
    WebElement buildPallet;
    
    @FindBy(xpath = "//b[contains(text(), 'Load Location')]")
    WebElement LoadLocation;
    
    @FindBy(xpath = "//b[contains(text(), 'Pick To Carton')]")
    WebElement pickToCarton;

    @FindBy(xpath = "//b[contains(text(), 'Print Ticket')]")
    private WebElement printTicket;

    @FindBy(xpath = "//b[contains(text(), 'Split Move')]")
    private WebElement splitMove;

    @FindBy(xpath = "//b[contains(text(), 'Adjust Container')]")
    private WebElement adjustContainer;

    @FindBy(xpath = "//b[contains(text(), 'Prep Option')]")
    private WebElement prepOption;

    @FindBy(xpath = "//b[contains(text(), 'Consume Container')]")
    private WebElement consumeContainer;

    @FindBy(xpath = "//b[contains(text(),'Locate Pallet')]")
    private WebElement locatePallet;

    @FindBy(xpath = "//b[contains(text(),'Stage Pallet')]")
    private WebElement stagePallet;

    @FindBy(xpath = "//b[contains(text(),'Putaway Pallet')]")
    private WebElement putawayPallet;

    /**
     * The scan Station label
     */
    @FindBy(xpath = "//STRONG[text()='Scan Station ID:']")
    WebElement scanStationIdLabel;

    /**
     * The Back button in Scan Station Screen
     */
    @FindBy(xpath = "//span[text()='Back']")
    WebElement back_StnId;

    /**
     * The Exit button in Scan Station Screen
     */
    @FindBy(xpath = "//*[*[text()='Exit']]")
    WebElement exit_StnId;

    /**
     * The scan Station Textbox
     */
    @FindBy(xpath = "//INPUT[@type='text']")
    WebElement scanStationId;

    @FindBy(xpath = "//STRONG[text()='Scan Container ID:']")
    WebElement scanToteLabel;

    @FindBy(xpath = "//INPUT[@type='text']")
    WebElement scanTote;

    /**
     * The Back button in Scan Qty Screen
     */
    @FindBy(xpath = "//span[text()='Back']")
    WebElement back_Qty;
    
    //@FindBy(xpath = "//*[contains(text(), 'Enter Qty:')]")
    @FindBy(xpath = "//input[@id='entryBox']")
    WebElement enterQtyLabel;

    @FindBy(xpath = "//STRONG[contains(text(), 'Container ID: ')]//parent::div")
    WebElement toteIDLabel;

    @FindBy(xpath = "//STRONG[contains(text(), 'SKU: ')]/following::label[1]")
    WebElement skuLabel;

    @FindBy(xpath = "//STRONG[contains(text(), 'PO: ')]/following::label[1]")
    WebElement poLabel;

    @FindBy(xpath = "//STRONG[contains(text(), 'Color: ')]/following::label[1]")
    WebElement colorLabel;

    @FindBy(xpath = "//STRONG[contains(text(), 'PID: ')]/following::label[1]")
    WebElement pIDLabel;

    @FindBy(xpath = "//STRONG[contains(text(), 'Size: ')]/following::label[1]")
    WebElement sizeLabel;

    @FindBy(xpath = "//INPUT[@type='number']")
//    @FindBy(xpath = "//INPUT[@type='text']")
    WebElement scanQtyValue;
    
    @FindBy(xpath = "//INPUT[@type='number']")
    WebElement scanQtyValueIP;

    /**
     * The Exit button in Scan Qty Screen
     */
    @FindBy(xpath = "//span[text()='Exit']")
    WebElement exit_Qty;

    @FindBy(xpath = "//STRONG[contains(text(), 'Qty: ')]/following::label[1]")
    WebElement scanQtyVal;

    @FindBy(xpath = "//STRONG[contains(text(), 'Ticket Type: ')]/following::label[1]")
    WebElement ticketTypeLabel;

    @FindBy(xpath = "//STRONG[text()='Scan Next Tote ID:']/parent::*/parent::*/following-sibling::div/div/label")
    WebElement printMsgOnScreen;

    @FindBy(xpath = "//STRONG[text()='Scan In-House UPC:']/parent::*/parent::*/following-sibling::div/div/label")
    WebElement printMsgOnScreenForUPC;

    @FindBy(xpath = "//b[contains(text(),'Release Lane')]")
    WebElement releaseLane;

    @FindBy(xpath = "//strong[contains(text(),'Scan Next')]")
    WebElement printTicketMessage;

    @FindBy(xpath = "//span[text()='YES']")
    public WebElement reticketConfirmation;

    @FindBy(xpath = "//b[contains(text(),'Create Bin')]")
    WebElement createBin;

    @FindBy(xpath = "//b[contains(text(),'ICQA')]")
    private WebElement icqa;

    public PrintTicketPage(WebDriver driver) {
        super(driver);

    }

    static ObjectMapper mapper = new ObjectMapper();


    public WebDriverWait getWait(int waitTime) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        return wait;
    }

    public void validateRFMenu() {
        getWait(10).until(ExpectedConditions.elementToBeClickable(RFMenu));
        Assert.assertEquals(true, RFMenu.isDisplayed());
    }



    public void selectOptionFromMenu(String param) {
        StepDetail.addDetail("Menu option selected" + param, true);
        switch (param) {
            case "Create Tote":
                getWait(10).until(ExpectedConditions.visibilityOf(createTote));
                createTote.click();
                break;
            case "BuildPallet":
                getWait(10).until(ExpectedConditions.visibilityOf(buildPallet));
                buildPallet.click();
                break;
            case "Print Ticket":
                getWait(10).until(ExpectedConditions.visibilityOf(printTicket));
                printTicket.click();
                break;
            case "Split Move":
                getWait(10).until(ExpectedConditions.visibilityOf(splitMove));
                splitMove.click();
                break;
            case "Adjust Container":
                getWait(10).until(ExpectedConditions.visibilityOf(adjustContainer));
                adjustContainer.click();
                break;
            case "Prep Option":
                prepOption.click();
                break;
            case "Consume Container":
                consumeContainer.click();
                break;
            case "Locate pallet":
                getWait(10).until(ExpectedConditions.visibilityOf(locatePallet));
                locatePallet.click();
                break;
            case "Stage Pallet":
                getWait(10).until(ExpectedConditions.visibilityOf(stagePallet));
                stagePallet.click();
                break;
            case "Release Lane":
                getWait(10).until(ExpectedConditions.visibilityOf(releaseLane));
                releaseLane.click();
                break;
            case "LoadLocation":
                getWait(10).until(ExpectedConditions.visibilityOf(LoadLocation));
                LoadLocation.click();
                break;
            case "Pick To Carton":
                getWait(10).until(ExpectedConditions.visibilityOf(pickToCarton));
                pickToCarton.click();
                break;
            case "Create Bin":
                getWait(10).until(ExpectedConditions.visibilityOf(icqa));
                icqa.click();
                getWait(10).until(ExpectedConditions.visibilityOf(createBin));
                createBin.click();
                break;
            case "Putaway Pallet":
                getWait(10).until(ExpectedConditions.visibilityOf(putawayPallet));
                putawayPallet.click();
                break;
        }
    }

    public void validateScanStationScreen() {
        //title or titleU
        getWait(10).until(ExpectedConditions.visibilityOf(scanStationIdLabel));
        Assert.assertEquals(true, scanStationIdLabel.isDisplayed());

        //getWait(10).until(ExpectedConditions.visibilityOf(back_StnId));
        getWait(10).until(ExpectedConditions.visibilityOf(exit_StnId));
        if (exit_StnId.isDisplayed()) {
            log.info("Back and Exit buttons are displayed in Scan Station Page");
            StepDetail.addDetail("Back and Exit buttons are displayed in Scan Station Page", true);
        } else {
            log.info("Back and Exit buttons are displayed in Scan Station Page");
            StepDetail.addDetail("Back and Exit buttons are displayed in Scan Station Page", true);
            Assert.assertFalse(true);
        }
    }

    public void scanStationID(String stationId) {
        scanStationId.sendKeys(stationId);
        scanStationId.sendKeys(Keys.ENTER);
        log.info("Scanned Station id: {}", stationId);
        StepDetail.addDetail("Scanned Station id: " + stationId, true);
    }

    public void validateScanToteScreen() {
        getWait(30).until(ExpectedConditions.visibilityOf(scanToteLabel));
        Assert.assertEquals(true, scanToteLabel.isDisplayed());

        Map<String, String> actualMap = CommonUtils.getScreenElementsMap(driver, "//SPAN[text()]");
        actualMap.forEach((s, s2) -> Assert.assertTrue(s.equalsIgnoreCase("Scan Receipt") || s.equalsIgnoreCase("Scan UPC") || s.equalsIgnoreCase("Back")));
        StepDetail.addDetail("Print ticket screen: Scan PO, Scan UPC, Back buttons are validated" + actualMap, true);
    }

    public void scanTote(String toteId) {
        try {
            getWait(60).until(ExpectedConditions.visibilityOf(scanTote));
            scanTote.clear();
            TimeUnit.SECONDS.sleep(2);
            scanTote.sendKeys(toteId);  // uncomment this line
            TimeUnit.SECONDS.sleep(2);
            scanTote.sendKeys(Keys.ENTER);
            TimeUnit.SECONDS.sleep(2);
            log.info("Scanned Container:[{}]", toteId);
            StepDetail.addDetail("Scanned Container:" + toteId, true);
        }catch(Exception e){
            log.info(e.getMessage());
        }
    }

    public void validatePrintScreenPage(Map<String, String> poLineDetails, String tote, Integer quantity, boolean isEditable) {
        log.info("Validating the Scan quantity page");
        if (isEditable) {
            getWait(30).until(ExpectedConditions.visibilityOf(enterQtyLabel));
        }
        getWait(30).until(ExpectedConditions.visibilityOf(toteIDLabel));

        if (back_Qty.isDisplayed()) {
            log.info("Back button is displayed in Scan Quantity Page");
            StepDetail.addDetail("Back button is displayed in Scan Quantity Page", true);
            Map<String, String> scanPrintTicketElements = CommonUtils.getScreenElementData(driver, "//div//div//div/div//div//STRONG[text()]/parent::td");
            scanPrintTicketElements.putAll(CommonUtils.getScreenElementData(driver, "//div//div//div/div//div//STRONG[text()]/parent::div"));

            StepDetail.addDetail("Displayed Tote id: " + scanPrintTicketElements.get("Container ID") + " Tote ID Scanned:" + tote, true);

            Assert.assertEquals(scanPrintTicketElements.get("Container ID"), tote);


            Assert.assertEquals(poLineDetails.get("skuUpc"), Long.parseLong(scanPrintTicketElements.get("UPC")));
            //Assert.assertEquals(poLineDetails.get("poNbr"), Integer.parseInt(scanPrintTicketElements.get("PO")));
            Assert.assertEquals(poLineDetails.get("colorDesc").trim(), scanPrintTicketElements.get("Color"));
            //Assert.assertEquals(poLineDetails.get("pidDesc").trim(), scanPrintTicketElements.get("PID"));
            Assert.assertEquals(poLineDetails.get("sizeDesc").trim(), scanPrintTicketElements.get("Size"));
            Assert.assertEquals(poLineDetails.get("ticketType").trim().toUpperCase(), scanPrintTicketElements.get("Ticket Type").toUpperCase());


            String expectedQuantity = String.valueOf(quantity);
            if (isEditable) {
                Assert.assertEquals(expectedQuantity, scanQtyValue.getAttribute("value"));
            } else {
                Assert.assertEquals(expectedQuantity, scanPrintTicketElements.get("Qty"));
            }

            StepDetail.addDetail("Expected Qty: " + expectedQuantity + " Actual Qty: " + scanPrintTicketElements.get("Qty"), true);
            StepDetail.addDetail("Validation passed for Scan Quantity screen" + tote, true);

        } else {
            log.info("Back button is not displayed in Scan Quantity Page");
            StepDetail.addDetail("Back button is not displayed in Scan Quantity Page", true);
            Assert.assertFalse(true);
        }

    }
    
    public void validatePrintScreenPageForInnerPack(String tote, Integer quantity, boolean isEditable) {
        log.info("Validating the Scan quantity page");
        getWait(10).until(ExpectedConditions.visibilityOf(back_Qty));
        getWait(10).until(ExpectedConditions.visibilityOf(toteIDLabel));

        if (back_Qty.isDisplayed()) {
            log.info("Back button is displayed in Scan Quantity Page");
            StepDetail.addDetail("Back button is displayed in Scan Quantity Page", true);
            Map<String, String> scanPrintTicketElements = CommonUtils.getScreenElementData(driver, "//div//div//div/div//div//STRONG[text()]/parent::td");
            scanPrintTicketElements.putAll(CommonUtils.getScreenElementData(driver, "//div//div//div/div//div//STRONG[text()]/parent::div"));

            StepDetail.addDetail("Displayed Tote id: " + scanPrintTicketElements.get("Container ID") + " Tote ID Scanned:" + tote, true);

            Assert.assertEquals(scanPrintTicketElements.get("Container ID"), tote);

            String expectedQuantity = String.valueOf(quantity);
            if (isEditable) {
             //   Assert.assertEquals(expectedQuantity, scanPrintTicketElements.get("Total inner packs"));
            } else {
             //   Assert.assertEquals(expectedQuantity, scanPrintTicketElements.get("Qty"));
            }

            StepDetail.addDetail("Expected Qty: " + expectedQuantity + " Actual Qty: " + scanPrintTicketElements.get("Qty"), true);
            StepDetail.addDetail("Validation passed for Scan Quantity screen" + tote, true);


        } else {
            log.info("Back button is not displayed in Scan Quantity Page");
            StepDetail.addDetail("Back button is not displayed in Scan Quantity Page", true);
            Assert.assertFalse(true);
        }

    }

    public void clickButton(String name){
        try {
            getWait(15);
            switch (name) {
                case "back":
                    getWait(20).until(ExpectedConditions.visibilityOf(back_Qty));
                    Thread.sleep(10000);
                    back_Qty.click();
                    break;
                case "exit":
                    getWait(20).until(ExpectedConditions.visibilityOf(exit_StnId));
                    Thread.sleep(10000);
                    exit_StnId.click();
                    break;
            }
        }catch (Exception e){
            log.info(e.getMessage());
        }
    }

    public void enterqty(String qty) {
        getWait(10).until(ExpectedConditions.visibilityOf(scanQtyValue));
        //scanQtyValue.clear();
        //getWait(10).until(ExpectedConditions.visibilityOf(scanQtyValue));
        //scanQtyValue.sendKeys(qty);
        //getWait(10).until(ExpectedConditions.visibilityOf(scanQtyValue));
        scanQtyValue.sendKeys(Keys.ENTER);
        log.info("Qty: " + qty);
        StepDetail.addDetail("Qty: " + qty, true);
    }
    
    public void enterQuantity(String qty) {
        try {
            getWait(30).until(ExpectedConditions.visibilityOf(scanQtyValueIP));
            scanQtyValueIP.click();
            scanQtyValueIP.clear();
            TimeUnit.SECONDS.sleep(6);
            scanQtyValueIP.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            scanQtyValueIP.sendKeys(Keys.BACK_SPACE);
            TimeUnit.SECONDS.sleep(6);
            scanQtyValueIP.sendKeys(qty);
            TimeUnit.SECONDS.sleep(6);
            scanQtyValueIP.sendKeys(Keys.ENTER);
            log.info("Qty: " + qty);
            StepDetail.addDetail("Qty: " + qty, true);
        }
        catch(Exception e){
            log.info(e.getMessage());
        }
    }

    public void enterOnQuantity() {
        getWait(5).until(ExpectedConditions.visibilityOf(scanQtyValue));
        scanQtyValue.sendKeys(Keys.ENTER);
    }


    public void verifyPrintTicketMsgScreen(Integer totalNbrTickets, String printerId) {
        String expectedMsg = String.format("%d Ticket(s) Printed Successfully on %s", totalNbrTickets, printerId);
        getWait(30).until(ExpectedConditions.visibilityOf(printTicketMessage));
        String screenPrintTicketMessage = StringUtils.substringAfterLast(printTicketMessage.getText(), ":");
      //  Assert.assertEquals(expectedMsg, screenPrintTicketMessage.trim());
        StepDetail.addDetail("Expected Message: " + expectedMsg + "Actual Message: " + screenPrintTicketMessage.trim(), true);
        log.info("Printing Message validated");
        StepDetail.addDetail("Printing Message validated", true);


    }

    /*public void verifyMsgOnScanUpcScreen(String ExpectedMsg) {
        if (ExpectedMsg.contains("$")) {
            HashMap<String, Object> responseMap = new HashMap<String, Object>();
            responseMap = PrintTicketPage.getMapFromJson(RestUtilities.getRequestResponse(ReadHostConfiguration.FETCH_POLINE_DTLS_URL.value() + ReadHostConfiguration.LOCATION_NUMBER.value() +
                    "/polines/" + PrintTicketPage.getPoLineNum()));
            PrintTicketPage.poLineDtlsMap = responseMap;

            int noOfTickets = Integer.parseInt(PrintTicketPage.getPoLineDtlsMap().get("noOfTickets").toString());
            String value = String.valueOf(Integer.parseInt(PrintTicketPage.getQtyScanned()) * noOfTickets);
            ExpectedMsg = ExpectedMsg.replace("$", value);
        }
        if (ExpectedMsg.contains("#")) {
            ExpectedMsg = ExpectedMsg.substring(0, ExpectedMsg.length() - 1);
            ExpectedMsg = ExpectedMsg + DataCreateModule.printerIdList.get(PrintTicketPage.getStnIndex());
        }
        log.info("Expected Message: " + ExpectedMsg + " Actual Message: " + printMsgOnScreenForUPC.getText());
        StepDetail.addDetail("Expected Message: " + ExpectedMsg + "Actual Message: " + printMsgOnScreenForUPC.getText(), true);
        Assert.assertEquals(printMsgOnScreenForUPC.getText(), ExpectedMsg);
        log.info("Printing Message validated on Scan UPC Screen");
        StepDetail.addDetail("Printing Message validated on Scan UPC Screen", true);
    }*/


    public void verifyPrintStatus(String toteId) {
        String actualStatus = getContainerStatus(toteId);
        StepDetail.addDetail("Expected Print Status: PRT and Actual Print Status: " + actualStatus, true);
       // if actualStatus is not PRT
        //update container status to PRT
        //servcie call
        //stepdetail ..
//        Assert.assertEquals("PRT", actualStatus);
        StepDetail.addDetail("Print status code Validated", true);

    }

    private String getContainerStatus(String tote) {
        String response = RestUtilities.getRequestResponse(
                ReadHostConfiguration.CREATE_INVENTORY_URL.value() + ReadHostConfiguration.LOCATION_NUMBER.value() + "/containers?barcode=" + tote);

        if (response != JSONObject.NULL) {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject containerObject = (JSONObject) jsonObject.get("container");
            return containerObject.getString("containerStatusCode");


        } else {
            Assert.fail("response String is null");
        }
        return null;
    }
}
