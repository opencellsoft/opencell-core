package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.BusinessServiceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessServiceModelApi extends BaseApi {

    @Inject
    private BusinessServiceService businessServiceService;

    public void create(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        if (businessServiceService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(BusinessServiceModel.class, postData.getCode());
        }

        businessServiceService.create(BusinessServiceModelDto.fromDTO(postData, null), currentUser);
    }

    public void update(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        BusinessServiceModel bsm = businessServiceService.findByCode(postData.getCode(), currentUser.getProvider());
        if (bsm == null) {
            throw new EntityDoesNotExistsException(BusinessServiceModel.class, postData.getCode());
        }

        businessServiceService.update(BusinessServiceModelDto.fromDTO(postData, bsm), currentUser);
    }

    public BusinessServiceModelDto find(String businessServiceModelCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(businessServiceModelCode)) {
            missingParameters.add("businessServiceModelCode");
        }
        handleMissingParameters();

        BusinessServiceModel businessServiceModel = businessServiceService.findByCode(businessServiceModelCode, provider);
        if (businessServiceModel != null) {
            BusinessServiceModelDto businessServiceModelDto = new BusinessServiceModelDto();
            businessServiceModelDto.setCode(businessServiceModel.getCode());
            businessServiceModelDto.setDescription(businessServiceModel.getDescription());
            if (businessServiceModel.getServiceTemplate() != null) {
                businessServiceModelDto.setServiceTemplateCode(businessServiceModel.getServiceTemplate().getCode());
            }
            if (businessServiceModel.getScript() != null) {
                businessServiceModelDto.setScriptCode(businessServiceModel.getScript().getCode());
            }

            return businessServiceModelDto;
        }

        throw new EntityDoesNotExistsException(BusinessServiceModel.class, businessServiceModelCode);

    }

    public void remove(String businessServiceModelCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(businessServiceModelCode)) {
            missingParameters.add("businessServiceModelCode");
        }

        handleMissingParameters();
        BusinessServiceModel businessServiceModel = businessServiceService.findByCode(businessServiceModelCode, provider);
        if (businessServiceModel == null) {
            throw new EntityDoesNotExistsException(BusinessServiceModel.class, businessServiceModelCode);
        }

        businessServiceService.remove(businessServiceModel);
    }

    public void createOrUpdate(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
        BusinessServiceModel businessServiceModel = businessServiceService.findByCode(postData.getCode(), currentUser.getProvider());
        if (businessServiceModel == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }
}
