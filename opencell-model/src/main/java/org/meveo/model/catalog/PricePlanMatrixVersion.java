package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
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
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.cpq.enums.PriceVersionTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;

/**
 * @author Tarik FA.
 * @version 10.0
 */
@SuppressWarnings("serial")
@Entity
@ExportIdentifier({"pricePlanMatrix.code", "currentVersion"})
@Table(name = "cpq_price_plan_version", uniqueConstraints = @UniqueConstraint(columnNames = { "ppm_id", "current_version" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_version_seq") })
@NamedQueries({
        @NamedQuery(name = "PricePlanMatrixVersion.findByPricePlanAndVersionOrderByPmPriority", query = "select p from PricePlanMatrixVersion p left join fetch p.columns pc left join fetch p.lines pl where p.currentVersion=:currentVersion and lower(p.pricePlanMatrix.code)=:pricePlanMatrixCode order by p.pricePlanMatrix.priority asc"),
        @NamedQuery(name = "PricePlanMatrixVersion.lastVersion", query = "select p from PricePlanMatrixVersion p left join p.pricePlanMatrix pp where pp.code=:pricePlanMatrixCode order by p.currentVersion desc"),
        @NamedQuery(name = "PricePlanMatrixVersion.lastCurrentVersion", query = "select p.currentVersion from PricePlanMatrixVersion p where  p.pricePlanMatrix=:pricePlanMatrix order by p.currentVersion desc"),
        @NamedQuery(name = "PricePlanMatrixVersion.getLastPublishedVersion", query = "select p from PricePlanMatrixVersion p left join p.pricePlanMatrix pp where pp.code=:pricePlanMatrixCode and p.status=org.meveo.model.cpq.enums.VersionStatusEnum.PUBLISHED order by p.currentVersion desc"),
        @NamedQuery(name = "PricePlanMatrixVersion.getPricePlanVersionsByIds", query = "select p from PricePlanMatrixVersion p left join p.pricePlanMatrix pp where p.id IN (:ids) order by p.id desc"),
        @NamedQuery(name = "PricePlanMatrixVersion.getPublishedVersionValideForDate", query = "select v from PricePlanMatrixVersion v where v.pricePlanMatrix.id=:pricePlanMatrixId and v.status=org.meveo.model.cpq.enums.VersionStatusEnum.PUBLISHED and (v.validity.from is null or v.validity.from<=:operationDate) and (v.validity.to is null or v.validity.to>:operationDate)", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE"), @QueryHint(name = "org.hibernate.readOnly", value = "true") }),
        @NamedQuery(name = "PricePlanMatrixVersion.getPublishedVersionValideForDateByPpmCode", query = "select v from PricePlanMatrixVersion v where v.pricePlanMatrix.code=:pricePlanMatrixCode and v.status=org.meveo.model.cpq.enums.VersionStatusEnum.PUBLISHED and (v.validity.from is null or v.validity.from<=:operationDate) and (v.validity.to is null or v.validity.to>:operationDate)", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE"), @QueryHint(name = "org.hibernate.readOnly", value = "true") }),        
        @NamedQuery(name = "PricePlanMatrixVersion.findEndDates", query = "from PricePlanMatrixVersion p where p.status='PUBLISHED' and p.pricePlanMatrix=:pricePlanMatrix and (p.validity.to >= :date or p.validity.to is null) order by p.validity.from desc"),
        @NamedQuery(name = "PricePlanMatrixVersion.findByPricePlan", query = "select p from PricePlanMatrixVersion p where p.pricePlanMatrix=:priceplan and p.status<>'CLOSED'"),
        @NamedQuery(name = "PricePlanMatrixVersion.findByPricePlans", query = "select p from PricePlanMatrixVersion p where p.pricePlanMatrix in :priceplans and p.status<>'CLOSED'"),
        @NamedQuery(name = "PricePlanMatrixVersion.getAllVersionsForChargeTemplates", query = "select p from PricePlanMatrixVersion p join p.pricePlanMatrix.chargeTemplates ct where ct.id in (:charges)"),
    })
@Cacheable
public class PricePlanMatrixVersion extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private VersionStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(name = "price", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal price;

    @Column(name = "price", insertable = false, updatable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    @Deprecated
    private BigDecimal amountWithoutTax;

    @Column(name = "price", insertable = false, updatable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    @Deprecated
    private BigDecimal amountWithTax;

    @Column(name = "price_el")
    private String priceEL;

    @OneToMany(mappedBy = "pricePlanMatrixVersion", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<PricePlanMatrixLine> lines = new HashSet<>();

    @OneToMany(mappedBy = "pricePlanMatrixVersion", fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    @Cache(usage =  CacheConcurrencyStrategy.READ_WRITE)
    private Set<PricePlanMatrixColumn> columns = new HashSet<>();

    @OneToMany(mappedBy = "pricePlanMatrixVersion", fetch = FetchType.LAZY)
    private Set<TradingPricePlanVersion> tradingPricePlanVersions = new HashSet<>();

    /**
     * The lower number, the higher the priority is
     */
    @Column(name = "priority")
    private int priority = 0;
    /**
     * The price
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "price_version_type")
    @NotNull
    private PriceVersionTypeEnum priceVersionType = PriceVersionTypeEnum.FIXED;

    public PricePlanMatrixVersion() {
    }

    public PricePlanMatrixVersion(PricePlanMatrixVersion copy) {
        this.status = VersionStatusEnum.DRAFT;
        this.pricePlanMatrix = copy.pricePlanMatrix;
        this.currentVersion = 1;
        this.label = copy.label;
        this.statusDate = new Date();
        this.isMatrix = copy.isMatrix;
        this.price = copy.price;
        this.priceEL = copy.priceEL;
        this.lines = new HashSet<>();
        this.columns = new HashSet<>();
        this.priority = copy.priority;
        this.version = 1;
    }

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
        if (getId() != null) {
            setStatusChangeLog(", status changed from " + this.status + " to " + status + ".");
        }
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

    public BigDecimal getPrice() {
    	return isMatrix? null : price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Deprecated
    public BigDecimal getAmountWithoutTax() {
        return getPrice();
    }

    @Deprecated
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        setPrice(amountWithoutTax);
    }

    @Deprecated
    public BigDecimal getAmountWithTax() {
        return getPrice();
    }

    @Deprecated
    public void setAmountWithTax(BigDecimal amountWithTax) {
        setPrice(amountWithTax);
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

    @Transient
    private String statusChangeLog = "";

    /**
     * @return the statusChangeLog
     */
    public String getStatusChangeLog() {
        return statusChangeLog;
    }

    /**
     * @param statusChangeLog the statusChangeLog to set
     */
    public void setStatusChangeLog(String statusChangeLog) {
        this.statusChangeLog = statusChangeLog;
    }
    
    public String getPriceEL() {
        return priceEL;
    }

    public void setPriceEL(String priceEL) {
        this.priceEL = priceEL;
    }

    public PriceVersionTypeEnum getPriceVersionType() {
        return priceVersionType;
    }

    public void setPriceVersionType(PriceVersionTypeEnum priceVersionType) {
        this.priceVersionType = priceVersionType;
    }

	public Set<TradingPricePlanVersion> getTradingPricePlanVersions() {
		return tradingPricePlanVersions;
	}

	public void setTradingPricePlanVersions(Set<TradingPricePlanVersion> tradingPricePlanVersions) {
		this.tradingPricePlanVersions = tradingPricePlanVersions;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(price, priceEL, columns, currentVersion, isMatrix, label, pricePlanMatrix,
          priority, status, statusChangeLog, statusDate, validity);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PricePlanMatrixVersion))
            return false;
        PricePlanMatrixVersion other = (PricePlanMatrixVersion) obj;
        return Objects.equals(price, other.price)
                && Objects.equals(priceEL, other.priceEL)
                && Objects.equals(columns, other.columns) && currentVersion == other.currentVersion && isMatrix == other.isMatrix && Objects.equals(label, other.label)
                && Objects.equals(pricePlanMatrix, other.pricePlanMatrix) && priority == other.priority && status == other.status
                && Objects.equals(statusChangeLog, other.statusChangeLog) && Objects.equals(statusDate, other.statusDate) && Objects.equals(validity, other.validity);
    }


}
