	package com.macys.mst.DC2.EndToEnd.pageobjects;

	import com.macys.mst.DC2.EndToEnd.configuration.PO4WallEndPoint;
	import com.macys.mst.DC2.EndToEnd.db.app.SQLPo4Walls;
	import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
	import com.macys.mst.artemis.db.DBUtils;
	import lombok.extern.slf4j.Slf4j;
	import org.codehaus.jettison.json.JSONObject;
	import org.jbehave.core.steps.context.StepsContext;
	import org.openqa.selenium.NoSuchElementException;
	import org.openqa.selenium.*;
	import org.openqa.selenium.interactions.Actions;
	import org.openqa.selenium.support.FindBy;
	import org.testng.Assert;

	import java.sql.SQLException;
	import java.util.*;
	import java.util.concurrent.TimeUnit;

	import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;


@Slf4j
public class PODistroPage  extends BasePage {

	private StepsContext stepsContext;
	
	public PODistroPage(WebDriver driver) {
		super(driver);
	}

	CommonUtils commonUtils = new CommonUtils();
	public String hostName = "https://qa-backstage.devops.fds.com";

	public String reportingResponse = null;
	
	@FindBy(xpath = "//span[contains(text(),'PO Dashboard')]")
	WebElement poDashboard;

	@FindBy(xpath = "//span[contains(text(),'PO Inquiry')]")
	WebElement poInquiry;

	@FindBy(xpath = "//*[@id='breadcrumbContainer']/div/button")
	WebElement breadcrumb;		
	
	@FindBy(xpath = "//*[@id='breadcrumbContainer']//button[contains(.,'PO Details')]")
	WebElement podetailsbreadcrumb;			
	
	@FindBy(xpath = "//button[(@id = 'clearButton')]")
	WebElement clearButton;
	
	@FindBy(xpath = "(//*[@col-id='poNbr'])[2]//button")
	WebElement poLink;
	
 /*	@FindBy(id = "gridSection")
	WebElement filterResultTable; */
	
	@FindBy(xpath=".//*[(@id ='gridSection')]")
	WebElement filterResultTable;
	
	@FindBy(xpath = ".//div[contains(@ref, 'gridPanel')]")
	WebElement gridPanel;
	
	@FindBy(xpath = "//span[@class='ag-header-select-all']/span/span[@class='ag-icon ag-icon-checkbox-unchecked']")
	WebElement headerCheckBox;
	
	@FindBy(xpath="//div[@class='ag-header ag-pivot-off']")
	WebElement gridHeader1;

	private By gridHeader =By.xpath(".//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[1]/div[2]/div/div");
	
//	private By gridHeader =By.xpath("//div[@class='ag-header ag-pivot-off']");
	
	@FindBy(xpath = "//*[@id='gridContainer']/div/div/div/div[2]/div[1]/div[3]/div[2]/div/div/div")
	WebElement gridRow1;
	
	private By gridRows = By.xpath("//*[@id='gridContainer']/div[1]/div[1]/div[1]/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]");
	
//	private By gridRows = By.xpath("//div[@class='ag-center-cols-container']");
	
    private By cellLocator = By.xpath(".//div[contains(@role, 'gridcell') and contains(@class, 'ag-cell-value')]");
	
	private By noRowsLocator = By.xpath(".//div[contains(@ref, 'eOverlay')]/div/div/span[contains(text(), 'No Rows To Show')]");
	
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
	
	@FindBy(xpath = "//button[@id='gridPoDistro']")
	WebElement distroButton;
 	
	@FindBy(xpath = "//div[@role='gridcell'][@col-id='inhouseUpc']")
	WebElement inHouseUpClick;
	
		
    public String clickFirstGridCell(String columnName) {
    	
    	log.info("Started to validate Receipt number based on Po");
    	getWait(5).until(visibilityOf(gridHeader1));
    	gridPanel.click();

    	log.info("Clicked gridPanel");
        //scrollElementIntoView(gridToolbar);

        WebElement gridHeaderLocator = driver.findElement(gridHeader);
        log.info("GridHeaderLocator");
        List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("div/div/div"));
        log.info("HeaderList", headerList);
		List<WebElement> gridRowsLocator = driver.findElements(gridRows);
        List<String> columnsList = new ArrayList<>();

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
            	String columnname = column.getText().toString();
                String colID = getColID(columnname);
               
             
               WebElement cellContent = gridRowsLocator.get(0).findElement(By.xpath("/div/div[(@col-id='" + colID + "')]/div/button"));
               log.info(cellContent.getText().toString());
              
