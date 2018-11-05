/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.diagnostics.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.diagnostics.actionexecutor.ServerProcess;
import org.wso2.carbon.diagnostics.application.config.ConfigConstants;
import org.wso2.carbon.diagnostics.logtailor.Tailer;
import org.wso2.carbon.diagnostics.regextree.RegexNode;
import org.wso2.carbon.diagnostics.regextree.RegexTree;

import java.io.FileReader;
import java.io.IOException;

/**
 * Diagnostic tool launcher.
 * This is a simple Java Main class.
 */
public class Application {

    private static final Log log = LogFactory.getLog(Application.class);
    private static final String CONFIG_FILE_PATH = "/resources/DiagnosticConfig.json";

    public static void main(String[] args) {

        log.info("................loading  Diagnostics Tool .............");

        JSONParser parser = new JSONParser();
        RegexTree regexTree = new RegexTree();

        try {
            FileReader jsonFileReader = new FileReader(System.getProperty("user.dir") + CONFIG_FILE_PATH);
            JSONObject jsonObject = (JSONObject) parser.parse(jsonFileReader);
            RegexNode root = regexTree.expandTree(jsonObject);

            regexTree.setRoot(root);

            JSONArray actionExecutorConfig = (JSONArray) jsonObject.get(
                    ConfigConstants.JSON_NAME_ACTION_EXECUTOR_CONFIGURATION);
            root.setActionExecutorConfiguration(actionExecutorConfig);
            for (Object aEObject : actionExecutorConfig) {
                JSONObject aEJSON = (JSONObject) aEObject;
                root.addToHashTable(aEJSON.get(ConfigConstants.JSON_NAME_EXECUTOR).toString(),
                        aEJSON.get(ConfigConstants.JSON_NAME_RELOAD_TIME).toString());
            }

            JSONObject logFileConfig = (JSONObject) ((JSONArray) jsonObject.get(
                    ConfigConstants.JSON_NAME_LOG_FILE_CONFIGURATION)).get(0);
            ServerProcess.setProcessId((String) logFileConfig.get(ConfigConstants.JSON_NAME_PROCESS_ID_PATH));
            regexTree.setStartRegex((String) logFileConfig.get(ConfigConstants.JSON_NAME_START_REGEX));
            regexTree.setEndRegex((String) logFileConfig.get(ConfigConstants.JSON_NAME_END_REGEX));

            MatchRuleEngine matchRuleEngine = new MatchRuleEngine(regexTree);

            log.info("listening to :" + logFileConfig.get(ConfigConstants.JSON_NAME_FILE_PATH));

            Tailer carbonLogTailor = new Tailer((String) logFileConfig.get(ConfigConstants.JSON_NAME_FILE_PATH),
                    matchRuleEngine, 100, true);
            carbonLogTailor.start();

        } catch (ParseException e) {
            log.error("parse exception occurred");
        } catch (IOException e) {
            log.error("IO exception occurred");
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
