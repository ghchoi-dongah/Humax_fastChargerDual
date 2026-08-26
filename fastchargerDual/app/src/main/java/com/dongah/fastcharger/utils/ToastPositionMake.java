package com.dongah.fastcharger.utils;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToastPositionMake {

    public static final Logger logger = LoggerFactory.getLogger(ToastPositionMake.class);
    Activity activity;

    public ToastPositionMake(Activity activity) {
        setActivity(activity);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void onShowToast(int ch, String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
                    switch (ch) {
                        case 0:
                            toast.setGravity(Gravity.TOP | Gravity.LEFT, 150, 450);
                            break;
                        case 1:
                            toast.setGravity(Gravity.TOP | Gravity.LEFT, 650, 450);
                            break;
                    }
                    toast.show();
                } catch (Exception e) {
                    logger.error("ToastPositionMake error : {}", e.getMessage());
                }
            }
        });
    }

}
