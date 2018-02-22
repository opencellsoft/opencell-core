package org.meveo.service.script.module;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

public interface ModuleScriptInterface extends ScriptInterface {

    /**
     * Module being installed - called before installation starts
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=MeveoModule
     * 
     * @throws BusinessException business exception.
     */
    public void preInstallModule(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Module being installed - called after installation completes successfully
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=MeveoModule
     * 
     * @throws BusinessException business exception.
     */
    public void postInstallModule(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Module being uninstalled - called before uninstallation starts
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=MeveoModule
     * 
     * @throws BusinessException business exception.
     */
    public void preUninstallModule(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Module being uninstalled - called after uninstallation completes successfully
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=MeveoModule
     * 
     * @throws BusinessException business exception.
     */
    public void postUninstallModule(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Module being activated - called before activation starts. Not called when module is being installed - only when activating desactivated module.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=MeveoModule
     * 
     * @throws BusinessException business exception.
     */
    public void preEnableModule(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Module being activated - called after activation completes successfully. Not called when module is being installed - only when activating desactivated module.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=MeveoModule
     * 
     * @throws BusinessException business exception.
     */
    public void postEnableModule(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Module being deactivated - called before deactivation starts. Not called when active module is being deinstalled - only when deactivating an installed and active module
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=MeveoModule
     * 
     * @throws BusinessException business exception.
     */
    public void preDisableModule(Map<String, Object> methodContext) throws BusinessException;

    /**
     * Module being deactivated - called after deactivation completes successfully. Not called when active module is being deinstalled - only when deactivating an installed and
     * active module
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=MeveoModule
     * 
     * @throws BusinessException business exception.
     */
    public void postDisableModule(Map<String, Object> methodContext) throws BusinessException;
}