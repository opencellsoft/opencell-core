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

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.listeners.TradingCountryListener;

/**
 * Country enabled in application
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Entity
@ObservableEntity
@ExportIdentifier({ "code" })
@EntityListeners({ TradingCountryListener.class })
@Cacheable
@Table(name = "billing_trading_country")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_trading_country_seq"), })
@NamedQueries({ @NamedQuery(name = "TradingCountry.getByCode", query = "from TradingCountry tr where tr.country.countryCode = :tradingCountryCode ", hints = {
        @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class TradingCountry extends EnableBusinessCFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Country
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

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
