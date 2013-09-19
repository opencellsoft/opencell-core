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
package org.meveo.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

public class BeforeDBTest {

    public static EntityManagerFactory factory;

    private static final String DB_USERNAME = "";
    private static final String DB_PASSWORD = "hibernate.connection.password";
    private static final String DB_URL = "hibernate.connection.url";
    private static final String DB_HBM2DDL = "hibernate.hbm2ddl.auto";
    private static final String DB_DRIVER_CLASS = "hibernate.connection.driver_class";
    private static final String DB_SHOW_SQL = "hibernate.show_sql";

    @SuppressWarnings("deprecation")
    @BeforeGroups(groups = { "db" })
    public void setUp() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put(DB_USERNAME, "sa");
        params.put(DB_PASSWORD, "");
        params.put(DB_URL, "jdbc:hsqldb:mem:model");
        params.put(DB_HBM2DDL, "create-drop");
        params.put(DB_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
        params.put(DB_SHOW_SQL, "false");
        factory = Persistence.createEntityManagerFactory("ModelPU", params);
        IDatabaseTester tester = new JdbcDatabaseTester("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:model", "sa", "");
        tester.setDataSet(new FlatXmlDataSet(new File("target/test-classes/test-data.xml")));
        IDatabaseConnection connection = tester.getConnection();
        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
        tester.onSetup();
    }

    @Test(groups = { "db" })
    public void dummy() {

    }

}
