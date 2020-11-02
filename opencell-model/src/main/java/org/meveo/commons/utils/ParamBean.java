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
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains application configuration settings
 * 
 * @author anasseh
 * @author akadid abdelmounaim
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
public class ParamBean {

    private static final Logger log = LoggerFactory.getLogger(ParamBean.class);

    private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private String _propertyFile;

    /**
     * Save properties imported from the file.
     */
    private Properties properties = new Properties();

    /**
     * Map of categories.
     */
    private HashMap<String, String> categories = new HashMap<String, String>();

    /**
     * True if read file is ok.
     */
    private boolean valid = false;

    /**
     * Are current properties for secondary provider/tenant
     */
    private boolean isSubTenant = false;

    /**
     * Configuration instance
     */
    private static ParamBean instance = null;

    /**
     * Is multitenancy enabled
     */
    private static Boolean multiTenancyEnabled;

    /**
     * Application configuration settings by a provider/tenant
     */
    private static Map<String, ParamBean> multiTenancyParams = new HashMap<String, ParamBean>();

    /**
     * Reload application configuration properties file.
     */
    private static boolean reload = false;

    protected ParamBean() {

    }

    /**
     * Loads application configuration properties from file
     * 
     * @param name System property containing a file name, or a relative filename in Wildfly server's configuration directory
     */
    private ParamBean(String name) {
        super();
        _propertyFile = name;
        if (System.getProperty(name) != null) {
            _propertyFile = System.getProperty(name);
        } else {
            // https://docs.jboss.org/author/display/AS7/Command+line+parameters
            // http://www.jboss.org/jdf/migrations/war-stories/2012/07/18/jack_wang/
            if (System.getProperty("jboss.server.config.dir") == null) {
                _propertyFile = ResourceUtils.getFileFromClasspathResource(name).getAbsolutePath();
            } else {
                _propertyFile = System.getProperty("jboss.server.config.dir") + File.separator + name;
            }
        }
        log.info("Created Parambean for file:" + _propertyFile);
        initialize();
    }

    /**
     * Get an application configuration instance from a given file.
     * 
     * @param propertiesName System property containing a file name, or a relative filename in Wildfly server's configuration directory
     * @return Application configuration instance
     */
    public static ParamBean getInstance(String propertiesName) {
        if (reload || instance == null) {
            instance = new ParamBean(propertiesName);
        }

        return instance;
    }

    /**
     * Get an application configuration instance from a default file.
     * 
     * @return Application configuration instance
     */
    public static synchronized ParamBean getInstance() {
        try {
            return getInstance("opencell-admin.properties");
        } catch (Exception e) {
            log.error("Failed to initialize opencell-admin.properties file.", e);
            return null;
        }
    }

    /**
     * Return an application configuration instance of the specific provider, if it does not exists, it will be created By the default the file name is
     * &gt;providerCode.properties&lt;
     * 
     * @param provider Provider code
     * @return Application configuration instance
     */
    public static ParamBean getInstanceByProvider(String provider) {
        try {
            if (!isMultitenancyEnabled() || "".equals(provider) || StringUtils.isBlank(provider)) {
                return getInstance();
            }

            if (multiTenancyParams.containsKey(provider)) {
                return multiTenancyParams.get(provider);
            }

            ParamBean providerParamBean = new ParamBean(provider + ".properties");
            providerParamBean.isSubTenant = true;
            multiTenancyParams.put(provider, providerParamBean);
            return providerParamBean;

        } catch (Exception e) {
            log.error("Failed to initialize " + provider + ".properties file.", e);
            return null;
        }
    }

    /**
     * Check whether service multi instantiation is allowed
     * 
     * @return is allowed.
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public boolean isServiceMultiInstantiation() {
        return "true".equalsIgnoreCase(getProperty("service.allowMultiInstantiation", "false"));
    }

    /**
     * Checks if multitenancy is enabled. Flag is consulted in a main provider's/tenant's property file
     * 
     * @return True of multitenancy is enabled
     */
    public static boolean isMultitenancyEnabled() {
        ParamBean currentInstance = getInstance();
        if (currentInstance == null) {
            return multiTenancyEnabled;
        }

        if (multiTenancyEnabled == null) {
            multiTenancyEnabled = Boolean.valueOf(currentInstance.getProperty("meveo.multiTenancy", "false"));
        }
        return multiTenancyEnabled;
    }

