package hirs.utils.rim.unsignedRim.cbor.ietfCoswid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.CoswidConfigValidator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CoswidConfigValidator unit tests
 * Reads in config files and checks for a set number of fields passing or failing
 */
public class CoswidConfigValidatorTest {
    /**
     * Checks that a known good config file passes
     * @throws IOException
     */
    @Test
    public final void testGoodCosiwdConfig() throws IOException {
        String coswidConfigFile = "coswid/config/coswid_rim_1_good.json";
        ObjectMapper mapper = new ObjectMapper();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream coswidConfigStream = classLoader.getResourceAsStream(coswidConfigFile);
        byte[] coswidData = coswidConfigStream.readAllBytes();

        JsonNode rootNode = mapper.readTree(coswidData);
        CoswidConfigValidator ConfigValidator = new CoswidConfigValidator();
        boolean isValid = ConfigValidator.isValid(rootNode);
        assertTrue(isValid);
        int invalidItems=ConfigValidator.getInvalidFieldCount();
        assertEquals(0,ConfigValidator.getInvalidFieldCount());
        if (! isValid)
        {
            System.out.println( "There are " + ConfigValidator.getInvalidFieldCount() + " invalid field(s) : " + ConfigValidator.getInvalidFields());
        }
    }

    /**
     * Checks that a single bad field is detected
     * @throws IOException
     */
    @Test
    public final void testSingleBadfield() throws IOException {
        String coswidConfigFile = "coswid/config/coswid_rim_bad_tagId.json";
        ObjectMapper mapper = new ObjectMapper();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream coswidConfigStream = classLoader.getResourceAsStream(coswidConfigFile);
        byte[] coswidData = coswidConfigStream.readAllBytes();

        JsonNode rootNode = mapper.readTree(coswidData);
        CoswidConfigValidator ConfigValidator = new CoswidConfigValidator();
        boolean isValid = ConfigValidator.isValid(rootNode);
        assertFalse(isValid);
        assertEquals(1,ConfigValidator.getInvalidFieldCount());
    }

    /**
     * Checks that the validator picks up all bad fields in a file
     * @throws IOException
     */
    @Test
    public final void testBadCoswidConfig() throws IOException {
        String coswidConfigFile = "coswid/config/coswid_rim_all_bad.json";
        ObjectMapper mapper = new ObjectMapper();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream coswidConfigStream = classLoader.getResourceAsStream(coswidConfigFile);
        byte[] coswidData = coswidConfigStream.readAllBytes();

        JsonNode rootNode = mapper.readTree(coswidData);
        CoswidConfigValidator ConfigValidator = new CoswidConfigValidator();
        boolean isValid = ConfigValidator.isValid(rootNode);
        assertFalse(isValid);
        assertEquals(27,ConfigValidator.getInvalidFieldCount());
    }
}