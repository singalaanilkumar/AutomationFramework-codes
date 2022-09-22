package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class BatchEnquaryPage extends BasePage {

    public BatchEnquaryPage(WebDriver driver) {
        super(driver);
    }

    public GridUtils Grid = new GridUtils(driver);
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    public  InputUtils input=new InputUtils(driver);

    private By gridToolbar = By.xpath("//div[@class='mat-paginator-container']");
    private By gridHeaderNew = By.xpath(".//*[@class='compact streched mat-table']/thead/tr");
    private By gridRows = By.xpath(".//*[@class='compact streched mat-table']/tbody/tr");

    private By checkRows = By.xpath(".//*[@class='compact streched mat-table']/tbody/tr[1]/td[1]/mat-checkbox/label/div");

    Actions action= new Actions(driver);
    @FindBy(xpath = "//*[@id='mat-checkbox-3'']/label/div")
    WebElement checkBox;

  private  By checkElement=By.xpath(".//*[@class='mat-checkbox mat-primary']");

    public void clickCheckbox1(String rowNo) throws InterruptedException{
        driver.findElement(checkRows).click();
     //   checkBox.click();
    }

    public void clickCheckbox(String rowNo) throws InterruptedException {

        List<WebElement> gridRowsLocators = driver.findElements(checkElement);

        for (WebElement inputField : gridRowsLocators) {
            try {

//*[@id="mat-checkbox-172"]/label/div

                String s= inputField.getAttribute("id");
                By checkElement=By.xpath("//*[@id='"+s+"']/label/div");
                driver.findElement(checkElement).click();
              //  WebElement inputLabel1 = inputLabel.findElement(By.xpath("/div"));
              //  inputLabel1.click();

                  //  inputLabel.click();

                    break;

            } catch (Exception ignored) {
            }
        }

/*
        for(int i = 1; i<=gridRowsLocator.size(); i++) {
            if (Integer.parseInt(rowNo) == i) {

                WebElement cellContent = gridRowsLocator.get(i - 1).findElement(By.xpath(".//mat-checkbox"));
              // Thread.sleep(3000);
            //   getWait().until(ExpectedConditions.elementToBeClickable(cellContent));

                cellContent.click();
                break;
            }

        }*/

    }





    public void clickSearchOrResetButton(String fieldName){

        waitForElement(searchByCriteria, 10);
        List<WebElement> searchInputsLocators = driver.findElements(By.xpath(".//*[@class='criteria']/div/button"));
        for (WebElement inputField : searchInputsLocators) {
            try {

                WebElement inputLabel = inputField.findElement(By.xpath(".//span"));
                if (inputLabel.getText().replace(" *", "").equals(fieldName)) {
                    LOGGER.info("Entering data into field " + inputLabel.getText());
                   inputLabel.click();

                    break;
                }
            } catch (Exception ignored) {
            }
        }

    }

    By searchByCriteria = By.xpath(".//*[@class='criteria']");
    By getSearchByCriteria=By.xpath(".//*[@class='criteria']/mat-form-field");

    public void typeIntoInputFieldID(String fieldName, String input) throws InterruptedException{
      Thread.sleep(2000);
        //  scrollElementIntoView(driver.findElement(searchBox));
        waitForElement(searchByCriteria, 10);
        List<WebElement> searchInputsLocators = driver.findElements(searchByCriteria);
        for (WebElement inputField : searchInputsLocators) {
            try {

                WebElement inputLabel = inputField.findElement(By.xpath(".//label"));
                if (inputLabel.getText().replace(" *", "").equals(fieldName)) {
                    LOGGER.info("Entering data into field " + inputLabel.getText());
                    WebElement inputFieldLocator = inputField.findElement(By.xpath(".//input"));
                    inputFieldLocator.click();
                    inputFieldLocator.clear();
                    inputFieldLocator.sendKeys(input);
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void selectWaveType(String option, String fieldName) throws InterruptedException{
        Thread.sleep(2000);
        //  scrollElementIntoView(driver.findElement(searchBox));
        waitForElement(searchByCriteria, 10);
        List<WebElement> searchInputsLocators = driver.findElements(getSearchByCriteria);
        for (WebElement inputField : searchInputsLocators) {
            try {

                WebElement inputLabel = inputField.findElement(By.xpath(".//label"));
                if (inputLabel.getText().replace(" *", "").equals(fieldName)) {
                    LOGGER.info("Entering data into field " + inputLabel.getText());
                    WebElement inputFieldLocator = inputField.findElement(By.xpath(".//mat-select"));
                    inputFieldLocator.click();
                    List<WebElement> allOptions=driver.findElements(By.xpath(" .//*[@id='cdk-overlay-0']/div/div/mat-option"));

                    boolean found = false;
                    for (WebElement allOption : allOptions) {
                        String s=allOption.getText() ;
                        if (allOption.getText().equalsIgnoreCase(option)) {
                            found = true;
                            Thread.sleep(3000);
                            allOption.click();

                            break;
                        }
                    }
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }


    // Submit batch

    @FindBy(xpath = "//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[1]")
    WebElement SubmitBatchButton;

    @FindBy(xpath = "//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[2]")
    WebElement CancelBatchButton;

    @FindBy(xpath = "//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[3]")
    WebElement ReprintBatchSheetButton;

    @FindBy(xpath = "//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[4]")
    WebElement ReprintInvoicesButton;

    @FindBy(xpath = "//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[5]")
    WebElement ViewDetailsButton;

    @FindBy(xpath = "//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[6]")
    WebElement ViewBatchSheetButton;


    public void clickAnyOneOfSixButton(String buttonName) {
        if(buttonName.contains("Submit")){
            waitForElement(By.xpath("//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[1]"), 5);
            SubmitBatchButton.click();
        }else if(buttonName.contains("Cancel")){
            waitForElement(By.xpath("//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[2]"), 5);
            CancelBatchButton.click();
        }else if(buttonName.contains("Reprint Batch")){
            waitForElement(By.xpath("//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[3]"), 5);
            ReprintBatchSheetButton.click();
        }else if(buttonName.contains("Reprint Invoices")){
            waitForElement(By.xpath("//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[4]"), 5);
            ReprintInvoicesButton.click();
        }else if(buttonName.contains("View Details")){
            //navigating to po details enquiry page
            waitForElement(By.xpath("//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[5]"), 5);
            ViewDetailsButton.click();
        }else if(buttonName.contains("View Batch")){
            waitForElement(By.xpath("//app-batch-inquiry[@class='ng-star-inserted']/div[2]/div/button[6]"), 5);
            ViewBatchSheetButton.click();
        }
//        scrollElementIntoView(SubmitBatchButton);

    }

    @FindBy(xpath = ".//*[@class='btns btn-grp']/button[1]")
    WebElement yesButtonDiolog;

    @FindBy(xpath = ".//*[@class='btns btn-grp']/button[2]")
    WebElement noButtonDialog;

    public void clickYesOrNoInPreviewSubmitBatchPopup(String YesOrNo){
        if(YesOrNo.equalsIgnoreCase("Yes")){
            waitForElement(By.xpath(".//*[@class='btns btn-grp']/button[1]"), 5);
            yesButtonDiolog.click();
        }else{
            waitForElement(By.xpath(".//*[@class='btns btn-grp']/button[2]"), 5);
            noButtonDialog.click();
        }

    }


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

    @FindBy(xpath = ".//*[@class='popup-box only-printer-name']/div[2]/div/app-select")
    WebElement printerNameInSelectPrinter;

    public List<WebElement> getDropdownListOfOptionsForPrinter() {
        printerNameInSelectPrinter.click();
        List<WebElement> options=driver.findElements(By.xpath(" .//*[@id='cdk-overlay-0']/div/div/div"));
        return options;

    }

    @FindBy(xpath = ".//*[@class='popup-box only-printer-name']/div[3]/button[1]")
    WebElement printButtonInSelectPrinterPopup;

    public void clickPrintButtonInSelectPrinterPopup(){
        waitForElement(By.xpath(".//*[@class='popup-box only-printer-name']/div[3]/button[1]"), 5);
    /*
        List<WebElement> buttons=driver.findElements(By.xpath(" .//*[@class='popup-box only-printer-name']/div[3]"));

        for (WebElement inputField : buttons) {
            try {

                WebElement inputLabel = inputField.findElement(By.xpath(".//span"));
                if (inputLabel.getText().replace(" *", "").equals("Print")) {
                    LOGGER.info("Entering data into field " + inputLabel.getText());
                    WebElement inputFieldLocator = inputField.findElement(By.xpath(".//button"));
                    inputFieldLocator.click();
                    break;
                }
            } catch (Exception ignored) {
            }
        }

*/

        printButtonInSelectPrinterPopup.click();
    }



    @FindBy(xpath = ".//*[@class='dialog']/div[2]")
    WebElement successDialogMsgAfterPrint;

    @FindBy(xpath = ".//*[@class='dialog']/div[3]/button")
    WebElement successDialogCloseAfterPrint;


    public void clickSuccessDialogCloseAfterPrint() throws InterruptedException{
        Thread.sleep(3000);
        List<WebElement> buttons=driver.findElements(By.xpath(" .//*[@class='dialog']/div[3]/button"));

        for (WebElement button : buttons) {
            try {

                WebElement buttonLabel = button.findElement(By.xpath(".//span"));
                if (buttonLabel.getText().replace(" *", "").equals("Close")) {
                    LOGGER.info("Entering data into field " + buttonLabel.getText());
                    String fetchBatchNumberMsg=successDialogMsgAfterPrint.getText();
                    String BatchNumber =fetchBatchNumberMsg.substring(fetchBatchNumberMsg.length() - 9);
                    dataStorage.getStoredData().put("BatchNumber",BatchNumber);
                    button.click();
                    break;
                }
            } catch (Exception ignored) {
            }
        }




     //   waitForElement(By.xpath(".//*[@class='dialog']/div[3]/button"), 5);



    //    successDialogCloseAfterPrint.click();

    }

    public void selectNumberOfRowsPerPage(String numberOfRows) {
        scrollElementIntoView(driver.findElement(gridToolbar));
        List<WebElement> paginator=driver.findElements(By.xpath(" .//*[@class='mat-paginator-outer-container']/div"));

        for (WebElement inputField : paginator) {
            try {

                WebElement inputLabel = inputField.findElement(By.xpath(".//mat-form-field"));
                inputLabel.click();
                List<WebElement> rowsPerPageOptions = driver.findElements(By.xpath(".//mat-option[@role='option']"));
                for (WebElement number : rowsPerPageOptions)

                    if (number.getAttribute("innerText").equals(numberOfRows)) {
                        number.click();
                        break;
                    }

            } catch (Exception e) {

            }
        }

    }


}
