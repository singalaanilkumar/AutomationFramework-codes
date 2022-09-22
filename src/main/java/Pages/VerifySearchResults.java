package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class VerifySearchResults {

    WebDriver driver; //local variable
    @FindBy(xpath = "//div[@class='search-result-txt-section  marT12']/span[@style='color: #212121; font-weight: normal']")
    WebElement SearchResult;

    public VerifySearchResults(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public WebElement VerifysearchCriteria() {
        return SearchResult;

    }
}
