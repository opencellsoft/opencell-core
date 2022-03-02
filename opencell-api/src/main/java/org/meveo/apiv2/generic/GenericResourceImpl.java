package org.meveo.apiv2.generic;


import org.meveo.apiv2.GenericOpencellRestful;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.Set;

import static org.meveo.apiv2.generic.services.PersistenceServiceHelper.getPersistenceService;

@Stateless
public class GenericResourceImpl implements GenericResource {
    @Inject
    private GenericApiLoadService loadService;

    @Inject
    private GenericApiAlteringService genericApiAlteringService;

    @Override
    public Response getAll(Boolean extractList, String entityName, GenericPagingAndFiltering searchConfig) {
        Set<String> genericFields = null;
        Set<String> nestedEntities = null;
        Set<String> excludedFields = null;
        if(searchConfig != null){
            genericFields = searchConfig.getGenericFields();
            nestedEntities = searchConfig.getNestedEntities();
            excludedFields = searchConfig.getExcluding();
        }
        Class entityClass = GenericHelper.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
        return Response.ok().entity(loadService.findPaginatedRecords(extractList, entityClass, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth(), null, excludedFields))
                .links(buildPaginatedResourceLink(entityName)).build();
    }
    
    @Override
    public Response get(Boolean extractList, String entityName, Long id, GenericPagingAndFiltering searchConfig) {
        Set<String> genericFields = null;
        Set<String> nestedEntities = null;
        Set<String> excludedFields = null;
        if(searchConfig != null){
            genericFields = searchConfig.getGenericFields();
            nestedEntities = searchConfig.getNestedEntities();
            excludedFields = searchConfig.getExcluding();
        }
        Class entityClass = GenericHelper.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
        if(genericFields != null && loadService.isCustomFieldQuery(genericFields)){
        	return Response.ok().entity(loadService.findPaginatedRecords(extractList, entityClass, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth(), id, null))
                    .links(buildPaginatedResourceLink(entityName)).build();
        } else {    
        	return loadService.findByClassNameAndId(extractList, entityClass, id, genericRequestMapper.mapTo(searchConfig), genericFields, nestedEntities, searchConfig.getNestedDepth(), excludedFields)
	                .map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(entityName, id)).build())
	                .orElseThrow(() -> new NotFoundException("entity " + entityName + " with id "+id+ " not found."));
        }
    }

    @Override
    public Response getFullListEntities() {
        return Response.ok().entity(GenericOpencellRestful.ENTITIES_MAP).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @Override
    public Response getRelatedFieldsAndTypesOfEntity( String entityName ) {
        Class entityClass = GenericHelper.getEntityClass(entityName);
        return Response.ok().entity(getPersistenceService(entityClass).mapRelatedFields()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    @Override
    public Response update(String entityName, Long id, String dto) {
        return Response.ok().entity(genericApiAlteringService.update(entityName, id, dto)).links(buildSingleResourceLink(entityName, id)).build();
    }
    
    @Override
    public Response create(String entityName, String dto) {
        return  genericApiAlteringService.create(entityName, dto)
                .map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId))
                .links(buildSingleResourceLink(entityName, entityId))
                .build())
                .get();
    }

    @Override
    public Response delete(String entityName, Long id) {
        return Response.ok().entity(genericApiAlteringService.delete(entityName, id))
                        .links(buildSingleResourceLink(entityName, id)).build();
    }

    @Override
    public Response getVersions() {
        return Response.ok().entity(GenericOpencellRestful.VERSION_INFO).type(MediaType.APPLICATION_JSON_TYPE).build();
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
