package ro.amihai.geocoding.processor;

import static ro.amihai.geocoding.aggregator.OutputAggregator.AGGREGATED_LIST_HEADER;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ro.amihai.geocoding.bean.GeocodingXLSColumn;

/**
 * @author Andrei Mihai
 */
public class CreateXLS implements Processor {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

	/**
	 * Convert the list of Messages aggregated on the exchange into an XLS object.
	 */
    @Override
    public void process(Exchange exchange) throws Exception {
        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) exchange.getIn().getHeader(AGGREGATED_LIST_HEADER);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        int rownum = 0;

        //First Create the Header row
        Row header = sheet.createRow(rownum++);
        for (GeocodingXLSColumn column : GeocodingXLSColumn.values()) {
            Cell cell = header.createCell(column.getIndex());
            cell.setCellValue(column.getXlsHeader());
        }

        for (Message message : messages) {
            Row row = sheet.createRow(rownum++);
            for (GeocodingXLSColumn column : GeocodingXLSColumn.values()) {
                Cell cell = row.createCell(column.getIndex());

                if (String.class.equals(column.getType())) {
                    cell.setCellValue(message.getHeader(column.getExchangeHeaderName(), String.class));
                } else {
                    cell.setCellValue(Double.valueOf(message.getHeader(column.getXlsHeader(), String.class)));
                }
            }
        }

        exchange.getIn().setBody(workbook);

    }

}
