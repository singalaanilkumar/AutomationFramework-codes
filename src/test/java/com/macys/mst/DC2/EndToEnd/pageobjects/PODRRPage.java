package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.DC2.EndToEnd.configuration.OrderEndPoint;
import com.macys.mst.DC2.EndToEnd.configuration.PO4WallEndPoint;
import com.macys.mst.DC2.EndToEnd.db.app.DBMethods;
import com.macys.mst.DC2.EndToEnd.db.app.SQLPo4Walls;
import com.macys.mst.DC2.EndToEnd.db.app.SQLQueriesDRR;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.artemis.db.DBUtils;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;


@Slf4j
public class PODRRPage extends BasePage {

	private HashMap<String, HashMap<String, String>> globalLookupMap;

	CommonUtils commonUtils = new CommonUtils();

	public String reportingResponse = null;
	@FindBy(xpath = "//*[@id='username']")
	WebElement userName;

	@FindBy(xpath = "//*[@id='password']")
	WebElement password;

	@FindBy(xpath = "//*[@type='submit']")
	WebElement submitButton;

	@FindBy(xpath = "//span[contains(text(),'PO Dashboard')]")
	WebElement poDashboard;

	@FindBy(xpath = "//span[contains(text(),'PO Inquiry')]")
	WebElement poInquiry;

	@FindBy(xpath = "//*[@id='inhouseUpc']")
	List<WebElement> skuValList;

	@FindBy(xpath = "//*[@id='breadcrumbContainer']/div/button")
	WebElement breadcrumb;		

	@FindBy(xpath = "//button[(@id = 'clearButton')]")
	WebElement clearButton;

	@FindBy(id = "gridSection")
	WebElement filterResultTable;

	@FindBy(xpath = "//div[@role='button']/div/span[strong[contains(text(),'In-House UPC:')]]")
	List<WebElement> skuHeaderList;

	@FindBy(xpath = ".//div[contains(@ref, 'gridPanel')]")
	WebElement gridPanel;


	@FindBy(xpath=".//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[1]/div[2]/div/div")
	WebElement gridHeader1;

	private By gridHeader =By.xpath(".//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[1]/div[2]/div/div");

	@FindBy(xpath = "//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[3]/div[2]/div/div/div")
	WebElement gridRow1;

	private By gridRows = By.xpath("//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[3]/div[2]/div/div/div");

	@FindBy(xpath = ".//div[contains(@role, 'gridcell') and contains(@class, 'ag-cell-value")
	WebElement cellLocator;

	@FindBy(xpath = ".//div[contains(@ref, 'eOverlay')]/div/div/span[contains(text(), 'No Rows To Show')]")
	WebElement noRowsLocator;

	@FindBy(xpath = "//div[(@id='poDistroHdr')]/div")
	public List<WebElement> poDistroHeaderValues;


	@FindBy(id = "gridToolbarContainer")
	WebElement  gridToolbar;

	@FindBy(xpath = "//*[@id='searchBox']")
	private WebElement searchBox;

	@FindBy(xpath = "//*[contains(@id,'select')]")
	private WebElement selectDropdown;

	@FindBy(xpath = "div[(@col-id='oscUnits')]//div[@class='div-percent-bar']")
	private WebElement oscUnits;

	@FindBy(xpath = "//button[(@ref='btNext')]")
	private WebElement btNextButton;

	private By searchByInputs = By.xpath(".//*[@id='searchBox']/div/div/div[2]/div/div/div/div");

	@FindBy(xpath = "//button[(@id = 'searchButton')]")
	WebElement searchButton;

	@FindBy(xpath = "//input[@name='rcptNbr']")
	WebElement rcptNbrValue;

	@FindBy(xpath = "//input[@name='vendor']")
	WebElement vendorValue;

	@FindBy(xpath = "//input[@name='deptNbr']")
	WebElement deptNbrValue;

	@FindBy(xpath = "//input[@name='poNbr']")
	WebElement poNbrValue;

	@FindBy(xpath = "//div[(@col-id='inhouseUpc')]")
	WebElement inHouseUpcValue;


	@FindBy(xpath = "//div[(@id='drrHeader')]")
	WebElement breadCrumbElement;

	@FindBy(xpath = "//*[@id='headerRefreshContainer']")
	WebElement refreshElement;

	@FindBy(xpath = "//*[@id='print-content']/button/span[2]")
	WebElement printElement;

	//div[@id='headerPrintContainer']

	@FindBy(xpath = "//*[@id='headerPrintContainer']/div/button/span/img")
	WebElement printElementImg;

	@FindBy(xpath = "//*[@id='headerRefreshContainer']/button/span/img")
	WebElement refreshElementImg;

	@FindBy(xpath = "//*[@id='searchBox']")
	WebElement headerPath;

	@FindBy(xpath = "//*[@id='barCodeSection']/div[p[text()='Report Id']]")
	WebElement reportIDBarCodeElement;

	@FindBy(xpath = "//*[@id='barCodeSection']/div[2]")
	WebElement dateElement;

	@FindBy(xpath = "//*[@id='barCodeSection']/div[p[text()='Po']]")
	WebElement poNbrBarCodeElement;

	@FindBy(xpath = "//*[@id='globalAttributes']")
	private List<WebElement> searchByLineItem;


	@FindBy(xpath = "//*[@id='print-content']")
	WebElement pdfElement;

	@FindBy(xpath = "//*[contains(text(),'EDIT')]")
	WebElement EditButton;

	String selectedTxt="";
	String selectedPrepTxt="";

	public void selectPOInquiry() {
		getWait(25).until(visibilityOf(poInquiry));
		poInquiry.click();
		log.info("PO Inquiry is selected");
	}	

