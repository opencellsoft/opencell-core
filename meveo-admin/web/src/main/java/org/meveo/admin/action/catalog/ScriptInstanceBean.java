/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link ScriptInstance} (extends {@link BaseBean} that provides
 * almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF
 * components.
 */
@Named
@ViewScoped
public class ScriptInstanceBean extends BaseBean<ScriptInstance> {
	private static final long serialVersionUID = 1L;
	/**
	 * Injected @{link ScriptInstance} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private ScriptInstanceService scriptInstanceService;

	
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ScriptInstanceBean() {
		super(ScriptInstance.class);
		
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public ScriptInstance initEntity() {
		log.debug("start conversation id: {}", conversation.getId());
		ScriptInstance scriptInstance = super.initEntity();
//		if(entity != null){
//			scriptInstanceService.clearLogs(getCurrentProvider().getCode(), entity.getCode());	
//		}
		return scriptInstance;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<ScriptInstance> getPersistenceService() {
		return scriptInstanceService;
	}

	

	@Override
	protected String getListViewName() {
		return "scriptInstances";
	}

	/**
	 * Fetch customer field so no LazyInitialize exception is thrown when we
	 * access it from account edit view.
	 * 
	 * @see org.manaty.beans.base.BaseBean#getFormFieldsToFetch()
	 */
	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
	@Override
	public String saveOrUpdate(ScriptInstance entity) throws BusinessException {
		String result = getListViewName();
		try {
			scriptInstanceService.saveOrUpdate(entity, getCurrentUser(), getCurrentProvider());
			if(entity.getError().booleanValue()){
				result = null;	
			}
			 
		} catch (Exception e) {
			messages.error(e.getMessage());
			result = null;
		}
		return result;
	}
	
	public void execute(){
		scriptInstanceService.test(getCurrentProvider(), entity.getCode(),null);
	}
	
	public List<String> getLogs(){
		return scriptInstanceService.getLogs(getCurrentProvider().getCode(), entity.getCode());	
	}
}
