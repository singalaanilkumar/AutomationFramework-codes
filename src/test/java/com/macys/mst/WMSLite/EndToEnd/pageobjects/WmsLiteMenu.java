package com.macys.mst.WMSLite.EndToEnd.pageobjects;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class WmsLiteMenu extends BasePage {

    public WmsLiteMenu(WebDriver driver) { super(driver); }

    @FindBy(xpath = "//mat-icon[@class='cursor-arrow unselectable mat-icon notranslate material-icons mat-icon-no-color' ][@role='img']")
    protected WebElement menu;

    @FindBy(xpath = "//span[text()='Receiving']")
    public WebElement Receiving;

    @FindBy(xpath = "//span[text()='Picking']")
    public WebElement Picking;

    @FindBy(xpath = "//span[text()='Drivers']")
    public WebElement Drivers;

    @FindBy(xpath = "//span[text()='ICQA']")
    public WebElement ICQA;

    @FindBy(xpath = "//span[text()='Planner']")
    public WebElement Planner;

    @FindBy(xpath = "//span[text()='Admin']")
    public WebElement Admin;

    @FindBy(xpath = "//span[text()=Shipping']")
    public WebElement Shipping;


}
