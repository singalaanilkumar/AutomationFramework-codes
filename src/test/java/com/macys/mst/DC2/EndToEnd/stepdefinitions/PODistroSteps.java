package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODashboardReleasePage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PODistroPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.POInquiryUIPage;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.artemis.testNg.TestNGListener;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData;
import com.macys.mst.foundationalServices.StepDefinitions.CreatePO.PoLineBarCodeData.PoLinebarCode;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PODistroSteps {

	public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
	
	private POInquiryUIPage poInquiryUIPage = PageFactory.initElements(driver, POInquiryUIPage.class);
	private PODashboardReleasePage poDashboardReleasePage = PageFactory.initElements(driver, PODashboardReleasePage.class);

	PODistroPage poDistroPage = PageFactory.initElements(driver, PODistroPage.class);
	
	public long TestNGThreadID = Thread.currentThread().getId();

	private StepsContext stepsContext;
	BasePage basePage = new BasePage(driver);

	public PODistroSteps(StepsContext stepsContext) {
		this.stepsContext = stepsContext;
	}

	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String, String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
		TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}


	
	@Then("The $pagetitle page is displayed")
    public void checkPageTitle(String pagetitle){
        Assert.assertTrue(poDistroPage.getPageBreadcrumb().contains(pagetitle));
        
        
    }
	
		
	@When("PO distro UI is provided with receipt number in $page UI")
	public void clickFirstPONumber(String page) throws SQLException, Exception{
		List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
	 for (PoLinebarCode poLine : poLinebarCode) {
			 
			 String PO = poLine.getPoNbr();
		     String ReceiptNbr = poLine.getReceiptNbr();
		  
			 poDashboardReleasePage.loginSCM();
		     TimeUnit.SECONDS.sleep(20);
		     clickHomeNavOption(page);
			 poInquiryUIPage.clickClearButton();
			 poInquiryUIPage.typeIntoInputField("PO *",PO);
			 poInquiryUIPage.clickSearchButton();
		
			 log.info("Started to navigate to po Details page by clicking on Po");
			 TimeUnit.SECONDS.sleep(20);
			 poDistroPage.clickPO(PO);
			// clickPONumber(PO,ReceiptNbr);

			log.info("Started to validate Distro page by clicking on InHouseUpc");
		//	String skuUPC = poDistroPage.clickInHouseUpc();
		//	stepsContext.put(Context.SKU_INHOUSEUPC_BARCODE.name(), skuUPC, ToContext.RetentionLevel.SCENARIO);
			 clickInHouseUpc();
			 break;
			 
	 }
			 
		
			 

		

	}
	
	@Then("System fetches orignal distro details for PO recipt combination and quantities are validated against threshold")
	public void  validateDistroDetails() throws SQLException, Exception {
		
		List<PoLineBarCodeData.PoLinebarCode> poLinebarCode = (List<PoLineBarCodeData.PoLinebarCode>) stepsContext.get(Context.PO_LINES_BARCODE_DATA.name());
		//PO Distro Page Validation
		 for (PoLinebarCode poLine : poLinebarCode) {
			 String skuUpc = poLine.getSKU();
		 
		 poDistroPage.distroPageFieldValidation(skuUpc);
		 log.info("PO distro Page is validated");
		 break;
	/*  poDistroPage.navigatepodetails();
		 
	    clickInHouseUpc(skuUpc); */
		 
		 }
		 StepDetail.addDetail("PO distro Page is validated", true);
	
	}


	 @Then("Check grid is not empty")
	 public void isGridEmpty()    {
	        Assert.assertNotNull(poDistroPage.getGridContent());
	 }
	 
	 @Then("PO Distro header is displayed on the page")
	 public void isDistroHeaderDisplayed(){
	        Assert.assertTrue(poDistroPage.isHeaderDisplayed());
	 }

	 @Then("PO Distro HEADER is populated with values")
	 public void isHeaderCompleted() {
	        poDistroPage.isHeaderPopulated();
	    }
	 
	 public void clickInHouseUpc() throws InterruptedException{
			
			//	poDistroPage.clickFirstGridCell("In-House UPC");
				poDistroPage.clickInHouseUPCCell();
		        
		    }
	 
	 public void clickInHouseUpc(String SkuUpc) throws InterruptedException{
		 poDistroPage.clickInHouseUPCCell(SkuUpc);
	 }
	 
	 public void clickPONumber(String value1,String value2){
			poInquiryUIPage.clickGridCell("PO",value1,"Receipt #",value2);
		}
	 
	 public void clickHomeNavOption(String option) throws InterruptedException {
			Actions action = new Actions(driver);
			action.sendKeys(Keys.ESCAPE).build().perform();
			Assert.assertTrue(basePage.isNavMenuDisplayed());
			TimeUnit.SECONDS.sleep(20);
			basePage.clickNavOption(option);
		}
}
