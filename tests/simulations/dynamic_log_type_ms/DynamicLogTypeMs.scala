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
 *  Developed by: cgobbi@br.ibm.com
 *  Based on: https://jira.sec.ibm.com/browse/QX-5458
 *  Updated by: Eugeniu Vatamaniuc
 *  Based on: https://jira.sec.ibm.com/browse/XPS-87096
 */

class DynamicLogTypeMs extends BaseTest {

  val limitOfRecords = "2"
  val dynamicLogTypeId = "P00000000000002"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request and jsession
  val req01 = "Should return data for all dynamic log types"
  val req02 = "Should return data from an specific ID " + dynamicLogTypeId
  val req03 = "Should return only the amount of records set as the limit (" + limitOfRecords + ")"
  val req04 = "Fetching for a specific name - Firewall"
  val req05 = "Fetching data by multiple names"
  val req06 = "Fetching data by short name"
  val req07 = "Fetching data by status Active"
  val req08 = "Check new field serviceType Id return in response"

  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
  val js07 = "jsessionid07"
  val js08 = "jsessionid08"

  val scn = scenario("DynamicLogType")
    // Should return data for all dynamic log types
    .exec(http(req01)
      .get("micro/dynamic_log_type_ms/")
      .check(status.is(200))
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].firewallConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].status").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].idsConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].diskStorageEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].portalViewable").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].reportingEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].id").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].siteProtectorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].name").is("Anti-Spam"))
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].managementStationConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].lastModifiedDate").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].scanDroneConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].antivirusConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].shortName").is("spam"))
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].firewallConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].status").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].idsConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].diskStorageEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].portalViewable").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].reportingEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].id").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].siteProtectorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].name").is("Anti-Virus"))
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].managementStationConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].lastModifiedDate").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].scanDroneConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].antivirusConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].shortName").is("av"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

   // Should return data from an specific ID
   .exec(http(req02)
     .get("micro/dynamic_log_type_ms/" + dynamicLogTypeId)
     .check(status.is(200))
     .check(jsonPath("$..[?(@.name == \"Firewall\")].firewallConfigEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].realTimeAnalyticsEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].status").is("Active"))
     .check(jsonPath("$..[?(@.name == \"Firewall\")].onsiteAggregatorConfigEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].bluecoatReporterConfigEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].idsConfigEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].diskStorageEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].portalViewable").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].reportingEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].id").is("P00000000000002"))
     .check(jsonPath("$..[?(@.name == \"Firewall\")].siteProtectorConfigEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].name").is("Firewall"))
     .check(jsonPath("$..[?(@.name == \"Firewall\")].managementStationConfigEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].lastModifiedDate").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].scanDroneConfigEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].antivirusConfigEnabled").exists)
     .check(jsonPath("$..[?(@.name == \"Firewall\")].shortName").is("fw"))
     .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
   ).exec(flushSessionCookies)
   .doIf(session => !session.contains(js02)) {
     exec( session => {
       session.set(js02, "Unable to retrieve JSESSIONID for this request")
     })
   }

    // Should return only the amount of records set as the limit
    .exec(http(req03)
      .get("micro/dynamic_log_type_ms/")
      .queryParam("limit", limitOfRecords)
      .check(status.is(200))
      .check(jsonPath("$..firewallConfigEnabled").exists)
      .check(jsonPath("$..firewallConfigEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$..realTimeAnalyticsEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").count.is(limitOfRecords))
      .check(jsonPath("$..onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$..onsiteAggregatorConfigEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$..bluecoatReporterConfigEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..idsConfigEnabled").exists)
      .check(jsonPath("$..idsConfigEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..diskStorageEnabled").exists)
      .check(jsonPath("$..diskStorageEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..reportingEnabled").exists)
      .check(jsonPath("$..reportingEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").count.is(limitOfRecords))
      .check(jsonPath("$..siteProtectorConfigEnabled").exists)
      .check(jsonPath("$..siteProtectorConfigEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..name").count.is(limitOfRecords))
      .check(jsonPath("$..managementStationConfigEnabled").exists)
      .check(jsonPath("$..managementStationConfigEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..lastModifiedDate").exists)
      .check(jsonPath("$..lastModifiedDate").count.is(limitOfRecords))
      .check(jsonPath("$..scanDroneConfigEnabled").exists)
      .check(jsonPath("$..scanDroneConfigEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..antivirusConfigEnabled").exists)
      .check(jsonPath("$..antivirusConfigEnabled").count.is(limitOfRecords))
      .check(jsonPath("$..shortName").exists)
      .check(jsonPath("$..shortName").count.is(limitOfRecords))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching data for a specific name 'Firewall'
    .exec(http(req04)
      .get("micro/dynamic_log_type_ms/")
      .check(status.is(200))
      .queryParam("name", "Firewall")
      .check(jsonPath("$..[?(@.name != \"Firewall\")].name").count.is(0))
      .check(jsonPath("$..[?(@.name == \"Firewall\")].firewallConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].status").is("Active"))
      .check(jsonPath("$..[?(@.name == \"Firewall\")].onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].idsConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].diskStorageEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].portalViewable").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].reportingEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].id").is("P00000000000002"))
      .check(jsonPath("$..[?(@.name == \"Firewall\")].siteProtectorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].name").is("Firewall"))
      .check(jsonPath("$..[?(@.name == \"Firewall\")].managementStationConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].lastModifiedDate").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].scanDroneConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].antivirusConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].shortName").is("fw"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching data for 2 specific names
    .exec(http(req05)
      .get("micro/dynamic_log_type_ms/")
      .check(status.is(200))
      .queryParam("name", "Anti-Spam,Anti-Virus")
      .check(jsonPath("$..name").count.is(2))
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].firewallConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].status").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].idsConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].diskStorageEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].portalViewable").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].reportingEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].id").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].siteProtectorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].name").is("Anti-Spam"))
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].managementStationConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].lastModifiedDate").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].scanDroneConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].antivirusConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Spam\")].shortName").is("spam"))
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].firewallConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].status").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].idsConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].diskStorageEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].portalViewable").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].reportingEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].id").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].siteProtectorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].name").is("Anti-Virus"))
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].managementStationConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].lastModifiedDate").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].scanDroneConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].antivirusConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Anti-Virus\")].shortName").is("av"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching data by short name
    .exec(http(req06)
      .get("micro/dynamic_log_type_ms/")
      .check(status.is(200))
      .queryParam("shortName", "fw")
      .check(jsonPath("$..[?(@.name != \"Firewall\")].name").count.is(0))
      .check(jsonPath("$..[?(@.name == \"Firewall\")].firewallConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].status").is("Active"))
      .check(jsonPath("$..[?(@.name == \"Firewall\")].onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].idsConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].diskStorageEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].portalViewable").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].reportingEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].id").is("P00000000000002"))
      .check(jsonPath("$..[?(@.name == \"Firewall\")].siteProtectorConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].name").is("Firewall"))
      .check(jsonPath("$..[?(@.name == \"Firewall\")].managementStationConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].lastModifiedDate").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].scanDroneConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].antivirusConfigEnabled").exists)
      .check(jsonPath("$..[?(@.name == \"Firewall\")].shortName").is("fw"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching data by status Active
    .exec(http(req07)
      .get("micro/dynamic_log_type_ms/")
      .check(status.is(200))
      .queryParam("status", "Active")
      .check(jsonPath("$..[?(@.status == \"Active\")].id").count.gte(1))
      .check(jsonPath("$..[?(@.status != \"Active\")].id").count.is(0))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check new field serviceType Id return in response
    //XPS-104335 , QX-10081
    .exec(http(req08)
      .get("micro/dynamic_log_type_ms/?limit=500")
      .check(status.is(200))
      .check(jsonPath("$[*]..serviceTypeId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}