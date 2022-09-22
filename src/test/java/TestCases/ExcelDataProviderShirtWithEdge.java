package TestCases;


import Pages.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class ExcelDataProviderShirtWithEdge {
    WebDriver driver;


    @BeforeMethod()
    @Parameters("browser")
    // public void launchBrowserAndExecution() throws IOException {
    public void launchBrowserAndExecution(String browser) throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/config/Item.properties");
        prop.load(fis);
        String url = prop.getProperty("url");
        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("InternetExplorer")) {
            WebDriverManager.iedriver().setup();
            driver = new InternetExplorerDriver();
        } else if (browser.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get(url);
        Reporter.log("successfully Launched the browser and Navigated Snapdeal", true);
    }

    @DataProvider(name = "excel-data", parallel = true)
    public Object[][] excelDP() throws IOException {
        //We are creating an object from the excel sheet data by calling a method that reads data from the excel stored locally in our system
        Object[][] arrObj = getExcelData("C:\\Users\\as61837\\Downloads\\exceldata\\shirtitem.xlsx");
        return arrObj;
    }

    //This method handles the excel - opens it and reads the data from the respective cells using a for-loop & returns it in the form of a string array
    public String[][] getExcelData(String fileName) {

        String[][] data = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sh = wb.getSheetAt(0);
            XSSFRow row = sh.getRow(0);
            int noOfRows = sh.getPhysicalNumberOfRows();
            int noOfCols = row.getLastCellNum();
            //System.out.println(noOfRows); //4
            //System.out.println(noOfCols); //1
            Cell cell;
            data = new String[noOfRows - 1][noOfCols];

            for (int i = 1; i < noOfRows; i++) {
                for (int j = 0; j < noOfCols; j++) {
                    //System.out.println(i + " " + j);
                    row = sh.getRow(i);
                    cell = row.getCell(j);
                    data[i - 1][j] = cell.getStringCellValue();
                }
            }
        } catch (Exception e) {
            System.out.println("The exception is: " + e.getMessage());
        }
        return data;
    }

    @Test(dataProvider = "excel-data")
    public void SmartWatchWithEdgeBrowser(String searchName) throws IOException, InterruptedException {
        SearchItemData si = new SearchItemData(driver);
        si.enterItemInSearch().sendKeys(searchName);
        si.ClickSearchButton().click();
        Reporter.log("successfully  search the Bluetooth item", true);
        VerifySearchResults vsr = new VerifySearchResults(driver);
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(5));
        w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='search-result-txt-section  marT12']/span[@style='color: #212121; font-weight: normal']")));
        String actualString = vsr.VerifysearchCriteria().getText();
        System.out.println(actualString);
        Assert.assertTrue(actualString.contains("We've got"));
        Reporter.log("successfully verified search result", true);
        SortBy sb = new SortBy(driver);
        Thread.sleep(2000);
        sb.ClickonSortby().click();
        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='sorting-sec animBounce']/ul[@style='z-index: 17;']/li[@data-sorttype='plrty']")));
        sb.clickpopularity().click();
        Reporter.log("successfully Sorted with Popularity", true);
        SelectPriceRange spr = new SelectPriceRange(driver);
        spr.clickFristPriceRange().click();
        spr.clickFristPriceRange().clear();
        spr.clickFristPriceRange().sendKeys("700");
        Thread.sleep(2000);
        spr.clickLastPriceRange().click();
        spr.clickLastPriceRange().clear();
        spr.clickLastPriceRange().sendKeys("3000");
        Thread.sleep(2000);
        spr.clickonGOButton();
        Reporter.log("successfully Selected Range 700 to 3000", true);
        SaveFristItemNdPriceInExcel sfite = new SaveFristItemNdPriceInExcel(driver);
        Thread.sleep(2000);
        sfite.ExcelWriter();
        Reporter.log("successfully Written the frist  Item & price  Data in excel", true);
        SelectFristItem sfi = new SelectFristItem(driver);
        sfi.clickOnFristIteam();
        Set<String> windows = driver.getWindowHandles(); //[parentid,childid,subchildId]
        Iterator<String> it = windows.iterator();
        String parentId = it.next();
        String childId = it.next();
        driver.switchTo().window(childId);
        Reporter.log("successfully moved to another tab", true);
        AddToCart atc = new AddToCart(driver);
        atc.SelectAddtoCart().click();
        Reporter.log("successfully clicked the AddToCart", true);
        VerifyAddToCartItem vai = new VerifyAddToCartItem(driver);
        vai.clickonviewCart().click();
        Thread.sleep(3000);
        String verifiediteam = vai.ChecktheAddCartIteamRnot().getText();
        System.out.println(verifiediteam);
        Assert.assertEquals(verifiediteam, "Shopping Cart (1 Item)");
        Reporter.log("successfully verified AddtoCart item", true);
        RemoveCartAndVerify rcv = new RemoveCartAndVerify(driver);
        rcv.clickRemoveCart().click();
        Thread.sleep(3000);
        String verifedremovecart = rcv.verifyremoveCart().getText();
        System.out.println(verifedremovecart);
        Assert.assertEquals(verifedremovecart, "Shopping Cart is empty!");
        Reporter.log("successfully verified remove cart item", true);
    }

    @AfterMethod
    public void teardown() {
        //driver.close();
        driver.quit();
    }

}



