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
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WalletReservationService extends
		PersistenceService<WalletReservation> {

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RealtimeChargingService realtimeChargingService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private SellerService sellerService;

	public BigDecimal getCurrentBalanceWithoutTax(EntityManager em,
			Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode
					+ " does not exists.");
		}

		return walletOperationService.getBalanceAmount(em, provider, seller,
				null, null, userAccount.getBillingAccount(), null, startDate,
				endDate, false, 1);
	}

	public BigDecimal getCurrentBalanceWithTax(EntityManager em,
			Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode
					+ " does not exists.");
		}

		return walletOperationService.getBalanceAmount(em, provider, seller,
				null, null, userAccount.getBillingAccount(), null, startDate,
				endDate, true, 1);
	}

	public BigDecimal getReservedBalanceWithoutTax(EntityManager em,
			Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode
					+ " does not exists.");
		}

		return walletOperationService.getBalanceAmount(em, provider, seller,
				null, null, userAccount.getBillingAccount(), null, startDate,
				endDate, false, 2);
	}

	public BigDecimal getReservedBalanceWithTax(EntityManager em,
			Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode
					+ " does not exists.");
		}

		return walletOperationService.getBalanceAmount(em, provider, seller,
				null, null, userAccount.getBillingAccount(), null, startDate,
				endDate, true, 2);
	}

	public BigDecimal getOpenBalanceWithoutTax(EntityManager em,
			Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode
					+ " does not exists.");
		}

		return walletOperationService.getBalanceAmount(em, provider, seller,
				null, null, userAccount.getBillingAccount(), null, startDate,
				endDate, false, 3);
	}

	public BigDecimal getOpenBalanceWithTax(EntityManager em,
			Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode
					+ " does not exists.");
		}

		return walletOperationService.getBalanceAmount(em, provider, seller,
				null, null, userAccount.getBillingAccount(), null, startDate,
				endDate, true, 3);
	}

	public BigDecimal getCurrentAmountWithoutTax() {
		// return getOpenBalanceWithoutTax().add(getCurrentBalanceWithoutTax());
		return null;
	}

	public BigDecimal getCurrentAmountWithTax() {
		// return getOpenBalanceWithTax().add(getCurrentBalanceWithTax());
		return null;
	}

	public void updateReservationStatus(EntityManager em, Long reservationId,
			WalletOperationStatusEnum status) {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE WalletReservation w SET w.status=:status, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");

		try {
			em.createQuery(sb.toString()).setParameter("updated", new Date())
					.setParameter("status", status)
					.setParameter("reservationId", reservationId)
					.executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public BigDecimal getSpentCredit(EntityManager em, Provider provider,
			Seller seller, OfferTemplate offerTemplate,
			UserAccount userAccount, Date subscriptionDate, String param1,
			String param2, String param3) throws BusinessException {
		return getSpentCredit(em, provider, seller, offerTemplate, userAccount,
				subscriptionDate, param1, param2, param3, new BigDecimal(1));
	}

	public BigDecimal getSpentCredit(EntityManager em, Provider provider,
			Seller seller, OfferTemplate offerTemplate,
			UserAccount userAccount, Date subscriptionDate, String param1,
			String param2, String param3, BigDecimal quantity)
			throws BusinessException {
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
					.getActivationServicePrice(em,
							userAccount.getBillingAccount(), st,
							subscriptionDate, new BigDecimal(1), param1,
							param2, param3, true));

		}

		BigDecimal ratedAmount = walletOperationService.getRatedAmount(em,
				provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, true);

		BigDecimal spentCredit = servicesSum.add(ratedAmount);

		return spentCredit;
	}

	public void updateSpendCredit(EntityManager em, Long reservationId,
			BigDecimal amount, boolean amountWithTax) {
		StringBuilder sb = new StringBuilder();

		if (amountWithTax) {
			sb.append("UPDATE WalletReservation w SET w.amountWithTax=:amount, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");
		} else {
			sb.append("UPDATE WalletReservation w SET w.amountWithoutTax=:amount, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");
		}

		try {
			em.createQuery(sb.toString()).setParameter("updated", new Date())
					.setParameter("amount", amount)
					.setParameter("reservationId", reservationId)
					.executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
