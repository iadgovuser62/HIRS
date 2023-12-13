package hirs.attestationca.portal.page.controllers;

import hirs.attestationca.persist.entity.Appraiser;
import hirs.attestationca.persist.entity.manager.PolicyRepository;
import hirs.attestationca.persist.entity.userdefined.PolicySettings;
import hirs.attestationca.portal.page.PageController;
import hirs.attestationca.portal.page.PageControllerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static hirs.attestationca.portal.page.Page.POLICY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

//        // create default group so that the policy can be applied as a default.
//        if (groupManager.getDeviceGroup(DeviceGroup.DEFAULT_GROUP) == null) {
//            groupManager.saveDeviceGroup(new DeviceGroup(DeviceGroup.DEFAULT_GROUP));
//        }
//
//        appraiserManager.saveAppraiser(new SupplyChainAppraiser());
//        final Appraiser supplyChainAppraiser = appraiserManager.getAppraiser(
//                SupplyChainAppraiser.NAME);
//
//        policy = new SupplyChainPolicy("DEFAULT SCP", "a default policy");
//        policyManager.savePolicy(policy);
//        policyManager.setDefaultPolicy(supplyChainAppraiser, policy);
//
//
//        policy = (SupplyChainPolicy) policyManager.getDefaultPolicy(
//                supplyChainAppraiser);

        // create the supply chain policy
        policy = new PolicySettings("DEFAULT SCP", "a default policy");
        policyRepository.save(policy);

        policy = (PolicySettings) policyRepository.findByName("DEFAULT SCP");

    }

    /**
     * Verifies that spring is initialized properly by checking that an autowired bean
     * is populated.
     */
    @Test
    public void verifySpringInitialized() {
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

        boolean ec = policy.isEcValidationEnabled();
        boolean pc = policy.isPcValidationEnabled();
        boolean fm = policy.isFirmwareValidationEnabled();


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

//    /**
//     * Verifies the rest call for enabling the EC Validation policy setting.
//     *
//     * @throws Exception if test fails
//     */
//    @Test
//    public void testUpdateEcValEnable() throws Exception {
//
//        final String baseURL = "/" + POLICY.getViewName();
//        ResultActions actions;
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-ec-validation")
//                        .param("ecValidate", "checked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("success",
//                                hasItem("Endorsement credential validation enabled"))));
//
//        policy = getDefaultPolicy();
//        Assert.assertTrue(policy.isEcValidationEnabled());
//    }
//
//    /**
//     * Verifies the rest call for disabling the EC Validation policy setting.
//     *
//     * @throws Exception if test fails
//     */
//    @Test
//    public void testUpdateEcValDisable() throws Exception {
//
//        final String baseURL = "/" + POLICY.getViewName();
//        ResultActions actions;
//
//        //init the database
//        policy = getDefaultPolicy();
//        policy.setPcValidationEnabled(false);
//        policy.setEcValidationEnabled(true);
//        policy.setFirmwareValidationEnabled(false);
//        policyManager.updatePolicy(policy);
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-ec-validation")
//                        .param("ecValidate", "unchecked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("success",
//                                hasItem("Endorsement credential validation disabled"))));
//
//        policy = getDefaultPolicy();
//        Assert.assertFalse(policy.isEcValidationEnabled());
//
//        //reset database for invalid policy test
//        policy.setEcValidationEnabled(true);
//        policy.setPcValidationEnabled(true);
//        policy.setFirmwareValidationEnabled(false);
//        policyManager.updatePolicy(policy);
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-ec-validation")
//                        .param("ecValidate", "unchecked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("error",
//                                hasItem("To disable Endorsement Credential Validation, Platform Validation"
//                                        + " must also be disabled."))));
//
//        policy = getDefaultPolicy();
//        Assert.assertTrue(policy.isEcValidationEnabled());
//
//    }
//
//    /**
//     * Verifies the rest call for enabling the PC Validation policy setting.
//     *
//     * @throws Exception if test fails
//     */
//    @Test
//    public void testUpdatePcValEnable() throws Exception {
//
//        final String baseURL = "/" + POLICY.getViewName();
//        ResultActions actions;
//
//        //init the database
//        policy = getDefaultPolicy();
//        policy.setEcValidationEnabled(true);
//        policy.setPcValidationEnabled(false);
//        policy.setFirmwareValidationEnabled(false);
//        policyManager.updatePolicy(policy);
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-pc-validation")
//                        .param("pcValidate", "checked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("success",
//                                hasItem("Platform certificate validation enabled"))));
//
//        policy = getDefaultPolicy();
//        Assert.assertTrue(policy.isPcValidationEnabled());
//
//        //reset database for invalid policy test
//        policy.setEcValidationEnabled(false);
//        policy.setPcValidationEnabled(false);
//        policy.setFirmwareValidationEnabled(false);
//        policyManager.updatePolicy(policy);
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-pc-validation")
//                        .param("pcValidate", "checked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("error",
//                                hasItem("Unable to change Platform Validation setting,"
//                                        + "  invalid policy configuration."))));
//
//        policy = getDefaultPolicy();
//        Assert.assertFalse(policy.isPcValidationEnabled());
//
//    }
//
//    /**
//     * Verifies the rest call for disabling the PC Validation policy setting.
//     * @throws Exception if test fails
//     */
//    @Test
//    public void testUpdatePcValDisable() throws Exception {
//
//        final String baseURL = "/" + POLICY.getViewName();
//        ResultActions actions;
//
//        //init the database
//        policy = getDefaultPolicy();
//        policy.setPcValidationEnabled(true);
//        policy.setPcAttributeValidationEnabled(false);
//        policy.setFirmwareValidationEnabled(false);
//        policyManager.updatePolicy(policy);
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-pc-validation")
//                        .param("pcValidate", "unchecked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("success",
//                                hasItem("Platform certificate validation disabled"))));
//
//        policy = getDefaultPolicy();
//        Assert.assertFalse(policy.isPcValidationEnabled());
//
//        //reset database for invalid policy test
//        policy.setPcAttributeValidationEnabled(true);
//        policy.setPcValidationEnabled(true);
//        policy.setFirmwareValidationEnabled(false);
//        policyManager.updatePolicy(policy);
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-pc-validation")
//                        .param("pcValidate", "unchecked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("error",
//                                hasItem("Unable to change Platform Validation setting,"
//                                        + "  invalid policy configuration."))));
//
//        policy = getDefaultPolicy();
//        Assert.assertTrue(policy.isPcValidationEnabled());
//
//    }
//
//    /**
//     * Verifies the rest call for enabling the PC attribute Validation policy setting.
//     *
//     * @throws Exception if test fails
//     */
//    @Test
//    public void testUpdatePcAttributeValEnable() throws Exception {
//
//        final String baseURL = "/" + POLICY.getViewName();
//        ResultActions actions;
//
//        //init the database
//        policy = getDefaultPolicy();
//        policy.setPcAttributeValidationEnabled(false);
//        policy.setPcValidationEnabled(true);
//        policy.setFirmwareValidationEnabled(false);
//        policyManager.updatePolicy(policy);
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-pc-attribute-validation")
//                        .param("pcAttributeValidate", "checked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("success",
//                                hasItem("Platform certificate attribute validation enabled"))));
//
//        policy = getDefaultPolicy();
//        Assert.assertTrue(policy.isPcAttributeValidationEnabled());
//
//        //reset database for invalid policy test
//        policy.setPcAttributeValidationEnabled(false);
//        policy.setPcValidationEnabled(false);
//        policyManager.updatePolicy(policy);
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-pc-attribute-validation")
//                        .param("pcAttributeValidate", "checked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("error",
//                                hasItem("To enable Platform Attribute Validation,"
//                                        + " Platform Credential Validation must also be enabled."))));
//
//        policy = getDefaultPolicy();
//        Assert.assertFalse(policy.isPcAttributeValidationEnabled());
//
//    }
//
//    /**
//     * Verifies the rest call for disabling the PC attribute validation policy setting.
//     * @throws Exception if test fails
//     */
//    @Test
//    public void testUpdatePcAttributeValDisable() throws Exception {
//
//        final String baseURL = "/" + POLICY.getViewName();
//        ResultActions actions;
//
//        // perform the mock request
//        actions = getMockMvc()
//                .perform(MockMvcRequestBuilders.post(baseURL + "/update-pc-attribute-validation")
//                        .param("pcAttributeValidate", "unchecked"));
//
//        actions
//                // check HTTP status
//                .andExpect(status().is3xxRedirection())
//                // check the messages forwarded to the redirected page
//                .andExpect(flash().attribute(PageController.MESSAGES_ATTRIBUTE,
//                        hasProperty("success",
//                                hasItem("Platform certificate attribute validation disabled"))));
//
//        policy = getDefaultPolicy();
//        Assert.assertFalse(policy.isPcAttributeValidationEnabled());
//    }
//
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
