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
 * Developed by: Renata Angelelli
 * Development Date: 11/19/2020
 * Based on: QX-7377/XPS-57283
 * Updated by: Ashok Korke / Renata Angelelli / Eugeniu Vatamaniuc
 * Based on XPS-82969/ QX-9254
 * Based on XPS-87102 /
 */

class SiteMs extends BaseTest {

  // Name of each request
  val req1 = "GET all sites - Global"
  val req2 = "GET all sites - QA Customer"
  val req3 = "GET all sites - Demo Customer"
  val req4 = "GET by site Id 'P00000005011976' - Global"
  val req5 = "GET by site Id 'P00000005011976' - QA Customer"
  val req6 = "GET by site Id 'P00000005011976' - Demo Customer - empty body"
  val req7 = "GET by site name parameter - Global"
  val req8 = "GET by site name parameter - QA Customer"
  val req9 = "GET by site name parameter - Demo Customer"
  val req10 = "GET by customer Id 'P000000614' - Global"
  val req11 = "GET by customer Id 'P000000614' - QA Customer"
  val req12 = "GET by customer Id 'P000000614' - Demo Customer - 401 Permission Denied"
  val req13 = "GET by partner Id 'CIDS705057' - Global"
  val req14 = "GET all belonging for test_user credentials"
  val req15 = "GET by partner Id 'P000000613' - QA Customer"
  val req16 = "GET by customer Name 'QA Customer' - Global"
  val req17 = "GET by customer Name 'QA Customer' - QA Customer"
  val req18 = "GET by customer Name 'QA Customer' - Demo Customer - empty body"
  val req19 = "GET by customer Id 'CID001696', start as '0' and limit as '3' - Global"
  val req20 = "GET by customer Id 'CID001696', start as '2' and limit as '3' - Global"
  val req21 = "GET by site statusVal 'Active' - Global"
  val req22 = "GET by site statusVal 'Active' - QA Customer"
  val req23 = "GET by site city 'Atlanta' - Global"
  val req24 = "GET by site city 'Atlanta' - QA Customer"
  val req25 = "PATCH site Id 'P00000005011976'"
  val req26 = "GET if site Id 'P00000005011976' got updated"
  val req27 = "PATCH site Id 'P00000005011976' - changing back to its initial value"
  val req28 = "GET if site Id 'P00000005011976' got changed back"
  val req29 = "GET by statusVal=Active,Inactive"
  val req30 = "GET values for testing ascending by 'name'"
  val req31 = "GET - response should be sorted ascending by 'name'"
  val req32 = "GET values for testing descending by 'name'"
  val req33 = "GET - response should be sorted descending by 'name'"
  val req34 = "GET value based on multiple customerIds"
  val req35 = "GET by textToSearch param"

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

  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("SiteMs")

