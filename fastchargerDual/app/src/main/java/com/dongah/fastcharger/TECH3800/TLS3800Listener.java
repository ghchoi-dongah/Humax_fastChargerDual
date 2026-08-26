package com.dongah.fastcharger.TECH3800;

import java.util.HashMap;

public interface TLS3800Listener {
    void onTLS3800ResponseCallBack(int ch, TLS3800ResponseType type, HashMap<String, String> returnValue);
}
