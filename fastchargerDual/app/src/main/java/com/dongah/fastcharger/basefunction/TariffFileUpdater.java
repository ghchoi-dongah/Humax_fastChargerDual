package com.dongah.fastcharger.basefunction;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TariffFileUpdater {

    private static final Logger logger = LoggerFactory.getLogger(TariffFileUpdater.class);


    private static final Object FILE_LOCK = new Object();
    boolean isCheck = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateTariffFile(File file, JSONObject inputData) throws Exception {

        synchronized (FILE_LOCK) {

            JSONArray rootArray;
            if (file.exists()) {
                StringBuilder jsonBuilder = new StringBuilder();

                try (FileInputStream fis = new FileInputStream(file);
                     InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                     BufferedReader br = new BufferedReader(isr)) {

                    String line;
                    while ((line = br.readLine()) != null) {
                        jsonBuilder.append(line);
                    }
                }

                String json = jsonBuilder.toString().trim();
                rootArray = json.isEmpty() ? new JSONArray() : new JSONArray(json);

            } else {
                // 파일이 없으면 새로 생성
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    isCheck = parent.mkdirs();
                }
                isCheck = file.createNewFile();
                rootArray = new JSONArray();
            }

            String targetUserType = inputData.getString("userType");
            boolean updated = false;
            for (int i = 0; i < rootArray.length(); i++) {
                JSONObject row = rootArray.getJSONObject(i);

                if (targetUserType.equals(row.optString("userType"))) {

                    if (inputData.has("HmChargingLimitFee")) {
                        row.put("HmChargingLimitFee", inputData.getString("HmChargingLimitFee")
                        );
                    }

                    if (inputData.has("tariff")) {
                        row.put("tariff", inputData.getJSONArray("tariff")
                        );
                    }
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                rootArray.put(inputData);
            }
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(osw)) {

                bw.write(rootArray.toString(2));
                bw.flush();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getPrice(String userType) throws Exception {

        File file = new File(
                GlobalVariables.getRootPath()
                        + File.separator
                        + GlobalVariables.UNIT_FILE_NAME
        );


        if (file.exists()) {
            StringBuilder jsonBuilder = new StringBuilder();

            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {

                String line;
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                String json = jsonBuilder.toString();

                JSONArray rootArray = new JSONArray(json);
                ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
                ZonedDateTime now = zonedDateTimeConvert.doGetCurrentTime();

                for (int i = 0; i < rootArray.length(); i++) {
                    JSONObject userRow = rootArray.getJSONObject(i);
                    if (!userType.equals(userRow.getString("userType"))) continue;

                    JSONArray tariffArray = userRow.getJSONArray("tariff");
                    for (int j = 0; j < tariffArray.length(); j++) {
                        JSONObject tariff = tariffArray.getJSONObject(j);

                        ZonedDateTime startAt = ZonedDateTime.parse(
                                tariff.getString("startAt"),
                                DateTimeFormatter.ISO_DATE_TIME
                        );
                        ZonedDateTime endAt = ZonedDateTime.parse(
                                tariff.getString("endAt"),
                                DateTimeFormatter.ISO_DATE_TIME
                        );

                        if ((now.isEqual(startAt) || now.isAfter(startAt)) &&
                                (now.isBefore(endAt) || now.isEqual(endAt))) {

                            return tariff.getString("price");
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("FILE", "요금 파일 처리 오류", e);
            }
        }

        return "0";
    }
}
