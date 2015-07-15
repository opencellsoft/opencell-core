package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.catalog.WalletTemplate;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "WalletTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletTemplateDto extends BaseDto {

	private static final long serialVersionUID = 2681139334253613359L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private String walletType;
	private boolean consumptionAlertSet;
	private int fastRatingLevel;
	private BigDecimal lowBalanceLevel;

	public WalletTemplateDto() {
		// TODO Auto-generated constructor stub
	}

	public WalletTemplateDto(WalletTemplate e) {
		code = e.getCode();
		description = e.getDescription();
		walletType = e.getWalletType().name();
		consumptionAlertSet = e.isConsumptionAlertSet();
		fastRatingLevel = e.getFastRatingLevel();
		lowBalanceLevel = e.getLowBalanceLevel();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWalletType() {
		return walletType;
	}

	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}

	public boolean isConsumptionAlertSet() {
		return consumptionAlertSet;
	}

	public void setConsumptionAlertSet(boolean consumptionAlertSet) {
		this.consumptionAlertSet = consumptionAlertSet;
	}

	public int getFastRatingLevel() {
		return fastRatingLevel;
	}

	public void setFastRatingLevel(int fastRatingLevel) {
		this.fastRatingLevel = fastRatingLevel;
	}

	public BigDecimal getLowBalanceLevel() {
		return lowBalanceLevel;
	}

	public void setLowBalanceLevel(BigDecimal lowBalanceLevel) {
		this.lowBalanceLevel = lowBalanceLevel;
	}

	@Override
	public String toString() {
		return "WalletTemplateDto [code=" + code + ", description=" + description + ", walletType=" + walletType
				+ ", consumptionAlertSet=" + consumptionAlertSet + ", fastRatingLevel=" + fastRatingLevel
				+ ", lowBalanceLevel=" + lowBalanceLevel + "]";
	}

}
