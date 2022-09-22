package com.macys.mst.DC2.EndToEnd.pageobjects.handheld.DC2Pages;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.handheld.SubMenu;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DC2SubMenu extends BasePage implements SubMenu {

    @FindBy(linkText = "Pack Away Sorting")
    public WebElement packAwaySorting;

    @FindBy(linkText = "Create Inner Pack")
    public WebElement createInnerPack;

    @FindBy(linkText = "Create Tote")
    public WebElement createTote;

    @FindBy(linkText = "SortToStore")
    public WebElement sortToStore;

    @FindBy(linkText = "Print Ticket")
    public WebElement printTicket;

    @FindBy(linkText = "Putaway Pallet")
    public WebElement putawayPallet;

    @FindBy(linkText = "Split Move")
    public WebElement splitMove;

    @FindBy(linkText = "Adjust Container")
    public WebElement adjustContainer;

    @FindBy(linkText = "Locate Container")
    public WebElement locateContainer;

    @FindBy(linkText = "Ticket/Prep")
    public WebElement ticketPrep;

    @FindBy(linkText = "Consume Container")
    public WebElement consumeContainer;

    @FindBy(linkText = "Release Lane")
    public WebElement releaseLane;

    @FindBy(linkText = "Container Inquiry")
    public WebElement containerInquiry;

    @FindBy(linkText = "Pack Away Pull")
    public WebElement packAwayPull;

    @FindBy(linkText = "Runner")
    public WebElement runner;

    @FindBy(linkText = "Cycle Count")
    public WebElement cycleCount;

    @FindBy(linkText = "Exception Lane")
    public WebElement ExceptionLane;

    @FindBy(xpath = "//a[@href='/buildPallet']")
    WebElement  buildPalletLink;

    @FindBy(xpath = "//a[@href='/rebuildModifyPallet']")
    WebElement  Split_Adjust_pallet_link;

    @FindBy(xpath = "//span[text()='Back']")
    public WebElement backButton;

    @FindBy(xpath = "//span[text()='Exit']")
    public WebElement exitButton;

    @FindBy(linkText = "Dock Scan")
    public WebElement dockScan;


    @Override
    public void clickOnGivenSubMenu(String selecetedSubMenu) {
        switch (selecetedSubMenu) {
            case "Pack_Away_Sorting":
                packAwaySorting.click();
                getWait().until(ExpectedConditions.urlContains("packaway-sorting"));
                break;
            case "Create_Inner_Pack":
                createInnerPack.click();
                getWait().until(ExpectedConditions.urlContains("createInnerpack"));
                break;
            case "Create Tote":
                createTote.click();
                getWait().until(ExpectedConditions.urlContains("create-tote"));
                break;
            case "SortToStore":
                sortToStore.click();
                getWait().until(ExpectedConditions.urlContains("sortToStore"));
                break;
            case "Print_Ticket":
                printTicket.click();
                getWait().until(ExpectedConditions.urlContains("print-ticket"));
                break;
            case "Putaway_Pallet":
                putawayPallet.click();
                getWait(5).until(ExpectedConditions.urlContains("locate-pallet"));
                break;
            case "Split_Move":
                splitMove.click();
                getWait(5).until(ExpectedConditions.urlContains("splitMove"));
                break;
            case "Adjust_Container":
                adjustContainer.click();
                getWait(5).until(ExpectedConditions.urlContains("adjustContainer"));
                break;
            case "Locate Container":
                locateContainer.click();
                getWait(5).until(ExpectedConditions.urlContains("stage-pallet"));
                break;
            case "Ticket/Prep":
                ticketPrep.click();
                getWait(5).until(ExpectedConditions.urlContains("prep-option"));
                break;
            case "Consume_Container":
                consumeContainer.click();
                getWait(5).until(ExpectedConditions.urlContains("consumeContainer"));
                break;
            case "Release_Lane":
                releaseLane.click();
                getWait(5).until(ExpectedConditions.urlContains("releaseLane"));
                break;
            case "Container_Inquiry":
                containerInquiry.click();
                getWait(5).until(ExpectedConditions.urlContains("containerInquiry"));
                break;
            case "Pack_Away_Pull":
                packAwayPull.click();
                getWait(5).until(ExpectedConditions.urlContains("packaway-pull"));
                break;
            case "Runner":
                runner.click();
                getWait(5).until(ExpectedConditions.urlContains("packaway-pull-runner"));
                break;
            case "Cycle_Count":
                cycleCount.click();
                getWait(5).until(ExpectedConditions.urlContains("cyclecount"));
                break;
            case "Build_Pallet":
                buildPalletLink.click();
                getWait(5).until(ExpectedConditions.urlContains("buildPallet"));
                break;
            case "Split_Adjust_pallet":
                Split_Adjust_pallet_link.click();
                getWait(5).until(ExpectedConditions.urlContains("rebuildModifyPallet"));
                break;
            case "Exception_Lane":
                ExceptionLane.click();
                getWait().until(ExpectedConditions.urlContains("exceptionLane"));
                break;
            case "Dock_Scan" :
                dockScan.click();
                getWait(5).until(ExpectedConditions.urlContains("dockScan"));
                break;
            default:
                Assert.fail("Passed submenu value doesn't match, send correct value");
                break;
        }
    }

    @Override
    public void back() {
        backButton.click();
    }

    @Override
    public void exit() {
        exitButton.click();
    }
}
