/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

/**
 * 
 */
package org.meveo.commons.encryption;

import org.meveo.commons.utils.ParamBean;

/**
 * @author melyoussoufi
 * @lastModifiedVersion 6.2.1
 *
 */
public class PersonnalDataEncryptor implements IEncryptionConverter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.
	 * Object) Checks if encryption is enabled before performing encryption
	 */
	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (TRUE_STR
				.equalsIgnoreCase(ParamBean.getInstance().getProperty(ENCRYPT_PERSONNAL_DATA_PROPERTY, FALSE_STR))) {
			return encrypt(attribute);
		}
		return attribute;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.
	 * Object) Checks if encryption is enabled before performing decryption
	 */
	@Override
	public String convertToEntityAttribute(String dbData) {
		if (TRUE_STR
				.equalsIgnoreCase(ParamBean.getInstance().getProperty(ENCRYPT_PERSONNAL_DATA_PROPERTY, FALSE_STR))) {
			return decrypt(dbData);
		}else if(dbData != null && FALSE_STR
				.equalsIgnoreCase(ParamBean.getInstance().getProperty(ENCRYPT_PERSONNAL_DATA_PROPERTY, FALSE_STR)) && dbData.startsWith(AES_ALOGRITHM)) {
			return ON_ERROR_RETURN;
		}
		return dbData;
	}

}
