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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Database connection scanner class is used to check whether external database connection is established or not.
 *
 * @author thumilan@wso2.com
 */
public class DatabaseConnectionScanner extends ActionExecutor {

    /**
     * Simple Constructor.
     */
    public DatabaseConnectionScanner() {

    }

    /**
     * This method used to do scan db connection.
     * It sends some characters to db server port and listens for the reply.
     */
    @Override
    public void execute(String folderpath) {

        try {
            Socket echoSocket = new Socket("localhost", 3306);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            String sample = "sample";
            out.print(sample.charAt(0));
            out.close();
            in.close();
            echoSocket.close();
            System.out.print("\tDatabase connection is Alive\n");

        } catch (UnknownHostException e) {
            System.out.print("Don't know about host: ");

        } catch (IOException e) {
            System.out.print("Couldn't get I/O for the connection to:");

        }

    }
}