    //all sites - Global - Should return a large set of results of sites for many different customers
    .exec(http(req1)
      .get("micro/site")
      .queryParam("limit", "20")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").count.lte(20))
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..name").find.saveAs("NAME_AD"))
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec(session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //all sites - QA Customer - Should return several sites (all should be for QA customer - P000000614)
    .exec(http(req2)
      .get("micro/site")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..name").find.saveAs("NAME_QA"))
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //all sites - Demo Customer - Should return several sites
    .exec(http(req3)
      .get("micro/site")
      .basicAuth("qademouser", contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..name").find.saveAs("NAME_QA_DEMO"))
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec(session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //siteId P00000005011976 - Global - Should return site with requested site ID: P00000005011976
    .exec(http(req4)
      .get("micro/site/P00000005011976")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("P00000005011976"))
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec(session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //siteId P00000005011976 - QA Customer - Should return site with requested site ID: P00000005011976
    .exec(http(req5)
      .get("micro/site/P00000005011976")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("P00000005011976"))
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec(session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //siteId P00000005011976 - Wrong credentials - Should return empty body
    .exec(http(req6)
      .get("micro/site/P00000005011976")
      .basicAuth("qademouser", contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..message").notExists)
      .check(bodyString.transform(_.size < 3).is(true))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec(session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //site search by name parameter - Global
    .exec(http(req7)
      .get("micro/site")
      .queryParam("name", "${NAME_AD}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec(session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //site search by name parameter - QA Customer.
    .exec(http(req8)
      .get("micro/site")
      .queryParam("name", "${NAME_QA}")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec(session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //site search by name parameter - Demo Customer
    .exec(http(req9)
      .get("micro/site")
      .queryParam("name", "${NAME_QA_DEMO}")
      .basicAuth("qademouser", contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec(session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customerId 'P000000614' - Global - Should return all sites for QA Customer
    .exec(http(req10)
      .get("micro/site")
      .queryParam("customerId", "P000000614")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec(session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customerId 'P000000614' - QA Customer - Should return all sites for QA Customer
    .exec(http(req11)
      .get("micro/site")
      .queryParam("customerId", "P000000614")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec(session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //customerId 'P000000614' - Demo Customer - Should return 401, message 'Permission denied'
    .exec(http(req12)
      .get("micro/site")
      .queryParam("customerId", "P000000614")
      .basicAuth("qademouser", contactPass)
      .check(status.is(401))
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec(session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //partnerId 'CIDS705057' - Global - Should return several records, all for "Demo Partner" (CIDS705057)
    .exec(http(req13)
      .get("micro/site")
      .queryParam("partnerId", "CIDS705057")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").is("CIDS705057"))
      .check(jsonPath("$..partnerName").is("Demo Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec(session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //credentials as test_user - Should return several records, all for "Demo Partner" (CIDS705057)
    .exec(http(req14)
      .get("micro/site")
      .basicAuth("test_user", contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").is("CIDS705057"))
      .check(jsonPath("$..partnerName").is("Demo Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec(session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //partnerId 'P000000613' - QA Customer - Should only get "QA Customer" records
    .exec(http(req15)
      .get("micro/site")
      .queryParam("partnerId", "P000000613")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec(session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customerName 'QA Customer' - Global - Should only get "QA Customer" records
    .exec(http(req16)
      .get("micro/site")
      .queryParam("customerName", "QA Customer")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec(session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customerName 'QA Customer' - QA Customer - Should only get "QA Customer" records
    .exec(http(req17)
      .get("micro/site")
      .queryParam("customerName", "QA Customer")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec(session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customerName 'QA Customer' - Demo Customer - Should return an empty body
    .exec(http(req18)
      .get("micro/site")
      .basicAuth("qademouser", contactPass)
      .queryParam("customerName", "QA Customer")
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..message").notExists)
      .check(bodyString.transform(_.size < 3).is(true))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec(session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customerId 'CID001696', start '0' and limit '3' - Global - Should return exactly 3 site records for Demo Customer
    .exec(http(req19)
      .get("micro/site")
      .basicAuth(adUser, adPass)
      .queryParam("customerId", "CID001696")
      .queryParam("start", "0")
      .queryParam("limit", "3")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(3))
      .check(jsonPath("$[2]..id").find.saveAs("validatingId"))
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("CID001696"))
      .check(jsonPath("$..customerName").is("Demo Customer"))
      .check(jsonPath("$..partnerId").is("CIDS705057"))
      .check(jsonPath("$..partnerName").is("Demo Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec(session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customerId 'CID001696', start '2' and limit '3' - Global - Should return exactly 3 site records for Demo Customer and first returned site ID should match the last site ID returned from #18
    .exec(http(req20)
      .get("micro/site")
      .basicAuth(adUser, adPass)
      .queryParam("customerId", "CID001696")
      .queryParam("start", "2")
      .queryParam("limit", "3")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(3))
      .check(jsonPath("$[0]..id").is("${validatingId}"))
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("CID001696"))
      .check(jsonPath("$..customerName").is("Demo Customer"))
      .check(jsonPath("$..partnerId").is("CIDS705057"))
      .check(jsonPath("$..partnerName").is("Demo Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec(session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //site statusVal 'Active' - Global
    .exec(http(req21)
      .get("micro/site")
      .queryParamSeq(Seq(("start", "0"), ("limit", "20"), ("statusVal", "Active")))
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").is("0"))
      .check(jsonPath("$[?(@.statusVal == 'Active' || @.statusVal == 'active')].statusVal").count.gte(1))
      .check(jsonPath("$[?(@.statusVal != 'Active' && @.statusVal != 'active')].statusVal").count.is(0))
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js21)) {
      exec(session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //site statusVal 'Active' - QA Customer.
    .exec(http(req22)
      .get("micro/site")
      .queryParamSeq(Seq(("start", "0"), ("limit", "20"), ("statusVal", "Active")))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").is("0"))
      .check(jsonPath("$[?(@.statusVal == 'Active' || @.statusVal == 'active')].statusVal").count.gte(1))
      .check(jsonPath("$[?(@.statusVal != 'Active' && @.statusVal != 'active')].statusVal").count.is(0))
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js22)) {
      exec(session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //site city 'Atlanta' - Global
    .exec(http(req23)
      .get("micro/site")
      .queryParamSeq(Seq(("start", "0"), ("limit", "20"), ("city", "Atlanta")))
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$[?(@.city == 'Atlanta' || @.city == 'atlanta')].city").count.gte(1))
      .check(jsonPath("$[?(@.city != 'Atlanta' && @.city != 'atlanta')].city").count.is(0))
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js23)) {
      exec(session => {
        session.set(js23, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //site city 'Atlanta' - QA Customer.
    .exec(http(req24)
      .get("micro/site")
      .queryParamSeq(Seq(("start", "0"), ("limit", "20"), ("city", "Atlanta")))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..timeZoneNew").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$[?(@.city == 'Atlanta' || @.city == 'atlanta')].city").count.gte(1))
      .check(jsonPath("$[?(@.city != 'Atlanta' && @.city != 'atlanta')].city").count.is(0))
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js24)) {
      exec(session => {
        session.set(js24, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Update some fields for P00000005011976
    .exec(http(req25)
      .patch("micro/site/P00000005011976")
      .body(StringBody("{\"statusVal\": \"Active\", \"address\": \"Where QA leaves Happily Ever After, 1-23-4\", \"zip\": \"123-4567\"}"))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("P00000005011976"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
    ).exec(flushSessionCookies).pause(20 seconds)
    .doIf(session => !session.contains(js25)) {
      exec(session => {
        session.set(js25, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get updates for P00000005011976
    .exec(http(req26)
      .get("micro/site/P00000005011976")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("P00000005011976"))
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").is("Active"))
      .check(jsonPath("$..address").is("Where QA leaves Happily Ever After, 1-23-4"))
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").is("123-4567"))
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js26))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js26)) {
      exec(session => {
        session.set(js26, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Update some fields for P00000005011976 to its initial value
    .exec(http(req27)
      .patch("micro/site/P00000005011976")
      .body(StringBody("{\"statusVal\": \"Active\", \"address\": \"6303 Barfield Road\", \"zip\": \"30328\"}"))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("P00000005011976"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
    ).exec(flushSessionCookies).pause(20 seconds)
    .doIf(session => !session.contains(js27)) {
      exec(session => {
        session.set(js27, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get changes for P00000005011976 again
    .exec(http(req28)
      .get("micro/site/P00000005011976")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("P00000005011976"))
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").is("Active"))
      .check(jsonPath("$..address").is("6303 Barfield Road"))
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").is("30328"))
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js28))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js28)) {
      exec(session => {
        session.set(js28, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get status as Active or Inactive
    .exec(http(req29)
      .get("micro/site?statusVal=Active,Inactive")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").in("Active", "Inactive"))
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js29))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js29)) {
      exec(session => {
        session.set(js29, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Get values for testing ascending by 'name'
    .exec(http(req30)
      .get("micro/site")
      .queryParam("customerId", "CID001696")
      .queryParam("sort", "name.asc")
      .queryParam("limit", "20")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..name").findAll.saveAs("ASC_NAMES"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js30))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js30)) {
      exec(session => {
        session.set(js30, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Getting values returned from 1st ascending call to sort them ascending to compare later
    .exec(session => {
      val ascNamesAsString = session("ASC_NAMES").as[String]
      val ascNamesAsStringSplit = ascNamesAsString.split("Vector\\(")(1)
      val index = ascNamesAsStringSplit.lastIndexOf(")")
      val myCuttedString = ascNamesAsStringSplit.substring(0, index)
      val ascNamesSplitted = myCuttedString.split(", ")
      val ascNamesAsVector = ascNamesSplitted.toVector
      val sortedNamesAscending = ascNamesAsVector.sortBy(_.toLowerCase)
      session.set("SORTED_LIST_ASCENDING", sortedNamesAscending)
    })

    // Testing the microservice returns values Ascending
    .exec(http(req31)
      .get("micro/site")
      .queryParam("customerId", "CID001696")
      .queryParam("sort", "name.asc")
      .queryParam("limit", "20")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..name").findAll.is("${SORTED_LIST_ASCENDING}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js31))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js31)) {
      exec(session => {
        session.set(js31, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Get values for testing descending by 'name'
    .exec(http(req32)
      .get("micro/site")
      .queryParam("customerId", "CID001696")
      .queryParam("sort", "name.desc")
      .queryParam("limit", "5")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..name").findAll.saveAs("DESC_NAMES"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js32))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js32)) {
      exec(session => {
        session.set(js32, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Getting values returned from 1st descending call to sort them descending to compare later
    .exec(session => {
      val descNamesAsString = session("DESC_NAMES").as[String]
      val descNamesAsStringSplit = descNamesAsString.split("Vector\\(")(1)
      val index = descNamesAsStringSplit.lastIndexOf(")")
      val myCuttedString = descNamesAsStringSplit.substring(0, index)
      val descNamesSplitted = myCuttedString.split(", ")
      val descNamesAsVector = descNamesSplitted.toVector
      val sortedNamesDescending = descNamesAsVector.sortBy(_.toLowerCase).reverse
      session.set("SORTED_LIST_DESCENDING", sortedNamesDescending)
    })

    // Testing the microservice returns values Descending
    .exec(http(req33)
      .get("micro/site")
      .queryParam("customerId", "CID001696")
      .queryParam("sort", "name.desc")
      .queryParam("limit", "5")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..name").findAll.is("${SORTED_LIST_DESCENDING}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js33))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js33)) {
      exec(session => {
        session.set(js33, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // GET value based on multiple customerId XPS-81284
    .exec(http(req34)
      .get("micro/site?customerIds=CID001696,P000000614")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").in("CID001696", "P000000614"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js34))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js34)) {
      exec(session => {
        session.set(js34, "Unable to retrieve JSESSIONID for this request")
      })
    }
    // GET by textToSearch param
    .exec(http(req35)
      .get("micro/site?textToSearch=partnerId%3AP000000613")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..address").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..zip").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js35))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js35)) {
      exec(session => {
        session.set(js35, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}

