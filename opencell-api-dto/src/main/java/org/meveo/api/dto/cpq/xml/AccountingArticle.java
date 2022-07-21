package org.meveo.api.dto.cpq.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.model.quote.QuoteArticleLine;

@XmlAccessorType(XmlAccessType.FIELD)
public class AccountingArticle {

    @XmlAttribute
    private String code;
    @XmlAttribute
    private String label;
    @XmlAttribute
    private String openOrderNumber;
    @XmlAttribute
    private String openOrderReference;
    @XmlAttribute
    private Date openOrderStartDate;
    @XmlElementWrapper(name = "quoteLines")
    @XmlElement(name = "quoteLine")
    private List<QuoteLine> quoteLines;
   

    public AccountingArticle(org.meveo.model.article.AccountingArticle accountingArticle, List<QuoteArticleLine> lines, String tradingLanguage) {
        this(accountingArticle, lines, tradingLanguage, null, null, null);
    }

    public AccountingArticle(org.meveo.model.article.AccountingArticle accountingArticle, List<QuoteArticleLine> lines, String tradingLanguage, String openOrderNumber, String openOrderReference, Date openOrderStartDate) {
        this.code = accountingArticle.getCode();
        this.label = accountingArticle.getDescriptionI18nNotNull().get(tradingLanguage) == null ? accountingArticle.getDescription() : accountingArticle.getDescriptionI18n().get(tradingLanguage);
        this.openOrderNumber = openOrderNumber;
        this.openOrderReference = openOrderReference;
        this.openOrderStartDate = openOrderStartDate;
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

	public String getOpenOrderNumber() {
		return openOrderNumber;
	}

	public void setOpenOrderNumber(String openOrderNumber) {
		this.openOrderNumber = openOrderNumber;
	}

	public String getOpenOrderReference() {
		return openOrderReference;
	}

	public void setOpenOrderReference(String openOrderReference) {
		this.openOrderReference = openOrderReference;
	}

	public Date getOpenOrderStartDate() {
		return openOrderStartDate;
	}

	public void setOpenOrderStartDate(Date openOrderStartDate) {
		this.openOrderStartDate = openOrderStartDate;
	}
    
}
