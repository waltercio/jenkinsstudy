require 'uri'
require 'net/http'
require 'json'
require 'jsonpath'

#getting environment variables for vault
vaultRoleId=ARGV[0].to_s
vaultSecretId=ARGV[1].to_s
environment=ARGV[2].to_s
ocp_deployment=ARGV[3].to_s
#getting vault client token
vaultGetTokenURI=URI('https://dev-vault.sec.ibm.com:8200/v1/auth/approle/login')
responseForVaultToken=Net::HTTP.post(vaultGetTokenURI,{ "role_id" => vaultRoleId, "secret_id" => vaultSecretId }.to_json,"Content-Type" => "application/json")
jsonParse=JSON.parse(responseForVaultToken.body)
vaultClientToken=JsonPath.on(jsonParse, '$..client_token')

#getting vault credentials
vaultApiAutomationPort=8200
vaultApiAutomationUri=URI('https://dev-vault.sec.ibm.com/v1/secret/data/modules/qa_automation/api-automation')
requestVaultCredentials=Net::HTTP::Get.new(vaultApiAutomationUri)
requestVaultCredentials['X-Vault-Token']=vaultClientToken
responseForVaultCredentials=Net::HTTP.start(vaultApiAutomationUri.hostname,vaultApiAutomationPort,use_ssl: vaultApiAutomationUri.scheme == 'https'){ |http|
  http.request(requestVaultCredentials)
}

#extracting credentials from vault response
vaultCredentialsJsonParse=JSON.parse(responseForVaultCredentials.body)
AD_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..AD_PASS')
AD_USER=JsonPath.on(vaultCredentialsJsonParse, '$..AD_USER')
AUTHORIZATION_TOKEN_DEV=JsonPath.on(vaultCredentialsJsonParse, '$..AUTHORIZATION_TOKEN_DEV')
AUTHORIZATION_TOKEN_EU=JsonPath.on(vaultCredentialsJsonParse, '$..AUTHORIZATION_TOKEN_EU')
AUTHORIZATION_TOKEN_PRD=JsonPath.on(vaultCredentialsJsonParse, '$..AUTHORIZATION_TOKEN_PRD')
AUTHORIZATION_TOKEN_STG=JsonPath.on(vaultCredentialsJsonParse, '$..AUTHORIZATION_TOKEN_STG')
AUTHORIZATION_TOKEN_RUH=JsonPath.on(vaultCredentialsJsonParse, '$..AUTHORIZATION_TOKEN_RUH')
AUTHORIZATION_TOKEN_USER=JsonPath.on(vaultCredentialsJsonParse, '$..AUTHORIZATION_TOKEN_USER')
CBC_API_ID=JsonPath.on(vaultCredentialsJsonParse, '$..CBC_API_ID')
CBC_API_KEY=JsonPath.on(vaultCredentialsJsonParse, '$..CBC_API_KEY')
CBC_ORG_ID=JsonPath.on(vaultCredentialsJsonParse, '$..CBC_ORG_ID')
CBC_ORG_KEY=JsonPath.on(vaultCredentialsJsonParse, '$..CBC_ORG_KEY')

if environment != "RUH"
  CONTACT_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..CONTACT_PASS')
  CONTACT_USER=JsonPath.on(vaultCredentialsJsonParse, '$..CONTACT_USER')
else
  CONTACT_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..CONTACT_PASS_KSA')
  CONTACT_USER=JsonPath.on(vaultCredentialsJsonParse, '$..CONTACT_USER_KSA')
end

