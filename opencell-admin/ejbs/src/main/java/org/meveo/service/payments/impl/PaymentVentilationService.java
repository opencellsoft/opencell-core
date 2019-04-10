package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.payments.*;
import org.meveo.service.base.PersistenceService;

@Stateless
public class PaymentVentilationService extends PersistenceService<PaymentVentilation> {

    @Inject
    private OtherTransactionGeneralService otherTransactionGeneralService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private CustomerAccountService customerAccountService;


    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void ventilatePayment(PaymentVentilation entity) throws Exception {
        BigDecimal ventilationAmout = entity.getVentilationAmount();
        BigDecimal unventilatedAmount = entity.getOriginalOT().getUnMatchingAmount();
        OtherTransactionGeneral originalOTG = (OtherTransactionGeneral) entity.getOriginalOT();
        originalOTG.setUnMatchingAmount(unventilatedAmount.subtract(ventilationAmout));
        originalOTG.setMatchingAmount(originalOTG.getMatchingAmount().add(ventilationAmout));
        MatchingStatusEnum matchingStatus = originalOTG.getUnMatchingAmount().compareTo(BigDecimal.ZERO) == 0 ? MatchingStatusEnum.L : MatchingStatusEnum.P;
        originalOTG.setMatchingStatus(matchingStatus);
        otherTransactionGeneralService.update(originalOTG);
        entity.setVentilationDate(new Date());
        ParamBean paramBean = paramBeanFactory.getInstance();
        OCCTemplate occTemplate = getOCCTemplate(paramBean.getProperty("occ.payment.arc", "ARC"));
        entity.setAccountOperation(createPayment(entity, occTemplate)); //// Account operation Credit
        entity.setNewOT(createVentilatedOTG(entity));
        create(entity);

    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void unventilatePayment(PaymentVentilation paymentVentilation) throws UnbalanceAmountException, Exception {
        paymentVentilation.setVentilationActionStatus(VentilationActionStatusEnum.U);
        update(paymentVentilation);

        createUnventilatedOTG(paymentVentilation);
        ParamBean paramBean = paramBeanFactory.getInstance();
        OCCTemplate occTemplate = getOCCTemplate(paramBean.getProperty("occ.payment.ard", "ARD"));
        Payment p = createPayment(paymentVentilation, occTemplate); //// Account operation Debit}
        List<Long> operationIds = new ArrayList<Long>();
        operationIds.add(p.getId());
        operationIds.add(paymentVentilation.getAccountOperation().getId());
        matchingCodeService.matchOperations(paymentVentilation.getCustomerAccount().getId(), null, operationIds, null, MatchingTypeEnum.A);
    }

    private Payment createPayment(PaymentVentilation paymentVentilation, OCCTemplate occTemplate) throws BusinessException {
        BigDecimal ventilationAmout = paymentVentilation.getVentilationAmount();
        OtherTransactionGeneral originalOTG = (OtherTransactionGeneral) paymentVentilation.getOriginalOT();

        Payment payment = new Payment();
        CustomerAccount customerAccount = customerAccountService.retrieveIfNotManaged(paymentVentilation.getCustomerAccount());
        payment.setCustomerAccount(customerAccount);
        payment.setDescription(originalOTG.getDescription());
        payment.setPaymentMethod(originalOTG.getPaymentMethod());
        payment.setAmount(ventilationAmout);
        payment.setUnMatchingAmount(ventilationAmout);
        payment.setMatchingAmount(BigDecimal.ZERO);
        payment.setAccountingCode(occTemplate.getAccountingCode());
        payment.setOccCode(occTemplate.getCode());
        payment.setOccDescription(occTemplate.getDescription());
        payment.setTransactionCategory(occTemplate.getOccCategory());
        payment.setReference(originalOTG.getReference());
        payment.setTransactionDate(originalOTG.getTransactionDate());
        payment.setDueDate(originalOTG.getDueDate());
        payment.setMatchingStatus(MatchingStatusEnum.O);
        // Additional payment information 1 - Bank Code
        payment.setPaymentInfo1(originalOTG.getPaymentInfo1());
        // Additional payment information 2 - Branch Code
        payment.setPaymentInfo2(originalOTG.getPaymentInfo2());
        // Additional payment information 3 - Account Number
        payment.setPaymentInfo3(originalOTG.getPaymentInfo3());
        paymentService.create(payment);

        return payment;

    }

    private OtherTransactionGeneral createVentilatedOTG(PaymentVentilation entity) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        BigDecimal ventilationAmout = entity.getVentilationAmount();
        OCCTemplate occTemplate = getOCCTemplate(paramBean.getProperty("occ.payment.otd_suspense_account", "OTD_Suspense_Account"));
        return createOTG(occTemplate, entity, ventilationAmout, ventilationAmout, BigDecimal.ZERO, MatchingStatusEnum.L);
    }

    private OtherTransactionGeneral createUnventilatedOTG(PaymentVentilation paymentVentilation) throws BusinessException {
        ParamBean paramBean = paramBeanFactory.getInstance();
        BigDecimal ventilationAmout = paymentVentilation.getVentilationAmount();
        OCCTemplate occTemplate = getOCCTemplate(paramBean.getProperty("occ.payment.otc.suspense.account", "OTC_Suspense_Account"));
        return createOTG(occTemplate, paymentVentilation, ventilationAmout, BigDecimal.ZERO, ventilationAmout, MatchingStatusEnum.O);

    }

    private OtherTransactionGeneral createOTG(OCCTemplate occTemplate, PaymentVentilation paymentVentilation, BigDecimal amount, BigDecimal ventilatedAmout,
            BigDecimal unventilatedAmout, MatchingStatusEnum status) throws BusinessException {

        OtherTransactionGeneral originalOTG = (OtherTransactionGeneral) paymentVentilation.getOriginalOT();

        OtherTransactionGeneral otg = new OtherTransactionGeneral();
        otg.setGeneralLedger(originalOTG.getGeneralLedger());
        otg.setDescription(originalOTG.getDescription());
        otg.setPaymentMethod(originalOTG.getPaymentMethod());
        otg.setAmount(amount);
        otg.setMatchingAmount(ventilatedAmout);
        otg.setUnMatchingAmount(unventilatedAmout);
        otg.setAccountingCode(occTemplate.getAccountingCode());
        otg.setOccCode(occTemplate.getCode());
        otg.setOccDescription(occTemplate.getDescription());
        otg.setTransactionCategory(occTemplate.getOccCategory());
        otg.setReference(originalOTG.getReference());
        otg.setTransactionDate(originalOTG.getTransactionDate());
        otg.setDueDate(originalOTG.getDueDate());
        otg.setMatchingStatus(status);
        // Additional payment information 1 - Bank Code
        otg.setPaymentInfo1(originalOTG.getPaymentInfo1());
        // Additional payment information 2 - Branch Code
        otg.setPaymentInfo2(originalOTG.getPaymentInfo2());
        // Additional payment information 3 - Account Number
        otg.setPaymentInfo3(originalOTG.getPaymentInfo3());

        otg.setPaymentInfo7(originalOTG.getPaymentInfo7());
        otherTransactionGeneralService.create(otg);

        return otg;

    }

    /**
     * get OCC template by code.
     *
     * @param occTemplateCode the occ template code
     * @return OCC Template
     * @throws BusinessException Business Exception
     */
    private OCCTemplate getOCCTemplate(String occTemplateCode) throws BusinessException {
        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }
        return occTemplate;
    }

}
