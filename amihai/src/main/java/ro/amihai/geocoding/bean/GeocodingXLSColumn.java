package ro.amihai.geocoding.bean;

/**
 * This enum allow us to iterate more easy on the columns of the input XLS file
 * 
 * @author Andrei Mihai
 */
public enum GeocodingXLSColumn {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    //J-
    ADDRESS("Address", 0, "AddressColumn", String.class),
    CITY("City", 1, "City", String.class),
    COUNTRY1("Country", 2, "Country1", String.class),
    POSTAL_CODE("Postal_Code", 3, "Postal_Code", Double.class),
    STATE_PROVINCE("State_Province", 4, "State_Province", String.class),
    COUNTRY2("Country", 5, "Country2", String.class),
    GOOGLE_VERIFIED_ADDRESS("Google Verified Address", 6, "Google_Verified_Address", String.class),
    LATITUDE("Latitude", 7, "Latitude", String.class),
    LONGITUDE("Longitude", 8, "Longitude", String.class);
    //J+

    //Header from the XLS Input
    private String xlsHeader;
    
    //The Column Index
    private int index;
    
    //The name of the header from the exhange that will be used t ostore the object
    private String exchangeHeaderName;
    
    //The Java type of the column's cell
    private Class<? extends Object> type;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    private GeocodingXLSColumn(String xlsHeader, int index, String exchangeHeaderName, Class<? extends Object> type) {
        this.xlsHeader = xlsHeader;
        this.index = index;
        this.exchangeHeaderName = exchangeHeaderName;
        this.type = type;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public String getXlsHeader() {
        return xlsHeader;
    }

    public int getIndex() {
        return index;
    }

    public String getExchangeHeaderName() {
        return exchangeHeaderName;
    }

    public Class<? extends Object> getType() {
        return type;
    }

}
