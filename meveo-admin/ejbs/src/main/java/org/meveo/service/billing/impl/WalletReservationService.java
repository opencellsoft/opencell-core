package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
public class WalletReservationService extends PersistenceService<WalletReservation> {

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

	public BigDecimal getCurrentBalanceWithoutTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		return walletOperationService.getBalanceAmount(provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, false, 1);
	}

	public BigDecimal getCurrentBalanceWithTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		return walletOperationService.getBalanceAmount(provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, true, 1);
	}

	public BigDecimal getReservedBalanceWithoutTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		return walletOperationService.getBalanceAmount(provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, false, 2);
	}

	public BigDecimal getReservedBalanceWithTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		return walletOperationService.getBalanceAmount(provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, true, 2);
	}

	public BigDecimal getOpenBalanceWithoutTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		return walletOperationService.getBalanceAmount(provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, false, 3);
	}

	public BigDecimal getOpenBalanceWithTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code=" + userAccountCode + " does not exists.");
		}

		Seller seller = sellerService.findByCode(sellerCode, provider);
		if (seller == null) {
			throw new BusinessException("Seller with code=" + sellerCode + " does not exists.");
		}

		return walletOperationService.getBalanceAmount(provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, true, 3);
	}

	public BigDecimal getCurrentAmountWithoutTax() {
		// return getOpenBalanceWithoutTax().add(getCurrentBalanceWithoutTax());
		return null;
	}

	public BigDecimal getCurrentAmountWithTax() {
		// return getOpenBalanceWithTax().add(getCurrentBalanceWithTax());
		return null;
	}

	public void updateReservationStatus(Long reservationId, WalletOperationStatusEnum status) {
		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE WalletReservation w SET w.status=:status, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");

		try {
			getEntityManager().createQuery(sb.toString()).setParameter("updated", new Date())
					.setParameter("status", status).setParameter("reservationId", reservationId).executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public BigDecimal getSpentCredit(Provider provider, Seller seller, OfferTemplate offerTemplate,
			UserAccount userAccount, Date subscriptionDate, String param1, String param2, String param3,
			BigDecimal quantity) throws BusinessException {

		BigDecimal servicesSum = computeServicesSum(offerTemplate, userAccount, subscriptionDate, param1, param2,
				param3, quantity);

		BigDecimal ratedAmount = computeRatedAmount(provider, seller, userAccount, subscriptionDate);

		BigDecimal spentCredit = servicesSum.add(ratedAmount);

		return spentCredit;
	}

	public BigDecimal computeRatedAmount(Provider provider, Seller seller, UserAccount userAccount,
			Date subscriptionDate) {
		Date startDate = null;
		Date endDate = null;

		Calendar cal = calendarService.findByCode(paramBean.getProperty("default.calendar.monthly", "MONTHLY"),
				provider);
		cal.setInitDate(subscriptionDate);
		startDate = cal.previousCalendarDate(subscriptionDate);
		endDate = cal.nextCalendarDate(subscriptionDate);

		BigDecimal ratedAmount = walletOperationService.getBalanceAmount(provider, seller, null, null, null,
				userAccount, startDate, endDate, false, 1);

		return ratedAmount;
	}

	public BigDecimal computeServicesSum(OfferTemplate offerTemplate, UserAccount userAccount, Date subscriptionDate,
			String param1, String param2, String param3, BigDecimal quantity) throws BusinessException {
		BigDecimal servicesSum = new BigDecimal(0);

		for (ServiceTemplate st : offerTemplate.getServiceTemplates()) {
			servicesSum = servicesSum.add(realtimeChargingService.getActivationServicePrice(
					userAccount.getBillingAccount(), st, subscriptionDate, offerTemplate.getCode(), quantity, param1,
					param2, param3, true));
		}

		return servicesSum;
	}

	public void updateSpendCredit(Long reservationId, BigDecimal amount, boolean amountWithTax) {
		StringBuilder sb = new StringBuilder();

		if (amountWithTax) {
			sb.append("UPDATE WalletReservation w SET w.amountWithTax=:amount, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");
		} else {
			sb.append("UPDATE WalletReservation w SET w.amountWithoutTax=:amount, w.auditable.updated=:updated WHERE w.reservation.id=:reservationId");
		}

		try {
			getEntityManager().createQuery(sb.toString()).setParameter("updated", new Date())
					.setParameter("amount", amount).setParameter("reservationId", reservationId).executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
