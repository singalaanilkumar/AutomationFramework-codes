package com.macys.mst.DC2.EndToEnd.pageobjects;

import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Slf4j
public class POInquiryUIPage extends BasePage{
	
    protected By searchBox = By.id("searchBox");
    private  By filterResultTable = By.id("gridSection");
    private By clearButton = By.id("clearButton");
    private By searchByInputs = By.xpath(".//*[@id='searchBox']/div/div/div[2]/div/div/div/div");

	@FindBy(id = "searchButton")
    private WebElement searchButton;


	public POInquiryUIPage(WebDriver driver) {
		super(driver);
	}
	
	public void typeIntoInputField(String fieldName, String input) {
		scrollElementIntoView(driver.findElement(searchBox));
		waitForElement(searchByInputs, 10);
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
					log.info("Entering data into field " + inputLabel.getText());
					StepDetail.addDetail("Entering data into field " + inputLabel.getText(), true);
					WebElement inputFieldLocator = inputField.findElement(By.xpath(".//label[text()='" + fieldName + "']/following-sibling::div/input"));
					inputFieldLocator.click();
					inputFieldLocator.clear();
					inputFieldLocator.sendKeys(input);
					log.info("Entering PO: " + input);
					StepDetail.addDetail("Entering PO: " + input, true);
					break;
				}
			} catch (Exception ignored) {
			}
		}
	}
	
	public void clickSearchButton() {
        waitForElement(By.xpath("//button[(@id = 'searchButton') and not(@disabled)]"),5);
        searchButton.click();
        waitForElement(filterResultTable, 10);
        log.info("Clicking on Search button");
        StepDetail.addDetail("Clicking on Search button", true);
    }
	
	public void clickClearButton() {
        waitForElement(clearButton,5);
        scrollElementIntoView(driver.findElement(searchBox));
        driver.findElement(clearButton).click();
        log.info("Clicking on clear button");
        StepDetail.addDetail("Clicking on clear button", true);
    }
	
	public boolean isSearchResultTableDisplayed() {
		try {
			waitForElement(filterResultTable, 3);
			if (driver.findElement(filterResultTable).isDisplayed()) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

}
