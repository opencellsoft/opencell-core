package org.meveo.api.restful.util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.restful.pagingFiltering.ImmutablePagingAndFilteringRest;
import org.meveo.api.restful.pagingFiltering.PagingAndFilteringRest;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

/**
 * Utils class for working with GenericPagingAndFiltering.
 *
 * @author Thang Nguyen
 */

public class GenericPagingAndFilteringUtils {

    private static final String LIMIT = "limit";

    private static final String API_LIST_MAX_LIMIT_KEY = "api.list.maxLimit";
    private static final String API_LIST_DEFAULT_LIMIT = "api.list.defaultLimit";
    private static final String OFFSET = "offset";
    private static final String SORT = "sort";
    private static final String FIELDS = "fields";
    private static final char DESCENDING_SIGN = '-';
    private static final char OPEN_HOOK = '[';
    private static final char CLOSE_HOOK = ']';
    public static final String OPEN_ACCOLADE = "{";
    public static final String OPEN_ACCOLADE_ENCODED = "%7B";
    public static final String CLOSE_ACCOLADE = "}";
    public static final String CLOSE_ACCOLADE_ENCODED = "%7D";
    public static final String BLANK_SPACE = " ";
    public static final String BLANK_SPACE_ENCODED = "%20";
    public static final String QUOTE = "\"";
    public static final String QUOTE_ENCODED = "%22";
    private static final String ASCENDING_ORDER = "ASC";
    private static final String DESCENDING_ORDER = "DESC";
    private static final String MULTI_SORTING_DELIMITER = ",";

    // pagination configuration
    private PaginationConfiguration paginationConfig;
    private PagingAndFiltering pagingAndFiltering;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    private static GenericPagingAndFilteringUtils instance = new GenericPagingAndFilteringUtils();

    private GenericPagingAndFilteringUtils(){}

    public static GenericPagingAndFilteringUtils getInstance(){
        return instance;
    }

    /**
     * Function used to create an instance of PagingAndFiltering from the generic pagingAndFilteringRest
     *
     * @param pagingAndFilteringRest PagingAndFilteringRest containing all query params (limit, offset, sort, filters etc.)
     * @return an instance of PagingAndFiltering
     * set paging and filtering as following format :
     *         "offset": 0,
     *         "limit": 4,
     *         "sort": "-description,-id",
     *         filters : {
     *               "likeCriteria description": "*Description*"
     *               "code": "*FR",
     *               "SQL": "code='SELLER_FR'",
     *               "inList id": [-3,1],
     *               "ne id": -3,
     *               "fromRange id": -6,
     *               "toRange id": 0
     *         }
     */
    public void constructPagingAndFiltering(PagingAndFilteringRest pagingAndFilteringRest,MultivaluedMap<String, String> queryParamsMap) {
        if ( pagingAndFilteringRest == null )
            pagingAndFilteringRest = ImmutablePagingAndFilteringRest.builder().build();

        pagingAndFiltering = new PagingAndFiltering();

        Integer limitParameter = queryParamsMap != null && queryParamsMap.get("limit") != null ? Integer.parseInt(queryParamsMap.get("limit").get(0)) : null;
        pagingAndFiltering.setLimit((int) instance.getLimit(limitParameter));
        pagingAndFiltering.setOffset( pagingAndFilteringRest.getOffset() );

        String allSortFieldsAndOrders = pagingAndFilteringRest.getSort();
        String[] allSortFieldsSplit = allSortFieldsAndOrders.split(MULTI_SORTING_DELIMITER);
        StringBuilder sortFields = new StringBuilder();
        for ( int i = 0; i < allSortFieldsSplit.length - 1; i++ ) {
            if ( allSortFieldsSplit[i].charAt(0) == DESCENDING_SIGN ) {
                // Remove the sign '-' in case of DESCENDING
                sortFields.append( allSortFieldsSplit[i].substring(1) + BLANK_SPACE
                        + DESCENDING_ORDER + MULTI_SORTING_DELIMITER + BLANK_SPACE );
            }
            else {
                sortFields.append( allSortFieldsSplit[i] + BLANK_SPACE
                        + ASCENDING_ORDER + MULTI_SORTING_DELIMITER + BLANK_SPACE );
            }
        }
        if ( allSortFieldsSplit[allSortFieldsSplit.length - 1].charAt(0) == DESCENDING_SIGN ) {
            // Remove the sign '-' in case of DESCENDING
            sortFields.append( allSortFieldsSplit[allSortFieldsSplit.length - 1].substring(1) + BLANK_SPACE
                    + DESCENDING_ORDER );
        }
        else {
            sortFields.append( allSortFieldsSplit[allSortFieldsSplit.length - 1] + BLANK_SPACE
                    + ASCENDING_ORDER );
        }
        pagingAndFiltering.setSortBy( sortFields.toString() );

        if ( ! MapUtils.isEmpty(pagingAndFilteringRest.getFilters()) ) {
            Map<String, Object> genericFilters = new HashMap<>(pagingAndFilteringRest.getFilters());
            pagingAndFiltering.setFilters( genericFilters );
        }
    }

    public long getLimit(Integer userLimit) {

        int limit = 0;
        int apiListMaxLimit = ParamBean.getInstance().getPropertyAsInteger(API_LIST_MAX_LIMIT_KEY, 1000);
        int apiDefaultLimit = ParamBean.getInstance().getPropertyAsInteger(API_LIST_DEFAULT_LIMIT, 100);

        if (userLimit != null && userLimit > 0) {
            limit = Math.min(userLimit, apiListMaxLimit);
        } else {
            limit = Math.min(apiDefaultLimit, apiListMaxLimit);
        }

        return limit;
    }

