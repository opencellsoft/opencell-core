package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.CalendarService;

@Stateless
public class WalletReservationService extends
		PersistenceService<WalletReservation> {

	private ParamBean paramBean = ParamBean.getInstance();

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RealtimeChargingService realtimeChargingService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private SellerService sellerService;

	@Inject
	private CalendarService calendarService;

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

		BigDecimal servicesSum = computeServicesSum(em, offerTemplate,
				userAccount, subscriptionDate, param1, param2, param3, quantity);

		BigDecimal ratedAmount = computeRatedAmount(em, provider, seller,
				userAccount, subscriptionDate);

		BigDecimal spentCredit = servicesSum.add(ratedAmount);

		return spentCredit;
	}

	public BigDecimal computeRatedAmount(EntityManager em, Provider provider,
			Seller seller, UserAccount userAccount, Date subscriptionDate) {
		Date startDate = null;
		Date endDate = null;

		Calendar cal = calendarService.findByName(em,
				paramBean.getProperty("default.calendar.monthly", "MONTHLY"));
		startDate = cal.previousCalendarDate(subscriptionDate);
		endDate = cal.nextCalendarDate(subscriptionDate);

		BigDecimal ratedAmount = walletOperationService.getRatedAmount(em,
				provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, true);

		return ratedAmount;
	}

	public BigDecimal computeServicesSum(EntityManager em,
			OfferTemplate offerTemplate, UserAccount userAccount,
			Date subscriptionDate, String param1, String param2, String param3,
			BigDecimal quantity) throws BusinessException {
		BigDecimal servicesSum = new BigDecimal(0);

		for (ServiceTemplate st : offerTemplate.getServiceTemplates()) {
			servicesSum = servicesSum.add(realtimeChargingService
					.getActivationServicePrice(em,
							userAccount.getBillingAccount(), st,
							subscriptionDate, quantity, param1, param2, param3,
							true));
		}

		return servicesSum;
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
