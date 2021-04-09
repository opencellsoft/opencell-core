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

package org.meveo.api.rest.admin.impl;

import org.meveo.api.admin.FileFormatApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.admin.FileFormatDto;
import org.meveo.api.dto.admin.FileFormatListResponseDto;
import org.meveo.api.dto.admin.FileFormatResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.admin.FileFormatRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * File format resource
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class FileFormatRsImpl extends BaseRs implements FileFormatRs {

    @Inject
    private FileFormatApi fileFormatApi;

    @Override
    public ActionStatus create(FileFormatDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            fileFormatApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(FileFormatDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            fileFormatApi.update(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            fileFormatApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(FileFormatDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            fileFormatApi.createOrUpdate(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public FileFormatResponseDto find(String code) {
        FileFormatResponseDto result = new FileFormatResponseDto();

        try {
            result.setDto(fileFormatApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }


    @Override
    public FileFormatListResponseDto searchGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        FileFormatListResponseDto result;

        try {
            result = new FileFormatListResponseDto(fileFormatApi.search(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder)));
        } catch (Exception e) {
            result = new FileFormatListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public FileFormatListResponseDto listGetAll() {

        FileFormatListResponseDto result = new FileFormatListResponseDto();

        try {
            result = new FileFormatListResponseDto( fileFormatApi.search(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering()) );
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public FileFormatListResponseDto searchPost(PagingAndFiltering pagingAndFiltering) {

        FileFormatListResponseDto result;

        try {
            result = new FileFormatListResponseDto(fileFormatApi.search(pagingAndFiltering));
        } catch (Exception e) {
            result = new FileFormatListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }
}