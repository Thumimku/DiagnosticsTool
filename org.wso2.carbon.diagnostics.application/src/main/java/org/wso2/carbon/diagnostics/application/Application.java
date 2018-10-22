package org.wso2.carbon.diagnostics.application;
/*
 * Copyright (c) 2005-2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.diagnostics.actionexecutor.diagnosticCommand.ServerProcess;
import org.wso2.carbon.diagnostics.logtailor.Tailer;
import org.wso2.carbon.diagnostics.regextree.RegexNode;
import org.wso2.carbon.diagnostics.regextree.RegexTree;

import java.io.FileReader;
import java.io.IOException;

/**
 * Typical Java Application class.
 */
public class Application {

    public static void main(String[] args) {

        System.out.print("................loading  OnBoard Diagnostics Tool .............\n\n");
        System.out.print(".............Loading Log File path Configuration data...........\n\n");

        JSONParser parser = new JSONParser();
        RegexTree regexTree = new RegexTree();

        try {

            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(System.getProperty("user.dir")+"/org.wso2.carbon.diagnostics.application/src/main/"+"resources/DiagnosticConfig.json"));
            RegexNode root = regexTree.expandTree(jsonObject);

            regexTree.setRoot(root);

            JSONArray actionExecutorConfig = (JSONArray) jsonObject.get("ActionExecutorConfiguration");
            root.setActionExecutorConfiguration((JSONArray) jsonObject.get("ActionExecutorConfiguration"));
            for (Object AEObject : actionExecutorConfig) {
                JSONObject AEJSON = (JSONObject) AEObject;
                root.addToHashTable(AEJSON.get("Executor").toString(), AEJSON.get("ReloadTime").toString());
            }

            JSONObject logFileConfig = (JSONObject) ((JSONArray) jsonObject.get("LogFileConfiguration")).get(0);
            ServerProcess.setProcessId((String) logFileConfig.get("ProcessIdPath"));
            regexTree.setStartRegex((String) logFileConfig.get("StartRegex"));
            regexTree.setEndRegex((String) logFileConfig.get("EndRegex"));

            MatchRuleEngine matchRuleEngine = new MatchRuleEngine(regexTree);

            Tailer carbonLogTailor = new Tailer((String) logFileConfig.get("FilePath"), matchRuleEngine, 100, true);
            carbonLogTailor.start();

        }catch (ParseException e) {
            System.out.print(e.getMessage());
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
//
//
//
//        //Initiate tailer class to tail the file
//
//      Thread correlationLogTailor= new Tailer(new File(XmlHelper.CorrelationLogPath),new LogReader(),1000,true);

//      correlationLogTailor.start();

    }
}
