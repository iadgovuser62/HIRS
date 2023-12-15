package hirs.attestationca.portal.page.controllers;

import hirs.attestationca.persist.entity.Appraiser;
import hirs.attestationca.persist.entity.manager.PolicyRepository;
import hirs.attestationca.persist.entity.userdefined.PolicySettings;
import hirs.attestationca.persist.entity.userdefined.certificate.CertificateAuthorityCredential;
import hirs.attestationca.persist.entity.userdefined.certificate.EndorsementCredential;
import hirs.attestationca.portal.page.PageController;
import hirs.attestationca.portal.page.PageControllerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static hirs.attestationca.portal.page.Page.POLICY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests that test the URL End Points of PolicyPageController.
 */
public class PolicyPageControllerTest extends PageControllerTest {

    // Base path for the page
    private String pagePath;

    // Repository manager to handle data access between policy entity and data storage in db
    @Autowired
    private PolicyRepository policyRepository;

    // Policy refers to the settings such as whether to validate endorsement credentials, platform credentials, etc
    private PolicySettings policy;

    /**
     * Constructor requiring the Page's display and routing specification.
     *
     */
    public PolicyPageControllerTest() {
        super(POLICY);
        pagePath = getPagePath();
    }

    /**
     * Sets up policy
     */
    @BeforeAll
    public void setUpPolicy() {

        // create the supply chain policy
        policy = policyRepository.findByName("Default");
    }

    /**
     * Verifies that spring is initialized properly by checking that an autowired bean
     * is populated.
     */
    @Test
    public void verifySpringInitialized() {

        System.out.println("XXXX verifySpringInitialized");

        assertNotNull(policyRepository);
        assertNotNull(policy);
    }

    /**
     * Checks that the page initializes correctly.
     *
     * @throws Exception if test fails
     */
    @Test
    public void testInitPage() throws Exception {

        System.out.println("XXXX testInitPage");

        boolean ec = policy.isEcValidationEnabled();
        boolean pc = policy.isPcValidationEnabled();
        boolean fm = policy.isFirmwareValidationEnabled();

        // jamie print
        // TODO in master, if one of these is false, this test still passes (but does not pass here in main)
        //     for example, if this test runs after ec enable test is run so ec enable is true
        System.out.println("    XXXX testInitPage, ec: " + ec);
        System.out.println("    XXXX testInitPage, pc: " + pc);
        System.out.println("    XXXX testInitPage, fm: " + fm);

        // perform test
        getMockMvc()
                .perform(MockMvcRequestBuilders.get(pagePath))
                .andExpect(status().isOk())
                // Test that the two boolean policy values sent to the page match
                // the actual policy values.
                .andExpect(model().attribute(PolicyPageController.INITIAL_DATA,
                        hasProperty("enableEcValidation", is(ec))))
                .andExpect(model().attribute(PolicyPageController.INITIAL_DATA,
                        hasProperty("enablePcCertificateValidation", is(pc))))
                .andExpect(model().attribute(PolicyPageController.INITIAL_DATA,
                        hasProperty("enableFirmwareValidation", is(fm))));
    }

