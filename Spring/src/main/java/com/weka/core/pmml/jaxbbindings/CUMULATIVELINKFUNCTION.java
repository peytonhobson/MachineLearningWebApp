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
 * <p>Java class for CUMULATIVE-LINK-FUNCTION.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CUMULATIVE-LINK-FUNCTION">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="logit"/>
 *     &lt;enumeration value="probit"/>
 *     &lt;enumeration value="cloglog"/>
 *     &lt;enumeration value="loglog"/>
 *     &lt;enumeration value="cauchit"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum CUMULATIVELINKFUNCTION {

    @XmlEnumValue("cauchit")
    CAUCHIT("cauchit"),
    @XmlEnumValue("cloglog")
    CLOGLOG("cloglog"),
    @XmlEnumValue("logit")
    LOGIT("logit"),
    @XmlEnumValue("loglog")
    LOGLOG("loglog"),
    @XmlEnumValue("probit")
    PROBIT("probit");
    private final String value;

    CUMULATIVELINKFUNCTION(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CUMULATIVELINKFUNCTION fromValue(String v) {
        for (CUMULATIVELINKFUNCTION c: CUMULATIVELINKFUNCTION.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}