package org.meveo.service.script.payment;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.Refund;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.script.Script;

public class IngenicoReportProcessorScript extends Script {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String RECORD_VARIABLE_NAME = "record";

    private static final String PAYMENT_DATE_LITERAL = "payment_Date";
    private static final String TRANSACTION_ID_LITERAL = "transaction_ID";
    private static final String OPERATION_TYPE_LITERAL = "operation_Type";
    private static final String COMMISSION_AMOUNT_LITERAL = "commission_Amount";
    private static final String DESCRIPTION_LITERAL = "Description";
    private static final String PAYOUT_DATE_LITERAL = "payout_Date";
    private static final String PAYOUT_REFERENCE_LITERAL = "payout_Ref";
    private static final String GROSS_AMOUNT = "gross_Amount";
  private static final String NET_AMOUNT = "net_Amount";

//    private static final String CF_COMMISSION_AMOUNT = "CF_COMMISSION_AMOUNT";
//  private static final String CF_NET_AMOUNT = "CF_NET_AMOUNT";
//    private static final String CF_EXPORTED = "CF_EXPORTED";
    private static final String CF_EXPORT_FILE = "CF_EXPORT_FILE";

    private static final String COMMISSION_OPERATION = "COM_PSP";
    private static final List<String> COMMISSION_TRANSACTION_TYPES =
            asList("NAST", "NMON", "NYEA", "NSST", "NPAY", "NREF", "NAUT", "NCBK");
  
    private static final String DATE_FORMAT_PATTERN = "yyyyMMdd";
    private static final String TDEB_OPERATION_TYPE = "TDEB";
    private static final String TREF_OPERATION_TYPE = "TREF";

    private AccountOperationService accountOperationService =
            (AccountOperationService) getServiceInterface("AccountOperationService");
    private OCCTemplateService occTemplateService =
            (OCCTemplateService) getServiceInterface("OCCTemplateService");
    private PaymentService paymentService = (PaymentService) getServiceInterface("PaymentService");
    private MatchingCodeService matchingCodeService = (MatchingCodeService) getServiceInterface("MatchingCodeService");
    
