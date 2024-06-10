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
 *  Developed by: Ashok.Korke@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-11271
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-11262
 *  Based on Jira Story: https://jira.sec.ibm.com/browse/XPS-95373 and https://jira.sec.ibm.com/browse/XPS-144901 

 */

class StaticChangeWindowMs extends BaseTest {

  val customerId = "P000000614"
  val range= "dateRangeStart(2023-06-20, 2023-07-3)"

  // Name of each request
  val req1 = "Fetch QA Customer data with customerID as parameter using QA Customer credentials"
  val req2 = "Fetch QA Customer data with customerID as parameter using admin credentials"
  val req3 = "Fetch Demo Customer data with customerID as parameter using QA Customer credentials"
  val req4 = "Fetch QA Customer data with customerID and date range[<=3 week] as parameter using QA Customer credentials"
  val req5 = "Fetch QA Customer data with customerID and date range[<=3 week] as parameter using using admin credentials"
  val req6 = "Fetch QA Customer data with customerID and date range[>3 week] as parameter using QA Customer credentials"
  val req7 = "Fetch QA Customer data with customerID and only start/end date(range not provided) as parameter using QA Customer credentials"
  val req8 = "Fetch Non existing Customer data using Admin credentials"
  val req9 = "Fetch QA Customer data with customerID and date range[<=3 week] as parameter where no windows defined using QA Customer credentials"
  val req10 = "Negative test - Fetch QA Customer data with customerID as parameter using Invalid credentials"

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

  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("Static Change Window Ms")
    //Fetch QA Customer data with customerID as parameter using QA Customer credentials
    .exec(http(req1)
      .get("micro/static-change-window")
      .queryParam("customerId", customerId)
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..changeWindows").exists)
      .check(jsonPath("$..changeWindows[0].name").exists)
      .check(jsonPath("$..changeWindows[0].end_date_time").exists)
      .check(jsonPath("$..changeWindows[0].start_date_time").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec(session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Fetch QA Customer data with customerID as parameter using admin credentials
    .exec(http(req2)
      .get("micro/static-change-window")
      .queryParam("customerId", customerId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..changeWindows").exists)
      .check(jsonPath("$..changeWindows[0].name").exists)
      .check(jsonPath("$..changeWindows[0].end_date_time").exists)
      .check(jsonPath("$..changeWindows[0].start_date_time").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Fetch Demo Customer data with customerID as parameter using QA Customer credentials
    .exec(http(req3)
      .get("micro/static-change-window")
      .queryParam("customerId", "CID001696")
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec(session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Fetch QA Customer data with customerID and date range[<=3 week] as parameter using QA Customer credentials
    .exec(http(req4)
      .get("micro/static-change-window")
      .queryParam("customerId", customerId)
      .queryParam("range", range)
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..changeWindows").exists)
      .check(jsonPath("$..changeWindows[0].name").exists)
      .check(jsonPath("$..changeWindows[0].end_date_time").exists)
      .check(jsonPath("$..changeWindows[0].start_date_time").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec(session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Fetch QA Customer data with customerID and date range[<=3 week] as parameter using using admin credentials
    .exec(http(req5)
      .get("micro/static-change-window")
      .queryParam("customerId", customerId)
      .queryParam("range", range)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..changeWindows").exists)
      .check(jsonPath("$..changeWindows[0].name").exists)
      .check(jsonPath("$..changeWindows[0].end_date_time").exists)
      .check(jsonPath("$..changeWindows[0].start_date_time").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec(session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Fetch QA Customer data with customerID and date range[>3 week] as parameter using QA Customer credentials
    .exec(http(req6)
      .get("micro/static-change-window")
      .queryParam("customerId", customerId)
      .queryParam("range", "dateRangeStart(2023-06-10, 2023-07-05)")
      .basicAuth(contactUser, contactPass)
      .check(status.is(400))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec(session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Fetch QA Customer data with customerID and only start/end date(range not provided) as parameter using QA Customer credentials
    .exec(http(req7)
      .get("micro/static-change-window")
      .queryParam("customerId", customerId)
      .queryParam("range", "dateRangeStart(2023-06-10)")
      .basicAuth(contactUser, contactPass)
      .check(status.is(400))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec(session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Fetch Non existing Customer data using Admin credentials"
    .exec(http(req8)
      .get("micro/static-change-window")
      .queryParam("customerId", "CID000016")
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(jsonPath("$..message").is("No matching records found for the given search criteria."))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec(session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Fetch QA Customer data with customerID and date range[<=3 week] as parameter where no windows defined using QA Customer credentials
    .exec(http(req9)
      .get("micro/static-change-window")
      .queryParam("customerId", customerId)
      .queryParam("range", "dateRangeStart(2022-01-05, 2022-01-22)")
      .basicAuth(contactUser, contactPass)
      .check(status.is(404))
      .check(jsonPath("$..message").is("No matching records found for the given search criteria."))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec(session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Negative test - Fetch QA Customer data with customerID as parameter using Invalid credentials
    .exec(http(req10)
      .get("micro/static-change-window")
      .queryParam("customerId", customerId)
      .basicAuth(contactUser, "ABCD")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec(session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Exporting all jsession ids
    .exec(session => {
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
