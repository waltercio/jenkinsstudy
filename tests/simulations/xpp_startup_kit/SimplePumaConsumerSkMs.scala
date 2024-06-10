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
 *  Developed by: Caio Gobbi/ Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8656
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8708
 */

class SimplePumaConsumerSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/simple_puma_consumer_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]


  // Scenarios to add
  // Validate the metrics value are changing (which they don't seem to be as of right now) - track in XPS-80493
  // /micro/qa is accepting any payload, without validating customer or device id - track in XPS-80498
  // Include step to force error in QRadar log source API
  // ToDos - to be implemented still

  val req1 = "GET - Saving the error metric count before any testing is done"
  val req2 = "GET - Saving the success metric count before any testing is done"
  val req3 = "GET - Saving the unknown metric count before any testing is done"
  val req4 = "GET - Sending a GET request to /micro/qa to generate default data on QRadar Console"
  val req5 = "POST - Posting some custom data to /micro/qa with wrong information" //ToDo
  val req6 = "POST - Posting some custom data to /micro/qa with correct information"
  val req7 = "GET - Getting the success metrics sent in request #5 and #6"
  val req8 = "POST - Sendind an incorrect Basic Auth authorization - Negative Scenario"
  val req9 = "GET - Getting the unknown metric sent in request #8" //ToDo
  val req10 = "POST - Sendind a No Auth authorization - Negative Scenario"
  val req11 = "GET - Getting the unknown metric sent in request #10" //ToDo

  val httpProtocolSimplePumaConsumerSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("SimplePumaConsumerSkMs")
    .exec(http(req1)
      .get("micro/metric/error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("errorMetricsBefore"))
    )

    .exec(http(req2)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    .exec(session => {
      val successMetricsBefore = session("successMetricsBefore").as[Double]
      val successMetricsAfter = successMetricsBefore + 3
      //println(successMetricsAfter)
      session.set("successMetricsAfter", successMetricsAfter)
    })

    .exec(http(req3)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("unknownMetricsBefore"))
    )

    .exec(http(req4)
      .get("micro/qa")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

    //FIXME - In the future output won't be 200 status anymore. 
    .exec(http(req5)
      .post("micro/qa")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\":\"xpp-sk-custId\",\"deviceId\":\"xpp-sk-deviceId\",\"rawData\":\"This is an automated using a wrong payload.\"}"))
      .check(status.is(200))
    )

    .exec(http(req6)
      .post("micro/qa")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\":\"CIDD706957\",\"deviceId\":\"PRD00002\",\"rawData\":\"This is an automated test using the correct payload.\"}"))
      .check(status.is(200))
    )
    
    .exec(http(req7)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.not("${successMetricsBefore}"))
      .check(regex("0.0").notExists)
      .check(bodyString.is("${successMetricsAfter}"))
    )

    .exec(http(req8)
      .get("micro/qa")
      .basicAuth("wrongUser", sKPass)
      .check(status.is(401))
    )
    //FIXME - metrics needs to be fixed
    .exec(http(req9)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )
    
    .exec(http(req10)
      .get("micro/qa")
      .check(jsonPath("$..timestamp").exists)
      .check(jsonPath("$..status").is("401"))
      .check(jsonPath("$..error").is("Unauthorized"))
      .check(jsonPath("$..message").exists) //to be fixed in XPS-78883
      .check(jsonPath("$..path").exists)
      .check(status.is(401))
    )
    //FIXME - metrics needs to be fixed
    .exec(http(req11)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSimplePumaConsumerSkMs).assertions(global.failedRequests.count.is(0))
}