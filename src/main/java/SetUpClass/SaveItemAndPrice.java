package SetUpClass;

import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaveItemAndPrice {
    WebDriver driver;

    public SaveItemAndPrice(WebDriver driver) {
        this.driver = driver;
    }

    public void CsvReaderNdWriter() throws IOException {
        List<WebElement> Title_List = driver.findElements(By.xpath("//p[@class='product-title']"));
        List<WebElement> Price_List = driver.findElements(By.xpath("//span[@class='lfloat product-price']"));

        CSVWriter csvWriter = new CSVWriter(new FileWriter("StoreItem.csv"));
        int n = 5;
        for (int i = 0; i <= n; i++) {
            String title_name = Title_List.get(i).getText();
            try {
               // System.out.println(i + " " + title_name);
                String set[] = {title_name};
                csvWriter.writeNext(set);
                for (int j = 0; j <= 5; j++) {
                    if (j == i) {
                        String price_value = Price_List.get(j).getText();
                       // System.out.println(j + " " + price_value);
                        String set1[] = {price_value};
                        csvWriter.writeNext(set1);
                    } else {
                        continue;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("The Index you have return is invalid");
            }
        }
        csvWriter.close();
    }
}

