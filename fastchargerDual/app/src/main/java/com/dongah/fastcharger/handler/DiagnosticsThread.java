package com.dongah.fastcharger.handler;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.utils.FileManagement;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class DiagnosticsThread extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(DiagnosticsThread.class);

    private static final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/Download";
    private static final String FILE_NAME = "diagnostics.dongah";
    boolean stopped;
    long delayTime;


    FileManagement fileManagement;
    ZonedDateTimeConvert zonedDateTimeConvert;
    DecimalFormat powerFormatter = new DecimalFormat("######0.00");


    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }


    public DiagnosticsThread(long delayTime) {
        this.delayTime = delayTime;
        fileManagement = new FileManagement();
        zonedDateTimeConvert = new ZonedDateTimeConvert();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        super.run();
        int count = 0;
        while (!stopped) {
            try {
                Thread.sleep(1000);
                count++;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            if (count > delayTime) {
                try {
                    count = 0;
                    String startTime = zonedDateTimeConvert.doGetUtcDatetimeAsString();
                    String powerMeter = powerFormatter.format(((MainActivity) MainActivity.mContext).getControlBoard().getRxData(0).getPowerMeter() * 0.01);
                    JSONArray data = insertData(startTime, powerMeter);
                    JSONObject obj = new JSONObject();
                    obj.put("diagnostics", data);
                    fileManagement.stringToFileSave(FILE_PATH, FILE_NAME, obj.toString(), true);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    public JSONArray insertData(String startTime, String powerMeter) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("startTime", startTime);
            jsonObject.put("Energy.Active.Export.Register", powerMeter);
            return jsonArray.put(jsonObject);
        } catch (Exception e) {
            logger.error("insertData() : {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
