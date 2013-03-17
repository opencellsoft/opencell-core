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
package org.meveo.oudaya;

import java.util.HashMap;
import java.util.Map;

import org.meveo.commons.utils.ParamBean;
import org.meveo.config.MeveoConfig;

/**
 * Configuration wrapper for oudaya.
 * 
 * @author Ignas Lelys
 * @created 2009.06.11
 */
public class OudayaConfig implements MeveoConfig {

    private static final String OUDAYA_PROPERTIES_FILENAME = "oudaya.properties";
    private static final String SCANNING_INTERVAL = "oudaya.scanningInterval";
    private static final String PERSISTENCE_UNIT_NAME = "oudaya.persistenceUnitName";
    private static final String SQL_BATCH_SIZE = "oudaya.sqlBatchSize";
    private static final String WORKING_THREADS = "oudaya.workingThreadCount";
    private static final String DB_USERNAME = "hibernate.connection.username";
    private static final String DB_PASSWORD = "hibernate.connection.password";
    private static final String DB_URL = "hibernate.connection.url";
    private static final String DB_HBM2DDL = "hibernate.hbm2ddl.auto";
    private static final String DB_DRIVER_CLASS = "hibernate.connection.driver_class";
    private static final String DB_SHOW_SQL = "hibernate.show_sql";
    private static final String OUDAYA_GUICE_MODULE = "oudaya.guiceConfigurationModule";
    private static final String OUDAYA_INVOICES_DIR = "oudaya.invoicesDir";
    private static final String OUDAYA_PROVIDER_CODES = "oudaya.provider.codes";
    private static final String OUDAYA_DECIMAL_FORMAT = "oudaya.decimalFormat";

    private static final String CUSTOMER_ACCOUNT_SERVICE_NAME = "oudaya.customerAccountServiceName";
    private static final String SERVICE_PROVIDER_URL = "oudaya.serviceProviderUrl";
    private static final String DEFAULT_PROVIDER_ID = "oudaya.defaultProvider.id";
    private static final String TO_LABEL = "to.label";
    // Defaults
    private static final String DEFAULT_SCANNING_INTERVAL = "5000"; // 0.5s
    private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "OudayaPU";
    private static final String DEFAULT_SQL_BATCH_SIZE = "100";
    private static final String DEFAULT_WORKING_THREADS = "2";
    private static final String DEFAULT_CUSTOMER_ACCOUNT_SERVICE_NAME = "meveo-admin/CustomerAccountService/remote";
    private static final String DEFAULT_SERVICE_PROVIDER_URL = "jnp://127.0.0.1:1099";
    private static final String DEFAULT_DEFAULT_PROVIDER_ID = "1";

    /**
     * Get persistence unit name.
     * 
     */
    public static String getPersistenceUnitName() {
        return ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(PERSISTENCE_UNIT_NAME,
                DEFAULT_PERSISTENCE_UNIT_NAME);
    }

    /**
     * Get overridden persistence configuration.
     */
    public static Map<String, String> getPersistenceProperties() {
        String username = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(DB_USERNAME);
        String password = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(DB_PASSWORD);
        String url = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(DB_URL);
        String hbm2ddl = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(DB_HBM2DDL);
        String driverClass = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(DB_DRIVER_CLASS);
        String showSql = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(DB_SHOW_SQL);

        Map<String, String> params = new HashMap<String, String>();
        if (username != null) {
            params.put(DB_USERNAME, username);
        }
        if (password != null) {
            params.put(DB_PASSWORD, password);
        }
        if (url != null) {
            params.put(DB_URL, url);
        }
        if (hbm2ddl != null) {
            params.put(DB_HBM2DDL, hbm2ddl);
        }
        if (driverClass != null) {
            params.put(DB_DRIVER_CLASS, driverClass);
        }
        if (showSql != null) {
            params.put(DB_SHOW_SQL, showSql);
        }
        return params;
    }

    /**
     * Get configuration module for Oudaya application.
     */
    public static String getOudayaConfigurationModule() {
        return ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(OUDAYA_GUICE_MODULE);
    }

    /**
     * @see org.meveo.config.MeveoConfig#getSQLBatchSize()
     */
    public long getSQLBatchSize() {
        String value = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(SQL_BATCH_SIZE,
                DEFAULT_SQL_BATCH_SIZE);
        return Long.parseLong(value);
    }

    /**
     * @see org.meveo.config.MeveoConfig#getScanningInterval()
     */
    public long getScanningInterval() {
        String value = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(SCANNING_INTERVAL,
                DEFAULT_SCANNING_INTERVAL);
        return Long.parseLong(value);
    }

    /**
     * @see org.meveo.config.MeveoConfig#getThreadCount()
     */
    public int getThreadCount() {
        String value = ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(WORKING_THREADS,
                DEFAULT_WORKING_THREADS);
        return Integer.parseInt(value);
    }

    /**
     * @see org.meveo.config.MeveoConfig#getApplicationName()
     */
    public String getApplicationName() {
        return "OUDAYA";
    }

    /**
     * @see org.meveo.config.MeveoConfig#getBatchJobCron(java.lang.String)
     */
    public String getBatchJobCron(String batchJobName) {
        return null;
    }

    public static String getOudayaInvoicesDirectory() {
        return ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(OUDAYA_INVOICES_DIR);
    }

    public static String[] getProviderCodes() {
        return ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(OUDAYA_PROVIDER_CODES, ",").replaceAll(
                ";", ",").split(",");
    }

    public static String getDecimalFormat() {
        return ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(OUDAYA_DECIMAL_FORMAT, "#,##0.00");
    }

    /**
     * Customer account service name.
     */
    public static String getCustomerAccountServiceName() {
        return ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(CUSTOMER_ACCOUNT_SERVICE_NAME,
                DEFAULT_CUSTOMER_ACCOUNT_SERVICE_NAME);
    }

    public static String getServiceProviderUrl() {
        return ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(SERVICE_PROVIDER_URL,
                DEFAULT_SERVICE_PROVIDER_URL);
    }

	public Long getDefaultProviderId() {
		return Long.valueOf(ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(DEFAULT_PROVIDER_ID,
				DEFAULT_DEFAULT_PROVIDER_ID));
	}
	
	public static String getToLabel(String languageCode) {
		return ParamBean.getInstance(OUDAYA_PROPERTIES_FILENAME).getProperty(TO_LABEL+"."+languageCode,
				"to");
	}

}
