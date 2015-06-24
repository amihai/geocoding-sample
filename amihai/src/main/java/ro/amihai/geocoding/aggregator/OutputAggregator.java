package ro.amihai.geocoding.aggregator;

import java.util.LinkedList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;


/**
 * @author Andrei Mihai
 */
public class OutputAggregator implements AggregationStrategy {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------
	
	
	public static final String AGGREGATED_LIST_HEADER = "aggregated.list";

	/**
	 * Aggregate all the rows of an XLS after we did the request to Geocoding API
	 */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

    	//If this is the first aggregation
        if (oldExchange == null) {
            List<Message> aggregatedList = new LinkedList<Message>();
            aggregatedList.add(newExchange.getIn());
            newExchange.getIn().setHeader(AGGREGATED_LIST_HEADER, aggregatedList);

            return newExchange;
        }

        @SuppressWarnings("unchecked")
		List<Message> aggregatedList = oldExchange.getIn().getHeader(AGGREGATED_LIST_HEADER, List.class);
        aggregatedList.add(newExchange.getIn());

        return oldExchange;
    }

}
