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

package org.meveo.api;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldMatrixColumnDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.meveo.util.EntityCustomizationUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */

@Stateless
public class CustomFieldTemplateApi extends BaseApi {

    public static final String FIELD_CODE_REGEX = "^[A-Za-z0-9_]+$";
    @Inject
    private CalendarService calendarService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CustomizedEntityService customizedEntityService;

    @Inject
    CustomEntityTemplateService customEntityTemplateService;

    public void create(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {
        create(postData, appliesTo, true);
    }

    public void createWithoutUniqueConstraint(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {
        create(postData, appliesTo, false);
    }

	private void create(CustomFieldTemplateDto postData, String appliesTo, Boolean updateUniqueConstraint) {
		if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        } else if (!org.meveo.commons.utils.StringUtils.isMatch(postData.getCode(), FIELD_CODE_REGEX)) {
            throw new BusinessException(String.format("code must match %s", FIELD_CODE_REGEX));
        }
        if (StringUtils.isBlank(postData.getDescription())) {
            missingParameters.add("description");
        }
        if (appliesTo == null && StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }
        if (postData.getFieldType() == null) {
            missingParameters.add("fieldType");
        }
        if (postData.getStorageType() == null) {
            missingParameters.add("storageType");
        }
        if (postData.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && (postData.getMatrixColumns() == null || postData.getMatrixColumns().isEmpty())) {
            missingParameters.add("matrixColumns");

        } else if (postData.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            for (CustomFieldMatrixColumnDto columnDto : postData.getMatrixColumns()) {
                if (StringUtils.isBlank(columnDto.getCode())) {
                    missingParameters.add("matrixColumns/code");
                }
                if (StringUtils.isBlank(columnDto.getLabel())) {
                    missingParameters.add("matrixColumns/label");
                }
                if (columnDto.getKeyType() == null) {
                    missingParameters.add("matrixColumns/keyType");
                }
            }
        }

        if (postData.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY
                && (postData.getStorageType() != CustomFieldStorageTypeEnum.LIST || (postData.isVersionable() != null && postData.isVersionable()))) {
            throw new InvalidParameterException("Custom field of type CHILD_ENTITY only supports unversioned values and storage type of LIST");
        }
        if (postData.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY
                && (postData.getChildEntityFieldsForSummary() == null || postData.getChildEntityFieldsForSummary().isEmpty())) {
            missingParameters.add("childEntityFieldsForSummary");
        }
        handleMissingParameters();
        checkWhenURL(postData);

        if (appliesTo != null) {
            postData.setAppliesTo(appliesTo);

        } else {
            // Support for old API
            if (postData.getAppliesTo() == null && postData.getAccountLevel() != null) {
                appliesTo = postData.getAccountLevel();
            } else {
                appliesTo = postData.getAppliesTo();
            }
        }

        // Support for old names
        appliesTo = updateAppliesToToNewValue(appliesTo);
        postData.setAppliesTo(appliesTo);

        if (!getCustomizedEntitiesAppliesTo().contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        if (customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo) != null) {
            throw new EntityAlreadyExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        CustomFieldTemplate cft = fromDTO(postData, appliesTo, null);
        if(updateUniqueConstraint) {
        	customFieldTemplateService.create(cft);
        } else {
        	customFieldTemplateService.createWithoutUniqueConstraint(cft);
        }
	}

    private void checkWhenURL(CustomFieldTemplateDto postData) {
        if (CustomFieldTypeEnum.URL == postData.getFieldType()
                && postData.getDefaultValue() != null
                && !postData.getDefaultValue().isEmpty()) {


            if (postData.getRegExp() != null
                    && !postData.getRegExp().isEmpty()
                    && !postData.getDefaultValue().matches(postData.getRegExp())) {

                throw new InvalidParameterException("Wrong URL format. URL should match regular expression " + postData.getRegExp());

            } else {
                try {
                    new URL(postData.getDefaultValue());
                } catch (Exception exception){
                     throw new InvalidParameterException("Wrong URL format." );
                }

            }

        }
    }

    public void update(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {
        update(postData, appliesTo, false);
    }
    
    public void updateWithoutUniqueConstraint(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {
        update(postData, appliesTo, true);
    }

	private void update(CustomFieldTemplateDto postData, String appliesTo, boolean withoutUniqueConstraint) {
		if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (appliesTo == null && StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }

        if (postData.getMatrixColumns() != null) {
            for (CustomFieldMatrixColumnDto columnDto : postData.getMatrixColumns()) {
                if (StringUtils.isBlank(columnDto.getCode())) {
                    missingParameters.add("matrixColumns/code");
                }
                if (StringUtils.isBlank(columnDto.getLabel())) {
                    missingParameters.add("matrixColumns/label");
                }
                if (columnDto.getKeyType() == null) {
                    missingParameters.add("matrixColumns/keyType");
                }
            }
        }

        handleMissingParameters();
        checkWhenURL(postData);

        if (appliesTo != null) {
            postData.setAppliesTo(appliesTo);

        } else {
            // Support for old API
            if (postData.getAppliesTo() == null && postData.getAccountLevel() != null) {
                appliesTo = postData.getAccountLevel();
            } else {
                appliesTo = postData.getAppliesTo();
            }
        }

        // Support for old names
        appliesTo = updateAppliesToToNewValue(appliesTo);
        postData.setAppliesTo(appliesTo);

        if (!getCustomizedEntitiesAppliesTo().contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo);
        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, postData.getCode());
        }

        if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY && postData.isVersionable() != null && postData.isVersionable()) {
            throw new InvalidParameterException("Custom field of type CHILD_ENTITY only supports unversioned values and storage type of LIST");
        }
        if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY && (cft.getChildEntityFields() == null || postData.getChildEntityFieldsForSummary().isEmpty())) {
            missingParameters.add("childEntityFieldsForSummary");
        }

        CustomFieldTemplate oldCft = SerializationUtils.clone(cft);
        cft = fromDTO(postData, appliesTo, cft);
        
		if(withoutUniqueConstraint) {
        	customFieldTemplateService.updateWithoutUniqueConstraint(oldCft, cft);
        }else {
        	customFieldTemplateService.update(oldCft, cft);
        }
	}

