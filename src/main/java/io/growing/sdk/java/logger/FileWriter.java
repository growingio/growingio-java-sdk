package io.growing.sdk.java.logger;

import io.growing.sdk.java.utils.ConfigUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileWriter {
    private static final String loggerFilePath;
    private static final int loggerFileMaxSize;
    private static final int loggerFileMaxDays;

    static {
        loggerFilePath = ConfigUtils.getStringValue("logger.file.path", "/logs").trim();
        loggerFileMaxSize = ConfigUtils.getIntValue("logger.file.max_size", 10);
        loggerFileMaxDays = ConfigUtils.getIntValue("logger.file.max_days", -1);
    }

    private FileWriter() {
    }

    private static class SingleInstance {
        private static final FileWriter INSTANCE = new FileWriter();
    }

    public static FileWriter getInstance() {
        return FileWriter.SingleInstance.INSTANCE;
    }

    private FileChannel fileChannel;
    private Date lastDate;
    private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public synchronized void log(String message) {
        try {
            if (lastDate == null || intervalDays(lastDate.getTime(), new Date().getTime()) > 0) {
                createFileIfNotExist();
            }

            if (fileChannel != null && fileChannel.isOpen()) {
                // 超过单个文件大小限制后直接关闭channel，不再写入日志
                if (fileChannel.size() > ((long) loggerFileMaxSize * 1024L * 1024L)) {
                    if (fileChannel != null) {
                        fileChannel.close();
                        fileChannel = null;
                    }
                    return;
                }

                ByteBuffer buffer = ByteBuffer.wrap((logDateFormat.format(new Date()) + ": " + message + "\n").getBytes());
                fileChannel.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFileIfNotExist() {
        try {
            String currentDate = fileDateFormat.format(new Date());
            lastDate = fileDateFormat.parse(currentDate);
            String filePath = loggerFilePath + File.separator + currentDate + ".log";
            File file = new File(filePath);
            if (!createMissingParentDirectories(file)) {
                return;
            }
            if (file.createNewFile()) {
                // 每次创建新的文件时，关闭上一个fileChannel，并且删除过期的日志文件
                if (fileChannel != null) {
                    fileChannel.close();
                    fileChannel = null;
                }
                deleteExpiredFiles();
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(randomAccessFile.length());
            fileChannel = randomAccessFile.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteExpiredFiles() {
        try {
            if (loggerFileMaxDays > 0) {
                File logsDir = new File(loggerFilePath);
                File[] logFiles = logsDir.listFiles();
                if (logFiles == null || logFiles.length == 0) {
                    return;
                }
                for (File logFile : logFiles) {
                    String fileName = logFile.getName();
                    if (logFile.isFile() && fileName.endsWith(".log") && isExpiredFile(fileName)) {
                        logFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean isExpiredFile(String fileName) {
        try {
            String fileTime = fileName.substring(0, fileName.lastIndexOf("."));
            Date fileDate = fileDateFormat.parse(fileTime);
            Date currentDate = new Date();
            long days = intervalDays(fileDate.getTime(), currentDate.getTime());
            if (days > loggerFileMaxDays) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private long intervalDays(long lastTime, long currentTime) {
        return (currentTime - lastTime) / (1000 * 60 * 60 * 24);
    }

    private boolean createMissingParentDirectories(File file) {
        File parent = file.getParentFile();
        if (parent == null) {
            return true;
        }

        parent.mkdirs();
        return parent.exists();
    }
}
