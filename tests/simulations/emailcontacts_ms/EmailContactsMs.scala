import scala.concurrent.duration._
import scala.xml._
import scala.util.parsing.json._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.io.Source
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson._
import org.json4s._
import org.json4s.JsonAST.JValue
import io.gatling.core.assertion._
import sys.process._
// JSESSION
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

class EmailContactsMs extends Simulation {

  //Getting environment parameters
  val environment = System.getenv("ENV")
  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"
  val currentDirectory = new java.io.File(".").getCanonicalPath
  println("environment is: " + environment)

  //Define main variables for Path,Configuration Source, Resource Folder and request and response
  implicit val formats = DefaultFormats
  val emailcontacts_ms_config = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/emailcontacts_ms/emailcontacts_ms_config.json").getLines().mkString)
  val configuration_global = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/configuration_global.json").getLines().mkString)

  // Get values for request parameters from configuration.json file using Json4s
  val headerUsername = System.getenv("AD_USER")
  val headerPassword = System.getenv("AD_PASS")
  val emailContactsMsForDeviceIdsURN = (emailcontacts_ms_config \\ environment \\ "emailContactsMsForDeviceIdsURN").extract[String]
  val emailContactsMsForContactRolesForVSOCPortalURN = (emailcontacts_ms_config \\ environment \\ "emailContactsMsForContactRolesForVSOCPortalURN").extract[String]
  val emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN = (emailcontacts_ms_config \\ environment \\ "emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN").extract[String]
  val emailContactsMsForManagedSIEMrulePolicyAttremailGlobalContactstrueURN = (emailcontacts_ms_config \\ environment \\ "emailContactsMsForManagedSIEMrulePolicyAttremailGlobalContactstrueURN").extract[String]
  val emailContactsMsForParametersNotNalidError400URN = (emailcontacts_ms_config \\ "emailContactsMsForParametersNotNalidError400URN").extract[String]
  val emailContactsMsForFailedToFindEmailContactsError500URN = (emailcontacts_ms_config \\ "emailContactsMsForFailedToFindEmailContactsError500URN").extract[String]
  val emailContactsMsFor400BadRequestURN = (emailcontacts_ms_config \\ "emailContactsMsFor400BadRequestURN").extract[String]
  val headerCheckAssertionStatusCode = (emailcontacts_ms_config \\ "headerCheckAssertion" \\ "statusCode").extract[String]
  val emailContactsMsRequestForCustomerContactOnServicesURN = (emailcontacts_ms_config \\ "emailContactsMsRequestForCustomerContactOnServicesURN").extract[String]

  //jsession variables for step1
  val req01 = "Get Email Contacts To Get Number of Records - For Device Only"
  val js01 = "jsessionIDGetEmailContactsGetNumberOfRecords - For Device Only"
  val req02 = "Get Email Contacts To Get Main Values For Future Check For Global Contacts - For Device Only"
  val js02 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForGlobalContactsForDeviceOnly"
  val req03 = "Get Contact Info From Services For Global Contacts - For Device Only"
  val js03 = "jsessionIDGetContactInfoFromServicesForGlobalContactsForDeviceOnly"   
  val req04 = "Get Roles For VSOC Portal No For Global Contacts - For Device Only"
  val js04 = "jsessionIDGetRolesForVSOCPortalNoForGlobalContactsForDeviceOnly"
  val req05 = "Get Roles Values From EmailContactsMs For SpecificContact For Global Contacts - For DeviceOnly"
  val js05 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForGlobalContactsForDeviceOnly"
  val req06 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Global Contacts - For Device Only"
  val js06 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForGlobalContactsForDeviceOnly"
  val req07 = "Get Email Contacts To Get Main Values For Future Check For Site Contacts - For Device Only"
  val js07 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForSiteContactsForDeviceOnly"  
  val req08 = "Get Contact Info From Services For Site Contacts - For Device Only"
  val js08 = "jsessionIDGetContactInfoFromServicesForSiteContactsForDeviceOnly" 
  val req09 = "Get Roles For VSOC Portal No For Site Contacts - For Device Only"
  val js09 = "jsessionIDGetRolesForVSOCPortalNoForSiteContactsForDeviceOnly"
  val req10 = "Get Roles Values From EmailContactsMs For SpecificContact For Site Contacts - For DeviceOnly"
  val js10 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsForDeviceOnly"
  val req11 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Site Contacts - For Device Only"
  val js11 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsForDeviceOnly"
  val req12 = "Get Email Contacts To Get Main Values For Future Check For Site Contacts Not On Ticket - For Device Only"
  val js12 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForSiteContactsNotOnTicketContactsForDeviceOnly"
  val req13 = "Get Contact Info From Services For Site Contacts Not On Ticket - For Device Only"
  val js13 = "jsessionIDGetContactInfoFromServicesForSiteContactsNotOnTicketForDeviceOnly" 
  val req14 = "Get Roles For VSOC Portal No For Site Contacts Not On Ticket - For Device Only"
  val js14 = "jsessionIDGetRolesForVSOCPortalNoForSiteContactsNotOnTicketForDeviceOnly"
  val req15 = "Get Roles Values From EmailContactsMs For SpecificContact For Site Contacts Not On Ticket - For DeviceOnly"
  val js15 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsNotOnTicketForDeviceOnly"
  val req16 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Site Contacts Not On Ticket - For Device Only"
  val js16 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsNotOnTicketForDeviceOnly"
  
  //jsession variables for step2
  val req17 = "Get Email Contacts To Get Number of Records - For VSOC_Portal"
  val js17 = "jsessionIDGetEmailContactsGetNumberOfRecords - For VSOC_Portal"
  val req18 = "Get Email Contacts To Get Main Values For Future Check For Global Contacts - For VSOC_Portal"
  val js18 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForGlobalContactsForVSOC_Portal"
  val req19 = "Get Contact Info From Services For Global Contacts - For VSOC_Portal"
  val js19 = "jsessionIDGetContactInfoFromServicesForGlobalContactsForVSOC_Portal"   
  val req20 = "Get Roles For VSOC Portal No For Global Contacts - For VSOC_Portal"
  val js20 = "jsessionIDGetRolesForVSOCPortalNoForGlobalContactsForVSOC_Portal"
  val req21 = "Get Roles Values From EmailContactsMs For SpecificContact For Global Contacts - For VSOC_Portal"
  val js21 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForGlobalContactsForVSOC_Portal"
  val req22 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Global Contacts - For VSOC_Portal"
  val js22 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForGlobalContactsForVSOC_Portal"
  val req23 = "Get Email Contacts To Get Main Values For Future Check For Site Contacts - For VSOC_Portal"
  val js23 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForSiteContactsForVSOC_Portal"  
  val req24 = "Get Contact Info From Services For Site Contacts - For VSOC_Portal"
  val js24 = "jsessionIDGetContactInfoFromServicesForSiteContactsForVSOC_Portal" 
  val req25 = "Get Roles For VSOC Portal No For Site Contacts - For VSOC_Portal"
  val js25 = "jsessionIDGetRolesForVSOCPortalNoForSiteContactsForVSOC_Portal"
  val req26 = "Get Roles Values From EmailContactsMs For SpecificContact For Site Contacts - For VSOC_Portal"
  val js26 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsForVSOC_Portal"
  val req27 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Site Contacts - For VSOC_Portal"
  val js27 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsForVSOC_Portal"
  val req28 = "Get Email Contacts To Get Main Values For Future Check For Site Contacts Not On Ticket - For VSOC_Portal"
  val js28 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForSiteContactsNotOnTicketContactsForVSOC_Portal"
  val req29 = "Get Contact Info From Services For Site Contacts Not On Ticket - For VSOC_Portal"
  val js29 = "jsessionIDGetContactInfoFromServicesForSiteContactsNotOnTicketForVSOC_Portal" 
  val req30 = "Get Roles For VSOC Portal No For Site Contacts Not On Ticket - For VSOC_Portal"
  val js30 = "jsessionIDGetRolesForVSOCPortalNoForSiteContactsNotOnTicketForVSOC_Portal"
  val req31 = "Get Roles Values From EmailContactsMs For SpecificContact For Site Contacts Not On Ticket - For VSOC_Portal"
  val js31 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsNotOnTicketForVSOC_Portal"
  val req32 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Site Contacts Not On Ticket - For VSOC_Portal"
  val js32 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsNotOnTicketForVSOC_Portal"
  
  //jsession variables for step3
  val req33 = "Get Email Contacts To Get Number of Records - For SocConsoleOrAI"
  val js33 = "jsessionIDGetEmailContactsGetNumberOfRecords - For SocConsoleOrAI"
  val req34 = "Get Email Contacts To Get Main Values For Future Check For Global Contacts - For SocConsoleOrAI"
  val js34 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForGlobalContactsForSocConsoleOrAI"
  val req35 = "Get Contact Info From Services For Global Contacts - For SocConsoleOrAI"
  val js35 = "jsessionIDGetContactInfoFromServicesForGlobalContactsForSocConsoleOrAI"   
  val req36 = "Get Roles For VSOC Portal No For Global Contacts - For SocConsoleOrAI"
  val js36 = "jsessionIDGetRolesForVSOCPortalNoForGlobalContactsForSocConsoleOrAI"
  val req37 = "Get Roles Values From EmailContactsMs For SpecificContact For Global Contacts - For SocConsoleOrAI"
  val js37 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForGlobalContactsForSocConsoleOrAI"
  val req38 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Global Contacts - For SocConsoleOrAI"
  val js38 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForGlobalContactsSocConsoleOrAI"
  val req39 = "Get Email Contacts To Get Main Values For Future Check For Site Contacts - For SocConsoleOrAI"
  val js39 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForSiteContactsForSocConsoleOrAI"  
  val req40 = "Get Contact Info From Services For Site Contacts - For SocConsoleOrAI"
  val js40 = "jsessionIDGetContactInfoFromServicesForSiteContactsForSocConsoleOrAI" 
  val req41 = "Get Roles For VSOC Portal No For Site Contacts - For SocConsoleOrAI"
  val js41 = "jsessionIDGetRolesForVSOCPortalNoForSiteContactsForSocConsoleOrAI"
  val req42 = "Get Roles Values From EmailContactsMs For SpecificContact For Site Contacts - For SocConsoleOrAI"
  val js42 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsForSocConsoleOrAI"
  val req43 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Site Contacts - For SocConsoleOrAI"
  val js43 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsForSocConsoleOrAI"
  val req44 = "Get Email Contacts To Get Main Values For Future Check For Site Contacts Not On Ticket - For SocConsoleOrAI"
  val js44 = "jsessionIDGetEmailContactsToGetMainValuesForFutureCheckForSiteContactsNotOnTicketContactsForSocConsoleOrAI"
  val req45 = "Get Contact Info From Services For Site Contacts Not On Ticket - For SocConsoleOrAI"
  val js45 = "jsessionIDGetContactInfoFromServicesForSiteContactsNotOnTicketForSocConsoleOrAIy" 
  val req46 = "Get Roles For VSOC Portal No For Site Contacts Not On Ticket - For SocConsoleOrAI"
  val js46 = "jsessionIDGetRolesForVSOCPortalNoForSiteContactsNotOnTicketForSocConsoleOrAI"
  val req47 = "Get Roles Values From EmailContactsMs For SpecificContact For Site Contacts Not On Ticket - For SocConsoleOrAI"
  val js47 = "jsessionIDGetRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsNotOnTicketForSocConsoleOrAI"
  val req48 = "Get Roles Values From Services For Specific Contact And Check Match With The Ones FromEmail ContactMs For Site Contacts Not On Ticket - For SocConsoleOrAI"
  val js48 = "jsessionIDgetRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsNotOnTicketForSocConsoleOrAI"
  val req49 = "Auto Escalation Contacts"
  val js49 = "jsessionIDAutoEscalationContacts"
  