    public void remove(String code, String appliesTo) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        // Support for old names
        appliesTo = updateAppliesToToNewValue(appliesTo);

        if (!getCustomizedEntitiesAppliesTo().contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(code, appliesTo);
        if (cft != null) {
            customFieldTemplateService.remove(cft.getId());
        } else {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
        }
    }

    /**
     * Enable or disable Custom field template
     * 
     * @param code CFT code
     * @param appliesTo Entity it Applies to
     * @param enable Should CFT be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String code, String appliesTo, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        // Support for old names
        appliesTo = updateAppliesToToNewValue(appliesTo);

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(code, appliesTo);
        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code);
        }
        if (enable) {
            customFieldTemplateService.enable(cft);
        } else {
            customFieldTemplateService.disable(cft);
        }
    }

    /**
     * Find Custom Field Template by its code and appliesTo attributes.
     * 
     * @param code Custom Field Template code
     * @param appliesTo Applies to
     * @return DTO
     * @throws EntityDoesNotExistsException Custom Field Template was not found
     * @throws InvalidParameterException AppliesTo value is incorrect
     * @throws MissingParameterException A parameter, necessary to find an Custom Field Template, was not provided
     */
    public CustomFieldTemplateDto find(String code, String appliesTo) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        // Support for old names
        appliesTo = updateAppliesToToNewValue(appliesTo);

        if (!getCustomizedEntitiesAppliesTo().contains(appliesTo)) {
            throw new InvalidParameterException("appliesTo", appliesTo);
        }

        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesToNoCache(code, appliesTo);

