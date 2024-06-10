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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10840
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-10916
 */

//To get environment variables, contact Renata Angelelli/Laura Salomao

class CbcResponseServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/cbc_response_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

val req1 = "Negative Test: Authenticating with bad credentials"
val req2 = "Negative Test: Authenticating with no credentials"
val req3 = "POST: Post action BAN_HASH"
val req4 = "Negative Test: Cookie check - providing no credentials should fail"
val req5 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req6 = "POST: Post action UNBAN_HASH"
val req7 = "POST: Post action ISOLATE_MACHINE"
val req8 = "POST: Post action UNISOLATE_MACHINE"

  
  val httpProtocolCbcResponseServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("CbcResponseServiceSkMs")

    // "ORG ID secret"
    .exec(session => {
      val orgId = System.getenv("CBC_ORG_ID")
      session.set("CBC_ORG_ID", orgId)
    })

    // "ORG KEY secret"
    .exec(session => {
      val orgKey = System.getenv("CBC_ORG_KEY")
      session.set("CBC_ORG_KEY", orgKey)
    })

    // "API ID secret"
    .exec(session => {
      val apiId = System.getenv("CBC_API_ID")
      session.set("CBC_API_ID", apiId)
    })

    // "API KEY secret"
    .exec(session => {
      val apiKey = System.getenv("CBC_API_KEY")
      session.set("CBC_API_KEY", apiKey)
    })



    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("respond")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"321a38851faf17530a6315706d23882f93af0c3dc26491541d6a5857f18a2ef1\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"defense-eap01.conferdeploy.net\",\"orgId\": \"${CBC_ORG_ID}\",\"orgKey\": \"${CBC_ORG_KEY}\",\"apiId\": \"${CBC_API_ID}\",\"apiKey\": \"${CBC_API_KEY}\"}}"))
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("respond")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"321a38851faf17530a6315706d23882f93af0c3dc26491541d6a5857f18a2ef1\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"defense-eap01.conferdeploy.net\",\"orgId\": \"${CBC_ORG_ID}\",\"orgKey\": \"${CBC_ORG_KEY}\",\"apiId\": \"${CBC_API_ID}\",\"apiKey\": \"${CBC_API_KEY}\"}}"))
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "POST: Post action BAN_HASH"
    .exec(http(req3)
      .post("respond")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"321a38851faf17530a6315706d23882f93af0c3dc26491541d6a5857f18a2ef1\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"defense-eap01.conferdeploy.net\",\"orgId\": \"${CBC_ORG_ID}\",\"orgKey\": \"${CBC_ORG_KEY}\",\"apiId\": \"${CBC_API_ID}\",\"apiKey\": \"${CBC_API_KEY}\"}}"))
      .check(status.is(200))
    )

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req4)
      .post("respond")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"321a38851faf17530a6315706d23882f93af0c3dc26491541d6a5857f18a2ef1\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"defense-eap01.conferdeploy.net\",\"orgId\": \"${CBC_ORG_ID}\",\"orgKey\": \"${CBC_ORG_KEY}\",\"apiId\": \"${CBC_API_ID}\",\"apiKey\": \"${CBC_API_KEY}\"}}"))
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req5)
      .post("respond")
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"321a38851faf17530a6315706d23882f93af0c3dc26491541d6a5857f18a2ef1\",\"actionType\": \"BAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"defense-eap01.conferdeploy.net\",\"orgId\": \"${CBC_ORG_ID}\",\"orgKey\": \"${CBC_ORG_KEY}\",\"apiId\": \"${CBC_API_ID}\",\"apiKey\": \"${CBC_API_KEY}\"}}"))
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "POST: Post action UNBAN_HASH"
    .exec(http(req6)
      .post("respond")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"actionId\": \"53694\",\"actionTarget\": \"321a38851faf17530a6315706d23882f93af0c3dc26491541d6a5857f18a2ef1\",\"actionType\": \"UNBAN_HASH\",\"clientConfiguration\": {\"baseUrl\": \"defense-eap01.conferdeploy.net\",\"orgId\": \"${CBC_ORG_ID}\",\"orgKey\": \"${CBC_ORG_KEY}\",\"apiId\": \"${CBC_API_ID}\",\"apiKey\": \"${CBC_API_KEY}\"}}"))
      .check(status.is(200))
    )

    // "POST: Post action ISOLATE_MACHINE"
    .exec(http(req7)
      .post("respond")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"actionId\": \"000-001-0001\",\"actionTarget\": \"172689\",\"actionType\": \"QUARANTINE_DEVICE\",\"clientConfiguration\": {\"baseUrl\": \"defense-eap01.conferdeploy.net\",\"orgId\": \"${CBC_ORG_ID}\",\"orgKey\": \"${CBC_ORG_KEY}\",\"apiId\": \"${CBC_API_ID}\",\"apiKey\": \"${CBC_API_KEY}\"}}"))
      .check(status.is(200))
    )

    // "POST: Post action UNISOLATE_MACHINE"
    .exec(http(req8)
      .post("respond")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"actionId\": \"1\",\"actionTarget\": \"172689\",\"actionType\": \"UNQUARANTINE_DEVICE\",\"clientConfiguration\": {\"baseUrl\": \"defense-eap01.conferdeploy.net\",\"orgId\": \"${CBC_ORG_ID}\",\"orgKey\": \"${CBC_ORG_KEY}\",\"apiId\": \"${CBC_API_ID}\",\"apiKey\": \"${CBC_API_KEY}\"}}"))
      .check(status.is(200))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolCbcResponseServiceSkMs).assertions(global.failedRequests.count.is(0))
}
