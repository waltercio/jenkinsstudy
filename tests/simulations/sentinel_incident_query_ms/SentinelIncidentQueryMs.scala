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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9077
 *  Functional test link: N/A.
 */

class SentinelIncidentQueryMs extends BaseTest {

  val sentinelIncidentQueryResourceFile: JValue = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/sentinel_incident_query_ms/configuration.json").getLines().mkString)
  val sentinelIncidentQueryExpiredToken: JValue = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/sentinel_incident_query_ms/expired_token.json").getLines().mkString)

  val deviceId = (sentinelIncidentQueryResourceFile \\ "deviceId" \\ environment).extract[String]
  val expiredManagementApiToken = (sentinelIncidentQueryExpiredToken \\ "managementapitoken").extract[String]
  val expiredLogApiToken = (sentinelIncidentQueryExpiredToken \\ "logapitoken").extract[String]

  //val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  // Name of each request
  val req1 = "GET - Health check"
  val req2 = "POST - Access to Azure Management API Token"
  val req3 = "POST - Access to Azure Log API Token"
  val req4 = "GET - sentinel-incident-query with device ID"
  val req5 = "GET - sentinel-incident-query with properties/incidentNumber eq"
  val req6 = "GET - sentinel-incident-query with properties/status ne 'Closed'"
  val req7 = "GET - sentinel-incident-query with properties/status eq 'Closed' & top=5 & orderby=properties/incidentNumber ASC"
  val req8 = "GET - sentinel-incident-query with device ID & properties/severity eq 'High' & orderby=properties/incidentNumber DESC & requesttype=ALL"
  val req9 = "GET - Negative Scenario - w/o required header - subscriptionid"
  val req10 = "GET - Negative Scenario - w/o required header - resourcegroup"
  val req11 = "GET - Negative Scenario - w/o required header - workspacename"
  val req12 = "GET - Negative Scenario - Expired Token"
  val req13 = "GET - Negative Scenario - No Auth"
  val req14 = "GET - Negative Scenario - Wrong Auth"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  val js9 = "jsession9"
  val js10 = "jsession10"
  val js11 = "jsession11"
  val js12 = "jsession12"
  val js13 = "jsession13"
  val js14 = "jsession14"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("SentinelIncidentQueryMs")
  
