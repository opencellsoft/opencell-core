package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

@Stateless
public class PaymentMethodApi extends BaseApi {

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private PaymentMethodService paymentMethodService;

    public Long create(PaymentMethodDto paymentMethodDto) throws InvalidParameterException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
        paymentMethodDto.validate(true);
        CustomerAccount customerAccount = customerAccountService.findByCode(paymentMethodDto.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, paymentMethodDto.getCustomerAccountCode());
        }

        PaymentMethod paymentMethod = paymentMethodDto.fromDto(customerAccount);
        paymentMethodService.create(paymentMethod);
        return paymentMethod.getId();
    }

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

    public List<PaymentMethodDto> list(Long customerAccountId, String customerAccountCode) throws MissingParameterException, EntityDoesNotExistsException {
        return list(customerAccountId, customerAccountCode, null,null,null,null,null,null,null);
    }

    public List<PaymentMethodDto> list(Long customerAccountId, String customerAccountCode, PaymentMethodEnum type, Boolean isPreferred, String info1, String info2,
	    String info3, String info4, String info5)
            throws MissingParameterException, EntityDoesNotExistsException {

       
        CustomerAccount customerAccount = null;

        if (!StringUtils.isBlank(customerAccountId)) {
            customerAccount = customerAccountService.findById(customerAccountId);
        }
        if (!StringUtils.isBlank(customerAccountCode)) {
            customerAccount = customerAccountService.findByCode(customerAccountCode);
        }
        List<PaymentMethodDto> paymentMethodDtos = new ArrayList<PaymentMethodDto>();        
        List<PaymentMethod> paymentMethods = paymentMethodService.list(customerAccount, type, isPreferred, info1, info2, info3, info4, info5);
        if(paymentMethods != null) {
            for (PaymentMethod paymentMethod : paymentMethods) {
                paymentMethodDtos.add(new PaymentMethodDto(paymentMethod));
            }
        }
        return paymentMethodDtos;
    }

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