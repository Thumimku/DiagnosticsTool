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
Json file contains Three parts. This file is supposed to to configure the tool to read one log file. 

1.REGEX TREE

This regex tree implements following logic.
```
BEGIN

	IF <parent_regex> OCCURRED THEN CHECK FOR MATCHING <child_regex>.
  
		<parent_regex>:- Strings in current node's "Regex" JSON array.
    
		<child_regex>:- Strings in "Children" JSON array's "Regex" JSON array.
    
	IF MATCH FOUND LOOP TO PREVIOUS ONE ALTERING PARENT NODE AS CHILD NODE.
  
	ELSE RETURN DIAGNOSIS JSON  ARRAY
  
END
 ```
  
STRUCTURE

- Id – Unique String to identify 

- Description – Short description about the node/error.

- Children – JSON array which contains children nodes.

- Diagnosis – JSON array which contains executor json object. 

		Note that executor name should be it’s class name.
    
2.ACTION EXECUTOR CONFIGURATION

- This is a JSON array contains configuration  for the action executors. 
  
3.LOG FILE CONFIGURATION

- This JSON array has attributes of log file. 

## How to run

- Clone the project
- Go to `DiagnosticsTool/org.wso2.carbon.diagnostics.application/resources`
- Open the RegexTree.json file in any text editor
- Edit `LogFileConfiguration` by providing wso2carbon.log file path and wso2carbon.pid file path
- Build the project using `mvn clean install`
- Go to `DiagnosticsTool/org.wso2.carbon.diagnostics.application/target/Diagnostic-tool/bin`
- run the bash file 


