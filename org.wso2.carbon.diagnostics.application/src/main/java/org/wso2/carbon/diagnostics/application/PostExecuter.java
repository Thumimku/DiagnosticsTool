package org.wso2.carbon.diagnostics.application;

import org.wso2.carbon.diagnostics.actionexecutor.postexecutor.LogLineWriter;
import org.wso2.carbon.diagnostics.actionexecutor.postexecutor.ZipFileExecutor;

import java.io.File;
import java.util.TimerTask;

public class PostExecuter extends TimerTask {

    private String logLine;

    private String folderpath;

    public PostExecuter(String logLine, String folderpath) {

        this.logLine = logLine;
        this.folderpath = folderpath;
    }

    @Override
    public void run() {
        this.writeLogLine(logLine);
        this.executeZipFileExecuter();
        this.deleteFolder();
    }


    /**
     * This method is used to call LogLine Writer to write the log line.
     *
     * @param logline error log line
     */
    private void writeLogLine(String logline) {

        LogLineWriter logLineWriter = new LogLineWriter();
        logLineWriter.execute(logline, folderpath);
    }

    /**
     * This method is used to call ZipFileExecutor to file the dump folder.
     */
    private void executeZipFileExecuter() {

        ZipFileExecutor zipFileExecutor = new ZipFileExecutor();
        zipFileExecutor.execute(folderpath);
    }

    private void deleteFolder() {

        File dumpfolder = new File(this.folderpath);
        if (dumpfolder.exists()) {
            String[] entries = dumpfolder.list();
            for (String entry : entries) {
                File currentFile = new File(dumpfolder.getPath(), entry);
                currentFile.delete();
            }
            dumpfolder.delete();
        }

    }
}
