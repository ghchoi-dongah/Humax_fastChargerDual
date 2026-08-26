package com.dongah.fastcharger.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.Vector;

public class SftpUtil {
    private static final Logger logger = LoggerFactory.getLogger(SftpUtil.class);

    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;


    /**
     * version & charger UI Download (업로드 위치 : /app/upload/dongah/apk)
     *
     * @param host     sftp server ip (211.44.234.120)
     * @param userName sftp user name (root)
     * @param passWord sftp password (dev!1q2w3e)
     */
    public void sftpConnect(String host, String userName, String passWord) {
        JSch jSch = new JSch();
        try {
            //connection private key 또는 password 필요
//            FileManagement fileManagement = new FileManagement();
//            String privateKey = fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "privateKey");
//            jSch.addIdentity(privateKey);

            session = jSch.getSession(userName, host);
            session.setPassword(passWord);

            //property setting
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no"); // 접속 시 hostkeychecking 여부
            session.setConfig(config);
            session.connect();
            //sftp connect
            channel = session.openChannel("sftp");
            channel.connect();
            logger.trace("sftp connect success...");
            channelSftp = (ChannelSftp) channel;
        } catch (Exception e) {
            logger.error("jSch sftpConnection error : {}", e.getMessage());
        }
    }

    /**
     * remote mkdir
     *
     * @param dir       current directory
     * @param mkdirName mkdir
     */
    public void mkdir(String dir, String mkdirName) {
        try {
            if (!this.exists(dir + File.separator + mkdirName)) {
                channelSftp.cd(dir);
                channelSftp.mkdir(mkdirName);
            }
        } catch (Exception e) {
            logger.error("jSch mkdir error : {}", e.getMessage());
        }
    }

    /**
     * file exists
     *
     * @param path file path
     * @return found : true , not found : false
     */
    public boolean exists(String path) {
        Vector res = null;
        try {
            res = channelSftp.ls(path);
        } catch (Exception e) {
            logger.error("jSch file exists error: {}", e.getMessage());
        }
        return res != null && !res.isEmpty();
    }

    /**
     * sftp upload
     *
     * @param dir  upload target directory
     * @param file upload file name
     * @return success : true, fail : false
     */
    public boolean upload(String dir, File file) {
        boolean isUpload = false;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            channelSftp.cd(dir);
            channelSftp.put(in, file.getName());
            // upload file exists check
            if (this.exists(dir + File.separator + file.getName())) {
                isUpload = true;
            }
        } catch (Exception e) {
            logger.error("jSch file upload error : {}", e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception e) {
                logger.error("jSch file upload finally error : {}", e.getMessage());
            }
        }
        return isUpload;
    }

    /**
     * sftp download
     *
     * @param dir              target directory
     * @param downloadFileName target file name
     * @param path             local save path + file name
     */
    public boolean download(String dir, String downloadFileName, String path) {
        boolean result = false;
        long fileSize = 0, downloadSize = 0;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            channelSftp.cd(dir);
            in = channelSftp.get(downloadFileName);
            fileSize = channelSftp.lstat(downloadFileName).getSize();

        } catch (Exception e) {
            logger.error("jSch file download error : " + e.getMessage());
            return result;
        }
        // file save
        try {
            out = new FileOutputStream(new File(path));
            int i;
            byte[] data = new byte[2048];
            while ((i = in.read(data)) != -1) {
                out.write(data, 0, i);
                downloadSize += i;
            }

            //success check
            result = Objects.equals(fileSize, downloadSize);

        } catch (Exception e) {
            logger.error("jSch file download save error : {}", e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    public String getRemoteVersion(String dir, String downloadFileName) {
        String remoteVersion = "none";
        InputStream in = null;
        try {
            try {
                channelSftp.cd(dir);
                in = channelSftp.get(downloadFileName);
            } catch (Exception e) {
                logger.error("jSch file download error : {}", e.getMessage());
            }
            // file read
            int count;
            byte[] data = new byte[10];
            while ((count = in.read(data)) != -1) {
                remoteVersion = new String(data);
                break;
            }
            return remoteVersion;
        } catch (Exception e) {
            logger.error(" download error : {} " ,  e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return "none";
    }

    /**
     * sftp disconnect
     */
    public void sftpDisconnect() {
        try {
            channelSftp.quit();
            session.disconnect();
        } catch (Exception e) {
            logger.error("jSch sftpDisconnect error : {}", e.getMessage());
        }
    }
}
