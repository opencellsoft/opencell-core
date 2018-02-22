package org.meveo.service.script.product;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

/**
 * @author Edward P. Legaspi
 * 
 * This interface is implemented by a custom user class.
 */
public interface ProductScriptInterface extends ScriptInterface {

	/**
	 * Called at the beginning of BusinessOfferModelService.createOfferFromBOM
	 * method at the beginning of product template creation for each product to
	 * duplicate.
	 * 
	 * @param methodContext
	 *            Method variables in a form of a map where
	 *            CONTEXT_PARAMETERS=List&lt;CustomFieldDto&gt;
	 * @throws BusinessException business exception
	 */
	void beforeCreate(Map<String, Object> methodContext) throws BusinessException;

	/**
	 * Called at the end of BusinessOfferModelService.createOfferFromBOM method
	 * at the end of product template creation for each product to duplicate.
	 * 
	 * @param methodContext
	 *            Method variables in a form of a map where
	 *            CONTEXT_ENTITY=ServiceTemplate,
	 *            CONTEXT_PARAMETERS=List&lt;CustomFieldDto&gt;
	 * @throws BusinessException business exception
	 */
	void afterCreate(Map<String, Object> methodContext) throws BusinessException;

}
