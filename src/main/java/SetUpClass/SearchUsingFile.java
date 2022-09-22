package SetUpClass;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;

public class SearchUsingFile {
    @Test
    public static void openBrowser() throws IOException, CsvValidationException {
        WebDriver driver;
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.get("https://www.snapdeal.com/");

        CSVReader reader ;
        reader = new CSVReader(new FileReader("SearchItem.csv"));
        String[] cell = reader.readNext();


        while ((cell = reader.readNext()) != null) {
            for (int i = 0; i < 1; i++) {
                String Keyword = cell[i];
                driver.findElement(By.id("inputValEnter")).sendKeys(Keyword);
            }
        }

       /* driver.findElement(By.xpath("//*[@id=\"sdHeader\"]/div[4]/div[2]/div/div[2]/button/span")).click();
        List<WebElement> Title_List =
                driver.findElements(By.xpath("//p[@class='product-title']"));
        List<WebElement> Price_List =
                driver.findElements(By.xpath("//span[@class='lfloat product-price']"));
        System.out.println("Product List :");
        CSVWriter csvWriter = new CSVWriter(new FileWriter("StoreItem.csv"));

        int n = 5;
        for (int i = 0; i <= n; i++) {

            String title_name = Title_List.get(i).getText();
            try {
                System.out.println(i + " " + title_name);
                String set[] = {title_name};
                csvWriter.writeNext(set);
                for (int j = 0; j <= 5; j++) {
                    if (j == i) {
                        String price_value = Price_List.get(j).getText();
                        System.out.println(j + " " + price_value);
                        String set1[] = {price_value};
                        csvWriter.writeNext(set1);
                    } else {
                        continue;
                    }

                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("The Index you have return is invalid");
            }

        }*/
    }
}

