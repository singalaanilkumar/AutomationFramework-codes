package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RemoveCartAndVerify {
    WebDriver driver;

    public RemoveCartAndVerify(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[@class='remove-item-div']" )
    WebElement removeCart;


    @FindBy(xpath = "//div[@class='empty-cart-wrapper']/div[1]/h3" )
    WebElement VerifyRemoveCart;

    public WebElement clickRemoveCart()
    {
        return removeCart;
    }

    public WebElement verifyremoveCart()
    {
        return VerifyRemoveCart;
    }

}
