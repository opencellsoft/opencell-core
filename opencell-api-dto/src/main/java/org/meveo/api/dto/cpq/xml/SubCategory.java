package org.meveo.api.dto.cpq.xml;

import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.quote.QuoteArticleLine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
    @XmlElementWrapper(name = "articleLines")
    @XmlElement(name = "articleLine")
    private List<ArticleLine> articleLines;

    public SubCategory(InvoiceSubCategory invoiceSubCategory, List<ArticleLine> articleLines, String tradingLanguage) {
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

    public List<ArticleLine> getArticleLines() {
        return articleLines;
    }

    public void setArticleLines(List<ArticleLine> articleLines) {
        this.articleLines = articleLines;
    }
}
