package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class ContainerInquiryPage extends BasePage {
    CommonUtils commonUtils = new CommonUtils();

    public ContainerInquiryPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//strong[contains(text(),'Scan Container ID :')]")
    WebElement scanContainerIDLabel;

    @FindBy(xpath = "//input[@id='entryBox']")
    WebElement scanContainerIDBox;

    @FindBy(xpath = "//button/span[contains(text(),'Details')]")
    WebElement detailsBtn;

    @FindBy(xpath = "//button/span[contains(text(),'Attributes')]")
    WebElement attributeBtn;

    @FindBy(xpath = "//span[text()='OK']")
    private WebElement okBtn;

    @FindBy(xpath = "//span[text()='Exit']")
    private WebElement exitBtn;

    @FindBy(xpath = "//span[text()='Back']")
    private WebElement backBtn;

    @FindBy(xpath = "//*[text()='UPC Details']")
    private WebElement containerUPCDetailsHeader;

    public void scanContainerID(String containerID) {
        pageLoadWait();
        log.info("Scan ContainerID :[{}]", containerID);
        getWait(10).until(visibilityOf(scanContainerIDLabel));
        scanContainerIDBox.sendKeys(containerID);
        scanContainerIDBox.sendKeys(Keys.ENTER);
    }

    public void clickDetailsButton() {
        getWait(10).until(visibilityOf(detailsBtn));
        detailsBtn.click();
    }

    public void clickUPCLink(String upc) {
        getWait(10).until(ExpectedConditions.elementToBeClickable(By.linkText(upc)));
        driver.findElement(By.linkText(upc)).click();
        getWait(20).until(visibilityOf(containerUPCDetailsHeader));
    }

    public void clickOKButton() {
        getWait(15).until(visibilityOf(okBtn));
        okBtn.click();
    }

    public void clickExitButton(){
        getWait(15).until(visibilityOf(exitBtn));
        exitBtn.click();
    }
    public void clickBackButton(){
        getWait(15).until(visibilityOf(backBtn));
        backBtn.click();
    }

    public void clickAttributeButton() {
        getWait(10).until(visibilityOf(attributeBtn));
        attributeBtn.click();
    }
}

