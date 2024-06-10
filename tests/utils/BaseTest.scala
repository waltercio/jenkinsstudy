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
import io.gatling.core.session.Session

class BaseTest extends Simulation {

  //Getting the environment values
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val ocpServer = System.getenv("OCP_SERVER")

  val authToken = System.getenv("AUTHORIZATION_TOKEN_USER")
  val authPass = environment match {
    case "DEV"  => System.getenv("AUTHORIZATION_TOKEN_DEV")
    case "STG"  => System.getenv("AUTHORIZATION_TOKEN_STG")
    case "PRD"  => System.getenv("AUTHORIZATION_TOKEN_PRD")
    case "EU"  => System.getenv("AUTHORIZATION_TOKEN_EU")
    case "RUH"  => System.getenv("AUTHORIZATION_TOKEN_RUH")
    case _  => "Invalid environment"  // the default, catch-all
  }
  
  //token for Glass Cases Tests
  val GlassApiToken = environment match {
      case "DEV"  => System.getenv("GLASS_API_KEY_DEV")
      case "STG"  => System.getenv("GLASS_API_KEY_PRD")
      case "PRD"  => System.getenv("GLASS_API_KEY_PRD")
      case "EU"  => System.getenv("GLASS_API_KEY_PRD")
      case "RUH"  => System.getenv("GLASS_API_KEY_PRD")
      case _  => "Invalid environment"  // the default, catch-all
  }
  
  //Defining Vault credentials
  var vaultUtils: VaultUtil = new VaultUtil  
  
  val currentDirectory = new java.io.File(".").getCanonicalPath

  // Getting the configuration values
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/configuration_global.json").getLines().mkString)

  // Reading configurations from file
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val adUser = System.getenv("AD_USER")
  val adPass = System.getenv("AD_PASS")
  val w3User = System.getenv("W3_USER")
  val w3Pass = System.getenv("W3_PASS")
  val contactUser = System.getenv("CONTACT_USER")
  val contactPass = System.getenv("CONTACT_PASS")
  val qaDemoUser = System.getenv("QA_DEMO_USER")
  val qaDemoPass = System.getenv("QA_DEMO_PASS")
  val partnerLevelUser = System.getenv("PARTNERLEVELUSER")
  val partnerLevelPassword = System.getenv("PARTNERLEVELPASSWORD")
  val vaultURL = (configurations \\ "vaultURL").extract[String]
  val vaultRoleId = System.getenv("VAULT_ROLE_ID")
  val vaultSecretId = System.getenv("VAULT_SECRET_ID")
  val elasticSearchUser = System.getenv("ELASTIC_SEARCH_USER")
  val elasticSearchPass = System.getenv("ELASTIC_SEARCH_PASS")
  val elasticSearchUrl = System.getenv("ELASTIC_SEARCH_URL")
  val accessKey = System.getenv("ACCESS_KEY")
  val secretKey = System.getenv("SECRET_KEY")
  val auraDevmonUser: String = System.getenv("AURA_DEVMON_USER")
  val auraDevmonPass = System.getenv("AURA_DEVMON_PASSWORD")
  val auraSocUser = System.getenv("AURA_SOC_USER")
  val auraSocPass = System.getenv("AURA_SOC_PASSWORD")
  
  //setting specific httpProtocol for device-ms - jwt-provider scenarios needs no auth httpProtocol  
  val httpProtocol = http
    .baseUrl(baseUrl)
    .basicAuth(adUser, adPass)
    .headers(Map("Content-Type" -> "application/json", "server" -> ocpServer))
    
  val httpProtocolNoBasicAuth = http
    .baseUrl(baseUrl)
    .headers(Map("Content-Type" -> "application/json", "server" -> ocpServer))
}
