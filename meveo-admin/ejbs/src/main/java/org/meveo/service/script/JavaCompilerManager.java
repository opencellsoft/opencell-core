// Copyright (c) 2007 by David J. Biesack, All Rights Reserved.
// Author: David J. Biesack David.Biesack@sas.com
// Created on Nov 4, 2007

package org.meveo.service.script;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptTypeEnum;
import org.slf4j.Logger;

/**
 * JavaCompilerManager executed on startup, find all java scriptsInstance from
 * DB, and then compile and store classes in a map
 * 
 */

@Startup
@Singleton
public class JavaCompilerManager {

	private Map<String, Map<String, Class<ScriptInterface>>> allScriptInterfaces = new HashMap<String, Map<String, Class<ScriptInterface>>>();

	@Inject
	protected Logger log;

	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Inject
	private ScriptInstanceErrorService scriptInstanceErrorService;

	private CharSequenceCompiler<ScriptInterface> compiler;
	
	private String classpath = "";

	@PostConstruct
	void compileAll() {
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
			List<ScriptInstance> scriptInstances = scriptInstanceService.findByType(ScriptTypeEnum.JAVA);
			for (ScriptInstance scriptInstance : scriptInstances) {
				compileScript(scriptInstance);
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public void compileScript(String scriptInstanceCode, Provider provider) {
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, provider);
		if (scriptInstance == null) {
			log.error("compileScript cannot find scriptInstance by code:" + scriptInstanceCode);
		} else {
			compileScript(scriptInstance);
		}
	}

	public void compileScript(ScriptInstance scriptInstance) {
		try {
			compiler = new CharSequenceCompiler<ScriptInterface>(ScriptInterface.class.getClassLoader(), Arrays.asList(new String[] { "-cp", classpath }));
			final String packageName = getPackageName(scriptInstance.getScript());
			final String qName = packageName + '.' + getClassName(scriptInstance.getScript());
			final String codeSource = scriptInstance.getScript();
			log.debug("codeSource to compile:" + codeSource);
			scriptInstance.setError(false);
			scriptInstance.setCode(qName);
			scriptInstanceService.removeErrors(scriptInstance);
			scriptInstance.getScriptInstanceErrors().clear();
			scriptInstanceService.update(scriptInstance);
			final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
			Class<ScriptInterface> compiledScript = compiler.compile(qName, codeSource, errs, new Class<?>[] { ScriptInterface.class });
			log.debug("set script provider:{} scriptCode:{}", scriptInstance.getProvider().getCode(), scriptInstance.getCode());
			if (!allScriptInterfaces.containsKey(scriptInstance.getProvider().getCode())) {
				allScriptInterfaces.put(scriptInstance.getProvider().getCode(), new HashMap<String, Class<ScriptInterface>>());
				log.debug("create Map for {}", scriptInstance.getProvider().getCode());
			}
			Map<String, Class<ScriptInterface>> providerScriptInterfaces = allScriptInterfaces.get(scriptInstance.getProvider().getCode());
			providerScriptInterfaces.put(scriptInstance.getCode(), compiledScript);
			log.debug("add script to Map -> new size {}", providerScriptInterfaces.size());
			
		} catch (CharSequenceCompilerException e) {
			log.error("Compilation error...");
			List<Diagnostic<? extends JavaFileObject>> diagnosticList = e.getDiagnostics().getDiagnostics();
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
				if ("ERROR".equals(diagnostic.getKind().name())) {
					ScriptInstanceError scriptInstanceError = new ScriptInstanceError();
					scriptInstanceError.setMessage(diagnostic.getMessage(Locale.getDefault()));
					scriptInstanceError.setLineNumber(diagnostic.getLineNumber());
					scriptInstanceError.setColumnNumber(diagnostic.getColumnNumber());
					scriptInstanceError.setSourceFile(diagnostic.getSource().toString());
					scriptInstanceError.setScriptInstance(scriptInstance);
					scriptInstance.getScriptInstanceErrors().add(scriptInstanceError);
					scriptInstanceErrorService.create(scriptInstanceError, scriptInstance.getAuditable().getCreator(), scriptInstance.getProvider());
					log.warn(diagnostic.getKind().name());
					log.warn(diagnostic.getMessage(Locale.getDefault()));
					log.warn("line:" + diagnostic.getLineNumber());
					log.warn("column" + diagnostic.getColumnNumber());
				}
			}
			scriptInstance.setError(true);
			scriptInstanceService.update(scriptInstance);

		} catch (Exception e) {
			log.error("", e);
		}
	}

	public Class<ScriptInterface> getScriptInterface(Provider provider, String scriptCode) {
		Class<ScriptInterface> result = null;
		if (allScriptInterfaces.containsKey(provider.getCode())) {
			result = allScriptInterfaces.get(provider.getCode()).get(scriptCode);
		}
		if (result == null) {
			ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptCode, provider);
			if (scriptInstance != null) {
				compileScript(scriptInstance);
				if (allScriptInterfaces.containsKey(provider.getCode())) {
					result = allScriptInterfaces.get(provider.getCode()).get(scriptCode);
				}
			}
		}
		log.debug("getScriptInterface provider:{} scriptCode:{} -> {}", provider.getCode(), scriptCode, result);
		return result;
	}
	
	private String getPackageName(String src){
		return StringUtils.patternMacher("package (.*?);", src);
	}
		
	private String getClassName(String src){
		return StringUtils.patternMacher("public class (.*) extends", src);
	}

}