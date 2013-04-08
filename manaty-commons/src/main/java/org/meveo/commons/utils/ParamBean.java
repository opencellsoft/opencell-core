/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author anasseh
 */
public class ParamBean {

	private static final Logger log = Logger.getLogger(ParamBean.class);

	private String _propertyFile;

	/**
	 * Save properties imported from the file
	 */
	private Properties properties;

	/**
	 * Initialisation du Bean correcte.
	 */
	private boolean valid = false;

	/**
	 * instance unique
	 */
	private static ParamBean instance = null;

	private static boolean reload = false;

	public ParamBean(){
	    
	}
	
	/**
	 * Constructeur de ParamBean.
	 * 
	 */
	private ParamBean(String name) {
		super();
		if (System.getProperty(name) != null) {
			_propertyFile = System.getProperty(name);
		} else {
			// https://docs.jboss.org/author/display/AS7/Command+line+parameters
			// http://www.jboss.org/jdf/migrations/war-stories/2012/07/18/jack_wang/
			if (System.getProperty("jboss.server.config.dir") == null) {
				_propertyFile = ResourceUtils
						.getFileFromClasspathResource(name).getAbsolutePath();
			} else {
				_propertyFile = System.getProperty("jboss.server.config.dir")
						+ File.separator + name;
			}
		}
		setValid(initialize());
	}

	/**
	 * Retourne une instance de ParamBean.
	 * 
	 * @return ParamBean
	 */
	public static ParamBean getInstance(String propertiesName) {
		if (reload) {
			setInstance(new ParamBean(propertiesName));
		} else if (instance == null)
			setInstance(new ParamBean(propertiesName));

		return instance;
	}

	/*
	 * Mis ï¿½ jour de l'instance de ParamBean.
	 * 
	 * @param newInstance ParamBean
	 */
	/**
	 * 
	 * @param newInstance
	 */
	private static void setInstance(ParamBean newInstance) {
		instance = newInstance;
	}

	/**
	 * Retourne les propriï¿½tï¿½s de l'application.
	 * 
	 * @return Properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Retourne une propriï¿½tï¿½ de l'application.
	 * 
	 * @param property_p
	 *            Le nom de la propriï¿½tï¿½ ï¿½ rechercher
	 * @return La valeur de la propriï¿½tï¿½ trouvï¿½e
	 */
	public String getProperty(String property_p) {
		// if (getProperties().getProperty(property_p) == null)
		// log.error("- getProperty : La propriete " + property_p
		// + " n'est pas definie dans le fichier : " + _propertyFile);
		return getProperties().getProperty(property_p);
	}

    public int getPropertyAsInt(String property) {
        try {
            return Integer.parseInt(getProperty(property));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
	
	/**
	 * 
	 * @param new_valid
	 */
	protected void setValid(boolean new_valid) {
		valid = new_valid;
	}

	/**
	 * Initialise les donnï¿½es ï¿½ partir du fichier de propriï¿½tï¿½s.
	 * 
	 * @return <code>true</code> si l'initialisation s'est bien passï¿½e,
	 *         <code>false</code> sinon
	 */
	public boolean initialize() {
		log.debug("-Debut initialize  from file :" + _propertyFile + "...");
		if (_propertyFile.startsWith("file:")) {
			_propertyFile = _propertyFile.substring(5);
		}

		boolean result = false;
		FileInputStream propertyFile = null;
		try {
			Properties pr = new Properties();
			pr.load(new FileInputStream(_propertyFile));
			setProperties(pr);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (propertyFile != null) {
				try {
					propertyFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// log.debug("-Fin initialize , result:" + result
		// + ", portability.defaultDelay="
		// + getProperty("portability.defaultDelay"));
		return result;
	}

	public static void setReload(boolean reload) {
		ParamBean.reload = reload;
	}

	/**
	 * Accesseur sur l'init du Bean.
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Met ï¿½ jour les propriï¿½tï¿½s de l'application.
	 * 
	 * @param new_properties
	 *            Properties
	 */
	public void setProperties(Properties new_properties) {
		properties = new_properties;
	}

	/**
	 * Met ï¿½ jour la propriï¿½tï¿½ nommï¿½e "property_p"
	 * 
	 * @param property_p
	 *            java.lang.String
	 * @return String
	 */
	public void setProperty(String property_p, String vNewValue) {
		getProperties().setProperty(property_p, vNewValue);
	}

	/**
	 * Sauvegarde du fichier de propriï¿½tï¿½s en vigueur.
	 * 
	 * @return <code>true</code> si la sauvegarde a rï¿½ussi, <code>false</code>
	 *         sinon
	 */
	public boolean saveProperties() {
		return saveProperties(new File(_propertyFile));
	}

	/**
	 * Sauvegarde du fichier de propriï¿½tï¿½s.
	 * 
	 * @return <code>true</code> si la sauvegarde a rï¿½ussi, <code>false</code>
	 *         sinon
	 */
	public boolean saveProperties(File file) {
		// log.info("-Debut saveProperties ...");
		boolean result = false;
		String fileName = file.getAbsolutePath();
		OutputStream propertyFile = null;
		try {
			propertyFile = new FileOutputStream(file);
			properties.store(propertyFile, fileName);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (propertyFile != null) {
				try {
					propertyFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		setInstance(new ParamBean(fileName));
		// log.info("-Fin saveProperties , result:" + result);
		return result;
	}

	public String getProperty(String key, String defaultValue) {
		String result = null;
		try {
			result = getProperty(key);
		} catch (Exception e) {
		}
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	public static void reload(String propertiesName) {
		// log.info("Reload");
		setInstance(new ParamBean(propertiesName));
	}
}
