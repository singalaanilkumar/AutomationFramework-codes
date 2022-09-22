package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.PageObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class RFMenuPage extends PageObject {

    @FindBy(xpath = "//*[@type='text']")
    WebElement userName;

    @FindBy(xpath = "//*[@type='password']")
    WebElement password;

    @FindBy(xpath = "//button[span[text()='Login']]")
    WebElement loginButton;

    @FindBy(xpath = "//*[@type='text']")
    WebElement textBox;

    @FindBy(xpath = "//*[@type='string'] | //input[@id=\"entryBox\"]")
    WebElement inputTextBox;

    @FindBy(xpath = "//*[@name='po']")
    WebElement poTextBox;

    @FindBy(xpath = "//*[@name='poReceipt']")
    WebElement poReceiptTextBox;

    @FindBy(xpath = "//button[span[text()='Back']] | //button[@aria-label='back']")
    WebElement backButton;

    @FindBy(xpath = "//button[span[text()='Exit']] | //button[@aria-label='exit']")
    WebElement exitButton;

    @FindBy(xpath = "//button[span[text()='EXIT']]")
    WebElement EXITButton;

    @FindBy(xpath = "//button[span[text()='CLOSE']]")
    WebElement closeButton;

    @FindBy(xpath = "//button[span[text()='OK']]")
    WebElement OkButton;

    @FindBy(xpath = "//*[contains(text(), 'DC2.0 RF Options')]")
    WebElement rfOptions;

    @FindBy(xpath = "//*[contains(text(), 'Manual ASN')]")
    WebElement ManualASNLabel;

    @FindBy(xpath = "//*[contains(text(),'Single SKU')]")
    WebElement singleSKU;

    @FindBy(xpath = "//*[contains(text(),'Single UPC')]")
    WebElement singleUPC;

    @FindBy(xpath = "//b[contains(text(),'Inner Pack')]")
    WebElement innerPack;

    @FindBy(xpath = "//b[contains(text(),'Pallet')]")
    WebElement pallet;

    @FindBy(xpath = "//button[@aria-label='exit']")
    WebElement preSortExitButton;

    @FindBy(xpath = "//b[contains(text(),'Create Bin')]")
    WebElement createBin;

    private String prodHostName = "https://hh.msc.gcp.cloudrts.net/login";
    private String prodUserName = System.getProperty("racfId");
    private String prodPassWord = System.getProperty("password");

    public RFMenuPage(WebDriver driver) {
        super(driver);
    }

    public void prodlogin() throws Exception {
        if (StringUtils.isBlank(prodUserName) || StringUtils.isBlank(prodPassWord)) {
            Assert.fail("UserName and Password not provided to login to Handheld UI");
        }
        log.info("{HH Prod URL}", prodHostName);
        log.info("{HH Prod UserName}", prodUserName);

        driver.get(prodHostName);

        getWait(20).ignoring(Exception.class).until(visibilityOf(userName));
        userName.clear();
        userName.sendKeys(prodUserName);

        getWait(20).ignoring(Exception.class).until(visibilityOf(password));
        password.clear();
        password.sendKeys(prodPassWord);

        getWait(20).ignoring(Exception.class).until(visibilityOf(loginButton));
        loginButton.click();

        TimeUnit.SECONDS.sleep(20);

        getWait(20).ignoring(Exception.class).until(elementToBeClickable(rfOptions));
        Assert.assertEquals(true, rfOptions.isDisplayed());
    }

    public void clickNavOption(String option){
        WebElement navOption = driver.findElement(By.linkText(option));
        navOption.click();
        log.info("Navigating to " + option + " UI");
        StepDetail.addDetail("Navigating to " + option + " UI", true);
    }

    protected WebElement waitForElement(By locator, int timeout) {
        int count = 0;
        while (count < timeout) {
            try {
                return driver.findElement(locator);
            } catch (NoSuchElementException e) {
                count++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("Error: ", e1);
                }
            }
        }
        log.info("Element could not be found " + locator);
        return null;
    }

    public WebDriverWait getWait(int waitTime) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        return wait;
    }

    public Boolean isElementDisplayed(By locator) {
        try {
            waitForElement(locator, 15);
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void scanBarcode(String scanText) {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(textBox));
            textBox.clear();
            textBox.sendKeys(scanText);
            textBox.sendKeys(Keys.ENTER);
            log.info("Barcode scanned with value" + textBox);
            StepDetail.addDetail("Barcode scanned with value" + textBox, true);
        } catch (Exception e) {
            log.info("Unable to scan Barcode" + e.getLocalizedMessage());
            StepDetail.addDetail("Unable to scan Barcode" + e.getLocalizedMessage(), false);
        }
    }

    public void scanContainer(String scanText) {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(textBox));
            textBox.clear();
            textBox.sendKeys(scanText);
            textBox.sendKeys(Keys.ENTER);
            log.info("Container ID scanned with value" + textBox);
            StepDetail.addDetail("Container ID scanned with value" + textBox, true);
        } catch (Exception e) {
            log.info("Unable to scan Container ID" + e.getLocalizedMessage());
            StepDetail.addDetail("Unable to scan Container ID" + e.getLocalizedMessage(), false);
        }
    }


    public void validatePopupMessageAndClose(String popUpText){
        WebElement popUpMsgElmnt = driver.findElement(By.xpath("//*[contains(text(),'" + popUpText + "')]"));
        popUpMsgElmnt.click();
        log.info("Validated message " + popUpText);
        StepDetail.addDetail("Validated message " + popUpText, true);

            if (popUpText.contains("Not a  Valid Exception Lane") || popUpText.contains("not a valid lane"))
                clickOkButton();
            else if(popUpText.contains("does not exist")) {
                clickBackButton();
            }if(popUpText.contains("Invalid Barcode")) {
            clickBackButton();
        }else {
                clickCloseButton();
            }
    }

    private void clickOkButton() {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(OkButton));
            OkButton.click();
            log.info("Close button clicked");
        } catch (Exception e) {
            log.info("Unable to click Close button" + e.getLocalizedMessage());
        }

    }

    public void clickExitButton() {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(exitButton));
            exitButton.click();
            log.info("Exit button clicked");
        } catch (Exception e) {
            log.info("Unable to click Exit button" + e.getLocalizedMessage());
        }
    }

    public void clickBackButton() {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(backButton));
            backButton.click();
            log.info("Back button clicked");
        } catch (Exception e) {
            log.info("Unable to click Back button" + e.getLocalizedMessage());
        }
    }

    public void clickCloseButton() {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(closeButton));
            closeButton.click();
            log.info("Close button clicked");
        } catch (Exception e) {
            log.info("Unable to click Close button" + e.getLocalizedMessage());
        }
    }

    public void clickOnSelectProcessArea() {
        WebElement processAreaDropdown = driver.findElement(By.id("selector"));
        getWait(25).ignoring(Exception.class).until(visibilityOf(processAreaDropdown));
        processAreaDropdown.click();

    }

    public void selectProcessArea(String selectProcessAreaString) throws InterruptedException {
        WebElement processAreaDropdown = driver.findElement(By.id("selector"));
        getWait(25).ignoring(Exception.class).until(visibilityOf(processAreaDropdown));

        Select selDropdown = new Select(processAreaDropdown);
        List<WebElement> options = selDropdown.getOptions();

        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getText().equalsIgnoreCase(selectProcessAreaString)) {
                StepDetail.addDetail("Selecting the process area: " + options.get(i).getText(), true);
                log.info("Selecting the process area :{}", options.get(i).getText());
                options.get(i).click();
                break;
            }
        }
    }

    public void selectSingleOrInnerOption(Boolean hasInnerPack) {
        log.info("Tote Option Selected. hasInnerPack:[{}]", hasInnerPack);
        if (hasInnerPack) {
            getWait(5).ignoring(Exception.class).until(visibilityOf(innerPack));
            innerPack.click();
        } else {
            getWait(15).ignoring(Exception.class).until(visibilityOf(singleUPC));
            singleUPC.click();
        }

    }

    public void selectPallet() {
        log.info("Pallet Option Selected");
        getWait(5).ignoring(Exception.class).until(visibilityOf(pallet));
        pallet.click();

    }

    public void scanRandomText(String scanText) {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(inputTextBox));
            inputTextBox.clear();
            inputTextBox.sendKeys(scanText);
            inputTextBox.sendKeys(Keys.ENTER);
            log.info("Text scanned with value" + textBox);
            StepDetail.addDetail("Barcode scanned with value" + textBox, true);
        } catch (Exception e) {
            log.info("Unable to scan Text" + e.getLocalizedMessage());
            StepDetail.addDetail("Unable to scan Text" + e.getLocalizedMessage(), false);
        }

    }

    public void clickEXITButton() {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(EXITButton));
            EXITButton.click();
            log.info("Exit button clicked");
        } catch (Exception e) {
            log.info("Unable to click Exit button" + e.getLocalizedMessage());
        }

    }

    public void clickpresortBackButton() {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(preSortExitButton));
            preSortExitButton.click();
            log.info("Exit button clicked");
        } catch (Exception e) {
            log.info("Unable to click exit button" + e.getLocalizedMessage());
        }

    }

    public void scanPO(String scanText) {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(poTextBox));
            poTextBox.clear();
            poTextBox.sendKeys(scanText);
            poTextBox.sendKeys(Keys.ENTER);
            log.info("Text scanned with value" + textBox);
            StepDetail.addDetail("PO scanned with value" + textBox, true);
        } catch (Exception e) {
            log.info("Unable to scan Text" + e.getLocalizedMessage());
            StepDetail.addDetail("Unable to scan Text" + e.getLocalizedMessage(), false);
        }

    }

    public void scanPOReceipt(String scanText) {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(poReceiptTextBox));
            poReceiptTextBox.clear();
            poReceiptTextBox.sendKeys(scanText);
            poReceiptTextBox.sendKeys(Keys.ENTER);
            log.info("Text scanned with value" + textBox);
            StepDetail.addDetail("POReceipt scanned with value" + textBox, true);
        } catch (Exception e) {
            log.info("Unable to scan Text" + e.getLocalizedMessage());
            StepDetail.addDetail("Unable to scan Text" + e.getLocalizedMessage(), false);
        }

    }

    public void validateManualASN() {
        try {
            getWait(20).until(ExpectedConditions.visibilityOf(poTextBox));
            log.info("poTextBox is displayed" + textBox);
            getWait(20).until(ExpectedConditions.visibilityOf(ManualASNLabel));
            log.info("ManualASNLabel is displayed" + ManualASNLabel.getText());
            StepDetail.addDetail("Manual ASN is displayed", true);
        } catch (Exception e) {
            log.info("Manual ASN is displayed" + e.getLocalizedMessage());
            StepDetail.addDetail("Manual ASN is displayed" + e.getLocalizedMessage(), false);
        }

    }


}
