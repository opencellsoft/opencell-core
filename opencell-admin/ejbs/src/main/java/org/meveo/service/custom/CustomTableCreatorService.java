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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.hibernate.Session;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.slf4j.Logger;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.change.AddColumnConfig;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.AddDefaultValueChange;
import liquibase.change.core.AddNotNullConstraintChange;
import liquibase.change.core.CreateSequenceChange;
import liquibase.change.core.CreateTableChange;
import liquibase.change.core.DropColumnChange;
import liquibase.change.core.DropDefaultValueChange;
import liquibase.change.core.DropNotNullConstraintChange;
import liquibase.change.core.DropTableChange;
import liquibase.change.core.ModifyDataTypeChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.statement.SequenceNextValueFunction;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CustomTableCreatorService implements Serializable {

    private static final long serialVersionUID = -5858023657669249422L;

    @PersistenceUnit(unitName = "MeveoAdmin")
    private EntityManagerFactory emf;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private EntityManagerProvider entityManagerProvider;

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

                Database database;
                try {
                    database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

                    Liquibase liquibase = new liquibase.Liquibase(dbLog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update(new Contexts(), new LabelExpression());

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

        if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
            column.setType("datetime");
        } else if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
            column.setType("numeric(23, 12)");
            if (cft.getDefaultValue() != null) {
                column.setDefaultValueNumeric(cft.getDefaultValue());
            }
        } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
            column.setType("bigInt");
            if (cft.getDefaultValue() != null) {
                column.setDefaultValueNumeric(cft.getDefaultValue());
            }

        } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST) {
            column.setType("varchar(" + (cft.getMaxValue() == null ? CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING : cft.getMaxValue()) + ")");

            if (cft.getDefaultValue() != null) {
                column.setDefaultValue(cft.getDefaultValue());
            }
        }

        addColumnChange.addColumn(column);

        changeSet.addChange(addColumnChange);
        dbLog.addChangeSet(changeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                Database database;
                try {
                    database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

                    Liquibase liquibase = new liquibase.Liquibase(dbLog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update(new Contexts(), new LabelExpression());

                } catch (Exception e) {
                    log.error("Failed to add a field {} to a custom table {}", dbTableName, dbFieldname, e);
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
    public void updateField(String dbTableName, CustomFieldTemplate cft) {

        String dbFieldname = cft.getDbFieldname();

        DatabaseChangeLog dbLog = new DatabaseChangeLog("path");

        // Drop not null constraint and add again if needed - a better way would be to check if valueRequired field value was changed
        ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_RNN_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
        changeSet.setFailOnError(false);

        DropNotNullConstraintChange dropNotNullChange = new DropNotNullConstraintChange();
        dropNotNullChange.setTableName(dbTableName);
        dropNotNullChange.setColumnName(dbFieldname);

        if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
            dropNotNullChange.setColumnDataType("datetime");
        } else if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
            dropNotNullChange.setColumnDataType("numeric(23, 12)");
        } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
            dropNotNullChange.setColumnDataType("bigInt");
        } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST) {
            dropNotNullChange.setColumnDataType("varchar(" + (cft.getMaxValue() == null ? CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING : cft.getMaxValue()) + ")");
        }

        changeSet.addChange(dropNotNullChange);
        dbLog.addChangeSet(changeSet);

        // Add not null constraint if needed
        if (cft.isValueRequired()) {
            changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_ANN_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
            AddNotNullConstraintChange addNotNullChange = new AddNotNullConstraintChange();

            addNotNullChange.setTableName(dbTableName);
            addNotNullChange.setColumnName(dbFieldname);

            if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
                addNotNullChange.setColumnDataType("datetime");
            } else if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                addNotNullChange.setColumnDataType("numeric(23, 12)");
            } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
                addNotNullChange.setColumnDataType("bigInt");
            } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST) {
                addNotNullChange.setColumnDataType("varchar(" + (cft.getMaxValue() == null ? CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING : cft.getMaxValue()) + ")");
            }

            changeSet.addChange(addNotNullChange);
            dbLog.addChangeSet(changeSet);

        }

        // Drop default value and add it again if needed - a better way would be to check if defaultValue field value was changed
        // Default value does not apply to date type field
        if (cft.getFieldType() != CustomFieldTypeEnum.DATE) {
            changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_RD_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
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
            if (cft.getDefaultValue() != null) {
                changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_AD_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
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
        if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST) {
            changeSet = new ChangeSet(dbTableName + "_CT_" + dbFieldname + "_M_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
            changeSet.setFailOnError(false);

            ModifyDataTypeChange modifyDataTypeChange = new ModifyDataTypeChange();
            modifyDataTypeChange.setTableName(dbTableName);
            modifyDataTypeChange.setColumnName(dbFieldname);
            modifyDataTypeChange.setNewDataType("varchar(" + (cft.getMaxValue() == null ? CustomFieldTemplate.DEFAULT_MAX_LENGTH_STRING : cft.getMaxValue()) + ")");

            changeSet.addChange(modifyDataTypeChange);
            dbLog.addChangeSet(changeSet);
        }

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                Database database;
                try {
                    database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

                    Liquibase liquibase = new liquibase.Liquibase(dbLog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update(new Contexts(), new LabelExpression());

                } catch (Exception e) {
                    log.error("Failed to update a field {} in a custom table {}", dbTableName, dbFieldname, e);
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

                Database database;
                try {
                    database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

                    Liquibase liquibase = new liquibase.Liquibase(dbLog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update(new Contexts(), new LabelExpression());

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

        // Remove table
        ChangeSet changeSet = new ChangeSet(dbTableName + "_CT_R_" + System.currentTimeMillis(), "Opencell", false, false, "opencell", "", "", dbLog);
        changeSet.setFailOnError(false);

        DropTableChange dropTableChange = new DropTableChange();
        dropTableChange.setTableName(dbTableName);
        dropTableChange.setCascadeConstraints(true);

        changeSet.addChange(dropTableChange);
        dbLog.addChangeSet(changeSet);

        EntityManager em = entityManagerProvider.getEntityManagerWoutJoinedTransactions();

        Session hibernateSession = em.unwrap(Session.class);

        hibernateSession.doWork(new org.hibernate.jdbc.Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                Database database;
                try {
                    database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

                    Liquibase liquibase = new liquibase.Liquibase(dbLog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update(new Contexts(), new LabelExpression());

                } catch (Exception e) {
                    log.error("Failed to drop a custom table {}", dbTableName, e);
                    throw new SQLException(e);
                }
            }
        });
    }
}