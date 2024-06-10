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
 *  Developed by: wobc@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-7691
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-7730
 */

class DeviceDetailLogSourceMs extends BaseTest{
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  
  /**Get Ticket to test**/
  val logSourceTestFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_detail_log_source_ms/logSourceIDs.json").getLines().mkString)
  val logSourceAntivirus = (logSourceTestFile \\ "LogSourceAntivirus" \\ environment).extract[String]
  val logSourceManagementStation = (logSourceTestFile \\ "LogSourceManagementStation" \\ environment).extract[String]
  val logSourceIds = (logSourceTestFile \\ "LogSourceIds" \\ environment).extract[String]
  val logSourceSiteProtector = (logSourceTestFile \\ "LogSourceSiteProtector" \\ environment).extract[String]
  val logSourceFirewall = (logSourceTestFile \\ "LogSourceFirewall" \\ environment).extract[String]
  val logSourceBluecoatReporter = (logSourceTestFile \\ "LogSourceBluecoatReporter" \\ environment).extract[String]
  val logSourceOnsiteAggregator = (logSourceTestFile \\ "LogSourceOnsiteAggregator" \\ environment).extract[String]
  val logSourceManagerPlataform = (logSourceTestFile \\ "LogSourceManagerPlataform" \\ "positive" \\ environment).extract[String]
  val logSourceMetadata = (logSourceTestFile \\ "LogSourceMetadata" \\ environment).extract[String]
  val logSourceScanDrone = (logSourceTestFile \\ "LogSourceScanDrone" \\ "positive" \\ environment).extract[String]
  val logSourceDataMapFixesXPS164078 = (logSourceTestFile \\ "DataMapFixes-XPS-164078" \\ environment).extract[String]
  val metadataId = (logSourceTestFile \\ "MetadataIds" \\ environment).extract[String]

  val req01 = "Grab all device detail log source with limit 5"
  val req02 = "Grab data of a specific antivirus log source"
  val req03 = "Grab data of a specific BluecoatReporter log source"
  val req04 = "Grab data of a specific Firewall log source"
  val req05 = "Grab data of a specific Ids log source"
  val req06 = "Grab data of a specific ManagementStation log source"
  val req07 = "Grab data of a specific ManagerPlataform log source"
  val req08 = "Grab data of a specific Metadata log source"
  val req09 = "Grab data of a specific OnsiteAggregator log source"
  val req10 = "Grab data of a specific SiteProtector log source"
  val req11 = "PUT method for device detail log source"
  val req12 = "Validate data after method PUT"
  val req13 = "POST method for device detail log source"
  val req14 = "Validate data after method POST"
  val req15 = "Check all highAvailability=Disabled only when using highAvailability=Disable"
  val req16 = "Check all highAvailability=Disabled only when using highAvailability=Disabled"
  val req17 = "Check all highAvailability=Enabled only when using highAvailability=Enabled"
  val req18 = "Check all highAvailability=Enabled only when using highAvailability=Enable"
  val req19 = "Check masterSharedPlatform=Yes in the response exists"
  val req20 = "Check NO masterSharedPlatform=true in the response exists"
  val req21 = "Check masterSharedPlatform=No in the response exists"
  val req22 = "Check NO masterSharedPlatform=false in the response exists"
  val req23 = "GET: by lastModifiedDate range query - XPS-159935"
  val req24 = "GET: by lastModifiedDate range query with time - XPS-159935"
  val req25 = "GET: some fields yes/no and some fields with valid values or empty value - XPS-162102"
  val req26 = "GET: data mapping fixes - Remedy Mode - XPS-164078"
  val req27 = "Check groupBy parameter to find list of feature for customer"

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
   
  val scn = scenario("DeviceDetailLogSourceMs")
  
