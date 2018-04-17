package org.meveo.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

import org.meveo.api.message.exception.InvalidDTOException;

/**
 * A base class for all API DTO classes
 * 
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@JsonInclude(Include.NON_NULL)
public abstract class BaseDto implements Serializable {

    private static final long serialVersionUID = 4456089256601996946L;

    /**
     * Validate DTO
     * 
     * @throws InvalidDTOException Validation exception
     */
    public void validate() throws InvalidDTOException {

    }
}