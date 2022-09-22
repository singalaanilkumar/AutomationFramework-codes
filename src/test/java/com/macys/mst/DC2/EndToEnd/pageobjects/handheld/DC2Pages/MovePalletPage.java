package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Map;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class MovePalletPage extends BasePage {
    CommonUtils commonUtils = new CommonUtils();

    public MovePalletPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//strong[contains(text(),'Scan Pallet :')]")
    WebElement scanContainerIDLabel;

    @FindBy(xpath = "//input[@type='text']")
    WebElement scanContainerIDBox;

    @FindBy(xpath = "//input[@type='text']")
    WebElement scanLocationIDBox;

    @FindBy(xpath = "//span[text()='OK']")
    private WebElement okBtn;

    @FindBy(xpath = "//span[text()='Yes']")
    private WebElement yesBtn;

    @FindBy(xpath = "//span[text()='Back']")
    private WebElement backBtn;

    @FindBy(xpath = "//span[text()='Exit']")
    private WebElement exitBtn;

    // PopUp Message elements
    @FindBy(xpath = "//h6[contains(text(),'Confirmation')]")
    private WebElement confirmationPopUpTitle;

    @FindBy(xpath = "//*[contains(text(),'has been created with')]")
    private WebElement confirmationPopUpText;

    @FindBy(xpath = "//*[contains(text(), 'Is FROM container empty?')]")
    private WebElement emptyContainerConfirmationPopUpText;

    public void scanContainerID(String containerID) {
        pageLoadWait();
        log.info("Scan ContainerID :[{}]", containerID);
        getWait(10).until(visibilityOf(scanContainerIDLabel));
        scanContainerIDBox.sendKeys(containerID);
        scanContainerIDBox.sendKeys(Keys.ENTER);
    }


    public void clickExitButton() {
        getWait(15).until(visibilityOf(exitBtn));
        exitBtn.click();
    }


    public void validateMovePalletScreen(String ContainerId, Map<String, String> movePalletAPI) {
        try {
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(backBtn));
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(exitBtn));
            Map<String, String> movePalletUI = getScreenData("//div[@style='margin-bottom: 10px;']");
        } catch (Exception e) {
            log.info("Move Pallet screen is not displayed properly", e.getMessage());

        }

    }


    public void movePalletToStagingLocation(String stagingLocation) {
        try {

            log.info("Scan LocationID :[{}]", stagingLocation);
            scanLocationIDBox.sendKeys(stagingLocation);
            scanLocationIDBox.sendKeys(Keys.ENTER);

            log.info("Pallet is moved to the staging location");
            StepDetail.addDetail("Pallet is moved to the staging location", true);
            clickExitButton();

        } catch (Exception e) {
            log.info("Pallet is not moved to the staging location", e.getMessage());

        }

    }


}

	       

