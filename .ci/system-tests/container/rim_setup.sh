#!/bin/bash
#########################################################################################
#   Setup for PC Client Reference Integrity Manifest (RIM) tests
#   usage rim_setup.sh <profile> <test> <option>
#   use "clear" option to clear existing TPM PCR values
#########################################################################################

profile=$1
test=$2
options=$3
tcgDir="/boot/tcg"
propFile="/etc/hirs/tcg_boot.properties";
profileDir="/HIRS/.ci/system-tests/profiles/$profile"
defaultDir="$profileDir/default"
testDir="/HIRS/.ci/system-tests/profiles/$profile/$test"
eventLog="$testDir"/"$profile"_"$test"_binary_bios_measurements
swidDir="$testDir/swidtags"
rimDir="$testDir/rims"
pcrScript="$testDir/"$profile"_"$test"_setpcrs.sh"

source /HIRS/.ci/setup/container/tpm2_common.sh

echo "Test is using RIM files from $profile : $test"

# Make sure TCG defined RIM folders exist and are cleared out
mkdir -p $tcgDir/manifest/rim/;  # Create the platform cert folder if its not there
rm -f $tcgDir/manifest/rim/*;    # clear out any previous data

mkdir -p $tcgDir/manifest/swidtag/;  # Create the platform cert folder if its not there
rm -f $tcgDir/manifest/swidtag/*;   # clear out any previous data

# Step 1: Update the tcg_boot.properties to use test specific binary_bios_measurement file
#      a: if file does not exist in the test folder then use the default measurement file
#      b: change the property file to point to the the test file

if [[ ! -f "$eventLog" ]]; then
    eventLog="$defaultDir"/"$profile"_default_binary_bios_measurements
fi    
sed -i "s:tcg.event.file=.*:tcg.event.file=$eventLog:g" "$propFile"
#echo "eventLog used was  $eventLog"

# Step 2: Copy Base RIM files to the TCG folder
#      a: See if test specific swidtag folder exists, if not use the defualt folder
if [[ ! -d $swidDir ]]; then
    swidDir=$defaultDir/swidtags;
fi
pushd $swidDir > /dev/null
  if [[ ! -f ".gitignore" ]]; then
    for swidtag in * ; do
          cp -f $swidtag $tcgDir/manifest/swidtag/$swidtag;
    done
  fi
popd > /dev/null
# Step 3: Copy Support RIM files to the TCG folder in the same mannor
if [[ ! -d $rimDir ]]; then 
    rimDir=$defaultDir/rims;
fi
pushd $rimDir > /dev/null

  if [[ ! -f ".gitignore" ]]; then
    for rim in * ; do
          cp -f $rim $tcgDir/manifest/rim/$rim;
    done
  fi
popd > /dev/null

#  echo "Contents of tcg swidtag folder $tcgDir/manifest/swidtag/ : $(ls $tcgDir/manifest/swidtag/)"
#  echo "Contents of tcg rim folder tcgDir/manifest/rim/: $(ls $tcgDir/manifest/rim/)"

#Step 4, run the setpcr script to make the TPM emulator hold values that correspond the binary_bios_measurement file
#     a: Clear the TPM PCR registers vi a call to the tss clear 
#     b: Check if a test specific setpcr.sh file exists. If not use the profiles default script

if [[ $options == "clear" ]]; then
   resetTpm2Emulator
fi

if [[ ! -f $pcrScript ]]; then
    pcrScript="$profileDir/default/"$profile"_default_setpcrs.sh"
fi
sh $pcrScript;
#echo "PCR script was $pcrScript"
#tpm2_pcrlist -g sha256 

# Done with rim_setup