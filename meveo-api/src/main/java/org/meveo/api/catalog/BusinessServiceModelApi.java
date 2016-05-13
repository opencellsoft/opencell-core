package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.module.ModuleApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessServiceModelApi extends BaseApi {

    @Inject
    private BusinessServiceModelService businessServiceModelService;

    @Inject
    private MeveoModuleService meveoModuleService;

    @Inject
    private ModuleApi moduleApi;

    public void create(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getServiceTemplate() == null || StringUtils.isBlank(postData.getServiceTemplate().getCode())) {
            missingParameters.add("serviceTemplate.code");
        }

        if (postData.getScript() != null) {
            // If script was passed code is needed if script source was not passed.
            if (StringUtils.isBlank(postData.getScript().getCode()) && StringUtils.isBlank(postData.getScript().getScript())) {
                missingParameters.add("script.code");

                // Otherwise code is calculated from script source by combining package and classname
            } else {
                String fullClassname = ScriptInstanceService.getFullClassname(postData.getScript().getScript());
                if (!StringUtils.isBlank(postData.getScript().getCode()) && !postData.getScript().getCode().equals(fullClassname)) {
                    throw new BusinessApiException("The code and the canonical script class name must be identical");
                }
                postData.getScript().setCode(fullClassname);
            }
        }

        handleMissingParameters();

        if (meveoModuleService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(BusinessServiceModel.class, postData.getCode());
        }

        BusinessServiceModel bsm = new BusinessServiceModel();
        moduleApi.parseModuleFromDto(bsm, postData, currentUser);

        businessServiceModelService.create(bsm, currentUser);
    }

    public void update(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getServiceTemplate() == null || StringUtils.isBlank(postData.getServiceTemplate().getCode())) {
            missingParameters.add("serviceTemplate.code");
        }
        if (postData.getScript() != null) {
            // If script was passed code is needed if script source was not passed.
            if (StringUtils.isBlank(postData.getScript().getCode()) && StringUtils.isBlank(postData.getScript().getScript())) {
                missingParameters.add("script.code");

                // Otherwise code is calculated from script source by combining package and classname
            } else {
                String fullClassname = ScriptInstanceService.getFullClassname(postData.getScript().getScript());
                if (!StringUtils.isBlank(postData.getScript().getCode()) && !postData.getScript().getCode().equals(fullClassname)) {
                    throw new BusinessApiException("The code and the canonical script class name must be identical");
                }
                postData.getScript().setCode(fullClassname);
            }
        }

        handleMissingParameters();

        BusinessServiceModel bsm = businessServiceModelService.findByCode(postData.getCode(), currentUser.getProvider());
        if (bsm == null) {
            throw new EntityDoesNotExistsException(BusinessServiceModel.class, postData.getCode());
        }

        moduleApi.parseModuleFromDto(bsm, postData, currentUser);
        businessServiceModelService.update(bsm, currentUser);
    }

    public void remove(String businessServiceModelCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(businessServiceModelCode)) {
            missingParameters.add("businessServiceModelCode");
        }

        handleMissingParameters();
        BusinessServiceModel businessServiceModel = businessServiceModelService.findByCode(businessServiceModelCode, provider);
        if (businessServiceModel == null) {
            throw new EntityDoesNotExistsException(BusinessServiceModel.class, businessServiceModelCode);
        }

        businessServiceModelService.remove(businessServiceModel);
    }

    public void createOrUpdate(BusinessServiceModelDto postData, User currentUser) throws MeveoApiException, BusinessException {

        BusinessServiceModel businessServiceModel = businessServiceModelService.findByCode(postData.getCode(), currentUser.getProvider());
        if (businessServiceModel == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }
}