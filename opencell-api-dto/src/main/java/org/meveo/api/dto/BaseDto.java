package org.meveo.api.dto;

import java.io.Serializable;

import org.meveo.api.message.exception.InvalidDTOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@JsonInclude(Include.NON_NULL)
public abstract class BaseDto implements Serializable {

    private static final long serialVersionUID = 4456089256601996946L;

    public void validate() throws InvalidDTOException {

    }

}
