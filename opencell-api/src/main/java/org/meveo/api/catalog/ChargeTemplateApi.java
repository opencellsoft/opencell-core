package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ChargeTemplateApi extends BaseApi {

    @Inject
    private ChargeTemplateServiceAll chargeTemplateService;


    public ChargeTemplateDto find(String chargeTemplateCode) throws MeveoApiException {
        if (StringUtils.isBlank(chargeTemplateCode)) {
            missingParameters.add("chargeTemplateCode");
        }
        handleMissingParameters();
        
        ChargeTemplate chargeTemplate = (ChargeTemplate) chargeTemplateService.findByCode(chargeTemplateCode);
        if (chargeTemplate == null) {
            throw new EntityDoesNotExistsException(ChargeTemplate.class, chargeTemplateCode);
        }

        return new ChargeTemplateDto(chargeTemplate, entityToDtoConverter.getCustomFieldsDTO(chargeTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
    }
}
