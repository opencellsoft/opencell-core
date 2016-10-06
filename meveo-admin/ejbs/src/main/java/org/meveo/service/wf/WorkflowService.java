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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.wf.IWorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

@Stateless
public class WorkflowService extends BusinessService<Workflow> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private WFTransitionService wfTransitionService;

    @Inject
    private WFActionService wfActionService;

    @Inject
    private BaseEntityService baseEntityService;

    @SuppressWarnings("unchecked")
    public List<Workflow> getWorkflows(Provider provider) {
        return (List<Workflow>) getEntityManager().createQuery("from " + Workflow.class.getSimpleName() + " where disabled=:disabled and provider=:provider")
            .setParameter("disabled", false).setParameter("provider", provider).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Workflow> findByWFType(String wfType, Provider provider) {
        return (List<Workflow>) getEntityManager().createQuery("from " + Workflow.class.getSimpleName() + " where disabled=:disabled and wfType=:wfType and provider=:provider")
            .setParameter("disabled", false).setParameter("wfType", wfType).setParameter("provider", provider).getResultList();
    }

    public List<Workflow> findByWFTypeWithoutStatus(String wfType, Provider provider) {
        return (List<Workflow>) getEntityManager().createQuery("from " + Workflow.class.getSimpleName() + " where wfType=:wfType and provider=:provider")
            .setParameter("wfType", wfType).setParameter("provider", provider).getResultList();
    }

    /**
     * Return all workflowType classes
     * 
     * @param provider
     * @return
     */
    public List<Class<?>> getAllWFTypes(Provider provider) {
        Set<Class<?>> classes = null;
        List<Class<?>> result = new ArrayList<Class<?>>();
        classes = ReflectionUtils.getClassesAnnotatedWith(WorkflowTypeClass.class, "org.meveo");
        if (CollectionUtils.isNotEmpty(classes)) {
            for (Class<?> cls : classes) {
                if (!Modifier.isAbstract(cls.getModifiers())) {
                    result.add(cls);
                }
            }
        }
        Map<String, Class<ScriptInterface>> mmap = scriptInstanceService.getAllScriptInterfaces(provider);

        if (mmap != null) {
            for (Entry<String, Class<ScriptInterface>> entry : mmap.entrySet()) {
                if (entry.getValue().isAnnotationPresent(WorkflowTypeClass.class)) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * Return all workflowType classes applied on an Entity
     * 
     * @param entityClass Entity class to match
     * @return All enabled workflowType classes applied on an Entity
     */
    @SuppressWarnings("rawtypes")
    private List<Class<?>> getWFTypeByEntity(Class<? extends IEntity> entityClass, Provider provider) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        for (Class<?> clazz : getAllWFTypes(provider)) {
            Class<?> genericClass = null;
            while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
                clazz = clazz.getSuperclass();
            }
            Object o = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

            if (o instanceof TypeVariable) {
                genericClass = (Class<?>) ((TypeVariable) o).getBounds()[0];
            } else {
                genericClass = (Class<?>) o;
            }

            if (genericClass.isAssignableFrom(entityClass)) {
                result.add(clazz);
            }
        }
        return result;
    }

    /**
     * Find a Workflow by an Entity
     * 
     * @param entityClass
     * @param provider
     * @return
     */
    public List<Workflow> findByEntity(Class<? extends IEntity> entityClass, Provider provider) {
        List<Workflow> result = new ArrayList<Workflow>();
        List<Class<?>> listWFType = getWFTypeByEntity(entityClass, provider);
        for (Class<?> wfTypeclass : listWFType) {
            result.addAll(findByWFType(wfTypeclass.getName(), provider));
        }
        return result;

    }

    /**
     * Check if there is any Workflow setup for a given entity class
     * 
     * @param entityClass
     * @param provider
     * @return
     */
    public boolean isWorkflowSetup(Class<? extends IEntity> entityClass, Provider provider) {
        List<Workflow> workflows = findByEntity(entityClass, provider);
        return !workflows.isEmpty();
    }

    /**
     * Execute a concrete workflow on the given entity
     * 
     * @param entity Entity to execute worklows on
     * @param workflowCode A concrete worklfow to execute
     * @param currentUser Current user
     * @return Updated entity
     * @throws BusinessException
     */
    public IEntity executeWorkflow(IEntity entity, String workflowCode, User currentUser) throws BusinessException {

        Workflow workflow = findByCode(workflowCode, currentUser.getProvider());
        if (workflow == null) {
            throw new EntityNotFoundException("Cant find Workflow entity by code:" + workflowCode);
        }
        entity = executeWorkflow(entity, workflow, currentUser);
        return entity;
    }

    /**
     * Execute all matching workflows on the given entity
     * 
     * @param entity Entity to execute worklows on
     * @param currentUser Current user
     * @return Updated entity
     * @throws BusinessException
     */
    public IEntity executeMatchingWorkflows(IEntity entity, User currentUser) throws BusinessException {

        List<Workflow> wfs = findByEntity(entity.getClass(), currentUser.getProvider());
        if (wfs == null || wfs.isEmpty()) {
            throw new EntityNotFoundException("Cant find any Workflow entity for the given entity " + entity);
        }
        for (Workflow wf : wfs) {
            entity = executeWorkflow(entity, wf, currentUser);
        }

        return entity;
    }

    /**
     * Execute given workflow on the given entity
     * 
     * @param entity Entity to execuet workflow on
     * @param workflow Workflow to execute
     * @param currentUser Current user
     * @throws BusinessException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public IEntity executeWorkflow(IEntity entity, Workflow workflow, User currentUser) throws BusinessException {
        try {

            log.debug("Executing workflow:{} on entity {}", workflow.getCode(), entity);
            Class<?> wfTypeClass = getWFTypeClassForName(workflow.getWfType(), currentUser.getProvider());
            Constructor<?> constructor = wfTypeClass.getConstructor(entity.getClass());

            IWorkflowType wfType = (IWorkflowType) constructor.newInstance(entity);
            log.trace("Actual status: {}", wfType.getActualStatus());
            List<WFTransition> listByFromStatus = wfTransitionService.listByFromStatus(wfType.getActualStatus(), workflow);

            for (WFTransition wfTransition : listByFromStatus) {

                if (matchExpression(wfTransition.getCombinedEl(), entity)) {

                    log.debug("Processing transition: {} on entity {}", wfTransition, entity);

                    List<WFAction> listWFAction = wfActionService.listByTransition(wfTransition);
                    for (WFAction wfAction : listWFAction) {
                        if (matchExpression(wfAction.getConditionEl(), entity)) {
                            log.debug("Processing action: {} on entity {}", wfAction);
                            Object actionResult = executeExpression(wfAction.getActionEl(), entity);
                            log.trace("Workflow action executed. Action {}, entity {}", wfAction, entity);
                            if (entity.equals(actionResult)){
                                entity = (IEntity) actionResult;
                            }
                        }
                    }
                    wfType.setEntity((BaseEntity) entity);
                    wfType.changeStatus(wfTransition.getToStatus(), currentUser);

                    log.trace("Entity status will be updated to {}. Entity {}", entity, wfTransition.getToStatus());
                    entity = baseEntityService.update(entity, currentUser);
                    return entity;
                }
            }

        } catch (Exception e) {
            log.error("Failed to execute workflow {} on {}", workflow.getCode(), entity, e);
            throw new BusinessException(e);
        }

        return entity;
    }

    /**
     * Return the workflowType class by name
     * 
     * @param wfTypeClassName
     * @param provider
     * @return
     * @throws ClassNotFoundException
     * @throws InvalidScriptException
     */
    public Class<?> getWFTypeClassForName(String wfTypeClassName, Provider provider) throws ClassNotFoundException, InvalidScriptException {

        try {
            return Class.forName(wfTypeClassName);

        } catch (ClassNotFoundException ex) {
            try {
                Class<?> clazz = scriptInstanceService.getScriptInterface(provider, wfTypeClassName);
                return clazz;

            } catch (ElementNotFoundException e) {
                throw new ClassNotFoundException("Class " + wfTypeClassName);
            }
        }
    }

    private boolean matchExpression(String expression, Object object) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return true;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (expression.indexOf("entity") >= 0) {
            userMap.put("entity", object);
        }

        return ValueExpressionWrapper.evaluateToBoolean(expression, "entity", object);

    }

    private Object executeExpression(String expression, Object object) throws BusinessException {

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("entity", object);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, Object.class);
    }

}