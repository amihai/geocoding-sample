package ro.amihai.geocoding.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Andrei Mihai
 */
public class WriteXLS {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

	/**
	 * Dump on disk the actual XLS file. We cannot use the "file" camel endpoint because
	 * it is not supporting the XSSFWorkbook as a payload so we need to manually write on disk
	 */
    public void writeXLS(Exchange exchange, String outputDirectoryPath) throws IOException {
        XSSFWorkbook workbook = exchange.getIn().getBody(XSSFWorkbook.class);

        File outputDirectory = new File(outputDirectoryPath);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        File outputFile = new File(outputDirectory.getAbsolutePath() + File.separator + exchange.getIn().getHeader(Exchange.FILE_NAME));
        outputFile.createNewFile();

        FileOutputStream out = new FileOutputStream(outputFile);

        workbook.write(out);
    }

}
