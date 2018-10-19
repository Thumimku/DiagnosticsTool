package org.wso2.carbon.diagnostics.application;
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
import org.wso2.carbon.diagnostics.actionexecutor.LogLineWriter;
import org.wso2.carbon.diagnostics.actionexecutor.ZipFileExecutor;
import org.wso2.carbon.diagnostics.actionexecutor.diagnosticCommand.ActionExecutor;
import org.wso2.carbon.diagnostics.actionexecutor.diagnosticCommand.ActionExecutorFactory;
import org.wso2.carbon.diagnostics.regextree.ErrorRegexNode;
import org.wso2.carbon.diagnostics.regextree.ErrorRegexTree;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Whenever there is error occur in Wso2server MatchRuleEngine detects it.
 * It invokes the methods of this class's instance.
 * This class is used to interpret the error and do appropriate actions for that error.
 * <p>
 * Example error scenario :-
 * When an error occurs interpreter instance create a new folder name as current Time stamp.
 * Then let threadDumper do thread dumper in that folder.
 * Then invoke zip execution to write logLine in the folder and zip it.
 *
 * @author thumilan@wso2.com
 */
public class Interpreter {

    private ActionExecutorFactory actionExecutorFactory; // ActionExecutorFactory to create executor objects

    private String folderpath; // Folder path of the TimeStamp Folder

//    //This HashMap used to map error type and their waiting time
//    private  HashMap<String, Long> errorTimingMap = new HashMap<>();

    private  Hashtable <String,Integer> actioexecutorLastTime;

    private ErrorRegexNode root;

    private ErrorRegexTree regexTree;
    private int count;



    /**
     * public Constructor.
     * Current action executor is set as zipFile executor
     * This constructor calls createFolder to create the Thread Dump folder and do thread dump.
     */
    public Interpreter(ErrorRegexTree regexTree) {

        this.actionExecutorFactory = new ActionExecutorFactory();
        createLogFolder();
        this.regexTree=regexTree;
        this.root = regexTree.getRoot();
        actioexecutorLastTime = new Hashtable<>();
        count=1;

    }

    /**
     * Method used to interpret logLine.
     * This method checks the validity of the error line.
     * Valid error log lines will go under diagnosis process.
     * If the diagnosis succeeds then certain dump files and error log line will be dumped at time stamped folder.
     *
     * @param logLine error log line
     */
    public void interpret(StringBuilder logLine) {

        //First check whether the error line is valid or not.
        if (this.checkValidity(logLine)) {
            //If it is a valid error then diagnose it.
            //if (this.diagnoseError(logLine)) {
            //Write the error log line into the folder
            //this.writeLogLine(logLine);
            //Zip the folder
            //this.executeZipFileExecuter();

            //}



            this.diagnoseError(logLine);

        }

    }

    /**
     * This method used to diagnose error.
     * First  match error regex to find the error.
     * Then check whether the error occurred recently or not.
     * Finally do the analysis.
     *
     * @param logLine error line
     * @return
     */
    private void diagnoseError(StringBuilder logLine) {



        ErrorRegexNode errorNode = regexTree.findDiagnosis(logLine);

        if((errorNode.diagnosis)!=null){
            JSONArray diagnosisArray = errorNode.diagnosis;
            System.out.print(count+" : "+errorNode.Description+"\n");
            count++;
            this.createFolder();
            if(this.doAnalysis(diagnosisArray,logLine.toString())){

                this.writeLogLine(logLine.toString());
                this.executeZipFileExecuter();
//                    this.deleteFolder();
            }else {
                this.deleteFolder();
            }


        }


    }

    /**
     * This method is used to do analysis.
     * First get diagnose json array and invoke certain action executor
     *
     * @param diagnoseArray JSON array
     */
    private boolean doAnalysis(JSONArray diagnoseArray,String logLine) {
        boolean analysed =  false;
        for (Object object : diagnoseArray) {
            JSONObject errorJsonObject = (JSONObject) object;
            if(checkActionExecutorReloadTime(logLine,errorJsonObject.get("Executor").toString(),root.getActionexecutorReloadTime().get(errorJsonObject.get("Executor").toString()))){
                ActionExecutor actionExecutor = actionExecutorFactory.getActionExecutor(errorJsonObject.get("Executor").toString(),root);
                if (actionExecutor != null) {
                    actionExecutor.execute(this.folderpath);
                    analysed = true;
                }
            }




        }
        return analysed;

    }

    /**
     * This method used to check validity of the error.
     *
     * @param logLine error log
     * @return
     */
    private boolean checkValidity(StringBuilder logLine) {

        return ((logLine.toString().split("\n")).length > 2);
    }

