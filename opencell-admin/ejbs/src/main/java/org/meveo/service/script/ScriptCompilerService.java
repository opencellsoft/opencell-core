/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.NoResultException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.cache.CacheKeyStr;
import org.meveo.cache.CompiledScript;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.base.BusinessService;

/**
 * Compiles scripts and provides compiled script classes.
 * 
 * NOTE: Compilation methods are executed synchronously due to WRITE lock. DO NOT CHANGE IT, so there would be only one attempt to compile a new script class
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 7.2.0
 *
 */
@Singleton
@Lock(LockType.WRITE)
public class ScriptCompilerService extends BusinessService<ScriptInstance> {

    /**
     * Stores compiled scripts. Key format: &lt;cluster node code&gt;_&lt;scriptInstance code&gt;. Value is a compiled script class and class instance
     */
    @Resource(lookup = "java:jboss/infinispan/cache/opencell/opencell-script-cache")
    private Cache<CacheKeyStr, CompiledScript> compiledScripts;

    private CharSequenceCompiler<ScriptInterface> compiler;

    private String classpath = "";

    /**
     * Compile and initialize all scriptInstances.
     */
    public void compileAndInitializeAll() {

        List<ScriptInstance> scriptInstances = findByType(ScriptSourceTypeEnum.JAVA_CLASS);
        for (ScriptInstance scriptInstance : scriptInstances) {
            if (!scriptInstance.isReuse()) {
                continue;
            }
            try {
                // Obtain a deployed script
                ScriptInterface script = (ScriptInterface) EjbUtils
                    .getServiceInterface(scriptInstance.getCode().lastIndexOf('.') > 0 ? scriptInstance.getCode().substring(scriptInstance.getCode().lastIndexOf('.') + 1) : scriptInstance.getCode());
                if (script == null) {
                    log.error("Script " + scriptInstance.getCode() + " was not found as a deployed script");
                } else {
                    log.info("Initializing script " + scriptInstance.getCode());
                    script.init(new HashMap<String, Object>());
                }
            } catch (Exception e) {
                log.error("Failed to initialize a script " + scriptInstance.getCode(), e);
            }
        }

        scriptInstances = findByType(ScriptSourceTypeEnum.JAVA);
        compile(scriptInstances);

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
            if (!scriptInstance.isError()) {
                try {
                    scriptInterfaces.add(getScriptInterfaceWCompile(scriptInstance.getCode()));
                } catch (ElementNotFoundException | InvalidScriptException e) {
                    // Ignore errors here as they were logged in a call before
                }
            }
        }

