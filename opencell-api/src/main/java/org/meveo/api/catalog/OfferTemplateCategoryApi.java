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
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;

@Stateless
public class OfferTemplateCategoryApi extends BaseCrudApi<OfferTemplateCategory, OfferTemplateCategoryDto> {

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    /**
     * 
     * @param postData posted data to API
     * @return offer template category
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public OfferTemplateCategory create(OfferTemplateCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
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

        offerTemplateCategoryService.create(offerTemplateCategory);

        return offerTemplateCategory;
    }

    /**
     * 
     * @param postData posted data to API
     * @return offer template category
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
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
        offerTemplateCategory.setCode(StringUtils.isBlank(postData.getUpdatedCode())?postData.getCode():postData.getUpdatedCode());
        offerTemplateCategory.setDescription(postData.getDescription());
        offerTemplateCategory.setName(postData.getName());

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

        offerTemplateCategory = offerTemplateCategoryService.update(offerTemplateCategory);

        return offerTemplateCategory;
    }

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
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

        offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory);

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

        OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, uriInfo.getBaseUri().toString());

        return offerTemplateCategoryDto;
    }

    /**
     * 
     * @param code code of offer template category
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code);

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        deleteImage(offerTemplateCategory);
        offerTemplateCategoryService.remove(offerTemplateCategory);

    }

    /**
     * 
     * @param postData posted data to API
     * @return offer template category
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exceptions
     */
    public OfferTemplateCategory createOrUpdate(OfferTemplateCategoryDto postData) throws MeveoApiException, BusinessException {
       if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }        
        if (offerTemplateCategoryService.findByCode(postData.getCode()) == null) {
            return create(postData);
        } else {
            return update(postData);
        }
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
                OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory);
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
                OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, uriInfo.getBaseUri().toString());
                offerTemplateCategoryDtos.add(offerTemplateCategoryDto);
            }
        }

        return offerTemplateCategoryDtos;
    }

    /**
     * 
     * @param code code of offer template category

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
        offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory);

        return offerTemplateCategoryDto;
    }

    /**
     * @param uriInfo uri information
     * @param code code of offer template category

     * @return found offer template category
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws InvalidParameterException invalid parameter exception
     * @throws MissingParameterException missing parameter exception
     */
    public OfferTemplateCategoryDto findByCode(String code, UriInfo uriInfo) throws EntityDoesNotExistsException, InvalidParameterException,
            MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code);

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, uriInfo.getBaseUri().toString());

        return offerTemplateCategoryDto;
    }
}
