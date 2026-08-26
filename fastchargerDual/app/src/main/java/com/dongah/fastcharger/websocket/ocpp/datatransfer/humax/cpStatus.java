package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

public enum cpStatus {

    Unknown(0),                             //알수 없음
    CommunicationFail(1),                   //통신 이상
    WaitingForCharging(2),                  //충전 대기
    Charging(3),                            //충전중
    OutOfOperation(4),                      //운연 중지
    UnderMaintenance(5),                    //점검중
    Reserved(6),                            //예약중
    NotDisplay(7),                          //표시 안함
    ChargingComplete(8),                    //충전 완료
    StatusUnknown(9);                       //상태 미확인

    private final int value;

    cpStatus (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
