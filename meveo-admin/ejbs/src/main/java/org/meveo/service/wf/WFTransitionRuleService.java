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

import org.meveo.model.crm.Provider;
import org.meveo.model.wf.TransitionRuleTypeEnum;
import org.meveo.model.wf.WFTransitionRule;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WFTransitionRuleService extends PersistenceService<WFTransitionRule> {

    public Integer getMaxPriority(String ruleName, TransitionRuleTypeEnum type, Provider provider) {
        try {
            return (Integer) getEntityManager()
                .createQuery(
                        "select MAX(wfr.priority) from " + WFTransitionRule.class.getSimpleName()
                                + " wfr where wfr.name=:name and wfr.type=:type and wfr.provider=:provider")
                .setParameter("name", ruleName)
                .setParameter("type", type)
                .setParameter("provider", provider)
                .getSingleResult();
        } catch (NoResultException e) {
            log.error("failed to find ", e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<String> getDistinctNameWFTransitionRules(Provider provider) {
        try {
            return (List<String>) getEntityManager()
                    .createQuery(
                            "select DISTINCT(wfr.name) from " + WFTransitionRule.class.getSimpleName()
                                    + " wfr where wfr.provider=:provider")
                    .setParameter("provider", provider)
                    .getResultList();
        } catch (NoResultException e) {
            log.error("failed to find ", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<WFTransitionRule> getWFTransitionRules(String name, Provider provider) {
        try {
            return (List<WFTransitionRule>) getEntityManager()
                    .createQuery(
                            "from " + WFTransitionRule.class.getSimpleName()
                                    + " where name=:name and provider=:provider")
                    .setParameter("name", name)
                    .setParameter("provider", provider)
                    .getResultList();
        } catch (NoResultException e) {
            log.error("failed to find WFTransitionRule", e);
        }
        return null;
    }

    public WFTransitionRule getWFTransitionRule(String name, String value, Integer priority, TransitionRuleTypeEnum type, Provider provider) {
        WFTransitionRule wfTransitionRule = null;
        try {
            wfTransitionRule = (WFTransitionRule) getEntityManager()
                    .createQuery(
                            "from " + WFTransitionRule.class.getSimpleName()
                                    + " where name=:name and value=:value and priority=:priority and type=:type and provider=:provider")

                    .setParameter("name", name)
                    .setParameter("value", value)
                    .setParameter("priority", priority)
                    .setParameter("type", type)
                    .setParameter("provider", provider)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.error("failed to find WFTransitionRule", e);
        }
        return wfTransitionRule;
    }

    public WFTransitionRule getWFTransitionRuleByNameValue(String name, String value, Provider provider) {
        WFTransitionRule wfTransitionRule = null;
        try {
            wfTransitionRule = (WFTransitionRule) getEntityManager()
                    .createQuery(
                            "from " + WFTransitionRule.class.getSimpleName()
                                    + " where name=:name and value=:value and provider=:provider")

                    .setParameter("name", name)
                    .setParameter("value", value)
                    .setParameter("provider", provider)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.error("failed to find WFTransitionRule", e);
        }
        return wfTransitionRule;
    }
}
