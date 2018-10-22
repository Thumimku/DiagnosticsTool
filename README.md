# Diagnostics Tool 

## About

This tool is designed to diagnose errors by tailing the log file. It is a stand alone java application with less memory footprint.
## Requirement

1. Java 8
2. IDE (Inteillj - not necessary )
3. Maven
4. WSO2 IS Server

## Libraries
1.Json Simple

## Structure and Functionalities
### Logtailor

[Apache.Commons.Tailor](https://commons.apache.org/proper/commons-io/javadocs/api-2.4/org/apache/commons/io/input/Tailer.html) implmentation used to tail the log file. It uses file channel to read the data from the log file and load the data into byte buffer. 

### MatchRuleEngine

Since it implements TailerListener, it handles all the log lines and separate all the ERROR log lines from INFO log lines.

### Interpreter

Interpreter first validates the log line ,then it analyses the log line and find what error occured ,later it triggers respective action executors to diagnose the error and finally it triggers post action executors to do post actions.

### ActionExecutors

Classes which extend Action executor abstract class can be used as ActionExecutors. Execute function with parameter Folder path (which contains of the folder path where action execution results sholud store) used to do diagnosis. ActionExecutors can use root(RegexNode) for configuration purpose.

### PostActionExecutors

Currently ZipFileExecutor is the only one post action executor which used to file the diagnostics reults folder.

### RegexTree and RegexNode

![alt text](https://github.com/Thumimku/DiagnosticsTool/blob/master/img/RegexTree.jpg "Logo Title Text 1")

Regex Tree is built up in tool start up phase. It used as a static memory. Json file configuration is given below.

## Extensibility

- User can extend the tree by adding nodes by giving proper regex as path.
- User can add one or more diagnostic action executors to both leaf nodes and non leaf nodes.
- User can add one or more regex pattern as path between parent node and child node.
- User can add ActionExecutors.



## Current Progress


## How to Configure Json File

## How to run

- Clone the project
- Go to `DiagnosticsTool/org.wso2.carbon.diagnostics.application/resources`
- Open the RegexTree.json file in any text editor
- Edit `LogFileConfiguration` by providing wso2carbon.log file path and wso2carbon.pid file path
- Build the project using `mvn clean install`
- Go to `DiagnosticsTool/org.wso2.carbon.diagnostics.application/target`
- run jar file 


