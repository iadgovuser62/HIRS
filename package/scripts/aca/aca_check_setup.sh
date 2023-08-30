#!/bin/bash
############################################################################################
# Checks the setup for the ACA:
# 
############################################################################################

SCRIPT_DIR=$( dirname -- "$( readlink -f -- "$0"; )"; )
LOG_FILE=/dev/null
CERT_PATH="/etc/hirs/certificates/HIRS/"
RSA_PATH=rsa_3k_sha384_certs
ECC_PATH=ecc_512_sha384_certs

RSA_HIRS_ROOT="HIRS_root_ca_rsa_3k_sha384.pem"
RSA_HIRS_INTERMEDIATE="HIRS_intermediate_ca_rsa_3k_sha384.pem"
RSA_HIRS_CA1="HIRS_leaf_ca1_rsa_3k_sha384.pem"
RSA_HIRS_CA2="HIRS_leaf_ca2_rsa_3k_sha384.pem"
RSA_HIRS_CA3="HIRS_leaf_ca3_rsa_3k_sha384.pem"
RSA_TRUST_STORE="HIRS_rsa_3k_sha384_Cert_Chain.pem"
RSA_RIM_SIGNER="HIRS_rim_signer_rsa_3k_sha384.pem"
RSA_DB_CLIENT_CERT="HIRS_db_client_rsa_3k_sha384.pem"
RSA_DN_SRV_CERT="HIRS_db_srv_rsa_3k_sha384.pem"
RSA_WEB_TLS_CERT="HIRS_aca_tls_rsa_3k_sha384.pem"

ECC_HIRS_ROOT="HIRS_root_ca_ecc_512_sha384.pem"
ECC_HIRS_INTERMEDIATE="HIRS_intermediate_ca_ecc_512_sha384.pem"
ECC_HIRS_CA1="HIRS_leaf_ca1_ecc_512_sha384.pem"
ECC_HIRS_CA2="HIRS_leaf_ca2_ecc_512_sha384.pem"
ECC_HIRS_CA3="HIRS_leaf_ca3_ecc_512_sha384.pem"

ECC_TRUST_STORE="HIRS_ecc_512_sha384_Cert_Chain.pem"
ECC_RIM_SIGNER="HIRS_rim_signer_ecc_512_sha384.pem"
ECC_DB_CLIENT_CERT="HIRS_db_client_ecc_512_sha384.pem"
ECC_DN_SRV_CERT="HIRS_db_srv_ecc_512_sha384.pem"
ECC_WEB_TLS_CERT="HIRS_aca_tls_ecc_512_sha384.pem"

DB_SRV_CONF="/etc/my.cnf.d/mariadb-server.cnf"
DB_CLIENT_CONF="/etc/my.cnf.d/client.cnf"
ALL_CHECKS_PASSED=true
ALL_CERTS_PASSED=true

# Check for Admin privileges
if [ "$EUID" -ne 0 ]; then
      echo "This script requires root.  Please run as root"
      exit 1
fi
# Check install setup pki files
if [ ! -d $CERT_PATH ]; then
  echo "$CERT_PATH directory does not exist. Please run aca_setup.sh and try again."
     exit 1;
fi

# Argument handling 

while [[ $# -gt 0 ]]; do
  case $1 in
    -v|--verbose)
      ARG_VERBOSE=YES
      echo "verbose parameters"
      shift # past argument
      ;;
    -*|--*)
      echo "Unknown option $1"
      exit 1
      ;;
    *)
      echo "Unknown argument $1"
      exit 1
      shift # past argument
      ;;
  esac
done

source /etc/hirs/aca/aca.properties;

check_pwds () {

PRESENT=true
echo "Checking if ACA passwords are in aca.properties"
 if [ -z $hirs_pki_password ]; then
   echo "hirs pki password not set" 
   PRESENT=false 
 fi
 if [ -z $hirs_db_username ]; then
   echo "hirs db username not set"
   PRESENT=false 
 fi
 if [ -z $hirs_db_password ]; then
   echo "hirs db password not set"
   PRESENT=false  
 fi
 if [ $PRESENT ]; then
   echo "   HIRS passwords were created"
  else
   echo "   ERROR finding HIRS passwords"
   ALL_CHECKS_PASSED=false
 fi
}

