package org.meveo.service.script.module;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.module.MeveoModule;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;

@Stateless
public class ModuleScriptService implements Serializable {

    private static final long serialVersionUID = -9085236365753820714L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public ModuleScriptInterface preInstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preInstallModule(scriptContext);
        return scriptInterface;
    }

    public void postInstallModule(ModuleScriptInterface scriptInterface, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postInstallModule(scriptContext);
    }

    public ModuleScriptInterface preUninstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preUninstallModule(scriptContext);
        return scriptInterface;
    }

    public void postUninstallModule(ModuleScriptInterface scriptInterface, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postUninstallModule(scriptContext);
    }

    public ModuleScriptInterface preEnableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preEnableModule(scriptContext);
        return scriptInterface;
    }

    public void postEnableModule(ModuleScriptInterface scriptInterface, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postEnableModule(scriptContext);
    }

    public ModuleScriptInterface preDisableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preDisableModule(scriptContext);
        return scriptInterface;
    }

    public void postDisableModule(ModuleScriptInterface scriptInterface, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postDisableModule(scriptContext);
    }
}