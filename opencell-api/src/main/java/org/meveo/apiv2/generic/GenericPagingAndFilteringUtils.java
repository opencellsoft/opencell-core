package org.meveo.apiv2.generic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

/**
 * Utils class for working with GenericPagingAndFiltering.
 *
 * @author Thang Nguyen
 */

public class GenericPagingAndFilteringUtils {

    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String SORT = "sort";
    private static final String FIELDS = "fields";
    private static final char DESCENDING_SIGN = '-';
    private static final char OPEN_HOOK = '[';
    private static final char CLOSE_HOOK = ']';
    private static final char OPEN_ACCOLADE = '{';
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

    private static GenericPagingAndFilteringUtils instance = new GenericPagingAndFilteringUtils();

    private GenericPagingAndFilteringUtils(){}

    public static GenericPagingAndFilteringUtils getInstance(){
        return instance;
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

            if ( ! MapUtils.isEmpty(genericFilters) )
                pagingAndFiltering.setFilters( genericFilters );
        }
    }

    public PaginationConfiguration getPaginationConfiguration(){
        PaginationConfiguration aPagingConfig = paginationConfig;
        reinitializePaginationConfiguration();
        return aPagingConfig;
    }

    public void reinitializePaginationConfiguration(){
        paginationConfig = new PaginationConfiguration(null );
    }

    public PagingAndFiltering getPagingAndFiltering(){
        PagingAndFiltering aPagingAndFiltering = pagingAndFiltering;
        reinitializePagingAndFiltering();
        return aPagingAndFiltering;
    }

    public void reinitializePagingAndFiltering(){
        pagingAndFiltering = new PagingAndFiltering();
    }

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