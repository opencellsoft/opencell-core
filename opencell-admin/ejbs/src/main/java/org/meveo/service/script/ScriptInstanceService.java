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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.security.Role;

@Singleton
@Lock(LockType.READ)
public class ScriptInstanceService extends CustomScriptService<ScriptInstance, ScriptInterface> {

    /**
     * Get all ScriptInstances with error.
     *
     * @return list of custom script.
     */
    public List<CustomScript> getScriptInstancesWithError() {
        return ((List<CustomScript>) getEntityManager().createNamedQuery("CustomScript.getScriptInstanceOnError", CustomScript.class).setParameter("isError", Boolean.TRUE)
            .getResultList());
    }

    /**
     * Count scriptInstances with error.
     * 
     * @return number of script instances with error.
     */
    public long countScriptInstancesWithError() {
        return ((Long) getEntityManager().createNamedQuery("CustomScript.countScriptInstanceOnError", Long.class).setParameter("isError", Boolean.TRUE).getSingleResult());
    }

    /**
     * Compile all scriptInstances.
     */
    public void compileAll() {

        List<ScriptInstance> scriptInstances = findByType(ScriptSourceTypeEnum.JAVA);
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
        isUserHasExecutionRole(scriptInstance);
        return super.execute(scriptCode, context);
    }

    /**
     * Wrap the logger and execute script.
     *
     * @param scriptCode code of script
     * @param context context used in execution of script.
     * @return Log messages
     */
    public String test(String scriptCode, Map<String, Object> context) {
        try {
            ScriptInstance scriptInstance = findByCode(scriptCode);
            isUserHasExecutionRole(scriptInstance);
            String javaSrc = scriptInstance.getScript();
            javaSrc = javaSrc.replaceAll("log.", "logTest.");
            Class<ScriptInterface> compiledScript = compileJavaSource(javaSrc);
            ScriptInterface scriptClassInstance = compiledScript.newInstance();
            executeWInitAndFinalize(scriptClassInstance, context);

            String logMessages = scriptClassInstance.getLogMessages();
            return logMessages;

        } catch (Exception e) {
            log.error("Script test failed", e);
            return ExceptionUtils.getStackTrace(e);
        }
    }

    /**
     * Only users having a role in executionRoles can execute the script, not having the role should throw an InvalidPermission exception that extends businessException. A script
     * with no executionRoles can be executed by any user.
     *
     * @param scriptInstance instance of script
     * @throws InvalidPermissionException invalid permission exception.
     */
    public void isUserHasExecutionRole(ScriptInstance scriptInstance) throws InvalidPermissionException {
        if (scriptInstance != null && scriptInstance.getExecutionRoles() != null && !scriptInstance.getExecutionRoles().isEmpty()) {
            Set<Role> execRoles = scriptInstance.getExecutionRoles();
            for (Role role : execRoles) {
                if (currentUser.hasRole(role.getName())) {
                    return;
                }
            }
            throw new InvalidPermissionException();
        }
    }

    /**
     * @param scriptInstance instance of script
     * @return true if user have the souring role.
     */
    public boolean isUserHasSourcingRole(ScriptInstance scriptInstance) {
        if (scriptInstance != null && scriptInstance.getSourcingRoles() != null && !scriptInstance.getSourcingRoles().isEmpty()) {
            Set<Role> sourcingRoles = scriptInstance.getSourcingRoles();
            for (Role role : sourcingRoles) {
                if (currentUser.hasRole(role.getName())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * This is used to invoke a method in a new transaction from a script.<br>
     * This will prevent DB errors in the script from affecting notification history creation.
     *
     * @param workerName The name of the API or service that will be invoked.
     * @param methodName The name of the method that will be invoked.
     * @param parameters The array of parameters accepted by the method. They must be specified in exactly the same order as the target method.
     * @throws BusinessException business exception.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callWithNewTransaction(String workerName, String methodName, Object... parameters) throws BusinessException {
        try {
            Object worker = EjbUtils.getServiceInterface(workerName);
            String workerClassName = ReflectionUtils.getCleanClassName(worker.getClass().getName());
            Class<?> workerClass = Class.forName(workerClassName);
            Method method = null;
            if (parameters.length < 1) {
                method = workerClass.getDeclaredMethod(methodName);
            } else {
                String className = null;
                Object parameter = null;
                Class<?>[] parameterTypes = new Class<?>[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    parameter = parameters[i];
                    className = ReflectionUtils.getCleanClassName(parameter.getClass().getName());
                    parameterTypes[i] = Class.forName(className);
                }
                method = workerClass.getDeclaredMethod(methodName, parameterTypes);
            }
            method.setAccessible(true);
            method.invoke(worker, parameters);
        } catch (Exception e) {
            if (e.getCause() != null) {
                throw new BusinessException(e.getCause());
            } else {
                throw new BusinessException(e);
            }
        }
    }

    /**
     * Get all script interfaces with compiling those that are not compiled yet
     * 
     * @return the allScriptInterfaces
     */
    public List<Class<ScriptInterface>> getAllScriptInterfacesWCompile() {

        List<Class<ScriptInterface>> scriptInterfaces = new ArrayList<>();

        List<ScriptInstance> scriptInstances = findByType(ScriptSourceTypeEnum.JAVA);
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