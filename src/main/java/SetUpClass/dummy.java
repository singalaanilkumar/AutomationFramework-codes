package SetUpClass;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class dummy {
    WebDriver driver;

    @Test
    public void verify() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get("https://www.snapdeal.com/search?keyword=Bluetooth%20Headphone&santizedKeyword=Bluetooth+Headphone&catId=0&categoryId=0&suggested=false&vertical=p&noOfResults=20&searchState=&clickSrc=go_header&lastKeyword=&prodCatId=&changeBackToAll=false&foundInAll=false&categoryIdSearched=&cityPageUrl=&categoryUrl=&url=&utmContent=&dealDetail=&sort=plrty&q=Price%3A700%2C3000%7C");
        List<WebElement> ClickFristItem = driver.findElements(By.xpath("//p[@class='product-title']"));
        for(int i=1;i<=1;i++)
        {
            ClickFristItem.get(i).click();
        }
        Set<String> windows = driver.getWindowHandles(); //[parentid,childid,subchildId]
        Iterator<String> it = windows.iterator();
        String parentId = it.next();
        String childId = it.next();
        driver.switchTo().window(childId);
       // Thread.sleep(3000);
        driver.findElement(By.xpath("//div[@id='add-cart-button-id']")).click();
     //   Thread.sleep(3000);
        driver.findElement(By.xpath("//div[@class='col-xs-15 btn-container']/div")).click();
        Thread.sleep(3000);
        System.out.println(driver.findElement(By.xpath("//div[@class='cart-heading clearfix']/h3")).getText());
        driver.findElement(By.xpath("//div[@class='remove-item-div']")).click();
        Thread.sleep(3000);
        System.out.println(driver.findElement(By.xpath("//div[@class='empty-cart-wrapper']/div[1]/h3")).getText());


    }
}
