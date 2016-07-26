package org.meveo.admin.action.order;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;

public class OfferItemInfo implements Serializable {

    private static final long serialVersionUID = 2813002300477029504L;

    private String code;

    private String description;

    private List<ProductCharacteristic> characteristics;

    public OfferItemInfo(String code, String description, List<ProductCharacteristic> characteristicsList) {
        super();
        this.code = code;
        this.description = description;
        this.characteristics = characteristicsList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<ProductCharacteristic> characteristics) {
        this.characteristics = characteristics;
    }

    public String getCodeAndDescription() {
        if (StringUtils.isBlank(description)) {
            return code;
        } else {
            return code + " - " + description;
        }
    }
}
