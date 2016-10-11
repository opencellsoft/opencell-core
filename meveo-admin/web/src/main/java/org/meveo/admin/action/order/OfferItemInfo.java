package org.meveo.admin.action.order;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.order.OrderProductCharacteristicEnum;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;

public class OfferItemInfo implements Serializable {

    private static final long serialVersionUID = 2813002300477029504L;

    private BusinessEntity template;

    private Map<OrderProductCharacteristicEnum, Object> characteristics;

    private boolean main = false;
    private boolean selected = false;
    private boolean mandatory = false;

    private BusinessCFEntity entityForCFValues;

    /**
     * Offer ordering item information
     * 
     * @param template Offering template (offerTemplate or productTemplate) or its sub components (serviceTemplate or productTemplate)
     * @param characteristics A map of characteristics to apply to item being ordered
     * @param main Is it a main offering template - in case of OfferTemlate, it has subcomponents: serviceTemplates and productTemplates)
     * @param selected Is item ordered - when creating a new order, all subcomponents of offer are shown, but only those that are desired to be ordered should be shown as selected
     * @param mandatory Is item mandatory for order
     * @param entityForCFValues An entity corresponding to what offering template will translate to. OfferTemplate>Subscription, serviceTemplate>serviceInstance,
     *        productTemplate>productInstance
     */
    public OfferItemInfo(BusinessEntity template, Map<OrderProductCharacteristicEnum, Object> characteristics, boolean main, boolean selected, boolean mandatory,
            BusinessCFEntity entityForCFValues) {
        super();
        this.main = main;
        this.template = template;
        this.characteristics = characteristics;
        this.selected = selected;
        this.mandatory = mandatory;

        // If not provided, supply an empty entity corresponding to what offering template will translate to. OfferTemplate>Subscription, serviceTemplate>serviceInstance,
        // productTemplate>productInstance
        if (entityForCFValues != null) {
            this.entityForCFValues = entityForCFValues;
        } else if (template instanceof OfferTemplate) {
            this.entityForCFValues = new Subscription();
            ((Subscription) this.entityForCFValues).setOffer((OfferTemplate) template);

        } else if (template instanceof ProductTemplate) {
            this.entityForCFValues = new ProductInstance();
            ((ProductInstance) this.entityForCFValues).setProductTemplate((ProductTemplate) template);

        } else if (template instanceof ServiceTemplate) {
            this.entityForCFValues = new ServiceInstance();
            ((ServiceInstance) this.entityForCFValues).setCode(template.getCode());
            ((ServiceInstance) this.entityForCFValues).setDescription(template.getDescription());
            ((ServiceInstance) this.entityForCFValues).setServiceTemplate((ServiceTemplate) template);
        }
    }

    public BusinessEntity getTemplate() {
        return template;
    }

    public void setTemplate(BusinessEntity template) {
        this.template = template;
    }

    public Map<OrderProductCharacteristicEnum, Object> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Map<OrderProductCharacteristicEnum, Object> characteristics) {
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

    public boolean isMandatory() {
        return mandatory;
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

    public BusinessCFEntity getEntityForCFValues() {
        return entityForCFValues;
    }
}