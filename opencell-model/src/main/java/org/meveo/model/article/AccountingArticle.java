package org.meveo.model.article;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.accountingScheme.AccountingCodeMapping;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.tax.TaxClass;

@Entity@CustomFieldEntity(cftCodePrefix = "Article")
@Table(name = "billing_accounting_article", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_accounting_article_seq"), })
@NamedQueries({
        @NamedQuery(name = "AccountingArticle.findByAccountingCode", query = "select a from AccountingArticle a where a.accountingCode.code = :accountingCode"),
        @NamedQuery(name = "AccountingArticle.findByTaxClassAndSubCategory", query = "select a from AccountingArticle a where a.taxClass = :taxClass and a.invoiceSubCategory = :invoiceSubCategory"),
})
public class AccountingArticle extends EnableBusinessCFEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@OneToOne(fetch = EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "tax_class_id")
    private TaxClass taxClass;

    @OneToOne(fetch = LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    @OneToOne(fetch = LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "article_family_id")
    private ArticleFamily articleFamily;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;
    
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "invoice_type_id")
    private InvoiceType invoiceType;

    @Column(name = "invoice_type_el")
    private String invoiceTypeEl;    
    
    @Column(name = "analytic_code_1")
    private String analyticCode1;

    @Column(name = "analytic_code_2")
    private String analyticCode2;

    @Column(name = "analytic_code_3")
    private String analyticCode3;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;

    @Column(name = "accountingcode_el", length = 500)
    private String accountingCodeEl;

    @Column(name = "column_criteria_el", length = 500)
    private String columnCriteriaEL;

    @OneToMany(mappedBy = "accountingArticle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountingCodeMapping> accountingCodeMappings;
    

    /**
     * Ignore aggregation
     */
    @Type(type = "numeric_boolean")
    @Column(name = "ignore_aggregation", nullable = false)
    private boolean ignoreAggregation;

    public AccountingArticle() {
    }

    public AccountingArticle(Long id) {
        this.id = id;
    }

    public AccountingArticle(String code, String description, TaxClass taxClass, InvoiceSubCategory invoiceSubCategory) {
        this.code = code;
        this.description = description;
        this.taxClass = taxClass;
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public TaxClass getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(TaxClass taxClass) {
        this.taxClass = taxClass;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public ArticleFamily getArticleFamily() {
        return articleFamily;
    }

    public void setArticleFamily(ArticleFamily articleFamily) {
        this.articleFamily = articleFamily;
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public Map<String, String> getDescriptionI18n() {
        if(descriptionI18n == null)
            descriptionI18n = new HashMap<>();
        return descriptionI18n;
    }

    public Map<String, String> getDescriptionI18nNotNull() {
        if(descriptionI18n == null)
            descriptionI18n = new HashMap<>();
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    public String getAnalyticCode1() {
        return analyticCode1;
    }

    public void setAnalyticCode1(String analyticCode1) {
        this.analyticCode1 = analyticCode1;
    }

    public String getAnalyticCode2() {
        return analyticCode2;
    }

    public void setAnalyticCode2(String analyticCode2) {
        this.analyticCode2 = analyticCode2;
    }

    public String getAnalyticCode3() {
        return analyticCode3;
    }

    public void setAnalyticCode3(String analyticCode3) {
        this.analyticCode3 = analyticCode3;
    }
    
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTypeEl() {
        return invoiceTypeEl;
    }

    public void setInvoiceTypeEl(String invoiceTypeEL) {
        this.invoiceTypeEl = invoiceTypeEL;
    }

    public String getAccountingCodeEl() {
        return accountingCodeEl;
    }

    public void setAccountingCodeEl(String accountingCodeEl) {
        this.accountingCodeEl = accountingCodeEl;
    }

    public String getColumnCriteriaEL() {
        return columnCriteriaEL;
    }

    public void setColumnCriteriaEL(String columCriteriaEL) {
        this.columnCriteriaEL = columCriteriaEL;
    }

    @Override
   	public int hashCode() {
   		final int prime = 31;
   		int result = super.hashCode();
   		result = prime * result;
   		return result;
   	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof AccountingArticle))
			return false;
		AccountingArticle other = (AccountingArticle) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

    public List<AccountingCodeMapping> getAccountingCodeMappings() {
        return accountingCodeMappings;
    }

    public void setAccountingCodeMappings(List<AccountingCodeMapping> accountingCodeMappings) {
        this.accountingCodeMappings = accountingCodeMappings;
    }

    public boolean isIgnoreAggregation() {
        return ignoreAggregation;
    }

    public void setIgnoreAggregation(boolean ignoreAggregation) {
        this.ignoreAggregation = ignoreAggregation;
    }
}