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
package org.meveo.service.generic.wf;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.generic.wf.WFStatusDto;
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
    
    public void updateStatusByGenericWorkflow(GenericWorkflowDto genericWorkflowDto, GenericWorkflow genericWorkflow) {
        deleteByGenericWorkflow(genericWorkflow.getId());        
        genericWorkflow.getStatuses().clear();        
        List<WFStatus> wFStatusAdd = new ArrayList<WFStatus>();
        for (WFStatusDto wfStatusDto : genericWorkflowDto.getStatuses()) {
            WFStatus wfStatus = wfStatusDto.toWFStatus();          
            wfStatus.setGenericWorkflow(genericWorkflow);
            create(wfStatus);
            wFStatusAdd.add(wfStatus);
        }
        genericWorkflow.getStatuses().addAll(wFStatusAdd);
    }
    
    public void deleteByGenericWorkflow(Long genericWorkflowId) {
        getEntityManager().createNamedQuery("WFStatus.deleteByGenericWorkflow").setParameter("genericWorkflowId", genericWorkflowId).executeUpdate();
    }
}
