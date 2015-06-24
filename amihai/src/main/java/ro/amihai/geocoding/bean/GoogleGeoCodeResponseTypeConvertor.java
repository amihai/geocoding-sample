package ro.amihai.geocoding.bean;

import com.google.gson.Gson;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;

import static ro.amihai.geocoding.bean.GeocodingXLSColumn.GOOGLE_VERIFIED_ADDRESS;
import static ro.amihai.geocoding.bean.GeocodingXLSColumn.LATITUDE;
import static ro.amihai.geocoding.bean.GeocodingXLSColumn.LONGITUDE;


/**
 * This TypeConverter is used by Camel each time when we call from DSL .convertBodyTo(GoogleGeoCodeResponse.class)
 * 
 * @author Andrei Mihai
 *
 */
public class GoogleGeoCodeResponseTypeConvertor extends TypeConverterSupport {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
        String json = (String) value;

        Gson gson = new Gson();

        // If the json is not a JSON convert it JSON first. Sometime the answer is a String instead of JSON
        // This is actually a bug in the gson library
        if (json.startsWith("\"")) {
            json = gson.fromJson(json, String.class);
        }

        GoogleGeoCodeResponse result = gson.fromJson(json, GoogleGeoCodeResponse.class);

        if ("OK".equals(result.status)) {
        	//We need to set this headers here because we will automatically iterate on all the headers and create the XLS output at the end of the route
            exchange.getIn().setHeader(GOOGLE_VERIFIED_ADDRESS.getExchangeHeaderName(), result.results[0].formatted_address);
            exchange.getIn().setHeader(LATITUDE.getExchangeHeaderName(), result.results[0].geometry.location.lat);
            exchange.getIn().setHeader(LONGITUDE.getExchangeHeaderName(), result.results[0].geometry.location.lng);
        }
        exchange.getIn().setHeader("geocoding.status", result.status);
        exchange.getIn().setHeader("geocoding.error_message", result.error_message);

        return (T) result;
    }

}
