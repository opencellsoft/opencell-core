package org.meveo.api.payment;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.message.exception.InvalidDTOException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;
import org.primefaces.model.SortOrder;

/**
 * The CRUD Api for PaymentMethod.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @lastModifiedVersion 5.0
 */
@Stateless
public class PaymentMethodApi extends BaseApi {

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The customer service. */
    @Inject
    private CustomerService customerService;

    /** The payment method service. */
    @Inject
    private PaymentMethodService paymentMethodService;

    /**
     * Creates the PaymentMethod.
     *
     * @param paymentMethodDto the payment method dto
     * @return the long
     * @throws InvalidParameterException the invalid parameter exception
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException the business exception
     */
    @SuppressWarnings("deprecation")
	public Long create(PaymentMethodDto paymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
        validate(paymentMethodDto, true);

        CustomerAccount customerAccount = customerAccountService.findByCode(paymentMethodDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, paymentMethodDto.getCustomerAccountCode());
        }

        PaymentMethod paymentMethod = paymentMethodDto.fromDto(customerAccount, currentUser);
        if(paymentMethodDto instanceof CardPaymentMethodDto) {
        	paymentMethod.setTokenId(paymentMethodDto.getTokenId());
        }
        paymentMethodService.create(paymentMethod);
        return paymentMethod.getId();
    }

    /**
     * Update the PaymentMethod.
     *
     * @param paymentMethodDto the payment method dto
     * @throws InvalidParameterException the invalid parameter exception
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException the business exception
     */
    public void update(PaymentMethodDto paymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(paymentMethodDto.getId())) {
            missingParameters.add("Id");
        }
        handleMissingParameters();

        PaymentMethod paymentMethod = null;
        paymentMethod = paymentMethodService.findById(paymentMethodDto.getId());
        if (paymentMethod == null) {
            throw new EntityDoesNotExistsException(PaymentMethod.class, paymentMethodDto.getId());
        }

        paymentMethodService.update(paymentMethodDto.updateFromDto(paymentMethod));
    }

    /**
     * Removes the PaymentMethod.
     *
     * @param id the id
     * @throws InvalidParameterException the invalid parameter exception
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException the business exception
     */
    public void remove(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (id == null) {
            missingParameters.add("id");
        }
        handleMissingParameters();
        PaymentMethod paymentMethod = null;
        if (id != null) {
            paymentMethod = (PaymentMethod) paymentMethodService.findById(id);
        }
        if (paymentMethod == null) {
            throw new EntityDoesNotExistsException(DDPaymentMethod.class, id);
        }
        paymentMethodService.remove(paymentMethod);
    }

    /**
     * List the PaymentMethods for given criteria.
     *
     * @param customerAccountId the customer account id
     * @param customerAccountCode the customer account code
     * @return the payment method tokens dto
     * @throws InvalidParameterException the invalid parameter exception
     */
    @Deprecated // used only for listCardPaymentMethods for the moment, please use list(PagingAndFiltering pagingAndFiltering) instead.
    public PaymentMethodTokensDto list(Long customerAccountId, String customerAccountCode) throws InvalidParameterException {
        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering();
        Map<String, Object> filters = new HashedMap<String, Object>();
        filters.put("paymentType", PaymentMethodEnum.CARD);
        if (!StringUtils.isBlank(customerAccountCode)) {
            filters.put("customerAccount.code", customerAccountCode);
        }
        if (customerAccountId != null) {
            filters.put("customerAccount.id", customerAccountId);
        }
        pagingAndFiltering.setFilters(filters);
        return list(pagingAndFiltering);
    }

    /**
     * List the PaymentMethods for given criteria.
     *
     * @param pagingAndFiltering the paging and filtering
     * @return the payment method tokens dto
     * @throws InvalidParameterException the invalid parameter exception
     */
    public PaymentMethodTokensDto list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
        PaymentMethodTokensDto result = new PaymentMethodTokensDto();
        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.DESCENDING, null, pagingAndFiltering, PaymentMethod.class);
        Long totalCount = paymentMethodService.count(paginationConfig);
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        if (totalCount > 0) {
            List<PaymentMethod> PaymentMethods = paymentMethodService.list(paginationConfig);
            for (PaymentMethod paymentMethod : PaymentMethods) {
                result.getPaymentMethods().add(new PaymentMethodDto(paymentMethod));
            }
        }
        return result;
    }

    /**
     * Find the paymentMethod.
     *
     * @param id the id
     * @return the payment method dto
     * @throws InvalidParameterException the invalid parameter exception
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws BusinessException the business exception
     */
    public PaymentMethodDto find(Long id) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (id == null) {
            missingParameters.add("id");
        }
        handleMissingParameters();
        PaymentMethod paymentMethod = paymentMethodService.findById(id);
        if (paymentMethod == null) {
            throw new EntityDoesNotExistsException(PaymentMethod.class, id);
        }
        return new PaymentMethodDto(paymentMethod);

    }

    /**
     * Validate the PaymentMethodDto.
     * 
     * @param paymentMethodDto paymentMethodDto to check.
     * @param isRoot is the root Dto or sub Dto.
     */
    public void validate(PaymentMethodDto paymentMethodDto, boolean isRoot) {
        PaymentMethodEnum type = paymentMethodDto.getPaymentMethodType();
        if (type == null) {
            throw new InvalidDTOException("Missing payment method type");
        }
        if (isRoot && StringUtils.isBlank(paymentMethodDto.getCustomerAccountCode())) {
            throw new InvalidDTOException("Missing customerAccountCode");
        }
        if (type == PaymentMethodEnum.CARD) {
            int numberLength = paymentMethodDto.getCardNumber().length();
            CreditCardTypeEnum cardType = paymentMethodDto.getCardType();
            if (StringUtils.isBlank(paymentMethodDto.getCardNumber()) || (numberLength != 16 && cardType != CreditCardTypeEnum.AMERICAN_EXPRESS)
                    || (numberLength != 15 && cardType == CreditCardTypeEnum.AMERICAN_EXPRESS)) {
                throw new InvalidDTOException("Invalid cardNumber");
            }
            if (StringUtils.isBlank(paymentMethodDto.getOwner())) {
                throw new InvalidDTOException("Missing Owner");
            }
            if (StringUtils.isBlank(paymentMethodDto.getMonthExpiration()) || StringUtils.isBlank(paymentMethodDto.getYearExpiration())) {
                throw new InvalidDTOException("Missing expiryDate");
            }

            return;
        }
        if (type == PaymentMethodEnum.DIRECTDEBIT) {
            validateBankCoordinates(paymentMethodDto);
            return;
        }

    }

    /**
     * Check bank coordinates fields.
     *
     * @param paymentMethodDto the paymentMethodDto to check.
     */
    private void validateBankCoordinates(PaymentMethodDto paymentMethodDto) {
        BankCoordinatesDto bankCoordinates = paymentMethodDto.getBankCoordinates();

        if (paymentMethodDto.getPaymentMethodType() == PaymentMethodEnum.DIRECTDEBIT) {
            // Start compatibility with pre-4.6 versions
            if (paymentMethodDto.getMandateIdentification() == null && bankCoordinates == null) {
                throw new InvalidDTOException("Missing Bank coordinates or MandateIdentification.");
            }

            if (bankCoordinates != null) {
                if (StringUtils.isBlank(bankCoordinates.getAccountOwner())) {
                    throw new InvalidDTOException("Missing account owner.");
                }

                if (StringUtils.isBlank(bankCoordinates.getBankName())) {
                    throw new InvalidDTOException("Missing bank name.");
                }
                CustomerAccount customerAccount = customerAccountService.findByCode(paymentMethodDto.getCustomerAccountCode());
                org.meveo.model.crm.Customer cust = null;
                if (customerAccount == null) {
                    cust = customerService.findByCode(paymentMethodDto.getCustomerCode());
                } else {
                    cust = customerAccount.getCustomer();
                }
                if (StringUtils.isBlank(bankCoordinates.getBic()) && customerService.isBicRequired(cust, bankCoordinates.getIban())) {
                    throw new InvalidDTOException("Missing BIC.");
                }

                if (StringUtils.isBlank(bankCoordinates.getIban())) {
                    throw new InvalidDTOException("Missing IBAN.");
                }
            } else {
                if (StringUtils.isBlank(paymentMethodDto.getMandateIdentification())) {
                    throw new InvalidDTOException("Missing mandate identification.");
                }
                if (paymentMethodDto.getMandateDate() == null) {
                    throw new InvalidDTOException("Missing mandate date.");
                }
            }
            // End of compatibility with pre-4.6 versions
        }

    }

    /**
     * Enable or disable Payment method
     * 
     * @param id Payment method identifier
     * @param enable Should Trading currency be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(Long id, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (id == null) {
            missingParameters.add("id");
        }
        handleMissingParameters();

        PaymentMethod paymentMethod = paymentMethodService.findById(id);
        if (paymentMethod == null) {
            throw new EntityDoesNotExistsException(PaymentMethod.class, id);
        }
        if (enable) {
            paymentMethodService.enable(paymentMethod);
        } else {
            paymentMethodService.disable(paymentMethod);
        }
    }

    public String getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput)  throws BusinessException {
        return paymentMethodService.getHostedCheckoutUrl(hostedCheckoutInput);
    }

}