package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.github.javaparser.utils.Log;
import com.macys.mst.DC2.EndToEnd.configuration.Context;
import com.macys.mst.DC2.EndToEnd.configuration.WsmEndpoint;
import com.macys.mst.DC2.EndToEnd.pageobjects.PickToCartonPage;
import com.macys.mst.DC2.EndToEnd.pageobjects.PrintTicketPage;
import com.macys.mst.artemis.testNg.LocalDriverManager;
import com.macys.mst.foundationalServices.utils.CommonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.context.StepsContext;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Random;

@Slf4j
public class PickToCartonSteps {

	@Setter
	@Getter
	private List<String> toteIds;
	public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
	// PrintTicketPage printTicketPage = PageFactory.initElements(driver,
	// PrintTicketPage.class);

	private StepsContext stepsContext;

	public PickToCartonSteps(StepsContext stepsContext) {
		this.stepsContext = stepsContext;
	}

	@Given("User verify scan tote page")
	public void verifyScantote() {

	}

	@When("user scans the location and carton and completes the pick to carton")
	public void scanLconAndCarton() throws Exception {

		PrintTicketPage printTicketPage = new PrintTicketPage(driver);
		printTicketPage.selectOptionFromMenu("Pick To Carton");
		System.out.println("In Pick To Carton");
		PickToCartonPage pp = new PickToCartonPage(driver);
		String lanes = stepsContext.get(Context.LANES.name()).toString();
		//String lanes ="KA01A009,KA01A010";
		Log.info("lanes = " + lanes);	
		String[] laneList = lanes.split(",");
		int stores = (int)stepsContext.get(Context.STORES.name());
		for(int i = 0; i < stores; i++){ //loop by store total number
			String lane = laneList[0];
			Log.info("lane = " + lane);	
			int cartonIdTmp = 100000000 + new Random().nextInt(900000000);
			String cartonId = "15000826543" + String.valueOf(cartonIdTmp);
			System.out.println("Carton ID is " + cartonId);
			pp.scanLocationActionId(lane);
			pp.scanCarton(cartonId);
			pp.scanFullQty();
			for(int j = 0; j < laneList.length - 1; j++){
				pp.scanLocationId();//scan lane on screen
				pp.scanFullQty();
			}
			pp.clickButton("CLOSE");
		}
		
		for(int i = 0; i < pp.activityIds.size(); i++){
			String activityId = pp.activityIds.get(i);
			//String wsmActivityToteEndPoint = "https://dev-backstage.devops.fds.com/wsm-service/wsm/7221/activities?id=" + activityId + "&status=COMPLETED";
			String wsmActivityToteEndPoint = String.format(WsmEndpoint.WSM_Activities_SEARCH_ID_STATUS, activityId, "COMPLETED");
			
			log.info("WSM ActivityToteEndpoint: {}" + wsmActivityToteEndPoint);
			CommonUtil.getRequestResponse(wsmActivityToteEndPoint); //will throw assert failure if result is 204
		}
		
		pp.clickButton("EXIT");
	}

	@When("user clicks on $buttontype button")
	public void buttonclick(String button) {

	}

	@Given("user scans the $locationId location in scan area screen")
	public void scanlocation(String locationId) {

	}

	@Given("user scans $carton in scan carton screen")
	public void scanCartonScreen() {

	}

	@Given("user scans $qty in scan units screen")
	public void scanQtyScreen() {

	}
}
