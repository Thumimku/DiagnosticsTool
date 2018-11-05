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

package org.wso2.carbon.diagnostics.application.config;

/**
 * Configuration class
 */
public class ConfigConstants {

    public static final String JSON_NAME_ACTION_EXECUTOR_CONFIGURATION = "ActionExecutorConfiguration";
    public static final String JSON_NAME_EXECUTOR = "Executor";
    public static final String JSON_NAME_RELOAD_TIME = "ReloadTime";
    public static final String JSON_NAME_LOG_FILE_CONFIGURATION = "LogFileConfiguration";
    public static final String JSON_NAME_PROCESS_ID_PATH = "ProcessIdPath";
    public static final String JSON_NAME_START_REGEX = "StartRegex";
    public static final String JSON_NAME_END_REGEX = "EndRegex";
    public static final String JSON_NAME_FILE_PATH = "FilePath";


    /**
     * Prevents instantiation as this holds constant definitions.
     */
    private ConfigConstants() {

    }
}
