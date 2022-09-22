package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class SelectFristItem {
    WebDriver driver;

    @FindBy(xpath = "//p[@class='product-title']")
    List<WebElement> Item;

    public SelectFristItem(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickOnFristIteam()
    {
        List<WebElement> ClickFristItem = Item ;
        for(int i=1;i<=1;i++)
        {
            ClickFristItem.get(i).click();
        }
    }
}


