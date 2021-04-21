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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.DigitalResourceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.service.catalog.impl.DigitalResourceService;

@Stateless
public class DigitalResourceApi extends BaseCrudApi<DigitalResource, DigitalResourceDto> {

    @Inject
    private DigitalResourceService digitalResourceService;

    @Override
    public DigitalResourceDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("digitalResource code");
            handleMissingParameters();
        }

        DigitalResource digitalResource = digitalResourceService.findByCode(code);
        if (digitalResource == null) {
            throw new EntityDoesNotExistsException(DigitalResource.class, code);
        }

        return new DigitalResourceDto(digitalResource);
    }

    @Override
    public DigitalResource create(DigitalResourceDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(DigitalResource.class.getName(), postData);
        }

        handleMissingParametersAndValidate(postData);

        DigitalResource digitalResource = digitalResourceService.findByCode(postData.getCode());
        if (digitalResource != null) {
            throw new EntityAlreadyExistsException(DigitalResource.class, postData.getCode());
        }

        digitalResource = convertFromDto(postData, null);
        digitalResourceService.create(digitalResource);
        return digitalResource;
    }

    @Override
    public DigitalResource update(DigitalResourceDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        DigitalResource digitalResource = digitalResourceService.findByCode(postData.getCode());
        if (digitalResource == null) {
            throw new EntityDoesNotExistsException(DigitalResource.class, postData.getCode());
        }

        digitalResource = convertFromDto(postData, digitalResource);
        digitalResource = digitalResourceService.update(digitalResource);
        return digitalResource;
    }

    public DigitalResource convertFromDto(DigitalResourceDto digitalResourcesDto, DigitalResource digitalResourceToUpdate) throws MeveoApiException {

        DigitalResource digitalResource = digitalResourceToUpdate;

        if (digitalResource == null) {
            digitalResource = new DigitalResource();
            digitalResource.setCode(digitalResourcesDto.getCode());
            if (digitalResourcesDto.isDisabled() != null) {
                digitalResource.setDisabled(digitalResourcesDto.isDisabled());
            }

        } else if (!StringUtils.isBlank(digitalResourcesDto.getUpdatedCode())) {
            digitalResource.setCode(digitalResourcesDto.getUpdatedCode());

        }
        digitalResource.setDescription(keepOldValueIfNull(digitalResourcesDto.getDescription(), digitalResource.getDescription()));
        digitalResource.setUri(keepOldValueIfNull(digitalResourcesDto.getUri(), digitalResource.getUri()));
        digitalResource.setMimeType(keepOldValueIfNull(digitalResourcesDto.getMimeType(), digitalResource.getMimeType()));

        return digitalResource;
    }
}
