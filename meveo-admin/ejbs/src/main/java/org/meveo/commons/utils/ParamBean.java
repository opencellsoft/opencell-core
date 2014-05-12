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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author anasseh
 */
public class ParamBean {

	private static final Logger log = LoggerFactory.getLogger(ParamBean.class);
	private static final char[] hexDigit = {
		        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
		        };
	private String _propertyFile;

	/**
	 * Save properties imported from the file
	 */
	private Properties properties=new Properties();

	private HashMap<String,String> categories = new HashMap<String, String>();
	/**
	 * Initialisation du Bean correcte.
	 */
	private boolean valid = false;

	/**
	 * instance unique
	 */
	private static ParamBean instance = null;

	private static boolean reload = false;

	public ParamBean() {

	}

	/**
	 * Constructeur de ParamBean.
	 * 
	 */
	private ParamBean(String name) {
		super();
		_propertyFile=name;
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
		log.info("Created Parambean for file:"+_propertyFile);
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

	public static ParamBean getInstance() {
		return getInstance("meveo-admin.properties");
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
		Properties pr = new Properties();
		File file = new File(_propertyFile);
		try {
			if(file.createNewFile()){
				setProperties(pr);
				saveProperties(file);
				result = true;
			} else {
				pr.load(new FileInputStream(_propertyFile));
				setProperties(pr);
				result = true;
			}
		} catch (IOException e1) {
			log.error("Impossible to create :"+_propertyFile);
			e1.printStackTrace();
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
		log.info("setProperty "+property_p+"->"+vNewValue);
		getProperties().setProperty(property_p, vNewValue);
	}

	public void setProperty(String key, String value, String category) {
		setProperty(key,value);
		if(category!=null){
			categories.put(key,category);
		}
	}

	/**
	 * Sauvegarde du fichier de propriï¿½tï¿½s en vigueur.
	 * 
	 * @return <code>true</code> si la sauvegarde a rï¿½ussi, <code>false</code>
	 *         sinon
	 */
	public synchronized boolean saveProperties() {
		return saveProperties(new File(_propertyFile));
	}

	/**
	 * Sauvegarde du fichier de propriï¿½tï¿½s.
	 * 
	 * @return <code>true</code> si la sauvegarde a rï¿½ussi, <code>false</code>
	 *         sinon
	 */
	public boolean saveProperties(File file) {
		boolean result = false;
		String fileName = file.getAbsolutePath();
		log.info("saveProperties to "+fileName);
		OutputStream propertyFile = null;
		BufferedWriter bw = null;
		try {
			propertyFile = new FileOutputStream(file);
			bw = new BufferedWriter(new OutputStreamWriter(propertyFile));
			bw.write("#" + new Date().toString());
			bw.newLine();
			String lastCategory="";
			synchronized (this) {
				List<String> keys = new ArrayList<String>();
				Enumeration<Object> keysEnum = properties.keys();
			    while(keysEnum.hasMoreElements()){
			       keys.add((String)keysEnum.nextElement());
			     }
				Collections.sort(keys);
				for (String key:keys) {
			     key = saveConvert(key,true,true);
			     String val = saveConvert((String)properties.get(key),true,true);
			     if(categories.containsKey(key)){
			    	 if(!lastCategory.equals(categories.get(key))){
			    		 lastCategory=categories.get(key);
					     bw.newLine();
					     bw.write("#" + lastCategory);
					     bw.newLine();
			    	 }
			     }
			     bw.write(key + "=" + val);
			     bw.newLine();
			     }
			}
			bw.flush();
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
			if(bw!=null){
				try {
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//setInstance(new ParamBean(fileName));
		// log.info("-Fin saveProperties , result:" + result);
		return result;
	}

	public String getProperty(String key, String defaultValue) {
		String result = null;
		if(properties.containsKey(key)){
			result = properties.getProperty(key);
		} else {
			result = defaultValue;
			properties.put(key, defaultValue);
			saveProperties();
		}
		return result;
	}

	public static void reload(String propertiesName) {
		// log.info("Reload");
		setInstance(new ParamBean(propertiesName));
	}

	private String saveConvert(String theString,
			                                boolean escapeSpace,
			                                boolean escapeUnicode) {
			         int len = theString.length();
			         int bufLen = len * 2;
			         if (bufLen < 0) {
			             bufLen = Integer.MAX_VALUE;
			         }
			         StringBuffer outBuffer = new StringBuffer(bufLen);
			 
			         for(int x=0; x<len; x++) {
			             char aChar = theString.charAt(x);
			             // Handle common case first, selecting largest block that
			             // avoids the specials below
			             if ((aChar > 61) && (aChar < 127)) {
			                 if (aChar == '\\') {
			                     outBuffer.append('\\'); outBuffer.append('\\');
			                     continue;
			                 }
			                 outBuffer.append(aChar);
			                 continue;
			             }
			             switch(aChar) {
			                 case ' ':
			                     if (x == 0 || escapeSpace)
			                         outBuffer.append('\\');
			                     outBuffer.append(' ');
			                     break;
			                 case '\t':outBuffer.append('\\'); outBuffer.append('t');
			                           break;
			                 case '\n':outBuffer.append('\\'); outBuffer.append('n');
			                           break;
			                 case '\r':outBuffer.append('\\'); outBuffer.append('r');
			                           break;
			                 case '\f':outBuffer.append('\\'); outBuffer.append('f');
			                           break;
			                 case '=': // Fall through
			                 case ':': // Fall through
			                 case '#': // Fall through
			                 case '!':
			                     outBuffer.append('\\'); outBuffer.append(aChar);
			                     break;
			                 default:
			                     if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
			                         outBuffer.append('\\');
			                         outBuffer.append('u');
			                         outBuffer.append(toHex((aChar >> 12) & 0xF));
			                         outBuffer.append(toHex((aChar >>  8) & 0xF));
			                         outBuffer.append(toHex((aChar >>  4) & 0xF));
			                         outBuffer.append(toHex( aChar        & 0xF));
			                     } else {
			                         outBuffer.append(aChar);
			                     }
			             }
			         }
			         return outBuffer.toString();
			     }
	
	private static char toHex(int nibble) {
		        return hexDigit[(nibble & 0xF)];
		      }
}
