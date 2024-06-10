import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import java.io._
import org.json4s.jackson.Serialization._


class JWTProvider extends BaseTest {

  /**
   *  Updated by: Eugeniu Vatamaniuc
   *  Functional test link: https://jira.sec.ibm.com/browse/QX-4994
   */

  //_Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //local variables
  val expInUnix = HelperMethods.currentTimeInUnix(86400)
  val expInUnixNegative = HelperMethods.currentTimeInUnix(86420)
  val expInUnixNegativeDev = HelperMethods.currentTimeInUnix(604850)

  //  Name of each request
  val req01 = "POST request to generates a jwt_provider token"
  val req02 = "POST request to validate data from jwt_provider token"
  val req03 = "Negative - POST request with expired date"
  val req04 = "Negative - POST request with expired date for DEV"

  // Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"

  val scn = scenario("JWTPermissionMS")

    //POST request to generates a jwt_provider token
    .exec(http(req01)
      .post("micro/jwt_provider/issue")
      .basicAuth(adUser, adPass)
      .body(StringBody("{ \"iss\": \"Issuer\", \"sub\": \"Subject\", \"aud\": \"Audience\", \"exp\":\"" + expInUnix + "\" , \"nbf\": \"1420099200\", \"iat\": \"1420099200\", \"jti\": \"e2568b44-bab5-420b-bdcf-9c4dfc8f3af4\", \"custom-claim-1\": \"value1\", \"custom-claim-2\": \"value2\"}"))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      .check(bodyString.saveAs("responseBody"))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //POST request to validate data from jwt_provider token
    .exec(http(req02)
      .post("micro/jwt_provider/verify")
      .basicAuth(adUser, adPass)
      .body(StringBody("${responseBody}"))
      .check(status.is(200))
      .check(jsonPath("$..sub").is("Subject"))
      .check(jsonPath("$..aud").is("Audience"))
      .check(jsonPath("$..nbf").is("1420099200"))
      .check(jsonPath("$..custom-claim-2").is("value2"))
      .check(jsonPath("$..custom-claim-1").is("value1"))
      .check(jsonPath("$..iss").is("Issuer"))
      .check(jsonPath("$..exp").is(expInUnix))
      .check(jsonPath("$..iat").is("1420099200"))
      .check(jsonPath("$..jti").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - POST request with expired date
    .doIf(environment != "DEV") {
      exec(http(req03)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .body(StringBody("{ \"iss\": \"Issuer\", \"sub\": \"Subject\", \"aud\": \"Audience\", \"exp\":\"" + expInUnixNegative + "\" , \"nbf\": \"1420099200\", \"iat\": \"1420099200\", \"jti\": \"e2568b44-bab5-420b-bdcf-9c4dfc8f3af4\", \"custom-claim-1\": \"value1\", \"custom-claim-2\": \"value2\"}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("Invalid expiry time for token"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js03)) {
          exec(session => {
            session.set(js03, "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    //Negative - POST request with expired date for Dev
    .doIf(environment == "DEV") {
      exec(http(req04)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .body(StringBody("{ \"iss\": \"Issuer\", \"sub\": \"Subject\", \"aud\": \"Audience\", \"exp\":\"" + expInUnixNegativeDev + "\" , \"nbf\": \"1420099200\", \"iat\": \"1420099200\", \"jti\": \"e2568b44-bab5-420b-bdcf-9c4dfc8f3af4\", \"custom-claim-1\": \"value1\", \"custom-claim-2\": \"value2\"}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("Invalid expiry time for token"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js04)) {
          exec(session => {
            session.set(js04, "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    //Exporting all jsession ids
    .exec(session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
