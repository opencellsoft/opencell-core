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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.service.base.PersistenceService;

import com.google.common.collect.Maps;

@Stateless
public class WorkflowInstanceService extends PersistenceService<WorkflowInstance> {

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private WFStatusService wfStatusService;

    public void changeStatus(WorkflowInstance wfInstance, WFStatus wfStatus) throws BusinessException {
        changeStatus(wfInstance, wfStatus.getCode());
    }

    public void changeStatus(WorkflowInstance wfInstance, String statusCode) throws BusinessException {
        WFStatus wfStatus = wfStatusService.findByCode(statusCode);

        if (wfStatus == null) {
            throw new BusinessException("Cant find workflow status entity by code: " + statusCode);
        }

        GenericWorkflow genericWorkflow = wfInstance.getGenericWorkflow();
        List<WFStatus> statuses = genericWorkflowService.findById(genericWorkflow.getId(), Arrays.asList("statuses")).getStatuses();
        if (!statuses.contains(wfStatus)) {
            throw new BusinessException("Cant find status=" + statusCode + " in generiw workflow parent");
        }

        wfInstance.setWfStatus(wfStatus);

        update(wfInstance);
    }

    public List<WorkflowInstance> findByEntityInstanceCode(String entityInstanceCode) {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From WorkflowInstance where entityInstanceCode = :entityInstanceCode";
        params.put("entityInstanceCode", entityInstanceCode);

        return (List<WorkflowInstance>) executeSelectQuery(query, params);
    }
}
