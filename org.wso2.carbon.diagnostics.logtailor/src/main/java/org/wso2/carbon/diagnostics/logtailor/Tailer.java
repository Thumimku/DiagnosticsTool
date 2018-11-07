/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.diagnostics.logtailor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

/**
 * Implementation of the unix "tail -f" functionality, forked from the Apache Commons IO project and providing fixes,
 * cleaner APIs and improved
 * performance with buffered reads.
 * <p>
 * Functionally speaking, the only notable difference with the Apache version is that the tailer thread will be able to
 * read lines only if shorter than the buffer size,
 * and will pause after a full buffer is read, even if end of file hasn't been reached yet:
 * this is in order to avoid flooding with tail requests in case of large log files; as a consequence, please set an
 * appropriate buffer size (default one is 4096 bytes).
 * </p>
 *
 * @author Apache Commons IO Team
 * @author Sergio Bossa
 * @author thumilan@wso2.com
 * @see TailerListener
 * @see TailerListenerAdapter
 */
public class Tailer extends Thread {

    private static final Log log = LogFactory.getLog(Tailer.class);

    /**
     * The file which will be tailed.
     */
    private final File file;
    /**
     * The amount of time to wait for the file to be updated.
     */
    private final long delay;
    /**
     * Whether to tail from the end or start of file.
     */
    private final boolean end;
    /**
     * The listener to notify of events when tailing.
     */
    private final TailerListener listener;
    /**
     * The tailer will run as long as this value is true.
     */
    private volatile boolean run = true;



    /**
     * The "truncated" line from buffered reads (when buffer ends prior to reaching eol or eof), as a recycled char array.
     */
    private volatile char[] remaind;
    private volatile int remaindIndex;

    /**
     * The recycled buffer for buffered reads.
     */
    private final byte[] buffer;

//    private ByteBuffer buffer;

//    /**
//     * The recycled buffer for buffered reads.
//     */
//
//    private StringBuilder charBuffer;

    /**
     * The Tailer position in the file.
     */
    private long position;

    private RandomAccessFile reader;

    /**
     * Creates a Tailer for the given file, starting from the beginning, with the default delay of 1.0s,
     * and the default buffer size of 1024 bytes.
     *
     * @param filepath The file to follow.
     * @param listener the TailerListener to use.
     */
    public Tailer(String filepath, TailerListener listener) {

        this(filepath, listener, 1000);
    }

    /**
     * Creates a Tailer for the given file, starting from the beginning and with the default buffer size of 1024 bytes.
     *
     * @param filepath the file to follow.
     * @param listener the TailerListener to use.
     * @param delay    the delay between checks of the file for new content in milliseconds.
     */
    public Tailer(String filepath, TailerListener listener, long delay) {

        this(filepath, listener, delay, true);
    }

    /**
     * Creates a Tailer for the given file, with the default buffer size of 1024 bytes.
     *
     * @param filepath the file to follow.
     * @param listener the TailerListener to use.
     * @param delay    the delay between checks of the file for new content in milliseconds.
     * @param end      Set to true to tail from the end of the file, false to tail from the beginning of the file.
     */
    public Tailer(String filepath, TailerListener listener, long delay, boolean end) {

        this(filepath, listener, delay, end, 4096);
    }

    /**
     * Creates a Tailer for the given file.
     *
     * @param filepath   the file to follow.
     * @param listener   the TailerListener to use.
     * @param delay      the delay between checks of the file for new content in milliseconds.
     * @param end        Set to true to tail from the end of the file, false to tail from the beginning of the file.
     * @param bufferSize Buffer size for buffered reads from the tailed file.
     */
    public Tailer(String filepath, TailerListener listener, long delay, boolean end, int bufferSize) {

        this.file = new File(filepath);
        this.delay = delay;
        this.end = end;


        this.buffer = new byte[bufferSize];
        this.remaind = new char[bufferSize];
//        this.remaind = CharBuffer.allocate(bufferSize);
        this.listener = listener;

        listener.init(this);

    }

    /**
     * Follows changes in the file, calling the TailerListener's handle method for each new line.
     */
    public void run() {

        reader = null;
        FileChannel fileChannel = null;
        try {
            position = 0; // position within the file
            // Open the file
            while (run && reader == null) {
                try {
                    reader = new RandomAccessFile(file, "r");
                    fileChannel = reader.getChannel();
                } catch (FileNotFoundException e) {
                    listener.fileNotFound();
                }

                if (reader == null) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        log.error("Interrupted Exception occurred");
                    }
                } else {
                    // The current position in the file
                    position = end ? file.length() : 0;
                    reader.seek(position);
                }
            }

