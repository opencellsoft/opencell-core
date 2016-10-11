package org.meveo.api.ws;

import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;

@WebService
public interface FilteredListWs extends IBaseWs {

	/**
     * Execute a filter to retrieve a list of entities
     * 
     * @param filter - if the code is set we lookup the filter in DB, else we parse the inputXml to create a transient filter
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @return
     */
    @WebMethod
    public FilteredListResponseDto listByFilter(@WebParam(name = "filter") FilterDto filter, @WebParam(name = "from") Integer from,
    		@WebParam(name = "size") Integer size);
    

    /**
     * Execute a search in Elastic Search on all fields (_all field)
     * 
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param query Query - words (will be joined by AND) or query expression (+word1 - word2)
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @return	
     */
    @WebMethod
    public FilteredListResponseDto search(@WebParam(name = "classnamesOrCetCodes") String[] classnamesOrCetCodes, @WebParam(name = "query") String query,
            @WebParam(name = "from") Integer from, @WebParam(name = "size") Integer size);

    /**
     * Execute a search in Elastic Search on given fields for given values
     * 
     * @param classnamesOrCetCodes Entity classes to match - full class name
     * @param query Fields and values to match in a form of a map
     * @param from Pagination - starting record
     * @param size Pagination - number of records per page
     * @return
     */
    @WebMethod
    public FilteredListResponseDto searchByField(@WebParam(name = "classnamesOrCetCodes") String[] classnamesOrCetCodes, @WebParam(name = "query") Map<String, String> query,
            @WebParam(name = "from") Integer from, @WebParam(name = "size") Integer size);

    /**
     * Clean and reindex Elastic Search repository
     * 
     * @return
     */
    public ActionStatus reindex();
}