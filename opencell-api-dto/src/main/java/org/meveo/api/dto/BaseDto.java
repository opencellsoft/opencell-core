package org.meveo.api.dto;

import java.io.Serializable;

import org.meveo.api.message.exception.InvalidDTOException;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
public abstract class BaseDto implements Serializable {

	private static final long serialVersionUID = 4456089256601996946L;

	public void validate() throws InvalidDTOException {

	}

}
