package org.meveo.api.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.billing.AmountsDto;
import org.meveo.api.dto.billing.FindWalletOperationsDto;
import org.meveo.api.dto.billing.WalletBalanceDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.billing.WalletReservationDto;
import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletReservationService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.primefaces.model.SortOrder;

/**
 * Wallet operation and balance related API
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0.1
 **/
@Stateless
public class WalletApi extends BaseApi {

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @Inject
    private WalletTemplateService walletTemplateService;

    @Inject
    private WalletService walletService;

    @Inject
    private WalletReservationService walletReservationService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private SellerService sellerService;

    @Inject
    private CustomerService customerService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private CurrencyService currencyService;

    /**
     * Calculate current (open or reserved) wallet balance at a given level
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Current balance (open or reserved) amount
     * @throws MeveoApiException API exception
     * @throws BusinessException Business exception
     */
    public AmountsDto getCurrentAmount(WalletBalanceDto calculateParameters) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(calculateParameters.getSellerCode()) && StringUtils.isBlank(calculateParameters.getCustomerCode())
                && StringUtils.isBlank(calculateParameters.getCustomerAccountCode()) && StringUtils.isBlank(calculateParameters.getBillingAccountCode())
                && StringUtils.isBlank(calculateParameters.getUserAccountCode())) {
            missingParameters.add("either sellerCode, customerCode, customerAccountCode, billingAccountCode or userAccountCode should be provided");
        }
        handleMissingParameters();

        Seller seller = null;
        Customer customer = null;
        CustomerAccount customerAccount = null;
        BillingAccount billingAccount = null;
        UserAccount userAccount = null;

        if (!StringUtils.isBlank(calculateParameters.getSellerCode())) {
            seller = sellerService.findByCode(calculateParameters.getSellerCode());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, calculateParameters.getSellerCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getCustomerCode())) {
            customer = customerService.findByCode(calculateParameters.getCustomerCode());
            if (customer == null) {
                throw new EntityDoesNotExistsException(Customer.class, calculateParameters.getCustomerCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getCustomerAccountCode())) {
            customerAccount = customerAccountService.findByCode(calculateParameters.getCustomerAccountCode());
            if (customerAccount == null) {
                throw new EntityDoesNotExistsException(CustomerAccount.class, calculateParameters.getCustomerAccountCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getBillingAccountCode())) {
            billingAccount = billingAccountService.findByCode(calculateParameters.getBillingAccountCode());
            if (billingAccount == null) {
                throw new EntityDoesNotExistsException(BillingAccount.class, calculateParameters.getBillingAccountCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getUserAccountCode())) {
            userAccount = userAccountService.findByCode(calculateParameters.getUserAccountCode());
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, calculateParameters.getUserAccountCode());
            }
        }

        return new AmountsDto(walletReservationService.getCurrentBalance(seller, customer, customerAccount, billingAccount, userAccount, calculateParameters.getStartDate(),
            calculateParameters.getEndDate()));
    }

    /**
     * Calculate reserved wallet balance at a given level
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Reserved balance amount
     * @throws MeveoApiException API exception
     * @throws BusinessException Business exception
     */
    public AmountsDto getReservedAmount(WalletBalanceDto calculateParameters) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(calculateParameters.getSellerCode()) && StringUtils.isBlank(calculateParameters.getCustomerCode())
                && StringUtils.isBlank(calculateParameters.getCustomerAccountCode()) && StringUtils.isBlank(calculateParameters.getBillingAccountCode())
                && StringUtils.isBlank(calculateParameters.getUserAccountCode())) {
            missingParameters.add("either sellerCode, customerCode, customerAccountCode, billingAccountCode or userAccountCode should be provided");
        }

        handleMissingParameters();

        Seller seller = null;
        Customer customer = null;
        CustomerAccount customerAccount = null;
        BillingAccount billingAccount = null;
        UserAccount userAccount = null;

        if (!StringUtils.isBlank(calculateParameters.getSellerCode())) {
            seller = sellerService.findByCode(calculateParameters.getSellerCode());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, calculateParameters.getSellerCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getCustomerCode())) {
            customer = customerService.findByCode(calculateParameters.getCustomerCode());
            if (customer == null) {
                throw new EntityDoesNotExistsException(Customer.class, calculateParameters.getCustomerCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getCustomerAccountCode())) {
            customerAccount = customerAccountService.findByCode(calculateParameters.getCustomerAccountCode());
            if (customerAccount == null) {
                throw new EntityDoesNotExistsException(CustomerAccount.class, calculateParameters.getCustomerAccountCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getBillingAccountCode())) {
            billingAccount = billingAccountService.findByCode(calculateParameters.getBillingAccountCode());
            if (billingAccount == null) {
                throw new EntityDoesNotExistsException(BillingAccount.class, calculateParameters.getBillingAccountCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getUserAccountCode())) {
            userAccount = userAccountService.findByCode(calculateParameters.getUserAccountCode());
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, calculateParameters.getUserAccountCode());
            }
        }

        return new AmountsDto(walletReservationService.getReservedBalance(seller, customer, customerAccount, billingAccount, userAccount, calculateParameters.getStartDate(),
            calculateParameters.getEndDate()));
    }

    /**
     * Calculate open wallet balance at a given level
     * 
     * @param calculateParameters Wallet balance calculation parameters
     * @return Open balance amount
     * @throws MeveoApiException API exception
     * @throws BusinessException Business exception
     */
    public AmountsDto getOpenAmount(WalletBalanceDto calculateParameters) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(calculateParameters.getSellerCode()) && StringUtils.isBlank(calculateParameters.getCustomerCode())
                && StringUtils.isBlank(calculateParameters.getCustomerAccountCode()) && StringUtils.isBlank(calculateParameters.getBillingAccountCode())
                && StringUtils.isBlank(calculateParameters.getUserAccountCode())) {
            missingParameters.add("either sellerCode, customerCode, customerAccountCode, billingAccountCode or userAccountCode should be provided");
        }

        handleMissingParameters();

        Seller seller = null;
        Customer customer = null;
        CustomerAccount customerAccount = null;
        BillingAccount billingAccount = null;
        UserAccount userAccount = null;

        if (!StringUtils.isBlank(calculateParameters.getSellerCode())) {
            seller = sellerService.findByCode(calculateParameters.getSellerCode());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, calculateParameters.getSellerCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getCustomerCode())) {
            customer = customerService.findByCode(calculateParameters.getCustomerCode());
            if (customer == null) {
                throw new EntityDoesNotExistsException(Customer.class, calculateParameters.getCustomerCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getCustomerAccountCode())) {
            customerAccount = customerAccountService.findByCode(calculateParameters.getCustomerAccountCode());
            if (customerAccount == null) {
                throw new EntityDoesNotExistsException(CustomerAccount.class, calculateParameters.getCustomerAccountCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getBillingAccountCode())) {
            billingAccount = billingAccountService.findByCode(calculateParameters.getBillingAccountCode());
            if (billingAccount == null) {
                throw new EntityDoesNotExistsException(BillingAccount.class, calculateParameters.getBillingAccountCode());
            }
        }

        if (!StringUtils.isBlank(calculateParameters.getUserAccountCode())) {
            userAccount = userAccountService.findByCode(calculateParameters.getUserAccountCode());
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, calculateParameters.getUserAccountCode());
            }
        }

        return new AmountsDto(walletReservationService.getOpenBalance(seller, customer, customerAccount, billingAccount, userAccount, calculateParameters.getStartDate(),
            calculateParameters.getEndDate()));
    }

    public Long createReservation(WalletReservationDto walletReservation) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(walletReservation.getSellerCode())) {
            missingParameters.add("sellerCode");
        }
        if (StringUtils.isBlank(walletReservation.getOfferCode())) {
            missingParameters.add("offerCode");
        }
        if (StringUtils.isBlank(walletReservation.getUserAccountCode())) {
            missingParameters.add("userAccountCode");
        }
        if (walletReservation.getSubscriptionDate() == null) {
            missingParameters.add("subscriptionDate");
        }
        if (walletReservation.getCreditLimit() == null) {
            missingParameters.add("creditLimit");
        }

        handleMissingParameters();

        return reservationService.createReservation(walletReservation.getSellerCode(), walletReservation.getOfferCode(), walletReservation.getUserAccountCode(),
            walletReservation.getSubscriptionDate(), walletReservation.getExpirationDate(), walletReservation.getCreditLimit(), walletReservation.getParam1(),
            walletReservation.getParam2(), walletReservation.getParam3(), walletReservation.isAmountWithTax());
    }

    public void updateReservation(WalletReservationDto walletReservation) throws MeveoApiException, BusinessException {

        if (walletReservation.getReservationId() == null) {
            missingParameters.add("reservationId");
        }
        if (StringUtils.isBlank(walletReservation.getSellerCode())) {
            missingParameters.add("sellerCode");
        }
        if (StringUtils.isBlank(walletReservation.getOfferCode())) {
            missingParameters.add("offerCode");
        }
        if (StringUtils.isBlank(walletReservation.getUserAccountCode())) {
            missingParameters.add("userAccountCode");
        }
        if (walletReservation.getSubscriptionDate() == null) {
            missingParameters.add("subscriptionDate");
        }
        if (walletReservation.getCreditLimit() == null) {
            missingParameters.add("creditLimit");
        }

        handleMissingParameters();

        reservationService.updateReservation(walletReservation.getReservationId(), walletReservation.getSellerCode(), walletReservation.getOfferCode(),
            walletReservation.getUserAccountCode(), walletReservation.getSubscriptionDate(), walletReservation.getExpirationDate(), walletReservation.getCreditLimit(),
            walletReservation.getParam1(), walletReservation.getParam2(), walletReservation.getParam3(), walletReservation.isAmountWithTax());
    }

    public void cancelReservation(Long reservationId) throws MeveoApiException, BusinessException {
        Reservation reservation = reservationService.findById(reservationId);
        if (reservation == null) {
            throw new MeveoApiException("Reservation with id=" + reservationId + " does not exists.");
        }

        reservationService.cancelReservation(reservation);

    }

    public BigDecimal confirmReservation(WalletReservationDto walletReservation) throws MeveoApiException, BusinessException {

        if (walletReservation.getReservationId() == null) {
            missingParameters.add("reservationId");
        }
        if (StringUtils.isBlank(walletReservation.getSellerCode())) {
            missingParameters.add("sellerCode");
        }
        if (StringUtils.isBlank(walletReservation.getOfferCode())) {
            missingParameters.add("offerCode");
        }
        if (walletReservation.getSubscriptionDate() == null) {
            missingParameters.add("subscriptionDate");
        }
        if (walletReservation.getCreditLimit() == null) {
            missingParameters.add("creditLimit");
        }

        handleMissingParameters();

        return reservationService.confirmReservation(walletReservation.getReservationId(), walletReservation.getSellerCode(), walletReservation.getOfferCode(),
            walletReservation.getSubscriptionDate(), walletReservation.getTerminationDate(), walletReservation.getParam1(), walletReservation.getParam2(),
            walletReservation.getParam3(), walletReservation.isAmountWithTax());
    }

    public void createOperation(WalletOperationDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getUserAccount()) && StringUtils.isBlank(postData.getSubscription())) {
            missingParameters.add("userAccount or subscription");
        }
        if (StringUtils.isBlank(postData.getChargeInstance()) && postData.getChargeInstanceId() == null) {
            missingParameters.add("chargeInstance or chargeInstanceId");
        }
        if (StringUtils.isBlank(postData.getCurrency())) {
            missingParameters.add("currency");
        }

        handleMissingParameters();

        Subscription subscription = null;
        UserAccount userAccount = null;
        if (!StringUtils.isBlank(postData.getSubscription())) {
            subscription = subscriptionService.findByCode(postData.getSubscription());
            if (subscription == null) {
                throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
            }
            userAccount = subscription.getUserAccount();

        } else {
            userAccount = userAccountService.findByCode(postData.getUserAccount());
            if (userAccount == null) {
                throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
            }
        }

        if (walletOperationService.findByUserAccountAndCode(postData.getCode(), userAccount) != null) {
            throw new EntityAlreadyExistsException(WalletOperation.class, postData.getCode());
        }

        WalletTemplate walletTemplate = null;
        if (!StringUtils.isBlank(postData.getWalletTemplate())) {
            if (!postData.getWalletTemplate().equals(WalletTemplate.PRINCIPAL)) {
                walletTemplate = walletTemplateService.findByCode(postData.getWalletTemplate());
                if (walletTemplate == null) {
                    throw new EntityDoesNotExistsException(WalletTemplate.class, postData.getWalletTemplate());
                }
            } else {
                walletTemplate = new WalletTemplate();
                walletTemplate.setCode(WalletTemplate.PRINCIPAL);
            }
        } else {
            walletTemplate = new WalletTemplate();
            walletTemplate.setCode(WalletTemplate.PRINCIPAL);
        }

        WalletInstance walletInstance = walletService.getWalletInstance(userAccount, walletTemplate, false);
        if (walletInstance == null) {
            throw new EntityDoesNotExistsException(WalletInstance.class, walletTemplate.getCode());
        }

        Currency currency = currencyService.findByCode(postData.getCurrency());
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCurrency());
        }

        ChargeInstance chargeInstance = null;
        if (postData.getChargeInstanceId() != null) {
            chargeInstance = chargeInstanceService.findById(postData.getChargeInstanceId());
            if (chargeInstance == null) {
                throw new EntityDoesNotExistsException(ChargeInstance.class, postData.getChargeInstanceId());
            } else if (subscription != null) {
                if (!subscription.equals(chargeInstance.getSubscription())) {
                    throw new InvalidParameterException("Charge instance " + postData.getChargeInstanceId() + " does not correspond to subscription " + subscription.getCode());
                }
            } else if (!userAccount.equals(chargeInstance.getUserAccount())) {
                throw new InvalidParameterException("Charge instance " + postData.getChargeInstanceId() + " does not correspond to user account " + userAccount.getCode());
            }

        } else {
            chargeInstance = chargeInstanceService.findByCodeAndSubscription(postData.getChargeInstance(), subscription);
            if (chargeInstance == null) {
                throw new EntityDoesNotExistsException(ChargeInstance.class, postData.getChargeInstance());
            }
        }
        WalletOperation walletOperation = new WalletOperation();
        walletOperation.setDescription(postData.getDescription());
        walletOperation.setCode(postData.getCode());
        if (subscription != null) {
            // walletOperation.setOfferCode(subscription.getOffer().getCode()); offerCode is set in walletOperation.setOfferTemplate
            walletOperation.setOfferTemplate(subscription.getOffer());
        }
        walletOperation.setSeller(userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller());
        walletOperation.setCurrency(currency);
        walletOperation.setWallet(walletInstance);
        walletOperation.setChargeInstance(chargeInstance);
        walletOperation.setType(postData.getType());
        walletOperation.setStatus(postData.getStatus());
        walletOperation.setCounter(null);
        walletOperation.setRatingUnitDescription(postData.getRatingUnitDescription());
        walletOperation.setTaxPercent(postData.getTaxPercent());
        walletOperation.setUnitAmountTax(postData.getUnitAmountTax());
        walletOperation.setUnitAmountWithoutTax(postData.getUnitAmountWithoutTax());
        walletOperation.setUnitAmountWithTax(postData.getUnitAmountWithTax());
        walletOperation.setQuantity(postData.getQuantity());
        walletOperation.setAmountTax(postData.getAmountTax());
        walletOperation.setAmountWithoutTax(postData.getAmountWithoutTax());
        walletOperation.setAmountWithTax(postData.getAmountWithTax());
        walletOperation.setParameter1(postData.getParameter1());
        walletOperation.setParameter2(postData.getParameter2());
        walletOperation.setParameter3(postData.getParameter3());
        walletOperation.setParameterExtra(postData.getParameterExtra());
        walletOperation.setOrderNumber(postData.getOrderNumber());
        walletOperation.setStartDate(postData.getStartDate());
        walletOperation.setEndDate(postData.getEndDate());
        walletOperation.setSubscriptionDate(postData.getSubscriptionDate());
        walletOperation.setOperationDate(postData.getOperationDate() == null ? new Date() : postData.getOperationDate());
        if (chargeInstance.getInvoicingCalendar() != null) {
            Calendar cal = chargeInstance.getInvoicingCalendar();
            cal.setInitDate(postData.getSubscriptionDate());
            walletOperation.setInvoicingDate(cal.nextCalendarDate(walletOperation.getOperationDate()));
        }

        walletOperationService.create(walletOperation);

    }

    public FindWalletOperationsResponseDto findOperations(FindWalletOperationsDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        if (postData != null) {
            if (StringUtils.isBlank(postData.getUserAccount())) {
                missingParameters.add("userAccount");
                handleMissingParameters();
            }

            pagingAndFiltering.addFilter("wallet.userAccount.code", postData.getUserAccount());

            if (!StringUtils.isBlank(postData.getWalletTemplate()) && !postData.getWalletTemplate().equals(WalletTemplate.PRINCIPAL)) {
                pagingAndFiltering.addFilter("wallet.walletTemplate.code", postData.getWalletTemplate());
            } else {
                pagingAndFiltering.addFilter("wallet.code", WalletTemplate.PRINCIPAL);
            }

            pagingAndFiltering.addFilter("status", postData.getStatus());
            pagingAndFiltering.addFilter("chargeInstance.code", postData.getChargeTemplateCode());
            pagingAndFiltering.addFilter("fromRange operationDate", postData.getFromDate());
            pagingAndFiltering.addFilter("toRange operationDate", postData.getToDate());
            pagingAndFiltering.addFilter("offerCode", postData.getOfferTemplateCode());
            pagingAndFiltering.addFilter("orderNumber", postData.getOrderNumber());
            pagingAndFiltering.addFilter("parameter1", postData.getParameter1());
            pagingAndFiltering.addFilter("parameter2", postData.getParameter2());
            pagingAndFiltering.addFilter("parameter3", postData.getParameter3());
            pagingAndFiltering.addFilter("chargeInstance.subscription.code", postData.getSubscriptionCode());

        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null, pagingAndFiltering, WalletOperation.class);

        Long totalCount = walletOperationService.count(paginationConfig);

        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<WalletOperation> walletOperations = walletOperationService.list(paginationConfig);
            for (WalletOperation wo : walletOperations) {
                result.getWalletOperations().add(new WalletOperationDto(wo));
            }
        }

        return result;
    }

    public void create(WalletTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(postData.getWalletType())) {
            missingParameters.add("walletType");
        }

        handleMissingParameters();

        if (walletTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(WalletTemplate.class, postData.getCode());
        }

        WalletTemplate wt = new WalletTemplate();
        wt.setCode(postData.getCode());
        wt.setDescription(postData.getDescription());
        wt.setWalletType(postData.getWalletType());
        wt.setConsumptionAlertSet(postData.isConsumptionAlertSet());
        wt.setFastRatingLevel(postData.getFastRatingLevel());
        wt.setLowBalanceLevel(postData.getLowBalanceLevel());

        walletTemplateService.create(wt);

    }

    public void update(WalletTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(postData.getWalletType())) {
            missingParameters.add("walletType");
        }

        handleMissingParameters();

        WalletTemplate wt = walletTemplateService.findByCode(postData.getCode());
        if (wt == null) {
            throw new EntityDoesNotExistsException(WalletTemplate.class, postData.getCode());
        }

        wt.setDescription(postData.getDescription());
        wt.setWalletType(postData.getWalletType());

        wt.setConsumptionAlertSet(postData.isConsumptionAlertSet());
        wt.setFastRatingLevel(postData.getFastRatingLevel());
        wt.setLowBalanceLevel(postData.getLowBalanceLevel());

        walletTemplateService.update(wt);
    }

    public WalletTemplateDto find(String walletTemplateCode) throws MeveoApiException {
        if (StringUtils.isBlank(walletTemplateCode)) {
            missingParameters.add("walletTemplateCode");
            handleMissingParameters();
        }
        WalletTemplate wt = walletTemplateService.findByCode(walletTemplateCode);
        if (wt == null) {
            throw new EntityDoesNotExistsException(WalletTemplate.class, walletTemplateCode);
        }

        return new WalletTemplateDto(wt);
    }

    public void remove(String walletTemplateCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(walletTemplateCode)) {
            missingParameters.add("walletTemplateCode");
            handleMissingParameters();
        }

        WalletTemplate wt = walletTemplateService.findByCode(walletTemplateCode);
        if (wt == null) {
            throw new EntityDoesNotExistsException(WalletTemplate.class, walletTemplateCode);
        }

        walletTemplateService.remove(wt);
    }

    /**
     * Create or update walletTemplate.
     * 
     * @param postData wallet template infos
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception/
     */
    public void createOrUpdate(WalletTemplateDto postData) throws MeveoApiException, BusinessException {
        if (walletTemplateService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
}