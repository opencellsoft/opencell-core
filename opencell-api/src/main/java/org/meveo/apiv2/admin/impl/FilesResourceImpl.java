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
package org.meveo.apiv2.admin.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.admin.File;
import org.meveo.apiv2.admin.FilesPagingAndFiltering;
import org.meveo.apiv2.admin.resource.FilesResource;
import org.meveo.apiv2.admin.service.FilesApiService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Files resource implementation.
 *
 * @author Abdellatif BARI
 * @since 14.1.16
 */
@Interceptors({WsRestApiInterceptor.class})
public class FilesResourceImpl implements FilesResource {

    @Inject
    private FilesApiService filesApiService;

    @Override
    public Response search(FilesPagingAndFiltering searchConfig) {
        List<File> list = filesApiService.searchFiles(searchConfig);
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("total", list.size());
        results.put("limit", searchConfig.getLimit());
        results.put("offset", searchConfig.getOffset());
        results.put("data", list);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Response.ok().entity(mapper.writeValueAsString(results)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json formatting exception", e);
        }

    }
}