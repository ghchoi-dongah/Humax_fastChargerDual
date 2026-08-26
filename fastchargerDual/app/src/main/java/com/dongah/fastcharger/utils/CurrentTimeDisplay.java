package com.dongah.fastcharger.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.dongah.fastcharger.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTimeDisplay {

    private static final Logger logger = LoggerFactory.getLogger(CurrentTimeDisplay.class);

    Activity activity;
    TextView txtColon, txtHour, txtMinute;
    Animation animation;
    int counter = 0;

    Handler mHandler;
    Runnable mRunnable;

    public CurrentTimeDisplay(Activity activity, TextView txtColon, TextView txtHour, TextView txtMinute) {
        this.activity = activity;
        this.txtColon = txtColon;
        this.txtHour = txtHour;
        this.txtMinute = txtMinute;
    }

    public void TwinkleTxtStart() {
        ((MainActivity) MainActivity.mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    animation = new AlphaAnimation(1, 0);
                    animation.setDuration(500);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(Animation.REVERSE);
                    txtColon.startAnimation(animation);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }

    public void TwinkleTxtStop() {
        txtColon.clearAnimation();
    }

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatterHour = new SimpleDateFormat("aa hh");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatterMinute = new SimpleDateFormat("mm");

    public void TimeDisplayStart() {
        counter = 0;
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Date jusTime = new Date();
                    String mHour = formatterHour.format(jusTime);
                    String mMinute = formatterMinute.format(jusTime);
                    txtHour.setText(mHour);
                    txtMinute.setText(mMinute);
                    mHandler.postDelayed(mRunnable, 1000);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    public void TimeDisplayStop() {
        try {
            mHandler.removeMessages(0);
            mHandler.removeCallbacks(mRunnable);
            mHandler.removeCallbacksAndMessages(null);
            if (mHandler != null) mHandler = null;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
