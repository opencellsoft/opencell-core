package org.meveo.service.script.module;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.Script;

/**
 * @author Andrius Karpavicius
 **/
public class ModuleScript extends Script implements ModuleScriptInterface {

    @Override
    public void preInstallModule(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void postInstallModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void preUninstallModule(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void postUninstallModule(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void preEnableModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void postEnableModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void preDisableModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void postDisableModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }
}