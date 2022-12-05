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
package org.meveo.service.billing.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import org.meveo.model.billing.TradingCountry;
import org.meveo.service.base.PersistenceService;

@Stateless
public class TradingCountryService extends PersistenceService<TradingCountry> {

    /**
     * Find TradingCountry by its trading country code.
     * 
     * Deprecated in 9.0.1. Use findByCode instead.
     * 
     * @param tradingCountryCode Trading Country Code
     * @return Trading country found or null.
     */
    
    @Deprecated
    public TradingCountry findByTradingCountryCode(String tradingCountryCode) {
        return findByCode(tradingCountryCode);
    }
    /**
     * Find TradingCountry by its trading country code.
     * 
     * @param tradingCountryCode Trading Country Code
     * @return Trading country found or null.
     */
    public TradingCountry findByCode(String tradingCountryCode) {
        try {
            return getEntityManager().createNamedQuery("TradingCountry.getByCode", TradingCountry.class).setParameter("tradingCountryCode", tradingCountryCode).getSingleResult();

        } catch (NoResultException e) {
            log.warn("Trading country not found : country={}", tradingCountryCode);
            return null;
        }
    }
}
