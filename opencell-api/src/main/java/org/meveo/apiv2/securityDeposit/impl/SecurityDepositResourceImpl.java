package org.meveo.apiv2.securityDeposit.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.apiv2.securityDeposit.*;
import org.meveo.apiv2.securityDeposit.resource.SecurityDepositResource;
import org.meveo.apiv2.securityDeposit.service.SecurityDepositApiService;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;

public class SecurityDepositResourceImpl implements SecurityDepositResource {

    @Inject
    SecurityDepositApiService securityDepositApiService;

    @Inject
    SecurityDepositService securityDepositService;
    
    @Inject
    private AuditLogService auditLogService;
    
    @Inject
    private PaymentApi paymentApi;
    
    @Inject
    private PaymentService paymentService;
    
    @Inject
    private InvoiceApi invoiceApi;
    
    SecurityDepositMapper securityDepositMapper = new SecurityDepositMapper();

    @Override
    public Response instantiate(SecurityDepositInput securityDepositInput) {
        SecurityDeposit result = securityDepositApiService.instantiate(securityDepositMapper.toEntity(securityDepositInput)).get();
        try {
            invoiceApi.validateInvoice(result.getSecurityDepositInvoice().getId(), true, false, false);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return Response.ok(ImmutableSecurityDepositSuccessResponse
                .builder()
                .status("SUCCESS")
                .newSecurityDeposit(securityDepositMapper.toResource(result))
                .build()
            ).build();
    }
    
    @Override
    public Response update(Long id, SecurityDepositInput securityDepositInput) {
        SecurityDeposit securityDepositToUpdate = securityDepositService.findById(id);
        
        if(securityDepositToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit template with id "+id+" does not exist.");
        }
        
        //Not Allowed in Update
        String msgErrValidation = "";
        if(securityDepositInput.getStatus() != null) {
            msgErrValidation = "Status ";
        }  
        if(securityDepositInput.getCurrentBalance() != null) {
            msgErrValidation += "".equals(msgErrValidation) ? "Current Balance " : "- Current Balance ";
        }
        if(securityDepositInput.getRefundReason() != null) {
            msgErrValidation += "".equals(msgErrValidation) ? "Refund Reason " : "- Refund Reason ";
        }
        if(securityDepositInput.getCancelReason() != null) {
            msgErrValidation += "".equals(msgErrValidation) ? "Cancel Reason " : "- Cancel Reason ";
        }
        if(!"".equals(msgErrValidation)) {
            throw new ValidationException(msgErrValidation + "not allowed for Update.");
        }

        BigDecimal oldAmountSD = securityDepositToUpdate.getAmount();
        securityDepositToUpdate = securityDepositMapper.toEntity(securityDepositToUpdate, securityDepositInput);
        securityDepositService.checkParameters(securityDepositToUpdate, securityDepositInput, oldAmountSD);
        securityDepositApiService.linkRealEntities(securityDepositToUpdate);        
        securityDepositService.update(securityDepositToUpdate);
        auditLogService.trackOperation("UPDATE", new Date(), securityDepositToUpdate, securityDepositToUpdate.getCode());
        return Response.ok().entity(buildResponse(securityDepositMapper.toResource(securityDepositToUpdate))).build();

    }

    @Override
    public Response refund(Long id, SecurityDepositRefundInput securityDepositInput) {
        SecurityDeposit securityDepositToUpdate = securityDepositService.findById(id);
        if(securityDepositToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit with id " + id + " does not exist.");
        }
        
        if(!SecurityDepositStatusEnum.LOCKED.equals(securityDepositToUpdate.getStatus()) 
                && !SecurityDepositStatusEnum.UNLOCKED.equals(securityDepositToUpdate.getStatus())
                && !SecurityDepositStatusEnum.HOLD.equals(securityDepositToUpdate.getStatus())){
            throw new EntityDoesNotExistsException("The refund is possible ONLY if the status of the security deposit is at 'Locked' or 'Unlocked' or 'HOLD'");
        }    

        securityDepositService.refund(securityDepositToUpdate, securityDepositInput.getRefundReason(), SecurityDepositOperationEnum.REFUND_SECURITY_DEPOSIT, SecurityDepositStatusEnum.REFUNDED, "REFUND");
        return Response.ok().entity(buildResponse(securityDepositMapper.toResource(securityDepositToUpdate))).build();
    }
    
    @Override
    public Response cancel(Long id, SecurityDepositCancelInput securityDepositInput) {
        SecurityDeposit securityDepositToUpdate = securityDepositService.findById(id);
        if(securityDepositToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit with id " + id + " does not exist.");
        }
        
        if(SecurityDepositStatusEnum.CANCELED.equals(securityDepositToUpdate.getStatus())){
            throw new EntityDoesNotExistsException("The Cancel is not possible if the status of the security deposit is at 'Cancel'");
        } 
        
        securityDepositService.refund(securityDepositToUpdate, securityDepositInput.getCancelReason(), SecurityDepositOperationEnum.CANCEL_SECURITY_DEPOSIT, SecurityDepositStatusEnum.CANCELED, "CANCEL");
        return Response.ok().entity(buildResponse(securityDepositMapper.toResource(securityDepositToUpdate))).build();
    }
    
    @Override
    public Response credit(Long id, SecurityDepositCreditInput securityDepositInput) {
        SecurityDeposit securityDepositToUpdate = securityDepositService.findById(id);
        if(securityDepositToUpdate == null) {
            throw new EntityDoesNotExistsException("security deposit with id " + id + " does not exist.");
        }
        if(SecurityDepositStatusEnum.CANCELED.equals(securityDepositToUpdate.getStatus())){
            throw new EntityDoesNotExistsException("The Credit is not possible if the status of the security deposit is at 'Cancel'");
        } 
        securityDepositService.credit(securityDepositToUpdate, securityDepositInput);
        PaymentDto paymentDto = createPaymentDto(securityDepositInput);     
        Long idPayment = null;
        try {
            idPayment = paymentApi.createPayment(paymentDto);
        } catch (BusinessException e) {
            throw new BusinessException(e);
        } catch (MeveoApiException | NoAllOperationUnmatchedException | UnbalanceAmountException e) {
            throw new MeveoApiException(e);
        }
        Payment payment = paymentService.findById(idPayment);
        securityDepositService.createSecurityDepositTransaction(securityDepositToUpdate, securityDepositInput.getAmountToCredit(), 
            SecurityDepositOperationEnum.CREDIT_SECURITY_DEPOSIT, OperationCategoryEnum.CREDIT, payment);        
        auditLogService.trackOperation("CREDIT", new Date(), securityDepositToUpdate, securityDepositToUpdate.getCode());
        return Response.ok().entity(buildResponse(securityDepositMapper.toResource(securityDepositToUpdate))).build();
    }


    @Override
    public Response payInvoices(Long id, SecurityDepositPaymentInput securityDepositPaymentInput) {

        securityDepositService.payInvoices(id, securityDepositPaymentInput);
        return Response.ok().build();
    }

    private PaymentDto createPaymentDto(SecurityDepositCreditInput securityDepositInput) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setToMatching(securityDepositInput.getIsToMatching());
        paymentDto.setCustomerAccountCode(securityDepositInput.getCustomerAccountCode());        
        paymentDto.setPaymentMethod(securityDepositInput.getPaymentMethod());
        paymentDto.setAmount(securityDepositInput.getAmountToCredit());
        paymentDto.setDescription(null);
        paymentDto.setReference(securityDepositInput.getReference());
        paymentDto.setDueDate(new Date());
        paymentDto.setTransactionDate(new Date());
        paymentDto.setBankLot(securityDepositInput.getBankLot());
        paymentDto.setPaymentInfo(securityDepositInput.getPaymentInfo());
        paymentDto.setPaymentInfo1(securityDepositInput.getPaymentInfo1());
        paymentDto.setPaymentInfo2(securityDepositInput.getPaymentInfo2());
        paymentDto.setPaymentInfo3(securityDepositInput.getPaymentInfo3());
        paymentDto.setPaymentInfo4(securityDepositInput.getPaymentInfo4());
        paymentDto.setPaymentInfo5(securityDepositInput.getPaymentInfo5());
        paymentDto.setOccTemplateCode(securityDepositInput.getOccTemplateCode());
        paymentDto.setPaymentInfo6(null);
        paymentDto.setFees(null);
        paymentDto.setComment(null);
        paymentDto.setPaymentOrder(null);
        paymentDto.setCollectionDate(new Date());
        return paymentDto;
    }
        
    private Map<String, Object> buildResponse(SecurityDepositInput resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status","SUCCESS"));
        response.put("securityDeposit", resource);
        return response;
    }

}
