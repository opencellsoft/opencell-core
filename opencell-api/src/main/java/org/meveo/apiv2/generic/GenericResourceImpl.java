package org.meveo.apiv2.generic;

import static org.meveo.apiv2.generic.services.PersistenceServiceHelper.getPersistenceService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.GenericOpencellRestful;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.exception.MeveoExceptionMapper;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.util.Inflector;

@Stateless
@Interceptors({ WsRestApiInterceptor.class })
public class GenericResourceImpl implements GenericResource {
    @Inject
    private GenericApiLoadService loadService;

    @Inject
    private GenericApiAlteringService genericApiAlteringService;

    @Inject
    private FinanceSettingsService financeSettingsService;

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
        boolean isHugeVolume = false;
        if(searchConfig != null && StringUtils.isBlank(searchConfig.getSortOrder())) {
            FinanceSettings financeSetting = financeSettingsService.getFinanceSetting();
            isHugeVolume = Optional.ofNullable(financeSetting)
                                           .map(FinanceSettings::getEntitiesWithHugeVolume)
                                           .map(Map::keySet)
                                           .orElse(new HashSet<>())
                                           .stream()
                                           .anyMatch(e -> e.equalsIgnoreCase(entityName));
        }
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService(), isHugeVolume);
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
    public Response getEntity(Boolean extractList, String entityName, Long id, GenericPagingAndFiltering searchConfig) {
        // if entityName is of plural form, process the request
        if (Inflector.getInstance().pluralize(entityName).equals(entityName)) {
            entityName = Inflector.getInstance().singularize(entityName);

            return get(extractList, entityName, id, searchConfig);
        }
        // otherwise, entityName is not of plural form, raise an exception
        else {
            MeveoApiException invalidPluralFormException = new MeveoApiException(
                    "The entity name " + entityName + " is not a valid plural form");
            MeveoExceptionMapper meveoExceptionMapper = new MeveoExceptionMapper();

            return meveoExceptionMapper.toResponse(invalidPluralFormException);
        }
    }

    @Override
    public Response getAllEntities(Boolean extractList, String entityName, GenericPagingAndFiltering searchConfig) {
        // if entityName is of plural form, process the request
        if (Inflector.getInstance().pluralize(entityName).equals(entityName)) {
            entityName = Inflector.getInstance().singularize(entityName);

            return getAll(extractList, entityName, searchConfig);
        }
        // otherwise, entityName is not of plural form, raise an exception
        else {
            MeveoApiException invalidPluralFormException = new MeveoApiException(
                    "The entity name " + entityName + " is not a valid plural form");
            MeveoExceptionMapper meveoExceptionMapper = new MeveoExceptionMapper();

            return meveoExceptionMapper.toResponse(invalidPluralFormException);
        }
    }

    @Override
    public Response getFullListEntities(boolean onlyBusinessEntities, boolean withFullName) {

        List<String> entities = GenericOpencellRestful.ENTITIES_LIST.stream().filter(aClass ->
                (!onlyBusinessEntities || BusinessEntity.class.isAssignableFrom(aClass)))
                . map(aClass -> withFullName ? aClass.getCanonicalName(): aClass.getSimpleName()).collect(Collectors.toList());

        return Response.ok().entity(Map.of("entities", entities)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @Override
    public Response getRelatedFieldsAndTypesOfEntity( String entityName, String filter, Long depth ) {
        Class entityClass = GenericHelper.getEntityClass(entityName);
        return Response.ok().entity(getPersistenceService(entityClass).mapRelatedFields(filter, depth != null ? depth : 2L, 0L, null)).type(MediaType.APPLICATION_JSON_TYPE).build();
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
    
    @Override
    public Response export(String entityName, String fileFormat, String locale, GenericPagingAndFiltering searchConfig) throws ClassNotFoundException {
        Set<String> genericFields = null;
        List<GenericFieldDetails> genericFieldDetails = null;

        if(searchConfig != null){
        	if (searchConfig.getNestedEntities() != null && !searchConfig.getNestedEntities().isEmpty()) {
                throw new MeveoApiException("Nested entities are not handled by the export api");
            }
        	if (CollectionUtils.isEmpty(searchConfig.getGenericFields()) && CollectionUtils.isEmpty(searchConfig.getGenericFieldDetails())) {
                throw new MeveoApiException("One of 'Generic fields' or 'Generic field details' are mandatory");
            }
            if (CollectionUtils.isNotEmpty(searchConfig.getGenericFields()) && CollectionUtils.isNotEmpty(searchConfig.getGenericFieldDetails())) {
                throw new MeveoApiException("Only one of 'Generic fields' or 'Generic field details' shall be specified");
            }
            genericFields = searchConfig.getGenericFields();
            genericFieldDetails = searchConfig.getGenericFieldDetails();
            
        }
        if(!fileFormat.equals("CSV") && !fileFormat.equals("EXCEL") && !fileFormat.equalsIgnoreCase("pdf")){
            throw new BadRequestException("Accepted formats for export are (CSV, pdf or EXCEL).");
        }
        if (StringUtils.isBlank(locale)) {
            locale = "EN"; // default value EN
        }
        Class entityClass = GenericHelper.getEntityClass(entityName);
        GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
        String filePath = loadService.export(entityClass, genericRequestMapper.mapTo(searchConfig), genericFields, genericFieldDetails, fileFormat, entityName, locale);
        return Response.ok()
                .entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"\"}, \"data\":{ \"filePath\":\""+ filePath +"\"}}")
                .build();
    }
}
