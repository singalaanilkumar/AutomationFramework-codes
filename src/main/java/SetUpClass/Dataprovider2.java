package SetUpClass;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class Dataprovider2 {
    WebDriver driver;

    @org.testng.annotations.DataProvider(name="test_data")
    public Object[][]datafile()
    {
        return new Object[][]
                {
                        {"Anil@123"},// uname ,password
                        {"mohan1"},
                        {"mohan2"}
                };
    }
    //@BeforeTest
    @BeforeMethod
    public void setup()
    {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("https://petstore.octoperf.com/actions/Catalog.action");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }
    @Test(dataProvider="test_data")
    public void login(String uname)
    {
        driver.findElement(By.linkText("Sign In")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.findElement(By.name("username")).sendKeys(uname);
        Reporter.log("the enter username is="+uname,true);
        driver.findElement(By.name("password")).clear();
        // driver.findElement(By.name("password")).sendKeys("password");
        // Reporter.log("the enter password is="+password,true);
        driver.findElement(By.name("signon")).click();
        Reporter.log("sucessfully loged into application",true);
    }
    //@AfterTest
    @AfterMethod
    public void teardown()
    {
        driver.quit();
    }
}

