package com.dongah.fastcharger.TECH3800;

public abstract class TLS3800Reader {

    protected TLS3800Listener tls3800Listener = null;

    public void setTls3800Listener(TLS3800Listener listener) {
        this.tls3800Listener = listener;
    }

    public abstract void onTLS3800Request(int ch, byte cmd, int cancelType);

}