  //jsession variables for Negative Scenario 1
  val req50 = "Negative Scenario - Missing all parameters"
  val js50 = "jsessionIDMissingallparameters"
  val req51 = "Negative Scenario - Invalid Customer ID"
  val js51 = "jsessionIDInvalidCustomerID"
  val req52 = "Negative Scenario - Invalid parameters"
  val js52 = "jsessionIDInvalidparameters"
  
  //jsession variables for defaultEmailFlag scenarios and auto escalation
  val req53 = "rulePolicyAttr_serviceLine"
  val js53 = "jsessionIDrulePolicyAttr_serviceLine"
  val req54 = "rulePolicyAttr_emailGlobalContacts=<number>"
  val js54 = "jsessionIDulePolicyAttr_emailGlobalContacts=<number>"
  val req55 = "rulePolicyAttr_emailGlobalContacts=true"
  val js55 = "jsessionIDrulePolicyAttr_emailGlobalContacts=true"
  val req56 = "rulePolicyAttr_emailSiteContacts=true"
  val js56 = "jsessionIDrulePolicyAttr_emailSiteContacts=true"
  val req57 = "rulePolicyAttr_emailSiteContacts=<number>"
  val js57 = "jsessionIDrulePolicyAttr_emailSiteContacts=<number>"
  
  //set baseURL according to environment parameter
  val baseURL = (configuration_global \\ "baseURL" \\ environment).extract[String]
  
  //Define Group Names For Response
  val globalContactsGroupName = "GlobalContacts"
  val siteContactsGroupName = "SiteContacts"
  val siteContactsNotOnTicketGroupName = "Site Contacts Not On Ticket"

  //Define httpProtocol for execution
  val httpProtocolEmailContactsMs = http
    .baseUrl(baseURL)

  //Define the scenario for CapsAssetsDeleteAsset
  val getEmailContactsMsScenario = scenario("getEmailContactsScenario")
  
    /***uncomment the code below if need to get values from Remedy.. the code below gets token from Remedy
     	.exec(http("Generate Token on Remedy")
		  .post("https://stage-remedy.sec.ibm.com:8443/api/jwt/login")
		  .queryParam("username", "apttus-qa")
		  .queryParam("password", "apttus-test")
		  .header("Content-type", "application/x-www-form-urlencoded")
		  .check(bodyString.saveAs("remedyToken")))
   */    
  
   /***************START OF step1 of teste -> request with deviceIds, no vsoc_portal, no soc_console_or_AI*************************/
  
  .exec({session =>
        println("*****START OF step1 of teste -> request with deviceIds, no vsoc_portal, no soc_console_or_AI*****")
        session
       }) 
  
   //getEmailContacts to get Number of Records ${numberOfRecords} for step1 of teste -> request with deviceIds, no vsoc_portal, no soc_console_or_AI 
   .exec(getEmailContactsToGetNumberOfRecords(req01, js01, emailContactsMsForDeviceIdsURN)) 
    
   //clean cookies to get new jsession for next exec call
   .exec(flushSessionCookies)    
   
      /**Checking for all Global Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForGlobal"){//"${numberOfRecordsForGlobal}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
            var indexRepeatNumberOfRecordsForGlobal: Int = session("indexRepeatNumberOfRecordsForGlobal").as[Int]
            session.set("indexRepeatNumberOfRecordsForGlobal", indexRepeatNumberOfRecordsForGlobal) //index saved on session variable
            val contactEmailUserIdResponse = session("allUserIdsForGlobal").as[Seq[String]].apply(indexRepeatNumberOfRecordsForGlobal)
            session.set("contactUserIdResponse", session("allUserIdsForGlobal").as[Seq[String]].apply(indexRepeatNumberOfRecordsForGlobal))
        
            })  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)  
      
        //Get Contact info for one specific contact (using emailContacts_ms)
        .exec(getEmailContactsToGetMainValuesForFutureCheck(req02, js02, emailContactsMsForDeviceIdsURN, globalContactsGroupName, "${indexRepeatNumberOfRecordsForGlobal}"))  
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
        
        
        //transform contactInfoContactNameFromGetEmailContactsCall to remove special characters
        .exec({session =>
                    
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)

                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)
                })     
                
        //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
        .exec(getContactInfoFromServices(req03, js03))  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
    
        //print variables to debub if needed
        .exec(session => {

            println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
            println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
            println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
            println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
            println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
            println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
            println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
            println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
            println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
            println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
            println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
            println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
            session
        })
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)

        //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
        .exec(getRolesForVSOCPortalNoForGlobalContactsGroup(req04, js04, "${contactUserIdResponse}"))   
        
        //exclude number of roles with rank=0 of the role checking
        .exec(session => {                  
                     var numberOfRolesFromServicesWithRankZero: Int = session("numberOfRolesFromServicesWithRankZero").as[Int] 
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithRankZero
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
    
            .repeat("${numberOfRolesFromServices}", "index"){
        
                exec {session =>
                    val offsetCounter = session("index").as[Int]
                    val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                    session.set("offsetCounter", offsetCounter)
                    session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
                }
        
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get Contact info for one specific contact (using emailContacts_ms)
                //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
                .exec(getRolesValuesFromEmailContactsMsForSpecificContact(req05, js05, emailContactsMsForDeviceIdsURN, globalContactsGroupName, "${indexRepeatNumberOfRecordsForGlobal}"))
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
                 
                //transfor contact name
                .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameFromEmailContactsMsCallToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameFromEmailContactsMsCallToTransform = roleContactNameFromEmailContactsMsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameFromEmailContactsMsCallToTransform)
                })
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get contact roles from services rest call
                .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMs(req06, js06,"${contactUserIdResponse}"))
             
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
              
                .exec(session => {
                     println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                     println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                     println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                     println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                     println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                     println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                     println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                     println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                     println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                     println("role contact siteId from services: " + session("roleSiteIdFromServicesCall").as[String])
                     println("role contact siteName from services: " + session("roleSiteNameFromServicesCall").as[String])
                     println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                     println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                     println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                     println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                     println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                     println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                     println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                     println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                     println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                     println("role contact roleType from email contacts ms: " + session(s"roleTypeFromEmailContactsMsCall").as[String])
                     println("role contact siteId from email contacts ms: " + session("roleSiteIdFromEmailContactsMsCall").as[String])
                     println("role contact siteName from email contacts ms: " + session("roleSiteNameFromEmailContactsMsCall").as[String])
                     println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                     session
                })  
                  
            }      
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)  
    
 		  }/**End Checking for all Global Records**/  
    
      /**Checking for all SiteContacts Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForSiteContacts"){//"${numberOfRecordsForSiteContacts}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
            var indexRepeatNumberOfRecordsForSiteContacts: Int = session("indexRepeatNumberOfRecordsForSiteContacts").as[Int]
            session.set("indexRepeatNumberOfRecordsForSiteContacts", indexRepeatNumberOfRecordsForSiteContacts) //index saved on session variable
            val contactEmailUserIdResponse = session("allUserIdsForSiteContacts").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContacts)
            session.set("contactUserIdResponse", session("allUserIdsForSiteContacts").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContacts))
        
          })  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)  
      
        //Get Contact info for one specific contact (using emailContacts_ms)
        .exec(getEmailContactsToGetMainValuesForFutureCheck(req07, js07, emailContactsMsForDeviceIdsURN, siteContactsGroupName, "${indexRepeatNumberOfRecordsForSiteContacts}"))  
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
        
        //transform contactInfoContactNameFromGetEmailContactsCall
          .exec({session =>
                    var contactNameToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactNameToTransform)
               })      
    
        //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
        .exec(getContactInfoFromServices(req08, js08))  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
    
        //set indexRepeatNumberOfRecords to session variable
        .exec(session => {

            println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
            println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
            println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
            println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
            println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
            println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
            println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
            println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
            println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
            println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
            println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
            println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
            session
        })
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)

        //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
        .exec(getRolesForVSOCPortalNoForSiteContactsGroup(req09, js09, "${contactUserIdResponse}"))   
        
        //exclude number of roles with rank=0 of the role checking
        .exec(session => {                  
                     var numberOfRolesFromServicesWithRankZero: Int = session("numberOfRolesFromServicesWithRankZero").as[Int] 
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithRankZero
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
    
            .repeat("${numberOfRolesFromServices}", "index"){
        
                exec {session =>
                    val offsetCounter = session("index").as[Int]
                    val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                    session.set("offsetCounter", offsetCounter)
                    session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
                }
        
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get Contact info for one specific contact (using emailContacts_ms)
                //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
                .exec(getRolesValuesFromEmailContactsMsForSpecificContact(req10, js10, emailContactsMsForDeviceIdsURN, siteContactsGroupName, "${indexRepeatNumberOfRecordsForSiteContacts}"))
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
                 
                //transfor contact name
                .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
                })
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get contact roles from services rest call
                .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMs(req11, js11, "${contactUserIdResponse}"))
             
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
              
                //print the values to log to debug if needed
                .exec(session => {
                     println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                     println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                     println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                     println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                     println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                     println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                     println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                     println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                     println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                     println("role contact siteId from services: " + session("roleSiteIdFromServicesCall").as[String])
                     println("role contact siteName from services: " + session("roleSiteNameFromServicesCall").as[String])
                     println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                     println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                     println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                     println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                     println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                     println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                     println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                     println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                     println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                     println("role contact roleType from email contacts ms: " + session(s"roleTypeFromEmailContactsMsCall").as[String])
                     println("role contact siteId from email contacts ms: " + session("roleSiteIdFromEmailContactsMsCall").as[String])
                     println("role contact siteName from email contacts ms: " + session("roleSiteNameFromEmailContactsMsCall").as[String])
                     println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                     session
                })  
                  
            }      
          
          //clean cookies to get new jsession for next exec call
         .exec(flushSessionCookies)  
    
 		  }/**End Checking for all SiteContacts Records**/
      
    
      /**Checking for all Site Contacts Not On Ticket Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForSiteContactsNotOnTicket"){ //"${numberOfRecordsForSiteContactsNotOnTicket}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
              var indexRepeatNumberOfRecordsForSiteContactsNotOnTicket: Int = session("indexRepeatNumberOfRecordsForSiteContactsNotOnTicket").as[Int]
              session.set("indexRepeatNumberOfRecordsForSiteContactsNotOnTicket", indexRepeatNumberOfRecordsForSiteContactsNotOnTicket) //index saved on session variable
              val contactEmailUserIdResponse = session("allUserIdsForSiteContactsNotOnTicket").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContactsNotOnTicket)
              session.set("contactUserIdResponse", session("allUserIdsForSiteContactsNotOnTicket").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContactsNotOnTicket))
        
          })  
      
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)  
      
          //Get Contact info for one specific contact (using emailContacts_ms)
          .exec(getEmailContactsToGetMainValuesForFutureCheck(req12, js12, emailContactsMsForDeviceIdsURN, siteContactsNotOnTicketGroupName, "${indexRepeatNumberOfRecordsForSiteContactsNotOnTicket}"))  
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)
          
          //transform contactInfoContactNameFromGetEmailContactsCall
          .exec({session =>
                    var contactNameToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactNameToTransform)
               })
    
          //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
          .exec(getContactInfoFromServices(req13, js13))  
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)
    
          //set indexRepeatNumberOfRecords to session variable
          .exec(session => {

              println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
              println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
              println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
              println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
              println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
              println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
              println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
              println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
              println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
              println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
              println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
              println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
              session
          })
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)

          //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
          .exec(getRolesForVSOCPortalNoForSiteContactsNotOnTicketGroup(req14, js14, "${contactUserIdResponse}"))  
          
          //set numberOfRolesFromServices excluding numberOfRolesFromServicesWithSiteIdAtlanta
          .exec(session => {                   
                     println("Number Of Roles From Services With Site Id Atlanta: " + session("numberOfRolesFromServicesWithSiteIdAtlanta").as[String])
                     println("Number Of Roles From Services Before Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
                
          .exec(session => {                   
                     var numberOfRolesFromServicesWithSiteIdAtlanta: Int = session("numberOfRolesFromServicesWithSiteIdAtlanta").as[Int]
                     var numberOfRolesFromServicesWithSiteIdAtlantaAndRankZero: Int = session("numberOfRolesFromServicesWithSiteIdAtlantaAndRankZero").as[Int]
                     var numberOfRolesFromServicesWithRankZero: Int = session("numberOfRolesFromServicesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRankZeroAfterSet = numberOfRolesFromServicesWithRankZero - numberOfRolesFromServicesWithSiteIdAtlantaAndRankZero
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithSiteIdAtlanta - numberOfRolesFromServicesWithRankZeroAfterSet
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
                
          .exec(session => {                   
                     println("Number Of Roles From Services After Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
           //END set numberOfRolesFromServices excluding numberOfRolesFromServicesWithSiteIdAtlanta
    
          .repeat("${numberOfRolesFromServices}", "index"){
        
              //set the counter variable for loop
              exec {session =>
                  val offsetCounter = session("index").as[Int]
                  val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                  session.set("offsetCounter", offsetCounter)
                  session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
              }
        
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //Get Contact info for one specific contact (using emailContacts_ms)
               //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
               .exec(getRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsNotOnTicketGroupName(req15, js15, emailContactsMsForDeviceIdsURN, siteContactsNotOnTicketGroupName, "${indexRepeatNumberOfRecordsForSiteContactsNotOnTicket}"))
            
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //transfor contact name
               .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
               })
            
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //Get contact roles from services rest call
               .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsNotOnTicketGroupName(req16, js16, "${contactUserIdResponse}"))
             
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
              
               //print the variables to log to debug if needed
               .exec(session => {
                    println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                    println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                    println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                    println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                    println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                    println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                    println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                    println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                    println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                    println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                    println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                    println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                    println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                    println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                    println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                    println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                    println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                    println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                    println("role contact roleType from email contacts ms: " + session("roleTypeFromEmailContactsMsCall").as[String])
                    println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                    session
               })  
                  
          }      
          
       //clean cookies to get new jsession for next exec call
       .exec(flushSessionCookies)     
    
 		  } /**End Checking for all Site Contacts Not On Ticket Records**/  
  
  
  /***uncomment the code below if need to get values from Remedy.. the code below releases token from Remedy
    .exec(http("Release Token on Remedy")
		.post("https://stage-remedy.sec.ibm.com:8443/api/jwt/logout")
		.header("Authorization", "${remedyToken}"))
		*/
		
