package ro.amihai.rest;

import java.io.File;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Andrei Mihai
 */
public class RestSimulatorHandler {

    private static final String SIMULATOR_SAMPLE_DIRECTORY = "src/test/resources/sample/simulator/";
	//~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void doGet(Exchange exchange) throws IOException {
        String address = exchange.getIn().getHeader("address", String.class);

        logger.debug("DO GET for address [{}]", address);

        String response;

        if ((null != address) && !address.contains("INVALID")) {
            response = FileUtils.readFileToString(new File(SIMULATOR_SAMPLE_DIRECTORY + "validAnswer.json"));
        } else {
            response = FileUtils.readFileToString(new File(SIMULATOR_SAMPLE_DIRECTORY + "invalidAnswer.json"));
        }
        exchange.getIn().setBody(response);

        logger.debug("Response for address [{}] IS [{}]", address, response);
    }
}
