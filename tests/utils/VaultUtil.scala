import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.io.Source
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

class VaultUtil extends Simulation {
  
  def getVaultClientTokenFromVault(vaultURL: String, vaultRoleId: String, vaultSecretId: String) = {
    http("Get Vault Client Token")
      .post(vaultURL + "v1/auth/approle/login")
      .body(StringBody("{\"role_id\": \"" + vaultRoleId + "\", \"secret_id\": \"" + vaultSecretId + "\"}"))
  }
  
  def getSK_PASSFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getCBC_ORG_IDFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getCBC_ORG_KEYFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getCBC_API_IDFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getCBC_API_KEYFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getCR_USERFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getCR_PASSFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getCS_API_IDFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getCS_API_KEYFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getMDATP_API_IDFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getMDATP_API_KEYFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getMDATP_TENANT_IDFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getMDATP_LOGINFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getGLASS_API_KEYFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getQRADAR_AUTHENTICATION_TOKENFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getPARTNERLEVELUSERFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getPARTNERLEVELPASSWORDFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getSNIP_KEYFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
  def getSPLUNK_PASSFromVault(vaultURL: String, clientToken: String) = {
    http("Get PlatoPassword")
      .get(vaultURL + "v1/secret/data/modules/qa_automation/api-automation")
      .header("X-Vault-Token", clientToken)     
  }
  
}