package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class InputUtils extends BasePage{

    public InputUtils(WebDriver driver) {
        super(driver);
    }

    public Boolean isInputDisplayed(String name) {
        waitForElement(searchByInputs, 10);
        List<WebElement> searchInputsLocators = driver.findElements(searchByInputs);
        for (WebElement inputField : searchInputsLocators) {
            try {
                WebElement inputLabel = inputField.findElement(By.xpath(".//label"));
                if (inputLabel.getText().replace(" *", "").equals(name)) {
                    if (inputField.findElement(By.xpath(".//label[contains(text(),'" + inputLabel.getText() + "')]/following-sibling::input")).isDisplayed()) {
                        return true;
                    }
                }
            } catch (NoSuchElementException e) {
                return name.equals("Department") && driver.findElement(By.xpath(".//*[@name='department']")).isDisplayed();
            }
        }
        return false;
    }

    public void typeIntoInputField(String fieldName, String input) {
      //  scrollElementIntoView(driver.findElement(searchBox));
        waitForElement(searchByInputs, 10);
        List<WebElement> searchInputsLocators = driver.findElements(searchByInputs);
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

    public String getInputFieldValue(String fieldName) {
   //     wait(1);
        WebElement inputFieldLocator = driver.findElement(By.xpath(".//label[contains(text(), '" + fieldName + "')]/following-sibling::div/input"));
        return inputFieldLocator.getAttribute("value");
    }


}
