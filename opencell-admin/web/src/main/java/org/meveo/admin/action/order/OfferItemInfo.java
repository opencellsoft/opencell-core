/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldValues;

public class OfferItemInfo implements Serializable {

    private static final long serialVersionUID = 2813002300477029504L;

    private BusinessEntity template;

    private Map<OrderProductCharacteristicEnum, Object> characteristics;

    private boolean main = false;
    private boolean selected = false;
    private boolean mandatory = false;

    private BusinessCFEntity entityForCFValues;

    public OfferItemInfo(BusinessEntity template, Map<OrderProductCharacteristicEnum, Object> characteristics, boolean main, boolean selected, boolean mandatory,
                         BusinessCFEntity entityForCFValues){
        this(template, characteristics,  main,  selected,  mandatory, entityForCFValues, null);
    }

    /**
     * Offer ordering item information
     *  @param template Offering template (offerTemplate or productTemplate) or its sub components (serviceTemplate or productTemplate)
     * @param characteristics A map of characteristics to apply to item being ordered
     * @param main Is it a main offering template - in case of OfferTemlate, it has subcomponents: serviceTemplates and productTemplates)
     * @param selected Is item ordered - when creating a new order, all subcomponents of offer are shown, but only those that are desired to be ordered should be shown as selected
     * @param mandatory Is item mandatory for order
     * @param entityForCFValues An entity corresponding to what offering template will translate to. OfferTemplate&gt;Subscription, serviceTemplate&gt;serviceInstance,
     * @param customFieldValues
     */
    public OfferItemInfo(BusinessEntity template, Map<OrderProductCharacteristicEnum, Object> characteristics, boolean main, boolean selected, boolean mandatory,
                         BusinessCFEntity entityForCFValues, CustomFieldValues customFieldValues) {
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
            ((Subscription) this.entityForCFValues).setCode((String) characteristics.get(OrderProductCharacteristicEnum.SUBSCRIPTION_CODE));
            ((Subscription) this.entityForCFValues).setCfValues(customFieldValues);

        } else if (template instanceof ProductTemplate) {
            this.entityForCFValues = new ProductInstance();
            ((ProductInstance) this.entityForCFValues).setProductTemplate((ProductTemplate) template);
            ((ProductInstance) this.entityForCFValues).setCode((String) characteristics.get(OrderProductCharacteristicEnum.PRODUCT_INSTANCE_CODE));
            ((ProductInstance) this.entityForCFValues).setCfValues(customFieldValues);

        } else if (template instanceof ServiceTemplate) {
            this.entityForCFValues = new ServiceInstance();
            ((ServiceInstance) this.entityForCFValues).setCode(template.getCode());
            ((ServiceInstance) this.entityForCFValues).setDescription(template.getDescription());
            ((ServiceInstance) this.entityForCFValues).setServiceTemplate((ServiceTemplate) template);
            ((ServiceInstance) this.entityForCFValues).setCfValues(customFieldValues);
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