package org.meveo.model.article;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.tax.TaxClass;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashMap;
import java.util.Map;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity@CustomFieldEntity(cftCodePrefix = "Article")
@Table(name = "billing_accounting_article", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_accounting_article_seq"), })
@NamedQuery(name = "AccountingArticle.findByAccountingCode", query = "select a from AccountingArticle a where a.accountingCode.code = :accountingCode")
public class AccountingArticle extends EnableBusinessCFEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@OneToOne(fetch = LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "tax_class_id")
    private TaxClass taxClass;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "article_family_id")
    private ArticleFamily articleFamily;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @Column(name = "analytic_code_1")
    private String analyticCode1;

    @Column(name = "analytic_code_2")
    private String analyticCode2;

    @Column(name = "analytic_code_3")
    private String analyticCode3;

    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    
    
}
