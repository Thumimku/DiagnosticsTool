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

import org.wso2.carbon.diagnostics.regextree.ErrorRegexNode;

import java.lang.reflect.InvocationTargetException;

/**
 * This Factory class used to create various Executors instance by their class name.
 *
 * @author thumilan@wso2.com
 */
public class ActionExecutorFactory {



    /**
     * This Method used to create Executor objects.
     *
     * @param executorType the executor type.
     * @return PostExecutor
     */
    public ActionExecutor getActionExecutor(String executorType, ErrorRegexNode root) {

        try {
            String classnameShell = "org.wso2.carbon.diagnostics.actionexecutor.diagnosticCommand.";

            ActionExecutor actionExecutor= (ActionExecutor) Class.forName(classnameShell + executorType).getConstructor().newInstance();
            actionExecutor.setRoot(root);
            return actionExecutor;
        } catch (NoSuchMethodException e) {
            System.out.print("Invalid executor configured as " + executorType + " . Unable to load the class");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}
