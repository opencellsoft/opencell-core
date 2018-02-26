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
 * @author anasseh
 */
/**
 * @author Wassim Drira
 *
 */
public class ParamBean {

    private static final Logger log = LoggerFactory.getLogger(ParamBean.class);

    private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private String _propertyFile;

    /**
     * true if it allows multi service instance.
     */
    public static boolean ALLOW_SERVICE_MULTI_INSTANTIATION = false;

    /**
     * Save properties imported from the file.
     */
    private Properties properties = new Properties();

    /**
     * map of categories.
     */
    private HashMap<String, String> categories = new HashMap<String, String>();
    /**
     * true if read file is ok.
     */
    private boolean valid = false;

    /**
     * instance unique.
     */
    private static ParamBean instance = null;

    private static Boolean multiTenancyEnabled;
    private static Map<String, ParamBean> multiTenancyParams = new HashMap<String, ParamBean>();
    private static boolean inheritance = true;
    private boolean isSubTenant = false;

    /**
     * reload properties file.
     */
    private static boolean reload = false;

    /**
     * default constructor.
     */
    public ParamBean() {

    }

    /**
     * Constructeur de ParamBean.
     * 
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
        setValid(initialize());
    }

    /**
     * Retourne une instance de ParamBean.
     * 
     * @param propertiesName property name
     * @return propertiesName properties name
     */
    public static ParamBean getInstance(String propertiesName) {
        if (reload) {
            setInstance(new ParamBean(propertiesName));
        } else if (instance == null) {
            setInstance(new ParamBean(propertiesName));
        }

        return instance;
    }

    /**
     * @return param bean.
     */
    public static ParamBean getInstance() {
        try {
            return getInstance("meveo-admin.properties");
        } catch (Exception e) {
            log.error("Failed to initialize meveo-admin.properties file.", e);
            return null;
        }
    }

    /**
     * Return a ParamBean of the specific provider, if it does not exists, it will be created By the default the file name is <providerCode.properties>
     * 
     * @param provider
     * @return param bean.
     */
    public static ParamBean getInstanceByProvider(String provider) {
        try {
            if (!isMultitenancyEnabled() || "".equals(provider)) {
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
     * Checks if multitenancy is enabled
     */
    public static boolean isMultitenancyEnabled() {
        if (multiTenancyEnabled == null) {
            multiTenancyEnabled = Boolean.valueOf(getInstance().getProperty("meveo.multiTenancy", "false"));
        }
        return multiTenancyEnabled;
    }

    /**
     * @param provider
     * @return
     */
    public String getChrootDir(String provider) {
        if (!isMultitenancyEnabled() || "".equals(provider) || provider == null) {
            return getInstance().getProperty("providers.rootDir", "./opencelldata") + File.separator + instance.getProperty("provider.rootDir", "default");
        }

        String dir;
        dir = getInstance().getProperty("providers.rootDir", "./opencelldata");
        dir += File.separator;
        dir += getInstanceByProvider(provider).getProperty("provider.rootDir", provider);
        return dir;
    }

    /**
     * 
     * @param newInstance instance of ParamBean
     */
    private static void setInstance(ParamBean newInstance) {
        instance = newInstance;
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
     * 
     * @param new_valid true/false
     */
    protected void setValid(boolean new_valid) {
        valid = new_valid;
    }

    /**
     * 
     * @return <code>true/false</code>
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
            if (file.createNewFile()) {
                setProperties(pr);
                saveProperties(file);
                result = true;
            } else {
                pr.load(new FileInputStream(_propertyFile));
                setProperties(pr);
                result = true;
            }
        } catch (IOException e1) {
            log.error("Impossible to create :" + _propertyFile);
        } finally {
            if (propertyFile != null) {
                try {
                    propertyFile.close();
                } catch (Exception e) {
                    log.error("FileInputStream error", e);
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
     * 
     * 
     * @param new_properties Properties
     */
    public void setProperties(Properties new_properties) {
        properties = new_properties;
    }

    /**
     * Set property.
     * 
     * @param property_p java.lang.String
     * @param vNewValue new value.
     */
    public void setProperty(String property_p, String vNewValue) {
        log.info("setProperty " + property_p + "->" + vNewValue);
        if (vNewValue == null) {
            vNewValue = "";
        }
        getProperties().setProperty(property_p, vNewValue);
    }

    public void setProperty(String key, String value, String category) {
        setProperty(key, value);
        if (category != null) {
            categories.put(key, category);
        }
    }

    /**
     * 
     * @return <code>true if is ok</code>
     */
    public synchronized boolean saveProperties() {
        return saveProperties(new File(_propertyFile));
    }

    /**
     * 
     * @param file properties file
     * @return <code>true</code> if we save file sucessfully.
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
     * @param key key of property
     * @param defaultValue default value for key.
     * @return value of property
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
     * @param propertiesName property name
     */
    public static void reload(String propertiesName) {
        // log.info("Reload");
        setInstance(new ParamBean(propertiesName));
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
                if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
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
}