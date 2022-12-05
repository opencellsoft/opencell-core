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

package org.meveo.model.finance;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.annotation.ImageType;
import org.meveo.model.catalog.IImageUpload;
import org.meveo.model.scripts.ScriptInstance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ReportExtract can either be an SQL or a Java Script. In case of SQL, we need to provide an sql that returns a list of records. On the other hand if it is a Java script, we can
 * also execute queries by calling the services.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "ReportExtract")
@Table(name = "dwh_report_extract", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dwh_report_extract_seq"), })
@NamedQueries(@NamedQuery(name = "ReportExtract.listIds", query = "select re.id from ReportExtract re where re.disabled=false"))
public class ReportExtract extends EnableBusinessCFEntity implements IImageUpload {

    private static final long serialVersionUID = 879663935811446632L;

    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "output_dir", length = 100)
    private String outputDir;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "script_type", length = 10, nullable = false)
    private ReportExtractScriptTypeEnum scriptType;

    @NotNull
    @Column(name = "filename_format", length = 100, nullable = false)
    private String filenameFormat;

    @NotNull
    @Column(name = "file_separator", length = 1, nullable = false)
    private String fileSeparator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "sql_query")
    private String sqlQuery;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dwh_report_extract_params")
    private Map<String, String> params = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", length = 10)
    private ReportExtractResultTypeEnum reportExtractResultType = ReportExtractResultTypeEnum.CSV;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "style")
    private String style;

    @ImageType
    @Column(name = "image_path", length = 100)
    @Size(max = 100)
    private String imagePath;

    @OneToMany(mappedBy = "reportExtract", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ReportExtractExecutionResult> executionResults = new ArrayList<>();

    private transient Date startDate;
    private transient Date endDate;

    @Column(name = "custom_table_code", length = 100)
    private String customTableCode;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "accumulate")
    private boolean accumulate;

    @Column(name = "decimal_separator", length = 1)
    private String decimalSeparator;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "generate_empty_report")
    private boolean generateEmptyReport;

    @Column(name = "maximum_line")
    private Long maximumLine;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "include_headers")
    private boolean includeHeaders;

    public String getFilenameFormat() {
        return filenameFormat;
    }

    public void setFilenameFormat(String filenameFormat) {
        this.filenameFormat = filenameFormat;
    }

    public String getFileSeparator() {
        return fileSeparator;
    }
    
    public void setFileSeparator(String fileSeparator) {
        this.fileSeparator = fileSeparator;
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
    
    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
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

    public ReportExtractResultTypeEnum getReportExtractResultType() {
        return reportExtractResultType;
    }

    public void setReportExtractResultType(ReportExtractResultTypeEnum reportExtractResultType) {
        this.reportExtractResultType = reportExtractResultType;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<ReportExtractExecutionResult> getExecutionResults() {
        return executionResults;
    }

    public void setExecutionResults(List<ReportExtractExecutionResult> executionResults) {
        this.executionResults = executionResults;
    }

    public String getCustomTableCode() {
        return customTableCode;
    }

    public void setCustomTableCode(String customTableCode) {
        this.customTableCode = customTableCode;
    }

    public boolean isAccumulate() {
        return accumulate;
    }

    public void setAccumulate(boolean accumulate) {
        this.accumulate = accumulate;
    }

    public boolean isGenerateEmptyReport() {
        return generateEmptyReport;
    }

    public void setGenerateEmptyReport(boolean generateEmptyReport) {
        this.generateEmptyReport = generateEmptyReport;
    }

    public Long getMaximumLine() {
        return maximumLine == null ? 0L : maximumLine;
    }

    public void setMaximumLine(Long maximumLine) {
        this.maximumLine = maximumLine;
    }

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }
}
