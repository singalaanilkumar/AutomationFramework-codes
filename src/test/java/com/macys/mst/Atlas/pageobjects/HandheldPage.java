package com.macys.mst.Atlas.pageobjects;

import com.macys.mst.Atlas.configuration.ReadHostConfiguration;
import com.macys.mst.Atlas.execdrivers.ExecutionConfig;
import com.macys.mst.Atlas.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class HandheldPage extends BasePage {
    private static final String SELECT_PICKING = "SELECT_PICKING";
    private static final String SELECT_PACKING = "SELECT_PACKING";
    private static final String SELECT_PACK_AND_PRINT = "SELECT_PACK_AND_PRINT";
    private static final String SELECT_PICK_BY_WORK_ID = "SELECT_PICK_BY_WORK_ID";
    private static final String SELECT_OUTBOUND = "SELECT_OUTBOUND";
    private static final String SELECT_EXCEPTIONS = "SELECT_EXCEPTIONS";
    private static final String SELECT_RF_MANIFEST_PACKAGE = "SELECT_RF_MANIFEST_PACKAGE";


    private StepsDataStore dataStorage = StepsDataStore.getInstance();

    public HandheldPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//input[@type='text']")
    WebElement userName;

    @FindBy(xpath = "//INPUT[@type='password']")
    WebElement password;

    @FindBy(xpath = "//input[@id='Login'] | //button[text()='Submit']")
    WebElement loginButton;

    @FindBy(xpath = "//b[contains(text(),'Outbound')]")
    WebElement outbound;

    @FindBy(xpath = "//b[contains(text(),'Inbound')]")
    WebElement inbound;

    @FindBy(xpath = "//*[contains(text(), 'Picking')]")
    WebElement picking;

    @FindBy(xpath = "//*[contains(text(), 'Packing')]")
    WebElement packing;

    @FindBy(xpath = "//*[contains(text(), 'Exceptions')]")
    WebElement exceptions;

    @FindBy(xpath = "//*[contains(text(), 'Pack and Print')]")
    WebElement packAndPrint;

    @FindBy(xpath = "//b[contains(text(), 'Pick By Work ID')]")
    WebElement pickByWorkId;

    @FindBy(xpath = "//b[contains(text(), 'RF Manifest Package')]")
    WebElement rfManifestPackage;

    @FindBy(xpath = "//input[@name='printerBarcode']")
    WebElement printerBarcode;

    @FindBy(xpath = "//input[@name='scanBarcode']")
    WebElement scanBarcode;

    @FindBy(xpath = "//input[@name='scanPackage']")
    WebElement scanPackage;

    @FindBy(xpath = "//span[text()='Select an application']")
    WebElement selectAnApllication;

    @FindBy(xpath = "//*[contains(text(),'Previous Package')]")
    WebElement previousPackage;

    @FindBy(xpath = "//*[contains(text(),'Manifest request submitted for the Package')]")
    WebElement manifestMessage;


    public void navigateToHandheld() throws Exception {
        log.info("driver = " + driver);
        StepDetail.addDetail(ReadHostConfiguration.HANDHELD_URL.value(), true);
        driver.get(ReadHostConfiguration.HANDHELD_URL.value());
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(20).ignoring(Exception.class).until(visibilityOf(userName));
        userName.clear();
//        userName.sendKeys(ExecutionConfig.appUIUserName);
        userName.sendKeys("BH16854");
        getWait(5000).ignoring(Exception.class).until(visibilityOf(password));
        password.clear();
//        password.sendKeys(ExecutionConfig.appUIPassword);
        password.sendKeys("Jul@2021");
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(20).ignoring(Exception.class).until(visibilityOf(loginButton));
        loginButton.click();
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(inbound));
        Assert.assertEquals(true, inbound.isDisplayed());
    }

    public void selectOptionFromWMSMenu(String param) {
        switch (param) {
            case SELECT_OUTBOUND:
                getWait(40).ignoring(Exception.class).until(elementToBeClickable(outbound));
                if (outbound.isDisplayed()) {
                    outbound.click();
                    getWait(20).ignoring(Exception.class).until(elementToBeClickable(picking));
                    Assert.assertEquals(true, picking.isDisplayed());
                    StepDetail.addDetail("Outbound option clicked",true);
                }
                break;
        }
    }

    public void selectOptionFromOutboundMenu(String param) {
        switch (param) {
            case SELECT_PICKING:
                getWait(5).ignoring(Exception.class).until(visibilityOf(picking));
                picking.click();
                break;
            case SELECT_PACKING:
                if (packing.isDisplayed()) {
                    packing.click();
                    getWait(20).ignoring(Exception.class).until(elementToBeClickable(packAndPrint));
                    Assert.assertEquals(true, packAndPrint.isDisplayed());
                    StepDetail.addDetail("Packing option clicked",true);
                }
                break;
            case SELECT_EXCEPTIONS:
                if (exceptions.isDisplayed()) {
                    exceptions.click();
                    getWait(20).ignoring(Exception.class).until(elementToBeClickable(rfManifestPackage));
                    Assert.assertEquals(true, rfManifestPackage.isDisplayed());
                    StepDetail.addDetail("Exceptions option clicked",true);
                }
                break;

        }
    }

    public void selectOptionFromPickingMenu(String param) {
        switch (param) {
            case SELECT_PICK_BY_WORK_ID:
                getWait(5).ignoring(Exception.class).until(visibilityOf(pickByWorkId));
                pickByWorkId.click();
                break;
        }
    }

    public void selectOptionFromPackingMenu(String param) {
        switch (param) {
            case SELECT_PACK_AND_PRINT:
                if (packAndPrint.isDisplayed()) {
                    packAndPrint.click();
                    getWait(20).ignoring(Exception.class).until(elementToBeClickable(printerBarcode));
                    Assert.assertEquals(true, printerBarcode.isDisplayed());
                    StepDetail.addDetail("Pack and Print option clicked",true);
                }
                break;
        }
    }

    public void selectOptionFromExceptionsMenu(String param) {
        switch (param) {
            case SELECT_RF_MANIFEST_PACKAGE:
                if (rfManifestPackage.isDisplayed()) {
                    rfManifestPackage.click();
                    getWait(20).ignoring(Exception.class).until(elementToBeClickable(scanPackage));
                    Assert.assertEquals(true, scanPackage.isDisplayed());
                    StepDetail.addDetail("RF Manifest Package option clicked",true);
                }
                break;
        }
    }

    public void scanPrinter() throws InterruptedException {
        selectOptionFromWMSMenu("SELECT_OUTBOUND");
        selectOptionFromOutboundMenu("SELECT_PACKING");
        selectOptionFromPackingMenu("SELECT_PACK_AND_PRINT");
        printerBarcode.sendKeys("123-123" + Keys.ENTER);
        getWait(20).ignoring(Exception.class).until(elementToBeClickable(scanBarcode));
        Assert.assertEquals(true, scanBarcode.isDisplayed());
        String package_no = dataStorage.getStoredData().get("package_number").toString();
        scanBarcode.sendKeys(package_no + Keys.ENTER);
//        Thread.sleep(1000);
//        driver.switchTo().alert().accept();
        Thread.sleep(20);
        getWait(5000).ignoring(Exception.class).until(elementToBeClickable(previousPackage));
        StepDetail.addDetail("Scanned package no : " + package_no, true);
    }

    public void rfManifestPackageNumber(){
        String package_no = dataStorage.getStoredData().get("package_number").toString();
//        String package_no = "10000000000043241994";
        selectOptionFromWMSMenu("SELECT_OUTBOUND");
        selectOptionFromOutboundMenu("SELECT_EXCEPTIONS");
        selectOptionFromExceptionsMenu("SELECT_RF_MANIFEST_PACKAGE");
        scanPackage.sendKeys(package_no + Keys.ENTER);
    }

    public void validateManifestation(){
        getWait(20).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(manifestMessage));
        Assert.assertEquals(true, manifestMessage.isDisplayed());
        StepDetail.addDetail("Manifest request submitted",true);
    }
}
