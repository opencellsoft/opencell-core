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

package org.meveo.api.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;

@Stateless
public class OfferTemplateCategoryApi extends BaseCrudApi<OfferTemplateCategory, OfferTemplateCategoryDto> {

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Override
    public OfferTemplateCategory create(OfferTemplateCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(OfferTemplateCategory.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getName())) {
            missingParameters.add("name");
        }

        handleMissingParametersAndValidate(postData);

        if (offerTemplateCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(OfferTemplateCategory.class, postData.getCode());
        }

        OfferTemplateCategory offerTemplateCategory = new OfferTemplateCategory();
        offerTemplateCategory.setCode(postData.getCode());
        offerTemplateCategory.setDescription(postData.getDescription());
        offerTemplateCategory.setName(postData.getName());
        if(postData.getLanguageDescriptions() != null) {
            offerTemplateCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        }
        if (postData.isActive() != null) {
            offerTemplateCategory.setActive(postData.isActive());
        } else if (postData.isDisabled() != null) {
            offerTemplateCategory.setDisabled(postData.isDisabled());
        }
        try {
            saveImage(offerTemplateCategory, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        String parentCode = postData.getOfferTemplateCategoryCode();
        if (!StringUtils.isBlank(parentCode)) {
            if (postData.getCode().equals(parentCode)) {
                throw new InvalidParameterException("Invalid parent offer template category code - can not point to itself");
            }

            OfferTemplateCategory parentOfferTemplateCategory = offerTemplateCategoryService.findByCode(parentCode, Arrays.asList("children"));
            if (parentOfferTemplateCategory == null) {
                throw new EntityDoesNotExistsException(OfferTemplateCategory.class, parentCode);
            }

            if (CollectionUtils.isNotEmpty(parentOfferTemplateCategory.getChildren())) {
                OfferTemplateCategory lastChild = parentOfferTemplateCategory.getChildren().get(parentOfferTemplateCategory.getChildren().size() - 1);
                offerTemplateCategory.setOrderLevel(lastChild.getOrderLevel() + 1);
            } else {
                offerTemplateCategory.setOrderLevel(1);
            }
            offerTemplateCategory.setOfferTemplateCategory(parentOfferTemplateCategory);
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), offerTemplateCategory, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        offerTemplateCategoryService.create(offerTemplateCategory);

        return offerTemplateCategory;
    }

    @Override
    public OfferTemplateCategory update(OfferTemplateCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            missingParameters.add("name");
        }

        handleMissingParametersAndValidate(postData);

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postData.getCode());

        if (offerTemplateCategory == null) {
            throw new EntityAlreadyExistsException(OfferTemplateCategory.class, postData.getCode());
        }
        offerTemplateCategory.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        offerTemplateCategory.setDescription(postData.getDescription());
        offerTemplateCategory.setName(postData.getName());
        if(postData.getLanguageDescriptions() != null) {
            offerTemplateCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        }

        try {
            saveImage(offerTemplateCategory, postData.getImagePath(), postData.getImageBase64());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        String parentCode = postData.getOfferTemplateCategoryCode();
        if (!StringUtils.isBlank(parentCode)) {
            if (postData.getCode().equals(parentCode)) {
                throw new InvalidParameterException("Invalid parent offer template category code - can not point to itself");
            }

            OfferTemplateCategory parentOfferTemplateCategory = offerTemplateCategoryService.findByCode(parentCode);
            if (parentOfferTemplateCategory == null) {
                throw new EntityDoesNotExistsException(OfferTemplateCategory.class, parentCode);
            }

            if (CollectionUtils.isNotEmpty(parentOfferTemplateCategory.getChildren())) {
                OfferTemplateCategory lastChild = parentOfferTemplateCategory.getChildren().get(parentOfferTemplateCategory.getChildren().size() - 1);
                offerTemplateCategory.setOrderLevel(lastChild.getOrderLevel() + 1);
            } else {
                offerTemplateCategory.setOrderLevel(1);
            }
            offerTemplateCategory.setOfferTemplateCategory(parentOfferTemplateCategory);
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), offerTemplateCategory, false, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        offerTemplateCategory = offerTemplateCategoryService.update(offerTemplateCategory);

        return offerTemplateCategory;
    }

