package SetUpClass.Excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ReadExcel {


    @Test
    public  void excel() throws IOException {

        File src=new File("C:\\Users\\as61837\\Downloads\\exceldata\\ReadWriteExcelData.xlsx");
        FileInputStream fis=new FileInputStream(src);
        XSSFWorkbook wb=new XSSFWorkbook(fis);
        XSSFSheet sh=wb.getSheetAt(0);
        System.out.println(sh.getRow(1).getCell(0).getStringCellValue());
        System.out.println(sh.getRow(2).getCell(0).getStringCellValue());
        System.out.println(sh.getRow(3).getCell(0).getStringCellValue());
    }
}
