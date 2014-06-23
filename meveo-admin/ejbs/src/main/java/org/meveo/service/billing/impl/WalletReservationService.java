package org.meveo.service.billing.impl;

import java.math.BigDecimal;

import javax.ejb.Stateless;

import org.meveo.model.billing.WalletReservation;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WalletReservationService extends
		PersistenceService<WalletReservation> {

	public BigDecimal getCurrentBalanceWithoutTax() {
		return null;
	}

	public BigDecimal getCurrentBalanceWithTax() {
		return null;
	}

	public BigDecimal getReservedBalanceWithoutTax() {
		return null;
	}

	public BigDecimal getReservedBalanceWithTax() {
		return null;
	}

	public BigDecimal getOpenBalanceWithoutTax() {
		return null;
	}

	public BigDecimal getOpenBalanceWithTax() {
		return null;
	}

	public BigDecimal getCurrentAmountWithoutTax() {
		return getOpenBalanceWithoutTax().add(getCurrentBalanceWithoutTax());
	}

	public BigDecimal getCurrentAmountWithTax() {
		return getOpenBalanceWithTax().add(getCurrentBalanceWithTax());
	}

}
