package com.dongah.fastcharger.basefunction;

import androidx.fragment.app.Fragment;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FragmentCurrent {

    private static final Logger logger = LoggerFactory.getLogger(FragmentCurrent.class);

    public FragmentCurrent() {
    }

    public Fragment getCurrentFragment(int ch) {
        return ((MainActivity) MainActivity.mContext).getSupportFragmentManager().findFragmentById(ch == 0 ? R.id.ch0 : R.id.ch1);
    }

    public Fragment getCurrentFragment() {
        return ((MainActivity) MainActivity.mContext).getSupportFragmentManager().findFragmentById(R.id.fullScreen);
    }

}
