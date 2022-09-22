package SetUpClass.Excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteData {

    @Test
    public void write() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        sheet.createRow(0);
        sheet.getRow(0).createCell(0).setCellValue("sanpdeal");

        File file =new File( "C:\\Users\\as61837\\Downloads\\document.xlsx");
        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);
        workbook.close();
        System.out.println("data writted succesfully");
    }
}