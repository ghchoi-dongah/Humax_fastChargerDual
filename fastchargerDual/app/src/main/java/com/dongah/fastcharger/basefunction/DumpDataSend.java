package com.dongah.fastcharger.basefunction;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.websocket.ocpp.common.JSONCommunicator;
import com.dongah.fastcharger.websocket.ocpp.common.model.Message;
import com.dongah.fastcharger.websocket.socket.SendHashMapObject;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

public class DumpDataSend extends JSONCommunicator {


    private static final Logger logger = LoggerFactory.getLogger(DumpDataSend.class);


    Message message = null;
    String uuid = null;
    int connectorId = 0;

    public void onDumpSend() {

        // 미전송 데이터 유뮤 판단.
        try {
            String line, actionName;
            JSONObject jsonObject;
            FileReader fileReader;
            BufferedReader bufferedReader;
            String path = GlobalVariables.getRootPath() + File.separator + "dump" + File.separator + "dump";
            File file = new File(path);
            if (file.exists()) {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    message = parse(line);
                    int resultType = message.getResultType();
                    actionName = message.getAction();

                    if (Objects.equals(actionName, "StartTransaction")) {
                        jsonObject = new JSONObject(message.getPayload().toString());
                        uuid = message.getId();
                        connectorId = jsonObject.getInt("connectorId");

                        SendHashMapObject sendHashMapObject = new SendHashMapObject();
                        sendHashMapObject.setConnectorId(connectorId);
                        sendHashMapObject.setActionName(actionName);
                        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().setNewHashMapUuid(uuid, sendHashMapObject);


                        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().onSend(line);
                        //


                    } else if (Objects.equals(actionName, "StopTransaction")) {


                        jsonObject = new JSONObject(message.getPayload().toString());

                        int transactionId = GlobalVariables.dumpTransactionId;

                        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().onSend(line);
                    } else {
                        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().onSend(line);
                    }


                }
                boolean result = file.delete();
                fileReader.close();
                bufferedReader.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

}
