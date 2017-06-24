package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name="ProductCharacteristic", namespace="http://www.tmforum.org")
@XmlType(name="ProductCharacteristic", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ProductCharacteristic implements Serializable {

    private static final long serialVersionUID = 3077606313766009585L;
    private String name;
    private String value;

    public ProductCharacteristic() {
    }

    public ProductCharacteristic(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
