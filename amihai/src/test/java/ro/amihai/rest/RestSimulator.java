package ro.amihai.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;


/**
 * @author Andrei Mihai
 */
public class RestSimulator extends RouteBuilder {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private RestSimulatorHandler handler = new RestSimulatorHandler();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    public void configure() throws Exception {
        //J-
        restConfiguration()
                .component("jetty")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .port(8080);
        //J+

        //J-
        rest("/json").description("Geocoding Rest simulator")
                .produces("application/json")
                .get()
                    .to("direct:response");

        //J+

        //J-
        from("direct:response")
                .bean(handler, "doGet");
        //J+

    }

}
