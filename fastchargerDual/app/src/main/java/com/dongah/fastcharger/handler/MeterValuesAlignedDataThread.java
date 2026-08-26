package com.dongah.fastcharger.handler;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.websocket.ocpp.core.MeterValue;
import com.dongah.fastcharger.websocket.ocpp.core.MeterValuesRequest;
import com.dongah.fastcharger.websocket.ocpp.core.SampledValue;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;
import com.dongah.fastcharger.websocket.socket.SocketState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Objects;

public class MeterValuesAlignedDataThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(MeterValuesAlignedDataThread.class);

    boolean stopped = false;
    int delayTime;
    int connectorId;

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

    public MeterValuesAlignedDataThread(int connectorId, int delayTime) {
        super();
        this.connectorId = connectorId;
        this.delayTime = delayTime;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        super.run();
        int count = 0;
        while (!isStopped()) {
            try {
                sleep(1000);
                count++;
                if (count >= (getDelayTime())) {
                    count = 0;
                    SocketReceiveMessage socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                    ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
                    if (Objects.equals(socketReceiveMessage.getSocket().getState(), SocketState.OPEN)) {
                        ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(getConnectorId() - 1);
                        SampledValue[] sampledValues = chargingCurrentData.getSampleValueData().getSampledValues(chargingCurrentData);
                        MeterValue[] meterValues = {new MeterValue(timestamp, sampledValues)};
                        MeterValuesRequest meterValuesRequest = new MeterValuesRequest(chargingCurrentData.getConnectorId());
                        meterValuesRequest.setMeterValue(meterValues);
                        meterValuesRequest.setTransactionId(chargingCurrentData.getTransactionId());
                        socketReceiveMessage.onSend(getConnectorId(), meterValuesRequest.getActionName(), meterValuesRequest);
                    }
                }
            } catch (Exception e) {
                logger.error(" thread error : {}", e.getMessage());
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        setStopped(true);
    }
}
