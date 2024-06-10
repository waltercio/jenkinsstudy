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
import java.time.Instant

/**
 *  Developed by: Caio Gobbi
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8833
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8813
*/

class SimpleTcpListenerSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath

  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/simple_tcp_listener_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val unixTimestamp = Instant.now.getEpochSecond


  val req1 = "Metrics: Fetch the current success metrics before any testing is done"
  val req2 = "Metrics: Fetch the current unknown metrics before any testing is done"
  val req3 = "POST existing deviceIp that belongs to only one customer"
  val req4 = "Metrics: Validate the success metric has increased after a successful POST"
  val req5 = "POST with a non existent Device IP"
  val req6 = "Metrics: Validate the unknown metric has increased after a POST with wrong data"
  val req7 = "Negative Test: Try to authenticate with wrong credentials"
  val req8 = "Negative Test: Try to authenticate with no credentials"
  val req9 = "Metrics: Validate the error metric is equals to zero"
  val req10 = "Metrics: Validate the duplicate metric is equals to zero"
  val req11 = "Negative Test: Validate you can't login without authentication (cookie test)"

  val httpProtocolSimpleTcpListenerSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("SimpleTcpListenerSkMs")

    // "Metrics: Fetch the current success metrics before any testing is done"
    .exec(http(req1)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // "Metrics: Fetch the current unknown metrics before any testing is done"
    .exec(http(req2)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("unknownMetricsBefore"))
    )

    // "POST existing deviceIp that belongs to only one customer"
    .exec(http(req3)
      .post("micro/qa")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"ip\":\"206.253.230.49\",\"text\":\"This is an automated test from QA via simple-tcplistener-sk\"}"))
      .check(status.is(200))
    )

    //   val req4 = "Metrics: Validate the success metric has increased after a successful POST"
    .exec(http(req4)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // "POST with a non existent Device IP"
    .exec(http(req5)
      .post("micro/qa")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"ip\":\"" + unixTimestamp +"\",\"text\":\"This is an automated test from QA via simple-tcplistener-sk\"}"))
      .check(status.is(200))
    )

    // "Metrics: Validate the unknown metric has increased after a POST with wrong data"
    .exec(http(req6)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedUnknownMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedUnknownMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${unknownMetricsBefore}"))
    )

    // "Negative Test: Try to authenticate with wrong credentials"
    .exec(http(req7)
      .get("micro/metric/error")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Try to authenticate with no credentials"
    .exec(http(req8)
      .get("micro/metric/duplicate")
      .check(status.is(401))
    )

    // "Metrics: Validate the error metric is equals to zero"
    .exec(http(req9)
      .get("micro/metric/error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("0.0"))
    )

    // "Metrics: Validate the duplicate metric is equals to zero"
    .exec(http(req10)
      .get("micro/metric/duplicate")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("0.0"))
    )

    // "Negative Test: Validate you can't login without authentication (cookie test)"
    .exec(http(req11)
      .get("micro/metric/duplicate")
      .check(status.is(401))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSimpleTcpListenerSkMs).assertions(global.failedRequests.count.is(0))
}