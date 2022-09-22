package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.datasetup.DataCreateModule;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CreateBinPage extends BasePage{
    private static final String SELECT_CREATE_TOTE = "Create Bin";
    CommonUtils commonUtils = new CommonUtils();
    public CreateBinPage(WebDriver driver) {
        super(driver);
    }

    PrintTicketPage printTicketPage = PageFactory.initElements(driver, PrintTicketPage.class);
    CreateTotePage createTotePage = PageFactory.initElements(driver, CreateTotePage.class);

    @FindBy(xpath = "//h5")
    WebElement pageTitle;

    @FindBy(xpath = "//label")
    WebElement label;

    @FindBy(xpath = "//input")
    WebElement textBox;

    @FindBy(xpath = "//div[@role='alert']")
    WebElement alertLabel;

    @FindBy(xpath = "//*[@class='mui']//*[@role='listbox']")
    WebElement listBox;

    @FindBy(xpath = "//button[@aria-label='back']")
    WebElement backButton;

    @FindBy(xpath = "//button[@aria-label='exit']")
    WebElement exitButton;

    public void validateCreateBinScanScreen(String transaction) {
        getWait(10).until(ExpectedConditions.visibilityOf(pageTitle));
        if (pageTitle.isDisplayed()) {
            Assert.assertEquals("CREATE BIN\nScan "+transaction, pageTitle.getText());
            StepDetail.addDetail("CREATE BIN  Scan "+transaction+" is displayed", true);
        } else {
            Assert.fail("Scan "+transaction+" Page Title not found");
        }
        Assert.assertTrue(label.isDisplayed());
        if(transaction.equalsIgnoreCase("PO") ||transaction.equalsIgnoreCase("Receipt") ){
            Assert.assertEquals("Scan "+transaction+" Number", label.getText());
        }else{
            Assert.assertEquals("Scan "+transaction, label.getText());
        }

        Assert.assertTrue(textBox.isDisplayed());
        Assert.assertTrue(backButton.isDisplayed());
        Assert.assertTrue(exitButton.isDisplayed());

        StepDetail.addDetail("Successfully validated "+transaction+" scan screen", true);
    }

    public void validateSelectReasonScreen() {
        getWait(10).until(ExpectedConditions.visibilityOf(pageTitle));
        if (pageTitle.isDisplayed()) {
            Assert.assertEquals("CREATE BIN\nSelect Reason", pageTitle.getText());
            StepDetail.addDetail("CREATE BIN - Select Reason is displayed", true);
        } else {
            Assert.fail("Select Reason Page Title not found");
        }

        Assert.assertTrue(alertLabel.isDisplayed());
        Assert.assertEquals("Reason Code", alertLabel.getText());

        Assert.assertTrue(listBox.isDisplayed());
        Assert.assertTrue(backButton.isDisplayed());
        Assert.assertTrue(exitButton.isDisplayed());

        StepDetail.addDetail("Successfully validated Select ReasonCode screen", true);
    }

    public void navigateToCreateBin() throws Exception {
        createTotePage.navigateToCreateTote();
        getWait(30);
        printTicketPage.selectOptionFromMenu("Create Bin");
        validateCreateBinScanScreen("PO");
    }
    public void backToMainMenu(){
        printTicketPage.clickButton("back");
    }

    public void scanPoNbr(String poNbr) throws InterruptedException {
        while(!textBox.isEnabled()){
            wait(5);
        }
        textBox.sendKeys(poNbr);
        textBox.sendKeys(Keys.ENTER);
        log.info("Scanned poNumber: {}", poNbr);
        StepDetail.addDetail("Scanned poNumber: " + poNbr, true);
        validateCreateBinScanScreen("Receipt");
        StepDetail.addDetail("Validated scan rcptNumber page after poNbr scan", true);
    }

    public void scanRcptNbr(String rcptNbr) throws InterruptedException {
        while(!textBox.isEnabled()){
            wait(5);
        }
        textBox.sendKeys(rcptNbr);
        textBox.sendKeys(Keys.ENTER);
        log.info("Scanned rcptNumber: {}", rcptNbr);
        StepDetail.addDetail("Scanned rcptNumber: " + rcptNbr, true);
        validateSelectReasonScreen();
        StepDetail.addDetail("Validated select reasonCode page after rcptNbr scan", true);
    }

    public void selectReasonCode(String reason) throws InterruptedException {
        while(!listBox.isEnabled()){
            wait(5);
        }
        listBox.click();
        List<WebElement> options = listBox.findElements(By.xpath("//*[@role='option']"));
        WebElement toBeSelectedOption = null;
        for (WebElement option : options) {
            System.out.println(option.getText());
            if(option.getText().equalsIgnoreCase(reason)){
                toBeSelectedOption = option;
            }
        }
        toBeSelectedOption.click();
        StepDetail.addDetail("Selected ReasonCode: "+reason, true);
        validateCreateBinScanScreen("Bin");
        StepDetail.addDetail("Validated Bin Scan after selecting ReasonCode", true);
    }

    public void scanBin(String bin) throws InterruptedException {
        while(!textBox.isEnabled()){
            wait(5);
        }
        textBox.sendKeys(bin);
        textBox.sendKeys(Keys.ENTER);
        log.info("Scanned Bin: {}", bin);
        StepDetail.addDetail("Scanned bin: " + bin, true);
        validateCreateBinScanScreen("UPC");
        StepDetail.addDetail("Validated Scan UPC page after Bin Scan", true);
    }

    public String createBinId() throws Exception {
        return new DataCreateModule().generateContainer("empty", "binbox");
    }

    public void scanUPC(String upc) throws InterruptedException {
        while(!textBox.isEnabled()){
            wait(5);
        }
        textBox.sendKeys(upc);
        textBox.sendKeys(Keys.ENTER);
        log.info("Scanned UPC: {}", upc);
        StepDetail.addDetail("Scanned UPC: " + upc, true);
        validateCreateBinScanScreen("Qty");
        StepDetail.addDetail("Validated Scan Qty page after UPC Scan", true);
    }

    public void scanQty(String qty) throws InterruptedException {
        textBox.sendKeys(qty);
        textBox.sendKeys(Keys.ENTER);
        log.info("Scanned Qty: {}", qty);
        StepDetail.addDetail("Scanned Qty: " + qty, true);
        TimeUnit.SECONDS.sleep(10);
        validateCreateBinScanScreen("Bin");
        StepDetail.addDetail("Validated Scan Bin page after Qty Scan", true);
    }

    public void clickExit() throws InterruptedException {
        exitButton.click();
        TimeUnit.SECONDS.sleep(10);
        backToMainMenu();
    }

    public Response updateCloseStatusForReceipt(String receipt) {
        String endPoint = commonUtils.getUrl("").replace("#rcptNbr", receipt);
        return WhmRestCoreAutomationUtils.putRequestResponse(endPoint, "{}").asResponse();
    }
}
