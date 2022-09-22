package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OrderInquiryAndDetailInquiryPage extends BasePage {

    public OrderInquiryAndDetailInquiryPage(WebDriver driver) {
        super(driver);
    }

    public WebDriverWait getWait(int waitTime) {
        WebDriverWait wait = new WebDriverWait(driver, waitTime);
        return wait;
    }

    public GridUtils Grid = new GridUtils(driver);
    StepsDataStore dataStorage = StepsDataStore.getInstance();


    public void filterButton() {

        waitForElement(Grid.filterResultTable, 1500);
        By filterElements=By.xpath(".//*[@class='mat-expansion-panel-header-title']/mat-icon");
        List<WebElement> searchInputsLocators = driver.findElements(filterElements);
        for (WebElement inputField : searchInputsLocators) {
            try {
                inputField.click();

                    break;

            } catch (Exception ignored) {
            }
        }

    }

    By searchByInputsOIDI = By.xpath(".//*[@id='cdk-accordion-child-0']/div/mat-form/mat-form-field");

    public void typeIntoInputField(String fieldName, String input) {

        waitForElement(searchByInputsOIDI, 10);
        List<WebElement> searchInputsLocators = driver.findElements(searchByInputsOIDI);
        for (WebElement inputField : searchInputsLocators) {
            try {

                WebElement inputLabel = inputField.findElement(By.xpath(".//label"));
                if (inputLabel.getText().replace(" *", "").equals(fieldName)) {
                    LOGGER.info("Entering data into field " + inputLabel.getText());
                    WebElement inputFieldLocator = inputField.findElement(By.xpath(".//label[contains(text(),'" + inputLabel.getText() + "')]/following-sibling::input"));
                    inputFieldLocator.click();
                    inputFieldLocator.clear();
                    inputFieldLocator.sendKeys(input);
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }


    public void searchButton(String buttonName) {

        By filterElements=By.xpath(".//*[@id='cdk-accordion-child-0']/div/mat-form/div/button");
        List<WebElement> searchInputsLocators = driver.findElements(filterElements);
        for (WebElement inputField : searchInputsLocators) {
            try {
                WebElement buttonLabel = inputField.findElement(By.xpath(".//span"));
                if (buttonLabel.getText().replace(" *", "").equals(buttonName)) {
                    LOGGER.info("Entering data into field " + buttonLabel.getText());
                    getWait(1000).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(buttonLabel));
                    buttonLabel.click();
                    break;
                }

            } catch (Exception ignored) {
            }
        }

    }

    @FindBy(xpath = ".//*[@id='cdk-accordion-child-0']/div/mat-form/div/button[2]")
    WebElement resetButton;

    @FindBy(xpath = ".//*[@class='btns btn-grp']/button[1]")
    WebElement yesButtonDiolog;

    @FindBy(xpath = ".//*[@class='btns btn-grp']/button[2]")
    WebElement noButtonDialog;



    public void clickResetButton() {
        waitForElement(By.xpath(".//*[@id='cdk-accordion-child-0']/div/form/div[15]/button[2]"), 5);
        resetButton.click();
        waitForElement(Grid.filterResultTable, 10);
    }


    public void clickYesOrderInquiryPopup(){

        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@class='btns btn-grp']/button[1]")));
        yesButtonDiolog.click();
        waitForElement(Grid.filterResultTable, 1500);

    }
    public void clickNoInPreviewSubmitBatchPopup(){
        waitForElement(By.xpath(".//*[@class='btns btn-grp']/button[2]"), 5);
        noButtonDialog.click();
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


    public void clickExportButton() {
        waitForElement(Grid.filterResultTable, 1500);
        By exportElements=By.xpath(".//*[@class='btns-left-to-filter true']/button");
        List<WebElement> buttonLocators = driver.findElements(exportElements);
        for (WebElement button : buttonLocators) {
            try {

                WebElement buttonLabel = button.findElement(By.xpath(".//span"));
                if (buttonLabel.getText().replace(" *", "").equals("Export")) {
                    LOGGER.info("Entering data into field " + buttonLabel.getText());
                    getWait(3000).ignoring(Exception.class).until(ExpectedConditions.visibilityOf(buttonLabel));
                    buttonLabel.click();
                    break;
                }
            } catch (Exception ignored) {
            }
        }

    }

}