            while (run) {

                // Check the file length to see if it was rotated
                long length = file.length();


                boolean shorterLength = length < position;
                boolean longerLength = length > position;

                if (shorterLength) {

                    // File was rotated
                    listener.fileRotated();

                    // Reopen the reader after rotation
                    try {
                        // Ensure that the old file is closed iff we re-open it successfully
                        closeQuietly(reader);
                        reader = new RandomAccessFile(file, "r");
                        fileChannel = reader.getChannel();
                        position = 0;
                    } catch (FileNotFoundException e) {
                        listener.fileNotFound();
                    }
                } else if (longerLength) {

                    // File was not rotated
                    // The file has more content than it did last time
                    long oldPosition = position;
                    if (fileChannel != null) {
                        position = readLines(reader);
                    }
                    // If position is equal to old position but wasn't supposed to be because file seems to be modified
                    // it means file has been rotated but we're not correctly reading it, so force rotation:
                    if (position == oldPosition) {
                        listener.error(new IllegalStateException("Illegal position, try rotating..."));
                        position = Long.MAX_VALUE;
                    }
                }  else {
                    try {

                        Tailer.sleep(delay);
                    } catch (Exception e) {
                        log.error("Error occurred during thread sleep",e);
                    }
                }
            }

            listener.stop();
        } catch (Exception e) {
            listener.error(e);
        } finally {
            closeQuietly(reader);
        }
    }

    /**
     * Allows the tailer to complete its current loop and return.
     */
    public void stopTail() {

        this.run = false;
    }

//    /**
//     * Read new lines.
//     *
//     * @param fileChannel The file channel of the file
//     * @return The new position after the lines have been read
//     */
//    private long readLines(FileChannel fileChannel) {
//
//        long read = 0;
//        try {
//            read = fileChannel.read(buffer);
//            buffer.flip();
//            int pos = 0;
//            int size = buffer.remaining();
//            Character charString;
//            while (pos < size) {
//                pos++;
//                charString = (char) buffer.get();
//                charBuffer.append(charString);
//                if (charString.compareTo('\n') == 0) {
//                    listener.handle(charBuffer.toString());
//                    charBuffer = new StringBuilder();
//                }
//            }
//            buffer.clear();
//            return fileChannel.position();
//        } catch (IOException e) {
//            log.error("IOException error occurred. Unable to read the file");
//        }
//        return read;
//    }

    private void closeQuietly(Closeable closeable) {

        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            log.error("unable to close the file");
        }
    }

//    public boolean isEnd() {
//
//        try {
//            return (!(buffer.hasRemaining()) && ((position + 4096) > (reader.length())));
//        } catch (IOException e) {
//            log.error("Error occurred");
//        }
//        return false;
//    }



    /**
     * Read new lines.
     *
     * @param reader The file to read
     * @return The new position after the lines have been read
     * @throws java.io.IOException if an I/O error occurs.
     */
    private long readLines(RandomAccessFile reader) throws IOException {
        int read = reader.read(buffer);
        readLinesFromBuffer(read);
        return reader.getFilePointer();
    }

    /**
     * Read lines from the buffer of given size.
     *
     * @param size The buffer size.
     * @throws java.io.IOException if an I/O error occurs.
     */
    private void readLinesFromBuffer(int size) throws IOException {
        int read = readLineFromBuffer(0, size);
        int pos = read;
        while (pos < size) {
            read = readLineFromBuffer(pos, size);
            pos += read;
        }
    }

    /**
     * Buffered read line.
     *
     * @param start the buffer starting index.
     * @param size The buffer size.
     * @return Number of bytes read.
     * @throws java.io.IOException if an I/O error occurs.
     */
    private int readLineFromBuffer(int start, int size) throws IOException {
        int read = 0;
        int current = start;
        int ch = 0;
        boolean eol = false;
        boolean seenCR = false;
        while (current < size && !eol) {
            ch = buffer[current++];
            read++;
            switch (ch) {
                case '\n':
                    eol = true;
                    remaind[remaindIndex++] = (char) ch;
                    break;
                case '\r':
                    seenCR = true;
                    break;
                default:
                    if (seenCR) {
                        remaind[remaindIndex++] = '\r';
                        seenCR = false;
                    }
                    remaind[remaindIndex++] = (char) ch; // add character, not its ascii value
            }
        }
        if (eol) {

            listener.handle(new String(remaind, 0, remaindIndex));
            remaindIndex = 0;
        }
        return read;
    }

}
