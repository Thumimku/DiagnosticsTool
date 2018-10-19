# Diagnostics Tool 

## About

This tool is designed to diagnose errors by tailing the log file. It is a stand alone java application with less memory footprint.

## Structure
# Logtailor

## Requirement

1. Java 8
2. IDE (Inteillj - not necessary )
3. Maven
4. WSO2 IS Server

## Current Progress

- Application can detect error logs in real time and write it in a seperate file for further research.
- Whenever error detected application do `Thread Dump` and save thread dumps in same folder of log line.
- Finally application zip the entire folder of error log and thread dumps.

## How to Configure Json File
Inorder to run the application first program needs wsocarbon.log path and wso2carbon.pid path
- Set wso2carbon.log path
  - open wso2conf.xml (src/resources/wsp2conf.xml)
  - Replace the path with your wso2carbon.log file path (<IS_HOME>/repository/logs/wso2carbon.log)
- Set wso2carbon.pid path
  - open wso2conf.xml (src/resources/wsp2conf.xml)
  - Replace the path with your wso2carbon.pid file path (<IS_HOME>/wso2carbon.pid)
Check whether your wso2server folder grant `read access` for `others`

## Run the Application

- Run `OnBoardDiagnostics.jar`.
- Run wso2server
- If there any error occurs thread dumps and log line are zipped in Log folder.

Sample Thread dump and error log line is in `src/main/resources/log`
