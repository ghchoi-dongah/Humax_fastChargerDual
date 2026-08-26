package com.dongah.fastcharger.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedModel extends ViewModel {

    private final MutableLiveData<String[]> mutableLiveData = new MutableLiveData<>();

    public LiveData<String[]> getLiveData() {
        return mutableLiveData;
    }

    public void setMutableLiveData(String[] str) {
        mutableLiveData.setValue(str);
    }
}