check_mysql () {
  echo "Checking mysqld status..."
  if [[ $(pgrep -c -u mysql mysqld) -ne 0 ]]; then
      echo "   mysql process exists..."
    else
      echo "   mysqld process does NOT exist, attempting to restart mysql..."
    /usr/bin/mysqld_safe &
  fi

 # Wait for mysql to start before continuing.

  while ! mysqladmin ping -h "$localhost" --silent; do
     sleep 1;
  done

  echo "   mysqld is running."

  # Check DB server/client TLS setup.
  if [[ $(cat "$DB_SRV_CONF" | grep -c "ssl") < 1 ]]; then
     echo "   Mysql server ($DB_SRV_CONF) is NOT configured for Server Side TLS"
     ALL_CHECKS_PASSED=false
   else
     echo "   Mysql server ($DB_SRV_CONF) is configured for Server Side TLS"
  fi
  if [[ $(cat "$DB_CLIENT_CONF" | grep -c "ssl") < 1 ]]; then
      echo "   Mysql client ($DB_CLIENT_CONF)is NOT configured for command line use of TLS without provding key/cert ino the commandline"
      ALL_CHECKS_PASSED=false
    else
       echo "   Mysql client ($DB_CLIENT_CONF) is configured for command line use of TLS"
  fi
  
  if [ ! -z $mysql_admin_password ]; then
    if [ ! -z "${ARG_VERBOSE}" ]; then
      echo "Mysql status:"
      mysql -u root --password=$mysql_admin_password -e "STATUS;"
      echo "Listing mysql users:"
      mysql -u root --password=$mysql_admin_password -e "Select user from mysql.user;"
      echo "Listing all databses:"
      mysql -u root --password=$mysql_admin_password -e "show databases;"
    fi
  fi
}

check_cert () {
TRUST_STORE=$1
CERT=$2
RESULT=$(openssl verify -CAfile "$TRUST_STORE" $CERT)
if [ $? -ne 0 ]; then
   ALL_CHECKS_PASSED=false
   ALL_CERTS_PASSED=false
fi
if [ ! -z "${ARG_VERBOSE}" ]; then
    echo "     "$RESULT
fi
}

check_pki () {
 echo "Checking HIRS PKI certificates"
  if [ ! -d "/etc/hirs/certificates" ]; then
     echo "/etc/hirs/certificates doesn't exists, was aca_setup.sh run ? /
           Skipping PKI Checks." 
  fi

  pushd $CERT_PATH$RSA_PATH > /dev/null
   check_cert $RSA_TRUST_STORE $RSA_HIRS_ROOT
   check_cert $RSA_TRUST_STORE $RSA_HIRS_INTERMEDIATE
   check_cert $RSA_TRUST_STORE $RSA_HIRS_CA1
   check_cert $RSA_TRUST_STORE $RSA_HIRS_CA2
   check_cert $RSA_TRUST_STORE $RSA_HIRS_CA3
   check_cert $RSA_TRUST_STORE $RSA_RIM_SIGNER
   check_cert $RSA_TRUST_STORE $RSA_DN_SRV_CERT
   check_cert $RSA_TRUST_STORE $RSA_DB_CLIENT_CERT
   check_cert $RSA_TRUST_STORE $RSA_WEB_TLS_CERT

  popd  > /dev/null
  pushd $CERT_PATH$ECC_PATH > /dev/null
   check_cert $ECC_TRUST_STORE $ECC_HIRS_ROOT
   check_cert $ECC_TRUST_STORE $ECC_HIRS_INTERMEDIATE
   check_cert $ECC_TRUST_STORE $ECC_HIRS_CA1
   check_cert $ECC_TRUST_STORE $ECC_HIRS_CA2
   check_cert $ECC_TRUST_STORE $ECC_HIRS_CA3
   check_cert $ECC_TRUST_STORE $ECC_RIM_SIGNER
   check_cert $ECC_TRUST_STORE $ECC_DN_SRV_CERT
   check_cert $ECC_TRUST_STORE $ECC_DB_CLIENT_CERT
   check_cert $ECC_TRUST_STORE $ECC_WEB_TLS_CERT
  popd  > /dev/null

  if [ -z "${ARG_VERBOSE}" ]; then
    if [ $ALL_CERTS_PASSED == true ]; then
         echo "   All RSA and ECC certificates under $CERT_PATH are valid"
      else
         echo "   There were error in the certificates under $CERT_PATH"
    fi
       keytool -list -keystore  /etc/hirs/certificates/HIRS/TrustStore.jks  -storepass $hirs_pki_password | grep hirs | sed -e 's/^/     /' > /dev/null
    else  #verbose
       echo "   Checking KeyStore, Keystore aliases, and pki password"
       echo "   Keystore alias list:"
       keytool -list -keystore  /etc/hirs/certificates/HIRS/TrustStore.jks  -storepass $hirs_pki_password | grep hirs | sed -e 's/^/     /' 
  fi
  
  if [ $? -eq 0 ]; then
      echo "   JKS Trust Store File (/etc/hirs/certificates/HIRS/TrustStore.jks) is correct: HIRS pki password is correct"
    else
      echo "   Error with JKS Trust Store: HIRS pki password is NOT correct"
      ALL_CHECKS_PASSED=false
  fi
}

