package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AddToCart {
    WebDriver driver;
    @FindBy(xpath = "//div[@id='add-cart-button-id']")
    WebElement clickAddtoCart;

    public AddToCart(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public WebElement SelectAddtoCart() {
        return clickAddtoCart;
    }
}

