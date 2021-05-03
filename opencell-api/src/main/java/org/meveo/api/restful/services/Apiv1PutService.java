package org.meveo.api.restful.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meveo.api.dto.*;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.billing.*;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.invoice.CancelInvoiceRequestDto;
import org.meveo.api.dto.invoice.ValidateInvoiceRequestDto;
import org.meveo.api.restful.GenericOpencellRestfulAPIv1;
import org.meveo.api.restful.filter.AuthenticationFilter;
import org.meveo.apiv2.generic.core.GenericHelper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Thang Nguyen
 */
public class Apiv1PutService {

    private static final String METHOD_UPDATE = "/";

    private static final String FORWARD_SLASH = "/";
    private static final String DTO_SUFFIX = "dto";

    private static final String API_REST = "api/rest";

    private List<PathSegment> segmentsOfPathAPIv1;
    private String entityCode;
    private String pathIBaseRS;
    private String entityClassName;

    /*
     * Function used to update a special service, such as : activation, suspension, termination, update services of a subscription,
     * cancel/validate an existing invoice, cancel/validate a billing run, send an existing invoice by email
     */
    public Response updateService(UriInfo uriInfo, String putPath, String jsonDto) throws URISyntaxException, JsonProcessingException {
        URI redirectURI;
        segmentsOfPathAPIv1 = uriInfo.getPathSegments();
        pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_REGEX_PATH_AND_IBASE_RS_PATH.get( putPath );

        Class entityDtoClass = GenericOpencellRestfulAPIv1.MAP_SPECIAL_IBASE_RS_PATH_AND_DTO_CLASS.get( pathIBaseRS );
        Object aDto = null;

        if ( entityDtoClass != null ) {
            aDto = new ObjectMapper().readValue( jsonDto, entityDtoClass );

            // Handle the special endpoint, such as: activation, suspension, termination, update services of a subscription,
            // cancel/validate an existing invoice, cancel/validate a billing run, send an existing invoice by email
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS );

            if ( aDto instanceof ActivateSubscriptionRequestDto)
                ((ActivateSubscriptionRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString() );
            else if ( aDto instanceof OperationSubscriptionRequestDto)
                ((OperationSubscriptionRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString() );
            else if ( aDto instanceof TerminateSubscriptionRequestDto)
                ((TerminateSubscriptionRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString() );
            else if ( aDto instanceof UpdateServicesRequestDto)
                ((UpdateServicesRequestDto) aDto).setSubscriptionCode( segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString() );
            else if ( aDto instanceof CancelInvoiceRequestDto)
                ((CancelInvoiceRequestDto) aDto).setInvoiceId( Long.parseLong(segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString()) );
            else if ( aDto instanceof CancelBillingRunRequestDto)
                ((CancelBillingRunRequestDto) aDto).setBillingRunId( Long.parseLong(segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString()) );
            else if ( aDto instanceof ValidateInvoiceRequestDto)
                ((ValidateInvoiceRequestDto) aDto).setInvoiceId( Long.parseLong(segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString()) );
            else if ( aDto instanceof ValidateBillingRunRequestDto )
                ((ValidateBillingRunRequestDto) aDto).setBillingRunId( Long.parseLong(segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString()) );
        }
        else {
            // Handle the special endpoint, such as: stop a job
            redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                    + API_REST + pathIBaseRS + FORWARD_SLASH + segmentsOfPathAPIv1.get(segmentsOfPathAPIv1.size() - 2).toString() );
        }

        return AuthenticationFilter.httpClient.target( redirectURI ).request().put( Entity.entity(aDto, MediaType.APPLICATION_JSON) );
    }

    /*
     * Function used to update an entity or a service
     */
    public Response updateEntity(UriInfo uriInfo, String pathUpdate, String jsonDto) throws URISyntaxException, JsonProcessingException {
        URI redirectURI;
        segmentsOfPathAPIv1 = uriInfo.getPathSegments();

        pathIBaseRS = GenericOpencellRestfulAPIv1.MAP_NEW_PATH_AND_IBASE_RS_PATH.get( pathUpdate );
        entityClassName = pathIBaseRS.split( FORWARD_SLASH )[ pathIBaseRS.split( FORWARD_SLASH ).length - 1 ];

        if ( entityClassName.equals( "job" ) )
            entityClassName = "jobInstance";
        else if ( entityClassName.equals( "timer" ) )
            entityClassName = "timerEntity";
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.TRIGGERED_EDR ) )
            entityClassName = "triggeredEDRTemplate";
        else if ( pathIBaseRS.equals( Apiv1ConstantDictionary.CUSTOM_ENTITY_TEMPLATE ) )
            entityClassName = "customEntityTemplate";

        Class entityDtoClass = GenericHelper.getEntityDtoClass( entityClassName.toLowerCase() + DTO_SUFFIX );
        entityCode = segmentsOfPathAPIv1.get( segmentsOfPathAPIv1.size() - 1 ).getPath();
        Object aDto = new ObjectMapper().readValue( jsonDto, entityDtoClass );
        if ( aDto instanceof BusinessEntityDto)
            ((BusinessEntityDto) aDto).setCode(entityCode);
        else if ( aDto instanceof IEntityDto)
            ((IEntityDto) aDto).setId(Long.parseLong(entityCode));
        else if ( aDto instanceof AccountHierarchyDto)
            ((AccountHierarchyDto) aDto).setCustomerCode(entityCode);
        else if ( aDto instanceof AccessDto)
            ((AccessDto) aDto).setCode(entityCode);
        else if ( aDto instanceof LanguageDto)
            ((LanguageDto) aDto).setCode(entityCode);
        else if ( aDto instanceof CountryDto)
            ((CountryDto) aDto).setCountryCode(entityCode);
        else if ( aDto instanceof CurrencyDto)
            ((CurrencyDto) aDto).setCode(entityCode);
        else if ( aDto instanceof UserDto )
            ((UserDto) aDto).setUsername(entityCode);
        else if ( aDto instanceof DiscountPlanItemDto)
            ((DiscountPlanItemDto) aDto).setCode(entityCode);
        else if ( aDto instanceof CountryIsoDto )
            ((CountryIsoDto) aDto).setCountryCode(entityCode);
        else if ( aDto instanceof CurrencyIsoDto )
            ((CurrencyIsoDto) aDto).setCode(entityCode);

        redirectURI = new URI( uriInfo.getBaseUri().toString().substring(0, uriInfo.getBaseUri().toString().length() - 3 )
                + API_REST + pathIBaseRS + METHOD_UPDATE );

        return AuthenticationFilter.httpClient.target( redirectURI ).request().put( Entity.entity( aDto, MediaType.APPLICATION_JSON ) );
    }

}
