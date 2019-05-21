/**
 * 
 */
package org.meveo.commons.encryption;

import javax.persistence.AttributeConverter;

/**
 * @author melyoussoufi
 * @lastModifiedVersion 6.2.1
 *
 */
public interface IEncryptionConverter extends AttributeConverter<String, String>, IEncryptable {

}
