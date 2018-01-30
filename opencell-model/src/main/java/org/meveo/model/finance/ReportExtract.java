package org.meveo.model.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.scripts.ScriptInstance;

/**
 * @author Edward P. Legaspi
 * @created 29 Jan 2018
 **/
@Entity
@Table(name = "dwh_report_extract", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dwh_report_extract_seq"), })
public class ReportExtract extends BusinessEntity {

    private static final long serialVersionUID = 879663935811446632L;

    @NotNull
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "category", length = 50)
    private String category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "script_type", length = 10, nullable = false)
    private ReportExtractScriptTypeEnum scriptType;

    @Column(name = "filename_format", length = 100)
    private String filenameFormat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    @Column(name = "sql_query", length = 2000)
    @Size(max = 2000)
    private String sqlQuery;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

}
