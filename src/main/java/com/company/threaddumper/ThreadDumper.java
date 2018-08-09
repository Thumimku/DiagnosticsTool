package com.company.threaddumper;
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


import java.io.File;
import java.io.IOException;

/**
 *
 * @author thumilan@wso2.com
 */
public class ThreadDumper {

    /**
     * This string is used to represent process id.
     */
    private String processid;
    /**
     * This integer is used to store thread dump file suffix.
     */
    private Integer fileSuffix;
    /**
     * This long is used to refer delay between thread dumps.
     */
    private long delay;
    /**
     * This int is used to refer how many thread dumps needed.
     */
    private int threadDumpCount;
    /**
     * Creates Thread Dumper with process id and delay.
     * Default fileSuffix = 1
     * Default threadDumpCount = 5
     * @param processid process id which used for thread dumping
     * @param delay delay between two thread dumps
     */
    public ThreadDumper(String processid, long delay) {
        this(processid, delay, 5);

    }

    /**
     * Creates Thread Dumper with process id and delay.
     * Default fileSuffix = 1
     * Default threadDumpCount = 5
     * Default delay = 1000
     * @param processid process id which used for thread dumping
     *
     */
    public ThreadDumper(String processid) {
        this(processid, 1000);
    }
    /**
     * Creates Thread Dumper with process id and delay.
     * Default fileSuffix = 1
     * @param processid process id which used for thread dumping
     * @param delay delay between two thread dumps
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
    public synchronized void createFolder(){

    }

    public void doThreadDumping(String filepath) {


        if (new File(filepath).exists()) {
            String stackframe = "jstack -l " + processid + " > " + filepath;

            for (int counter = threadDumpCount; counter > 0; counter--) {
                try {
                    String command = System.getenv("JAVA_HOME") + "/bin/" + stackframe + "/td_" + fileSuffix + ".txt";
                    Runtime.getRuntime().exec(new String []{"bash", "-c", command});
                    fileSuffix++;
                    synchronized (this) {
                        this.wait(delay);
                    }



                } catch (IOException e) {
                    System.out.print("Unable to do thread dump for " + processid);
                } catch (InterruptedException e) {

                }

            }
        }

    }

}
