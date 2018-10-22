/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;

@Entity
@Table(name = "billing_one_shot_charge_inst")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_one_shot_chrg_inst_seq"), })
public class OneShotChargeInstance extends ChargeInstance {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subs_serv_inst_id")
    private ServiceInstance subscriptionServiceInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_serv_inst_id")
    private ServiceInstance terminationServiceInstance;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    protected BigDecimal quantity = BigDecimal.ONE;

    public OneShotChargeInstance() {

    }

    public OneShotChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, OneShotChargeTemplate chargeTemplate, ServiceInstance serviceInstance,
            InstanceStatusEnum status) {

        super(amountWithoutTax, amountWithTax, chargeTemplate, serviceInstance, status);

        this.quantity = serviceInstance.getQuantity() == null ? BigDecimal.ONE : serviceInstance.getQuantity();

        if (chargeTemplate.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.TERMINATION) {
            setTerminationServiceInstance(serviceInstance);
        } else {
            setSubscriptionServiceInstance(serviceInstance);
        }
        if (serviceInstance.getSubscription().getSeller() != null) {
            this.seller = serviceInstance.getSubscription().getSeller();
        }
    }

    public OneShotChargeInstance(String description, Date chargeDate, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal quantity, String orderNumber,
            Subscription subscription, OneShotChargeTemplate chargeTemplate) {

        this.code = chargeTemplate.getCode();
        if (StringUtils.isBlank(description)) {
            if (chargeTemplate.getDescriptionI18n() != null) {
                String languageCode = subscription.getUserAccount().getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
                if (!StringUtils.isBlank(chargeTemplate.getDescriptionI18n().get(languageCode))) {
                    this.description = chargeTemplate.getDescriptionI18n().get(languageCode);
                }
            }
            if (StringUtils.isBlank(this.description)) {
                this.description = chargeTemplate.getDescription();
            }
        } else {
            this.description = description;
        }
        this.chargeDate = chargeDate;
        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
        this.userAccount = subscription.getUserAccount();
        this.subscription = subscription;
        this.country = subscription.getUserAccount().getBillingAccount().getTradingCountry();
        this.currency = subscription.getUserAccount().getBillingAccount().getCustomerAccount().getTradingCurrency();
        this.chargeTemplate = chargeTemplate;
        this.quantity = quantity == null ? BigDecimal.ONE : quantity;
        this.orderNumber = orderNumber;
        this.status = InstanceStatusEnum.ACTIVE;
        if (subscription.getSeller() != null) {
            this.seller = subscription.getSeller();
        }
    }

    public ServiceInstance getSubscriptionServiceInstance() {
        return subscriptionServiceInstance;
    }

    public void setSubscriptionServiceInstance(ServiceInstance subscriptionServiceInstance) {
        this.subscriptionServiceInstance = subscriptionServiceInstance;
    }

    public ServiceInstance getTerminationServiceInstance() {
        return terminationServiceInstance;
    }

    public void setTerminationServiceInstance(ServiceInstance terminationServiceInstance) {
        this.terminationServiceInstance = terminationServiceInstance;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}