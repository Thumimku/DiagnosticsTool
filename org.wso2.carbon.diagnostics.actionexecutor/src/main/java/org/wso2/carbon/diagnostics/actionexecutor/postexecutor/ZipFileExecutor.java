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

package org.wso2.carbon.diagnostics.actionexecutor.postexecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZipLineExecutor used to print the logLines into the console.
 * Currently this class designed as a Singleton class inorder to optimise CPU usage.
 * Further research is required to change the design.
 * <p>
 * PostExecutor interface is implemented for code reuse.
 *
 * @author thumilan@wso2.com
 */
public class ZipFileExecutor {

    private static Logger log = LoggerFactory.getLogger(ZipFileExecutor.class);

    /**
     * public Constructor.
     */
    private String logDirpath = (System.getProperty("user.dir") + "/Zip/"); //Refers the log Folder.

    public ZipFileExecutor() {

        File logfolder = new File(logDirpath);
        if (!(logfolder.exists())) {
            logfolder.mkdir();
        }
    }

    /**
     * This method is used to write the log line into destination file and zip the folder.
     */
    public void execute(String path) {

        File folder = new File(path);

        try {
            zipFolder(path, logDirpath + folder.getName() + ".zip");
            log.info("Diagnosis Dumped in :" + logDirpath + folder.getName() + ".zip\n");
        } catch (Exception e) {
            log.error("Unable to zip the file at " + path);
        }

    }

    /**
     * Method used to zip folder.
     *
     * @param srcFolder   file which needed to zip.
     * @param destZipFile destination path of zip folder.
     * @throws Exception throw a exception
     */
    private void zipFolder(String srcFolder, String destZipFile) {

        ZipOutputStream zip;
        FileOutputStream fileWriter;
        try {
            fileWriter = new FileOutputStream(destZipFile); // set file output stream
            zip = new ZipOutputStream(fileWriter);

            addFolderToZip("", srcFolder, zip);

            zip.flush();
            zip.close();
        } catch (Exception e) {

        }

    }

    /**
     * Method used to zip file.
     *
     * @param path    destination path of the file
     * @param srcFile source path of the file which need to zip
     * @param zip     ZipOutputStream
     * @throws Exception
     */
    private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
            throws Exception {

        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip); // if current file is directory do zip folder.
        } else {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);

            }

        }
    }

    /**
     * Mehtod used to zip folder.
     *
     * @param path      destination  path of the folder.
     * @param srcFolder source path of the folder.
     * @param zip       ZipOutputStream
     * @throws Exception throw exception to parent method
     */
    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
            throws Exception {

        File folder = new File(srcFolder);

        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }

}
