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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9600
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9597
 */

class CustomerServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/customer_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "GET - Verify 401 is returned when no credentials are provided"
  val req2 = "GET - Verify 401 is returned when incorrect user/pass is provided"
  val req3 = "GET - Collect the amount of success metrics before any testing is done - dbsuccess"
  val req4 = "POST - Create a new customer"
  val req5 = "GET - Verify the amount of success metrics increased after posting new data - dbsuccess"
  val req6 = "GET - Verify 401 is returned when no credentials are provided again (cookies check)"
  val req7 = "POST - Negative Scenario - Repeat request #4 with exactly same data - Should fail"
  val req8 = "GET - Get a specific customer"
  val req9 = "GET - Get all customers"
  val req10 = "PUT - Update a specific customer"
  val req11 = "GET - Check if the previous update worked"
  val req12 = "GET - Get all customers again after request #8"
  val req13 = "PUT - Negative Scenario - Update a customer that doesn't exist - Should fail"
  val req14 = "DELETE - Delete the customer previously created"
  val req15 = "GET - Get the deleted customer - Should bring an emptier list"
  val req16 = "DELETE - Delete a customer that does not exist - Should fail"

  val httpProtocolCustomerServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("CustomerServiceSkMs")
    .exec(http(req1)
      .get("micro/customers")
      .check(status.is(401))
      .check(jsonPath("$..error").is("Unauthorized"))
    )

    .exec(http(req2)
      .get("micro/customers")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    .exec(http(req3)
      .get("micro/customer/metric/dbsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("dbSuccessMetricBefore"))
    )
    
    .exec(http(req4)
      .post("micro/customer")
      .body(StringBody("{\"customerId\": \"123456\",\"customerName\": \"QA-Customer\",\"services\": [\"xdr\"],\"splunkToCaseCollectionFrequency\": 60, \"splunkToCaseLastCollectionTime\": 0,\"pollingRequired\": false}"))
      .basicAuth("admin", sKPass)
      .check(status.is(201))
      .check(jsonPath("$..success").is("Customer created successfully"))
    ).exec(flushSessionCookies).pause(5 seconds)

    .exec(http(req5)
      .get("micro/customer/metric/dbsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedDbSuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newDbSuccessMetric = session("updatedDbSuccessMetric");
        newDbSuccessMetric.as[Double]
      }).gt("${dbSuccessMetricBefore}"))
    )

    .exec(http(req6)
      .get("micro/customers")
      .check(status.is(401))
      .check(jsonPath("$..error").is("Unauthorized"))
    )

    .exec(http(req7)
      .post("micro/customer")
      .body(StringBody("{\"customerId\": \"123456\",\"customerName\": \"QA-Customer\",\"services\": [\"xdr\"],\"splunkToCaseCollectionFrequency\": 60, \"splunkToCaseLastCollectionTime\": 0,\"pollingRequired\": false}"))
      .basicAuth("admin", sKPass)
      .check(status.is(409))
      .check(jsonPath("$..error").is("customer already exists with id 123456"))
    )

    .exec(http(req8)
      .get("micro/customer/123456")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").is("123456"))
      .check(jsonPath("$..customerName").is("QA-Customer"))
      .check(jsonPath("$..services").is("[\"xdr\"]"))
      .check(jsonPath("$..pollingRequired").is("false"))
      .check(jsonPath("$.._id").exists)
      .check(jsonPath("$.._rev").exists)
    )

    .exec(http(req9)
      .get("micro/customers")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..pollingRequired").is("true"))
      .check(jsonPath("$.._id").exists)
      .check(jsonPath("$.._rev").exists)
    )

    .exec(http(req10)
      .put("micro/customer")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\": \"123456\",\"customerName\": \"QA-Customer\",\"services\": [\"xdr\"],\"splunkToCaseCollectionFrequency\": 60, \"splunkToCaseLastCollectionTime\": 0,\"pollingRequired\": true}"))
      .check(status.is(204))
    )

    .exec(http(req11)
      .get("micro/customer/123456")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").is("123456"))
      .check(jsonPath("$..customerName").is("QA-Customer"))
      .check(jsonPath("$..services").is("[\"xdr\"]"))
      .check(jsonPath("$..pollingRequired").is("true"))
      .check(jsonPath("$.._id").exists)
      .check(jsonPath("$.._rev").exists)
    )

    .exec(http(req12)
      .get("micro/customers")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.customerId == \"123456\")].customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..services").exists)
      .check(jsonPath("$..pollingRequired").is("true"))
      .check(jsonPath("$.._id").exists)
      .check(jsonPath("$.._rev").exists)
    )

    .exec(http(req13)
      .put("micro/customer")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\": \"654321\",\"customerName\": \"QA-Customer\",\"services\": [\"xdr\"],\"splunkToCaseCollectionFrequency\": 60, \"splunkToCaseLastCollectionTime\": 0,\"pollingRequired\": true}"))
      .check(status.is(404))
      .check(jsonPath("$.error").is("customer not found for customer id 654321"))
    )

    .exec(http(req14)
      .delete("micro/customer/123456")
      .basicAuth("admin", sKPass)
      .check(status.is(204))
    )

    .exec(http(req15)
      .get("micro/customer/123456")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").notExists)
      .check(jsonPath("$..customerName").notExists)
      .check(jsonPath("$..services").notExists)
      .check(jsonPath("$..pollingRequired").is("true"))
      .check(jsonPath("$.._id").notExists)
      .check(jsonPath("$.._rev").notExists)
    )

    .exec(http(req16)
      .delete("micro/customer/654321")
      .basicAuth("admin", sKPass)
      .check(status.is(404))
      .check(jsonPath("$.error").is("customer not found"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolCustomerServiceSkMs).assertions(global.failedRequests.count.is(0))
}