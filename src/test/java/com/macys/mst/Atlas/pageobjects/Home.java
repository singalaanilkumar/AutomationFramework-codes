package com.macys.mst.Atlas.pageobjects;

import com.macys.mst.Atlas.execdrivers.ExecutionConfig;
import com.macys.mst.artemis.config.FileConfig;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Home extends BasePage {
    Menu menu=new Menu();
    private String apolloUIUrl = FileConfig.getInstance().getStringConfigValue("AppUrls.apolloUIUrl");

    @FindBy(xpath = "//input[@type='text']")
    WebElement userName;

    @FindBy(xpath = "//INPUT[@type='password']")
    WebElement password;

    @FindBy(xpath = "//span[text()='Sign In']")
    WebElement signInButton;

    public void signIn() {

        driver.navigate().to(apolloUIUrl);
        if(driver.getCurrentUrl().contains("login")) {
            getWait().until(ExpectedConditions.visibilityOf(userName));
            userName.clear();
            userName.sendKeys(ExecutionConfig.appUIUserName);
            password.clear();
            password.sendKeys(ExecutionConfig.appUIPassword);
            signInButton.click();
        } else{
            menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.POInquiry));
        }
    }


    public void cleanUpDriver(){
        driver.quit();
    }
}
