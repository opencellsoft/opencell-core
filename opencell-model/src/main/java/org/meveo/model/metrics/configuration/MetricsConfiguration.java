package org.meveo.model.metrics.configuration;

import java.util.Objects;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
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
@Table(name = "meveo_metrics_config", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_metrics_config_seq") })
@NamedQuery(name = "MetricConfiguration.findAllForCache", query = "SELECT m FROM MetricsConfiguration m")
public class MetricsConfiguration extends BusinessEntity {
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
    @Column(name = "metrics_type")
    private String metricsType;

    /**
     * the full path after opencell
     */
    @Column(name = "metrics_unit")
    private String metricsUnit;

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

    public String getMetricsType() {
        return metricsType;
    }

    public void setMetricsType(String metricType) {
        this.metricsType = metricType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        MetricsConfiguration that = (MetricsConfiguration) o;
        return Objects.equals(fullPath, that.fullPath) && Objects.equals(method, that.method)
                && Objects.equals(metricsType, that.metricsType) && Objects.equals(metricsUnit, that.metricsUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fullPath, method, metricsType, metricsUnit);
    }

    @Override
    public String toString() {
        return "MetricConfiguration{" + "fullPath='" + fullPath + '\'' + ", method='" + method + '\'' + ", metricType='" + metricsType + '\'' + ", code='" + code + '\''
                + ", previousCode='" + previousCode + '\'' + ", description='" + description + '\'' + ", appendGeneratedCode=" + appendGeneratedCode + ", auditable=" + auditable
                + ", id=" + id + ", version=" + version + '}';
    }

    public String getMetricsUnit() {
        return metricsUnit;
    }

    public void setMetricsUnit(String metricUnit) {
        this.metricsUnit = metricUnit;
    }
}