    /**
     * Verifies the rest call for enabling the EC Validation policy setting.
     *
     * @throws Exception if test fails
     */
    @Test
    public void testUpdateEcValEnable() throws Exception {

        System.out.println("XXXX testUpdateEcValEnable");

        ResultActions actions;

        //init the database (should all initially be false, but set them just in case the tests run out of order)
        policy = policyRepository.findByName("Default");
        policy.setPcValidationEnabled(false);
        policy.setEcValidationEnabled(false);
        policy.setFirmwareValidationEnabled(false);
        policyRepository.save(policy);

        //jamie prints
        List<PolicySettings> records1 = policyRepository.findAll();
        System.out.println("    XXXX testUpdateEcValEnable, before findbyname: isEcValidationEnabled: " + records1.get(0).isEcValidationEnabled());


        policy = policyRepository.findByName("Default");

        //jamie prints
        records1 = policyRepository.findAll();
        System.out.println("    XXXX testUpdateEcValEnable, after findbyname: isEcValidationEnabled: " + records1.get(0).isEcValidationEnabled());
        System.out.println("    XXXX testUpdateEcValEnable, after findbyname: isPcValidationEnabled: " + records1.get(0).isPcValidationEnabled());


        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-ec-validation")
                        .param("ecValidate", "checked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("success",
                                hasItem("Endorsement credential validation enabled"))));

        //jamie prints
        records1 = policyRepository.findAll();
        System.out.println("    XXXX testUpdateEcValEnable, after /update-ec-validation checked: isEcValidationEnabled: " + records1.get(0).isEcValidationEnabled());


        policy = policyRepository.findByName("Default");
        assertTrue(policy.isEcValidationEnabled());
    }

    /**
     * Verifies the rest call for disabling the EC Validation policy setting.
     *
     * @throws Exception if test fails
     */
    @Test
    public void testUpdateEcValDisable() throws Exception {

        System.out.println("XXXX testUpdateEcValDisable");

        //jamie prints
        List<PolicySettings> records1 = policyRepository.findAll();
        System.out.println("    XXXX testUpdateEcValDisable, before setting ec true: isEcValidationEnabled: " + records1.get(0).isEcValidationEnabled());


        ResultActions actions;

        //init the database
        policy = policyRepository.findByName("Default");
        policy.setPcValidationEnabled(false);
        policy.setEcValidationEnabled(true);
        policy.setFirmwareValidationEnabled(false);
        policyRepository.save(policy);

        records1 = policyRepository.findAll();
        System.out.println("    XXXX testUpdateEcValDisable, after setting ec true: isEcValidationEnabled: " + records1.get(0).isEcValidationEnabled());



        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-ec-validation")
                        .param("ecValidate", "unchecked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("success",
                                hasItem("Endorsement credential validation disabled"))));

        records1 = policyRepository.findAll();
        System.out.println("    XXXX testUpdateEcValDisable, after /update-ec-validation unchecked: isEcValidationEnabled: " + records1.get(0).isEcValidationEnabled());


        policy = policyRepository.findByName("Default");
        assertFalse(policy.isEcValidationEnabled());

        //reset database for invalid policy test
        policy.setEcValidationEnabled(true);
        policy.setPcValidationEnabled(true);
        policy.setFirmwareValidationEnabled(false);
        policyRepository.save(policy);

        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-ec-validation")
                        .param("ecValidate", "unchecked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("error",
                                hasItem("To disable Endorsement Credential Validation, Platform Validation"
                                        + " must also be disabled."))));

        policy = policyRepository.findByName("Default");
        assertTrue(policy.isEcValidationEnabled());

    }

    /**
     * Verifies the rest call for enabling the PC Validation policy setting.
     *
     * @throws Exception if test fails
     */
    @Test
    public void testUpdatePcValEnable() throws Exception {

        System.out.println("XXXX testUpdatePcValEnable");

        ResultActions actions;

        //init the database
        policy = policyRepository.findByName("Default");
        policy.setEcValidationEnabled(true);
        policy.setPcValidationEnabled(false);
        policy.setFirmwareValidationEnabled(false);
        policyRepository.save(policy);

        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-pc-validation")
                        .param("pcValidate", "checked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("success",
                                hasItem("Platform certificate validation enabled"))));

        policy = policyRepository.findByName("Default");
        assertTrue(policy.isPcValidationEnabled());

        //reset database for invalid policy test
        policy.setEcValidationEnabled(false);
        policy.setPcValidationEnabled(false);
        policy.setFirmwareValidationEnabled(false);
        policyRepository.save(policy);

        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-pc-validation")
                        .param("pcValidate", "checked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("error",
                                hasItem("Unable to change Platform Validation setting,"
                                        + "  invalid policy configuration."))));

        policy = policyRepository.findByName("Default");
        assertFalse(policy.isPcValidationEnabled());

    }

    /**
     * Verifies the rest call for disabling the PC Validation policy setting.
     * @throws Exception if test fails
     */
    @Test
    public void testUpdatePcValDisable() throws Exception {

        System.out.println("XXXX testUpdatePcValDisable");

        ResultActions actions;

        //init the database
        policy = policyRepository.findByName("Default");
        policy.setPcValidationEnabled(true);
        policy.setPcAttributeValidationEnabled(false);
        policy.setFirmwareValidationEnabled(false);
        policyRepository.save(policy);

        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-pc-validation")
                        .param("pcValidate", "unchecked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("success",
                                hasItem("Platform certificate validation disabled"))));

        policy = policyRepository.findByName("Default");
        assertFalse(policy.isPcValidationEnabled());

        // jamo this is making ec enable test not work
        //reset database for invalid policy test
        policy.setPcAttributeValidationEnabled(true);
        policy.setPcValidationEnabled(true);
        policy.setFirmwareValidationEnabled(false);
        policyRepository.save(policy);

        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-pc-validation")
                        .param("pcValidate", "unchecked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("error",
                                hasItem("Unable to change Platform Validation setting,"
                                        + "  invalid policy configuration."))));

        policy = policyRepository.findByName("Default");
        assertTrue(policy.isPcValidationEnabled());

    }

    /**
     * Verifies the rest call for enabling the PC attribute Validation policy setting.
     *
     * @throws Exception if test fails
     */
    @Test
    public void testUpdatePcAttributeValEnable() throws Exception {

        System.out.println("XXXX testUpdatePcAttributeValEnable");

        ResultActions actions;

        //init the database
        policy = policyRepository.findByName("Default");
        policy.setPcAttributeValidationEnabled(false);
        policy.setPcValidationEnabled(true);
        policy.setFirmwareValidationEnabled(false);
        policyRepository.save(policy);

        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-pc-attribute-validation")
                        .param("pcAttributeValidate", "checked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("success",
                                hasItem("Platform certificate attribute validation enabled"))));

        policy = policyRepository.findByName("Default");
        assertTrue(policy.isPcAttributeValidationEnabled());

        //reset database for invalid policy test
        policy.setPcAttributeValidationEnabled(false);
        policy.setPcValidationEnabled(false);
        policyRepository.save(policy);

        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-pc-attribute-validation")
                        .param("pcAttributeValidate", "checked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("error",
                                hasItem("To enable Platform Attribute Validation,"
                                        + " Platform Credential Validation must also be enabled."))));

        policy = policyRepository.findByName("Default");
        assertFalse(policy.isPcAttributeValidationEnabled());

    }

    /**
     * Verifies the rest call for disabling the PC attribute validation policy setting.
     * @throws Exception if test fails
     */
    @Test
    public void testUpdatePcAttributeValDisable() throws Exception {

        System.out.println("XXXX testUpdatePcAttributeValDisable");

        ResultActions actions;

        // perform the mock request
        actions = getMockMvc()
                .perform(MockMvcRequestBuilders.post(pagePath + "/update-pc-attribute-validation")
                        .param("pcAttributeValidate", "unchecked"));

        actions
                // check HTTP status
                .andExpect(status().is3xxRedirection())
                // check the messages forwarded to the redirected page
                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
                        hasProperty("success",
                                hasItem("Platform certificate attribute validation disabled"))));

        policy = policyRepository.findByName("Default");
        assertFalse(policy.isPcAttributeValidationEnabled());
    }

//    /**
//     * Helper function to get a fresh load of the default policy from the DB.
//     *
//     * @return The default Supply Chain Policy
//     */
//    private SupplyChainPolicy getDefaultPolicy() {
//        final Appraiser supplyChainAppraiser = appraiserManager.getAppraiser(
//                SupplyChainAppraiser.NAME);
//        return (SupplyChainPolicy) policyManager.getDefaultPolicy(
//                supplyChainAppraiser);
//    }

}
