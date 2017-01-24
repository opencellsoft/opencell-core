package org.meveo.service.script.module;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.admin.User;
import org.meveo.model.module.MeveoModule;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;

@Singleton
@Startup
public class ModuleScriptService implements Serializable {

    private static final long serialVersionUID = -9085236365753820714L;


    @Inject
    @CurrentUser
    private MeveoUser currentUser;
    
    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void preInstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preInstallModule(scriptContext, currentUser);
    }

    public void postInstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postInstallModule(scriptContext, currentUser);
    }

    public void preUninstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preUninstallModule(scriptContext, currentUser);
    }

    public void postUninstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postUninstallModule(scriptContext, currentUser);
    }

    public void preEnableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preEnableModule(scriptContext, currentUser);
    }

    public void postEnableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postEnableModule(scriptContext, currentUser);
    }

    public void preDisableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preDisableModule(scriptContext, currentUser);
    }

    public void postDisableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(currentUser.getProvider(), scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postDisableModule(scriptContext, currentUser);
    }
}