package org.meveo.api.dto.cpq.xml;

import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.quote.QuoteArticleLine;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import java.util.stream.Collectors;
@XmlAccessorType(XmlAccessType.FIELD)
public class SubCategory {
    @XmlAttribute
    private String code;
    @XmlAttribute
    private String label;
    @XmlAttribute
    private Integer sortIndex;
    @XmlElementWrapper(name = "accountingArticles")
    @XmlElement(name = "accountingArticle")
    private List<AccountingArticle> articleLines;

    public SubCategory(InvoiceSubCategory invoiceSubCategory, List<AccountingArticle> articleLines, String tradingLanguage) {
        this.code = invoiceSubCategory.getCode();
        this.label = invoiceSubCategory.getDescriptionI18nNullSafe().get(tradingLanguage) == null ? invoiceSubCategory.getDescription() : invoiceSubCategory.getDescriptionI18n().get(tradingLanguage);
        this.sortIndex = invoiceSubCategory.getSortIndex();
        this.articleLines = articleLines;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

	public List<AccountingArticle> getArticleLines() {
		return articleLines;
	}

	public void setArticleLines(List<AccountingArticle> articleLines) {
		this.articleLines = articleLines;
	}






}
