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
    *  Developed by: Renata Angelelli
    *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9759
    *  Functional test link: https://jira.sec.ibm.com/browse/QX-9486
    *  Updated: https://jira.sec.ibm.com/browse/QX-14033
 */

class AthenaMs extends BaseTest {

  val athenaBaseURL = (configurations \\ "athenaBaseURL" \\ environment).extract[String]

  // Name of each request
  val req01 = "POST - Generate JWT Token"
  val req02 = "GET - health/ready"
  val req03 = "GET - health/alive"
 // val req04 = "GET - athena-ms/docs"
 // val req05 = "GET - info"
 // val req06 = "GET - metrics"
  val req07 = "POST - athena/mdr/prediction"
  val req08 = "POST - Negative Scenario - prediciton without alertId"
 // val req09 = "POST - Wrong Auth" TBD
 // val req10 = "POST - No Auth" TBD

  // Creating a val to store the jsession of each request
  val js01 = "jsession01"
  val js02 = "jsession02"
  val js03 = "jsession03"
 // val js04 = "jsession04"
 // val js05 = "jsession05"
 // val js06 = "jsession06"
  val js07 = "jsession07"
  val js08 = "jsession08"
 // val js09 = "jsession09"
 // val js10 = "jsession10"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AthenaMs")

    // POST - Generate JWT Token
    .exec(http(req01)
      .post("micro/jwt_provider/issue")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"x-remoteip\": \"206.253.242.201\", \"sub\": \"Microservices\", \"user-realm\": \"MSS\", \"iss\": \"sec.ibm.com\", \"privileged-user\": true, \"username\": MSSToken }"))
      .check(status.is(200))
      .check(bodyString.saveAs("JWT_TOKEN"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // GET - health/ready
    .exec(http(req02)
      .get(athenaBaseURL + "health/ready")
      .header("Authorization", "Bearer " + "${JWT_TOKEN}")
      .check(status.is(200))
      .check(jsonPath("$..status").is("True"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // GET - health/alive
    .exec(http(req03)
      .get(athenaBaseURL + "health/alive")
      .header("Authorization", "Bearer " + "${JWT_TOKEN}")
      .check(status.is(200))
      .check(jsonPath("$..status").is("running"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //TO BE IMPLEMENTED YET  - Reviewing this endpoint with the AI-ML team
  //  .exec(http(req04)
  //    .get(baseURL + "micro/athena/docs")
  //    .basicAuth(adUser, adPass)
  //    .check(status.is(200))
  //    .check(bodyString.transform(_.size > 100).is(true))
  //    .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
  //  ).exec(flushSessionCookies)
  //  .doIf(session => !session.contains(js04)) {
  //    exec( session => {
  //      session.set(js04, "Unable to retrieve JSESSIONID for this request")
  //    })
  //  }

    //TO BE IMPLEMENTED YET  - Reviewing this endpoint with the AI-ML team
  //  .exec(http(req05)
   //   .get(athenaBaseURL + "info")
  //    .basicAuth(adUser, adPass)
  //    .check(status.is(200))
  //    .check(jsonPath("$..build_version").exists)
  //    .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
  //  ).exec(flushSessionCookies)
  //  .doIf(session => !session.contains(js05)) {
  //    exec( session => {
  //      session.set(js05, "Unable to retrieve JSESSIONID for this request")
  //    })
  //  }

  //TO BE IMPLEMENTED YET  - Reviewing this endpoint with the AI-ML team
  //  .exec(http(req06)
  //    .get(athenaBaseURL + "metrics")
  //    .basicAuth(adUser, adPass)
  //    .check(status.is(200))
  //    .check(bodyString.transform(_.size > 100).is(true))
  //   .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
  //  ).exec(flushSessionCookies)
  //  .doIf(session => !session.contains(js06)) {
  //    exec( session => {
  //      session.set(js06, "Unable to retrieve JSESSIONID for this request")
  //    })
  //  }

    // POST - athena/mdr/prediction
    .exec(http(req07)
      .post(baseUrl + "micro/athena/mdr/prediction")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"1234567\":{ \"customer_id\": \"PR00005403\", \"alert_id\": 1234567, \"siem_vendor\": \"CROWDSTRIKE\", \"command_line_value\": \"['C:\\\\Program Files (x86)\\\\Internet Explorer\\\\IEXPLORE.EXE SCODEF:18560 CREDAT:17600 /prefetch:2']\", \"customer_name\": \"COSAN S\\/A - SANTA HELENA\", \"device_name\": \"CG_CAMP_N131\", \"event_names\": \"Adware\\/PUP\", \"process_name\": \"iexplore.exe\", \"hash\": \"071277cc2e3df41eeea8013e2ab58d5a\", \"description\": \"Flow: Outbound Traffic.\", \"rule_name\": \"MDR_AE_CS_NGAV_Low\", \"date\": \"2022-04-01 22:58:00\", \"signature_count\": 2, \"severity\": 90 }}"))
      .check(status.is(200))
      .check(jsonPath("$..1234567..customer_id").is("PR00005403"))
      .check(jsonPath("$..1234567..alert_id").is("1234567"))
      .check(jsonPath("$..1234567..predictions..model_name").exists)
      .check(jsonPath("$..1234567..predictions..confidence").exists)
      .check(jsonPath("$..1234567..predictions..recommendation").exists)
      .check(jsonPath("$..1234567..rare_events").exists)
      .check(jsonPath("$..1234567..key_indicators..name").exists)
      .check(jsonPath("$..1234567..key_indicators..value").exists)
      .check(jsonPath("$..1234567..conclusion").exists)
      .check(substring("Machine Learning recommends Alert to be").exists)
      .check(jsonPath("$..1234567..models_version").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // POST - Negative Scenario - prediciton without alertId
    .exec(http(req08)
      .post(baseUrl + "micro/athena/mdr/prediction")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"1234567\":{ \"customer_id\": \"PR00005403\", \"siem_vendor\": \"CROWDSTRIKE\", \"command_line_value\": \"['C:\\\\Program Files (x86)\\\\Internet Explorer\\\\IEXPLORE.EXE SCODEF:18560 CREDAT:17600 /prefetch:2']\", \"customer_name\": \"COSAN S\\/A - SANTA HELENA\", \"device_name\": \"CG_CAMP_N131\", \"event_names\": \"Adware\\/PUP\", \"process_name\": \"iexplore.exe\", \"hash\": \"071277cc2e3df41eeea8013e2ab58d5a\", \"description\": \"Flow: Outbound Traffic.\", \"rule_name\": \"MDR_AE_CS_NGAV_Low\", \"date\": \"2022-04-01 22:58:00\", \"signature_count\": 2, \"severity\": 90 }}"))
      .check(status.is(500))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //TO BE IMPLEMENTED YET  - Reviewing this scenario with the AI-ML team
    // .exec(http(req09)
    //   .post("athena/mdr/prediction")
    //   .basicAuth(adUser, "wrongPass")
    //   .body(StringBody("{\"1234567\":{ \"customer_id\": \"PR00005403\", \"alert_id\": 1234567, \"siem_vendor\": \"CROWDSTRIKE\", \"command_line_value\": \"['C:\\\\Program Files (x86)\\\\Internet Explorer\\\\IEXPLORE.EXE SCODEF:18560 CREDAT:17600 /prefetch:2']\", \"customer_name\": \"COSAN S\\/A - SANTA HELENA\", \"device_name\": \"CG_CAMP_N131\", \"event_names\": \"Adware\\/PUP\", \"process_name\": \"iexplore.exe\", \"hash\": \"071277cc2e3df41eeea8013e2ab58d5a\", \"description\": \"Flow: Outbound Traffic.\", \"rule_name\": \"MDR_AE_CS_NGAV_Low\", \"date\": \"2022-04-01 22:58:00\", \"signature_count\": 2, \"severity\": 90 }}")
    //   .check(status.is(401))
    //   .check(jsonPath("$..message").is("Unauthenticated"))
    //   .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    // ).exec(flushSessionCookies)
    // .doIf(session => !session.contains(js09)) {
    //   exec( session => {
    //     session.set(js09, "Unable to retrieve JSESSIONID for this request")
    //   })
    // }

    //TO BE IMPLEMENTED YET  - Reviewing this scenario with the AI-ML team
    // .exec(http(req10)
    //   .post("athena/mdr/prediction")
    //   .body(StringBody("{\"1234567\":{ \"customer_id\": \"PR00005403\", \"alert_id\": 1234567, \"siem_vendor\": \"CROWDSTRIKE\", \"command_line_value\": \"['C:\\\\Program Files (x86)\\\\Internet Explorer\\\\IEXPLORE.EXE SCODEF:18560 CREDAT:17600 /prefetch:2']\", \"customer_name\": \"COSAN S\\/A - SANTA HELENA\", \"device_name\": \"CG_CAMP_N131\", \"event_names\": \"Adware\\/PUP\", \"process_name\": \"iexplore.exe\", \"hash\": \"071277cc2e3df41eeea8013e2ab58d5a\", \"description\": \"Flow: Outbound Traffic.\", \"rule_name\": \"MDR_AE_CS_NGAV_Low\", \"date\": \"2022-04-01 22:58:00\", \"signature_count\": 2, \"severity\": 90 }}")
    //   .check(status.is(401))
    //   .check(jsonPath("$..message").is("Unauthenticated"))
    //   .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    // ).exec(flushSessionCookies)
    // .doIf(session => !session.contains(js10)) {
    //   exec( session => {
    //     session.set(js10, "Unable to retrieve JSESSIONID for this request")
    //   })
    // }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
     // jsessionMap += (req04 -> session(js04).as[String])
     // jsessionMap += (req05 -> session(js05).as[String])
     // jsessionMap += (req06 -> session(js06).as[String])
      jsessionMap += (req07 -> session(js07).as[String])
      jsessionMap += (req08 -> session(js08).as[String])
     // jsessionMap += (req09 -> session(js09).as[String])
     // jsessionMap += (req10 -> session(js10).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolNoBasicAuth).assertions(global.failedRequests.count.is(0))
}