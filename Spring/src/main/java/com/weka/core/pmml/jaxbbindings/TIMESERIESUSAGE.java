//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.20 at 12:48:21 PM GMT 
//


package com.weka.core.pmml.jaxbbindings;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for TIMESERIES-USAGE.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TIMESERIES-USAGE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="original"/>
 *     &lt;enumeration value="logical"/>
 *     &lt;enumeration value="prediction"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum TIMESERIESUSAGE {

    @XmlEnumValue("logical")
    LOGICAL("logical"),
    @XmlEnumValue("original")
    ORIGINAL("original"),
    @XmlEnumValue("prediction")
    PREDICTION("prediction");
    private final String value;

    TIMESERIESUSAGE(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TIMESERIESUSAGE fromValue(String v) {
        for (TIMESERIESUSAGE c: TIMESERIESUSAGE.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
