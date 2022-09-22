package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.*;
import com.macys.mst.DC2.EndToEnd.pageobjects.supplychain.POInquiryPage;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import org.jbehave.core.annotations.Composite;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SupplychainBasicSteps {
    Home home = new Home();
    Menu menu=new Menu();
    SubMenu subMenu;
    ChildMenu childMenu;
    String selectedMainMenu;
    POInquiryPage pOInquiryPage = new POInquiryPage();


    @When("user click on supplychain main menu $mainMenu")
    public void clickOnMainMenu(String mainMenu) {
        Boolean isValid=false;
        selectedMainMenu = mainMenu;
        switch(mainMenu)
        {
            case "POInquiry":
                menu.getWait(40).until(ExpectedConditions.elementToBeClickable(menu.POInquiry));
                menu.POInquiry.click();
                try {
                    menu.getWait(40).until(ExpectedConditions.elementToBeClickable(pOInquiryPage.PO));
                }catch(Exception e){
                    menu.POInquiry.click();
                    menu.getWait().until(ExpectedConditions.elementToBeClickable(pOInquiryPage.PO));
                }
                menu.getWait().until(ExpectedConditions.urlContains("inquiry"));
                isValid=Menu.driver.getCurrentUrl().contains("inquiry");
                break;
            case "POLineItem":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.POLineItem));
                menu.POLineItem.click();
                menu.getWait().until(ExpectedConditions.urlContains("poLine"));
                isValid=Menu.driver.getCurrentUrl().contains("poLine");
                break;
            case "PODashboard":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.PODashboard));
                menu.PODashboard.click();
                menu.getWait().until(ExpectedConditions.urlContains("PoDashboard"));
                isValid=Menu.driver.getCurrentUrl().contains("PoDashboard");
                break;
            case "ASNReceipts":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.ASNReceipts));
                menu.ASNReceipts.click();
                menu.getWait().until(ExpectedConditions.urlContains("AsnReceipts"));
                isValid=Menu.driver.getCurrentUrl().contains("AsnReceipts");
                break;
            case "DCConfig":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.DCConfig));
                menu.DCConfig.click();
                menu.getWait().until(ExpectedConditions.urlContains("dcConfig"));
                isValid=Menu.driver.getCurrentUrl().contains("dcConfig");
                subMenu = new SupplychainSubMenu();
                childMenu=new SupplychainChildMenu();
                break;
            case "ResearchInventory":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.ResearchInventory));
                menu.ResearchInventory.click();
                subMenu = new SupplychainSubMenu();
                ((SupplychainSubMenu) subMenu).getWait().until(ExpectedConditions.elementToBeClickable(((SupplychainSubMenu) subMenu).ContainerInquiry  ));
                break;
            case "MHE":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.MHE));
                menu.MHE.click();
                subMenu = new SupplychainSubMenu();
                ((SupplychainSubMenu) subMenu).getWait().until(ExpectedConditions.elementToBeClickable(((SupplychainSubMenu) subMenu).MheSearch  ));
                break;
            case "Manifest":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.Manifest));
                menu.Manifest.click();
                menu.getWait().until(ExpectedConditions.urlContains("manifest"));
                isValid=Menu.driver.getCurrentUrl().contains("manifest");
                break;
            case "WSM":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.WSM));
                menu.WSM.click();
                subMenu = new SupplychainSubMenu();
                ((SupplychainSubMenu) subMenu).getWait().until(ExpectedConditions.elementToBeClickable(((SupplychainSubMenu) subMenu).ManageActivities  ));
                break;
            case "POManualReceipt":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.POManualReceipt));
                menu.POManualReceipt.click();
                menu.getWait().until(ExpectedConditions.urlContains("poManualReceipt"));
                isValid=Menu.driver.getCurrentUrl().contains("poManualReceipt");
                break;
            case "ASN Receipts":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.ASNReceipts));
                menu.ASNReceipts.click();
                menu.getWait().until(ExpectedConditions.urlContains("AsnReceipts"));
                isValid=Menu.driver.getCurrentUrl().contains("AsnReceipts");
                break;
            case "Wave":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.Wave));
                menu.Wave.click();
                subMenu = new SupplychainSubMenu();
                ((SupplychainSubMenu) subMenu).getWait().until(ExpectedConditions.elementToBeClickable(((SupplychainSubMenu) subMenu).WorkloadPlanning  ));
                break;
            case "SupportUI":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.SupportUI));
                menu.SupportUI.click();
                subMenu = new SupplychainSubMenu();
                ((SupplychainSubMenu) subMenu).getWait().until(ExpectedConditions.elementToBeClickable(((SupplychainSubMenu) subMenu).Diagnostics  ));
                break;
            case "NetworkMap":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.NetworkMap));
                menu.NetworkMap.click();
                break;
            default:
                Assert.fail("No such option available in main menu");

        }
        CommonUtils.doJbehavereportConsolelogAndAssertion("Main menu selected", selectedMainMenu, true);
    }

    @When("user click on supplychain sub menu $selectedSubMenu")
    public void clickOnSubMenu(String selectedSubMenu) {
        subMenu.clickOnGivenSubMenu(selectedSubMenu);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Submenu selected", selectedSubMenu, true);
    }

    @When("user click on supplychain child menu $selectedChildMenu")
    public void clickOnChildMenu(String selectedChildMenu) {
        childMenu.clickOnGivenChildMenu(selectedChildMenu);
        CommonUtils.doJbehavereportConsolelogAndAssertion("Childmenu selected", selectedChildMenu, true);
    }

    @Given("user signed in supplychain and selected $mainMenu")
    @Then("user signed in supplychain and selected $mainMenu")
    @Composite(steps = {"Given SupplyChain home page",
            "When user click on supplychain main menu $mainMenu"})
    public void loginAndSelectMainOption(String mainMenu) {

    }

    @Given("user logged in supplychain and selected $subMenu of $mainMenu")
    @Composite(steps = {"Given user signed in supplychain and selected $mainMenu",
            "When user click on supplychain sub menu $selectedSubMenu"})
    public void loginSelectMenuAndSubmenu(String selectedSubMenu, String mainMenu) {
    }

    @Given("user logged into supplychain and selected $childMenu in $subMenu of $mainMenu")
    @Composite(steps = {"Given user signed in supplychain and selected $mainMenu",
            "When user click on supplychain sub menu $selectedSubMenu",
            "When user click on supplychain child menu $selectedChildMenu"})
    public void loginSelectMenuAndSubmenuAndChildmenu(String selectedChildMenu,String selectedSubMenu, String mainMenu) {
    }
}
