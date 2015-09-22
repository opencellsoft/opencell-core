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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptTypeEnum;
import org.meveo.service.base.PersistenceService;

@Singleton
@Startup
public class ScriptInstanceService extends PersistenceService<ScriptInstance> {

	@Inject
	ScriptInstanceErrorService scriptInstanceErrorService;

	@Inject
	private ResourceBundle resourceMessages;

	private Map<String, Map<String, List<String>>> allLogs = new HashMap<String, Map<String, List<String>>>();

	private Map<String, Map<String, Class<ScriptInterface>>> allScriptInterfaces = new HashMap<String, Map<String, Class<ScriptInterface>>>();

	private CharSequenceCompiler<ScriptInterface> compiler;

	private String classpath = "";

	/**
	 * Find ScriptInstances by type
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ScriptInstance> findByType(ScriptTypeEnum type) {
		List<ScriptInstance> result = new ArrayList<ScriptInstance>();
		QueryBuilder qb = new QueryBuilder(ScriptInstance.class, "t");
		qb.addCriterionEnum("t.scriptTypeEnum", type);
		try {
			result = (List<ScriptInstance>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {

		}
		return result;
	}
    
	/**
	 * Find ScriptInstance by code and provider
	 * 
	 * @param code
	 * @param provider
	 * @return
	 */
	public ScriptInstance findByCode(String code, Provider provider) {
		log.debug("find ScriptInstance by code {}", code);
		QueryBuilder qb = new QueryBuilder(ScriptInstance.class, "t", null, provider);
		qb.addCriterion("t.code", "=", code, false);
		try {
			return (ScriptInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Save or update the script instance and set the code as the canonical name of script class
	 * 
	 * @param scriptInstance
	 * @param user
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public ScriptInstance saveOrUpdate(ScriptInstance scriptInstance, User user, Provider provider) throws Exception {

		String packageName = getPackageName(scriptInstance.getScript());
		String className = getClassName(scriptInstance.getScript());
		if (packageName == null || className == null) {
			throw new Exception(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
		}

		scriptInstance.setCode(packageName + "." + className);
		if (scriptInstance.isTransient()) {
			create(scriptInstance, user, provider);
		} else {
			update(scriptInstance, user);
		}
		compileScript(scriptInstance);
		scriptInstance = findById(scriptInstance.getId());
		return scriptInstance;
	}

	/**
	 * Remove all compilation error for scriptInstance
	 * 
	 * @param scriptInstance
	 */
	public void removeErrors(ScriptInstance scriptInstance) {
		getEntityManager().createQuery("delete from ScriptInstanceError o where o.scriptInstance=:scriptInstance").setParameter("scriptInstance", scriptInstance).executeUpdate();
	}
    
	/**
	 * Get all ScriptInstances with error for a provder
	 * 
	 * @param provider
	 * @return
	 */
	public List<ScriptInstance> getScriptInstancesWithError(Provider provider) {
		return ((List<ScriptInstance>) getEntityManager().createNamedQuery("ScriptInstance.getScriptInstanceOnError", ScriptInstance.class).setParameter("isError", Boolean.TRUE).setParameter("provider", provider).getResultList());
	}
    
	/**
	 * Count scriptInstances with error for a provider
	 * 
	 * @param provider
	 * @return
	 */
	public long countScriptInstancesWithError(Provider provider) {
		return ((Long) getEntityManager().createNamedQuery("ScriptInstance.countScriptInstanceOnError", Long.class).setParameter("isError", Boolean.TRUE).setParameter("provider", provider).getSingleResult());
	}

	/**
	 * Build the classpath and compile all scriptInstances
	 */
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
			List<ScriptInstance> scriptInstances = findByType(ScriptTypeEnum.JAVA);
			for (ScriptInstance scriptInstance : scriptInstances) {
				compileScript(scriptInstance);
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	/**
	 * Find ScriptInstance and compile it
	 * 
	 * @param scriptInstance
	 */
	public void compileScript(String scriptInstanceCode, Provider provider) {
		ScriptInstance scriptInstance = findByCode(scriptInstanceCode, provider);
		if (scriptInstance == null) {
			log.error("compileScript cannot find scriptInstance by code:" + scriptInstanceCode);
		} else {
			compileScript(scriptInstance);
		}
	}
	
	/**
	 * Compile ScriptInstance and update status
	 * 
	 * @param scriptInstance
	 */
	public void compileScript(ScriptInstance scriptInstance) {
		try {
			final String packageName = getPackageName(scriptInstance.getScript());
			final String qName = packageName + '.' + getClassName(scriptInstance.getScript());
			final String codeSource = scriptInstance.getScript();
			log.debug("codeSource to compile:" + codeSource);
			scriptInstance.setError(false);
			scriptInstance.setCode(qName);
			removeErrors(scriptInstance);
			scriptInstance.getScriptInstanceErrors().clear();
			update(scriptInstance);
			Class<ScriptInterface> compiledScript = compileJavaSrouce(codeSource, qName);
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
			update(scriptInstance);

		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	/**
	 * Compile java Source script
	 * 
	 * @param javaSrc Java source to compile
	 * @param qName Canonical Name
	 * @return 
	 * @throws CharSequenceCompilerException
	 */
	public Class<ScriptInterface> compileJavaSrouce(String javaSrc, String qName) throws CharSequenceCompilerException {
		compiler = new CharSequenceCompiler<ScriptInterface>(ScriptInterface.class.getClassLoader(), Arrays.asList(new String[] { "-cp", classpath }));
		final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
		Class<ScriptInterface> compiledScript = compiler.compile(qName, javaSrc, errs, new Class<?>[] { ScriptInterface.class });
		return compiledScript;
	}

	/**
	 *  Execute the scriptInstance
	 *  
	 * @param provider Provider's scriptInstance
	 * @param scriptCode ScriptInstanceCode
	 * @param context  Context params
	 */
	public void execute(Provider provider, String scriptCode, Map<String, Object> context) {
		try {
			execute( getScriptInterface(provider, scriptCode),context,provider);
		} catch (Exception e) {
			log.error("Script execution failed",e);
		} 
	}	
	
	/**
	 * Execute a class that extends Script
	 * 
	 * @param scriptClass
	 * @param context
	 * @param provider
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BusinessException
	 */
	private  void execute(Class<ScriptInterface> scriptClass, Map<String, Object> context,Provider provider) throws InstantiationException, IllegalAccessException, BusinessException {
			ScriptInterface script = scriptClass.newInstance();
			script.execute(context, getCurrentProvider());		
	}

	/**
	 *  Wrap the logger and execute script
	 *  
	 * @param provider  
	 * @param scriptCode
	 * @param context
	 */
	public void test(Provider provider, String scriptCode, Map<String, Object> context) {		
		try{
			clearLogs(provider.getCode(), scriptCode);
		    ScriptInstance scriptInstance = findByCode(scriptCode, provider);		
			String javaSrc = scriptInstance.getScript();
			javaSrc = javaSrc.replaceAll("LoggerFactory.getLogger", "new org.meveo.service.script.RunTimeLogger(" + getClassName(javaSrc) + ".class,\"" + provider.getCode() + "\",\"" + scriptCode + "\");//");
			log.debug("script for testing: {}", javaSrc);
			Class<ScriptInterface> compiledScript = compileJavaSrouce(javaSrc, getPackageName(scriptInstance.getScript()) + "." + getClassName(scriptInstance.getScript()));
			execute(compiledScript,context,provider);

		} catch (Exception e) {
			log.error("Script test failed",e);
		}

	}
	
	/**
	 * Find  the class for ScriptInstance
	 * 
	 * @param provider
	 * @param scriptCode
	 * @return Script Class
	 */
	public Class<ScriptInterface> getScriptInterface(Provider provider, String scriptCode) {
		Class<ScriptInterface> result = null;
		if (allScriptInterfaces.containsKey(provider.getCode())) {
			result = allScriptInterfaces.get(provider.getCode()).get(scriptCode);
		}
		if (result == null) {
			ScriptInstance scriptInstance = findByCode(scriptCode, provider);
			if (scriptInstance != null) {
				compileScript(scriptInstance);
				if (allScriptInterfaces.containsKey(provider.getCode())) {
					result = allScriptInterfaces.get(provider.getCode()).get(scriptCode);
				}
			}else{
              log.debug("ScriptInstance with " + scriptCode + " does not exist");
			}
		}
		log.debug("getScriptInterface provider:{} scriptCode:{} -> {}", provider.getCode(), scriptCode, result);
		return result;
	}
	
	/**
	 *  Add a log line for a scriptInstance
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
	 *  Get logs for scriptInstance
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
	 *  Clear all logs for a scriptInstance
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
	public String getPackageName(String src) {
		return StringUtils.patternMacher("package (.*?);", src);
	}

	/**
	 * Find the class name in a source java text
	 * 
	 * @param src Java source code
	 * @return Class name
	 */
	public String getClassName(String src) {
		return StringUtils.patternMacher("public class (.*) extends", src);
	}
}