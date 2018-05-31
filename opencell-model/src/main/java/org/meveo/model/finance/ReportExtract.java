package org.meveo.model.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.scripts.ScriptInstance;

/**
 * ReportExtract can either be an SQL or a Java Script. In case of SQL, we need to provide an sql that returns a list of records. On the other hand if it is a Java script, we can
 * also execute queries by calling the services.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
@Entity
@CustomFieldEntity(cftCodePrefix = "REPORT")
@Table(name = "dwh_report_extract", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dwh_report_extract_seq"), })
@NamedQueries(@NamedQuery(name = "ReportExtract.listIds", query = "select re.id from ReportExtract re where re.disabled=false"))
public class ReportExtract extends EnableBusinessEntity {

    private static final long serialVersionUID = 879663935811446632L;

    @Column(name = "category", length = 50)
    private String category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "script_type", length = 10, nullable = false)
    private ReportExtractScriptTypeEnum scriptType;

    @NotNull
    @Column(name = "filename_format", length = 100, nullable = false)
    private String filenameFormat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    @Column(name = "sql_query", columnDefinition = "TEXT")
    private String sqlQuery;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dwh_report_extract_params")
    private Map<String, String> params = new HashMap<>();

    private transient Date startDate;
    private transient Date endDate;

    public String getFilenameFormat() {
        return filenameFormat;
    }

    public void setFilenameFormat(String filenameFormat) {
        this.filenameFormat = filenameFormat;
    }

    public ReportExtractScriptTypeEnum getScriptType() {
        return scriptType;
    }

    public void setScriptType(ReportExtractScriptTypeEnum scriptType) {
        this.scriptType = scriptType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}
