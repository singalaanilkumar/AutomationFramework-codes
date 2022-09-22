package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.artemis.selenium.PageObject;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class CreateInnerPacksPage extends PageObject {

    public CreateInnerPacksPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(linkText = "Create Pack")
    WebElement CreatePack;

    @FindBy(xpath = "//*[text()='Inner Pack']")
    WebElement InnerPack;

    @FindBy(xpath = "//STRONG[contains(text(),'Scan In-House UPC :')]")
    WebElement scanInHouseUPC;

    @FindBy(xpath = "//INPUT[@id='entryBox']")
    WebElement entryBoxUPC;

    @FindBy(xpath = "//input[@type='number']")
    WebElement enterQtyinput;

    @FindBy(xpath = "//button/span[contains(text(),'End')]")
    WebElement endCreateInnerPack;

    @FindBy(xpath = "//button/span[contains(text(),'Done')]")
    WebElement doneCreateInnerPack;

    @FindBy(xpath = "//button/span[contains(text(),'Back')]")
    WebElement BackButton;

    @FindBy(xpath = "//*[text()='Delete']")
    WebElement deleteButton;

    @FindBy(xpath = "//*[text()='Yes']")
    WebElement YesButton;


    public static int count;

    public void createInnerPacks(Map<PoLineBarCodeData.PoLinebarCode,String> innerPack_SKU_qty) throws Exception {
        for (Map.Entry<PoLineBarCodeData.PoLinebarCode,String> entry : innerPack_SKU_qty.entrySet()){
            navigateToCreateInnerPack(entry.getKey().getPoLineBarCode(),entry.getValue());
        }
        endInnerPack();
        clickDoneInnerPack();
        exitInnerpackCreation();
    }
    public void navigateToCreateInnerPack(String inHouseUPC, String quantity)throws Exception{
        if(count == 0) {
            selectCreatePack();
            TimeUnit.SECONDS.sleep(2);
            selectInnerPack();
            scanInHouseUPC(inHouseUPC);
            TimeUnit.SECONDS.sleep(2);
            boolean deletButtonflg = isElementPresent(deleteButton);
            if(deletButtonflg){
                deleteButton.click();
                getWait(10).until(ExpectedConditions.visibilityOf(YesButton));
                YesButton.click();
                getWait(10).until(ExpectedConditions.visibilityOf(scanInHouseUPC));
                scanInHouseUPC(inHouseUPC);
            }
            enterInHouseUPCQty(quantity);
            count++;
        }else{
            scanInHouseUPC(inHouseUPC);
            enterInHouseUPCQty(quantity);
        }
    }

    public static boolean isElementPresent(WebElement element) {
        try {
            element.getText();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    public void selectCreatePack() {
        getWait(30).until(visibilityOf(CreatePack));
        if (CreatePack.isDisplayed()) {
            CreatePack.click();
        }
    }

    public void selectInnerPack() {
        getWait(30).ignoring(Exception.class).until(visibilityOf(InnerPack));
        if (InnerPack.isDisplayed()) {
            InnerPack.click();
        }
    }

    public void enterInHouseUPCQty(String quantity) {
        getWait(15).until(visibilityOf(enterQtyinput));
        enterQtyinput.clear();
        enterQtyinput.sendKeys(quantity);
        enterQtyinput.sendKeys(Keys.ENTER);
    }

    public void endInnerPack(){
        getWait(5).until(visibilityOf(endCreateInnerPack));
        endCreateInnerPack.click();
    }

    public void clickDoneInnerPack(){
        getWait(5).until(visibilityOf(doneCreateInnerPack));
        doneCreateInnerPack.click();
    }

    public void exitInnerpackCreation(){
        getWait(5).until(visibilityOf(BackButton));
        BackButton.click();
        getWait(5).until(visibilityOf(BackButton));
        BackButton.click();
    }
    public void scanInHouseUPC(String inHouseUPC) {
        log.info("Scan In House UPC line :[{}]", inHouseUPC);
        getWait(5).until(visibilityOf(entryBoxUPC));
        entryBoxUPC.sendKeys(inHouseUPC);
        entryBoxUPC.sendKeys(Keys.ENTER);

    }
    private WebDriverWait getWait(int secs) {
        WebDriverWait wait = new WebDriverWait(driver, secs);
        return wait;
    }
}