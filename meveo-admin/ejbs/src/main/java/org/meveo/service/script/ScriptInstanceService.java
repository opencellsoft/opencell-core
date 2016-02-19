/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.script;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.security.Role;

@Singleton
@Startup
public class ScriptInstanceService extends CustomScriptService<ScriptInstance, ScriptInterface> {

    @Inject
    private ResourceBundle resourceMessages;

    @Override
    public void create(ScriptInstance scriptInstance, User creator, Provider provider) {
        String packageName = getPackageName(scriptInstance.getScript());
        String className = getClassName(scriptInstance.getScript());
        if (packageName == null || className == null) {
            throw new RuntimeException(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
        }
        scriptInstance.setCode(packageName + "." + className);

        super.create(scriptInstance, creator, provider);

    }

    @Override
    public ScriptInstance update(ScriptInstance scriptInstance, User updater) {

        String packageName = getPackageName(scriptInstance.getScript());
        String className = getClassName(scriptInstance.getScript());
        if (packageName == null || className == null) {
            throw new RuntimeException(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
        }
        scriptInstance.setCode(packageName + "." + className);

        scriptInstance = super.update(scriptInstance, updater);

        return scriptInstance;
    }

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
     * @param scriptCode ScriptInstanceCode
     * @param context Context parameters (optional)
     * @param currentUser User executor
     * @param currentProvider Provider
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws ElementNotFoundException Script not found
     * @throws BusinessException Any execution exception
     */
    @Override
    public Map<String, Object> execute(String scriptCode, Map<String, Object> context, User currentUser, Provider currentProvider) throws ElementNotFoundException,
            InvalidScriptException, InvalidPermissionException, BusinessException {
        
        ScriptInstance scriptInstance = findByCode(scriptCode, currentProvider);
        // Check access to the script
        isUserHasExecutionRole(scriptInstance, getCurrentUser());
        return super.execute(scriptCode, context, currentUser, currentProvider);
    }

    /**
     * Wrap the logger and execute script
     * 
     * @param provider
     * @param scriptCode
     * @param context
     */
    public void test(String scriptCode, Map<String, Object> context, User currentUser, Provider currentProvider) {
        try {
            clearLogs(currentProvider.getCode(), scriptCode);
            ScriptInstance scriptInstance = findByCode(scriptCode, currentProvider);
            isUserHasExecutionRole(scriptInstance, currentUser);
            String javaSrc = scriptInstance.getScript();
            javaSrc = javaSrc.replaceAll("LoggerFactory.getLogger", "new org.meveo.service.script.RunTimeLogger(" + getClassName(javaSrc) + ".class,\"" + currentProvider.getCode()
                    + "\",\"" + scriptCode + "\",\"ScriptInstanceService\");//");
            Class<ScriptInterface> compiledScript = compileJavaSource(javaSrc, getPackageName(scriptInstance.getScript()) + "." + getClassName(scriptInstance.getScript()));
            execute(compiledScript.newInstance(), context, currentUser, currentProvider);

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
            List<Role> execRoles = scriptInstance.getExecutionRoles();
            execRoles.retainAll(user.getRoles());
            if (execRoles.isEmpty()) {
                throw new InvalidPermissionException();
            }
        }
    }

    public boolean isUserHasSourcingRole(ScriptInstance scriptInstance, User user) {
        if (scriptInstance != null && user != null && scriptInstance.getSourcingRoles() != null && !scriptInstance.getSourcingRoles().isEmpty()) {
            List<Role> sourcingRoles = scriptInstance.getSourcingRoles();
            sourcingRoles.retainAll(user.getRoles());
            if (sourcingRoles.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}