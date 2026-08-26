package com.dongah.fastcharger.basefunction;

import com.dongah.fastcharger.websocket.ocpp.core.SampledValue;
import com.dongah.fastcharger.websocket.ocpp.core.ValueFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class MeterValuesAlignedData {
    private static final Logger logger = LoggerFactory.getLogger(MeterValuesAlignedData.class);

    ChargingCurrentData chargingCurrentData;
    SampledValue sampledValue;
    DecimalFormat powerFormatter = new DecimalFormat("######0.00");


    public MeterValuesAlignedData() {
        //2. 유효 입력 전력량
        sampledValue = new SampledValue();
        sampledValue.setFormat(ValueFormat.Raw);
        sampledValue.setMeasurand("Energy.Active.Import.Register");
        sampledValue.setUnit("kWh");
        sampledValue.setValue("0");
    }

    public SampledValue getMeterValuesAlignedData(ChargingCurrentData chargingCurrentData) {
        this.chargingCurrentData = chargingCurrentData;
        updateSampleValue();
        return sampledValue;
    }


    public void updateSampleValue() {
        try {
            //충전기 현재의 값을 갖고 온다.
            sampledValue.setValue(powerFormatter.format(chargingCurrentData.getPowerMeter() * 0.01));              //유효 입력 전력량 (kWh)
        } catch (Exception e) {
            logger.error("Update SampledValues setting error : {}", e.getMessage());
        }
    }

}
