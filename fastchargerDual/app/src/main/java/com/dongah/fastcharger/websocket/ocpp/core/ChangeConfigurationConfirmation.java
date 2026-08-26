package com.dongah.fastcharger.websocket.ocpp.core;

import android.app.usage.ConfigurationStats;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Returned from Charge Point to Central System
 */
@XmlRootElement(name = "changeConfigurationResponse")
public class ChangeConfigurationConfirmation implements Confirmation {

    private ConfigurationStatus status;
    private static final String ACTION_NAME = "ChangeConfiguration";

    @Deprecated
    public ChangeConfigurationConfirmation() {
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Handle required fields
     *
     * @param status the {@link ConfigurationStats}, see {@link #setStatus(ConfigurationStatus)}
     */
    public ChangeConfigurationConfirmation(ConfigurationStatus status) {
        setStatus(status);
    }

    public ConfigurationStatus getStatus() {
        return status;
    }

    /**
     * Required. Returns whether configuration change has been accepted.
     *
     * @param status the {@link ConfigurationStatus}
     */
    @XmlElement
    public void setStatus(ConfigurationStatus status) {
        this.status = status;
    }

    /**
     * Returns whether configuration change has been accepted.
     *
     * @return the {@link ConfigurationStatus}.
     */
    @Deprecated
    public ConfigurationStatus objStatus() {
        return status;
    }


    @Override
    public boolean validate() {
        return status != null;
    }
}
