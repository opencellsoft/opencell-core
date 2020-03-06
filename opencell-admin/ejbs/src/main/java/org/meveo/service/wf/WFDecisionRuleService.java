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
