package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.model.CartonDetails;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import io.restassured.path.json.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.ToContext;
import org.jbehave.core.steps.context.StepsContext;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class DockScanPage extends BasePage {

    @FindBy(xpath = "//a[1]")
    public WebElement lastScan;

    @FindBy(xpath = "//a[2]")
    public WebElement tie;

    @FindBy(xpath = "//h5")
    WebElement pageTitle;

    @FindBy(xpath = "//label")
    WebElement label;

    @FindBy(xpath = "//input")
    WebElement textBox;

    @FindBy(xpath = "//button[@aria-label='back']")
    WebElement backButton;

    @FindBy(xpath = "//button[@aria-label='exit']")
    WebElement exitButton;

    @FindBy(linkText = "Untie")
    public WebElement unTieLink;

    @FindBy(xpath = "//span[@class='MuiIconButton-label']")
    WebElement inlineMsgExitButton;

    @FindBy(xpath = "//span[text()='Exit']")
    WebElement logOffExitButton;

    @FindBy(xpath = "//*[@id='Door Number']")
    WebElement doorNumber;

    @FindBy(xpath = "//*[@id='Appointment']")
    WebElement apptNumber;

    @FindBy(xpath = "//*[@id='Appointment Status']")
    WebElement apptStatus;

    @FindBy(xpath = "//*[@id='Destination Locn']")
    WebElement destinationLocn;

    @FindBy(linkText = "Load Inquiry")
    public WebElement loadInquiry;

    public static final String DOOR_Number = "doorNumbers";
    public static final String DOOR_SHIPVIA_MAP = "doorShipviaMap";
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    private static StepsContext stepsContext;

    public DockScanPage(WebDriver driver) {
        super(driver);
    }

    CommonUtils commonUtils = new CommonUtils();

    public void validateDoorScanScreen() {
        getWait(10).until(ExpectedConditions.visibilityOf(pageTitle));
        if (pageTitle.isDisplayed()) {
            Assert.assertEquals("DOCK OPTIONS\nDoor Scan", pageTitle.getText());
            StepDetail.addDetail("DOCK OPTIONS  Door Scan is displayed", true);
        } else {
            Assert.fail("Dock scan Page Title not found");
        }
        Assert.assertTrue(label.isDisplayed());
        Assert.assertEquals("Scan Door Number", label.getText());

        Assert.assertTrue(textBox.isDisplayed());

        Assert.assertTrue(exitButton.isDisplayed());

        StepDetail.addDetail("successfully validated Door scan screen", true);
    }

    public void scanDoorNumber(String doorNumber) {
        textBox.sendKeys(doorNumber);
        textBox.sendKeys(Keys.ENTER);
        log.info("Scanned doorNumber: {}", doorNumber);
        StepDetail.addDetail("Scanned doorNumber: " + doorNumber, true);
        dataStorage.getStoredData().put("doorNumbers", doorNumber);
    }

    public void enterCartonNumber(String cartonNumber) {
        textBox.sendKeys(cartonNumber);
        textBox.sendKeys(Keys.ENTER);
        log.info("Scanned Carton Number: {}", cartonNumber);
        StepDetail.addDetail("Scanned Carton Number: " + cartonNumber, true);
    }

    public void validateLoadInquiryDetails(String details, List<String> cartonDetails) {
        getWait(10).until(ExpectedConditions.visibilityOf(pageTitle));
        if (pageTitle.isDisplayed()) {
            Assert.assertEquals("LOAD INQUIRY\nDetails", pageTitle.getText());
            StepDetail.addDetail("LOAD INQUIRY Details is displayed", true);
        } else {
            Assert.fail("LOAD INQUIRY Details Page Title not found");
        }
        if (details.equalsIgnoreCase("door")) {

            String doorDetailResponse = CommonUtils.getRequestResponse(String.format(commonUtils.getUrl("LoadInquiry.getDoorDetails"), (String) dataStorage.getStoredData().get("doorNumbers")));
            log.info("Response :- " + doorDetailResponse);
            JsonPath doorDetails = new JsonPath(doorDetailResponse);
            Assert.assertEquals(doorDetails.getString("[0].doorNbr"), doorNumber.getText().split(":")[1].trim());
            Assert.assertEquals(doorDetails.getString("[0].apptNbr"), apptNumber.getText().split(":")[1].trim());
            Assert.assertEquals(doorDetails.getString("[0].apptStatDesc"), apptStatus.getText().split(":")[1].trim());
            Assert.assertEquals(doorDetails.getString("[0].destinationLocnName"), destinationLocn.getText().split(":")[1].trim());
        }
        if (details.equalsIgnoreCase(("carton"))) {
            for (int i = 0; i < cartonDetails.size(); i++) {
                enterCartonNumber(cartonDetails.get(i));
                String cartonDetailResponse = CommonUtils.getRequestResponse(String.format(commonUtils.getUrl("LoadInquiry.getCartonDetails"), cartonDetails.get(i)));
                log.info("Response :- " + cartonDetailResponse);
                JsonPath doorDetails = new JsonPath(cartonDetailResponse);
                Assert.assertEquals(doorDetails.getString("doorNbr"), doorNumber.getText().split(":")[1].trim());
                Assert.assertEquals(doorDetails.getString("apptNbr"), apptNumber.getText().split(":")[1].trim());
                Assert.assertEquals(doorDetails.getString("apptStatDesc"), apptStatus.getText().split(":")[1].trim());
                Assert.assertEquals(doorDetails.getString("destinationLocnName"), destinationLocn.getText().split(":")[1].trim());
                exitButton.click();
                loadInquiry.click();
                getWait().until(ExpectedConditions.urlContains("loadInquiry"));
            }
        }

    }


    public void validateDockOptionScreen() {
        getWait(10).until(ExpectedConditions.visibilityOf(pageTitle));
        getWait(10).until(ExpectedConditions.visibilityOf(lastScan));
        if (pageTitle.isDisplayed()) {
            Assert.assertEquals("DOCK OPTIONS\nDock Options", pageTitle.getText());
            StepDetail.addDetail("DOCK OPTIONS  Dock Options is displayed", true);
        } else {
            Assert.fail("Dock Options Page Title not found");
        }

        Assert.assertTrue(lastScan.isDisplayed());
        Assert.assertTrue(tie.isDisplayed());

        Assert.assertTrue(backButton.isDisplayed());
        Assert.assertTrue(exitButton.isDisplayed());

        StepDetail.addDetail("successfully validated Door Options screen", true);

    }

    public void selectOption(String option) throws InterruptedException {

        TimeUnit.SECONDS.sleep(2);
        log.info("Dock option:{}", option);

        switch (option.toLowerCase()) {
            case "lastscan":
                lastScan.click();
                getWait().until(ExpectedConditions.urlContains("/lastScan"));
                break;
            case "tie":
                tie.click();
                break;
            case "untie":
                unTieLink.click();
                break;
            case "back":
                backButton.click();
                break;
            case "exit":
                exitButton.click();
                break;
            case "logoff":
                logOffExitButton.click();
                break;
            default:
                CommonUtils.doJbehavereportConsolelogAndAssertion("Invalid Dock Option", option, false);
                break;
        }
    }

    public void validateSelectedDockOptionScreen(String option, String doorNbr, Integer appoinmentNbr) {
        log.info("Selected door:{} and appointment:{}", doorNbr, appoinmentNbr);
        getWait(10).until(ExpectedConditions.visibilityOf(pageTitle));
        if (pageTitle.isDisplayed()) {
            if ("lastscan".equalsIgnoreCase(option)) {
                Assert.assertEquals("DOCK OPTIONS\nLast Scan", pageTitle.getText());
                StepDetail.addDetail("DOCK OPTIONS  Last Scan is displayed", true);
            } else if ("tie".equalsIgnoreCase(option)) {
                Assert.assertEquals("DOCK OPTIONS\nTie", pageTitle.getText());
                StepDetail.addDetail("DOCK OPTIONS Tie is displayed", true);
            } else if ("untie".equalsIgnoreCase(option)) {
                Assert.assertEquals("DOCK OPTIONS\nUntie", pageTitle.getText());
                StepDetail.addDetail("DOCK OPTIONS Untie is displayed", true);
            } else {
                Assert.fail(String.format("Dock Options %s Page Title not found", option));
            }
        }
        Map<String, String> screenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div//div");
        if (!"untie".equalsIgnoreCase(option)) {
            screenElements.containsKey("Appointment");
            Assert.assertTrue("Appointment Label found", screenElements.containsKey("Appointment"));
            Assert.assertEquals(String.valueOf(appoinmentNbr), screenElements.get("Appointment"));

            Assert.assertTrue("Door Number Label found", screenElements.containsKey("Door Number"));
            Assert.assertEquals(doorNbr, screenElements.get("Door Number"));
        }
        Assert.assertTrue("Scan Carton Label found", screenElements.containsKey("Scan Carton"));
        Assert.assertTrue("Scan Carton textbox found", textBox.isDisplayed());

        Assert.assertTrue(backButton.isDisplayed());
        Assert.assertTrue(exitButton.isDisplayed());
        StepDetail.addDetail("Successfully validated Dock option Screen:" + option, true);
    }

    public void scanCartonNumber(String cartonNumber) {
        textBox.sendKeys(cartonNumber);
        textBox.sendKeys(Keys.ENTER);
        log.info("Scanned cartonNumber: {}", cartonNumber);
        StepDetail.addDetail("Scanned cartonNumber: " + cartonNumber, true);
    }

    public void validateWarningPopup(String expMessage, String yesNo) {
        FluentWait<WebDriver> wait = getWait(5).withTimeout(Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[text()='" + expMessage + "']")));
        WebElement element = driver.findElement(By.xpath("//h5[text()='" + expMessage + "']"));
        WebElement yesElement = driver.findElement(By.xpath("//span[contains(text(),'Yes')]"));
        WebElement noElement = driver.findElement(By.xpath("//span[contains(text(),'No')]"));
        String message = element.getText();
        Assert.assertEquals(true, element.isDisplayed());
        Assert.assertEquals(true, yesElement.isDisplayed());
        Assert.assertEquals(true, noElement.isDisplayed());
        Assert.assertTrue(expMessage.equals(message));
        log.info("expMessage Displayed : " + expMessage);
        StepDetail.addDetail("expMessage Displayed : " + expMessage, true);
        if (yesNo.equalsIgnoreCase("yes")) {
            yesElement.click();
        } else {
            noElement.click();
        }
    }

    public void validateInlineMessage(String msg, String msgType) {
        getWait(5).until(ExpectedConditions.visibilityOf(inlineMsgExitButton));
        Map<String, String> screenElements = CommonUtils.getScreenElementData(driver, "//div//div//div//div//div//div//div//div//div//div");
        CommonUtils.doJbehavereportConsolelogAndAssertion("Inline exit button is displayed", "NA", inlineMsgExitButton.isDisplayed());
        CommonUtils.doJbehavereportConsolelogAndAssertion("Validate Inline Message:", msg, screenElements.containsKey(msg));
        CommonUtils.doJbehavereportConsolelogAndAssertion("Validate Inline Message Type:", msgType, screenElements.containsKey(msgType));
        inlineMsgExitButton.click();
    }


}
