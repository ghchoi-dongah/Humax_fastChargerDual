package com.dongah.fastcharger.basefunction;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FTPHelper {

    private static final Logger logger = LoggerFactory.getLogger(FTPHelper.class);

    private static final int PORT = 21;                     // 기본 포트
    FTPClient ftpClient;


    public void ftpConnect(String server, String userName, String pass) {
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(server, PORT);
            ftpClient.login(userName, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        } catch (Exception e) {
            logger.error(" froConnect error {} ", e.getMessage());
        }
    }

    public boolean uploadFile(String localFilePath, String remoteFilePath) {
        try {
            File file = new File(localFilePath);
            try (FileInputStream inputStream = new FileInputStream(file)) {
                return ftpClient.storeFile(remoteFilePath, inputStream);
            }
        } catch (Exception e) {
            logger.error(" uploadFile {} ", e.getMessage());
            return false;
        } finally {
            disconnect(ftpClient);
        }
    }


    public boolean downloadFile(String remoteFilePath, String localFilePath) {
        try {
            try (FileOutputStream outputStream = new FileOutputStream(localFilePath)) {
                return ftpClient.retrieveFile(remoteFilePath, outputStream);
            }
        } catch (IOException ex) {
            logger.error(" downloadFile error :  {} ", ex.getMessage());
            return false;
        } finally {
            disconnect(ftpClient);
        }
    }


    private void disconnect(FTPClient ftpClient) {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error(" disconnect error :  {} ", e.getMessage());
            }
        }
    }

}