    /**
     * Get a file directory root for a given provider.
     * 
     * @param provider Provider code
     * @return Full path to provider's data files
     */
    public String getChrootDir(String provider) {
        ParamBean currentInstance = getInstance();
        if (currentInstance == null) {
            return null;
        }

        if (!isMultitenancyEnabled() || "".equals(provider) || provider == null) {
            return currentInstance.getProperty("providers.rootDir", "./opencelldata") + File.separator + instance.getProperty("provider.rootDir", "default");
        }

        String dir;
        dir = currentInstance.getProperty("providers.rootDir", "./opencelldata");
        dir += File.separator;
        ParamBean instanceByProvider = getInstanceByProvider(provider);
        if (instanceByProvider != null) {
            dir += instanceByProvider.getProperty("provider.rootDir", provider);
        }
        return dir;
    }

    /**
     * Get application configuration properties
     * 
     * @return Properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Is file a valid file (was read successfully)
     * 
     * @return boolean
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Initialize/load application configuration property file
     * 
     */
    private void initialize() {
        log.debug("Initialize  from file :" + _propertyFile + "...");
        if (_propertyFile.startsWith("file:")) {
            _propertyFile = _propertyFile.substring(5);
        }

        boolean result = false;
        Properties pr = new Properties();
        File file = new File(_propertyFile);
        try {
            if (file.createNewFile()) {
                setProperties(pr);
                saveProperties(file);
                result = true;
            } else {
                try(FileInputStream propertyFileInputStream = new FileInputStream(_propertyFile)){
                    pr.load(propertyFileInputStream);
                    setProperties(pr);
                    result = true;
                }
            }
        } catch (IOException e1) {
            log.error("Impossible to create :" + _propertyFile);
        }finally {

        }
        // log.debug("-Fin initialize , result:" + result
        // + ", portability.defaultDelay="
        // + getProperty("portability.defaultDelay"));
        valid = result;
    }

    /**
     * Should application configuration instance be reloaded
     * 
     * @param reload True if reload
     */
    public static void setReload(boolean reload) {
        ParamBean.reload = reload;
    }

    /**
     * Set properties
     * 
     * @param new_properties New properties to set
     */
    public void setProperties(Properties new_properties) {
        properties = new_properties;
    }

    /**
     * Set a single property.
     * 
     * @param property Property key
     * @param value Property value
     */
    public void setProperty(String property, String value) {
        log.info("setProperty " + property + "->" + value);
        if (value == null) {
            value = "";
        }
        getProperties().setProperty(property, value);
    }

    /**
     * Set a single property in a category
     * 
     * @param property Property key
     * @param value Property value
     * @param category Category name
     */
    public void setProperty(String property, String value, String category) {
        setProperty(property, value);
        if (category != null) {
            categories.put(property, category);
        }
    }

    /**
     * Save application configuration properties to a default file
     * 
     * @return True if is ok
     */
    public synchronized boolean saveProperties() {
        return saveProperties(new File(_propertyFile));
    }

