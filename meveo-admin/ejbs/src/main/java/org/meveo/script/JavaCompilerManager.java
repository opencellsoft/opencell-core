// Copyright (c) 2007 by David J. Biesack, All Rights Reserved.
// Author: David J. Biesack David.Biesack@sas.com
// Created on Nov 4, 2007

package org.meveo.script;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.ScriptInstance;
import org.meveo.model.jobs.ScriptTypeEnum;
import org.meveo.service.job.ScriptInstanceService;
import org.slf4j.Logger;


/**
 *  JavaCompilerManager executed on startup, find all java scriptsInstance from DB,
 *   and then compile and store classes in a map 
 * 
 */

@Startup
@Singleton
public class JavaCompilerManager  {

	private Map<String, ScriptInterface> allScriptInterfaces = new HashMap<String, ScriptInterface>();

	@Inject
	protected Logger log;

	@Inject 
	private ScriptInstanceService scriptInstanceService;

	private	 CharSequenceCompiler<ScriptInterface> compiler;

	@PostConstruct
	void compileAll() {
		try {	
				
			String classpath="";
			VirtualFile virtualLibDir = VFS.getChild("/content/"+ParamBean.getInstance().getProperty("meveo.moduleName", "meveo")+".war/WEB-INF/lib");  			
			if(!virtualLibDir.exists()){
				log.info("cannot find /content in VFS ");
				VirtualFile virtualDeploymentDirs = VFS.getChild("deployment");
				if(!virtualDeploymentDirs.exists() || virtualDeploymentDirs.getChildren().size()==0 ){
					log.info("cannot find /deployment in VFS");
				} else {
					//get the last deployment dir
					VirtualFile virtualDeploymentDir=null;
					for(VirtualFile virtualDeployment:virtualDeploymentDirs.getChildren()){
						if(virtualDeploymentDir==null){
							virtualDeploymentDir = virtualDeployment;
						} else {
							if(virtualDeployment.getLastModified()>virtualDeploymentDir.getLastModified()){
								virtualDeploymentDir = virtualDeployment;
							}
						}
					}
					File physicalLibDirs = virtualDeploymentDir.getPhysicalFile();
					for(File physicalLibDir : physicalLibDirs.listFiles()){
						if(physicalLibDir.isDirectory()){
							for(File f : FileUtils.getFilesToProcess(physicalLibDir, "*", "jar")){
								classpath+=f.getCanonicalPath()+File.pathSeparator;
							}
						}
					}
				}
			} else {
				File physicalLibDir = virtualLibDir.getPhysicalFile();
				for(File f : FileUtils.getFilesToProcess(physicalLibDir, "*", "jar")){
					classpath+=f.getCanonicalPath()+File.pathSeparator;
				}
			}
			if(classpath.length()==0){
				String jbossHome = System.getProperty("jboss.home.dir");
				File deploymentLibDirs = new File(jbossHome+"/standalone/tmp/vfs/deployment");
				if(!deploymentLibDirs.exists()){
					log.error("cannot find "+jbossHome+"/standalone/tmp/vfs/deployment .. are you deploying on jboss 7 ?");
					return;
				} else {
					File deploymentDir=null;
					for(File deployment:deploymentLibDirs.listFiles()){
						if(deploymentDir==null){
							deploymentDir = deployment;
						} else {
							if(deployment.lastModified()>deploymentDir.lastModified()){
								deploymentDir = deployment;
							}
						}
					}
					for(File physicalLibDir : deploymentDir.listFiles()){
						if(physicalLibDir.isDirectory()){
							for(File f : FileUtils.getFilesToProcess(physicalLibDir, "*", "jar")){
								classpath+=f.getCanonicalPath()+File.pathSeparator;
							}
						}
					}
				} 
			}
			log.info("compileAll classpath={}",classpath);
			compiler = new CharSequenceCompiler<ScriptInterface>(ScriptInterface.class.getClassLoader(), Arrays.asList(new String[] { "-cp",classpath}));	
			List<ScriptInstance> scriptInstances = scriptInstanceService.findByType(ScriptTypeEnum.JAVA);
			for(ScriptInstance scriptInstance : scriptInstances){
				compileScript(scriptInstance);
			}
		} catch (Exception e) {
			log.error("",e);
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

	public void compileScript(ScriptInstance scriptInstance){
		try {
			final String packageName = ParamBean.getInstance().getProperty("meveo.scripting.java.packageName", "org.meveo.script") ;
			final String className = scriptInstance.getCode() ;
			final String qName = packageName + '.' + className;
			final String codeSource =scriptInstance.getScript();
			log.debug("codeSource to compile:"+codeSource);
			final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
			Class<ScriptInterface> compiledFunction = compiler.compile(qName, codeSource, errs,new Class<?>[] { ScriptInterface.class });
			allScriptInterfaces.put(scriptInstance.getCode(),compiledFunction.newInstance());
		} catch (CharSequenceCompilerException e) {
			List<Diagnostic<? extends JavaFileObject>> list = e.getDiagnostics().getDiagnostics();
			for(Diagnostic<? extends JavaFileObject> a :list){
				log.warn(a.getKind().name());
				log.warn(a.getMessage(Locale.getDefault()));
				log.warn("line:"+a.getLineNumber());
				log.warn("column"+a.getColumnNumber()); 
				log.warn("source:"+a.getSource());
				log.warn("code:"+a.getCode());
			}
		} catch (Exception e) {
			log.error("",e);
		} 
	}

	/**
	 * @return the allScriptInterfaces
	 */
	public Map<String, ScriptInterface> getAllScriptInterfaces() {
		return allScriptInterfaces;
	}

	/**
	 * @param allScriptInterfaces the allScriptInterfaces to set
	 */
	public void setAllScriptInterfaces(Map<String, ScriptInterface> allScriptInterfaces) {
		this.allScriptInterfaces = allScriptInterfaces;
	}
	
}
