package com.dongah.fastcharger.basefunction;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.websocket.ocpp.common.OccurenceConstraintException;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatusNotificationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HTTPHelper {
    private static final Logger logger = LoggerFactory.getLogger(HTTPHelper.class);

    private static final String TARGET_FILE_PATH = Environment.getExternalStorageDirectory().toString() +  File.separator + "Download";

    private String location;
    private String urlPath;
    private String fileName;
    private final File destinationFile ;

    FirmwareStatusNotificationRequest firmwareStatusNotificationRequest;

    public HTTPHelper(String location) {
        this.location = location;
        String[] fullPath = urlParsing(this.location);
        assert fullPath != null;
        setUrlPath(fullPath[0]);
        setFileName(fullPath[1]);
        destinationFile = new File(TARGET_FILE_PATH + File.separator + getFileName());
        firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Single<String> download() {

        return Single.fromCallable(() -> {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
//                URL url = new URL("https://wqa.humaxcharger.com/firmwaredownload/10876/DEVS050S12.apk");
                URL url = new URL(getLocation());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestProperty("Accept", "*/*");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    //connection fail
//                    throw new IOException("Server returned HTTP " + responseCode);
                    return "ConnectFail";
                }


                try {
                    input = connection.getInputStream();
                    //output = Files.newOutputStream(destinationFile.toPath());
                    output = new FileOutputStream( new File(TARGET_FILE_PATH + File.separator + getFileName()));

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    output.flush();
                    ((MainActivity) MainActivity.mContext).getChargerConfiguration().setFirmwareStatus(FirmwareStatus.Downloaded);
                    firmwareStatusNotificationRequest.setStatus(FirmwareStatus.Downloaded);
                    try {
                        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                .onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);
                    } catch (OccurenceConstraintException e) {
                        throw new RuntimeException(e);
                    }
                    return "success";
                } catch (Exception w) {
                    logger.error(" download fail : {}", w.getMessage());
                    ((MainActivity) MainActivity.mContext).getChargerConfiguration().setFirmwareStatus(FirmwareStatus.DownloadFailed);
                    firmwareStatusNotificationRequest.setStatus(FirmwareStatus.DownloadFailed);
                    try {
                        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                .onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);
                    } catch (OccurenceConstraintException e) {
                        throw new RuntimeException(e);
                    }
                    return "DownloadFail";
                }
            } finally {
                if (input != null) input.close();
                if (output != null) output.close();
                if (connection != null) connection.disconnect();
            }
        }).subscribeOn(Schedulers.io()); // 백그라운드에서 실행

    }


    private String[] urlParsing(String value) {
        try {
            URL url = new URL(value);
            String host = url.getHost();               // www-dev.humaxcharger.com
            String path = url.getPath();

            int lastSlash = path.lastIndexOf("/");
            String directoryPath = path.substring(0, lastSlash); // /firmwaredownload/10812
            String fileName = path.substring(lastSlash + 1);     // 파일명

            // 도메인과 디렉토리 경로를 합치기
            String fullPath = "https://" + host + directoryPath;
            return new String[]{fullPath, fileName};
        } catch (Exception e) {
            logger.error(" urlParsing error : {}", e.getMessage());
        }
        return null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
