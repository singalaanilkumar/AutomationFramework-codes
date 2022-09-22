package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplitMovePage extends BasePage {
    CommonUtils commonUtils = new CommonUtils();

    public SplitMovePage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//strong[contains(text(),'Scan source container :')]")
    WebElement scanSourceContainerIDLabel;
    
    @FindBy(xpath = "//strong[contains(text(),'Scan Target Container:')]")
    WebElement scanTargetContainerIDLabel;
    
    @FindBy(xpath = "//strong[contains(text(),'Units to Move')]")
    WebElement UnitsToMoveLabel;
    
    @FindBy(xpath = "//strong[contains(text(),'Enter Units:')]")
    WebElement scansourceQtyLabel;

    @FindBy(xpath = "//input[@type='text']")
    WebElement scanContainerIDBox;

    @FindBy(xpath = "//button/span[contains(text(),'Details')]")
    WebElement detailsBtn;

    @FindBy(xpath = "//span[text()='OK']")
    private WebElement okBtn;
    
    @FindBy(xpath = "//span[text()='Yes']")
    private WebElement yesBtn;

    @FindBy(xpath = "//*[text()='UPC Details']")
    private WebElement containerUPCDetailsHeader;
    
 // PopUp Message elements
    @FindBy(xpath = "//h6[contains(text(),'Confirmation')]")
    private WebElement confirmationPopUpTitle;
    
    @FindBy(xpath = "//*[contains(text(),'has been created with')]")
    private WebElement confirmationPopUpText;
    
    @FindBy(xpath = "//*[contains(text(), 'Is FROM container empty?')]")
    private WebElement emptyContainerConfirmationPopUpText;

    public void scansourceContainerID(String containerID) {
        pageLoadWait();
        log.info("Scan ContainerID :[{}]", containerID);
        getWait(10).until(visibilityOf(scanSourceContainerIDLabel));
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

    public void clickYesButton() {
        getWait(15).until(visibilityOf(yesBtn));
        yesBtn.click();
    }

	public void scanUnits(int sourceQty) {
		  log.info("Scan Quantity :[{}]", sourceQty);
	      getWait(10).until(visibilityOf(scansourceQtyLabel));
	      scanContainerIDBox.sendKeys(String.valueOf(sourceQty));
	      scanContainerIDBox.sendKeys(Keys.ENTER);
		
	}

	public void scanTargetContainer(String targetToteId) {
		pageLoadWait();
        log.info("Scan ContainerID :[{}]", targetToteId);
        getWait(10).until(visibilityOf(UnitsToMoveLabel));
        getWait(10).until(visibilityOf(scanTargetContainerIDLabel));
        scanContainerIDBox.sendKeys(targetToteId);
        scanContainerIDBox.sendKeys(Keys.ENTER);
	}
	
	public void validateSplitMoveConfirmationMsg(String toteId,Integer targetQty) {
		 try {
	            getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
	            StringBuffer ConfirmationMsg = new StringBuffer();
	            ConfirmationMsg.append(toteId);
	            ConfirmationMsg.append(" has been created with ");
	            ConfirmationMsg.append(String.valueOf(targetQty));
	            ConfirmationMsg.append(" unit(s). Do you want to continue to split ?");
	            
	            boolean msgValidation = (confirmationPopUpText.getText()).equalsIgnoreCase(String.valueOf(ConfirmationMsg));
                Assert.assertTrue("Split Move confirmation is not displayed", msgValidation);
	            clickYesButton();
	            log.info("Split Move is performed successfully");
	            StepDetail.addDetail("Split Move is performed successfully", true);
	            
	        } catch (Exception e) {
	            log.info("Split Move is not performed successfully", e.getMessage());
	            
	        }
		 
	}

	public void validateEmptyContainerConfirmationMsg() {
		try {
            getWait(30).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(confirmationPopUpTitle));
            
            if (emptyContainerConfirmationPopUpText.getText().contains("Is FROM container empty?")) {
            	clickYesButton();
                log.info("Empty container confirmation is validated successfully");
                StepDetail.addDetail("Empty container confirmation is validated successfully", true);
            }else{
                Assert.assertTrue("Empty container confirmation is not validated : "+confirmationPopUpText.getText(),false);
            }
        } catch (Exception e) {
            log.info("Empty container confirmation is not validated successfully", e.getMessage());
            
        }
		
	}
	
}

	       

