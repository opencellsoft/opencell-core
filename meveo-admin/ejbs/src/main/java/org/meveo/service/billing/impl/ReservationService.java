package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;

@Stateless
public class ReservationService extends PersistenceService<Reservation> {

	@Inject
	private WalletReservationService walletReservationService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private RealtimeChargingService realtimeChargingService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private ReservationService reservationService;

	@Inject
	private WalletOperationService walletOperationService;

	public Long reserveCredit(Provider provider, Seller seller,
			String offerCode, String userAccountCode, Date subscriptionDate,
			BigDecimal creditLimit, String param1, String param2, String param3)
			throws BusinessException {

		// #1 Check the credit limit (servicesSum + getCurrentAmount) & return
		// error if KO.
		OfferTemplate offerTemplate = offerTemplateService.findByCode(
				offerCode, provider);

		UserAccount userAccount = userAccountService.findByCode(
				userAccountCode, provider);
		if (userAccount == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not exists.");
		}

		if (userAccount.getBillingAccount() == null) {
			throw new BusinessException("UserAccount with code="
					+ userAccountCode + " does not have a billingAccount set.");
		}

		if (offerTemplate == null) {
			throw new BusinessException("OfferTemplate with code=" + offerCode
					+ " does not exists.");
		}

		if (offerTemplate.getServiceTemplates() == null
				|| offerTemplate.getServiceTemplates().size() < 1) {
			throw new BusinessException(
					"OfferTemplate doesn't have linked serviceTemplate.");
		}

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
					.getActivationServicePrice(userAccount.getBillingAccount(),
							st, subscriptionDate, new BigDecimal(1), param1,
							param2, param3, true));

		}

		BigDecimal ratedAmount = walletOperationService.getRatedAmount(
				provider, seller, null, null, userAccount.getBillingAccount(),
				null, startDate, endDate, true);

		BigDecimal spentCredit = servicesSum.add(ratedAmount);

		if (spentCredit.compareTo(creditLimit) > 0) {
			log.debug("Credit limit exceeded for seller code={}",
					seller.getCode());
			throw new BusinessException(
					"Credit limit exception for seller with code="
							+ seller.getCode());
		}

		// #2 Create a reservation (store UA), status=OPEN.
		Reservation reservation = new Reservation();
		reservation.setStatus(ReservationStatus.OPEN);
		reservation.setUserAccount(userAccount);
		reservationService.create(reservation);

		// #3 Create the reserved wallet operation. Not associated to charge,
		// status=RESERVED, associated to the reservation, amount=servicesSum.
		TradingCurrency currency = userAccount.getBillingAccount()
				.getCustomerAccount().getTradingCurrency();
		WalletReservation walletReservation = new WalletReservation();
		walletReservation.setReservation(reservation);
		walletReservation.setStatus(WalletOperationStatusEnum.RESERVED);

		walletReservation.setSubscriptionDate(null);
		walletReservation.setOperationDate(new Date());
		walletReservation.setParameter1(param1);
		walletReservation.setParameter2(param2);
		walletReservation.setParameter3(param3);
		walletReservation.setChargeInstance(null);
		walletReservation.setCode(null);
		walletReservation.setSeller(userAccount.getBillingAccount()
				.getCustomerAccount().getCustomer().getSeller());
		// FIXME: get the wallet from the ServiceUsageChargeTemplate
		walletReservation.setWallet(userAccount.getWallet());
		walletReservation.setQuantity(new BigDecimal(1));
		walletReservation.setStartDate(null);
		walletReservation.setEndDate(null);
		walletReservation.setCurrency(currency.getCurrency());
		walletReservation.setProvider(provider);
		// walletReservation.setTaxPercent(tax.getPercent());
		// if (chargeInstance.getCounter() != null) {
		// walletOperation.setCounter(chargeInstance.getCounter());
		// }

		walletReservationService.create(walletReservation, null, provider);

		// #4 Return the reservationId.
		return reservation.getId();
	}

	public void cancelCredit() {

	}

	public void confirmCredit() {

	}

}
