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
package org.meveo.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.oracle.OracleDataTypeFactory;

public class BeforeDBTest {

	public static EntityManagerFactory factory;

	private static final String DB_USERNAME = "";
	private static final String DB_PASSWORD = "hibernate.connection.password";
	private static final String DB_URL = "hibernate.connection.url";
	private static final String DB_HBM2DDL = "hibernate.hbm2ddl.auto";
	private static final String DB_DRIVER_CLASS = "hibernate.connection.driver_class";
	private static final String DB_SHOW_SQL = "hibernate.show_sql";

	@SuppressWarnings("deprecation")
	// @BeforeGroups(groups = { "db" })
	public void setUp() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put(DB_USERNAME, "sa");
		params.put(DB_PASSWORD, "");
		params.put(DB_URL, "jdbc:hsqldb:mem:model");
		params.put(DB_HBM2DDL, "create-drop");
		params.put(DB_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
		params.put(DB_SHOW_SQL, "false");

		IDatabaseTester tester = new JdbcDatabaseTester(
				"org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:model", "sa", "");
		tester.setDataSet(new FlatXmlDataSet(new File(
				"target/test-classes/test-data.xml")));
		IDatabaseConnection connection = tester.getConnection();
		DatabaseConfig config = connection.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new OracleDataTypeFactory());
		tester.onSetup();
	}

	// @Test(groups = { "db" })
	public void dummy() {

	}

}
