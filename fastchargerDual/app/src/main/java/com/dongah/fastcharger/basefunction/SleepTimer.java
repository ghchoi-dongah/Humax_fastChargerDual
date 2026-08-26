package com.dongah.fastcharger.basefunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepTimer {

    private static final Logger logger = LoggerFactory.getLogger(SleepTimer.class);

    public SleepTimer() {
    }

    public void sleep(long timeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - currentTimeMillis >= timeMillis) {
                break;
            }
        }
    }
}
