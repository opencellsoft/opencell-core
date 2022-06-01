package org.meveo.api.restful.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.*;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.restful.GenericOpencellRestfulAPIv1;
import org.meveo.api.restful.filter.AuthenticationFilter;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.util.Inflector;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Thang Nguyen
 */
public class Apiv1PostService {

    private static final String METHOD_CREATE = "/";
    private static final String METHOD_CREATE_BIS = "/create";
    private static final String METHOD_CREATE_OR_UPDATE = "/createOrUpdate";

    private static final String FORWARD_SLASH = "/";
    private static final String QUERY_PARAM_SEPARATOR = "?";
    private static final String QUERY_PARAM_VALUE_SEPARATOR = "=";
    private static final String PAIR_QUERY_PARAM_SEPARATOR = "&";
    private static final String DTO_SUFFIX = "dto";

    // static final string for enabling and disabling services
    private static final String ENABLE_SERVICE = "enable";
    private static final String DISABLE_SERVICE = "disable";

    private List<PathSegment> segmentsOfPathAPIv1;
    private StringBuilder queryParams;
    private MultivaluedMap<String, String> queryParamsMap;

    private String pathIBaseRS;

    /*
     * Function used to create a new entity
     */
    public Response createEntity(UriInfo uriInfo, String postPath, String jsonDto) throws JsonProcessingException, URISyntaxException {
        URI redirectURI;

        pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.get( postPath );

        if ( pathIBaseRS.equals( "/jobInstance" ) || pathIBaseRS.equals( "/job" ) )
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + METHOD_CREATE_BIS );
        else if ( pathIBaseRS.equals( "/invoice/sendByEmail" ) )
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS );
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.CUSTOM_ENTITY_INSTANCE ) ) {
            CustomEntityInstanceDto aDto = new ObjectMapper().readValue( jsonDto, CustomEntityInstanceDto.class );
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + FORWARD_SLASH + aDto.getCetCode() );
        }
        else
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS + METHOD_CREATE );

        return Response.temporaryRedirect( redirectURI ).entity( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) ).build();
    }

    /*
     * Function used to enable or disable an entity or a service
     */
    public Response enableOrDisableEntity(UriInfo uriInfo, String postPath, String jsonDto) throws URISyntaxException, IOException {
        URI redirectURI = null;
        
        if(uriInfo.getPathSegments() == null) {
        	throw new IOException();
        }

        segmentsOfPathAPIv1 = uriInfo.getPathSegments();
        queryParamsMap = uriInfo.getQueryParameters();
        queryParams = new StringBuilder( QUERY_PARAM_SEPARATOR );
        for( String aKey : queryParamsMap.keySet() ){
            queryParams.append( aKey + QUERY_PARAM_VALUE_SEPARATOR
                    + queryParamsMap.get( aKey ).get(0).replace( GenericPagingAndFilteringUtils.BLANK_SPACE, GenericPagingAndFilteringUtils.BLANK_SPACE_ENCODED )
                    + PAIR_QUERY_PARAM_SEPARATOR );
        }

        pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_RESTFUL_REGEX_PATH_AND_IBASE_RS_PATH.get( postPath );

        // Handle the generic special endpoint: enable a service
        if ( segmentsOfPathAPIv1.get( segmentsOfPathAPIv1.size() - 1 ).getPath().equals(ENABLE_SERVICE) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS
                    + FORWARD_SLASH + segmentsOfPathAPIv1.get( segmentsOfPathAPIv1.size() - 2 ).getPath()
                    + FORWARD_SLASH + ENABLE_SERVICE + queryParams.substring( 0, queryParams.length() - 1 ) );
        }
        // Handle the generic special endpoint: disable a service
        else if ( segmentsOfPathAPIv1.get( segmentsOfPathAPIv1.size() - 1 ).getPath().equals(DISABLE_SERVICE) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + pathIBaseRS
                    + FORWARD_SLASH + segmentsOfPathAPIv1.get( segmentsOfPathAPIv1.size() - 2 ).getPath()
                    + FORWARD_SLASH + DISABLE_SERVICE + queryParams.substring( 0, queryParams.length() - 1 ) );
        }

        return Response.temporaryRedirect( redirectURI ).entity( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) ).build();
    }

    /*
     * Function used to enable or disable an entity or a service
     */
    public Response handleOther(UriInfo uriInfo, String postPath, String jsonDto) throws URISyntaxException {
        URI redirectURI;

        if ( postPath.equals( "/api/rest/v1/invoices/pdfInvoices" ) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + "/invoice/fetchPdfInvoice" );
            return Response.temporaryRedirect( redirectURI )
                    .entity( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) ).build();
        }
        else if ( postPath.equals( "/api/rest/v1/invoices/xmlInvoices" ) ) {
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                    + "/invoice/fetchXMLInvoice" );
            return Response.temporaryRedirect( redirectURI )
                    .entity( Entity.entity(jsonDto, MediaType.APPLICATION_JSON) ).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /*
     * Function used to create or update an entity
     */
    public Response postCreationOrUpdate(UriInfo uriInfo, String jsonDto) throws URISyntaxException, JsonProcessingException {
        URI redirectURI;
        StringBuilder aPathBd = new StringBuilder(GenericOpencellRestfulAPIv1.REST_PATH);
        segmentsOfPathAPIv1 = uriInfo.getPathSegments();

        String entityCode = segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString();

        if ( segmentsOfPathAPIv1.size() >= 3 ) {
            for (int i = 0; i <= segmentsOfPathAPIv1.size() - 3; i++ )
                aPathBd.append( FORWARD_SLASH + segmentsOfPathAPIv1.get(i) );
            String aPath = aPathBd.toString();

            if ( GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.containsKey(aPath) ) {
                pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_RESTFUL_PATH_AND_IBASE_RS_PATH.get(aPath);
                String entityClassName = Inflector.getInstance().singularize(segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 3));

                if ( pathIBaseRS.equals( Apiv1ConstantDictionary.TRIGGERED_EDR ) )
                    entityClassName = "triggeredEDRTemplate";
                else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.COUNTRY_ISO ) )
                    entityClassName = "countryIso";
                else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.CURRENCY_ISO ) )
                    entityClassName = "currencyIso";
                else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.CUSTOM_ENTITY_TEMPLATE ) )
                    entityClassName = "customEntityTemplate";

                Class entityDtoClass = GenericHelper.getEntityDtoClass( entityClassName.toLowerCase() + DTO_SUFFIX );
                Object aDto = new ObjectMapper().readValue( jsonDto, entityDtoClass );
                if ( aDto instanceof BusinessEntityDto)
                    ((BusinessEntityDto) aDto).setCode(entityCode);
                else if ( aDto instanceof AccountHierarchyDto)
                    ((AccountHierarchyDto) aDto).setCustomerCode(entityCode);
                else if ( aDto instanceof AccessDto)
                    ((AccessDto) aDto).setCode(entityCode);
                else if ( aDto instanceof DiscountPlanItemDto)
                    ((DiscountPlanItemDto) aDto).setCode(entityCode);
                else if ( aDto instanceof CountryDto)
                    ((CountryDto) aDto).setCountryCode(entityCode);
                else if ( aDto instanceof CountryIsoDto)
                    ((CountryIsoDto) aDto).setCountryCode(entityCode);
                else if ( aDto instanceof CurrencyDto)
                    ((CurrencyDto) aDto).setCode(entityCode);
                else if ( aDto instanceof CurrencyIsoDto )
                    ((CurrencyIsoDto) aDto).setCode(entityCode);

                if ( aPath.equals("/api/rest/v1/accountManagement/customerCategories") )
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                            + pathIBaseRS + "/createOrUpdateCategory" );
                else if ( aPath.equals("/api/rest/v1/accountManagement/customerBrands") )
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                            + pathIBaseRS + "/createOrUpdateBrand" );
                else
                    redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 4 )
                            + pathIBaseRS + METHOD_CREATE_OR_UPDATE );

                return AuthenticationFilter.httpClient.target( redirectURI ).request()
                        .post( Entity.entity( aDto, MediaType.APPLICATION_JSON ) );
            }
            else {
                ActionStatus notFoundStatus = new ActionStatus(ActionStatusEnum.FAIL,
                        MeveoApiErrorCodeEnum.URL_NOT_FOUND, "The specified URL cannot be found");

                return Response.status(Response.Status.NOT_FOUND)
                        .entity(notFoundStatus).type(MediaType.APPLICATION_JSON_TYPE).build();
            }
        }
        else {
            ActionStatus notFoundStatus = new ActionStatus(ActionStatusEnum.FAIL,
                    MeveoApiErrorCodeEnum.URL_NOT_FOUND, "The specified URL cannot be found");

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(notFoundStatus).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
    }

}