    .exec(http(req1)
      .get("micro/sentinel-incident-query/incidents/check")
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..status").is("Incidents up and running."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .post("https://login.microsoftonline.com/27613f5c-e693-4845-b6e7-264f8b632a56/oauth2/token")
      .basicAuth(authToken, authPass)
      .header("uuidMars", "001-pos-mgmt-oauth2-token")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("grant_type", "client_credentials")
      .formParam("client_secret","6F[a5/=uSOUnqHIeJ4y:veysMBxMHwe3")
      .formParam("client_id", "f83c68a9-7163-40f6-8bfb-72cf971d1c8d")
      .formParam("resource", "https://management.azure.com")
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get("micro/sentinel-vendor-config/" + deviceId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.managementApiToken").saveAs("managementApiToken"))
      .check(jsonPath("$.logApiToken").saveAs("logApiToken"))
      .check(jsonPath("$..subscriptionId").saveAs("subscriptionId")) 
      .check(jsonPath("$..resourceGroup").saveAs("resourceGroup")) 
      .check(jsonPath("$..workspaceName").saveAs("workspaceName")) 
      .check(jsonPath("$..workspaceId").saveAs("workspaceId"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .basicAuth(authToken, authPass)
      .header("subscriptionId", "${subscriptionId}")
      .header("resourceGroup", "${resourceGroup}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.in(200 to 206))
      .check(jsonPath("$..incident..id").exists)
      .check(jsonPath("$..incident..name").exists)
      .check(jsonPath("$..incident..type").exists)
      .check(jsonPath("$..incident..properties..title").exists)
      .check(jsonPath("$..incident..properties..description").exists)
      .check(jsonPath("$..incident..properties..severity").exists)
      .check(jsonPath("$..incident..properties..status").exists)
      .check(jsonPath("$..incident..properties..owner").exists)
      .check(jsonPath("$..incident..properties..incidentNumber").exists)
      .check(jsonPath("$[-1:]..incident..properties..incidentNumber").saveAs("incidentId"))
      .check(jsonPath("$..incident..properties..additionalData").exists)
      .check(jsonPath("$..incident..properties..relatedAnalyticRuleIds").exists)
      .check(jsonPath("$..incident..properties..incidentUrl").exists)
      .check(jsonPath("$..incident..properties..suppressionEnabled").exists)
      .check(jsonPath("$..alertRules..id").exists)
      .check(jsonPath("$..alertRules..name").exists)
      .check(jsonPath("$..alertRules..type").exists)
      .check(jsonPath("$..alertRules..properties..description").exists)
      .check(jsonPath("$..alertRules..properties..severity").exists)
      .check(jsonPath("$..alertRules..properties..incidentNumber").exists)
      .check(jsonPath("$..alertRules..properties..incidentConfiguration..createIncident").is("true"))
      .check(jsonPath("$..alertRules..properties..incidentConfiguration..groupingConfiguration").exists)
      .check(jsonPath("$..alertRules..properties..query").exists)
      .check(jsonPath("$..alertRules..properties..suppressionEnabled").exists)
      .check(jsonPath("$..alertRules..properties..eventGroupingSettings..aggregationKind").exists)
      .check(jsonPath("$..alertRules..properties..displayName").exists)
      .check(jsonPath("$..alertRules..properties..enabled").exists)
      .check(jsonPath("$..alertRules..properties..tactics").exists)
      .check(jsonPath("$..alertRules..properties..lastModifiedUtc").exists)
      .check(jsonPath("$..alertRules..properties..createIncident").exists)
      .check(jsonPath("$..alertRules..properties..groupingConfiguration").exists)
      .check(jsonPath("$..alerts..value..id").exists)
      .check(jsonPath("$..alerts..value..name").exists)
      .check(jsonPath("$..alerts..value..type").exists)
      .check(jsonPath("$..alerts..value..kind").exists)
      .check(jsonPath("$..alerts..value..properties..description").exists)
      .check(jsonPath("$..alerts..value..properties..severity").exists)
      .check(jsonPath("$..alerts..value..properties..status").exists)
      .check(jsonPath("$..alerts..value..properties..incidentNumber").exists)
      .check(jsonPath("$..alerts..value..properties..additionalData").exists)
      .check(jsonPath("$..alerts..value..properties..suppressionEnabled").exists)
      .check(jsonPath("$..alerts..value..properties..enabled").exists)
      .check(jsonPath("$..alerts..value..properties..tactics").exists)
      .check(jsonPath("$..alerts..value..properties..friendlyName").exists)
      .check(jsonPath("$..alerts..value..properties..systemAlertId").exists)
      .check(jsonPath("$..alerts..value..properties..alertDisplayName").exists)
      .check(jsonPath("$..alerts..value..properties..confidenceLevel").exists)
      .check(jsonPath("$..alerts..value..properties..vendorName").exists)
      .check(jsonPath("$..alerts..value..properties..productName").exists)
      .check(jsonPath("$..alerts..value..properties..productComponentName").exists)
      .check(jsonPath("$..alerts..value..properties..alertType").exists)
      .check(jsonPath("$..alerts..value..properties..processingEndTime").exists)
      .check(jsonPath("$..alerts..value..properties..endTimeUtc").exists)
      .check(jsonPath("$..alerts..value..properties..timeGenerated").exists)
      .check(jsonPath("$..alerts..value..properties..providerAlertId").exists)
      .check(jsonPath("$..alerts..value..properties..resourceIdentifiers..type").exists)
      .check(jsonPath("$..alerts..value..properties..resourceIdentifiers..workspaceId").exists)
      .check(jsonPath("$..alerts..value..properties..resourceIdentifiers..subscriptionId").exists)
      .check(jsonPath("$..alerts..value..properties..resourceIdentifiers..resourceGroup").exists)
      .check(jsonPath("$..events..tables..name").exists)
      .check(jsonPath("$..events..tables..columns..name").exists)
      .check(jsonPath("$..events..tables..columns..type").exists)
      .check(jsonPath("$..events..tables..rows").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .queryParam("filter", "properties/incidentNumber eq ${incidentId}")
      .basicAuth(authToken, authPass)
      .header("subscriptionId", "${subscriptionId}")
      .header("resourceGroup", "${resourceGroup}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.in(200 to 206))
      .check(jsonPath("$..incident").exists)
      .check(jsonPath("$..incident..properties..incidentNumber").is("${incidentId}"))
      .check(jsonPath("$..alertRules").exists)     
      // .check(jsonPath("$..relatedEntities").exists)
      .check(jsonPath("$..alerts").exists)
      .check(jsonPath("$..events").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .queryParam("filter", "properties/status ne 'Closed'")
      .basicAuth(authToken, authPass)
      .header("subscriptionId", "${subscriptionId}")
      .header("resourceGroup", "${resourceGroup}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.in(200 to 206))
      .check(jsonPath("$..incident").exists)
      .check(jsonPath("$[*]..incident..status").not("Closed"))
      .check(jsonPath("$..alertRules").exists)     
      // .check(jsonPath("$..relatedEntities").exists)
      .check(jsonPath("$..alerts").exists)
      .check(jsonPath("$..events").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req7)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .queryParam("filter", "properties/status eq 'Closed'")
      .queryParam("orderby", "properties/incidentNumber ASC")
      .queryParam("top", "5")
      .queryParam("requesttype", "ALL")
      .basicAuth(authToken, authPass)
      .header("subscriptionId", "${subscriptionId}")
      .header("resourceGroup", "${resourceGroup}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.in(200 to 206))
      .check(jsonPath("$..incident..id").count.is(5))
      .check(jsonPath("$[*]..incident..incidentNumber").count.is(5))
      .check(jsonPath("$[1]..incident..incidentNumber").find.saveAs("incident01"))
      .check(jsonPath("$[2]..incident..incidentNumber").find.saveAs("incident02"))
      .check(jsonPath("$[3]..incident..incidentNumber").find.saveAs("incident03"))
      .check(jsonPath("$[4]..incident..incidentNumber").find.saveAs("incident04"))
      .check(jsonPath("$[*]..incident..status").is("Closed"))
      .check(jsonPath("$..alertRules..id").count.is(5))    
      // .check(jsonPath("$..relatedEntities").exists)
      .check(jsonPath("$..alerts..value..id").count.is(5)) 
      .check(jsonPath("$..events").exists)
      .check(jsonPath("$[0]..incident..incidentNumber").lt("${incident01}"))
      .check(jsonPath("$[1]..incident..incidentNumber").lt("${incident02}"))
      .check(jsonPath("$[2]..incident..incidentNumber").lt("${incident03}"))
      .check(jsonPath("$[3]..incident..incidentNumber").lt("${incident04}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req8)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .queryParam("filter", "properties/severity eq 'High'")
      .queryParam("orderby", "properties/incidentNumber DESC")
      .queryParam("top", "5")
      .queryParam("requesttype", "ALL")
      .basicAuth(authToken, authPass)
      .header("subscriptionId", "${subscriptionId}")
      .header("resourceGroup", "${resourceGroup}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.in(200 to 206))
      .check(jsonPath("$..incident").exists)
      .check(jsonPath("$[*]..incident..incidentNumber").count.is(5))
      .check(jsonPath("$[1]..incident..incidentNumber").find.saveAs("incident01"))
      .check(jsonPath("$[2]..incident..incidentNumber").find.saveAs("incident02"))
      .check(jsonPath("$[3]..incident..incidentNumber").find.saveAs("incident03"))
      .check(jsonPath("$[4]..incident..incidentNumber").find.saveAs("incident04"))
      .check(jsonPath("$[*]..incident..severity").is("High"))
      .check(jsonPath("$..alertRules").exists)
      // .check(jsonPath("$..relatedEntities").exists)
      .check(jsonPath("$..alerts").exists)
      .check(jsonPath("$..events").exists)
      .check(jsonPath("$[0]..incident..incidentNumber").gt("${incident01}"))
      .check(jsonPath("$[1]..incident..incidentNumber").gt("${incident02}"))
      .check(jsonPath("$[2]..incident..incidentNumber").gt("${incident03}"))
      .check(jsonPath("$[3]..incident..incidentNumber").gt("${incident04}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req9)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .basicAuth(authToken, authPass)
      .header("resourceGroup", "${resourceGroup}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.is(520))
      .check(jsonPath("$..errors..vendor..message").is("400 BAD_REQUEST \\\"{\\\"error\\\":{\\\"code\\\":\\\"InvalidSubscriptionId\\\",\\\"message\\\":\\\"The provided subscription identifier 'resourceGroups' is malformed or invalid.\\\"}}\\\""))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req10)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .basicAuth(authToken, authPass)
      .header("subscriptionId", "${subscriptionId}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.is(520))
      .check(regex("403 FORBIDDEN").exists)
      .check(regex("AuthorizationFailed").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req11)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .basicAuth(authToken, authPass)
      .header("subscriptionId", "${subscriptionId}")
      .header("resourceGroup", "${resourceGroup}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.is(520))
      .check(regex("403 FORBIDDEN").exists)
      .check(regex("AuthorizationFailed").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req12)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .basicAuth(authToken, authPass)
      .header("subscriptionid", "f4122767-4f74-4e66-96cc-fe9604c36ada")
      .header("resourcegroup", "sentinel_lab")
      .header("workspacename", "Sentinel-Lab-Analytics")
      .header("managementapitoken", expiredManagementApiToken)
      .header("logapitoken", expiredLogApiToken)
      .check(status.is(520))
      .check(regex("ExpiredAuthenticationToken").exists)
      .check(regex("The access token expiry UTC time").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req13)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .header("subscriptionId", "${subscriptionId}")
      .header("resourceGroup", "${resourceGroup}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req14)
      .get("micro/sentinel-incident-query/incidents/device/" + deviceId)
      .header("subscriptionId", "${subscriptionId}")
      .header("resourceGroup", "${resourceGroup}")
      .header("workspaceName", "${workspaceName}")
      .header("managementApiToken", "${managementApiToken}")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "${workspaceId}")
      .basicAuth(authToken, "wrongPass")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      jsessionMap += (req7 -> session(js7).as[String])
      jsessionMap += (req8 -> session(js8).as[String])
      jsessionMap += (req9 -> session(js9).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}