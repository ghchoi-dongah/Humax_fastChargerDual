package com.dongah.fastcharger.websocket.ocpp.firmware;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "getDiagnosticsResponse")
public class GetDiagnosticsConfirmation implements Confirmation {

    private String fileName;

    public String getFileName() {
        return fileName;
    }


    /**
     * Optional. This contains the name of the file with diagnostics information that will be uploaded.
     * This fields is not present when no diagnostics information is available.
     * 진단정보가 업로드될 파일이름, 진단정보가 없으면 필드 사용안함.
     *
     * @param fileName string, file name
     */
    @XmlElement
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public GetDiagnosticsConfirmation(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetDiagnosticsConfirmation that = (GetDiagnosticsConfirmation) o;
        return Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fileName", fileName)
                .add("isValid", validate())
                .toString();
    }
}
