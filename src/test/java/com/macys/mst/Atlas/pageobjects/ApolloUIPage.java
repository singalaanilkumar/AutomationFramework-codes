package com.macys.mst.Atlas.pageobjects;

import com.macys.mst.Atlas.configuration.ReadHostConfiguration;
import com.macys.mst.Atlas.execdrivers.ExecutionConfig;
import com.macys.mst.Atlas.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class ApolloUIPage extends BasePage{
    private static final String SELECT_TALOS = "SELECT_TALOS";
    private static final String SELECT_ATLAS = "SELECT_ATLAS";
    private static final String SELECT_PACKAGE_DETAIL_INQUIRY = "SELECT_PACKAGE_DETAIL_INQUIRY";
    private static final String SELECT_MANIFEST_PACKAGE = "SELECT_MANIFEST_PACKAGE";


    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    public ApolloUIPage(WebDriver driver) {
        super(driver);
    }
    static String mainPage = null;
    static String invoice = null;
    String packageNo = null;

    @FindBy(xpath = "//input[@type='text']")
    WebElement userName;

    @FindBy(xpath = "//INPUT[@type='password']")
    WebElement password;

    @FindBy(xpath = "//input[@id='Login'] | //button[text()='Submit']")
    WebElement loginButton;

    @FindBy(xpath = "//span[text()='Select an application']")
    WebElement selectAnApllication;

    @FindBy(xpath = "//li[text()='TALOS']")
    WebElement talos;

    @FindBy(xpath = "//li[text()='TALOS']")
    WebElement atlas;

    @FindBy(xpath = "//span[text()='Select a menu']")
    WebElement selectmenu;

    @FindBy(xpath = "//div[@id='SUB_MENU']//div[@class='chosen-search']//input[@type='text']")
    WebElement selectSubMenu;

    @FindBy(xpath = "//*[text()='Package Detail Inquiry']")
    WebElement packageDetailInquiry;

    @FindBy(xpath = "//input[@name='packageNumber']")
    WebElement packageNumber;

    @FindBy(xpath = "//input[@name='packageNbr']")
    WebElement packageNbr;

    @FindBy(xpath = "//td[text()='Package Number:']/following-sibling::td")
    WebElement packageNbrTextField;

    @FindBy(xpath = " //b[contains(text(),'Advanced Search')]")
    WebElement advancedSearch;

    @FindBy(xpath = "//select[@name='toStatus']")
    WebElement toStatus;

    @FindBy(xpath = "//select[@name='toStatus']//option[text()='Shipped']")
    WebElement shipped;

    @FindBy(xpath = "//input[@value='Search']")
    WebElement search;

    @FindBy(xpath = "//INPUT[@type='checkbox']")
    WebElement checkbox;

    @FindBy(xpath = "//input[@value='View Invoice']")
    WebElement viewInvoice;

    @FindBy(xpath = "//input[@value='View Shipment Label']")
    WebElement viewShipmentLabel;

    @FindBy(xpath = "//b[text()='LOGOUT']")
    WebElement logout;

    @FindBy(xpath = "//input[@id='actualWt']")
    WebElement weightField;

    @FindBy(xpath = "//input[@value='Manifest']")
    WebElement manifestButton;

    @FindBy(xpath = "//input[@value='Manifest']")
    WebElement manifestMessage;

    @FindBy(xpath = "//span[text()='Ok']")
    WebElement errorOkButton;


    public void navigateToApollo(){
        log.info("driver = " + driver);
        StepDetail.addDetail(ReadHostConfiguration.APOLLO_URL.value(), true);
        driver.get(ReadHostConfiguration.APOLLO_URL.value());
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(20).ignoring(Exception.class).until(visibilityOf(userName));
        userName.clear();
        userName.sendKeys(ExecutionConfig.appUIUserName);
        //userName.sendKeys("BH16854");
        getWait(20).ignoring(Exception.class).until(visibilityOf(password));
        password.clear();
        password.sendKeys(ExecutionConfig.appUIPassword);
        //password.sendKeys("Jul@2021");
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(20).ignoring(Exception.class).until(visibilityOf(loginButton));
        loginButton.click();
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(selectAnApllication));
        Assert.assertEquals(true, selectAnApllication.isDisplayed());
    }

    public void navigateToApolloTalos(String page) throws InterruptedException{
        StepDetail.addDetail(ReadHostConfiguration.APOLLO_URL.value(), true);
        driver.get(ReadHostConfiguration.APOLLO_URL.value());
        log.info("driver = " + driver.getCurrentUrl());
        log.info("driver = " + driver.getTitle());
        getWait(5000).ignoring(Exception.class).until(elementToBeClickable(selectmenu));
        Assert.assertEquals(true, selectmenu.isDisplayed());
        selectmenu.click();
        Thread.sleep(20);
        WebElement element = driver.findElement(By.xpath("//div[@id='SUB_MENU']//div[@class='chosen-search']//input[@type='text']"));
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(element));
        element.click();
        if(page.equalsIgnoreCase("Package Detail Inquiry")) {
            element.sendKeys(page + Keys.ENTER);
            getWait(40).ignoring(Exception.class).until(elementToBeClickable(packageNumber));
            Assert.assertEquals(true, packageNumber.isDisplayed());
            StepDetail.addDetail("Successfully landed on page "+page,true);
        }
    }

    public void selectOptionFromApolloMainMenu(String param) {
        switch (param) {
            case SELECT_TALOS:
                getWait(40).ignoring(Exception.class).until(elementToBeClickable(talos));
                Assert.assertEquals(true, talos.isDisplayed());
                talos.click();
                break;
            case SELECT_ATLAS:
                getWait(40).ignoring(Exception.class).until(elementToBeClickable(atlas));
                Assert.assertEquals(true, atlas.isDisplayed());
                atlas.click();
                break;
        }
    }

    public void selectOptionFromTalos(String param) {
        switch (param) {
            case SELECT_PACKAGE_DETAIL_INQUIRY:
                selectSubMenu.sendKeys("Package Detail Inquiry" + Keys.ENTER);
                getWait(40).ignoring(Exception.class).until(elementToBeClickable(packageNumber));
                Assert.assertEquals(true, packageNumber.isDisplayed());
                StepDetail.addDetail("Successfully landed to page : Package Detail Inquiry",true);
                break;
        }
    }

    public void selectOptionFromAtlas(String param) {
        switch (param) {
            case SELECT_MANIFEST_PACKAGE:
                selectSubMenu.sendKeys("Manifest Package" + Keys.ENTER);
                getWait(40).ignoring(Exception.class).until(elementToBeClickable(packageNbr));
                Assert.assertEquals(true, packageNbr.isDisplayed());
                StepDetail.addDetail("Successfully landed to page : Manifest Package",true);
                break;
        }
    }

    public void navigateToMainMenuSubMenu(String mainMenu, String subMenu) throws InterruptedException {
        boolean flag = true;
        selectAnApllication.click();
        if(mainMenu.equalsIgnoreCase("Talos")) {
            selectOptionFromApolloMainMenu("SELECT_TALOS");
        }
        else if(mainMenu.equalsIgnoreCase("Atlas")) {
            selectOptionFromApolloMainMenu("SELECT_ATLAS");
        }

        getWait(5000).ignoring(Exception.class).until(elementToBeClickable(selectmenu));
        Assert.assertEquals(true, selectmenu.isDisplayed());
        selectmenu.click();
        Thread.sleep(20);
       // WebElement element = driver.findElement(By.xpath("//div[@id='SUB_MENU']//div[@class='chosen-search']//input[@type='text']"));
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(selectSubMenu));
        selectSubMenu.click();
        if(subMenu.equalsIgnoreCase("Manifest Package")) {
            selectOptionFromTalos("SELECT_PACKAGE_DETAIL_INQUIRY");
        }
        if(subMenu.equalsIgnoreCase("Package Detail Inquiry")) {
            selectOptionFromAtlas("SELECT_MANIFEST_PACKAGE");
        }
    }

    public void searchUsingPackageNo() throws InterruptedException{
        packageNo = dataStorage.getStoredData().get("package_number").toString();
//        packageNo = "10000000000043241994";
        packageNumber.clear();
        getWait(1000).ignoring(Exception.class).until(elementToBeClickable(packageNumber));
        packageNumber.sendKeys(packageNo);
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(advancedSearch));
        advancedSearch.click();
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(toStatus));
        shipped.click();
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(search));
        search.click();
        //Thread.sleep(20);
    }

    public void enterPackageNo(){
        packageNo = dataStorage.getStoredData().get("package_number").toString();
//        packageNo = "10000000000043241994";
        packageNbr.clear();
        getWait(1000).ignoring(Exception.class).until(elementToBeClickable(packageNbr));
        packageNbr.sendKeys(packageNo);
        try{
            getWait(10000).until(elementToBeClickable(errorOkButton));
            errorOkButton.click();
        }
        catch(Exception e){
            log.info("Error popup was not displayed");
        }
        getWait(1000).ignoring(Exception.class).until(elementToBeClickable(packageNbrTextField));
        Assert.assertNotNull("Package details are not populated",packageNbrTextField.getText());
    }

    public void downloadInvoice() throws InterruptedException, IOException, AWTException {
        getWait(1000).ignoring(Exception.class).until(elementToBeClickable(checkbox));
        checkbox.click();
        mainPage = getWindowId();
        checkbox.click();
        Thread.sleep(10000);
        //packageNo = dataStorage.getStoredData().get("package_number").toString();
        //getWait(10000).ignoring(Exception.class).until(elementToBeClickable(driver.findElement(By.xpath("//td[text()='"+packageNo+"']"))));
        checkbox.click();
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(viewInvoice));
        Assert.assertEquals(true, viewInvoice.isDisplayed());
        viewInvoice.click();
        Thread.sleep(10000);
        invoice = getWindowId();
        driver.switchTo().window(invoice);
        Thread.sleep(10000);
        Assert.assertTrue(driver.getCurrentUrl().contains("viewInvoiceAsPDF"));
        StepDetail.addDetail("Invoice generated successfully",true);
        driver.close();
        Thread.sleep(3000);
        driver.switchTo().window(mainPage);
        Thread.sleep(5000);
    }

    public void downloadShipmentLabel() throws InterruptedException{
        getWait(40).ignoring(Exception.class).until(elementToBeClickable(viewShipmentLabel));
        Assert.assertEquals(true, viewShipmentLabel.isDisplayed());
        viewShipmentLabel.click();
        Thread.sleep(10000);
        String shipmentLabel = getWindowId();
        driver.switchTo().window(shipmentLabel);
        Thread.sleep(10000);
        Assert.assertTrue(driver.getCurrentUrl().contains("viewShippingLabelAsPDF"));
        StepDetail.addDetail("ShipmentLabel generated successfully",true);
        driver.close();
        Thread.sleep(2000);
        driver.switchTo().window(mainPage);
    }

    public void logoutFromApollo() throws InterruptedException{
        getWait(30000).ignoring(Exception.class).until(elementToBeClickable(logout));
        logout.click();
        Thread.sleep(5000);
    }

    public void enterWeightInWeightField(){
        String weight = dataStorage.getStoredData().get("Estimated_Weight").toString();
        weightField.sendKeys(weight);
    }

    public void clickOnManifestButton(){
        getWait(30000).ignoring(Exception.class).until(elementToBeClickable(manifestButton));
        manifestButton.click();
    }

    public void validateManifestMessage(){
        getWait(30000).ignoring(Exception.class).until(elementToBeClickable(manifestMessage));
        Assert.assertEquals("Manifest message is not available",manifestMessage.getText(),"");
        StepDetail.addDetail("Manifest success message received successfully ",true);
    }

    public void validatePackageStatusInDB(){

    }








}