               if(columnName.equalsIgnoreCase("In-House UPC")) {
            	   String cellContentText = cellContent.getText().toString();
            	   
            	   cellContent.click();
            	   log.info("Selected Column"+ colID);
            	   return cellContentText;
            	   
               }
               else {
           
               cellContent.click();
               log.info("Selected Column"+ colID);

               
                break;
               }
            }
        }
        return null;
      
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
	 
	protected Boolean isElementDisplayed(By locator) {
		try {
			waitForElement(locator, 5);
			return driver.findElement(locator).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}




	protected WebElement waitForElement(By locator, int timeout) {
		int count = 0;
		while (count < timeout) {
			try {
				return driver.findElement(locator);
			} catch (NoSuchElementException e) {
				count++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					log.error("Error: ",e1);
				}
			}
		}
		log.info("Element could not be found " + locator);
		return null;
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

	 public HashMap<String,String> getPoDistroPageValidation(String skuUpc) throws SQLException, Exception{
		 

		  	HashMap<String,String> distroAttributeMap = new HashMap<String,String>();
		
		 	
		 	 String poNbr = poNbrValue.getAttribute("value").toString();
		 	 String rcptNbr = rcptNbrValue.getAttribute("value").toString();
		 	 
		 	  
		 	String query= String.format(SQLPo4Walls.GET_BARCODE, poNbr, rcptNbr,skuUpc);
		 	log.info("Query" ,query); 
		 	
		 	String polineslist = DBUtils.getDBValueInString("pofourwalls", query);
		    log.info("PO lines {}", polineslist);
		 	
		 	
		 	String response = CommonUtils.getRequestResponse(PO4WallEndPoint.PO4WALL_GET_POLINE_DETAILS + polineslist);
			log.info("DRR service response {}", response);
				
		 		log.info("JSON response {}", response);
		 		
		 		if(response != null){
		 			JSONObject jsonObject = new JSONObject(response);

		 			distroAttributeMap.put("PolineBarcode",String.valueOf(jsonObject.get("poLineBarCode")));
		 			distroAttributeMap.put("VendorNumericDesc",String.valueOf(jsonObject.get("vndNumericDesc")));
		 			distroAttributeMap.put("ReportID",String.valueOf(jsonObject.get("reportId")));
		 			distroAttributeMap.put("Receipt NBR",String.valueOf(jsonObject.get("rcptNbr")));
		 			distroAttributeMap.put("Dept", String.valueOf(jsonObject.get("deptNbr")));
		 			distroAttributeMap.put("LOCN NBR", String.valueOf(jsonObject.get("locnNbr")));
		 			distroAttributeMap.put("PO NBR", String.valueOf(jsonObject.get("poNbr")));
		 			distroAttributeMap.put("PID", String.valueOf(jsonObject.get("pid")));
		 			distroAttributeMap.put("MarkStyle", String.valueOf(jsonObject.get("markstyle")));
		 			distroAttributeMap.put("PO Line Itm", String.valueOf(jsonObject.get("poLineItm")));
		 			distroAttributeMap.put("VendStyle", String.valueOf(jsonObject.get("vendStyle")));
		 			distroAttributeMap.put("PID Description", String.valueOf(jsonObject.get("pidDesc")));
		 			distroAttributeMap.put("color", String.valueOf(jsonObject.get("color")));
		 			distroAttributeMap.put("Colour Description", String.valueOf(jsonObject.get("colorDesc")));
		 			distroAttributeMap.put("Size", String.valueOf(jsonObject.get("size")));
		 			distroAttributeMap.put("Size Description", String.valueOf(jsonObject.get("sizeDesc")));
		 			distroAttributeMap.put("processArea", String.valueOf(jsonObject.get("processArea")));
		 			distroAttributeMap.put("TicketType", String.valueOf(jsonObject.get("ticketType")));
		 			distroAttributeMap.put("Inhouseupc", String.valueOf(jsonObject.get("skuUpc")));
		 			distroAttributeMap.put("Prep1", String.valueOf(jsonObject.get("prep1")));
		 			distroAttributeMap.put("Expected Quantity", String.valueOf(jsonObject.get("ordqty")));
		 			distroAttributeMap.put("Recieved Quantity", String.valueOf(jsonObject.get("rcvqty")));



		 		}
		 				
		 		else {
		 			log.info("response is empty for PO");}

		 		return distroAttributeMap;
		 
		 /*
	       HashMap<String,HashMap<String,String>> receiptDistroAttributeMap = new HashMap<String,HashMap<String,String>>();
	 	   String poNbr = poNbrValue.getAttribute("value").toString();
	 	   String rcptNbr = rcptNbrValue.getAttribute("value").toString();
	 	   String query = String.format(SQLPo4Walls.GET_BARCODE, poNbr, rcptNbr,skuUpc);
           String polines = DBUtils.getDBValueInString("pofourwalls", query);
           log.info("POlines: "+polines);
 	 	   String response = CommonUtils.getRequestResponse(OrderEndPoint.ORDER_SERVICE_PO_LINE+polines);
	 	   log.info("JSON response {}", response);
	 		
	 		if(response != null){
	 			JSONObject jsonObject = new JSONObject(response);
	 					HashMap<String,String> poDistroMap = new HashMap<String,String>();
	 					poDistroMap.put("Receipt NBR",String.valueOf(jsonObject.get("rcptNbr")));
	 					poDistroMap.put("PO NBR", String.valueOf(jsonObject.get("poNbr")));
	 					poDistroMap.put("Dept", String.valueOf(jsonObject.get("deptNbr")));
	 					poDistroMap.put("VendDesc", String.valueOf(jsonObject.get("vndNumericDesc")));
	 					poDistroMap.put("Inhouseupc", String.valueOf(jsonObject.get("skuUpc")));
	 					poDistroMap.put("Mark Style", String.valueOf(jsonObject.get("markstyle")));
	 					poDistroMap.put("Vendor Style", String.valueOf(jsonObject.get("vendStyle")));
	 					poDistroMap.put("PID", String.valueOf(jsonObject.get("pid")));
	 					poDistroMap.put("PID Description", String.valueOf(jsonObject.get("pidDesc")));
	 					poDistroMap.put("Colour Description", String.valueOf(jsonObject.get("colorDesc")));
	 					poDistroMap.put("Size Description", String.valueOf(jsonObject.get("sizeDesc")));
	 					poDistroMap.put("Expected Quantity", String.valueOf(jsonObject.get("ordqty")));
	 					poDistroMap.put("Recieved Quantity", String.valueOf(jsonObject.get("rcvqty")));

	 					    receiptDistroAttributeMap.put(String.valueOf(jsonObject.get("rcptNbr")), poDistroMap);
	 					}
	 				
	 		else {
	 			log.info("response is empty for PO");
	 		}
	 		
	 		
	 		return receiptDistroAttributeMap;  */
	 	}
	 
	 public String distroPageFieldValidation(String skuUpc) throws SQLException, Exception {
	
		 
/*	ArrayList<String> skuList = getSKUListfromUI();
	for(String sku : skuList){
	
	 
	 */
     log.info("distroPageFieldValidation");
     HashMap<String,String> attributesVal = getPoDistroPageValidation(skuUpc);
	// log.info("List of PO Details," +poDetailsList);
	 String rcptNbrInPage = rcptNbrValue.getAttribute("value").toString();
	 String vendNbrInPage = vendorValue.getAttribute("value").toString();
	 String deptNbrInPage = deptNbrValue.getAttribute("value").toString();
	 String poNbrInPage = poNbrValue.getAttribute("value").toString();
	 String inHouseUpcPage="";


//	 HashMap<String,String> attributesVal = poDetailsList.get(rcptNbrInPage);
	 log.info("Attributes List" +attributesVal);
	 
	 Assert.assertEquals(attributesVal.get("Receipt NBR").toString(),rcptNbrInPage);
	 Assert.assertEquals(attributesVal.get("VendorNumericDesc").toString(),vendNbrInPage);
	 Assert.assertEquals(attributesVal.get("Dept").toString(),deptNbrInPage);
	 Assert.assertEquals(attributesVal.get("PO NBR").toString(),poNbrInPage); 
	 
	 int ExpectedUnits =0;

	 List<WebElement> gridRowsLocator = driver.findElements(By.xpath("//*[@class='ag-center-cols-container']/div[@role='row']"));
	 for (WebElement row : gridRowsLocator) {
			inHouseUpcPage = row.findElement(By.xpath("//div[(@col-id='inhouseUpc') and (@role='gridcell')]")).getText().toString();
		log.info("inHouseUpcPage" +inHouseUpcPage);		
       	if(!(inHouseUpcPage.isEmpty())) {
				
             Assert.assertEquals(attributesVal.get("Inhouseupc"), row.findElement(By.xpath("//div[(@col-id='inhouseUpc') and (@role='gridcell')]")).getText().toString());
             Assert.assertEquals(attributesVal.get("MarkStyle"), row.findElement(By.xpath("//div[(@col-id='mkStyl') and (@role='gridcell')]")).getText().toString());
             Assert.assertEquals(attributesVal.get("VendStyle"), row.findElement(By.xpath("//div[(@col-id='vndStyle') and (@role='gridcell')]")).getText().toString());
             Assert.assertEquals(attributesVal.get("PID").toString(), row.findElement(By.xpath("//div[(@col-id='pid') and (@role='gridcell')]")).getText().toString());
             Assert.assertEquals(attributesVal.get("PID Description"), row.findElement(By.xpath("//div[(@col-id='pidDesc') and (@role='gridcell')]")).getText().toString());
             Assert.assertEquals(attributesVal.get("Colour Description"), row.findElement(By.xpath("//div[(@col-id='colDesc') and (@role='gridcell')]")).getText().toString());
             Assert.assertEquals(attributesVal.get("Size Description"), row.findElement(By.xpath("//div[(@col-id='sizeDesc') and (@role='gridcell')]")).getText().toString());
             
         //   String expectedUnits = row.findElement(By.xpath("//div[(@col-id='expectedUnits') and (@role='gridcell')]")).getText().toString();
            		
          //  		 ExpectedUnits = ExpectedUnits + Integer.parseInt(expectedUnits);
          /*   
             if(String.valueOf("//div[(@col-id='actualUnits') and (@role='gridcell')]").equalsIgnoreCase("")) {
            	String nullactualUnits = "0";
            	 Assert.assertEquals(attributesVal.get("Recieved Quantity").toString(), nullactualUnits);
                 log.info("PO Distro details are validated");
         			}
             else 
            	 Assert.assertEquals(attributesVal.get("Recieved Quantity").toString(), row.findElement(By.xpath("//div[(@col-id='actualUnits') and (@role='gridcell')]")).getText().toString());
                 log.info("PO Distro details are validated");
            */	
       	}
       	else 
       	             
       		
 			log.info("PO Distro details not found");
       	
		 }
	 
	  //  Assert.assertEquals(attributesVal.get("Expected Quantity"),String.valueOf(ExpectedUnits));
			
    	return null;
			
}

	public void clickHeaderCheckbox() {
		// TODO Auto-generated method stub
		headerCheckBox.click();
		
	}


	public void distroClick() {
		distroButton.click();
		
	}


	public void clickInHouseUPCCell() throws InterruptedException {
	
	//	inHouseUpClick.click();
		List<String> skuNumberService = new ArrayList<String>();
		TimeUnit.SECONDS.sleep(30);
		ArrayList<String> skuList = getSKUListfromUI();
		
	}
	
	public ArrayList<String> getSKUListfromUI() throws InterruptedException{
		ArrayList<String> skuList = new ArrayList<String>();
		List<WebElement> listOfSKUElements = driver.findElements(By.xpath("//div[@role='gridcell'][@col-id='inhouseUpc']"));
		for(WebElement element : listOfSKUElements){
			skuList.add(String.valueOf(element.getAttribute("value")));
			element.click();
			TimeUnit.SECONDS.sleep(30);
			break;
		}
		return skuList;
	}


	public void clickPO(String po) {
		
		poLink.click();
		
	}


	public void clickInHouseUPCCell(String skuUpc) throws InterruptedException {
		
		TimeUnit.SECONDS.sleep(30);
		ArrayList<String> skuList = getSKUListfromUI(skuUpc);
		
	}


	private ArrayList<String> getSKUListfromUI(String skuUpc) throws InterruptedException {
		ArrayList<String> skuList = new ArrayList<String>();
		List<WebElement> listOfSKUElements = driver.findElements(By.xpath("//div[@role='gridcell'][@col-id='inhouseUpc']"));
		for(WebElement element : listOfSKUElements){
			skuList.add(String.valueOf(element.getAttribute("value")));
			if(String.valueOf(element.getAttribute("value")).equalsIgnoreCase(skuUpc))
			element.click();
			TimeUnit.SECONDS.sleep(30);
			break;
		}
		return skuList;
	}


	public void navigatepodetails() {
		
		getWait(10).until(visibilityOf(podetailsbreadcrumb));
		podetailsbreadcrumb.click();
		
	}
	
	 

	
}