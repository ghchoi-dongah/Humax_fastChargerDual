package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Single sampled value in {@link MeterValue}. Each value an be accompanied by optional fields.
 * 1. format : [raw] 숫자    [SignedData] 2진수
 */
@XmlRootElement
@XmlType(propOrder = {"value", "context", "format", "measurand", "phase", "location", "unit"})
public class SampledValue implements Validatable {
    private static final Logger logger = LoggerFactory.getLogger(SampledValue.class);

    private String value;
    private String context;
    private ValueFormat format;
    private String measurand;
    private String phase;
    private Location location;
    private String unit;

//    /**
//     * @deprecated use {@link #SampledValue(String)} to be sure to set required fields
//     */
//    @Deprecated
//    public SampledValue() {
//    }

    /**
     * Handle required fields.
     *
     * @param value String, the value, see {@link #setValue(String)}
     */
//    public SampledValue(String value) {
//        try {
//            setValue(value);
//        } catch (PropertyConstraintException e) {
//            logger.error("constructor of SampledValue",e);
//        }
//    }
    public SampledValue() {
    }


    public String getValue() {
        return value;
    }

    /**
     * Required. Value as a {@code Raw} (decimal) number or {@code SignedData}. Field Type us String
     * to allow for digitally signed data readings. Decimal numeric values are also acceptable to
     * allow for measurands such as Temperature and Current.
     *
     * @param value String, the value
     */
    @XmlElement
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Type of detail value : start, end or sample.
     *
     * @return enum value for context
     */
    public String getContext() {
        return context;
    }

    /**
     * Optional. Type of detail value : start, end or sample. Default = {@code Sample.Periodic}
     * <P> Enum value with accepted values : {@code Interruption.Begin}, {@code Interruption.End},
     * {@code Other}, {@code Sample.Clock}, {@code Sample.Periodic}, {@code Transaction.Begin},
     * {@code Transaction.End}, {@code Trigger} </P>
     *
     * @param context String, see description for accepted values.
     */
    @XmlElement
    public void setContext(String context) {
        if (context != null && !isValidContext(context)) {
            throw new PropertyConstraintException(context, "contextis not properly defined");
        }
        this.context = context;
    }

    private boolean isValidContext(String context) {
        String[] readingContext = {
                "Interruption.Begin",
                "Interruption.End",
                "Other",
                "Sample.Clock",
                "Sample.Periodic",
                "Transaction.Begin",
                "Transaction.End",
                "Trigger"
        };
        return ModelUtil.isAmong(context, readingContext);
    }


    public ValueFormat getFormat() {
        return format == null ? ValueFormat.Raw : format;
    }

    /**
     * Optional. Raw or signed data. Default = {@code Raw}
     *
     * @param format the {@link ValueFormat}
     */
    public void setFormat(ValueFormat format) {
        this.format = format;
    }

    /**
     * Raw or signed data
     *
     * @return the {@link ValueFormat}
     */
    @Deprecated
    public ValueFormat objFormat() {
        return format == null ? ValueFormat.Raw : format;
    }

    /**
     * Type of measurement
     *
     * @return enum value of measurand
     */
    public String getMeasurand() {
        return measurand == null ? "Energy.Active.Import.Register" : measurand;
    }

    @XmlElement
    public void setMeasurand(String measurand) {
        if (measurand != null && !isValidMeasurand(measurand)) {
            throw new PropertyConstraintException(measurand, "measurand value is not properly defined");
        }
        this.measurand = measurand;
    }

    private boolean isValidMeasurand(String measurand) {
        String[] measurandValues = {
                "Current.Export",                           //출력전류
                "Current.Import",                           //압력전류
                "Current.Offered",                          //??
                "Energy.Active.Export.Register",            //유효 출력 전력량
                "Energy.Active.Import.Register",            //유효 입력 전력량
                "Energy.Reactive.Export.Register",          //무효 출력 전력량
                "Energy.Reactive.Import.Register",          //무효 입력 전력량
                "Energy.Active.Export.Interval",            //???
                "Energy.Active.Import.Interval",            //???
                "Energy.Reactive.Export.Interval",          //??
                "Energy.Reactive.Import.Interval",          //??
                "Frequency",                                //주파수
                "Power.Active.Export",                      //유효 출력 전력
                "Power.Active.Import",                      //유효 입력 전력
                "Power.Factor",                             //역률
                "Power.Offered",                            //??
                "Power.Reactive.Export",                    //무효 출력 전력
                "Power.Reactive.Import",                    //무효 입력 전력
                "RPM",                                      //??    
                "SoC",                                      //??
                "Temperature",                              //온도
                "Voltage"                                   //전압
        };
        return ModelUtil.isAmong(measurand, measurandValues);
    }

    public String getPhase() {
        return phase;
    }

    @XmlElement
    public void setPhase(String phase) {
        if (phase != null && isValidPhase(phase)) {
            throw new PropertyConstraintException(phase, "phase is not properly defined");
        }
        this.phase = phase;
    }

    private boolean isValidPhase(String phase) {
        return ModelUtil.isAmong(phase, "L1", "L2", "L3", "N", "L1-N", "L2-N", "L3-N", "L1-L2", "L2-L3", "L3-L1");
    }


    public Location getLocation() {
        return location == null ? Location.Outlet : location;
    }

    @XmlElement
    public void setLocation(Location location) {
        this.location = location;
    }

    public Location objLocation() {
        return location == null ? Location.Outlet : location;
    }

    public String getUnit() {
        return unit == null && getMeasurand().startsWith("Energy") ? "Wh" : unit;
    }

    @XmlElement
    public void setUnit(String unit) {
        this.unit = unit;
    }

    private boolean isValidUnit(String unit) {
        String[] unitOfMeasure = {
                "Wh",
                "kWh",
                "varh",
                "kvarh",
                "W",
                "kW",
                "VA",
                "kVA",
                "var",
                "kvar",
                "A",
                "V",
                "Celsius",
                "Fahrenheit",
                "K",
                "Percent"
        };
        return ModelUtil.isAmong(unit, unitOfMeasure);
    }


    @Override
    public boolean validate() {
        return this.value != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampledValue that = (SampledValue) o;
        return Objects.equals(value, that.value)
                && Objects.equals(context, that.context)
                && format == that.format
                && Objects.equals(measurand, that.measurand)
                && Objects.equals(phase, that.phase)
                && location == that.location
                && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, context, format, measurand, phase, location, unit);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .add("context", context)
                .add("format", format)
                .add("measurand", measurand)
                .add("phase", phase)
                .add("location", location)
                .add("unit", unit)
                .add("isValid", validate())
                .toString();
    }
}