		//clean cookies to get new jsession for next exec call
    .exec(flushSessionCookies)
     
    .exec({session =>
        println("*****END OF step1 of teste -> request with deviceIds, no vsoc_portal, no soc_console_or_AI*****")
        session
       }) 
       
    /***************END OF step1 of teste -> request with deviceIds, no vsoc_portal, no soc_console_or_AI*************************/
    

    /***************START OF step2 of teste -> request with deviceIds, VSOC_Portal*************************/
  
  .exec({session =>
        println("*****START OF step2 of teste -> request with deviceIds, VSOC_Portal*****")
        session
       }) 
  
   //getEmailContacts to get Number of Records ${numberOfRecords} for step1 of teste -> request with VSOC_Portal 
   .exec(getEmailContactsToGetNumberOfRecords(req17, js17, emailContactsMsForContactRolesForVSOCPortalURN)) 
    
   //clean cookies to get new jsession for next exec call
   .exec(flushSessionCookies)    
   
      /**Checking for all Global Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForGlobal"){//"${numberOfRecordsForGlobal}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
            var indexRepeatNumberOfRecordsForGlobal: Int = session("indexRepeatNumberOfRecordsForGlobal").as[Int]
            session.set("indexRepeatNumberOfRecordsForGlobal", indexRepeatNumberOfRecordsForGlobal) //index saved on session variable
            val contactEmailUserIdResponse = session("allUserIdsForGlobal").as[Seq[String]].apply(indexRepeatNumberOfRecordsForGlobal)
            session.set("contactUserIdResponse", session("allUserIdsForGlobal").as[Seq[String]].apply(indexRepeatNumberOfRecordsForGlobal))
        
            })  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)  
      
        //Get Contact info for one specific contact (using emailContacts_ms)
        .exec(getEmailContactsToGetMainValuesForFutureCheck(req18, js18, emailContactsMsForContactRolesForVSOCPortalURN, globalContactsGroupName, "${indexRepeatNumberOfRecordsForGlobal}"))  
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
        
        
        //transform contactInfoContactNameFromGetEmailContactsCall to remove special characters
        .exec({session =>
                    
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)

                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)
                })     
              
        //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
        .exec(getContactInfoFromServices(req19, js19))  
      
        .exec(session => {
                  println("Valor isEmailTagFound depois de getContactInfoFromServices req18: " +  session("isEmailTagFound").as[String])
                  session
                })  
                
        .exec(session => {
                    session.remove("isEmailTagFound")

                })        
        
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
    
        //print variables to debub if needed
        .exec(session => {

            println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
            println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
            println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
            println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
            println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
            println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
            println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
            println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
            println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
            println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
            println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
            println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
            session
        })
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)

        //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
        .exec(getRolesForVSOCPortalYesForGlobalContactsGroup(req20, js20, "${contactUserIdResponse}"))  
        
        
        //set numberOfRolesFromServices excluding roleName=All roles and roleType=MFS - All Roles || VMS - All Roles || MDP - All Roles
          .exec(session => {                   
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MFSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=VMSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MDPAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MFSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=VMSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MDPAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MFSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=VMSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MDPAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MFSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=VMSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MDPAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With rank=0: " + session("numberOfRolesFromServicesWithRankZero").as[String])
                     println("Number Of Roles From Services Before Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
                
          .exec(session => {                   
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRankZero: Int = session("numberOfRolesFromServicesWithRankZero").as[Int] - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero  
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles - numberOfRolesFromServicesWithRankZero
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
                
          .exec(session => {                   
                     println("Number Of Roles From Services After Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
           //END set numberOfRolesFromServices excluding numberOfRolesFromServicesWithSiteIdAtlanta
    
            .repeat("${numberOfRolesFromServices}", "index"){
        
                exec {session =>
                    val offsetCounter = session("index").as[Int]
                    val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                    session.set("offsetCounter", offsetCounter)
                    session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
                }
        
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get Contact info for one specific contact (using emailContacts_ms)
                //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
                .exec(getRolesValuesFromEmailContactsMsForSpecificContact(req21, js21, emailContactsMsForContactRolesForVSOCPortalURN, globalContactsGroupName, "${indexRepeatNumberOfRecordsForGlobal}"))
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
                 
                //transfor contact name
                .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")                  
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    
                })
                
                .exec({session =>
                    
                    var roleContactNameFromEmailContactsMsCallToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameFromEmailContactsMsCallToTransform = roleContactNameFromEmailContactsMsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")                
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameFromEmailContactsMsCallToTransform)

                })
                
                .exec({session =>
                    var roleNameFromEmailContactsMsCallToTransform: String = session("roleNameFromEmailContactsMsCall").as[String]
                    roleNameFromEmailContactsMsCallToTransform = roleNameFromEmailContactsMsCallToTransform.replaceAll("roles", "Roles")
                    session.set("roleNameFromEmailContactsMsCall",roleNameFromEmailContactsMsCallToTransform)
                })
                
                .exec({session =>
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)

                })
                /**
                .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameFromEmailContactsMsCallToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameFromEmailContactsMsCallToTransform = roleContactNameFromEmailContactsMsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    //roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    
                    var roleNameFromEmailContactsMsCallToTransform: String = session("roleNameFromEmailContactsMsCall").as[String]
                    roleNameFromEmailContactsMsCallToTransform = roleNameFromEmailContactsMsCallToTransform.replaceAll("roles", "Roles")
                    
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    //session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameFromEmailContactsMsCallToTransform)
                    session.set("roleNameFromEmailContactsMsCall",roleNameFromEmailContactsMsCallToTransform)
                })**/
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get contact roles from services rest call
                .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsShowOnPortalYes(req22, js22,"${contactUserIdResponse}"))
             
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
              
                .exec(session => {
                     println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                     println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                     println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                     println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                     println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                     println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                     println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                     println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                     println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                     println("role contact siteId from services: " + session("roleSiteIdFromServicesCall").as[String])
                     println("role contact siteName from services: " + session("roleSiteNameFromServicesCall").as[String])
                     println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                     println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                     println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                     println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                     println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                     println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                     println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                     println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                     println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                     println("role contact roleType from email contacts ms: " + session(s"roleTypeFromEmailContactsMsCall").as[String])
                     println("role contact siteId from email contacts ms: " + session("roleSiteIdFromEmailContactsMsCall").as[String])
                     println("role contact siteName from email contacts ms: " + session("roleSiteNameFromEmailContactsMsCall").as[String])
                     println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                     session
                })
                
                
                .exec(session => {
                  println("Value of isSiteIdFound after getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsShowOnPortalYes req21: " +   session("isSiteIdFound").as[String])
                  session
                })
                
                //remove siteId and siteName from session to get isSiteIdFound.exists() and isSiteNameFound.exists() false if not find in the call
                .exec(session => {
                    session.remove("isSiteIdFound") 
                    })
                .exec(session => {
                    session.remove("isSiteNameFound") 
                    })
                  
            }      
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)  
    
 		  }/**End Checking for all Global Records**/  
    
      /**Checking for all SiteContacts Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForSiteContacts"){//"${numberOfRecordsForSiteContacts}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
            var indexRepeatNumberOfRecordsForSiteContacts: Int = session("indexRepeatNumberOfRecordsForSiteContacts").as[Int]
            session.set("indexRepeatNumberOfRecordsForSiteContacts", indexRepeatNumberOfRecordsForSiteContacts) //index saved on session variable
            val contactEmailUserIdResponse = session("allUserIdsForSiteContacts").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContacts)
            session.set("contactUserIdResponse", session("allUserIdsForSiteContacts").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContacts))
        
          })  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)  
      
        //Get Contact info for one specific contact (using emailContacts_ms)
        .exec(getEmailContactsToGetMainValuesForFutureCheck(req23, js23, emailContactsMsForContactRolesForVSOCPortalURN, siteContactsGroupName, "${indexRepeatNumberOfRecordsForSiteContacts}"))  
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
        
        //transform contactInfoContactNameFromGetEmailContactsCall
          .exec({session =>
                    var contactNameToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactNameToTransform)
               })      
    
        //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
        .exec(getContactInfoFromServices(req24, js24))  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
    
        //set indexRepeatNumberOfRecords to session variable
        .exec(session => {

            println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
            println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
            println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
            println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
            println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
            println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
            println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
            println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
            println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
            println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
            println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
            println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
            session
        })
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)

        //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
        .exec(getRolesForVSOCPortalYesForSiteContactsGroup(req25, js25, "${contactUserIdResponse}"))   
        
        //set numberOfRolesFromServices excluding roleName=All roles and roleType=MFS - All Roles || VMS - All Roles || MDP - All Roles
          .exec(session => {                   
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MFSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=VMSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MDPAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MFSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=VMSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MDPAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MFSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=VMSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MDPAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MFSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=VMSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MDPAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With rank=0: " + session("numberOfRolesFromServicesWithRankZero").as[String])
                     println("Number Of Roles From Services Before Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
                
          .exec(session => {                   
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRankZero: Int = session("numberOfRolesFromServicesWithRankZero").as[Int] - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero  
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles - numberOfRolesFromServicesWithRankZero
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
                
          .exec(session => {                   
                     println("Number Of Roles From Services After Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
           //END set numberOfRolesFromServices excluding numberOfRolesFromServicesWithSiteIdAtlanta
    
            .repeat("${numberOfRolesFromServices}", "index"){
        
                exec {session =>
                    val offsetCounter = session("index").as[Int]
                    val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                    session.set("offsetCounter", offsetCounter)
                    session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
                }
        
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get Contact info for one specific contact (using emailContacts_ms)
                //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
                .exec(getRolesValuesFromEmailContactsMsForSpecificContact(req26, js26, emailContactsMsForContactRolesForVSOCPortalURN, siteContactsGroupName, "${indexRepeatNumberOfRecordsForSiteContacts}"))
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
                 
                //transfor contact name
                .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")                  
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    
                })
                
                .exec({session =>
                    
                    var roleContactNameFromEmailContactsMsCallToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameFromEmailContactsMsCallToTransform = roleContactNameFromEmailContactsMsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")                
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameFromEmailContactsMsCallToTransform)

                })
                
                .exec({session =>
                    var roleNameFromEmailContactsMsCallToTransform: String = session("roleNameFromEmailContactsMsCall").as[String]
                    roleNameFromEmailContactsMsCallToTransform = roleNameFromEmailContactsMsCallToTransform.replaceAll("roles", "Roles")
                    session.set("roleNameFromEmailContactsMsCall",roleNameFromEmailContactsMsCallToTransform)
                })
                
                .exec({session =>
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)

                })
                /**.exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    
                    var roleNameFromEmailContactsMsCallToTransform: String = session("roleNameFromEmailContactsMsCall").as[String]
                    roleNameFromEmailContactsMsCallToTransform = roleNameFromEmailContactsMsCallToTransform.replaceAll("roles", "Roles")
                    
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
                    session.set("roleNameFromEmailContactsMsCall",roleNameFromEmailContactsMsCallToTransform)
                })**/
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get contact roles from services rest call
                .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsShowOnPortalYes(req27, js27, "${contactUserIdResponse}"))
             
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
              
                //print the values to log to debug if needed
                .exec(session => {
                     println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                     println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                     println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                     println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                     println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                     println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                     println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                     println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                     println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                     println("role contact siteId from services: " + session("roleSiteIdFromServicesCall").as[String])
                     println("role contact siteName from services: " + session("roleSiteNameFromServicesCall").as[String])
                     println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                     println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                     println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                     println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                     println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                     println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                     println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                     println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                     println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                     println("role contact roleType from email contacts ms: " + session(s"roleTypeFromEmailContactsMsCall").as[String])
                     println("role contact siteId from email contacts ms: " + session("roleSiteIdFromEmailContactsMsCall").as[String])
                     println("role contact siteName from email contacts ms: " + session("roleSiteNameFromEmailContactsMsCall").as[String])
                     println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                     session
                }) 
                
                .exec(session => {
                  println("Value of isSiteIdFound after getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsShowOnPortalYes req21: " +   session("isSiteIdFound").as[String])
                  session
                })
                
                .exec(session => {
                    session.remove("isEmailTagFound")

                })
                  
            }      
          
          //clean cookies to get new jsession for next exec call
         .exec(flushSessionCookies)  
    
 		  }/**End Checking for all SiteContacts Records**/
      
    
      /**Checking for all Site Contacts Not On Ticket Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForSiteContactsNotOnTicket"){//"${numberOfRecordsForSiteContactsNotOnTicket}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
              var indexRepeatNumberOfRecordsForSiteContactsNotOnTicket: Int = session("indexRepeatNumberOfRecordsForSiteContactsNotOnTicket").as[Int]
              session.set("indexRepeatNumberOfRecordsForSiteContactsNotOnTicket", indexRepeatNumberOfRecordsForSiteContactsNotOnTicket) //index saved on session variable
              val contactEmailUserIdResponse = session("allUserIdsForSiteContactsNotOnTicket").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContactsNotOnTicket)
              session.set("contactUserIdResponse", session("allUserIdsForSiteContactsNotOnTicket").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContactsNotOnTicket))
        
          })  
      
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)  
      
          //Get Contact info for one specific contact (using emailContacts_ms)
          .exec(getEmailContactsToGetMainValuesForFutureCheck(req28, js28, emailContactsMsForContactRolesForVSOCPortalURN, siteContactsNotOnTicketGroupName, "${indexRepeatNumberOfRecordsForSiteContactsNotOnTicket}"))  
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)
          
          //transform contactInfoContactNameFromGetEmailContactsCall
          .exec({session =>
                    var contactNameToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactNameToTransform)
               })
    
          //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
          .exec(getContactInfoFromServices(req29, js29))  
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)
    
          //set indexRepeatNumberOfRecords to session variable
          .exec(session => {

              println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
              println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
              println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
              println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
              println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
              println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
              println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
              println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
              println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
              println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
              println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
              println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
              session
          })
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)

          //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
          .exec(getRolesForVSOCPortalYesForSiteContactsNotOnTicketGroup(req30, js30, "${contactUserIdResponse}"))  
          
          //set numberOfRolesFromServices excluding numberOfRolesFromServicesWithSiteIdAtlanta
          .exec(session => {                   
                     println("Site not on ticket")
                     println("Test")
                     println("Sites siteID: " + session("sitesSiteId").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MFSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=VMSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MDPAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MFSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=VMSAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MDPAlllRoles: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MFSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=VMSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All Roles and roleType=MDPAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MFSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=VMSAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With roleName=All roles and roleType=MDPAlllRoles WithRankZero: " + session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero").as[String])
                     println("Number Of Roles From Services With rank=0: " + session("numberOfRolesFromServicesWithRankZero").as[String])
                     println("Number Of Roles From Services Before Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
                
          .exec(session => {                   
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero: Int = session("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero").as[Int]
                     var numberOfRolesFromServicesWithRankZero: Int = session("numberOfRolesFromServicesWithRankZero").as[Int] - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero  
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles - numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles - numberOfRolesFromServicesWithRankZero
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
                
          .exec(session => {             
                     println("Site not on ticket")
                     println("Number Of Roles From Services After Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
           //END set numberOfRolesFromServices excluding numberOfRolesFromServicesWithSiteIdAtlanta
    
          .repeat("${numberOfRolesFromServices}", "index"){
        
              //set the counter variable for loop
              exec {session =>
                  val offsetCounter = session("index").as[Int]
                  val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                  session.set("offsetCounter", offsetCounter)
                  session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
              }
        
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //Get Contact info for one specific contact (using emailContacts_ms)
               //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
               .exec(getRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsNotOnTicketGroupName(req31, js31, emailContactsMsForContactRolesForVSOCPortalURN, siteContactsNotOnTicketGroupName, "${indexRepeatNumberOfRecordsForSiteContactsNotOnTicket}"))
            
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //transfor contact name
               .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")                  
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    
                })
                
                .exec({session =>
                    
                    var roleContactNameFromEmailContactsMsCallToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameFromEmailContactsMsCallToTransform = roleContactNameFromEmailContactsMsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")                
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameFromEmailContactsMsCallToTransform)

                })
                
                .exec({session =>
                    var roleNameFromEmailContactsMsCallToTransform: String = session("roleNameFromEmailContactsMsCall").as[String]
                    roleNameFromEmailContactsMsCallToTransform = roleNameFromEmailContactsMsCallToTransform.replaceAll("roles", "Roles")
                    session.set("roleNameFromEmailContactsMsCall",roleNameFromEmailContactsMsCallToTransform)
                })
                
                .exec({session =>
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)

                })
               /**.exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    
                    var roleNameFromEmailContactsMsCallToTransform: String = session("roleNameFromEmailContactsMsCall").as[String]
                    roleNameFromEmailContactsMsCallToTransform = roleNameFromEmailContactsMsCallToTransform.replaceAll("roles", "Roles")
                    
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
                    session.set("roleNameFromEmailContactsMsCall",roleNameFromEmailContactsMsCallToTransform)
               })**/
            
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //Get contact roles from services rest call
               .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsNotOnTicketGroupNameShowOnPortalYes(req32, js32, "${contactUserIdResponse}"))
             
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
              
               //print the variables to log to debug if needed
               .exec(session => {
                    println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                    println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                    println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                    println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                    println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                    println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                    println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                    println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                    println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                    println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                    println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                    println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                    println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                    println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                    println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                    println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                    println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                    println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                    println("role contact roleType from email contacts ms: " + session("roleTypeFromEmailContactsMsCall").as[String])
                    println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                    session
               })  
                  
          }      
          
       //clean cookies to get new jsession for next exec call
       .exec(flushSessionCookies)     
    
 		  } /**End Checking for all Site Contacts Not On Ticket Records**/  
  
  
  /***uncomment the code below if need to get values from Remedy.. the code below releases token from Remedy
    .exec(http("Release Token on Remedy")
		.post("https://stage-remedy.sec.ibm.com:8443/api/jwt/logout")
		.header("Authorization", "${remedyToken}"))
		*/
		
		//clean cookies to get new jsession for next exec call
    .exec(flushSessionCookies)
  
    .exec({session =>
        println("*****END OF step2 of teste -> request with deviceIds, VSOC_Portal*****")
        session
       }) 
       
    /***************END OF step2 of teste -> request with deviceIds, VSOC_Portal*************************/      
       
 /***************START OF step3 of teste -> request with deviceIds, SOC_Console_OR_AI*************************/
  
  .exec({session =>
        println("*****START OF step3 of teste -> request with deviceIds, SOC_Console_OR_AI*****")
        session
       }) 
  
   //getEmailContacts to get Number of Records ${numberOfRecords} for step1 of teste -> request with deviceIds, no vsoc_portal, no soc_console_or_AI 
   .exec(getEmailContactsToGetNumberOfRecords(req33, js33, emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN)) 
    
   //clean cookies to get new jsession for next exec call
   .exec(flushSessionCookies)    
   
      /**Checking for all Global Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForGlobal"){//"${numberOfRecordsForGlobal}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
            var indexRepeatNumberOfRecordsForGlobal: Int = session("indexRepeatNumberOfRecordsForGlobal").as[Int]
            session.set("indexRepeatNumberOfRecordsForGlobal", indexRepeatNumberOfRecordsForGlobal) //index saved on session variable
            val contactEmailUserIdResponse = session("allUserIdsForGlobal").as[Seq[String]].apply(indexRepeatNumberOfRecordsForGlobal)
            session.set("contactUserIdResponse", session("allUserIdsForGlobal").as[Seq[String]].apply(indexRepeatNumberOfRecordsForGlobal))
        
            })  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)  
      
        //Get Contact info for one specific contact (using emailContacts_ms)
        .exec(getEmailContactsToGetMainValuesForFutureCheck(req34, js34, emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN, globalContactsGroupName, "${indexRepeatNumberOfRecordsForGlobal}"))  
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
        
        
        //transform contactInfoContactNameFromGetEmailContactsCall to remove special characters
        .exec({session =>
                    
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)

                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)
                })     
                
        //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
        .exec(getContactInfoFromServices(req35, js35))  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
    
        //print variables to debub if needed
        .exec(session => {

            println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
            println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
            println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
            println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
            println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
            println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
            println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
            println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
            println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
            println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
            println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
            println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
            session
        })
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)

        //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
        .exec(getRolesForVSOCPortalNoForGlobalContactsGroup(req36, js36, "${contactUserIdResponse}"))           
        
        //exclude number of roles with rank=0 of the role checking
        .exec(session => {                  
                     var numberOfRolesFromServicesWithRankZero: Int = session("numberOfRolesFromServicesWithRankZero").as[Int] 
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithRankZero
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
    
            .repeat("${numberOfRolesFromServices}", "index"){
        
                exec {session =>
                    val offsetCounter = session("index").as[Int]
                    val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                    session.set("offsetCounter", offsetCounter)
                    session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
                }
        
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get Contact info for one specific contact (using emailContacts_ms)
                //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
                .exec(getRolesValuesFromEmailContactsMsForSpecificContact(req37, js37, emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN, globalContactsGroupName, "${indexRepeatNumberOfRecordsForGlobal}"))
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
                 
                //transfor contact name
                .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameFromEmailContactsMsCallToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameFromEmailContactsMsCallToTransform = roleContactNameFromEmailContactsMsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var contactInfoContactNameFromGetEmailContactsCallToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactInfoContactNameFromGetEmailContactsCallToTransform = contactInfoContactNameFromGetEmailContactsCallToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactInfoContactNameFromGetEmailContactsCallToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameFromEmailContactsMsCallToTransform)
                })
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get contact roles from services rest call
                .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMs(req38, js38,"${contactUserIdResponse}"))
             
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
              
                .exec(session => {
                     println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                     println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                     println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                     println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                     println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                     println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                     println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                     println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                     println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                     println("role contact siteId from services: " + session("roleSiteIdFromServicesCall").as[String])
                     println("role contact siteName from services: " + session("roleSiteNameFromServicesCall").as[String])
                     println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                     println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                     println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                     println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                     println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                     println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                     println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                     println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                     println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                     println("role contact roleType from email contacts ms: " + session(s"roleTypeFromEmailContactsMsCall").as[String])
                     println("role contact siteId from email contacts ms: " + session("roleSiteIdFromEmailContactsMsCall").as[String])
                     println("role contact siteName from email contacts ms: " + session("roleSiteNameFromEmailContactsMsCall").as[String])
                     println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                     session
                })  
                  
            }      
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)  
    
 		  }/**End Checking for all Global Records**/  
    
      /**Checking for all SiteContacts Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForSiteContacts"){//"${numberOfRecordsForSiteContacts}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
            var indexRepeatNumberOfRecordsForSiteContacts: Int = session("indexRepeatNumberOfRecordsForSiteContacts").as[Int]
            session.set("indexRepeatNumberOfRecordsForSiteContacts", indexRepeatNumberOfRecordsForSiteContacts) //index saved on session variable
            val contactEmailUserIdResponse = session("allUserIdsForSiteContacts").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContacts)
            session.set("contactUserIdResponse", session("allUserIdsForSiteContacts").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContacts))
        
          })  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)  
      
        //Get Contact info for one specific contact (using emailContacts_ms)
        .exec(getEmailContactsToGetMainValuesForFutureCheck(req39, js39, emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN, siteContactsGroupName, "${indexRepeatNumberOfRecordsForSiteContacts}"))  
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
        
        //transform contactInfoContactNameFromGetEmailContactsCall
          .exec({session =>
                    var contactNameToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactNameToTransform)
               })      
    
        //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
        .exec(getContactInfoFromServices(req40, js40))  
      
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)
    
        //set indexRepeatNumberOfRecords to session variable
        .exec(session => {

            println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
            println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
            println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
            println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
            println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
            println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
            println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
            println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
            println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
            println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
            println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
            println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
            session
        })
    
        //clean cookies to get new jsession for next exec call
        .exec(flushSessionCookies)

        //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
        .exec(getRolesForVSOCPortalNoForSiteContactsGroup(req41, js41, "${contactUserIdResponse}"))   
        
        //exclude number of roles with rank=0 of the role checking
        .exec(session => {                  
                     var numberOfRolesFromServicesWithRankZero: Int = session("numberOfRolesFromServicesWithRankZero").as[Int] 
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithRankZero
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
    
            .repeat("${numberOfRolesFromServices}", "index"){
        
                exec {session =>
                    val offsetCounter = session("index").as[Int]
                    val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                    session.set("offsetCounter", offsetCounter)
                    session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
                }
        
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get Contact info for one specific contact (using emailContacts_ms)
                //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
                .exec(getRolesValuesFromEmailContactsMsForSpecificContact(req42, js42, emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN, siteContactsGroupName, "${indexRepeatNumberOfRecordsForSiteContacts}"))
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
                 
                //transfor contact name
                .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
                })
            
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
            
                //Get contact roles from services rest call
                .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMs(req43, js43, "${contactUserIdResponse}"))
             
                //clean cookies to get new jsession for next exec call
                .exec(flushSessionCookies)
              
                //print the values to log to debug if needed
                .exec(session => {
                     println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                     println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                     println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                     println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                     println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                     println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                     println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                     println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                     println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                     println("role contact siteId from services: " + session("roleSiteIdFromServicesCall").as[String])
                     println("role contact siteName from services: " + session("roleSiteNameFromServicesCall").as[String])
                     println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                     println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                     println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                     println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                     println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                     println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                     println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                     println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                     println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                     println("role contact roleType from email contacts ms: " + session(s"roleTypeFromEmailContactsMsCall").as[String])
                     println("role contact siteId from email contacts ms: " + session("roleSiteIdFromEmailContactsMsCall").as[String])
                     println("role contact siteName from email contacts ms: " + session("roleSiteNameFromEmailContactsMsCall").as[String])
                     println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                     session
                })  
                  
            }      
          
          //clean cookies to get new jsession for next exec call
         .exec(flushSessionCookies)  
    
 		  }/**End Checking for all SiteContacts Records**/
      
    
      /**Checking for all Site Contacts Not On Ticket Records**/
      .repeat(2, "indexRepeatNumberOfRecordsForSiteContactsNotOnTicket"){//"${numberOfRecordsForSiteContactsNotOnTicket}", "indexRepeatNumberOfRecords"){
    
          //set indexRepeatNumberOfRecords to session variable
          exec(session => {
              var indexRepeatNumberOfRecordsForSiteContactsNotOnTicket: Int = session("indexRepeatNumberOfRecordsForSiteContactsNotOnTicket").as[Int]
              session.set("indexRepeatNumberOfRecordsForSiteContactsNotOnTicket", indexRepeatNumberOfRecordsForSiteContactsNotOnTicket) //index saved on session variable
              val contactEmailUserIdResponse = session("allUserIdsForSiteContactsNotOnTicket").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContactsNotOnTicket)
              session.set("contactUserIdResponse", session("allUserIdsForSiteContactsNotOnTicket").as[Seq[String]].apply(indexRepeatNumberOfRecordsForSiteContactsNotOnTicket))
        
          })  
      
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)  
      
          //Get Contact info for one specific contact (using emailContacts_ms)
          .exec(getEmailContactsToGetMainValuesForFutureCheck(req44, js44, emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN, siteContactsNotOnTicketGroupName, "${indexRepeatNumberOfRecordsForSiteContactsNotOnTicket}"))  
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)
          
          //transform contactInfoContactNameFromGetEmailContactsCall
          .exec({session =>
                    var contactNameToTransform: String = session("contactInfoContactNameFromGetEmailContactsCall").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    session.set("contactInfoContactNameFromGetEmailContactsCall",contactNameToTransform)
               })
    
          //Get Contact Info from Services Call to grab contact variables and compare with emailcontacts_ms variables
          .exec(getContactInfoFromServices(req45, js45))  
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)
    
          //set indexRepeatNumberOfRecords to session variable
          .exec(session => {

              println("contact contactId from services: " + session("contactInfoContactIdFromServices").as[String])
              println("contact contactName from services: " + session("contactInfoFullNameFromServices").as[String])
              println("contact email from services: " + session("contactInfoEmailFromServices").as[String])
              println("contact phoneNumber from services: " + session("contactInfoPhoneNumberFromServices").as[String])
              println("contact global from services: " + session("contactInfoGlobalFromServices").as[String])
              println("contact locale from services: " + session("contactInfoLocaleFromServices").as[String])
            
              println("contact contactId from emailContacts call: " + session("contactInfoUserIdFromGetEmailContactsCall").as[String])
              println("contact contactName from emailContacts call: " + session("contactInfoContactNameFromGetEmailContactsCall").as[String])
              println("contact email from emailContacts call: " + session("contactInfoEmailFromGetEmailContactsCall").as[String])
              println("contact phoneNumber from emailContacts call: " + session("contactInfoPhoneNumberFromGetEmailContactsCall").as[String])
              println("contact global from emailContacts call: " + session("contactInfoGlobalFromGetEmailContactsCall").as[String])
              println("contact locale from emailContacts call: " + session("contactInfoLocaleFromGetEmailContactsCall").as[String])
              session
          })
    
          //clean cookies to get new jsession for next exec call
          .exec(flushSessionCookies)

          //Get Roles from Services Call  to get number of roles ${numberOfRolesFromServices}
          .exec(getRolesForVSOCPortalNoForSiteContactsNotOnTicketGroup(req46, js46, "${contactUserIdResponse}"))  
          
          //set numberOfRolesFromServices excluding numberOfRolesFromServicesWithSiteIdAtlanta
          .exec(session => {                   
                     println("Number Of Roles From Services With Site Id Atlanta: " + session("numberOfRolesFromServicesWithSiteIdAtlanta").as[String])
                     println("Number Of Roles From Services Before Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
                
          .exec(session => {                   
                     var numberOfRolesFromServicesWithSiteIdAtlanta: Int = session("numberOfRolesFromServicesWithSiteIdAtlanta").as[Int]
                     var numberOfRolesFromServices: Int = session("numberOfRolesFromServices").as[Int]
                     var numberOfRolesFromServicesAfterSet: Integer = numberOfRolesFromServices - numberOfRolesFromServicesWithSiteIdAtlanta
                     println("Number Of Roles From Services Just After Set In The Middle Method: " + numberOfRolesFromServicesAfterSet.toString())
                     session.set("numberOfRolesFromServices", numberOfRolesFromServicesAfterSet)
                })
                
          .exec(session => {                   
                     println("Number Of Roles From Services After Set: " + session("numberOfRolesFromServices").as[String])
                     session
                })
           //END set numberOfRolesFromServices excluding numberOfRolesFromServicesWithSiteIdAtlanta
    
          .repeat("${numberOfRolesFromServices}", "index"){
        
              //set the counter variable for loop
              exec {session =>
                  val offsetCounter = session("index").as[Int]
                  val indexRepeatNumberOfRolesFromServices = session("index").as[String]
                  session.set("offsetCounter", offsetCounter)
                  session.set("indexRepeatNumberOfRolesFromServices", indexRepeatNumberOfRolesFromServices)
              }
        
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //Get Contact info for one specific contact (using emailContacts_ms)
               //.exec(getEmailContacts(req01, js01, emailContactsMsForDeviceIdsURN))           
            
               .exec(getRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsNotOnTicketGroupName(req47, js47, emailContactsMsForCustomerContactRolesForSOCConsoleORAIURN, siteContactsNotOnTicketGroupName, "${indexRepeatNumberOfRecordsForSiteContactsNotOnTicket}"))
            
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //transfor contact name
               .exec({session =>
                    var contactNameToTransform: String = session("contactInfoFullNameFromServices").as[String]
                    contactNameToTransform = contactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    var roleContactNameToTransform: String = session("roleContactNameFromEmailContactsMsCall").as[String]
                    roleContactNameToTransform = roleContactNameToTransform.replaceAll("""[^a-zA-Z0-9]""", "")
                    //some names comes with special characters like chinese ones.. remove the comment on next line to debug the name that comes from emailcontact_ms call
                    //println("value of contactNameToTransform :" + contactNameToTransform)
                    session.set("contactInfoFullNameFromServices",contactNameToTransform)
                    session.set("roleContactNameFromEmailContactsMsCall",roleContactNameToTransform)
               })
            
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
            
               //Get contact roles from services rest call
               .exec(getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsNotOnTicketGroupName(req48, js48, "${contactUserIdResponse}"))
             
               //clean cookies to get new jsession for next exec call
               .exec(flushSessionCookies)
              
               //print the variables to log to debug if needed
               .exec(session => {
                    println("role contact id from services: " + session("roleIdFromServicesCall").as[String])
                    println("role contact roleName from services: " + session("roleNameFromServicesCall").as[String])
                    println("role contact contactId from services: " + session("roleContactIdFromServicesCall").as[String])
                    println("role contact contactName from services: " + session("roleContactNameFromServicesCall").as[String])
                    println("role contact serviceLine from services: " + session("roleServiceLineFromServicesCall").as[String])
                    println("role contact customerId from services: " + session("roleCustomerIdFromServicesCall").as[String])
                    println("role contact customerName from services: " + session("roleCustomerNameFromServicesCall").as[String])
                    println("role contact partnerId from services: " + session("rolePartnerIdFromServicesCall").as[String])
                    println("role contact roleType from services: " + session(s"roleTypeFromServicesCall").as[String])
                    println("role contact rank from services: " + session("roleRankFromServicesCall").as[String])
            
                    println("role contact id from email contacts ms: " + session("roleIdFromEmailContactsMsCall").as[String])
                    println("role contact roleName from email contacts ms: " + session("roleNameFromEmailContactsMsCall").as[String])
                    println("role contact contactId from email contacts ms: " + session("roleContactIdFromEmailContactsMsCall").as[String])
                    println("role contact contactName from email contacts ms: " + session("roleContactNameFromEmailContactsMsCall").as[String])
                    println("role contact serviceLine from email contacts ms: " + session("roleServiceLineFromEmailContactsMsCall").as[String])
                    println("role contact customerId from email contacts ms: " + session("roleCustomerIdFromEmailContactsMsCall").as[String])
                    println("role contact customerName from email contacts ms: " + session("roleCustomerNameFromEmailContactsMsCall").as[String])
                    println("role contact partnerId from email contacts ms: " + session("rolePartnerIdFromEmailContactsMsCall").as[String])
                    println("role contact roleType from email contacts ms: " + session("roleTypeFromEmailContactsMsCall").as[String])
                    println("role contact rank from email contacts ms: " + session("roleRankFromEmailContactsMsCall").as[String])
                    session
               })  
                  
          }      
          
       //clean cookies to get new jsession for next exec call
       .exec(flushSessionCookies)     
    
 		  } /**End Checking for all Site Contacts Not On Ticket Records**/  
  
  
  /***uncomment the code below if need to get values from Remedy.. the code below releases token from Remedy
    .exec(http("Release Token on Remedy")
		.post("https://stage-remedy.sec.ibm.com:8443/api/jwt/logout")
		.header("Authorization", "${remedyToken}"))
		*/
		
		//clean cookies to get new jsession for next exec call
    .exec(flushSessionCookies)
    
    .exec({session =>
        println("*****END OF step3 of teste -> request with deviceIds, soc_console_or_AI*****")
        session
       }) 
       
    /***************END OF step3 of teste -> request with deviceIds, soc_console_or_AI*************************/   
 
      
    /***************Start NOF Check Auto Escalation Contacts*************************/  
    
    .exec({session =>
        println("*****START OF Check Auto Escalation Contacts*****")
        session
       })    
    .exec(http(req49)
      .get("micro/emailcontacts/auto_escalation_contacts?customerId=CID001696&deviceIds=P00000008028160&serviceLine=Managed SIEM&rulePolicyAttr_emailGlobalContacts=true&rulePolicyAttr_emailSiteContacts=2&rulePolicyAttr_emailOtherContacts=true&rulePolicyAttr_emailOtherContactsList=test,test@test.com")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(200))
      //.check(jsonPath("$..message").is("Parameters not valid."))
      .check(jsonPath("$..contacts").exists)
    )
    .exec({session =>
        println("*****END OF Check Auto Escalation Contact*****")
        session
       })
       
    /***************END OF Check Auto Escalation Contact*************************/ 
  
    /***************Start OF Negative Scenario 1 - No parameters*************************/  
    
    .exec({session =>
        println("*****START OF Negative Scenario 1 - No parameters*****")
        session
       })    
    .exec(http(req50)
      .get("micro/emailcontacts/")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(400))
      .check(jsonPath("$..message").is("Parameters not valid."))
      .check(jsonPath("$..errors").exists)
    )
    .exec({session =>
        println("*****END OF Negative Scenario 1 - No parameters*****")
        session
       })
       
    /***************END OF Negative Scenario 1 - No parameters*************************/
         
  
    /***************Start OF Negative Scenario 2 - Invalid Customer ID*************************/  
    .exec({session =>
        println("*****START OF Negative Scenario 2 - Invalid Customer ID*****")
        session
       })    
    .exec(http(req51)
      .get("micro/emailcontacts/CID111111")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(500))
      .check(jsonPath("$..message").is("Failed to find email contacts."))
    )
    .exec({session =>
        println("*****END OF Negative Scenario 2 - Invalid Customer ID*****")
        session
       })
       
    /***************END OF Negative Scenario 2 - Invalid Customer ID*************************/   
       
   
  
    /***************Start OF Negative Scenario 3 - Invalid parameter*************************/  
    .exec({session =>
        println("*****START OF Negative Scenario 3 - Invalid parameter*****")
        session
       })    
    .exec(http(req52)
      .get("micro/emailcontacts/SS%$%")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(400))
    )
    .exec({session =>
        println("*****END OF Negative Scenario 3 - Invalid parameter*****")
        session
       })
       
    /***************END OF Negative Scenario 3 - Invalid parameter*************************/ 
    
  
    /***************START Check rulePolicyAttr_serviceLine set Managed Intrusion Detection*************************/  
    
    .exec({session =>
        println("*****START Check rulePolicyAttr_serviceLine set Managed Intrusion Detection*****")
        session
       })    
    .exec(http(req53)
      .get("micro/emailcontacts/api/v1?customerId=P000000614&deviceIds=P00000008041909&serviceLine=Managed Intrusion Detection")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(200))
      .check(jsonPath("$[*]['GlobalContacts'][?(@.userRoles[0].serviceLine==\"Managed Intrusion Detection\")].defaultEmailFlag").is("true"))
      .check(jsonPath("$[*]['SiteContacts'][?(@.userRoles[0].serviceLine==\"Managed Intrusion Detection\")].defaultEmailFlag").is("true"))
      .check(jsonPath("$[*]['Site Contacts Not On Ticket'][?(@.userRoles[0].serviceLine==\"Managed Intrusion Detection\")].defaultEmailFlag").is("true"))
    )
    .exec({session =>
        println("*****END Check rulePolicyAttr_serviceLine set Managed Intrusion Detection*****")
        session
       })
       
    /***************END Check rulePolicyAttr_serviceLine set Managed Intrusion Detection*************************/
        
       
    /***************Start Check rulePolicyAttr_emailGlobalContacts=<number>*************************/  
    
    .exec({session =>
        println("*****START Check rulePolicyAttr_emailGlobalContacts=<number>*****")
        session
       })    
    .exec(http(req54)
      .get("micro/emailcontacts/api/v1?customerId=P000000614&deviceIds=P00000008041909&rulePolicyAttr_emailGlobalContacts=1")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(200))
      .check(jsonPath("$..GlobalContacts[?(@.ranks[0]==1)].defaultEmailFlag").find.optional.saveAs("isGlobalContactWithRankEqualExist"))
      .check(checkIf("${isGlobalContactWithRankEqualExist.exists()}"){jsonPath("$..GlobalContacts[?(@.ranks[0]==1)].defaultEmailFlag").is("true")})
      .check(jsonPath("$..GlobalContacts[?(@.ranks[0]!=1)].defaultEmailFlag").find.optional.saveAs("isGlobalContactWithRankNotEqualExist"))
      .check(checkIf("${isGlobalContactWithRankNotEqualExist.exists()}"){jsonPath("$..GlobalContacts[?(@.ranks[0]!=1)].defaultEmailFlag").is("false")})
    )
    .exec({session =>
        println("*****END Check rulePolicyAttr_emailGlobalContacts=<number>*****")
        session
       })
       
    /***************END Check rulePolicyAttr_emailGlobalContacts=<number>*************************/
   
       
    /***************Start Check rulePolicyAttr_emailGlobalContacts=true*************************/  
    
    .exec({session =>
        println("*****START Check rulePolicyAttr_emailGlobalContacts=true*****")
        session
       })    
    .exec(http(req55)
      .get("micro/emailcontacts/api/v1?customerId=P000000614&deviceIds=P00000008041909&rulePolicyAttr_emailGlobalContacts=true")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(200))
      //.check(jsonPath("$..message").is("Parameters not valid."))
      .check(jsonPath("$..SiteContacts[*].defaultEmailFlag").is("true"))
      .check(jsonPath("$..GlobalContacts[*].defaultEmailFlag").is("true"))
    )
    .exec({session =>
        println("*****END Check rulePolicyAttr_emailGlobalContacts=true*****")
        session
       })
       
    /***************END Check rulePolicyAttr_emailGlobalContacts=true*************************/   
       
     
    /***************Start Check rulePolicyAttr_emailSiteContacts=true*************************/  
    
    .exec({session =>
        println("*****START Check rulePolicyAttr_emailSiteContacts=true*****")
        session
       })    
    .exec(http(req56)
      .get("micro/emailcontacts/api/v1?customerId=P000000614&deviceIds=P00000008041909&rulePolicyAttr_emailSiteContacts=true")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(200))
      .check(jsonPath("$..SiteContacts[*].defaultEmailFlag").is("true"))
      .check(jsonPath("$..GlobalContacts[*].defaultEmailFlag").is("true"))
    )
    .exec({session =>
        println("*****END Check rulePolicyAttr_emailSiteContacts=true*****")
        session
       })
       
    /***************END Check rulePolicyAttr_emailSiteContacts=true*************************/   
       
      
    /***************Start Check rulePolicyAttr_emailSiteContacts=<number>*************************/  
    
    .exec({session =>
        println("*****START Check rulePolicyAttr_emailSiteContacts=<number>*****")
        session
       })    
    .exec(http(req57)
      .get("micro/emailcontacts/api/v1?customerId=P000000614&deviceIds=P00000008041909&rulePolicyAttr_emailSiteContacts=1")
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(200))
      .check(jsonPath("$..SiteContacts[?(@.ranks[0]==1)].defaultEmailFlag").find.optional.saveAs("isSiteContactsWithRankEqualExist"))
      .check(checkIf("${isSiteContactsWithRankEqualExist.exists()}"){jsonPath("$..SiteContacts[?(@.ranks[0]==1)].defaultEmailFlag").is("true")})
      .check(jsonPath("$..SiteContacts[?(@.ranks[0]==1)].defaultEmailFlag").find.optional.saveAs("isSiteContactsWithRankNotEqualExist"))
      .check(checkIf("${isSiteContactsWithRankNotEqualExist.exists()}"){jsonPath("$..SiteContacts[?(@.ranks[0]!=1)].defaultEmailFlag").is("false")})
    )
    .exec({session =>
        println("*****END Check rulePolicyAttr_emailSiteContacts=<number>*****")
        session
       })
       
    /***************END Check rulePolicyAttr_emailSiteContacts=<number>*************************/    
 
    //Exporting all jsession ids
    .exec(session => {  
      println(session("allUserIds"))//.as[Seq[String]].apply(0))
      println(session("numberOfRecords").as[Integer])
      val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
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
      jsessionMap += (req47 -> session(js47).as[String])
      jsessionMap += (req48 -> session(js48).as[String])
      jsessionMap += (req49 -> session(js49).as[String])
      jsessionMap += (req50 -> session(js50).as[String])
      jsessionMap += (req51 -> session(js51).as[String])
      jsessionMap += (req52 -> session(js52).as[String])
      jsessionMap += (req53 -> session(js53).as[String])
      jsessionMap += (req54 -> session(js54).as[String])
      jsessionMap += (req55 -> session(js55).as[String])
      jsessionMap += (req56 -> session(js56).as[String])
      jsessionMap += (req57 -> session(js57).as[String])
      val writer = new PrintWriter(new File(jsessionFileName))
      writer.write(write(jsessionMap))
      writer.close()
      session
    })
    
 
       
  //Setup Method to execute the scenario
  setUp(getEmailContactsMsScenario.inject(atOnceUsers(1))
    .protocols(httpProtocolEmailContactsMs))
    .assertions(global.failedRequests.count.is(0))

  //GetEmailContacts method to get number of Records and userIds
  def getEmailContactsToGetNumberOfRecords(scenarioName: String, jsessionName: String, emailContactsURN: String) = {
    http(scenarioName)
      .get(emailContactsURN)
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..GlobalContacts[*].userId").findAll.optional.saveAs("allUserIdsForGlobal"))
      .check(jsonPath("$..GlobalContacts[*].userId").count.optional.saveAs("numberOfRecordsForGlobal"))
      .check(jsonPath("$..SiteContacts[*].userId").findAll.optional.saveAs("allUserIdsForSiteContacts"))
      .check(jsonPath("$..SiteContacts[*].userId").count.optional.saveAs("numberOfRecordsForSiteContacts"))
      .check(jsonPath("$[*]['Site Contacts Not On Ticket'][*].userId").findAll.optional.saveAs("allUserIdsForSiteContactsNotOnTicket"))
      .check(jsonPath("$[*]['Site Contacts Not On Ticket'][*].userId").count.optional.saveAs("numberOfRecordsForSiteContactsNotOnTicket"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
    
  def getEmailContactsToGetMainValuesForFutureCheck(scenarioName: String, jsessionName: String, emailContactsURN: String, groupName: String, indexRepeatNumberOfRecords: String) = {
    http(scenarioName)
      .get(emailContactsURN)
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
          
      //get the number of Roles from email contats ms 
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[*].id").count.saveAs("numberOfRolesFromEmailContactsMs"))
                
      //save the values from contact to compare with the ones on services rest call
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userId").saveAs("contactInfoUserIdFromGetEmailContactsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].contactName").saveAs("contactInfoContactNameFromGetEmailContactsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].email").saveAs("contactInfoEmailFromGetEmailContactsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].phoneNumber").saveAs("contactInfoPhoneNumberFromGetEmailContactsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].global").saveAs("contactInfoGlobalFromGetEmailContactsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].locale").saveAs("contactInfoLocaleFromGetEmailContactsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].sites[0].siteId").optional.saveAs("sitesSiteId"))
      //get jession number for call
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))    
      
  }  
  
  //GetEmailContacts method to check the blank values and get the values from response to match with the ones from Services call
  def getEmailContactsToCheckDeviceIdsOnly(scenarioName: String, jsessionName: String, emailContactsURN: String) = {
    http(scenarioName)
      .get(emailContactsURN)
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      
      //Check that the global contact has no site (only site defined for specific role)
      .check(jsonPath("$[*].GlobalContacts[*].sites[*].siteId").findAll.is(""))   
      .check(jsonPath("$[*].GlobalContacts[*].sites[*].siteName").findAll.is(""))
      .check(jsonPath("$[*].GlobalContacts[*].sites[*].status").findAll.is("0"))
      
       //Check that site Ids and names of Site Contacts Not On Ticket are the same as siteIds and site names of device
      .check(jsonPath("$[*].'Site Contacts Not On Ticket'[*].sites[*].siteName").findAll.is(""))
      .check(jsonPath("$[*].'Site Contacts Not On Ticket'[*].sites[*].status").findAll.is("0"))
      
      //Check that site Ids and names of site contacts are the same as siteIds and site names of device
      .check(jsonPath("$[*].SiteContacts[*].sites[*].siteName").findAll.is(""))
      .check(jsonPath("$[*].SiteContacts[*].sites[*].status").findAll.is("0"))
             
      //get jession number for call
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
    def backUp(scenarioName: String, jsessionName: String, emailContactsURN: String, indexRepeatNumberOfRecords: String) = {
    http(scenarioName)
      .get(emailContactsURN)
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
          
      //get the number of Roles from email contats ms 
      .check(jsonPath("$..GlobalContacts[" + "${indexRepeatNumberOfRecords}" + "].userRoles[*].id").count.saveAs("numberOfRolesFromEmailContactsMs"))
                
      //save the values from contact to compare with the ones on services rest call
      .check(jsonPath("$..GlobalContacts[" + indexRepeatNumberOfRecords + "].userId").saveAs("contactInfoUserIdFromGetEmailContactsCall"))
      .check(jsonPath("$..GlobalContacts[" + indexRepeatNumberOfRecords + "].contactName").saveAs("contactInfoContactNameFromGetEmailContactsCall"))
      .check(jsonPath("$..GlobalContacts[" + indexRepeatNumberOfRecords + "].email").saveAs("contactInfoEmailFromGetEmailContactsCall"))
      .check(jsonPath("$..GlobalContacts[" + indexRepeatNumberOfRecords + "].phoneNumber").saveAs("contactInfoPhoneNumberFromGetEmailContactsCall"))
      .check(jsonPath("$..GlobalContacts[" + indexRepeatNumberOfRecords + "].global").saveAs("contactInfoGlobalFromGetEmailContactsCall"))
      .check(jsonPath("$..GlobalContacts[" + indexRepeatNumberOfRecords + "].locale").saveAs("contactInfoLocaleFromGetEmailContactsCall"))
      
      //get jession number for call
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))    
      
  }
 
  
  
  //GetEmailContacts method to check the blank values
  def getRolesValuesFromEmailContactsMsForSpecificContact(scenarioName: String, jsessionName: String, emailContactsURN: String, groupName: String, indexRepeatNumberOfRecords: String) = {
    http(scenarioName)
      .get(emailContactsURN)
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
          
      //compare and match the values from contact email ms with the ones on services rest call
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].id").saveAs("roleIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].roleName").saveAs("roleNameFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].contactId").saveAs("roleContactIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].contactName").saveAs("roleContactNameFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].serviceLine").saveAs("roleServiceLineFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].customerId").saveAs("roleCustomerIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].customerName").saveAs("roleCustomerNameFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].partnerId").saveAs("rolePartnerIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].roleType").saveAs("roleTypeFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].siteId").optional.saveAs("roleSiteIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].siteName").optional.saveAs("roleSiteNameFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].rank").saveAs("roleRankFromEmailContactsMsCall"))  
      
      //get jession number for call
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
   //GetEmailContacts method to check the blank values
  def getRolesValuesFromEmailContactsMsForSpecificContactForSiteContactsNotOnTicketGroupName(scenarioName: String, jsessionName: String, emailContactsURN: String, groupName: String, indexRepeatNumberOfRecords: String) = {
    http(scenarioName)
      .get(emailContactsURN)
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
          
      //compare and match the values from contact email ms with the ones on services rest call
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].id").saveAs("roleIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].roleName").saveAs("roleNameFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].contactId").saveAs("roleContactIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].contactName").saveAs("roleContactNameFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].serviceLine").saveAs("roleServiceLineFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].customerId").saveAs("roleCustomerIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].customerName").saveAs("roleCustomerNameFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].partnerId").saveAs("rolePartnerIdFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].roleType").saveAs("roleTypeFromEmailContactsMsCall"))
      .check(jsonPath("$[*]['" + groupName + "'][" + indexRepeatNumberOfRecords + "].userRoles[" + "${indexRepeatNumberOfRolesFromServices}" + "].rank").saveAs("roleRankFromEmailContactsMsCall"))  
      
      //get jession number for call
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  //Method to get contact infro from Remedy
  def getContactInfoFromRemedy(contactUserId: String) = {
    http("get from Remedy")
      .get("https://stage-remedy.sec.ibm.com:8443/api/arsys/v1/entry/HD:Customer-Contacts/" + contactUserId)
      .basicAuth(headerUsername, headerPassword)
      .header("Authorization","${remedyToken}")
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..values['Contact ID']").saveAs("remedyContactId"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  def getContactInfoFromServices(scenarioName: String, jsessionName: String) = {
    http(scenarioName)
      .get(baseURL + emailContactsMsRequestForCustomerContactOnServicesURN)
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].id").saveAs("contactInfoContactIdFromServices"))
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].fullName").saveAs("contactInfoFullNameFromServices"))
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].email").optional.saveAs("contactInfoEmailFromServices"))
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].phoneNumber").saveAs("contactInfoPhoneNumberFromServices"))
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].global").saveAs("contactInfoGlobalFromServices"))
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].locale").saveAs("contactInfoLocaleFromServices"))
      
      //check that the values are
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].id").is("${contactInfoUserIdFromGetEmailContactsCall}"))
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].fullName").transform(string => string.replace("?", "")).transform(string => string.replace("-", "")).transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).transform(string => string.replace("\\", "")).transform(string => string.replace("\u00e7", "")).is("${contactInfoContactNameFromGetEmailContactsCall}"))
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].email").find.optional.saveAs("isEmailTagFound"))
      .check(checkIf("${isEmailTagFound.exists()}"){(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].email").transform(string => string.replace(" ","")).is("${contactInfoEmailFromGetEmailContactsCall}"))})   
      //.check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].email").transform(string => string.replace(" ","")).is("${contactInfoEmailFromGetEmailContactsCall}")) //some emails from servicess call cames with blank spaces before and after email, needed to remove before checking
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].phoneNumber").is("${contactInfoPhoneNumberFromGetEmailContactsCall}"))
      //.check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].global").is("${contactInfoGlobalFromGetEmailContactsCall}")) this is being checked on getEmailContactsToGetMainValuesForFutureCheck method
      .check(jsonPath("$..items[?(@.id=='" + "${contactUserIdResponse}" + "')].locale").is("${contactInfoLocaleFromGetEmailContactsCall}"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  //Get roles for VSOC Portal = No (this is on DEV, PRD and STG.. for VSOC Portal = YES is not on PRD yet)
  def getRolesForVSOCPortalNoForGlobalContactsGroup(scenarioName: String, jsessionName: String, contactUserId: String) = {
    http(scenarioName)
      .get(baseURL + "/rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=NO" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..items[*].id").count.saveAs("numberOfRolesFromServices"))
      .check(jsonPath("$..items[*].id").findAll.saveAs("contactIds"))
      .check(jsonPath("$..[?(@.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRankZero"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
   //Get roles for VSOC Portal = No (this is on DEV, PRD and STG.. for VSOC Portal = YES is not on PRD yet)
  def getRolesForVSOCPortalYesForGlobalContactsGroup(scenarioName: String, jsessionName: String, contactUserId: String) = {
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=YES" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='MFS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='VMS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='MDP - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='MFS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='VMS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='MDP - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='MFS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='VMS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='MDP - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='MFS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='VMS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='MDP - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRankZero"))
      .check(jsonPath("$..items[*].id").count.saveAs("numberOfRolesFromServices"))
      .check(jsonPath("$..items[*].id").findAll.saveAs("contactIds"))

      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  //Get roles for VSOC Portal for Site Contacts Group = No (this is on DEV, PRD and STG.. for VSOC Portal = YES is not on PRD yet)
  def getRolesForVSOCPortalNoForSiteContactsGroup(scenarioName: String, jsessionName: String, contactUserId: String) = {
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=NO" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..items[?(@.siteName=='Atlanta')].id").count.saveAs("numberOfRolesFromServices"))
      .check(jsonPath("$..items[?(@.siteName=='Atlanta')].id").findAll.saveAs("contactIds"))
      .check(jsonPath("$..items[?(@.siteName=='Atlanta' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRankZero"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  def getRolesForVSOCPortalYesForSiteContactsGroup(scenarioName: String, jsessionName: String, contactUserId: String) = {
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=YES" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='MFS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='VMS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='MDP - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='MFS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='VMS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='MDP - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='MFS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='VMS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All Roles' && @.roleType=='MDP - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='MFS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='VMS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.roleName=='All roles' && @.roleType=='MDP - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRankZero"))
      .check(jsonPath("$..items[?(@.siteName=='Atlanta')].id").count.saveAs("numberOfRolesFromServices"))
      .check(jsonPath("$..items[?(@.siteName=='Atlanta')].id").findAll.saveAs("contactIds"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  def getRolesForVSOCPortalNoForSiteContactsNotOnTicketGroup(scenarioName: String, jsessionName: String, contactUserId: String) = {
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=NO" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..items[?(@.siteId=='P00000005011243')].id").count.optional.saveAs("numberOfRolesFromServicesWithSiteIdAtlanta"))
      .check(jsonPath("$..items[?(@.siteId=='P00000005011243' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithSiteIdAtlantaAndRankZero"))
      .check(jsonPath("$..totalCount").saveAs("numberOfRolesFromServices"))
      .check(jsonPath("$..[?(@.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRankZero"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
   def getRolesForVSOCPortalYesForSiteContactsNotOnTicketGroup(scenarioName: String, jsessionName: String, contactUserId: String) = {
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=YES" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='MFS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRoles"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='VMS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRoles"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='MDP - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRoles"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='MFS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllroles"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='VMS - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllroles"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='MDP - All Roles')].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllroles"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='MFS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='VMS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='MDP - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZero"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='MFS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='VMS - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='MDP - All Roles' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZero"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='MFS - All Roles' && @.rank==0 && @.siteId!=null)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllRolesWithRankZeroAndWithSiteId"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='VMS - All Roles' && @.rank==0 && @.siteId!=null)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllRolesWithRankZeroAndWithSiteId"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All Roles' && @.roleType=='MDP - All Roles' && @.rank==0 && @.siteId!=null)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllRolesWithRankZeroAndWithSiteId"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='MFS - All Roles' && @.rank==0 && @.siteId!=null)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMFSAlllrolesWithRankZeroAndWithSiteId"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='VMS - All Roles' && @.rank==0 && @.siteId!=null)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionVMSAlllrolesWithRankZeroAndWithSiteId"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.roleName=='All roles' && @.roleType=='MDP - All Roles' && @.rank==0 && @.siteId!=null)].id").count.optional.saveAs("numberOfRolesFromServicesWithRoleNameRoleTypeConditionMDPAlllrolesWithRankZeroAndWithSiteId"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.siteId!=null)].id").count.optional.saveAs("numberOfRolesFromServicesWithSiteId"))
      .check(jsonPath("$..[?(@.siteId=='" + "${sitesSiteId}" + "' && @.rank==0)].id").count.optional.saveAs("numberOfRolesFromServicesWithRankZero"))
      .check(jsonPath("$..items[?(@.siteId=='P00000005011243')].id").count.optional.saveAs("numberOfRolesFromServicesWithSiteIdAtlanta"))
      .check(jsonPath("$..items[?(@.siteId=='" + "${sitesSiteId}" + "')].id").count.saveAs("numberOfRolesFromServices"))
      //.check(jsonPath("$..totalCount").saveAs("numberOfRolesFromServices"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  //Get roles for VSOC Portal = yes (this is NOT on PRD.. for VSOC Portal = YES is not on PRD yet)
  def getRolesForVSOCPortalYes(scenarioName: String, jsessionName: String, contactUserId: String) = {
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=NO" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..items[*].id").count.saveAs("numberOfRolesFromServices"))
      .check(jsonPath("$..items[*].id").findAll.saveAs("contactIds"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  def getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMs(scenarioName: String, jsessionName: String, contactUserId: String) = {  
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=NO" )
      .basicAuth(headerUsername, headerPassword)   
      .check(status.is(headerCheckAssertionStatusCode)) 
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].id").saveAs("roleIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleName").saveAs("roleNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactId").saveAs("roleContactIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactName").transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).transform(string => string.replace("\u00e7", "")).saveAs("roleContactNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].serviceLine").saveAs("roleServiceLineFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerId").saveAs("roleCustomerIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerName").saveAs("roleCustomerNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].partnerId").saveAs("rolePartnerIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleType").saveAs("roleTypeFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteId").saveAs("roleSiteIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteName").saveAs("roleSiteNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].rank").saveAs("roleRankFromServicesCall"))         
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].id").is("${roleIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleName").is("${roleNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactId").is("${roleContactIdFromEmailContactsMsCall}".toString()))
     
      //some names comes with special characters like chinese ones.. the next line remove those characters and spaces to compara with the same from EmailContact_Ms response
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactName").find.transform(string => string.replace("?", "")).transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).transform(string => string.replace("\u00e7", "")).is("${roleContactNameFromEmailContactsMsCall}"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].serviceLine").is("${roleServiceLineFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerId").is("${roleCustomerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerName").is("${roleCustomerNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].partnerId").is("${rolePartnerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleType").is("${roleTypeFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteId").is("${roleSiteIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteName").is("${roleSiteNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].rank").is("${roleRankFromEmailContactsMsCall}".toString()))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  def getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsShowOnPortalYes(scenarioName: String, jsessionName: String, contactUserId: String) = {  
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=YES" )
      .basicAuth(headerUsername, headerPassword)   
      .check(status.is(headerCheckAssertionStatusCode)) 
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].id").saveAs("roleIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleName").saveAs("roleNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactId").saveAs("roleContactIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactName").transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).transform(string => string.replace("\u00e7", "")).saveAs("roleContactNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].serviceLine").saveAs("roleServiceLineFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerId").saveAs("roleCustomerIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerName").saveAs("roleCustomerNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].partnerId").saveAs("rolePartnerIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleType").saveAs("roleTypeFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteId").optional.saveAs("roleSiteIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteName").optional.saveAs("roleSiteNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].rank").saveAs("roleRankFromServicesCall"))         
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].id").is("${roleIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleName").find.transform(string => string.replace("roles", "Roles")).is("${roleNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactId").is("${roleContactIdFromEmailContactsMsCall}".toString()))
     
      //some names comes with special characters like chinese ones.. the next line remove those characters and spaces to compara with the same from EmailContact_Ms response
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactName").find.transform(string => string.replace("\\", "")).transform(string => string.replace("?", "")).transform(string => string.replace("-", "")).transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).transform(string => string.replace("\u00e7", "")).is("${roleContactNameFromEmailContactsMsCall}"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].serviceLine").is("${roleServiceLineFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerId").is("${roleCustomerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerName").is("${roleCustomerNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].partnerId").is("${rolePartnerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleType").is("${roleTypeFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteId").find.optional.saveAs("isSiteIdFound"))
      .check(checkIf("${isSiteIdFound.exists()}"){(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteId").is("${roleSiteIdFromEmailContactsMsCall}".toString()))})
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteName").find.optional.saveAs("isSiteNameFound"))
      .check(checkIf("${isSiteNameFound.exists()}"){(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].siteName").is("${roleSiteNameFromEmailContactsMsCall}".toString()))})
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].rank").is("${roleRankFromEmailContactsMsCall}".toString()))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  
  def getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsGroupName(scenarioName: String, jsessionName: String, contactUserId: String) = {  
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=NO" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode)) 
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].id").saveAs("roleIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].roleName").saveAs("roleNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].contactId").saveAs("roleContactIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].contactName").transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).transform(string => string.replace("\u00e7", "")).saveAs("roleContactNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].serviceLine").saveAs("roleServiceLineFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].customerId").saveAs("roleCustomerIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].customerName").saveAs("roleCustomerNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].partnerId").saveAs("rolePartnerIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].roleType").saveAs("roleTypeFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].siteId").saveAs("roleSiteIdFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].siteName").saveAs("roleSiteNameFromServicesCall"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].rank").saveAs("roleRankFromServicesCall"))       
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].id").is("${roleIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].roleName").is("${roleNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].contactId").is("${roleContactIdFromEmailContactsMsCall}".toString()))

      //some names comes with special characters like chinese ones.. the next line remove those characters and spaces to compara with the same from EmailContact_Ms response
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].contactName").find.transform(string => string.replace("?", "")).transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).transform(string => string.replace("\u00e7", "")).is("${roleContactNameFromEmailContactsMsCall}"))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].serviceLine").is("${roleServiceLineFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].customerId").is("${roleCustomerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].customerName").is("${roleCustomerNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].partnerId").is("${rolePartnerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].roleType").is("${roleTypeFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].siteId").is("${roleSiteIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].siteName").is("${roleSiteNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "' && @.siteName=='" + "${roleSiteNameFromEmailContactsMsCall}" + "')].rank").is("${roleRankFromEmailContactsMsCall}".toString()))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  def getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsNotOnTicketGroupName(scenarioName: String, jsessionName: String, contactUserId: String) = {  
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=NO" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].id").saveAs("roleIdFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleName").saveAs("roleNameFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactId").saveAs("roleContactIdFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactName").saveAs("roleContactNameFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].serviceLine").saveAs("roleServiceLineFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerId").saveAs("roleCustomerIdFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerName").saveAs("roleCustomerNameFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].partnerId").saveAs("rolePartnerIdFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleType").saveAs("roleTypeFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].rank").saveAs("roleRankFromServicesCall"))         
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].id").is("${roleIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleName").is("${roleNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactId").is("${roleContactIdFromEmailContactsMsCall}".toString()))

      //some names comes with special characters like chinese ones.. the next line remove those characters and spaces to compara with the same from EmailContact_Ms response
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactName").find.transform(string => string.replace("?", "")).transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).is("${roleContactNameFromEmailContactsMsCall}"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].serviceLine").is("${roleServiceLineFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerId").is("${roleCustomerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerName").is("${roleCustomerNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].partnerId").is("${rolePartnerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleType").is("${roleTypeFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].rank").is("${roleRankFromEmailContactsMsCall}".toString()))
     // .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  def getRolesValuesFromServicesForSpecificContactAndCheckMatchWithTheOnesFromEmailContactMsForSiteContactsNotOnTicketGroupNameShowOnPortalYes(scenarioName: String, jsessionName: String, contactUserId: String) = {  
    http(scenarioName)
      .get(baseURL + "rest/CustomerContactRole?contactId=" + contactUserId + "&format=json&showOnPortal=YES" )
      .basicAuth(headerUsername, headerPassword)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].id").saveAs("roleIdFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleName").saveAs("roleNameFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactId").saveAs("roleContactIdFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactName").saveAs("roleContactNameFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].serviceLine").saveAs("roleServiceLineFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerId").saveAs("roleCustomerIdFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerName").saveAs("roleCustomerNameFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].partnerId").saveAs("rolePartnerIdFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleType").saveAs("roleTypeFromServicesCall"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].rank").saveAs("roleRankFromServicesCall"))         
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].id").is("${roleIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleName").find.transform(string => string.replace("roles", "Roles")).is("${roleNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactId").is("${roleContactIdFromEmailContactsMsCall}".toString()))

      //some names comes with special characters like chinese ones.. the next line remove those characters and spaces to compara with the same from EmailContact_Ms response
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].contactName").find.transform(string => string.replace("?", "")).transform(string => string.replace(" ", "")).transform(string => string.replace("(", "")).transform(string => string.replace(")", "")).transform(string => string.replace("_", "")).is("${roleContactNameFromEmailContactsMsCall}"))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].serviceLine").is("${roleServiceLineFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerId").is("${roleCustomerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].customerName").is("${roleCustomerNameFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].partnerId").is("${rolePartnerIdFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].roleType").is("${roleTypeFromEmailContactsMsCall}".toString()))
      .check(jsonPath("$..items[" + "?(@.id=='" + "${roleIdFromEmailContactsMsCall}" + "')].rank").is("${roleRankFromEmailContactsMsCall}".toString()))
     // .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  
  //method to get token from Remedy
  def generateAuthToken(token: String) {
    http("Generate Token on Remedy")
		.get("https://stage-remedy.sec.ibm.com:8443/api/jwt/login?username=apttus-qa&password=apttus-test")
		.check(bodyString.saveAs("remedyToken"))
		
	}

  //method to release token from Remedy
  def releaseAuthToken(token: String) {
    http("Generate Token on Remedy")
		.get("https://stage-remedy.sec.ibm.com:8443/api/jwt/logout")
		.header("Authorization", "${remedyToken}")
		
	}
  
}