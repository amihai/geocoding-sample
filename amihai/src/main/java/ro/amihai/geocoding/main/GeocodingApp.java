package ro.amihai.geocoding.main;

import org.apache.camel.spring.Main;

import ro.amihai.geocoding.routes.GeocodingRouteBuilder;

/**
 * 
 * This class allow us to boot the Spring Context and Camel Context from command line.
 * @see GeocodingRouteBuilder for the entry point of the application.
 * @author Andrei Mihai
 *
 */
public class GeocodingApp {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void main(String... args) throws Exception {

        // This line will start the routes defined in the GeocodingRouteBuilder
        // So the GeocodingRouteBuilder is the main entry point of my application
        Main.main(args);
    }
}
