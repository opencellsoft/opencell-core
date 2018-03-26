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

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@ExportIdentifier({ "country.countryCode" })
@Cacheable
@Table(name = "billing_trading_country")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_trading_country_seq"), })
@NamedQueries({ @NamedQuery(name = "TradingCountry.getByCode", query = "from TradingCountry tr where tr.country.countryCode = :tradingCountryCode) ", hints = {
        @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class TradingCountry extends EnableEntity {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "tradingCountry", fetch = FetchType.LAZY)
    private List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "pr_description", length = 255)
    @Size(max = 255)
    private String prDescription;

    @Transient
    String countryCode;

    public String getPrDescription() {
        return prDescription;
    }

    public void setPrDescription(String prDescription) {
        this.prDescription = prDescription;
    }

    public List<InvoiceSubcategoryCountry> getInvoiceSubcategoryCountries() {
        return invoiceSubcategoryCountries;
    }

    public void setInvoiceSubcategoryCountries(List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries) {
        this.invoiceSubcategoryCountries = invoiceSubcategoryCountries;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCountryCode() {
        return country.getCountryCode();
    }

    @Override
    public String toString() {
        return String.format("TradingCountry [country=%s, id=%s]", country, getId());
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof TradingCountry)) {
            return false;
        }

        TradingCountry other = (TradingCountry) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;

        } else if (country.getId().equals(other.getCountry().getId())) {
            return true;
        }
        return false;
    }

}
