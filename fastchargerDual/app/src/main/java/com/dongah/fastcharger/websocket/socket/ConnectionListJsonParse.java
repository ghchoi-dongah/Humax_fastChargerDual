package com.dongah.fastcharger.websocket.socket;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConnectionListJsonParse {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionListJsonParse.class);

    public List<Connector> parseConnectorList(String jsonString) {
        List<Connector> connectorList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray connectors = jsonObject.getJSONArray("connectorList");
            ChargerConfiguration chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
            for (int i = 0; i < connectors.length(); i++) {
                JSONObject item = connectors.getJSONObject(i);
                int connectorId = item.getInt("connectorId");
                int searchKey = item.getInt("searchKey");
                String qrUrl = item.getString("qrUrl");

                Connector connector = new Connector(connectorId, searchKey, qrUrl);
                connectorList.add(connector);

                //charger ID
                chargerConfiguration.setChargerId(String.valueOf(searchKey));
            }
        } catch (Exception e){
            logger.error(" parseConnectorList error : {}", e.getMessage());
        }
        return connectorList;
    }
}
