package com.macys.mst.DC2.EndToEnd.pageobjects.supplychain;

import com.macys.mst.DC2.EndToEnd.pageobjects.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;


public class PODetailsPage extends BasePage {
 
    @FindBy(xpath = "//*[@col-id='inhouseUpc']/div/button")
    private List<WebElement> inhouseUpcList;

    public void clickSKU(String SKUNumber) {
    	getWait().until(ExpectedConditions.visibilityOfAllElements(inhouseUpcList));
    	//getWait().until(ExpectedConditions.urlContains("poDistro"));
        for (WebElement inhouseUpc : inhouseUpcList) {
			getWait().until(ExpectedConditions.visibilityOf(inhouseUpc));
        	if(SKUNumber.equals(inhouseUpc.getText())){
        		try {
					inhouseUpc.click();
					break;
				}catch(Exception e){
					inhouseUpc.click();
					break;
				}

        	}
    	}
	}
    
}




