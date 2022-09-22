package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.PageObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
@Slf4j
@Data
public class SCMUIPage  extends PageObject {

    public SCMUIPage(WebDriver driver) {
        super(driver);
    }
	WebDriverWait wait=new WebDriverWait(driver,5);
    public String hostName = "https://dev-backstage.devops.fds.com";

    public static LinkedList<Long> UPCsForRelease = new LinkedList<Long>();

    public String reportingResponse = null;
    @FindBy(xpath = "//*[@id='username']")
    WebElement userName;

    @FindBy(xpath = "//*[@id='password']")
    WebElement password;

    @FindBy(xpath = "//*[@type='submit']")
    WebElement submitButton;

    @FindBy(xpath = "//span[contains(text(),'PO Dashboard')]")
    WebElement poDashboard;
    
	@FindBy(xpath = "//Input[@name='poNbr']")
	private WebElement poNbrBox;
	
    @FindBy(xpath = "//span[contains(text(),'Logout')]")
    WebElement logout;

    @FindBy(xpath = "//span[contains(text(),'CLEAR')]")
    private WebElement clearButton;

    protected By searchBox = By.id("searchBox");
     
    @FindBy(xpath = "//div[@row-id='0']/div[@col-id='inhouseUpc'][@role='gridcell']")
	WebElement firstSkuUpcElement;
    
    @FindBy(xpath = "//p[contains(text(),'PO Details')]")
	WebElement poDetailsBreadCrumb;

    @FindBy(xpath = "//button[@id='gridPoDistro']")
	WebElement distroButton;
  
    @FindBy(xpath = "//p[contains(text(),'PO Distro')]")
	WebElement poDistroBreadCrumb;
    
	@FindBy(xpath = "//div[@row-id='0']/div[@col-id='inhouseUpc']")
	WebElement skuElement;
    
    @FindBy(xpath = "//button[(@id = 'searchButton') and not(@disabled)]")
    private WebElement searchEnable;
    
    @FindBy(xpath = "//div[@col-id='poNbr'][@role='gridcell']")
    private WebElement poNbrLink;
    
    @FindBy(xpath = "(//div[@col-id='reportId']//button)[1]")
    private WebElement reportIDLink;
    
	@FindBy(xpath = "//input[@name='rcptNbr']")
	private WebElement receiptNbr;
	
	@FindBy(xpath = "//input[@name='poNbr']")
	private WebElement poNumber;
	
	@FindBy(xpath = "//p[text()='Release Receipt']")
    private WebElement releasePageBreadCrumb;
	
	@FindBy(xpath = "//p[text()='Release Receipt']")
    private WebElement wholeInnerReleasebutton;
	
	@FindBy(xpath = "//div[@class='ag-header-select-all ag-labeled ag-label-align-right ag-checkbox'][@ref='cbSelectAll']/div/div/span[@class='ag-icon ag-icon-checkbox-checked']")
    private WebElement selectAllCheckBox;   

    private By searchByInputs = By.xpath(".//*[@id='searchBox']/div/div/div[2]/div/div/div/div");

    @FindBy(xpath = "//button[(@id = 'searchButton')]")
    private WebElement searchButton;

    @FindBy(xpath = "(//*[@id=\"gridContainer\"]//div[@class=\"ag-react-container\"])[6]")
    private WebElement releaseButton;

    @FindBy(xpath = "//span[contains(text(),'PO NBR')]")
    private WebElement poNbr;

    @FindBy(xpath = "//span[contains(text(),'PO NBR')]")
    private WebElement firstUpc;
    
    @FindBy(xpath = "(//div[@col-id='reportId']//button)[1]")
    private WebElement reportIDElement;
    
    @FindBy(xpath = "//div[@col-id='xpctdCntrQty'][@role='gridcell']")
    private WebElement expectedCartonElement;
    
    @FindBy(xpath = "//div[@col-id='actCartons'][@role='gridcell']")
    private WebElement actualCartonElement;
    
    @FindBy(xpath = "//div[@col-id='apptNo'][@role='gridcell']")
    private WebElement apptNum;

    
    @FindBy(xpath="((//div[@style='display: inherit;']/p)[1])")
    private WebElement BarcodeValue;
    
    private By BarcodeValues = By.xpath(".//div[@style='display: inherit;']/p");
    
    @FindBy(xpath="//div[@id='print-content']/div/button/span[1]")
    private WebElement PrintButton;
    
    @FindBy(xpath = "//div[@id='drrHeader']")
    private WebElement DRRHeaderValue;
    
    @FindBy(xpath = "//div[@id= 'gridSection']")
      private WebElement filterResultTable; 
    
