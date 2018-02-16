package org.meveo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.EntityCustomActionService;
import org.meveo.util.EntityCustomizationUtils;

/**
 * @author Andrius Karpavicius
 **/
@Stateless
public class CustomEntityTemplateApi extends BaseCrudApi<CustomEntityTemplate, CustomEntityTemplateDto> {

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private EntityCustomActionService entityActionScriptService;

    @Inject
    private EntityCustomActionApi entityCustomActionApi;

    public CustomEntityTemplate create(CustomEntityTemplateDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        if (customEntityTemplateService.findByCode(dto.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomEntityTemplate.class, dto.getCode());
        }

        CustomEntityTemplate cet = CustomEntityTemplateDto.fromDTO(dto, null);
        customEntityTemplateService.create(cet);

        if (dto.getFields() != null) {
            for (CustomFieldTemplateDto cftDto : dto.getFields()) {
                customFieldTemplateApi.createOrUpdate(cftDto, cet.getAppliesTo());
            }
        }

        if (dto.getActions() != null) {
            for (EntityCustomActionDto actionDto : dto.getActions()) {
                entityCustomActionApi.createOrUpdate(actionDto, cet.getAppliesTo());
            }
        }

        return cet;
    }

    public CustomEntityTemplate updateEntityTemplate(CustomEntityTemplateDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dto.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCode());
        }

        cet = CustomEntityTemplateDto.fromDTO(dto, cet);
        cet = customEntityTemplateService.update(cet);

        synchronizeCustomFieldsAndActions(cet.getAppliesTo(), dto.getFields(), dto.getActions());

        return cet;
    }

    public void removeEntityTemplate(String code) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(code);
        if (cet != null) {
            // Related custom field templates will be removed along with CET
            customEntityTemplateService.remove(cet);
        } else {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
    }

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public CustomEntityTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("customEntityTemplateCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(code);

        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, code);
        }
        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());

        Map<String, EntityCustomAction> cetActions = entityActionScriptService.findByAppliesTo(cet.getAppliesTo());

        return CustomEntityTemplateDto.toDTO(cet, cetFields.values(), cetActions.values());
    }
    
    @Override
    public CustomEntityTemplate createOrUpdate(CustomEntityTemplateDto postData) throws MeveoApiException, BusinessException {
        CustomEntityTemplate cet = customEntityTemplateService.findByCode(postData.getCode());
        if (cet == null) {
            return create(postData);
        } else {
            return updateEntityTemplate(postData);
        }
    }

    public List<CustomEntityTemplateDto> listCustomEntityTemplates(String code) {

        List<CustomEntityTemplate> cets = null;
        if (StringUtils.isBlank(code)) {
            cets = customEntityTemplateService.list();
        } else {
            cets = customEntityTemplateService.findByCodeLike(code);
        }

        List<CustomEntityTemplateDto> cetDtos = new ArrayList<CustomEntityTemplateDto>();

        for (CustomEntityTemplate cet : cets) {

            Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
            Map<String, EntityCustomAction> cetActions = entityActionScriptService.findByAppliesTo(cet.getAppliesTo());

            cetDtos.add(CustomEntityTemplateDto.toDTO(cet, cetFields.values(), cetActions.values()));
        }

        return cetDtos;
    }

    @SuppressWarnings("rawtypes")
    public void customizeEntity(EntityCustomizationDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getClassname())) {
            missingParameters.add("className");
        }

        handleMissingParameters();

        Class clazz;
        try {
            clazz = Class.forName(dto.getClassname());
        } catch (ClassNotFoundException e) {
            throw new EntityDoesNotExistsException("Customizable entity of class " + dto.getClassname() + " not found");
        }

        String appliesTo = EntityCustomizationUtils.getAppliesTo(clazz, null);

        synchronizeCustomFieldsAndActions(appliesTo, dto.getFields(), dto.getActions());
    }

    private void synchronizeCustomFieldsAndActions(String appliesTo, List<CustomFieldTemplateDto> fields, List<EntityCustomActionDto> actions)
            throws MeveoApiException, BusinessException {

        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesToNoCache(appliesTo);

        // Create, update or remove fields as necessary
        List<CustomFieldTemplate> cftsToRemove = new ArrayList<CustomFieldTemplate>();
        if (fields != null && !fields.isEmpty()) {

            for (CustomFieldTemplate cft : cetFields.values()) {
                boolean found = false;
                for (CustomFieldTemplateDto cftDto : fields) {
                    if (cftDto.getCode().equals(cft.getCode())) {
                        found = true;
                        break;
                    }
                }

                // Old field is no longer needed. Remove by id, as CFT might come detached from cache
                if (!found) {
                    cftsToRemove.add(cft);
                }
            }
            // Update or create custom field templates
            for (CustomFieldTemplateDto cftDto : fields) {
                customFieldTemplateApi.createOrUpdate(cftDto, appliesTo);
            }

        } else {
            cftsToRemove.addAll(cetFields.values());
        }

        for (CustomFieldTemplate cft : cftsToRemove) {
            customFieldTemplateService.remove(cft.getId());
        }

        Map<String, EntityCustomAction> cetActions = entityActionScriptService.findByAppliesTo(appliesTo);

        // Create, update or remove fields as necessary
        List<EntityCustomAction> actionsToRemove = new ArrayList<EntityCustomAction>();
        if (actions != null && !actions.isEmpty()) {

            for (EntityCustomAction action : cetActions.values()) {
                boolean found = false;
                for (EntityCustomActionDto actionDto : actions) {
                    if (actionDto.getCode().equals(action.getCode())) {
                        found = true;
                        break;
                    }
                }

                // Old action is no longer needed. Remove by id, as Action might come detached from cache
                if (!found) {
                    actionsToRemove.add(action);
                }
            }
            // Update or create custom field templates
            for (EntityCustomActionDto actionDto : actions) {
                entityCustomActionApi.createOrUpdate(actionDto, appliesTo);
            }

        } else {
            actionsToRemove.addAll(cetActions.values());
        }

        for (EntityCustomAction action : actionsToRemove) {
            entityActionScriptService.remove(action.getId());
        }
    }

    @SuppressWarnings("rawtypes")
    public EntityCustomizationDto findEntityCustomizations(String customizedEntityClass) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(customizedEntityClass)) {
            missingParameters.add("customizedEntityClass");
        }

        handleMissingParameters();

        Class clazz;
        try {
            clazz = Class.forName(customizedEntityClass);
        } catch (ClassNotFoundException e) {
            throw new EntityDoesNotExistsException("Customizable entity of class " + customizedEntityClass + " not found");
        }

        String appliesTo = EntityCustomizationUtils.getAppliesTo(clazz, null);

        Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(appliesTo);

        Map<String, EntityCustomAction> cetActions = entityActionScriptService.findByAppliesTo(appliesTo);

        return EntityCustomizationDto.toDTO(clazz, cetFields.values(), cetActions.values());
    }
    
	public List<BusinessEntityDto> listBusinessEntityForCFVByCode(String code, String wildcode)
			throws MeveoApiException {
		List<BusinessEntityDto> result = new ArrayList<>();

		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}
		
		if(StringUtils.isBlank(wildcode)) {
			wildcode = "";
		}

		handleMissingParameters();

		CustomFieldTemplate cft = customFieldTemplateService.findByCode(code);
		if (cft == null) {
			throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
		}

		String entityClazz = cft.getEntityClazz();
		if (!StringUtils.isBlank(entityClazz)) {
			List<BusinessEntity> businessEntities = customFieldInstanceService
					.findBusinessEntityForCFVByCode(entityClazz, wildcode);
			if (businessEntities != null) {
				for (BusinessEntity be : businessEntities) {
					result.add(new BusinessEntityDto(be));
				}
			}
		}

		return result;
	}

	public EntityCustomizationDto listELFiltered(String appliesTo, String entityCode)
			throws MissingParameterException, BusinessException {
		EntityCustomizationDto result = new EntityCustomizationDto();
		log.debug("IPIEL: listELFiltered");

		if (StringUtils.isBlank(appliesTo)) {
			missingParameters.add("appliesTo");
		}
		if (StringUtils.isBlank(entityCode)) {
			missingParameters.add("entityCode");
		}

		handleMissingParameters();

		@SuppressWarnings("rawtypes")
		Class entityClass = null;
		// get all the class annotated with customFieldEntity
		Set<Class<?>> cfClasses = ReflectionUtils.getClassesAnnotatedWith(CustomFieldEntity.class);
		for (Class<?> clazz : cfClasses) {
			// check if appliesTo match, eg OFFER
			if (appliesTo.equals(clazz.getAnnotation(CustomFieldEntity.class).cftCodePrefix())) {
				entityClass = clazz;
				break;
			}
		}

		// search for custom field entity filtered by type and code
		ICustomFieldEntity entityInstance = customEntityTemplateService.findByClassAndCode(entityClass, entityCode);

		// custom fields that applies to an entity type, eg. OFFER
		Map<String, CustomFieldTemplate> cetFields = customFieldTemplateService.findByAppliesTo(appliesTo);
		result = EntityCustomizationDto.toDTO(entityClass, cetFields.values(), null);

		// evaluate the CFT againsts the entity
		List<CustomFieldTemplateDto> evaluatedCFTDto = new ArrayList<>();
		for (CustomFieldTemplateDto cft : result.getFields()) {
			if (ValueExpressionWrapper.evaluateToBooleanOneVariable(cft.getApplicableOnEl(), "entity", entityInstance)) {
				evaluatedCFTDto.add(cft);
			}
		}
		result.setFields(evaluatedCFTDto);

		return result;
	}

}