    /**
     * this method used to create the log folder.
     */
    private void createLogFolder() {

        folderpath = (System.getProperty("user.dir") + "/log/"); // get log file path

        File logfolder = new File(folderpath);
        if (!(logfolder.exists())) {
            logfolder.mkdir();
        }
    }

    private void deleteFolder(){
        File dumpfolder = new File(this.folderpath);
        if (dumpfolder.exists()){
            String[]entries = dumpfolder.list();
            for(String entry: entries){
                File currentFile = new File(dumpfolder.getPath(),entry);
                currentFile.delete();
            }
            dumpfolder.delete();
        }

    }

    /**
     * Create folder for dump.
     */

    public void createFolder() {

        folderpath = (System.getProperty("user.dir") + "/log/"); // get log file path

        File logfolder = new File(folderpath);
        if (!(logfolder.exists())) {
            logfolder.mkdir();
        }

        // folder name set as timestamp
        String foldername = new Timestamp(System.currentTimeMillis()).toString().replace(" ", "_");
        foldername = "WSO2_IS_@_" + foldername;
        File dumpFolder = new File(folderpath + foldername);
        if (!dumpFolder.exists()) {
            try {
                dumpFolder.mkdir(); // create folder if not exists.

                folderpath = folderpath + foldername;
            } catch (SecurityException se) {
                //handle it
                System.out.print(se.getMessage());
            }
        } else {

        }
    }

    /**
     * This method is used to call LogLine Writer to write the log line.
     *
     * @param Logline
     */
    public void writeLogLine(String Logline) {

        LogLineWriter logLineWriter = new LogLineWriter();
        logLineWriter.execute(Logline, folderpath);
    }

    /**
     * This method is used to call ZipFileExecutor to file the dump folder.
     */
    public void executeZipFileExecuter() {

        ZipFileExecutor zipFileExecutor = new ZipFileExecutor();
        zipFileExecutor.execute(folderpath);
    }

    /**
     * This method is used to check current error time and previous time when the same error occurred.
     * It also return whether tool has to do analysis for current error.
     *
     * @param testline error line
     * @param error    error type
     * @return
     */
//    private boolean checkErrorTime(String testline, String error, String time) {
//
//    String timeRegex = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
//        //Grep the first line of the error line.
//        String[] errorLine = testline.split("\n");
//
//
//        Long reloadTime= Long.parseLong(time);
//
//        Pattern pattern = Pattern.compile(timeRegex);
//
//        Matcher matcher = pattern.matcher(errorLine[0]);
//        if (matcher.find()) {
//
//            long errorTime = calculatetime(matcher.group(0));
//
//            if (errorTimingMap.containsKey(error)) {
//                //System.out.print((errorTimingMap.get(error) + " : " + errorTime + " : " + (errorTimingMap.get(error) - errorTime) + "\n"));
//                if ((errorTime - errorTimingMap.get(error)) > reloadTime) {
//                    errorTimingMap.replace(error, errorTime);
//                } else {
//                    return false;
//                }
//
//            } else {
//                errorTimingMap.put(error, errorTime);
//                return true;
//            }
//        }
//        return true;
//    }

    /**
     * This method is used to calculate current error time form log line.
     *
     * @param timeStr
     * @return
     */
    private int calculatetime(String timeStr) {

        String[] timeArray = timeStr.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int minute = Integer.parseInt(timeArray[1]);
        int second = Integer.parseInt(timeArray[0].substring(0, 2));
        return (hour * 3600) + (minute * 60) + second;
    }

    private boolean checkActionExecutorReloadTime(String testline, String error, String time) {

        String timeRegex = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";

        //Grep the first line of the error line.
        String[] errorLine = testline.split("\n");


        Long reloadTime= Long.parseLong(time);

        Pattern pattern = Pattern.compile(timeRegex);

        Matcher matcher = pattern.matcher(errorLine[0]);
        if (matcher.find()) {

            long errorTime = calculatetime(matcher.group(0));

            if (actioexecutorLastTime.containsKey(error)) {
//                System.out.print((actioexecutorLastTime.get(error) + " : " + errorTime + " : " + (actioexecutorLastTime.get(error) - errorTime) + "\n"));
                if ((errorTime - actioexecutorLastTime.get(error)) > reloadTime) {
                    actioexecutorLastTime.replace(error, (int)errorTime);
//                    System.out.print((actioexecutorLastTime.get(error) + " : " + errorTime + " : " + "\n"));

                } else {
                    return false;
                }

            } else {
                actioexecutorLastTime.put(error,(int)errorTime);
//                System.out.print((actioexecutorLastTime.get(error) + " : " + errorTime + " : " + "\n"));

                return true;
            }
        }
        return true;
    }
}