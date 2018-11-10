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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.diagnostics.actionexecutor.ServerProcess;
import org.wso2.carbon.diagnostics.application.config.ConfigConstants;
import org.wso2.carbon.diagnostics.logtailor.Tailer;
import org.wso2.carbon.diagnostics.regextree.RegexNode;
import org.wso2.carbon.diagnostics.regextree.RegexTree;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Diagnostic tool launcher.
 * This is a simple Java Main class.
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String CONFIG_FILE_PATH = "DiagnosticConfig.json";
    private static final String CMD_OPTION_CONFIG_FILE = "c";

    public static void main(String[] args) {

        log.info("Diagnostic tool is starting...");
        Options options = new Options();

        options.addOption(CMD_OPTION_CONFIG_FILE, true, "Configuration File");

        String configFilePath = null;

        try {
            CommandLineParser cmdParser = new DefaultParser();
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption(CMD_OPTION_CONFIG_FILE)) {
                configFilePath = cmd.getOptionValue(CMD_OPTION_CONFIG_FILE);
            }

            JSONParser parser = new JSONParser();
            RegexTree regexTree = new RegexTree();

            File configFile;
            if (configFilePath != null) {
                configFile = new File(configFilePath);
            } else {
                configFile = new File(Application.class.getClassLoader().getResource(CONFIG_FILE_PATH).getPath());
            }
            log.info("Reading config file at the location: " + configFile.getAbsolutePath());
            if (!configFile.exists() || !configFile.isFile()) {
                log.error("Diagnostic configuration does not exists in the path: " + configFile.getAbsolutePath());
            } else {
                JSONObject logFileConfig = readConfiguration(parser, regexTree, configFile);

                MatchRuleEngine matchRuleEngine = new MatchRuleEngine(regexTree);

                log.info("listening to :" + logFileConfig.get(ConfigConstants.JSON_NAME_FILE_PATH));

                Tailer carbonLogTailor = new Tailer((String) logFileConfig.get(ConfigConstants.JSON_NAME_FILE_PATH),
                        matchRuleEngine, 100, true);
                carbonLogTailor.start();
            }

        } catch (ParseException e) {
            log.error("Parse exception occurred", e);
        } catch (IOException e) {
            log.error("IO exception occurred", e);
        } catch (org.apache.commons.cli.ParseException e) {
            log.error("Unable to detect command line options");
            printError(options, "command");
        }
    }

    private static void printError(Options options, String commadName) {

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(commadName, options);
    }

    private static JSONObject readConfiguration(JSONParser parser, RegexTree regexTree, File configFile)
            throws IOException, ParseException {

        FileReader jsonFileReader = new FileReader(configFile);
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
        return logFileConfig;
    }
}
