#!/usr/bin/env bash

# Checking the necessary environment variables are set before the tests start
# tests/environment_variables_helper.sh
# if [ $? -ne 0 ]
# then
#   exit 1
# fi

TIMESTAMP=$(date +"%Y_%m_%d_%H_%M_%S") #Get the current timestamp
SUITE_FOLDER="gatling_suite_${TIMESTAMP}" #Generates a unique folder to store all logs

#Sub-folder of results to hold all jsession values for each simulation
JSESSION_SUITE_FOLDER="${PWD}/tests/_results/jsession_${TIMESTAMP}"
mkdir -p "${JSESSION_SUITE_FOLDER}"
export JSESSION_SUITE_FOLDER=$JSESSION_SUITE_FOLDER #Exporting the location so Gatling can read it.

#This help function displays how to use the script.
helpFunction()
{
  echo ""
  echo "Usage: $0 -e -t -d -l"
  echo -e "\t NOTE: You must pass at least a single test to be executed. If you wish to execute all tests, use '-t full'"
  echo -e "\t When passing multiple values for either tests or projects, these values must be passed between double quotes."
  echo -e "\t-e => Environment in which the tests will run. Accepted parameters are: DEV | STG | PRD | EU | RUH"
  echo -e "\t-i => OCP Server in which the tests will run. Accepted parameters are: A | B"
  echo -e "\t-t => Name of the tests to be executed. Check 'tests_to_run.yaml' file to see all tests"
  echo -e "\t-R => Especify vault role ID"
  echo -e "\t-S => Especify vault secret ID"
  echo -e "\t-s => Optional ARG: Used with -t allows to pass suite name to be executed (e.g. execute all tests present in tests_to_run_glass.yaml file)"
  echo -e "\t-d => Optional ARG: Allows to pass a value to be used as a limit inside one or more simulations."
  echo -e "\t-l => Optional ARG: Allows to pass a load value for performance testing."
  echo -e "===================================================================================================="
  echo -e ""
  echo -e "List of projects and tests ready to be executed:"
  echo -e ""
  echo -e "===================================================================================================="
  cat tests/tests_to_run.yaml | awk '$1 ~ /^[^#]/'
  exit 1 # Exit script after printing help
}

setVariables(){
  ruby ./tests/vault.rb "$arrINvr" "$arrINvs" "$environment" "$ocp_deployment" 
  source "./variables"
} 

removeCredentialsVariablesFile(){
  rm -f ./variables
}
validateVaultRoleAndSecretProvided(){  
 
  #Verify that r_id has been inormed
  if [ -z "$r_id" ]
  then
    echo "You must provide vault role id. Use the flag -h to see all tests available."
  helpFunction
  fi
  
  #Verify that s_id has been inormed
  if [ -z "$s_id" ]
  then
    echo "You must provide vault secret id. Use the flag -h to see all tests available."
  helpFunction
  fi
  }
  
exportTestsToRun(){  
  rm -f tests/tests_to_run # Removing 'tests to run' file from any previous execution.

  # Verify yq is available
  yq --version > /dev/null 2>&1
  if [ $? -ne 0 ]
  then
    clear
    echo -e "YQ must be installed to run the tests. Instructions can be found here -> https://github.com/mikefarah/yq"
    exit 1
  fi

  # Verify jq is available
  jq --version > /dev/null 2>&1
  if [ $? -ne 0 ]
  then
    clear
    echo -e "JQ must be installed to run the tests. Instructions can be found here -> https://stedolan.github.io/jq/download/"
    exit 1
  fi
  
  # Verify jsonpath is available
  gem list jsonpath | grep -q 'jsonpath' > /dev/null 2>&1
  if [ $? -ne 0 ];then
    echo $?
    clear
    echo -e "jsonpath not installed. Please use 'gem install jsonpath -v 0.5.8' to install it"
    exit 1
  fi
  
  #Verify that at least a test has been informed
  if [ -z "$selected_test" ]
  then
    echo "You must provide a test to run. Use the flag -h to see all tests available."
  fi
  
  if [[ "$selected_test" == *"full"* ]]; then
    cat tests/tests_to_run$SUITE.yaml | sed s/-//g | sed s/' '//g | sed '/tests:/d' > tests/tests_to_run #if -s flag is passed it will join the suite name to the string, if not... the file will be tests_to_run.yaml
  else
    for test in ${selected_test};do
      cat tests/tests_to_run.yaml | grep -w $test > /dev/null 2>&1

  if [ $? -eq 0 ]; then # Checking the grep was successful and the test exists
        cat tests/tests_to_run.yaml | grep -w $test | sed s/-//g | sed s/' '//g >> tests/tests_to_run
      else
        echo "The test specified to run does not exist ($test), please refer to 'tests_to_run.yaml' or use the flag -h to see the list of available tests."
        exit 1
      fi
    done
  fi
}

