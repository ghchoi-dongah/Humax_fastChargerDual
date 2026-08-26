package com.dongah.fastcharger.handler;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CustomStatusNotificationThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(CustomStatusNotificationThread.class);

    boolean stopped = false;
    int delayTime;
    int connectorId;

    public CustomStatusNotificationThread(int connectorId, int delayTime) {
        this.connectorId = connectorId;
        this.delayTime = delayTime;
    }


    @Override
    public void run() {
        super.run();
        int count = 0;
        while (!stopped) {
            try {
                Thread.sleep(1000);
                count++;
            } catch (Exception e) {
                logger.error(" CustomStatusNotification run thread  error : {}", e.getMessage());
            }

            UiSeq uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(connectorId - 1).getUiSeq();
            if (Objects.equals(uiSeq, UiSeq.CHARGING)) {
                if (delayTime != Integer.parseInt(GlobalVariables.getHmChargingTranTerm())) count = 0;
                delayTime = Integer.parseInt(GlobalVariables.getHmChargingTranTerm());
            } else {
                if (delayTime != Integer.parseInt(GlobalVariables.getHmPreparingTranTerm())) count = 0;
                delayTime = Integer.parseInt(GlobalVariables.getHmPreparingTranTerm());
            }

            try {
                if (count >= (delayTime)) {
                    //CustomStatusNoti
                    count = 0;
                    ProcessHandler processHandler = ((MainActivity) MainActivity.mContext).getProcessHandler();
                    SocketReceiveMessage socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_CUSTOM_STATUS_NOTIFICATION,
                            connectorId,
                            0,
                            null,
                            null,
                            null,
                            false));
                }
            } catch (Exception e) {
                logger.error(" CustomStatusNotification error : {}", e.getMessage());
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public boolean isInterrupted() {
        return super.isInterrupted();
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

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }
}
