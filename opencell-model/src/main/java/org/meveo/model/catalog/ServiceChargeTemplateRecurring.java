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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

/**
 * Service template to recurring charge template mapping
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */

@Entity
@Cacheable
@ExportIdentifier({ "chargeTemplate.code", "serviceTemplate.code" })
@Table(name = "cat_serv_rec_charge_template")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_serv_recchrg_templt_seq"), })
public class ServiceChargeTemplateRecurring extends ServiceChargeTemplate<RecurringChargeTemplate> {

    private static final long serialVersionUID = -5599952620149127436L;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_template_id")
    private CounterTemplate counterTemplate;

    /**
     * Prepaid wallet templates to charge on
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_serv_rec_wallet_template", joinColumns = @JoinColumn(name = "service_rec_templt_id"), inverseJoinColumns = @JoinColumn(name = "wallet_template_id"))
    @OrderColumn(name = "INDX")
    private List<WalletTemplate> walletTemplates;

    /**
     * Counters associated to a charge template.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_serv_rec_counter_template", joinColumns = @JoinColumn(name = "service_rec_templt_id"), inverseJoinColumns = @JoinColumn(name = "counter_template_id"))
    @OrderColumn(name = "INDX")
    private List<CounterTemplate> accumulatorCounterTemplates;

    /**
     * Get the counter template.
     *
     * @return a counter template.
     */

    public CounterTemplate getCounterTemplate() {
        return counterTemplate;
    }

    /**
     * Set counter template.
     *
     * @param counterTemplate
     */
    public void setCounterTemplate(CounterTemplate counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    public List<WalletTemplate> getWalletTemplates() {
        return walletTemplates;
    }

    public void setWalletTemplates(List<WalletTemplate> walletTemplates) {
        this.walletTemplates = walletTemplates;
    }

    /**
     * Gets Counters template for a charge template.
     *
     * @return list of counters template
     */
    public List<CounterTemplate> getAccumulatorCounterTemplates() {
        return accumulatorCounterTemplates;
    }

    /**
     * Sets counters template.
     *
     * @param counterTemplates counters template
     */
    public void setAccumulatorCounterTemplates(List<CounterTemplate> counterTemplates) {
        this.accumulatorCounterTemplates = counterTemplates;
    }

    @Override
    public int hashCode() {
        return 961 + ("ServiceChargeTemplateRecurring" + id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof ServiceChargeTemplateRecurring)) {
            return false;
        }

        ServiceChargeTemplateRecurring other = (ServiceChargeTemplateRecurring) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

}
