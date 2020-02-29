package org.meveo.api.catalog;

import java.util.Arrays;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 **/
@Stateless
public class UsageChargeTemplateApi extends ChargeTemplateApi<UsageChargeTemplate, UsageChargeTemplateDto> {

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Override
    public UsageChargeTemplate create(UsageChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (StringUtils.isBlank(postData.getTaxClassCode())) {
            missingParameters.add("taxClassCode");
        }

        handleMissingParametersAndValidate(postData);

        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(postData.getCode());
        if (chargeTemplate != null) {
            throw new EntityAlreadyExistsException(UsageChargeTemplate.class, postData.getCode());
        }

        chargeTemplate = dtoToEntity(postData, null);

        usageChargeTemplateService.create(chargeTemplate);
        return chargeTemplate;
    }

    @Override
    public UsageChargeTemplate update(UsageChargeTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getInvoiceSubCategory() != null && StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (postData.getTaxClassCode() != null && StringUtils.isBlank(postData.getTaxClassCode())) {
            missingParameters.add("taxClassCode");
        }
        handleMissingParametersAndValidate(postData);

        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(postData.getCode());
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplate.class, postData.getCode());
        }
        chargeTemplate = dtoToEntity(postData, chargeTemplate);
        return usageChargeTemplateService.update(chargeTemplate);
    }

    /**
     * Convert/update DTO object to an entity object
     * 
     * @param postData DTO object
     * @param chargeTemplate Entity object to update
     * @return A new or updated entity object
     * @throws MeveoApiException General API exception
     * @throws BusinessException General exception
     */
    private UsageChargeTemplate dtoToEntity(UsageChargeTemplateDto postData, UsageChargeTemplate chargeTemplate) throws MeveoApiException, BusinessException {

        boolean isNew = chargeTemplate == null;

        if (isNew) {
            chargeTemplate = new UsageChargeTemplate();
            chargeTemplate.setCode(postData.getCode());
        } else {
            chargeTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        }

        super.dtoToEntity(postData, chargeTemplate, isNew);

        if (postData.getPriority() != null) {
            chargeTemplate.setPriority(postData.getPriority());
        }
        if (postData.getFilterParam1() != null) {
            chargeTemplate.setFilterParam1(postData.getFilterParam1());
        }
        if (postData.getFilterParam2() != null) {
            chargeTemplate.setFilterParam2(postData.getFilterParam2());
        }
        if (postData.getFilterParam3() != null) {
            chargeTemplate.setFilterParam3(postData.getFilterParam3());
        }
        if (postData.getFilterParam4() != null) {
            chargeTemplate.setFilterParam4(postData.getFilterParam4());
        }

        chargeTemplate.setTriggerNextCharge(postData.getTriggerNextCharge());
        chargeTemplate.setTriggerNextChargeEL(postData.getTriggerNextChargeEL());

        return chargeTemplate;
    }

    @Override
    public UsageChargeTemplateDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("usageChargeTemplateCode");
            handleMissingParameters();
        }

        UsageChargeTemplateDto result;

        // check if code already exists
        UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(code, Arrays.asList("invoiceSubCategory"));
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(UsageChargeTemplateDto.class, code);
        }

        result = new UsageChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        return result;
    }
}