package com.macys.mst.WMSLite.EndToEnd.stepdefinitions;


import com.macys.mst.WMSLite.EndToEnd.pageobjects.ChildMenu;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.Home;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.WmsLiteMenu;
import com.macys.mst.WMSLite.EndToEnd.pageobjects.WmsLiteSubMenu;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import org.jbehave.core.annotations.Composite;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class WmsLiteBasicSteps {
    public WebDriver driver = LocalDriverManager.getInstance().getDriver();
    Home home = new Home();
    WmsLiteMenu menu=new WmsLiteMenu(driver);

    WmsLiteSubMenu subMenu= new WmsLiteSubMenu();
    ChildMenu childMenu;
    String selectedMainMenu;
    String selectedWmsliteSubMenu;


    @Then("user click on wmslite $subMenu submenu of main menu $mainMenu")
    public void clickOnMainMenu(String subSelectedMenu,String mainMenu) throws InterruptedException {
        selectedMainMenu = mainMenu;
        selectedWmsliteSubMenu=subSelectedMenu;
        switch(mainMenu)
        {
            case "Receiving":
                menu.getWait(40).until(ExpectedConditions.elementToBeClickable(menu.Receiving));
                menu.Receiving.click();
                try {
                 //  menu.getWait(40).until(ExpectedConditions.elementToBeClickable(pOInquiryPage.PO));
                }catch(Exception e){
                    menu.Receiving.click();
                 //   menu.getWait().until(ExpectedConditions.elementToBeClickable(pOInquiryPage.PO));
                }
             //   menu.getWait().until(ExpectedConditions.urlContains("inquiry"));
             //   isValid=Menu.driver.getCurrentUrl().contains("inquiry");
                break;
            case "Picking":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.Picking));
                menu.Picking.click();
                break;
            case "Drivers":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.Drivers));
                menu.Drivers.click();
                break;
            case "ICQA":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.ICQA));
                menu.ICQA.click();
                break;
            case "Planner":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.Planner));
                boolean plannerDisplayed=menu.Planner.isDisplayed();
                Thread.sleep(3000);
                menu.Planner.click();
                StepDetail.addDetail("popup is displayed",plannerDisplayed);
                subMenu = new WmsLiteSubMenu();
                 ((WmsLiteSubMenu) subMenu).getWait().until(ExpectedConditions.elementToBeClickable(((WmsLiteSubMenu) subMenu).OrderSelectionPrinting  ));
                Thread.sleep(3000);
                if(selectedWmsliteSubMenu.equalsIgnoreCase("Order Selection & Printing")){
                    subMenu.OrderSelectionPrinting.click();
                }
                if(selectedWmsliteSubMenu.equalsIgnoreCase("Batch Inquiry")){
                    subMenu.BatchInquiry.click();
                }
                if(selectedWmsliteSubMenu.equalsIgnoreCase("Batch Detail Inquiry")){
                    subMenu.BatchDetailInquiry.click();
                }
                if(selectedWmsliteSubMenu.equalsIgnoreCase("Order Inquiry")){
                    subMenu.OrderInquiry.click();
                }
                if(selectedWmsliteSubMenu.equalsIgnoreCase("Order Detail Inquiry")){
                    subMenu.OrderDetailInquiry.click();
                }
                break;
            case "Admin":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.Admin));
                menu.Admin.click();
                break;
            case "Shipping":
                menu.getWait().until(ExpectedConditions.elementToBeClickable(menu.Shipping));
                menu.Shipping.click();
                break;

            default:
                Assert.fail("No such option available in main menu");

        }
        CommonUtils.doJbehavereportConsolelogAndAssertion("Main menu selected", selectedMainMenu, true);
    }


    @When("user click on wmslite sub menu $selectedSubMenu")
    public void clickOnSubMenu(String selectedSubMenu) throws InterruptedException {
        Thread.sleep(3000);

        subMenu.clickOnGivenSubMenu(selectedSubMenu);
      //  CommonUtils.doJbehavereportConsolelogAndAssertion("Submenu selected", selectedSubMenu, true);
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
