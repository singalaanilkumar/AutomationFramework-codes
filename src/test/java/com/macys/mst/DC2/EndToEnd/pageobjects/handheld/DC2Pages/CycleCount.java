package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CycleCount extends BasePage {

    CommonUtils commonUtils =  new CommonUtils();

    @FindBy(id="entryBox")
    private WebElement entryBox;

    @FindBy(xpath = "//span[text()='Exit']")
    private WebElement exitButton;

    @FindBy(xpath = "//span[text()='Back']")
    private WebElement backButton;

    @FindBy(xpath = "//span[text()='End Location']")
    private WebElement endLocation;

    @FindBy(xpath = "//span[text()='YES']")
    private WebElement yesButton;

    @FindBy(xpath = "//span[text()='CLOSE']")
    private WebElement closeButton;

    @FindBy(xpath = "//*[contains(text(), ' Total Containers in Location:')]//parent::div")
    public WebElement totalContainersScanned;

    @FindBy(xpath = "//table/tbody/tr")
    public List<WebElement> scannedBarcodeRows;

    @FindBy(tagName = "table")
    public WebElement scannedBarcodeTable;

    public void scanLocation(String location) throws InterruptedException {
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(entryBox));
        clearText(entryBox);
        entryBox.sendKeys(location);
        entryBox.sendKeys(Keys.ENTER);
        TimeUnit.SECONDS.sleep(2);
    }



    public void scanContainer(String containerBarcode){
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(entryBox));
        clearText(entryBox);
        entryBox.sendKeys(containerBarcode);
        entryBox.sendKeys(Keys.ENTER);
    }

    public void endLocation() throws InterruptedException {
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(endLocation));
        endLocation.click();
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(yesButton));
        yesButton.click();
        TimeUnit.SECONDS.sleep(2);
    }

    public void exit(){
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(exitButton));
        exitButton.click();
    }

    public void back(){
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(backButton));
        backButton.click();
    }

    public void closeButton(){
        pageLoadWait();
        getWait().until(ExpectedConditions.elementToBeClickable(closeButton));
        closeButton.click();
    }

    public int getTotalContainersScanned() throws InterruptedException {
        try {
            pageLoadWait();
            getWait().until(ExpectedConditions.visibilityOf(totalContainersScanned));
            return Integer.parseInt(totalContainersScanned.getText().substring(30));
        } catch (Exception ex) {
            endLocation();
            commonUtils.doJbehavereportConsolelogAndAssertion("Exception occurred in Validation,","But performed END LOCATION",false);
        }
        return 0;
    }

    public int getActualCountOfRowsInUITable() throws InterruptedException {
        try {
            pageLoadWait();
            getWait().until(ExpectedConditions.visibilityOf(scannedBarcodeRows.get(0)));
            return scannedBarcodeRows.size();
        }catch (Exception ex){
            endLocation();
            commonUtils.doJbehavereportConsolelogAndAssertion("Exception occurred in Validation,","But performed END LOCATION",false);
        }
        return 0;
    }

    public List<String> getBarcodeListFromUITable() throws InterruptedException {
        List<String> barcodeListFromUI = new ArrayList<>();
        try {
            pageLoadWait();
            getWait().until(ExpectedConditions.visibilityOf(scannedBarcodeRows.get(0)));
            for(WebElement row:scannedBarcodeRows){
                barcodeListFromUI.add(row.findElement(By.tagName("td")).getText());
            }
        }catch (Exception e){
            endLocation();
            commonUtils.doJbehavereportConsolelogAndAssertion("Exception occurred in Validation,","But performed END LOCATION",false);
        }
        return barcodeListFromUI;
    }

    public boolean dataTableAndCountFieldAreNotPresent() throws InterruptedException {
        boolean areElementsPresent = false;
        try {
            totalContainersScanned.isDisplayed();
        } catch (NoSuchElementException exception) {
            try {
                scannedBarcodeTable.isDisplayed();
            } catch (NoSuchElementException exc) {
                endLocation();
                areElementsPresent = true;
            }
        }
        return areElementsPresent;
    }

}
