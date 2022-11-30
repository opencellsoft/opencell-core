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

package org.meveo.api.tunnel;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.tunnel.ElectronicSignatureDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.tunnel.ElectronicSignature;
import org.meveo.service.tunnel.ElectronicSignatureService;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author Ilham CHAFIK
 */
@Stateless
public class ElectronicSignatureApi extends BaseCrudApi<ElectronicSignature, ElectronicSignatureDto> {

    @Inject
    private ElectronicSignatureService signatureService;

    @Override
    public ElectronicSignature create(ElectronicSignatureDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(ElectronicSignature.class.getName(), postData);
        }

        if (signatureService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(ElectronicSignature.class, postData.getCode());
        }

        ElectronicSignature entity = new ElectronicSignature();
        dtoToEntity(postData, entity);
        signatureService.create(entity);

        return entity;
    }

    @Override
    public ElectronicSignature update(ElectronicSignatureDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        ElectronicSignature signature = signatureService.findByCode(postData.getCode());
        if (signature == null) {
            throw new EntityDoesNotExistsException(ElectronicSignature.class, postData.getCode());
        }

        dtoToEntity(postData, signature);
        signatureService.update(signature);
        return signature;
    }


    /**
     * Populate entity with fields from DTO entity
     *
     * @param dto DTO entity object to populate from
     * @param entity Entity to populate
     **/
    private void dtoToEntity(ElectronicSignatureDto dto, ElectronicSignature entity) {

        entity.setCode(dto.getCode());
        if (dto.getElectronicSignature() != null) {
            entity.setElectronicSignature(dto.getElectronicSignature());
        }
        if (dto.getLabel() != null) {
            entity.setLabel(dto.getLabel());
        }
        if (dto.getSignatureApi() != null) {
            entity.setSignatureApi(dto.getSignatureApi());
        }
        if (dto.getPopupUrl() != null) {
            entity.setPopupUrl(dto.getPopupUrl());
        }
        if (dto.getSignatureStatusApi() != null) {
            entity.setSignatureStatusApi(dto.getSignatureStatusApi());
        }
        if (dto.getGetSignedfileApi() != null) {
            entity.setGetSignedfileApi(dto.getGetSignedfileApi());
        }
    }

}
