package org.meveo.api.dto.cpq.xml;

import org.meveo.model.article.AccountingArticle;
import org.meveo.model.quote.QuoteArticleLine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleLine {

    @XmlAttribute
    private String code;
    @XmlAttribute
    private String label;
    private String articleCode;
    private String articleLabel;
    @XmlElementWrapper(name = "quoteLines")
    @XmlElement(name = "quoteLine")
    private List<QuoteLine> quoteLines;

    public ArticleLine(AccountingArticle accountingArticle, List<QuoteArticleLine> lines, String tradingLanguage) {
        this.code = accountingArticle.getCode();
        this.label = accountingArticle.getDescriptionI18nNotNull().get(tradingLanguage) == null ? accountingArticle.getDescription() : accountingArticle.getDescriptionI18n().get(tradingLanguage);
        this.articleCode = this.code;
        this.articleLabel = this.label;
        this.quoteLines = lines.stream()
                .map(line -> new QuoteLine(line))
                .collect(Collectors.toList());
    }

    public List<QuoteLine> getQuoteLines() {
        return quoteLines;
    }

    public void setQuoteLines(List<QuoteLine> quoteLines) {
        this.quoteLines = quoteLines;
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

    public String getArticleCode() {
        return articleCode;
    }

    public void setArticleCode(String articleCode) {
        this.articleCode = articleCode;
    }

    public String getArticleLabel() {
        return articleLabel;
    }

    public void setArticleLabel(String articleLabel) {
        this.articleLabel = articleLabel;
    }
}
