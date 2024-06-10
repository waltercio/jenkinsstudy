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
*  Developed by: Laura Salomao
*  Automation task for this script: https://jira.sec.ibm.com/browse/QX-11359
*  Functional test link: https://jira.sec.ibm.com/browse/QX-11349
*/


class MdrHealthcheckSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/mdr_healthcheck_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

val req1 = "Negative Test: Authenticating with bad credentials"
val req2 = "Negative Test: Authenticating with no credentials"
val req3 = "Metrics: Get the update success metrics"
val req4 = "Metrics: Get the update error metrics"
val req5 = "GET: Run health check"
val req6 = "Negative Test: Cookie check - providing no credentials should fail"
val req7 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req8 = "Metrics: Validate the update success metric increased"
val req9 = "Metrics: Validate the update error metric increased"

  val httpProtocolMdrHealthcheckSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("MdrHealthcheckSkMs")

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .get("test/alerts")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .get("test/alerts")
      .check(status.is(401))
    )

    // "Metrics: Get the update success metrics"
    .exec(http(req3)
      .get("micro/metrics/health_alert_update_success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

  // "Metrics: Get the update error metrics"
  .exec(http(req4)
    .get("micro/metrics/health_alert_update_error")
    .basicAuth("admin", sKPass)
    .check(status.is(200))
    .check(bodyString.saveAs("updateErrorMetrics"))
  )

    // "GET: Run health check"
    .exec(http(req5)
      .get("test/alerts")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    ).pause(45) // adding 45 seconds so that we see metrics increasing

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req6)
      .get("test/alerts")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req7)
      .get("test/alerts")
      .check(status.is(401))
    )

    // "Metrics: Validate the update success metric increased"
    .exec(http(req8)
      .get("micro/metrics/health_alert_update_success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updateSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updateSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // "Metrics: Validate update error metric increased"
  .exec(http(req9)
    .get("micro/metrics/health_alert_update_error")
    .basicAuth("admin", sKPass)
    .check(status.is(200))
    .check(bodyString.is("${updateErrorMetrics}"))
  )


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolMdrHealthcheckSkMs).assertions(global.failedRequests.count.is(0))
}
