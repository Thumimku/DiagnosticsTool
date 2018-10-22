package org.wso2.carbon.diagnostics.actionexecutor.postexecutor;
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

/**
 * PrintLineExecutor used to print the logLines into the console.
 * Currently this class designed as a Singleton class inorder to optimise CPU usage.
 * Further research is required to change the design.
 * <p>
 * PostExecutor interface is implemented for code reuse.
 *
 * @author thumilan@wso2.com
 */
public class PrintLineExecutor {

    /**
     * public Constructor.
     */
    public PrintLineExecutor() {

    }

    /**
     * override method used to print the logLines.
     *
     * @param logLine the line
     */

    public void execute(StringBuilder logLine, String path) {

        System.out.print(logLine + "\n");
    }
}
