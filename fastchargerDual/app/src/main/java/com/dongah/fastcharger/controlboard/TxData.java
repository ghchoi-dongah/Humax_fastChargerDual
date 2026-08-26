package com.dongah.fastcharger.controlboard;

import com.dongah.fastcharger.utils.BitUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxData {

    private static final Logger logger = LoggerFactory.getLogger(TxData.class);
    private static final int TX_DATA_CNT = 10;

    public short[] rawData = new short[TX_DATA_CNT];

    public boolean IsStart = false;             //3 bit
    public boolean IsStop = false;              //4 bit
    public boolean IsReset = false;             //7 bit

    public short uiSequence = 1;                //202 ==> 1: 대기 2: 충전  3: 종료
    public short chargerPointMode = 0;          //204 ==> 충전(일반:0 , 테스트:1 ,   IO 테스트: 2)모드
    public short testDualSingle = 0;            //205 ==> 테스트 (0: 왼쪽, 1: 오른쪽, 2: 듀얼)모드
    public short testDrVoltage = 0;             //206 ==>  테스트 모드 전압 지령(1채널)
    public short testDrCurrent = 0;             //207 ==>  테스트 모드 전류 지령(1채널)

    public boolean IsRelay1 = false;            //208[0] RY1 제어
    public boolean IsRelay2 = false;            //208[1] RY2 제어
    public boolean IsRelay3 = false;            //208[2] RY3 제어
    public boolean IsRelay4 = false;            //208[3] RY4 제어
    public boolean IsRelay5 = false;            //208[4] RY5 제어
    public boolean IsRelay6 = false;            //208[5] RY6 제어
    public boolean IsMC1 = false;               //208[6] MC1
    public boolean IsMC2 = false;               //208[7] MC2
    public boolean IsFan1 = false;              //208[8] fan
    public boolean IsOut1 = false;              //208[9] out1
    public boolean IsOut2 = false;              //208[10] out2
    public boolean IsOut3 = false;              //208[11] out3
    public boolean IsOut4 = false;              //208[12] out4
    public boolean IsOut5 = false;              //208[13] out5
    public boolean IsOut6 = false;              //208[14] out6
    public boolean IsOut7 = false;              //208[15] out7

    public short outPowerLimit = 0;             //209 출략 제한


    public void setInit() {
        try {
            IsStart = IsStop = IsReset = false;
            uiSequence = 1;
            chargerPointMode = testDualSingle = testDrVoltage = testDrCurrent = 0;
            IsRelay1 = IsRelay2 = IsRelay3 = false;
            IsRelay4 = IsRelay5 = IsRelay6 = false;
            IsMC1 = IsMC2 = IsFan1 = false;
            IsOut1 = IsOut2 = IsOut3 = IsOut4 = false;
            IsOut5 = IsOut6 = IsOut7 = false;
            outPowerLimit = 0;
        } catch (Exception e) {
            logger.error(" tx data init error : {}", e.getMessage());
        }
    }

    public short[] Encode() {
        try {
            rawData[0] = BitUtilities.setBit(rawData[0], 3, IsStart);
            rawData[0] = BitUtilities.setBit(rawData[0], 4, IsStop);
            rawData[0] = BitUtilities.setBit(rawData[0], 7, IsReset);
            rawData[1] = 0;
            rawData[2] = uiSequence;
            rawData[3] = 0;
            rawData[4] = chargerPointMode;
            rawData[5] = testDualSingle;
            rawData[6] = testDrVoltage;
            rawData[7] = testDrCurrent;
            rawData[8] = BitUtilities.setBit(rawData[8], 0, IsRelay1);
            rawData[8] = BitUtilities.setBit(rawData[8], 1, IsRelay2);
            rawData[8] = BitUtilities.setBit(rawData[8], 2, IsRelay3);
            rawData[8] = BitUtilities.setBit(rawData[8], 3, IsRelay4);
            rawData[8] = BitUtilities.setBit(rawData[8], 4, IsRelay5);
            rawData[8] = BitUtilities.setBit(rawData[8], 5, IsRelay6);
            rawData[8] = BitUtilities.setBit(rawData[8], 6, IsMC1);
            rawData[8] = BitUtilities.setBit(rawData[8], 7, IsMC2);
            rawData[8] = BitUtilities.setBit(rawData[8], 8, IsFan1);
            rawData[8] = BitUtilities.setBit(rawData[8], 9, IsOut1);
            rawData[8] = BitUtilities.setBit(rawData[8], 10, IsOut2);
            rawData[8] = BitUtilities.setBit(rawData[8], 11, IsOut3);
            rawData[8] = BitUtilities.setBit(rawData[8], 12, IsOut4);
            rawData[8] = BitUtilities.setBit(rawData[8], 13, IsOut5);
            rawData[8] = BitUtilities.setBit(rawData[8], 14, IsOut6);
            rawData[8] = BitUtilities.setBit(rawData[8], 15, IsOut7);
            rawData[9] = outPowerLimit;
            return rawData;
        } catch (Exception e) {
            logger.error(" tx data encode error : {}", e.getMessage());
        }
        return null;
    }

    public boolean isStart() {
        return IsStart;
    }

    public void setStart(boolean start) {
        IsStart = start;
    }

    public boolean isStop() {
        return IsStop;
    }

    public void setStop(boolean stop) {
        IsStop = stop;
    }

    public boolean isReset() {
        return IsReset;
    }

    public void setReset(boolean reset) {
        IsReset = reset;
    }

    public short getUiSequence() {
        return uiSequence;
    }

    public void setUiSequence(short uiSequence) {
        this.uiSequence = uiSequence;
    }

    public short getChargerPointMode() {
        return chargerPointMode;
    }

    public void setChargerPointMode(short chargerPointMode) {
        this.chargerPointMode = chargerPointMode;
    }

    public short getTestDualSingle() {
        return testDualSingle;
    }

    public void setTestDualSingle(short testDualSingle) {
        this.testDualSingle = testDualSingle;
    }

    public short getTestDrVoltage() {
        return testDrVoltage;
    }

    public void setTestDrVoltage(short testDrVoltage) {
        this.testDrVoltage = testDrVoltage;
    }

    public short getTestDrCurrent() {
        return testDrCurrent;
    }

    public void setTestDrCurrent(short testDrCurrent) {
        this.testDrCurrent = testDrCurrent;
    }

    public boolean isRelay1() {
        return IsRelay1;
    }

    public void setRelay1(boolean relay1) {
        IsRelay1 = relay1;
    }

    public boolean isRelay2() {
        return IsRelay2;
    }

    public void setRelay2(boolean relay2) {
        IsRelay2 = relay2;
    }

    public boolean isRelay3() {
        return IsRelay3;
    }

    public void setRelay3(boolean relay3) {
        IsRelay3 = relay3;
    }

    public boolean isRelay4() {
        return IsRelay4;
    }

    public void setRelay4(boolean relay4) {
        IsRelay4 = relay4;
    }

    public boolean isRelay5() {
        return IsRelay5;
    }

    public void setRelay5(boolean relay5) {
        IsRelay5 = relay5;
    }

    public boolean isRelay6() {
        return IsRelay6;
    }

    public void setRelay6(boolean relay6) {
        IsRelay6 = relay6;
    }

    public boolean isMC1() {
        return IsMC1;
    }

    public void setMC1(boolean MC1) {
        IsMC1 = MC1;
    }

    public boolean isMC2() {
        return IsMC2;
    }

    public void setMC2(boolean MC2) {
        IsMC2 = MC2;
    }

    public boolean isFan1() {
        return IsFan1;
    }

    public void setFan1(boolean fan1) {
        IsFan1 = fan1;
    }

    public boolean isOut1() {
        return IsOut1;
    }

    public void setOut1(boolean out1) {
        IsOut1 = out1;
    }

    public boolean isOut2() {
        return IsOut2;
    }

    public void setOut2(boolean out2) {
        IsOut2 = out2;
    }

    public boolean isOut3() {
        return IsOut3;
    }

    public void setOut3(boolean out3) {
        IsOut3 = out3;
    }

    public boolean isOut4() {
        return IsOut4;
    }

    public void setOut4(boolean out4) {
        IsOut4 = out4;
    }

    public boolean isOut5() {
        return IsOut5;
    }

    public void setOut5(boolean out5) {
        IsOut5 = out5;
    }

    public boolean isOut6() {
        return IsOut6;
    }

    public void setOut6(boolean out6) {
        IsOut6 = out6;
    }

    public boolean isOut7() {
        return IsOut7;
    }

    public void setOut7(boolean out7) {
        IsOut7 = out7;
    }

    public short getOutPowerLimit() {
        return outPowerLimit;
    }

    public void setOutPowerLimit(short outPowerLimit) {
        this.outPowerLimit = outPowerLimit;
    }
}
