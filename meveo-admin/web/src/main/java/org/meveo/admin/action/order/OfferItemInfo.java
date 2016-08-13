package org.meveo.admin.action.order;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;

public class OfferItemInfo implements Serializable {

    private static final long serialVersionUID = 2813002300477029504L;

    private BusinessEntity template;

    private Map<String, Object> characteristics;

    private boolean main = false;
    private boolean selected = false;

    public OfferItemInfo(BusinessEntity template, Map<String, Object> characteristics, boolean main, boolean selected) {
        super();
        this.main = main;
        this.template = template;
        this.characteristics = characteristics;
        this.selected = selected;
    }

    public BusinessEntity getTemplate() {
        return template;
    }

    public void setTemplate(BusinessEntity template) {
        this.template = template;
    }

    public Map<String, Object> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Map<String, Object> characteristics) {
        this.characteristics = characteristics;
    }

    public String getCodeAndDescription() {
        if (StringUtils.isBlank(template.getDescription())) {
            return template.getCode();
        } else {
            return template.getCode() + " - " + template.getDescription();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isMain() {
        return main;
    }

    public boolean isOffer() {
        return template instanceof OfferTemplate;
    }

    public boolean isService() {
        return template instanceof ServiceTemplate;
    }

    public boolean isProduct() {
        return template instanceof ProductTemplate;
    }

}