package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.model.WSMActivity;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.ContainerInquirySteps;
import com.macys.mst.DC2.EndToEnd.stepdefinitions.PrepOptionSteps;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Map;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Slf4j
public class MagicTotePage extends BasePage {
    private StepsContext stepsContext;
    public MagicTotePage(WebDriver driver) {
        super(driver);
    }
    PrepOptionSteps prepOptionSteps = new PrepOptionSteps(stepsContext);
    @FindBy(xpath = "//*[text()='Magic Tote']")
    WebElement magicTote;

    @FindBy(id = "entryBox")
    WebElement scanTote;

    @FindBy(xpath = "//span[text()='CLOSE']")
    private WebElement closeBtn;

    @FindBy(xpath = "//*[text()='YES']")
    private WebElement YesBtn;

    @FindBy(xpath = "//*[text()='Clear Duplicate Activity?']")
    private WebElement ConfirmationPopUp;

    StepsDataStore dataStorage = StepsDataStore.getInstance();
    public void selectMagicTote(){
        try {
            getWait(5).until(ExpectedConditions.visibilityOf(magicTote));
            magicTote.click();
        } catch (Exception e) {
            log.info("Exception occured while selecting Magic Tote");
        }
    }

    public void remove_badTote(){
        try {
            getWait(5).until(ExpectedConditions.visibilityOf(scanTote));
            scanTote.click();
            String Tote = (String) dataStorage.getStoredData().get("Tote_locataeContainer");
            scanTote.sendKeys(Tote);
            scanTote.sendKeys(Keys.ENTER);
            getWait(10).until(ExpectedConditions.visibilityOf(ConfirmationPopUp));
            String confirmationPopUpTxt = ConfirmationPopUp.getText().toString();
            CommonUtils.doJbehavereportConsolelogAndAssertion("confirmationPopUp:", confirmationPopUpTxt, true);
            getWait(5).until(ExpectedConditions.visibilityOf(YesBtn ));
            YesBtn.click();
        } catch (Exception e) {
            log.info("Exception occured while selecting Magic Tote");
        }
    }

    public void validateToteActivities(){
        String Tote = (String) dataStorage.getStoredData().get("Tote_locataeContainer");
        List<WSMActivity> wsmActivities = prepOptionSteps.getWSMActivities(Tote);
        String activitySize = String.valueOf(wsmActivities.size());
        getWait(5);
        if(wsmActivities.size()==1){
            CommonUtils.doJbehavereportConsolelogAndAssertion("WSM activites:", activitySize, true);
        }
        else{
            CommonUtils.doJbehavereportConsolelogAndAssertion("WSM activites:", activitySize, false);
        }
    }
}

	       

