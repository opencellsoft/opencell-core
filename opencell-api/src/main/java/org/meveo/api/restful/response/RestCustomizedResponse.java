package org.meveo.api.restful.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.meveo.api.dto.SellersDto;
import org.meveo.api.dto.response.SearchResponse;
import org.meveo.commons.utils.StringUtils;
import org.meveo.util.Inflector;

import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestCustomizedResponse extends SearchResponse {

    /** The sellers. */
    private SellersDto sellers = new SellersDto();

    /*
     * This function is used to format the response from the requests get all entities
     */
    public Map<String, Object> customizeResponse(Response getResponse, String entityName ) throws IOException {
        Map<String, Object> customResponse = new LinkedHashMap<>();
        if ( getResponse.hasEntity() ) {
            Object aResponse2 = getResponse.getEntity();
            Map<String, Object> origResponse = new ObjectMapper().readValue( (InputStream) aResponse2, Map.class );

            for (Map.Entry<String,Object> entry : origResponse.entrySet()) {
                if ( entry.getKey().equals("actionStatus") || entry.getKey().equals("paging") )
                    customResponse.put(entry.getKey(), entry.getValue());
                else if ( entry.getKey().equals( "pricePlanMatrixes" ) ) {
                    if ( entry.getValue() instanceof Map ) {
                        Map mapEntities = (Map) entry.getValue();
                        for (Object aKey : mapEntities.keySet()) {
                            customResponse.put( "pricePlanMatrices", mapEntities.get(aKey) );
                        }
                    }
                }
                else if ( entry.getKey().equals( "list" + StringUtils.capitalizeFirstLetter(entityName) )
                        || entry.getKey().equals( entityName ) || entry.getKey().equals( "dto" )
                        || entry.getKey().equals( Inflector.getInstance().pluralize(entityName) )
                        || entry.getKey().equals( Inflector.getInstance().pluralize(entityName) + "Dto" ) ) {
                    if ( entry.getValue() instanceof Map ) {
                        Map mapEntities = (Map) entry.getValue();
                        for (Object aKey : mapEntities.keySet()) {
                            if ( aKey.equals( Inflector.getInstance().singularize(entityName) ) ||
                                    aKey.equals( Inflector.getInstance().pluralize(entityName) ) ||
                                    aKey.equals( entityName ) )
                                if ( CollectionUtils.isNotEmpty((List) mapEntities.get(aKey)) )
                                    customResponse.put( Inflector.getInstance().pluralize(entityName), mapEntities.get(aKey) );
                        }
                    }
                    else if ( entry.getValue() instanceof List )
                        if ( CollectionUtils.isNotEmpty((List) entry.getValue() ) )
                            customResponse.put( Inflector.getInstance().pluralize(entityName), entry.getValue() );
                }
                else
                    customResponse.put(entry.getKey(), entry.getValue());
            }
        }

        return customResponse;
    }
}