check_db () {
  echo "Check DB server TLS config..."
  RESULT=$(mysql -u root --password=$mysql_admin_password -e "SHOW VARIABLES LIKE '%have_ssl%'" |  grep -o YES )
  if [ "$RESULT" == "YES" ]; then
      echo "   Mysql Server side TLS is enabled:"
    else
      echo "   Mysql Sever side TLS is NOT enabled:"
      ALL_CHECKS_PASSED=false
  fi
  
  RESULT=$(mysqlshow --user=hirs_db --password=$hirs_db_password hirs_db|  grep -o hirs_db)
  if [ "$RESULT" == "hirs_db" ]; then
      echo "   The hirs_db database is visable by the hirs_db user"
    else
      echo "   The hirs_db database is NOT visable by the hirs_db user"
      ALL_CHECKS_PASSED=false
  fi
   if [ ! -z "${ARG_VERBOSE}" ]; then
   echo "   Show hirs_db user config using hirs_db password"
   mysql -u hirs_db --password=$hirs_db_password -e "SHOW CREATE USER 'hirs_db'@'localhost';" \
    --ssl-ca=/etc/hirs/certificates/HIRS/rsa_3k_sha384_certs/HIRS_rsa_3k_sha384_Cert_Chain.pem \
    --ssl-cert=/etc/hirs/certificates/HIRS/rsa_3k_sha384_certs/HIRS_db_client_rsa_3k_sha384.pem \
    --ssl-key=/etc/hirs/certificates/HIRS/rsa_3k_sha384_certs/HIRS_db_client_rsa_3k_sha384.key
    echo "Mysql TLS configuration"
    mysql -u root --password=$mysql_admin_password -e "SHOW VARIABLES LIKE '%ssl%'"
    echo "hirs_db user database access:"
    mysql -u hirs_db --password=$hirs_db_password -e "SHOW DATABASES;";
    echo "Privileges for the hirs_db user:"
    mysql -u hirs_db --password=$hirs_db_password -e "SHOW GRANTS FOR 'hirs_db'@'localhost'" 
  fi
  
}

# Check selinux status and files that require specific contexts
check_selinux () {
   SELINUXSTATUS=$(getenforce)
   DB_SRV_CONTEXT=$(ls -Z $DB_SRV_CONF)
   DB_CLIENT_CONTEXT=$(ls -Z $DB_CLIENT_CONF)
   
   if [[ "$SELINUXSTATUS" == *"Enforcing"* ]]; then
     if [[ "$DB_SRV_CONTEXT" == *"mysqld_etc_t"* &&  "$DB_CLIENT_CONTEXT" == *"mysqld_etc_t"* ]]; then
         echo "Selinux status is $SELINUXSTATUS and both $DB_SRV_CONF and $DB_SRV_CONF contexts are correct"
     elif [[ "$DB_CLIENT_CONTEXT" == *"mysqld_etc_t"* ]]; then
        echo "Selinux status is $SELINUXSTATUS and $DB_SRV_CONF context is incorrect: $DB_SRV_CONTEXT"
        ALL_CHECKS_PASSED=false
     else
        echo "Selinux status is $SELINUXSTATUS and $DB_CLIENT_CONF context is incorrect: $DB_CLIENT_CONTEXT"
        ALL_CHECKS_PASSED=false
     fi
   fi
}

check_pwds
check_pki
check_mysql
check_db
check_selinux

if [ $ALL_CHECKS_PASSED = true  ]; then
  echo "ACA setup checks passed!"
else
  echo "ACA setup checks failed."
fi