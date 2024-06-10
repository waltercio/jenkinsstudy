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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10967
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-10892
 */

//To get environment variables, contact Renata Angelelli/Laura Salomao

class CrResponseServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/cr_response_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

val req1 = "Negative Test: Authenticating with bad credentials"
val req2 = "Negative Test: Authenticating with no credentials"
val req3 = "POST: Post action BAN_HASH"
val req4 = "Negative Test: Cookie check - providing no credentials should fail"
val req5 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req6 = "POST: Post action UNBAN_HASH"
val req7 = "POST: Post action ISOLATE_MACHINE"
val req8 = "POST: Post action UNISOLATE_MACHINE"

  
  val httpProtocolCrResponseServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("CrResponseServiceSkMs")

    // "Cr Username secret"
    .exec(session => {
      val username = System.getenv("CR_USER")
      session.set("CR_USER", username)
    })

    // "Cr password secret"
    .exec(session => {
      val password = System.getenv("CR_PASS")
      session.set("CR_PASS", password)
    })

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("respond")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"ae61d8f04bcde8158304067913160b31\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"deepblue.cybereason.net\",\"username\": \"${CR_USER}\",\"password\": \"${CR_PASS}\"}}"))
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("respond")
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"ae61d8f04bcde8158304067913160b31\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"deepblue.cybereason.net\",\"username\": \"${CR_USER}\",\"password\": \"${CR_PASS}\"}}"))
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "POST: Post action BAN_HASH"
    .exec(http(req3)
      .post("respond")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"ae61d8f04bcde8158304067913160b31\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"deepblue.cybereason.net\",\"username\": \"${CR_USER}\",\"password\": \"${CR_PASS}\"}}"))
      .check(status.is(200))
    )

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req4)
      .post("respond")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"ae61d8f04bcde8158304067913160b31\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"deepblue.cybereason.net\",\"username\": \"${CR_USER}\",\"password\": \"${CR_PASS}\"}}"))
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req5)
      .post("respond")
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"ae61d8f04bcde8158304067913160b31\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"deepblue.cybereason.net\",\"username\": \"${CR_USER}\",\"password\": \"${CR_PASS}\"}}"))
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "POST: Post action UNBAN_HASH"
    .exec(http(req6)
      .post("respond")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"ae61d8f04bcde8158304067913160b31\",\"actionType\": \"UNBAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"deepblue.cybereason.net\",\"username\": \"${CR_USER}\",\"password\": \"${CR_PASS}\"}}"))      
      .check(status.is(200))
    )

    // "POST: Post action ISOLATE_MACHINE"
    .exec(http(req7)
      .post("respond")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"PYLUMCLIENT_DEEPBLUE_WIN-1QCECDN7GD2_000C29DCC6DC\",\"actionType\": \"ISOLATE_MACHINE\",\"clientConfiguration\": {\"baseUrl\": \"deepblue.cybereason.net\",\"username\": \"${CR_USER}\",\"password\": \"${CR_PASS}\"}}"))      
      .check(status.is(200))
    )

    // "POST: Post action UNISOLATE_MACHINE"
    .exec(http(req8)
      .post("respond")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"PYLUMCLIENT_DEEPBLUE_WIN-1QCECDN7GD2_000C29DCC6DC\",\"actionType\": \"UNISOLATE_MACHINE\",\"clientConfiguration\": {\"baseUrl\": \"deepblue.cybereason.net\",\"username\": \"${CR_USER}\",\"password\": \"${CR_PASS}\"}}"))      
      .check(status.is(200))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolCrResponseServiceSkMs).assertions(global.failedRequests.count.is(0))
}