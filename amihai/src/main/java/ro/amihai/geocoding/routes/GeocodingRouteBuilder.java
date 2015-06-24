package ro.amihai.geocoding.routes;

import static org.apache.camel.Exchange.HTTP_METHOD;
import static org.apache.camel.Exchange.HTTP_QUERY;
import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.component.cache.CacheConstants.CACHE_KEY;
import static org.apache.camel.component.cache.CacheConstants.CACHE_OPERATION;
import static org.apache.camel.component.cache.CacheConstants.CACHE_OPERATION_ADD;
import static org.apache.camel.component.cache.CacheConstants.CACHE_OPERATION_GET;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cache.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;

import ro.amihai.geocoding.aggregator.OutputAggregator;
import ro.amihai.geocoding.bean.GoogleGeoCodeResponse;
import ro.amihai.geocoding.bean.GoogleGeoCodeResponseTypeConvertor;
import ro.amihai.geocoding.processor.CreateXLS;
import ro.amihai.geocoding.processor.GeocodingErrorHandler;
import ro.amihai.geocoding.processor.WriteXLS;
import ro.amihai.geocoding.processor.XLSSplitter;

/**
 * @author Andrei Mihai
 */
public class GeocodingRouteBuilder extends RouteBuilder {

    private static final String CACHE = "cache://geocoding";

	//~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Autowired
    private XLSSplitter inputSplitter;

    @Autowired
    private CreateXLS createXLS;

    @Autowired
    private WriteXLS writeXLS;

    @Autowired
    private OutputAggregator outputAggregator;

    @Autowired
    private GeocodingErrorHandler geocodingErrorHandler;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * This is the logical entry point of the application. 
     * Here we define the Apache Camel routes that will draw the skeleton of the entire functionality. 
     */
    public void configure() {

        //J-
        onException(Exception.class)
                .handled(true)
                .process(geocodingErrorHandler)
                .stop()
        .end();
        //J+

        //J- This is the entry point of the application
        from("file:{{geocoding.input.folder}}").routeId("Geocoding Route")
            .split().method(inputSplitter)
            
            //This address header is specific to the Geocoding API
            .setHeader("address", simple("${in.header.AddressColumn},${in.header.City},${in.header.Country1}"))
            
             //Try to read the value (JSON String) from cache using the address as key
            .setHeader(CACHE_OPERATION, constant(CACHE_OPERATION_GET))
            .setHeader(CACHE_KEY, simple("${in.header.address}"))
            .to(CACHE)
            // Check if entry was not found
            .choice()
            	.when(header(CacheConstants.CACHE_ELEMENT_WAS_FOUND).isNull())
            		.setHeader(HTTP_QUERY, simple("address=${in.header.address}&key={{geocoding.API_KEY}}"))
            		.setHeader(HTTP_METHOD, constant("GET"))
            		.to("{{geocoding.url}}")
            		.convertBodyTo(String.class)
            		.setHeader(CACHE_OPERATION, constant(CACHE_OPERATION_ADD))
            		.setHeader(CACHE_KEY, simple("${in.header.address}"))
            		.to(CACHE)
            .end()		
            .convertBodyTo(GoogleGeoCodeResponse.class)
            .choice()
                .when(header("geocoding.status").isNotEqualTo("OK"))
                     .log(ERROR, "${in.header[geocoding.error_message]}")
                     //TODO - add here specific logic per each error return type (for example a retry mechanism)
                .endChoice()
                .otherwise()
                     .log("Request processed wihout error")
                .endChoice()
            .end()
            .aggregate(header("input.uuid"), outputAggregator).completionSize(simple("${in.header[input.size]}")) //aggregate all the rows of the output file
            .process(createXLS)
            .bean(writeXLS, "writeXLS(*, {{geocoding.output.folder}})");
        //J+
        
        //J- Configure the cache component
        from(CACHE +
                "?maxElementsInMemory=1000" +
                "&memoryStoreEvictionPolicy=" +
                    "MemoryStoreEvictionPolicy.LFU" +
                "&overflowToDisk=true" +
                "&eternal=true" +
                "&timeToLiveSeconds=300" +
                "&timeToIdleSeconds=300" +
                "&diskPersistent=true" +
                "&diskExpiryThreadIntervalSeconds=300")
                .stop();
        //J+

        getContext().getTypeConverterRegistry().addTypeConverter(GoogleGeoCodeResponse.class, String.class, new GoogleGeoCodeResponseTypeConvertor());

    }

    public void setInputSplitter(XLSSplitter inputSplitter) {
        this.inputSplitter = inputSplitter;
    }

    public void setGeocodingErrorHandler(GeocodingErrorHandler geocodingErrorHandler) {
        this.geocodingErrorHandler = geocodingErrorHandler;
    }

    public void setCreateXLS(CreateXLS createXLS) {
        this.createXLS = createXLS;
    }

    public void setOutputAggregator(OutputAggregator outputAggregator) {
        this.outputAggregator = outputAggregator;
    }

    public void setWriteXLS(WriteXLS writeXLS) {
        this.writeXLS = writeXLS;
    }

}
