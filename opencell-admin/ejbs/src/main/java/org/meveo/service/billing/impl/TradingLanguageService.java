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
package org.meveo.service.billing.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
     * @throws ElementNotFoundException
     */
    public TradingLanguage findByTradingLanguageCode(String tradingLanguageCode) {
        try {
            log.debug("findByTradingLanguageCode tradingLanguageCode={}", tradingLanguageCode);
            Query query = getEntityManager().createQuery("select b from TradingLanguage b where b.language.languageCode = :tradingLanguageCode ");
            query.setParameter("tradingLanguageCode", tradingLanguageCode);
            return (TradingLanguage) query.getSingleResult();

        } catch (NoResultException e) {
            log.warn("findByTradingLanguageCode not found : tradingLanguageCode={}", tradingLanguageCode);
            return null;
        }
    }

    public int getNbLanguageNotAssociated() {
        return ((Long) getEntityManager().createNamedQuery("TradingLanguage.getNbLanguageNotAssociated", Long.class).getSingleResult()).intValue();
    }

    public List<TradingLanguage> getLanguagesNotAssociated() {
        return (List<TradingLanguage>) getEntityManager().createNamedQuery("TradingLanguage.getLanguagesNotAssociated", TradingLanguage.class).getResultList();
    }

    public Set<String> listLanguageCodes() {
        Set<String> codes = new HashSet<>();
        codes.addAll(getEntityManager().createNamedQuery("TradingLanguage.languageCode", String.class).getResultList());
        return codes;
    }
}
