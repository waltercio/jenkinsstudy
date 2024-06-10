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
import java.time.Instant
import io.gatling.core.structure.ChainBuilder

/**
 *  Developed by: wobc
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-11615
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8704
 *  Updates from https://jira.sec.ibm.com/browse/XPS-152147 for functional (functional does not have checkedOut,checkOutUserId,checkOutUserDisplayName in response)
 *  Updates from https://jira.sec.ibm.com/browse/XPS-152147 - PhisicalIp is only returned/used for Dynamic and Functional credentials
 */


object variables1{
  
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
  import java.time.Instant
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val adRaw = System.getenv("AD_USER")
  val adUser = adRaw.replace("/", "\\")
  val adPass = System.getenv("AD_PASS")
  val auraAdminPass = System.getenv("AURA_ADMIN_PASS")

  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new 
    Exception().getStackTrace.head.getFileName.split("\\.scala")(0) + ".json"
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val globalConfiguration = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/configuration_global.json").getLines().mkString)
  val auraCredentialsValues = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/aura_credentials_ms.json").getLines().mkString)  
  val privateConfiguration = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/aura_credentials_ms_configuration.json").getLines().mkString)

  val req1="GET - Fetch all Customer Credentials Secrets from Dynamic folder"
  val req2="Negative: GET - Fetch all Customer Credentials Secrets from Dynamic folder using controlled combination"
  val req3="Negative: GET - Fetch all Customer Credentials Secrets from Dynamic folder using functional combination"
  val req4="GET - Fetch all Customer Credentials Secrets from Controlled folder"
  val req5="Negative: GET - Fetch all Customer Credentials Secrets from Controlled folder using Dynamic combination"
  val req6="Negative: GET - Fetch all Customer Credentials Secrets from Controlled folder using Functional combination"
  val req7="GET - Fetch all Customer Credentials Secrets from Functional folder"
  val req8="Negative: GET - Fetch all Customer Credentials Secrets from Functional folder using Dynamic combination"
  val req9="Negative: GET - Fetch all Customer Credentials Secrets from Functional folder using Controlled combination"
  val req10="GET - Fetch all Customer Credentials Secrets with limit = 20"
  val req11="GET - Fetch all Customer Credentials Secrets with limit = 100"
  val req12="Negative: GET - Fetch all Customer Credentials Secrets with limit > 100"
  val req13="POST - Create a new Customer Controlled Credential"
  val req14="POST - Create a new Customer Dynamic Credential"
  val req15="POST - Create a new Customer Functional Credential"
  val req16="GET - Fetch newly created Customer Dynamic Credential Using DeviceId and Credential Name"
  val req17="GET - Fetch newly created Customer Controlled Credential Using DeviceId and Credential Name"
  val req18="GET - Fetch newly created Customer Functional Credential Using DeviceId and Credential Name"
  val req19="Negative: POST - Create another Customer Credential with the same deviceID and credentialName"
  val req20="Negative - GET credential by deviceId and credentialName using invalid credentialName and valid deviceId"
  val req21="Negative - GET credential by deviceId and credentialName using valid credentialName and invalid deviceId"
  val req22="PUT - Update the Customer Credential previously created"
  val req23="GET - Fetch details of updated Customer Dynamic Credential Using DeviceId and Credential Name"
  val req24="POST - Create a 2nd Customer Dynamic Credential in the same folder"
  val req25="PUT - Negative: Update an existing credential using a different existing login name"
  val req26="GET - Fetch details dynamic credential by device id"
  val req27="GET - Fetch details controlled credential by device id"
  val req28="POST - Check-out credential"
  val req29="GET - Checking credentials check-out out ok"
  val req30="Negative POST - Check-out already checked-out credential"
  val req31="GET - Checking credentials still checkout-out ok after negative scenario"
  val req32="POST - Check-in credential"
  val req33="GET - Checking credentials check-in ok"
  val req34="Negative - POST - Check-in already checked-in credential"
  val req35="GET - Checking credentials still checked-in ok after negative scenario"
  val req36="DELETE - Deleting the 1st credential created"
  val req37="DELETE - Deleting the 2nd credential created"
  val req38="DELETE - Deleting the 3rd credential created"
  val req39="DELETE - Deleting the 4th credential created"
  val req40="GET - Check 1st credential has been deleted"
  val req41="GET - Check 2nd credential has been deleted"
  val req42="GET - Check 3rd credential has been deleted"
  val req43="GET - Check 4th credential has been deleted"
  val req44="DELETE - Negative - Try to Delete the same credentials previously deleted"
  val req45="GET - Security - Fetch all Customer Credentials Secrets using valid username and invalid password"
  val req46="GET - Security - Fetch all Customer Credentials Secrets using invalid username and valid password"
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  val js9 = "jsession9"
  val js10 = "jsession10"
  val js11 = "jsession11"
  val js12 = "jsession12"
  val js13 = "jsession13"
  val js14 = "jsession14"
  val js15 = "jsession15"
  val js16 = "jsession16"
  val js17 = "jsession17"
  val js18 = "jsession18"
  val js19 = "jsession19"
  val js20 = "jsession20"
  val js21 = "jsession21"
  val js22 = "jsession22"
  val js23 = "jsession23"
  val js24 = "jsession24"
  val js25 = "jsession25"
  val js26 = "jsession26"
  val js27 = "jsession27"
  val js28 = "jsession28"
  val js29 = "jsession29"
  val js30 = "jsession30"
  val js31 = "jsession31"
  val js32 = "jsession32"
  val js33 = "jsession33"
  val js34 = "jsession34"
  val js35 = "jsession35"
  val js36 = "jsession36"
  val js37 = "jsession37"
  val js38 = "jsession38"
  val js39 = "jsession39"
  val js40 = "jsession40"
  val js41 = "jsession41"
  val js42 = "jsession42"
  val js43 = "jsession43"
  val js44 = "jsession44"
  val js45 = "jsession45"
  val js46 = "jsession46"
  
  val baseUrl = (globalConfiguration \\ "auraCredentialsBaseUrl" \\ environment).extract[String]
  val dynamicFolder = (auraCredentialsValues \\ "dynamicFolder" \\ environment).extract[String]
  val controlledFolder = (auraCredentialsValues \\ "controlledFolder" \\ environment).extract[String]
  val functionalFolder = (auraCredentialsValues \\ "functionalFolder" \\ environment).extract[String]
  val folderLimit20 = (auraCredentialsValues \\ "folderLimit20" \\ environment).extract[String]
  val unixTimestamp = Instant.now.getEpochSecond
  
