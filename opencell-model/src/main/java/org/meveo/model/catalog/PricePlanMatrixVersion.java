package org.meveo.model.catalog;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.enums.VersionStatusEnum;
/**
 * @author Tarik FA.
 * @version 10.0
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_price_plan_version", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "price_plan_version" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_version_seq"), })
@NamedQuery(name = "PricePlanMatrixVersion.findByCode", query = "select p from PricePlanMatrixVersion p where p.code=:code and p.pricePlanVersion=:priceVersion")
public class PricePlanMatrixVersion extends BusinessEntity {
    
    @Column(name = "price_plan_version", nullable = false)
    private int pricePlanVersion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VersionStatusEnum status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date", nullable = false)
    private Date statusDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;
    
    @Type(type = "numeric_boolean")
    @Column(name = "price_list_flag", nullable = false)
    private boolean priceListFlag;
    
    @Column(name = "dim_number")
    private int dimNumber;
    @Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal pricetWithoutTax;
    /**
     * @return the pricePlanVersion
     */
    public int getPricePlanVersion() {
        return pricePlanVersion;
    }
    /**
     * @param pricePlanVersion the pricePlanVersion to set
     */
    public void setPricePlanVersion(int pricePlanVersion) {
        this.pricePlanVersion = pricePlanVersion;
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
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }
    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }
    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    /**
     * @return the priceListFlag
     */
    public boolean isPriceListFlag() {
        return priceListFlag;
    }
    /**
     * @param priceListFlag the priceListFlag to set
     */
    public void setPriceListFlag(boolean priceListFlag) {
        this.priceListFlag = priceListFlag;
    }
    /**
     * @return the dimNumber
     */
    public int getDimNumber() {
        return dimNumber;
    }
    /**
     * @param dimNumber the dimNumber to set
     */
    public void setDimNumber(int dimNumber) {
        this.dimNumber = dimNumber;
    }
    /**
     * @return the pricetWithoutTax
     */
    public BigDecimal getPricetWithoutTax() {
        return pricetWithoutTax;
    }
    /**
     * @param pricetWithoutTax the pricetWithoutTax to set
     */
    public void setPricetWithoutTax(BigDecimal pricetWithoutTax) {
        this.pricetWithoutTax = pricetWithoutTax;
    }
}