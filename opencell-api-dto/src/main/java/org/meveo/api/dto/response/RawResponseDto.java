package org.meveo.api.dto.response;

/**
 * A Dto class to wrap a raw API response.
 *
 * @author Said Ramli
 * @param <T> the generic type
 */
public class RawResponseDto<T> extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L; 
    
    /** The response. */
    private T response;

    /**
     * Gets the response.
     *
     * @return the response
     */
    public T getResponse() {
        return response;
    }

    /**
     * Sets the response.
     *
     * @param response the response to set
     */
    public void setResponse(T response) {
        this.response = response;
    }

}
