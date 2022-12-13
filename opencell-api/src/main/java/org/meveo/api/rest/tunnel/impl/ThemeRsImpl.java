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

package org.meveo.api.rest.tunnel.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.tunnel.ThemeDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tunnel.ThemeRs;
import org.meveo.api.tunnel.ThemeApi;
import org.meveo.model.tunnel.Theme;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * @author Mohamed CHAOUKI
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ThemeRsImpl extends BaseRs implements ThemeRs {

    @Inject
    private ThemeApi themeApi;


    @Override
    public Theme create(ThemeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Theme theme = new Theme();
        try {
            theme = themeApi.create(postData);
            result.setEntityCode(theme.getCode());
            result.setEntityId(theme.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return theme;
    }

    @Override
    public Theme update(ThemeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Theme theme = new Theme();
        try {
            theme = themeApi.update(postData);
            result.setEntityCode(theme.getCode());
            result.setEntityId(theme.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return theme;
    }

    @Override
    public ActionStatus delete(String code) {
        ActionStatus result = new ActionStatus();

        try {
            themeApi.delete(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
