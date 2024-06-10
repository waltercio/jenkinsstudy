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
 *  Developed by: guilherme basaglia
 *  Based on: STRY0130891
 */

class AiHostInfoMs extends BaseTest {

  // Name of each request
  val req1 = "GET host information where hostType is unknown"
  val req2 = "GET host information where hostType is source"
  val req3 = "GET host information where hostType is destination"
  val req4 = "Missing hostType"
  val req5 = "Missing hostIp"
  val req6 = "Missing customerId"
  val req7 = "Wrong credentials"
  val req8 = "Cross contamination check"

  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"

  //Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiHostInfoMs")
 
      //GET host information where hostType is unknown
      .exec(http(req1)
      .get("micro/ai-host-info/host-info?customerId=CID001696&hostIp=10.10.10.10&hostType=UNKNOWN")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
       .check(jsonPath("$.customerId").is("CID001696"))
      .check(jsonPath("$.ipAddress").is("10.10.10.10"))
      .check(jsonPath("$.hostType").is("UNKNOWN"))
      .check(jsonPath("$.critical").exists)
      .check(jsonPath("$.internal").exists)
      .check(jsonPath("$.proxy").exists)
      .check(jsonPath("$.rfc1918").exists)
      .check(jsonPath("$.unassignedIP").exists)
      .check(jsonPath("$.suspiciousHost").exists)
      .check(jsonPath("$.acceptableTrafficSource").exists)
      .check(jsonPath("$.acceptableTrafficDestination").exists)
      .check(jsonPath("$.highVulnerabilityCount").exists)
      .check(jsonPath("$.mediumVulnerabilityCount").exists)
      .check(jsonPath("$.lowVulnerabilityCount").exists)
      .check(jsonPath("$..version").exists)
      .check(jsonPath("$..ip").is("10.10.10.10"))
      .check(jsonPath("$..countryId").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..countryCode").exists)
      .check(jsonPath("$..region").exists)
      .check(jsonPath("$..regionCode").exists)
      .check(jsonPath("$..regionId").exists)
      .check(jsonPath("$..cityId").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..stateId").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..stateCode").exists)
      .check(jsonPath("$..isp").exists)
      .check(jsonPath("$..latitude").exists)
      .check(jsonPath("$..longitude").exists)
      .check(jsonPath("$..addressRange").exists)
      .check(jsonPath("$..addressRange.startIp.numericValue").exists)
      .check(jsonPath("$..addressRange.startIp.stringValue").exists)
      .check(jsonPath("$..addressRange.startIp.protocol").exists)
      .check(jsonPath("$..endIp.numericValue").exists)
      .check(jsonPath("$..endIp.stringValue").exists)
      .check(jsonPath("$..endIp.protocol").exists)
      .check(jsonPath("$.osname").exists) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

      //GET host information where hostType is SOURCE
      .exec(http(req2)
      .get("micro/ai-host-info/host-info?customerId=CID001696&hostIp=10.10.10.10&hostType=SOURCE")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.customerId").is("CID001696"))
      .check(jsonPath("$.ipAddress").is("10.10.10.10"))
      .check(jsonPath("$.hostType").is("SOURCE"))
      .check(jsonPath("$.critical").exists)
      .check(jsonPath("$.internal").exists)
      .check(jsonPath("$.proxy").exists)
      .check(jsonPath("$.rfc1918").exists)
      .check(jsonPath("$.unassignedIP").exists)
      .check(jsonPath("$.suspiciousHost").exists)
      .check(jsonPath("$.acceptableTrafficSource").exists)
      .check(jsonPath("$.acceptableTrafficDestination").exists)
      .check(jsonPath("$.highVulnerabilityCount").exists)
      .check(jsonPath("$.mediumVulnerabilityCount").exists)
      .check(jsonPath("$.lowVulnerabilityCount").exists)
      .check(jsonPath("$..version").exists)
      .check(jsonPath("$..ip").is("10.10.10.10"))
      .check(jsonPath("$..countryId").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..countryCode").exists)
      .check(jsonPath("$..region").exists)
      .check(jsonPath("$..regionCode").exists)
      .check(jsonPath("$..regionId").exists)
      .check(jsonPath("$..cityId").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..stateId").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..stateCode").exists)
      .check(jsonPath("$..isp").exists)
      .check(jsonPath("$..latitude").exists)
      .check(jsonPath("$..longitude").exists)
      .check(jsonPath("$..addressRange").exists)
      .check(jsonPath("$..addressRange.startIp.numericValue").exists)
      .check(jsonPath("$..addressRange.startIp.stringValue").exists)
      .check(jsonPath("$..addressRange.startIp.protocol").exists)
      .check(jsonPath("$..endIp.numericValue").exists)
      .check(jsonPath("$..endIp.stringValue").exists)
      .check(jsonPath("$..endIp.protocol").exists)
      .check(jsonPath("$.osname").exists) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

      //GET host information where hostType is DESTINATION
      .exec(http(req3)
      .get("micro/ai-host-info/host-info?customerId=CID001696&hostIp=10.10.10.10&hostType=DESTINATION")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
       .check(jsonPath("$.customerId").is("CID001696"))
      .check(jsonPath("$.ipAddress").is("10.10.10.10"))
      .check(jsonPath("$.hostType").is("DESTINATION"))
      .check(jsonPath("$.critical").exists)
      .check(jsonPath("$.internal").exists)
      .check(jsonPath("$.proxy").exists)
      .check(jsonPath("$.rfc1918").exists)
      .check(jsonPath("$.unassignedIP").exists)
      .check(jsonPath("$.suspiciousHost").exists)
      .check(jsonPath("$.acceptableTrafficSource").exists)
      .check(jsonPath("$.acceptableTrafficDestination").exists)
      .check(jsonPath("$.highVulnerabilityCount").exists)
      .check(jsonPath("$.mediumVulnerabilityCount").exists)
      .check(jsonPath("$.lowVulnerabilityCount").exists)
      .check(jsonPath("$..version").exists)
      .check(jsonPath("$..ip").is("10.10.10.10"))
      .check(jsonPath("$..countryId").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..countryCode").exists)
      .check(jsonPath("$..region").exists)
      .check(jsonPath("$..regionCode").exists)
      .check(jsonPath("$..regionId").exists)
      .check(jsonPath("$..cityId").exists)
      .check(jsonPath("$..city").exists)
      .check(jsonPath("$..stateId").exists)
      .check(jsonPath("$..state").exists)
      .check(jsonPath("$..stateCode").exists)
      .check(jsonPath("$..isp").exists)
      .check(jsonPath("$..latitude").exists)
      .check(jsonPath("$..longitude").exists)
      .check(jsonPath("$..addressRange").exists)
      .check(jsonPath("$..addressRange.startIp.numericValue").exists)
      .check(jsonPath("$..addressRange.startIp.stringValue").exists)
      .check(jsonPath("$..addressRange.startIp.protocol").exists)
      .check(jsonPath("$..endIp.numericValue").exists)
      .check(jsonPath("$..endIp.stringValue").exists)
      .check(jsonPath("$..endIp.protocol").exists)
      .check(jsonPath("$.osname").exists) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Missing hostType
    .exec(http(req4)
      .get("micro/ai-host-info/host-info?customerId=CID001696&hostIp=10.10.10.10")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$.errors").exists) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Missing hostIp
    .exec(http(req5)
      .get("micro/ai-host-info/host-info?customerId=CID001696&hostType=DESTINATION")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$.errors").exists) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Missing customerId
    .exec(http(req6)
      .get("micro/ai-host-info/host-info?&hostIp=10.10.10.10&hostType=DESTINATION")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$.errors").exists) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Wrong credentials
    .exec(http(req7)
      .get("micro/ai-host-info/host-info?&hostIp=10.10.10.10&hostType=DESTINATION")
      .basicAuth(adUser, "somepass")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Cross contamination
    .exec(http(req8)
      .get("micro/ai-host-info/host-info?&hostIp=10.10.10.10&hostType=DESTINATION")
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}