validateEnvValuesExist(){
  #Verifying the GATLING_HOME variable is set
  if [ -z "$GATLING_HOME" ]
  then
    echo "The GATLING_HOME environment variable must be set."
    exit 1 #Exiting the script
  fi

  # Verifying there is an environment and test suite values provided.
  if [ -z "$environment" ]
  then
    echo "The environment in which the tests will be executed needs to be provided.";
    helpFunction
  #Verifying the value provided matches with one of our accepted list.
  elif [ "$environment" != "ANZ" -a "$environment" != "US-SOC" -a "$environment" != "DEV" -a "$environment" != "STG" -a "$environment" != "PRD" -a "$environment" != "EU" -a "$environment" != "RUH" ]
  then
    echo "The environment value provided is not in the accepted list of parameters"
    helpFunction
  fi
  export ENV=$environment  #Setting the environment where tests are going to run
}

validateOCPServerValues(){
 
  #Verifying the OCP Server value provided matches with one of our accepted list.
  if [ "$ocp_deployment" != "A" -a "$ocp_deployment" != "B" -a "$ocp_deployment" != "" ]
  then
    echo "The OCP Server value provided is not in the accepted list of parameters"
    helpFunction
  fi

}

validateOptionalArgs(){
  # Optional arg: gives the ability to pass a limit as a parameter for requests
  if [ ! -z "$delimiter" ]
  then
    export LIMIT=$delimiter
  fi

  # Optional arg: gives the ability to set the load of the test on the fly.
  if [ ! -z "$load" ]
  then
    export LOAD=$load # Exporting the load parameter
  fi
  
  # Optional arg: gives the ability to set the suite to be run instead of the full suite (e.g glass, etc).
  if [ ! -z "$suite" ]
  then
    export SUITE=_$suite # Exporting the suite parameter
  fi
}

exportVaultSecrets(){
  if [ -z "$VAULT_TOKEN" ]
  then
    clear
    echo "A vault token must be informed. Log into vault and then run the test again providing a VAULT_TOKEN env variable."
    exit 1
  fi

  #TODO: update this block to read the data from vault a single time and > to a .json file.
  # From this json file read the values into each variable.
  # Delete the temp json file once it is done. 
  echo "Fetching secrets from Vault..."
  export VAULT_TOKEN=$VAULT_TOKEN
  export VAULT_ADDR=https://vault.sec.ibm.com:8200
  export ADMIN_USER=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.admin)
  export PASS_ADMIN=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.passAdmin)
  export W3_USER=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.email)
  export W3_PASS=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.passW3)
  export TEST_USER=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.testUser)
  export TEST_PASS=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.testPass)
  export AUTHORIZATION_TOKEN_USER=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.authorizationTokenUser)
  export AUTHORIZATION_TOKEN_DEV=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.authorizationTokenPassDEV)
  export AUTHORIZATION_TOKEN_STG=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.authorizationTokenPassSTG)
  export AUTHORIZATION_TOKEN_PRD=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.authorizationTokenPassPRD)
  export SNIP_KEY=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/data/modules/qa_automation/api-automation | jq -r .data.data.snipKey)
}

#Reading the parameters
while getopts "e:t:d:l:s:R:S:i:" opt
do
  case "$opt" in
    e ) environment="$OPTARG" ;; # Environment tests will run (stg, prd, etc)
    t ) selected_test="$OPTARG" ;; # Name of the tests to run
    d ) delimiter="$OPTARG" ;; # Used to set a limit of data inside some tests (can be omitted)
    l ) load="$OPTARG" ;; #Used when you want to specify a value to do stress testing.
	s ) suite="$OPTARG" ;; #Used when you want to specify specific suite to be full run (e.g glass, etc)
	R ) r_id="$OPTARG" ;; #Used to provide role (mandatory)
	S ) s_id="$OPTARG" ;; #Used to provide secret(mandatory)
	i ) ocp_deployment="$OPTARG" ;; #Used to provide ocp deployment server (A or B)
    ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
  esac
done

