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
