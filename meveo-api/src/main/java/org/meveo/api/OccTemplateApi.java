package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.payments.impl.OCCTemplateService;

@Stateless
public class OccTemplateApi extends BaseApi {

    @Inject
    private OCCTemplateService occTemplateService;

    public void create(OccTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getAccountCode())) {
            missingParameters.add("accountCode");
        }
        if (StringUtils.isBlank(postData.getOccCategory())) {
            missingParameters.add("occCategory");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        if (occTemplateService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(OCCTemplate.class, postData.getCode());
        }

        OCCTemplate occTemplate = new OCCTemplate();
        occTemplate.setProvider(provider);
        occTemplate.setCode(postData.getCode());
        occTemplate.setDescription(postData.getDescription());
        occTemplate.setAccountCode(postData.getAccountCode());
        occTemplate.setAccountCodeClientSide(postData.getAccountCodeClientSide());
        occTemplate.setOccCategory(postData.getOccCategory());

        occTemplateService.create(occTemplate, currentUser);
    }

    public void update(OccTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getAccountCode())) {
            missingParameters.add("accountCode");
        }
        if (StringUtils.isBlank(postData.getOccCategory())) {
            missingParameters.add("occCategory");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        OCCTemplate occTemplate = occTemplateService.findByCode(postData.getCode(), provider);
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getCode());
        }

        occTemplate.setDescription(postData.getDescription());
        occTemplate.setAccountCode(postData.getAccountCode());
        occTemplate.setAccountCodeClientSide(postData.getAccountCodeClientSide());
        occTemplate.setOccCategory(postData.getOccCategory());

        occTemplateService.update(occTemplate, currentUser);

    }

    public OccTemplateDto find(String code, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("occTemplateCode");
            handleMissingParameters();
        }

        OCCTemplate occTemplate = occTemplateService.findByCode(code, provider);
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, code);
        }

        return new OccTemplateDto(occTemplate);

    }

    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("occTemplateCode");
            handleMissingParameters();
        }

        OCCTemplate occTemplate = occTemplateService.findByCode(code, currentUser.getProvider());
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, code);
        }

        occTemplateService.remove(occTemplate, currentUser);
    }

    /**
     * create or update occ template based on occ template code
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(OccTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {

        OCCTemplate occTemplate = occTemplateService.findByCode(postData.getCode(), currentUser.getProvider());

        if (occTemplate == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}