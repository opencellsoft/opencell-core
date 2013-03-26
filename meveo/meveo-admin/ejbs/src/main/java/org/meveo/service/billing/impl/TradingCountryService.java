/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.meveo.admin.exception.ElementNotFoundException;

import org.meveo.model.billing.TradingCountry;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.local.TradingCountryServiceLocal;


/**
 * TradingCountry service implementation.
 * 
 * @author Marouane ALAMI	
 * @created 25-03-2013
 */
@Stateless
@Name("tradingCountryService")
@AutoCreate
public class TradingCountryService extends PersistenceService<TradingCountry> implements TradingCountryServiceLocal {
    /**
     * Find TradingCountry by its trading country code.
     * 
     * @param tradingCountryCode
     *            Trading Country Code
     * @return Trading country found or null.
     * @throws ElementNotFoundException
     */
    public TradingCountry findByTradingCountryCode(String tradingCountryCode, Provider provider) {
        try {
            log.info("findByTradingCountryCode tradingCountryCode=#0,provider=#1", tradingCountryCode,
                    provider != null ? provider.getCode() : null);
            Query query = em
                    .createQuery("select b from TradingCountry b where b.code = :tradingCountryCode and b.provider=:provider");
            query.setParameter("tradingCountryCode", tradingCountryCode);
            query.setParameter("provider", provider);
            return (TradingCountry) query.getSingleResult();
        } catch (NoResultException e) {
            log.warn("findByTradingCountryCode billing cycle not found : tradingCountryCode=#0,provider=#1",
                    tradingCountryCode, provider != null ? provider.getCode() : null);
            return null;
        }
    }
}