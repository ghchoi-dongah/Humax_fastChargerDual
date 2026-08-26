package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;


public class PaymentCancelData implements Validatable {


    private int connectorId;
    private String idTag;                       //결제 요청시 서버에서 회신한 인증키값(Payment.idTag)
    private String resultCode;                  //결과 코드
    private String resultMsg;                   //결과 메시지
    private String tid;                         //PG거래번호(PG거래 부분취소시 필요한 거래일련번호)
    private int cancelAmt;                      //취소 금액
    private String cancelDate;                  //취소 일자

    public PaymentCancelData() {
    }


    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getCancelAmt() {
        return cancelAmt;
    }

    public void setCancelAmt(int cancelAmt) {
        this.cancelAmt = cancelAmt;
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }


    @Override
    public boolean validate() {
        return resultCode != null && tid != null && cancelAmt != 0
                && cancelDate != null;
    }
}
