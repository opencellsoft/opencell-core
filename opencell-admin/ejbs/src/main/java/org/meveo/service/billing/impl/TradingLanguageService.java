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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.service.base.PersistenceService;

@Stateless
public class TradingLanguageService extends PersistenceService<TradingLanguage> {
    /**
     * Find TradingLanguage by its trading language code.
     * 
     * @param tradingLanguageCode Trading Language Code
     * @return Trading language found or null.
     */
    public TradingLanguage findByTradingLanguageCode(String tradingLanguageCode) {
        try {
            return getEntityManager().createNamedQuery("TradingLanguage.getByCode", TradingLanguage.class).setParameter("tradingLanguageCode", tradingLanguageCode)
                .getSingleResult();

        } catch (NoResultException e) {
            log.warn("Trading language not found : language={}", tradingLanguageCode);
            return null;
        }
    }

    public int getNbLanguageNotAssociated() {
        return ((Long) getEntityManager().createNamedQuery("TradingLanguage.getNbLanguageNotAssociated", Long.class).getSingleResult()).intValue();
    }

    public List<TradingLanguage> getLanguagesNotAssociated() {
        return (List<TradingLanguage>) getEntityManager().createNamedQuery("TradingLanguage.getLanguagesNotAssociated", TradingLanguage.class).getResultList();
    }

    public List<String> listLanguageCodes() {
        return getEntityManager().createNamedQuery("TradingLanguage.languageCodes", String.class).getResultList();
    }
}
