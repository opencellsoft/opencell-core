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

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidPermissionException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.BusinessService;

public abstract class CustomScriptService<T extends CustomScript, SI extends ScriptInterface> extends BusinessService<T> {

    @Inject
    private ResourceBundle resourceMessages;
    
    @Inject
    private UserService userService;

    protected final Class<SI> scriptInterfaceClass;

    private Map<String, Map<String, List<String>>> allLogs = new HashMap<String, Map<String, List<String>>>();

    private Map<String, Map<String, Class<SI>>> allScriptInterfaces = new HashMap<String, Map<String, Class<SI>>>();

	private Map<String, Map<String, SI>> allScriptInstances=new HashMap<String,Map<String,SI>>();
	
    private CharSequenceCompiler<SI> compiler;

    private String classpath = "";


    /**
     * Constructor.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CustomScriptService() {
        super();
        Class clazz = getClass();
        while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }
        Object o = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[1];

        if (o instanceof TypeVariable) {
            this.scriptInterfaceClass = (Class<SI>) ((TypeVariable) o).getBounds()[0];
        } else {
            this.scriptInterfaceClass = (Class<SI>) o;
        }
    }

    /**
     * Find scripts by type
     * 
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> findByType(ScriptSourceTypeEnum type) {
        List<T> result = new ArrayList<T>();
        QueryBuilder qb = new QueryBuilder(getEntityClass(), "t");
        qb.addCriterionEnum("t.sourceTypeEnum", type);
        try {
            result = (List<T>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {

        }
        return result;
    }

    @Override
    public void create(T script, User creator) throws BusinessException {

        String className = getClassName(script.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
        }
        String fullClassName = getFullClassname(script.getScript());

        if (isOverwritesJavaClass(fullClassName)) {
            throw new BusinessException(resourceMessages.getString("message.scriptInstance.classInvalid", fullClassName));
        }
        script.setCode(fullClassName);

        super.create(script, creator);
        compileScript(script, false);
    }

    @Override
    public T update(T script, User updater) throws BusinessException {

        String className = getClassName(script.getScript());
        if (className == null) {
            throw new BusinessException(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
        }

        String fullClassName = getFullClassname(script.getScript());
        if (isOverwritesJavaClass(fullClassName)) {
            throw new BusinessException(resourceMessages.getString("message.scriptInstance.classInvalid", fullClassName));
        }

        script.setCode(fullClassName);

        script = super.update(script, updater);

        compileScript(script, false);

        return script;
    }

    /**
     * Check full class name is existed class path or not
     */
    public static boolean isOverwritesJavaClass(String fullClassName) {
        try {
            Class.forName(fullClassName);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    /**
     * Build the classpath and compile all scripts
     */
    protected void compile(List<T> scripts) {
        try {

            VirtualFile virtualLibDir = VFS.getChild("/content/" + ParamBean.getInstance().getProperty("meveo.moduleName", "meveo") + ".war/WEB-INF/lib");
            if (!virtualLibDir.exists()) {
                log.info("cannot find /content in VFS ");
                VirtualFile virtualDeploymentDirs = VFS.getChild("deployment");
                if (!virtualDeploymentDirs.exists() || virtualDeploymentDirs.getChildren().size() == 0) {
                    log.info("cannot find /deployment in VFS");
                } else {
                    // get the last deployment dir
                    VirtualFile virtualDeploymentDir = null;
                    for (VirtualFile virtualDeployment : virtualDeploymentDirs.getChildren()) {
                        if (virtualDeploymentDir == null) {
                            virtualDeploymentDir = virtualDeployment;
                        } else {
                            if (virtualDeployment.getLastModified() > virtualDeploymentDir.getLastModified()) {
                                virtualDeploymentDir = virtualDeployment;
                            }
                        }
                    }
                    File physicalLibDirs = virtualDeploymentDir.getPhysicalFile();
                    for (File physicalLibDir : physicalLibDirs.listFiles()) {
                        if (physicalLibDir.isDirectory()) {
                            for (File f : FileUtils.getFilesToProcess(physicalLibDir, "*", "jar")) {
                                classpath += f.getCanonicalPath() + File.pathSeparator;
                            }
                        }
                    }
                }
            } else {
                File physicalLibDir = virtualLibDir.getPhysicalFile();
                for (File f : FileUtils.getFilesToProcess(physicalLibDir, "*", "jar")) {
                    classpath += f.getCanonicalPath() + File.pathSeparator;
                }
            }
            if (classpath.length() == 0) {
                String jbossHome = System.getProperty("jboss.home.dir");
                File deploymentLibDirs = new File(jbossHome + "/standalone/tmp/vfs/deployment");
                if (!deploymentLibDirs.exists()) {
                    log.error("cannot find " + jbossHome + "/standalone/tmp/vfs/deployment .. are you deploying on jboss 7 ?");
                    return;
                } else {
                    File deploymentDir = null;
                    for (File deployment : deploymentLibDirs.listFiles()) {
                        if (deploymentDir == null) {
                            deploymentDir = deployment;
                        } else {
                            if (deployment.lastModified() > deploymentDir.lastModified()) {
                                deploymentDir = deployment;
                            }
                        }
                    }
                    for (File physicalLibDir : deploymentDir.listFiles()) {
                        if (physicalLibDir.isDirectory()) {
                            for (File f : FileUtils.getFilesToProcess(physicalLibDir, "*", "jar")) {
                                classpath += f.getCanonicalPath() + File.pathSeparator;
                            }
                        }
                    }
                }
            }
            log.info("compileAll classpath={}", classpath);

            for (T script : scripts) {
                compileScript(script, false);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * Compile script and update status
     * 
     * @param script Script entity to compile
     */
    public void compileScript(T script, boolean testCompile) {
        try {
            final String qName = getFullClassname(script.getScript());
            final String codeSource = script.getScript();

            log.debug("Compiling code for {}: {}", qName, codeSource);
            script.setError(false);
            script.getScriptErrors().clear();

            Class<SI> compiledScript = compileJavaSource(codeSource, qName);

            if (!testCompile) {
                if (!allScriptInterfaces.containsKey(script.getProvider().getCode())) {
                    allScriptInterfaces.put(script.getProvider().getCode(), new HashMap<String, Class<SI>>());
                    allScriptInstances.put(script.getCode(),new HashMap<String, SI>());
                          log.debug("create Map for {}", script.getProvider().getCode());
                }
                Map<String, Class<SI>> providerScriptInterfaces = allScriptInterfaces.get(script.getProvider().getCode());
                providerScriptInterfaces.put(script.getCode(), compiledScript);
                Map<String,SI> providerScriptInstances = allScriptInstances.get(script.getProvider().getCode());
                log.debug("get providerScriptInstances {}",providerScriptInstances);
                if(providerScriptInstances==null){
                	providerScriptInstances=new HashMap<String,SI>();
                	allScriptInstances.put(script.getProvider().getCode(), providerScriptInstances);
                }
                providerScriptInstances.put(script.getCode(),compiledScript.newInstance());
                log.debug("Added script {} for provider {} to Map", script.getCode(), script.getProvider().getCode());
            }
        } catch (CharSequenceCompilerException e) {
            log.error("Failed to compile script {} for provider {}. Compilation errors:", script.getCode(), script.getProvider().getCode());
            List<Diagnostic<? extends JavaFileObject>> diagnosticList = e.getDiagnostics().getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
                if ("ERROR".equals(diagnostic.getKind().name())) {
                    ScriptInstanceError scriptInstanceError = new ScriptInstanceError();
                    scriptInstanceError.setMessage(diagnostic.getMessage(Locale.getDefault()));
                    scriptInstanceError.setLineNumber(diagnostic.getLineNumber());
                    scriptInstanceError.setColumnNumber(diagnostic.getColumnNumber());
                    scriptInstanceError.setSourceFile(diagnostic.getSource().toString());
                    // scriptInstanceError.setScript(scriptInstance);
                    script.getScriptErrors().add(scriptInstanceError);
                    // scriptInstanceErrorService.create(scriptInstanceError, scriptInstance.getAuditable().getCreator(), scriptInstance.getProvider());
                    log.warn("{} script {} location {}:{}: {}", diagnostic.getKind().name(), script.getCode(), diagnostic.getLineNumber(), diagnostic.getColumnNumber(),
                        diagnostic.getMessage(Locale.getDefault()));
                }
            }
            script.setError(true);

        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * Compile java Source script
     * 
     * @param javaSrc Java source to compile
     * @param fullClassName Canonical class name
     * @return Compiled class instance
     * @throws CharSequenceCompilerException
     */
    protected Class<SI> compileJavaSource(String javaSrc, String fullClassName) throws CharSequenceCompilerException {
        compiler = new CharSequenceCompiler<SI>(this.getClass().getClassLoader(), Arrays.asList(new String[] { "-cp", classpath }));
        final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
        Class<SI> compiledScript = compiler.compile(fullClassName, javaSrc, errs, new Class<?>[] { scriptInterfaceClass });
        return compiledScript;
    }

    /**
     * Find the script class for a given script code
     * 
     * @param provider Provider
     * @param scriptCode Script code
     * @return Script interface Class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    public Class<SI> getScriptInterface(Provider provider, String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        Class<SI> result = null;

        if (allScriptInterfaces.containsKey(provider.getCode())) {
            result = allScriptInterfaces.get(provider.getCode()).get(scriptCode);
        }
        if (result == null) {
            T script = findByCode(scriptCode, provider);
            if (script == null) {
                log.debug("ScriptInstance with {} does not exist", scriptCode);
                throw new ElementNotFoundException(scriptCode, getEntityClass().getName());
            }
            compileScript(script, false);
            if (script.isError()) {
                log.debug("ScriptInstance {} failed to compile. Errors: {}", scriptCode, script.getScriptErrors());
                throw new InvalidScriptException(scriptCode, getEntityClass().getName());
            }
            result = allScriptInterfaces.get(provider.getCode()).get(scriptCode);
        }

        if (result == null) {
            log.debug("ScriptInstance with {} does not exist", scriptCode);
            throw new ElementNotFoundException(scriptCode, getEntityClass().getName());
        }

        log.debug("getScriptInterface provider:{} scriptCode:{} -> {}", provider.getCode(), scriptCode, result);
        return result;
    }

    /**
     * Get a compiled script class
     * 
     * @param provider Provider
     * @param scriptCode Script code
     * @return A compiled script class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    public SI getScriptInstance(Provider provider, String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        Class<SI> scriptClass = getScriptInterface(provider, scriptCode);

        try {
            SI script = scriptClass.newInstance();
            return script;

        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate script {}", scriptCode, e);
            throw new InvalidScriptException(scriptCode, getEntityClass().getName());
        }
    }

    public SI getCachedScriptInstance(Provider provider, String scriptCode) throws ElementNotFoundException, InvalidScriptException {
    	SI script = null;
        script = allScriptInstances.get(provider.getCode()).get(scriptCode);
        return script;
    }
    
    /**
     * Add a log line for a script
     * 
     * @param message
     * @param providerCode
     * @param scriptCode
     */
    public void addLog(String message, String providerCode, String scriptCode) {
        if (!allLogs.containsKey(providerCode)) {
            allLogs.put(providerCode, new HashMap<String, List<String>>());
        }
        if (!allLogs.get(providerCode).containsKey(scriptCode)) {
            allLogs.get(providerCode).put(scriptCode, new ArrayList<String>());
        }
        allLogs.get(providerCode).get(scriptCode).add(message);
    }

    /**
     * Get logs for script
     * 
     * @param providerCode
     * @param scriptCode
     * @return
     */
    public List<String> getLogs(String providerCode, String scriptCode) {
        if (!allLogs.containsKey(providerCode)) {
            return new ArrayList<String>();
        }
        if (!allLogs.get(providerCode).containsKey(scriptCode)) {
            return new ArrayList<String>();
        }
        return allLogs.get(providerCode).get(scriptCode);
    }

    /**
     * Clear all logs for a script
     * 
     * @param providerCode
     * @param scriptCode
     */
    public void clearLogs(String providerCode, String scriptCode) {
        if (allLogs.containsKey(providerCode)) {
            if (allLogs.get(providerCode).containsKey(scriptCode)) {
                allLogs.get(providerCode).get(scriptCode).clear();
            }
        }
    }

    /**
     * Find the package name in a source java text
     * 
     * @param src Java source code
     * @return Package name
     */
    public static String getPackageName(String src) {
        return StringUtils.patternMacher("package (.*?);", src);
    }

    /**
     * Find the class name in a source java text
     * 
     * @param src Java source code
     * @return Class name
     */
    public static String getClassName(String src) {
        String className = StringUtils.patternMacher("public class (.*) extends", src);
        if (className == null) {
            className = StringUtils.patternMacher("public class (.*) implements", src);
        }
        return className!=null?className.trim():null;
    }

    /**
     * Gets a full classname of a script by combining a package (if applicable) and a classname
     * 
     * @param script Java source code
     * @return Full classname
     */
    public static String getFullClassname(String script) {
        String packageName = getPackageName(script);
        String className = getClassName(script);
        return (packageName != null ? packageName.trim() + "." : "") + className;
    }

    /**
     * Execute action on an entity
     * 
     * @param entity Entity to execute action on
     * @param scriptCode Script to execute, identified by a code
     * @param encodedParameters Additional parameters encoded in URL like style param=value&param=value
     * @param currentUser Current user
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws ElementNotFoundException Script not found
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> execute(IEntity entity, String scriptCode, String encodedParameters, User currentUser) throws InvalidPermissionException, ElementNotFoundException,
            BusinessException {

        return execute(entity, scriptCode, CustomScriptService.parseParameters(encodedParameters), currentUser);
    }

    /**
     * Execute action on an entity
     * 
     * @param entity Entity to execute action on
     * @param scriptCode Script to execute, identified by a code
     * @param context Additional parameters
     * @param currentUser Current user
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> execute(IEntity entity, String scriptCode, Map<String, Object> context, User currentUser) throws InvalidScriptException, ElementNotFoundException,
            InvalidPermissionException, BusinessException {

        if (context == null) {
            context = new HashMap<String, Object>();
        }
        context.put(Script.CONTEXT_ENTITY, entity);
        currentUser = userService.attach(currentUser);
        Map<String, Object> result = execute(scriptCode, context, currentUser);
        return result;
    }

    /**
     * Execute action on an entity
     * 
     * @param entity Entity to execute action on
     * @param scriptCode Script to execute, identified by a code
     * @param context Method context
     * @param currentUser Current user
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     * @throws InvalidPermissionException Insufficient access to run the script
     * @throws BusinessException Any execution exception
     */
    public Map<String, Object> execute(String scriptCode, Map<String, Object> context, User currentUser) throws ElementNotFoundException, InvalidScriptException,
            InvalidPermissionException, BusinessException {

        log.trace("Script {} to be executed with parameters {}", scriptCode, context);

        SI classInstance = getScriptInstance(currentUser.getProvider(), scriptCode);
        classInstance.execute(context, currentUser);

        log.trace("Script {} executed with parameters {}", scriptCode, context);
        return context;
    }

    /**
     * Execute a class that extends Script
     * 
     * @param compiledScript Compiled script class
     * @param context Method context
     * @param currentUser Current user
     * @return Context parameters. Will not be null even if "context" parameter is null.
     * @throws BusinessException Any execution exception
     */
    protected Map<String, Object> execute(SI compiledScript, Map<String, Object> context, User currentUser) throws BusinessException {
        if (context == null) {
            context = new HashMap<String, Object>();
        }

        compiledScript.execute(context, currentUser);
        return context;
    }

    /**
     * Parse parameters encoded in URL like style param=value&param=value
     * 
     * @param encodedParameters Parameters encoded in URL like style param=value&param=value
     * @return A map of parameter keys and values
     */
    public static Map<String, Object> parseParameters(String encodedParameters) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        if (!StringUtils.isBlank(encodedParameters)) {
            StringTokenizer tokenizer = new StringTokenizer(encodedParameters, "&");
            while (tokenizer.hasMoreElements()) {
                String paramValue = tokenizer.nextToken();
                String[] paramValueSplit = paramValue.split("=");
                if (paramValueSplit.length == 2) {
                    parameters.put(paramValueSplit[0], paramValueSplit[1]);
                } else {
                    parameters.put(paramValueSplit[0], null);
                }
            }

        }
        return parameters;
    }

    /**
     * Get all script interfaces for a given provider
     * 
     * @return the allScriptInterfaces
     */
    public Map<String, Class<SI>> getAllScriptInterfaces(Provider provider) {
        return allScriptInterfaces.get(provider.getCode());
    } 
}