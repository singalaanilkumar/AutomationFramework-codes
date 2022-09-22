package com.macys.mst.DC2.EndToEnd.pageobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.macys.mst.DC2.EndToEnd.configuration.ReadHostConfiguration;
import com.macys.mst.DC2.EndToEnd.execdrivers.ExecutionConfig;
import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.PageObject;
import com.macys.mst.artemis.selenium.SeUiContextBase;
import com.macys.mst.artemis.testNg.LocalDriverManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasePage extends PageObject{

	static LocalDriverManager localDriverManager = LocalDriverManager.getInstance();
	public static WebDriver driver = localDriverManager.getDriver();
	private static SeUiContextBase helperClass = new SeUiContextBase();

	public BasePage(){
		super(driver);
	}

	// WebElements
	@FindBy(xpath = ".//h1/span")
	private WebElement title;

	@FindBy(xpath = "//input[@id='username']")
	private WebElement userName;

	@FindBy(xpath = "//input[@id='password']")
	private WebElement password;
	
	@FindBy(xpath = "//*[@ref='lbTotal']")
	private WebElement totalPages;
	
	@FindBy(xpath = "//*[@ref='lbCurrent']")
	private WebElement currentPage;
	
	@FindBy(xpath = "//*[@ref='btPrevious']")
	private WebElement previousPage;
	
	@FindBy(xpath = "//*[@ref='btNext']")
	private WebElement nextPage;
	
	@FindBy(xpath = "//*[@ref='btFirst']")
	private WebElement firstPage;
	
	@FindBy(xpath = "//*[@ref='btLast']")
	private WebElement lastPage;
	
	@FindBy(xpath = "//span[text()='Sign In']")
	private WebElement loginButton;
	
	@FindBy(xpath = "(//*[@ref='eBodyHorizontalScrollViewport'])[1]")
	public WebElement horizontalScrollBar;
	
	@FindBy(xpath = "//*[@ref='eBodyViewport']")
	WebElement gridResultTable;

	// locators
	private By breadcrumb = By.xpath("//*[@id='breadcrumbContainer']/div/button");
	private By gridHeader = By.xpath(".//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[1]/div[2]/div/div");
	private By gridToolbar = By.id("gridToolbarContainer");
	private By gridRows = By.xpath("//*[@id='gridContainer']/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]");
	private By cellLocator = By.xpath(".//div[contains(@role, 'gridcell') and contains(@class, 'ag-cell-value')]");
	private By noRowsLocator = By.xpath(".//div[contains(@ref, 'eOverlay')]/div/div/span[contains(text(), 'No Rows To Show')]");

	private String header = "/html[1]/body[1]/div[1]/div[1]/div[1]/div[3]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]";
	private String values = "/html[1]/body[1]/div[1]/div[1]/div[1]/div[3]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]";

	// URLs
	private static String baseUrl = FileConfig.getInstance().getStringConfigValue("WMSUI.baseURL");
	protected int scrollCountMax = 3;

	public BasePage(WebDriver driver) {
		super(driver);
	}

	public String getHomepageTitle() {
		return title.getText();
	}

	public void loadHomepageURL() {
		driver.get(baseUrl);
		log.info("Base Url : "+baseUrl);
		StepDetail.addDetail("Base Url : "+baseUrl, true);
	}

	public void login() throws Exception {
		getWait(5).until(ExpectedConditions.visibilityOf(userName));
		userName.clear();
		userName.sendKeys(ExecutionConfig.appUIUserName);
		password.clear();
		password.sendKeys(ExecutionConfig.appUIPassword);
		loginButton.click();
		log.info("Logged in successfully");
		StepDetail.addDetail("Logged in successfully : ", true);
	}

	public Boolean isNavMenuDisplayed(){
		try{
			driver.findElement(By.xpath(".//div[contains(@class,'Navigation-sideNav-5')]/div[contains(@style, 'transform: translateX(-324px)')]"));
			return false;
		}catch (NoSuchElementException e){
			return true;
		}
	}
	
	public void clickNavOption(String option) {
		getWait(15).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(),'" + option + "')]")));
		WebElement navOption = driver.findElement(By.xpath("//*[text()='" + option + "']"));
		navOption.click();
		log.info("Navigating to "+option+" UI");
		StepDetail.addDetail("Navigating to "+option+" UI", true);
	}

	public List<String> getPageBreadcrumb() {
		waitForElement(breadcrumb, 5);
		List<String> displayedButtons = new ArrayList<>();
		List<WebElement> breadcrumbTextList = driver.findElements(breadcrumb);
		for (WebElement item : breadcrumbTextList)
			displayedButtons.add(item.getText());
		return displayedButtons;
	}

	protected WebElement waitForElement(By locator, int timeout) {
		int count = 0;
		while (count < timeout) {
			try {
				return driver.findElement(locator);
			} catch (NoSuchElementException e) {
				count++;
				getWait(10);
			}
		}
		log.info("Element could not be found " + locator);
		return null;
	}

	protected void scrollElementIntoView(WebElement webElement) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView(true);", webElement);
	}

	public WebDriverWait getWait(int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		return wait;
	}

	public List<Map<String, String>> getColumnData() {
		waitForElement(gridHeader, 10);
		List<Map<String, String>> columnData = new ArrayList<>();

		scrollElementIntoView(driver.findElement(gridToolbar));

		WebElement gridHeaderLocator = driver.findElement(gridHeader);
		List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("div"));
		WebElement gridRowsLocator = driver.findElement(gridRows);
		List<WebElement> rowsList = gridRowsLocator.findElements(By.xpath("div"));
		List<String> columnsList = new ArrayList<>();

		for (WebElement column : headerList) {
			if(!StringUtils.isEmpty(column.getText()))
				columnsList.add(column.getText());
			else{
				int i = columnsList.size()+1;
				JavascriptExecutor je = (JavascriptExecutor) driver;
				WebElement element = driver.findElement(By.xpath(header+"/div["+i+"]"));
				je.executeScript("arguments[0].scrollIntoView(true);",element);
				columnsList.add(column.getText());
			}
			for (int i = 1; i<rowsList.size(); i++ ) {
				Map<String, String> rowData = new HashMap<>();
				String colID = getColID(column.getText());
				if(!StringUtils.isEmpty(colID)){
					WebElement cell = driver.findElement(By.xpath(values+"/div["+i+"]"));
					WebElement cellContent = cell.findElement(By.xpath("div[(@col-id='" + colID + "')]"));
					rowData.put("Row #", String.valueOf(i));
					rowData.put(column.getText(), cellContent.getText());
					columnData.add(rowData);
				}
			}
		}
		List<Map<String,String>> columnList = new ArrayList<>();
		for(int row=1; row<= rowsList.size();row++){
			Map<String,String> columns = new HashMap<>();
			for(Map<String,String> col : columnData){
				if(String.valueOf(row).equalsIgnoreCase(col.get("Row #"))){
					col.remove("Row #");
					columns.putAll(col);
				}
			}
			columnList.add(columns);
		}
		return columnList;
	}

	private void scrollCellBeginningOfGrid(List<WebElement> headerList) {
		if (isElementDisplayed(cellLocator)) {
			List<WebElement> gridContainer = driver.findElements(cellLocator);
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

	private void scrollCellMidOfGrid(List<WebElement> headerList) {
		if (isElementDisplayed(cellLocator)) {
			List<WebElement> gridContainer = driver.findElements(cellLocator);
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
			colID = "processArea";
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
		case "RCV +/-":
			colID = "rcv";
			break;
		case "PID":
			colID = "pid";
			break;
		case "PID Desc":
			colID = "pidDesc";
			break;
		case "Vendor UPC":
			colID = "vendorUpc";
			break;
		case "VNDSTYL":
			colID = "vndStyle";
			break;
		case "Col Desc":
			colID = "colDesc";
			break;
		case "Size Desc":
			colID = "sizeDesc";
			break;
		case "Compare @ Retail":
			colID = "compareRetail";
			break;
		case "Line Item Status":
			colID = "status";
			break;
		}
		return colID;
	}

	protected Boolean isElementDisplayed(By locator) {
		try {
			waitForElement(locator, 5);
			return driver.findElement(locator).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public void clickGridCell(String columnName1, String value1,String columnName2,String value2) {
		waitForElement(gridHeader, 10);

		scrollElementIntoView(driver.findElement(gridToolbar));

		WebElement gridHeaderLocator = driver.findElement(gridHeader);
		List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("div"));
		WebElement gridRowsLocator = driver.findElement(gridRows);
		List<WebElement> rowsList = gridRowsLocator.findElements(By.xpath("div"));
		List<String> columnsList = new ArrayList<>();

		boolean found = false;
		for (WebElement column : headerList) {
			columnsList.add(column.getText());
			if (column.getText().equals(columnName1)) {
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
					if (headerList.get(headerList.size() - 3).getText().equals(columnName1)) {
						endOfList = true;
						break;
					}
					scrollCellMidOfGrid(headerList);

					headerList = gridHeaderLocator.findElements(By.xpath("div"));
					if (columnsList.get(columnsList.size() - 1).equals(headerList.get(headerList.size() - 2).getText())) {
						if (headerList.get(headerList.size() - 1).getText().equals(columnName1)) {
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
			if (column.getText().equals(columnName2) && found) {
				for (WebElement row : rowsList) {
					String col2 = getColID(columnName2);
					WebElement cellContent2 = row.findElement(By.xpath("div[(@col-id='" + col2 + "')]"));
					if (cellContent2.getText().equals(value2)) {
						String col1 = getColID(columnName1);
						WebElement cellContent1 = row.findElement(By.xpath("div[(@col-id='" + col1 + "')]"));
						if (cellContent1.getText().equals(value1)) {
							cellContent1.click();
							log.info("Clicking on PO link in PO Inquiry Screen");
							StepDetail.addDetail("Clicking on PO link in PO Inquiry Screen", true);
							break;
						}
					}
				}
				break;
			}
		}
	}

	public WebDriverWait getWait(){
		return new WebDriverWait(driver,30);
	}

	public static void cleanAllDriverActivities(){
		localDriverManager.driverCleanup();
	}

	public static SeUiContextBase getRequiredAction(){
		return helperClass;
	}

	public Map<String, String> getScreenData(String xpath) {
		getWait(5);
		List<WebElement> allFields = driver.findElements(By.xpath(xpath));
		Map<String,String> allData = new HashMap<>();
		for (WebElement element : allFields) {
			if (StringUtils.isNotEmpty(element.getText())) {
				String text[] = element.getText().trim().split(":");
				if(text.length<2)
					allData.put(text[0].trim(),"");
				else if(text.length==2){
					allData.put(text[0].trim(),text[1].trim());
				}else if(text.length>2){
					allData.put(text[0].trim(),element.getText().replaceFirst(text[0], "").replaceFirst(":","").trim());
				}
			}
		}
		log.info("UIScreen Data {}",allData.toString());
		return allData;
	}

    public void scrollGridTableRight(Integer index){
    	try {
			WebElement horizontalScrollBar = driver.findElement(By.xpath("(//*[@ref='eBodyHorizontalScrollViewport'])["+index+"]"));
			if(horizontalScrollBar.isDisplayed()){
				Rectangle horizontalScrollBarRect = horizontalScrollBar.getRect();
				new Actions(driver).moveToElement(horizontalScrollBar,  (horizontalScrollBarRect.width/2)-18, (horizontalScrollBarRect.height/2)).click().build().perform();
			}else{
				log.info("ScrollBar not visible for Grid");
			}
		} catch (Exception e) {
			log.info("Exception while scrolling scroll bar to the right ",e.getMessage());
		}
    }   

    
    public void scrollGridTableLeft(Integer index){
    	try {
    		WebElement horizontalScrollBar = driver.findElement(By.xpath("(//*[@ref='eBodyHorizontalScrollViewport'])["+index+"]"));
			if(horizontalScrollBar.isDisplayed()){
				Rectangle horizontalScrollBarRect = horizontalScrollBar.getRect();
				new Actions(driver).moveToElement(horizontalScrollBar, -(horizontalScrollBarRect.width/2)+18, (horizontalScrollBarRect.height/2)).click().build().perform();
			}else{
				log.info("ScrollBar not visible for Grid");
			}
		} catch (Exception e) {
			log.info("Exception while scrolling scroll bar to the left ", e.getMessage());
		}
    }

	public void clearText(WebElement webElement){
		webElement.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		webElement.sendKeys(Keys.DELETE);
	}

	public void pageLoadWait(){
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}


	public void jsClick(WebElement element){
		try {
			JavascriptExecutor executor = (JavascriptExecutor)driver;
			executor.executeScript("arguments[0].click();", element);
		} catch (Exception e) {
			log.info("unable to JS click on element{}",e.getLocalizedMessage());
		}
	}
	
	public void waitForProcessing() {
		try {
			getWait(5).ignoring(Exception.class).until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//p[text()='Processing... Please wait.']")));
		} catch (Exception e) {
			log.info("Processing "+e.getMessage());
		}
		try {
			getWait(10).ignoring(Exception.class).until(ExpectedConditions
					.invisibilityOfElementLocated(By.xpath("//p[text()='Processing... Please wait.']")));
		} catch (Exception e) {
			log.info("Processing "+e.getMessage());
		}
	}
	
	public void waitForProcessing(int secondsTime) {
		try {
			getWait(secondsTime).ignoring(Exception.class).until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//p[text()='Processing... Please wait.']")));
		} catch (Exception e) {
			log.info("Processing "+e.getMessage());
		}
		try {
			getWait(secondsTime).ignoring(Exception.class).until(ExpectedConditions
					.invisibilityOfElementLocated(By.xpath("//p[text()='Processing... Please wait.']")));
		} catch (Exception e) {
			log.info("Processing "+e.getMessage());
		}
	}
	
	public void waitForHeader(String pageHeaderName) {
		try {
			getWait(15).ignoring(Exception.class).until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[text()='"+pageHeaderName+"']")));
		} catch (Exception e) {
			log.info("Page Header '{}' not visible, "+e.getMessage(),pageHeaderName);
		}
	}

	public List<Map<String, String>> getGridElementsMap(){
    	getWait(30).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@ref='eBodyViewport']")));
		scrollElementIntoView(driver.findElement(By.xpath("//*[@ref='eBodyViewport']")));
		Map<String,String> colIDnameMap = new HashMap<String,String>(); 
		Map<String,String> colIDvalueMap = new HashMap<String,String>(); 
		Map<String,Map<String, String>> colNameValueMapList = new HashMap<String,Map<String,String>>();
	
		try {
			int scrollCount = 0;
    		while(scrollCount<this.scrollCountMax){
    			helperClass.waitFor(2);
    			List<WebElement> headerRowCells = driver.findElements(By.xpath("//*[@ref='eHeaderContainer']//*[@col-id!='0']"));
    			
    			for(int i=0;i<headerRowCells.size();i++){
    				try {
    					String headerColID = headerRowCells.get(i).getAttribute("col-id");
						String headerName  = StringUtils.normalizeSpace(headerRowCells.get(i).getText());
						if(StringUtils.isNotBlank(headerColID) && StringUtils.isNotBlank(headerName))
							colIDnameMap.put(headerColID,headerName);
					} catch (StaleElementReferenceException e) {
						//ignore
					}
    			}
				getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']")));
				List<WebElement> valueRows = driver.findElements(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']"));
				for(int i=0;i<valueRows.size();i++){
					try {
						if(valueRows.get(i).isDisplayed()){
							String rowID = String.valueOf(valueRows.get(i).getAttribute("row-id"));
							colIDvalueMap = colNameValueMapList.containsKey(rowID)?colNameValueMapList.get(rowID):(new HashMap<String,String>());
							List<WebElement> valueCells = valueRows.get(i).findElements(By.xpath(".//*[@role='gridcell' and @col-id!='0']"));
							for(int j=0;j<valueCells.size();j++){
								try {
									String colID = valueCells.get(j).getAttribute("col-id");
									String colText = (StringUtils.isEmpty(valueCells.get(j).getText()))?"":valueCells.get(j).getText();
									if(StringUtils.isNotBlank(colID) && colIDnameMap.containsKey(colID)){
										colIDvalueMap.put(colIDnameMap.get(colID), StringUtils.normalizeSpace(colText.trim()));
									}
								} catch (StaleElementReferenceException e) {
									//ignore
								}
							}
							colNameValueMapList.put(rowID,colIDvalueMap);
						}
					} catch (StaleElementReferenceException e) {
						//ignore
					}
				}
				if(horizontalScrollBar.isDisplayed()){
					scrollGridTableRight(1);
					scrollCount++;
				}else{
					break;
				}
    		}
			log.info(colIDnameMap.toString());
			scrollCount = 0;
			while(scrollCount<scrollCountMax){
				scrollGridTableLeft(1);
				scrollCount++;
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
		}    	
		return colNameValueMapList.values().stream().collect(Collectors.toList());
    }
	
	public List<Map<String, String>> getGridElementsMap(Integer gridIndex,Integer scrollCountMax){
    	getWait(30).ignoring(Exception.class).until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//*[@ref='eBodyViewport'])["+gridIndex.toString()+"]")));
		scrollElementIntoView(driver.findElement(By.xpath("//*[@ref='eBodyViewport']")));
		Map<String,String> colIDnameMap = new HashMap<String,String>(); 
		Map<String,String> colIDvalueMap = new HashMap<String,String>(); 
		Map<String,Map<String, String>> colNameValueMapList = new HashMap<String,Map<String,String>>();
	
		try {
			int scrollCount = 0;
    		while(scrollCount<scrollCountMax){
    			helperClass.waitFor(2);
    			List<WebElement> headerRowCells = driver.findElements(By.xpath("(//*[@ref='eHeaderContainer'])["+gridIndex.toString()+"]//*[@col-id!='0']"));
    			
    			for(int i=0;i<headerRowCells.size();i++){
    				try {
    					String headerColID = headerRowCells.get(i).getAttribute("col-id");
						String headerName  = StringUtils.normalizeSpace(headerRowCells.get(i).getText());
						if(StringUtils.isNotBlank(headerColID) && StringUtils.isNotBlank(headerName))
							colIDnameMap.put(headerColID,headerName);
					} catch (StaleElementReferenceException e) {
						//ignore
					}
    			}
				getWait(10).ignoring(Exception.class).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("(//*[@ref='eCenterContainer'])["+gridIndex.toString()+"]//*[@role='row']")));
				List<WebElement> valueRows = driver.findElements(By.xpath("//*[@ref='eCenterContainer']//*[@role='row']"));
				for(int i=0;i<valueRows.size();i++){
					try {
						String rowID = String.valueOf(valueRows.get(i).getAttribute("row-id"));
						colIDvalueMap = colNameValueMapList.containsKey(rowID)?colNameValueMapList.get(rowID):(new HashMap<String,String>());
						List<WebElement> valueCells = valueRows.get(i).findElements(By.xpath(".//*[@role='gridcell' and @col-id!='0']"));
						for(int j=0;j<valueCells.size();j++){
							try {
								String colID = valueCells.get(j).getAttribute("col-id");
								String colText = (StringUtils.isEmpty(valueCells.get(j).getText()))?"":valueCells.get(j).getText();
								if(StringUtils.isNotBlank(colID) && colIDnameMap.containsKey(colID)){
									colIDvalueMap.put(colIDnameMap.get(colID), StringUtils.normalizeSpace(colText.trim()));
								}
							} catch (StaleElementReferenceException e) {
								//ignore
							}
						}
						colNameValueMapList.put(rowID,colIDvalueMap);
					} catch (StaleElementReferenceException e) {
						//ignore
					}
				}
			scrollGridTableRight(gridIndex);
			scrollCount++;
    		}
			log.info(colIDnameMap.toString());
			scrollCount = 0;
			while(scrollCount<scrollCountMax){
				scrollGridTableLeft(gridIndex);
				scrollCount++;
			}
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
		}    	
		return colNameValueMapList.values().stream().collect(Collectors.toList());
    }
		
	public List<Map<String, String>> getGridElementsMapAllPages() {
		List<Map<String, String>> dbGridMapList = new ArrayList<Map<String, String>>();
		try {
			TimeUnit.SECONDS.sleep(10);
			selectRowsPerPage("10");
			Integer currentPgeNbr = 1;
			Integer totalPageCount = getTotalPageCount();
			if(totalPageCount>1){
				for (int k = 1; k <= totalPageCount; k++) {
					currentPgeNbr = getCurrentPageNumber();
					if (currentPgeNbr <= totalPageCount) {
						dbGridMapList.addAll(getGridElementsMap());
						if (currentPgeNbr < totalPageCount) {
							navigateToNextPage();
						}
						else if (currentPgeNbr == totalPageCount) {
							navigateToFirstPage();
							break;
						}
					}
				}
				
			}else{
				dbGridMapList.addAll(getGridElementsMap());
			}			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage());
		}
		return dbGridMapList;
	}
	
    public void clickSCMmenu(){
    	getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='appBarHamburgerMenu']/button")));
    	driver.findElement(By.xpath("//*[@id='appBarHamburgerMenu']/button")).click();	
    }

    public void loginSCM() {
        driver.get(ReadHostConfiguration.SCM_MENU_URL.value());
        if(driver.getCurrentUrl().contains("login")){
        	getWait(20).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(userName));
            userName.clear();
            //userName.sendKeys(ExecutionConfig.appUIUserName);
            userName.sendKeys("");

            getWait(20).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(password));
            password.clear();
            //password.sendKeys(ExecutionConfig.appUIPassword);
			password.sendKeys("");

            getWait(20).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(loginButton));
    		loginButton.click();
        }else{
            log.info("Login success");
        }
    }

	protected void scrollToTop(){
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollTo(0, 0);");
	}
	
	public boolean navigateToNextPage(){
		try {
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(nextPage));
			nextPage.click();
			return true;
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			return false;
		}		
	}
	
	public boolean navigateToPreviousPage(){
		try {
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(previousPage));
			previousPage.click();
			return true;
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			return false;
		}		
	}
	
	public boolean navigateToFirstPage(){
		try {
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(firstPage));
			firstPage.click();
			return true;
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			return false;
		}		
	}
	
	public boolean navigateToLastPage(){
		try {
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(lastPage));
			lastPage.click();
			return true;
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			return false;
		}		
	}
	
	public Integer getCurrentPageNumber(){
		try {
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(currentPage));
			return Integer.valueOf(currentPage.getText());
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			return 0;
		}		
	}
	
	public Integer getTotalPageCount(){
		try {
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(totalPages));
			return Integer.valueOf(totalPages.getText());
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			return 0;
		}		
	}
	
	public boolean selectRowsPerPage(String rowCount){
		try {
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Rows per page:']")));
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[input[@id='gridRowsPerPage']]")));
			driver.findElement(By.xpath("//*[input[@id='gridRowsPerPage']]")).click();
			getWait(10).ignoring(Exception.class).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-value='"+rowCount+"']")));
			driver.findElement(By.xpath("//*[@data-value='"+rowCount+"']")).click();
			return true;
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
			return false;
		}		
	}
	
}