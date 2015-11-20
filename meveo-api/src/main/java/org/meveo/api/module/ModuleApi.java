package org.meveo.api.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.module.ModuleItemDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.MeveoModule;
import org.meveo.model.admin.MeveoModuleItem;
import org.meveo.model.admin.ModuleItemTypeEnum;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.notification.NotificationService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
 **/
@Stateless
public class ModuleApi extends BaseApi {

	@Inject
	private MeveoModuleService meveoModuleService;
	@Inject
	private CustomEntityTemplateService customEntityTemplateService;
	@Inject
	private CustomFieldTemplateService customFieldTemplateService;
	@Inject
	private FilterService filterService;
	@Inject
	private ScriptInstanceService scriptInstanceService;
	@Inject
	private JobInstanceService jobInstanceService;
	@Inject
	private NotificationService notificationService;

	public void create(ModuleDto moduleDto, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(moduleDto.getCode()) || StringUtils.isBlank(moduleDto.getDescription())||StringUtils.isBlank(moduleDto.getLicense())) {
			if (StringUtils.isBlank(moduleDto.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(moduleDto.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(moduleDto.getLicense())) {
				missingParameters.add("license");
			}
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		Provider provider = currentUser.getProvider();
		if (meveoModuleService.findByCode(moduleDto.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(MeveoModule.class, moduleDto.getCode());
		}
		MeveoModule meveoModule = new MeveoModule();
		meveoModule.setCode(moduleDto.getCode());
		meveoModule.setDescription(moduleDto.getDescription());
		meveoModule.setActive(true);
		meveoModule.setLicense(moduleDto.getLicense());
		List<ModuleItemDto> itemDtos = moduleDto.getModuleItems();
		if (itemDtos != null) {
			MeveoModuleItem moduleItem = null;
			for (ModuleItemDto itemDto : itemDtos) {
				if (itemDto.getItemType() == null) {
					missingParameters.add("module item type is null");
					throw new MissingParameterException(getMissingParametersExceptionMessage());
				}
				moduleItem=getModuleItemFromDto(itemDto, provider);
				meveoModule.addModuleItem(moduleItem);
			}
		}
		meveoModuleService.create(meveoModule, currentUser, provider);
	}

	private MeveoModuleItem getModuleItemFromDto(ModuleItemDto itemDto,Provider provider) throws EntityDoesNotExistsException{
		MeveoModuleItem result=null;
		switch (itemDto.getItemType()) {
			case CET:
				CustomEntityTemplate cet = customEntityTemplateService.findByCode(itemDto.getItemCode(),provider);
				if(cet!=null){
					result=new MeveoModuleItem(cet.getCode(), ModuleItemTypeEnum.CET);
				}else{
					throw new EntityDoesNotExistsException(CustomEntityTemplate.class,itemDto.getItemCode());
				}
				break;
			case CFT:
				CustomFieldTemplate cft=customFieldTemplateService.findByCodeAndAppliesTo(itemDto.getItemCode(),itemDto.getAppliesTo(),provider);
				if(cft!=null){
					result=new MeveoModuleItem(cft.getCode(),cft.getAppliesTo(),ModuleItemTypeEnum.CFT);
				}else{
					throw new EntityDoesNotExistsException(CustomFieldTemplate.class,itemDto.getItemCode());
				}
				break;
			case FILTER:
				Filter filter = filterService.findByCode(itemDto.getItemCode(),provider);
				if(filter!=null){
					result=new MeveoModuleItem(filter.getCode(),ModuleItemTypeEnum.FILTER);
				}else{
					throw new EntityDoesNotExistsException(Filter.class,itemDto.getItemCode());
				}
				break;
			case SCRIPT:
				ScriptInstance script = scriptInstanceService.findByCode(itemDto.getItemCode(),provider);
				if(script!=null){
					result=new MeveoModuleItem(itemDto.getItemCode(),ModuleItemTypeEnum.SCRIPT);
				}else{
					throw new EntityDoesNotExistsException(ScriptInstance.class,itemDto.getItemCode());
				}
				break;
			case JOBINSTANCE:
				JobInstance job=jobInstanceService.findByCode(itemDto.getItemCode(),provider);
				if(job!=null){
					result=new MeveoModuleItem(itemDto.getItemCode(),ModuleItemTypeEnum.JOBINSTANCE);
				}else{
					throw new EntityDoesNotExistsException(JobInstance.class,itemDto.getItemCode());
				}
				break;
			case NOTIFICATION:
				Notification notification=notificationService.findByCode(itemDto.getItemCode(),provider);
				if(notification!=null){
					result=new MeveoModuleItem(itemDto.getItemCode(), ModuleItemTypeEnum.NOTIFICATION);
				}else{
					throw new EntityDoesNotExistsException(Notification.class,itemDto.getItemCode());
				}
				break;
			default:
			}
		return result;
	}
	public void update(ModuleDto moduleDto, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(moduleDto.getCode()) && StringUtils.isBlank(moduleDto.getDescription())
				|| StringUtils.isBlank(moduleDto.getDisabled())||StringUtils.isBlank(moduleDto.getLicense())) {
			if (StringUtils.isBlank(moduleDto.getCode())) {
				missingParameters.add("module code is null");
			}
			if (StringUtils.isBlank(moduleDto.getDescription())) {
				missingParameters.add("module description is null");
			}
			if (StringUtils.isBlank(moduleDto.getDisabled())) {
				missingParameters.add("module disabled is null");
			}
			if (StringUtils.isBlank(moduleDto.getLicense())) {
				missingParameters.add("module license is null");
			}
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		} else {
			Provider provider = currentUser.getProvider();
			MeveoModule meveoModule = meveoModuleService.findByCode(moduleDto.getCode(), provider);
			if (meveoModule == null) {
				throw new EntityDoesNotExistsException(MeveoModule.class, moduleDto.getCode());
			}
			meveoModule.setDescription(moduleDto.getDescription());
			meveoModule.setDisabled(moduleDto.getDisabled());
			meveoModule.setLicense(moduleDto.getLicense());
			List<MeveoModuleItem> temps=new ArrayList<MeveoModuleItem>();
			if (!StringUtils.isBlank(moduleDto.getModuleItems())&&moduleDto.getModuleItems().size()>0) {
				MeveoModuleItem moduleItem = null;
				for (ModuleItemDto itemDto : moduleDto.getModuleItems()) {
					if (StringUtils.isBlank(itemDto.getItemType())) {
						missingParameters.add("module item type is null");
						throw new MissingParameterException(getMissingParametersExceptionMessage());
					}
					moduleItem=getModuleItemFromDto(itemDto, provider);
					if(!meveoModule.getModuleItems().contains(moduleItem)){
						meveoModule.addModuleItem(moduleItem);
					}
					temps.add(moduleItem);
				}
			}
			Iterator<MeveoModuleItem> it=meveoModule.getModuleItems().iterator();
			while(it.hasNext()){
				MeveoModuleItem i=it.next();
				if(!temps.contains(i)){
					i.setMeveoModule(null);
					it.remove();
				}
			}
			meveoModuleService.update(meveoModule, currentUser);
		}
	}

	public void delete(String code, User currentUser) throws EntityDoesNotExistsException {
		Provider provider = currentUser.getProvider();
		MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
		if (meveoModule == null) {
			throw new EntityDoesNotExistsException(MeveoModule.class, code);
		}
		meveoModuleService.remove(meveoModule);
	}

	public List<ModuleDto> list(User currentUser) throws EntityDoesNotExistsException {
		Provider provider = currentUser.getProvider();
		List<MeveoModule> meveoModules = meveoModuleService.list(provider);
		List<ModuleDto> result=new ArrayList<ModuleDto>();
		ModuleDto moduleDto=null;
		for(MeveoModule meveoModule:meveoModules){
			moduleDto=new ModuleDto(meveoModule.getCode(),meveoModule.getDescription(),meveoModule.getLicense(),meveoModule.isDisabled());
			List<MeveoModuleItem> moduleItems=meveoModule.getModuleItems();
			if(moduleItems!=null&&moduleItems.size()>0){
				List<ModuleItemDto> itemDtos=new ArrayList<ModuleItemDto>();
				ModuleItemDto itemDto=null;
				for(MeveoModuleItem moduleItem:moduleItems){
					itemDto=new ModuleItemDto(moduleItem.getItemCode(),moduleItem.getAppliesTo(),moduleItem.getItemType());
					itemDtos.add(itemDto);
				}
				moduleDto.setModuleItems(itemDtos);
			}
			result.add(moduleDto);
		}
		return result;
	}
	public ModuleDto get(String code, User currentUser) throws EntityDoesNotExistsException {
		Provider provider = currentUser.getProvider();
		MeveoModule meveoModule = meveoModuleService.findByCode(code, provider);
		if (meveoModule == null) {
			throw new EntityDoesNotExistsException(MeveoModule.class, code);
		}
		ModuleDto moduleDto=new ModuleDto(meveoModule.getCode(),meveoModule.getDescription(),meveoModule.getLicense(),meveoModule.isDisabled());
		List<MeveoModuleItem> moduleItems=meveoModule.getModuleItems();
		if(moduleItems!=null&&moduleItems.size()>0){
			List<ModuleItemDto> itemDtos=new ArrayList<ModuleItemDto>();
			ModuleItemDto itemDto=null;
			for(MeveoModuleItem moduleItem:moduleItems){
				itemDto=new ModuleItemDto(moduleItem.getItemCode(),moduleItem.getAppliesTo(),moduleItem.getItemType());
				itemDtos.add(itemDto);
			}
			moduleDto.setModuleItems(itemDtos);
		}
		return moduleDto;
	}
}
