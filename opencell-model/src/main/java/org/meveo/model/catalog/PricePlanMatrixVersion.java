package org.meveo.model.catalog;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 * @author Tarik FA.
 * @version 10.0
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_price_plan_version", uniqueConstraints = @UniqueConstraint(columnNames = { "ppm_id", "current_version" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_version_seq"), })
@NamedQueries({
        @NamedQuery(name = "PricePlanMatrixVersion.findByPricePlanAndVersion", query = "select p from PricePlanMatrixVersion p where p.currentVersion=:currentVersion and lower(p.pricePlanMatrix.code)=:pricePlanMatrixCode"),
        @NamedQuery(name = "PricePlanMatrixVersion.lastVersion", query = "select max(p.currentVersion) from PricePlanMatrixVersion p where p.pricePlanMatrix.code=:pricePlanMatrixCode"),
        @NamedQuery(name = "PricePlanMatrixVersion.findByCode", query = "select p from PricePlanMatrixVersion p left join p.pricePlanMatrix pp where pp.code=:code order by p.currentVersion desc ")
})
public class PricePlanMatrixVersion extends AuditableEntity {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private VersionStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "ppm_id")
    @NotNull
    private PricePlanMatrix pricePlanMatrix;

    @Column(name = "current_version", nullable = false)
    @Min(1)
    private int currentVersion;

    @Column(name = "label")
    private String label;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date", nullable = false)
    @NotNull
    private Date statusDate;

    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod validity;

    @Type(type = "numeric_boolean")
    @Column(name = "is_matrix")
    private Boolean isMatrix;

    @Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal priceWithoutTax;

    @OneToMany(mappedBy = "pricePlanMatrixVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricePlanMatrixLine> lines;

    @OneToMany(mappedBy = "pricePlanMatrixVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricePlanMatrixColumn> columns;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name ="default_line_id")
    private PricePlanMatrixLine defaultLine;
    /**
     * @return the status
     */
    public VersionStatusEnum getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(VersionStatusEnum status) {
        this.status = status;
    }
    /**
     * @return the statusDate
     */
    public Date getStatusDate() {
        return statusDate;
    }
    /**
     * @param statusDate the statusDate to set
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * @return the pricetWithoutTax
     */
    public BigDecimal getPriceWithoutTax() {
        return priceWithoutTax;
    }
    /**
     * @param priceWithoutTax the pricetWithoutTax to set
     */
    public void setPriceWithoutTax(BigDecimal priceWithoutTax) {
        this.priceWithoutTax = priceWithoutTax;
    }

    public PricePlanMatrix getPricePlanMatrix() {
        return pricePlanMatrix;
    }

    public void setPricePlanMatrix(PricePlanMatrix pricePlanMatrix) {
        this.pricePlanMatrix = pricePlanMatrix;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    public DatePeriod getValidity() {
        return validity;
    }

    public void setValidity(DatePeriod validity) {
        this.validity = validity;
    }

    public Boolean getMatrix() {
        return isMatrix;
    }

    public void setMatrix(Boolean matrix) {
        isMatrix = matrix;
    }

    public List<PricePlanMatrixLine> getLines() {
        return lines;
    }

    public void setLines(List<PricePlanMatrixLine> lines) {
        this.lines = lines;
    }

    public List<PricePlanMatrixColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<PricePlanMatrixColumn> columns) {
        this.columns = columns;
    }

    public PricePlanMatrixLine getDefaultLine() {
        return defaultLine;
    }

    public void setDefaultLine(PricePlanMatrixLine defaultLine) {
        this.defaultLine = defaultLine;
    }
}