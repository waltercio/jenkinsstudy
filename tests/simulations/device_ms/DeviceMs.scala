import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._
import io.gatling.core.session.Session
import java.text.SimpleDateFormat
import java.util.Calendar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.sql.Timestamp
import io.gatling.core.structure.ChainBuilder

/** Developed by: cgobbi@br.ibm.com
 *  Based on: https://jira.sec.ibm.com/browse/QX-5249
 *  Updated based on: https://jira.sec.ibm.com/browse/XPS-161696
 *  Updated based on: https://jira.sec.ibm.com/browse/XPS-162432
 *  Created scenario 87 based https://jira.sec.ibm.com/browse/XPS-168898
 */

/**For partner Level request (req8 you need to set PARTNERLEVELUSER as "imitintegrationtest"
 * and PARTNERLEVELPASSWORD = <Password> (need to check with Caio, Waltercio or Daniel Kraai)
 */

 object deviceMsVariables extends BaseTest{
  
  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  val endpoint = "micro/device/"
  val vendorEndpoint = "micro/device/vendor"

 //  Name of each request
   val req01 = "Query for all devices using qatest"
   val req02 = "Query for a specific device based on id"
   val req03 = "Query for devices based on hostName"
   val req04 = "Fetching all devices based on a Customer ID"
   val req05 = "Query for multiple devices based on IDs"
   val req06 =  "Negative - Permission denied to access another's customer device data"
   val req07 =  "Query for devices using admin account with limit=5"
   val req08 =  "Fetching devices for User under Partner Level (imitintegrationtest user - applicable only for STG and ATL)"
   val req09 =  "Create new device"
   val req10 =  "Get and check brand new created device data"
   val req11 =  "PATCH: Update new device using AD Credentials"
   val req12 =  "PUT: Deactivate the device"
   val req13 =  "GET:Get and check brand new update device data"
   val req14 =  "GET:Return all fields"
   val req15 =  "GET:Fetch records based on single exclude param"
   val req16 =  "GET:Fetch records based on multiple exclude param"
   val req17 =  "GET:Check total count return by device ms"
   val req18 =  "GET:Check total count for given device id"
   val req19 =  "GET:Filter records with managementCollector"
   val req20 =  "GET:Fetch all devices with AD credentials"
   val req21 =  "GET: device data only for imitintegrationtest creds"  
   val req22 =  "PUT: Negative - Verify customer contact can not update not permitted fields"
   val req23 =  "PUT: Update new device with permitted fields for customer contact"
   val req24 =  "GET: Verify that all customer contact permitted fields has been updated successfully"
   val req25 =  "PUT: Update new device customerHostName field back to original value"
   val req26 =  "GET: Verify that customer contact permitted fields have been updated back to original value successfully"  
   val req27 =  "Check qatest user should not be able to modify records of imitintegrationtest"
   val req28 =  "Check imitintegrationtest user should not be able to modify records of qatest"
   val req29 =  "GET jwt token to use in GET call using token"
   val req30 =  "Get response using jwt-token"
   val req31 =  "Get All with TotalCount=true"
   val req32 =  "Get using ids parameter with TotalCount=true"
   val req33 =  "Negative Get All with TotalCount=false"
   val req34 =  "PUT : Update clusterConfiguration to Master"
   val req35 =  "GET: Check Update clusterConfiguration to Master"
   val req36 =  "PUT : Update clusterConfiguration to Child and masterDevice and masterDeviceId values"
   val req37 =  "GET: Check Update clusterConfiguration to Child and masterDevice and masterDeviceId values"
   val req38 =  "POST: Validate the response of POST endpoint to retrieve device data by passing valid payload data"
   val req39 =  "POST: Validate the response of POST endpoint to retrieve device data using totalCount"
   val req40 =  "POST: Validate the response of POST endpoint to retrieve device data using totalCount and start and limit"
   val req41 =  "POST: Validate the response of POST endpoint to retrieve device data by passing empty payload data"
   val req42 =  "POST: Validate the response of POST endpoint to retrieve device data by passing Invalid payload data"
   val req43 =  "POST:: Validate the response of POST endpoint to retrieve device data by passing valid payload data with AD credentials"
   val req44 =  "GET - Save total count for all customer contact devices"
   val req45 =  "GET - Match total count for all customer contact devices with totalCount when limit parameter is defined"
   val req46 =  "Negative - POST - Try to create a new device using existing hostname in servicenow"
   val req47 =  "GET - Verify existing device values have not been updated"
   val req48 =  "GET - Query using multiple filters"
   val req49 =  "GET jwt token using email contact to use in GET call using token"
   val req50 =  "Negative - Permission denied to access another's customer device data using email contact token"
   val req51 =  "PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DDThh:mm:ssZ"
   val req52 =  "GET: Verify lastAutodetected updated using ISO format YYYY-MM-DDThh:mm:ssZ"
   val req53 =  "PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DDThh:mm:ss.sssZ"
   val req54 =  "GET: Verify lastAutodetected updated using ISO format YYYY-MM-DDThh:mm:ss.sssZ"
   val req55 =  "PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DDThh:mmZ"
   val req56 =  "GET: Verify lastAutodetected updated using ISO format YYYY-MM-DDThh:mmZ"
   val req57 =  "PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DDThhZ"
   val req58 =  "GET: Verify lastAutodetected updated using ISO format YYYY-MM-DDThhZ"
   val req59 =  "PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DD"
   val req60 =  "GET: Verify lastAutodetected updated using ISO format YYYY-MM-DD"
   val req61 =  "Negative: POST: Create new device with no customerId in the payload using admin credentials"
   val req62 =  "POST: Create new device with no customerId in the payload using contact credentials"
   val req63 =  "GET: Verify ticket has been created when using empty customerId and contact credentials"
   val req64 =  "Negative: PATCH: Update ticket with no customerId in the payload using contact credendials"
   val req65 =  "GET : Verify ticket has not been updated when using empty customerId and contact credentials"
   val req66 =  "Negative: PATCH: Update ticket with no customerId in the payload using admin credentials"
   val req67 =  "GET: Verify ticket has not been updated when using empty customerId and admin credentials"
   val req68 =  "Negative: PATCH: Update ticket with different customerId in the payload using admin credentials"
   val req69 =  "GET: Verify ticket has not been updated when using different customerId and admin credentials"
   val req70 =  "GET list of all Vendors available in the OPS:Device Details schema - Global"
   val req71 =  "GET list of all Vendors available in the OPS:Device Details schema - QA Customer"
   val req72 =  "GET negative scenario - Wrong Credentials"
   val req73 =  "PATCH: Update customerNotes using contact credentials"
   val req74 =  "GET: Check customerNotes updated successfully using contact credentials"
   val req75 =  "Negative - POST - Try to create a new device using invalid vendorId and valid hardwareModel"
   val req76 =  "Negative - POST - Try to create a new device using valid vendorId and invalid hardwareModel"
   val req77 =  "Negative - POST - Try to create a new device using invalid osModel"
   val req78 =  "Negative - PUT - Try to update device using invalid vendorId and valid hardwareModel"
   val req79 =  "GET - Verify existing device values have not been updated"
   val req80 =  "Negative - PUT - Try to update device using valid vendorId and invalid hardwareModel"
   val req81 =  "GET - Verify existing device values have not been updated"
   val req82 =  "Negative - PUT - Try to update device using invalid osModel"
   val req83 =  "GET - Verify existing device values have not been updated"
   val req84 =  "Deactivate the device created in step 62"
   val req85 =  "PATCH - Delete IP addresses from device"
   val req86 =  "GET - Check IP addresses deleted from device"
   val req87 =  "GET - Check no error when limit=1000"

   // Name of each jsession
   val js01 = "jsessionid01"
   val js02 = "jsessionid02"
   val js03 = "jsessionid03"
   val js04 = "jsessionid04"
   val js05 = "jsessionid05"
   val js06 = "jsessionid06"
   val js07 = "jsessionid07"
   val js08 = "jsessionid08"
   val js09 = "jsessionid09"
   val js10 = "jsessionid10"
   val js11 = "jsessionid11"
   val js12 = "jsessionid12"
   val js13 = "jsessionid13"
   val js14 = "jsessionid14"
   val js15 = "jsessionid15"
   val js16 = "jsessionid16"
   val js17 = "jsessionid17"
   val js18 = "jsessionid18"
   val js19 = "jsessionid19"
   val js20 = "jsessionid20"
   val js21 = "jsessionid21"
   val js22 = "jsessionid22"
   val js23 = "jsessionid23"
   val js24 = "jsessionid24"
   val js25 = "jsessionid25"
   val js26 = "jsessionid26"
   val js27 = "jsessionid27"
   val js28 = "jsessionid28"
   val js29 = "jsessionid29"
   val js30 = "jsessionid30"
   val js31 = "jsessionid31"
   val js32 = "jsessionid32"
   val js33 = "jsessionid33"
   val js34 = "jsessionid34"
   val js35 = "jsessionid35"
   val js36 = "jsessionid36"
   val js37 = "jsessionid37"
   val js38 = "jsessionid38"
   val js39 = "jsessionid39"
   val js40 = "jsessionid40"
   val js41 = "jsessionid41"
   val js42 = "jsessionid42"
   val js43 = "jsessionid43"
   val js44 = "jsessionid44"
   val js45 = "jsessionid45"
   val js46 = "jsessionid46"
   val js47 = "jsessionid47"
   val js48 = "jsessionid48"
   val js49 = "jsessionid49"
   val js50 = "jsessionid50"
   val js51 = "jsessionid51"
   val js52 = "jsessionid52"
   val js53 = "jsessionid53"
   val js54 = "jsessionid54"
   val js55 = "jsessionid55"
   val js56 = "jsessionid56"
   val js57 = "jsessionid57"
   val js58 = "jsessionid58"
   val js59 = "jsessionid59"
   val js60 = "jsessionid60"
   val js61 = "jsessionid61"
   val js62 = "jsessionid62"
   val js63 = "jsessionid63"
   val js64 = "jsessionid64"
   val js65 = "jsessionid65"
   val js66 = "jsessionid66"
   val js67 = "jsessionid67"
   val js68 = "jsessionid68"
   val js69 = "jsessionid69"
   val js70 = "jsessionid70"
   val js71 = "jsessionid71"
   val js72 = "jsessionid72"
   val js73 = "jsessionid73"
   val js74 = "jsessionid74"
   val js75 = "jsessionid75"
   val js76 = "jsessionid76"
   val js77 = "jsessionid77"
   val js78 = "jsessionid78"
   val js79 = "jsessionid79"
   val js80 = "jsessionid80"
   val js81 = "jsessionid81"
   val js82 = "jsessionid82"
   val js83 = "jsessionid83"
   val js84 = "jsessionid84"
   val js85 = "jsessionid85"
   val js86 = "jsessionid86"
   val js87 = "jsessionid87"
   
   //Getting timestamp for unique ids
    val timestamp: Timestamp = new Timestamp(System.currentTimeMillis());
    val timestampString = timestamp.getTime;
   
   //define variables for new device payload
    var newDevicePayloadFile:org.json4s.JValue = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_ms/newDevicePayload.json").getLines().mkString)
    if(environment.equals("RUH")){
      newDevicePayloadFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_ms/newDevicePayload_ksa.json").getLines().mkString)
    }
    val newDeviceLmsAggregatorIpAddr = (newDevicePayloadFile \\ "lmsAggregatorIpAddr").extract[String]
    val newDeviceApplicationServerIp = (newDevicePayloadFile \\ "applicationServerIp").extract[String]
    val newDeviceHostName = (newDevicePayloadFile \\ "hostName").extract[String]
    val newDeviceOsModel = (newDevicePayloadFile \\ "osModel").extract[String]
    val newDeviceLogRetentionPeriod = (newDevicePayloadFile \\ "logRetentionPeriod").extract[String]
    val newDeviceMonitoredBy = (newDevicePayloadFile \\ "monitoredBy").extract[String]
    val newDeviceSiteId = (newDevicePayloadFile \\ "siteId").extract[String]
    val newDevicePartnerId = (newDevicePayloadFile \\ "partnerId").extract[String]
    val newDevicePartnerName = (newDevicePayloadFile \\ "partnerName").extract[String]
    val newDeviceCustomerId = (newDevicePayloadFile \\ "customerId").extract[String]
    val newDeviceCustomerName = (newDevicePayloadFile \\ "customerName").extract[String]
    val newDeviceEventCollectorIp = (newDevicePayloadFile \\ "eventCollectorIp").extract[String]
    val newDeviceSensorName = (newDevicePayloadFile \\ "sensorName").extract[String]
    val newDeviceAutodetSensorName = (newDevicePayloadFile \\ "autodetSensorName").extract[String]
    val newDeviceInstalledMemory = (newDevicePayloadFile \\ "installedMemory").extract[String]
    val newDeviceStatus = (newDevicePayloadFile \\ "status").extract[String]
    val newDeviceNotes = (newDevicePayloadFile \\ "notes").extract[String]
    val newDeviceCustomerHostName = (newDevicePayloadFile \\ "customerHostName").extract[String]
    val newDeviceSerialNumber = (newDevicePayloadFile \\ "serialNumber").extract[String]
    val newDeviceSiteName = (newDevicePayloadFile \\ "siteName").extract[String]
    val newDeviceSnmpContextName = (newDevicePayloadFile \\ "snmpContextName").extract[String]
    val newDeviceSnmpEnabled = (newDevicePayloadFile \\ "snmpEnabled").extract[String]   
    val newDeviceSnmpPrivateProtocol = (newDevicePayloadFile \\ "snmpPrivateProtocol").extract[String]
    val newDeviceSnmpVersion = (newDevicePayloadFile \\ "snmpVersion").extract[String]
    val newDeviceSnmpAuthType = (newDevicePayloadFile \\ "snmpAuthType").extract[String]
    val newDeviceSnmpEngineId = (newDevicePayloadFile \\ "snmpEngineId").extract[String]
    val newDeviceVendorId = (newDevicePayloadFile \\ "vendorId").extract[String]
    val newDeviceExternalIp = (newDevicePayloadFile \\ "externalIp").extract[String]
    val newDevicePhysicalIp = (newDevicePayloadFile \\ "physicalIp").extract[String]
    val newDeviceSecondaryExternalIp = (newDevicePayloadFile \\ "secondaryExternalIp").extract[String]
    val newDeviceSecondaryPhysicalIp = (newDevicePayloadFile \\ "secondaryPhysicalIp").extract[String]
    val newDeviceDefaultGateway= (newDevicePayloadFile \\ "defaultGateway").extract[String]
    val newDeviceVirtualIp = (newDevicePayloadFile \\ "virtualIp").extract[String]
    val newDeviceClusterConfiguration = (newDevicePayloadFile \\ "clusterConfiguration").extract[String]
    val newDeviceHardwareModel = (newDevicePayloadFile \\ "hardwareModel").extract[String]
    val newDeviceSsdaDeviceType = (newDevicePayloadFile \\ "ssdaDeviceType").extract[String]
    val newDeviceIsConsole = (newDevicePayloadFile \\ "isConsole").extract[String]
    val newDeviceAlgoSecLogForwarding = (newDevicePayloadFile \\ "algoSecLogForwarding").extract[String]
    val newDeviceOperatingSystem = (newDevicePayloadFile \\ "operatingSystem").extract[String]
    val newDeviceAlgoSecUniqueName = (newDevicePayloadFile \\ "algoSecUniqueName").extract[String]
    
    //define variables for updated device payload
    var updateDevicePayloadFile:org.json4s.JValue = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_ms/updateDevicePayload.json").getLines().mkString)
    if(environment.equals("RUH")){
      updateDevicePayloadFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_ms/updateDevicePayload_ksa.json").getLines().mkString)
    }
    val updateDeviceOsModel = (updateDevicePayloadFile \\ "osModel").extract[String]
    val updateDeviceSiteId = (updateDevicePayloadFile \\ "siteId").extract[String]
    val updateDeviceNotes = (updateDevicePayloadFile \\ "notes").extract[String]
    val updateDeviceCustomerHostName = (updateDevicePayloadFile \\ "customerHostName").extract[String]
    val updateDeviceEventCollectorIp = (updateDevicePayloadFile \\ "eventCollectorIp").extract[String]
    val updateDeviceSnmpContextName = (updateDevicePayloadFile \\ "snmpContextName").extract[String]
    val updateDeviceSnmpEnabled = (updateDevicePayloadFile \\ "snmpEnabled").extract[String]   
    val updateDeviceSnmpPrivateProtocol = (updateDevicePayloadFile \\ "snmpPrivateProtocol").extract[String]
    val updateDeviceSnmpVersion = (updateDevicePayloadFile \\ "snmpVersion").extract[String]
    val updateDeviceSnmpAuthType = (updateDevicePayloadFile \\ "snmpAuthType").extract[String]
    val updateDeviceSnmpEngineId = (updateDevicePayloadFile \\ "snmpEngineId").extract[String]
    val updateDeviceVendorId = (updateDevicePayloadFile \\ "vendorId").extract[String]
    val updateDeviceExternalIp= (updateDevicePayloadFile \\ "externalIp").extract[String]
    val updateDevicePhysicalIp = (updateDevicePayloadFile \\ "physicalIp").extract[String]
    val updateDeviceSecondaryExternalIp = (updateDevicePayloadFile \\ "secondaryExternalIp").extract[String]
    val updateDeviceSecondaryPhysicalIp = (updateDevicePayloadFile \\ "secondaryPhysicalIp").extract[String]
    val updateDeviceDefaultGateway= (updateDevicePayloadFile \\ "defaultGateway").extract[String]
    val updateDeviceVirtualIp = (updateDevicePayloadFile \\ "virtualIp").extract[String]
    val updateDeviceClusterConfiguration = (updateDevicePayloadFile \\ "clusterConfiguration").extract[String]
    val updateDeviceHardwareModel = (updateDevicePayloadFile \\ "hardwareModel").extract[String]
    val updateDeviceSsdaDeviceType = (updateDevicePayloadFile \\ "ssdaDeviceType").extract[String]
    val updateDeviceIsConsole = (updateDevicePayloadFile \\ "isConsole").extract[String]
    val updateDeviceAlgoSecLogForwarding = (updateDevicePayloadFile \\ "algoSecLogForwarding").extract[String]
    val updateDeviceOperatingSystem = (updateDevicePayloadFile \\ "operatingSystem").extract[String]
    val updateDeviceAlgoSecUniqueName = (updateDevicePayloadFile \\ "algoSecUniqueName").extract[String]
    
    //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
    var customerId: String = "P000000614"
    var customerName: String = "QA Customer"
    var partnerId: String = "P000000613"
    var partnerName: String = "QA Partner"
    var fetchAllJson: String = "fetchAll.json"
    var deviceIdQACustomer:String = "P00000008041809"
    var customerIdDemoCustomer: String = "CID001696"
    var customerNameDemoCustomer: String = "Demo Customer"
    var deviceIDDemoCustomer:String = "P00000008080493" //used for negative scenario req06
    var siteIdQACustomer: String = "P00000005011438"
    var updateDevicePayload: String = "updateDevicePayload.json"
    var updateDeviceNoCustomerIdPayload: String = "updateDeviceNoCustomerIdPayload.json"
    var updateDeviceDifferentCustomerIdPayload: String = "updateDeviceDifferentCustomerIdPayload.json"
    
    if(environment.equals("RUH")){
      customerId = "KSAP000000614"
      customerName = "KSA QA Customer"
      partnerId = "KSAP000000613"
      partnerName = "KSA QA Partner"
      fetchAllJson = "fetchAll_ksa.json"
      deviceIdQACustomer = "DEVGD00006084"
      deviceIDDemoCustomer = "DEVGD00006032" //used for negative scenario req06
      siteIdQACustomer = "KSA00005011438"
      updateDevicePayload = "updateDevicePayload_ksa.json"
      updateDeviceNoCustomerIdPayload = "updateDeviceNoCustomerIdPayload_ksa.json"
      updateDeviceDifferentCustomerIdPayload = "updateDeviceDifferentCustomerIdPayload_ksa.json"
    }
   
    // Expectations for the test
    val deviceIdsFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_ms/deviceIds.json").getLines().mkString)
    val expectation01 = 5 // Minimum amount of devices you're specting // Change it to a minor number to run in DEV
    val hostNameExpected = "diegocs-test" // All devices returned must be from this hostName
    
    
    val deviceIDQACustomer = (deviceIdsFile \\ "customerId" \\ "P000000614" \\ environment).extract[String] //used for negative scenario req02
    val masterDeviceId = "P00000008041888" //used for negative scenario req32
    val masterDevice = "CheckPoint2-ids-qa" //used for negative scenario req32
  
}