CR_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..CR_PASS')
CR_USER=JsonPath.on(vaultCredentialsJsonParse, '$..CR_USER')
CS_API_ID=JsonPath.on(vaultCredentialsJsonParse, '$..CS_API_ID')
CS_API_KEY=JsonPath.on(vaultCredentialsJsonParse, '$..CS_API_KEY')
GLASS_API_KEY=JsonPath.on(vaultCredentialsJsonParse, '$..GLASS_API_KEY')
MDATP_API_ID=JsonPath.on(vaultCredentialsJsonParse, '$..MDATP_API_ID')
MDATP_API_KEY=JsonPath.on(vaultCredentialsJsonParse, '$..MDATP_API_KEY')
MDATP_LOGIN=JsonPath.on(vaultCredentialsJsonParse, '$..MDATP_LOGIN')
MDATP_TENANT_ID=JsonPath.on(vaultCredentialsJsonParse, '$..MDATP_TENANT_ID')
PARTNERLEVELPASSWORD=JsonPath.on(vaultCredentialsJsonParse, '$..PARTNERLEVELPASSWORD')
PARTNERLEVELUSER=JsonPath.on(vaultCredentialsJsonParse, '$..PARTNERLEVELUSER')
QRADAR_AUTHENTICATION_TOKEN=JsonPath.on(vaultCredentialsJsonParse, '$..QRADAR_AUTHENTICATION_TOKEN')
SK_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..SK_PASS')
SNIP_KEY=JsonPath.on(vaultCredentialsJsonParse, '$..SNIP_KEY')
SPLUNK_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..SPLUNK_PASS')
W3_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..W3_PASS')
W3_USER=JsonPath.on(vaultCredentialsJsonParse, '$..W3_USER')
GLASS_API_KEY_DEV=JsonPath.on(vaultCredentialsJsonParse, '$..GLASS_API_KEY_DEV')
GLASS_API_KEY_STG=JsonPath.on(vaultCredentialsJsonParse, '$..GLASS_API_KEY_STG')
GLASS_API_KEY_PRD=JsonPath.on(vaultCredentialsJsonParse, '$..GLASS_API_KEY_PRD')
GLASS_API_KEY_EU=JsonPath.on(vaultCredentialsJsonParse, '$..GLASS_API_KEY_EU')
GLASS_API_KEY_KSA=JsonPath.on(vaultCredentialsJsonParse, '$..GLASS_API_KEY_KSA')

if environment != "RUH"
  QA_DEMO_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..QA_DEMO_PASS')
  QA_DEMO_USER=JsonPath.on(vaultCredentialsJsonParse, '$..QA_DEMO_USER')
else
  QA_DEMO_PASS=JsonPath.on(vaultCredentialsJsonParse, '$..QA_DEMO_PASS_KSA')
  QA_DEMO_USER=JsonPath.on(vaultCredentialsJsonParse, '$..QA_DEMO_USER_KSA')
end

accessKey=JsonPath.on(vaultCredentialsJsonParse, '$..accessKey')
secretKey=JsonPath.on(vaultCredentialsJsonParse, '$..secretKey')

#setting OCP_SERVER variables
OCP_SERVER="A"
if !(ocp_deployment.eql? "")
  OCP_SERVER=ocp_deployment 
end

#setting aura credentials based on environment:
if environment != "PRD" && environment != "EU" && environment != "RUH"
  AURA_DEVMON_USER=JsonPath.on(vaultCredentialsJsonParse, '$..AURA_DEVMON_USER')
  AURA_DEVMON_PASSWORD=JsonPath.on(vaultCredentialsJsonParse, '$..AURA_DEVMON_PASSWORD')
  AURA_SOC_USER=JsonPath.on(vaultCredentialsJsonParse, '$..AURA_SOC_USER')
  AURA_SOC_PASSWORD=JsonPath.on(vaultCredentialsJsonParse, '$..AURA_SOC_PASSWORD')
else
  AURA_DEVMON_USER=JsonPath.on(vaultCredentialsJsonParse, '$..AURA_DEVMON_USER_PRD')
  AURA_DEVMON_PASSWORD=JsonPath.on(vaultCredentialsJsonParse, '$..AURA_DEVMON_PASSWORD_PRD')
  AURA_SOC_USER=JsonPath.on(vaultCredentialsJsonParse, '$..AURA_SOC_USER_PRD')
  AURA_SOC_PASSWORD=JsonPath.on(vaultCredentialsJsonParse, '$..AURA_SOC_PASSWORD_PRD')
end


