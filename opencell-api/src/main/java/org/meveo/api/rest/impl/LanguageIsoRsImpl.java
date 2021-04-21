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

package org.meveo.api.rest.impl;

import org.meveo.api.LanguageIsoApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.LanguageIsoDto;
import org.meveo.api.dto.response.GetLanguageIsoResponse;
import org.meveo.api.dto.response.GetLanguagesIsoResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.LanguageIsoRs;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class LanguageIsoRsImpl extends BaseRs implements LanguageIsoRs {

    @Inject
    private LanguageIsoApi languageIsoApi;

    @Override
    public ActionStatus create(LanguageIsoDto languageIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.create(languageIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetLanguageIsoResponse find(String languageCode) {
        GetLanguageIsoResponse result = new GetLanguageIsoResponse();

        try {
            result.setLanguage(languageIsoApi.find(languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String languageCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.remove(languageCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(LanguageIsoDto languageIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.update(languageIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(LanguageIsoDto languageIsoDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageIsoApi.createOrUpdate(languageIsoDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

	@Override
	public GetLanguagesIsoResponse list() {
		GetLanguagesIsoResponse result = new GetLanguagesIsoResponse();

        try {
            result.setLanguages(languageIsoApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
	}

    @Override
    public GetLanguagesIsoResponse listGetAll() {

        GetLanguagesIsoResponse result = new GetLanguagesIsoResponse();

        try {
            result = languageIsoApi.list(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}
