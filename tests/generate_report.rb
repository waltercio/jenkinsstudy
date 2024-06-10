require 'json'

# Getting entrypoint values.
suite_folder_name = ARGV[0]
jsession_suite_folder = ARGV[1]
env = ARGV[2]
jsession = ARGV[3]
unless jsession.nil? 
  jsession = jsession.downcase
end

suite_folder = "#{__dir__}/_results/#{suite_folder_name}"

# Getting all child folders from the gatling suite results
Dir.chdir(suite_folder)
log_folders = Dir.glob('*/')

# Variables used for Reports generation
requests_count = 0
number_of_simulations = 0
passed_requests_count = 0
failed_requests_count = 0

#Hashes for each type of report
ms_result = Hash.new {|h,k| h[k]=[]}
ms_failed_requests = Hash.new {|h,k| h[k]=[]}

# Looping through all the log folders
log_folders.each do |f|
  number_of_simulations +=1
  ms_name = ""
  jsessionJson = Hash.new
  File.open("#{suite_folder}/#{f}simulation.log").each do |line|
    simulation = Array.new
    request = Array.new

    # Collecting the name of the simulation and the json cointaining all jsessions for that simulation.
    if line.include? "RUN"
      line.split("\t").each do |field|
        field.chomp!
        unless field.to_s.strip.empty?
          simulation.push field
        end
      end #end line loop
      ms_name = simulation[1]
      unless jsession == "no"
        jsessionJson = JSON.load(File.read("#{jsession_suite_folder}/#{ms_name}.json"))
      end 
    end #end if 'RUN'

    # Collecting the name and status of each request and attaching it to the simulation name
    if line.include? "REQUEST"
      line.split("\t").each do |field|
        field.chomp!
        unless field.to_s.strip.empty?
          request.push field
        end
      end #end field loop

      elapsed_time = request[4].to_i - request[3].to_i # Total time of each request
      # Collecting info based on request status
      if request[5] == "OK"
        passed_requests_count += 1
        if jsession == "no"
          ms_result[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "status" => request[5]}
        else
		  ms_result[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "status" => request[5], "jsessionId" => jsessionJson["#{request[2]}"]}
        end
      else
        failed_requests_count +=1
        unless jsession == "no"
          # Getting kibana info - Each env will have its own url and certain ids for each panel displayed.
          kibana_version = "6.1.2"
          case env
          when "PRD"
            kibana_url = "https://atl-prd-kibana-01a.sec.ibm.com/app/kibana#/dashboard?_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-1y,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:'722a5690-dfb6-11e9-9bea-db0d5a3a456f',key:message,negate:!f,params:(query:'" + jsessionJson["#{request[2]}"] + "',type:phrase),type:phrase,value:'" + jsessionJson["#{request[2]}"] + "'),query:(match:(message:(query:'"+ jsessionJson["#{request[2]}"] + "',type:phrase))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((gridData:(h:2,i:'1',w:12,x:0,y:0),id:'9e4cbd00-2c82-11e8-aca3-2f8d009e6945',panelIndex:'1',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'2',w:3,x:0,y:2),id:e3c06e90-2c82-11e8-aca3-2f8d009e6945,panelIndex:'2',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'3',w:3,x:0,y:6),id:'0cf1c7f0-2c83-11e8-aca3-2f8d009e6945',panelIndex:'3',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'4',w:3,x:0,y:8),id:'30cd29d0-2c83-11e8-aca3-2f8d009e6945',panelIndex:'4',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'5',w:3,x:0,y:10),id:'53c4e270-2c83-11e8-aca3-2f8d009e6945',panelIndex:'5',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'6',w:3,x:0,y:4),id:c23feca0-2c82-11e8-aca3-2f8d009e6945,panelIndex:'6',type:visualization,version:'" + kibana_version + "'),(gridData:(h:12,i:'7',w:9,x:3,y:2),id:'5f0fc600-2c82-11e8-aca3-2f8d009e6945',panelIndex:'7',type:search,version:'" + kibana_version + "'),(gridData:(h:2,i:'8',w:3,x:0,y:12),id:'678e4390-5f4c-11e8-9b18-a3b8d734f6e9',panelIndex:'8',type:visualization,version:'" + kibana_version + "')),query:(language:lucene,query:''),timeRestore:!f,title:'java-apps%20dashboard',uiState:(),viewMode:view)"
          when "STG"
            kibana_url = "https://atl-stg-kibana-01a.sec.ibm.com/app/kibana#/dashboard?_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-1y,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:a2d24a60-de3e-11e9-98d6-91298e3d1e1b,key:message,negate:!f,params:(query:'" + jsessionJson["#{request[2]}"] + "',type:phrase),type:phrase,value:'" + jsessionJson["#{request[2]}"] + "'),query:(match:(message:(query:'" + jsessionJson["#{request[2]}"] + "',type:phrase))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((gridData:(h:2,i:'2',w:12,x:0,y:0),id:bca96ee0-2c70-11e8-bba9-f17aa089c49e,panelIndex:'2',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'3',w:3,x:0,y:2),id:f87cae00-2c70-11e8-bba9-f17aa089c49e,panelIndex:'3',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'4',w:3,x:0,y:6),id:'35b06780-2c71-11e8-bba9-f17aa089c49e',panelIndex:'4',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'5',w:3,x:0,y:8),id:'5ebfb9f0-2c71-11e8-bba9-f17aa089c49e',panelIndex:'5',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'6',w:3,x:0,y:10),id:afef43e0-2c71-11e8-bba9-f17aa089c49e,panelIndex:'6',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'7',w:3,x:0,y:4),id:d9f8d4d0-2c71-11e8-bba9-f17aa089c49e,panelIndex:'7',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'8',w:3,x:0,y:12),id:'14f96bb0-3728-11e8-bba9-f17aa089c49e',panelIndex:'8',type:visualization,version:'" + kibana_version + "'),(gridData:(h:12,i:'9',w:9,x:3,y:2),id:'662cc710-2c70-11e8-bba9-f17aa089c49e',panelIndex:'9',type:search,version:'" + kibana_version + "')),query:(language:lucene,query:''),timeRestore:!f,title:'java-apps%20dashboard',uiState:(),viewMode:view)"
          when "DEV"
            kibana_url = "https://dal09-dev-kibana-01a.sec.ibm.com/app/kibana#/dashboard?_g=()&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:cf9a7960-de2e-11e9-91b1-452d1400603d,key:message,negate:!f,params:(query:'" + jsessionJson["#{request[2]}"] + "',type:phrase),type:phrase,value:'" + jsessionJson["#{request[2]}"] + "'),query:(match:(message:(query:'" + jsessionJson["#{request[2]}"] + "',type:phrase))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((gridData:(h:12,i:'1',w:9,x:3,y:2),id:'5c5db8a0-22ae-11e8-b28c-63ccf318507f',panelIndex:'1',type:search,version:'" + kibana_version + "'),(gridData:(h:2,i:'2',w:12,x:0,y:0),id:'9bd15a90-22af-11e8-b28c-63ccf318507f',panelIndex:'2',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'3',w:3,x:0,y:2),id:'287b7c50-22b0-11e8-b28c-63ccf318507f',panelIndex:'3',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'4',w:3,x:0,y:6),id:'7d275bc0-22b0-11e8-b28c-63ccf318507f',panelIndex:'4',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'5',w:3,x:0,y:8),id:ae085410-22b0-11e8-b28c-63ccf318507f,panelIndex:'5',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'6',w:3,x:0,y:10),id:fa26dec0-22b0-11e8-b28c-63ccf318507f,panelIndex:'6',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'7',w:3,x:0,y:4),id:'25dbfcd0-22b1-11e8-b28c-63ccf318507f',panelIndex:'7',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'9',w:3,x:0,y:12),id:e879a3e0-3464-11e8-b28c-63ccf318507f,panelIndex:'9',type:visualization,version:'" + kibana_version + "')),query:(language:lucene,query:''),timeRestore:!f,title:'java-apps%20dashboard',uiState:(P-6:(spy:(mode:(fill:!f,name:!n)),vis:(legendOpen:!t))),viewMode:view)"
          when "EU"
            kibana_url = "https://fra02-prd-kibana-01a.sec.ibm.com/app/kibana#/dashboard?_g=()&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:e43f9920-e484-11e9-8970-57a8c7de33f3,key:message,negate:!f,params:(query:'" + jsessionJson["#{request[2]}"] + "',type:phrase),type:phrase,value:'" + jsessionJson["#{request[2]}"] + "'),query:(match:(message:(query:'" + jsessionJson["#{request[2]}"] + "',type:phrase))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((gridData:(h:2,i:'1',w:12,x:0,y:0),id:'9e4cbd00-2c82-11e8-aca3-2f8d009e6945',panelIndex:'1',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'2',w:3,x:0,y:2),id:e3c06e90-2c82-11e8-aca3-2f8d009e6945,panelIndex:'2',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'3',w:3,x:0,y:6),id:'0cf1c7f0-2c83-11e8-aca3-2f8d009e6945',panelIndex:'3',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'4',w:3,x:0,y:8),id:'30cd29d0-2c83-11e8-aca3-2f8d009e6945',panelIndex:'4',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'5',w:3,x:0,y:10),id:'53c4e270-2c83-11e8-aca3-2f8d009e6945',panelIndex:'5',type:visualization,version:'" + kibana_version + "'),(gridData:(h:2,i:'6',w:3,x:0,y:4),id:c23feca0-2c82-11e8-aca3-2f8d009e6945,panelIndex:'6',type:visualization,version:'" + kibana_version + "'),(gridData:(h:12,i:'7',w:9,x:3,y:2),id:'5f0fc600-2c82-11e8-aca3-2f8d009e6945',panelIndex:'7',type:search,version:'" + kibana_version + "'),(gridData:(h:2,i:'8',w:3,x:0,y:12),id:'678e4390-5f4c-11e8-9b18-a3b8d734f6e9',panelIndex:'8',type:visualization,version:'" + kibana_version + "')),query:(language:lucene,query:''),timeRestore:!f,title:'java-apps%20dashboard',uiState:(),viewMode:view)"
          end
          kibana_shorten_url = JSON.parse(`curl -s -X POST http://ibm.biz/api/shorten -H 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'api_key=#{ENV["SNIP_KEY"]}' --data-urlencode "url=#{kibana_url}"`)
        end

        # In case of a 504 the kibana url won't be attached as it is not available
        if request[6].include? "found 504"
          if jsession == "no"
            ms_result[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "status" => request[5], "failure_message" => request[6]}
            ms_failed_requests[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "failure_message" => request[6]}
          else
            ms_result[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "status" => request[5], "failure_message" => request[6], "jsessionId" => jsessionJson["#{request[2]}"]}
            ms_failed_requests[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "failure_message" => request[6], "jsessionId" => jsessionJson["#{request[2]}"]}
          end

        else
          if jsession == "no"
            ms_result[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "status" => request[5], "failure_message" => request[6]}
            ms_failed_requests[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "failure_message" => request[6]}
          else
            ms_result[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "status" => request[5], "failure_message" => request[6], "jsessionId" => jsessionJson["#{request[2]}"], "kibanaUrl" => kibana_shorten_url["url"]}
            ms_failed_requests[ms_name] << {"request_name" => request[2], "start_time" => request[3], "end_time" => request[4], "total_time_in_miliseconds" => elapsed_time, "failure_message" => request[6], "jsessionId" => jsessionJson["#{request[2]}"], "kibanaUrl" => kibana_shorten_url["url"]}
          end
        end

      end # End if REQUEST is OK
      requests_count +=1
    end # end if REQUEST

  end # end line loop

end # end folders loop

#Exporting all tests results json
File.open("#{suite_folder}/results.json","w") do |f|
  f.write(JSON.pretty_generate(ms_result))
end
puts "Json report for all tests generated at: tests/_results/#{suite_folder_name}/results.json"

# Exporting failed requests json
status=0
if failed_requests_count > 0
  File.open("#{suite_folder}/failed.json","w") do |f|
    f.write(JSON.pretty_generate(ms_failed_requests))
  end
  status=1
  puts "Json report for failed tests generated at: tests/_results/#{suite_folder_name}/failed.json"
end

# Printing a summary of the execution to the shell
results = []
results << ""
results << "===================================================================="
results << "> Total of simulations executed: #{number_of_simulations}"
results << "> Total of requests executed: #{requests_count}"
results << "> Requests successful: #{passed_requests_count}"
results << "> Requests failed: #{failed_requests_count}"
results << "===================================================================="
results.join("\n")

results.each do |line|
  puts line
end

puts ""
puts "List of simulations executed:"
print ms_result.keys.sort
puts ""
exit status
