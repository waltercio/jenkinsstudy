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

/**
 *  Developed by: guibasa@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10955
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-10210
 *  PLEASE, MAKE SURE TO EXECUTE THE TEST ON ALL ENVIRONMENTS.
 */

class UserEntityMs extends BaseTest {

  // Name of each request
  val req1 = "Fetch a specific User Detail using userName"
  val req2 = "Fetch a specific User Detail using wrong userName"
  val req3 = "Fetch a specific User Detail using email"
  val req4 = "Fetch a specific User Detail using wrong email"
  val req5 = "Fetch a specific Customer Contact Detail using wrong id"
  val req6 = "Fetch all Customer Contact Details"
  val req7 = "Fetch all Customer Contact Details using id"
  val req8 = "Fetch total user details"
  val req9 = "Fetch all Customer Contact"
  val req10 = "Input Details are not provided"
  val req11 = "No Authentication"
  
  // Creating a val to store the jsession of each request
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
  

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("UserEntityMs")

//Fetch a specific User Detail using userName
    .exec(http(req1)
      .get("micro/user-entity/user?userName=letcalv")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("P00000000002396"))
      .check(jsonPath("$..userName").is("letcalv"))
      .check(jsonPath("$..email").is("letcalv@us.ibm.com"))
      .check(jsonPath("$..statusVal").is("Current"))
      .check(jsonPath("$..fullName").is("Letitia K. Calvert"))
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..department").is("Deployment Management"))
      .check(checkIf(environment == "DEV"){jsonPath("$..managerName").is("Audrey  (AUDREY) Harte Milko")})
      .check(checkIf(environment == "STG"){jsonPath("$..managerName").is("Audrey A. (AUDREY) Harte Milko")})
      .check(jsonPath("$..subDepartment").is("Technical Security Coordinators"))
      .check(jsonPath("$..managerEmail").is("hartemilkoa@us.ibm.com"))
      .check(jsonPath("$..ibmSerialNumber").is("7A2321"))
      .check(jsonPath("$..managerSerialNumber").is("2D7995"))
      .check(jsonPath("$..ibmDepartmentName").is("8F"))
      .check(jsonPath("$..ibmDepartmentCode").is("8FE32A"))
      .check(jsonPath("$..ibmCountryCode").is("897"))
      .check(jsonPath("$..activeDirectoryGroups").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
//Fetch a specific User Detail using wrong userName
    .exec(http(req2)
      .get("micro/user-entity/user?userName=noname")
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
//Fetch a specific User Detail using email
    .exec(http(req3)
      .get("micro/user-entity/user?email=hartemilkoa@us.ibm.com")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("STG700000001842"))
      .check(jsonPath("$..userName").is("hartemilkoa"))
      .check(jsonPath("$..email").is("hartemilkoa@us.ibm.com"))
      .check(jsonPath("$..statusVal").is("Current"))
      .check(jsonPath("$..fullName").is("Audrey Harte Milko"))
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..department").is("Deployment Management"))
      .check(jsonPath("$..managerName").is("Kevin Thomas"))
      .check(jsonPath("$..subDepartment").is("Deployment Management"))
      .check(jsonPath("$..managerEmail").is("kevintho@us.ibm.com"))
      .check(jsonPath("$..ibmSerialNumber").is("2D7995"))
      .check(jsonPath("$..managerSerialNumber").is("5A0743"))
      .check(jsonPath("$..ibmDepartmentName").is("8F"))
      .check(jsonPath("$..ibmDepartmentCode").is("8FE32"))
      .check(jsonPath("$..ibmCountryCode").is("897"))
      .check(jsonPath("$..activeDirectoryGroups").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
//Fetch a specific User Detail using invalid email
    .exec(http(req4)
      .get("micro/user-entity/user?email=mail@ibmmail.com")
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }
//Fetch a specific Customer Contact Detail using wrong id
    .exec(http(req5)
      .get("micro/user-entity/user?id=DEV000000000000")
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
//Fetch all Customer Contact Details
    .exec(http(req6)
      .get("micro/user-entity/users?start=0&limit=2")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("000000000000002"))
      .check(jsonPath("$..userName").is("test"))
      .check(jsonPath("$..email").is("C-K3BB897@nomail.relay.ibm.com"))
      .check(jsonPath("$..statusVal").is("Current"))
      .check(jsonPath("$..fullName").is("Test User (See web apps team)"))
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..department").is("SOC Operations"))
      .check(jsonPath("$..managerName").is("Brian Clement"))
      .check(jsonPath("$..managerEmail").is("bclement@us.ibm.com"))
      .check(jsonPath("$..managerSerialNumber").is("459710"))
      .check(jsonPath("$..ibmDepartmentName").is("8F TAB"))
      .check(jsonPath("$..ibmDepartmentCode").is("DDZA"))
      .check(jsonPath("$..mss_sort_statusVal").is("0"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
//Fetch all Customer Contact Details using id
    .exec(http(req7)
      .get("micro/user-entity/user?id=DEV000007029344")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..locale").is("en_US"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..id").is("DEV000007029344"))
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..phoneNumber").is("123-456-7890"))
      .check(jsonPath("$..createDate").is("Fri Jun 15 03:42:50 GMT 2018"))
      .check(jsonPath("$..siteName").is("Atlanta"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..userLoginType").is("UNLINKED"))
      .check(jsonPath("$..lastModifiedOn").is("Wed Oct 27 13:14:28 GMT 2021"))
      .check(jsonPath("$..guid").is("IDGAA5V0FCRFIAPO4P17PN7521QCXD"))
      .check(jsonPath("$..siteId").is("P00000005011976"))
      .check(jsonPath("$..statusVal").is("Inactive"))
      .check(jsonPath("$..lastModifiedBy").is("data_sync_ocp"))
      .check(jsonPath("$..fullName").is("ServicesUnitTestContact"))
      .check(jsonPath("$..accessLevelVal").is("Regular User"))
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..mss_sort_accessLevelVal").is("0"))
      .check(jsonPath("$..mss_sort_statusVal").is("1"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }
//Fetch total user details
    .exec(http(req8)
      .get("micro/user-entity/users?start=0&limit=4&sort=id.desc&includeTotalCount=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(4))
      .check(jsonPath("$..userName").count.is(4))
      .check(jsonPath("$..email").count.is(4))
      .check(jsonPath("$..fullName").count.is(4))
      .check(jsonPath("$..remedyAppsTime").count.is(4))
      .check(jsonPath("$..department").count.is(4))
      .check(jsonPath("$..managerName").count.is(4))
      .check(jsonPath("$..subDepartment").count.is(4))
      .check(jsonPath("$..managerEmail").count.is(4))
      .check(jsonPath("$..ibmSerialNumber").count.is(4))
      .check(jsonPath("$..managerSerialNumber").count.is(4))
      .check(jsonPath("$..ibmDepartmentName").count.is(4))
      .check(jsonPath("$..ibmDepartmentCode").count.is(4))
      .check(jsonPath("$..ibmCountryCode").count.is(4))
      .check(jsonPath("$..activeDirectoryGroups").count.is(4))
      .check(jsonPath("$..mss_sort_statusVal").count.is(4))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

//Fetch all Customer Contact
    .exec(http(req9)
      .get("micro/user-entity/users?start=2&limit=2&sort=id.desc")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }
//Input Details are not provided
    .exec(http(req10)
      .get("micro/user-entity/user")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }
// No Authentication
    .exec(http(req11)
      .get("micro/user-entity/users?start=2&limit=2&sort=id.desc")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}