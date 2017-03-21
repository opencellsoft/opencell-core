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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.wf.WFDecisionRule;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WFDecisionRuleService extends PersistenceService<WFDecisionRule> {

    @SuppressWarnings("unchecked")
    public List<String> getDistinctNameWFTransitionRules() {
        try {
            return (List<String>) getEntityManager()
                    .createQuery(
                            "select DISTINCT(wfr.name) from " + WFDecisionRule.class.getSimpleName()
                                    + " wfr ")
                    
                    .getResultList();
        } catch (NoResultException e) {
            log.error("failed to find ", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<WFDecisionRule> getWFDecisionRules(String name) {
        try {
            return (List<WFDecisionRule>) getEntityManager()
                    .createQuery(
                            "from " + WFDecisionRule.class.getSimpleName()
                                    + " where name=:name ")
                    .setParameter("name", name)
                    
                    .getResultList();
        } catch (NoResultException e) {
            log.error("failed to find WFDecisionRule", e);
        }
        return null;
    }

    public WFDecisionRule getWFDecisionRuleByNameValue(String name, String value) {
        WFDecisionRule wfDecisionRule = null;
        try {
            wfDecisionRule = (WFDecisionRule) getEntityManager()
                    .createQuery(
                            "from " + WFDecisionRule.class.getSimpleName()
                                    + " where name=:name and value=:value ")

                    .setParameter("name", name)
                    .setParameter("value", value)
                    
                    .getSingleResult();
        } catch (NoResultException e) {
            log.error("failed to find WFDecisionRule", e);
        }
        return wfDecisionRule;
    }

    public WFDecisionRule getWFDecisionRuleByName(String name) {
        WFDecisionRule wfDecisionRule = null;
        try {
            wfDecisionRule = (WFDecisionRule) getEntityManager()
                    .createQuery(
                            "from " + WFDecisionRule.class.getSimpleName()
                                    + " where model=:model and name=:name ")

                    .setParameter("model", true)
                    .setParameter("name", name)
                    
                    .getSingleResult();
        } catch (NoResultException e) {
            log.error("failed to find WFDecisionRule", e);
        }
        return wfDecisionRule;
    }
}
