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
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;

@Stateless
public class OfferTemplateCategoryApi extends BaseCrudApi<OfferTemplateCategory, OfferTemplateCategoryDto> {

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public OfferTemplateCategory create(OfferTemplateCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        if (offerTemplateCategoryService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(OfferTemplateCategory.class, postData.getCode());
        }

        OfferTemplateCategory offerTemplateCategory = new OfferTemplateCategory();
        offerTemplateCategory.setCode(postData.getCode());
        offerTemplateCategory.setDescription(postData.getDescription());
        offerTemplateCategory.setName(postData.getName());
        try {
            saveImage(offerTemplateCategory, postData.getImagePath(), postData.getImageBase64(), currentUser.getProvider().getCode());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        String parentCode = postData.getOfferTemplateCategoryCode();
        if (!StringUtils.isBlank(parentCode)) {
            if (postData.getCode().equals(parentCode)) {
                throw new InvalidParameterException("Invalid parent offer template category code - can not point to itself");
            }

            OfferTemplateCategory parentOfferTemplateCategory = offerTemplateCategoryService.findByCode(parentCode, provider, Arrays.asList("children"));
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

        offerTemplateCategoryService.create(offerTemplateCategory, currentUser);

        return offerTemplateCategory;
    }

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public OfferTemplateCategory update(OfferTemplateCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();
        String currentCode = StringUtils.isBlank(postData.getCurrentCode())?postData.getCode():postData.getCurrentCode();
        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(currentCode, provider);

        if (offerTemplateCategory == null) {
            throw new EntityAlreadyExistsException(OfferTemplateCategory.class, currentCode);
        }
        offerTemplateCategory.setCode(postData.getCode());
        offerTemplateCategory.setDescription(postData.getDescription());
        offerTemplateCategory.setName(postData.getName());

        try {
            saveImage(offerTemplateCategory, postData.getImagePath(), postData.getImageBase64(), currentUser.getProvider().getCode());
        } catch (IOException e1) {
            log.error("Invalid image data={}", e1.getMessage());
            throw new InvalidImageData();
        }

        String parentCode = postData.getOfferTemplateCategoryCode();
        if (!StringUtils.isBlank(parentCode)) {
            if (postData.getCode().equals(parentCode)) {
                throw new InvalidParameterException("Invalid parent offer template category code - can not point to itself");
            }

            OfferTemplateCategory parentOfferTemplateCategory = offerTemplateCategoryService.findByCode(parentCode, provider);
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

        offerTemplateCategory = offerTemplateCategoryService.update(offerTemplateCategory, currentUser);

        return offerTemplateCategory;
    }

    /**
     * 
     * @param code
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public OfferTemplateCategoryDto find(String code, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategoryDto offerTemplateCategoryDto = null;

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code, currentUser.getProvider());

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory);

        return offerTemplateCategoryDto;

    }

    /**
     * 
     * @param code
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public OfferTemplateCategoryDto find(String code, Provider provider, UriInfo uriInfo) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code, provider);

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, uriInfo.getBaseUri().toString());

        return offerTemplateCategoryDto;
    }

    /**
     * 
     * @param code
     * @param provider
     * @throws MeveoApiException
     */
    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code, currentUser.getProvider());

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        deleteImage(offerTemplateCategory, currentUser.getProvider().getCode());
        offerTemplateCategoryService.remove(offerTemplateCategory, currentUser);

    }

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public OfferTemplateCategory createOrUpdate(OfferTemplateCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {
       if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        String currentCode = StringUtils.isBlank(postData.getCurrentCode())?postData.getCode():postData.getCurrentCode();
        if (offerTemplateCategoryService.findByCode(currentCode, currentUser.getProvider()) == null) {
            return create(postData, currentUser);
        } else {
            return update(postData, currentUser);
        }
    }

    /**
     * 
     * @return
     * @throws MeveoApiException
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
     * 
     * @return
     * @throws MeveoApiException
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
     * @param code
     * @param currentUser
     * @return
     * @throws MeveoApiException
     */
    public OfferTemplateCategoryDto findByCode(String code, User currentUser) throws MeveoApiException {
        OfferTemplateCategoryDto offerTemplateCategoryDto = null;

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code, currentUser.getProvider());
        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }
        offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory);

        return offerTemplateCategoryDto;
    }

    /**
     * 
     * @param code
     * @param currentUser
     * @return
     * @throws EntityDoesNotExistsException
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws MeveoApiException
     */
    public OfferTemplateCategoryDto findByCode(String code, User currentUser, UriInfo uriInfo) throws EntityDoesNotExistsException, InvalidParameterException,
            MissingParameterException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code, currentUser.getProvider());

        if (offerTemplateCategory == null) {
            throw new EntityDoesNotExistsException(OfferTemplateCategory.class, code);
        }

        OfferTemplateCategoryDto offerTemplateCategoryDto = new OfferTemplateCategoryDto(offerTemplateCategory, uriInfo.getBaseUri().toString());

        return offerTemplateCategoryDto;
    }
}
