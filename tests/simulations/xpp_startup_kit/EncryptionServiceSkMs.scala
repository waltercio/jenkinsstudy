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
 *  Developed by: Caio Gobbi
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8745
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8733
*/

class EncryptionServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/encryption_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "Negative Test: Try to encrypt a string without any credentials"
  val req2 = "Negative Test: Try to encrypt a string with wrong credentials"
  val req3 = "Encrypt a string and save the encrypted value"
  val req4 = "Negative Test: Try to decrypt the encrypted value without any credentials (cookie check)"
  val req5 = "Negative Test: Try to decrypt the encrypted value with wrong credentials (cookie check)"
  val req6 = "Decrypt the previously encrypted string"

  val unixTimestamp = Instant.now.getEpochSecond
  val decryptedString = "Automation_string_to_encrypt_" + unixTimestamp

  val httpProtocolEncryptionServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("EncryptionServiceSkMs")
    // "Negative Test: Try to encrypt a string without any credentials"
    .exec(http(req1)
      .post("api/encrypt")
      .check(status.is(401))
      .check(jsonPath("$.message").is("Missing Authorization Header"))
    )

    // "Negative Test: Try to encrypt a string with wrong credentials"
    .exec(http(req2)
      .post("api/encrypt")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
      .check(jsonPath("$.message").is("Invalid Authentication Credentials"))
    )

    // "Encrypt a string and save the encrypted value"
    .exec(http(req3)
      .post("api/encrypt")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"content\": \"" + decryptedString + "\"}"))
      .check(status.is(200))
      .check(jsonPath("$.content").saveAs("ENCRYPTED_VALUE"))
    )
    
    // "Negative Test: Try to decrypt the encrypted value without any credentials (cookie check)"
    .exec(http(req4)
      .post("api/decrypt")
      .check(status.is(401))
      .check(jsonPath("$.message").is("Missing Authorization Header"))
    )
    
    // "Negative Test: Try to decrypt the encrypted value with wrong credentials (cookie check)"
    .exec(http(req5)
      .post("api/decrypt")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
      .check(jsonPath("$.message").is("Invalid Authentication Credentials"))
    )
    
    // "Decrypt the previously encrypted string"
    .exec(http(req6)
      .post("api/decrypt")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"content\": \"${ENCRYPTED_VALUE}\"}"))
      .check(status.is(200))
      .check(jsonPath("$.content").is(decryptedString))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolEncryptionServiceSkMs).assertions(global.failedRequests.count.is(0))
}