  //Getting Controlled credentials values from json and generating the payload
  var newControlledCredentialPayloadFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/payloadNewControlledCredential.json").getLines().mkString)
  var controlledNextPassword = (newControlledCredentialPayloadFile \\ "NextPassword").extract[String]
  var controlledNextPreviousPassword = (newControlledCredentialPayloadFile \\ "PreviousPassword").extract[String]
  var controlledStatus = (newControlledCredentialPayloadFile \\ "Status").extract[String]
  var controlledITSMId = (newControlledCredentialPayloadFile \\ "ITSMId").extract[String]
  var controlledSecretTemplateId = (auraCredentialsValues \\ "createNewControlledCredentialTemplateId" \\ environment).extract[String]
  var controlledFolderId= (auraCredentialsValues \\ "createNewControlledCredentialFolderId" \\ environment).extract[String]
  var controlledAutoChangeEnabled = (newControlledCredentialPayloadFile \\ "autoChangeEnabled").extract[String]
  var controlledCheckOutEnabled = (newControlledCredentialPayloadFile \\ "checkOutEnabled").extract[String]
  var controlledHostName = (newControlledCredentialPayloadFile \\ "HostName").extract[String]
  var controlledLoginName = (newControlledCredentialPayloadFile \\ "LoginName").extract[String]
  var controlledPassword = (newControlledCredentialPayloadFile \\ "Password").extract[String]
  var controlledLoginType = (newControlledCredentialPayloadFile \\ "LoginType").extract[String]
  var controlledCustomerName = (newControlledCredentialPayloadFile \\ "CustomerName").extract[String]
  var controlledCustomerId = (newControlledCredentialPayloadFile \\ "CustomerId").extract[String]
  var controlledExternalIP = (newControlledCredentialPayloadFile \\ "ExternalIP").extract[String]
  var controlledMachinePlatform = (newControlledCredentialPayloadFile \\ "MachinePlatform").extract[String]
  var controlledOperatingSystem = (newControlledCredentialPayloadFile \\ "OperatingSystem").extract[String]
  var controlledDeviceId = (newControlledCredentialPayloadFile \\ "DeviceId").extract[String]
  var controlledLoginId = (newControlledCredentialPayloadFile \\ "LoginId").extract[String]
  
  
  //Getting Dynamic credentials values from json and generating the payload
  var newDynamicCredentialPayloadFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/payloadNewDynamicCredential.json").getLines().mkString)
  var dynamicId = (newDynamicCredentialPayloadFile \\ "id").extract[String]
  var dynamicSecretTemplateId = (auraCredentialsValues \\ "createNewDynamicCredentialTemplateId" \\ environment).extract[String]
  var dynamicFolderId= (auraCredentialsValues \\ "createNewDynamicCredentialFolderId" \\ environment).extract[String]
  var dynamicAutoChangeEnabled = (newDynamicCredentialPayloadFile \\ "autoChangeEnabled").extract[String]
  var dynamicCheckOutEnabled = (newDynamicCredentialPayloadFile \\ "checkOutEnabled").extract[String]
  var dynamicHostName = (newDynamicCredentialPayloadFile \\ "HostName").extract[String]
  var dynamicLoginName = (newDynamicCredentialPayloadFile \\ "LoginName").extract[String]
  var dynamicPassword = (newDynamicCredentialPayloadFile \\ "Password").extract[String]
  var dynamicLoginType = (newDynamicCredentialPayloadFile \\ "LoginType").extract[String]
  var dynamicCustomerName = (newDynamicCredentialPayloadFile \\ "CustomerName").extract[String]
  var dynamicCustomerId = (newDynamicCredentialPayloadFile \\ "CustomerId").extract[String]
  var dynamicExternalIP = (newDynamicCredentialPayloadFile \\ "ExternalIP").extract[String]
  var dynamicMachinePlatform = (newDynamicCredentialPayloadFile \\ "MachinePlatform").extract[String]
  var dynamicOperatingSystem = (newDynamicCredentialPayloadFile \\ "OperatingSystem").extract[String]
  var dynamicDeviceId = (newDynamicCredentialPayloadFile \\ "DeviceId").extract[String]
  var dynamicLoginId = (newDynamicCredentialPayloadFile \\ "LoginId").extract[String]
  var dynamicPhysicalIP = (newDynamicCredentialPayloadFile \\ "PhysicalIP").extract[String]
 
  
  //Getting Functional credentials values from json and generating the payload
  var newFunctionalCredentialPayloadFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/payloadNewFunctionalCredential.json").getLines().mkString)
  var functionalId = (newFunctionalCredentialPayloadFile \\ "id").extract[String]
  var functionalSecretTemplateId = (auraCredentialsValues \\ "createNewFunctionalCredentialTemplateId" \\ environment).extract[String]
  var functionalFolderId= (auraCredentialsValues \\ "createNewFunctionalCredentialFolderId" \\ environment).extract[String]
  var functionalAutoChangeEnabled = (newFunctionalCredentialPayloadFile \\ "autoChangeEnabled").extract[String]
  var functionalCheckOutEnabled = (newFunctionalCredentialPayloadFile \\ "checkOutEnabled").extract[String]
  var functionalHostName = (newFunctionalCredentialPayloadFile \\ "HostName").extract[String]
  var functionalLoginName = (newFunctionalCredentialPayloadFile \\ "LoginName").extract[String]
  var functionalPassword = (newFunctionalCredentialPayloadFile \\ "Password").extract[String]
  var functionalLoginType = (newFunctionalCredentialPayloadFile \\ "LoginType").extract[String]
  var functionalCustomerName = (newFunctionalCredentialPayloadFile \\ "CustomerName").extract[String]
  var functionalCustomerId = (newFunctionalCredentialPayloadFile \\ "CustomerId").extract[String]
  var functionalExternalIP = (newFunctionalCredentialPayloadFile \\ "ExternalIP").extract[String]
  var functionalMachinePlatform = (newFunctionalCredentialPayloadFile \\ "MachinePlatform").extract[String]
  var functionalOperatingSystem = (newFunctionalCredentialPayloadFile \\ "OperatingSystem").extract[String]
  var functionalDeviceId = (newFunctionalCredentialPayloadFile \\ "DeviceId").extract[String]
  var functionalLoginId = (newFunctionalCredentialPayloadFile \\ "LoginId").extract[String]
  var functionalPhysicalIP = (newFunctionalCredentialPayloadFile \\ "PhysicalIP").extract[String]
  
  val httpProtocolAuraCredentialsMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")
  
}

object methods{
  
