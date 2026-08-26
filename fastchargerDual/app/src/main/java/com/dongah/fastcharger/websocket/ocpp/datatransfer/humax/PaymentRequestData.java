package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

public class PaymentRequestData implements Validatable {

    private int connectorId;
    private String resultCode;              //결과 코드
    private String resultMsg;               //결과 메시지
    private String mid;                     //가맹 번호
    private String tid;                     //PG거래번호(PG거래 부분취소시 필요한 거래일련번호)
    private String oid;                     //주문 번호
    private int amt;                        //거래 금액
    private String authCode;                //PG/VAN 승인코드(승인번호)
    private String authDate;                //PG/VAN 승인일자
    private String buyerName;               //구매자명
    private String cardNum;                 //마스킹된 결제카드번호
    private String fnCd;                    //결제카드사코드
    private String fnName;                  //결제카드사명

    public PaymentRequestData() {
    }

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
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

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public int getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAuthDate() {
        return authDate;
    }

    public void setAuthDate(String authDate) {
        this.authDate = authDate;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getFnCd() {
        return fnCd;
    }

    public void setFnCd(String fnCd) {
        this.fnCd = fnCd;
    }

    public String getFnName() {
        return fnName;
    }

    public void setFnName(String fnName) {
        this.fnName = fnName;
    }

    @Override
    public boolean validate() {
        return resultCode != null && tid != null && amt != 0
                && authCode != null && authDate != null;
    }
}