        if (cft == null) {
            throw new EntityDoesNotExistsException(CustomFieldTemplate.class, code + "/" + appliesTo);
        }
        CustomFieldTemplateDto customFieldTemplateDto = new CustomFieldTemplateDto(cft);
        customFieldTemplateDto.setReferenceTable(customEntityTemplateService.getReferenceTable(cft));
        return customFieldTemplateDto;
    }

    /**
     * Same as find method, only ignore EntityDoesNotExistException exception and return Null instead.
     * 
     * @param code Custom Field Template code
     * @param appliesTo Applies to
     * @return DTO or Null if not found
     * @throws InvalidParameterException AppliesTo value is incorrect
     * @throws MissingParameterException A parameter, necessary to find an Custom Field Template, was not provided
     */
    public CustomFieldTemplateDto findIgnoreNotFound(String code, String appliesTo) throws MissingParameterException, InvalidParameterException {
        try {
            return find(code, appliesTo);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }

    public void createOrUpdate(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {
        createOrUpdate(postData, appliesTo, false);
    }
    
    public void createOrUpdateWithoutUniqueConstraint(CustomFieldTemplateDto postData, String appliesTo) throws MeveoApiException, BusinessException {
        createOrUpdate(postData, appliesTo, true);
    }

	private void createOrUpdate(CustomFieldTemplateDto postData, String appliesTo, boolean withoutUniqueConstraint) {
		if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (appliesTo == null && StringUtils.isBlank(postData.getAccountLevel()) && StringUtils.isBlank(postData.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();
        
        if(StringUtils.isNotBlank(postData.getEntityClazz())) {
            List<String> cetArrays = Arrays.stream(postData.getEntityClazz().split("-"))
                                           .map(String::trim)
                                           .collect(Collectors.toList());

            checkEntityClazzIsPresent(cetArrays.get(0));
            if(cetArrays.size() > 1 && StringUtils.isNotBlank(cetArrays.get(1))) {
                CustomEntityTemplate cet = customEntityTemplateService.findByCode(cetArrays.get(1));
                if(cet == null) {
                    throw new EntityDoesNotExistsException(CustomEntityTemplate.class, cetArrays.get(1));
                }
            }
        }

        if (appliesTo != null) {
            postData.setAppliesTo(appliesTo);

        } else {
            // Support for old API
            if (postData.getAppliesTo() == null && postData.getAccountLevel() != null) {
                appliesTo = postData.getAccountLevel();
            } else {
                appliesTo = postData.getAppliesTo();
            }
        }

        // Support for old names
        appliesTo = updateAppliesToToNewValue(appliesTo);

        CustomFieldTemplate customFieldTemplate = customFieldTemplateService.findByCodeAndAppliesToNoCache(postData.getCode(), appliesTo);
		if(withoutUniqueConstraint) {
			if (customFieldTemplate == null) {
	            createWithoutUniqueConstraint(postData, appliesTo);
	        } else {
	            updateWithoutUniqueConstraint(postData, appliesTo);
	        }
		}else {
	        if (customFieldTemplate == null) {
	            create(postData, appliesTo);
	        } else {
	            update(postData, appliesTo);
	        }
		}
	}

    /**
     * Check if entity class is present on the classpath
     * @param clazzEntity
     */
    private static void checkEntityClazzIsPresent(String clazzEntity) {
        try {
            Class.forName(clazzEntity);
        } catch (ClassNotFoundException e) {
            throw new BusinessException("Unknown entity class '" + clazzEntity + "'", e);
        }
    }

    protected CustomFieldTemplate fromDTO(CustomFieldTemplateDto dto, String appliesTo, CustomFieldTemplate cftToUpdate) throws InvalidParameterException {

        // Set default values
        if (dto.getFieldType() == CustomFieldTypeEnum.STRING && dto.getMaxValue() == null) {
            dto.setMaxValue(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING);
        }

        CustomFieldTemplate cft = cftToUpdate;
        if (cftToUpdate == null) {
            cft = new CustomFieldTemplate();
            cft.setCode(dto.getCode());
            cft.setFieldType(dto.getFieldType());
            cft.setStorageType(dto.getStorageType());
            cft.setGuiPosition(dto.getGuiPosition());
            if (appliesTo == null) {

                // Support for old API
                if (dto.getAccountLevel() != null) {
                    appliesTo = dto.getAccountLevel();
                } else {
                    appliesTo = dto.getAppliesTo();
                }
            }
            cft.setAppliesTo(appliesTo);

            if (dto.isDisabled() != null) {
                cft.setDisabled(dto.isDisabled());
            }
        }

        if (dto.getDisplayFormat() != null) {
            cft.setDisplayFormat(dto.getDisplayFormat());
        }

        if (dto.getDescription() != null) {
            cft.setDescription(dto.getDescription());
        }

        if (dto.getDefaultValue() != null) {
            cft.setDefaultValue(dto.getDefaultValue());
        }
        if (dto.isUseInheritedAsDefaultValue() != null) {
            cft.setUseInheritedAsDefaultValue(dto.isUseInheritedAsDefaultValue());
        }
        if (dto.isValueRequired() != null) {
            cft.setValueRequired(dto.isValueRequired());
        }
        if (dto.isVersionable() != null) {
            cft.setVersionable(dto.isVersionable());
        }
        if (dto.isTriggerEndPeriodEvent() != null) {
            cft.setTriggerEndPeriodEvent(dto.isTriggerEndPeriodEvent());
        }
        if (dto.getEntityClazz() != null) {
            cft.setEntityClazz(org.apache.commons.lang3.StringUtils.trimToNull(dto.getEntityClazz()));
        }
        if (dto.isAllowEdit() != null) {
            cft.setAllowEdit(dto.isAllowEdit());
        }
        if (dto.isHideOnNew() != null) {
            cft.setHideOnNew(dto.isHideOnNew());
        }
        if (dto.getMinValue() != null) {
            cft.setMinValue(dto.getMinValue());
        }
        if (dto.getMaxValue() != null) {
            cft.setMaxValue(dto.getMaxValue());
        }
        if (dto.getRegExp() != null) {
            cft.setRegExp(dto.getRegExp());
        }
        if (dto.getGuiPosition() != null) {
            cft.setGuiPosition(dto.getGuiPosition());
        }
        if (dto.getApplicableOnEl() != null) {
            cft.setApplicableOnEl(dto.getApplicableOnEl());
        }

        if ((cft.getFieldType() == CustomFieldTypeEnum.LIST || cft.getFieldType() == CustomFieldTypeEnum.CHECKBOX_LIST) && dto.getListValues() != null) {
            cft.setListValues(dto.getListValues());
        }

        if (dto.getMapKeyType() != null) {
            cft.setMapKeyType(dto.getMapKeyType());
        }
        if (dto.getIndexType() != null) {
            cft.setIndexType(dto.getIndexType());
        }
        if (dto.getTags() != null) {
            cft.setTags(dto.getTags());
        }
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP && cft.getMapKeyType() == null) {
            cft.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        }

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && dto.getMatrixColumns() != null) {
            if (cft.getMatrixColumns() == null) {
                cft.setMatrixColumns(new ArrayList<CustomFieldMatrixColumn>());
            } else {
                cft.getMatrixColumns().clear();
            }

            for (CustomFieldMatrixColumnDto columnDto : dto.getMatrixColumns()) {
                cft.getMatrixColumns().add(CustomFieldMatrixColumnDto.fromDto(columnDto));
            }
        }

        if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
            cft.setStorageType(CustomFieldStorageTypeEnum.LIST);
            cft.setVersionable(false);
            if (dto.getChildEntityFieldsForSummary() != null) {
                cft.setChildEntityFieldsAsList(dto.getChildEntityFieldsForSummary());
            }
        }

        if (dto.getCalendar() != null) {
            if (StringUtils.isBlank(dto.getCalendar())) {
                cft.setCalendar(null);
            } else {
                Calendar calendar = calendarService.findByCode(dto.getCalendar());
                if (calendar != null) {
                    cft.setCalendar(calendar);
                } else {
                    cft.setCalendar(null);
                }
            }
        }

        if (dto.getLanguageDescriptions() != null) {
            cft.setDescriptionI18n(convertMultiLanguageToMapOfValues(dto.getLanguageDescriptions(), cft.getDescriptionI18n()));
        }

        if (dto.getNbDecimal() != null) {
            cft.setNbDecimal(dto.getNbDecimal());
        }
        if (dto.getRoundingMode() != null) {
            cft.setRoundingMode(dto.getRoundingMode());
        }

        if (dto.getUniqueConstraint() != null) {
            cft.setUniqueConstraint(dto.getUniqueConstraint());
        }

        if (dto.getCustomTableCodeEL() != null) {
            cft.setCustomTableCodeEL(dto.getCustomTableCodeEL());
        }
        if (dto.getDataFilterEL() != null) {
            cft.setDataFilterEL(dto.getDataFilterEL());
        }
        if (dto.getFieldsEL() != null) {
            cft.setFieldsEL(dto.getFieldsEL());
        }
        cft.setAnonymizeGdpr(dto.isAnonymize());
        return cft;

    }

    private List<String> getCustomizedEntitiesAppliesTo() {
        List<String> cftAppliesto = new ArrayList<String>();
        List<CustomizedEntity> entities = customizedEntityService.getCustomizedEntities(null, false, true, true, null, null);
        for (CustomizedEntity customizedEntity : entities) {
            cftAppliesto.add(EntityCustomizationUtils.getAppliesTo(customizedEntity.getEntityClass(), customizedEntity.getEntityCode()));
        }
        return cftAppliesto;
    }

    /**
     * Change old appliesTo value to a new one
     * 
     * @param appliesTo Old appliesTo value
     * @return New appliesTo value
     */
    private String updateAppliesToToNewValue(String appliesTo) {

        if (appliesTo == null) {
            return null;
        }

        if (appliesTo.equals("PROVIDER")) {
            return "Provider";
        } else if (appliesTo.equals("PRODUCT")) {
            return "ProductTemplate";
        } else if (appliesTo.equals("PRODUCT_INSTANCE")) {
            return "ProductInstance";
        } else if (appliesTo.equals("OFFER")) {
            return "OfferTemplate";
        } else if (appliesTo.equals("SELLER")) {
            return "Seller";
        } else if (appliesTo.equals("CUST")) {
            return "Customer";
        } else if (appliesTo.equals("CA")) {
            return "CustomerAccount";
        } else if (appliesTo.equals("BA")) {
            return "BillingAccount";
        } else if (appliesTo.equals("UA")) {
            return "UserAccount";
        } else if (appliesTo.equals("SERVICE")) {
            return "ServiceTemplate";
        } else if (appliesTo.equals("SERVICE_INSTANCE")) {
            return "ServiceInstance";
        } else if (appliesTo.equals("SUB")) {
            return "Subscription";
        } else if (appliesTo.equals("ACC")) {
            return "Access";
        } else if (appliesTo.equals("CHARGE")) {
            return "ChargeTemplate";
        } else if (appliesTo.equals("PRICEPLAN")) {
            return "PricePlanMatrix";
        } else if (appliesTo.equals("BILLING_CYCLE")) {
            return "BillingCycle";
        } else if (appliesTo.equals("TAX")) {
            return "Tax";
        } else if (appliesTo.equals("INV_CAT")) {
            return "InvoiceCategory";
        } else if (appliesTo.equals("INVOICE")) {
            return "Invoice";
        } else if (appliesTo.equals("ACCT_CODE")) {
            return "AccountingCode";
        } else if (appliesTo.equals("FILTER")) {
            return "Filter";
        } else if (appliesTo.equals("QUOTE")) {
            return "Quote";
        } else if (appliesTo.equals("ORDER")) {
            return "Order";
        } else if (appliesTo.equals("USER")) {
            return "User";
        } else if (appliesTo.equals("JOB")) {
            return "JobInstance";
        } else if (appliesTo.equals("DISCOUNT_PLAN_INSTANCE")) {
            return "DiscountPlanInstance";
        } else if (appliesTo.equals("DISCOUNT_PLAN")) {
            return "DiscountPlan";
        } else if (appliesTo.equals("OFFER_CATEGORY")) {
            return "OfferTemplateCategory";
        } else if (appliesTo.equals("INV_SUB_CAT")) {
            return "InvoiceSubCategory";
        } else if (appliesTo.equals("ACC_OP")) {
            return "AccountOperation";
        } else if (appliesTo.equals("BILLING_RUN")) {
            return "BillingRun";
        } else if (appliesTo.equals("INVOICE_TYPE")) {
            return "InvoiceType";
        } else if (appliesTo.equals("DISCOUNT_PLAN_ITEM")) {
            return "DiscountPlanItem";
        } else if (appliesTo.equals("OTH_TR")) {
            return "OtherTransaction";
        } else if (appliesTo.equals("REPORT")) {
            return "ReportExtract";
        } else if (appliesTo.equals("BUNDLE")) {
            return "BundleTemplate";
        } else if (appliesTo.equals("PAYMENT_SCH_INSTANCE")) {
            return "PaymentScheduleInstance";
        } else if (appliesTo.equals("DDREQ_BUILDER")) {
            return "DDRequestBuilder";
        } else if (appliesTo.equals("PAYMENT_SCH")) {
            return "PaymentScheduleTemplate";
        } else if (appliesTo.startsWith("JOB_")) {
            return "JobInstance_" + appliesTo.substring(4);
        }
        return appliesTo;

    }
}
