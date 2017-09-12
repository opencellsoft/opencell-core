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

    public void preInstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preInstallModule(scriptContext);
    }

    public void postInstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postInstallModule(scriptContext);
    }

    public void preUninstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preUninstallModule(scriptContext);
    }

    public void postUninstallModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postUninstallModule(scriptContext);
    }

    public void preEnableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preEnableModule(scriptContext);
    }

    public void postEnableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postEnableModule(scriptContext);
    }

    public void preDisableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.preDisableModule(scriptContext);
    }

    public void postDisableModule(String scriptCode, MeveoModule module) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ModuleScriptInterface scriptInterface = (ModuleScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(Script.CONTEXT_ENTITY, module);
        scriptInterface.postDisableModule(scriptContext);
    }
}