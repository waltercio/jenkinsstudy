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
 *  Developed by: cgobbi@br.ibm.com
    Updated by: rlopesangelelli@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9409
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9310
 */

class SimpleAmEngineSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  
  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new 
    Exception().getStackTrace.head.getFileName.split("\\.scala")(0) + ".json"
  val currentDirectory = new java.io.File(".").getCanonicalPath
  
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_configuration.json").getLines().mkString)
  val amProcessorConfiguration = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val baseUrlAmProcessor = (amProcessorConfiguration \\ "baseURL" \\ environment).extract[String]
  
  val req1 = "Test Log Against Single Rule - Match single srcIp"
  val req2 = "Test Log Against Single Rule - Must match 2 fields"
  val req3 = "Test Log Against Single Rule - Operator OR with 1 match"
  val req4 = "Test Log Against Single Rule - No match - Single field"
  val req5 = "Test Log Against Single Rule - Operator AND with 1 match"
  val req6 = "Create a new rule"
  val req7 = "Try to create a rule with duplicate ID"
  val req8 = "Get the metrics of read success"
  val req9 = "Fetch a rule by ID"
  val req10 = "Verify the read success metrics increased"
  val req11 = "Fetch all rules"
  val req12 = "Update the previously created rule"
  val req13 = "Fetch the updated rule by id and verify it was updated"
  val req14 = "Set the created rule to inactive" //TBD
  val req15 = "Try to fetch by ID a rule that is inactive" //TBD
  val req16 = "Set the created rule to active again" //TBD
  val req17 = "Fetch the rule by ID again and this time it should be found" //TBD
  val req18 = "Test that your log fires one rule"
  val req19 = "Test that your log does not fire any rule"
  val req20 = "Get the metrics of errors for processed rules"
  val req21 = "Put a log into the xpslog kafka topic"
  val req22 = "Send a log directly to triage app"
  val req23 = "Get the metrics of errors for processed rules again and verify it hasnt increased"
  val req24 = "Delete the created rule - QA endpoint only"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))

  val httpProtocolSimpleAmEngineSkMs = http
    .baseUrl(baseUrl)
    .basicAuth("admin", sKPass)
    .header("Content-Type", "application/json")

  val scn = scenario("SimpleAmEngineSkMs")
    
    // "Test Log Against Single Rule - Match single srcIp"
    .exec(session => {
      session.setAll(
        // "RULE_ID" -> "ruleId_01",
        "RULE_ID" -> 1,
        "CONDITION" -> "srcip:1.1.1.1",
        "WORKLOG" -> "Matching a log against a single rule. Condition - scrip matches a given scrIp value",
      )
    })
    .exec(http(req1)
      .post("micro/testSingleRule")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_log_against_rule_payload.json")).asJson
      .check(status.is(200))
      .check(substring("ruleId=${RULE_ID}").exists) // This id comes from the json payload sent as body
      .check(substring("statusOwner=RuleEngine").exists) // Since one rule matched the owner should be RuleEngine 
      .check(substring("ruleResult=true").exists) // Because it matched a rule
      .check(substring("status=close").exists) // This close comes from the action in the rule.
      .check(substring("${WORKLOG}").exists) // Because it matched a rule
    ).exec(flushSessionCookies)

    // "Test Log Against Single Rule - Must match 2 fields"
    .exec(session => { session.reset })
    .exec(session => {
      session.setAll(
        // "RULE_ID" -> "ruleId_02",
        "RULE_ID" -> 2,
        "CONDITION" -> "srcip:1.1.1.1 AND dstPort:443",
        "WORKLOG" -> "This rule matched a given srcIp and dstPort, thus, its priority has been raised to High",
      )
    })
    .exec(http(req2)
      .post("micro/testSingleRule")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_log_against_rule_payload.json")).asJson
      .check(status.is(200))
      .check(substring("ruleId=${RULE_ID}").exists)
      .check(substring("statusOwner=RuleEngine").exists)
      .check(substring("ruleResult=true").exists)
      .check(substring("status=close").exists)
      .check(substring("${WORKLOG}").exists)
    ).exec(flushSessionCookies)

    // "Test Log Against Single Rule - Operator OR with 1 match"
    .exec(session => { session.reset })
    .exec(session => {
      session.setAll(
        // "RULE_ID" -> "ruleId_03",
        "RULE_ID" -> 3,
        "CONDITION" -> "srcip:7.7.7.7 OR priority:Low",
        "WORKLOG" -> "This rule matched the OR condition for priority",
      )
    })
    .exec(http(req3)
      .post("micro/testSingleRule")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_log_against_rule_payload.json")).asJson
      .check(status.is(200))
      .check(substring("ruleId=${RULE_ID}").exists)
      .check(substring("statusOwner=RuleEngine").exists)
      .check(substring("ruleResult=true").exists)
      .check(substring("status=close").exists)
      .check(substring("${WORKLOG}").exists)
    ).exec(flushSessionCookies)

    // "Test Log Against Single Rule - No match - Single field"
    .exec(session => { session.reset })
    .exec(session => {
      session.setAll(
        // "RULE_ID" -> "ruleId_04",
        "RULE_ID" -> 4,
        "CONDITION" -> "srcip:1.1.1.19",
        "WORKLOG" -> "This rule does not match any condition",
      )
    })
    .exec(http(req4)
      .post("micro/testSingleRule")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_log_against_rule_payload.json")).asJson
      .check(status.is(200))
      .check(substring("ruleId=null").exists)
      .check(substring("statusOwner=RuleEngine").exists)
      .check(substring("ruleResult=false").exists)
      .check(substring("status=escalateTriage").exists)
      .check(substring("workLog=Passed ruleEngine - escalateTriage by default from simple-am-engine-sk app").exists)
    ).exec(flushSessionCookies)


    // "Test Log Against Single Rule - Operator AND with 1 match"
    .exec(session => { session.reset })
    .exec(session => {
      session.setAll(
        "RULE_ID" -> 5,
        "CONDITION" -> "srcip:1.1.1.19",
        "WORKLOG" -> "This rule does not match any condition",
      )
    })
    .exec(http(req5)
      .post("micro/testSingleRule")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_log_against_rule_payload.json")).asJson
      .check(status.is(200))
      .check(substring("ruleId=null").exists)
      .check(substring("statusOwner=RuleEngine").exists)
      .check(substring("ruleResult=false").exists)
      .check(substring("status=escalateTriage").exists)
      .check(substring("workLog=Passed ruleEngine - escalateTriage by default from simple-am-engine-sk app").exists)
    ).exec(flushSessionCookies)

    // "Create a new rule"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond
      val ruleName = unixTimestamp
      session.set("NEW_RULE_ID", ruleName)
    })
    .exec(http(req6)
      .post("micro/rules")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_new_alert_payload.json")).asJson
      .check(status.is(200))
      .check(jsonPath("$.ok").exists)
      .check(jsonPath("$.id").exists)
      .check(jsonPath("$.rev").exists)
    ).exec(flushSessionCookies)

    // "Try to create a rule with duplicate ID"
    .exec(http(req7)
      .post("micro/rules")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_new_alert_payload.json")).asJson
      .check(status.is(200))
      .check(jsonPath("$..ruleId").exists)
      .check(jsonPath("$..status").is("412"))
      .check(jsonPath("$..message").is("Rule Id : ${NEW_RULE_ID} is already being used. Please ignore creation or create a new rule with different rule id"))
    ).exec(flushSessionCookies)


    // "Get the metrics of read success"
    .exec(http(req8)
      .get("micro/metric/ruleReadSuccess")
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    ).exec(flushSessionCookies)

    // "Fetch a rule by ID"
    .exec(http(req9)
      .get("micro/rules/${NEW_RULE_ID}")
      .check(status.is(200))
      .check(bodyString.saveAs("RULE_CREATED"))
      // All information below comes from simple_am_engine_new_alert_payload.json payload
      .check(jsonPath("$.result.id").is("${NEW_RULE_ID}"))
      .check(jsonPath("$.result.author").is("qa-automation"))
      .check(jsonPath("$.result.logType").is("alert"))
      .check(jsonPath("$.result.status").is("active"))
      .check(jsonPath("$.result.condition").is("srcip:1.1.1.10"))
      .check(jsonPath("$.result.action.type").is("close"))
      .check(jsonPath("$.result.action.actionSetting.workLog").is("This rule matched a given srcIp and it's priority has been raised to Medium"))
      .check(jsonPath("$.result.action.actionSetting.priority").is("Medium"))
      .check(jsonPath("$.result.notification.type").is("email"))
      .check(jsonPath("$.result.notification.notificationSetting").exists)
      .check(jsonPath("$.result.duration").exists)
      /* 
        Association is not implemented as of today, I am hardcoding this as false
        and once this is implemented the test needs to be updated as well.
      */
      .check(jsonPath("$.result.association").is("false")) 
      .check(jsonPath("$.result.ruleType").exists)
      .check(jsonPath("$.result._id").exists)
      .check(jsonPath("$.result._id").saveAs("COUCH_DB_ID"))
      .check(jsonPath("$.result._rev").exists)
      .check(jsonPath("$.result._rev").saveAs("COUCH_DB_REV"))
    ).exec(flushSessionCookies)

    // "Verify the read success metrics increased"
    .exec(http(req10)
      .get("micro/metric/ruleReadSuccess")
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )
  
    // "Fetch all rules and validate your created rule is there"
    .exec(http(req11)
      .get("micro/rules")
      .check(status.is(200))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].id").is("${NEW_RULE_ID}"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].author").is("qa-automation"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].logType").is("alert"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].status").is("active"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].condition").is("srcip:1.1.1.10"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].action.type").is("close"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].action.actionSetting.workLog").is("This rule matched a given srcIp and it's priority has been raised to Medium"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].action.actionSetting.priority").is("Medium"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].notification.type").is("email"))
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].notification.notificationSetting").exists)
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].duration").exists)
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].association").is("false")) 
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})].ruleType").exists)
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})]._id").exists)
      .check(jsonPath("$..[?(@.id == ${NEW_RULE_ID})]._rev").exists)
    ).exec(flushSessionCookies)

    // "Update the previously created rule"
    .exec(session => {
      session.setAll(
        "STATUS" -> "active",
        "WORKLOG" -> "This rule has been updated",
      )
    })
    .exec(http(req12)
      .put("micro/rules")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_update_alert_payload.json")).asJson
      .check(status.is(200))
      .check(jsonPath("$.id").exists)
      .check(jsonPath("$.id").saveAs("COUCH_DB_ID"))
      .check(jsonPath("$.rev").exists)
      .check(jsonPath("$.rev").saveAs("COUCH_DB_REV"))
    ).exec(flushSessionCookies)


    // val req14 = "Fetch the updated rule by id and verify it was updated"
    .exec(http(req13)
      .get("micro/rules/${NEW_RULE_ID}")
      .check(status.is(200))
      .check(jsonPath("$.result.id").is("${NEW_RULE_ID}"))
      .check(jsonPath("$.result.author").is("qa-automation"))
      .check(jsonPath("$.result.logType").is("alert"))
      .check(jsonPath("$.result.status").is("active"))
      .check(jsonPath("$.result.condition").is("srcip:1.1.1.10"))
      .check(jsonPath("$.result.action.type").is("close"))
      .check(jsonPath("$.result.action.actionSetting.workLog").is("This rule has been updated"))
      .check(jsonPath("$.result.action.actionSetting.priority").is("Medium"))
      .check(jsonPath("$.result.notification.type").is("email"))
      .check(jsonPath("$.result.notification.notificationSetting").exists)
      .check(jsonPath("$.result.duration").exists)
      .check(jsonPath("$.result.association").is("false")) 
      .check(jsonPath("$.result.ruleType").exists)
      .check(jsonPath("$.result._id").exists)
      .check(jsonPath("$.result._id").saveAs("COUCH_DB_ID"))
      .check(jsonPath("$.result._rev").exists)
      .check(jsonPath("$.result._rev").saveAs("COUCH_DB_REV"))
    ).exec(flushSessionCookies)

    // "Set the created rule to inactive"
    .exec(session => {
      session.setAll(
        "STATUS" -> "inactive",
        "WORKLOG" -> "This rule has been set to inactive",
      )
    }) /*
    .exec(http(req14)
      .put("micro/rules")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_update_alert_payload.json")).asJson
      .check(status.is(200))
      .check(jsonPath("$.id").exists)
      .check(jsonPath("$.id").saveAs("COUCH_DB_ID"))
      .check(jsonPath("$.rev").exists)
      .check(jsonPath("$.rev").saveAs("COUCH_DB_REV"))
    ).exec(flushSessionCookies)

    // "Try to fetch by ID a rule that is inactive"
    .exec(http(req15)
      .get("micro/rules/${NEW_RULE_ID}")
      .check(status.is(200))
      .check(substring("There is no rule found").exists)
    ).exec(flushSessionCookies) */
    
    // "Set the created rule to active again"
    .exec(session => {
      session.setAll(
        "STATUS" -> "active",
        "WORKLOG" -> "This rule has been set to active again",
      )
    })
    .exec(http(req16)
      .put("micro/rules")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_update_alert_payload.json")).asJson
      .check(status.is(200))
      .check(jsonPath("$.id").exists)
      .check(jsonPath("$.id").saveAs("COUCH_DB_ID"))
      .check(jsonPath("$.rev").exists)
      .check(jsonPath("$.rev").saveAs("COUCH_DB_REV"))
    ).exec(flushSessionCookies)


    // "Fetch the rule by ID again and this time it should be found"
    .exec(http(req17)
      .get("micro/rules/${NEW_RULE_ID}")
      .check(status.is(200))
      .check(jsonPath("$.result.id").is("${NEW_RULE_ID}"))
      .check(jsonPath("$.result.author").is("qa-automation"))
      .check(jsonPath("$.result.logType").is("alert"))
      .check(jsonPath("$.result.status").is("active"))
      .check(jsonPath("$.result.condition").is("srcip:1.1.1.10"))
      .check(jsonPath("$.result.action.type").is("close"))
      .check(jsonPath("$.result.action.actionSetting.workLog").is("This rule has been set to active again"))
      .check(jsonPath("$.result.action.actionSetting.priority").is("Medium"))
      .check(jsonPath("$.result.notification.type").is("email"))
      .check(jsonPath("$.result.notification.notificationSetting").exists)
      .check(jsonPath("$.result.duration").exists)
      .check(jsonPath("$.result.association").is("false")) 
      .check(jsonPath("$.result.ruleType").exists)
      .check(jsonPath("$.result._id").exists)
      .check(jsonPath("$.result._id").saveAs("COUCH_DB_ID"))
      .check(jsonPath("$.result._rev").exists)
      .check(jsonPath("$.result._rev").saveAs("COUCH_DB_REV"))
    ).exec(flushSessionCookies)

    // "Test that your log fires one rule"
    .exec(http(req18)
      .post("micro/testAllRule")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_run_log_against_all_rules.json")).asJson
      .check(status.is(200))
      //The static values validated below can be seen at the simple_am_engine_run_log_against_all_rules.json file
      .check(substring("alertId=alert00001").exists)
      .check(substring("customerId=CIDD706957").exists)
      .check(substring("deviceId=DEV0001").exists)
      .check(substring("timestamp=1623266738").exists)
      .check(substring("platform=splunk").exists)
      .check(substring("rawData=this is a test from the automation").exists)
      .check(substring("logType=alert").exists)
      .check(substring("srcIp=1.1.1.10").exists)
      .check(substring("dstIp=2.2.2.2").exists)
      .check(substring("srcPort=234").exists)
      .check(substring("dstPort=493").exists)
      .check(substring("priority=").exists)
      .check(substring("eventName=testEventName").exists)
      .check(substring("action=log").exists)
      .check(substring("count=1").exists)
      .check(substring("logValues={testAttributeName=testvalue}").exists)
      .check(substring("ruleId=").exists)
      .check(substring("status=close").exists)
      .check(substring("statusTimestamp").exists)
      .check(substring("statusOwner=RuleEngine").exists)
      .check(substring("reason=srcip:1.1.1.10").exists)
      .check(substring("workLog=").exists)
      .check(substring("ruleResult=true").exists)
      .check(substring("associateAlertId=null").exists) // association not implemented for now
      .check(substring("prediction=null").exists) // prediction not implemented for now
    ).exec(flushSessionCookies)

    // "Test that your log does not fire any rule"
    .exec(http(req19)
      .post("micro/testAllRule")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_run_log_against_all_rules_negative.json")).asJson
      .check(status.is(200))
      //The static values validated below can be seen at the simple_am_engine_run_log_against_all_rules_negative.json file
      .check(substring("alertId=alert00002").exists)
      .check(substring("customerId=CIDD706957").exists)
      .check(substring("deviceId=DEV00019").exists)
      .check(substring("timestamp=1623266749").exists)
      .check(substring("platform=qradar").exists)
      .check(substring("rawData=this is an automated test to make sure the rule is not fired").exists)
      .check(substring("logType=alert").exists)
      .check(substring("srcIp=x.x.x.x").exists)
      .check(substring("dstIp=2.2.2.2").exists)
      .check(substring("srcPort=invalidPort").exists)
      .check(substring("dstPort=invalidPort").exists)
      .check(substring("priority=High").exists)
      .check(substring("eventName=testEventName").exists)
      .check(substring("action=invalidLog").exists)
      .check(substring("count=1").exists)
      .check(substring("logValues={testAttributeName=Negative test}").exists)
      .check(substring("ruleId=null").exists)
      .check(substring("status=escalateTriage").exists)
      .check(substring("statusTimestamp=1").exists)
      .check(substring("statusOwner=RuleEngine").exists)
      .check(substring("reason=Passed ruleEngine - escalateTriage by default from simple-am-engine-sk app").exists)
      .check(substring("workLog=Passed ruleEngine - escalateTriage by default from simple-am-engine-sk app").exists)
      .check(substring("ruleResult=false").exists)
      .check(substring("associateAlertId=null").exists)
      .check(substring("prediction=null").exists)
    ).exec(flushSessionCookies)

    // "Get the metrics of errors for processed rules"
    .exec(http(req20)
      .get("micro/metric/processRuleError")
      .check(status.is(200))
      .check(bodyString.saveAs("processRuleErrorMetrics"))
    ).exec(flushSessionCookies)
  
    // "Put a log into the xpslog kafka topic"
    .exec(http(req21)
      .post(baseUrlAmProcessor + "micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_xpslog_kafka_topic.json")).asJson
      .check(status.is(200))
    ).exec(flushSessionCookies)
  
    // "Send a log directly to triage app"
    .exec(http(req22)
      .post("micro/xpsalert")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/simple_am_engine_send_log_triage.json")).asJson
      .check(status.is(200))
    ).exec(flushSessionCookies)

    // "Get the metrics of errors for processed rules again and verify it hasnt increased"
    .exec(http(req23)
      .get("micro/metric/processRuleError")
      .check(status.is(200))
      .check(bodyString.is("${processRuleErrorMetrics}"))
    )

    // "Set the created rule to inactive again"
    .exec(session => {
      session.setAll(
        "STATUS" -> "inactive",
        "WORKLOG" -> "This rule has been set to inactive after all automated tests were done",
      )
    })
    .exec(http(req24)
      .delete("micro/rules/delete/${NEW_RULE_ID}")
      .check(status.is(200))
      .check(substring("rule deleted.").exists)
    ).exec(flushSessionCookies)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSimpleAmEngineSkMs).assertions(global.failedRequests.count.is(0))
}