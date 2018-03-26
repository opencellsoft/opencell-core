/**
 * 
 */
package org.meveo.admin.async;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class FlatFileAsyncListResponse its a list for FlatFileAsyncUnitResponse.
 * 
 * @author anasseh
 * @lastModifiedVersion willBeSetLater
 */
public class FlatFileAsyncListResponse {
    
    /** The responses. */
    private List<FlatFileAsyncUnitResponse> responses = new ArrayList<FlatFileAsyncUnitResponse>();
   
    /**
     * Instantiates a new flat file async list response.
     */
    public FlatFileAsyncListResponse() {        
    }

    /**
     * Gets the responses.
     *
     * @return the responses
     */
    public List<FlatFileAsyncUnitResponse> getResponses() {
        return responses;
    }

    /**
     * Sets the responses.
     *
     * @param responses the responses to set
     */
    public void setResponses(List<FlatFileAsyncUnitResponse> responses) {
        this.responses = responses;
    }    
}
