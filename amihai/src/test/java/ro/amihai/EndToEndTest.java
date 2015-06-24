package ro.amihai;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ro.amihai.util.TestIOUtil.OUTPUT_PATH;
import static ro.amihai.util.TestIOUtil.SAMPLE_PATH;
import static ro.amihai.util.TestIOUtil.cleanupBeforeTest;
import static ro.amihai.util.TestIOUtil.processInput;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ro.amihai.rest.RestSimulator;
import ro.amihai.util.AssertUtil;


/**
 * @author Andrei Mihai
 */
public class EndToEndTest extends CamelSpringTestSupport {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "file:{{geocoding.input.folder}}")
    protected ProducerTemplate template;
    
    private CamelContext camelContext;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Before
    public void tearUp() throws Exception {
        camelContextSetup();

        cleanupBeforeTest();
    }

    /**
     * Test processing a valid set of answers from Geocoding API
     */
    @Test
    public void testValidResponse() throws IOException, InterruptedException {
        testResponseForSample("Addresses_input.xlsx");
    }

    /**
     * Test that the application can process an error response from the Geocoding API. We should skip updating the
     * fields from the xls.
     */
    @Test
    public void testErrorResponse() throws IOException, InterruptedException {
        testResponseForSample("Addresses_invalidResponse.xlsx");
    }

    /**
     * Test the processing of old Office 2007 XLS format
     */
    @Test
    public void testProcessOffice2007Files() throws IOException, InterruptedException {
        testResponseForSample("Addresses_office_2007.xls");
    }

    @After
    public void tearDown() throws Exception {
        camelContext.stop();
        TimeUnit.SECONDS.sleep(2);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
    	ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("META-INF/spring/*.xml");
        camelContext = (SpringCamelContext) applicationContext.getBean(CamelContext.class);
        return applicationContext;
    }

    //Do the actual testing for the specified sample
    private void testResponseForSample(String sampleFileName) throws IOException, InterruptedException {
        processInput(sampleFileName);

        resultEndpoint.reset();

        resultEndpoint.expectedMessageCount(1);

        MockEndpoint.assertIsSatisfied(30L, SECONDS, resultEndpoint);

        File expected = new File(SAMPLE_PATH + "expected" + File.separator + sampleFileName);
        File actual = new File(OUTPUT_PATH + File.separator + sampleFileName);
        AssertUtil.asserXLSEquals("The actual XLS returned by the application is different from the expected one", expected, actual);
    }

    //When we will have more test classed we will need to move this method to an util class
    private void camelContextSetup() throws Exception {
        // Add rest simulator
        camelContext.addRoutes(new RestSimulator());

        ((ModelCamelContext) camelContext).getRouteDefinition("Geocoding Route").adviceWith(((ModelCamelContext) camelContext), new AdviceWithRouteBuilder() {
                @Override
                public void configure() throws Exception {
                    weaveAddLast().to(resultEndpoint);
                }
            });

        camelContext.start();
    }

}
