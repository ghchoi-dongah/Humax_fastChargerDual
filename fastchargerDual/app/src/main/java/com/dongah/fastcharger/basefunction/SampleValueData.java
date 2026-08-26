package com.dongah.fastcharger.basefunction;

import com.dongah.fastcharger.controlboard.ControlBoardUtil;
import com.dongah.fastcharger.websocket.ocpp.core.SampledValue;
import com.dongah.fastcharger.websocket.ocpp.core.ValueFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class SampleValueData {

    private static final Logger logger = LoggerFactory.getLogger(SampleValueData.class);

    ChargingCurrentData chargingCurrentData;
    ControlBoardUtil controlBoardUtil;
    SampledValue[] sampledValues = new SampledValue[7];
    DecimalFormat powerFormatter = new DecimalFormat("######0.00");
    DecimalFormat voltageFormatter = new DecimalFormat("######0.00");

    public SampleValueData() {
        //1. 유효입력 전력
        sampledValues[0] = new SampledValue();
        sampledValues[0].setFormat(ValueFormat.Raw);
        sampledValues[0].setMeasurand("Power.Active.Import");
        sampledValues[0].setUnit("W");
        sampledValues[0].setValue("0");
        //2. 유효입력 전력량
        sampledValues[1] = new SampledValue();
        sampledValues[1].setFormat(ValueFormat.Raw);
        sampledValues[1].setMeasurand("Energy.Active.Import.Register");
        sampledValues[1].setUnit("Wh");
        sampledValues[1].setValue("0");
        //3.출력전류
        sampledValues[2] = new SampledValue();
        sampledValues[2].setFormat(ValueFormat.Raw);
        sampledValues[2].setMeasurand("Current.Export");
        sampledValues[2].setUnit("A");
        sampledValues[2].setValue("0");
        //4. 주파수
        sampledValues[3] = new SampledValue();
        sampledValues[3].setFormat(ValueFormat.Raw);
        sampledValues[3].setMeasurand("Frequency");
        sampledValues[3].setUnit("var");
        sampledValues[3].setValue("0");
        //5.SoC
        sampledValues[4] = new SampledValue();
        sampledValues[4].setFormat(ValueFormat.Raw);
        sampledValues[4].setMeasurand("SoC");
        sampledValues[4].setUnit("Percent");
        sampledValues[4].setValue("0");
        //6.EV에 제공하는 최대 전력
        sampledValues[5] = new SampledValue();
        sampledValues[5].setFormat(ValueFormat.Raw);
        sampledValues[5].setMeasurand("Power.Offered");
        sampledValues[5].setUnit("W");
        sampledValues[5].setValue("0");
        //7.전압
        sampledValues[6] = new SampledValue();
        sampledValues[6].setFormat(ValueFormat.Raw);
        sampledValues[6].setMeasurand("Voltage");
        sampledValues[6].setUnit("V");
        sampledValues[6].setValue("0");

        initSampledValues();
    }

    public SampledValue[] getSampledValues(ChargingCurrentData chargingCurrentData) {
        this.chargingCurrentData = chargingCurrentData;
        controlBoardUtil = new ControlBoardUtil();
        updateSampleValues();
        return sampledValues;
    }

    public void initSampledValues() {
        try {
            sampledValues[0].setValue("0");             //1. 유효입력 전력
            sampledValues[1].setValue("0");             //2. 유효입력 전력량
            sampledValues[2].setValue("0");             //3. 출력전류
            sampledValues[3].setValue("0");             //4. 주파수
            sampledValues[4].setValue("0");             //5. SoC
            sampledValues[5].setValue("0");             //6. EV에 제공하는 최대 전력
            sampledValues[6].setValue("0");             //7. 전압
        } catch (Exception e) {
            logger.error("initSampledValues setting error....: {} ", e.getMessage());
        }
    }

    public void updateSampleValues() {
        try {
            // Meter values 콜할때 charge point current value (충전중일때만)
            //충전기 현재의 값을 갖고 온다.
            sampledValues[0].setValue(powerFormatter.format(chargingCurrentData.getOutPutCurrent() * 0.001 * chargingCurrentData.getOutPutVoltage() * 0.01));   //1. 유효입력 전력 (w)
            sampledValues[1].setValue(powerFormatter.format(chargingCurrentData.getPowerMeter() * 10));                //2. 유효입력 전력량 (W)
            sampledValues[2].setValue(powerFormatter.format(chargingCurrentData.getOutPutCurrent() * 0.001));          //3. 출력전류 (A)
            sampledValues[3].setValue(powerFormatter.format(60));                                                       //4. 주파수
            sampledValues[4].setValue(String.valueOf(chargingCurrentData.getSoc()));                                            //5. SoC (%)
            sampledValues[5].setValue("0");                                                                                     //6. EV에 제공하는 최대 전력(W)
            sampledValues[6].setValue(voltageFormatter.format(chargingCurrentData.getOutPutVoltage() * 0.01));         //7. 전압(V)
        } catch (Exception e) {
            logger.error("Update SampledValues setting error : {}", e.getMessage());
        }
    }


}
