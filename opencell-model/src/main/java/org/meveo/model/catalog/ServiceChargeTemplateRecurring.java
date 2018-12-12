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
package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;

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
    
    public CounterTemplate getCounterTemplate() {
        return counterTemplate;
    }

    public void setCounterTemplate(CounterTemplate counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    public List<WalletTemplate> getWalletTemplates() {
        return walletTemplates;
    }

    public void setWalletTemplates(List<WalletTemplate> walletTemplates) {
        this.walletTemplates = walletTemplates;
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
