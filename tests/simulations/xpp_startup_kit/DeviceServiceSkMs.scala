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
 *  Developed by: Renata Angelelli / Caio Gobbi
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8615
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8603
 */

class DeviceServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/device_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "GET - Verify 401 is returned when no credentials are provided"
  val req2 = "GET - Verify 401 is returned when incorrect user/pass is provided"
  val req3 = "GET - Verify all records are returned when base URL is used (should return at least one device of each type"
  val req4 = "GET - Verify 401 is returned when no credentials are provided again (cookies check)"
  val req5 = "GET - Verify proper records are returned for text search 'qradar'"
  val req6 = "GET - Verify proper records are returned for text search 'splunk'"
  val req7 = "GET - Verify proper records are returned for text search 'cp4s'"
  val req8 = "GET - Verify case insensitivity, same as above with 'QRADAR'"
  val req9 = "GET - Verify incorrect search pattern returns 200 but no data, e.g 'qrada'"
  val req10 = "POST - Create a new BOGUS device"
  val req11 = "GET - Fetch the BOGUS device previously created"
  val req12 = "DELETE - Delete the BOGUS device previously created"
  val req13 = "GET - Validate the BOGUS device previously created does not exist anymore"
  val req14 = "DELETE - Try to delete a BOGUS device that does not exist"

  val httpProtocolDeviceServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("DeviceServiceSkMs")
    .exec(http(req1)
      .get("micro/device")
      .check(status.is(401))
      .check(jsonPath("$..timestamp").exists)
      .check(jsonPath("$..status").is("401"))
      .check(jsonPath("$..error").is("Unauthorized"))
      .check(jsonPath("$..message").exists)
      .check(jsonPath("$..path").is("/micro/device"))
    )

    .exec(http(req2)
      .get("micro/device")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )
    
    .exec(http(req3)
      .get("micro/device")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].customerId").is("CIDD706957"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].customerName").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].deviceId").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].deviceIp").is("207.231.141.101"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].hostName").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].platform").is("QRadar Console"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].pollingRequired").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].siemInfo").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].customerId").is("CIDD706957"))
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].customerName").is("UAT-Customer"))
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].deviceId").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].deviceIp").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].hostName").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].platform").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].pollingRequired").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].siemInfo..splunkPassword").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].siemInfo..splunkUser").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].siemInfo..splunkHost").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].siemInfo..qradarMultiTenant").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].customerId").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].customerName").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].deviceId").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].deviceIp").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].hostName").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].platform").is("cp4sSoar"))
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].pollingRequired").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].siemInfo.soarOrgId").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].siemInfo.accountName").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].siemInfo.soarKeySecret").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].siemInfo.soarKeyId").exists)
    )

    .exec(http(req4)
      .get("micro/device")
      .check(status.is(401))
      .check(jsonPath("$..timestamp").exists)
      .check(jsonPath("$..status").is("401"))
      .check(jsonPath("$..error").is("Unauthorized"))
      .check(jsonPath("$..message").exists)
      .check(jsonPath("$..path").is("/micro/device"))
    )

    .exec(http(req5)
      .get("micro/device/platform/qradar")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].customerId").is("CIDD706957"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].customerName").is("UAT-Customer"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].deviceId").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].deviceIp").is("207.231.141.101"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].hostName").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].platform").is("QRadar Console"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].pollingRequired").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].siemInfo").exists)
      .check(jsonPath("$..[?(@.platform == \"Splunk Search Head\")].customerId").notExists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].customerId").notExists)
    )

    .exec(http(req6)
      .get("micro/device/platform/splunk")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].customerId").is("CIDD706957"))
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].customerName").is("UAT-Customer"))
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].deviceId").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].deviceIp").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].hostName").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].platform").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].pollingRequired").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].siemInfo..splunkPassword").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].siemInfo..splunkUser").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].siemInfo..splunkHost").exists)
      .check(jsonPath("$..[?(@.hostName == \"splunkServer\")].siemInfo..qradarMultiTenant").exists)
      .check(jsonPath("$..[?(@.hostName == \"QRadar Console\")].customerId").notExists)
      .check(jsonPath("$..[?(@.hostName == \"cp4sSoar\")].customerId").notExists)
    )

    .exec(http(req7)
      .get("micro/device/platform/cp4sSoar")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].customerId").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].customerName").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].deviceId").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].deviceIp").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].hostName").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].platform").is("cp4sSoar"))
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].pollingRequired").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].siemInfo.soarOrgId").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].siemInfo.accountName").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].siemInfo.soarKeySecret").exists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].siemInfo.soarKeyId").exists)
      .check(jsonPath("$..[?(@.platform == \"Splunk Search Head\")].customerId").notExists)
      .check(jsonPath("$..[?(@.platform == \"QRadar Console\")].customerId").notExists)
    )

    .exec(http(req8)
      .get("micro/device/platform/QRADAR")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].customerId").is("CIDD706957"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].customerName").is("UAT-Customer"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].deviceId").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].deviceIp").is("207.231.141.101"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].hostName").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].platform").is("QRadar Console"))
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].pollingRequired").exists)
      .check(jsonPath("$..[?(@.deviceId == \"PRD00001\")].siemInfo").exists)
      .check(jsonPath("$..[?(@.platform == \"Splunk Search Head\")].customerId").notExists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].customerId").notExists)
    )

    .exec(http(req9)
      .get("micro/device/platform/qrada")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.platform == \"Splunk Search Head\")].customerId").notExists)
      .check(jsonPath("$..[?(@.platform == \"cp4sSoar\")].customerId").notExists)
      .check(jsonPath("$..[?(@.platform == \"QRadar Console\")].customerId").notExists)
      .check(bodyString.transform(_.size < 3).is(true))
    )


    // THE SCENARIOS BELOW ARE ONLY IMPLEMENTED ON DAL09 AT THE MOMENT
      
    // // "POST - Create a new BOGUS device"
    // exec(http(req10)
    //   .post("micro/device")
    //   .basicAuth("admin", sKPass)
    //   .body(StringBody("{\"customerId\": \"BogusCustomerID\",\"customerName\": \"BOGUS-Customer\",\"deviceId\": \"AutomationTestDeviceIp\",\"deviceIp\": \"127.0.0.1\", \"platform\": \"AutomationTestPlatform\",\"pollingRequired\": false}"))
    //   .check(status.is(201))
    //   .check(jsonPath("$.success").is("Device created successfully"))
    // )

    // // "GET - Fetch the BOGUS device previously created"
    // .exec(http(req11)
    //   .get("micro/device")
    //   .basicAuth("admin", sKPass)
    //   .check(status.is(200))
    //   .check(jsonPath("$..[?(@.platform == \"AutomationTestPlatform\")].customerId").is("BogusCustomerID"))
    // )

    // // "DELETE - Delete the BOGUS device previously created"
    // .exec(http(req12)
    //   .delete("micro/device/AutomationTestDeviceIp")
    //   .basicAuth("admin", sKPass)
    //   .check(status.is(204))
    // )

    // "GET - Validate the BOGUS device previously created does not exist anymore"
    .exec(http(req13)
      .get("micro/device")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.platform == \"AutomationTestPlatform\")].customerId").notExists)
    )

    // // "DELETE - Try to delete a BOGUS device that does not exist"
    // .exec(http(req14)
    //   .delete("micro/device/AutomationTestDeviceIp")
    //   .basicAuth("admin", sKPass)
    //   .check(status.is(404))
    //   .check(jsonPath("$.error").is("device AutomationTestDeviceIp not found"))
    // )


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolDeviceServiceSkMs).assertions(global.failedRequests.count.is(0))
}