	public void clickSearchButton(){
		try {
			getWait(10).until(visibilityOf(searchButton));
			searchButton.click();
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> getPageBreadcrumb() {
		getWait(10).until(visibilityOf(breadcrumb));
		List<String> displayedButtons = new ArrayList<>();
		List<WebElement> breadcrumbTextList = driver.findElements((By) breadcrumb);
		for (WebElement item : breadcrumbTextList)
			displayedButtons.add(item.getText());
		return displayedButtons;
	}

	public void searchDashboard(String searchCriteria, String Option) {
		try {
			Thread.sleep(3000);
			getWait(35).until(visibilityOf(clearButton));
			clearButton.click();
			log.info("search Dashboard");
			if (searchCriteria.equalsIgnoreCase("PO *"))
				typeIntoInputField(searchCriteria, Option);
			else
				setOptionInDropdownnew(searchCriteria, Option);
			getWait(25).until(visibilityOf(searchButton));
			searchButton.click();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void typeIntoInputField(String fieldName, String input) {
		log.info("Entering typeIntoInputField ");
		List<WebElement> searchInputsLocators = driver.findElements(searchByInputs);
		for (WebElement inputField : searchInputsLocators) {
			try {
				String pathToLabel;
				if (fieldName.equals("Department")) {
					pathToLabel = "div/label";
				} else {
					pathToLabel = "label";
				}
				WebElement inputLabel = inputField.findElement(By.xpath(pathToLabel));
				if (inputLabel.getText().equals(fieldName)) {
					log.info("Entering data into field {} ", inputLabel.getText());
					WebElement inputFieldLocator = inputField
							.findElement(By.xpath(".//label[text()='" + fieldName + "']/following-sibling::div/input"));
					inputFieldLocator.click();
					inputFieldLocator.clear();
					inputFieldLocator.sendKeys(input);
					break;
				}
			} catch (Exception ignored) {
			}
		}
	}

	public void setOptionInDropdownnew(String dropDownName, String option) {
		List<WebElement> searchInputsLocators = driver.findElements((By) searchByInputs);
		for (WebElement inputField : searchInputsLocators) {
			try {
				WebElement inputLabel = inputField.findElement(By.xpath("label"));
				if (inputLabel.getText().equals(dropDownName)) {
					getWait(5).until(visibilityOf(selectDropdown));
					selectDropdown.click();
					log.info("Selecting option {}" + option);
					List<WebElement> options = driver.findElements(By.xpath("//*[@id='menu-areaFlow']//li"));
					for (WebElement option1 : options) {
						String optionText = option1.getAttribute("data-value");
						if (option.equals(optionText)) {
							option1.click();
							break;
						}
					}
					/*
					 * Actions action = new Actions(driver);
					 * action.sendKeys(Keys.ESCAPE).build().perform();
					 */
					break;
				}
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}
	}

	public boolean isSearchResultTableDisplayed() {
		try {
			getWait(5).until(visibilityOf(filterResultTable));
			if (filterResultTable.isDisplayed()) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public String clickReportID() {
		return clickFirstGridCell("Report ID");
	}
	
	public void clickReportID(String reportID) {
		try{
			if(EditButton.isDisplayed()){
			}
		}catch(Exception e) {
			getWait(30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@col-id='reportId']//*[text()='" + reportID + "']")));
			driver.findElement(By.xpath("//*[@col-id='reportId']//*[text()='" + reportID + "']")).click();
		}
	}

	public String clickFirstGridCell(String columnName) {

		log.info("Started to validate Receipt number based on Po");
		getWait(35).until(visibilityOf(gridHeader1));
		gridPanel.click();

		log.info("Clicked gridPanel");
		//scrollElementIntoView(gridToolbar);

		WebElement gridHeaderLocator = driver.findElement(gridHeader);
		List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("div"));
		List<WebElement> gridRowsLocator = driver.findElements(gridRows);
		List<String> columnsList = new ArrayList<String>();

		boolean found = false;
		for (WebElement column : headerList) {
			columnsList.add(column.getText());
			if (column.getText().equals(columnName)) {
				found = true;
				break;
			}
		}

		if (!found) {
			scrollCellBeginningOfGrid(headerList);
			boolean endOfList = false;
			while (!endOfList) {
				headerList = gridHeaderLocator.findElements(By.xpath("div"));

				while (!columnsList.get(columnsList.size() - 2).equals(headerList.get(headerList.size() - 3).getText())) {
					columnsList.add(headerList.get(headerList.size() - 3).getText());
					if (headerList.get(headerList.size() - 3).getText().equals(columnName)) {
						endOfList = true;
						break;
					}
					scrollCellMidOfGrid(headerList);

					headerList = gridHeaderLocator.findElements(By.xpath("div"));
					if (columnsList.get(columnsList.size() - 1).equals(headerList.get(headerList.size() - 2).getText())) {
						if (headerList.get(headerList.size() - 1).getText().equals(columnName)) {
							endOfList = true;
							break;
						}
						endOfList = true;
						break;
					}
				}
			}
		}

		for (WebElement column : headerList) {
			if (column.getText().equalsIgnoreCase(columnName)) {
				String colID = getColID(column.getText());

				WebElement cellContent = gridRowsLocator.get(0).findElement(By.xpath("div[(@col-id='" + colID + "')]/div/button"));


				if(columnName.equalsIgnoreCase("Report ID")) {
					String cellContentText = cellContent.getText().toString();

					cellContent.click();

					return cellContentText;

				}

				cellContent.click();
				log.info("Selected Column", colID);


				break;
			}
		}
		return null;

	}

	private void scrollCellMidOfGrid(List<WebElement> headerList) {
		if (isElementDisplayed(cellLocator)) {
			@SuppressWarnings("unchecked")
			List<WebElement> gridContainer = (List<WebElement>) cellLocator;
			int index = 0;
			for (int i = headerList.size()-1; i >= 0; i--) {
				WebElement col = headerList.get(i).findElement(By.xpath("div[2]/div/span[1]"));
				if(!col.getText().equals("")){
					index = i;
					break;
				}
			}
			WebElement gridCell = gridContainer.get(index);
			gridCell.click();
			gridCell.sendKeys(Keys.ARROW_RIGHT);
		} else if (isElementDisplayed(noRowsLocator)) {
			WebElement singleCell = driver.findElement(By.xpath(".//div[contains(@ref, 'eBodyContainer')]"));
			Actions action = new Actions(driver);
			action.click(singleCell);
			for (int i = 0; i <= 3; i++) {
				action.sendKeys(Keys.ARROW_RIGHT);
			}
		}
	}

	private void scrollCellBeginningOfGrid(List<WebElement> headerList) {
		if (isElementDisplayed(cellLocator)) {
			@SuppressWarnings("unchecked")
			List<WebElement> gridContainer = (List<WebElement>) cellLocator;
			int index = 0;
			for (int i=0; i < headerList.size(); i++) {
				WebElement col = headerList.get(i).findElement(By.xpath("div[2]/div/span[1]"));
				if(col.getText().equals("")){
					index = i;
					break;
				}
			}
			WebElement gridCell = gridContainer.get(index - 1);
			gridCell.click();
			gridCell.sendKeys(Keys.ARROW_RIGHT);
		} else if (isElementDisplayed(noRowsLocator)) {
			WebElement singleCell = driver.findElement(By.id("gridSection"));
			Actions action = new Actions(driver);
			action.click(singleCell);
			action.sendKeys(Keys.ARROW_RIGHT);
			action.sendKeys(Keys.ARROW_RIGHT);
		}
	}

	protected Boolean isElementDisplayed(WebElement locator) {
		try {
			Thread.sleep(1000);
			return locator.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}



	public void scrollElementIntoView(WebElement locator) {

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView(true);", locator);
	}

	private String getColID(String columnName) {
		String colID = null;
		switch (columnName) {
		case "Report ID":
			colID = "reportId";
			break;
		case "Appt #":
			colID = "apptNo";
			break;
		case "Appt Date":
			colID = "apptDate";
			break;
		case "Receipt #":
			colID = "receiptNbr";
			break;
		case "PO":
			colID = "poNbr";
			break;
		case "Gen Type":
			colID = "genType";
			break;
		case "Appt Status":
			colID = "apptStatDesc";
			break;
		case "Exp. Cartons":
			colID = "expCartons";
			break;
		case "Act. Cartons":
			colID = "actCartons";
			break;
		case "Exp. Units":
			colID = "expUnits";
			break;
		case "Act. Units":
			colID = "actUnits";
			break;
		case "UPC Count":
			colID = "upcCount";
			break;
		case "Allocation Status":
			colID = "allocationStatus";
			break;
		case "Distro":
			colID = "distro";
			break;
		case "Process Area":
			colID = "processFlow";
			break;
		case "Multiple Appts":
			colID = "multipleApts";
			break;
		case "Multiple Receiving DCs":
			colID = "multipleRcvDcs";
			break;
		case "New Store":
			colID = "newStore";
			break;
		case "PO Status":
			colID = "poStatus";
			break;
		case "In-House UPC":
			colID = "inhouseUpc";
			break;
		case "MKSTYL":
			colID = "mkStyl";
			break;
		case "Exp Units":
			colID = "expectedUnits";
			break;
		case "Act Units":
			colID = "actualUnits";
			break;
		case "RCV":
			colID = "rcv";
			break;
		}
		return colID;
	}

	/*	public String clickInHouseUpc(){
        return clickFirstGridCell("In-House UPC");

    }
	 */
	public ArrayList<LinkedHashMap<String, String>> getGridContent() {
		log.info("Get Grid Content");
		getWait(25).until(visibilityOf(gridHeader1));
		ArrayList<LinkedHashMap<String, String>> gridDetails = new ArrayList<>();
		//driver.findElement(By.xpath(".//div[contains(@ref, 'gridPanel')]")).click();

		//scrollElementIntoView(gridToolbar);

		WebElement gridHeaderLocator = driver.findElement(gridHeader);
		List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("div/div/div/span[1]"));

		List<WebElement> gridRowsLocator = driver.findElements(gridRows);

		for (WebElement row : gridRowsLocator) {
			LinkedHashMap<String, String> rowData = new LinkedHashMap<>();

			for (WebElement column : headerList) {
				int colIterator = headerList.indexOf(column) + 1;
				WebElement cellContent = row.findElement(By.xpath("div[" + colIterator + "]"));
				if (headerList.indexOf(column) == 0 && !cellContent.getText().equals("")) {
					rowData.put("Row #", row.getAttribute("row-index"));
					rowData.put(column.getAttribute("innerText"), cellContent.getText());
				} else if (!cellContent.getText().equals("")) {
					rowData.put(column.getAttribute("innerText"), cellContent.getText());
				} else {
					rowData.put(column.getAttribute("innerText"), "");
				}

			}
			gridDetails.add(rowData);
		}
		return gridDetails;
	}

	public boolean isHeaderDisplayed()	    {
		return driver.findElement(By.id("poDistroHdr")).isDisplayed();
	}

	public boolean isHeaderPopulated()  {
		Map<String, String> poHeaderMap = getPOHeader();
		Set set = poHeaderMap.entrySet();
		Iterator i = set.iterator();
		while (i.hasNext()) {
			Map.Entry header = (Map.Entry) i.next();
			if (header.getValue().equals("")) return false;
		}
		return true;
	}

	public Map<String, String> getPOHeader() {
		Map<String, String> poHeaderMap = new HashMap<>();
		for(WebElement item : poDistroHeaderValues){
			poHeaderMap.put(item.findElement(By.xpath("label")).getText(), item.findElement(By.xpath("div/input")).getAttribute("value"));
		}
		return new TreeMap<>(poHeaderMap);
	}

	public void validateBreadCrumbsinDRR() {
		try {
			Thread.sleep(3000);
			log.info("reportpage");
			Assert.assertEquals("Home element not displayed on BreadCrumbs","Home", breadCrumbElement.findElement(By.xpath("/div/button[1]")).getText());
			Assert.assertEquals("'PO Inquiry' element not displayed on BreadCrumbs","PO Inquiry", breadCrumbElement.findElement(By.xpath("/div/button[2]")).getText());
			Assert.assertEquals("'Detail Receiving Report' element not displayed on BreadCrumbs","Detail Receiving Report", breadCrumbElement.findElement(By.xpath("/div/button[3]")).getText());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public HashMap<String,String> getPOHeaderMapFromAPITrans(String reportID) throws JSONException {

		HashMap<String,String> poHeaderElementsFromAPI = new HashMap<String,String>();
		ArrayList<String> attributesList = new ArrayList<String>();

		String drrResponse = CommonUtils.getRequestResponse(OrderEndPoint.ORDER_SERVICE_DRR_PO_DETAIL_HEADER + reportID); 
		log.info("DRR service response {}", drrResponse);

		if(drrResponse.isEmpty()){
			Assert.assertTrue(false,"Unable to access Reporting API for reportID "+reportID);
		}			

		Map<String, String> servicePOHeaderElements = CommonUtils.getMapFromJson(new JSONObject(drrResponse).getJSONObject("poDetail").getJSONObject("poDetailHdr").toString());

		//getMapFromJson(new JSONObject(drrResponse).getJSONObject("poDetail").getJSONObject("poDetailHdr").toString());
		attributesList = new ArrayList<>(Arrays.asList("rcptNbr","wkcnt", "deptNbr", "orderProfile", "keyRec", "poNbr", "vendor", "newStore", "poType","distro","cdp"));
		for(String attributeLabel : attributesList){
			MutablePair<String, String> poHeaderDetail = getTranslatedValueforPOHeaderAPI(attributeLabel, String.valueOf(servicePOHeaderElements.get(attributeLabel)));
			poHeaderElementsFromAPI.put(poHeaderDetail.getKey(),poHeaderDetail.getValue());
		}

		ArrayList<String> skuList = getSKUListfromUI(reportID);
		int skuCount = skuList.size();
		if (skuCount<2) {
			String postatusResponse = CommonUtils.getRequestResponse(OrderEndPoint.ORDER_SERVICE_DRR_PO_STATUS+reportID);
			log.info(postatusResponse);
			if (!(postatusResponse.equals(null)))

			{
				MutablePair<String, String> poStatus = getTranslatedValueforPOHeaderAPI("poStatus", String.valueOf(new JSONObject(postatusResponse).getJSONArray("poInquiry").getJSONObject(0).getString("poStatus")));
				poHeaderElementsFromAPI.put(poStatus.getKey(),poStatus.getValue());
			}
			else {
				log.info("postatusResponse response is null");
			}
		}

		/*		String receiptNbr = servicePOHeaderElements.get("rcptNbr").toString();
		log.info(receiptNbr);*/
		/*	String poAptResponse = CommonUtils.getRequestResponse(OrderEndPoint.ORDER_SERVICE_DRR_APPTNBR+receiptNbr);
		if (!(poAptResponse==null))
		{
			//	RestAssured.given().get(hostName+"/msp-whm-shipment-service/shipment/7221/appt-stat?rcptNbr="+servicePOHeaderElements.get("rcptNbr")).body().asString();
		MutablePair<String, String> apptNbr = getTranslatedValueforPOHeaderAPI("apptNbr", String.valueOf(new JSONObject(poAptResponse).getJSONArray("ShipAppts").getJSONObject(1).getLong("apptNbr")));
		returnLineItemElements.put(apptNbr.getKey(),apptNbr.getValue());
		}
		else {
			log.info("poAptResponse response is null");
		} */

		return poHeaderElementsFromAPI;	
	}
	public MutablePair<String,String> getTranslatedValueforPOHeaderAPI(String apiLabel,String value){

		MutablePair<String,String> translatedLabelValue = null;

		try {
			switch (apiLabel) {
			case "orderProfile": 
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Order Profile","");
				else
					translatedLabelValue = new MutablePair<String,String>("Order Profile",value);
				break;
			case "distro":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Distro","");
				else if(value.equals("N") || value.equals("No"))
					translatedLabelValue = new MutablePair<String,String>("Distro","No");
				else if(value.equals("Y") || value.equals("Yes"))
					translatedLabelValue = new MutablePair<String,String>("Distro","Yes");
				break;
			case "rcptNbr":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Rcpt Nbr","");
				else
					translatedLabelValue = new MutablePair<String,String>("Rcpt Nbr",value);
				break;
			case "poNbr":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("PO Number","");
				else
					translatedLabelValue = new MutablePair<String,String>("PO Number",value);
				break;
			case "wkcnt":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Wkcnt","");
				else
					translatedLabelValue = new MutablePair<String,String>("Wkcnt",value);
				break;
			case "newStore":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("New Store","");
				else
					translatedLabelValue = new MutablePair<String,String>("New Store",value);
				break;
			case "cdp":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("CDP","");
				else if(value.equals("N") || value.equals("No"))
					translatedLabelValue = new MutablePair<String,String>("CDP","No");
				else if(value.equals("Y") || value.equals("Yes"))
					translatedLabelValue = new MutablePair<String,String>("CDP","Yes");
				break;	
			case "keyRec":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Key Rec","");
				else
					translatedLabelValue = new MutablePair<String,String>("Key Rec",value);
				break;
			case "deptNbr":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Dept","");
				else
					translatedLabelValue = new MutablePair<String,String>("Dept",value);
				break;
			case "vendor":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Vendor","");
				else
					translatedLabelValue = new MutablePair<String,String>("Vendor",value);
				break;
			case "poType":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("PO Type","");
				else
					translatedLabelValue = new MutablePair<String,String>("PO Type",value);
				break;
			case "poStatus":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("PO Status","");
				else
					translatedLabelValue = new MutablePair<String,String>("PO Status",value);
				break;
			case "apptNbr":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Appt #","");
				else
					translatedLabelValue = new MutablePair<String,String>("Appt #",value);
				break;
			default :
				translatedLabelValue = new MutablePair<String,String>(apiLabel,value);
				break;
			}
		} catch (Exception e) {
			log.info("Unable to convert Label: "+apiLabel+".Error: "+e.getMessage());
		}
		return translatedLabelValue;
	}

	public HashMap<String, String> getPOHeaderMapFromScreen(){
		String headerpath = "//*[@id='searchBox']";
		HashMap<String,String> screenMap = new HashMap<String,String>();
		List<WebElement> allElements = driver.findElements(By.xpath(headerpath));
		String val = null;
		String label = null;
		int size = allElements.size();

		for (int i = 0; i<size; i++) {
			int tempsize = allElements.get(i).findElements(By.xpath(".//label")).size();
			for (int j = 0; j < tempsize; j++) {
				label = allElements.get(i).findElements(By.xpath(".//label")).get(j).getText();
				if (allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName().equalsIgnoreCase("input")) {
					val = allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getAttribute("value");
				} else if (allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName().equalsIgnoreCase("select")) {
					Select select = new Select(allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j));
					WebElement option = select.getFirstSelectedOption();
					val = option.getText();
				}
				if (val != null && label != null) {
					screenMap.put(label, String.valueOf(val));

				}

			}   


		}



		return screenMap;	
	}

	public void clickButton(String button, String skuNumber) {
		switch(button) {
		case "Expand":
			List<WebElement> sectionHeaderList = driver.findElements(By.xpath("//div[div/span[strong[contains(text(),'In-House UPC:')]]]"));
			List<WebElement> sectionHeaderButtonList = driver.findElements(By.xpath("//div[div/span[strong[contains(text(),'In-House UPC:')]]]//div[@role='button']"));	
			for(int i = 0; i < skuHeaderList.size(); i++) {
				String skuvalues = skuHeaderList.get(i).getText();
				WebElement sectionHeader = sectionHeaderList.get(i);
				if(sectionHeader.getAttribute("aria-expanded").equals("true") && !(skuvalues.contains(String.valueOf(skuNumber)))){
					/*JavascriptExecutor js = (JavascriptExecutor) driver;
				 js.executeScript("arguments[0].scrollIntoView();", sectionHeaderButtonList.get(i));
				 js.executeScript("window.scrollBy(0,-300)");
				 String test = sectionHeader.getText();*/
					sectionHeaderButtonList.get(i).click();
				} 
			}
			for(int i = 0; i < skuHeaderList.size(); i++) {
				String skuvalues = skuHeaderList.get(i).getText();
				WebElement sectionHeader = sectionHeaderList.get(i);
				if(skuvalues.contains(String.valueOf(skuNumber))){
					if(sectionHeader.getAttribute("aria-expanded").equals("false") && (skuvalues.contains(String.valueOf(skuNumber)))){
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("arguments[0].scrollIntoView();", sectionHeaderButtonList.get(i));
						js.executeScript("window.scrollBy(0,-300)");
						sectionHeaderButtonList.get(i).click();
						js.executeScript("arguments[0].scrollIntoView();", sectionHeaderButtonList.get(i));
						js.executeScript("window.scrollBy(0,-300)");
					} 
					break;
				}	
			}
			break;
		case "Edit":
			List<WebElement> editButtonList = driver.findElements(By.xpath("//*[contains(text(),'EDIT')]"));	
			List<WebElement> headerSKUList = driver.findElements(By.xpath("//*[*[contains(text(),'In-House UPC:')]]"));
			for(int i = 0; i < skuValList.size(); i++) {
				if(skuValList.get(i).getAttribute("value").equalsIgnoreCase(skuNumber)) {
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("arguments[0].scrollIntoView();", headerSKUList.get(i));
					js.executeScript("window.scrollBy(0,-250)");
					editButtonList.get(i).click(); 
					break;
				}				 
			}			 		    		        
			break;
		case "Override":
			List<WebElement> overrideThresholdList = driver.findElements(By.xpath("//*[contains(text(),'OVERRIDE THRESHOLD')]"));
			for(int i = 0; i < skuValList.size(); i++) {
				if(skuValList.get(i).getAttribute("value").equalsIgnoreCase(skuNumber)) {
					overrideThresholdList.get(i).click(); 
					break;
				}				 
			}	
			break;	
		case "Save":
			List<WebElement> saveButtonList1 = driver.findElements(By.xpath("//*[*[contains(text(),'SAVE')]]"));
			headerSKUList = driver.findElements(By.xpath("//*[*[contains(text(),'In-House UPC:')]]"));
			for(int i = 0; i < skuValList.size(); i++) {
				if(skuValList.get(i).getAttribute("value").equalsIgnoreCase(skuNumber)){
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("arguments[0].scrollIntoView();", headerSKUList.get(i));
					js.executeScript("window.scrollBy(0,-250)");
					saveButtonList1.get(i).click(); 
				}
			}
			break;	
		}
	}



	public HashMap<String,String> getPOLineItemMapFromAPITrans(String SkuNbr,String reportID,String attributeType) throws JSONException {

		Map<String, String> servicePOLineItemElements = new HashMap<String,String>();
		HashMap<String,String> returnLineItemElements = new HashMap<String,String>();
		ArrayList<String> attributesList = new ArrayList<String>();

		String drrResponse = CommonUtils.getRequestResponse(OrderEndPoint.ORDER_SERVICE_DRR_PO_DETAIL_HEADER + reportID); 

		if(drrResponse == null){
			Assert.assertTrue(false, "Unable to access Reporting API for reportID "+reportID);
		}			

		JSONArray poDetailArray =  new JSONObject(drrResponse).getJSONObject("poDetail").getJSONArray("poLineDetails");
		for(int i = 0; i < poDetailArray.length(); i++) {
			servicePOLineItemElements = CommonUtils.getMapFromJson(String.valueOf(poDetailArray.getJSONObject(i)));
			log.info("poline" +servicePOLineItemElements);

			if(servicePOLineItemElements.get("inhouseUpc").equalsIgnoreCase(SkuNbr)){break;}
		}

		if(StringUtils.compare(attributeType, "editableMinusPrep")==0){
			attributesList = new ArrayList<>(Arrays.asList("processAreaDesc","caseAssortmentDesc","caseType","innerPack","productCube","fragile",""
					+ "ticketType","noOfTickets","hazmat","packawayStorageAttribute"));
			for(String attributeLabel : attributesList){
				MutablePair<String, String> test = getTranslatedValueforAPIEditableAttributes(attributeLabel, String.valueOf(servicePOLineItemElements.get(attributeLabel)));
				returnLineItemElements.put(test.getKey(),StringUtils.stripToEmpty(test.getValue()));
				if(StringUtils.compare(test.getKey(),"Pack")==0){
					returnLineItemElements.put("Pack QTY","0");
				}
			}		
		}else if(StringUtils.compare(attributeType, "noneditable")==0){
			attributesList = new ArrayList<>(Arrays.asList("inhouseUpc","tktRetail","mkStyl","vndStyle","pid","pidDesc","colDesc","sizeDesc",
					"compareRetail","expectedUnits","actualUnits","ticketStatus","processAreaDesc"));
			for(String attributeLabel : attributesList){
				MutablePair<String, String> test = getTranslatedValueforAPINonEditableAttributes(attributeLabel, String.valueOf(servicePOLineItemElements.get(attributeLabel)));
				returnLineItemElements.put(test.getKey(),StringUtils.stripToEmpty(test.getValue()));
			}		
		}else if(StringUtils.compare(attributeType, "header")==0){
			attributesList = new ArrayList<>(Arrays.asList("inhouseUpc","vndStyle","pid","pidDesc"));
			for(String attributeLabel : attributesList){
				MutablePair<String, String> test = getTranslatedValueforAPIHeaderAttributes(attributeLabel, String.valueOf(servicePOLineItemElements.get(attributeLabel)));
				returnLineItemElements.put(test.getKey(),StringUtils.stripToEmpty(test.getValue()));
			}		
		}

		return returnLineItemElements;	
	}

	public MutablePair<String,String> getTranslatedValueforAPIEditableAttributes(String apiLabel,String value){

		MutablePair<String,String> translatedLabelValue = null;

		try {
			switch (apiLabel) {
			case "innerPack":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Pack","No");
				else if(value.equals("N") || value.equals("No"))
					translatedLabelValue = new MutablePair<String,String>("Pack","No");
				else if(value.equals("Y") || value.equals("Yes"))
					translatedLabelValue = new MutablePair<String,String>("Pack","Yes");
				break;
			case "noOfTickets":  
				translatedLabelValue = new MutablePair<String,String>("# of Tickets",value);
				break;
			case "caseType":  
				translatedLabelValue = new MutablePair<String,String>("Case Type",value);
				break;
			case "productCube":  
				translatedLabelValue = new MutablePair<String,String>("Product Cube",value);
				break;
			case "fragile":  
				translatedLabelValue = new MutablePair<String,String>("Fragile Y/N",value);
				break;
			case "caseAssortmentDesc":  
				translatedLabelValue = new MutablePair<String,String>("Case Assortment",value);
				break;
			case "innerPackQty":  
				translatedLabelValue = new MutablePair<String,String>("Pack QTY",value);
				break;
			case "hazmat":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("HAZMAT","No");
				else if(value.equals("N") || value.equals("No"))
					translatedLabelValue = new MutablePair<String,String>("HAZMAT","No");
				else if(value.equals("Y") || value.equals("Yes"))
					translatedLabelValue = new MutablePair<String,String>("HAZMAT","Yes");
				break;
			case "processAreaDesc":  
				translatedLabelValue = new MutablePair<String,String>("Process Area Conf",value);
				break;
			case "ticketType": 
				translatedLabelValue = new MutablePair<String,String>("Ticket Type",value);
				break;
			case "packawayStorageAttribute": 
				translatedLabelValue = new MutablePair<String,String>("Packaway Storage Attribute",value);
				break;
			default :
				translatedLabelValue = new MutablePair<String,String>(apiLabel,value);
				break;
			}
		} catch (Exception e) {
			log.info("Unable to convert Label: "+apiLabel+".Error: "+e.getMessage());
		}
		return translatedLabelValue;
	}	

	public MutablePair<String,String> getTranslatedValueforAPINonEditableAttributes(String apiLabel,String value){

		MutablePair<String,String> translatedLabelValue = null;

		try {
			switch (apiLabel) {
			case "inhouseUpc": 
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("In-House UPC","");
				else
					translatedLabelValue = new MutablePair<String,String>("In-House UPC",value);
				break;
			case "mkStyl":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("MKSTYL","");
				else
					translatedLabelValue = new MutablePair<String,String>("MKSTYL",value);
				break;
			case "vndStyle":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("VNDSTYL","");
				else
					translatedLabelValue = new MutablePair<String,String>("VNDSTYL",value);
				break;
			case "pid":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("PID","");
				else
					translatedLabelValue = new MutablePair<String,String>("PID",value);
				break;
			case "pidDesc":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("PID Description","");
				else
					translatedLabelValue = new MutablePair<String,String>("PID Description",value);
				break;
			case "colDesc":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Color Description","");
				else
					translatedLabelValue = new MutablePair<String,String>("Color Description",value);
				break;
			case "sizeDesc":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Size Description","");
				else
					translatedLabelValue = new MutablePair<String,String>("Size Description",value);
				break;
			case "compareRetail":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Compare @ Retail","0");
				else
					translatedLabelValue = new MutablePair<String,String>("Compare @ Retail",value);
				break;
			case "tktRetail":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("TKT Retail","0");
				else
					translatedLabelValue = new MutablePair<String,String>("TKT Retail",value);
				break;
			case "expectedUnits":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Expected Unit QTY","0");
				else
					translatedLabelValue = new MutablePair<String,String>("Expected Unit QTY",value);
				break;
			case "actualUnits":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Actual Unit QTY","0");
				else
					translatedLabelValue = new MutablePair<String,String>("Actual Unit QTY",value);
				break;
			case "ticketStatus":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Ticket Status","");
				else
					translatedLabelValue = new MutablePair<String,String>("Ticket Status",value);
				break;
			case "processAreaDesc":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("Process Area","");
				else
					translatedLabelValue = new MutablePair<String,String>("Process Area",value);
				break;
			default :
				translatedLabelValue = new MutablePair<String,String>(apiLabel,value);
				break;
			}
		} catch (Exception e) {
			log.info("Unable to convert Label: "+apiLabel+".Error: "+e.getMessage());
		}
		return translatedLabelValue;
	}
	public MutablePair<String,String> getTranslatedValueforAPIHeaderAttributes(String apiLabel,String value){

		MutablePair<String,String> translatedLabelValue = null;

		try {
			switch (apiLabel) {
			case "inhouseUpc": 
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("In-House UPC","");
				else
					translatedLabelValue = new MutablePair<String,String>("In-House UPC",value);
				break;
			case "vndStyle":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("VNDSTYL","");
				else
					translatedLabelValue = new MutablePair<String,String>("VNDSTYL",value);
				break;
			case "pid":  
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("PID","");
				else
					translatedLabelValue = new MutablePair<String,String>("PID",value);
				break;
			case "pidDesc":
				if(StringUtils.isBlank(value) || value.equals("null"))
					translatedLabelValue = new MutablePair<String,String>("PID Desc","");
				else
					translatedLabelValue = new MutablePair<String,String>("PID Desc",value);
				break;
			default :
				translatedLabelValue = new MutablePair<String,String>(apiLabel,value);
				break;
			}
		} catch (Exception e) {
			log.info("Unable to convert Label: "+apiLabel+".Error: "+e.getMessage());
		}
		return translatedLabelValue;
	}

	public ArrayList<String> getSKUListfromUI(String reportID){
		ArrayList<String> skuList = new ArrayList<String>();
		List<WebElement> listOfSKUElements = driver.findElements(By.xpath("//*[@id='inhouseUpc']"));
		for(WebElement element : listOfSKUElements){
			skuList.add(String.valueOf(element.getAttribute("value")));
		}
		return skuList;
	}

	public void validaterefreshandprint(){

		getWait(25).until(visibilityOf(refreshElement));
		if (refreshElement.isEnabled())
		{log.info("refresh  is enabled");
		}
		else {log.info("refresh  is not enabled");}

		Assert.assertEquals(refreshElementImg.getAttribute("alt"),"refresh","Refresh button is displayed");

		getWait(25).until(visibilityOf(printElement));
		if (printElement.isEnabled())
		{log.info("print  is enabled");
		}
		else {log.info("print  is not enabled");}

		//	Assert.assertEquals(printElement.findElement(By.xpath("div/button/span/img")).getAttribute("src"),"data:image/svg+xml,%3C?xml version='1.0'?%3E %3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'%3E %3Cpath d='M19 8H5c-1.66 0-3 1.34-3 3v6h4v4h12v-4h4v-6c0-1.66-1.34-3-3-3zm-3 11H8v-5h8v5zm3-7c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm-1-9H6v4h12V3z'/%3E %3Cpath d='M0 0h24v24H0z' fill='none'/%3E %3C/svg%3E","Print element not displayed");

		Assert.assertEquals(printElementImg.getAttribute("alt"),"print","Print button is displayed");

		//printElement.click();
		printElementImg.sendKeys(Keys.RETURN);


		try {
			if (printElement.isEnabled() && printElement.isDisplayed()) {

				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("#print-content > button > span.jss257.click();",printElement);
			}

			else {
				System.out.println("Unable to click on element");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		/*	WebElement reportElement = driver.findElement(By.xpath("//*[(@id='plugin') and (@name='plugin')]"));

	Assert.assertEquals(reportElement.findElement(By.xpath("//*[(@id='plugin') and (@name='plugin')]")).getAttribute("type"),"application/pdf","PDF is displayed");
	reportElement.sendKeys("^{W}"); */

		//*[(@id="plugin") and (@type="application/pdf")]
		/*List<String> printOptionsList= new ArrayList<String>();
	for(WebElement temp : printElement.findElements(By.xpath("div/div/div[@role='tooltip']/div/ul/li"))){
		printOptionsList.add(temp.getText());
	}
	Assert.assertEquals(this.printOptionListExpected, printOptionsList); */
		log.info("Print options are displayed as expected");

	}

	public void verifyPDFInURL() {


		try {

			if(pdfElement.isEnabled()) {
				pdfElement.click();
				log.info("Opening PDF for respective PO#");

				Set<String> windows = driver.getWindowHandles();
				Iterator<String> it  = windows.iterator();
				String parentWindow = it.next();
				String pdfWindow = it.next();
				driver.switchTo().window(pdfWindow);
				WebElement pdfPlugin = driver.findElement(By.xpath("//embed[@id='plugin']"));
				Assert.assertEquals(pdfPlugin.getAttribute("type"),"application/pdf","Pdf file is not opening ");
				log.info("Pdf page is validated for given PO number");
				driver.switchTo().window(parentWindow);
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
	public void barcodeElementsvalidation(String reportID,String poNbr) {


		Assert.assertEquals(reportIDBarCodeElement.findElement(By.xpath("div/p")).getText(),reportID,"Report ID not displayed");
		//	Assert.assertEquals(dateElement.getText(),DateTimeFormatter.ofPattern("M-dd-yyyy").format(LocalDate.now()),"Date not displayed");

		Assert.assertEquals(poNbrBarCodeElement.findElement(By.xpath("div/p")).getText(),poNbr,"Po Number not displayed");

	}

	public HashMap <String,String> getPOLineItemMapFromUI(String attributeType, String SkuNbr) {

		String xPathLineItemReference = "//*[contains(@id,'globalAttributes')]";

		if(attributeType.equals("editable")||attributeType.equals("editableMinusPrep")){
			xPathLineItemReference = "//*[contains(@id,'editPoField')]";
		}else if(attributeType.equals("noneditable")){
			xPathLineItemReference = "//*[contains(@id,'nonEditPoField')]";	
		}else if(attributeType.equals("prep")){
			xPathLineItemReference = "//*[(label[contains(text(),'Prep')])]";	
		}

		String xpathSKU = "//*[@id='inhouseUpc']";
		HashMap < String, String > screenMap = new HashMap < String, String > ();
		List < WebElement > allElements = driver.findElements(By.xpath(xPathLineItemReference));
		String val = null;
		String label = null;
		for (int i = 0; i < allElements.size(); i++) {
			String SkuNumber = driver.findElements(By.xpath(xpathSKU)).get(i).getAttribute("value");
			if (SkuNumber.equalsIgnoreCase(SkuNbr)) {
				int tempsize = allElements.get(i).findElements(By.xpath(".//label")).size();
				for (int j = 0; j < tempsize; j++) {
					label = allElements.get(i).findElements(By.xpath(".//label")).get(j).getText();
					if (allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName().equalsIgnoreCase("input")) {
						val = allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getAttribute("value");
					} else if (allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName().equalsIgnoreCase("select")) {
						Select select = new Select(allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j));
						WebElement option = select.getFirstSelectedOption();
						val = option.getText();
					}
					if (val != null && label != null) {
						screenMap.put(label, StringUtils.stripToEmpty(String.valueOf(val)));	                    
					}
				}
			}
		}
		if(attributeType.equals("editableMinusPrep")){
			screenMap.remove("Prep1");
			screenMap.remove("Prep2");
			screenMap.remove("Prep3");
			screenMap.remove("Prep4");
		}
		screenMap.remove("OVERRIDE THRESHOLD");

		return screenMap;
	}

	public boolean updateLineItemAttributes(Map<String, String>attributesMap, String skuNumber) {
		int elementFound = 0;
		int count=0;
		boolean updateLineItemAttributes = false;
		for(int i = 0; i < skuValList.size(); i++) {
			if(skuValList.get(i).getAttribute("value").equalsIgnoreCase(skuNumber)) {
				List <WebElement> inputLabel  = searchByLineItem.get(i).findElements(By.xpath(".//label"));				
				for(String lbl:attributesMap.keySet()) {
					for(int j = 0; j < inputLabel.size(); j++) {
						if(inputLabel.get(j).getText().trim().equalsIgnoreCase(lbl.trim())) {
							elementFound = elementFound + 1;
							if(searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName().equalsIgnoreCase("input")) {
								searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j).sendKeys(attributesMap.get(lbl));
							}else if(searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName().equalsIgnoreCase("select")) {
								Select dropdown = new Select(searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j));
								selectedTxt = dropdown.getFirstSelectedOption().getText();
								log.info("selectedTxt :{}",selectedTxt);
								List<WebElement> allOptions= dropdown.getOptions();
								log.info("allOptions :{}",allOptions);
								for(WebElement ew: allOptions){
									if(count >0){
										dropdown.selectByVisibleText(selectedTxt);
										String orgselectedTxt = dropdown.getFirstSelectedOption().getText();
										log.info("updselectedTxt to Original value:{}",orgselectedTxt);
										break;
									}
									else if(!ew.getText().equalsIgnoreCase(selectedTxt)){
										dropdown.selectByVisibleText(ew.getText());
										String updselectedTxt = dropdown.getFirstSelectedOption().getText();
										log.info("updselectedTxt :{}",updselectedTxt);
										count++;
										break;
									}
								}
							}

						}	
					}
				}				 				                                                    		    				
			}				 
		}
		if((Integer.compare(attributesMap.size(), elementFound))==0) {
			updateLineItemAttributes = true;
		}
		return updateLineItemAttributes;

	}

	public void sendEscape() {
		try{
			Actions action = new Actions(driver);
			action.sendKeys(Keys.ESCAPE).build().perform();
		}catch (Exception e) {
			log.info("Save alert not displayed");
		}			

	}

	public void clickButton(String button){
		switch(button){
		case "APPLY TO CURRENT":
			driver.findElement(By.xpath("//*[contains(text(),'APPLY TO CURRENT')]")).click();			
			break;
		case "APPLY TO ALL":
			driver.findElement(By.xpath("//*[contains(text(),'APPLY TO ALL')]")).click();			
			break;
		case "CANCEL":
			driver.findElement(By.xpath("//*[contains(text(),'CANCEL')]")).click();			
			break;
		case "OK":
			driver.findElement(By.xpath("//*[contains(text(),'OK')]")).click();			
			break;
		case "YES":
			driver.findElement(By.xpath("//div[div[@id='alert-dialog-title']]//*[contains(text(),'YES')]")).click();			
			break;
		case "NO":
			driver.findElement(By.xpath("//div[div[@id='alert-dialog-title']]//*[contains(text(),'NO')]")).click();			
			break;
		}	 
	}

	public HashMap<String,String> getDRRUpdateFieldValidation(String poNbr,String rcptNbr,String skuNumber) throws SQLException, Exception{

		HashMap<String,String> drrAttributeMap = new HashMap<String,String>();
		String query= String.format(SQLPo4Walls.GET_BARCODE, poNbr, rcptNbr,skuNumber);
		log.info("Query" ,query); 

		String polineslist = DBUtils.getDBValueInString("pofourwalls", query);
		log.info("PO lines{}", polineslist);


		String response = CommonUtils.getRequestResponse(PO4WallEndPoint.PO4WALL_GET_POLINE_DETAILS + polineslist);
		log.info("DRR service response {}", response);

		log.info("JSON response {}", response);

		if(response != null){
			JSONObject jsonObject = new JSONObject(response);

			drrAttributeMap.put("PolineBarcode",String.valueOf(jsonObject.get("poLineBarCode")));
			drrAttributeMap.put("ReportID",String.valueOf(jsonObject.get("reportId")));
			drrAttributeMap.put("Receipt NBR",String.valueOf(jsonObject.get("rcptNbr")));
			drrAttributeMap.put("Dept", String.valueOf(jsonObject.get("deptNbr")));
			drrAttributeMap.put("LOCN NBR", String.valueOf(jsonObject.get("locnNbr")));
			drrAttributeMap.put("PO NBR", String.valueOf(jsonObject.get("poNbr")));
			drrAttributeMap.put("PID", String.valueOf(jsonObject.get("pid")));
			drrAttributeMap.put("PO Line Itm", String.valueOf(jsonObject.get("poLineItm")));
			drrAttributeMap.put("PID Description", String.valueOf(jsonObject.get("pidDesc")));
			drrAttributeMap.put("color", String.valueOf(jsonObject.get("color")));
			drrAttributeMap.put("Colour Description", String.valueOf(jsonObject.get("colorDesc")));
			drrAttributeMap.put("Size", String.valueOf(jsonObject.get("size")));
			drrAttributeMap.put("Size Description", String.valueOf(jsonObject.get("sizeDesc")));
			drrAttributeMap.put("processArea", String.valueOf(jsonObject.get("processArea")));
			drrAttributeMap.put("TicketType", String.valueOf(jsonObject.get("ticketType")));
			drrAttributeMap.put("Inhouseupc", String.valueOf(jsonObject.get("skuUpc")));
			drrAttributeMap.put("Prep1", String.valueOf(jsonObject.get("prep1")));



		}

		else {
			log.info("response is empty for PO");}

		return drrAttributeMap;
	}

	public void validatesPOLineHeaderElementsforSKU(String rcptNbr,String reportID) throws SQLException, Exception {

		String poNbr = poNbrBarCodeElement.findElement(By.xpath("div/p")).getText();

		String query = String.format(SQLPo4Walls.GET_SKU, poNbr, rcptNbr);
		log.info("Get SKU details qerry for " +rcptNbr+ "{}" +query);

		String skuNumber = DBUtils.getDBValueInString("pofourwalls", query);
		log.info("Get SKU details qerry for " +rcptNbr+ "{}" +skuNumber);



		clickButton("Expand", skuNumber);
		HashMap<String,String> lineHeaderAttributesUI = getLineHeaderElementsforSKU(skuNumber);
		HashMap<String,String> lineHeaderAttributesAPI = getPOLineItemMapFromAPITrans(skuNumber,reportID,"header");
		CommonUtils.compareValues(lineHeaderAttributesUI, lineHeaderAttributesAPI);
		log.info("Line Header Attributes are validated for SKU: "+skuNumber+" on ReportID: "+reportID);



	}

	public void validateSkulist(String poNbr,String rcptNbr,String reportID) throws SQLException, Exception {
		List<String> skuNumberService = new ArrayList<String>();
		ArrayList<String> skuList = getSKUListfromUI(reportID);
		for(String skuNumber: skuList){

			clickButton("Expand", skuNumber);
			HashMap<String,String> DiplayHeaderAttributes = getLineHeaderElementsforSKU(skuNumber);
			HashMap<String,String> ServiceHeaderAttributes = getPOLineItemMapFromAPITrans(skuNumber,reportID,"header");
			log.info("Line Header Attributes from service" + ServiceHeaderAttributes);
			CommonUtils.compareValues(DiplayHeaderAttributes, ServiceHeaderAttributes);
			log.info("Line Header Attributes  validated for SKU: "+skuNumber+" on ReportID: "+reportID);
			skuNumberService.add(ServiceHeaderAttributes.get("In-House UPC"));
		}
		log.info("All SKU Line Header Attributes  validated for ReportID: "+reportID);	
		Assert.assertEquals(skuNumberService.size(), skuList.size(),"Skulist is not matching with expected result");


		for(String sku : skuList) {
			HashMap<String,String> poDetailsListBeforeEdit = getDRRUpdateFieldValidation(poNbr,rcptNbr,sku);
			String ticketTypeB4Edit = poDetailsListBeforeEdit.get("TicketType");
			String prepTypeB4Edit = poDetailsListBeforeEdit.get("Prep1");


			String editedTicketType = null;
			String editedPrepType = null;
			TimeUnit.SECONDS.sleep(25);
			clickButton("Expand", sku);
			TimeUnit.SECONDS.sleep(25);

			clickButton("Edit", sku);
			String  ticketTypeElement = "//*[@id='"+sku+"-ticketType']/option";
			List<WebElement> ticketTypeDropDown = driver.findElements(By.xpath(ticketTypeElement));	

			for (int i =0; i< ticketTypeDropDown.size(); i++) 
			{

				String ticketTypedropdownvalue = ticketTypeDropDown.get(i).getText();
				if(ticketTypeB4Edit.equalsIgnoreCase(ticketTypedropdownvalue))
				{
					editedTicketType = ticketTypeDropDown.get(i+1).getText();
					ticketTypeDropDown.get(i+1).click();
					log.info("DropDown is validated" +editedTicketType);
					break;
				}

			}

			String prepElement = "//*[@id='"+sku+"-prep1']/option";
			List<WebElement> prepDropDown = driver.findElements(By.xpath(prepElement));	
			for (int i =0; i< prepDropDown.size(); i++) 
			{
				String prepdropdownvalue = prepDropDown.get(i).getText();
				System.out.println(prepdropdownvalue);


				editedPrepType= prepDropDown.get(i+1).getText();
				prepDropDown.get(i+1).click();
				log.info("DropDown is validated" +editedPrepType);	
				break;

			}


			clickButton("Save", sku);
			userAppliesSKU_PID("SKU");

			HashMap<String,String> poDetailsListAfterEdit = getDRRUpdateFieldValidation(poNbr,rcptNbr,sku);

			log.info("Before edit the ticket type is {}", ticketTypeB4Edit+"then updated  ticket type is {}",poDetailsListAfterEdit.get("TicketType"));
			log.info("Before edit the prep type is {}",prepTypeB4Edit+"the updated  ticket type is {}",poDetailsListAfterEdit.get("prep1"));

			Assert.assertEquals(editedTicketType, poDetailsListAfterEdit.get("TicketType"), "Selected ticket type is not updated");
			if(poDetailsListAfterEdit.get("Prep1") != null)
				Assert.assertEquals(editedPrepType, poDetailsListAfterEdit.get("Prep1"), "Selected prep type is not updated");

		}

	}
	public void userAppliesSKU_PID(String element) {
		if(element.equals("SKU")){
			clickButton("APPLY TO CURRENT");		
		}else if(element.equals("PID")){
			clickButton("APPLY TO ALL");	
		}else{
			clickButton("CANCEL");	
		}
	}

	public ArrayList<String> getPOLineOptionsMapFromUI(String SkuNbr,String attributeName) {

		String xPathLineItemReference = "//*[contains(@id,'editPoField')]";

		String xpathSKU = "//*[@id='inhouseUpc']";
		ArrayList<String> attributeOptions = new ArrayList<String>();
		List < WebElement > allElements = driver.findElements(By.xpath(xPathLineItemReference));
		String val = null;
		String label = null;
		for (int i = 0; i < allElements.size(); i++) {
			String SkuNumber = driver.findElements(By.xpath(xpathSKU)).get(i).getAttribute("value");
			if (SkuNumber.equalsIgnoreCase(SkuNbr)) {
				int tempsize = allElements.get(i).findElements(By.xpath(".//label")).size();
				for (int j = 0; j < tempsize; j++) {
					label = allElements.get(i).findElements(By.xpath(".//label")).get(j).getText();
					if(StringUtils.compare(label, attributeName)==0){
						if(("select").equalsIgnoreCase(allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName())){
							Select select = new Select(allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j));
							for(WebElement option : select.getOptions()){
								val = option.getText();
								if (StringUtils.isNotBlank(val)){
									attributeOptions.add(StringUtils.stripToEmpty(String.valueOf(val)));
								}
							}

						}
						break;
					}
				}
			}

		}
		return attributeOptions;
	}

	public HashMap<String, HashMap<String,String>> getOverrideLineItemsDB(String reportID){
		HashMap<String,HashMap<String,String>> overridelineItemMap = new HashMap<String,HashMap<String,String>>();
		try {
			List<Map<Object, Object>> resultList = DBMethods.getValuesFromDBAsStringList(String.format(SQLQueriesDRR.OVERRIDE_ATTRIBS_PO_SQL, reportID), "pofourwalls");
			System.out.println(resultList);
			for(Map<Object, Object> result : resultList)
			{
				@SuppressWarnings({ "unchecked", "rawtypes" })
				HashMap<String, String> stringOverrideMap = new HashMap<String, String>((Map)result);
				List<String> prepIDList = DBMethods.getDBValueInList(String.format(SQLQueriesDRR.OVERRIDE_PREP_SQL, reportID, stringOverrideMap.get("SKU_UPC")), "pofourwalls");
				for(int i=0;i<prepIDList.size();i++){
					stringOverrideMap.put("PREP"+i+"ID", prepIDList.get(i));
				}
				overridelineItemMap.put(result.get("SKU_UPC").toString(),stringOverrideMap);
				stringOverrideMap.remove("SKU_UPC");
			}
		} catch (Exception e) {
			log.info("No Override Values from DB. Error: "+e.getMessage());
		}
		return overridelineItemMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap<String,String> getOverrideLineItemsDBTrans(String reportID,String skuNumber){
		HashMap<String,String> stringOverrideMap = new HashMap<String,String>();
		HashMap<String,String> translatedOverrideAttributesMap = new HashMap<String,String>();
		try{
			List<Map<Object, Object>> resultList = DBMethods.getValuesFromDBAsStringList(String.format(SQLQueriesDRR.OVERRIDE_ATTRIBS_SKU_SQL, reportID, skuNumber), "pofourwalls");
			System.out.println(resultList);
			stringOverrideMap = new HashMap<String, String>((Map)resultList.get(0));
			stringOverrideMap.remove("SKU_UPC");

			MutablePair<String, String> translatedElement = new MutablePair<String, String>();

			for(String dbLabel : stringOverrideMap.keySet()){
				translatedElement = getTranslatedValueforID(dbLabel,String.valueOf(stringOverrideMap.get(dbLabel)));
				translatedOverrideAttributesMap.put(translatedElement.getKey(), translatedElement.getValue());
			}

		} catch (Exception e) {
			log.info("No Overridden Attribute Values for SKU: "+skuNumber+".Error: "+e.getMessage());
		}
		return translatedOverrideAttributesMap;
	}

	public String getOverrideFlagforSKUFromDB(String reportID,String SKUNumber){
		String overrideFlag = "N";
		try {
			overrideFlag= DBMethods.getDBValueInString(String.format(SQLQueriesDRR.SKU_OVERRIDE_SQL, reportID, SKUNumber), "pofourwalls");
			if(StringUtils.isBlank(overrideFlag)){
				overrideFlag = "";
			}
		} catch (Exception e) {
			overrideFlag = "N";
		}
		return StringUtils.trimToEmpty(overrideFlag);
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap<String,String> getOverriddenValuesforSKUExcludePrep(String reportID,String SKUNumber){
		HashMap<String,String> overridelineItemMap = new HashMap<String,String>();
		try {
			List<Map<Object, Object>> resultList = DBMethods.getValuesFromDBAsStringList(String.format(SQLQueriesDRR.OVERRIDE_ATTRIBS_SKU_SQL, reportID, SKUNumber), "pofourwalls");
			overridelineItemMap = new HashMap<String, String>((Map)resultList.get(0));
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return overridelineItemMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap<String,String> getPrepOverriddenValuesforSKU(String reportID,String SKUNumber){
		HashMap<String,String> overridelineItemMap = new HashMap<String,String>();
		try {
			List<Map<Object, Object>> resultList = DBMethods.getValuesFromDBAsStringList(String.format(SQLQueriesDRR.OVERRIDE_ATTRIBS_SKU_SQL, reportID, SKUNumber), "pofourwalls");
			System.out.println(resultList);
			overridelineItemMap = new HashMap<String, String>((Map)resultList.get(0));
			List<String> prepIDList = DBMethods.getDBValueInList(String.format(SQLQueriesDRR.OVERRIDE_PREP_SQL, reportID, SKUNumber), "pofourwalls");
			for(int i=1;i<=prepIDList.size();i++){
				overridelineItemMap.put("PREP"+i+"_ID", prepIDList.get(i-1));
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return overridelineItemMap;
	}

	public boolean clearOverrideTablesforReportID(String reportID){
		try {
			List<String> prepIDList = DBMethods.getDBValueInList(String.format(SQLQueriesDRR.PREPOVERRIDE_IDS_BY_REPORTID, reportID), "pofourwalls");
			if(prepIDList.size()!=0){
				String deleteprepID = StringUtils.join(prepIDList, ',');
				String deleteprepQuery = String.format(SQLQueriesDRR.OVERRIDE_PREP_DELETE_REPORTID_SQL,deleteprepID);
				DBMethods.deleteOrUpdateDataBase(deleteprepQuery, "pofourwalls");
			}
			List<String> overrideIDList = DBMethods.getDBValueInList(String.format(SQLQueriesDRR.OVERRIDE_IDS_BY_REPORTID, reportID), "pofourwalls");
			if(overrideIDList.size()!=0){
				String deleteOverrideID = StringUtils.join(overrideIDList, ',');
				String deleteOverrideQuery = String.format(SQLQueriesDRR.OVERRIDE_DELETE_REPORTID_SQL,deleteOverrideID);
				DBMethods.deleteOrUpdateDataBase(deleteOverrideQuery, "pofourwalls");
			}

		} catch (Exception e) {
			log.info(e.getMessage());
			Assert.fail("Unable to clear Override Values. Error: "+e.getMessage());
		}
		return true;
	}


	public HashMap<String,String> getLineHeaderElementsforSKU(String skuNumber){

		List<WebElement> skuHeaderList = driver.findElements(By.xpath("//div[div/span[strong[contains(text(),'In-House UPC:')]]]"));
		HashMap<String,String> lineHeaderAttributes = new HashMap<String,String>();

		for(int i = 0; i < skuHeaderList.size(); i++) {
			String skuvalues = skuHeaderList.get(i).getText();
			if(skuvalues.contains(String.valueOf(skuNumber))){
				List <WebElement> allElements = skuHeaderList.get(i).findElements(By.xpath(".//span"));
				for(WebElement lineHeader : allElements)
				{
					String val = lineHeader.getText();
					if (StringUtils.isNotEmpty(val) ) {
						String[] lineHeaderKeyVal = val.split(":");
						lineHeaderAttributes.put(StringUtils.trimToEmpty(lineHeaderKeyVal[0]), StringUtils.trimToEmpty(lineHeaderKeyVal[1]));
					}
				}
				break;
			}
		}
		return lineHeaderAttributes;
	}

	public String getOverrideFlagforSKUfromUI(String skuNumber){
		String overrideFlag = "N";
		List<WebElement> overrideThresholdList = driver.findElements(By.xpath("//*[@id='nonEditPoField']/div/label[//*[contains(text(), 'OVERRIDE THRESHOLD')]]//input"));
		for(int i = 0; i < skuValList.size(); i++) {
			if(skuValList.get(i).getAttribute("value").equalsIgnoreCase(skuNumber)) {
				if(overrideThresholdList.get(i).isSelected())
				{overrideFlag = "Y";}
				break;
			}
		}
		return overrideFlag;
	}



	public MutablePair<String,String> getTranslatedValueforID(String dbLabel, String ID){
		if(globalLookupMap==null) {
			globalLookupMap = getOverRideAttributeIDValueMap();
			Assert.assertFalse(globalLookupMap==null, "Unable to retreive Global Attribute Lookup Response");
		}
		HashMap<String,String> IDValueMap = new HashMap<String,String>();
		MutablePair<String,String> translatedLabelValue = null;

		try {
			switch (dbLabel) {
			case "INNER_PACK":
				if(("N").equals(ID))
					translatedLabelValue = new MutablePair<String,String>("Pack","No");
				else if(("Y").equals(ID))
					translatedLabelValue = new MutablePair<String,String>("Pack","Yes");
				break;
			case "NO_OF_TICKETS":
				translatedLabelValue = new MutablePair<String,String>("# of Tickets",String.valueOf(ID));
				break;
			case "CASE_TYPE_ID":
				IDValueMap = globalLookupMap.get("Case Type");
				translatedLabelValue = new MutablePair<String,String>("Case Type",IDValueMap.get(ID)); 
				//		translatedLabelValue = new MutablePair<String,String>("Case Type",String.valueOf(ID));
				break;
			case "PRODUCT_CUBE_ID":
				IDValueMap = globalLookupMap.get("Product Cube");
				translatedLabelValue = new MutablePair<String,String>("Product Cube",IDValueMap.get(ID));
				break;
			case "FRAGILE_ID":
				IDValueMap = globalLookupMap.get("Fragile");
				translatedLabelValue = new MutablePair<String,String>("Fragile Y/N",IDValueMap.get(ID));
				break;
			case "CASE_ASSORTMENT_ID":
				IDValueMap = globalLookupMap.get("Case Assortment");
				translatedLabelValue = new MutablePair<String,String>("Case Assortment",IDValueMap.get(ID));
				break;
			case "PACK_QTY":
				translatedLabelValue = new MutablePair<String,String>("Pack QTY",String.valueOf(ID));
				break;
			case "HAZMAT":
				if(("N").equals(ID))
					translatedLabelValue = new MutablePair<String,String>("HAZMAT","No");
				else if(("Y").equals(ID))
					translatedLabelValue = new MutablePair<String,String>("HAZMAT","Yes");
				break;
			case "PROCESS_AREA_ID":
				IDValueMap = globalLookupMap.get("Process Area Confirmation");
				translatedLabelValue = new MutablePair<String,String>("Process Area Conf",IDValueMap.get(ID));
				break;
			case "TICKET_TYPE":
				IDValueMap = globalLookupMap.get("Ticket Type");
				translatedLabelValue = new MutablePair<String,String>("Ticket Type",IDValueMap.get(ID));
				break;
			case "PACKAWAY_STRG_ATTR_ID":
				IDValueMap = globalLookupMap.get("Packaway Storage Attribute");
				translatedLabelValue = new MutablePair<String,String>("Packaway Storage Attribute",IDValueMap.get(ID));
				break;
			default :
				translatedLabelValue = new MutablePair<String,String>(dbLabel,ID);
				break;
			}
		} catch (Exception e) {
			log.info("Unable to convert Label: "+dbLabel+".Error: "+e.getMessage());
		}
		return translatedLabelValue;
	}

	/*	public String openDRR()
	{
		for (Map<String, String> map : getGridElementsMap()) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				reportId=map.get("Report ID");
				if (key.equalsIgnoreCase("Report ID")) {
					List<WebElement> reportIds = driver.findElements(By.xpath("//*[@col-id='reportId']/div/button"));
					reportIds.get(0).click();
					getWait().until(ExpectedConditions.urlContains("drreport"));
				}

			}
		}
		return reportId;
	}
	 */

		public Map<String, String> getPrepOptionsforSKUFromUI(String SkuNbr) {
		String xPathLineItemReference = "//*[contains(@id,'editPoField')]";

		String xpathSKU = "//*[@id='inhouseUpc']";
		HashSet<String> prepOptions = new HashSet<String>();
		Map<String,String> prepwithOptions = new HashMap<>();
		List <WebElement> allElements = driver.findElements(By.xpath(xPathLineItemReference));
		String val = null;
		String label = null;
		for (int i = 0; i < allElements.size(); i++) {
			String SkuNumber = driver.findElements(By.xpath(xpathSKU)).get(i).getAttribute("value");
			if (SkuNumber.equalsIgnoreCase(SkuNbr)) {
				int tempsize = allElements.get(i).findElements(By.xpath(".//label")).size();
				for (int j = 0; j < tempsize; j++) {
					label = allElements.get(i).findElements(By.xpath(".//label")).get(j).getText();
					if (allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName().equalsIgnoreCase("input")) {
						val = allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getAttribute("value");
					} else if (allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName().equalsIgnoreCase("select")) {
						Select select = new Select(allElements.get(i).findElements(By.xpath(".//input | .//select")).get(j));
						WebElement option = select.getFirstSelectedOption();
						val = option.getText();
					}
					if (StringUtils.isNotBlank(val) && StringUtils.isNotBlank(label) && label.contains("Prep")) {
						prepOptions.add(String.valueOf(val));
						prepwithOptions.put(label,val);
					}
				}
			}
		}
	//	return prepOptions;
			return prepwithOptions;
	}


	public boolean updatesPrepAttributes(Map<String, String>attributesMap, String skuNumber) {
		int elementFound = 0;
		int count=0;
		boolean updateLineItemAttributes = false;
		for(int i = 0; i < skuValList.size(); i++) {
			if(skuValList.get(i).getAttribute("value").equalsIgnoreCase(skuNumber)){
				List <WebElement> inputLabel  = searchByLineItem.get(i).findElements(By.xpath(".//label"));
				for(String lbl:attributesMap.keySet()) {
					for(int j = 0; j < inputLabel.size(); j++) {
						if(inputLabel.get(j).getText().trim().equalsIgnoreCase(lbl.trim())) {
							elementFound = elementFound + 1;
							if(("input").equalsIgnoreCase(searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName())) {
								searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j).sendKeys(attributesMap.get(lbl));
							}else if(("select").equalsIgnoreCase(searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j).getTagName())) {
								/*if(!attributesMap.get(lbl).equals("None")){
									Select dropdown = new Select(searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j));
									dropdown.selectByVisibleText(attributesMap.get(lbl));
								}else if(attributesMap.get(lbl).equals("None")){
									Select dropdown = new Select(searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j));
									dropdown.selectByIndex(0);
								}*/
								Select dropdown = new Select(searchByLineItem.get(i).findElements(By.xpath(".//input | .//select")).get(j));
								selectedPrepTxt = dropdown.getFirstSelectedOption().getText();
								log.info("selectedPrepTxt :{}",selectedPrepTxt);
								List<WebElement> allPrepOptions= dropdown.getOptions();
								log.info("allPrepOptions :{}",allPrepOptions);
								for(WebElement ew: allPrepOptions){
									if(count >0){
										dropdown.selectByVisibleText(selectedPrepTxt);
										String orgprepselectedTxt = dropdown.getFirstSelectedOption().getText();
										log.info("updprepselectedTxt to Original value :{}",orgprepselectedTxt);
										break;
									}
									else if(!ew.getText().equalsIgnoreCase(selectedPrepTxt) && !ew.getText().isEmpty() && ew.getText() !=null){
										dropdown.selectByVisibleText(ew.getText());
										String updprepselectedTxt = dropdown.getFirstSelectedOption().getText();
										log.info("updprepselectedTxt :{}",updprepselectedTxt);
										count++;
										break;
									}
								}
							}

						}
					}
				}
			}
		}
		if((Integer.compare(attributesMap.size(), elementFound))==0) {
			updateLineItemAttributes = true;
		}
		return updateLineItemAttributes;

	}

	public HashSet<String> getPrepOptionsforSKUFromDB(String SKUNumber,String reportID){
		HashSet<String> prepOptions = new HashSet<String>();
		try {
			List<String> prepValueList = DBMethods.getDBValueInList(String.format(SQLQueriesDRR.OVERRIDE_PREPVALUE_SQL, reportID, SKUNumber), "pofourwalls");
			for(String prepValue : prepValueList){
				if(StringUtils.isNotBlank(prepValue))
					prepOptions.add(prepValue);
			}
		} catch (Exception e) {
			log.info("No Prep Attribute Values for SKU: "+SKUNumber+".Error: "+e.getMessage());
		}
		return prepOptions;
	}

	public HashMap<String, HashMap<String, String>> getOverRideAttributeIDValueMap(){
		HashMap<String,HashMap<String,String>> attributeMap = new HashMap<String,HashMap<String,String>>();
		
		String drrResponse = null;
		Response response = null;

		try {

			String endPoint = commonUtils.getUrl("pofourwalls.poAttributeLookup");
			response = WhmRestCoreAutomationUtils.getRequestResponse(endPoint, 200).asResponse();

			if(response.getStatusCode()==200){
				drrResponse = response.getBody().asString();
			}else{
				Assert.fail("Unable to get Global Lookup Attribute response. Response Status: "+response.getStatusCode());
			}
		
			Set<String> attributeList = new HashSet<String>();
			if(drrResponse != null){
				attributeList = CommonUtils.getMapFromJson(drrResponse).keySet();
				for(String attribValue : attributeList) {
					Map<String, String> attribMap = CommonUtils.getMapFromJson(new JSONObject(drrResponse).getJSONObject(attribValue).toString());
					HashMap<String, String> swappedAttribMap = new HashMap<>();
					for(Map.Entry<String, String> entry : attribMap.entrySet()){
						swappedAttribMap.put(String.valueOf(entry.getValue()), String.valueOf(entry.getKey()));
					}
					attributeMap.put(attribValue, swappedAttribMap);
				}
			}
		} catch (Exception e) {
			Assert.fail("Unable to get Global Lookup Attribute response"+e.getLocalizedMessage());
		}
		
		log.info("Attribute Map\n"+attributeMap);
		return attributeMap;
	}
}



