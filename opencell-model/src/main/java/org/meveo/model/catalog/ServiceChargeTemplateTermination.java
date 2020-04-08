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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.util.List;

/**
 * Service template to termination ones hot charge template mapping
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Cacheable
@ExportIdentifier({ "chargeTemplate.code", "serviceTemplate.code" })
@Table(name = "cat_serv_trm_charge_template")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_serv_trmchrg_templt_seq"), })
public class ServiceChargeTemplateTermination extends ServiceChargeTemplate<OneShotChargeTemplate> {

    private static final long serialVersionUID = 7811269692204342428L;

    /**
     * Counter associated to a charge template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_template_id")
    private CounterTemplate counterTemplate;

    /**
     * Prepaid wallet templates to charge on
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_serv_trm_wallet_template", joinColumns = @JoinColumn(name = "service_trm_templt_id"), inverseJoinColumns = @JoinColumn(name = "wallet_template_id"))
    @OrderColumn(name = "INDX")
    private List<WalletTemplate> walletTemplates;

    /**
     * Counters associated to a charge template.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_serv_trm_counter_template", joinColumns = @JoinColumn(name = "service_trm_templt_id"), inverseJoinColumns = @JoinColumn(name = "counter_template_id"))
    @OrderColumn(name = "INDX")
    private List<CounterTemplate> counterTemplates;

    /**
     * Use instead getCounterTemplates().
     *
     * @return
     */
    @Deprecated
    public CounterTemplate getCounterTemplate() {
        return counterTemplate;
    }

    /**
     * Use instead setCounterTemplates()
     *
     * @param counterTemplate
     */
    @Deprecated
    public void setCounterTemplate(CounterTemplate counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    @Override
    public List<WalletTemplate> getWalletTemplates() {
        return walletTemplates;
    }

    @Override
    public void setWalletTemplates(List<WalletTemplate> walletTemplates) {
        this.walletTemplates = walletTemplates;
    }

    /**
     * Gets Counters template for a charge template.
     *
     * @return list of counters template
     */
    @Override
    public List<CounterTemplate> getCounterTemplates() {
        return counterTemplates;
    }

    /**
     * Sets counters template.
     *
     * @param counterTemplates counters template
     */
    @Override
    public void setCounterTemplates(List<CounterTemplate> counterTemplates) {
        this.counterTemplates = counterTemplates;
    }

    @Override
    public int hashCode() {
        return 961 + ("ServiceChargeTemplateTermination" + id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof ServiceChargeTemplateTermination)) {
            return false;
        }

        ServiceChargeTemplateTermination other = (ServiceChargeTemplateTermination) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

}
