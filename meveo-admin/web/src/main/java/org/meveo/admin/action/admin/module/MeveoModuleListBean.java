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
package org.meveo.admin.action.admin.module;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.admin.custom.CustomizedEntity;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.reflections.Reflections;

@Named
@ConversationScoped
public class MeveoModuleListBean extends MeveoModuleBean {

	private static final long serialVersionUID = 1L;
//
//	@Inject
//	private JobInstanceService jobInstanceService;
//
//	private LazyDataModel<CustomizedEntity> customizedFieldDM = null;
//	private List<CustomizedEntity> allCustomFields;
//
//	public LazyDataModel<CustomizedEntity> getAllCustomFields() {
//		if (customizedFieldDM == null) {
//			allCustomFields = new ArrayList<CustomizedEntity>();
//			Reflections reflections = new Reflections("org.meveo.model");
//			Set<Class<? extends ICustomFieldEntity>> cfClasses = reflections.getSubTypesOf(ICustomFieldEntity.class);
//			for (Class<? extends ICustomFieldEntity> cfClass : cfClasses) {
//				if (JobInstance.class.isAssignableFrom(cfClass) || Modifier.isAbstract(cfClass.getModifiers())) {
//					continue;
//				}
//				allCustomFields.add(new CustomizedEntity(cfClass.getSimpleName(), cfClass, null, null));
//			}
//			// Find Jobs
//			for (Job job : jobInstanceService.getJobs()) {
//				if (job.getCustomFields() != null) {
//					String classname = job.getClass().getSimpleName();
//					log.debug("clazz name {}",classname);
//					int pos = classname.indexOf("$$");
//					if (pos > 0) {
//						classname = classname.substring(0, pos);
//					}
//					allCustomFields.add(new CustomizedEntity(classname, job.getClass(), null, null));
//				}
//			}
//			java.util.Collections.sort(allCustomFields, new Comparator<CustomizedEntity>() {
//				@Override
//				public int compare(CustomizedEntity o1, CustomizedEntity o2) {
//					return StringUtils.compare(o1.getEntityName(), o2.getEntityName());
//				}
//			});
//			customizedFieldDM = new LazyDataModel<CustomizedEntity>() {
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public Object getRowKey(CustomizedEntity object) {
//					return object.getEntityName();
//				}
//				
//
//				@Override
//				public CustomizedEntity getRowData(String rowKey) {
//					for(CustomizedEntity entity:allCustomFields){
//						if(entity.getEntityName().equals(rowKey)){
//							return entity;
//						}
//					}
//					return null;
//				}
//
//				@Override
//				public List<CustomizedEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder,
//						Map<String, Object> filters) {
//					setRowCount(allCustomFields.size());
//					return allCustomFields.subList(first,
//							(first + pageSize) > allCustomFields.size() ? allCustomFields.size() : (first + pageSize));
//				}
//
//			};
//		}
//		return customizedFieldDM;
//	}

}