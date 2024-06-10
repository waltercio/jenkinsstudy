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
 * Developed by: niti.dewan@ibm.com
 * Automation task for this script: STRY0125254
 * Functional test link: TEMT0001435
 */

class FavoritesMs extends BaseTest {


  val auraCredentialsBaseUrl = (configurations \\ "auraCredentialsBaseUrl" \\ environment).extract[String]
  val auraSecretServerUrl = (configurations \\ "auraSecretServerUrl" \\ environment).extract[String]
  val bearerTokenUrl = auraSecretServerUrl + "oauth2/token"
  val auraConfigurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/aura_favorite_ms.json").getLines().mkString)
  val secretId = (auraConfigurations \\ "secretId" \\ environment).extract[String] 
  val auraDevMonUser: String = auraDevmonUser.stripPrefix("[").stripSuffix("]").replaceAll("\"", "")
  val auraDevmonPwd : String = auraDevmonPass.stripPrefix("[").stripSuffix("]").replaceAll("\"", "")
  val auraSocUserId : String = auraSocUser.stripPrefix("[").stripSuffix("]").replaceAll("\"", "")
  val auraSocPwd : String = auraSocPass.stripPrefix("[").stripSuffix("]").replaceAll("\"", "")

  val req01 = "POST - Generate Bearer Token as DevMon user"
  val req02 = "GET - Fetch secrets marked as Favorite as DevMon user"
  val req03 = "POST - Mark a secret as Favorite as DevMon user"
  val req04 = "POST - Unmark a secret as Favorite as DevMon user"
  val req05 = "POST - Generate Bearer Token as SOC user"
  val req06 = "GET - Fetch secrets marked as Favorite as SOC user"
  val req07 = "POST - Mark a secret as Favorite as SOC user"
  val req08 = "POST - Unmark a secret as Favorite as SOC user"


  val js01 = "jsession1"
  val js02 = "jsession2"
  val js03 = "jsession3"
  val js04 = "jsession4"
  val js05 = "jsession5"
  val js06 = "jsession6"
  val js07 = "jsession7"
  val js08 = "jsession8"

  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val httpProtocolFavoritesMs = http
    .baseUrl(auraCredentialsBaseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("FavoritesMs")

  // req01 to req04 is ran using DEVMON user credentials
  //"POST - Generate Bearer Token"

    

   .doIf((environment == "DEV") || (environment == "STG")){
  exec(http(req01)
      .post(bearerTokenUrl)
      .header("Content-Type","application/x-www-form-urlencoded")
      .formParam("grant_type", "password")
      .formParam("username",auraDevMonUser)
      .formParam("password",auraDevmonPwd)
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("secretserverBearerToken"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
         session.set(js01, "Unable to retrieve JSESSIONID for this request")
        //println("Value of ID" + session("secretserverBearerToken").as[String])
        session
      })
    }
  }

  .doIf((environment == "PRD") || (environment == "RUH")|| (environment == "EU")){
    val prefixedUserId = "admin\\" + auraDevMonUser
    println ("value of prefixedUserId " + prefixedUserId)
  exec(http(req01)
      .post(bearerTokenUrl)
      .header("Content-Type","application/x-www-form-urlencoded")
      .formParam("grant_type", "password")
      .formParam("username",prefixedUserId)
      .formParam("password",auraDevmonPwd)
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("secretserverBearerToken"))
     // .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        //println("Value of ID" + session("secretserverBearerToken").as[String])
        session
      })
    }
  }

    // "GET - Fetch secrets marked as Favorite"
    .exec(http(req02)
      .get("credentials/getFavorites")
      .header("Authorization", "Bearer ${secretserverBearerToken}")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..folderId").exists)
      .check(jsonPath("$..folderPath").exists)
      .check(jsonPath("$..secretName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // "POST - Mark a secret as Favorite"
    .exec(http(req03)
      .post("credentials/markFavorites?secretId="+ secretId + "&action=true")
      .header("Authorization", "Bearer ${secretserverBearerToken}")
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    
    // "POST - Unmark a secret as Favorite"
    .exec(http(req04)
      .post("credentials/markFavorites?secretId="+ secretId + "&action=false")
      .header("Authorization", "Bearer ${secretserverBearerToken}")
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }


// req05 to req08 is ran using SOC user credentials
  //"POST - Generate Bearer Token"
   .doIf((environment == "DEV") || (environment == "STG")){
  exec(http(req05)
      .post(bearerTokenUrl)
      .header("Content-Type","application/x-www-form-urlencoded")
      .formParam("grant_type", "password")
      .formParam("username",auraSocUserId)
      .formParam("password",auraSocPwd)
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("secretserverBearerToken"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
        //println("Value of ID" + session("secretserverBearerToken").as[String])
        session
      })
    }
  }

  .doIf((environment == "PRD") || (environment == "RUH")|| (environment == "EU")){
    val prefixedUserId = "admin\\" + auraSocUserId
  exec(http(req05)
      .post(bearerTokenUrl)
      .header("Content-Type","application/x-www-form-urlencoded")
      .formParam("grant_type", "password")
      .formParam("username",prefixedUserId)
      .formParam("password",auraSocPwd)
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("secretserverBearerToken"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
       // println("Value of ID" + session("secretserverBearerToken").as[String])
        session
      })
    }
  }

    // "GET - Fetch secrets marked as Favorite"
    .exec(http(req06)
      .get("credentials/getFavorites")
      .header("Authorization", "Bearer ${secretserverBearerToken}")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..folderId").exists)
      .check(jsonPath("$..folderPath").exists)
      .check(jsonPath("$..secretName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // "POST - Mark a secret as Favorite"
    .exec(http(req07)
      .post("credentials/markFavorites?secretId="+ secretId + "&action=true")
      .header("Authorization", "Bearer ${secretserverBearerToken}")
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec(session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    
    // "POST - Unmark a secret as Favorite"
    .exec(http(req08)
      .post("credentials/markFavorites?secretId="+ secretId + "&action=false")
      .header("Authorization", "Bearer ${secretserverBearerToken}")
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec(session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    
    //Exporting all jsession ids
     .exec(session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      jsessionMap += (req06 -> session(js06).as[String])
      jsessionMap += (req07 -> session(js07).as[String])
      jsessionMap += (req08 -> session(js08).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolFavoritesMs).assertions(global.failedRequests.count.is(0))
}