validateEnvValuesExist #validate GATLING_HOME and environment to execute were passed.
validateVaultRoleAndSecretProvided #validate r_id and s_id were passed.
validateOptionalArgs # Get optional args
validateOCPServerValues
arrINvrentryb=(${r_id//-/ })
arrINvsentryb=(${s_id//-/ })
entry=($(cat ${PWD}/tests/lorces.rb))
arrINvc=(${entry//-/ })
arrINvr=${arrINvc[0]}-${arrINvrentryb[1]}-${arrINvrentryb[2]}-${arrINvrentryb[3]}-${arrINvrentryb[4]}
arrINvs=${arrINvc[1]}-${arrINvsentryb[1]}-${arrINvsentryb[2]}-${arrINvsentryb[3]}-${arrINvsentryb[4]}
setVariables #setting environment variables with secrets grabbed from vault
removeCredentialsVariablesFile #deleting the temporary variables file
clear #Clears anything from the console.
SECONDS=0 #Used to calculate the time it took for all tests to run.
exportTestsToRun #Exporting all tests for each different project
export JAVA_OPTS="-Dgatling.ahc.pooledConnectionIdleTimeout=1800000 -Dgatling.ahc.requestTimeout=180000 -Dgatling.charting.indicators.lowerBound=2000 -Dgatling.charting.indicators.higherBound=4000"
# exportVaultSecrets # Fetch the secrets from vault into a json file.
set_of_tests=($(cat ${PWD}/tests/tests_to_run)) #Reading the tests_to_run into an array
for test in "${set_of_tests[@]}" #Executing the tests
do
  # Execute each simulation
  echo "Executing simulation ${test}, please wait ..."
  #$GATLING_HOME/bin/gatling.sh -sf $PWD -s $test -rf $PWD/tests/_results/${SUITE_FOLDER} -bf $PWD/bin > "${PWD}"/tests/_results/gatling.log
  mvn gatling:test "-Dmaven.plugin.validation=NONE" "-DsimulationsFolder=${PWD}/tests" "-DclassName=${test}" "-DresultsFolder=${PWD}/tests/_results/${SUITE_FOLDER}" > "${PWD}"/tests/_results/gatling.log
  mkdir -p $PWD/tests/_results/_full_log_trace_${TIMESTAMP} # Creates a folder to hold logs

  # Each simulation will have its own full log.
  #mv $PWD/tests/_results/gatling.log $PWD/tests/_results/_full_log_trace_${TIMESTAMP}/${test}.log
  #sed -i '' '/^authorization:/d' $PWD/tests/_results/_full_log_trace_${TIMESTAMP}/${test}.log > /dev/null 2>&1
  if [[ "$test" == "AuthenticationMs" ]]; then
    sed '/byteArraysBody/d' $PWD/tests/_results/gatling.log | sed '/x-secibm-jwt/d' > $PWD/tests/_results/_full_log_trace_${TIMESTAMP}/"${test}".log
	rm -f $PWD/tests/_results/gatling.log
  else
    sed '/Basic/d' $PWD/tests/_results/gatling.log | sed '/x-secibm-jwt/d' > $PWD/tests/_results/_full_log_trace_${TIMESTAMP}/"${test}".log
    #cp $PWD/tests/_results/gatling.log $PWD/tests/_results/_full_log_trace_${TIMESTAMP}/"${test}".log
    rm -f $PWD/tests/_results/gatling.log
  fi
done

echo "==============================================="
echo ""
if (( $SECONDS > 60 )) ; then
    let "minutes=(SECONDS%3600)/60"
    let "seconds=(SECONDS%3600)%60"
    echo "All tests completed in $minutes minute(s) and $seconds second(s)"
else
    echo "All tests completed in $SECONDS seconds"
fi

echo ""
echo "==============================================="
echo "Generating API Suite reports..."
echo ""

JSESSION=$JSESSION  # Whether we should generate jsession or not
ruby tests/generate_report.rb ${SUITE_FOLDER} ${JSESSION_SUITE_FOLDER} ${environment} ${JSESSION}
status=$(echo $?)

# Cleaning up unnecessary log files and consolidating everything in one folder.
# cat $PWD/tests/_results/${SUITE_FOLDER}/results.json
cat $PWD/tests/_results/${SUITE_FOLDER}/failed.json > /dev/null 2>&1
mkdir -p $PWD/tests/_results/${SUITE_FOLDER}/_full_log_trace_${TIMESTAMP}
mv $PWD/tests/_results/_full_log_trace_${TIMESTAMP}/* $PWD/tests/_results/${SUITE_FOLDER}/_full_log_trace_${TIMESTAMP}
rm -R $PWD/tests/_results/_full_log_trace_${TIMESTAMP}
rm -R $JSESSION_SUITE_FOLDER
rm -f $PWD/tests/tests_to_run
rm -rf $PWD/tests/_results/jsession_*?
exit $status
