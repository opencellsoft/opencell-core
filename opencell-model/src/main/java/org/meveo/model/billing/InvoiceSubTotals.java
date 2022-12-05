package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.SqlTypes;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ModuleItem;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

@Entity
@ModuleItem
@Cacheable
@Table(name = "invoice_sub_totals")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "invoice_sub_totals_seq"), })
@NamedQueries(value = { @NamedQuery(name = "InvoiceSubTotals.findByInvoiceType", query = "From InvoiceSubTotals iv where iv.invoiceType=:invoiceType") })
public class InvoiceSubTotals extends EnableBusinessEntity {
    private static final long serialVersionUID = -1640429569087958882L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_type_id")
    private InvoiceType invoiceType;
    
    @Column(name = "sub_total_el", length = 2000)
    @Size(max = 2000)
    private String subTotalEl;

    /**
     * Label for button in GUI
     */
    @Column(name = "label", length = 50)
    @Size(max = 50)
    private String label;

    /**
     * Translated label in JSON format with language code as a key and translated label as a value
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "label_i18n", columnDefinition = "jsonb")
    private Map<String, String> labelI18n;
    
    @Transient
    private BigDecimal amountWithoutTax;
    
    @Transient
    private BigDecimal amountWithTax;

    @Transient
    private BigDecimal convertedAmountWithTax;
    
    @Transient
    private BigDecimal convertedAmountWithoutTax;
    
    @Override
    public String toString() {
        return String.format("InvoiceSubTotals [id=%s, label=%s]", id, label);
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getSubTotalEl() {
        return subTotalEl;
    }

    public void setSubTotalEl(String subTotalEl) {
        this.subTotalEl = subTotalEl;
    }
    

    public String getLabel() {
        return label;
    }
    
    public String getLabel(String language) {
    
        if (language == null || labelI18n == null || labelI18n.isEmpty()) {
            return label;
        }
    
        language = language.toUpperCase();
        if (!labelI18n.containsKey(language)) {
            return label;
        } else {
            return labelI18n.get(language);
        }
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, String> getLabelI18n() {
        return labelI18n;
    }

    public void setLabelI18n(Map<String, String> labelI18n) {
        this.labelI18n = labelI18n;
    }
    
    /**
     * Instantiate labelI18n field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record will be
     * updated in DB
     * 
     * @return labelI18n value or instantiated labelI18n field value
     */
    public Map<String, String> getLabelI18nNullSafe() {
        if (labelI18n == null) {
            labelI18n = new HashMap<>();
        }
        return labelI18n;
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

	public BigDecimal getConvertedAmountWithTax() {
		return convertedAmountWithTax;
	}

	public void setConvertedAmountWithTax(BigDecimal convertedAmountWithTax) {
		this.convertedAmountWithTax = convertedAmountWithTax;
	}

	public BigDecimal getConvertedAmountWithoutTax() {
		return convertedAmountWithoutTax;
	}

	public void setConvertedAmountWithoutTax(BigDecimal convertedAmountWithoutTax) {
		this.convertedAmountWithoutTax = convertedAmountWithoutTax;
	}
}
