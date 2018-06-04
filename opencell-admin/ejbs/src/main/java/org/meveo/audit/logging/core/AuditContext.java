package org.meveo.audit.logging.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.meveo.audit.logging.configuration.AuditConfiguration;
import org.meveo.audit.logging.dto.ClassAndMethods;
import org.meveo.audit.logging.handler.Handler;
import org.meveo.commons.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

/**
 * @author Edward P. Legaspi
 **/
public class AuditContext {

	private static final Logger log = LoggerFactory.getLogger(AuditContext.class);
	private final String AUDIT_CONFIG = "application-audit.config";
	private String _propertyFile;
	private AuditConfiguration auditConfiguration = new AuditConfiguration();
	private static AuditContext instance = null;

	public static AuditContext getInstance() {
		if (instance == null) {
			instance = new AuditContext();
			instance.init();
		}

		return instance;
	}

	/**
	 * Initialize from file.
	 */
	public void init() {
		_propertyFile = AUDIT_CONFIG;
		if (System.getProperty(AUDIT_CONFIG) != null) {
			_propertyFile = System.getProperty(AUDIT_CONFIG);
		} else {
			// https://docs.jboss.org/author/display/AS7/Command+line+parameters
			// http://www.jboss.org/jdf/migrations/war-stories/2012/07/18/jack_wang/
			if (System.getProperty("jboss.server.config.dir") == null) {
				_propertyFile = ResourceUtils.getFileFromClasspathResource(AUDIT_CONFIG).getAbsolutePath();
			} else {
				_propertyFile = System.getProperty("jboss.server.config.dir") + File.separator + AUDIT_CONFIG;
			}
		}

		initialize();

		log.info("Initialized AuditContext.");
	}

	public boolean initialize() {
		if (_propertyFile.startsWith("file:")) {
			_propertyFile = _propertyFile.substring(5);
		}

		boolean result = false;
		File file = new File(_propertyFile);
		try {
			if (file.createNewFile()) {
				// create new file and initialize configuration from default
				auditConfiguration.init();
				saveConfiguration();
				result = true;
			} else {
				// load configuration from file
				readConfiguration();
				result = true;
			}
		} catch (IOException e1) {
			log.error("Impossible to create :" + _propertyFile);
		}

		return result;
	}

	public void saveConfiguration() throws IOException {
		YamlWriter writer = new YamlWriter(new FileWriter(_propertyFile));
		writer.getConfig().setPropertyElementType(AuditConfiguration.class, "handlers", Handler.class);
		writer.getConfig().setClassTag("auditClass", ClassAndMethods.class);
		writer.write(auditConfiguration);
		writer.close();
	}

	public void readConfiguration() throws IOException {
		YamlReader reader = new YamlReader(new FileReader(_propertyFile));
		reader.getConfig().setPropertyElementType(AuditConfiguration.class, "handlers", Handler.class);
		reader.getConfig().setClassTag("auditClass", ClassAndMethods.class);
		auditConfiguration = reader.read(AuditConfiguration.class);

		log.info("Auditing {} classes", auditConfiguration.getClasses().size());
		log.info("Active handlers are: ");
		for (Handler h : auditConfiguration.getHandlers()) {
			log.info(h.getClass().getName());
		}

		reader.close();
	}

	public AuditConfiguration getAuditConfiguration() {
		return auditConfiguration;
	}

	public void setAuditConfiguration(AuditConfiguration auditConfiguration) {
		this.auditConfiguration = auditConfiguration;
	}

}
