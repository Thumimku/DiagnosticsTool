package com.company.actionexecutor;
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
 * This class is used to connect with ActionExecutorFactory.
 * Instead of exposing the actual ActionExecutor this connector helps encapsulate the creation of the object.
 *
 * Currently this class is removed from the flow due to redundancy.
 * @author thumilan@wo2.com
 */
public class ActionExecutorConnector {
    //connector class used to connect ActionExecutors
    private ActionExecutorFactory actionExecutorFactory;
    private ActionExecutor actionExecutor;
    public ActionExecutorConnector() {
        actionExecutorFactory = new ActionExecutorFactory();
    }
    //connect with printExecutor
    public void printexecution(StringBuilder logLine) {
        actionExecutor = actionExecutorFactory.getActionExecutor("PRINTLINEEXECUTOR");
        //actionExecutor.execute(logLine);
    }
    public void zipfileexecution(StringBuilder logLine) {
        actionExecutor = actionExecutorFactory.getActionExecutor("zipfileexecutor");
        //actionExecutor.execute(logLine);
    }
}
