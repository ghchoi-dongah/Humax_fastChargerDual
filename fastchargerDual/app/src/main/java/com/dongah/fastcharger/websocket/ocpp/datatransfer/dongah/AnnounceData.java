package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

import java.time.ZonedDateTime;
import java.util.Objects;

public class AnnounceData implements Validatable {


    private ZonedDateTime startTimeStamp;
    private ZonedDateTime endTimeStamp;
    private String context;

    public AnnounceData() {
    }

    public ZonedDateTime getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(ZonedDateTime startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public ZonedDateTime getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(ZonedDateTime endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnounceData that = (AnnounceData) o;
        return startTimeStamp == that.startTimeStamp && endTimeStamp == that.endTimeStamp
                && context == that.context;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTimeStamp, endTimeStamp, context);
    }
}