    @Override
    public OfferTemplateCategoryDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategoryDto offerTemplateCategoryDto = null;

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code);

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, entityToDtoConverter.getCustomFieldsDTO(offerTemplateCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return offerTemplateCategoryDto;

    }

    /**
     * 
     * @param code offer template category
     * @param uriInfo uri info
     * @return offer template category.
     * @throws MeveoApiException meveo api exception.
     */
    public OfferTemplateCategoryDto find(String code, UriInfo uriInfo) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code);

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, entityToDtoConverter.getCustomFieldsDTO(offerTemplateCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE),
            uriInfo.getBaseUri().toString());

        return offerTemplateCategoryDto;
    }

    /**
     * 
     * @return list of offer category
     * @throws MeveoApiException meveo api exception
     */
    public List<OfferTemplateCategoryDto> list() throws MeveoApiException {
        List<OfferTemplateCategoryDto> offerTemplateCategoryDtos = new ArrayList<OfferTemplateCategoryDto>();

        List<OfferTemplateCategory> offerTemplateCategories = offerTemplateCategoryService.list();
        if (offerTemplateCategories != null && !offerTemplateCategories.isEmpty()) {
            for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
                OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory,
                    entityToDtoConverter.getCustomFieldsDTO(offerTemplateCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                offerTemplateCategoryDtos.add(offerTemplateCategoryDto);
            }
        }

        return offerTemplateCategoryDtos;
    }

    /**
     * Returns All or only the active segment list
     * 
     * @return list of offer category
     * @throws MeveoApiException meveo api exception
     */
    public List<OfferTemplateCategoryDto> list(Boolean active) throws MeveoApiException {
        List<OfferTemplateCategoryDto> offerTemplateCategoryDtos = new ArrayList<OfferTemplateCategoryDto>();

        List<OfferTemplateCategory> offerTemplateCategories = offerTemplateCategoryService.list(active);
        if (offerTemplateCategories != null && !offerTemplateCategories.isEmpty()) {
            for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
                OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto();
                offerTemplateCategoryDto.setCode(offerTemplateCategory.getCode());
                offerTemplateCategoryDto.setName(offerTemplateCategory.getName());
                offerTemplateCategoryDto.setDescription(offerTemplateCategory.getDescription());
                offerTemplateCategoryDtos.add(offerTemplateCategoryDto);
            }
        }

        return offerTemplateCategoryDtos;
    }

    /**
     * 
     * @param uriInfo uri infos
     * @return list of offer template category
     * @throws MeveoApiException meveo api exception
     */
    public List<OfferTemplateCategoryDto> list(UriInfo uriInfo) throws MeveoApiException {
        List<OfferTemplateCategoryDto> offerTemplateCategoryDtos = new ArrayList<OfferTemplateCategoryDto>();

        List<OfferTemplateCategory> offerTemplateCategories = offerTemplateCategoryService.listActive();
        if (offerTemplateCategories != null && !offerTemplateCategories.isEmpty()) {
            for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
                OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory,
                    entityToDtoConverter.getCustomFieldsDTO(offerTemplateCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), uriInfo.getBaseUri().toString());
                offerTemplateCategoryDtos.add(offerTemplateCategoryDto);
            }
        }

        return offerTemplateCategoryDtos;
    }

    /**
     * 
     * @param code code of offer template category
     * 
     * @return offer template category
     * @throws MeveoApiException meveo api exception
     */
    public OfferTemplateCategoryDto findByCode(String code) throws MeveoApiException {
        OfferTemplateCategoryDto offerTemplateCategoryDto = null;

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code);
        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }
        offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, entityToDtoConverter.getCustomFieldsDTO(offerTemplateCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return offerTemplateCategoryDto;
    }

    /**
     * @param uriInfo uri information
     * @param code code of offer template category
     * 
     * @return found offer template category
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws InvalidParameterException invalid parameter exception
     * @throws MissingParameterException missing parameter exception
     */
    public OfferTemplateCategoryDto findByCode(String code, UriInfo uriInfo) throws EntityDoesNotExistsException, InvalidParameterException, MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code);

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, entityToDtoConverter.getCustomFieldsDTO(offerTemplateCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE),
            uriInfo.getBaseUri().toString());

        return offerTemplateCategoryDto;
    }
}
