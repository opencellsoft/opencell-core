package org.meveo.service.script.product;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.module.ModuleScript;

/**
 * @author Edward P. Legaspi
 */
public class ProductScript extends ModuleScript implements ProductScriptInterface {

    /**
     * Called before a new product is created.
     */
	@Override
	public void beforeCreate(Map<String, Object> methodContext) throws BusinessException {

	}

	/**
	 * Called after a new product is created.
	 */
	@Override
	public void afterCreate(Map<String, Object> methodContext) throws BusinessException {

	}

}
