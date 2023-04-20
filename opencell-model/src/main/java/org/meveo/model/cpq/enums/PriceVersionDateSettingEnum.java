package org.meveo.model.cpq.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Tarik.Rabeh
 *
 */
public enum PriceVersionDateSettingEnum {
	/**
	 * status of QUOTE.
	 */
	QUOTE("QUOTE"),
	/**
	 * status of DELIVERY.
	 */
	DELIVERY("DELIVERY"),
	/**
	 * status of RENEWAL.
	 */
	RENEWAL("RENEWAL"),
	/**
	 * status of EVENT.
	 */
	EVENT("EVENT"),
	/**
	 * status of MANUAL.
	 */
	MANUAL("MANUAL");
	
	private PriceVersionDateSettingEnum(String value) {
		this.value = value;
	}
	
	private String value;
	
	public String getValue() {
		return this.value;
	}

	/**
	 * 
	 * @param value
	 * @return current PriceVersionDateSetting status name  by its value
	 */
	public static Optional<PriceVersionDateSettingEnum> getCurrentStatus(String value) {
		return Stream.of(PriceVersionDateSettingEnum.values()).filter(v -> v.value.equalsIgnoreCase(value)).findFirst();
	}

}
