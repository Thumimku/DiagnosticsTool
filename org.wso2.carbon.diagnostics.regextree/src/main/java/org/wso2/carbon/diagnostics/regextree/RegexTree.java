package org.wso2.carbon.diagnostics.regextree;

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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Set;

/**
 * This class is used to represent regex tree.
 * Inorder to analyse and diagnose the error Interpreter need particular order of regex pattern.
 * This regex pattern order is given in the form of regexTree.
 * RegexTree will be created at the initialization of the tool from given json file.
 */

public class RegexTree {

    // A Regex Node to represent root node.
    private RegexNode root;

    // Following strings are used by Match Rule engine to pass certain log lines to Interpreter.
    private String startRegex;
    private String endRegex;

    /**
     * This method is used to expand Regex tree from the root.
     *
     * @param nodeJSON json object which used to expand the regex tree from root.
     * @return root node of the Regex Tree
     */
    public RegexNode expandTree(JSONObject nodeJSON) {

        // Following variables is used to store json object key.
        String description = "Description";
        String children = "Children";
        String diagnosis = "Diagnosis";
        String regex = "Regex";
        String id = "Id";

        //Create current parent node
        RegexNode node = new RegexNode(nodeJSON.get(id).toString(), nodeJSON.get(description).toString());
        // If current json object has diagnostics then add the diagnostics to current node.
        if (nodeJSON.containsKey(diagnosis)) {
            node.setDiagnosis((JSONArray) nodeJSON.get(diagnosis));
        }

        // If current node has children then call this method recursively to create children nodes.
        // finally add children nodes and their respective path regex to current node children has table.
        if (nodeJSON.containsKey(children)) {
            JSONArray childrenJSONArray = (JSONArray) nodeJSON.get(children);
            for (Object childObject : childrenJSONArray) {
                JSONObject childJSON = (JSONObject) childObject;
                RegexNode childnode = expandTree(childJSON);
                node.addchildren((ArrayList<String>) childJSON.get(regex), childnode);

            }
        }
        return node;
    }

    /**
     * This method is used to check the node structure of the regex tree.
     * This method can be used in developing purpose.
     *
     * @param root root node of Regex tree
     */
    public void checkTree(RegexNode root) {

        Set<ArrayList<String>> keys = root.getChildren().keySet();
        System.out.print(root.getChildren().toString() + "\n");
        for (ArrayList<String> key : keys) {
            checkTree(root.getChildren().get(key));
        }
    }

    /**
     * This method is used to analyse the error log line and find what error it is.
     * This method match the current node's child paths to log line.
     * If matches succeed it traverse to certain child node node and repeat the process until no child node found.
     * Then return the current leaf node.
     *
     * @param logLine error log line
     * @return respective leaf node for the log line
     */
    public RegexNode findDiagnosis(String logLine) {

        // Set current Node to root.
        RegexNode currentNode = root;

        //Initialise found as false.
        Boolean found = false;

        //Loop until find suitable node for the error log Line.
        while (!found) {

            // If current node is leaf then return it.
            if ((currentNode.getChildren().isEmpty())) {
                return currentNode;
            } else {
                // Create a temporary node for child nodes.
                RegexNode tempNode = null;
                Set<ArrayList<String>> keys = currentNode.getChildren().keySet();
                for (ArrayList<String> key : keys) {

                    tempNode = digDeep(logLine, key, currentNode);
                    if (tempNode != null) {
                        currentNode = tempNode;
                        break;
                    }
                }
                if (tempNode == null) {
                    return currentNode;
                }

            }

        }
        return null;

    }

    /**
     * This method is used to check whether is there a match between log line and regex in ArrayList
     * If there is a match then return the respective child.
     *
     * @param logLine     error log line.
     * @param key         ArrayList which is used to map parent node and child node.
     * @param currentNode current paretn node.
     * @return matched child node.
     */
    private RegexNode digDeep(String logLine, ArrayList<String> key, RegexNode currentNode) {

        for (String regex : key) {
            if (logLine.contains(regex)) {

                return currentNode.getChildren().get(key);

            }

        }
        return null;
    }

    public RegexNode getRoot() {

        return this.root;
    }

    public void setRoot(RegexNode root) {

        this.root = root;
    }

    public String getStartRegex() {

        return startRegex;
    }

    public void setStartRegex(String startRegex) {

        this.startRegex = startRegex;
    }

    public String getEndRegex() {

        return endRegex;
    }

    public void setEndRegex(String endRegex) {

        this.endRegex = endRegex;
    }
}