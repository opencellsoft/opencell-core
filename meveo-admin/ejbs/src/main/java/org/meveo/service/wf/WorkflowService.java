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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.IWorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
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
	private BaseEntityService baseEntityService;

	@SuppressWarnings("unchecked")
	public List<Workflow> getWorkflows(Provider provider) {
		return (List<Workflow>) getEntityManager().createQuery("from " + Workflow.class.getSimpleName() + " where status=:status and provider=:provider").setParameter("provider", provider).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Workflow> findByWFType(String wfType, Provider provider) {
		return (List<Workflow>) getEntityManager().createQuery("from " + Workflow.class.getSimpleName() + " where wfType=:wfType and provider=:provider").setParameter("wfType", wfType).setParameter("provider", provider).getResultList();
	}

	/**
	 * Return all workflowType classes
	 * 
	 * @param provider
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Class<?>> getAllWFTypes(Provider provider) {
		List<Class> classes = null;
		List<Class<?>> result = new ArrayList<Class<?>>();
		try {
			classes = ReflectionUtils.getClasses("org.meveo");
		} catch (Exception e) {
			log.error("Failed to get a list of classes for a model package", e);
			return null;
		}
		for (Class clazz : classes) {
			if (clazz.isAnnotationPresent(WorkflowTypeClass.class)) {
				result.add(clazz);
			}
		}
		Map<String, Class<ScriptInterface>> mmap = scriptInstanceService.getAllScriptInterfaces().get(provider.getCode());

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
	 * @param e
	 * @return
	 */
	public List<Class<?>> getWFTypeByEntity(Class<? extends IEntity> e, Provider provider) {
		List<Class<?>> result = new ArrayList<Class<?>>();
		for (Class<?> clazz : getAllWFTypes(provider)) {
			String genericClassName = "";
			while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
				clazz = clazz.getSuperclass();
			}
			Object o = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

			if (o instanceof TypeVariable) {
				genericClassName = ((Class<?>) ((TypeVariable) o).getBounds()[0]).getName();
			} else {
				genericClassName = ((Class<?>) o).getName();
			}

			if (e.getName().equals(genericClassName)) {
				result.add(clazz);
			}
		}
		return result;
	}

	/**
	 * Find a Workflow by an Entity
	 * @param class1
	 * @param provider
	 * @return
	 */
	public List<Workflow> findByEntity(Class<? extends IEntity> e, Provider provider) {
		List<Workflow> result = new ArrayList<Workflow>();
		List<Class<?>> listWFType = getWFTypeByEntity(e, provider);
		for (Class<?> wfTypeclass : listWFType) {
			result.addAll(findByWFType(wfTypeclass.getName(), provider));
		}
		return result;

	}

   /**
    * Execute all matching workflows for the entity if workflowCode is no set, 
    * 
    * @param entity
    * @param workflowCode
    * @param currentUser
    * @throws NoSuchMethodException
    * @throws SecurityException
    * @throws ClassNotFoundException
    * @throws InstantiationException
    * @throws IllegalAccessException
    * @throws IllegalArgumentException
    * @throws InvocationTargetException
    * @throws BusinessException
    */
	public void executeMatchingWorkflows(IEntity entity, String workflowCode, User currentUser) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, BusinessException {

		if (!StringUtils.isBlank(workflowCode)) {
			Workflow workflow = findByCode(workflowCode, currentUser.getProvider());
			if (workflow == null) {
				throw new EntityNotFoundException("Cant find Workflow entity by code:" + workflowCode);
			}
			executeWorkflow(entity, workflow, currentUser);
		} else {
			List<Workflow> wfs = findByEntity(entity.getClass(), currentUser.getProvider());
			if (wfs == null || wfs.isEmpty()) {
				throw new EntityNotFoundException("Cant find  any Workflow entity for the given baseEntity");
			}else{
				log.debug("list workflow matching:{}"+wfs);
			}
			for (Workflow wf : wfs) {
				executeWorkflow(entity, wf, currentUser);
			}
		}

	}

	/**
	 * Execute the workflow for the entity
	 * 
	 * @param entity
	 * @param workflow
	 * @param currentUser
	 * @throws BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void executeWorkflow(IEntity entity, Workflow workflow, User currentUser) throws BusinessException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		log.debug("Executing workflow:{} ..."+workflow.getCode());
		Class<?> wfTypeClass = getWFTypeClassForName(workflow.getWfType(),currentUser.getProvider());
		Constructor<?> constructor = wfTypeClass.getConstructor(entity.getClass());
		IWorkflowType wfType = (IWorkflowType) constructor.newInstance(entity);
		log.debug("ActualStatus:" + wfType.getActualStatus());
		log.debug("StatusList:" + wfType.getStatusList());
		List<WFTransition> listByFromStatus = wfTransitionService.listByFromStatus(wfType.getActualStatus(), workflow);
		log.debug("listByFromStatus.size:" + (listByFromStatus == null ? null : listByFromStatus.size()));
		for (WFTransition wfTransition : listByFromStatus) {
			log.debug("processing transition:" + wfTransition);
			log.debug("processing transition.getConditionEl:" + wfTransition.getConditionEl());
			if (matchExpression(wfTransition.getCombinedEl(), entity)) {
				log.debug("conditionEl is true");
				log.debug("listWfActions.size:" + (wfTransition.getWfActions() == null ? null : wfTransition.getWfActions().size()));
				for (WFAction wfAction : wfTransition.getWfActions()) {
					log.debug("matchExpression wfAction:" + wfAction);
					if (matchExpression(wfAction.getConditionEl(), entity)) {
						log.debug("wfAction conditionEl is true");
						matchExpression(wfAction.getActionEl(), entity);
						log.debug("wfAction executed");
						// TODO es history
					}
				}
				wfType.changeStatus(wfTransition.getToStatus());

				log.debug("wfType.changeStatus({}) done", wfTransition.getToStatus());
				baseEntityService.update(entity, currentUser);
				log.debug("entity updated");
				break;
			}
		}
	}

	/**
	 * Return the workflowType class by name
	 * 
	 * @param wfTypeClassName
	 * @param provider
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Class<?> getWFTypeClassForName(String wfTypeClassName,Provider provider) throws ClassNotFoundException {
		Class<?> clazz = null;
		if (scriptInstanceService.getAllScriptInterfaces().get(provider.getCode()).containsKey(wfTypeClassName)) {
			clazz = scriptInstanceService.getAllScriptInterfaces().get(provider.getCode()).get(wfTypeClassName);
		} else {
			clazz = Class.forName(wfTypeClassName);
		}
		return clazz;
	}

	private boolean matchExpression(String expression, Object object) throws BusinessException {
		Boolean result = true;
		if (StringUtils.isBlank(expression)) {
			return result;
		}
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		if (expression.indexOf("entity") >= 0) {
			userMap.put("entity", object);
		}

		Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
		try {
			result = (Boolean) res;
		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
		}
		return result;
	}
}