  import variables1._
  val controlledGeneratedCredential = generateNewControlledCredentialPayload()
  val dynamicGeneratedCredential = generateNewDynamicCredentialPayload()
  val dynamicNewGeneratedCredential = generateNew2ndDynamicCredentialPayload()
  val functionalGeneratedCredential = generateNewFunctionalCredentialPayload()
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))
  def generateNewControlledCredentialPayload() : String = {    
    var newControlledCredentialPayload = 
      "{\n" +
      "\"NextPassword\": \"\",\n" +
      "\"PreviousPassword\": \"\",\n" +
      "\"ITSMId\": \"\",\n" +
      "\"Status\": \"\",\n" +
      "\"secretTemplateId\": " + controlledSecretTemplateId + ",\n" +
      "\"folderId\": " + controlledFolderId + ",\n" +
      "\"autoChangeEnabled\": " + controlledAutoChangeEnabled + ",\n" +
      "\"checkOutEnabled\": " + controlledCheckOutEnabled + ",\n" +
      "\"HostName\": \"" + controlledHostName + "\",\n" +
      "\"LoginName\": \"" + controlledLoginName + "Controlled" + unixTimestamp + "\",\n" +
      "\"Password\": \"" + controlledPassword + "\",\n" +
      "\"LoginType\": \"" + controlledLoginType + "\",\n" +
      "\"CustomerName\": \"" + controlledCustomerName + "\",\n" +
      "\"CustomerId\": \"" + controlledCustomerId + "\",\n" +
      "\"ExternalIP\": \"" + controlledExternalIP + "\",\n" +
      "\"MachinePlatform\": \"" + controlledMachinePlatform + "\",\n" +
      "\"OperatingSystem\": \"" + controlledOperatingSystem + "\",\n" +
      "\"DeviceId\": \"" + controlledDeviceId + "\",\n" +
      "\"LoginId\": \"" + controlledLoginId + "Controlled" + unixTimestamp + "\"\n" +
      "}"
    return newControlledCredentialPayload
  }
  
  def generateNew2ndDynamicCredentialPayload() : String = {
    
    var newDynamicCredentialPayload = 
      "{\n" +
      "\"id\": " + dynamicId + ",\n" +
      "\"secretTemplateId\": " + dynamicSecretTemplateId + ",\n" +
      "\"folderId\": " + dynamicFolderId + ",\n" +
      "\"autoChangeEnabled\": " + dynamicCheckOutEnabled + ",\n" +
      "\"checkOutEnabled\": " + dynamicCheckOutEnabled + ",\n" +
      "\"HostName\": \"" + dynamicHostName + "\",\n" +
      "\"LoginName\": \"" + dynamicLoginName + "DynamicNew" + unixTimestamp + "\",\n" +
      "\"Password\": \"" + dynamicPassword + "\",\n" +
      "\"LoginType\": \"" + dynamicLoginType + "\",\n" +
      "\"CustomerName\": \"" + dynamicCustomerName + "\",\n" +
      "\"CustomerId\": \"" + dynamicCustomerId + "\",\n" +
      "\"ExternalIP\": \"" + dynamicExternalIP + "\",\n" +
      "\"MachinePlatform\": \"" + dynamicMachinePlatform + "\",\n" +
      "\"OperatingSystem\": \"" + dynamicOperatingSystem + "\",\n" +
      "\"DeviceId\": \"" + dynamicDeviceId + "\",\n" +
      "\"PhysicalIP\": \"" + dynamicPhysicalIP + "\",\n" +
      "\"LoginId\": \"" + dynamicLoginId + "DynamicNew" + unixTimestamp + "\"\n" +
      "}"
    return newDynamicCredentialPayload
  }
  
  def generateNewDynamicCredentialPayload() : String = {
    
    var newDynamicCredentialPayload = 
      "{\n" +
      "\"id\": " + dynamicId + ",\n" +
      "\"secretTemplateId\": " + dynamicSecretTemplateId + ",\n" +
      "\"folderId\": " + dynamicFolderId + ",\n" +
      "\"autoChangeEnabled\": " + dynamicCheckOutEnabled + ",\n" +
      "\"checkOutEnabled\": " + dynamicCheckOutEnabled + ",\n" +
      "\"HostName\": \"" + dynamicHostName + "\",\n" +
      "\"LoginName\": \"" + dynamicLoginName + "Dynamic" + unixTimestamp + "\",\n" +
      "\"Password\": \"" + dynamicPassword + "\",\n" +
      "\"LoginType\": \"" + dynamicLoginType + "\",\n" +
      "\"CustomerName\": \"" + dynamicCustomerName + "\",\n" +
      "\"CustomerId\": \"" + dynamicCustomerId + "\",\n" +
      "\"ExternalIP\": \"" + dynamicExternalIP + "\",\n" +
      "\"MachinePlatform\": \"" + dynamicMachinePlatform + "\",\n" +
      "\"OperatingSystem\": \"" + dynamicOperatingSystem + "\",\n" +
      "\"DeviceId\": \"" + dynamicDeviceId + "\",\n" +
      "\"PhysicalIP\": \"" + dynamicPhysicalIP + "\",\n" +
      "\"LoginId\": \"" + dynamicLoginId + "Dynamic" + unixTimestamp + "\"\n" +
      "}"
    return newDynamicCredentialPayload
  }
  
  def generateNewFunctionalCredentialPayload() : String = {
    
    var newFunctionalCredentialPayload = 
      "{\n" +
      "\"id\": " + functionalId + ",\n" +
      "\"secretTemplateId\": " + functionalSecretTemplateId + ",\n" +
      "\"folderId\": " + functionalFolderId + ",\n" +
      "\"autoChangeEnabled\": " + functionalAutoChangeEnabled + ",\n" +
      "\"checkOutEnabled\": " + functionalCheckOutEnabled + ",\n" +
      "\"HostName\": \"" + functionalHostName + "\",\n" +
      "\"LoginName\": \"" + functionalLoginName + "Functional" + unixTimestamp + "\",\n" +
      "\"Password\": \"" + functionalPassword + "\",\n" +
      "\"LoginType\": \"" + functionalLoginType + "\",\n" +
      "\"CustomerName\": \"" + functionalCustomerName + "\",\n" +
      "\"CustomerId\": \"" + functionalCustomerId + "\",\n" +
      "\"ExternalIP\": \"" + functionalExternalIP + "\",\n" +
      "\"MachinePlatform\": \"" + functionalMachinePlatform + "\",\n" +
      "\"OperatingSystem\": \"" + functionalOperatingSystem + "\",\n" +
      "\"DeviceId\": \"" + functionalDeviceId + "\",\n" +
      "\"PhysicalIP\": \"" + functionalPhysicalIP + "\",\n" +
      "\"LoginId\": \"" + functionalLoginId + "Functional" + unixTimestamp + "\"\n" +
      "}"
    return newFunctionalCredentialPayload
  }
  
  def generateUpdateCredentialPayload() : String = {
    var updateDynamicCredentialPayload = 
      "{\n" +
      "\"id\": " + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14}" + ",\n" +
      "\"secretTemplateId\": " + dynamicSecretTemplateId + ",\n" +
      "\"folderId\": " + dynamicFolderId + ",\n" +
      "\"autoChangeEnabled\": " + dynamicCheckOutEnabled + ",\n" +
      "\"checkOutEnabled\": " + dynamicCheckOutEnabled + ",\n" +
      "\"HostName\": \"" + "hostNameUpdate" + "\",\n" +
      "\"LoginName\": \"" + dynamicLoginName + "Dynamic" + unixTimestamp + "\",\n" +
      "\"Password\": \"" + dynamicPassword + "\",\n" +
      "\"LoginType\": \"" + dynamicLoginType + "\",\n" +
      "\"CustomerName\": \"" + dynamicCustomerName + "\",\n" +
      "\"CustomerId\": \"" + dynamicCustomerId + "\",\n" +
      "\"ExternalIP\": \"" + "externalIPUpdate" + "\",\n" +
      "\"MachinePlatform\": \"" + dynamicMachinePlatform + "\",\n" +
      "\"PhysicalIP\": \"" + dynamicPhysicalIP + "\",\n" +
      "\"OperatingSystem\": \"" + dynamicOperatingSystem + "\",\n" +
      "\"DeviceId\": \"" + dynamicDeviceId + "\",\n" +
      "\"LoginId\": \"" + dynamicLoginId + "Dynamic" + unixTimestamp + "\"\n" +
      "}"
    return updateDynamicCredentialPayload
  }
  
  def generateUpdateCredentialPayloadForNegativeTest() : String = {
    var updateDynamicCredentialPayload = 
      "{\n" +
      "\"id\": " + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14}" + ",\n" +
      "\"secretTemplateId\": " + dynamicSecretTemplateId + ",\n" +
      "\"folderId\": " + dynamicFolderId + ",\n" +
      "\"autoChangeEnabled\": " + dynamicCheckOutEnabled + ",\n" +
      "\"checkOutEnabled\": " + dynamicCheckOutEnabled + ",\n" +
      "\"HostName\": \"" + "hostNameUpdateNew" + "\",\n" +
      "\"LoginName\": \"" + dynamicLoginName + "DynamicNew" + unixTimestamp + "\",\n" +
      "\"Password\": \"" + dynamicPassword + "\",\n" +
      "\"LoginType\": \"" + dynamicLoginType + "\",\n" +
      "\"CustomerName\": \"" + dynamicCustomerName + "\",\n" +
      "\"CustomerId\": \"" + dynamicCustomerId + "\",\n" +
      "\"ExternalIP\": \"" + "externalIPUpdateNew" + "\",\n" +
      "\"MachinePlatform\": \"" + dynamicMachinePlatform + "\",\n" +
      "\"OperatingSystem\": \"" + dynamicOperatingSystem + "\",\n" +
      "\"PhysicalIP\": \"" + dynamicPhysicalIP + "\",\n" +
      "\"DeviceId\": \"" + dynamicDeviceId + "\",\n" +
      "\"LoginId\": \"" + dynamicLoginId + "Dynamic" + unixTimestamp + "\"\n" +
      "}"
    return updateDynamicCredentialPayload
  }
  
}

