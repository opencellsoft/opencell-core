package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author Edward P. Legaspi
 **/
public class InvalidImageData extends MeveoApiException {

	private static final long serialVersionUID = 667358860150384863L;

	public InvalidImageData() {
		super("Invalid image data.");
		setErrorCode(MeveoApiErrorCodeEnum.INVALID_IMAGE_DATA);
	}

	public InvalidImageData(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCodeEnum.INVALID_IMAGE_DATA);
	}

}
