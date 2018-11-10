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

package org.wso2.carbon.diagnostics.actionexecutor.postexecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * LogLineWriter used to print the logLines into the time-stamped folder.
 * PostExecutor interface is implemented for code reuse.
 *
 * @author thumilan@wso2.com
 */
public class LogLineWriter {

    private static Logger log = LoggerFactory.getLogger(LogLineWriter.class);

    /**
     * public Constructor.
     */

    public LogLineWriter() {

    }

    /**
     * This method is used to write the log line into destination file and zip the folder.
     *
     * @param logLine the line
     */

    public void execute(String logLine, String path) {

        File folder = new File(path);

        try {
            FileWriter writer = new FileWriter(path + "/" + folder.getName() + ".txt", true);
            writer.write(logLine);
            writer.close();
        } catch (IOException e) {
            log.error("exeception");
        }

    }

}
