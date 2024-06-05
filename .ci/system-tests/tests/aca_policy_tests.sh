#!/bin/bash
#########################################################################################
#    HIRS ACA Policy System Tests
#
#########################################################################################
source ./.ci/system-tests/sys_test_common.sh

testResult=false
totalTests=0;
failedTests=0;

# Start ACA Policy Tests
# provisionTpm2 takes 1 parameter (the expected result): "pass" or "fail"

case $1 in
    1)
        writeToLogs "### ACA POLICY TEST 1: Test ACA default policy  ###"
        setPlatformCerts "laptop" "empty"
        provisionTpm2 "pass"
        ;;
    2)
        writeToLogs "### ACA POLICY TEST 2: Test EK cert Only Validation Policy without a EK Issuer Cert in the trust store ###"
        setPolicyEkOnly
        provisionTpm2 "fail"
        ;;
    3)
        writeToLogs "### ACA POLICY TEST 3: Test EK Only Validation Policy ###"
        uploadTrustedCerts
        provisionTpm2 "pass"
        ;;
    4)
        writeToLogs "### ACA POLICY TEST 4: Test PC Validation Policy with no PC ###"
        setPolicyEkPc_noAttCheck
        provisionTpm2 "fail"
        ;;
    5)
        writeToLogs "### ACA POLICY TEST 5: Test FW and PC Validation Policy with no PC ###"
        setPolicyEkPcFw
        provisionTpm2 "fail"
        ;;
    6)
        writeToLogs "### ACA POLICY TEST 6: Test PC Validation Policy with valid PC with no Attribute Check ###"
        clearAcaDb
        setPolicyEkPc_noAttCheck
        uploadTrustedCerts
        setPlatformCerts "laptop" "default"
        provisionTpm2 "pass"
        ;;
    7)
        writeToLogs "### ACA POLICY TEST 7: Test PC Validation Policy with valid PC with Attribute Check ###"
        clearAcaDb
        setPolicyEkPc
        uploadTrustedCerts
        setPlatformCerts "laptop" "default"
        setPlatformOutput
        provisionTpm2 "pass"
        ;;
    8)
        writeToLogs "### ACA POLICY TEST 8: Test PC with RIM Validation Policy with valid PC and RIM ###"
        clearAcaDb
        setPolicyEkPcFw
        uploadTrustedCerts
        setPlatformCerts "laptop" "default"
        setRims "laptop" "default"
        provisionTpm2 "pass"
        ;;
esac
#  Process Test Results, any single failure will send back a failed result.
if [[ $failedTests != 0 ]]; then
    export TEST_STATUS=1;
    echo "****  $failedTests out of $totalTests ACA Policy Tests Failed! ****"
  else
    echo "****  $totalTests ACA Policy Tests Passed! ****"
fi
