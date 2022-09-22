package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SelectPriceRange {

    WebDriver driver;

    @FindBy(xpath = "//input[@name='fromVal']")
    WebElement clickFristPrice;

    @FindBy(xpath = "//input[@name='toVal']")
    WebElement clickLastPrice;

    @FindBy(xpath = "//div[@class='price-go-arrow btn btn-line btn-theme-secondary']")
    WebElement clickGoButton;

    public SelectPriceRange(WebDriver driver)

    {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public WebElement clickFristPriceRange() {
        return clickFristPrice;
    }

    public WebElement clickLastPriceRange() {
        return clickLastPrice;
    }

    public void clickonGOButton() {
        clickGoButton.click();
    }

}

