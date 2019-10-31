package org.meveo.model.metric.configuration;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Opencell monitoring configuration by metrics.
 *
 * Declare a configuration for an url to monitor by micro-profile metrics
 *
 * @author Mohamed STITANE
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "meveo_metric_config", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_metric_config_seq") })
@NamedQueries({ @NamedQuery(name = "MetricConfiguration.findAllForCache", query = "SELECT m FROM MetricConfiguration m", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }) })
public class MetricConfiguration extends BusinessEntity {
    /**
     * the full path after opencell
     */
    @Column(name = "full_path")
    private String fullPath;

    /**
     * the full path after opencell
     */
    @Column(name = "method")
    private String method;

    /**
     * the full path after opencell
     */
    @Column(name = "metric_type")
    private String metricType;

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    @Override
    public String toString() {
        return "MetricConfiguration{" + "fullPath='" + fullPath + '\'' + ", method='" + method + '\'' + ", metricType='" + metricType + '\'' + ", code='" + code + '\''
                + ", previousCode='" + previousCode + '\'' + ", description='" + description + '\'' + ", appendGeneratedCode=" + appendGeneratedCode + ", auditable=" + auditable
                + ", id=" + id + ", version=" + version + '}';
    }
}
