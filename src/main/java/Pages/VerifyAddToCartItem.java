package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class VerifyAddToCartItem {
    WebDriver driver;
    //div[@class='col-xs-15 btn-container']/div

    @FindBy(xpath = "//div[@class='col-xs-15 btn-container']/div")
    WebElement clickViewCart;

    @FindBy(xpath = "//div[@class='cart-heading clearfix']/h3")
    WebElement VerifyShoppingCart;

    public VerifyAddToCartItem(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public WebElement clickonviewCart() {
        return clickViewCart;
    }

    public WebElement ChecktheAddCartIteamRnot() {
        return VerifyShoppingCart;
    }
}
