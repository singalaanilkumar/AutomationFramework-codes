package TestCases;

import Pages.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TestCase {
    WebDriver driver;

    @BeforeTest
    //@Parameters("browser")
    public void launchBrowserAndExecution() throws IOException {
   // public void launchBrowserAndExecution(String browser ) throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/config/Item.properties");
        prop.load(fis);
        String url = prop.getProperty("url");
        String browser = prop.getProperty("browser");

        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("InternetExplorer")) {
            WebDriverManager.iedriver().setup();
            driver = new InternetExplorerDriver();
        } else if (browser.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        }
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get(url);
        Reporter.log("successfully Launched the browser and Navigated Snapdeal", true);
    }


    @Test(priority = 1)
    public void SearchModule() throws InterruptedException {
        SearchItem si = new SearchItem(driver);
        si.enterItemInSearch().sendKeys("Bluetooth Headphone");
        Thread.sleep(2000);
        si.ClickSearchButton().click();
        Reporter.log("successfully  search the Bluetooth item", true);

    }

    @Test(priority = 2)
    public void VerifyingSearchDetail() {
        VerifySearchResults vsr = new VerifySearchResults(driver);
        String actualString = vsr.VerifysearchCriteria().getText();
        System.out.println(actualString);
       // Assert.assertEquals( "We've got 852 results for 'Bluetooth Headphone'" , verifyresult);
        Assert.assertTrue(actualString.contains("We've got 8"));
        Reporter.log("successfully verified search result", true);
    }

    @Test(priority = 3)
    public void SortByModule() throws InterruptedException {
        SortBy sb = new SortBy(driver);
        sb.ClickonSortby().click();
        Thread.sleep(2000);
        sb.clickpopularity().click();
        Reporter.log("successfully Sorted", true);
    }

    @Test(priority = 4)
    public void RangeModule() throws InterruptedException {
        SelectPriceRange spr = new SelectPriceRange(driver);
        spr.clickFristPriceRange().click();
        spr.clickFristPriceRange().clear();
        spr.clickFristPriceRange().sendKeys("700");
        Thread.sleep(3000);
        spr.clickLastPriceRange().click();
        spr.clickLastPriceRange().clear();
        spr.clickLastPriceRange().sendKeys("3000");
        Thread.sleep(3000);
        spr.clickonGOButton();
        Reporter.log("successfully Selected Range 700 to 3000", true);
    }

    @Test(priority = 5)
    public void CSVreadndwrite() throws IOException {
        SaveFristItemAndPriceInCSV sfvt = new SaveFristItemAndPriceInCSV(driver);
        sfvt.CsvReaderNdWriter();
        Reporter.log("successfully Written the Item Data in CSV", true);
    }


    @Test(priority = 6)
    public void FristItem() {
        SelectFristItem sfi = new SelectFristItem(driver);
        sfi.clickOnFristIteam();
        Set<String> windows = driver.getWindowHandles(); //[parentid,childid,subchildId]
        Iterator<String> it = windows.iterator();
        String parentId = it.next();
        String childId = it.next();
        driver.switchTo().window(childId);
        Reporter.log("successfully moved to another tab", true);
    }

    @Test(priority = 7)
    public void ClickOnAddtoCart() {
        AddToCart atc = new AddToCart(driver);
        atc.SelectAddtoCart().click();
        Reporter.log("successfully clicked the AddToCart", true);
    }

    @Test(priority = 8)
    public void VerifyTheAddtoCartIteam() throws InterruptedException {
        VerifyAddToCartItem vai = new VerifyAddToCartItem(driver);
        vai.clickonviewCart().click();
        Thread.sleep(3000);
        String verifiediteam = vai.ChecktheAddCartIteamRnot().getText();
        System.out.println(verifiediteam);
        Assert.assertEquals(verifiediteam, "Shopping Cart (1 Item)");
        Reporter.log("successfully verified AddtoCart iteam", true);
    }

    @Test(priority = 9)
    public void RemoveAddCartAndVerify() throws InterruptedException {
        RemoveCartAndVerify rcv = new RemoveCartAndVerify(driver);
        rcv.clickRemoveCart().click();
        Thread.sleep(3000);
        String verifedremovecart = rcv.verifyremoveCart().getText();
        System.out.println(verifedremovecart);
        Assert.assertEquals(verifedremovecart, "Shopping Cart is empty!");
        Reporter.log("successfully verified remove cart iteam", true);

    }

    @AfterTest
    public void quit() throws InterruptedException {
        Thread.sleep(5000);
        driver.quit();
    }
}