    /**
     * Is used to create an instance of PagingAndFiltering
     *
     * @param queryParams a multivaluedMap containing all query params (limit, offset, etc.)
     * @return an instance of PagingAndFiltering
     */
    public void constructPagingAndFiltering(MultivaluedMap<String, String> queryParams) throws JsonProcessingException {
        pagingAndFiltering = new PagingAndFiltering();
        Iterator<String> itQueryParams = queryParams.keySet().iterator();
        Map<String, Object> genericFilters = new HashMap<>();
        List<String> aList;
        while (itQueryParams.hasNext()){
            String aKey = itQueryParams.next();
            aList = queryParams.get(aKey);
            if ( aKey.equals( LIMIT ) )
                pagingAndFiltering.setLimit( Integer.parseInt( aList.get(0) ) );
            else if ( aKey.equals( OFFSET ) )
                pagingAndFiltering.setOffset( Integer.parseInt( aList.get(0) ) );
            else if ( aKey.equals( SORT ) ) {
                String allSortFieldsAndOrders = aList.get(0);
                String[] allSortFieldsSplit = allSortFieldsAndOrders.split(MULTI_SORTING_DELIMITER);
                StringBuilder sortOrders = new StringBuilder();
                StringBuilder sortFields = new StringBuilder();
                for ( int i = 0; i < allSortFieldsSplit.length - 1; i++ ) {
                    if ( allSortFieldsSplit[i].charAt(0) == DESCENDING_SIGN ) {
                        sortOrders.append( DESCENDING_ORDER + MULTI_SORTING_DELIMITER );
                        // Remove the sign '-' in case of DESCENDING
                        sortFields.append( allSortFieldsSplit[i].substring(1) + BLANK_SPACE
                                + DESCENDING_ORDER + MULTI_SORTING_DELIMITER + BLANK_SPACE );
                    }
                    else {
                        sortOrders.append( ASCENDING_ORDER + MULTI_SORTING_DELIMITER );
                        sortFields.append( allSortFieldsSplit[i] + BLANK_SPACE
                                + ASCENDING_ORDER + MULTI_SORTING_DELIMITER + BLANK_SPACE );
                    }
                }
                if ( allSortFieldsSplit[allSortFieldsSplit.length - 1].charAt(0) == DESCENDING_SIGN ) {
                    sortOrders.append( DESCENDING_ORDER );
                    // Remove the sign '-' in case of DESCENDING
                    sortFields.append( allSortFieldsSplit[allSortFieldsSplit.length - 1].substring(1) + BLANK_SPACE
                            + DESCENDING_ORDER );
                }
                else {
                    sortOrders.append( ASCENDING_ORDER );
                    sortFields.append( allSortFieldsSplit[allSortFieldsSplit.length - 1] + BLANK_SPACE
                            + ASCENDING_ORDER );
                }
                pagingAndFiltering.setMultiSortOrder( sortOrders.toString() );
                pagingAndFiltering.setSortBy( sortFields.toString() );
            }
            else if ( aKey.equals( FIELDS ) ) {
                pagingAndFiltering.addFields( aList.get(0) );
            }
            else {
                ObjectMapper jsonMapper = new ObjectMapper();
                Object anObject = jsonMapper.readValue( aList.get(0), Object.class );
                genericFilters.put( aKey, anObject );
            }
        }

        if ( ! MapUtils.isEmpty(genericFilters) )
            pagingAndFiltering.setFilters( genericFilters );
    }

    public PaginationConfiguration getPaginationConfiguration(){
        if ( paginationConfig == null )
            paginationConfig = new PaginationConfiguration(null);
        return paginationConfig;
    }

    public PagingAndFiltering getPagingAndFiltering(){
    	PagingAndFilteringRest pagingAndFilteringRest = null;
    	if(pagingAndFiltering == null)
    		constructPagingAndFiltering(pagingAndFilteringRest,null);
        return pagingAndFiltering;
    }

//    public PaginationConfiguration getPaginationConfiguration(){
//        PaginationConfiguration aPagingConfig = paginationConfig;
//        reinitializePaginationConfiguration();
//        return aPagingConfig;
//    }
//
//    public void reinitializePaginationConfiguration(){
//        paginationConfig = new PaginationConfiguration(null );
//    }
//
//    public PagingAndFiltering getPagingAndFiltering(){
//        PagingAndFiltering aPagingAndFiltering = pagingAndFiltering;
//        reinitializePagingAndFiltering();
//        return aPagingAndFiltering;
//    }
//
//    public void reinitializePagingAndFiltering(){
//        pagingAndFiltering = new PagingAndFiltering();
//    }

    public void generatePagingConfig(Class entityClass){
        Map<String, Object> filters = pagingAndFiltering.getFilters();

        if ( filters == null )
            paginationConfig = new PaginationConfiguration(pagingAndFiltering.getOffset(), pagingAndFiltering.getLimit(),
                    null, pagingAndFiltering.getFullTextFilter(),
                    Collections.emptyList(), pagingAndFiltering.getSortBy(),
                    pagingAndFiltering.getMultiSortOrder());
        else {
            GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
            paginationConfig = new PaginationConfiguration(pagingAndFiltering.getOffset(), pagingAndFiltering.getLimit(),
                    genericRequestMapper.evaluateFilters( filters, entityClass ), pagingAndFiltering.getFullTextFilter(),
                    Collections.emptyList(), pagingAndFiltering.getSortBy(),
                    pagingAndFiltering.getMultiSortOrder());
        }
    }
}