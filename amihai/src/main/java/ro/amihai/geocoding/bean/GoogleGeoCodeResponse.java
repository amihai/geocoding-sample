package ro.amihai.geocoding.bean;

/**
 * This class only is copy paste from
 * http://stackoverflow.com/questions/7265833/how-to-serialize-and-deserialize-a-json-object-from-google-geocode-using-java
 */
public class GoogleGeoCodeResponse {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    public String status;
    public String error_message;
    public results[] results;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public GoogleGeoCodeResponse() {
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Inner Classes 
    //~ ----------------------------------------------------------------------------------------------------------------

    public class results {
        public String formatted_address;
        public geometry geometry;
        public String[] types;
        public address_component[] address_components;
    }

    public class geometry {
        public bounds bounds;
        public String location_type;
        public location location;
        public bounds viewport;
    }

    public class bounds {

        public location northeast;
        public location southwest;
    }

    public class location {
        public String lat;
        public String lng;
    }

    public class address_component {
        public String long_name;
        public String short_name;
        public String[] types;
    }
}
