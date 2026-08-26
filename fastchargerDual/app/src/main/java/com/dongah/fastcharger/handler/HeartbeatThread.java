package com.dongah.fastcharger.handler;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.ClassUiProcess;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.utils.LogDataSave;
import com.dongah.fastcharger.websocket.ocpp.common.OccurenceConstraintException;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointStatus;
import com.dongah.fastcharger.websocket.ocpp.core.HeartbeatRequest;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;
import com.dongah.fastcharger.websocket.socket.SocketState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

public class HeartbeatThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatThread.class);

    boolean stopped = false;
    int delayTime;
    int count = 0;
    boolean sendCheck = false;

    HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
    SocketReceiveMessage socketReceiveMessage;
    LogDataSave logDataSave = new LogDataSave("log");

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HeartbeatThread(int delayTime) {
        this.delayTime = delayTime;
        socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
        try {
            socketReceiveMessage.onSend(heartbeatRequest.getActionName(), heartbeatRequest);
        } catch (OccurenceConstraintException e) {
            throw new RuntimeException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        super.run();
        while (!isStopped()) {
            try {
                try {
                    Thread.sleep(1000);
                    count++;
                } catch (Exception e) {
                    logger.error("thread sleep error : {} ", e.getMessage());
                }
                if (count >= (delayTime)) {
                    count = 0;
                    sendCheck = false;
                    socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                    ClassUiProcess[] classUiProcesses = ((MainActivity) MainActivity.mContext).getClassUiProcess();
                    for (int i = 0; i < GlobalVariables.maxChannel; i++) {
                        ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(i);
                        if (!Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Charging)) {
                            sendCheck = true;
                        }
                    }
                    if (sendCheck)
                        socketReceiveMessage.onSend(100, heartbeatRequest.getActionName(), heartbeatRequest);
                    //30일 이상 로그 데이터 삭제
                    logDataSave.removeLogData();
                    // 미전송 데이터
                    SocketState socketState = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().getSocket().getState();
                    if (Objects.equals(socketState, SocketState.OPEN)) {
                        boolean chk = onDumpData(socketReceiveMessage);
                    }
                }
            } catch (Exception e) {
                logger.error("HeartbeatThread error : {} ", e.getMessage());
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    private boolean onDumpData(SocketReceiveMessage socketReceiveMessage) {
        try {
            String line;
            FileReader fileReader;
            BufferedReader bufferedReader;
            String filePath = GlobalVariables.getRootPath() + File.separator + "dump" + File.separator + "dump";
            File file = new File(filePath);
            if (file.exists()) {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    socketReceiveMessage.onSend(line);
                }
//                boolean result = file.delete();
                boolean result = true;
                fileReader.close();
                bufferedReader.close();
                return result;
            }
        } catch (Exception e) {
            logger.error("onDumpData error : {}", e.getMessage());
        }
        return false;
    }


}
