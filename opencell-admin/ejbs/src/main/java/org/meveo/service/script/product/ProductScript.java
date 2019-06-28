package org.meveo.service.script.product;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.module.ModuleScript;

/**
 * @author Edward P. Legaspi
 */
public class ProductScript extends ModuleScript implements ProductScriptInterface {

    private static final long serialVersionUID = 109603059581188830L;

    @Override
    public void beforeCreate(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void afterCreate(Map<String, Object> methodContext) throws BusinessException {

    }
}