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

package org.meveo.api.rest.account.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.account.ProviderContactApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.response.account.ProviderContactResponseDto;
import org.meveo.api.dto.response.account.ProviderContactsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.ProviderContactRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.ProviderContact;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Abdellatif BARI
 * @since Jun 3, 2016 4:40:19 AM
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ProviderContactRsImpl extends BaseRs implements ProviderContactRs {

    @Inject
    private ProviderContactApi providerContactApi;

    @Override
    public ActionStatus create(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ProviderContact providerContact = providerContactApi.create(providerContactDto);
            if (StringUtils.isBlank(providerContactDto.getCode())) {
                result.setEntityCode(providerContact.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.update(providerContactDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ProviderContactResponseDto find(String code) {
        ProviderContactResponseDto result = new ProviderContactResponseDto();

        try {
            result.setProviderContact(providerContactApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ProviderContactsResponseDto list() {
        ProviderContactsResponseDto result = new ProviderContactsResponseDto();

        try {
            result.setProviderContacts(providerContactApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ProviderContact providerContact = providerContactApi.createOrUpdate(providerContactDto);
            if (StringUtils.isBlank(providerContactDto.getCode())) {
                result.setEntityCode(providerContact.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

}
