package org.meveo.api.dto.response;

/**
 * A Dto class to wrap a raw API response
 * @author Said Ramli
 *
 * @param <T>
 */
public class RawResponseDto<T> extends BaseResponse {

    private static final long serialVersionUID = 1L; 
    
    /** The response. */
    private T response;

    /**
     * @return the response
     */
    public T getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(T response) {
        this.response = response;
    }

}
