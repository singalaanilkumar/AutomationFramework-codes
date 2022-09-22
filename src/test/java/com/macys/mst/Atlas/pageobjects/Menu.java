package com.macys.mst.Atlas.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Menu extends BasePage {
    @FindBy(xpath = "//span[text()='PO Inquiry']")
    public WebElement POInquiry;

    @FindBy(xpath = "//span[text()='PO Line Item']")
    public WebElement POLineItem;

    @FindBy(xpath = "//span[text()='PO Dashboard']")
    public WebElement PODashboard;

    @FindBy(xpath = "//span[text()   ='ASN Receipts']")
    public WebElement ASNReceipts;

    @FindBy(xpath = "//span[text()='DC Config']")
    public WebElement DCConfig;

    @FindBy(xpath = "//span[text()='Research Inventory']")
    public WebElement ResearchInventory;

    @FindBy(xpath = "//span[text()='MHE']")
    public WebElement MHE;

    @FindBy(xpath = "//span[text()='Manifest']")
    public WebElement Manifest;

    @FindBy(xpath = "//span[text()='WSM']")
    public WebElement WSM;

    @FindBy(xpath = "//span[text()='PO Manual Receipt']")
    public WebElement POManualReceipt;

    @FindBy(xpath = "//span[text()='Wave']")
    public WebElement Wave;

    @FindBy(xpath = "//span[text()='Support UI']")
    public WebElement SupportUI;

    @FindBy(xpath = "//span[text()='Network Map']")
    public WebElement NetworkMap;

}
