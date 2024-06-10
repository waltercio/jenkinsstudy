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

class SsoCisMs extends BaseTest {
  
    val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
    val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

    // Name of each request
    val req01 = "Valid scenario with vendor and customerId values provided"

    // Name of each jsession
    val js01 = "jsessionid01"

    val scn = scenario("Sso Cis ms")
      .exec(http(req01)
        .get("micro/sso_cis_ms/?vendor=Resilient")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Exporting all jsession ids
      .exec( session => {
        jsessionMap += (req01 -> session(js01).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

      setUp(
        scn.inject(atOnceUsers(1))
      ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
