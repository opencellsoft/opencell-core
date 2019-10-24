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
package org.meveo.model.listeners;

import org.meveo.model.billing.TradingCountry;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This interceptor allows to intercept TradingCountry objects, update their properties before it is saved or updated.
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */

public class TradingCountryListener {

    @PrePersist
    public void prePersist(TradingCountry tradingCountry) {
        if (tradingCountry.getCode() == null && tradingCountry.getCountry() != null) {
            tradingCountry.setCode(tradingCountry.getCountry().getCode());
        }
    }

    @PreUpdate
    public void preUpdate(TradingCountry tradingCountry) {
        if (tradingCountry.getCode() == null && tradingCountry.getCountry() != null) {
            tradingCountry.setCode(tradingCountry.getCountry().getCode());
        }
    }

}
