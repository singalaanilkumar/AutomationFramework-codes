package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Map;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class ConsumeContainerPage extends BasePage {
    CommonUtils commonUtils = new CommonUtils();

    public ConsumeContainerPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//strong[contains(text(),'Scan Container ID: ')]")
    WebElement scanContainerIDLabel;

    @FindBy(xpath = "//input[@type='text']")
    WebElement scanContainerIDBox;

    @FindBy(xpath = "//span[text()='CLOSE']")
    private WebElement closeBtn;

    @FindBy(xpath = "//*[text()='Consume']")
    private WebElement consumeBtn;

    @FindBy(xpath = "//*[text()='Back']")
    private WebElement backBtn;

    @FindBy(xpath = "//*[text()='Exit']")
    private WebElement ExitBtn;

    // PopUp Message elements
    @FindBy(xpath = "//h6[contains(text(),'INFO')]")
    private WebElement confirmationPopUpTitle;

    @FindBy(xpath = "//*[contains(text(),'is consumed')]")
    private WebElement confirmationPopUpText;

    public void scanContainerID(String containerID) {
        pageLoadWait();
        log.info("Scan ContainerID :[{}]", containerID);
        getWait(10).until(visibilityOf(scanContainerIDLabel));
        scanContainerIDBox.sendKeys(containerID);
        scanContainerIDBox.sendKeys(Keys.ENTER);
    }


    public void clickCloseButton() {
        getWait(15).until(visibilityOf(closeBtn));
        closeBtn.click();
    }


    public void validateConsumeContainerConfirmationMsg(String containerID) {
        try {
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
            StringBuffer ConfirmationMsg = new StringBuffer();
            ConfirmationMsg.append("Container ");
            ConfirmationMsg.append(containerID);
            ConfirmationMsg.append(" is consumed");
            boolean msgValidation = (confirmationPopUpText.getText()).equalsIgnoreCase(String.valueOf(ConfirmationMsg));
            Assert.assertTrue("Consume container confirmation is not displayed", msgValidation);
            clickCloseButton();
            log.info("Consume container is performed successfully");
            StepDetail.addDetail("Consume container is performed successfully", true);
            ExitBtn.click();
        } catch (Exception e) {
            log.info("Consume container is not performed successfully", e.getMessage());

        }

    }


    public void validateConsumeContainerScreen(String containerID) {
        try {
            Map<String, String> consumeContainerUI = getScreenData("//div[@style='margin-bottom: 10px;']");
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(backBtn));
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(consumeBtn));
            consumeBtn.click();
            log.info("Consume container screen validated successfully");
            StepDetail.addDetail("Consume container screen is validated successfully", true);

        } catch (Exception e) {
            log.info("Consume container screen is not validated successfully", e.getMessage());

        }

    }

}

	       

