package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.util.MeveoJpa;

@Stateless
public class WalletReservationService extends
		PersistenceService<WalletReservation> {

	@Inject
	@MeveoJpa
	protected EntityManager entityManager;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RealtimeChargingService realtimeChargingService;

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

	public void updateReservationStatus(Long reservationId,
			WalletOperationStatusEnum status) {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE WalletReservation w SET w.status=:status, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");

		try {
			getEntityManager().createQuery(sb.toString())
					.setParameter("updated", new Date())
					.setParameter("status", status)
					.setParameter("reservationId", reservationId)
					.executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public BigDecimal getSpentCredit(Provider provider, Seller seller,
			OfferTemplate offerTemplate, UserAccount userAccount,
			Date subscriptionDate, String param1, String param2, String param3)
			throws BusinessException {
		return getSpentCredit(provider, seller, offerTemplate, userAccount,
				subscriptionDate, param1, param2, param3, new BigDecimal(1));
	}

	public BigDecimal getSpentCredit(Provider provider, Seller seller,
			OfferTemplate offerTemplate, UserAccount userAccount,
			Date subscriptionDate, String param1, String param2, String param3,
			BigDecimal quantity) throws BusinessException {
		BigDecimal servicesSum = new BigDecimal(0);
		Date startDate = null;
		Date endDate = null;

		for (ServiceTemplate st : offerTemplate.getServiceTemplates()) {
			if (st.getRecurringCharges() != null
					&& st.getRecurringCharges().size() > 0) {
				for (RecurringChargeTemplate ct : st.getRecurringCharges()) {
					try {
						if (startDate == null
								|| ct.getCalendar()
										.previousCalendarDate(subscriptionDate)
										.before(startDate)) {
							startDate = ct.getCalendar().previousCalendarDate(
									subscriptionDate);
						}
						if (endDate == null
								|| ct.getCalendar()
										.nextCalendarDate(subscriptionDate)
										.after(endDate)) {
							endDate = ct.getCalendar().nextCalendarDate(
									subscriptionDate);
						}
					} catch (NullPointerException e) {
						log.debug(
								"Next or Previous calendar value is null for recurringChargeTemplate with code={}",
								ct.getCode());
					}
				}
			}

			servicesSum = servicesSum.add(realtimeChargingService
					.getActivationServicePrice(entityManager,
							userAccount.getBillingAccount(), st,
							subscriptionDate, new BigDecimal(1), param1,
							param2, param3, true));

		}

		BigDecimal ratedAmount = walletOperationService
				.getRatedAmount(entityManager, provider, seller, null, null,
						userAccount.getBillingAccount(), null, startDate,
						endDate, true);

		BigDecimal spentCredit = servicesSum.add(ratedAmount);

		return spentCredit;
	}

}
