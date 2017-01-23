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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.security.Role;

@Singleton
@Startup
public class ScriptInstanceService extends CustomScriptService<ScriptInstance, ScriptInterface> {

    /**
     * Get all ScriptInstances with error for a provider
     *
     * @param provider
     * @return
     */
    public List<CustomScript> getScriptInstancesWithError(Provider provider) {
        return ((List<CustomScript>) getEntityManager().createNamedQuery("CustomScript.getScriptInstanceOnError", CustomScript.class).setParameter("isError", Boolean.TRUE)
                .setParameter("provider", provider).getResultList());
    }

    /**
     * Count scriptInstances with error for a provider
     *
     * @param provider
     * @return
     */
    public long countScriptInstancesWithError(Provider provider) {
        return ((Long) getEntityManager().createNamedQuery("CustomScript.countScriptInstanceOnError", Long.class).setParameter("isError", Boolean.TRUE)
                .setParameter("provider", provider).getSingleResult());
    }

    /**
     * Compile all scriptInstances
     */
    @PostConstruct
    void compileAll() {

        List<ScriptInstance> scriptInstances = findByType(ScriptSourceTypeEnum.JAVA);
        compile(scriptInstances);
    }

    /**
     * Execute the script identified by a script code. No init nor finalize methods are called.
     *
     * @param scriptCode      ScriptInstanceCode
     * @param context         Context parameters (optional)
     * @param currentUser     User executor
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws ElementNotFoundException   Script not found
     * @throws BusinessException          Any execution exception
     */
    @Override
    public Map<String, Object> execute(String scriptCode, Map<String, Object> context, User currentUser) throws ElementNotFoundException, InvalidScriptException,
            InvalidPermissionException, BusinessException {

        ScriptInstance scriptInstance = findByCode(scriptCode, currentUser.getProvider());
        // Check access to the script
        isUserHasExecutionRole(scriptInstance, currentUser);
        return super.execute(scriptCode, context, currentUser);
    }

    /**
     * Wrap the logger and execute script
     *
     * @param scriptCode
     * @param context
     */
    public void test(String scriptCode, Map<String, Object> context, User currentUser) {
        try {
            clearLogs(currentUser.getProvider().getCode(), scriptCode);
            ScriptInstance scriptInstance = findByCode(scriptCode, currentUser.getProvider());
            isUserHasExecutionRole(scriptInstance, currentUser);
            String javaSrc = scriptInstance.getScript();
            javaSrc = javaSrc.replaceAll("LoggerFactory.getLogger", "new org.meveo.service.script.RunTimeLogger(" + getClassName(javaSrc) + ".class,\""
                    + currentUser.getProvider().getCode() + "\",\"" + scriptCode + "\",\"ScriptInstanceService\");//");
            Class<ScriptInterface> compiledScript = compileJavaSource(javaSrc, getFullClassname(scriptInstance.getScript()));
            execute(compiledScript.newInstance(), context, currentUser);

        } catch (Exception e) {
            log.error("Script test failed", e);
        }
    }

    /**
     * Only users having a role in executionRoles can execute the script, not having the role should throw an InvalidPermission exception that extends businessException. A script
     * with no executionRoles can be executed by any user.
     *
     * @param scriptInstance
     * @param user
     * @throws InvalidPermissionException
     */
    public void isUserHasExecutionRole(ScriptInstance scriptInstance, User user) throws InvalidPermissionException {
        if (scriptInstance != null && user != null && scriptInstance.getExecutionRoles() != null && !scriptInstance.getExecutionRoles().isEmpty()) {
            Set<Role> execRoles = scriptInstance.getExecutionRoles();
            execRoles.retainAll(user.getRoles());
            if (execRoles.isEmpty()) {
                throw new InvalidPermissionException();
            }
        }
    }

    public boolean isUserHasSourcingRole(ScriptInstance scriptInstance, User user) {
        if (scriptInstance != null && user != null && scriptInstance.getSourcingRoles() != null && !scriptInstance.getSourcingRoles().isEmpty()) {
            Set<Role> sourcingRoles = scriptInstance.getSourcingRoles();
            sourcingRoles.retainAll(user.getRoles());
            if (sourcingRoles.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This is used to invoke a method in a new transaction from a script.<br>
     * This will prevent DB errors in the script from affecting notification history creation.
     *
     * @param workerName The name of the API or service that will be invoked.
     * @param methodName The name of the method that will be invoked.
     * @param parameters The array of parameters accepted by the method.  They must be specified in exactly the same order as the target method.
     * @throws BusinessException
     */
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
            if(e.getCause() != null){
                throw new BusinessException(e.getCause());
            } else {
                throw new BusinessException(e);
            }
        }
    }
}