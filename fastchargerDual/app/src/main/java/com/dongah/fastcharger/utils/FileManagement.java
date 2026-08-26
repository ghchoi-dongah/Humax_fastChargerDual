package com.dongah.fastcharger.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.basefunction.GlobalVariables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FileManagement {
    private static final Logger logger = LoggerFactory.getLogger(FileManagement.class);

    public boolean stringToFileSave(String filePath, String fileName, String data, boolean append) {
        boolean result = false, check;
        try {
            File parent = new File(filePath);
            if (!parent.exists()) check = parent.mkdir();
            File file = new File(filePath + File.separator + fileName);
            if (!file.exists()) {
                check = file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, append);
            fileOutputStream.write(data.getBytes());
            if (append) fileOutputStream.write('\r');
            fileOutputStream.flush();
            fileOutputStream.close();
            result = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public String getStringFromFile(String filePath) throws Exception {
        File targetFile = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(targetFile);
        String result = convertStreamToString(fileInputStream);
        fileInputStream.close();
        return result;
    }

    private String convertStreamToString(InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    public boolean loadFileWrite(String filePath, String fileName, byte[] data, boolean append) {
        boolean result = false, check;

        try {
            File targetDir = new File(filePath);
            if (!targetDir.exists()) check = targetDir.mkdir();
            File targetFile = new File(filePath + File.separator + fileName);
            if (!targetFile.exists()) check = targetFile.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(targetFile, append);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
            result = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public boolean fileCreate(String fileName, String value) {
        boolean result = false;
        try {
            File file = new File(GlobalVariables.getRootPath() + File.separator + fileName);
            if (!file.exists()) {
                boolean check = file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.write(value.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            result = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    private File[] createFiles(String[] sFiles) {
        File[] rootingFiles = new File[sFiles.length];
        for (int i = 0; i < sFiles.length; i++) {
            rootingFiles[i] = new File(sFiles[i]);
        }
        return rootingFiles;
    }

    private boolean checkRootingFiles(File... file) {
        boolean result = false;
        for (File f : file) {
            if (f != null && f.exists() && f.isFile()) {
                result = true;
                break;
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void removeLine(File file, int lineIndex) {
        try {
            if (file.exists()) {
                List<String> lines = new LinkedList<>();
                Scanner reader = new Scanner(Files.newInputStream(file.toPath()), "UTF-8");
                while (reader.hasNextLine())
                    lines.add(reader.nextLine());
                reader.close();

                for (int i = 0; i < lineIndex; i++) {
                    lines.remove(0);
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
                for (String line : lines) {
                    if (!line.isEmpty()) {
                        writer.write(line + "\r");
                    }
                }
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void fileCopy(String srcPath, String desPath) throws IOException {
        File srcFile = new File(srcPath);
        File desFile = new File(desPath);
        FileInputStream inputStream = new FileInputStream(srcFile);
        FileOutputStream outputStream = new FileOutputStream(desFile);
        FileChannel inChannel = inputStream.getChannel();
        FileChannel outChannel = outputStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inputStream.close();
        outputStream.close();
    }

    public int countFileRows(File file) throws IOException {
        int rowCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                rowCount++;
            }
        }
        return rowCount;

    }
}
