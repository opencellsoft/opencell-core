package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ModuleItem;

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
    @Type(type = "json")
    @Column(name = "label_i18n", columnDefinition = "jsonb")
    private Map<String, String> labelI18n;
    
    @Transient
    private BigDecimal amountWithoutTax;
    
    @Transient
    private BigDecimal amountWithTax;

    @Transient
    private BigDecimal transactionalAmountWithTax;
    
    @Transient
    private BigDecimal transactionalAmountWithoutTax;
    
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

	public BigDecimal getTransactionalAmountWithTax() {
		return transactionalAmountWithTax;
	}

	public void setTransactionalAmountWithTax(BigDecimal transactionalAmountWithTax) {
		this.transactionalAmountWithTax = transactionalAmountWithTax;
	}

	public BigDecimal getTransactionalAmountWithoutTax() {
		return transactionalAmountWithoutTax;
	}

	public void setTransactionalAmountWithoutTax(BigDecimal transactionalAmountWithoutTax) {
		this.transactionalAmountWithoutTax = transactionalAmountWithoutTax;
	}

}
