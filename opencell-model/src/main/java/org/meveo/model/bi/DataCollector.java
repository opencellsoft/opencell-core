package org.meveo.model.bi;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.SqlTypes;
import org.meveo.model.BusinessEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "bi_data_collector")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @Parameter(name = "sequence_name", value = "bi_data_collector_seq"), })
@NamedQueries({
        @NamedQuery(name ="DataCollector.dataCollectorsByDate", query = "SELECT dc FROM DataCollector dc WHERE dc.auditable.created <= :to"),
        @NamedQuery(name ="DataCollector.dataCollectorsBetween", query = "SELECT dc FROM DataCollector dc WHERE dc.auditable.created BETWEEN :from AND :to"),
        @NamedQuery(name ="DataCollector.updateLastRunDate", query = "UPDATE DataCollector SET lastRunDate = :lastDateRun WHERE code IN :codes")
})
public class DataCollector extends BusinessEntity {

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "sql_query")
    private String sqlQuery;

    @Column(name = "custom_table_code")
    private String customTableCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "aliases", columnDefinition = "jsonb")
    private Map<String, String> aliases = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parameters", columnDefinition = "jsonb")
    private Map<String, String> parameters = new HashMap<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_run_date")
    private Date lastRunDate;

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getCustomTableCode() {
        return customTableCode;
    }

    public void setCustomTableCode(String customTableCode) {
        this.customTableCode = customTableCode;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }

    public void setAliases(Map<String, String> aliases) {
        this.aliases = aliases;
    }

    public Date getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(Date lastRunDate) {
        this.lastRunDate = lastRunDate;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}