package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GridUtils extends BasePage{

    public GridUtils(WebDriver driver) {
        super(driver);
    }

    public By filterResultTable = By.xpath("//table[@class='compact streched mat-table']");

    private By gridHeaderNew = By.xpath(".//*[@class='compact streched mat-table']/thead/tr");
    private By gridRows = By.xpath(".//*[@class='compact streched mat-table']/tbody/tr");
///html/body/app-root/div/div[2]/app-order-inquiry/app-paged-grid/div/div/table/thead/tr/th[1]/div/button

    private By gridToolbar = By.xpath("//div[@class='mat-paginator-container']");
    By cellLocator = By.xpath(".//td[contains(@role, 'gridcell') and contains(@class, 'mat-cell cdk')]");
    private By totalNumberOfGridPages = By.xpath("//div[(@class='mat-paginator-range-label')]");

    private By nextPageFromGrid = By.xpath("//button[(@class='mat-paginator-navigation-next mat-icon-button')]");

    public boolean isSearchResultTableDisplayed() {
        try {
            Thread.sleep(3000);
            waitForElement(filterResultTable, 3);
            if (driver.findElement(filterResultTable).isDisplayed()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public List<String> getGridHeader() {
        waitForElement(gridHeaderNew, 10);
        List<String> columnsList = new ArrayList<>();

       scrollElementIntoView(driver.findElement(gridToolbar));

        WebElement gridHeaderLocator = driver.findElement(gridHeaderNew);
        List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("th"));

        for (WebElement column : headerList) {
            columnsList.add(column.getAttribute("innerText"));
        }

        return columnsList;
    }

    public void selectNumberOfRowsPerPage(String numberOfRows) {
        scrollElementIntoView(driver.findElement(gridToolbar));

//input[@name="email" and contains(@placeholder,'Email')]
         //   driver.findElement(By.xpath("//mat-select[@aria-label='Items per page:']")).click();
        driver.findElement(By.xpath("//mat-select[@aria-label='Items per page:' or @role='listbox']")).click();
           // /html/body/app-root/div/div[2]/app-order-inquiry/app-paged-grid/div/div/mat-paginator/div/div/div[1]/mat-form-field/div/div[1]/div/mat-select/div/div[1]
            List<WebElement> rowsPerPageOptions = driver.findElements(By.xpath(".//mat-option[@role='option']"));
            for (WebElement number : rowsPerPageOptions)

                if (number.getAttribute("innerText").equals(numberOfRows)) {
                    number.click();
                    break;
                }

    }

    public List<String> getColumnItemsList(String colName){
        List<String> listOfItems = new ArrayList<>();
        for(Map<String, String> row : getGridContent()){
            String cellContent = row.get(colName);
            if(!listOfItems.contains(cellContent)){
                listOfItems.add(cellContent);
            }
        }
        return listOfItems;
    }




    public ArrayList<LinkedHashMap<String, String>> getGridContent() {
        waitForElement(gridHeaderNew, 10);
        ArrayList<LinkedHashMap<String, String>> gridDetails = new ArrayList<>();

       scrollElementIntoView(driver.findElement(gridToolbar));
        WebElement gridHeaderLocator = driver.findElement(gridHeaderNew);
      //  .//*[@class='compact streched mat-table']/thead/tr
        List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("th"));
        List<WebElement> gridRowsLocator = driver.findElements(gridRows);

        for (WebElement row : gridRowsLocator) {
            LinkedHashMap<String, String> rowData = new LinkedHashMap<>();

            for (WebElement column : headerList) {
                int colIterator = headerList.indexOf(column) + 1;
                WebElement cellContent = row.findElement(By.xpath("td[" + colIterator + "]"));
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

    private String getColID(String columnName) {
        String colID = null;
        switch (columnName) {
            case "Res Nbr":
                colID = "resNbr";
                break;
            case "Ship Nbr":
                colID = "shipNbr";
                break;
            case "Order Nbr":
                colID = "orderNbr";
                break;
            case "Order Date":
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
            case "Resv#":
                colID= "res_nbr";

        }
        return colID;
    }

    public int getTotalNumberOfPagesOfResults() {
        try {
            waitForElement(totalNumberOfGridPages, 5);
            String pagesRange= driver.findElement(totalNumberOfGridPages).getText();
           String totalNumberOfGridPagesNumber= pagesRange.substring(pagesRange.lastIndexOf(" ")+1);
            return Integer.parseInt(totalNumberOfGridPagesNumber);
        } catch (Exception e) {
            return 0;
        }
    }
    public int getCurrentNumberOfPagesOfResults() {
        try {
            waitForElement(totalNumberOfGridPages, 5);
            String pagesRange= driver.findElement(totalNumberOfGridPages).getText();
            String totalCurrentPages= CommonUtils.getNthWord(pagesRange,3);
            return Integer.parseInt(totalCurrentPages);
        } catch (Exception e) {
            return 0;
        }
    }


    public boolean checkIfTheNumberOfResultsPerPageIsCorrect(int rowsNumber) {
        if (getTotalNumberOfPagesOfResults() == 1) {
            return (getNumberOfRowsFromGrid() <= rowsNumber);
        } else {
            while (getCurrentNumberOfPagesOfResults() != getTotalNumberOfPagesOfResults()) {
                clickOnNextPageFromGridResultTable();
            }
            return (getNumberOfRowsFromGrid() <= rowsNumber);
        }


    }

    public int checkIfTheNumberOfRowsPerPageIsCorrect() {

      return getCurrentPageNumber();
    }

    private void clickOnNextPageFromGridResultTable() {
        scrollElementIntoView(driver.findElement(nextPageFromGrid));
        waitForElement(nextPageFromGrid, 2);
        driver.findElement(nextPageFromGrid).click();
    }

    private int getNumberOfRowsFromGrid() {
      //  wait(1);
        return driver.findElements(gridRows).size();
    }

    public Boolean isColumnSortedCorrectly(ArrayList<Map<String, String>> columnData, String order, String columnName) {
        String firstValue = null;
        String lastValue = null;
        for (Map<String, String> row : columnData) {
            for (String key : row.keySet()) {
                String rowNo = row.get(key);
                if (key.equals("Row #") && rowNo.equals("0")) {
                    firstValue = columnData.get(0).get(columnName);
                }
                if (key.equals("Row #") && rowNo.equals("9")) {
                    lastValue = columnData.get(9).get(columnName);
                }
            }
        }
        if (order.equals("ASC")) {
            if (firstValue.contains("/") && StringUtils.isNumeric(firstValue.split("/")[1])) {
                try {
                    Date date1 = new SimpleDateFormat("MM/dd/yyyy").parse(firstValue);
                    Date date2 = new SimpleDateFormat("MM/dd/yyyy").parse(lastValue);
                    return date1.before(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (StringUtils.isNumeric(firstValue)) {
                return Integer.parseInt(firstValue) <= Integer.parseInt(lastValue);
            } else {
                List<String> values = Arrays.asList(firstValue, lastValue);
                List<String> listSorted = new ArrayList<>(values);
                Collections.sort(listSorted);
                return listSorted.equals(values);
            }
        } else if (order.equals("DESC")) {
            if (firstValue.contains("/") && StringUtils.isNumeric(firstValue.split("/")[1])) {
                try {
                    Date date1 = new SimpleDateFormat("MM/dd/yyyy").parse(firstValue);
                    Date date2 = new SimpleDateFormat("MM/dd/yyyy").parse(lastValue);
                    return date1.after(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (StringUtils.isNumeric(firstValue)) {
                return Integer.parseInt(firstValue) >= Integer.parseInt(lastValue);
            } else {
                List<String> values = Arrays.asList(firstValue, lastValue);
                List<String> listSorted = new ArrayList<>(values);
                Collections.sort(listSorted);
                listSorted.sort(Collections.reverseOrder());
                return listSorted.equals(values);
            }
        }
        return false;
    }

    public void sortGridByColumn(String columnName, String order) {
        waitForElement(gridHeaderNew, 10);
        WebElement gridHeaderLocator = driver.findElement(gridHeaderNew);
        List<WebElement> headerList = gridHeaderLocator.findElements(By.xpath("th"));

        scrollElementIntoView(driver.findElement(gridToolbar));

        for (WebElement column : headerList) {
            if (column.getText().equals(columnName)) {
                WebElement col = column.findElement(By.xpath("div"));
                JavascriptExecutor jse = (JavascriptExecutor) driver;
                jse.executeScript("window.scrollTo(0," + col.getLocation().x + ")");
                scrollElementIntoView(driver.findElement(gridToolbar));
                if (order.equals("ASC")) {
                    col.click();
                } else if (order.equals("DESC")) {
                    col.click();
                    col.click();
                }
                return;
            }
        }

    //    scrollCellBeginningOfGrid(headerList);

        boolean endOfList = false;
        while (!endOfList) {
            headerList = gridHeaderLocator.findElements(By.xpath("div"));

            for(int i = headerList.size()-1; i >= 0; i--){
                if(headerList.get(i).getText().equals(columnName)){
                    WebElement col = headerList.get(i);
                    scrollElementIntoView(driver.findElement(gridToolbar));
                    if (order.equals("ASC")) {
                        col.click();
                    } else if (order.equals("DESC")) {
                        col.click();
                        col.click();
                    }
                    endOfList = true;
                    break;
                }
                if(!headerList.get(i).getText().equals("") && headerList.get(i-1).getText().equals("")){
                 //   scrollCellMidOfGrid(headerList);
                    break;
                }
            }
        }
    }
}
