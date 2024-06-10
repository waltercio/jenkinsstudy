import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import java.io._
import org.json4s.jackson.Serialization._

/**
 *  Developed by: diegobs@br.ibm.com
 *  Based on: Links for Funtional Test: https://jira.sec.ibm.com/browse/QX-1424
 *  Link for Automation task: https://jira.sec.ibm.com/browse/QX-13035
 *  Updates: https://jira.sec.ibm.com/browse/QX-13635
 */
class UserMs extends BaseTest {

    val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
    val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
    val UserMsEndpoint = "micro/user?datasource=snow"

    // Name of each request
    val req01 = "Retrieve all User records from User schema"
    val req02 = "Retrieve a specific User records from User schema using user?q=id:<user_record>"
    val req03 = "Retrieve a specific User records from User schema using user?q=<user_record>"
    val req04 = "Retrieve User records from User schema with limit=2"
    val req05 = "Retrieve User record from User schema with userName"
    val req06 = "Retrieve User records from User schema with userNames and emails"
    val req07 = "Retrieve User records from User schema with email"
    val req08 = "Retrieve User record from User schema with firstName"
    val req09 = "Retrieve QA Customer User records with limit=2 using qatest"
    val req10 = "Retrieve Demo Customer User records with limit=2 using qademouser"
    val req11 = "Check QA Customer User can not access other customer creds"
    val req12 = "Check Demo Customer User can not access other customer creds"

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

    val scn = scenario("UserMs")
    
