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
package org.meveo.service.wf;

import org.meveo.model.crm.Provider;
import org.meveo.model.wf.WFTransitionRule;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class WFTransitionRuleService extends PersistenceService<WFTransitionRule> {

    @SuppressWarnings("unchecked")
    public List<WFTransitionRule> getWFTransitionRules(Provider provider) {
        return (List<WFTransitionRule>) getEntityManager()
                .createQuery(
                        "from " + WFTransitionRule.class.getSimpleName()
                                + " where provider=:provider")
                .setParameter("provider", provider)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<String> getDistinctNameWFTransitionRules(Provider provider) {
        return (List<String>) getEntityManager()
                .createQuery(
                        "select DISTINCT(wfr.name) from " + WFTransitionRule.class.getSimpleName()
                                + " wfr where wfr.provider=:provider")
                .setParameter("provider", provider)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<String> getDistinctNameWFTransitionRules() {
        return (List<String>) getEntityManager()
                .createQuery(
                        "select DISTINCT(wfr.name) from " + WFTransitionRule.class.getSimpleName()
                                + " wfr")
                .getResultList();
    }


    @SuppressWarnings("unchecked")
    public List<WFTransitionRule> getWFTransitionRules(String name, Provider provider) {
        return (List<WFTransitionRule>) getEntityManager()
                .createQuery(
                        "from " + WFTransitionRule.class.getSimpleName()
                                + " where name=:name and provider=:provider")
                .setParameter("name", name)
                .setParameter("provider", provider)
                .getResultList();
    }
}