    public By filterTable = By.id("gridSection");
    
    @FindBy(xpath = "(//div[@row-id='0']/div/div/span/span[@class='ag-icon ag-icon-checkbox-unchecked'])[1]")
	WebElement firstSkuElementCheckbox;
    
    @FindBy(xpath = "//button[contains(text(),'PO Dashboard')]")
    private WebElement poDashboardBreadCrumb;

    private String prodhostname = "https://msc.gcp.cloudrts.net";
    private String username = "";
    private String userpassword = "";
    
    private WebDriverWait getWait(int secs) {
        WebDriverWait wait = new WebDriverWait(driver, secs);
        return wait;
    }

    public void loginProdSCM() throws Exception {

    	driver.get(prodhostname);
    	
        getWait(20).ignoring(Exception.class).until(visibilityOf(userName));
        userName.clear();
        userName.sendKeys(username);
        getWait(20).ignoring(Exception.class).until(visibilityOf(password));
        password.clear();
        password.sendKeys(userpassword);

		wait.until(ExpectedConditions.visibilityOf(submitButton));
        submitButton.click();

        log.info("Logged into PROD SCM UI with user: "+username);
    }

    public void selectPODashboard() {
		wait.until(ExpectedConditions.visibilityOf(poDashboard));
		poDashboard.click();
        log.info("PO Dashboard selected");
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
    
    public void verifyBarcodes(){
    	log.info("BarCode Verifications");
		wait.until(ExpectedConditions.visibilityOf(BarcodeValue));
    	String barcodeText=BarcodeValue.getText();
        log.info("Barcode is displayed{}",barcodeText);
    	
    }
    
    public void clickNavOption(String option) {
		WebElement navOption = driver.findElement(By.xpath("//span[contains(text(),'" + option + "')]"));
		navOption.click();
		log.info("Navigating to "+option+" UI");
		StepDetail.addDetail("Navigating to "+option+" UI", true);
	}
    
    public void clickSearchButton() throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOf(searchButton));
		searchButton.click();
		wait.until(ExpectedConditions.visibilityOf(filterResultTable));
		log.info("Clicking on Search button");
		StepDetail.addDetail("Clicking on Search button", true);
		boolean resulttable = isSearchResultTableDisplayed();
		Assert.assertEquals(true, resulttable);

    }
    
    public void clickClearButton() {
		wait.until(ExpectedConditions.visibilityOf(clearButton));
    	scrollElementIntoView(driver.findElement(searchBox));
        clearButton.click();
        log.info("Clicking on clear button");
        StepDetail.addDetail("Clicking on clear button", true);
    }
    
    
    public void verifyDRRHeader(){
    	log.info("Header Verifications");
		wait.until(ExpectedConditions.visibilityOf(DRRHeaderValue));
		  if (DRRHeaderValue.isDisplayed()) {
			   String HeaderText = DRRHeaderValue.getText();
			   log.info("Current Header is displayed {}",HeaderText);
			   Assert.assertEquals("Home>PO Inquiry>Detail Receiving Report", HeaderText);
			   
	            log.info("Current Header is displayed {}",HeaderText);
	            StepDetail.addDetail("Current Header displayed", true);
	        }
		  
    }

	public void verifyPrintOptions() throws InterruptedException {
		try
		{
			WebElement printObject = driver.findElement(By.xpath("//div[@id='print-content']/div/button/span[1]"));
			printObject.click();
		}
		catch(Exception e)
		{
			WebElement svgObj = driver.findElement(By.xpath("//div[@id='print-content']/div/button/span[1]/img"));
			Actions actionBuilder = new Actions(driver);
			actionBuilder.click(svgObj).build().perform();
		}
		
	}

	
	protected void scrollElementIntoView(WebElement locator) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView(true);", locator);
	}
	
	public void verifyInquiryPage() {
		
		wait.until(ExpectedConditions.visibilityOf(reportIDElement));
		  if (reportIDElement.isDisplayed()) {
			   String reportId = reportIDElement.getText();
	            log.info("ReportID is displayed {}",reportId);
	            StepDetail.addDetail("ReportID is displayed", true);
	        }
		wait.until(ExpectedConditions.visibilityOf(expectedCartonElement));
		if (expectedCartonElement.isDisplayed()) {
			   String expectedCarton = expectedCartonElement.getText();
	            log.info("ExpectedCarton is displayed {}",expectedCarton);
	            StepDetail.addDetail("ExpectedCarton is displayed", true);
	        }

		wait.until(ExpectedConditions.visibilityOf(apptNum));
		if (apptNum.isDisplayed()) {
			   String AppointmentNum = apptNum.getText();
	            log.info("Appointment Number is displayed {}",AppointmentNum);
	            StepDetail.addDetail("Appointment Number  is displayed", true);
	        }
		  
		  
	}

	public void clickPO(String poNbr) {
		wait.until(ExpectedConditions.visibilityOf(poNbrLink));
		poNbrLink.click();
		 log.info("Po Number link is clicked");
	}
	
	public void verifyPODetails() throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOf(poDetailsBreadCrumb));
		log.info("PO Details page bread Crumb is displayed");
	
    	wait.until(ExpectedConditions.visibilityOf(poNumber));
		log.info("PO Number is displayed");
		
		wait.until(ExpectedConditions.visibilityOf(receiptNbr));
		log.info("Receipt Number is displayed");
		
		wait.until(ExpectedConditions.visibilityOf(firstSkuUpcElement));
		String SelectedSkuNum = firstSkuUpcElement.getText();
		log.info("Sku number is displayed: "+SelectedSkuNum);
		
		log.info("PO Details page is verified");
		
		
	}

    public void navigatetoDistro() throws InterruptedException {
		
    	scrollElementIntoView(driver.findElement(filterTable));
		wait.until(ExpectedConditions.visibilityOf(firstSkuElementCheckbox));
		log.info("first SkuElement Checkbox is displayed");
		firstSkuElementCheckbox.click();
		wait.until(ExpectedConditions.visibilityOf(firstSkuUpcElement));

		String SkuNum = firstSkuUpcElement.getText();
		
		wait.until(ExpectedConditions.visibilityOf(distroButton));
		log.info("distroButton is displayed");
		distroButton.click();

    }
    
    public void verifydistroPage() throws InterruptedException {

		wait.until(ExpectedConditions.visibilityOf(poDistroBreadCrumb));
    	log.info("po Distro Bread Crumb is displayed");
		wait.until(ExpectedConditions.visibilityOf(skuElement));
    	log.info("skuElement is displayed");
	}
    
    
    public void searchDashboard(String poNbr) throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOf(poDashboardBreadCrumb));
		log.info("Po Dashboard bread Crumb is displayed");
		
	    StepDetail.addDetail("poDashboardBreadCrumb is displayed ", true);
	    
		wait.until(ExpectedConditions.visibilityOf(clearButton));
		TimeUnit.SECONDS.sleep(30);
		clearButton.click();

		log.info("search Dashboard");
		wait.until(ExpectedConditions.visibilityOf(poNbrBox));
		poNbrBox.clear();
		poNbrBox.sendKeys(poNbr);
		waitForElement(By.xpath("//button[(@id = 'searchButton') and not(@disabled)]"),10);
		wait.until(ExpectedConditions.visibilityOf(searchButton));
		TimeUnit.SECONDS.sleep(30);
        searchButton.click();
		wait.until(ExpectedConditions.visibilityOf(filterResultTable));
       log.info("Clicking on Search button");
        StepDetail.addDetail("Clicking on Search button", true);

	}

    
	public void verifyPODashboard(String PoNbr) throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOf(poNbrLink));
	    String poNumText = poNbrLink.getText();

        log.info("Po number link is displayed {}", poNumText);
	    StepDetail.addDetail("PO number link is displayed "+poNumText, true);
	    StepDetail.addDetail("PO dashboard screen is verified ", true);

	}
    
	public boolean isSearchResultTableDisplayed() {
		try {
			waitForElement(filterTable, 3);
			if (driver.findElement(filterTable).isDisplayed()) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public void clickReportID() throws InterruptedException {
		scrollElementIntoView(driver.findElement(filterTable));
		TimeUnit.SECONDS.sleep(30);
		reportIDLink.click();
		log.info("Report ID Link is clicked");
	}
	
	public void releasePOInDashboard() throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOf(poNbr));
        log.info("Release Operation to Begin");
		wait.until(ExpectedConditions.visibilityOf(releaseButton));
        releaseButton.click();
     }

	public void verifyReleasePage() throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOf(releasePageBreadCrumb));

	log.info("Release page bread Crumb is displayed");
	
    StepDetail.addDetail("Release page bread Crumb is displayed ", true);
		wait.until(ExpectedConditions.visibilityOf(wholeInnerReleasebutton));
	log.info("Inner Release button is displayed");
	
    StepDetail.addDetail("Inner Release button is displayed ", true);
		wait.until(ExpectedConditions.visibilityOf(selectAllCheckBox));

	log.info("select All CheckBox is displayed");
	
    StepDetail.addDetail("select All Check Box is displayed ", true);
    StepDetail.addDetail("PO Release page is verified", true);
	
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
}
