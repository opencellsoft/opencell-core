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
package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.listeners.TradingCountryListener;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;

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
