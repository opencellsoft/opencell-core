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

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
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
public class GenericChargeTemplateApi extends BaseApi {

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

	/**
	 * @param chargeTemplateCode
	 * @param status
	 * @return
	 */
	public void updateStatus(String chargeTemplateCode, String status) {
		ChargeTemplate chargeTemplate = chargeTemplateService.findByCode(chargeTemplateCode);
		if(chargeTemplate ==null) {
    		throw new EntityDoesNotExistsException(ChargeTemplate.class, chargeTemplateCode);
    	}
		chargeTemplateService.updateStatus(chargeTemplate, status);
	}

	/**
	 * @param chargeTemplateCode
	 * @return
	 */
	public GetChargeTemplateResponseDto duplicateCharge(String chargeTemplateCode) {
		ChargeTemplate chargeTemplate = chargeTemplateService.findByCode(chargeTemplateCode);
		
		if(chargeTemplate ==null) {
    		throw new EntityDoesNotExistsException(ChargeTemplate.class, chargeTemplateCode);
    	}
		
		GetChargeTemplateResponseDto getChargeTemplateResponseDto = new GetChargeTemplateResponseDto();
		ChargeTemplate duplicateChargeTemplate = chargeTemplateService.duplicateCharge(chargeTemplate);
		getChargeTemplateResponseDto.setChargeTemplate(new ChargeTemplateDto(duplicateChargeTemplate, entityToDtoConverter.getCustomFieldsDTO(duplicateChargeTemplate)));
		return getChargeTemplateResponseDto;
	}

}