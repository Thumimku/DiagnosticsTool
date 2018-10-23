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
package org.wso2.carbon.diagnostics.actionexecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * OpenFileFinder class is used to execute lsof command.
 *
 * @author thumilan@wso2.com
 */
public class OpenFileFinder extends ActionExecutor {

    private static Log log = LogFactory.getLog(OpenFileFinder.class);

    /**
     * This string is used to represent process id.
     */
    private String processId;

    /**
     * Creates MemoryDumper with process id.
     */
    public OpenFileFinder() {

        this.processId = ServerProcess.getProcessId();

    }

    /**
     * Method used to do memory dump with using Java Runtime Environment and jmap command.
     *
     * @param filepath file path of dump folder
     */
    @Override
    public void execute(String filepath) {

        if (new File(filepath).exists()) { // check whether file exists before dumping.
            String frame = filepath + "/lsof.txt ";
            String command = "lsof -p " + this.processId;
            log.info("\t lsof successfully Done.\n");

            try {

                Process process = Runtime.getRuntime().exec(command);
                Scanner scanner = new Scanner(process.getInputStream(), "IBM850");
                scanner.useDelimiter("\\A");
                try {
                    FileWriter writer = new FileWriter(frame);
                    writer.write(scanner.next());
                    writer.close();
                } catch (IOException e) {
                    log.error("Unable to do write in file in netstat");
                }
                scanner.close();


            } catch (IOException e) {
                log.error("Unable to do netstat");
            }
        }

    }

}
