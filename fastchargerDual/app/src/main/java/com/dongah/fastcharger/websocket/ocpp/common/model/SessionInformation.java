package com.dongah.fastcharger.websocket.ocpp.common.model;

import java.net.InetSocketAddress;

public class SessionInformation {
    private String identifier;
    private InetSocketAddress address;
    private String SOAPtoURL;

    public String getIdentifier() {
        return identifier;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getSOAPtoURL() {
        return SOAPtoURL;
    }

    public static class Builder {
        private String identifier;
        private InetSocketAddress address;
        private String SOAPtoURL;

        public Builder Identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder InternetAddress(InetSocketAddress address) {
            this.address = address;
            return this;
        }

        public SessionInformation build() {
            SessionInformation sessionInformation = new SessionInformation();
            sessionInformation.identifier = this.identifier;
            sessionInformation.address = this.address;
            sessionInformation.SOAPtoURL = this.SOAPtoURL;
            return sessionInformation;
        }

        public Builder SOAPtoURL(String toUrl) {
            this.SOAPtoURL = toUrl;
            return this;
        }

    }

}
