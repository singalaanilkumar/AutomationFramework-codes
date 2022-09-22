package com.macys.mst.DC2.EndToEnd.pageobjects.handheld;

import com.macys.mst.DC2.EndToEnd.execdrivers.ExecutionConfig;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.config.FileConfig;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class Home extends BasePage {

    private String handheldUrl = FileConfig.getInstance().getStringConfigValue("AppUrls.RFMenuUrl");

    @FindBy(xpath = "//input[@type='text']")
    WebElement userName;

    @FindBy(xpath = "//INPUT[@type='password']")
    WebElement password;

    @FindBy(xpath = "//span[text()='Login']")
    WebElement loginButton;

    @FindBy(xpath = "//button/span[contains(text(),'Sign In')]")
    WebElement loginButton2;

    @FindBy(xpath = "//span[text()='Logout']")
    public WebElement LogoutButton;

    @FindBy(xpath = "//span[text()='Exit']")
    WebElement exitButton;

    public void login() {
        driver.navigate().to(handheldUrl);
        getWait().until(ExpectedConditions.visibilityOf(userName));
        userName.clear();
        userName.sendKeys(ExecutionConfig.appUIUserName);


        password.clear();
        password.sendKeys(ExecutionConfig.appUIPassword);


        loginButton.click();
        CommonUtils.doJbehavereportConsolelogAndAssertion("Logged in ", "Success", true);

        try {
            getWait(3).until(ExpectedConditions.urlContains("menu"));
        } catch (TimeoutException timeoutException) {
            getWait().until(ExpectedConditions.urlContains("locationList"));
        }
    }

    public void UIlogin() {
        Boolean isPresent = driver.findElements(By.xpath("//span[text()='Logout']")).size() > 0;
        if (isPresent == true) {
            getWait().until(ExpectedConditions.elementToBeClickable(LogoutButton));
            getWait(30).ignoring(Exception.class).until(visibilityOf(LogoutButton));
            LogoutButton.click();
        }
        // driver.navigate().to("https://uss-qa.devops.fds.com/login");
        driver.navigate().to("https://uss-uat.devops.fds.com/login");
        getWait().until(ExpectedConditions.visibilityOf(userName));
        userName.clear();
        userName.sendKeys("b0$NMADMIN");
        password.clear();
        password.sendKeys("WhmAdmin01");
        loginButton2.click();

    }

    public void cleanUpDriver() {
        driver.quit();
    }
}
