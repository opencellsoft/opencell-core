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

package org.meveo.admin.action.crm;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn.CustomFieldColumnUseEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.meveo.util.EntityCustomizationUtils;
import org.primefaces.model.DualListModel;
import org.reflections.Reflections;

/**
 * The Class CustomFieldTemplateBean.
 *
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.2.1
 */

@Named
@ViewScoped
public class CustomFieldTemplateBean extends UpdateMapTypeFieldBean<CustomFieldTemplate> {

    private static final long serialVersionUID = 9099292371182275568L;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private CustomizedEntityService customizedEntityService;
    
    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private ResourceBundle resourceMessages;
    
    @Inject
    private CustomTableService customTableService;

    private DualListModel<CustomFieldMatrixColumn> childEntityFieldDM;

    /**
     * To what entity class CFT should be copied to - a appliesTo value
     */
    private String copyCftTo;
    private Set<CustomizedEntity> allClassNames;

    public CustomFieldTemplateBean() {
        super(CustomFieldTemplate.class);
    }

    @Override
    public CustomFieldTemplate initEntity() {
        CustomFieldTemplate customFieldTemplate = super.initEntity();

        if (customFieldTemplate != null) {
            extractMapTypeFieldFromEntity(customFieldTemplate.getListValuesSorted(), "listValues");
        }

        return customFieldTemplate;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        CustomFieldTypeEnum fieldType = entity.getFieldType();
		if (!StringUtils.isBlank(entity.getDisplayFormat())) {
            if (fieldType == CustomFieldTypeEnum.LONG || fieldType == CustomFieldTypeEnum.DOUBLE) {
                try {
                    new DecimalFormat(entity.getDisplayFormat());
                } catch (IllegalArgumentException e) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.invalidDisplayFormat"));
                    return null;
                }
            }
            if (fieldType == CustomFieldTypeEnum.DATE) {
                try {
                    new SimpleDateFormat(entity.getDisplayFormat());
                } catch (IllegalArgumentException e) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.invalidDisplayFormat"));
                    return null;
                }
            }
        }

        if (fieldType == CustomFieldTypeEnum.LIST || fieldType ==CustomFieldTypeEnum.CHECKBOX_LIST) {
            entity.setListValues(new TreeMap<>());
            updateMapTypeFieldInEntity(entity.getListValues(), "listValues");
        }
        
        if (fieldType == CustomFieldTypeEnum.CUSTOM_TABLE_WRAPPER) {
        	CustomEntityTemplate cet = customEntityTemplateService.findByCodeOrDbTablename(entity.getCustomTableCodeEL());
        	if(cet == null) {
	            messages.error(new BundleKey("messages", "customFieldTemplate.doNotExist"),entity.getCustomTableCodeEL());
	            return null;
        	}
        }

        CustomFieldTemplate cfDuplicate = customFieldTemplateService.findByCodeAndAppliesTo(entity.getCode(), entity.getAppliesTo());
        if (cfDuplicate != null && !cfDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "customFieldTemplate.alreadyExists"));
            return null;
        }

        // Update childEntityColumns
        if (getEntity().getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
            List<String> cheColumns = new ArrayList<>();
            for (CustomFieldMatrixColumn cheColumn : childEntityFieldDM.getTarget()) {
                cheColumns.add(cheColumn.getCode());
            }
            getEntity().setChildEntityFieldsAsList(cheColumns);
        } else {
            getEntity().setChildEntityFields(null);
        }

        if (entity.getCalendar() != null) {
            entity.setCalendar(calendarService.retrieveIfNotManaged(entity.getCalendar()));
        }
        return super.saveOrUpdate(killConversation);
    }

    @Override
    protected IPersistenceService<CustomFieldTemplate> getPersistenceService() {
        return customFieldTemplateService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    /**
     * Autocomplete method for selecting a class/custom entity template for entity reference type Custom field template
     *
     * @param query Partial value entered
     * @return A list of matching values
     */
    public List<String> autocompleteClassNames(String query) {
        if(allClassNames == null){
            allClassNames = getAllClassName();
        }
        return allClassNames.stream()
                .filter(businessEntity -> isClassNameMatchQuery(query, businessEntity.getEntityClass()))
                .map(customizedEntity -> customizedEntity.getClassnameToDisplay())
                .distinct()
                .collect(Collectors.toList());
    }

    private Set<CustomizedEntity> getAllClassName() {
        Set<CustomizedEntity> allClassName = new HashSet<>();
        Reflections reflections = new Reflections("org.meveo.model");
        Set<CustomizedEntity> customizedBusinessEntities = reflections.getSubTypesOf(BusinessEntity.class).stream()
                .filter(businessEntity -> !Modifier.isAbstract(businessEntity.getModifiers()))
                .map(businessEntity -> new CustomizedEntity(businessEntity))
                .collect(Collectors.toSet());

        allClassName.addAll(customizedBusinessEntities);
        allClassName.addAll(customizedEntityService.getCustomizedEntities("", true, true, false, null, null));
        allClassName.addAll(customEntityTemplateService.listCustomTableTemplates().stream().map(i -> new CustomizedEntity(i.getName(), CustomEntityTemplate.class)).collect(
                Collectors.toList()));
        return allClassName;
    }

    private boolean isClassNameMatchQuery(String query, Class businessEntity) {
        return isEmpty(query) || businessEntity.getSimpleName().toLowerCase()
                .contains(query.toLowerCase());
    }

    /**
     * Autocomplete method for selecting a custom entity template for child entity reference type Custom field template
     *
     * @param query Partial value entered
     * @return A list of matching values
     */
    public List<String> autocompleteClassNamesCEIOnly(String query) {
        List<String> clazzNames = new ArrayList<>();

        List<CustomizedEntity> entities = customizedEntityService.getCustomizedEntities(query, true, false, false, null, null);

        for (CustomizedEntity customizedEntity : entities) {
            clazzNames.add(customizedEntity.getClassnameToDisplay());
        }

        return clazzNames;
    }

    /**
     * Autocomplete method for selecting a class that implement ICustomFieldEntity. Return a human readable class name. Used in conjunction with CustomFieldAppliesToConverter
     *
     * @param query Partial class name to match
     * @return list of class name suggestions
     */
    public List<String> autocompleteClassNamesHuman(String query) {
        List<String> clazzNames = new ArrayList<>();

        List<CustomizedEntity> entities = customizedEntityService.getCustomizedEntities(query, false, true, true, null, null);

        for (CustomizedEntity customizedEntity : entities) {
            clazzNames.add(customizedEntity.getClassnameToDisplayHuman());
        }

        return clazzNames;
    }

    public void updateDefaultValues() {

        if (entity.getFieldType() == CustomFieldTypeEnum.STRING && entity.getMaxValue() == null) {
            entity.setMaxValue(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING);
        }
        if (entity.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
            entity.setStorageType(CustomFieldStorageTypeEnum.LIST);
            entity.setVersionable(false);
        }
        if (entity.getStorageType() == CustomFieldStorageTypeEnum.MAP && entity.getMapKeyType() == null) {
            entity.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        }
        if (entity.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
            entity.setStorageType(CustomFieldStorageTypeEnum.MATRIX);
        }
        if (entity.getFieldType() == CustomFieldTypeEnum.CHECKBOX_LIST) {
        	 entity.setStorageType(CustomFieldStorageTypeEnum.LIST);
        }
    }

    public void resetChildEntityFields() {
        childEntityFieldDM = null;
    }

    public DualListModel<CustomFieldMatrixColumn> getChildEntityFieldListModel() {
        if (childEntityFieldDM == null && CustomFieldTemplate.retrieveCetCode(entity.getEntityClazz()) != null) {

            List<CustomFieldMatrixColumn> perksSource = new ArrayList<>();
            perksSource.add(new CustomFieldMatrixColumn("code", "Code"));
            perksSource.add(new CustomFieldMatrixColumn("description", "Description"));

            Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
                .findByAppliesTo(EntityCustomizationUtils.getAppliesTo(CustomEntityTemplate.class, CustomFieldTemplate.retrieveCetCode(entity.getEntityClazz())));

            for (CustomFieldTemplate cft : cfts.values()) {
                perksSource.add(new CustomFieldMatrixColumn(cft.getCode(), cft.getDescription()));
            }

            // Custom field template stores selected fields as a comma separated string of field codes.
            List<CustomFieldMatrixColumn> perksTarget = new ArrayList<CustomFieldMatrixColumn>();
            if (getEntity().getChildEntityFields() != null) {
                for (String fieldCode : getEntity().getChildEntityFieldsAsList()) {
                    if (fieldCode.equals("code")) {
                        perksTarget.add(new CustomFieldMatrixColumn("code", "Code"));
                    } else if (fieldCode.equals("description")) {
                        perksTarget.add(new CustomFieldMatrixColumn("description", "Description"));
                    } else if (cfts.containsKey(fieldCode)) {
                        CustomFieldTemplate cft = cfts.get(fieldCode);
                        perksTarget.add(new CustomFieldMatrixColumn(cft.getCode(), cft.getDescription()));
                    }
                }
            }
            perksSource.removeAll(perksTarget);
            childEntityFieldDM = new DualListModel<CustomFieldMatrixColumn>(perksSource, perksTarget);
        }
        return childEntityFieldDM;
    }

    public void setChildEntityFieldListModel(DualListModel<CustomFieldMatrixColumn> childEntityFieldDM) {
        this.childEntityFieldDM = childEntityFieldDM;
    }

    /**
     * Validate matrix columns of a custom field template
     *
     * @param cft Custom field template
     */
    public void validateMatrixColumns(CustomFieldTemplate cft) {

        if (cft.getStorageType() != CustomFieldStorageTypeEnum.MATRIX) {
            return;
        }

        boolean valid = true;

        if (cft.getMatrixColumns() == null || cft.getMatrixColumns().isEmpty()) {
            FacesMessage msg = new FacesMessage(resourceMessages.getString("customFieldTemplate.matrixColumn.error.atLeastOne"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            facesContext.addMessage(null, msg);
            valid = false;
        } else {
        	boolean columnExist=false;
        	boolean keyExist=false;
            for (CustomFieldMatrixColumn column : cft.getMatrixColumns()) {
                if (StringUtils.isBlank(column.getCode()) || StringUtils.isBlank(column.getLabel()) || column.getKeyType() == null) {
                    FacesMessage msg = new FacesMessage(resourceMessages.getString("customFieldTemplate.matrixColumn.error.missingFields"));
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    facesContext.addMessage(null, msg);
                    valid = false;
                    break;
                }
                if(!columnExist && CustomFieldColumnUseEnum.USE_KEY.equals(column.getColumnUse())){
                	columnExist=true;
                } else if(!keyExist && CustomFieldColumnUseEnum.USE_VALUE.equals(column.getColumnUse())){
                	keyExist=true;
                }
            }
            
            if(valid && !(columnExist && keyExist)) {
            	FacesMessage msg = new FacesMessage(resourceMessages.getString("customFieldTemplate.matrixColumn.error.atLeastOneKeyValue"));
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                facesContext.addMessage(null, msg);
                valid = false;
            }
        }
        

        if (!valid) {
            facesContext.validationFailed();
            facesContext.renderResponse();
        }
    }

    public String getCopyCftTo() {
        return copyCftTo;
    }

    public void setCopyCftTo(String copyCftTo) {
        this.copyCftTo = copyCftTo;
    }

    /**
     * Copy and associate custom field template with another entity class
     *
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public void copyCFT() throws BusinessException {

        if (copyCftTo == null) {
            throw new ValidationException("Not specified what class to copy CFT to", "customFieldTemplate.copyCFT.targetNotSpecified");
        }

        entity = customFieldTemplateService.refreshOrRetrieve(entity);
        customFieldTemplateService.copyCustomFieldTemplate(entity, copyCftTo);

        messages.info(new BundleKey("messages", "customFieldTemplate.copyCFT.ok"));
    }

    @ActionMethod
    public void updateUniqueConstraint(ValueChangeEvent valueChangeEvent){
        entity.setUniqueConstraint((Boolean) valueChangeEvent.getNewValue());
    }
}