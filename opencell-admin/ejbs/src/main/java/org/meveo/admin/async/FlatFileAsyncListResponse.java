/**
 * 
 */
package org.meveo.admin.async;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anasseh
 *
 */
public class FlatFileAsyncListResponse {
    private List<FlatFileAsyncUnitResponse> responses = new ArrayList<FlatFileAsyncUnitResponse>();
   
    public FlatFileAsyncListResponse() {        
    }

    /**
     * @return the responses
     */
    public List<FlatFileAsyncUnitResponse> getResponses() {
        return responses;
    }

    /**
     * @param responses the responses to set
     */
    public void setResponses(List<FlatFileAsyncUnitResponse> responses) {
        this.responses = responses;
    }    
}
