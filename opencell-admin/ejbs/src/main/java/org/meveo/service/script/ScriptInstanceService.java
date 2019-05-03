/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;

/**
 * @author melyoussoufi
 * @lastModifiedVersion 7.2.0
 *
 */
@Singleton
@Lock(LockType.READ)
public class ScriptInstanceService extends CustomScriptService<ScriptInstance, ScriptInterface> {
	
	@Inject
    private ScriptInstanceServiceStateless scriptInstanceServiceStateless;

    /**
     * Compile all scriptInstances.
     */
    public void compileAll() {

        List<ScriptInstance> scriptInstances = scriptInstanceServiceStateless.findByType(ScriptSourceTypeEnum.JAVA);
        compile(scriptInstances);
    }

    /**
     * Execute the script identified by a script code. No init nor finalize methods are called.
     *
     * @param scriptCode ScriptInstanceCode
     * @param context Context parameters (optional)
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws ElementNotFoundException Script not found
     * @throws BusinessException Any execution exception
     */
    @Override
    @Lock(LockType.READ)
    public Map<String, Object> execute(String scriptCode, Map<String, Object> context) throws BusinessException {

        ScriptInstance scriptInstance = findByCode(scriptCode);
        // Check access to the script
        scriptInstanceServiceStateless.isUserHasExecutionRole(scriptInstance);
        return super.execute(scriptCode, context);
    }

    /**
     * Wrap the logger and execute script.
     *
     * @param scriptInstance Script to test
     * @param context context used in execution of script.
     * @return Log messages
     */
    public String test(ScriptInstance scriptInstance, Map<String, Object> context) {
        try {

        	scriptInstanceServiceStateless.isUserHasExecutionRole(scriptInstance);
            String javaSrc = scriptInstance.getScript();
            javaSrc = javaSrc.replaceAll("\\blog.", "logTest.");
            Class<ScriptInterface> compiledScript = compileJavaSource(javaSrc);
            ScriptInterface scriptClassInstance = compiledScript.newInstance();

            executeWInitAndFinalize(scriptClassInstance, context);

            String logMessages = scriptClassInstance.getLogMessages();
            return logMessages;

        } catch (CharSequenceCompilerException e) {
            log.error("Failed to compile script {}. Compilation errors:", scriptInstance.getCode());

            List<ScriptInstanceError> scriptErrors = new ArrayList<>();

            List<Diagnostic<? extends JavaFileObject>> diagnosticList = e.getDiagnostics().getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
                if ("ERROR".equals(diagnostic.getKind().name())) {
                    ScriptInstanceError scriptInstanceError = new ScriptInstanceError();
                    scriptInstanceError.setMessage(diagnostic.getMessage(Locale.getDefault()));
                    scriptInstanceError.setLineNumber(diagnostic.getLineNumber());
                    scriptInstanceError.setColumnNumber(diagnostic.getColumnNumber());
                    scriptInstanceError.setSourceFile(diagnostic.getSource().toString());
                    // scriptInstanceError.setScript(scriptInstance);
                    scriptErrors.add(scriptInstanceError);
                    // scriptInstanceErrorService.create(scriptInstanceError, scriptInstance.getAuditable().getCreator());
                    log.warn("{} script {} location {}:{}: {}", diagnostic.getKind().name(), scriptInstance.getCode(), diagnostic.getLineNumber(), diagnostic.getColumnNumber(),
                        diagnostic.getMessage(Locale.getDefault()));
                }
            }
            scriptInstance.setError(scriptErrors != null && !scriptErrors.isEmpty());
            scriptInstance.setScriptErrors(scriptErrors);

            return "Compilation errors";

        } catch (Exception e) {
            log.error("Script test failed", e);
            return ExceptionUtils.getStackTrace(e);
        }
    }

    /**
     * Get all script interfaces with compiling those that are not compiled yet
     * 
     * @return the allScriptInterfaces
     */
    public List<Class<ScriptInterface>> getAllScriptInterfacesWCompile() {

        List<Class<ScriptInterface>> scriptInterfaces = new ArrayList<>();

        List<ScriptInstance> scriptInstances = scriptInstanceServiceStateless.findByType(ScriptSourceTypeEnum.JAVA);
        for (ScriptInstance scriptInstance : scriptInstances) {
            try {
                scriptInterfaces.add(getScriptInterfaceWCompile(scriptInstance.getCode()));
            } catch (ElementNotFoundException | InvalidScriptException e) {
                // Ignore errors here as they were logged in a call before
            }
        }

        return scriptInterfaces;
    }
}