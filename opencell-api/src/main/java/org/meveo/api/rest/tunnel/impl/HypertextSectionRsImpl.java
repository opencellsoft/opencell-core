package org.meveo.api.rest.tunnel.impl;/*
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


import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.tunnel.HypertextSectionDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tunnel.HypertextSectionRs;
import org.meveo.api.tunnel.HypertextSectionApi;
import org.meveo.model.tunnel.HypertextSection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * @author Ilham CHAFIK
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class HypertextSectionRsImpl extends BaseRs implements HypertextSectionRs {

    @Inject
    private HypertextSectionApi sectionApi;

    @Override
    public ActionStatus create(HypertextSectionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            HypertextSection section = sectionApi.create(postData);
            result.setEntityCode(section.getCode());
            result.setEntityId(section.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(HypertextSectionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            HypertextSection section = sectionApi.update(postData);
            result.setEntityCode(section.getCode());
            result.setEntityId(section.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public List<HypertextSection> createOrUpdate(List<HypertextSectionDto> sectionsDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        List<HypertextSection> sections = null;
        try {
            sections = sectionApi.createOrUpdate(sectionsDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return sections;
    }

    @Override
    public ActionStatus delete(String code) {
        ActionStatus result = new ActionStatus();

        try {
            sectionApi.delete(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }


}
