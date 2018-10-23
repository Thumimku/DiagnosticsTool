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
 * NetStatExecuter class is used to execute netstat command.
 *
 * @author thumilan@wso2.com
 */
public class NetstatExecuter extends ActionExecutor {

    private static Log log = LogFactory.getLog(NetstatExecuter.class);

    /**
     * Method used to do memory dump with using Java Runtime Environment and jmap command.
     *
     * @param filepath file path of the dump folder
     */
    @Override
    public void execute(String filepath) {

        if (new File(filepath).exists()) { // check whether file exists before dumping.
            log.info("\t Netstat Successfuly done.\n");
            String filename = "/netstat.txt ";
            String frame = filepath + filename;
            String command;
            if (OsValidator.isUnix()) {
                command = "netstat -lt";

            } else if (OsValidator.isWindows()) {
                command = "netstat -f";
            } else {
                command = null;
            }
            try {
                if (command != null) {
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
                } else {
                    log.error("Unable to detect the OS");
                }


            } catch (IOException e) {
                log.error("Unable to do netstat");
            }

        }

    }

}