    /**
     * Save application configuration properties to a given file
     * 
     * @param file File to save to
     * @return True if file was saved successfully.
     */
    public boolean saveProperties(File file) {
        boolean result = false;
        String fileName = file.getAbsolutePath();
        log.info("saveProperties to " + fileName);
        OutputStream propertyFile = null;
        BufferedWriter bw = null;
        try {
            propertyFile = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(propertyFile));
            bw.write("#" + new Date().toString());
            bw.newLine();
            String lastCategory = "";
            synchronized (this) {
                List<String> keys = new ArrayList<String>();
                Enumeration<Object> keysEnum = properties.keys();
                while (keysEnum.hasMoreElements()) {
                    keys.add((String) keysEnum.nextElement());
                }
                Collections.sort(keys);
                for (String key : keys) {
                    key = saveConvert(key, true, true);
                    String val = saveConvert((String) properties.get(key), true, true);
                    if (categories.containsKey(key)) {
                        if (!lastCategory.equals(categories.get(key))) {
                            lastCategory = categories.get(key);
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
            log.error("failed to save properties ", e);
        } finally {
            if (propertyFile != null) {
                try {
                    propertyFile.close();
                } catch (Exception e) {
                    log.error("outputStream error ", e);
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (Exception e) {
                    log.error("BufferedWriter error", e);
                }
            }
        }
        // setInstance(new ParamBean(fileName));
        // log.info("-Fin saveProperties , result:" + result);
        return result;
    }

    /**
     * Get property value as integer. Sets the property to a default value if value was not set previously.
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @return Value of property, or a default value if it is not set yet
     */
    public int getPropertyAsInteger(String key, int defaultValue) {
        int value = defaultValue;
        try {
            value = Integer.parseInt(getProperty(key, Integer.toString(defaultValue)));
        } catch (NumberFormatException e) {
        }
        return value;
    }

    /**
     * Get property value as boolean. Sets the property to a default value if value was not set previously.
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @return Value of property, or a default value if it is not set yet
     */
    public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        boolean value = defaultValue;
        try {
            value = Boolean.parseBoolean(getProperty(key, Boolean.toString(defaultValue)));
        } catch (NumberFormatException e) {
        }
        return value;
    }

    /**
     * Get property value. Sets the property to a default value if value was not set previously.
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @return Value of property, or a default value if it is not set yet
     */
    public String getProperty(String key, String defaultValue) {
        String result = null;
        if (properties.containsKey(key)) {
            result = properties.getProperty(key);
        } else if (defaultValue != null) {
            result = defaultValue;
            properties.put(key, defaultValue);
            saveProperties();
        }
        return result;
    }

    /**
     * Reload application configuration from a given file
     * 
     * @param propertiesName System property containing a file name, or a relative filename in Wildfly server's configuration directory
     */
    public static void reload(String propertiesName) {
        // log.info("Reload");
        instance = new ParamBean(propertiesName);
    }

    /**
     * @param theString input string
     * @param escapeSpace true if escape spacce
     * @param escapeUnicode true if escape unicode
     * @return escaped string.
     */
    private String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);

        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
            case ' ':
                if (x == 0 || escapeSpace)
                    outBuffer.append('\\');
                outBuffer.append(' ');
                break;
            case '\t':
                outBuffer.append('\\');
                outBuffer.append('t');
                break;
            case '\n':
                outBuffer.append('\\');
                outBuffer.append('n');
                break;
            case '\r':
                outBuffer.append('\\');
                outBuffer.append('r');
                break;
            case '\f':
                outBuffer.append('\\');
                outBuffer.append('f');
                break;
            case '=': // Fall through
            case ':': // Fall through
            case '#': // Fall through
            case '!':
                outBuffer.append('\\');
                outBuffer.append(aChar);
                break;
            default:
                if (((aChar < 0x0020) || (aChar > 0x007e)) && escapeUnicode) {
                    outBuffer.append('\\');
                    outBuffer.append('u');
                    outBuffer.append(toHex((aChar >> 12) & 0xF));
                    outBuffer.append(toHex((aChar >> 8) & 0xF));
                    outBuffer.append(toHex((aChar >> 4) & 0xF));
                    outBuffer.append(toHex(aChar & 0xF));
                } else {
                    outBuffer.append(aChar);
                }
            }
        }
        return outBuffer.toString();
    }

    /**
     * @param nibble input int
     * @return hex output
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    /**
     * A shortcut to get date format.
     * 
     * @return Date format string
     */
    public String getDateFormat() {
        return getProperty("meveo.dateFormat", "dd/MM/yyyy");
    }

    /**
     * A shortcut to get date with time format.
     * 
     * @return date time format.
     */
    public String getDateTimeFormat() {
        return getProperty("meveo.dateTimeFormat", "dd/MM/yyyy HH:mm");
    }

    /**
     * Get a property value for a given provider. Sets the property to a default value if value was not set previously.
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @param provider Provider code
     * @return Value of property, or a default value if it is not set yet
     */
    public String getProperty(String key, String defaultValue, String provider) {
        String result = null;
        ParamBean params = getInstanceByProvider(provider);
        Properties properties = null;
        if (params != null) {
            properties = params.getProperties();
        }

        if (properties != null && properties.containsKey(key)) {
            result = properties.getProperty(key);
        } else if (defaultValue != null) {
            result = defaultValue;
            if (properties != null) {
                properties.put(key, defaultValue);
            }
            if (params != null) {
                params.setProperties(properties);
                params.saveProperties();
            }
        }
        return result;
    }

    /**
     * Get a property value for a given provider or if not set - from a main provider. Sets the property to a default value in main provider's configuration if value was not set
     * previously.
     * 
     * @param key Property key
     * @param defaultValue Default value
     * @param provider Provider code
     * @return Value of property, or a default value if it is not set yet
     */
    public String getInheritedProperty(String key, String defaultValue, String provider) {
        String result = null;
        Properties properties = null;
        ParamBean params = getInstanceByProvider(provider);
        if (params != null) {
            properties = params.getProperties();
        }

        if (properties != null && properties.containsKey(key)) {
            result = properties.getProperty(key);
        } else if (params != null && params.isSubTenant) {
            // check if a value is already defined for the main tenant
            ParamBean currentInstance = getInstance();
            if (currentInstance != null) {
                result = currentInstance.getProperty(key, defaultValue);
            }
            if (properties != null) {
                properties.put(key, result);
            }
            params.setProperties(properties);
            params.saveProperties();
        }

        return result;
    }

    /**
     * A shortcut to get date with time format for a given provider
     * 
     * @param provider Provider code
     * 
     * @return date time format.
     */
    public String getDateFormat(String provider) {
        if (!isSubTenant) {
            ParamBean instanceByProvider = getInstanceByProvider(provider);
            if (instanceByProvider != null) {
                return instanceByProvider.getDateFormat();
            }

        }
        return getDateFormat();
    }

    /**
     * A shortcut to get date with time format for a given provider
     * 
     * @param provider Provider code
     * 
     * @return date time format.
     */
    public String getDateTimeFormat(String provider) {
        if (!isSubTenant) {
            ParamBean instanceByProvider = getInstanceByProvider(provider);
            if (instanceByProvider != null) {
                return instanceByProvider.getDateTimeFormat();
            }

        }
        return getDateTimeFormat();
    }

    /**
     * Get a production stage property value. False means that is on development stage.
     *
     * @return true/false
     */
    public boolean isProductionStage() {
        ParamBean bean = ParamBean.getInstance();
        if (bean != null) {
            return Boolean.parseBoolean(bean.getProperty("production.stage", "false"));
        }
        return false;
    }

    /**
     * Check if Spark is enabled. If not, fields to enter Spark relaetd data will not be shown.
     *
     * @return true/false
     */
    public boolean isSparkEnabled() {
        ParamBean bean = ParamBean.getInstance();
        if (bean != null) {
            return Boolean.parseBoolean(bean.getProperty("spark.enabled", "false"));
        }
        return false;
    }
    
    /**
     * Check if boolean property is filled, the boolean value will be returned. if the value is not filled or not well spelled, null value is returned
     *
     * @param key Property key
     * @return true/false/null
     */
	public Boolean getBooleanValue(String key) {
		if (getProperty(key, "").equalsIgnoreCase("true")) {
			return true;
		}
		if (getProperty(key, "").equalsIgnoreCase("false")) {
			return false;
		}
		return null;
	}
	
    /**
     * Check if boolean property is filled, the boolean value will be returned. if the value is not filled or not well spelled, defaultValue value is returned
     *
     * @param key Property key
     * @param defaultValue Default value
     * @return true/false/null
     */
	public boolean getBooleanValue(String key, boolean defaultValue) {
		return getBooleanValue(key) != null ? getBooleanValue(key) : defaultValue;
	}
	
}