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

package org.meveo.apiv2;

import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.common.LinkGenerator;
import org.meveo.apiv2.services.generic.GenericApiLoadService;
import org.meveo.apiv2.services.generic.GenericApiAlteringService;
import org.meveo.apiv2.services.generic.GenericRequestMapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

@Stateless
public class GenericResourceImpl implements GenericResource {
    @Inject
    private GenericApiLoadService loadService;
    @Inject
    private GenericApiAlteringService genericApiAlteringService;

    @Override
    public Response getAll(String entityName, GenericPagingAndFiltering searchConfig) {
        Set<String> genericFields = null;
        if(searchConfig != null){
            genericFields = searchConfig.getGenericFields();
        }
        Class entityClass = loadService.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, loadService.getPersistenceService());
        return Response.ok().entity(loadService.findPaginatedRecords(entityClass, genericRequestMapper.mapTo(searchConfig), genericFields))
                .links(buildPaginatedResourceLink(entityName)).build();
    }
    
    @Override
    public Response get(String entityName, Long id, GenericPagingAndFiltering searchConfig) {
        Set<String> genericFields = null;
        if(searchConfig != null){
            genericFields = searchConfig.getGenericFields();
        }
        Class entityClass = loadService.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, loadService.getPersistenceService());
        return loadService.findByClassNameAndId(entityName, id, genericRequestMapper.mapTo(searchConfig), genericFields)
                .map(deletedEntity -> Response.ok().entity(deletedEntity).links(buildSingleResourceLink(entityName, id)).build())
                .orElseThrow(NotFoundException::new);
    }
    
    @Override
    public Response update(String entityName, Long id, String dto) {
        return genericApiAlteringService.update(entityName, id, dto)
                .map(deletedEntity -> Response.ok().entity(deletedEntity).links(buildSingleResourceLink(entityName, id)).build())
                .orElseThrow(NotFoundException::new);
    }
    
    @Override
    public Response create(String entityName, String dto) {
        Long entityId = genericApiAlteringService.create(entityName, dto);
        return Response.ok().entity(Collections.singletonMap("id", entityId))
                .links(buildSingleResourceLink(entityName, entityId))
                .build();
    }

    @Override
    public Response delete(String entityName, Long id) {
        return genericApiAlteringService.delete(entityName, id)
                .map(deletedEntity -> Response.ok().entity(deletedEntity)
                        .links(buildSingleResourceLink(entityName, id)).build())
                .orElseThrow(NotFoundException::new);
    }

    private Link buildPaginatedResourceLink(String entityName) {
        return new LinkGenerator.SelfLinkGenerator(GenericResource.class)
                .withGetAction().withPostAction()
                .withDeleteAction().build(entityName);
    }
    private Link buildSingleResourceLink(String entityName, Long entityId) {
        return new LinkGenerator.SelfLinkGenerator(GenericResource.class)
                .withGetAction().withPostAction().withId(entityId)
                .withDeleteAction().build(entityName);
    }
}
