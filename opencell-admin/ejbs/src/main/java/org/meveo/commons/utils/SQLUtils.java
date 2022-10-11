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

import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL Utility methods.
 * 
 * @author Donatas Remeika
 * 
 */
public final class SQLUtils {

    /**
     * No need to instantiate.
     */
    private SQLUtils() {

    }
    
    /** logger.*/
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Get String value from ResultSet and close it.
     * 
     * @param rs
     *            ResultSet to get value from.
     * @return String value.
     * @throws SQLException sql exception.
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
     * @param types class
     * @return types instance of Class.
     * @throws SQLException sql exception
     */
    @SuppressWarnings("rawtypes")
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
                    } else if (java.util.Date.class == type) {
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
     * @param rs ResultSet to get value from.
     * @return Integer value.
     */
    public static Integer getIntegerAndCloseResultSet(ResultSet rs) {
        try {
            if (rs.next()) {
                int value = rs.getInt(1);
                return rs.wasNull() ? null : value;
            }
        } catch (SQLException e) {
            log.error("Could not get Integer from ResultSet", e);
        } finally {
            SQLUtils.closeResultSet(rs);
        }
        return null;
    }

    /**
     * Get Integer value from ResultSet and close it.
     * 
     * @param rs ResultSet to get value from.
     * @return Integer value.
     */
    public static Long getLongAndCloseResultSet(ResultSet rs) {
        try {
            if (rs.next()) {
                long value = rs.getLong(1);
                return rs.wasNull() ? null : value;
            }
        } catch (SQLException e) {
            log.error("Could not get Long from ResultSet", e);
        } finally {
            SQLUtils.closeResultSet(rs);
        }
        return null;
    }

    /**
     * Close Statements.
     * 
     * @param statements
     *            Statements to close.
     */
    public static void closeStatements(Statement... statements) {
        for (Statement statement : statements) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error("Could not close Statement", e);
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
                log.error("Could not close ResultSet", e);
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
