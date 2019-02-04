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

import static org.meveo.admin.job.GenericWorkflowJob.GENERIC_WF;
import static org.meveo.admin.job.GenericWorkflowJob.IWF_ENTITY;
import static org.meveo.admin.job.GenericWorkflowJob.WF_INS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

@Stateless
public class GenericWorkflowService extends BusinessService<GenericWorkflow> {

    @Inject
    private BusinessEntityService businessEntityService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    static Set<Class<?>> WORKFLOWED_CLASSES = ReflectionUtils.getClassesAnnotatedWith(WorkflowedEntity.class, "org.meveo");

    public List<Class<?>> getAllWorkflowedClass() {
        List<Class<?>> result = new ArrayList<>(WORKFLOWED_CLASSES);
        return result;
    }

    /**
     * 
     * @param workflowInstance
     * @param genericWorkflow
     * @throws BusinessException
     */
    public void executeTransitionScript(WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) throws BusinessException {
        log.debug("Executing generic workflow script:{} on instance {}", genericWorkflow.getCode(), workflowInstance);
        try {
            String qualifiedName = genericWorkflow.getTargetEntityClass();
            Class<BusinessEntity> clazz = (Class<BusinessEntity>) Class.forName(qualifiedName);
            businessEntityService.setEntityClass(clazz);
            BusinessEntity businessEntity = businessEntityService.findByCode(workflowInstance.getEntityInstanceCode());

            // ScriptInstance scriptInstance = genericWorkflow.getTransitionScript();
            ScriptInstance scriptInstance = null;
            String scriptCode = scriptInstance.getCode();
            ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);
            Map<String, Object> methodContext = new HashMap<String, Object>();
            methodContext.put(GENERIC_WF, genericWorkflow);
            methodContext.put(WF_INS, workflowInstance);
            methodContext.put(IWF_ENTITY, (IWFEntity) businessEntity);
            methodContext.put(Script.CONTEXT_ACTION, scriptCode);
            if (script == null) {
                log.error("Script is null");
                throw new BusinessException("script is null");
            }
            script.execute(methodContext);
        } catch (Exception e) {
            log.error("Failed to execute generic workflow {} on {}", genericWorkflow.getCode(), workflowInstance, e);
            throw new BusinessException(e);
        }
    }
}