      .exec(http(req01)
        .get(UserMsEndpoint + "&limit=2000")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(checkIf(environment != "EU"){jsonPath("$[*]..id").count.is(2000)})
        //On EU the records does not bring some fields
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..statusVal").find.saveAs("statusVal"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..ibmDepartmentName").find.saveAs("ibmDepartmentName"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..userName").find.saveAs("userName"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..source").find.saveAs("source"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..managerName").find.saveAs("managerName"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..id").find.saveAs("id"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..userSysId").find.saveAs("userSysId"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..ibmCountryCode").find.saveAs("ibmCountryCode"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..customerName").find.saveAs("customerName"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..firstName").find.saveAs("firstName"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..lastName").find.saveAs("lastName"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..email").find.saveAs("email"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..customerId").find.saveAs("customerId"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..fullName").find.saveAs("fullName"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..adUser").find.saveAs("adUser"))
        .check(jsonPath("$[?(@.userName == 'diegobs@br.ibm.com')]..activeDirectoryGroups").find.saveAs("activeDirectoryGroups"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
      //Retrieve a specific User records from User schema using user?q=id:<user_record>
      .exec(http(req02)
        .get(UserMsEndpoint + "&q=id:" + "${id}")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[0]..statusVal").is("${statusVal}"))
        .check(jsonPath("$[0]..ibmDepartmentName").is("${ibmDepartmentName}"))
        .check(jsonPath("$[0]..userName").is("${userName}"))
        .check(jsonPath("$[0]..source").is("${source}"))
        .check(jsonPath("$[0]..managerName").is("${managerName}"))
        .check(jsonPath("$[0]..id").is("${id}"))
        .check(jsonPath("$[0]..userSysId").is("${userSysId}"))
        .check(jsonPath("$[0]..ibmCountryCode").is("${ibmCountryCode}"))
        .check(jsonPath("$[0]..customerName").is("${customerName}"))
        .check(jsonPath("$[0]..firstName").is("${firstName}"))
        .check(jsonPath("$[0]..lastName").is("${lastName}"))
        .check(jsonPath("$[0]..email").is("${email}"))
        .check(jsonPath("$[0]..customerId").is("${customerId}"))
        .check(jsonPath("$[0]..fullName").is("${fullName}"))
        .check(jsonPath("$[0]..adUser").is("${adUser}"))
        .check(jsonPath("$[0]..activeDirectoryGroups").is("${activeDirectoryGroups}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Retrieve a specific User records from User schema using user?q=<user_record>
      .exec(http(req03)
        .get(UserMsEndpoint + "&q=" + "${id}")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[0]..statusVal").is("${statusVal}"))
        .check(jsonPath("$[0]..ibmDepartmentName").is("${ibmDepartmentName}"))
        .check(jsonPath("$[0]..userName").is("${userName}"))
        .check(jsonPath("$[0]..source").is("${source}"))
        .check(jsonPath("$[0]..managerName").is("${managerName}"))
        .check(jsonPath("$[0]..id").is("${id}"))
        .check(jsonPath("$[0]..userSysId").is("${userSysId}"))
        .check(jsonPath("$[0]..ibmCountryCode").is("${ibmCountryCode}"))
        .check(jsonPath("$[0]..customerName").is("${customerName}"))
        .check(jsonPath("$[0]..firstName").is("${firstName}"))
        .check(jsonPath("$[0]..lastName").is("${lastName}"))
        .check(jsonPath("$[0]..email").is("${email}"))
        .check(jsonPath("$[0]..customerId").is("${customerId}"))
        .check(jsonPath("$[0]..fullName").is("${fullName}"))
        .check(jsonPath("$[0]..adUser").is("${adUser}"))
        .check(jsonPath("$[0]..activeDirectoryGroups").is("${activeDirectoryGroups}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Retrieve User records from User schema with limit=2
      .exec(http(req04)
        .get(UserMsEndpoint + "&limit=10")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[*]..id").count.is(10))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Retrieve User record from User schema with userName
      .exec(http(req05)
        .get(UserMsEndpoint + "&userName=" + "${adUser}")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[0]..statusVal").is("${statusVal}"))
        .check(jsonPath("$[0]..ibmDepartmentName").is("${ibmDepartmentName}"))
        .check(jsonPath("$[0]..userName").is("${userName}"))
        .check(jsonPath("$[0]..source").is("${source}"))
        .check(jsonPath("$[0]..managerName").is("${managerName}"))
        .check(jsonPath("$[0]..id").is("${id}"))
        .check(jsonPath("$[0]..userSysId").is("${userSysId}"))
        .check(jsonPath("$[0]..ibmCountryCode").is("${ibmCountryCode}"))
        .check(jsonPath("$[0]..customerName").is("${customerName}"))
        .check(jsonPath("$[0]..firstName").is("${firstName}"))
        .check(jsonPath("$[0]..lastName").is("${lastName}"))
        .check(jsonPath("$[0]..email").is("${email}"))
        .check(jsonPath("$[0]..customerId").is("${customerId}"))
        .check(jsonPath("$[0]..fullName").is("${fullName}"))
        .check(jsonPath("$[0]..adUser").is("${adUser}"))
        .check(jsonPath("$[0]..activeDirectoryGroups").is("${activeDirectoryGroups}"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec(session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Retrieve User records from User schema with userNames and emails
      .exec(http(req06)
        .get(UserMsEndpoint + "&userName=diegobs,wobc,veugeniu")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[*]..id").count.is(3))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec(session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Retrieve User record from User schema with email
      .exec(http(req07)
        .get(UserMsEndpoint + "&email=" + "${email}")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[0]..statusVal").is("${statusVal}"))
        .check(jsonPath("$[0]..ibmDepartmentName").is("${ibmDepartmentName}"))
        .check(jsonPath("$[0]..userName").is("${userName}"))
        .check(jsonPath("$[0]..source").is("${source}"))
        .check(jsonPath("$[0]..managerName").is("${managerName}"))
        .check(jsonPath("$[0]..id").is("${id}"))
        .check(jsonPath("$[0]..userSysId").is("${userSysId}"))
        .check(jsonPath("$[0]..ibmCountryCode").is("${ibmCountryCode}"))
        .check(jsonPath("$[0]..customerName").is("${customerName}"))
        .check(jsonPath("$[0]..firstName").is("${firstName}"))
        .check(jsonPath("$[0]..lastName").is("${lastName}"))
        .check(jsonPath("$[0]..email").is("${email}"))
        .check(jsonPath("$[0]..customerId").is("${customerId}"))
        .check(jsonPath("$[0]..fullName").is("${fullName}"))
        .check(jsonPath("$[0]..adUser").is("${adUser}"))
        .check(jsonPath("$[0]..activeDirectoryGroups").is("${activeDirectoryGroups}"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js07)) {
        exec(session => {
          session.set(js07, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Retrieve User record from User schema with firstName
      .exec(http(req08)
        .get(UserMsEndpoint + "&firstName=" + "${firstName}")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[*]..statusVal").exists)
        .check(jsonPath("$[*]..ibmDepartmentName").exists)
        .check(jsonPath("$[*]..userName").exists)
        .check(jsonPath("$[*]..source").exists)
        .check(jsonPath("$[*]..managerName").exists)
        .check(jsonPath("$[*]..id").exists)
        .check(jsonPath("$[*]..userSysId").exists)
        .check(jsonPath("$[*]..ibmCountryCode").exists)
        .check(jsonPath("$[*]..customerName").exists)
        .check(jsonPath("$[*]..firstName").is("${firstName}"))
        .check(jsonPath("$[*]..lastName").exists)
        .check(jsonPath("$[*]..email").exists)
        .check(jsonPath("$[*]..customerId").exists)
        .check(jsonPath("$[*]..fullName").exists)
        .check(jsonPath("$[*]..adUser").exists)
        .check(jsonPath("$[*]..activeDirectoryGroups").exists)
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js08)) {
        exec(session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Retrieve QA Customer User records with limit=2 using qatest
      .exec(http(req09)
        .get(UserMsEndpoint + "&limit=2" + "&customerId=P000000614")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$[*]..id").count.is(2))
        .check(jsonPath("$[*]..customerId").findAll.saveAs("QAcustomerIds"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js09)) {
        exec(session => {
          val customerIds = session("QAcustomerIds").as[Seq[String]]
          val firstId = customerIds.headOption.getOrElse("")
          val allIdsEqual = customerIds.forall(_ == firstId)
          println(s"All IDs are equal: $allIdsEqual")
          session.set(js09, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Retrieve Demo Customer User records with limit=2 using qademouser
      .exec(http(req10)
        .get(UserMsEndpoint + "&limit=2" + "&customerId=CID001696")
        .basicAuth(qaDemoUser, qaDemoPass)
        .check(status.is(200))
        .check(jsonPath("$[*]..id").count.is(2))
        .check(jsonPath("$[*]..customerId").findAll.saveAs("DemoCustomerIds"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js10)) {
        exec(session => {
          val customerIds = session("DemoCustomerIds").as[Seq[String]]
          val firstId = customerIds.headOption.getOrElse("")
          val allIdsEqual = customerIds.forall(_ == firstId)
          println(s"All IDs are equal: $allIdsEqual")
          session.set(js10, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check QA Customer User can not access other customer creds
      .exec(http(req11)
        .get(UserMsEndpoint + "&limit=5" + "&customerId=CID001696")
        .basicAuth(contactUser, contactPass)
        .check(status.is(401))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js11)) {
        exec(session => {
          session.set(js11, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check Demo Customer User can not access other customer creds
      .exec(http(req12)
        .get(UserMsEndpoint + "&limit=5" + "&customerId=P000000614")
        .basicAuth(qaDemoUser, qaDemoPass)
        .check(status.is(401))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js12)) {
        exec(session => {
          session.set(js12, "Unable to retrieve JSESSIONID for this request")
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
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

      setUp(
        scn.inject(atOnceUsers(1))
      ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
