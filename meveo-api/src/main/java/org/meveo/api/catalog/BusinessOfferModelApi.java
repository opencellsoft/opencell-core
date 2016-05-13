package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.module.ModuleApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.catalog.impl.BusinessOfferModelService;
import org.meveo.service.script.ScriptInstanceService;

@Stateless
public class BusinessOfferModelApi extends BaseApi {

    @Inject
    private BusinessOfferModelService businessOfferModelService;

    @Inject
    private MeveoModuleService meveoModuleService;

    @Inject
    private ModuleApi moduleApi;

    public void create(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getOfferTemplate() == null || StringUtils.isBlank(postData.getOfferTemplate().getCode())) {
            missingParameters.add("offerTemplate.code");
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
            throw new EntityAlreadyExistsException(BusinessOfferModel.class, postData.getCode());
        }

        BusinessOfferModel businessOfferModel = new BusinessOfferModel();
        moduleApi.parseModuleFromDto(businessOfferModel, postData, currentUser);

        businessOfferModelService.create(businessOfferModel, currentUser);

    }

    public void update(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getOfferTemplate() == null || StringUtils.isBlank(postData.getOfferTemplate().getCode())) {
            missingParameters.add("offerTemplate.code");
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

        BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(postData.getCode(), currentUser.getProvider());
        if (businessOfferModel == null) {
            throw new EntityDoesNotExistsException(BusinessOfferModel.class, postData.getCode());
        }

        moduleApi.parseModuleFromDto(businessOfferModel, postData, currentUser);
        businessOfferModelService.update(businessOfferModel, currentUser);

    }

    public void remove(String businessOfferModelCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(businessOfferModelCode)) {
            missingParameters.add("businessOfferModelCode");
        }
        handleMissingParameters();

        BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(businessOfferModelCode, provider);
        if (businessOfferModel == null) {
            throw new EntityDoesNotExistsException(BusinessOfferModel.class, businessOfferModelCode);
        }

        businessOfferModelService.remove(businessOfferModel);
    }

    public void createOrUpdate(BusinessOfferModelDto postData, User currentUser) throws MeveoApiException, BusinessException {
        BusinessOfferModel businessOfferModel = businessOfferModelService.findByCode(postData.getCode(), currentUser.getProvider());
        if (businessOfferModel == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }
}