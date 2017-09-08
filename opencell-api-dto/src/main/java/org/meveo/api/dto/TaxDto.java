package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Tax;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@XmlRootElement(name = "Tax")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxDto extends BusinessDto {

	private static final long serialVersionUID = 5184602572648722134L;

	@XmlElement(required = true)
	private BigDecimal percent;

	private String accountingCode;
	private List<LanguageDescriptionDto> languageDescriptions;
	
	private CustomFieldsDto customFields;

	public TaxDto() {

	}

	public TaxDto(Tax tax,CustomFieldsDto customFieldInstances) {
		super(tax);
		percent = tax.getPercent();
		accountingCode = tax.getAccountingCode();
		customFields = customFieldInstances;
        setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(tax.getDescriptionI18n()));
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}

	public String getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(String accountingCode) {
		this.accountingCode = accountingCode;
	}

	public List<LanguageDescriptionDto> getLanguageDescriptions() {
		return languageDescriptions;
	}

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
		this.languageDescriptions = languageDescriptions;
	}
	
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	@Override
	public String toString() {
        return "TaxDto [code=" + getCode() + ", description=" + getDescription() + ", percent=" + percent + ", accountingCode=" + accountingCode + ", languageDescriptions="
                + languageDescriptions + ", customFields=" + customFields + "]";
	}

}
