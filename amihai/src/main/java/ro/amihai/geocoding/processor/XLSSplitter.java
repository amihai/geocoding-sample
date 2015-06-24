package ro.amihai.geocoding.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.amihai.geocoding.bean.GeocodingXLSColumn;

/**
 * @author Andrei Mihai
 */
public class XLSSplitter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Split the input XLS file by row. Each row will be processed separately and after that it will be aggregated.
     * This splitter know to detect if the input file is an Old Office 2007 or an Office 2013. 
     */
    public List<Message> splitInput(Exchange exchange) throws IOException {
        logger.debug("Start to split the XLS from exchange [{}]", exchange.getExchangeId());

        List<Message> messages = new LinkedList<Message>();

        File inputFile = exchange.getIn().getBody(File.class);
        FileInputStream file = new FileInputStream(inputFile);

        Iterator<Row> iterator = null;
        XSSFWorkbook xssfWorkbook = null;
        HSSFWorkbook hssfWorkbook = null;

        //If Office 2013 we need to use POI-XSSF
        if (inputFile.getName().endsWith(".xlsx")) {
            xssfWorkbook = new XSSFWorkbook(file);

            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

            iterator = sheet.iterator();
        } else {
            hssfWorkbook = new HSSFWorkbook(file);

            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            iterator = sheet.iterator();
        }

        if (iterator.hasNext())
            iterator.next(); //skip the header

        while (iterator.hasNext()) {
            Row row = iterator.next();
            Message message = exchange.getIn().copy(); //We copy the In message to be sure that we will keep al lthe headers from the exchange

            //Set the XLS cell values as header on exchange
            for (GeocodingXLSColumn column : GeocodingXLSColumn.values()) {
                setMessageHeader(message, row, column);
            }

            messages.add(message);
        }

        if (null != xssfWorkbook) {
            xssfWorkbook.close();
        }
        if (null != hssfWorkbook) {
            hssfWorkbook.close();
        }

        UUID uuid = UUID.randomUUID();
        for (Message message : messages) {
            //I will use this header in the aggregation strategy
            message.setHeader("input.size", messages.size());

            //I will use this to know what messages I need to aggregate
            message.setHeader("input.uuid", uuid.toString());
        }

        logger.debug("The input was splited into [{}] messages", messages.size());

        return messages;
    }

    private static <T> void setMessageHeader(Message message, Row row, GeocodingXLSColumn entry) {
        Cell cell = row.getCell(entry.getIndex());
        if (null != cell) {
            String cellValue = null;
            if (String.class.equals(entry.getType())) {
                cellValue = cell.getStringCellValue();
            }
            if (Double.class.equals(entry.getType())) {
                cellValue = String.valueOf((int) cell.getNumericCellValue());
            }
            message.setHeader(entry.getExchangeHeaderName(), cellValue.replaceAll(" ", "+"));
        }
    }

}
