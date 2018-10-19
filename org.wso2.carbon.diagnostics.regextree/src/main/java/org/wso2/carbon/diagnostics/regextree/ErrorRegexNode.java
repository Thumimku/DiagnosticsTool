package org.wso2.carbon.diagnostics.regextree;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sun.security.krb5.internal.crypto.Des;

import java.util.ArrayList;
import java.util.Hashtable;

public class ErrorRegexNode {

    public Hashtable<ArrayList<String>, ErrorRegexNode> children;
    public JSONArray diagnosis;
    public String Description;
    public String Id;
    //public String reloadTime;
    private  Hashtable<String, String> actionexecutorReloadTime;
    private JSONArray actionExecutorConfiguration;

    public ErrorRegexNode(String Id, String Description, JSONArray data) {

        this.Id = Id;
        this.diagnosis = data;
        this.Description = Description;
        this.children = new Hashtable<>();
        this.actionexecutorReloadTime = new Hashtable<>();
        this.actionExecutorConfiguration = new JSONArray();

    }

    public ErrorRegexNode(String Id, String Description) {

        this(Id, Description, null);

    }

    public void addchildren(ArrayList<String> regex, ErrorRegexNode node) {

        this.children.put(regex, node);
    }
    public Hashtable<String, String> getActionexecutorReloadTime() {

        return actionexecutorReloadTime;
    }

    public void setActionexecutorReloadTime(Hashtable<String, String> actionexecutorReloadTime) {

        this.actionexecutorReloadTime = actionexecutorReloadTime;
    }

    public JSONArray getActionExecutorConfiguration() {

        return actionExecutorConfiguration;
    }

    public void setActionExecutorConfiguration(JSONArray actionExecutorConfiguration) {

        this.actionExecutorConfiguration = actionExecutorConfiguration;
    }

    public void addToHashTable(String key,String value){
        this.actionexecutorReloadTime.put(key,value);
    }
    public JSONObject getconfiguration(String className) {

        for (Object AEObject : this.actionExecutorConfiguration) {
            JSONObject AEJSON = (JSONObject) AEObject;
            if (AEJSON.get("Executor").toString().compareTo(className) == 0) {

                return AEJSON;
            }
        }
        return null;
    }


}


