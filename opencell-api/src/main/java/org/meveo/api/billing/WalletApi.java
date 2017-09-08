package org.meveo.api.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.billing.FindWalletOperationsDto;
import org.meveo.api.dto.billing.WalletBalanceDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.billing.WalletReservationDto;
import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.api.dto.response.Paging;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletReservationService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
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
    private UserAccountService userAccountService;

    @Inject
    private SellerService sellerService;

    @Inject
    private CurrencyService currencyService;

    public BigDecimal getCurrentAmount(WalletBalanceDto walletBalance) throws MeveoApiException {
        if (!StringUtils.isBlank(walletBalance.getSellerCode()) && !StringUtils.isBlank(walletBalance.getUserAccountCode())) {
            try {
                if (walletBalance.isAmountWithTax()) {
                    return walletReservationService.getCurrentBalanceWithTax(walletBalance.getSellerCode(), walletBalance.getUserAccountCode(), walletBalance.getStartDate(),
                        walletBalance.getEndDate());
                } else {
                    return walletReservationService.getCurrentBalanceWithoutTax(walletBalance.getSellerCode(), walletBalance.getUserAccountCode(), walletBalance.getStartDate(),
                        walletBalance.getEndDate());
                }
            } catch (BusinessException e) {
                throw new MeveoApiException(e.getMessage());
            }
        } else {
            StringBuilder sb = new StringBuilder("The following parameters are required ");
            List<String> missingFields = new ArrayList<String>();
            if (StringUtils.isBlank(walletBalance.getSellerCode())) {
                missingFields.add("sellerCode");
            }
            if (StringUtils.isBlank(walletBalance.getUserAccountCode())) {
                missingFields.add("userAccountCode");
            }
            if (missingFields.size() > 1) {
                sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
            } else {
                sb.append(missingFields.get(0));
            }
            sb.append(".");

            throw new MissingParameterException(sb.toString());
        }
    }

    public BigDecimal getReservedAmount(WalletBalanceDto walletBalance) throws MeveoApiException {
        if (!StringUtils.isBlank(walletBalance.getSellerCode()) && !StringUtils.isBlank(walletBalance.getUserAccountCode())) {
            try {
                if (walletBalance.isAmountWithTax()) {
                    return walletReservationService.getReservedBalanceWithTax(walletBalance.getSellerCode(), walletBalance.getUserAccountCode(), walletBalance.getStartDate(),
                        walletBalance.getEndDate());
                } else {
                    return walletReservationService.getReservedBalanceWithoutTax(walletBalance.getSellerCode(), walletBalance.getUserAccountCode(), walletBalance.getStartDate(),
                        walletBalance.getEndDate());
                }
            } catch (BusinessException e) {
                throw new MeveoApiException(e.getMessage());
            }
        } else {
            StringBuilder sb = new StringBuilder("The following parameters are required ");
            List<String> missingFields = new ArrayList<String>();
            if (StringUtils.isBlank(walletBalance.getSellerCode())) {
                missingFields.add("sellerCode");
            }
            if (StringUtils.isBlank(walletBalance.getUserAccountCode())) {
                missingFields.add("userAccountCode");
            }
            if (missingFields.size() > 1) {
                sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
            } else {
                sb.append(missingFields.get(0));
            }
            sb.append(".");

            throw new MissingParameterException(sb.toString());
        }
    }

    public BigDecimal getOpenAmount(WalletBalanceDto walletBalance) throws MeveoApiException {
        if (!StringUtils.isBlank(walletBalance.getSellerCode()) && !StringUtils.isBlank(walletBalance.getUserAccountCode())) {

            try {
                if (walletBalance.isAmountWithTax()) {
                    return walletReservationService.getOpenBalanceWithTax(walletBalance.getSellerCode(), walletBalance.getUserAccountCode(), walletBalance.getStartDate(),
                        walletBalance.getEndDate());
                } else {
                    return walletReservationService.getOpenBalanceWithoutTax(walletBalance.getSellerCode(), walletBalance.getUserAccountCode(), walletBalance.getStartDate(),
                        walletBalance.getEndDate());
                }
            } catch (BusinessException e) {
                throw new MeveoApiException(e.getMessage());
            }
        } else {
            StringBuilder sb = new StringBuilder("The following parameters are required ");
            List<String> missingFields = new ArrayList<String>();

            if (StringUtils.isBlank(walletBalance.getSellerCode())) {
                missingFields.add("sellerCode");
            }
            if (StringUtils.isBlank(walletBalance.getUserAccountCode())) {
                missingFields.add("userAccountCode");
            }
            if (missingFields.size() > 1) {
                sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
            } else {
                sb.append(missingFields.get(0));
            }
            sb.append(".");

            throw new MissingParameterException(sb.toString());
        }
    }

    public Long createReservation(WalletReservationDto walletReservation) throws MeveoApiException {

        if (!StringUtils.isBlank(walletReservation.getSellerCode()) && !StringUtils.isBlank(walletReservation.getOfferCode())
                && !StringUtils.isBlank(walletReservation.getUserAccountCode()) && walletReservation.getSubscriptionDate() != null && walletReservation.getCreditLimit() != null) {

            try {
                return reservationService.createReservation(walletReservation.getSellerCode(), walletReservation.getOfferCode(), walletReservation.getUserAccountCode(),
                    walletReservation.getSubscriptionDate(), walletReservation.getExpirationDate(), walletReservation.getCreditLimit(), walletReservation.getParam1(),
                    walletReservation.getParam2(), walletReservation.getParam3(), walletReservation.isAmountWithTax());
            } catch (BusinessException e) {
                throw new MeveoApiException(e.getMessage());
            }
        } else {
            StringBuilder sb = new StringBuilder("The following parameters are required ");
            List<String> missingFields = new ArrayList<String>();

            if (StringUtils.isBlank(walletReservation.getSellerCode())) {
                missingFields.add("sellerCode");
            }
            if (StringUtils.isBlank(walletReservation.getOfferCode())) {
                missingFields.add("offerCode");
            }
            if (StringUtils.isBlank(walletReservation.getUserAccountCode())) {
                missingFields.add("userAccountCode");
            }
            if (walletReservation.getSubscriptionDate() == null) {
                missingFields.add("subscriptionDate");
            }
            if (walletReservation.getCreditLimit() == null) {
                missingFields.add("creditLimit");
            }

            if (missingFields.size() > 1) {
                sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
            } else {
                sb.append(missingFields.get(0));
            }
            sb.append(".");

            throw new MissingParameterException(sb.toString());
        }
    }

    public void updateReservation(WalletReservationDto walletReservation) throws MeveoApiException {
        if (!StringUtils.isBlank(walletReservation.getSellerCode()) && !StringUtils.isBlank(walletReservation.getOfferCode())
                && !StringUtils.isBlank(walletReservation.getUserAccountCode()) && walletReservation.getSubscriptionDate() != null && walletReservation.getCreditLimit() != null) {

            try {
                reservationService.updateReservation(walletReservation.getReservationId(), walletReservation.getSellerCode(), walletReservation.getOfferCode(),
                    walletReservation.getUserAccountCode(), walletReservation.getSubscriptionDate(), walletReservation.getExpirationDate(), walletReservation.getCreditLimit(),
                    walletReservation.getParam1(), walletReservation.getParam2(), walletReservation.getParam3(), walletReservation.isAmountWithTax());
            } catch (BusinessException e) {
                throw new MeveoApiException(e.getMessage());
            }

        } else {
            StringBuilder sb = new StringBuilder("The following parameters are required ");
            List<String> missingFields = new ArrayList<String>();

            if (walletReservation.getReservationId() == null) {
                missingFields.add("reservationId");
            }
            if (StringUtils.isBlank(walletReservation.getSellerCode())) {
                missingFields.add("sellerCode");
            }
            if (StringUtils.isBlank(walletReservation.getOfferCode())) {
                missingFields.add("offerCode");
            }
            if (StringUtils.isBlank(walletReservation.getUserAccountCode())) {
                missingFields.add("userAccountCode");
            }
            if (walletReservation.getSubscriptionDate() == null) {
                missingFields.add("subscriptionDate");
            }
            if (walletReservation.getCreditLimit() == null) {
                missingFields.add("creditLimit");
            }

            if (missingFields.size() > 1) {
                sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
            } else {
                sb.append(missingFields.get(0));
            }
            sb.append(".");

            throw new MissingParameterException(sb.toString());
        }
    }

    public void cancelReservation(Long reservationId) throws MeveoApiException {
        Reservation reservation = reservationService.findById(reservationId);
        if (reservation == null) {
            throw new MeveoApiException("Reservation with id=" + reservationId + " does not exists.");
        }

        try {
            reservationService.cancelReservation(reservation);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
    }

    public BigDecimal confirmReservation(WalletReservationDto walletReservation) throws MeveoApiException {
        if (walletReservation.getReservationId() != null && !StringUtils.isBlank(walletReservation.getSellerCode()) && !StringUtils.isBlank(walletReservation.getOfferCode())
                && walletReservation.getSubscriptionDate() != null && walletReservation.getCreditLimit() != null) {

            try {
                return reservationService.confirmReservation(walletReservation.getReservationId(), walletReservation.getSellerCode(), walletReservation.getOfferCode(),
                    walletReservation.getSubscriptionDate(), walletReservation.getTerminationDate(), walletReservation.getParam1(), walletReservation.getParam2(),
                    walletReservation.getParam3(), walletReservation.isAmountWithTax());
            } catch (BusinessException e) {
                throw new MeveoApiException(e.getMessage());
            }

        } else {
            StringBuilder sb = new StringBuilder("The following parameters are required ");
            List<String> missingFields = new ArrayList<String>();

            if (walletReservation.getReservationId() == null) {
                missingFields.add("reservationId");
            }
            if (StringUtils.isBlank(walletReservation.getSellerCode())) {
                missingFields.add("sellerCode");
            }
            if (StringUtils.isBlank(walletReservation.getOfferCode())) {
                missingFields.add("offerCode");
            }
            if (walletReservation.getSubscriptionDate() == null) {
                missingFields.add("subscriptionDate");
            }
            if (walletReservation.getCreditLimit() == null) {
                missingFields.add("creditLimit");
            }

            if (missingFields.size() > 1) {
                sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
            } else {
                sb.append(missingFields.get(0));
            }
            sb.append(".");

            throw new MissingParameterException(sb.toString());
        }
    }

    public void createOperation(WalletOperationDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getUserAccount())) {
            missingParameters.add("userAccount");
        }
        if (StringUtils.isBlank(postData.getSubscription())) {
            missingParameters.add("subscription");
        }
        if (StringUtils.isBlank(postData.getChargeInstance())) {
            missingParameters.add("chargeInstance");
        }
        if (StringUtils.isBlank(postData.getSeller())) {
            missingParameters.add("seller");
        }

        handleMissingParameters();

        UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount());
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
        }

        if (walletOperationService.findByUserAccountAndCode(postData.getCode(), userAccount) != null) {
            throw new EntityAlreadyExistsException(WalletOperation.class, postData.getCode());
        }

        Subscription subscription = subscriptionService.findByCode(postData.getSubscription());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
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

        Seller seller = sellerService.findByCode(postData.getSeller());
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
        }

        Currency currency = currencyService.findByCode(postData.getCurrency());
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCurrency());
        }

        ChargeInstance chargeInstance = chargeInstanceService.findByCodeAndSubscription(postData.getChargeInstance(), subscription);
        if (chargeInstance == null) {
            throw new EntityDoesNotExistsException(ChargeInstance.class, postData.getChargeInstance());
        }

        WalletOperation walletOperation = new WalletOperation();
        walletOperation.setDescription(postData.getDescription());
        walletOperation.setCode(postData.getCode());
        walletOperation.setOfferCode(subscription.getOffer().getCode());
        walletOperation.setSeller(seller);
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

    public FindWalletOperationsResponseDto findOperations(FindWalletOperationsDto postData, Paging paging) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getUserAccount())) {
            missingParameters.add("userAccount");
            handleMissingParameters();
        }

        WalletTemplate walletTemplate = null;
        WalletInstance walletInstance = null;
        UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount());
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
        }

        if (!StringUtils.isBlank(postData.getWalletTemplate()) && !postData.getWalletTemplate().equals(WalletTemplate.PRINCIPAL)) {
            walletTemplate = walletTemplateService.findByCode(postData.getWalletTemplate());
            if (walletTemplate == null) {
                throw new EntityDoesNotExistsException(WalletTemplate.class, postData.getWalletTemplate());
            }
        } else {
            walletInstance = walletService.findByUserAccountAndCode(userAccount, WalletTemplate.PRINCIPAL);
        }

        Map<String, Object> filters = new HashMap<>();

        if (walletTemplate != null) {
            filters.put("wallet.walletTemplate", walletTemplate);
            filters.put("wallet.userAccount", userAccount);
        } else {
            filters.put("wallet", walletInstance);
        }
        filters.put("status", postData.getStatus());
        filters.put("chargeInstance.code", postData.getChargeTemplateCode());
        filters.put("fromRange-operationDate", postData.getFromDate());
        filters.put("toRange-operationDate", postData.getToDate());
        filters.put("offerCode", postData.getOfferTemplateCode());
        filters.put("orderNumber", postData.getOrderNumber());
        filters.put("parameter1", postData.getParameter1());
        filters.put("parameter2", postData.getParameter2());
        filters.put("parameter3", postData.getParameter3());
        filters.put("chargeInstance.subscription.code", postData.getSubscriptionCode());

        PaginationConfiguration paginationConfig = new PaginationConfiguration(paging != null ? paging.getOffset() : null, paging != null ? paging.getLimit() : null, filters,
            null, Arrays.asList("wallet"), paging != null && paging.getSortBy() != null ? paging.getSortBy() : "id",
            paging != null && paging.getSortOrder() != null ? SortOrder.valueOf(paging.getSortOrder().name()) : SortOrder.ASCENDING);

        Long totalCount = walletOperationService.count(paginationConfig);

        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();
        result.setPaging(paging != null ? paging : new Paging());
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
     * Create or update walletTemplate
     * 
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void createOrUpdate(WalletTemplateDto postData) throws MeveoApiException, BusinessException {
        if (walletTemplateService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
}