package com.dongah.fastcharger.controlboard;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlBoardUtil {

    private static final Logger logger = LoggerFactory.getLogger(ControlBoardUtil.class);


    public ControlBoardUtil() {
    }


    /**
     * firmware version format
     *
     * @param version 120
     * @return 1.2.0
     */
    @Nullable
    public String parseVersion(int version) {
        try {
            StringBuilder result = new StringBuilder();
            String value = String.valueOf(version);
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result.append(value.charAt(i)).append(i < len - 1 ? "." : "");
            }
            return result.toString();
        } catch (Exception e) {
            logger.error(" parseVersion {}", e.getMessage());
        }
        return null;
    }

    /**
     * charging remain time format
     *
     * @param remainTime sec
     * @return HH:mm:SS
     */
    @NonNull
    @SuppressLint("DefaultLocale")
    public String getRemainTime(long remainTime) {
        String result = "00:00:00";
        try {
            int hour = (int) remainTime / 3600;
            int minute = (int) (remainTime % 3600) / 60;
            int second = (int) remainTime % 60;
            return String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        } catch (Exception e) {
            logger.error("getRemainTime error : {}", e.getMessage());
        }
        return result;
    }


}
