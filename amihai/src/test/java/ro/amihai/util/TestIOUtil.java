package ro.amihai.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


/**
 * @author Andrei Mihai
 */
public class TestIOUtil {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static final String OUTPUT_PATH = "target/output/";
    public static final String INPUT_PATH = "target/input/";
    public static final String SAMPLE_PATH = "src/test/resources/sample/";

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void cleanupBeforeTest() throws IOException {
        File inputDir = new File(INPUT_PATH);
        if (inputDir.exists()) {
            FileUtils.cleanDirectory(inputDir);
        }

        File outputDir = new File(OUTPUT_PATH);
        if (outputDir.exists()) {
            FileUtils.cleanDirectory(outputDir);
        }
    }

    public static void processInput(String inputFileName) throws IOException {
        FileUtils.copyFile(new File(SAMPLE_PATH + inputFileName), new File(INPUT_PATH + inputFileName));
    }

}
