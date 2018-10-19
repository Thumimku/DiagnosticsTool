# Diagnostics Tool 

## About

This tool is designed to diagnose errors by tailing the log file. It is a stand alone java application with less memory footprint.
## Requirement

1. Java 8
2. IDE (Inteillj - not necessary )
3. Maven
4. WSO2 IS Server

## Structure and Functionalities
### Logtailor

### MatchRuleEngine

### Interpreter

### ActionExecutors

### PostActionExecutors

### RegexTree and RegexNode

## Extensibility





## Current Progress

- Application can detect error logs in real time and write it in a seperate file for further research.
- Whenever error detected application do `Thread Dump` and save thread dumps in same folder of log line.
- Finally application zip the entire folder of error log and thread dumps.

## How to Configure Json File

## How to run


## Run the Application

- Run `OnBoardDiagnostics.jar`.
- Run wso2server
- If there any error occurs thread dumps and log line are zipped in Log folder.

Sample Thread dump and error log line is in `src/main/resources/log`
