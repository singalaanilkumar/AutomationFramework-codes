package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchDetailEnquaryPage extends BasePage {

    public BatchDetailEnquaryPage(WebDriver driver) {
        super(driver);
    }

    public GridUtils Grid = new GridUtils(driver);
    StepsDataStore dataStorage = StepsDataStore.getInstance();
    public  InputUtils input=new InputUtils(driver);

    private By gridToolbar = By.xpath("//div[@class='mat-paginator-container']");
    private By gridHeaderNew = By.xpath(".//*[@class='compact streched mat-table']/thead/tr");
    private By gridRows = By.xpath(".//*[@class='compact streched mat-table']/tbody/tr");


    @FindBy(xpath = "//span[text()='Back to Batch Inquiry']")
    WebElement backToBatchButton;

    public void clickBackToBatchButton() throws InterruptedException{
        backToBatchButton.click();
    }

    private  By checkElement=By.xpath(".//*[@class='mat-checkbox mat-primary']");
    public void clickCheckbox(String rowNo) throws InterruptedException {

        List<WebElement> gridRowsLocators = driver.findElements(checkElement);

        for (WebElement inputField : gridRowsLocators) {
            try {



                String s= inputField.getAttribute("id");
                By checkElement=By.xpath(".//*[@id='"+s+"']/label/div");
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

    By searchByCriteria = By.xpath(".//*[@class='criteria']/mat-form-field");

    public void typeIntoInputFieldAtBatchDetail(String input, String fieldName) throws InterruptedException{
      Thread.sleep(2000);
        //  scrollElementIntoView(driver.findElement(searchBox));
        waitForElement(searchByCriteria, 10);
        List<WebElement> searchInputsLocators = driver.findElements(searchByCriteria);
        for (WebElement inputField : searchInputsLocators) {
            try {

                WebElement inputLabel = inputField.findElement(By.xpath(".//label"));
                String in=inputLabel.getText();
                if (inputLabel.getText().replace(" *", "").equals(fieldName)) {
                    LOGGER.info("Entering data into field " + inputLabel.getText());
                    WebElement inputFieldLocator = inputField.findElement(By.xpath(".//input"));
                    inputFieldLocator.clear();
                    inputFieldLocator.click();
                    inputFieldLocator.sendKeys(input);
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void typeIntoInputFieldIDText(String fieldName) throws InterruptedException{
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
                    inputFieldLocator.getText();
                    dataStorage.getStoredData().put("",inputFieldLocator.getText());

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
        List<WebElement> searchInputsLocators = driver.findElements(searchByCriteria);
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

    @FindBy(xpath = "//app-batch-detail-inquiry[@class='ng-star-inserted']/div/button[3]")
    WebElement UndoButton;

    public void clickAnyOneOfThreeButton(String buttonName) {


        if(buttonName.contains("Undo")){
            waitForElement(By.xpath("//app-batch-detail-inquiry[@class='ng-star-inserted']/div/button[1]"), 5);
            UndoButton.click();
        }

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
        printButtonInSelectPrinterPopup.click();
    }


    @FindBy(xpath = ".//*[@class='dialog']/div[2]")
    WebElement successDialogMsgAfterPrint;

    @FindBy(xpath = ".//*[@class='dialog']/div[3]/button")
    WebElement successDialogCloseAfterPrint;


    public void clickSuccessDialogCloseAfterPrint(){
        waitForElement(By.xpath(".//*[@class='dialog']/div[3]/button"), 5);

        String fetchBatchNumberMsg=successDialogMsgAfterPrint.getText();
        String BatchNumber =fetchBatchNumberMsg.substring(fetchBatchNumberMsg.length() - 2);
        dataStorage.getStoredData().put("BatchNumber",BatchNumber);
        successDialogCloseAfterPrint.click();

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

    public ArrayList<Map<String, String>> getColumnData(String columnName) {
        waitForElement(gridHeaderNew, 10);
        ArrayList<Map<String, String>> columnData = new ArrayList<>();

        scrollElementIntoView(driver.findElement(gridToolbar));

        WebElement gridHeaderLocator = driver.findElement(gridHeaderNew);
        List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("th"));
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
            // scrollCellBeginningOfGrid(headerList);
            boolean endOfList = false;
            while (!endOfList) {
                headerList = gridHeaderLocator.findElements(By.xpath("th"));

                while (!columnsList.get(columnsList.size() - 2).equals(headerList.get(headerList.size() - 3).getText())) {
                    columnsList.add(headerList.get(headerList.size() - 3).getText());
                    if (headerList.get(headerList.size() - 3).getText().equals(columnName)) {
                        endOfList = true;
                        break;
                    }
                    //     scrollCellMidOfGrid(headerList);

                    headerList = gridHeaderLocator.findElements(By.xpath("th"));
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
            if (column.getText().equals(columnName)) {
                for (WebElement row : gridRowsLocator) {
                    Map<String, String> rowData = new HashMap<>();
                    String colID = getColID(column.getText());
                    WebElement cellContent = row.findElement(By.xpath("td[(@class='mat-cell cdk-column-" + colID+" mat-column-" +colID+" ng-star-inserted" +  "')]"));
                    // mat-cell cdk-column-sellDivison mat-column-sellDivison ng-star-inserted
                    rowData.put("Row #", row.getAttribute("row-index"));
                    rowData.put(column.getText(), cellContent.getText());
                    columnData.add(rowData);
                }
                break;
            }
        }
        //Sort maps by Row #
        //  Collections.sort(columnData, (o1, o2) -> o1.get("Row #").compareTo(o2.get("Row #")));
        return columnData;
    }

    public List<String> getColumnDataNew(String columnName){
        waitForElement(gridHeaderNew, 10);
        List<String> columnData = new ArrayList<>();
        List<String> columnData1 = new ArrayList<>();

        scrollElementIntoView(driver.findElement(gridToolbar));

        WebElement gridHeaderLocator = driver.findElement(gridHeaderNew);
        List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("th"));
        List<WebElement> gridRowsLocator = driver.findElements(gridRows);
        String colID = getColID(columnName);
        String batchcolID = getColID("Batch#");
        for(int i = 1; i<=gridRowsLocator.size(); i++) {
            WebElement cellContentbatch = gridRowsLocator.get(i - 1).findElement(By.xpath("td[(@class='mat-cell cdk-column-" + batchcolID+" mat-column-" +batchcolID+" ng-star-inserted" +  "')]"));
            cellContentbatch.getText();
            columnData1.add(cellContentbatch.getText());
            dataStorage.getStoredData().put("BatchNumber",columnData1.get(0));

            WebElement cellContent = gridRowsLocator.get(i - 1).findElement(By.xpath("td[(@class='mat-cell cdk-column-" + colID+" mat-column-" +colID+" ng-star-inserted" +  "')]"));
              // Thread.sleep(3000);
            //   getWait().until(ExpectedConditions.elementToBeClickable(cellContent));

                 columnData.add(cellContent.getText());
                break;


        }
        return columnData;
    }

    private String getColID(String columnName) {
        String colID = null;
        switch (columnName) {
            case "Reservation#":
                colID = "pkt_nbr";
                break;
            case "Ship#":
                colID = "pkt_sfx";
                break;
            case "Batch#":
                colID = "batch_number";
                break;
            case "Batch Dtl Status":
                colID = "orderDate";
                break;
            case "Ship Date":
                colID = "shipDate";
                break;
            case "Shipping":
                colID = "shipService";
                break;
            case "Gift Wrap":
                colID = "giftWrap";
                break;
            case "Back Order":
                colID = "backOrder";
                break;
            case "Back Order Type":
                colID = "backOrderType";
                break;
            case "Sell Chan":
                colID = "sellChannel";
                break;
            case "Sell Div":
                colID = "sellDivison";
                break;
            case "Lines":
                colID = "lineCount";
                break;
            case "Work Group":
                colID = "workGroup";
                break;
            case "FF#":
                colID = "fullfillmentNbr";
                break;

        }
        return colID;
    }

}
