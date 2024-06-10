#!/bin/bash

# Accepting auth values from Environment variables.
SERVICES_USER=${SERVICES_USER}
SERVICES_PASS=${SERVICES_PASS}
START=${START}
NUMBER_OF_OFFENSES=0

#Reading arguments
list_customers="n"
while getopts ":c:o:l:" opt
do
  case "$opt" in
    c ) customer_name="$OPTARG" ;; # alias of the customer. Set in the configuration file
    l ) list_customers=${OPTARG:="n"};;
  esac
  # In case the parameter l is passed without specific customer we'll list all
  if [ "$OPTARG" == "l" ]; then
    list_customers="all"
  fi
done

function helpFunction() {
  clear
  echo ""
  echo "Usage: SERVICES_USER=myUser SERVICES_PASS=myAwesomePassword START=10 $0 -c"
  echo -e "\t Script designed to send two curl requests to qradar_offense_ms," \
  "first request will fetch a list of ids and the second request will fetch"   \
  "the entire offenses based on the list of ids collected. Each five seconds"  \
  "the test will run again increasing the number of offenses fetched by one, " \
  "until the request times out. PS: If no START value is passed, the script"   \
  "will default to 10 offenses."
  echo ""
  echo -e "\t NOTE: All Customer's information are stored in a JSON config"    \
  "file 'qradar_offense_config.json'. Make sure you are passing a valid alias,"\
  "and if it doesn't exist, create a new alias and set of data for the"        \
  "customer you are trying to test, following the same structure found in the" \
  "config file. DO NOT KEEP CUSTOMER'S PRIVATE INFO IN SHARED REPOSITORIES."
  echo ""
  echo -e "\tSERVICES_USER => Your services basic auth user"
  echo -e "\tSERVICES_PASS => Your services basic auth password"
  echo -e "\tSTART => Starting number of offenses. No value provided and the"  \
  "script will default to 10."
  echo -e "\t-c => Alias of the customer that the test will run against."
  echo -e "\t-l => OPTIONAL ARG: -l to list all aliases/customer's full name" \
  "or -l <customer alias> to list all info from that Customer."
  echo ""
  exit 1 # Exit script after printing help
}

function checkInput() {
  # Checking neither of the mandatory parameters are empty.
  if [ -z "$customer_name" ]; then
    helpFunction
  fi

  # Checking the customer provided exists.
  line_count=$(cat qradar_offense_config.json | jq -r --arg keyvar "$customer_name" '.[$keyvar]' | wc -l)
  # Checking the grep was successful and the test exists
  if [ $line_count -eq 1 ]; then
    helpFunction
  fi
}

function displayCustomers() {
  if [ "$list_customers" != "n" ]; then
    echo "---------------------------------"

    if [ "$list_customers" == "all" ]; then # In case parameter l is passed without any customer
      echo "List of all Customers in the config file (alias / full name)"
      cat qradar_offense_config.json | jq -r -M 'with_entries(.value |= .customerFullName)'
      exit
    fi

    # Checking the customer provided exists.
    line_count=$(cat qradar_offense_config.json | jq -r -M --arg keyvar "$list_customers" '.[$keyvar]' | wc -l)
    if [ $line_count -ne 1 ]; then
      echo "Showing details of Customer $list_customers:"
      cat qradar_offense_config.json | jq -r -M --arg keyvar "$list_customers" '.[$keyvar]'
    else
      clear
      echo "The customer alias provided does not exist. See below the full list"\
      "customers and their respective aliases, and if you don't find the"       \
      "customer, create its set of data in the json file config."
    fi
    echo "---------------------------------"
    exit
  fi
}

function displayTestInfo() {
  customer_full_name=`cat qradar_offense_config.json | jq -r --arg keyvar "$customer_name" '.[$keyvar].customerFullName'`
  qradar_host=`cat qradar_offense_config.json | jq -r --arg keyvar "$customer_name" '.[$keyvar].qradarHost'`
  api_version=`cat qradar_offense_config.json | jq -r --arg keyvar "$customer_name" '.[$keyvar].apiVersion'`
  qradar_authentication_token=`cat qradar_offense_config.json | jq -r --arg keyvar "$customer_name" '.[$keyvar].QradarAuthenticationToken'`

  echo "Starting tests..."
  echo "Executing tests for customer -> $customer_full_name"
  echo "Qradar Host: $qradar_host"
  echo "API Version: $api_version"
  echo "Qradar Authentication Token: $qradar_authentication_token"
}

