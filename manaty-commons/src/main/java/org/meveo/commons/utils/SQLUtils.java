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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO test
/**
 * SQL Utility methods.
 * 
 * @author Donatas Remeika
 * @created Mar 13, 2009
 */
public final class SQLUtils {

    /**
     * No need to instantiate.
     */
    private SQLUtils() {

    }

    private static final Logger logger = LoggerFactory.getLogger(SQLUtils.class);

    /**
     * Get String value from ResultSet and close it.
     * 
     * @param rs
     *            ResultSet to get value from.
     * @return String value.
     * @throws SQLException
     */
    public static String getStringAndCloseResultSet(ResultSet rs) throws SQLException {
        try {
            if (rs.next()) {
                return rs.getString(1);
            }
        } finally {
            SQLUtils.closeResultSet(rs);
        }
        return null;
    }

    /**
     * Get Object array from ResultSet and close it.
     * 
     * @param rs
     *            ResultSet to get value from.
     * @return Object array.
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public static Object[] getValuesAndCloseResultSet(ResultSet rs, Class... types) throws SQLException {
        try {
            if (rs.next()) {
                Object[] values = new Object[types.length];
                for (int i = 1; i <= types.length; i++) {
                    Class type = types[i - 1];
                    if (Long.class == type) {
                        values[i - 1] = rs.getLong(i);
                        if (rs.wasNull()) {
                            values[i - 1] = null;
                        }
                    } else if (String.class == type) {
                        values[i - 1] = rs.getString(i);
                    } else if (Date.class == type) {
                        values[i - 1] = rs.getDate(i);
                    } else if (java.util.Date.class == type ) {
                        values[i - 1] = rs.getDate(i);
                    } else {
                        values[i - 1] = null;
                    }
                }
                return values;
            }
        } finally {
            SQLUtils.closeResultSet(rs);
        }
        return null;
    }

    /**
     * Get Integer value from ResultSet and close it.
     * 
     * @param rs
     *            ResultSet to get value from.
     * @return Integer value.
     */
    public static Integer getIntegerAndCloseResultSet(ResultSet rs) {
        try {
            if (rs.next()) {
                int value = rs.getInt(1);
                return rs.wasNull() ? null : value;
            }
        } catch (SQLException e) {
            logger.error("Could not get Integer from ResultSet", e);
        } finally {
            SQLUtils.closeResultSet(rs);
        }
        return null;
    }
    
    /**
     * Get Integer value from ResultSet and close it.
     * 
     * @param rs
     *            ResultSet to get value from.
     * @return Integer value.
     */
    public static Long getLongAndCloseResultSet(ResultSet rs) {
        try {
            if (rs.next()) {
                long value = rs.getLong(1);
                return rs.wasNull() ? null : value;
            }
        } catch (SQLException e) {
            logger.error("Could not get Long from ResultSet", e);
        } finally {
            SQLUtils.closeResultSet(rs);
        }
        return null;
    }
    
    /**
     * Close Statements.
     * 
     * @param statement
     *            Statements to close.
     */
    public static void closeStatements(Statement... statements) {
        for (Statement statement : statements) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("Could not close Statement", e);
                }
            }
        }
    }

    /**
     * Close ResultSet.
     * 
     * @param rs
     *            ResultSet to close.
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("Could not close ResultSet", e);
            }
        }
    }

    /**
     * Execute batches for PreparedStatements.
     * 
     * @param statements
     *            List of PreparedStatements.
     * @throws SQLException
     *             on database error.
     */
    public static void executeBatches(PreparedStatement... statements) throws SQLException {
        for (PreparedStatement statement : statements) {
            statement.executeBatch();
        }
    }
}
