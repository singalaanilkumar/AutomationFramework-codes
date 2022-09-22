package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderSelectionAndPrintingPage extends BasePage {

    public OrderSelectionAndPrintingPage(WebDriver driver) {
        super(driver);
    }


    public DropdownUtils Dropdown = new DropdownUtils(driver);
    public InputUtils Input = new InputUtils(driver);
    public GridUtils Grid = new GridUtils(driver);
    StepsDataStore dataStorage = StepsDataStore.getInstance();


    @FindBy(xpath = "//mat-panel-title[text()='Search/Selection Criteria']")
    public WebElement Criteria;

    @FindBy(xpath = ".//*[@id='cdk-accordion-child-0']/div/form/div[15]/button[1]")
    WebElement searchButton;

    @FindBy(xpath = ".//*[@id='cdk-accordion-child-0']/div/form/div[15]/button[2]")
    WebElement resetButton;

    @FindBy(xpath = ".//*[@id='cdk-accordion-child-0']/div/form/div[15]/button[3]")
    WebElement orderDensityButton;

    // preview batch flow web element
    @FindBy(xpath = ".//*[@class='btn-grp ng-star-inserted']/button[1]")
    WebElement previewBatchButton;

    @FindBy(xpath = "//div[text()='Confirm - Preview Batch']")
    WebElement confirmPreviewBatchDialog;

   // By cellLocator = By.xpath(".//td[contains(@role, 'gridcell') and contains(@class, 'mat-cell cdk')]");

    @FindBy(xpath = "//span[contains(text()='order(s) are selected. Please click 'Yes' to continue.']")
    WebElement orderNumbersInDiolog;

    @FindBy(xpath = ".//*[@class='btns btn-grp']/button[1]")
    WebElement yesButtonDiolog;

    @FindBy(xpath = ".//*[@class='btns btn-grp']/button[2]")
    WebElement noButtonDialog;

    @FindBy(xpath = ".//*[@class='popup-box no-printer-name']")
    WebElement popupReservationsForPreview;

    @FindBy(xpath = ".//*[@class='popup-box no-printer-name']/div[2]/div/input")
    WebElement maxReservationsPerBatch;

    @FindBy(xpath = ".//*[@class='popup-box no-printer-name']/div[3]/div/input")
    WebElement maxReservationsPerPrint;

    @FindBy(xpath = ".//*[@class='block row btn-grp']/button[1]")
    WebElement previewButtonInPopup;

    @FindBy(xpath = ".//*[@class='block row btn-grp']/button[2]")
    WebElement cancelButtonInPopup;

   //submit batch flow web element
    @FindBy(xpath = ".//*[@class='btn-grp ng-star-inserted']/button[2]")
    WebElement submitBatchButton;

    @FindBy(xpath = "//div[text()='Confirm - Submit Batch']")
    WebElement confirmSubmitBatchDialog;

    public boolean isSubmitPopupDisplayed(){
        waitForElement(By.xpath("//div[text()='Confirm - Submit Batch'"), 5);
        return   confirmSubmitBatchDialog.isDisplayed();
    }


    @FindBy(xpath = ".//*[@class='popup-box']")
    WebElement popupReservationsInSubmit;

    @FindBy(xpath = ".//*[@class='popup-box']/div[2]/div/app-select")
    WebElement printerNameInSubmit;

    //*[@id="cdk-overlay-0"]/div/div/div[5]/i
//*[@id="cdk-overlay-0"]/div/div/div[6]/i

    public void setPrinterOptionInDropdown( String option) throws InterruptedException{
        List<WebElement> allOptions = getDropdownListOfOptionsForPrinter();
        boolean found = false;
        for (WebElement allOption : allOptions) {
            String s=allOption.getText() ;
            if (allOption.getText().contains(option)) {
                found = true;
                Thread.sleep(3000);
                allOption.click();

                break;
            }
        }
    }

    public List<WebElement> getDropdownListOfOptionsForPrinter() {
        printerNameInSubmit.click();
        List<WebElement> options=driver.findElements(By.xpath(" .//*[@id='cdk-overlay-0']/div/div/div"));
        return options;

    }

    @FindBy(xpath = ".//*[@class='popup-box']/div[3]/div/input")
    WebElement maxReservationsPerBatchInSubmit;

    @FindBy(xpath = ".//*[@class='popup-box']/div[3]/div/input")
    WebElement maxReservationsPerPrintInSubmit;



    public void clickSearchButton() {
        waitForElement(By.xpath(".//*[@id='cdk-accordion-child-0']/div/form/div[15]/button[1]"), 5);
        searchButton.click();
        waitForElement(Grid.filterResultTable, 10);
    }

    public void clickResetButton() {
        waitForElement(By.xpath(".//*[@id='cdk-accordion-child-0']/div/form/div[15]/button[2]"), 5);
        resetButton.click();
        waitForElement(Grid.filterResultTable, 10);
    }

    public void clickPreviewBatchButton(){
      //  waitForElement(By.xpath("//span[text()='Preview Batch'"), 5);
        getWait().until(ExpectedConditions.elementToBeClickable(previewBatchButton));
        previewBatchButton.click();
    }
    public void clickSubmitBatchButton(){
        //  waitForElement(By.xpath("//span[text()='Preview Batch'"), 5);
        getWait().until(ExpectedConditions.elementToBeClickable(submitBatchButton));
        submitBatchButton.click();
    }
    public boolean isPreviewPopupDisplayed(){
        waitForElement(By.xpath("//div[text()='Confirm - Preview Batch'"), 5);
      return   confirmPreviewBatchDialog.isDisplayed();
    }
    public void clickYesInPreviewSubmitBatchPopup(){
        waitForElement(By.xpath(".//*[@class='btns btn-grp']/button[1]"), 5);
        yesButtonDiolog.click();
    }
    public void clickNoInPreviewSubmitBatchPopup(){
        waitForElement(By.xpath(".//*[@class='btns btn-grp']/button[2]"), 5);
        noButtonDialog.click();
    }

    public boolean ispopupReservationsDisplayedForPreview(){
      //  waitForElement(By.xpath(".//*[@class='popup-box']/div"), 5);
        return popupReservationsForPreview.isDisplayed();
    }

    public boolean ispopupReservationsDisplayedForSubmit(){
        //  waitForElement(By.xpath(".//*[@class='popup-box']/div"), 5);
        return popupReservationsInSubmit.isDisplayed();
    }
    public void setMaxReservationsPerBatchForPreview(String input){

        maxReservationsPerBatch.clear();
        maxReservationsPerBatch.click();
        maxReservationsPerBatch.sendKeys(input);
    }

    public void setMaxReservationsPerPrintForPreview(String input){
        maxReservationsPerPrint.clear();
        maxReservationsPerPrint.click();
        maxReservationsPerPrint.sendKeys(input);
    }

    public void setMaxReservationsPerBatchForSubmit(String input){

        maxReservationsPerBatchInSubmit.clear();
        maxReservationsPerBatchInSubmit.click();
        maxReservationsPerBatchInSubmit.sendKeys(input);
    }

    public void setMaxReservationsPerPrintForSubmit(String input){
        maxReservationsPerPrintInSubmit.clear();
        maxReservationsPerPrintInSubmit.click();
        maxReservationsPerPrintInSubmit.sendKeys(input);
    }

    public void clickPreviewButtonInPopup(){
        previewButtonInPopup.click();
    }
    public void clickCancelButtonInPopup(){
        cancelButtonInPopup.click();
    }

    public boolean isSearchedDataDisplayedInTheGrid(String columnName)
    {
        String searchedValue = dataStorage.getStoredData().get("search_value").toString();

        if(Grid.getTotalNumberOfPagesOfResults()>0)
        {
            for(int i=0;i<Grid.getTotalNumberOfPagesOfResults();i++)
            {
                ArrayList<Map<String, String>> map = Grid.getColumnData(columnName);
                for(Map<String,String> row: map) {
                    if (!row.get(columnName).equals(searchedValue)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return true;
    }


}