object deviceMsExecution1 extends BaseTest{
  import deviceMsVariables._
  var deviceMsChainExecution1 = new ChainBuilder(Nil)
  deviceMsChainExecution1 = {
 
    /**
     *  Validates an X amount of records is returned at minimun
     */
    exec(http(req01)
        .get(endpoint + "?limit=500")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$..id").count.gte(expectation01))
        .check(jsonPath("$..createdDate").count.gte(expectation01))
        .check(jsonPath("$..status").count.gte(expectation01))
        .check(jsonPath("$..lmsAggregatorIpAddr").count.gte(expectation01))
        .check(jsonPath("$..hostName").count.gte(expectation01))
        .check(jsonPath("$..customerId").count.gte(expectation01))
        .check(jsonPath("$..customerName").count.gte(expectation01))
        .check(jsonPath("$..partnerId").count.gte(expectation01))
        .check(jsonPath("$..partnerName").count.gte(expectation01))
        .check(jsonPath("$..siteId").count.gte(expectation01))
        .check(jsonPath("$..siteName").count.gte(expectation01))
        .check(jsonPath("$[0]..id").saveAs("deviceID"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..customerId").saveAs("deviceCustomerId"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..partnerId").saveAs("devicePartnerId"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..partnerName").saveAs("devicePartnerName"))  
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')].hostName").saveAs("deviceHostName"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')].status").saveAs("deviceStatus"))
 
        // Second device
        .check(jsonPath("$[-1:]..id").saveAs("deviceID_1"))  
        .check(jsonPath("$[-1:]..customerId").saveAs("deviceCustomerId_1"))
        .check(jsonPath("$[-1:]..partnerId").saveAs("devicePartnerId_1"))
        .check(jsonPath("$[-1:]..partnerName").saveAs("devicePartnerName_1"))      
        .check(jsonPath("$[-1:]..status").saveAs("deviceStatus_1"))
        .check(jsonPath("$[-1:]..hostName").saveAs("deviceHostName_1"))
        
        .check(jsonPath("$[-2:]..id").saveAs("deviceID_2"))  
        .check(jsonPath("$[-2:]..customerId").saveAs("deviceCustomerId_2"))
        .check(jsonPath("$[-2:]..partnerId").saveAs("devicePartnerId_2"))
        .check(jsonPath("$[-2:]..partnerName").saveAs("devicePartnerName_2"))   
        .check(jsonPath("$[-2:]..status").saveAs("deviceStatus_2"))
        .check(jsonPath("$[-2:]..hostName").saveAs("deviceHostName_2"))
        
        .check(jsonPath("$[-3:]..id").saveAs("deviceID_3"))  
        .check(jsonPath("$[-3:]..customerId").saveAs("deviceCustomerId_3"))
        .check(jsonPath("$[-3:]..partnerId").saveAs("devicePartnerId_3"))
        .check(jsonPath("$[-3:]..partnerName").saveAs("devicePartnerName_3"))    
        .check(jsonPath("$[-3:]..status").saveAs("deviceStatus_3"))
        .check(jsonPath("$[-3:]..hostName").saveAs("deviceHostName_3"))
        
        .check(jsonPath("$[-4:]..id").saveAs("deviceID_4"))  
        .check(jsonPath("$[-4:]..customerId").saveAs("deviceCustomerId_4"))
        .check(jsonPath("$[-4:]..partnerId").saveAs("devicePartnerId_4"))
        .check(jsonPath("$[-4:]..partnerName").saveAs("devicePartnerName_4"))       
        .check(jsonPath("$[-4:]..status").saveAs("deviceStatus_4"))
        .check(jsonPath("$[-4:]..hostName").saveAs("deviceHostName_4"))
        
        //save device to update customerHostname
        .check(jsonPath("$[?(@.customerHostName != '' && @.machineHostName != '' && @.notes != '' && @.siteName != '' && @.siteId != '' && @.siteId != '" + siteIdQACustomer + "' && @.operatingSystem != 'Cisco AMP 6.5' && @.externalIp != '' && @.physicalIp != '' && @.serialNumber != '')]..id").saveAs("deviceWithCustomerHostnameToUpdateID"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].externalIp").saveAs("externalIpToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].notes").saveAs("notesToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].siteName").saveAs("siteNameToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].siteId").saveAs("siteIdToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].notes").saveAs("notesToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].customerHostName").saveAs("customerHostNameToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].hostName").saveAs("hostNameToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].externalIp").saveAs("externalIpToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].physicalIp").saveAs("physicalIpToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].serialNumber").saveAs("serialNumberToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].siteName").saveAs("siteNameToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].siteId").saveAs("siteIdToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].machinePlatform").saveAs("machinePlatformToUpdate"))
        .check(jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].operatingSystem").saveAs("operatingSystemToUpdate"))  
        
        //save device to update customerHostname for dev environment
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == 'STG000008067411')]..id").saveAs("deviceWithCustomerHostnameToUpdateID")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].externalIp").saveAs("externalIpToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].notes").saveAs("notesToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].siteName").saveAs("siteNameToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].siteId").saveAs("siteIdToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].notes").saveAs("notesToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].customerHostName").saveAs("customerHostNameToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].hostName").saveAs("hostNameToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].externalIp").saveAs("externalIpToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].physicalIp").saveAs("physicalIpToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].serialNumber").saveAs("serialNumberToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].siteName").saveAs("siteNameToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].siteId").saveAs("siteIdToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].machinePlatform").saveAs("machinePlatformToUpdate")})
        .check(checkIf(environment == "DEV"){jsonPath("$[?(@.id == '" + "${deviceWithCustomerHostnameToUpdateID}" + "')].operatingSystem").saveAs("operatingSystemToUpdate")}) 
        
        .check(jsonPath("$[0]..id").saveAs("DEVICE_ID_REQ01"))
        //.check(jsonPath("$[0]..notes").saveAs("DEVICE_NOTE_VALUE_REQO1"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      /**
       *  Validates only one record is returned
       *  This record must be of a certain id
       *  All other values from this record must not be empty
       *  No records from other ids should return
       */
       //Query for a specific device based on id
      .exec(http(req02)
        .get(endpoint + "${deviceID}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${deviceID}"))
        .check(jsonPath("$..customerId").is("${deviceCustomerId}"))
        .check(jsonPath("$..partnerId").is("${devicePartnerId}"))
        .check(jsonPath("$..partnerName").is("${devicePartnerName}"))
        .check(jsonPath("$..status").is("${deviceStatus}"))
        .check(jsonPath("$..hostName").is("${deviceHostName}"))     
        .check(jsonPath("$..id").count.is(1))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..status").exists)
        .check(jsonPath("$..hostName").exists)
        .check(jsonPath("$..customerId").exists)
        .check(jsonPath("$..customerName").exists)
        .check(jsonPath("$..partnerId").exists)
        .check(jsonPath("$..partnerName").exists)
        .check(jsonPath("$..healthCheckCompletedOn").exists)
        .check(jsonPath("$..complianceType").exists)
        .check(jsonPath("$..remediation").exists)
        .check(jsonPath("$..[?(@.id != \"" + "${deviceID}" + "\")].id").count.is(0))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // All records returned must match a hostName value
      .exec(http(req03)
        .get(endpoint)
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .queryParam("hostName", hostNameExpected)
        .check(status.is(200))
        .check(jsonPath("$..hostName").is(hostNameExpected))
        .check(jsonPath("$..[?(@.hostName != \"" + hostNameExpected + "\")].id").count.gte(0))
        .check(jsonPath("$..status").exists)
        .check(jsonPath("$..customerId").exists)
        .check(jsonPath("$..customerName").exists)
        .check(jsonPath("$..partnerId").exists)
        .check(jsonPath("$..partnerName").exists)
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // Fetching all devices based on a Customer ID
      .exec(http(req04)
        .get(endpoint)
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .queryParam("customerId", customerId)
        .check(status.is(200))
        .check(jsonPath("$..customerId").is(customerId))
        .check(jsonPath("$..[?(@.customerId != \"" + customerId + "\")].id").count.is(0))
        .check(jsonPath("$..[?(@.customerId == \"" + customerId + "\")].id").count.gte(1))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      /**
       * Validates multiple (2) devices records returned
       */
      .exec(http(req05)
        .get(endpoint + "?ids=" + "${deviceID_1}" + "," + "${deviceID}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..id").is("${deviceID}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..customerId").is("${deviceCustomerId}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..partnerId").is("${devicePartnerId}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..partnerName").is("${devicePartnerName}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..status").is("${deviceStatus}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..hostName").is("${deviceHostName}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..id").is("${deviceID_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..customerId").is("${deviceCustomerId_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..partnerId").is("${devicePartnerId_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..partnerName").is("${devicePartnerName_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..status").is("${deviceStatus_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..hostName").is("${deviceHostName_1}"))
        .check(jsonPath("$..id").count.is(2))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..status").exists)
        .check(jsonPath("$..hostName").exists)
        .check(jsonPath("$..customerId").exists)
        .check(jsonPath("$..customerName").exists)
        .check(jsonPath("$..partnerId").exists)
        .check(jsonPath("$..partnerName").exists)
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }
        
      //Negative - Permission denied to access another's customer device data
      .exec(http(req06)
        .get(endpoint + deviceIDDemoCustomer)
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .check(status.is(401))
        .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }
 
      .exec(http(req07)
        .get(endpoint + "?limit=5")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(5))
        .check(jsonPath("$..createdDate").count.is(5))
        .check(jsonPath("$..lastModifiedDate").count.is(5))
        .check(jsonPath("$..status").count.is(5))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js07)) {
        exec( session => {
          session.set(js07, "Unable to retrieve JSESSIONID for this request")
        })
      }


      // Fetching devices for User under Partner Level (imitintegrationtest user - applicable only for STG and ATL)
     .doIf(environment == "STG" || environment == "PRD"){     
      exec(http(req08)
        .get("micro/device?partnerDeviceIds=ece751f8dbdfe010df764b491396195d,imitestcust1-test-us-fw2-PDI")
        .header("Content-Type","application/json")
        .basicAuth(partnerLevelUser, partnerLevelPassword)
        .check(status.is(200))
        .check(jsonPath("$[?(@.partnerDeviceId == 'ece751f8dbdfe010df764b491396195d')].partnerDeviceId").exists)
        .check(jsonPath("$[?(@.partnerDeviceId == 'imitestcust1-test-us-fw2-PDI')].partnerDeviceId").exists)
        .check(jsonPath("$[?(@.partnerDeviceId != 'ece751f8dbdfe010df764b491396195d' && @.partnerDeviceId != 'imitestcust1-test-us-fw2-PDI')].partnerDeviceId").count.is(0))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }
     }


    // Create new device 
    .exec(http(req09)
        .post(endpoint)
        .header("Content-Type","application/json")
        .basicAuth(adUser,adPass)
        .body(StringBody(
            "{"
	        + "\"serialNumber\": \"" + newDeviceSerialNumber + "\","
	        + "\"lmsAggregatorIpAddr\": \"" + newDeviceLmsAggregatorIpAddr + "\","
	        + "\"applicationServerIp\": \"" + newDeviceApplicationServerIp + "\","
	        + "\"hostName\": \"" + newDeviceHostName + "" + timestampString + "\","
	        + "\"osModel\": \"" + newDeviceOsModel + "\","
	        + "\"logRetentionPeriod\": \"" + newDeviceLogRetentionPeriod + "\","
	        + "\"monitoredBy\": \"" + newDeviceMonitoredBy + "\","
	        + "\"siteId\": \"" + newDeviceSiteId + "\","
	        + "\"partnerId\": \"" + newDevicePartnerId + "\","
	        + "\"partnerName\": \"" + newDevicePartnerName + "\","
	        + "\"customerId\": \"" + newDeviceCustomerId + "\","
	        + "\"customerName\": \"" + newDeviceCustomerName + "\","
	        + "\"eventCollectorIp\": \"" + newDeviceEventCollectorIp + "\","
	        + "\"sensorName\": \"" + newDeviceSensorName + "\","
	        + "\"autodetSensorName\": \"" + newDeviceAutodetSensorName + "\","
	        + "\"installedMemory\": \"" + newDeviceInstalledMemory + "\","
	        + "\"status\": \"" + newDeviceStatus + "\","
	        + "\"notes\": \"" + newDeviceNotes + "\","
	        + "\"snmpContextName\": \"" + newDeviceSnmpContextName + "\","
	        + "\"snmpEnabled\": \"" + newDeviceSnmpEnabled + "\","
	        + "\"snmpPrivateProtocol\": \"" + newDeviceSnmpPrivateProtocol + "\","
	        + "\"snmpVersion\": \"" + newDeviceSnmpVersion + "\","
	        + "\"snmpAuthType\": \"" + newDeviceSnmpAuthType + "\","
	        + "\"snmpEngineId\": \"" + newDeviceSnmpEngineId + "\","
	        + "\"vendorId\": \"" + newDeviceVendorId + "\","
	        + "\"hardwareModel\": \"" + newDeviceHardwareModel + "\","
	        + "\"externalIp\": \"" + newDeviceExternalIp + "\","
	        + "\"physicalIp\": \"" + newDevicePhysicalIp + "\","
	        + "\"secondaryExternalIp\": \"" + newDeviceSecondaryExternalIp + "\","
	        + "\"secondaryPhysicalIp\": \"" + newDeviceSecondaryPhysicalIp + "\","
	        + "\"defaultGateway\": \"" + newDeviceDefaultGateway + "\","
	        + "\"virtualIp\": \"" + newDeviceVirtualIp + "\","
	        + "\"clusterConfiguration\": \"" + newDeviceClusterConfiguration + "\","
	        + "\"ssdaDeviceType\": \"" + newDeviceSsdaDeviceType + "\","
	        + "\"isConsole\": \"" + newDeviceIsConsole + "\","
	        + "\"algoSecLogForwarding\": \"" + newDeviceAlgoSecLogForwarding + "\","
	        + "\"algoSecUniqueName\": \"" + newDeviceAlgoSecUniqueName + "\","
	        + "\"operatingSystem\": \"" + newDeviceOperatingSystem + "\","
	        + "\"customerHostName\": \"" + newDeviceCustomerHostName + "" + timestampString + "\""
          + "  }"
          ))
        .check(status.is(201))
        .check(jsonPath("$..id").saveAs("DEVICE_ID_REQUEST09"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js09)) {
        exec( session => {
          session.set(js09, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
      //Get and check brand new created device data  
     .exec(http(req10)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..status").is(newDeviceStatus))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(newDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("Yes"))
        .check(jsonPath("$..snmpPrivateProtocol").is(newDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("2c"))
        .check(jsonPath("$..snmpAuthType").is(newDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(newDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString))
        .check(jsonPath("$..externalIp").is(newDeviceExternalIp))       
        .check(jsonPath("$..physicalIp").is(newDevicePhysicalIp))
        .check(jsonPath("$..secondaryExternalIp").is(newDeviceSecondaryExternalIp))       
        .check(jsonPath("$..secondaryPhysicalIp").is(newDeviceSecondaryPhysicalIp)) 
        .check(jsonPath("$..defaultGateway").is(newDeviceDefaultGateway))       
        .check(jsonPath("$..virtualIp").is(newDeviceVirtualIp)) 
        .check(jsonPath("$..clusterConfiguration").is(newDeviceClusterConfiguration)) 
        .check(jsonPath("$..osModel").is(newDeviceOsModel))
        .check(jsonPath("$..vendorId").is(newDeviceVendorId))
        .check(jsonPath("$..hardwareModel").is(newDeviceHardwareModel))
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(newDeviceSiteId))
        .check(jsonPath("$..siteName").is(newDeviceSiteName))
        .check(jsonPath("$..notes").is(newDeviceNotes))
        .check(jsonPath("$..customerHostName").is(newDeviceCustomerHostName+ timestampString))
        .check(jsonPath("$..serialNumber").is(newDeviceSerialNumber))
        .check(jsonPath("$..ssdaDeviceType").is(newDeviceSsdaDeviceType))
        .check(jsonPath("$..isConsole").is(newDeviceIsConsole))
        .check(jsonPath("$..algoSecLogForwarding").is(newDeviceAlgoSecLogForwarding))
        .check(jsonPath("$..algoSecUniqueName").is(newDeviceAlgoSecUniqueName))
        .check(jsonPath("$..notes").saveAs("customerNotesToUpdate"))
        .check(jsonPath("$..serialNumber").saveAs("serialNumberToUpdate"))
        .check(jsonPath("$..operatingSystem").is(newDeviceOperatingSystem))
        .check(jsonPath("$..siteId").saveAs("siteIdToUpdate"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js10)) {
        exec( session => {
          session.set(js10, "Unable to retrieve JSESSIONID for this request")
        })
      }

     // Update new device using AD Credentials
    .exec(http(req11)
        .patch(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(RawFileBody(currentDirectory + "/tests/resources/device_ms/" + updateDevicePayload))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js11)) {
        exec( session => {
          session.set(js11, "Unable to retrieve JSESSIONID for this request")
        })
      }

    // Deactivate the device
    .exec(http(req12)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId + "\",\"status\":\"Inactive\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js12)) {
        exec( session => {
          session.set(js12, "Unable to retrieve JSESSIONID for this request")
        })
      }

    //Get and check brand new update device data  
     .exec(http(req13)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..status").is("Inactive"))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(updateDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("No"))
        .check(jsonPath("$..snmpPrivateProtocol").is(updateDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("3"))
        .check(jsonPath("$..snmpAuthType").is(updateDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(updateDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString))
        .check(jsonPath("$..externalIp").is(updateDeviceExternalIp))       
        .check(jsonPath("$..physicalIp").is(updateDevicePhysicalIp)) 
        .check(jsonPath("$..secondaryExternalIp").is(updateDeviceSecondaryExternalIp))       
        .check(jsonPath("$..secondaryPhysicalIp").is(updateDeviceSecondaryPhysicalIp)) 
        .check(jsonPath("$..defaultGateway").is(updateDeviceDefaultGateway))       
        .check(jsonPath("$..virtualIp").is(updateDeviceVirtualIp))
        .check(jsonPath("$..osModel").is(updateDeviceOsModel))
        .check(jsonPath("$..vendorId").is(updateDeviceVendorId))
        .check(jsonPath("$..hardwareModel").is(updateDeviceHardwareModel))
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(updateDeviceSiteId))
        .check(jsonPath("$..notes").is(updateDeviceNotes))
        .check(jsonPath("$..customerHostName").is(updateDeviceCustomerHostName))
        .check(jsonPath("$..eventCollectorIp").is(updateDeviceEventCollectorIp))
        .check(jsonPath("$..clusterConfiguration").is(updateDeviceClusterConfiguration)) 
        .check(jsonPath("$..ssdaDeviceType").is(updateDeviceSsdaDeviceType))
        .check(jsonPath("$..isConsole").is(updateDeviceIsConsole))
        .check(jsonPath("$..algoSecLogForwarding").is(updateDeviceAlgoSecLogForwarding))
        .check(jsonPath("$..operatingSystem").is(updateDeviceOperatingSystem))
        .check(jsonPath("$..algoSecUniqueName").is(updateDeviceAlgoSecUniqueName))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js13)) {
        exec( session => {
          session.set(js13, "Unable to retrieve JSESSIONID for this request")
        })
      }

     //return all fields
     .exec(http(req14)
        .get(endpoint + deviceIDQACustomer)
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is(deviceIDQACustomer))
        .check(jsonPath("$..customerId").is(customerId))
        .check(jsonPath("$..partnerId").is(partnerId))
        .check(jsonPath("$..partnerName").is(partnerName))
        .check(jsonPath("$..status").exists)   
        .check(jsonPath("$..hostName").exists)
        .check(jsonPath("$..customerId").exists)
        .check(jsonPath("$..customerName").exists)
        .check(jsonPath("$..partnerId").exists)
        .check(jsonPath("$..partnerName").exists)
        .check(jsonPath("$..healthCheckCompletedOn").exists)
        .check(jsonPath("$..complianceType").exists)
        .check(jsonPath("$..remediation").exists)
        .check(jsonPath("$..customerDeviceId").exists)
		    .check(jsonPath("$..customerHostName").exists)
		    .check(jsonPath("$..operatingSystem").exists)
		    .check(jsonPath("$..externalIp").exists)
		    .check(jsonPath("$..physicalIp").exists)
		    .check(jsonPath("$..secondaryExternalIp").exists)
		    .check(jsonPath("$..secondaryPhysicalIp").exists)
		    .check(jsonPath("$..virtualIp").exists)
		    .check(jsonPath("$..onsiteAggregatorIp").exists)
		    .check(jsonPath("$..sensorName").exists)
		    .check(jsonPath("$..liveOn").exists)
	    	.check(jsonPath("$..deactivatedOn").exists)
	    	.check(jsonPath("$..machinePlatform").exists)
       	.check(jsonPath("$..installedMemory").exists)
	    	.check(jsonPath("$..serialNumber").exists)
	    	.check(jsonPath("$..managementCollector").exists)
		    .check(jsonPath("$..onsiteAggregatorName").exists)
		    .check(jsonPath("$..defaultGateway").exists)
		    .check(jsonPath("$..sumsHostname").exists)
		    .check(jsonPath("$..sumsIp").exists)
		    .check(jsonPath("$..sendToQradar").exists)
		    .check(jsonPath("$..lmsStorage").exists)
		    .check(jsonPath("$..healthCheckCompletedOn").exists)
		    .check(jsonPath("$..healthCheckResults").exists)		    
		    .check(jsonPath("$..createdDate").exists)
		    .check(jsonPath("$..lastModifiedDate").exists)
		    .check(jsonPath("$..lmsAggregatorIpAddr").exists)
		    .check(jsonPath("$..vendor").exists)
		    //.check(jsonPath("$..serviceName").exists)
		    .check(jsonPath("$..logRetentionPeriod").exists)
		    .check(jsonPath("$..managedByVPN").exists)
		    .check(jsonPath("$..localEndpoint").exists)
		    .check(jsonPath("$..monitoredBy").exists)
		    .check(jsonPath("$..siteId").exists)
		    .check(jsonPath("$..siteName").exists)
		    .check(jsonPath("$..ssdaDeviceType").exists)
		    .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js14)) {
        exec( session => {
          session.set(js14, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Fetch records based on single exclude param
      .exec(http(req15)
        .get(endpoint +"?status=Active"+"&"+"exclude=customerId(" + customerId + ")"+"&limit=100")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$[*]..id").count.gt(0))
        .check(jsonPath("$[*]..status").is("Active"))
        .check(jsonPath("$[*]..status").not("Deployment In Process"))
        .check(jsonPath("$[*]..customerId").not(customerId))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js15)) {
        exec( session => {
          session.set(js15, "Unable to retrieve JSESSIONID for this request")
        })
      }
      /**
      //Fetch records based on multiple exclude param
      .exec(http(req16)
        .get(endpoint +"?exclude=serviceType(Managed)"+","+"customerId(P000000614,PR00000663)"+"&limit=100")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$[*]..id").count.gt(0))
        .check(jsonPath("$[?(@.serviceType == 'Managed')].id").count.is(0))
        .check(jsonPath("$[?(@.customerId == 'P000000614')].id").count.is(0))
        .check(jsonPath("$[?(@.customerId == 'PR00000663')].id").count.is(0))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js16)) {
        exec( session => {
          session.set(js16, "Unable to retrieve JSESSIONID for this request")
        })
      }
			**/			
      // XPS-88358
      //Check total count return by device ms
      .exec(http(req17)
        .get(endpoint +"?includeTotalCount=true")
        .basicAuth(adUser,adPass)
        .check(status.is(200))
        .check(jsonPath("$.items[*]..id").count.gt(0))
        .check(jsonPath("$.items").exists)
        .check(jsonPath("$.totalCount").exists)
        .check(jsonPath("$..items[0]..id").saveAs("DEVICE_ID_01"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js17)) {
        exec( session => {
          session.set(js17, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check total count for given device id
      .exec(http(req18)
        .get(endpoint +"?id=" + "${DEVICE_ID_01}" + "&includeTotalCount=true")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$.items[*]..id").count.is(1))
        .check(jsonPath("$.items").exists)
        .check(jsonPath("$.totalCount").is("1"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js18)) {
        exec( session => {
          session.set(js18, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Filter records with managementCollector
      //XPS-108244
      .exec(http(req19)
        .get(endpoint + "?managementCollector=Managed")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..managementCollector").is("Managed"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js19)) {
        exec( session => {
          session.set(js19, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Fetch all devices with AD credentials
      //QX-11124
      .exec(http(req20)
        .post(endpoint)
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .queryParam("fetchAll", true)
        .body(RawFileBody(currentDirectory + "/tests/resources/device_ms/" + fetchAllJson))
        .check(status.is(200))
        .check(jsonPath("$[0].id").exists)
        .check(jsonPath("$[*].id").count.is(1))
        .check(jsonPath("$..id").is(deviceIdQACustomer))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js20)) {
        exec( session => {
          session.set(js20, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //GET device data only for imitintegrationtest creds
      //Fetching devices for User under Partner Level (imitintegrationtest user - applicable only for STG and ATL)
      .doIf(environment == "STG" || environment == "PRD") {
        exec(http(req21)
          .get(endpoint)
          .header("Content-Type","application/json")
          .basicAuth(partnerLevelUser, partnerLevelPassword)
          .check(status.is(200))
          .check(substring("imitestcust").exists)
          .check(jsonPath("$[0]..id").saveAs("IMIT_INTEGRATION_DEVICE_ID"))
          .check(header("x-datasource").is("snow"))
          .check(jsonPath("$[?(@.customerId != 'PR00003310' && @.customerId != 'PR00005800' && @.customerId != 'PR00003312' && @.customerId != 'PR00003311')].id").count.is(0))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
        ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js21)) {
            exec(session => {
              session.set(js21, "Unable to retrieve JSESSIONID for this request")
            })
          }
      }

      // PUT: Negative - Verify customer contact can not update not permitted fields
    .exec(http(req22)
        .put(endpoint + "${deviceWithCustomerHostnameToUpdateID}")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"customerId\":\"" + customerId + "\",\"status\": \"Inactive\",\"customerName\": \"QA Customer Test123\",\"customerHostName\": \"" + "${customerHostNameToUpdate}" + timestampString + "\"}"))
        .check(status.is(400))
        .check(jsonPath("$..rsp").is("Request body contains fields which are not allowed"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js22)) {
        exec( session => {
          session.set(js22, "Unable to retrieve JSESSIONID for this request")
        })
      }

    //PUT: Update new device with permitted fields for customer contact
    .exec(http(req23)
        .put(endpoint + "${deviceWithCustomerHostnameToUpdateID}")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"customerId\":\"" + customerId + "\",\"hostName\": \"" + "${hostNameToUpdate}" + timestampString + "\",\"customerHostName\": \"" + "${customerHostNameToUpdate}" + timestampString + "\",\"siteId\": \"" + siteIdQACustomer + "\", \"serialNumber\": \"" + "${serialNumberToUpdate}" + timestampString + "\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${deviceWithCustomerHostnameToUpdateID}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js23)) {
        exec( session => {
          session.set(js23, "Unable to retrieve JSESSIONID for this request")
        })
      }

     //GET: Verify that all customer contact permitted fields has been updated successfully
    .exec(http(req24)
        .get(endpoint + "${deviceWithCustomerHostnameToUpdateID}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${deviceWithCustomerHostnameToUpdateID}")) 
        .check(jsonPath("$..customerHostName").is("${customerHostNameToUpdate}" + timestampString))
        .check(jsonPath("$..siteName").is("Southfield"))
        .check(jsonPath("$..siteId").is(siteIdQACustomer))      
        .check(jsonPath("$..serialNumber").is("${serialNumberToUpdate}" + timestampString))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js24)) {
        exec( session => {
          session.set(js24, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //PUT: Update new device customerHostName field back to original value
    .exec(http(req25)
        .put(endpoint + "${deviceWithCustomerHostnameToUpdateID}")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"customerId\":\"" + customerId + "\",\"hostName\": \"" + "${hostNameToUpdate}\",\"customerHostName\": \"" + "${customerHostNameToUpdate}\",\"siteId\": \"${siteIdToUpdate}\", \"serialNumber\": \"" + "${serialNumberToUpdate}\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${deviceWithCustomerHostnameToUpdateID}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js25)) {
        exec( session => {
          session.set(js25, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //GET: Verify that customer contact permitted fields have been updated back to original value successfully
    .exec(http(req26)
        .get(endpoint + "${deviceWithCustomerHostnameToUpdateID}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${deviceWithCustomerHostnameToUpdateID}")) 
        //.check(jsonPath("$..externalIp").is("${externalIpToUpdate}"))
        //.check(jsonPath("$..physicalIp").is("${physicalIpToUpdate}"))
        .check(jsonPath("$..notes").is("${notesToUpdate}"))
        .check(jsonPath("$..siteName").is("${siteNameToUpdate}"))
        .check(jsonPath("$..siteId").is("${siteIdToUpdate}"))
        .check(jsonPath("$..hostName").is("${hostNameToUpdate}"))    
        .check(jsonPath("$..serialNumber").is("${serialNumberToUpdate}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js26))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js26)) {
        exec( session => {
          session.set(js26, "Unable to retrieve JSESSIONID for this request")
        })
      }

  }

}

 object deviceMsExecution2 extends BaseTest{
  import deviceMsVariables._
  var deviceMsChainExecution2 = new ChainBuilder(Nil)
  deviceMsChainExecution2 = {

    //Check qatest user should not be able to modify records of imitintegrationtest(imitintegrationtest user - applicable only for STG and ATL)
      doIf(environment == "STG" || environment == "PRD") {
       exec(http(req27)
        .put(endpoint + "${IMIT_INTEGRATION_DEVICE_ID}")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"siteName\":\"Test sitename\"}"))
        .check(status.is(401))
        .check(jsonPath("$..errors").exists)
        .check(jsonPath("$..errors./device[0]").is("${IMIT_INTEGRATION_DEVICE_ID}"))
        .check(jsonPath("$..code").is("401"))
        .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js27)) {
        exec( session => {
          session.set(js27, "Unable to retrieve JSESSIONID for this request")
        })
      }
      }

      //Check imitintegrationtest user should not be able to modify records of qatest
      // Fetching devices for User under Partner Level (imitintegrationtest user - applicable only STG and ATL)
      .doIf(environment == "STG" || environment == "PRD") {
        exec(http(req28)
          .put(endpoint + "${DEVICE_ID_REQ01}")
          .basicAuth(partnerLevelUser, partnerLevelPassword)
          .header("Content-Type","application/json")
          .body(StringBody("{\"notes\":\"Test notes\"}"))
          .check(status.is(401))
          .check(jsonPath("$..errors").exists)
          .check(jsonPath("$..code").is("401"))
          .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js28))
        ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js28)) {
            exec(session => {
              session.set(js28, "Unable to retrieve JSESSIONID for this request")
            })
          }
      }
   
      //GET jwt token to use in GET call using token
      .exec(http(req29)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"x-remoteip\":\"209.134.187.156\",\"sub\":\"Microservices\",\"user-realm\":\"CUSTOMER_CONTACT\",\"customerId\":\"" + customerId + "\",\"iss\":\"sec.ibm.com\",\"privileged-user\":false,\"partnerId\":\"" + partnerId + "\",\"username\":\"qatest\"}"))
        .check(status.is(200))
        .check(bodyString.saveAs("RESPONSE_TOKEN"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js29))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js29)) {
        exec( session => {
          session.set(js29, "Unable to retrieve JSESSIONID for this request")
        })
      }
  
      //Get response using jwt-token
      /**no basicAuth should be sent along with the token auth**/
      .exec(http(req30)
        .get(endpoint + "?start=0&limit=5")
        .header("Content-Type","application/json")
        .header("Authorization", "Bearer ${RESPONSE_TOKEN}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(5))
        .check(jsonPath("$..createdDate").count.is(5))
        .check(jsonPath("$..lastModifiedDate").count.is(5))
        .check(jsonPath("$..status").count.is(5))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js30))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js30)) {
        exec( session => {
          session.set(js30, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Get All with TotalCount=true
      .exec(http(req31)
        .get(endpoint + "?includeTotalCount=true")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").count.gte(1))
        .check(jsonPath("$..totalCount").exists)
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js31))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js31)) {
        exec( session => {
          session.set(js31, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Get using ids parameter with TotalCount=true
      .exec(http(req32)
        .get(endpoint + "?includeTotalCount=true&id=${DEVICE_ID_REQUEST09}&status=Inactive")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..totalCount").exists)
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js32))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js32)) {
        exec( session => {
          session.set(js32, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Negative Get All with TotalCount=false
      .exec(http(req33)
        .get(endpoint + "?includeTotalCount=false")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").count.gte(1))
        .check(jsonPath("$..totalCount").notExists)
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js33))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js33)) {
        exec( session => {
          session.set(js33, "Unable to retrieve JSESSIONID for this request")
        })
      }
      /**
      // PUT : Update clusterConfiguration to Master
      .exec(http(req34)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .header("Content-Type","application/json")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"	\":\"Master\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js34))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js34)) {
        exec( session => {
          session.set(js34, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //GET: Check Update clusterConfiguration to Master
      .exec(http(req35)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..clusterConfiguration").is("Master"))
        .check(jsonPath("$..masterDevice").notExists)
        .check(jsonPath("$..masterDeviceId").notExists)
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js35))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js35)) {
        exec( session => {
          session.set(js35, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      
      // PUT : Update clusterConfiguration to Child and masterDevice and masterDeviceId values
      .exec(http(req36)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .header("Content-Type","application/json")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"clusterConfiguration\":\"Child\",\"masterDevice\":\"" + masterDevice + "\",\"masterDeviceId\":\"" + masterDeviceId + "\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js36))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js36)) {
        exec( session => {
          session.set(js36, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      // GET: Check Update clusterConfiguration to Child and masterDevice and masterDeviceId values
      .exec(http(req37)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..clusterConfiguration").is("Child"))
        .check(jsonPath("$..masterDevice").is(masterDevice))
        .check(jsonPath("$..masterDeviceID").is(masterDeviceId))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js37))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js37)) {
        exec( session => {
          session.set(js37, "Unable to retrieve JSESSIONID for this request")
        })
      }     
**/
      // POST: Validate the response of POST endpoint to retrieve device data by passing valid payload data
      .exec(http(req38)
        .post(endpoint + "?fetchAll=true")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"ids\":[{\"id\": \"" + "${deviceID}" + "\"},{\"id\": \"" + "${deviceID_1}" + "\"},{\"id\": \"" + "${deviceID_2}" + "\"},{\"id\": \"" + "${deviceID_3}" + "\"},{\"id\": \"" + "${deviceID_4}" + "\"}]}"))
        .check(status.is(200))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..id").is("${deviceID}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..customerId").is("${deviceCustomerId}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..partnerId").is("${devicePartnerId}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..partnerName").is("${devicePartnerName}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..status").is("${deviceStatus}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..hostName").is("${deviceHostName}")) 

        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..id").is("${deviceID_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..customerId").is("${deviceCustomerId_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..partnerId").is("${devicePartnerId_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..partnerName").is("${devicePartnerName_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..status").is("${deviceStatus_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..hostName").is("${deviceHostName_1}"))   

        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..id").is("${deviceID_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..customerId").is("${deviceCustomerId_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..partnerId").is("${devicePartnerId_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..partnerName").is("${devicePartnerName_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..status").is("${deviceStatus_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..hostName").is("${deviceHostName_2}"))
        
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..id").is("${deviceID_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..customerId").is("${deviceCustomerId_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..partnerId").is("${devicePartnerId_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..partnerName").is("${devicePartnerName_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..status").is("${deviceStatus_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..hostName").is("${deviceHostName_3}"))
        
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..id").is("${deviceID_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..customerId").is("${deviceCustomerId_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..partnerId").is("${devicePartnerId_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..partnerName").is("${devicePartnerName_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..status").is("${deviceStatus_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..hostName").is("${deviceHostName_4}"))
        
        .check(jsonPath("$..totalCount").notExists) 
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js38))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js38)) {
        exec( session => {
          session.set(js38, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      // POST: Validate the response of POST endpoint to retrieve device data using totalCount
      .exec(http(req39)
        .post(endpoint + "?fetchAll=true&includeTotalCount=true")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId +  "\",\"ids\":[{\"id\": \"" + "${deviceID}" + "\"},{\"id\": \"" + "${deviceID_1}" + "\"},{\"id\": \"" + "${deviceID_2}" + "\"},{\"id\": \"" + "${deviceID_3}" + "\"},{\"id\": \"" + "${deviceID_4}" + "\"}]}"))
        .check(status.is(200))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..id").is("${deviceID}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..customerId").is("${deviceCustomerId}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..partnerId").is("${devicePartnerId}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..partnerName").is("${devicePartnerName}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..status").is("${deviceStatus}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..hostName").is("${deviceHostName}"))  

        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..id").is("${deviceID_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..customerId").is("${deviceCustomerId_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..partnerId").is("${devicePartnerId_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..partnerName").is("${devicePartnerName_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..status").is("${deviceStatus_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..hostName").is("${deviceHostName_1}"))   

        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..id").is("${deviceID_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..customerId").is("${deviceCustomerId_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..partnerId").is("${devicePartnerId_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..partnerName").is("${devicePartnerName_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..status").is("${deviceStatus_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..hostName").is("${deviceHostName_2}"))

        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..id").is("${deviceID_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..customerId").is("${deviceCustomerId_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..partnerId").is("${devicePartnerId_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..partnerName").is("${devicePartnerName_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..status").is("${deviceStatus_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..hostName").is("${deviceHostName_3}")) 
        
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..id").is("${deviceID_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..customerId").is("${deviceCustomerId_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..partnerId").is("${devicePartnerId_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..partnerName").is("${devicePartnerName_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..status").is("${deviceStatus_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..hostName").is("${deviceHostName_4}"))
        
        .check(jsonPath("$..totalCount").is("5")) 
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js39))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js39)) {
        exec( session => {
          session.set(js39, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      // POST: Validate the response of POST endpoint to retrieve device data using totalCount and start and limit
      .exec(http(req40)
        .post(endpoint + "?fetchAll=true&includeTotalCount=true")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId +  "\",\"ids\":[{\"id\": \"" + "${deviceID}" + "\"},{\"id\": \"" + "${deviceID_1}" + "\"},{\"id\": \"" + "${deviceID_2}" + "\"},{\"id\": \"" + "${deviceID_3}" + "\"},{\"id\": \"" + "${deviceID_4}" + "\"}]}"))
        .check(status.is(200))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..id").is("${deviceID}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..customerId").is("${deviceCustomerId}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..partnerId").is("${devicePartnerId}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..partnerName").is("${devicePartnerName}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..status").is("${deviceStatus}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID}" + "')]..hostName").is("${deviceHostName}"))

        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..id").is("${deviceID_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..customerId").is("${deviceCustomerId_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..partnerId").is("${devicePartnerId_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..partnerName").is("${devicePartnerName_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..status").is("${deviceStatus_1}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_1}" + "')]..hostName").is("${deviceHostName_1}"))  

        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..id").is("${deviceID_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..customerId").is("${deviceCustomerId_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..partnerId").is("${devicePartnerId_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..partnerName").is("${devicePartnerName_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..status").is("${deviceStatus_2}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_2}" + "')]..hostName").is("${deviceHostName_2}"))
        
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..id").is("${deviceID_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..customerId").is("${deviceCustomerId_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..partnerId").is("${devicePartnerId_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..partnerName").is("${devicePartnerName_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..status").is("${deviceStatus_3}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_3}" + "')]..hostName").is("${deviceHostName_3}"))
        
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..id").is("${deviceID_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..customerId").is("${deviceCustomerId_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..partnerId").is("${devicePartnerId_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..partnerName").is("${devicePartnerName_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..status").is("${deviceStatus_4}"))
        .check(jsonPath("$.items[?(@.id == '" + "${deviceID_4}" + "')]..hostName").is("${deviceHostName_4}"))
        
        .check(jsonPath("$..totalCount").is("5")) 
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js40))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js40)) {
        exec( session => {
          session.set(js40, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //POST: Validate the response of POST endpoint to retrieve device data by passing empty payload data
      .exec(http(req41)
        .post(endpoint + "?fetchAll=true")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"ids\": []}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("No device id passed in request")) 
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js41))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js41)) {
        exec( session => {
          session.set(js41, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //POST: Validate the response of POST endpoint to retrieve device data by passing Invalid payload data
      .exec(http(req42)
        .post(endpoint + "?fetchAll=true&includeTotalCount=true")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"id\": []}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("Invalid JSON Request")) 
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js42))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js42)) {
        exec( session => {
          session.set(js42, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      // POST: Validate the response of POST endpoint to retrieve device data by passing valid payload data using ad credentials
      .exec(http(req43)
        .post(endpoint + "?fetchAll=true")
        .header("Content-Type","application/json")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"customerId\":\"" + customerId +  "\",\"ids\":[{\"id\": \"" + "${deviceID}" + "\"},{\"id\": \"" + "${deviceID_1}" + "\"},{\"id\": \"" + "${deviceID_2}" + "\"},{\"id\": \"" + "${deviceID_3}" + "\"},{\"id\": \"" + "${deviceID_4}" + "\"}]}"))
        .check(status.is(200))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..id").is("${deviceID}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..customerId").is("${deviceCustomerId}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..partnerId").is("${devicePartnerId}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..partnerName").is("${devicePartnerName}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..status").is("${deviceStatus}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID}" + "')]..hostName").is("${deviceHostName}"))     

        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..id").is("${deviceID_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..customerId").is("${deviceCustomerId_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..partnerId").is("${devicePartnerId_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..partnerName").is("${devicePartnerName_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..status").is("${deviceStatus_1}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_1}" + "')]..hostName").is("${deviceHostName_1}"))   

        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..id").is("${deviceID_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..customerId").is("${deviceCustomerId_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..partnerId").is("${devicePartnerId_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..partnerName").is("${devicePartnerName_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..status").is("${deviceStatus_2}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_2}" + "')]..hostName").is("${deviceHostName_2}"))
        
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..id").is("${deviceID_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..customerId").is("${deviceCustomerId_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..partnerId").is("${devicePartnerId_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..partnerName").is("${devicePartnerName_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..status").is("${deviceStatus_3}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_3}" + "')]..hostName").is("${deviceHostName_3}"))
        
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..id").is("${deviceID_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..customerId").is("${deviceCustomerId_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..partnerId").is("${devicePartnerId_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..partnerName").is("${devicePartnerName_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..status").is("${deviceStatus_4}"))
        .check(jsonPath("$[?(@.id == '" + "${deviceID_4}" + "')]..hostName").is("${deviceHostName_4}"))
        
        .check(jsonPath("$..totalCount").notExists) 
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js43))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js43)) {
        exec( session => {
          session.set(js43, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //GET - Save total count for all customer contact devices
      .exec(http(req44)
        .get(endpoint + "?customerId=" + customerId +  "&limit=1000&includeTotalCount=true")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))     
        .check(jsonPath("$..totalCount").saveAs("TOTAL_COUNT"))
        .check(jsonPath("$..id").count.is("${TOTAL_COUNT}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js44))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js44)) {
        exec( session => {
          session.set(js44, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //GET - Match total count for all customer contact devices with totalCount when limit parameter is defined
      .exec(http(req45)
        .get(endpoint + "?customerId=" + customerId +  "&limit=100&includeTotalCount=true")
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$..id").count.lte("100"))
        .check(jsonPath("$..totalCount").is("${TOTAL_COUNT}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js45))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js45)) {
        exec( session => {
          session.set(js45, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      // Create new device 
    .exec(http(req46)
        .post(endpoint)
        .header("Content-Type","application/json")
        .basicAuth(adUser,adPass)
        .body(StringBody(
            "{"
	        + "\"serialNumber\": \"" + newDeviceSerialNumber + "\","
	        + "\"osModel\": \"" + newDeviceOsModel + "\","
	        + "\"lmsAggregatorIpAddr\": \"" + newDeviceLmsAggregatorIpAddr + "\","
	        + "\"applicationServerIp\": \"" + newDeviceApplicationServerIp + "\","
	        + "\"hostName\": \"" + newDeviceHostName + "" + timestampString + "\","
	        + "\"logRetentionPeriod\": \"" + newDeviceLogRetentionPeriod + "\","
	        + "\"monitoredBy\": \"" + newDeviceMonitoredBy + "\","
	        + "\"siteId\": \"" + newDeviceSiteId + "\","
	        + "\"partnerId\": \"" + newDevicePartnerId + "\","
	        + "\"partnerName\": \"" + newDevicePartnerName + "\","
	        + "\"customerId\": \"" + newDeviceCustomerId + "\","
	        + "\"customerName\": \"" + newDeviceCustomerName + "\","
	        + "\"eventCollectorIp\": \"" + newDeviceEventCollectorIp + "\","
	        + "\"sensorName\": \"" + newDeviceSensorName + "\","
	        + "\"autodetSensorName\": \"" + newDeviceAutodetSensorName + "\","
	        + "\"installedMemory\": \"" + newDeviceInstalledMemory + "\","
	        + "\"status\": \"" + newDeviceStatus + "\","
	        + "\"notes\": \"" + newDeviceNotes + "\","
	        + "\"snmpContextName\": \"" + newDeviceSnmpContextName + "\","
	        + "\"snmpEnabled\": \"" + newDeviceSnmpEnabled + "\","
	        + "\"snmpPrivateProtocol\": \"" + newDeviceSnmpPrivateProtocol + "\","
	        + "\"snmpVersion\": \"" + newDeviceSnmpVersion + "\","
	        + "\"snmpAuthType\": \"" + newDeviceSnmpAuthType + "\","
	        + "\"snmpEngineId\": \"" + newDeviceSnmpEngineId + "\","
	        + "\"vendorId\": \"" + newDeviceVendorId + "\","
	        + "\"hardwareModel\": \"" + newDeviceHardwareModel + "\","
	        + "\"customerHostName\": \"" + newDeviceCustomerHostName + "" + timestampString + "\""
          + "  }"
          ))
        .check(status.is(400))
        .check(jsonPath("$..id").notExists)
        .check(jsonPath("$..code").is("400"))
        .check(jsonPath("$..message").is("hostName already exists"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js46))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js46)) {
        exec( session => {
          session.set(js46, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //GET - Verify existing device values have not been updated
     .exec(http(req47)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..status").is("Inactive"))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(updateDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("No"))
        .check(jsonPath("$..snmpPrivateProtocol").is(updateDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("3"))
        .check(jsonPath("$..snmpAuthType").is(updateDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(updateDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString))
        //.check(jsonPath("$..externalIp").is("127.0.23.12"))       
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(updateDeviceSiteId))
        .check(jsonPath("$..notes").is(updateDeviceNotes))
        .check(jsonPath("$..customerHostName").is(updateDeviceCustomerHostName))
        .check(jsonPath("$..eventCollectorIp").is(updateDeviceEventCollectorIp))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js47))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js47)) {
        exec( session => {
          session.set(js47, "Unable to retrieve JSESSIONID for this request")
        })
      }
 
     //GET - Query using multiple filters
     .exec(http(req48)
        .get(endpoint + "?status=Active,Deployment%20Complete,%20Deployment%20In%20Process&includeTotalCount=true&serviceType=Managed,%20Managed%20%2B%20Security%20Event%20Monitoring")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..totalCount").exists)
        .check(jsonPath("$.items[?(@.status != 'Active' && @.status != 'Deployment Complete' && @.status != 'Deployment In Process')].id").count.is(0))
        .check(jsonPath("$.items[?(@.status == 'Active')].id").exists)
        .check(jsonPath("$.items[?(@.status == 'Deployment Complete')].id").exists)
        .check(jsonPath("$.items[?(@.status == 'Deployment In Process')].id").exists)
        .check(jsonPath("$.items[?(@.serviceType != 'Managed' && @.serviceType != 'Managed + Security Event Monitoring')].id").count.is(0))
        .check(jsonPath("$.items[?(@.serviceType == 'Managed')].id").exists)
        .check(jsonPath("$.items[?(@.serviceType == 'Managed + Security Event Monitoring')].id").exists)
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js48))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js48)) {
        exec( session => {
          session.set(js48, "Unable to retrieve JSESSIONID for this request")
        })
      }
     /**
     //GET jwt token using email contact to use in GET call using token
      exec(http(req49)
        .post("micro/jwt_provider/issue")
        .header("Content-Type","application/json")
        .body(StringBody("{\"user-realm\":\"CUSTOMER_CONTACT\",\"customerId\":\"P000000614\",\"iss\":\"sec.ibm.com\",\"privileged-user\":false,\"username\":\"dayne.dochi@outlook.com\"}"))
        .check(status.is(200))
        .check(bodyString.saveAs("RESPONSE_TOKEN_REQUEST_49"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js49))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js49)) {
        exec( session => {
          session.set(js29, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Negative - Permission denied to access another's customer device data using email contact token
      .exec(http(req50)
        .get(endpoint + deviceIDDemoCustomer)
        .header("Content-Type","application/json")
        .header("Authorization", "Bearer " + "${RESPONSE_TOKEN_REQUEST_49}")
        .check(status.is(401))
        .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js50))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js50)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }
      **/
      //PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DDThh:mm:ssZ
    	.exec(http(req51)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId +  "\",\"lastAutodetected\":\"2022-03-13T09:10:10Z\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js51))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js51)) {
        exec( session => {
          session.set(js51, "Unable to retrieve JSESSIONID for this request")
        })
      }
    	
    	//GET: Verify lastAutodetected updated using ISO format YYYY-MM-DDThh:mm:ssZ
      .exec(http(req52)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..lastAutodetected").is("Sun Mar 13 09:10:10 GMT 2022"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js52))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js52)) {
        exec( session => {
          session.set(js52, "Unable to retrieve JSESSIONID for this request")
        })
      }
      //PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DDThh:mm:ss.sssZ
    	.exec(http(req53)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId +  "\",\"lastAutodetected\":\"2021-10-09T11:11:58Z.234\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js53))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js53)) {
        exec( session => {
          session.set(js53, "Unable to retrieve JSESSIONID for this request")
        })
      }
    	
    	//GET: Verify lastAutodetected updated using ISO format YYYY-MM-DDThh:mm:ss.sssZ
      .exec(http(req54)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..lastAutodetected").is("Sat Oct 09 11:11:58 GMT 2021"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js54))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js54)) {
        exec( session => {
          session.set(js54, "Unable to retrieve JSESSIONID for this request")
        })
      }
      //PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DDThh:mmZ
    	.exec(http(req55)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId +  "\",\"lastAutodetected\":\"2023-02-10T10:10Z\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js55))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js55)) {
        exec( session => {
          session.set(js55, "Unable to retrieve JSESSIONID for this request")
        })
      }
    	
    	//GET: Verify lastAutodetected updated using ISO format YYYY-MM-DDThh:mmZ
      .exec(http(req56)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..lastAutodetected").is("Fri Feb 10 10:10:00 GMT 2023"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js56))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js56)) {
        exec( session => {
          session.set(js56, "Unable to retrieve JSESSIONID for this request")
        })
      }
      //PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DDThhZ
    	.exec(http(req57)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId +  "\",\"lastAutodetected\":\"2010-02-05T05Z\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js57))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js57)) {
        exec( session => {
          session.set(js57, "Unable to retrieve JSESSIONID for this request")
        })
      }
    	
    	//GET: Verify lastAutodetected updated using ISO format YYYY-MM-DDThhZ
      .exec(http(req58)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..lastAutodetected").is("Fri Feb 05 05:00:00 GMT 2010"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js58))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js58)) {
        exec( session => {
          session.set(js58, "Unable to retrieve JSESSIONID for this request")
        })
      }
      //PUT : Update lastAutodetected field to accept ISO format YYYY-MM-DD
    	.exec(http(req59)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId +  "\",\"lastAutodetected\":\"2009-01-08\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js59))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js59)) {
        exec( session => {
          session.set(js59, "Unable to retrieve JSESSIONID for this request")
        })
      }
    	
    	//GET: Verify lastAutodetected updated using ISO format YYYY-MM-DD
      .exec(http(req60)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..lastAutodetected").is("Thu Jan 08 00:00:00 GMT 2009"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js60))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js60)) {
        exec( session => {
          session.set(js60, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
    //Negative: POST: Create new device with no customerId in the payload using admin credentials
    .exec(http(req61)
        .post(endpoint)
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody(
            "{"
	        + "\"serialNumber\": \"" + newDeviceSerialNumber + "\","
	        + "\"lmsAggregatorIpAddr\": \"" + newDeviceLmsAggregatorIpAddr + "\","
	        + "\"applicationServerIp\": \"" + newDeviceApplicationServerIp + "\","
	        + "\"hostName\": \"" + newDeviceHostName + "" + timestampString + "1" + "\","
	        + "\"osModel\": \"" + newDeviceOsModel + "\","
	        + "\"logRetentionPeriod\": \"" + newDeviceLogRetentionPeriod + "\","
	        + "\"monitoredBy\": \"" + newDeviceMonitoredBy + "\","
	        + "\"siteId\": \"" + newDeviceSiteId + "\","
	        + "\"partnerId\": \"" + newDevicePartnerId + "\","
	        + "\"partnerName\": \"" + newDevicePartnerName + "\","
	        + "\"eventCollectorIp\": \"" + newDeviceEventCollectorIp + "\","
	        + "\"sensorName\": \"" + newDeviceSensorName + "\","
	        + "\"autodetSensorName\": \"" + newDeviceAutodetSensorName + "\","
	        + "\"installedMemory\": \"" + newDeviceInstalledMemory + "\","
	        + "\"status\": \"" + newDeviceStatus + "\","
	        + "\"notes\": \"" + newDeviceNotes + "\","
	        + "\"snmpContextName\": \"" + newDeviceSnmpContextName + "\","
	        + "\"snmpEnabled\": \"" + newDeviceSnmpEnabled + "\","
	        + "\"snmpPrivateProtocol\": \"" + newDeviceSnmpPrivateProtocol + "\","
	        + "\"snmpVersion\": \"" + newDeviceSnmpVersion + "\","
	        + "\"snmpAuthType\": \"" + newDeviceSnmpAuthType + "\","
	        + "\"snmpEngineId\": \"" + newDeviceSnmpEngineId + "\","
	        + "\"vendorId\": \"" + newDeviceVendorId + "\","
	        + "\"hardwareModel\": \"" + newDeviceHardwareModel + "\","
	        + "\"customerHostName\": \"" + newDeviceCustomerHostName + "" + timestampString + "\""
          + "  }"
          ))
        .check(status.is(401))
        .check(jsonPath("$..message").is("Missing customer ownership on record"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js61))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js61)) {
        exec( session => {
          session.set(js61, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //POST: Create new device with no customerId in the payload using contact credentials
    .exec(http(req62)
        .post(endpoint)
        .basicAuth(contactUser, contactPass)
        .header("Content-Type","application/json")
        //.body(RawFileBody(currentDirectory + "/tests/resources/device_ms/newDevicePaylod.json"))
        .body(StringBody(
            "{"
	        + "\"serialNumber\": \"" + newDeviceSerialNumber + "\","
	        + "\"lmsAggregatorIpAddr\": \"" + newDeviceLmsAggregatorIpAddr + "\","
	        + "\"applicationServerIp\": \"" + newDeviceApplicationServerIp + "\","
	        + "\"hostName\": \"" + newDeviceHostName + "" + timestampString + "1" + "\","
	        + "\"osModel\": \"" + newDeviceOsModel + "\","
	        + "\"logRetentionPeriod\": \"" + newDeviceLogRetentionPeriod + "\","
	        + "\"monitoredBy\": \"" + newDeviceMonitoredBy + "\","
	        + "\"siteId\": \"" + newDeviceSiteId + "\","
	        + "\"partnerId\": \"" + newDevicePartnerId + "\","
	        + "\"partnerName\": \"" + newDevicePartnerName + "\","
	        + "\"eventCollectorIp\": \"" + newDeviceEventCollectorIp + "\","
	        + "\"sensorName\": \"" + newDeviceSensorName + "\","
	        + "\"autodetSensorName\": \"" + newDeviceAutodetSensorName + "\","
	        + "\"installedMemory\": \"" + newDeviceInstalledMemory + "\","
	        + "\"status\": \"" + newDeviceStatus + "\","
	        + "\"notes\": \"" + newDeviceNotes + "\","
	        + "\"snmpContextName\": \"" + newDeviceSnmpContextName + "\","
	        + "\"snmpEnabled\": \"" + newDeviceSnmpEnabled + "\","
	        + "\"snmpPrivateProtocol\": \"" + newDeviceSnmpPrivateProtocol + "\","
	        + "\"snmpVersion\": \"" + newDeviceSnmpVersion + "\","
	        + "\"snmpAuthType\": \"" + newDeviceSnmpAuthType + "\","
	        + "\"snmpEngineId\": \"" + newDeviceSnmpEngineId + "\","
	        + "\"vendorId\": \"" + newDeviceVendorId + "\","
	        + "\"hardwareModel\": \"" + newDeviceHardwareModel + "\","
	        + "\"customerHostName\": \"" + newDeviceCustomerHostName + "" + timestampString + "\""
          + "  }"
          ))
        .check(status.is(201))
        .check(jsonPath("$..id").saveAs("DEVICE_ID_REQUEST62"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js62))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js62)) {
        exec( session => {
          session.set(js62, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
     //GET: Verify ticket has been created when using empty customerId and contact credentials  
     .exec(http(req63)
        .get(endpoint + "${DEVICE_ID_REQUEST62}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST62}"))
        .check(jsonPath("$..status").is(newDeviceStatus))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(newDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("Yes"))
        .check(jsonPath("$..snmpPrivateProtocol").is(newDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("2c"))
        .check(jsonPath("$..snmpAuthType").is(newDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(newDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString + "1"))
        //.check(jsonPath("$..externalIp").is("127.0.23.12"))       
        //.check(jsonPath("$..physicalIp").is("127.0.23.13")) 
        .check(jsonPath("$..osModel").is(newDeviceOsModel))
        .check(jsonPath("$..vendorId").is(newDeviceVendorId))
        .check(jsonPath("$..hardwareModel").is(newDeviceHardwareModel))
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(newDeviceSiteId))
        .check(jsonPath("$..siteName").is(newDeviceSiteName))
        .check(jsonPath("$..notes").is(newDeviceNotes))
        .check(jsonPath("$..customerHostName").is(newDeviceCustomerHostName+ timestampString))
        .check(jsonPath("$..serialNumber").is(newDeviceSerialNumber))     
        .check(jsonPath("$..notes").saveAs("customerNotesToUpdate"))
        .check(jsonPath("$..serialNumber").saveAs("serialNumberToUpdate"))
        .check(jsonPath("$..siteId").saveAs("siteIdToUpdate"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js63))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js63)) {
        exec( session => {
          session.set(js63, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     //Negative: PATCH: Update ticket with no customerId in the payload using contact credentials
    .exec(http(req64)
        .patch(endpoint + "${DEVICE_ID_REQUEST62}")
        .basicAuth(contactUser, contactPass)
        .header("Content-Type","application/json")
        .body(RawFileBody(currentDirectory + "/tests/resources/device_ms/" + updateDeviceNoCustomerIdPayload))
        .check(status.is(401))
        .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js64))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js64)) {
        exec( session => {
          session.set(js64, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //GET: Verify ticket has not been updated when using empty customerId and contact credentials
     .exec(http(req65)
        .get(endpoint + "${DEVICE_ID_REQUEST62}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST62}"))
        .check(jsonPath("$..status").is(newDeviceStatus))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(newDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("Yes"))
        .check(jsonPath("$..snmpPrivateProtocol").is(newDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("2c"))
        .check(jsonPath("$..snmpAuthType").is(newDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(newDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString + "1"))
        //.check(jsonPath("$..externalIp").is("127.0.23.12"))       
        //.check(jsonPath("$..physicalIp").is("127.0.23.13")) 
         .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(newDeviceSiteId))
        .check(jsonPath("$..siteName").is(newDeviceSiteName))
        .check(jsonPath("$..notes").is(newDeviceNotes))
        .check(jsonPath("$..customerHostName").is(newDeviceCustomerHostName+ timestampString))
        .check(jsonPath("$..serialNumber").is(newDeviceSerialNumber))     
        .check(jsonPath("$..notes").saveAs("customerNotesToUpdate"))
        .check(jsonPath("$..serialNumber").saveAs("serialNumberToUpdate"))
        .check(jsonPath("$..siteId").saveAs("siteIdToUpdate"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js65))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js65)) {
        exec( session => {
          session.set(js65, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
     //Negative: PATCH: Update ticket with no customerId in the payload using admin credentials
     .exec(http(req66)
        .patch(endpoint + "${DEVICE_ID_REQUEST62}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(RawFileBody(currentDirectory + "/tests/resources/device_ms/" + updateDeviceNoCustomerIdPayload))
        .check(status.is(401))
        .check(jsonPath("$..message").is("Missing customer ownership on record"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js66))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js66)) {
        exec( session => {
          session.set(js66, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     //GET: Verify ticket has not been updated when using empty customerId and admin credentials
     .exec(http(req67)
        .get(endpoint + "${DEVICE_ID_REQUEST62}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST62}"))
        .check(jsonPath("$..status").is(newDeviceStatus))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(newDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("Yes"))
        .check(jsonPath("$..snmpPrivateProtocol").is(newDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("2c"))
        .check(jsonPath("$..snmpAuthType").is(newDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(newDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString + "1"))
        //.check(jsonPath("$..externalIp").is("127.0.23.12"))       
        //.check(jsonPath("$..physicalIp").is("127.0.23.13")) 
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(newDeviceSiteId))
        .check(jsonPath("$..siteName").is(newDeviceSiteName))
        .check(jsonPath("$..notes").is(newDeviceNotes))
        .check(jsonPath("$..customerHostName").is(newDeviceCustomerHostName+ timestampString))
        .check(jsonPath("$..serialNumber").is(newDeviceSerialNumber))     
        .check(jsonPath("$..notes").saveAs("customerNotesToUpdate"))
        .check(jsonPath("$..serialNumber").saveAs("serialNumberToUpdate"))
        .check(jsonPath("$..siteId").saveAs("siteIdToUpdate"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js67))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js67)) {
        exec( session => {
          session.set(js67, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     //Negative: PATCH: Update ticket with different customerId in the payload using admin credentials
     .exec(http(req68)
        .patch(endpoint + "${DEVICE_ID_REQUEST62}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(RawFileBody(currentDirectory + "/tests/resources/device_ms/" + updateDeviceDifferentCustomerIdPayload))
        .check(status.is(400))
        .check(jsonPath("$..rsp").is("Request body contains fields which are not allowed"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js68))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js68)) {
        exec( session => {
          session.set(js68, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     //GET: Verify ticket has not been updated when using different customerId and admin credentials
     .exec(http(req69)
        .get(endpoint + "${DEVICE_ID_REQUEST62}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST62}"))
        .check(jsonPath("$..status").is(newDeviceStatus))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(newDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("Yes"))
        .check(jsonPath("$..snmpPrivateProtocol").is(newDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("2c"))
        .check(jsonPath("$..snmpAuthType").is(newDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(newDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString + "1"))
        //.check(jsonPath("$..externalIp").is("127.0.23.12"))       
        //.check(jsonPath("$..physicalIp").is("127.0.23.13")) 
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(newDeviceSiteId))
        .check(jsonPath("$..siteName").is(newDeviceSiteName))
        .check(jsonPath("$..notes").is(newDeviceNotes))
        .check(jsonPath("$..customerHostName").is(newDeviceCustomerHostName+ timestampString))
        .check(jsonPath("$..serialNumber").is(newDeviceSerialNumber))     
        .check(jsonPath("$..notes").saveAs("customerNotesToUpdate"))
        .check(jsonPath("$..serialNumber").saveAs("serialNumberToUpdate"))
        .check(jsonPath("$..siteId").saveAs("siteIdToUpdate"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js69))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js69)) {
        exec( session => {
          session.set(js69, "Unable to retrieve JSESSIONID for this request")
        })
      }
  }
 }

    object deviceMsExecution3 extends BaseTest{
      import deviceMsVariables._
      var deviceMsChainExecution3 = new ChainBuilder(Nil)
      deviceMsChainExecution3 = {

        //GET list of all Vendors available in the OPS:Device Details schema - Global
        exec(http(req70)
          .get(vendorEndpoint)
          .header("Content-Type","application/json")
          .basicAuth(adUser, adPass)
          .check(status.is(200))
          .check(substring("Akamai").count.is(1))
          .check(substring("Algosec").count.is(1))
          .check(substring("Amazon").count.is(1))
          .check(substring("Arcsight").count.is(1))
          .check(substring("AT&T").count.is(1))
          .check(substring("CentOS").count.is(1))
          .check(substring("Checkpoint").count.is(1))
          .check(substring("Cisco").count.is(1))
          .check(substring("Communication Devices").count.is(1))
          .check(substring("Compaq").count.is(1))
          .check(substring("CrowdStrike").count.is(1))
          .check(substring("Cyberark").count.is(1))
          .check(substring("Cybereason").count.is(1))
          .check(substring("Dell").count.is(1))
          .check(substring("F5").count.is(1))
          .check(substring("Finjan").count.is(1))
          .check(substring("FireEye").count.is(1))
          .check(substring("Firemon").count.is(1))
          .check(substring("Fortinet").count.is(1))
          .check(substring("Fujitsu").count.is(1))
          .check(substring("Generic").count.is(1))
          .check(substring("HP").count.is(1))
          .check(substring("IBM").count.is(1))
          .check(substring("Juniper").count.is(1))
          .check(substring("Lenovo").count.is(1))
          .check(substring("McAfee").count.is(1))
          .check(substring("Microsoft").count.is(1))
          .check(substring("Nozomi").count.is(1))
          .check(substring("Oracle").count.is(1))
          .check(substring("Palo Alto").count.is(1))
          .check(substring("Qualys").count.is(1))
          .check(substring("Rapid7").count.is(1))
          .check(substring("ReaQta").count.is(1))
          .check(substring("Red Hat").count.is(1))
          .check(substring("SUSE").count.is(1))
          .check(substring("Splunk").count.is(1))
          .check(substring("Sun").count.is(1))
          .check(substring("Symantec").count.is(1))
          .check(substring("TokenGuard").count.is(1))
          .check(substring("TrendMicro").count.is(1))
          .check(substring("Ubuntu").count.is(1))
          .check(substring("VMware").count.is(1))
          .check(substring("Zscaler").count.is(1))
          .check(checkIf(environment != "PRD"){substring("ReaQta").count.is(1)})
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js70))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js70)) {
         exec( session => {
          session.set(js70, "Unable to retrieve JSESSIONID for this request")
         })
        }

          //GET list of all Vendors available in the OPS:Device Details schema - QA Customer
          .exec(http(req71)
          .get(vendorEndpoint)
          .header("Content-Type","application/json")
          .basicAuth(contactUser, contactPass)
          .check(status.is(200))
          .check(substring("Akamai").count.is(1))
          .check(substring("Algosec").count.is(1))
          .check(substring("Amazon").count.is(1))
          .check(substring("Arcsight").count.is(1))
          .check(substring("AT&T").count.is(1))
          .check(substring("CentOS").count.is(1))
          .check(substring("Checkpoint").count.is(1))
          .check(substring("Cisco").count.is(1))
          .check(substring("Communication Devices").count.is(1))
          .check(substring("Compaq").count.is(1))
          .check(substring("CrowdStrike").count.is(1))
          .check(substring("Cyberark").count.is(1))
          .check(substring("Cybereason").count.is(1))
          .check(substring("Dell").count.is(1))
          .check(substring("F5").count.is(1))
          .check(substring("Finjan").count.is(1))
          .check(substring("FireEye").count.is(1))
          .check(substring("Firemon").count.is(1))
          .check(substring("Fortinet").count.is(1))
          .check(substring("Fujitsu").count.is(1))
          .check(substring("Generic").count.is(1))
          .check(substring("HP").count.is(1))
          .check(substring("IBM").count.is(1))
          .check(substring("Juniper").count.is(1))
          .check(substring("Lenovo").count.is(1))
          .check(substring("McAfee").count.is(1))
          .check(substring("Microsoft").count.is(1))
          .check(substring("Nozomi").count.is(1))
          .check(substring("Oracle").count.is(1))
          .check(substring("Palo Alto").count.is(1))
          .check(substring("Qualys").count.is(1))
          .check(substring("Rapid7").count.is(1))
          .check(substring("ReaQta").count.is(1))
          .check(substring("Red Hat").count.is(1))
          .check(substring("SUSE").count.is(1))
          .check(substring("Splunk").count.is(1))
          .check(substring("Sun").count.is(1))
          .check(substring("Symantec").count.is(1))
          .check(substring("TokenGuard").count.is(1))
          .check(substring("TrendMicro").count.is(1))
          .check(substring("Ubuntu").count.is(1))
          .check(substring("VMware").count.is(1))
          .check(substring("Zscaler").count.is(1))
          .check(checkIf(environment != "PRD"){substring("ReaQta").count.is(1)})
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js71))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js71)) {
         exec( session => {
          session.set(js71, "Unable to retrieve JSESSIONID for this request")
         })
        }

          //GET negative scenario - Wrong Credentials
          .exec(http(req72)
          .get(vendorEndpoint)
          .header("Content-Type","application/json")
          .basicAuth(contactUser, "wrongPass")
          .check(status.is(401))
          .check(jsonPath("$..code").is("401"))
          .check(jsonPath("$..message").is("Unauthenticated"))
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js72))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js72)) {
         exec( session => {
          session.set(js72, "Unable to retrieve JSESSIONID for this request")
         })
        }
     
     //PATCH: Update customerNotes using contact credentials
     .exec(http(req73)
        .patch(endpoint + deviceIDQACustomer)
        .basicAuth(contactUser, contactPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerNotes\": \"customerNotesUpdate" + timestampString + "\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is(deviceIDQACustomer))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js73))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js73)) {
        exec( session => {
          session.set(js73, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     //GET: Check customerNotes updated successfully using contact credentials
     .exec(http(req74)
        .get(endpoint + deviceIDQACustomer)
        .basicAuth(contactUser, contactPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is(deviceIDQACustomer))
        .check(jsonPath("$..customerNotes").is("customerNotesUpdate" + timestampString))      
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js74))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js74)) {
        exec( session => {
          session.set(js74, "Unable to retrieve JSESSIONID for this request")
        })
      }     
     
     //Negative - POST - Try to create a new device using invalid vendorId and valid hardwareModel 
    .exec(http(req75)
        .post(endpoint)
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody(
            "{"
	        + "\"serialNumber\": \"" + newDeviceSerialNumber + "\","
	        + "\"osModel\": \"" + newDeviceOsModel + "\","
	        + "\"lmsAggregatorIpAddr\": \"" + newDeviceLmsAggregatorIpAddr + "\","
	        + "\"applicationServerIp\": \"" + newDeviceApplicationServerIp + "\","
	        + "\"hostName\": \"" + newDeviceHostName + "" + timestampString + "2" + "\","
	        + "\"logRetentionPeriod\": \"" + newDeviceLogRetentionPeriod + "\","
	        + "\"monitoredBy\": \"" + newDeviceMonitoredBy + "\","
	        + "\"siteId\": \"" + newDeviceSiteId + "\","
	        + "\"partnerId\": \"" + newDevicePartnerId + "\","
	        + "\"partnerName\": \"" + newDevicePartnerName + "\","
	        + "\"customerId\": \"" + newDeviceCustomerId + "\","
	        + "\"customerName\": \"" + newDeviceCustomerName + "\","
	        + "\"eventCollectorIp\": \"" + newDeviceEventCollectorIp + "\","
	        + "\"sensorName\": \"" + newDeviceSensorName + "\","
	        + "\"autodetSensorName\": \"" + newDeviceAutodetSensorName + "\","
	        + "\"installedMemory\": \"" + newDeviceInstalledMemory + "\","
	        + "\"status\": \"" + newDeviceStatus + "\","
	        + "\"notes\": \"" + newDeviceNotes + "\","
	        + "\"snmpContextName\": \"" + newDeviceSnmpContextName + "\","
	        + "\"snmpEnabled\": \"" + newDeviceSnmpEnabled + "\","
	        + "\"snmpPrivateProtocol\": \"" + newDeviceSnmpPrivateProtocol + "\","
	        + "\"snmpVersion\": \"" + newDeviceSnmpVersion + "\","
	        + "\"snmpAuthType\": \"" + newDeviceSnmpAuthType + "\","
	        + "\"snmpEngineId\": \"" + newDeviceSnmpEngineId + "\","
	        + "\"vendorId\": \"" + "invalid" + "\","
	        + "\"hardwareModel\": \"" + newDeviceHardwareModel + "\","
	        + "\"customerHostName\": \"" + newDeviceCustomerHostName + "" + timestampString +  "2" + "\""
          + "  }"
          ))
        .check(status.is(400))
        .check(jsonPath("$..id").notExists)
        .check(jsonPath("$..code").is("400"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js75))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js75)) {
        exec( session => {
          session.set(js75, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
      //Negative - POST - Try to create a new device using valid vendorId and invalid hardwareModel 
      .exec(http(req76)
        .post(endpoint)
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody(
            "{"
	        + "\"serialNumber\": \"" + newDeviceSerialNumber + "\","
	        + "\"osModel\": \"" + newDeviceOsModel + "\","
	        + "\"lmsAggregatorIpAddr\": \"" + newDeviceLmsAggregatorIpAddr + "\","
	        + "\"applicationServerIp\": \"" + newDeviceApplicationServerIp + "\","
	        + "\"hostName\": \"" + newDeviceHostName + "" + timestampString +  "3" + "\","
	        + "\"logRetentionPeriod\": \"" + newDeviceLogRetentionPeriod + "\","
	        + "\"monitoredBy\": \"" + newDeviceMonitoredBy + "\","
	        + "\"siteId\": \"" + newDeviceSiteId + "\","
	        + "\"partnerId\": \"" + newDevicePartnerId + "\","
	        + "\"partnerName\": \"" + newDevicePartnerName + "\","
	        + "\"customerId\": \"" + newDeviceCustomerId + "\","
	        + "\"customerName\": \"" + newDeviceCustomerName + "\","
	        + "\"eventCollectorIp\": \"" + newDeviceEventCollectorIp + "\","
	        + "\"sensorName\": \"" + newDeviceSensorName + "\","
	        + "\"autodetSensorName\": \"" + newDeviceAutodetSensorName + "\","
	        + "\"installedMemory\": \"" + newDeviceInstalledMemory + "\","
	        + "\"status\": \"" + newDeviceStatus + "\","
	        + "\"notes\": \"" + newDeviceNotes + "\","
	        + "\"snmpContextName\": \"" + newDeviceSnmpContextName + "\","
	        + "\"snmpEnabled\": \"" + newDeviceSnmpEnabled + "\","
	        + "\"snmpPrivateProtocol\": \"" + newDeviceSnmpPrivateProtocol + "\","
	        + "\"snmpVersion\": \"" + newDeviceSnmpVersion + "\","
	        + "\"snmpAuthType\": \"" + newDeviceSnmpAuthType + "\","
	        + "\"snmpEngineId\": \"" + newDeviceSnmpEngineId + "\","
	        + "\"vendorId\": \"" + newDeviceVendorId + "\","
	        + "\"hardwareModel\": \"" + "invalid" + "\","
	        + "\"customerHostName\": \"" + newDeviceCustomerHostName + "" + timestampString +  "3" + "\""
          + "  }"
          ))
        .check(status.is(400))
        .check(jsonPath("$..id").notExists)
        .check(jsonPath("$..code").is("400"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js76))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js76)) {
        exec( session => {
          session.set(js76, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Negative - POST - Try to create a new device using invalid osModel 
      .exec(http(req77)
        .post(endpoint)
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody(
            "{"
	        + "\"serialNumber\": \"" + newDeviceSerialNumber + "\","
	        + "\"osModel\": \"" + "invalid" + "\","
	        + "\"lmsAggregatorIpAddr\": \"" + newDeviceLmsAggregatorIpAddr + "\","
	        + "\"applicationServerIp\": \"" + newDeviceApplicationServerIp + "\","
	        + "\"hostName\": \"" + newDeviceHostName + "" + timestampString +  "3" + "\","
	        + "\"logRetentionPeriod\": \"" + newDeviceLogRetentionPeriod + "\","
	        + "\"monitoredBy\": \"" + newDeviceMonitoredBy + "\","
	        + "\"siteId\": \"" + newDeviceSiteId + "\","
	        + "\"partnerId\": \"" + newDevicePartnerId + "\","
	        + "\"partnerName\": \"" + newDevicePartnerName + "\","
	        + "\"customerId\": \"" + newDeviceCustomerId + "\","
	        + "\"customerName\": \"" + newDeviceCustomerName + "\","
	        + "\"eventCollectorIp\": \"" + newDeviceEventCollectorIp + "\","
	        + "\"sensorName\": \"" + newDeviceSensorName + "\","
	        + "\"autodetSensorName\": \"" + newDeviceAutodetSensorName + "\","
	        + "\"installedMemory\": \"" + newDeviceInstalledMemory + "\","
	        + "\"status\": \"" + newDeviceStatus + "\","
	        + "\"notes\": \"" + newDeviceNotes + "\","
	        + "\"snmpContextName\": \"" + newDeviceSnmpContextName + "\","
	        + "\"snmpEnabled\": \"" + newDeviceSnmpEnabled + "\","
	        + "\"snmpPrivateProtocol\": \"" + newDeviceSnmpPrivateProtocol + "\","
	        + "\"snmpVersion\": \"" + newDeviceSnmpVersion + "\","
	        + "\"snmpAuthType\": \"" + newDeviceSnmpAuthType + "\","
	        + "\"snmpEngineId\": \"" + newDeviceSnmpEngineId + "\","
	        + "\"vendorId\": \"" + newDeviceVendorId + "\","
	        + "\"hardwareModel\": \"" + newDeviceHardwareModel + "\","
	        + "\"customerHostName\": \"" + newDeviceCustomerHostName + "" + timestampString +  "3" + "\""
          + "  }"
          ))
        .check(status.is(400))
        .check(jsonPath("$..id").notExists)
        .check(jsonPath("$..code").is("400"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js77))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js77)) {
        exec( session => {
          session.set(js77, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Negative - PUT - Try to update device using invalid vendorId and valid hardwareModel
      .exec(http(req78)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId + "\",\"vendorId\":\"Invalid\",\"hardwareModel\":\"FWSM\"}"))
        .check(status.is(400))
        .check(jsonPath("$..id").notExists)
        .check(jsonPath("$..code").is("400"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js78))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js78)) {
        exec( session => {
          session.set(js78, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
     //GET - Verify existing device values have not been updated 
     .exec(http(req79)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..status").is("Inactive"))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(updateDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("No"))
        .check(jsonPath("$..snmpPrivateProtocol").is(updateDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("3"))
        .check(jsonPath("$..snmpAuthType").is(updateDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(updateDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString))     
        .check(jsonPath("$..osModel").is(updateDeviceOsModel))
        .check(jsonPath("$..vendorId").is(updateDeviceVendorId))
        .check(jsonPath("$..hardwareModel").is(updateDeviceHardwareModel))
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(updateDeviceSiteId))
        .check(jsonPath("$..notes").is(updateDeviceNotes))
        .check(jsonPath("$..customerHostName").is(updateDeviceCustomerHostName))
        .check(jsonPath("$..eventCollectorIp").is(updateDeviceEventCollectorIp))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js79))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js79)) {
        exec( session => {
          session.set(js79, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     //Negative - PUT - Try to update device using valid vendorId and invalid hardwareModel
      .exec(http(req80)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId + "\",\"vendorId\":\"SN00011021\",\"hardwareModel\":\"Invalid\"}"))
        .check(status.is(400))
        .check(jsonPath("$..id").notExists)
        .check(jsonPath("$..code").is("400"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js80))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js80)) {
        exec( session => {
          session.set(js80, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
     //GET - Verify existing device values have not been updated 
     .exec(http(req81)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..status").is("Inactive"))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(updateDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("No"))
        .check(jsonPath("$..snmpPrivateProtocol").is(updateDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("3"))
        .check(jsonPath("$..snmpAuthType").is(updateDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(updateDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString))    
        .check(jsonPath("$..osModel").is(updateDeviceOsModel))
        .check(jsonPath("$..vendorId").is(updateDeviceVendorId))
        .check(jsonPath("$..hardwareModel").is(updateDeviceHardwareModel))
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(updateDeviceSiteId))
        .check(jsonPath("$..notes").is(updateDeviceNotes))
        .check(jsonPath("$..customerHostName").is(updateDeviceCustomerHostName))
        .check(jsonPath("$..eventCollectorIp").is(updateDeviceEventCollectorIp))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js81))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js81)) {
        exec( session => {
          session.set(js81, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     //Negative - PUT - Try to update device using invalid osModel
      .exec(http(req82)
        .put(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId + "\",\"osModel\":\"Invalid\"}"))
        .check(status.is(400))
        .check(jsonPath("$..id").notExists)
        .check(jsonPath("$..code").is("400"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js82))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js82)) {
        exec( session => {
          session.set(js82, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
     //GET - Verify existing device values have not been updated  
     .exec(http(req83)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..status").is("Inactive"))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(updateDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("No"))
        .check(jsonPath("$..snmpPrivateProtocol").is(updateDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("3"))
        .check(jsonPath("$..snmpAuthType").is(updateDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(updateDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString))     
        .check(jsonPath("$..osModel").is(updateDeviceOsModel))
        .check(jsonPath("$..vendorId").is(updateDeviceVendorId))
        .check(jsonPath("$..hardwareModel").is(updateDeviceHardwareModel))
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(updateDeviceSiteId))
        .check(jsonPath("$..notes").is(updateDeviceNotes))
        .check(jsonPath("$..customerHostName").is(updateDeviceCustomerHostName))
        .check(jsonPath("$..eventCollectorIp").is(updateDeviceEventCollectorIp))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js83))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js83)) {
        exec( session => {
          session.set(js83, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     // Deactivate the device created in step 62
    .exec(http(req84)
        .put(endpoint + "${DEVICE_ID_REQUEST62}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"customerId\":\"" + customerId + "\",\"status\":\"Inactive\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST62}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js84))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js84)) {
        exec( session => {
          session.set(js84, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    // Delete IP addresses from device
    .exec(http(req85)
        .patch(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{"
	                        + "\"customerId\": \"" + customerId + "\","
                          + "\"externalIp\": \"\","
                          + "\"physicalIp\": \"\","
                          + "\"secondaryExternalIp\": \"\","
                          + "\"secondaryPhysicalIp\": \"\"," 
	                        + "\"defaultGateway\": \"\","
	                        + "\"virtualIp\": \"\"" 
                          + "}"
                          ))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js85))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js85)) {
        exec( session => {
          session.set(js85, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //GET - Check IP addresses deleted from device
     .exec(http(req86)
        .get(endpoint + "${DEVICE_ID_REQUEST09}")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${DEVICE_ID_REQUEST09}"))
        .check(jsonPath("$..status").is("Inactive"))
        .check(jsonPath("$..customerId").is(newDeviceCustomerId))
        .check(jsonPath("$..partnerId").is(newDevicePartnerId))
        .check(jsonPath("$..partnerName").is(newDevicePartnerName))
        .check(jsonPath("$..customerName").is(newDeviceCustomerName))
        .check(jsonPath("$..snmpContextName").is(updateDeviceSnmpContextName))
        .check(jsonPath("$..snmpEnabled").is("No"))
        .check(jsonPath("$..snmpPrivateProtocol").is(updateDeviceSnmpPrivateProtocol))
        .check(jsonPath("$..snmpVersion").is("3"))
        .check(jsonPath("$..snmpAuthType").is(updateDeviceSnmpAuthType))
        .check(jsonPath("$..snmpEngineId").is(updateDeviceSnmpEngineId))
        .check(jsonPath("$..lmsAggregatorIpAddr").is(newDeviceLmsAggregatorIpAddr))
        .check(jsonPath("$..hostName").is(newDeviceHostName + timestampString))
        .check(jsonPath("$..externalIp").notExists)       
        .check(jsonPath("$..physicalIp").notExists)   
        .check(jsonPath("$..secondaryExternalIp").notExists)       
        .check(jsonPath("$..secondaryPhysicalIp").notExists) 
        .check(jsonPath("$..defaultGateway").notExists)       
        .check(jsonPath("$..virtualIp").notExists)  
        .check(jsonPath("$..osModel").is(updateDeviceOsModel))
        .check(jsonPath("$..vendorId").is(updateDeviceVendorId))
        .check(jsonPath("$..hardwareModel").is(updateDeviceHardwareModel))
        .check(jsonPath("$..logRetentionPeriod").is(newDeviceLogRetentionPeriod))
        .check(jsonPath("$..monitoredBy").is(newDeviceMonitoredBy))
        .check(jsonPath("$..siteId").is(updateDeviceSiteId))
        .check(jsonPath("$..notes").is(updateDeviceNotes))
        .check(jsonPath("$..customerHostName").is(updateDeviceCustomerHostName))
        .check(jsonPath("$..eventCollectorIp").is(updateDeviceEventCollectorIp))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js86))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js86)) {
        exec( session => {
          session.set(js86, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
     //GET - Check no error when limit=1000
     .exec(http(req87)
        .get(endpoint + "?limit=1000")
        .basicAuth(adUser,adPass)
        .header("Content-Type","application/json")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$..id").count.lte(1000))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js87))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js87)) {
        exec( session => {
          session.set(js87, "Unable to retrieve JSESSIONID for this request")
        })
      }

     
      //Exporting all jsession ids
      .exec( session => {
        jsessionMap += (req01 -> session(js01).as[String])
        jsessionMap += (req02 -> session(js02).as[String])
        jsessionMap += (req03 -> session(js03).as[String])
        jsessionMap += (req04 -> session(js04).as[String])
        jsessionMap += (req05 -> session(js05).as[String])
        jsessionMap += (req06 -> session(js06).as[String])
        jsessionMap += (req07 -> session(js07).as[String])
        jsessionMap += (req08 -> session(js08).as[String])
        jsessionMap += (req09 -> session(js09).as[String])
        jsessionMap += (req10 -> session(js10).as[String])
        jsessionMap += (req11 -> session(js11).as[String])
        jsessionMap += (req12 -> session(js12).as[String])
        jsessionMap += (req13 -> session(js13).as[String])
        jsessionMap += (req14 -> session(js14).as[String])
        jsessionMap += (req15 -> session(js15).as[String])
        //jsessionMap += (req16 -> session(js16).as[String])
        jsessionMap += (req17 -> session(js17).as[String])
        jsessionMap += (req18 -> session(js18).as[String])
        jsessionMap += (req19 -> session(js19).as[String])
        jsessionMap += (req20 -> session(js20).as[String])
        jsessionMap += (req21 -> session(js21).as[String])
        jsessionMap += (req22 -> session(js22).as[String])
        jsessionMap += (req23 -> session(js23).as[String])
        jsessionMap += (req24 -> session(js24).as[String])
        jsessionMap += (req25 -> session(js25).as[String])
        jsessionMap += (req26 -> session(js26).as[String])
        jsessionMap += (req27 -> session(js27).as[String])
        jsessionMap += (req28 -> session(js28).as[String])
        jsessionMap += (req29 -> session(js29).as[String])
        jsessionMap += (req30 -> session(js30).as[String])
        jsessionMap += (req31 -> session(js31).as[String])
        jsessionMap += (req32 -> session(js32).as[String])
        jsessionMap += (req33 -> session(js33).as[String])
        //jsessionMap += (req34 -> session(js34).as[String])
        //jsessionMap += (req35 -> session(js35).as[String])
        //jsessionMap += (req36 -> session(js36).as[String])
        //jsessionMap += (req37 -> session(js37).as[String])
        jsessionMap += (req38 -> session(js38).as[String])
        jsessionMap += (req39 -> session(js39).as[String])
        jsessionMap += (req40 -> session(js40).as[String])
        jsessionMap += (req41 -> session(js41).as[String])
        jsessionMap += (req42 -> session(js42).as[String])
        jsessionMap += (req43 -> session(js43).as[String])
        jsessionMap += (req44 -> session(js44).as[String])
        jsessionMap += (req45 -> session(js45).as[String])
        jsessionMap += (req46 -> session(js46).as[String])
        jsessionMap += (req47 -> session(js47).as[String])
        jsessionMap += (req48 -> session(js48).as[String])
        //jsessionMap += (req49 -> session(js49).as[String])
        //jsessionMap += (req50 -> session(js50).as[String])
        jsessionMap += (req51 -> session(js51).as[String])
        jsessionMap += (req52 -> session(js52).as[String])
        jsessionMap += (req53 -> session(js53).as[String])
        jsessionMap += (req54 -> session(js54).as[String])
        jsessionMap += (req55 -> session(js55).as[String])
        jsessionMap += (req56 -> session(js56).as[String])
        jsessionMap += (req57 -> session(js57).as[String])
        jsessionMap += (req58 -> session(js58).as[String])
        jsessionMap += (req59 -> session(js59).as[String])
        jsessionMap += (req60 -> session(js60).as[String])
        jsessionMap += (req61 -> session(js61).as[String])
        jsessionMap += (req62 -> session(js62).as[String])
        jsessionMap += (req63 -> session(js63).as[String])
        jsessionMap += (req64 -> session(js64).as[String])
        jsessionMap += (req65 -> session(js65).as[String])
        jsessionMap += (req66 -> session(js66).as[String])
        jsessionMap += (req67 -> session(js67).as[String])
        jsessionMap += (req68 -> session(js68).as[String])
        jsessionMap += (req69 -> session(js69).as[String])
        jsessionMap += (req70 -> session(js70).as[String])
        jsessionMap += (req71 -> session(js71).as[String])
        jsessionMap += (req72 -> session(js72).as[String])
        jsessionMap += (req73 -> session(js73).as[String])
        jsessionMap += (req74 -> session(js74).as[String])
        jsessionMap += (req75 -> session(js75).as[String])
        jsessionMap += (req76 -> session(js76).as[String])
        jsessionMap += (req77 -> session(js77).as[String])
        jsessionMap += (req78 -> session(js78).as[String])
        jsessionMap += (req79 -> session(js79).as[String])
        jsessionMap += (req80 -> session(js80).as[String])
        jsessionMap += (req81 -> session(js81).as[String])
        jsessionMap += (req82 -> session(js82).as[String])
        jsessionMap += (req83 -> session(js83).as[String])
        jsessionMap += (req84 -> session(js84).as[String])
        jsessionMap += (req85 -> session(js85).as[String])
        jsessionMap += (req86 -> session(js86).as[String])
        jsessionMap += (req87 -> session(js87).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })   
  }
  
 }

 class DeviceMs extends BaseTest {
   import deviceMsVariables._
   import deviceMsExecution1._
   import deviceMsExecution2._
   import deviceMsExecution3._
    val scn = scenario("DeviceMs")
    .exec(deviceMsChainExecution1,deviceMsChainExecution2,deviceMsChainExecution3);
   //.exec(deviceMsChainExecution3);
    setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpProtocolNoBasicAuth).assertions(global.failedRequests.count.is(0))

 }