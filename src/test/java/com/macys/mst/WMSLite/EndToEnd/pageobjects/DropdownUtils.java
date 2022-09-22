package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class DropdownUtils extends BasePage{

    public DropdownUtils(WebDriver driver) {
        super(driver);
    }
    //*[@id="cdk-accordion-child-0"]/div/form/div[1]/label
    public Boolean isDropdownDisplayed(String name) throws InterruptedException{
        waitForElement(searchByInputs, 10);
        Thread.sleep(3000);
        List<WebElement> searchInputsLocators = driver.findElements(searchByInputs);
        for (WebElement inputField : searchInputsLocators) {
            try {

                WebElement inputLabel = inputField.findElement(By.xpath(".//label"));

                String inputLabelName=inputLabel.getText();
                if (inputLabelName.equals(name)) {
                 //   ng-untouched ng-pristine ng-valid
                    if (inputField.findElement(By.xpath(".//select[contains(@class, 'ng-pristine')]")).isDisplayed()) {
                        return true;
                    }
                }
            } catch (NoSuchElementException e) {
                return false;
            }
        }
        return false;
    }

    public List<String> getDropdownList(String dropdownName) {
        List<String> listOfItems = new ArrayList<>();
        for(WebElement option : getDropdownListOfOptions(dropdownName)){
            if(!option.getText().equals(""))
                listOfItems.add(option.getText());
        }
        return listOfItems;
    }

    public List<WebElement> getDropdownListOfOptions(String dropdownName) {
        waitForElement(searchByInputs, 10);
        scrollElementIntoView(driver.findElement(searchByInputs));
        List<WebElement> searchInputsLocators = driver.findElements(searchByInputs);
        for (WebElement inputField : searchInputsLocators) {
            WebElement inputLabel = inputField.findElement(By.xpath(".//label"));
            WebElement field = inputField.findElement(By.xpath(".//select"));
            String s=field.getText();
            if (inputLabel.getText().equals(dropdownName)) {
                inputField.click();

                List<WebElement> options=driver.findElements(By.xpath(".//select[@name='"+getdropdownID(dropdownName)+"']/option"));

                return options;
            }
        }
        return null;
    }
    public void setOptionInDropdown(String dropDownName, String option) {
        List<WebElement> allOptions = getDropdownListOfOptions(dropDownName);
        boolean found = false;
        for (WebElement allOption : allOptions) {
            if (allOption.getText().equals(option)) {
                found = true;
                allOption.click();

                break;
            }
        }
    }
    /*
    public void setOptionInDropdown(String dropDownName, String option) {
        waitForElement(searchByInputs, 10);
        List<WebElement> searchInputsLocators = driver.findElements(searchByInputs);
        for (WebElement inputField : searchInputsLocators) {
            try {
                WebElement inputLabel = inputField.findElement(By.xpath(".//label"));
                if (inputLabel.getText().equals(dropDownName)) {
                    LOGGER.info("Selecting option " + option + " from drop-down " + dropDownName);
                    inputField.click();
                    Thread.sleep(3000);
                    List<WebElement> allOptions = driver.findElements(By.xpath(".//select[contains(@class, 'ng-ng-untouched ng-pristine ng-valid')]/option"));
                    boolean found = false;
                    for (WebElement allOption : allOptions) {
                        if (allOption.getText().equals(option)) {
                            found = true;
                            allOption.click();

                            break;
                        }
                    }
                    if(!found) {
                        LOGGER.info("Option " + option + " from drop-down " + dropDownName + " could not be found");
                        throw new Exception();
                    }
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }*/


    private String getdropdownID(String dropdownName) {

        String dropdownID = null;
        switch (dropdownName) {
            case "Selling Channel":
                dropdownID = "sellingChannel";
                break;
            case "Selling Division":
                dropdownID = "sellingDivisions";
                break;
            case "Work Group":
                dropdownID = "workGroup";
                break;
            case "Singles/Multi":
                dropdownID = "singles";
                break;
            case "Shipping Service":
                dropdownID = "shippingServiceCode";
                break;
        }
        return dropdownID;
    }
}