    /***
     * paymentInfo ==> payment reference (only for invoice AO), it is set when the payment has P9 status 
     * paymentInfo1 ==> CRE filename, it is set when the AO is exported
     * paymetInfo2 ===> NET_AMOUNT (get from ingenico file)
     * fees ===> COMMISSION_AMOUNT
     * ***/

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        try {
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
            if (recordMap != null && !recordMap.isEmpty()) {
                String paymentDate = (String) recordMap.get(PAYMENT_DATE_LITERAL);
                String transactionID = (String) recordMap.get(TRANSACTION_ID_LITERAL);
                String operationType = (String) recordMap.get(OPERATION_TYPE_LITERAL);
                String description = (String) recordMap.get(DESCRIPTION_LITERAL);
                String payoutDate = (String) recordMap.get(PAYOUT_DATE_LITERAL);
                String payoutReference = (String) recordMap.get(PAYOUT_REFERENCE_LITERAL);
                BigDecimal commissionAmount = new BigDecimal((String) recordMap.get(COMMISSION_AMOUNT_LITERAL));
                
                DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

                if (!validateInputs(paymentDate, commissionAmount, operationType)) {
                    log.error(format("Line with transaction ID [%s] will be skipped due to missing inputs", transactionID));
                    return;
                }
              log.info("Processing line OP type="+operationType);
              AccountOperation ao = null;
             BigDecimal netAmount = StringUtils.isNotBlank((String)recordMap.get(NET_AMOUNT))? new BigDecimal((String) recordMap.get(NET_AMOUNT)):BigDecimal.ZERO;
                if (operationType.equalsIgnoreCase(TDEB_OPERATION_TYPE)) {
                	ao = findByReferenceAndType(transactionID,"P");
                	if(ao!=null) {
                    	Payment transaction=(Payment)ao;
                        log.info("Found TDEB transaction Id="+transactionID);
                        transaction.setDepositDate(dateFormat.parse(paymentDate));
                        transaction.setPaymentInfo2(netAmount+"");
                        transaction.setFees(commissionAmount);
                    	 paymentService.update(transaction); 
                	}
 
                }  else if (operationType.equalsIgnoreCase(TREF_OPERATION_TYPE)) {
                	  log.info("Found TREF transaction Id="+transactionID);
                	ao = findByReferenceAndType(transactionID,"RF");
                	Payment paymentAo = (Payment) findByReferenceAndType(transactionID, "P");
                	
                	if(paymentAo==null) {
                		 log.error(format("Payment refund [%s] has no correspending with payment operation", transactionID));
                         return;
                	}
                	if(ao==null) {
                     log.info("create Rejected payment="+transactionID+"...");
                	netAmount=	netAmount.divide(new BigDecimal(100));
                	String occTemplateCode=null;
                	Refund refundPayment = new Refund();
                    refundPayment.setType("RF");
                    refundPayment.setDepositDate(dateFormat.parse(paymentDate));
                    refundPayment.setReference(transactionID+"_1"); 
                    refundPayment.setMatchingStatus(MatchingStatusEnum.O);
                    refundPayment.setUnMatchingAmount(netAmount);
                    refundPayment.setAmount(netAmount);
                    OCCTemplate occTemplate = occTemplateService.findByCode("REF_DDT");
                    if (occTemplate == null) {
                        throw new BusinessException("Cannot find AO Template with code:" + occTemplateCode);
                    }
                    refundPayment.setCustomerAccount(paymentAo.getCustomerAccount());
                    refundPayment.setAccountingCode(occTemplate.getAccountingCode());
                    refundPayment.setCode(occTemplate.getCode());
                    refundPayment.setDescription(occTemplate.getDescription());
                    refundPayment.setTransactionCategory(occTemplate.getOccCategory());
                    refundPayment.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
                    refundPayment.setPaymentMethod(paymentAo.getPaymentMethod());
                    refundPayment.setTransactionDate(new Date()); 

                    accountOperationService.create(refundPayment);
                    log.info(format("refund payment [%s] created", transactionID));
                    List<Long> aos = new ArrayList<>();
                    aos.add(paymentAo.getId());
                    aos.add(refundPayment.getId());
                    matchingCodeService.matchOperations(paymentAo.getCustomerAccount().getId(), paymentAo.getCustomerAccount().getCode(), aos, null);
                	}else {
                		Refund refund=(Refund)ao;
                		refund.setDepositDate(dateFormat.parse(paymentDate));
                		refund.setPaymentInfo2(netAmount+"");
                		refund.setPaymentInfo4(commissionAmount+""); 
                		accountOperationService.update(refund);
                	}
                	
                	
                	
                }
                if (COMMISSION_TRANSACTION_TYPES.contains(operationType)) {
                    OCCTemplate occTemplate = ofNullable(occTemplateService.findByCode(COMMISSION_OPERATION))
                            .orElseThrow(() -> new BusinessException("Account operation COM_PSP not found"));
                    IngenicoData ingenicoData = new IngenicoData(payoutReference, dateFormat.parse(payoutDate),
                            commissionAmount, operationType, description);
                    createCommissionOperation(ingenicoData, occTemplate);
                }
            } else {
                log.info("Header/End line or empty record");
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private boolean validateInputs(String paymentDate, BigDecimal commissionAmount, String operationType) {
        if (operationType == null) {
            log.error("PSP operationType is null");
            return false;
        }
        if (commissionAmount == null) {
            log.error("PSP Commission Amount is null");
            return false;
        }
        if (paymentDate == null) {
            log.error("PSP payment Date is null");
            return false;
        }
        return true;
    }



    private void createCommissionOperation(IngenicoData ingenicoData, OCCTemplate occTemplate) {
        OtherCreditAndCharge commission = new OtherCreditAndCharge();
        commission.setReference(ingenicoData.getPayoutReference());
        commission.setOperationDate(ingenicoData.getPayoutDate());
        commission.setDepositDate(ingenicoData.getPayoutDate());
        commission.setAmount(ingenicoData.getCommissionAmount());
        commission.setPaymentInfo(ingenicoData.getOperationType());
        commission.setPaymentInfo3(ingenicoData.getDescription());
        commission.setCode(occTemplate.getCode());
        commission.setDescription(occTemplate.getDescription());
        commission.setTransactionCategory(OperationCategoryEnum.DEBIT);
        accountOperationService.create(commission);
    }


    class IngenicoData {
        private String payoutReference;
        private Date payoutDate;
        private BigDecimal commissionAmount;
        private String operationType;
        private String description;

        public IngenicoData(String payoutReference, Date payoutDate, BigDecimal commissionAmount,
                            String operationType, String description) {
            this.payoutDate = payoutDate;
            this.payoutReference = payoutReference;
            this.commissionAmount = commissionAmount;
            this.operationType = operationType;
            this.description = description;
        }

        public Date getPayoutDate() {
            return payoutDate;
        }

        public String getPayoutReference() {
            return payoutReference;
        }

        public BigDecimal getCommissionAmount() {
            return commissionAmount;
        }

        public String getOperationType() {
            return operationType;
        }

        public String getDescription() {
            return description;
        }
    }
    public AccountOperation findByReferenceAndType(String reference,String type) {
        try {
            QueryBuilder qb = new QueryBuilder(AccountOperation.class, "a");
            qb.addCriterionWildcard("reference", reference+"*", true);
            qb.addCriterion("type", "=", type, true);
            return (AccountOperation) qb.getQuery(accountOperationService.getEntityManager()).getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }
}