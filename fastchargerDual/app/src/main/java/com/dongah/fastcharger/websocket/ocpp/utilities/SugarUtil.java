package com.dongah.fastcharger.websocket.ocpp.utilities;

import org.w3c.dom.Document;

import java.io.StringWriter;
import java.time.ZonedDateTime;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SugarUtil {

    public static String zonedDateTimeToString(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return "";
        return zonedDateTime.toString();
    }

    public static String docToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting to String", e);
        }
    }
}