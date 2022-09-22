package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SortBy {
    WebDriver driver;
    @FindBy(xpath = "//div[@class='sorting-sec animBounce']/div[@class='sort-drop clearfix']")
    WebElement clickSortby;

    @FindBy(xpath = "//div[@class='sorting-sec animBounce']/ul[@style='z-index: 17;']/li[@data-sorttype='plrty']")
    WebElement popularity;

    public SortBy(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public WebElement ClickonSortby() {
        return clickSortby;
    }

    public WebElement clickpopularity() {
        return popularity;
    }

}

