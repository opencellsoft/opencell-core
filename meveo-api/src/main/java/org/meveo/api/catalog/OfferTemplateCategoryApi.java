package org.meveo.api.catalog;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;

@Stateless
public class OfferTemplateCategoryApi extends BaseApi {

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void create(OfferTemplateCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

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
        } else {

            OfferTemplateCategory offerTemplateCategory = new OfferTemplateCategory();
            offerTemplateCategory.setCode(postData.getCode());
            offerTemplateCategory.setDescription(postData.getDescription());
            offerTemplateCategory.setName(postData.getName());

            if (postData.getImageByteValue() != null) {
                byte[] byteContent = postData.getImageByteValue().getBytes();
                try {
                    Blob blobImg = new SerialBlob(byteContent);
                    offerTemplateCategory.setImage(blobImg);

                } catch (SerialException e) {
                    throw new MeveoApiException("Invalid base64 encoded image string.");

                } catch (SQLException e) {
                    throw new MeveoApiException("System error.");
                }
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

        }
    }

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void update(OfferTemplateCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getName())) {
            missingParameters.add("name");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(postData.getCode(), provider);

        if (offerTemplateCategory == null) {
            throw new EntityAlreadyExistsException(OfferTemplateCategory.class, postData.getCode());
        }

        offerTemplateCategory.setDescription(postData.getDescription());
        offerTemplateCategory.setName(postData.getName());

        if (postData.getImageByteValue() != null) {
            byte[] byteContent = postData.getImageByteValue().getBytes();
            try {
                Blob blobImg = new SerialBlob(byteContent);
                offerTemplateCategory.setImage(blobImg);

            } catch (SerialException e) {
                throw new MeveoApiException("Invalid base64 encoded image string.");

            } catch (SQLException e) {
                throw new MeveoApiException("System error.");
            }
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

        offerTemplateCategoryService.update(offerTemplateCategory, currentUser);
    }

    /**
     * 
     * @param code
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public OfferTemplateCategoryDto find(String code, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        OfferTemplateCategoryDto offerTemplateCategoryDto = null;

        OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findByCode(code, provider);

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

        offerTemplateCategoryService.remove(offerTemplateCategory, currentUser);

    }

    /**
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void createOrUpdate(OfferTemplateCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        String code = postData.getCode();

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (offerTemplateCategoryService.findByCode(code, currentUser.getProvider()) == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

    /**
     * 
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
