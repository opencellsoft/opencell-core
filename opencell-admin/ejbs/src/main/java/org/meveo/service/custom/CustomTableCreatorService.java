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

package org.meveo.service.custom;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.slf4j.Logger;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.change.AddColumnConfig;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.AddDefaultValueChange;
import liquibase.change.core.AddForeignKeyConstraintChange;
import liquibase.change.core.AddNotNullConstraintChange;
import liquibase.change.core.AddUniqueConstraintChange;
import liquibase.change.core.CreateSequenceChange;
import liquibase.change.core.CreateTableChange;
import liquibase.change.core.DropColumnChange;
import liquibase.change.core.DropDefaultValueChange;
import liquibase.change.core.DropForeignKeyConstraintChange;
import liquibase.change.core.DropNotNullConstraintChange;
import liquibase.change.core.DropSequenceChange;
import liquibase.change.core.DropTableChange;
import liquibase.change.core.DropUniqueConstraintChange;
import liquibase.change.core.ModifyDataTypeChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.statement.SequenceNextValueFunction;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CustomTableCreatorService implements Serializable {

    private static final long serialVersionUID = -5858023657669249422L;

    @Inject
    private EntityManagerProvider entityManagerProvider;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private Logger log;

    /**
     * Create a table with a single 'id' field. Value is autoincremented for mysql or taken from sequence for Postgress databases.
     *
     * @param dbTableName DB table name
     */
    public void createTable(String dbTableName) {

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");

        // Changeset for Postgress
        ChangeSet pgChangeSet = new ChangeSet(dbTableName + "_CT_CP_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "postgresql", dbLog);

        CreateSequenceChange createPgSequence = new CreateSequenceChange();
        createPgSequence.setSequenceName(dbTableName + "_seq");
        createPgSequence.setStartValue(BigInteger.ONE);
        pgChangeSet.addChange(createPgSequence);

        CreateTableChange createPgTableChange = new CreateTableChange();
        createPgTableChange.setTableName(dbTableName);

        ColumnConfig pgIdColumn = new ColumnConfig();
        pgIdColumn.setName("id");
        pgIdColumn.setType("bigInt");
        pgIdColumn.setDefaultValueSequenceNext(new SequenceNextValueFunction(dbTableName + "_seq"));

        ConstraintsConfig idConstraints = new ConstraintsConfig();
        idConstraints.setNullable(false);
        idConstraints.setPrimaryKey(true);
        idConstraints.setPrimaryKeyName(dbTableName + "PK");

        pgIdColumn.setConstraints(idConstraints);
        createPgTableChange.addColumn(pgIdColumn);

        pgChangeSet.addChange(createPgTableChange);
        dbLog.addChangeSet(pgChangeSet);

        // Changeset for mysql
        ChangeSet mysqlChangeSet = new ChangeSet(dbTableName + "_CT_CM_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "mysql", dbLog);

        CreateTableChange createMsTableChange = new CreateTableChange();
        createMsTableChange.setTableName(dbTableName);

        ColumnConfig msIdcolumn = new ColumnConfig();
        msIdcolumn.setName("id");
        msIdcolumn.setType("bigInt");
        msIdcolumn.setAutoIncrement(true);

        msIdcolumn.setConstraints(idConstraints);
        createMsTableChange.addColumn(msIdcolumn);

        mysqlChangeSet.addChange(createMsTableChange);
        dbLog.addChangeSet(mysqlChangeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {
                try {
                    liquibaseUpdate(dbLog, connection);
                } catch (Exception e) {
                    log.error("Failed to create a custom table {}", dbTableName, e);
                    throw new SQLException(e);
                }
            }
        });
    }

    /**
     * Add a field to a db table. Creates a liquibase changeset to add a field to a table and executes it
     *
     * @param dbTableName DB Table name
     * @param cft Field definition
     */
    public void addField(String dbTableName, CustomFieldTemplate cft) {

        String dbFieldname = cft.getDbFieldname();

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");

        ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_AF_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);

        AddColumnChange addColumnChange = new AddColumnChange();
        addColumnChange.setTableName(dbTableName);
        AddColumnConfig column = new AddColumnConfig();
        column.setName(dbFieldname);

        if (cft.isValueRequired()) {
            ConstraintsConfig constraints = new ConstraintsConfig();
            constraints.setNullable(false);
            column.setConstraints(constraints);
        }
        setColumnType(cft, column);

        if (cft.getDefaultValue() != null) {
            column.setDefaultValue(cft.getDefaultValue());
        }

        addColumnChange.addColumn(column);

        changeSet.addChange(addColumnChange);
        dbLog.addChangeSet(changeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(connection -> {

            try {
                liquibaseUpdate(dbLog, connection);
            } catch (Exception e) {
                log.error("Failed to add a field {} to a custom table {}", dbTableName, dbFieldname, e);
                throw new SQLException(e);
            }
        });
    }

    void setColumnType(CustomFieldTemplate cft, AddColumnConfig column) {
        column.setType(cft.getFieldType().getDataType().replaceAll("%length", cft.getMaxValueOrDefault(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING).toString()).replaceAll("default false", ""));
    }

    /**
     * Add a field to a db table. Creates a liquibase changeset to add a field to a table and executes it
     *
     * @param dbTableName DB Table name
     * @param cft Field definition
     * @param oldCft Old custom field definition
     */
    public void updateField(String dbTableName, CustomFieldTemplate cft, CustomFieldTemplate oldCft) {

        String dbFieldname = cft.getDbFieldname();

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");

        // Drop not null constraint and add again if needed - a better way would be to check if valueRequired field value was changed
        if (oldCft.isValueRequired() && !cft.isValueRequired()) {
            ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_RNN_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
            changeSet.setFailOnError(false);

            DropNotNullConstraintChange dropNotNullChange = new DropNotNullConstraintChange();
            dropNotNullChange.setTableName(dbTableName);
            dropNotNullChange.setColumnName(dbFieldname);
            dropNotNullChange.setColumnDataType(cft.getFieldType().getDataType().replaceAll("%length", cft.getMaxValueOrDefault(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING).toString()));

            changeSet.addChange(dropNotNullChange);
            dbLog.addChangeSet(changeSet);

            // Add not null constraint if needed
        } else if (!oldCft.isValueRequired() && cft.isValueRequired()) {
            ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_ANN_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
            AddNotNullConstraintChange addNotNullChange = new AddNotNullConstraintChange();

            addNotNullChange.setTableName(dbTableName);
            addNotNullChange.setColumnName(dbFieldname);
            addNotNullChange.setColumnDataType(cft.getFieldType().getDataType().replaceAll("%length", cft.getMaxValueOrDefault(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING).toString()));

            changeSet.addChange(addNotNullChange);
            dbLog.addChangeSet(changeSet);
        }

        // Drop default value and add it again if needed
        // Default value does not apply to date type field
        if (cft.getFieldType() != CustomFieldTypeEnum.DATE) {

            // Default value was removed or has changed
            if ((oldCft.getDefaultValue() != null && cft.getDefaultValue() == null)) {

                ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_RD_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
                changeSet.setFailOnError(false);

                DropDefaultValueChange dropDefaultValueChange = new DropDefaultValueChange();
                dropDefaultValueChange.setTableName(dbTableName);
                dropDefaultValueChange.setColumnName(dbFieldname);

                if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                    dropDefaultValueChange.setColumnDataType("numeric(23, 12)");
                } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                    dropDefaultValueChange.setColumnDataType("bigInt");
                } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST) {
                    dropDefaultValueChange.setColumnDataType("varchar(" + (cft.getMaxValue() == null ? CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING : cft.getMaxValue()) + ")");
                }

                changeSet.addChange(dropDefaultValueChange);
                dbLog.addChangeSet(changeSet);

                // Add default value if needed
            } else if (cft.getDefaultValue() != null && !cft.getDefaultValue().equals(oldCft.getDefaultValue())) {
                ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_AD_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
                AddDefaultValueChange addDefaultValueChange = new AddDefaultValueChange();

                addDefaultValueChange.setTableName(dbTableName);
                addDefaultValueChange.setColumnName(dbFieldname);

                if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                    addDefaultValueChange.setColumnDataType("numeric(23, 12)");
                    addDefaultValueChange.setDefaultValueNumeric(cft.getDefaultValue());
                } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                    addDefaultValueChange.setColumnDataType("bigInt");
                    addDefaultValueChange.setDefaultValueNumeric(cft.getDefaultValue());
                } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST) {
                    addDefaultValueChange.setColumnDataType("varchar(" + (cft.getMaxValue() == null ? CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING : cft.getMaxValue()) + ")");
                    addDefaultValueChange.setDefaultValue(cft.getDefaultValue());
                }

                changeSet.addChange(addDefaultValueChange);
                dbLog.addChangeSet(changeSet);

            }
        }

        // Update field length for String type fields.
        if ((cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST)
                && !oldCft.getMaxValueOrDefault(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING).equals(cft.getMaxValueOrDefault(CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING))) {
            ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_M_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
            changeSet.setFailOnError(false);

            ModifyDataTypeChange modifyDataTypeChange = new ModifyDataTypeChange();
            modifyDataTypeChange.setTableName(dbTableName);
            modifyDataTypeChange.setColumnName(dbFieldname);
            modifyDataTypeChange.setNewDataType("varchar(" + (cft.getMaxValue() == null ? CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING : cft.getMaxValue()) + ")");

            changeSet.addChange(modifyDataTypeChange);
            dbLog.addChangeSet(changeSet);
        }

        if (!dbLog.getChangeSets().isEmpty()) {
            EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

            Session hibernateSession = em.unwrap(Session.class);

            hibernateSession.doWork(connection -> {

                try {
                    liquibaseUpdate(dbLog, connection);
                } catch (Exception e) {
                    log.error("Failed to update a field {} in a custom table {}", dbTableName, dbFieldname, e);
                    throw new SQLException(e);
                }
            });
        }
    }

    /**
     * Remove a field from a table
     *
     * @param dbTableName Db table name to remove from
     * @param cft Field definition
     */
    public void removeField(String dbTableName, CustomFieldTemplate cft) {

        String dbFieldname = cft.getDbFieldname();

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");

        // Remove field
        ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_RF_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
        changeSet.setFailOnError(false);

        DropColumnChange dropColumnChange = new DropColumnChange();
        dropColumnChange.setTableName(dbTableName);
        dropColumnChange.setColumnName(dbFieldname);

        changeSet.addChange(dropColumnChange);
        dbLog.addChangeSet(changeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {
                try {
                    liquibaseUpdate(dbLog, connection);
                } catch (Exception e) {
                    log.error("Failed to remove a field {} to a custom table {}", dbTableName, dbFieldname, e);
                    throw new SQLException(e);
                }
            }
        });
    }

    /**
     * Remove a table from DB
     *
     * @param dbTableName Db table name to remove from
     */
    public void removeTable(String dbTableName) {

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");

        // Remove table changeset
        ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_R_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
        changeSet.setFailOnError(false);

        DropTableChange dropTableChange = new DropTableChange();
        dropTableChange.setTableName(dbTableName);
        dropTableChange.setCascadeConstraints(true);

        changeSet.addChange(dropTableChange);

        dbLog.addChangeSet(changeSet);

        // Changeset for Postgress
        ChangeSet pgChangeSet = new ChangeSet(dbTableName + "_CT_CRP_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "postgresql", dbLog);
        pgChangeSet.setFailOnError(false);

        DropSequenceChange dropPgSequence = new DropSequenceChange();
        dropPgSequence.setSequenceName(dbTableName + "_seq");
        pgChangeSet.addChange(dropPgSequence);

        dbLog.addChangeSet(pgChangeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try {
                    liquibaseUpdate(dbLog, connection);
                } catch (Exception e) {
                    log.error("Failed to drop a custom table {}", dbTableName, e);
                    throw new SQLException(e);
                }
            }
        });
    }

    /**
     * Remove a field from a table
     *
     * @param dbTableName Db table name to remove from
     * @param cft Field definition
     */
    public void dropUniqueConstraint(String dbTableName, String constraintName) {

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");

        // Remove field
        ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + constraintName + "_DC_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
        changeSet.setFailOnError(true);

        DropUniqueConstraintChange dropUniqueConstraintStatement = new DropUniqueConstraintChange();
        dropUniqueConstraintStatement.setTableName(dbTableName);
        dropUniqueConstraintStatement.setConstraintName(constraintName);

        changeSet.addChange(dropUniqueConstraintStatement);
        dbLog.addChangeSet(changeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try {
                    liquibaseUpdate(dbLog, connection);
                } catch (Exception e) {
                    log.error("Failed to remove a constraint {} to a custom table {}", constraintName, dbTableName, e);
                    throw new SQLException(e);
                }
            }
        });
    }

    /**
     * Add a unique constraint to table
     *
     * @param dbTableName Db table name to add constraint
     * @param columnNames Field of the unique constraint
     */
    public String addUniqueConstraint(String dbTableName, String columnNames) {

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");
        String constraintName = extractUniqueConstraintName(dbTableName);
        // Remove field
        ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + constraintName + "_DC_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
        changeSet.setFailOnError(true);

        AddUniqueConstraintChange addUniqueConstraintStatement = new AddUniqueConstraintChange();
        addUniqueConstraintStatement.setTableName(dbTableName);
        addUniqueConstraintStatement.setConstraintName(constraintName);
        addUniqueConstraintStatement.setColumnNames(columnNames);

        changeSet.addChange(addUniqueConstraintStatement);
        dbLog.addChangeSet(changeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {
                try {
                    liquibaseUpdate(dbLog, connection);
                } catch (Exception e) {
                    log.error("Failed to add a constraint {} to a custom table {}", constraintName, dbTableName, e);
                    throw new SQLException(e);
                }
            }
        });
        return constraintName;
    }

    /**
     * Add a foreingKey constraint to table
     *
     * @param baseTable Db table name to add constraint
     * @param referencedTable Field of the unique constraint
     */
    public String addForeingKeyConstraint(String baseTable, String baseColumn, String referencedTable, String referencedColumn) {

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");
        String constraintName = extractFKConstraintName(baseTable, baseColumn, referencedTable, referencedColumn);

        ChangeSet changeSet = new ChangeSet(baseTable + "_CT_" + constraintName + "_CFK_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
        changeSet.setFailOnError(true);

        AddForeignKeyConstraintChange addFKConstraintStatement = new AddForeignKeyConstraintChange();
        addFKConstraintStatement.setConstraintName(constraintName);
        addFKConstraintStatement.setBaseTableName(baseTable);
        addFKConstraintStatement.setBaseColumnNames(baseColumn);
        addFKConstraintStatement.setReferencedTableName(referencedTable.toLowerCase());
        addFKConstraintStatement.setReferencedColumnNames(referencedColumn);

        changeSet.addChange(addFKConstraintStatement);
        dbLog.addChangeSet(changeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {
                try {
                    liquibaseUpdate(dbLog, connection);
                } catch (Exception e) {
                    log.error("Failed to add a constraint {} to a custom table {}", constraintName, baseTable, e);
                    throw new SQLException(e);
                }
            }
        });
        return constraintName;
    }

    /**
     * Remove a ForeingKay from a table
     *
     * @param dbTableName Db table name to remove from
     * @param cft Field definition
     */
    public void dropForeingKeyConstraint(String baseTable, String baseColumn, String referencedTable, String referencedColumn) {

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");

        String constraintName = extractFKConstraintName(baseTable, baseColumn, referencedTable, referencedColumn);

        ChangeSet changeSet = new ChangeSet(baseTable + "_CT_" + constraintName + "_DFK_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
        changeSet.setFailOnError(true);

        DropForeignKeyConstraintChange dropForeignKeyConstraintChange = new DropForeignKeyConstraintChange();
        dropForeignKeyConstraintChange.setBaseTableName(baseTable);
        dropForeignKeyConstraintChange.setConstraintName(constraintName);

        changeSet.addChange(dropForeignKeyConstraintChange);
        dbLog.addChangeSet(changeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                try {
                    liquibaseUpdate(dbLog, connection);
                } catch (Exception e) {
                    log.error("Failed to remove a constraint {} to a custom table {}", constraintName, baseTable, e);
                    throw new SQLException(e);
                }
            }
        });
    }

    public String extractUniqueConstraintName(String dbTableName) {
        String constraintName = "CT_UniqueConstraint_" + dbTableName;
        return constraintName;
    }

    public String extractFKConstraintName(String baseTable, String baseColumn, String referencedTable, String referencedColumn) {
        String constraintName = "CT_FOREING_KEY_CONSTRAINT_" + baseTable + "__" + baseColumn + "__" + referencedTable + "__" + referencedColumn;
        return constraintName;
    }

    private void liquibaseUpdate(DatabaseChangeLog dbLog, Connection connection) throws DatabaseException, LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        String currentproviderCode = currentUserProvider.getCurrentUserProviderCode();
        if (currentproviderCode != null) {
            database.setDefaultSchemaName(entityManagerProvider.convertToSchemaName(currentproviderCode));
        }

        Liquibase liquibase = new liquibase.Liquibase(dbLog, new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }
}