        return scriptInterfaces;
    }

    /**
     * Find scripts by source type.
     * 
     * @param type script source type
     * @return list of scripts
     */
    @SuppressWarnings("unchecked")
    public List<ScriptInstance> findByType(ScriptSourceTypeEnum type) {
        List<ScriptInstance> result = new ArrayList<ScriptInstance>();
        try {
            result = (List<ScriptInstance>) getEntityManager().createNamedQuery("CustomScript.getScriptInstanceByTypeActive").setParameter("sourceTypeEnum", type).getResultList();
        } catch (NoResultException e) {

        }
        return result;
    }

    /**
     * Construct classpath for script compilation
     * 
     * @throws IOException
     */
    private void constructClassPath() throws IOException {

        if (classpath.length() == 0) {

            // Check if deploying an exploded archive or a compressed file
            String thisClassfile = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();

            File realFile = new File(thisClassfile);

            // Was deployed as exploded archive
            if (realFile.exists()) {
                File deploymentDir = realFile.getParentFile();
                for (File file : deploymentDir.listFiles()) {
                    if (file.getName().endsWith(".jar")) {
                        classpath += file.getCanonicalPath() + File.pathSeparator;
                    }
                }

                // War was deployed as compressed archive
            } else {

                org.jboss.vfs.VirtualFile vFile = org.jboss.vfs.VFS.getChild(thisClassfile);
                realFile = new File(org.jboss.vfs.VFSUtils.getPhysicalURI(vFile).getPath());

                File deploymentDir = realFile.getParentFile().getParentFile();

                for (File physicalLibDir : deploymentDir.listFiles()) {
                    if (physicalLibDir.isDirectory()) {
                        for (File f : FileUtils.listFiles(physicalLibDir, "jar", "*")) {
                            classpath += f.getCanonicalPath() + File.pathSeparator;
                        }
                    }
                }
            }
        }
        log.info("compileAll classpath={}", classpath);

    }

    /**
     * Build the classpath and compile all scripts.
     * 
     * @param scripts list of scripts
     */
    protected void compile(List<ScriptInstance> scripts) {
        try {

            constructClassPath();

            for (ScriptInstance script : scripts) {
                compileScript(script, false);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /*
     * Compile a script
     */
    public void refreshCompiledScript(String scriptCode) {

        ScriptInstance script = findByCode(scriptCode);
        if (script == null) {
            clearCompiledScripts(scriptCode);
        } else {
            compileScript(script, false);
        }
        // detach(script);
    }

    /**
     * Compile script, a and update script entity status with compilation errors. Successfully compiled script is added to a compiled script cache if active and not in test
     * compilation mode. Script.init() method will be called during script instantiation (for cache) if script is marked as reusable.
     * 
     * @param script Script entity to compile
     * @param testCompile Is it a compilation for testing purpose. Won't clear nor overwrite existing compiled script cache.
     */
    public void compileScript(ScriptInstance script, boolean testCompile) {

        List<ScriptInstanceError> scriptErrors = compileScript(script.getCode(), script.getSourceTypeEnum(), script.getScript(), script.isActive(), script.isReuse(), testCompile);

        script.setError(scriptErrors != null && !scriptErrors.isEmpty());
        script.setScriptErrors(scriptErrors);
    }

    /**
     * Compile script. DOES NOT update script entity status. Successfully compiled script will be instantiated and added to a compiled script cache. Optionally Script.init() method
     * is called during script instantiation if requested so.
     * 
     * Script is not cached if disabled or in test compilation mode.
     * 
     * @param scriptCode Script entity code
     * @param sourceType Source code language type
     * @param sourceCode Source code
     * @param isActive Is script active. It will compile it anyway. Will clear but not overwrite existing compiled script cache.
     * @param initialize Should script be initialized when instantiating
     * @param testCompile Is it a compilation for testing purpose. Won't clear nor overwrite existing compiled script cache.
     * 
     * @return A list of compilation errors if not compiled
     */
    private List<ScriptInstanceError> compileScript(String scriptCode, ScriptSourceTypeEnum sourceType, String sourceCode, boolean isActive, boolean initialize, boolean testCompile) {

        log.debug("Compile script {}", scriptCode);

        try {
            if (!testCompile) {
                clearCompiledScripts(scriptCode);
            }

            // For now no need to check source type if (sourceType==ScriptSourceTypeEnum.JAVA){

            Class<ScriptInterface> compiledScript = compileJavaSource(sourceCode);

            if (!testCompile && isActive) {

                CacheKeyStr cacheKey = new CacheKeyStr(currentUser.getProviderCode(), EjbUtils.getCurrentClusterNode() + "_" + scriptCode);

                ScriptInterface scriptInstance = compiledScript.newInstance();
                if (initialize) {
                    log.debug("Will initialize script {}", scriptCode);
                    try {
                        scriptInstance.init(null);
                    } catch (Exception e) {
                        log.warn("Failed to initialize script for a cached script instance", e);
                    }
                }
                compiledScripts.put(cacheKey, new CompiledScript(compiledScript, scriptInstance));

                log.debug("Compiled script {} added to compiled interface map", scriptCode);
            }

            return null;

        } catch (CharSequenceCompilerException e) {
            log.error("Failed to compile script {}. Compilation errors:", scriptCode);

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
                    log.warn("{} script {} location {}:{}: {}", diagnostic.getKind().name(), scriptCode, diagnostic.getLineNumber(), diagnostic.getColumnNumber(), diagnostic.getMessage(Locale.getDefault()));
                }
            }
            return scriptErrors;

        } catch (Exception e) {
            log.error("Failed while compiling script", e);
            List<ScriptInstanceError> scriptErrors = new ArrayList<>();
            ScriptInstanceError scriptInstanceError = new ScriptInstanceError();
            scriptInstanceError.setMessage(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            scriptErrors.add(scriptInstanceError);

            return scriptErrors;
        }
    }

    /**
     * Compile java Source script
     * 
     * @param javaSrc Java source to compile
     * @return Compiled class instance
     * @throws CharSequenceCompilerException char sequence compiler exception.
     */
    public Class<ScriptInterface> compileJavaSource(String javaSrc) throws CharSequenceCompilerException {

        supplementClassPathWithMissingImports(javaSrc);

        String fullClassName = ScriptUtils.getFullClassname(javaSrc);

        log.trace("Compile JAVA script {} with classpath {}", fullClassName, classpath);

        compiler = new CharSequenceCompiler<ScriptInterface>(this.getClass().getClassLoader(), Arrays.asList(new String[] { "-cp", classpath }));
        final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
        Class<ScriptInterface> compiledScript = compiler.compile(fullClassName, javaSrc, errs, new Class<?>[] { ScriptInterface.class });
        return compiledScript;
    }

    /**
     * Supplement classpath with classes needed for the particular script compilation. Solves issue when classes server as jboss modules are referenced in script. E.g.
     * prg.slf4j.Logger
     * 
     * @param javaSrc Java source to compile
     */
    @SuppressWarnings("rawtypes")
    private void supplementClassPathWithMissingImports(String javaSrc) {

        String regex = "import (.*?);";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(javaSrc);
        while (matcher.find()) {
            String className = matcher.group(1);
            if(className.startsWith("static ")) {
            	className=className.substring(7, className.lastIndexOf("."));
            }
            try {
                if (!className.startsWith("java.") && !className.startsWith("org.meveo")) {
                    Class clazz = Class.forName(className);
                    try {
                        String location = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
                        if (location.startsWith("file:")) {
                            location = location.substring(5);
                        }
                        if (location.endsWith("!/")) {
                            location = location.substring(0, location.length() - 2);
                        }

                        if (!classpath.contains(location)) {
                            classpath += File.pathSeparator + location;
                        }

                    } catch (Exception e) {
                        log.warn("Failed to find location for class {}", className);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to find location for class {}", className);
            }
        }

    }

    /**
     * Compile the script class for a given script code if it is not compile yet.
     * 
     * @param scriptCode Script code
     * @return Script interface Class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    public Class<ScriptInterface> getScriptInterfaceWCompile(String scriptCode) throws ElementNotFoundException, InvalidScriptException {

        CompiledScript compiledScript = getOrCompileScript(scriptCode);

        return compiledScript.getScriptClass();
    }

    /**
     * Compile the script class for a given script code if it is not compile yet and return its instance. NOTE: Will return the SAME (cached) script class instance for subsequent
     * calls. If you need a new instance of a class, use getScriptInterfaceWCompile() and instantiate class yourself.
     * 
     * @param scriptCode Script code
     * @return Script instance
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    public ScriptInterface getScriptInstanceWCompile(String scriptCode) throws ElementNotFoundException, InvalidScriptException {

        CompiledScript compiledScript = getOrCompileScript(scriptCode);

        return compiledScript.getScriptInstance();
    }

    /**
     * Compile the script class for a given script code if it is not compile yet.
     * 
     * @param scriptCode Script code
     * @return Script instance
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    private CompiledScript getOrCompileScript(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        CacheKeyStr cacheKey = new CacheKeyStr(currentUser.getProviderCode(), EjbUtils.getCurrentClusterNode() + "_" + scriptCode);

        CompiledScript compiledScript = compiledScripts.get(cacheKey);
        if (compiledScript == null) {

            ScriptInstance script = findByCode(scriptCode);
            if (script == null) {
                log.debug("ScriptInstance with {} does not exist", scriptCode);
                throw new ElementNotFoundException(scriptCode, "ScriptInstance");
            } else if (script.isError()) {
                log.debug("ScriptInstance {} failed to compile. Errors: {}", scriptCode, script.getScriptErrors());
                throw new InvalidScriptException(scriptCode, getEntityClass().getName());
            }
            compileScript(script, false);

            compiledScript = compiledScripts.get(cacheKey);
        }

        if (compiledScript == null) {
            log.debug("ScriptInstance with {} does not exist", scriptCode);
            throw new ElementNotFoundException(scriptCode, "ScriptInstance");
        }

        return compiledScript;
    }

    /**
     * Remove compiled script, its logs and cached instances for given script code
     * 
     * @param scriptCode Script code
     */
    public void clearCompiledScripts(String scriptCode) {
        compiledScripts.remove(new CacheKeyStr(currentUser.getProviderCode(), EjbUtils.getCurrentClusterNode() + "_" + scriptCode));
    }

    /**
     * Remove all compiled scripts for a current provider
     */
    public void clearCompiledScripts() {

        String currentProvider = currentUser.getProviderCode();
        log.info("Clear CFTS cache for {}/{} ", currentProvider, currentUser);
        // cftsByAppliesTo.keySet().removeIf(key -> (key.getProvider() == null) ? currentProvider == null : key.getProvider().equals(currentProvider));
        Iterator<Entry<CacheKeyStr, CompiledScript>> iter = compiledScripts.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).entrySet().iterator();
        ArrayList<CacheKeyStr> itemsToBeRemoved = new ArrayList<>();
        while (iter.hasNext()) {
            Entry<CacheKeyStr, CompiledScript> entry = iter.next();
            boolean comparison = (entry.getKey().getProvider() == null) ? currentProvider == null : entry.getKey().getProvider().equals(currentProvider);
            if (comparison) {
                itemsToBeRemoved.add(entry.getKey());
            }
        }

        for (CacheKeyStr elem : itemsToBeRemoved) {
            log.debug("Remove element Provider:" + elem.getProvider() + " Key:" + elem.getKey() + ".");
            compiledScripts.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).remove(elem);
        }
    }
}