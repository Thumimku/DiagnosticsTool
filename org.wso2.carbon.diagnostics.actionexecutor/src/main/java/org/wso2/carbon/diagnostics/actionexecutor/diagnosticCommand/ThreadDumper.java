package org.wso2.carbon.diagnostics.actionexecutor.diagnosticCommand;
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

import org.json.simple.JSONObject;
import org.wso2.carbon.diagnostics.regextree.ErrorRegexNode;
import org.wso2.carbon.diagnostics.regextree.ErrorRegexTree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * ThreadDumper class is used to do thread dump for given process.
 * This Process was referenced by ServerProcess class.
 *
 * @author thumilan@wso2.com
 * @see ServerProcess
 * <p>
 * This class use Java Runtinme enviroment and jstack command to do thread dump.
 */
public class ThreadDumper extends ActionExecutor {

    /**
     * This string is used to represent process id.
     */
    private String processid;
    /**
     * This integer is used to store thread dump file suffix.
     */
    private Integer fileSuffix = 1;
    /**
     * This long is used to refer delay between thread dumps.
     */
    private long delay;
    /**
     * This int is used to refer how many thread dumps needed.
     */
    private int threadDumpCount;

    private JSONObject configuration;

    /**
     * Creates Thread Dumper with process id and delay.
     * Default fileSuffix = 1
     * Default threadDumpCount = 5
     *
     * @param processid process id which used for thread dumping
     * @param delay     delay between two thread dumps
     */
    public ThreadDumper(String processid, long delay) {

        this(processid, delay, 5);

    }

    /**
     * Creates Thread Dumper with process id and delay.
     * Default fileSuffix = 1
     * Default threadDumpCount = 5
     * Default delay = 1000
     *
     * @param processid process id which used for thread dumping
     */
    public ThreadDumper(String processid) {

        this(processid, 1000);
    }

    /**
     * Creates Thread Dumper with process id and delay.
     * Default fileSuffix = 1
     *
     * @param processid       process id which used for thread dumping
     * @param delay           delay between two thread dumps
     * @param threadDumpCount number of threadDumps.
     */
    public ThreadDumper(String processid, long delay, int threadDumpCount) {

        if (processid != null && delay > 999 && threadDumpCount > 3) {
            this.processid = processid;
            this.delay = delay;
            this.fileSuffix = 1;
            this.threadDumpCount = threadDumpCount;
        } else {
            throw new IllegalArgumentException();
        }

    }

    public ThreadDumper() {

        this.processid = ServerProcess.getProcessId();
        //this(new ServerProcess().getProcessId());

    }

    /**
     * Method used to do thread dump with using Java Runtime Environment and jstack command.
     * Currently its written for linux environment.
     *
     * @param folderpath
     */
    @Override
    public void execute(String folderpath) {

        if (configuration == null) {
            configuration = root.getconfiguration("ThreadDumper");
            threadDumpCount = Integer.parseInt(configuration.get("count").toString());
            delay = Integer.parseInt(configuration.get("delay").toString());
        }

        if (new File(folderpath).exists()) { // check whether file exists before dumping.
            String commandFrame = System.getenv("JAVA_HOME") + "/bin/jstack " + processid;
            System.out.print("\t Thread Dump Successfully Dumped.\n");

            for (int counter = threadDumpCount; counter > 0; counter--) {
                try {
                    String filepath = folderpath + "/td_" + fileSuffix + ".txt";
                    Process process = Runtime.getRuntime().exec(commandFrame);
                    Scanner scanner = new Scanner(process.getInputStream());
                    scanner.useDelimiter("\\A");
                    try {
                        FileWriter writer = new FileWriter(filepath);
                        writer.write(scanner.next());
                        writer.close();
                    } catch (IOException e) {
                        System.out.print("Unable to do write in file while thread dumping");
                    }

                    scanner.close();

                    fileSuffix++;
                    synchronized (this) {
                        this.wait(delay);
                    }
                } catch (IOException e) {
                    System.out.print("Unable to do thread dump for " + processid + "\n");
                } catch (InterruptedException e) {
                    System.out.print("Unable to do wait delay time due to : " + e.getMessage());
                }

            }
        }

    }

}
