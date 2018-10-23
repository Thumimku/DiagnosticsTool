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

package org.wso2.carbon.diagnostics.regextree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class represents each node in regex Tree.
 * RegexTree is used to create nodes from json file.
 *
 * @see RegexTree
 */
public class RegexNode {

    // A Hash table used to map children node to their respective path Regex.
    private Hashtable<ArrayList<String>, RegexNode> children;

    // A JSON Object to store the diagnostics for that particular node.
    private JSONArray diagnosis;

    // A String to address the node for user understanding.
    private String description;

    // A string id to identify a node for developing purpose.
    private String id;

    // A Hash table which used to map every action executor with their reload time.
    // This attribute is only for Root node
    private Hashtable<String, String> actionExecutorReloadTime;

    // A JsonArray which used to configure action Executor
    private JSONArray actionExecutorConfiguration;

    public RegexNode(String id, String description, JSONArray data) {

        this.id = id;
        this.diagnosis = data;
        this.description = description;
        this.children = new Hashtable<>();
        this.actionExecutorReloadTime = new Hashtable<>();
        this.actionExecutorConfiguration = new JSONArray();

    }

    RegexNode(String id, String description) {

        this(id, description, null);

    }

    void addchildren(ArrayList<String> regex, RegexNode node) {

        this.children.put(regex, node);
    }

    public Hashtable<String, String> getactionExecutorReloadTime() {

        return actionExecutorReloadTime;
    }

    public void setactionExecutorReloadTime(Hashtable<String, String> actionExecutorReloadTime) {

        this.actionExecutorReloadTime = actionExecutorReloadTime;
    }

    public JSONArray getActionExecutorConfiguration() {

        return actionExecutorConfiguration;
    }

    public void setActionExecutorConfiguration(JSONArray actionExecutorConfiguration) {

        this.actionExecutorConfiguration = actionExecutorConfiguration;
    }

    public void addToHashTable(String key, String value) {

        this.actionExecutorReloadTime.put(key, value);
    }

    public JSONObject getconfiguration(String className) {

        for (Object aEObject : this.actionExecutorConfiguration) {
            JSONObject aEJSON = (JSONObject) aEObject;
            if (aEJSON.get("Executor").toString().compareTo(className) == 0) {

                return aEJSON;
            }
        }
        return null;
    }

    public Hashtable<ArrayList<String>, RegexNode> getChildren() {

        return children;
    }

    public void setChildren(Hashtable<ArrayList<String>, RegexNode> children) {

        this.children = children;
    }

    public JSONArray getDiagnosis() {

        return diagnosis;
    }

    void setDiagnosis(JSONArray diagnosis) {

        this.diagnosis = diagnosis;
    }

    public String getDescription() {

        return description;
    }

    public String getid() {

        return id;
    }

}


