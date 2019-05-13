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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.NoResultException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.cache.CacheKeyStr;
import org.meveo.commons.utils.FileUtils;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.base.BusinessService;

/**
 * Compiles scripts and provides compiled script classes
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 7.2.0
 *
 */
@Singleton
@Lock(LockType.READ)
public class ScriptCompilerService extends BusinessService<ScriptInstance> {

    private Map<CacheKeyStr, Class<ScriptInterface>> allScriptInterfaces = new HashMap<>();

    private Map<CacheKeyStr, ScriptInterface> cachedScriptInstances = new HashMap<>();

    private CharSequenceCompiler<ScriptInterface> compiler;

    private String classpath = "";

    /**
     * Compile all scriptInstances.
     */
    public void compileAll() {

        List<ScriptInstance> scriptInstances = findByType(ScriptSourceTypeEnum.JAVA);
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
            try {
                scriptInterfaces.add(getScriptInterfaceWCompile(scriptInstance.getCode()));
            } catch (ElementNotFoundException | InvalidScriptException e) {
                // Ignore errors here as they were logged in a call before
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
     * compilation mode.
     * 
     * @param script Script entity to compile
     * @param testCompile Is it a compilation for testing purpose. Won't clear nor overwrite existing compiled script cache.
     */
    public void compileScript(ScriptInstance script, boolean testCompile) {

        List<ScriptInstanceError> scriptErrors = compileScript(script.getCode(), script.getSourceTypeEnum(), script.getScript(), script.isActive(), testCompile);

        script.setError(scriptErrors != null && !scriptErrors.isEmpty());
        script.setScriptErrors(scriptErrors);
    }

    /**
     * Compile script. DOES NOT update script entity status. Successfully compiled script is added to a compiled script cache if active and not in test compilation mode.
     * 
     * @param scriptCode Script entity code
     * @param sourceType Source code language type
     * @param sourceCode Source code
     * @param isActive Is script active. It will compile it anyway. Will clear but not overwrite existing compiled script cache.
     * @param testCompile Is it a compilation for testing purpose. Won't clear nor overwrite existing compiled script cache.
     * 
     * @return A list of compilation errors if not compiled
     */
    @Lock(LockType.WRITE)
    private List<ScriptInstanceError> compileScript(String scriptCode, ScriptSourceTypeEnum sourceType, String sourceCode, boolean isActive, boolean testCompile) {

        log.debug("Compile script {}", scriptCode);

        try {
            if (!testCompile) {
                clearCompiledScripts(scriptCode);
            }

            // For now no need to check source type if (sourceType==ScriptSourceTypeEnum.JAVA){

            Class<ScriptInterface> compiledScript = compileJavaSource(sourceCode);

            if (!testCompile && isActive) {

                allScriptInterfaces.put(new CacheKeyStr(currentUser.getProviderCode(), scriptCode), compiledScript);

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
                    log.warn("{} script {} location {}:{}: {}", diagnostic.getKind().name(), scriptCode, diagnostic.getLineNumber(), diagnostic.getColumnNumber(),
                        diagnostic.getMessage(Locale.getDefault()));
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
            try {
                if ((!className.startsWith("java") || className.startsWith("javax.persistence")) && !className.startsWith("org.meveo")) {
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
     * Find the script class for a given script code
     * 
     * @param scriptCode Script code
     * @return Script interface Class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    @Lock(LockType.READ)
    public Class<ScriptInterface> getScriptInterface(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        Class<ScriptInterface> result = null;

        result = allScriptInterfaces.get(new CacheKeyStr(currentUser.getProviderCode(), scriptCode));

        if (result == null) {
            result = getScriptInterfaceWCompile(scriptCode);
        }

        log.debug("getScriptInterface scriptCode:{} -> {}", scriptCode, result);
        return result;
    }

    /**
     * Compile the script class for a given script code if it is not compile yet. NOTE: method is executed synchronously due to WRITE lock. DO NOT CHANGE IT, so there would be only
     * one attempt to compile a new script class
     * 
     * @param scriptCode Script code
     * @return Script interface Class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    protected Class<ScriptInterface> getScriptInterfaceWCompile(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        Class<ScriptInterface> result = null;

        result = allScriptInterfaces.get(new CacheKeyStr(currentUser.getProviderCode(), scriptCode));

        if (result == null) {
            ScriptInstance script = findByCode(scriptCode);
            if (script == null) {
                log.debug("ScriptInstance with {} does not exist", scriptCode);
                throw new ElementNotFoundException(scriptCode, getEntityClass().getName());
            }
            compileScript(script, false);
            if (script.isError()) {
                log.debug("ScriptInstance {} failed to compile. Errors: {}", scriptCode, script.getScriptErrors());
                throw new InvalidScriptException(scriptCode, getEntityClass().getName());
            }
            result = allScriptInterfaces.get(new CacheKeyStr(currentUser.getProviderCode(), scriptCode));
        }

        if (result == null) {
            log.debug("ScriptInstance with {} does not exist", scriptCode);
            throw new ElementNotFoundException(scriptCode, getEntityClass().getName());
        }

        return result;
    }

    /**
     * Get a compiled script class
     * 
     * @param scriptCode Script code
     * @return A compiled script class
     * @throws InvalidScriptException Were not able to instantiate or compile a script
     * @throws ElementNotFoundException Script not found
     */
    @Lock(LockType.READ)
    public ScriptInterface getScriptInstance(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        Class<ScriptInterface> scriptClass = getScriptInterface(scriptCode);

        try {
            ScriptInterface script = scriptClass.newInstance();
            return script;

        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate script {}", scriptCode, e);
            throw new InvalidScriptException(scriptCode, getEntityClass().getName());
        }
    }

    /**
     * Get a the same/single/cached instance of compiled script class. A subsequent call to this method will retun the same instance of scipt.
     * 
     * @param scriptCode Script code
     * @return A compiled script class
     * @throws ElementNotFoundException ElementNotFoundException
     * @throws InvalidScriptException InvalidScriptException
     */
    public ScriptInterface getCachedScriptInstance(String scriptCode) throws ElementNotFoundException, InvalidScriptException {
        ScriptInterface script = cachedScriptInstances.get(new CacheKeyStr(currentUser.getProviderCode(), scriptCode));
        if (script == null) {
            script = getScriptInstance(scriptCode);

            cachedScriptInstances.put(new CacheKeyStr(currentUser.getProviderCode(), scriptCode), script);
        }
        return script;
    }

    /**
     * Remove compiled script, its logs and cached instances for given script code
     * 
     * @param scriptCode Script code
     */
    public void clearCompiledScripts(String scriptCode) {
        cachedScriptInstances.remove(new CacheKeyStr(currentUser.getProviderCode(), scriptCode));
        allScriptInterfaces.remove(new CacheKeyStr(currentUser.getProviderCode(), scriptCode));
    }
}