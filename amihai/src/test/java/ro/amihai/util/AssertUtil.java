package ro.amihai.util;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;


/**
 * @author Andrei Mihai
 */
public class AssertUtil {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

	/**
	 * Compare two XLS files.
	 */
    public static void asserXLSEquals(String message, File expected, File actual) throws IOException {
        Assert.assertNotNull("Expected XLS file must not be null", expected);
        Assert.assertTrue("Expected XLS file must exists", expected.exists());

        Assert.assertNotNull("Actual XLS file must not be null", actual);
        Assert.assertTrue("Actual XLS file must exists", actual.exists());

        FileInputStream expectedStream = new FileInputStream(expected);
        FileInputStream actualStream = new FileInputStream(actual);

        XSSFWorkbook expectedWorkbook = new XSSFWorkbook(expectedStream);
        XSSFWorkbook actualWorkbook = new XSSFWorkbook(actualStream);

        try {
            XSSFSheet expectedSheet = expectedWorkbook.getSheetAt(0);
            XSSFSheet actualSheet = actualWorkbook.getSheetAt(0);

            Assert.assertEquals("The number of records from the XLS files is different", expectedSheet.getLastRowNum(), actualSheet.getLastRowNum());

            for (int rowNum = 0; rowNum <= expectedSheet.getLastRowNum(); rowNum++) {
                Row expectedRow = expectedSheet.getRow(rowNum);
                Row actualRow = actualSheet.getRow(rowNum);

                Assert.assertEquals("The number of columns from the XLS files is different", expectedRow.getLastCellNum(), actualRow.getLastCellNum());

                for (int cellNum = 0; cellNum <= expectedRow.getLastCellNum(); cellNum++) {
                    if ((null != expectedRow.getCell(cellNum)) && (null != actualRow.getCell(cellNum))) {
                        if (expectedRow.getCell(cellNum).getCellType() == CELL_TYPE_STRING) {
                            Assert.assertEquals(expectedRow.getCell(cellNum).getStringCellValue(), actualRow.getCell(cellNum).getStringCellValue());
                        } else if (expectedRow.getCell(cellNum).getCellType() == CELL_TYPE_NUMERIC) {
                            Assert.assertTrue(expectedRow.getCell(cellNum).getNumericCellValue() == actualRow.getCell(cellNum).getNumericCellValue());
                        }
                    }
                }
            }
        } finally {
            expectedWorkbook.close();
            actualWorkbook.close();
        }

    }

}
