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
package org.meveo.admin.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.custom.CustomizedEntity;
import org.meveo.model.admin.MeveoModule;
import org.meveo.model.admin.MeveoModuleItem;
import org.meveo.model.admin.ModuleItemTypeEnum;
import org.meveo.model.admin.ModuleStatusEnum;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.notification.NotificationService;
import org.meveo.service.script.ScriptInstanceService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Meveo module bean
 * 
 * @author Tyshan Shi(tyshan@manaty.com)
 *
 */

@Named
@ViewScoped
public class MeveoModuleBean extends BaseBean<MeveoModule> {

	private static final long serialVersionUID = 1L;
	/**
	 * Injected @{link MeveoModule} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private MeveoModuleService meveoModuleService;
	@Inject
	private CustomEntityTemplateService customEntityTemplateService;
	@Inject
	private FilterService filterService;
	@Inject
	private ScriptInstanceService scriptInstanceService;
	@Inject
	private JobInstanceService jobInstanceService;
	@Inject
	private NotificationService notificationService;

	private List<CustomEntityTemplate> customEntities = new ArrayList<CustomEntityTemplate>();;
	private CustomEntityTemplate customEntity;

	private List<CustomizedEntity> customizedEntities = new ArrayList<CustomizedEntity>();
	private CustomizedEntity customizedEntity;

	private List<Filter> filters = new ArrayList<Filter>();
	private Filter filter;

	private List<ScriptInstance> scripts = new ArrayList<ScriptInstance>();
	private ScriptInstance script;

	private List<JobInstance> jobs = new ArrayList<JobInstance>();
	private JobInstance job;

	private List<Notification> notifications = new ArrayList<Notification>();
	private Notification notification;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public MeveoModuleBean() {
		super(MeveoModule.class);

	}

	@Override
	public MeveoModule initEntity() {
		MeveoModule module = super.initEntity();
		if (module != null && module.getModuleItems() != null) {
			for (MeveoModuleItem item : module.getModuleItems()) {
				switch (item.getItemType()) {
				case CET:
					CustomEntityTemplate cet = customEntityTemplateService.findByCode(item.getItemCode().substring(3),
							getCurrentProvider());
					if(cet!=null){
						customEntities.add(cet);
					}
					break;
				case CFT:
					if (item.getEntityName() != null) {
						customizedEntities.add(new CustomizedEntity(item.getEntityName(), null, null, null));
					}
					break;
				case FILTER:
					filter = filterService.findById(item.getItemId());
					if(filter!=null){
						filters.add(filter);
					}
					break;
				case SCRIPT:
					script = scriptInstanceService.findById(item.getItemId());
					if(script!=null){
						scripts.add(script);
					}
					break;
				case JOBINSTANCE:
					job=jobInstanceService.findById(item.getItemId());
					if(job!=null){
						jobs.add(job);
					}
				case NOTIFICATION:
					notification=notificationService.findById(item.getItemId());
					if(notification!=null){
						notifications.add(notification);
					}
				default:
				}
			}
		}
		return module;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<MeveoModule> getPersistenceService() {
		return meveoModuleService;
	}

	public CustomEntityTemplate getCustomEntity() {
		return customEntity;
	}

	public void setCustomEntity(CustomEntityTemplate customEntity) {
		// this.customEntity = customEntity;
		if (customEntity != null && !customEntities.contains(customEntity)) {
			entity.addModuleItem(
					new MeveoModuleItem(null, null, ModuleItemTypeEnum.CET, customEntity.getCFTPrefix(), null));
			this.customEntities.add(customEntity);
		}
	}

	public List<CustomEntityTemplate> getCustomEntities() {
		return customEntities;
	}

	public void setCustomEntities(List<CustomEntityTemplate> customEntities) {
		this.customEntities = customEntities;
	}

	public void removeCustomEntity(CustomEntityTemplate cet) {
		this.customEntities.remove(cet);
		for (MeveoModuleItem item : entity.getModuleItems()) {
			if (!item.getItemType().equals(ModuleItemTypeEnum.CET))
				continue;
			if (cet.getCFTPrefix().equalsIgnoreCase(item.getItemCode())) {
				entity.getModuleItems().remove(item);
				break;
			}
		}
	}

	public List<CustomizedEntity> getCustomizedEntities() {
		return customizedEntities;
	}

	public void setCustomizedEntities(List<CustomizedEntity> customizedEntities) {
		this.customizedEntities = customizedEntities;
	}

	public CustomizedEntity getCustomizedEntity() {
		return customizedEntity;
	}

	public void setCustomizedEntity(CustomizedEntity customizedEntity) {
		// this.customField = customField;
		log.debug("add customized entity {}", customizedEntity);
		if (customizedEntity != null) {
			boolean found = false;
			for (CustomizedEntity c : customizedEntities) {
				if (customizedEntity.getEntityName().equals(c.getEntityName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				customizedEntities.add(customizedEntity);
				String clazz = customizedEntity.getEntityClass().getName();
				if (clazz != null && clazz.indexOf("$$") > 0) {
					clazz = clazz.substring(0, clazz.indexOf("$$"));
				}
				entity.addModuleItem(new MeveoModuleItem(customizedEntity.getEntityName(), clazz,
						ModuleItemTypeEnum.CFT, null, null));
			}
		}
	}

	public void removeCustomizedEntity(CustomizedEntity customizedEntity) {
		customizedEntities.remove(customizedEntity);
		for (MeveoModuleItem item : entity.getModuleItems()) {
			if (!ModuleItemTypeEnum.CFT.equals(item.getItemType()))
				continue;
			if (customizedEntity.getEntityName().equals(item.getEntityName())) {
				entity.getModuleItems().remove(item);
				break;
			}
		}
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
		if (filter != null && !filters.contains(filter)) {
			entity.addModuleItem(new MeveoModuleItem(null, null, ModuleItemTypeEnum.FILTER, null, filter.getId()));
			filters.add(filter);
		}
	}

	public void removeFilter(Filter filter) {
		if (filter != null) {
			filters.remove(filter);
			for (MeveoModuleItem item : entity.getModuleItems()) {
				if (!ModuleItemTypeEnum.FILTER.equals(item.getItemType()))
					continue;
				if (filter.getId().equals(item.getItemId())) {
					entity.getModuleItems().remove(item);
					break;
				}
			}
		}
	}

	public List<Filter> getAllFilters() {
		return filters;
	}

	public ScriptInstance getScript() {
		return script;
	}

	public void setScript(ScriptInstance script) {
		if (script != null && !scripts.contains(script)) {
			// this.script = script;
			entity.addModuleItem(new MeveoModuleItem(null, null, ModuleItemTypeEnum.SCRIPT, null, script.getId()));
			this.scripts.add(script);
		}
	}

	public void removeScript(ScriptInstance script) {
		if (scripts.contains(script)) {
			scripts.remove(script);
			for (MeveoModuleItem item : entity.getModuleItems()) {
				if (!ModuleItemTypeEnum.SCRIPT.equals(item.getItemType()))
					continue;
				if (script.getId().equals(item.getItemId())) {
					entity.getModuleItems().remove(item);
					break;
				}
			}
		}
	}

	public List<ScriptInstance> getScripts() {
		return scripts;
	}

	public JobInstance getJob() {
		return job;
	}

	public void setJob(JobInstance job) {
		if (job != null && !jobs.contains(job)) {
			entity.addModuleItem(new MeveoModuleItem(null, null, ModuleItemTypeEnum.JOBINSTANCE, null, job.getId()));
			this.jobs.add(job);
		}
	}

	public void removeJob(JobInstance job) {
		if (jobs.contains(job)) {
			jobs.remove(job);
			for (MeveoModuleItem item : entity.getModuleItems()) {
				if (!ModuleItemTypeEnum.JOBINSTANCE.equals(item.getItemType()))
					continue;
				if (job.getId().equals(item.getItemId())) {
					entity.getModuleItems().remove(item);
					break;
				}
			}
		}
	}

	public List<JobInstance> getJobs() {
		return jobs;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		if (notification != null && !notifications.contains(notification)) {
			entity.addModuleItem(
					new MeveoModuleItem(null, null, ModuleItemTypeEnum.NOTIFICATION, null, notification.getId()));
			this.notifications.add(notification);
		}
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void removeNotification(Notification notification) {
		if (notifications.contains(notification)) {
			notifications.remove(notification);
			for (MeveoModuleItem item : entity.getModuleItems()) {
				if (!ModuleItemTypeEnum.NOTIFICATION.equals(item.getItemType()))
					continue;
				if (notification.getId().equals(item.getItemId())) {
					entity.getModuleItems().remove(item);
					break;
				}
			}
		}
	}
	public void activate(){
		log.debug("activate ...");
		entity.setStatus(ModuleStatusEnum.ACTIVATE);
		entity=meveoModuleService.update(entity);
	}
	public void deactivate(){
		log.debug("deactivate ...");
		entity.setStatus(ModuleStatusEnum.DEACTIVATE);
		entity=meveoModuleService.update(entity);
	}

}