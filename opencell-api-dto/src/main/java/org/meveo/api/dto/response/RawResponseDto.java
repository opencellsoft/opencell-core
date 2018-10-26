package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A Dto class to wrap a raw API response.
 *
 * @author Said Ramli
 * @param <T> the generic type
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
public class RawResponseDto<T> extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L; 
    
    /** The response. */
    @XmlElement(required=true)
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
