package org.meveo.apiv2.securityDeposit.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.securityDeposit.*;
import org.meveo.apiv2.securityDeposit.resource.SecurityDepositResource;
import org.meveo.apiv2.securityDeposit.service.SecurityDepositApiService;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;

@Interceptors({ WsRestApiInterceptor.class })
public class SecurityDepositResourceImpl implements SecurityDepositResource {

    @Inject
    SecurityDepositApiService securityDepositApiService;

    @Inject
    SecurityDepositService securityDepositService;
    
    @Inject
    private AuditLogService auditLogService;
    
    @Inject
    private InvoiceApi invoiceApi;
    
    SecurityDepositMapper securityDepositMapper = new SecurityDepositMapper();

    @Inject
    BillingAccountService billingAccountService;
    
    @Inject
    SellerService sellerService;

    @Override
    public Response instantiate(SecurityDepositInput securityDepositInput) {
        SecurityDeposit sd = null;
        try {
            if (securityDepositInput.getId() !=null) {
                sd = securityDepositApiService.findById(securityDepositInput.getId())
                        .orElseThrow(() -> new NotFoundException("The SecurityDeposit does not exist with id = " + securityDepositInput.getId()));
            }
            else {
                sd = new SecurityDeposit();
            }                    
            sd = securityDepositApiService.instantiate(securityDepositMapper.toEntity(sd, securityDepositInput), SecurityDepositStatusEnum.VALIDATED, true)
                    .orElseThrow(() -> new BusinessApiException("Security Deposit hasn't been initialized"));            
            invoiceApi.validateInvoice(sd.getSecurityDepositInvoice().getId(), true, false, false);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return Response.ok(ImmutableSecurityDepositSuccessResponse
                .builder()
                .status("SUCCESS")
                .newSecurityDeposit(securityDepositMapper.toResource(sd))
                .build()
            ).build();
    }
    
    @Override
    public Response create(SecurityDepositInput securityDepositInput) {
        SecurityDeposit result;
        try {
            result = securityDepositApiService.create(securityDepositMapper.toEntity(securityDepositInput), SecurityDepositStatusEnum.DRAFT, false)
                                                .orElseThrow(() -> new BusinessApiException("Security Deposit hasn't been initialized"));
        } catch (Exception e) {
            throw new BusinessApiException(e);
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

        securityDepositToUpdate = securityDepositMapper.toEntity(securityDepositToUpdate, securityDepositInput);
        securityDepositService.checkParameters(securityDepositToUpdate, securityDepositInput);
        securityDepositApiService.linkRealEntities(securityDepositToUpdate);        
        securityDepositService.update(securityDepositToUpdate);
        auditLogService.trackOperation("UPDATE", new Date(), securityDepositToUpdate, securityDepositToUpdate.getCode());
        return Response.ok().entity(buildResponse(securityDepositMapper.toResource(securityDepositToUpdate))).build();
    }

    @Override
    @Transactional
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

        try {
			securityDepositApiService.refund(securityDepositToUpdate, securityDepositInput.getRefundReason(), SecurityDepositOperationEnum.REFUND_SECURITY_DEPOSIT, SecurityDepositStatusEnum.REFUNDED, "REFUND");
        } catch (BusinessException e) {
            throw new BusinessException(e);
        } catch (ImportInvoiceException | InvoiceExistException | IOException | MeveoApiException e) {
            throw new MeveoApiException(e);
        }
        return Response.ok().entity(buildResponse(securityDepositMapper.toResource(securityDepositToUpdate))).build();
    }
    
    @Override
    @Transactional
    public Response cancel(Long id, SecurityDepositCancelInput securityDepositInput) {
        return Response.ok().entity(buildResponse(
                securityDepositMapper.toResource(securityDepositApiService.cancel(id, securityDepositInput)))).build();
    }
    
    @Override
    public Response credit(Long id, SecurityDepositCreditInput securityDepositInput) {
        SecurityDeposit securityDepositToUpdate = securityDepositApiService.credit(id, securityDepositInput);
        securityDepositToUpdate.setBillingAccount(billingAccountService.refreshOrRetrieve(securityDepositToUpdate.getBillingAccount()));
        securityDepositToUpdate.setSeller(sellerService.refreshOrRetrieve(securityDepositToUpdate.getSeller()));
        return Response.ok().entity(buildResponse(securityDepositMapper.toResource(securityDepositToUpdate))).build();
    }


    @Override
    public Response payInvoices(Long id, SecurityDepositPaymentInput securityDepositPaymentInput) {

        securityDepositService.payInvoices(id, securityDepositPaymentInput);
        return Response.ok().build();
    }

    private Map<String, Object> buildResponse(SecurityDepositInput resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("actionStatus", Collections.singletonMap("status","SUCCESS"));
        response.put("securityDeposit", resource);
        return response;
    }

}
