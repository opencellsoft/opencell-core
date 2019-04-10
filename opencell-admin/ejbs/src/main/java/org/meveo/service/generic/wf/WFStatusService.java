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
package org.meveo.service.generic.wf;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.service.base.BusinessService;

@Stateless
public class WFStatusService extends BusinessService<WFStatus> {

    public WFStatus findByCodeAndGWF(String statusCode, GenericWorkflow genericWorkflow) {
        WFStatus wFStatus = null;
        try {
            wFStatus = getEntityManager().createNamedQuery("WFStatus.findByCodeAndGWF", WFStatus.class).setParameter("code", statusCode)
                .setParameter("genericWorkflow", genericWorkflow).getSingleResult();
        } catch (NoResultException nre) {
            // Ignore this because as per your logic this is ok!
        }
        return wFStatus;
    }

    /**
     * Find Workflow status by uuid
     *
     * @param uuid uuid of workflow status
     * @return Workflow status
     */
    public WFStatus findTransitionByUUID(String uuid) {
        WFStatus wfStatus = null;
        try {
            wfStatus = (WFStatus) getEntityManager().createQuery("from " + WFStatus.class.getSimpleName() + " where uuid=:uuid").setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return wfStatus;
    }
}