# need to pass true or false as second parameter when calling this function
# true means the list of ids need to be printed and false it does not
function getCurlStatus() {
    local res=$1
    local body=${res::${#res}-3}
    local status=$(echo $res | tail -c 4)
    NUMBER_OF_OFFENSES=$(echo $body | jq -r '.[].id' | wc -l | sed -e 's/^[ \t]*//')
    echo "-----------------------------------------------------"
    echo "RESPONSE CODE: $status"

    if [ "$status" -ne "200" ]; then
      echo "There has been an issue with the request and a code other" \
      "than 200 returned, causing the test to stop. See details below:"
      echo ""
      echo $body
      echo ""
      echo ""
      echo "Timeout point was found trying to retrieve $START offenses"
      echo ""
      exit 1
    fi

    # It is needed to parse the output only for first request.
    if [ $2 = true ]; then
      list_of_collected_ids=$(echo $body | jq -r '.[].id' | awk 'NR > 1 { printf(",") } {printf "%s",$0}')
      echo "List of IDs collected during the first request:"
      echo $list_of_collected_ids
      echo "-----------------------------------------------------"
    else
      local timestamp=$(date +"%s") #Get the current timestamp
      if (( $START > 1)); then
        echo $body > "/tmp/response-body-$customer_name-$limit-offenses-$timestamp"
        echo "Response body was saved to /tmp/response-body-$customer_name-$limit-offenses-$timestamp"
      else
        echo $body > "/tmp/response-body-$customer_name-$limit-offense-$timestamp"
        echo "Response body was saved to /tmp/response-body-$customer_name-$limit-offense-$timestamp"
      fi
      echo "-----------------------------------------------------"
    fi
}

# Testing the authenticantion variables are set.
if [[ -z "$SERVICES_USER" || -z "$SERVICES_PASS" ]]; then
  helpFunction
  echo "SERVICES_USER and SERVICES_PASS environment variables are not set!"
fi

clear

if [ -z "$START" ]; then
  echo "Start value was not informed. Defaulting it to 10..."
  START=10
elif ! [[ $START =~ ^[0-9]+$ ]] ; then
  echo "Start value must be an integer. Defaulting it to 10"
  START=10
else
  START=${START}
fi

displayCustomers # Print customer config info if -l is passed.
checkInput #Check input is correct to run the test.
displayTestInfo #Displays info from the test currently running.

while [ $? -eq 0 ]
do
  echo ""
  echo "Running tests for $START offenses:"
  echo ""
  echo "First Request: Collecting a list of offenses IDs:"
  response_first_request=$(curl -s -w "%{http_code}" -X GET "https://services.sec.ibm.com/micro/qradar_offense?qradarHost=${qradar_host}&apiVersion=${api_version}&fields=id&start=0&limit=${START}" -H "QradarAuthenticationToken: ${qradar_authentication_token}" -H 'uuidMars: test01' -u "$SERVICES_USER:$SERVICES_PASS")
  getCurlStatus "$response_first_request" true

  if (( $NUMBER_OF_OFFENSES < $START)); then
    echo ""
    echo ""
    echo "The customer: $customer_full_name only have $NUMBER_OF_OFFENSES offenses"\
    "and up to this point all requests worked. It was not possible to find a"      \
    "timeout point for it."
    echo ""
    exit
  fi

  echo "Second Request: Fetch entire offenses based on the ids previously collected:"
  response_second_request=$(curl -s -w "%{http_code}" -X GET "https://services.sec.ibm.com/micro/qradar_offense?qradarHost=${qradar_host}&apiVersion=${api_version}&ids=${list_of_collected_ids}" -H "QradarAuthenticationToken: ${qradar_authentication_token}" -H 'uuidMars: test01' -u "$SERVICES_USER:$SERVICES_PASS")
  getCurlStatus "$response_second_request" false

  START=$[${START}+1]
  sleep 5
done