object chainMainExecution1{
  import variables1._
  import methods._
  var chain1 = new ChainBuilder(Nil)
  chain1 = {
    
    // "GET - Fetch all Customer Credentials Secrets from Dynamic folder"
    exec(http(req1)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=true&templateName=MSS%20Cisco%20ASA&folderName=" + dynamicFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..secretTemplateId").exists)
      .check(jsonPath("$..folderId").exists)
      .check(jsonPath("$..HostName").exists)
      .check(jsonPath("$..LoginName").exists)
      .check(jsonPath("$..LoginType").exists)
      .check(jsonPath("$..CustomerName").exists)
      .check(jsonPath("$..CustomerId").exists)
      .check(jsonPath("$..ExternalIP").exists)
      .check(jsonPath("$..MachinePlatform").exists)
      .check(jsonPath("$..OperatingSystem").exists)
      .check(jsonPath("$..DeviceId").exists)
      .check(jsonPath("$..LoginId").exists)
      .check(jsonPath("$..checkedOut").exists)
      .check(jsonPath("$..checkOutUserId").exists)
      .check(jsonPath("$..checkOutUserDisplayName").exists)
      .check(jsonPath("$..PhysicalIP").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
    // "Negative: GET - Fetch all Customer Credentials Secrets from Dynamic folder using Controlled combination"
    .exec(http(req2)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=false&templateName=MSS%20Cisco%20ASA&folderName=" + dynamicFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..secretTemplateId").notExists)
      .check(jsonPath("$..folderId").notExists)
      .check(jsonPath("$..HostName").notExists)
      .check(jsonPath("$..LoginName").notExists)
      .check(jsonPath("$..LoginType").notExists)
      .check(jsonPath("$..CustomerName").notExists)
      .check(jsonPath("$..CustomerId").notExists)
      .check(jsonPath("$..ExternalIP").notExists)
      .check(jsonPath("$..MachinePlatform").notExists)
      .check(jsonPath("$..OperatingSystem").notExists)
      .check(jsonPath("$..DeviceId").notExists)
      .check(jsonPath("$..LoginId").notExists)
      .check(jsonPath("$..checkedOut").notExists)
      .check(jsonPath("$..checkOutUserId").notExists)
      .check(jsonPath("$..checkOutUserDisplayName").notExists)
      .check(jsonPath("$..PhysicalIP").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative: GET - Fetch all Customer Credentials Secrets from Dynamic folder using Functional combination"
    .exec(http(req3)
      .get("credentials?checkOutEnabled=false&autoChangeEnabled=true&templateName=MSS%20Cisco%20ASA&folderName=" + dynamicFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..secretTemplateId").notExists)
      .check(jsonPath("$..folderId").notExists)
      .check(jsonPath("$..HostName").notExists)
      .check(jsonPath("$..LoginName").notExists)
      .check(jsonPath("$..LoginType").notExists)
      .check(jsonPath("$..CustomerName").notExists)
      .check(jsonPath("$..CustomerId").notExists)
      .check(jsonPath("$..ExternalIP").notExists)
      .check(jsonPath("$..MachinePlatform").notExists)
      .check(jsonPath("$..OperatingSystem").notExists)
      .check(jsonPath("$..DeviceId").notExists)
      .check(jsonPath("$..LoginId").notExists)
      .check(jsonPath("$..checkedOut").notExists)
      .check(jsonPath("$..checkOutUserId").notExists)
      .check(jsonPath("$..checkOutUserDisplayName").notExists)
      .check(jsonPath("$..PhysicalIP").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Fetch all Customer Credentials Secrets from Controlled folder"
    .exec(http(req4)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=false&templateName=MSS%20Controlled%20Device%Credential&folderName=" + controlledFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..secretTemplateId").exists)
      .check(jsonPath("$..folderId").exists)
      .check(jsonPath("$..HostName").exists)
      .check(jsonPath("$..LoginName").exists)
      .check(jsonPath("$..LoginType").exists)
      .check(jsonPath("$..CustomerName").exists)
      .check(jsonPath("$..CustomerId").exists)
      .check(jsonPath("$..ExternalIP").exists)
      .check(jsonPath("$..MachinePlatform").exists)
      .check(jsonPath("$..OperatingSystem").exists)
      .check(jsonPath("$..DeviceId").exists)
      .check(jsonPath("$..LoginId").exists)
      .check(jsonPath("$..Status").notExists)
      .check(jsonPath("$..checkedOut").exists)
      .check(jsonPath("$..checkOutUserId").exists)
      .check(jsonPath("$..checkOutUserDisplayName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
    // "Negative: GET - Fetch all Customer Credentials Secrets from Controlled folder using Dynamic combination"
    .exec(http(req5)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=true&templateName=MSS%20Controlled%20Device%20Credential&folderName=" + controlledFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..secretTemplateId").notExists)
      .check(jsonPath("$..folderId").notExists)
      .check(jsonPath("$..HostName").notExists)
      .check(jsonPath("$..LoginName").notExists)
      .check(jsonPath("$..LoginType").notExists)
      .check(jsonPath("$..CustomerName").notExists)
      .check(jsonPath("$..CustomerId").notExists)
      .check(jsonPath("$..ExternalIP").notExists)
      .check(jsonPath("$..MachinePlatform").notExists)
      .check(jsonPath("$..OperatingSystem").notExists)
      .check(jsonPath("$..DeviceId").notExists)
      .check(jsonPath("$..LoginId").notExists)
      .check(jsonPath("$..Status").notExists)
      .check(jsonPath("$..checkedOut").notExists)
      .check(jsonPath("$..checkOutUserId").notExists)
      .check(jsonPath("$..checkOutUserDisplayName").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative: GET - Fetch all Customer Credentials Secrets from Controlled folder using Functional combination"
    .exec(http(req6)
      .get("credentials?checkOutEnabled=false&autoChangeEnabled=true&templateName=MSS%20Controlled%20Device%20Credential&folderName=" + controlledFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..secretTemplateId").notExists)
      .check(jsonPath("$..folderId").notExists)
      .check(jsonPath("$..HostName").notExists)
      .check(jsonPath("$..LoginName").notExists)
      .check(jsonPath("$..LoginType").notExists)
      .check(jsonPath("$..CustomerName").notExists)
      .check(jsonPath("$..CustomerId").notExists)
      .check(jsonPath("$..ExternalIP").notExists)
      .check(jsonPath("$..MachinePlatform").notExists)
      .check(jsonPath("$..OperatingSystem").notExists)
      .check(jsonPath("$..DeviceId").notExists)
      .check(jsonPath("$..LoginId").notExists)
      .check(jsonPath("$..Status").notExists)
      .check(jsonPath("$..checkedOut").notExists)
      .check(jsonPath("$..checkOutUserId").notExists)
      .check(jsonPath("$..checkOutUserDisplayName").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // "GET - Fetch all Customer Credentials Secrets from Functional folder"
    .exec(http(req7)
      .get("credentials?checkOutEnabled=false&autoChangeEnabled=true&templateName=MSS%20Controlled%20Device%Credential&folderName=" + functionalFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..secretTemplateId").exists)
      .check(jsonPath("$..folderId").exists)
      .check(jsonPath("$..HostName").exists)
      .check(jsonPath("$..LoginName").exists)
      .check(jsonPath("$..LoginType").exists)
      .check(jsonPath("$..CustomerName").exists)
      .check(jsonPath("$..CustomerId").exists)
      .check(jsonPath("$..ExternalIP").exists)
      .check(jsonPath("$..MachinePlatform").exists)
      .check(jsonPath("$..OperatingSystem").exists)
      .check(jsonPath("$..DeviceId").exists)
      .check(jsonPath("$..LoginId").exists)
      .check(jsonPath("$..PhysicalIP").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
    // "Negative: GET - Fetch all Customer Credentials Secrets from Functional folder using Dynamic combination"
    .exec(http(req8)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=true&templateName=MSS%20Controlled%20Device%20Credential&folderName=" + functionalFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..secretTemplateId").notExists)
      .check(jsonPath("$..folderId").notExists)
      .check(jsonPath("$..HostName").notExists)
      .check(jsonPath("$..LoginName").notExists)
      .check(jsonPath("$..LoginType").notExists)
      .check(jsonPath("$..CustomerName").notExists)
      .check(jsonPath("$..CustomerId").notExists)
      .check(jsonPath("$..ExternalIP").notExists)
      .check(jsonPath("$..MachinePlatform").notExists)
      .check(jsonPath("$..OperatingSystem").notExists)
      .check(jsonPath("$..DeviceId").notExists)
      .check(jsonPath("$..LoginId").notExists)
      .check(jsonPath("$..Status").notExists)
      .check(jsonPath("$..checkedOut").notExists)
      .check(jsonPath("$..checkOutUserId").notExists)
      .check(jsonPath("$..checkOutUserDisplayName").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative: GET - Fetch all Customer Credentials Secrets from Functional folder using Controlled combination"
    .exec(http(req9)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=false&templateName=MSS%20Controlled%20Device%20Credential&folderName=" + functionalFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..secretTemplateId").notExists)
      .check(jsonPath("$..folderId").notExists)
      .check(jsonPath("$..HostName").notExists)
      .check(jsonPath("$..LoginName").notExists)
      .check(jsonPath("$..LoginType").notExists)
      .check(jsonPath("$..CustomerName").notExists)
      .check(jsonPath("$..CustomerId").notExists)
      .check(jsonPath("$..ExternalIP").notExists)
      .check(jsonPath("$..MachinePlatform").notExists)
      .check(jsonPath("$..OperatingSystem").notExists)
      .check(jsonPath("$..DeviceId").notExists)
      .check(jsonPath("$..LoginId").notExists)
      .check(jsonPath("$..Status").notExists)
      .check(jsonPath("$..checkedOut").notExists)
      .check(jsonPath("$..checkOutUserId").notExists)
      .check(jsonPath("$..checkOutUserDisplayName").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Fetch all Customer Credentials Secrets with limit = 20"
    .exec(http(req10)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=true&templateName=MSS%20Cisco%20ASA&folderName=" + folderLimit20 + "&searchText=ASA&start=1&limit=20")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(20))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Fetch all Customer Credentials Secrets with limit = 100"
    .exec(http(req11)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=true&templateName=MSS%20Cisco%20ASA&folderName=" + folderLimit20 + "&searchText=ASA&start=1&limit=100")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").count.lte(100))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative GET - Fetch all Customer Credentials Secrets with limit > 100"
    .exec(http(req12)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=true&templateName=MSS%20Cisco%20ASA&folderName=" + folderLimit20 + "&searchText=ASA&start=1&limit=101")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..message").is("Number of records to fetch cannot be more than 100 per request."))
      .check(jsonPath("$..errors").is("LIMIT_SIZE_EXCEED_ERROR"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "POST - Create a new Customer Controlled Credential"
    .exec(http(req13)
      .post("credentials")
      .basicAuth(adUser, adPass)
      .body(StringBody(controlledGeneratedCredential))
      .check(status.is(201))
      .check(jsonPath("$..id").saveAs("CONTROLLED_CUSTOMER_CREDENTIAL_ID_FROM_STEP_13"))
      .check(jsonPath("$..secretTemplateId").is(controlledSecretTemplateId))
      .check(jsonPath("$..folderId").is(controlledFolderId))
      .check(jsonPath("$..HostName").is(controlledHostName))
      .check(jsonPath("$..LoginName").is(controlledLoginName + "Controlled" + unixTimestamp))
      .check(jsonPath("$..Password").is(controlledPassword))
      .check(jsonPath("$..LoginType").is(controlledLoginType))
      .check(jsonPath("$..CustomerName").is(controlledCustomerName))
      .check(jsonPath("$..CustomerId").is(controlledCustomerId))
      .check(jsonPath("$..ExternalIP").is(controlledExternalIP))
      .check(jsonPath("$..MachinePlatform").is(controlledMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(controlledOperatingSystem))
      .check(jsonPath("$..DeviceId").is(controlledDeviceId))
      .check(jsonPath("$..LoginId").is(controlledLoginId + "Controlled" + unixTimestamp))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "POST - Create a new Customer Dynamic Credential"
    .exec(http(req14)
      .post("credentials")
      .basicAuth(adUser, adPass)
      .body(StringBody(dynamicGeneratedCredential))
      .check(status.is(201))
      .check(jsonPath("$..id").saveAs("DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is(dynamicHostName))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "Dynamic" + unixTimestamp))
      .check(jsonPath("$..Password").is(dynamicPassword))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is(dynamicExternalIP))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "Dynamic" + unixTimestamp))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }
   
    // "POST - Create a new Customer Functional Credential"
    .exec(http(req15)
      .post("credentials")
      .basicAuth(adUser, adPass)
      .body(StringBody(functionalGeneratedCredential))
      .check(status.is(201))
      .check(jsonPath("$..id").saveAs("FUNCTIONAL_CUSTOMER_CREDENTIAL_ID_FROM_STEP_15"))
      .check(jsonPath("$..secretTemplateId").is(functionalSecretTemplateId))
      .check(jsonPath("$..folderId").is(functionalFolderId))
      .check(jsonPath("$..HostName").is(functionalHostName))
      .check(jsonPath("$..LoginName").is(functionalLoginName + "Functional" + unixTimestamp))
      .check(jsonPath("$..Password").is(functionalPassword))
      .check(jsonPath("$..LoginType").is(functionalLoginType))
      .check(jsonPath("$..CustomerName").is(functionalCustomerName))
      .check(jsonPath("$..CustomerId").is(functionalCustomerId))
      .check(jsonPath("$..ExternalIP").is(functionalExternalIP))
      .check(jsonPath("$..MachinePlatform").is(functionalMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(functionalOperatingSystem))
      .check(jsonPath("$..DeviceId").is(functionalDeviceId))
      .check(jsonPath("$..LoginId").is(functionalLoginId + "Functional" + unixTimestamp))
      .check(jsonPath("$..PhysicalIP").is(functionalPhysicalIP))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Fetch details of the newly created Customer Dynamic Credential Using DeviceId and Credential Name"
    .exec(http(req16)
      .get("credentials/" + dynamicDeviceId + "/" + dynamicLoginName  + "Dynamic" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..id").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14}"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is(dynamicHostName))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "Dynamic" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is(dynamicExternalIP))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "Dynamic" + unixTimestamp))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(jsonPath("$..checkedOut").exists)
      .check(jsonPath("$..checkOutUserId").exists)
      .check(jsonPath("$..checkOutUserDisplayName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Fetch details of the newly created Customer Controlled Credential Using DeviceId and Credential Name"
    .exec(http(req17)
      .get("credentials/" + controlledDeviceId + "/" + controlledLoginName  + "Controlled" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=false")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..id").is("${CONTROLLED_CUSTOMER_CREDENTIAL_ID_FROM_STEP_13}"))
      .check(jsonPath("$..secretTemplateId").is(controlledSecretTemplateId))
      .check(jsonPath("$..folderId").is(controlledFolderId))
      .check(jsonPath("$..HostName").is(controlledHostName))
      .check(jsonPath("$..LoginName").is(controlledLoginName + "Controlled" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(controlledLoginType))
      .check(jsonPath("$..CustomerName").is(controlledCustomerName))
      .check(jsonPath("$..CustomerId").is(controlledCustomerId))
      .check(jsonPath("$..ExternalIP").is(controlledExternalIP))
      .check(jsonPath("$..MachinePlatform").is(controlledMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(controlledOperatingSystem))
      .check(jsonPath("$..DeviceId").is(controlledDeviceId))
      .check(jsonPath("$..LoginId").is(controlledLoginId + "Controlled" + unixTimestamp))
      .check(jsonPath("$..checkedOut").exists)
      .check(jsonPath("$..checkOutUserId").exists)
      .check(jsonPath("$..checkOutUserDisplayName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }
   
    // "GET - Fetch details of the newly created Customer Functional Credential Using DeviceId and Credential Name"
    .exec(http(req18)
      .get("credentials/" + functionalDeviceId + "/" + functionalLoginName  + "Functional" + unixTimestamp + "?checkOutEnabled=false&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..id").is("${FUNCTIONAL_CUSTOMER_CREDENTIAL_ID_FROM_STEP_15}"))
      .check(jsonPath("$..secretTemplateId").is(functionalSecretTemplateId))
      .check(jsonPath("$..folderId").is(functionalFolderId))
      .check(jsonPath("$..HostName").is(functionalHostName))
      .check(jsonPath("$..LoginName").is(functionalLoginName + "Functional" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(functionalLoginType))
      .check(jsonPath("$..CustomerName").is(functionalCustomerName))
      .check(jsonPath("$..CustomerId").is(functionalCustomerId))
      .check(jsonPath("$..ExternalIP").is(functionalExternalIP))
      .check(jsonPath("$..MachinePlatform").is(functionalMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(functionalOperatingSystem))
      .check(jsonPath("$..DeviceId").is(functionalDeviceId))
      .check(jsonPath("$..LoginId").is(functionalLoginId + "Functional" + unixTimestamp))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative: POST - Create another Customer Credential with the same deviceID and credentialName"
    .exec(http(req19)
      .post("credentials")
      .basicAuth(adUser, adPass)
      .body(StringBody(controlledGeneratedCredential))
      .check(status.is(400))
      .check(jsonPath("$..message").is("Credential with same name already exists."))
      .check(jsonPath("$..errors").is("ALREADY_EXISTS"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative - GET credential by deviceId and credentialName using invalid credentialName and valid deviceId"
    .exec(http(req20)
      .get("credentials/" + dynamicDeviceId + "/" + "invalidCredentialName"  + "Dynamic" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .body(StringBody(controlledGeneratedCredential))
      .check(status.is(404))
      .check(jsonPath("$..message").is("Credential not found for given Device Id and Credential name"))
      .check(jsonPath("$..errors").is("CREDENTIAL_NOT_FOUND"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative - GET credential by deviceId and credentialName using valid credentialName and invalid deviceId"
    .exec(http(req21)
      .get("credentials/" + "P000000" + "/" + dynamicLoginName  + "Dynamic" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .body(StringBody(controlledGeneratedCredential))
      .check(status.is(404))
      .check(jsonPath("$..message").is("Credentials not found for given Device Id"))
      .check(jsonPath("$..errors").is("CREDENTIALS_NOT_FOUND"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js21)) {
      exec( session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Update the Customer Credential previously created"
    .exec(http(req22)
      .put("credentials/${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14}")
      .basicAuth(adUser, adPass)
      .body(StringBody(generateUpdateCredentialPayload()))
      .check(status.is(200))
      .check(jsonPath("$..message").is("Credential updated successfully."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js22)) {
      exec( session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Fetch details of updated Customer Dynamic Credential Using DeviceId and Credential Name"
    .exec(http(req23)
      .get("credentials/" + dynamicDeviceId + "/" + dynamicLoginName  + "Dynamic" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..id").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14}"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is("hostNameUpdate"))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "Dynamic" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is("externalIPUpdate"))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "Dynamic" + unixTimestamp))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(jsonPath("$..checkedOut").exists)
      .check(jsonPath("$..checkOutUserId").exists)
      .check(jsonPath("$..checkOutUserDisplayName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js23)) {
      exec( session => {
        session.set(js23, "Unable to retrieve JSESSIONID for this request")
      })
    }
  }
  
}

object chainMainExecution2{
  import variables1._
  import methods._
  var chain2 = new ChainBuilder(Nil)
  chain2 = {
    // "POST - Create a 2nd Customer Dynamic Credential in the same folder"
    exec(http(req24)
      .post("credentials")
      .basicAuth(adUser, adPass)
      .body(StringBody(dynamicNewGeneratedCredential))
      .check(status.is(201))
      .check(jsonPath("$..id").saveAs("DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is(dynamicHostName))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..Password").is(dynamicPassword))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is(dynamicExternalIP))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js24)) {
      exec( session => {
        session.set(js24, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "PUT - Negative: Update an existing credential using a different existing login name"
    .exec(http(req25)
      .put("credentials/${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14}")
      .basicAuth(adUser, adPass)
      .body(StringBody(generateUpdateCredentialPayloadForNegativeTest()))
      .check(status.is(400))
      .check(jsonPath("$..message").is("Credential with same name already exists."))
      .check(jsonPath("$..errors").is("ALREADY_EXISTS"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js25)) {
      exec( session => {
        session.set(js25, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Fetch details dynamic credential by device id"
    .exec(http(req26)
      .get("credentials/" + dynamicDeviceId + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$[*][?(@.LoginName == '" + dynamicLoginName + "Dynamic" + unixTimestamp + "')].id").exists)
      .check(jsonPath("$[*][?(@.LoginName == '" + dynamicLoginName + "DynamicNew" + unixTimestamp + "')].id").exists)
      .check(jsonPath("$[*][?(@.LoginName == '" + controlledLoginName + "Controlled" + unixTimestamp + "')].id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js26))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js26)) {
      exec( session => {
        session.set(js26, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Fetch details controlled credential by device id"
    .exec(http(req27)
      .get("credentials/" + dynamicDeviceId + "?checkOutEnabled=true&autoChangeEnabled=false")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$[*][?(@.LoginName == '" + dynamicLoginName + "Dynamic" + unixTimestamp + "')].id").notExists)
      .check(jsonPath("$[*][?(@.LoginName == '" + dynamicLoginName + "DynamicNew" + unixTimestamp + "')].id").notExists)
      .check(jsonPath("$[*][?(@.LoginName == '" + controlledLoginName + "Controlled" + unixTimestamp + "')].id").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js27)) {
      exec( session => {
        session.set(js27, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "POST - Check-out credential"
    .exec(http(req28)
      .post("credentials/" + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}" + "/check-out") // need to be a not-updated credential, if update the credential will be checked out automatically
      .basicAuth(adUser, adPass)
      .body(StringBody(dynamicNewGeneratedCredential))
      .check(status.is(200))
      .check(jsonPath("$..id").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is(dynamicHostName))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is(dynamicExternalIP))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..checkedOut").is("true"))
      .check(jsonPath("$..checkOutUserId").notNull)
      .check(jsonPath("$..checkOutUserDisplayName").notNull)    
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js28))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js28)) {
      exec( session => {
        session.set(js28, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Checking credentials check-out ok"
    .exec(http(req29)
      .get("credentials/" + dynamicDeviceId + "/" + dynamicLoginName  + "DynamicNew" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..id").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is(dynamicHostName))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is(dynamicExternalIP))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(jsonPath("$..checkedOut").is("true"))
      .check(jsonPath("$..checkOutUserId").notNull)
      .check(jsonPath("$..checkOutUserDisplayName").notNull)    
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js29))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js29)) {
      exec( session => {
        session.set(js29, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative POST - Check-out already checked-out credential"
    .exec(http(req30)
      .post("credentials/" + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}" + "/check-out")
      .basicAuth(adUser, adPass)
      .body(StringBody(dynamicNewGeneratedCredential))
      .check(status.is(400))
      .check(jsonPath("$..message").is("Credential is already checked-out"))
      .check(jsonPath("$..errors").is("ALREADY_CHECKED_OUT"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js30))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js30)) {
      exec( session => {
        session.set(js30, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Checking credentials still checkout-out ok after negative scenario"
    .exec(http(req31)
      .get("credentials/" + dynamicDeviceId + "/" + dynamicLoginName  + "DynamicNew" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..id").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is(dynamicHostName))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is(dynamicExternalIP))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(jsonPath("$..checkedOut").is("true"))
      .check(jsonPath("$..checkOutUserId").notNull)
      .check(jsonPath("$..checkOutUserDisplayName").notNull)    
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js31))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js31)) {
      exec( session => {
        session.set(js31, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "POST - Check-in credential"
    .exec(http(req32)
      .post("credentials/" + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}" + "/check-in")
      .basicAuth(adUser, adPass)
      .body(StringBody(dynamicNewGeneratedCredential))
      .check(status.is(200))
      .check(jsonPath("$..id").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}"))
      .check(jsonPath("$..name").is(dynamicDeviceId + ":" + dynamicLoginName + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..siteId").exists)
      .check(jsonPath("$..active").exists)
      .check(jsonPath("$..checkedOut").is("false"))
      .check(jsonPath("$..isRestricted").exists)
      .check(jsonPath("$..isOutOfSync").exists)
      .check(jsonPath("$..outOfSyncReason").exists)
      .check(jsonPath("$..lastHeartBeatStatus").exists)
      .check(jsonPath("$..lastPasswordChangeAttempt").exists)
      .check(jsonPath("$..responseCodes").exists)
      .check(jsonPath("$..lastAccessed").exists)
      .check(jsonPath("$..extendedFields").exists)
      .check(jsonPath("$..checkOutEnabled").exists)
      .check(jsonPath("$..autoChangeEnabled").exists)
      .check(jsonPath("$..doubleLockEnabled").exists)
      .check(jsonPath("$..requiresApproval").exists)
      .check(jsonPath("$..requiresComment").exists)
      .check(jsonPath("$..inheritsPermissions").exists)
      .check(jsonPath("$..hidePassword").exists)
      .check(jsonPath("$..createDate").exists)
      .check(jsonPath("$..daysUntilExpiration").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js32))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js32)) {
      exec( session => {
        session.set(js32, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Checking credentials check-in ok"
    .exec(http(req33)
      .get("credentials/" + dynamicDeviceId + "/" + dynamicLoginName  + "DynamicNew" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..id").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is(dynamicHostName))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is(dynamicExternalIP))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..checkedOut").is(""))
      .check(jsonPath("$..checkOutUserId").notNull)
      .check(jsonPath("$..checkOutUserDisplayName").notNull)    
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js33))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js33)) {
      exec( session => {
        session.set(js33, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "Negative - POST - Check-in already checked-in credential"
    .exec(http(req34)
      .post("credentials/" + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}" + "/check-in")
      .basicAuth(adUser, adPass)
      .body(StringBody(dynamicNewGeneratedCredential))
      .check(status.is(400))
      .check(jsonPath("$..message").is("Credential is already in checked-in state"))
      .check(jsonPath("$..errors").is("ALREADY_CHECKED_IN"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js34))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js34)) {
      exec( session => {
        session.set(js34, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Checking credentials still checked-in ok after negative scenario"
    .exec(http(req35)
      .get("credentials/" + dynamicDeviceId + "/" + dynamicLoginName  + "DynamicNew" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..id").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}"))
      .check(jsonPath("$..secretTemplateId").is(dynamicSecretTemplateId))
      .check(jsonPath("$..folderId").is(dynamicFolderId))
      .check(jsonPath("$..HostName").is(dynamicHostName))
      .check(jsonPath("$..LoginName").is(dynamicLoginName + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..LoginType").is(dynamicLoginType))
      .check(jsonPath("$..CustomerName").is(dynamicCustomerName))
      .check(jsonPath("$..CustomerId").is(dynamicCustomerId))
      .check(jsonPath("$..ExternalIP").is(dynamicExternalIP))
      .check(jsonPath("$..MachinePlatform").is(dynamicMachinePlatform))
      .check(jsonPath("$..OperatingSystem").is(dynamicOperatingSystem))
      .check(jsonPath("$..DeviceId").is(dynamicDeviceId))
      .check(jsonPath("$..PhysicalIP").is(dynamicPhysicalIP))
      .check(jsonPath("$..LoginId").is(dynamicLoginId + "DynamicNew" + unixTimestamp))
      .check(jsonPath("$..checkedOut").is(""))
      .check(jsonPath("$..checkOutUserId").notNull)
      .check(jsonPath("$..checkOutUserDisplayName").notNull)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js35))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js35)) {
      exec( session => {
        session.set(js35, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "DELETE - Deleting the 1st credential created"
    .exec(http(req36)
      .delete("credentials/" + "${CONTROLLED_CUSTOMER_CREDENTIAL_ID_FROM_STEP_13}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..CredentialId").is("${CONTROLLED_CUSTOMER_CREDENTIAL_ID_FROM_STEP_13}" + " is deleted successfully."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js36))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js36)) {
      exec( session => {
        session.set(js36, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "DELETE - Deleting the 2nd credential created"
    .exec(http(req37)
      .delete("credentials/" + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..CredentialId").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_14}" + " is deleted successfully."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js37))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js37)) {
      exec( session => {
        session.set(js37, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "DELETE - Deleting the 3rd credential created"
    .exec(http(req38)
      .delete("credentials/" + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..CredentialId").is("${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}" + " is deleted successfully."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js38))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js38)) {
      exec( session => {
        session.set(js38, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "DELETE - Deleting the 4th credential created"
    .exec(http(req39)
      .delete("credentials/" + "${FUNCTIONAL_CUSTOMER_CREDENTIAL_ID_FROM_STEP_15}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))    
      .check(jsonPath("$..CredentialId").is("${FUNCTIONAL_CUSTOMER_CREDENTIAL_ID_FROM_STEP_15}" + " is deleted successfully."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js39))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js39)) {
      exec( session => {
        session.set(js39, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Check 1st credential has been deleted"
    .exec(http(req40)
      .get("credentials/" + controlledDeviceId + "/" + controlledLoginName  + "Controlled" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=false")
      .basicAuth(adUser, adPass)
      .check(status.is(404))    
      .check(jsonPath("$..id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js40))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js40)) {
      exec( session => {
        session.set(js40, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Check 2nd credential has been deleted"
    .exec(http(req41)
      .get("credentials/" + dynamicDeviceId + "/" + dynamicLoginName  + "Dynamic" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(404))    
      .check(jsonPath("$..id").notExists)  
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js41))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js41)) {
      exec( session => {
        session.set(js41, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Check 3rd credential has been deleted"
    .exec(http(req42)
      .get("credentials/" + dynamicDeviceId + "/" + dynamicLoginName  + "DynamicNew" + unixTimestamp + "?checkOutEnabled=true&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(404))    
      .check(jsonPath("$..id").notExists)  
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js42))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js42)) {
      exec( session => {
        session.set(js42, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET - Check 4th credential has been deleted"
    .exec(http(req43)
      .get("credentials/" + functionalDeviceId + "/" + functionalLoginName  + "Functional" + unixTimestamp + "?checkOutEnabled=false&autoChangeEnabled=true")
      .basicAuth(adUser, adPass)
      .check(status.is(404))    
      .check(jsonPath("$..id").notExists)  
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js43))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js43)) {
      exec( session => {
        session.set(js43, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //"DELETE - Negative - Try to Delete the same credentials previously deleted"
    .exec(http(req44)
      .delete("credentials/" + "${DYNAMIC_CUSTOMER_CREDENTIAL_ID_FROM_STEP_24}")
      .basicAuth(adUser, adPass)
      .check(status.is(400))    
      .check(jsonPath("$..message").is("Not permitted to access this credential or credential is deleted."))
      .check(jsonPath("$..errors").is("\\\"API_AccessDenied\\\""))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js44))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js44)) {
      exec( session => {
        session.set(js44, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // GET - Security - Fetch all Customer Credentials Secrets using valid username and invalid password
    .exec(http(req45)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=true&templateName=MSS%20Cisco%20ASA&folderName=" + dynamicFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth(adUser, "invalid")
      .check(status.is(400))
      .check(jsonPath("$..message").is("\\\"Login failed.\\\""))
      .check(jsonPath("$..errors").is("Bad Request"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js45))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js45)) {
      exec( session => {
        session.set(js45, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // GET - Security - Fetch all Customer Credentials Secrets using invalid username and valid password
    .exec(http(req46)
      .get("credentials?checkOutEnabled=true&autoChangeEnabled=true&templateName=MSS%20Cisco%20ASA&folderName=" + dynamicFolder + "&searchText=ASA&start=1&limit=10")
      .basicAuth("invalid", adPass)
      .check(status.is(400))
      .check(jsonPath("$..message").is("\\\"Login Failed\\\""))
      .check(jsonPath("$..errors").is("Bad Request"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js46))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js46)) {
      exec( session => {
        session.set(js46, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      jsessionMap += (req7 -> session(js7).as[String])
      jsessionMap += (req8 -> session(js8).as[String])
      jsessionMap += (req9 -> session(js9).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      jsessionMap += (req15 -> session(js15).as[String])
      jsessionMap += (req16 -> session(js16).as[String])
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
      jsessionMap += (req34 -> session(js34).as[String])
      jsessionMap += (req35 -> session(js35).as[String])
      jsessionMap += (req36 -> session(js36).as[String])
      jsessionMap += (req37 -> session(js37).as[String])
      jsessionMap += (req38 -> session(js38).as[String])
      jsessionMap += (req39 -> session(js39).as[String])
      jsessionMap += (req40 -> session(js40).as[String])
      jsessionMap += (req41 -> session(js41).as[String])
      jsessionMap += (req42 -> session(js42).as[String])
      jsessionMap += (req43 -> session(js43).as[String])
      jsessionMap += (req44 -> session(js44).as[String])
      jsessionMap += (req45 -> session(js45).as[String])
      jsessionMap += (req46 -> session(js46).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  }
  
}

class AuraCredentialsMs extends Simulation {
  implicit val formats = DefaultFormats
  import variables1._
  import methods._
  import chainMainExecution1._
  import chainMainExecution2._

  val scn = scenario("AuraCredentialsMs")
  .exec(chain1, chain2);

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolAuraCredentialsMs).assertions(global.failedRequests.count.is(0))
  

  
}

