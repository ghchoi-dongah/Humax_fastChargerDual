package com.dongah.fastcharger.basefunction;

import java.util.Objects;

public class NotifyPropertyChange {
    private boolean oldValue = false;
    public String alarmCode = "";

    public NotifyPropertyChange(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public boolean isResultCompare() {
        return ResultCompare;
    }

    public void setResultCompare(boolean resultCompare) {
        ResultCompare = resultCompare;
    }

    public boolean ResultCompare;

    public boolean isSetBool() {
        return oldValue;
    }

    public void setSetBool(boolean value) {
        if (!Objects.equals(ResultCompare, value)) {
            ResultCompare = value;
            oldValue = value;
        }
    }

    public boolean SetBool;
}
