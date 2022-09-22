package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.pageobjects.LoadLane;
import com.macys.mst.DC2.EndToEnd.pageobjects.PrintTicketPage;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.ToContext;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.List;
@Slf4j
public class LoadLaneSteps {
	@Setter
    @Getter
    private List<String> toteIds;
	 public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
	 //PrintTicketPage printTicketPage = PageFactory.initElements(driver, PrintTicketPage.class);
	 
	 private StepsContext stepsContext;
	 public LoadLaneSteps(StepsContext stepsContext) {
			this.stepsContext = stepsContext;
		}
	@Given("User verify scan tote page")
	public void verifyScantote() {
		
	}
	//pass the location for each tote with comma seperated
	@When("user scans the tote and completes the load location with location(s) $locationBrcd")
	public void scantoteId(String locationBrcd) throws Exception {
	//	String tote = null;
		//CreateTotePage createTotePage = new CreateTotePage(driver);
		//createTotePage.navigateToCreateTote();
		//List<String> toteIds = new ArrayList<String>();
		//toteIds.add("50000001566972600163");
		//toteIds.add("50000001567489740112");
		//setToteIds(toteIds);
	
		PrintTicketPage printTicketPage = new PrintTicketPage(driver);
		printTicketPage.selectOptionFromMenu("LoadLocation");
		System.out.println("In load location");
		LoadLane LL = new LoadLane(driver);
		CreateToteSteps PTS = new CreateToteSteps(stepsContext);
		
		
		System.out.println("the list ias as "+PTS.gettote());
		stepsContext.put(Context.LANES.name(), locationBrcd, ToContext.RetentionLevel.SCENARIO);
		String[] locationList = locationBrcd.split(",");
		Assert.assertEquals(locationList.length, PTS.gettote().size());
		int[] numArr = {0};
		PTS.gettote().forEach(toteid -> {
			System.out.println("Inside the scan tote screen");
			System.out.println("toteid is as "+toteid);
			 try {
				LL.scanTote(toteid);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 System.out.println("Inside the scan Loaction screen");
			 try {
				LL.scanLocation(locationList[numArr[0]]);
				numArr[0]++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 System.out.println("Inside the scan QTY screen");
			 try {
				LL.scanCompleteQty();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 });
		
		LL.clickButton("EXIT");
	}
	@When("user clicks on $buttontype button")
	public void buttonclick(String button)
	{
		
	}
	@Given("user scans the $locationt location in scan location screen")
	public void scanlocation(String locationt) {
		
	}
	@Given("user scans $qty in scan qty screen")
	public void scanQtyScreen() {
		
	}


}