#creating variables file
variables_file=File.new("variables","w")
variables_file.puts "export AD_PASS=#{AD_PASS[0]}"
variables_file.puts "export AD_USER=#{AD_USER[0]}"
variables_file.puts "export AUTHORIZATION_TOKEN_DEV='#{AUTHORIZATION_TOKEN_DEV[0]}'"
variables_file.puts "export AUTHORIZATION_TOKEN_EU='#{AUTHORIZATION_TOKEN_EU[0]}'"
variables_file.puts "export AUTHORIZATION_TOKEN_PRD='#{AUTHORIZATION_TOKEN_PRD[0]}'"
variables_file.puts "export AUTHORIZATION_TOKEN_STG='#{AUTHORIZATION_TOKEN_STG[0]}'"
variables_file.puts "export AUTHORIZATION_TOKEN_RUH='#{AUTHORIZATION_TOKEN_RUH[0]}'"
variables_file.puts "export AUTHORIZATION_TOKEN_USER='#{AUTHORIZATION_TOKEN_USER[0]}'"
variables_file.puts "export CBC_API_ID='#{CBC_API_ID[0]}'"
variables_file.puts "export CBC_API_KEY='#{CBC_API_KEY[0]}'"
variables_file.puts "export CBC_ORG_ID='#{CBC_ORG_ID[0]}'"
variables_file.puts "export CBC_ORG_KEY='#{CBC_ORG_KEY[0]}'"
variables_file.puts "export CONTACT_PASS='#{CONTACT_PASS[0]}'"
variables_file.puts "export CONTACT_USER='#{CONTACT_USER[0]}'"
variables_file.puts "export CR_PASS='#{CR_PASS[0]}'"
variables_file.puts "export CR_USER='#{CR_USER[0]}'"
variables_file.puts "export CS_API_ID='#{CS_API_ID[0]}'"
variables_file.puts "export CS_API_KEY='#{CS_API_KEY[0]}'"
variables_file.puts "export GLASS_API_KEY='#{GLASS_API_KEY[0]}'"
variables_file.puts "export MDATP_API_ID='#{MDATP_API_ID[0]}'"
variables_file.puts "export MDATP_API_KEY='#{MDATP_API_KEY[0]}'"
variables_file.puts "export MDATP_LOGIN='#{MDATP_LOGIN[0]}'"
variables_file.puts "export MDATP_TENANT_ID='#{MDATP_TENANT_ID[0]}'"
variables_file.puts "export PARTNERLEVELPASSWORD='#{PARTNERLEVELPASSWORD[0]}'"
variables_file.puts "export AD_PASS='#{AD_PASS[0]}'"
variables_file.puts "export PARTNERLEVELUSER='#{PARTNERLEVELUSER[0]}'"
variables_file.puts "export QRADAR_AUTHENTICATION_TOKEN='#{QRADAR_AUTHENTICATION_TOKEN[0]}'"
variables_file.puts "export SNIP_KEY='#{SNIP_KEY[0]}'"
variables_file.puts "export SK_PASS='#{SK_PASS[0]}'"
variables_file.puts "export SPLUNK_PASS='#{SPLUNK_PASS[0]}'"
variables_file.puts "export W3_PASS='#{W3_PASS[0]}'"
variables_file.puts "export W3_USER='#{W3_USER[0]}'"
variables_file.puts "export GLASS_API_KEY_DEV='#{GLASS_API_KEY_DEV[0]}'"
variables_file.puts "export GLASS_API_KEY_STG='#{GLASS_API_KEY_STG[0]}'"
variables_file.puts "export GLASS_API_KEY_PRD='#{GLASS_API_KEY_PRD[0]}'"
variables_file.puts "export GLASS_API_KEY_EU='#{GLASS_API_KEY_EU[0]}'"
variables_file.puts "export GLASS_API_KEY_KSA='#{GLASS_API_KEY_KSA[0]}'"
variables_file.puts "export QA_DEMO_PASS='#{QA_DEMO_PASS[0]}'"
variables_file.puts "export QA_DEMO_USER='#{QA_DEMO_USER[0]}'"
variables_file.puts "export ACCESS_KEY='#{accessKey[0]}'"
variables_file.puts "export SECRET_KEY='#{secretKey[0]}'"
variables_file.puts "export OCP_SERVER='#{OCP_SERVER}'"
variables_file.puts "export AURA_DEVMON_USER='#{AURA_DEVMON_USER}'"
variables_file.puts "export AURA_DEVMON_PASSWORD='#{AURA_DEVMON_PASSWORD}'"
variables_file.puts "export AURA_SOC_USER='#{AURA_SOC_USER}'"
variables_file.puts "export AURA_SOC_PASSWORD='#{AURA_SOC_PASSWORD}'"