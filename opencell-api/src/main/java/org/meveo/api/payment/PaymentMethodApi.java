package org.meveo.api.payment;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;
import org.primefaces.model.SortOrder;

/**
 * The CRUD Api for PaymentMethod.
 */
@Stateless
public class PaymentMethodApi extends BaseApi {

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

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
    public Long create(PaymentMethodDto paymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
        paymentMethodDto.validate(true);
        CustomerAccount customerAccount = customerAccountService.findByCode(paymentMethodDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, paymentMethodDto.getCustomerAccountCode());
        }

        PaymentMethod paymentMethod = paymentMethodDto.fromDto(customerAccount, currentUser);
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
}