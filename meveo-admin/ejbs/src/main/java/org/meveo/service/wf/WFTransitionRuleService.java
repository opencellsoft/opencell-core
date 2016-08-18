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

import org.meveo.model.crm.Provider;
import org.meveo.model.wf.TransitionRuleTypeEnum;
import org.meveo.model.wf.WFTransitionRule;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WFTransitionRuleService extends PersistenceService<WFTransitionRule> {

    @SuppressWarnings("rawtypes")
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
        } catch (Exception e) {
        }
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public List<String> getDistinctNameWFTransitionRules(Provider provider) {
        return (List<String>) getEntityManager()
                .createQuery(
                        "select DISTINCT(wfr.name) from " + WFTransitionRule.class.getSimpleName()
                                + " wfr where wfr.provider=:provider")
                .setParameter("provider", provider)
                .getResultList();
    }

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("unchecked")
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
        } catch (Exception e) {
        }
        return wfTransitionRule;
    }

    @SuppressWarnings("unchecked")
    public WFTransitionRule getWFTransitionRuleByNameTypeValue(String name, String value, TransitionRuleTypeEnum type, Provider provider) {
        WFTransitionRule wfTransitionRule = null;
        try {
            wfTransitionRule = (WFTransitionRule) getEntityManager()
                    .createQuery(
                            "from " + WFTransitionRule.class.getSimpleName()
                                    + " where name=:name and value=:value and type=:type and provider=:provider")

                    .setParameter("name", name)
                    .setParameter("value", value)
                    .setParameter("type", type)
                    .setParameter("provider", provider)
                    .getSingleResult();
        } catch (Exception e) {
        }
        return wfTransitionRule;
    }
}
