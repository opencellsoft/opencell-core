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
import javax.persistence.FetchType;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        @NamedQuery(name = "PricePlanMatrixVersion.findByPricePlanAndVersionOrderByPmPriority", query = "select p from PricePlanMatrixVersion p where p.currentVersion=:currentVersion and lower(p.pricePlanMatrix.code)=:pricePlanMatrixCode order by p.pricePlanMatrix.priority asc"),
        @NamedQuery(name = "PricePlanMatrixVersion.lastVersion", query = "select p from PricePlanMatrixVersion p left join p.pricePlanMatrix pp where pp.code=:pricePlanMatrixCode order by p.currentVersion desc"),
        @NamedQuery(name = "PricePlanMatrixVersion.getLastPublishedVersion", query = "select p from PricePlanMatrixVersion p left join p.pricePlanMatrix pp where pp.code=:pricePlanMatrixCode and p.status=org.meveo.model.cpq.enums.VersionStatusEnum.PUBLISHED order by p.currentVersion desc ")
})
// 
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
    private boolean isMatrix;

    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal amountWithoutTax;

    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal amountWithTax;

    @Column(name = "amount_without_tax_el", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private String amountWithoutTaxEL;

    @Column(name = "amount_with_tax_el", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private String amountWithTaxEL;

    @OneToMany(mappedBy = "pricePlanMatrixVersion", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PricePlanMatrixLine> lines = new HashSet<>();

    @OneToMany(mappedBy = "pricePlanMatrixVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PricePlanMatrixColumn> columns = new HashSet<>();
    
    /**
     * The lower number, the higher the priority is
     */
    @Column(name = "priority", columnDefinition = "int DEFAULT 0")
    private int priority = 0;

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

    public Boolean isMatrix() {
        return isMatrix;
    }

    public void setMatrix(boolean matrix) {
        isMatrix = matrix;
    }

    public Set<PricePlanMatrixLine> getLines() {
        return lines;
    }

    public void setLines(Set<PricePlanMatrixLine> lines) {
        this.lines = lines;
    }

    public Set<PricePlanMatrixColumn> getColumns() {
        return columns;
    }

    public void setColumns(Set<PricePlanMatrixColumn> columns) {
        this.columns = columns;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public String getAmountWithoutTaxEL() {
        return amountWithoutTaxEL;
    }

    public void setAmountWithoutTaxEL(String amountWithoutTaxEL) {
        this.amountWithoutTaxEL = amountWithoutTaxEL;
    }

    public String getAmountWithTaxEL() {
        return amountWithTaxEL;
    }

    public void setAmountWithTaxEL(String amountWithTaxEL) {
        this.amountWithTaxEL = amountWithTaxEL;
    }
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
    
    
}