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

package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.meveo.model.BaseEntity;

/**
 * Service template to charge template mapping
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 * @param <T> Charge template type
 */

@MappedSuperclass
public abstract class ServiceChargeTemplate<T extends ChargeTemplate> extends BaseEntity {

    private static final long serialVersionUID = -1872859127097329926L;

    /**
     * Service template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_template_id")
    protected ServiceTemplate serviceTemplate;

    /**
     * Charge template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_template_id")
    protected T chargeTemplate;

    /**
     * Counter template
     */
    @Transient
    private CounterTemplate counterTemplate;

    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    public T getChargeTemplate() {
        return chargeTemplate;
    }

    public void setChargeTemplate(T chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
    }

    public CounterTemplate getCounterTemplate() {
        return counterTemplate;
    }

    public void setCounterTemplate(CounterTemplate counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    public abstract List<WalletTemplate> getWalletTemplates();

    public abstract void setWalletTemplates(List<WalletTemplate> walletTemplates);

    /**
     * Gets Counters template for a charge template.
     *
     * @return list of counters template
     */
    public abstract List<CounterTemplate> getAccumulatorCounterTemplates();

    /**
     * Sets counters template.
     *
     * @param counterTemplates counters template
     */
    public abstract void setAccumulatorCounterTemplates(List<CounterTemplate> counterTemplates);
}