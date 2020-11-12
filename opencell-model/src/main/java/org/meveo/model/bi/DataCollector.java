package org.meveo.model.bi;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Column(name = "sql_query", columnDefinition = "text")
    private String sqlQuery;

    @Column(name = "custom_table_code")
    private String customTableCode;

    @Type(type = "json")
    @Column(name = "aliases", columnDefinition = "text")
    private Map<String, String> aliases = new HashMap<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_run_date", columnDefinition = "text")
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
}