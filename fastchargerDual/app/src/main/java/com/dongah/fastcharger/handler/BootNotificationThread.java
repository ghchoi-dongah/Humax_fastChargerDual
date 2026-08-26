package com.dongah.fastcharger.handler;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.websocket.ocpp.core.BootNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatusNotificationRequest;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;
import com.dongah.fastcharger.websocket.socket.SocketState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

public class BootNotificationThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(BootNotificationThread.class);

    boolean stopped = false;
    int delayTime;
    int count = 0;

    BootNotificationRequest bootNotificationRequest;
    ChargerConfiguration chargerConfiguration;
    SocketReceiveMessage socketReceiveMessage;

    public BootNotificationThread(int delayTime) {
        this.delayTime = delayTime;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }


    public BootNotificationThread() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        super.run();
        while (!isStopped()) {
            try {
                try {
                    // Thread loop 빠져나오기 위해 1sec 단위로 시간을 체크하고 interrupt 사용
                    // thread restart 가 안되고 new instance 해야 함.
                    Thread.sleep(1000);
                    count++;

                } catch (Exception e) {
                    logger.error("thread sleep error : {}", e.getMessage());
                }
                if (count >= (delayTime)) {
                    count = 0;

                    // 미전송 데이터 유뮤 판단.
                    try {
                        String line;
                        FileReader fileReader;
                        BufferedReader bufferedReader;
                        String path = GlobalVariables.getRootPath() + File.separator + "dump" + File.separator + "dump";
                        File file = new File(path);
                        if (file.exists()) {
                            fileReader = new FileReader(file);
                            bufferedReader = new BufferedReader(fileReader);
                            while ((line = bufferedReader.readLine()) != null) {
                                socketReceiveMessage.onSend(line);
                            }
//                            boolean result = file.delete();
                            fileReader.close();
                            bufferedReader.close();
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }

                    chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
                    socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                    // firmware status
                    String path = GlobalVariables.getRootPath() + File.separator + "FirmwareStatusNotification";
                    File firmwareFile = new File(path);
                    // file == exists =>  rebooting 후, firmware status
                    if (firmwareFile.exists()) {
                        String line;
                        FileReader fileReader = new FileReader(firmwareFile);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] resultStatus = line.split("-");
                            if (Objects.equals(resultStatus[0], "SignedFirmware")) {
                                SignedFirmwareStatus signedFirmwareStatus = SignedFirmwareStatus.valueOf(resultStatus[1]);
                                SignedFirmwareStatusNotificationRequest signedFirmwareStatusNotificationRequest =
                                        new SignedFirmwareStatusNotificationRequest(signedFirmwareStatus);
                                socketReceiveMessage.onSend(100, signedFirmwareStatusNotificationRequest.getActionName(), signedFirmwareStatusNotificationRequest);
                                chargerConfiguration.setSignedFirmwareStatus(signedFirmwareStatus);
                            } else if (Objects.equals(resultStatus[0], "Firmware")) {
                                FirmwareStatus firmwareStatus = FirmwareStatus.valueOf(resultStatus[1]);
                                ;
                                FirmwareStatusNotificationRequest firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest(firmwareStatus);
                                firmwareStatusNotificationRequest.setStatus(firmwareStatus);
                                socketReceiveMessage.onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);
                                chargerConfiguration.setFirmwareStatus(firmwareStatus);
                            }
                        }
                        boolean result = firmwareFile.delete();
                        fileReader.close();
                        bufferedReader.close();
                    }

                    SocketState status = socketReceiveMessage.getSocket().getState();
                    if (Objects.equals(status, SocketState.OPEN)) {
                        bootNotificationRequest = new BootNotificationRequest(
                                chargerConfiguration.getChargerPointVendor(),
                                chargerConfiguration.getChargerPointModel());
                        bootNotificationRequest.setFirmwareVersion(GlobalVariables.getVERSION());
                        bootNotificationRequest.setImsi(GlobalVariables.IMSI);
                        bootNotificationRequest.setChargePointSerialNumber(chargerConfiguration.getChargerPointSerialNumber());
                        socketReceiveMessage.onSend(100, bootNotificationRequest.getActionName(), bootNotificationRequest);
                    }
                }
            } catch (Exception e) {
                logger.error("BootNotificationThread error : {}", e.getMessage());
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

}
