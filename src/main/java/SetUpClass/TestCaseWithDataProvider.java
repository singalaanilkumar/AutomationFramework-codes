package SetUpClass;

import Pages.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class TestCaseWithDataProvider {
    WebDriver driver;

    @org.testng.annotations.DataProvider(name = "Search_data")
    public Object[][] datafile() {
        return new Object[][]
                {
                        {"Bluetooth Headphone"},// searchName
                        {"smartwatch"},
                        {"microfiber cloth"}
                };
    }


    @BeforeMethod
    //@Parameters("browser")
    public void launchBrowserAndExecution() throws IOException {
        // public void launchBrowserAndExecution(String browser ) throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("C:\\Users\\as61837\\Documents\\GitHub\\SnapdealProject\\src\\main\\java\\Config\\Item.Properties");
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get(url);
        Reporter.log("successfully Launched the browser and Navigated Snapdeal", true);
    }


    @Test(dataProvider = "Search_data")
    public void EnterItemInSearch(String searchName) throws InterruptedException, IOException {
        SearchItemData si = new SearchItemData(driver);
        si.enterItemInSearch().sendKeys(searchName);
        si.ClickSearchButton().click();
        Reporter.log("successfully  search the Bluetooth item", true);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        VerifySearchResults vsr = new VerifySearchResults(driver);
        String actualString = vsr.VerifysearchCriteria().getText();
        System.out.println(actualString);
        // Assert.assertEquals( "We've got 852 results for 'Bluetooth Headphone'" , verifyresult);
        Assert.assertTrue(actualString.contains("We've got"));
        Reporter.log("successfully verified search result", true);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        SortBy sb = new SortBy(driver);
        sb.ClickonSortby().click();
        Thread.sleep(2000);
        sb.clickpopularity().click();
        Reporter.log("successfully Sorted", true);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        SaveFristItemAndPriceInCSV sfvt = new SaveFristItemAndPriceInCSV(driver);
        Thread.sleep(2000);
        sfvt.CsvReaderNdWriter();
        Reporter.log("successfully Written the Item Data in CSV", true);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        SelectFristItem sfi = new SelectFristItem(driver);
        sfi.clickOnFristIteam();
        Set<String> windows = driver.getWindowHandles(); //[parentid,childid,subchildId]
        Iterator<String> it = windows.iterator();
        String parentId = it.next();
        String childId = it.next();
        driver.switchTo().window(childId);
        Reporter.log("successfully moved to another tab", true);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        AddToCart atc = new AddToCart(driver);
        atc.SelectAddtoCart().click();
        Reporter.log("successfully clicked the AddToCart", true);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        VerifyAddToCartItem vai = new VerifyAddToCartItem(driver);
        vai.clickonviewCart().click();
        Thread.sleep(3000);
        String verifiediteam = vai.ChecktheAddCartIteamRnot().getText();
        System.out.println(verifiediteam);
        Assert.assertEquals(verifiediteam, "Shopping Cart (1 Item)");
        Reporter.log("successfully verified AddtoCart iteam", true);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        RemoveCartAndVerify rcv = new RemoveCartAndVerify(driver);
        rcv.clickRemoveCart().click();
        Thread.sleep(3000);
        String verifedremovecart = rcv.verifyremoveCart().getText();
        System.out.println(verifedremovecart);
        Assert.assertEquals(verifedremovecart, "Shopping Cart is empty!");
        Reporter.log("successfully verified remove cart iteam", true);

    }
    @AfterMethod
    public void teardown()
    {
        driver.quit();
    }

}