    //Grab all log source with limit 5
    .exec(http(req01)
      .get("micro/device_detail_log_source/?limit=5")
      .check(jsonPath("$[*].id").count.is(5))
      .check(jsonPath("$[*].deviceId").count.is(5))
      .check(jsonPath("$[?(@.managementStationConfigEnabled != null && @.firewallConfigEnabled != null && @.idsConfigEnabled != null && @.siteProtectorConfigEnabled != null && @.antivirusConfigEnabled != null && @.onsiteAggregatorConfigEnabled != null && @.bluecoatReporterConfigEnabled != null && @.scanDroneConfigEnabled != null && @.managerPlatformEnabled != null && @.sharedPlatform != null && @.portalViewable != null)].id").saveAs("ID_REQ01"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].managementStationConfigEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].firewallConfigEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].idsConfigEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].siteProtectorConfigEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].antivirusConfigEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].onsiteAggregatorConfigEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].bluecoatReporterConfigEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].scanDroneConfigEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].managerPlatformEnabled").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].sharedPlatform").in("Yes","No"))
      .check(jsonPath("$[?(@.id == '" + "${ID_REQ01}" + "')].portalViewable").in("Yes","No"))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Grab data of a specific Antivirus log source
    .exec(http(req02)
      .get("micro/device_detail_log_source/" + logSourceAntivirus)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceAntivirus))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..ftp").exists)
      .check(jsonPath("$[0]..smtp").exists)
      .check(jsonPath("$[0]..http").exists)
      .check(jsonPath("$[0]..cvp").exists)
      .check(jsonPath("$[0]..browser").exists)
      .check(jsonPath("$[0]..scanEngine").exists)
      .check(jsonPath("$[?(@.highAvailability == 'Disable')].id").count.is(0))
      .check(jsonPath("$[?(@.highAvailability == 'Disabled')].id").count.gte(1))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Grab data of a specific BluecoatReporter log source
    .exec(http(req03)
      .get("micro/device_detail_log_source/" + logSourceBluecoatReporter)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceBluecoatReporter))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..port").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Grab data of a specific Firewall log source
    .exec(http(req04)
      .get("micro/device_detail_log_source/" + logSourceFirewall)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceFirewall))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..policyName").exists)
      .check(jsonPath("$[0]..configurationType").exists)
      .check(jsonPath("$[0]..location").exists)
      .check(jsonPath("$[0]..managementStationType").exists)
      .check(jsonPath("$[0]..haPartnerDevice").exists)
      .check(jsonPath("$[0]..haPartnerDeviceId").exists)
      .check(jsonPath("$[0]..clusterOrFwObjectName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Grab data of a specific Ids log source
    .exec(http(req05)
      .get("micro/device_detail_log_source/" + logSourceIds)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceIds))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..sensorType").exists)
      .check(jsonPath("$[0]..logType").exists)
      .check(jsonPath("$[0]..proventiaServerGroupName").exists)
      .check(jsonPath("$[0]..haPartnerDeviceId").exists)
      .check(jsonPath("$[0]..idsPolicyName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }
       
    // Grab data of a specific ManagementStation log source
    .exec(http(req06)
      .get("micro/device_detail_log_source/" + logSourceManagementStation)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceManagementStation))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..station").exists)
      .check(jsonPath("$[0]..backupStation").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Grab data of a specific ManagerPlataform log source
    .exec(http(req07)
      .get("micro/device_detail_log_source/" + logSourceManagerPlataform)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceManagerPlataform))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..spApplicationServer").exists)
      .check(jsonPath("$[0]..customerId").exists)
      .check(jsonPath("$[0]..logManagerType").exists)
      .check(jsonPath("$[0]..spDatabase").exists)
      .check(jsonPath("$[0]..spEventCollector").exists)
      .check(jsonPath("$[0]..spXPUServer").exists)
      .check(jsonPath("$[0]..spAgentManager").exists)
      .check(jsonPath("$[0]..urlFilteringManager").exists)
      .check(jsonPath("$[0]..otherType").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Grab data of a specific Metadata log source
    .exec(http(req08)
      .get("micro/device_detail_log_source/" + logSourceMetadata)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceMetadata))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..logSourceId").exists)
      .check(jsonPath("$[0]..customerId").exists)
      .check(jsonPath("$[0]..customerName").exists)
      .check(jsonPath("$[0]..partnerId").exists)
      .check(jsonPath("$[0]..partnerName").exists)
      .check(jsonPath("$[0]..active").exists)
      .check(jsonPath("$[0]..attributeName").exists)
      .check(jsonPath("$[0]..attributeValue").exists)
      .check(jsonPath("$[0]..metadataConfiguration[0].id").is(metadataId))
      .check(jsonPath("$[0]..metadataConfiguration[0].logSourceId").is(logSourceMetadata))
      .check(jsonPath("$[0]..metadataConfiguration[0].active").exists)
      .check(jsonPath("$[0]..metadataConfiguration[0].attributeName").exists)
      .check(jsonPath("$[0]..metadataConfiguration[0].attributeValue").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Grab data of a specific OnsiteAggregator log source
    .exec(http(req09)
      .get("micro/device_detail_log_source/" + logSourceOnsiteAggregator)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceOnsiteAggregator))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..highAvailability").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }
      
    // Grab data of a specific SiteProtector log source
    .exec(http(req10)
      .get("micro/device_detail_log_source/" + logSourceSiteProtector)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is(logSourceSiteProtector))
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..rsspInstance").exists)
      .check(jsonPath("$[0]..eventCollector").exists)
      .check(jsonPath("$[0]..haPartnerDevice").exists)
      .check(jsonPath("$[0]..haPartnerDeviceId").exists)
      .check(jsonPath("$[0]..rsspDatabaseName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check for update functionality using PUT
    // QX-9738
    .exec(http(req11)
      .put("micro/device_detail_log_source/" + logSourceIds)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"status\":\"Inactive\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is(logSourceIds))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies).pause(30 seconds)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Validate the update functionality after PUT
    // QX-9738
    .exec(http(req12)
      .get("micro/device_detail_log_source/" + logSourceIds)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is(logSourceIds))
      .check(jsonPath("$..status").is("Inactive"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies).pause(30 seconds)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check for update functionality using POST
    // QX-9738
    .exec(http(req13)
      .post("micro/device_detail_log_source/")
      .basicAuth(contactUser, contactPass)
      .body(StringBody("{\"customerId\": \"P000000614\",  \"deviceId\": \"STG000008064420\",  \"appVersion\": \"Unknown\",  \"appVendor\": \"Unknown\",  \"noLogsReceivedThreshold\": \"Disabled\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").saveAs("Id"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies).pause(30 seconds)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Validate the update functionality after POST
    // QX-9738
    .exec(http(req14)
      .get("micro/device_detail_log_source/" + "${Id}")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${Id}"))
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..deviceId").is("STG000008064420"))
      .check(jsonPath("$..appVersion").is("Unknown"))
      .check(jsonPath("$..appVendor").is("Unknown"))
      .check(jsonPath("$..noLogsReceivedThreshold").is("Disabled"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies).pause(30 seconds)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Check all highAvailability=Disabled only when using highAvailability=Disable
    .exec(http(req15)
      .get("micro/device_detail_log_source/?limit=5&highAvailability=Disable")
      .check(jsonPath("$[*].id").count.lte(5))
      .check(jsonPath("$[*].deviceId").count.lte(5))
      .check(jsonPath("$[?(@.highAvailability == 'Disable')].id").count.is(0))
      .check(jsonPath("$[?(@.highAvailability == 'Disabled')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Check all highAvailability=Disabled only when using highAvailability=Disabled
    .exec(http(req16)
      .get("micro/device_detail_log_source/?limit=5&highAvailability=Disabled")
      .check(jsonPath("$[*].id").count.lte(5))
      .check(jsonPath("$[*].deviceId").count.lte(5))
      .check(jsonPath("$[?(@.highAvailability == 'Disable')].id").count.is(0))
      .check(jsonPath("$[?(@.highAvailability == 'Disabled')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Check all highAvailability=Enabled only when using highAvailability=Enabled
    .exec(http(req17)
      .get("micro/device_detail_log_source/?limit=5&highAvailability=Enabled")
      .check(jsonPath("$[*].id").count.lte(5))
      .check(jsonPath("$[*].deviceId").count.lte(5))
      .check(jsonPath("$[?(@.highAvailability == 'Enable')].id").count.is(0))
      .check(jsonPath("$[?(@.highAvailability == 'Enabled')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Check all highAvailability=Enabled only when using highAvailability=Enable
    .exec(http(req18)
      .get("micro/device_detail_log_source/?limit=5&highAvailability=Enable")
      .check(jsonPath("$[*].id").count.lte(5))
      .check(jsonPath("$[*].deviceId").count.lte(5))
      .check(jsonPath("$[?(@.highAvailability == 'Enable')].id").count.is(0))
      .check(jsonPath("$[?(@.highAvailability == 'Enabled')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Check masterSharedPlatform=Yes in the response exists
    .exec(http(req19)
      .get("micro/device_detail_log_source/?limit=5&masterSharedPlatform=Yes")
      .check(jsonPath("$[*].id").count.lte(5))
      .check(jsonPath("$[*].deviceId").count.lte(5))
      .check(jsonPath("$[?(@.masterSharedPlatform == 'true')].id").count.is(0))
      .check(jsonPath("$[?(@.masterSharedPlatform == 'Yes')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Check NO masterSharedPlatform=true in the response exists
    .exec(http(req20)
      .get("micro/device_detail_log_source/?limit=5&masterSharedPlatform=true")
      .check(jsonPath("$[*].id").count.lte(5))
      .check(jsonPath("$[*].deviceId").count.lte(5))
      .check(jsonPath("$[?(@.masterSharedPlatform == 'true')].id").count.is(0))
      .check(jsonPath("$[?(@.masterSharedPlatform == 'Yes')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Check masterSharedPlatform=No in the response exists
    .exec(http(req21)
      .get("micro/device_detail_log_source/?limit=5&masterSharedPlatform=No")
      .check(jsonPath("$[*].id").count.lte(5))
      .check(jsonPath("$[*].deviceId").count.lte(5))
      .check(jsonPath("$[?(@.masterSharedPlatform == 'false')].id").count.is(0))
      .check(jsonPath("$[?(@.masterSharedPlatform == 'No')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js21)) {
      exec( session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Check NO masterSharedPlatform=false in the response exists
    .exec(http(req22)
      .get("micro/device_detail_log_source/?limit=5&masterSharedPlatform=false")
      .check(jsonPath("$[*].id").count.lte(5))
      .check(jsonPath("$[*].deviceId").count.lte(5))
      .check(jsonPath("$[?(@.masterSharedPlatform == 'false')].id").count.is(0))
      .check(jsonPath("$[?(@.masterSharedPlatform == 'No')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js22)) {
      exec( session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }
   
    //GET: some fields yes/no and some fields with valid values or empty value - XPS-162102
    .exec(http(req25)
      .get("micro/device_detail_log_source/?limit=40")
      .check(status.is(200))
      .check(jsonPath("$[?(@.noLogsReceivedAlertingEnabled == 'Yes')].id").count.gte(1))
      .check(jsonPath("$[?(@.realTimeAnalyticsEnabled == 'Yes')].id").count.gte(1))
      .check(jsonPath("$[?(@.haConfigEnabled == 'Yes')].id").count.gte(1))
      .check(jsonPath("$[?(@.noLogsReceivedAlertingEnabled == 'No')].id").count.gte(1))
      .check(jsonPath("$[?(@.realTimeAnalyticsEnabled == 'No')].id").count.gte(1))
      .check(jsonPath("$[?(@.haConfigEnabled == 'No')].id").count.gte(1))
      .check(jsonPath("$[?(@.noLogsReceivedAlertingEnabled != 'Yes' && @.noLogsReceivedAlertingEnabled != 'No')].id").count.is(0))
      .check(jsonPath("$[?(@.realTimeAnalyticsEnabled != 'Yes' && @.realTimeAnalyticsEnabled != 'No')].id").count.is(0))
      .check(jsonPath("$[?(@.haConfigEnabled != 'Yes' && @.haConfigEnabled != 'No')].id").count.is(0))    
      .check(jsonPath("$[?(@.trendAnalytics == 'Trend Analytics')].id").count.gte(1))
      .check(jsonPath("$[?(@.aggregateAnalytics == 'Aggregate Analytics')].id").count.gte(1))
      .check(jsonPath("$[?(@.realtimeAnalytics == 'Realtime Analytics')].id").count.gte(1))
      .check(jsonPath("$[?(@.trendAnalytics == '')].id").count.gte(1))
      .check(jsonPath("$[?(@.aggregateAnalytics == '')].id").count.gte(1))
      .check(jsonPath("$[?(@.realtimeAnalytics == '')].id").count.gte(1))
      .check(jsonPath("$[?(@.trendAnalytics != 'Trend Analytics' && @.trendAnalytics != '')].id").count.is(0))
      .check(jsonPath("$[?(@.aggregateAnalytics != 'Aggregate Analytics' && @.aggregateAnalytics != '')].id").count.is(0))
      .check(jsonPath("$[?(@.realtimeAnalytics != 'Realtime Analytics' && @.realtimeAnalytics != '')].id").count.is(0))    
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js25)) {
      exec( session => {
        session.set(js25, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //GET: data mapping fixes - Remedy Mode - XPS-164078
    .exec(http(req26)
      .get("micro/device_detail_log_source/" + logSourceDataMapFixesXPS164078)
      .check(status.is(200))
      .check(jsonPath("$[0].id").is(logSourceDataMapFixesXPS164078))
      .check(jsonPath("$[0].configurationType").is("HA - Load Balanced"))
      .check(jsonPath("$[0].browser").is(""))
      .check(jsonPath("$[0].cvp").is(""))
      .check(jsonPath("$[0].ftp").is(""))
      .check(jsonPath("$[0].http").is(""))
      .check(jsonPath("$[0].smtp").is(""))
      .check(jsonPath("$[0].otherType").is(""))
      .check(jsonPath("$[0].spAgentManager").is(""))
      .check(jsonPath("$[0].spDatabase").is(""))
      .check(jsonPath("$[0].urlFilteringManager").is(""))
      .check(jsonPath("$[0].spApplicationServer").is(""))
      .check(jsonPath("$[0].spXPUServer").is(""))
      .check(jsonPath("$[0].spEventCollector").is(""))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js26))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js26)) {
      exec( session => {
        session.set(js26, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    /** the code below is commented due to XPS-159935 is not in the code yet. let's keep till we have it ready to improve it
    //GET: by lastModifiedDate range query - XPS-159935
    .exec(http(req27)
      .get("micro/device_detail_log_source/?range=lastModifiedDate(2022-04-14,2022-04-21)")
      .check(status.is(200))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(8, 10)).in("14","15","16","17","18","19","20","21"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(8, 10)).in("14","15","16","17","18","19","20","21"))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(24, 28)).is("2022"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(24, 28)).is("2022"))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(4, 7)).is("Apr"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(4, 7)).is("Apr"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js27)) {
      exec( session => {
        session.set(js27, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
  //GET: by lastModifiedDate range query with time - XPS-159935
    .exec(http(req28)
      .get("micro/device_detail_log_source/?range=lastModifiedDate(2022-04-14 06:00:00,2022-04-14 13:10:00)")
      .check(status.is(200))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(8, 10)).in("14","15","16","17","18","19","20","21"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(8, 10)).in("14","15","16","17","18","19","20","21"))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(24, 28)).is("2022"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(24, 28)).is("2022"))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(4, 7)).is("Apr"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(4, 7)).is("Apr"))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(11, 13)).gte("06"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(11, 13)).lte("13"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js28))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js28)) {
      exec( session => {
        session.set(js28, "Unable to retrieve JSESSIONID for this request")
      })
    }
    

    **/

    //Check groupBy parameter to find list of feature for customer
    .exec(http(req27)
      .get("micro/device_detail_log_source")
      .check(status.is(200))
      .queryParam("customerId", "P000000614")
      .queryParam("groupBy", "logTypeDescription.raw")
      .queryParam("limit", "7")
      .check(status.is(200))
      .check(jsonPath("$..logTypeDescription").exists)
      .check(jsonPath("$..count").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js27)) {
      exec(session => {
        session.set(js27, "Unable to retrieve JSESSIONID for this request")
      })
    }

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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
