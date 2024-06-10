import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import org.json4s.jackson.Serialization._
import java.io._
import java.time.Instant

/**
 *  Developed by: Laura Salomao / laura.oliveira1@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10803
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-10892
 */

class MdatpResponseServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/mdatp_response_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

val req1 = "Negative Test: Authenticating with bad credentials"
val req2 = "Negative Test: Authenticating with no credentials"
val req3 = "POST: Post action BAN_HASH"
val req4 = "Negative Test: Cookie check - providing no credentials should fail"
val req5 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req6 = "POST: Post action UNBAN_HASH"
val req7 = "POST: Post action ISOLATE_MACHINE"
val req8 = "POST: Post action UNISOLATE_MACHINE"

  
  val httpProtocolMdatpResponseServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("MdatpResponseServiceSkMs")

    // "MDATP App Id secret"
    .exec(session => {
      val applicationId = System.getenv("MDATP_API_ID")
      session.set("MDATP_API_ID", applicationId)
    })

    // "MDATP App Key secret"
    .exec(session => {
      val applicationKey = System.getenv("MDATP_API_KEY")
      session.set("MDATP_API_KEY", applicationKey)
    })

    // "MDATP Tenent Id secret"
    .exec(session => {
      val tenantId = System.getenv("MDATP_TENANT_ID")
      session.set("MDATP_TENANT_ID", tenantId)
    })

    // "MDATP Login URL secret"
    .exec(session => {
      val loginUrl = System.getenv("MDATP_LOGIN")
      session.set("MDATP_LOGIN", loginUrl)
    })

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("respond")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"7395a3ada245df6c8ff1d66fcb54b96ae12961d5fd9b6a57c43a3e7ab83f3cc2\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"api.securitycenter.microsoft.com\",\"applicationKey\": \"${MDATP_API_KEY}\",\"applicationId\": \"${MDATP_API_ID}\",\"tenantId\": \"${MDATP_TENANT_ID}\",\"loginUrl\": \"${MDATP_LOGIN}\"}}"))      
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("respond")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"7395a3ada245df6c8ff1d66fcb54b96ae12961d5fd9b6a57c43a3e7ab83f3cc2\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"api.securitycenter.microsoft.com\",\"applicationKey\": \"${MDATP_API_KEY}\",\"applicationId\": \"${MDATP_API_ID}\",\"tenantId\": \"${MDATP_TENANT_ID}\",\"loginUrl\": \"${MDATP_LOGIN}\"}}"))      
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "POST: Post action BAN_HASH"
    .exec(http(req3)
      .post("respond")
      .basicAuth("admin", sKPass)
      .header("Content-Type", "application/json")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"7395a3ada245df6c8ff1d66fcb54b96ae12961d5fd9b6a57c43a3e7ab83f3cc2\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"api.securitycenter.microsoft.com\",\"applicationKey\": \"${MDATP_API_KEY}\",\"applicationId\": \"${MDATP_API_ID}\",\"tenantId\": \"${MDATP_TENANT_ID}\",\"loginUrl\": \"${MDATP_LOGIN}\"}}"))      
      .check(status.is(200))
    )

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req4)
      .post("respond")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"7395a3ada245df6c8ff1d66fcb54b96ae12961d5fd9b6a57c43a3e7ab83f3cc2\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"api.securitycenter.microsoft.com\",\"applicationKey\": \"${MDATP_API_KEY}\",\"applicationId\": \"${MDATP_API_ID}\",\"tenantId\": \"${MDATP_TENANT_ID}\",\"loginUrl\": \"${MDATP_LOGIN}\"}}"))      
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req5)
      .post("respond")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"7395a3ada245df6c8ff1d66fcb54b96ae12961d5fd9b6a57c43a3e7ab83f3cc2\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"api.securitycenter.microsoft.com\",\"applicationKey\": \"${MDATP_API_KEY}\",\"applicationId\": \"${MDATP_API_ID}\",\"tenantId\": \"${MDATP_TENANT_ID}\",\"loginUrl\": \"${MDATP_LOGIN}\"}}"))      
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "POST: Post action UNBAN_HASH"
    .exec(http(req6)
      .post("respond")
      .basicAuth("admin", sKPass)
      .header("Content-Type", "application/json")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"7395a3ada245df6c8ff1d66fcb54b96ae12961d5fd9b6a57c43a3e7ab83f3cc2\",\"actionType\": \"UNBAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"api.securitycenter.microsoft.com\",\"applicationKey\": \"${MDATP_API_KEY}\",\"applicationId\": \"${MDATP_API_ID}\",\"tenantId\": \"${MDATP_TENANT_ID}\",\"loginUrl\": \"${MDATP_LOGIN}\"}}"))      
      .check(status.is(200))
    )

    // "POST: Post action ISOLATE_MACHINE"
    .exec(http(req7)
      .post("respond")
      .basicAuth("admin", sKPass)
      .header("Content-Type", "application/json")
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"6a813bbd845c2bf3115e24c40fd9a902a3047e8b\",\"actionType\": \"ISOLATE_MACHINE\",\"clientConfiguration\": {\"baseUrl\": \"api.securitycenter.microsoft.com\",\"applicationKey\": \"${MDATP_API_KEY}\",\"applicationId\": \"${MDATP_API_ID}\",\"tenantId\": \"${MDATP_TENANT_ID}\",\"loginUrl\": \"${MDATP_LOGIN}\"}}"))      
      .check(status.is(200))
    )

    // "POST: Post action UNISOLATE_MACHINE"
    .exec(http(req8)
      .post("respond")
      .basicAuth("admin", sKPass)
      .header("Content-Type", "application/json")
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"6a813bbd845c2bf3115e24c40fd9a902a3047e8b\",\"actionType\": \"UNISOLATE_MACHINE\",\"clientConfiguration\": {\"baseUrl\": \"api.securitycenter.microsoft.com\",\"applicationKey\": \"${MDATP_API_KEY}\",\"applicationId\": \"${MDATP_API_ID}\",\"tenantId\": \"${MDATP_TENANT_ID}\",\"loginUrl\": \"${MDATP_LOGIN}\"}}"))      
      .check(status.is(200))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolMdatpResponseServiceSkMs).assertions(global.failedRequests.count.is(0))
}