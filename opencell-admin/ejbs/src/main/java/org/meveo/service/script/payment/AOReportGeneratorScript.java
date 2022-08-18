package org.meveo.service.script.payment;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.math.MathContext.DECIMAL32;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.Refund;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.finance.ReportExtractScript;

public class AOReportGeneratorScript extends ReportExtractScript {

    private static final String CF_ENTJURI = "CF_EXTERNAL_CODE";
    private static final String CURRENCY_CODE = "EUR";
    private static final String DATE_PATTERN = "yyyyMMdd";

    /***
     * paymentInfo ==> payment reference (only for invoice AO), it is set when the payment has P9 status
     * paymentInfo1 ==> CRE filename, it is set when the AO is exported
     * paymetInfo2 ===> NET_AMOUNT (get from ingenico file)
     * fees ===> COMMISSION_AMOUNT
     * ***/
    //=====================================================================================================//
    // Native query instead of jpql because we need to cast json fields to boolean before comparison=======//
    // And native query provide json cast==================================================================//
    //=====================================================================================================//
    private static final String AO_TO_EXPORT_IDS_QUERY = "SELECT id as ao_id FROM ar_account_operation ao " +
            " WHERE (ao.transaction_type = 'P' AND ao.deposit_date is NOT null AND ao.payment_info1 is null) " +
            " OR( ao.transaction_type = 'OCC' AND ao.code = 'COM_PSP' AND ao.payment_info1 is null) " +
            " OR( ao.transaction_type = 'I' AND ao.payment_info is NOT null AND ao.payment_info1 is null) " +
            " OR( ao.transaction_type = 'RF' AND ao.deposit_date is NOT null AND ao.payment_info1 is null)";

    private static final String CROID_SEQ_NEXT_VAL_QUERY = "select nextval('croid_seq')";

    private DecimalFormat decimalFormat = new DecimalFormat("000000000000000000");
    private DecimalFormat croFormat = new DecimalFormat("0000000000");
    private AccountOperationService accountOperationService =
            (AccountOperationService) getServiceInterface("AccountOperationService");
    private OCCTemplateService occTemplateService =
            (OCCTemplateService) getServiceInterface("OCCTemplateService");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);

    @Override
    public void execute(Map<String, Object> executeContext) throws BusinessException {
        log.info("generate AO report started");
        List<AccountOperation> accountOperations = retrieveAOToExport();
        log.info("Number of AO to export : " + accountOperations.size());
        String exportDir = String.valueOf(executeContext.get(DIR));
        String filename = String.valueOf(executeContext.get(FILENAME));

        File dir = createDirectory(exportDir);
        StringBuilder sb = new StringBuilder();
        for (AccountOperation ao : accountOperations) {
            sb.append(addCRO(ao, "%-10s"))
                    .append("0000001")
                    .append(addTransactionType(ao, "%-7s"));
            if (ao.getSeller() != null && ao.getSeller().getCfValues() != null) {
                Optional.ofNullable(getCFValue(ao.getSeller().getCfValues(), CF_ENTJURI))
                        .ifPresent(value -> sb.append(format("%-5s", value)));
            } else {
                sb.append(format("%-5s", "00003"));//the seller should be set for the P AO, or get it from matched invoice
            }
            sb.append("100")//Entité de gestion
                    .append(addClientCode(ao))
                    .append(addClientDescription(ao))
                    .append(addContractNumber(ao))
                    .append(addOperationReference(ao))
                    .append(addOperationType(ao))
                    .append(addAccountingDate(ao))
                    .append(addDueDate(ao))
                    .append(addSubscriptionStartDate(ao)) // Abo start date
                    .append(addSubscriptionEndDate(ao)) // Abo End date
                    .append(addAccountingDate(ao))
                    .append(addMatchingKey(ao)) // Key
                    .append(ao.getTransactionCategory().getLabel().toUpperCase().split("\\.")[1].charAt(0))
                    .append(CURRENCY_CODE)
                    .append(addTTC(ao))
                    .append(addHT(ao))
                    .append(addTVA(ao))
                    .append(addCommission(ao))
                    .append(addLibeCRI(ao))//LibeCRI
                    .append(format("%-4s", " ")) //CRB
                    .append(format("%-4s", " ")) //SA... seller
                    .append("  ") //FILIERE
                    .append("     ") //PROJET
                    .append(format("%-76s", " ")); //FILLER
            setAccountOperationCRE(ao, filename);
            sb.append("\n");
        }
        File file = new File(dir + File.separator + filename);
        try (FileWriter fileWriter = new FileWriter(file)) {
            file.createNewFile();
           // fileWriter.write(createHeader());
            fileWriter.write(sb.toString());
            log.info("AO report is exported successfully | file name : " + filename);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private List<AccountOperation> retrieveAOToExport() {
        Map<String, Object> parameters = new HashMap<>();
        List<Long> ids = findAOIdsToExport();
        if (ids != null && !ids.isEmpty()) {
            parameters.put("ids", ids);
            return (List<AccountOperation>) accountOperationService
                    .executeSelectQuery("FROM AccountOperation ao WHERE id in (:ids)", parameters);
        }
        return Collections.EMPTY_LIST;
    }

    private List<Long> findAOIdsToExport() {
        List<Map<String, Object>> ids = accountOperationService.executeNativeSelectQuery(AO_TO_EXPORT_IDS_QUERY, null);
        return ids.stream()
                .map(row -> (BigInteger) row.get("ao_id"))
                .map(BigInteger::longValue)
                .collect(Collectors.toList());
    }


    @SuppressWarnings("unused")
    private Object getNextValForCROID() {

        Query q = accountOperationService.getEntityManager().createNativeQuery(CROID_SEQ_NEXT_VAL_QUERY);
        Object id = q.getSingleResult();
        return id;
    }

    private File createDirectory(String exportDir) {
        File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private String addCRO(AccountOperation ao, String format) {
        return croFormat.format(getNextValForCROID());
    }
    private String addTransactionType(AccountOperation ao, String format) {
        String transactionType = ReportTransactionType.fromValue(ao.getType()).name();
        if(transactionType!=null && "REF_DDT".equalsIgnoreCase(transactionType)) {
        	transactionType="PAY_DDT";
        }
        return format(format, transactionType != null ? transactionType : "");
    }

    private Object getCFValue(CustomFieldValues values, String code) {
        return Optional.ofNullable(values.getValue(code))
                .orElse(null);
    }

    private String addClientCode(AccountOperation ao) {
        String clientCode = "";
        if (!ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
            clientCode = ao.getCustomerAccount().getCode();
        }
        return format("%-15s", clientCode);
    }

    private String addClientDescription(AccountOperation ao) {
        String clientDescription = "";
        if (!ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
            if (ao.getCustomerAccount().getName() != null) {
                clientDescription = ao.getCustomerAccount().getName().getLastName();
                if (clientDescription.length() > 15) {
                    clientDescription = clientDescription.substring(0, 15);
                }
            }
        }
        return format("%-15s", clientDescription);
    }

    private String addContractNumber(AccountOperation ao) {
        String contractCode = "";
        if (!ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
            try{
                contractCode = ao.getCustomerAccount().getBillingAccounts().get(0).getUsersAccounts().get(0).getSubscriptions().get(0).getCode();
            } catch (Exception exception) {
            }

        }
        return format("%-15s", contractCode);
    }

    private String addSubscriptionStartDate(AccountOperation ao) {
        Date startDate = null;
        if (!ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel()) && !ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) {
            try{
                startDate = ao.getCustomerAccount().getBillingAccounts().get(0).getUsersAccounts().get(0).getSubscriptions().get(0).getSubscriptionDate();
            } catch (Exception exception) {
            }

        }
        return startDate != null ?
                format("%-8s", simpleDateFormat.format(startDate)) : format("%-8s", "");
    }

    private String addSubscriptionEndDate(AccountOperation ao) {
        Date endDate = null;
        if (!ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
            try{
                endDate = ao.getCustomerAccount().getBillingAccounts().get(0).getUsersAccounts().get(0).getSubscriptions().get(0).getSubscribedTillDate();
            } catch (Exception exception) {
            }

        }
        return endDate != null ?
                format("%-8s", simpleDateFormat.format(endDate)) : format("%-8s", "");
    }

    private String addOperationReference(AccountOperation ao) {
        String operationReference = "";
        if (!ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {

            operationReference = ao.getReference();
        }
        return format("%-15s", operationReference);
    }

    private String addMatchingKey(AccountOperation ao) {
        String operationReference="";
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.INV_STD.getLabel())) {
            operationReference=ao.getReference();
        }else {
            AccountOperation matchedAO=getMatchedOperation(ao);
            operationReference = matchedAO!=null?matchedAO.getReference():"";
            operationReference=operationReference!=null?operationReference.split("_")[0]:"";
        }

        return format("%-15s", operationReference);
    }
    private AccountOperation getMatchedOperation(AccountOperation ao) {
    	if (!ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
    		AccountOperation matchedAO=null;
    		MatchingCode matchingCode=ao.getMatchingAmounts()!=null && !ao.getMatchingAmounts().isEmpty()?ao.getMatchingAmounts().get(0).getMatchingCode():null;
    		if(matchingCode!=null) {
    			if (ao.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel())) {

    				for(MatchingAmount matching:matchingCode.getMatchingAmounts()) {
    					matchedAO=matching.getAccountOperation();
    					if (matchedAO.getType().equalsIgnoreCase(ReportTransactionType.INV_STD.getLabel()) ) {
    						return matchedAO;
    					}
    				}
    			}else if (ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) { 
    				for(MatchingAmount matching:matchingCode.getMatchingAmounts()) {
    					matchedAO=matching.getAccountOperation();
    					if (matchedAO.getType().equalsIgnoreCase(ReportTransactionType.INV_CRN.getLabel()) ) {
    						return matchedAO;
    					}
    				}
    			}
    		}

    	}
    	return null;
    }

    private String addOperationType(AccountOperation ao) {
        String operationType = "";
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel()) ||ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) {
            ao=getMatchedOperation(ao);
        }
        if (ao!=null && ao.getType().equalsIgnoreCase(ReportTransactionType.INV_STD.getLabel())) {
            RecordedInvoice recordedInvoice = (RecordedInvoice)ao;
            log.info("recordedInvoice={},invoice={}",recordedInvoice.getId(),recordedInvoice.getInvoice().getId());
            if (recordedInvoice.getInvoice().getInvoiceAgregates()!=null) {
                operationType = "ACT";
                for(InvoiceAgregate invoiceAgregate:recordedInvoice.getInvoice().getInvoiceAgregates()) {
                	if(invoiceAgregate.getDescription()!=null) {
                	log.info("invoiceAgregate description= ",invoiceAgregate.getDescription());
                    if(invoiceAgregate.getDescription().contains("Abonnement")) {
                        operationType="ABO";
                        break;
                    }
                	}
                }
            }
        }
        return format("%-10s", operationType);
    }

    private String addAccountingDate(AccountOperation ao) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        Date accountingDate = null;
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel()) ||
                ao.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel()) || ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) {
            accountingDate = ao.getDepositDate();
        } else {
            if (!ao.getInvoices().isEmpty()) {
                accountingDate = ao.getInvoices().get(0).getInvoiceDate();
            }
        }
        return accountingDate != null ?
                format("%-8s", simpleDateFormat.format(accountingDate)) : format("%-8s", "");
    }

    private String addDueDate(AccountOperation ao) {

        Date accountingDate = null;
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.INV_STD.getLabel())) {
            accountingDate = ao.getDueDate();
        }
        return accountingDate != null ?
                format("%-8s", simpleDateFormat.format(accountingDate)) : format("%-8s", "");
    }

    private String addTTC(AccountOperation ao) {
        BigDecimal ttc;
        if(ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
            ttc = BigDecimal.ZERO; //ao.getAmount().multiply(new BigDecimal(120));//A ne pas véhiculer pendant les 6 premiers mois
        } else if (ao.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel())) {
        	Payment paymentAO=(Payment)ao;
        	ttc = paymentAO.getPaymentInfo2()!=null? new BigDecimal(paymentAO.getPaymentInfo2().trim()):new BigDecimal(0);
        }else if (ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) {
        	Refund refundPayment=(Refund)ao;
        	ttc = refundPayment.getPaymentInfo2()!=null? new BigDecimal(refundPayment.getPaymentInfo2().trim()):new BigDecimal(0);
        }else {
            ttc = ao.getAmount().multiply(new BigDecimal(100));
        }
        return decimalFormat.format(ttc);
    }

   private String addHT(AccountOperation ao) {
        BigDecimal ht = BigDecimal.ZERO;
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.INV_STD.getLabel())) {
            ht = ao.getAmountWithoutTax().multiply(new BigDecimal(100));
        } else if (ao.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel()) || ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) {
        	ht = ao.getAmount().multiply(new BigDecimal(100));//TTC facturé
        }else if (ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
            ht = ao.getAmount();//commission_amount
        }
        return decimalFormat.format(ht);
    }

    private String addTVA(AccountOperation ao) {
        BigDecimal tva = BigDecimal.ZERO;
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel()) || ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) {
        	 tva =  BigDecimal.ZERO;
        }
        if(ao.getType().equalsIgnoreCase(ReportTransactionType.INV_STD.getLabel())) {
            tva = ao.getTaxAmount().multiply(new BigDecimal(100));
        }
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
        	 tva =  BigDecimal.ZERO;
        }
        return decimalFormat.format(tva);
    }
  
    private String addCommission(AccountOperation ao) {
    	BigDecimal commissionAmount = BigDecimal.ZERO;
    	if (ao.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel())) {
    		Payment paymentAO=(Payment)ao;
    		commissionAmount = paymentAO.getFees()!=null?paymentAO.getFees():new BigDecimal(0);     
    	}
    	if (ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) {
    		Refund refundPayment=(Refund)ao;
    		commissionAmount = refundPayment.getPaymentInfo4()!=null?(new BigDecimal(refundPayment.getPaymentInfo4().trim())).abs():new BigDecimal(0);     
    	}
    	return decimalFormat.format(commissionAmount);
    }
    
    private String addLibeCRI(AccountOperation ao) {
        String libCRI = "";
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.PAY_DDT.getLabel()) ) {
        	 AccountOperation matchedAO=getMatchedOperation(ao);
        	 libCRI = matchedAO!=null?matchedAO.getReference():"";
        }
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.REF_DDT.getLabel())) { 
    		MatchingCode matchingCode=ao.getMatchingAmounts()!=null && !ao.getMatchingAmounts().isEmpty()?ao.getMatchingAmounts().get(0).getMatchingCode():null;
    		if(matchingCode!=null) {
			for(MatchingAmount matching:matchingCode.getMatchingAmounts()) {
				AccountOperation matchedAO=matching.getAccountOperation();
				if (matchedAO.getType().equalsIgnoreCase(ReportTransactionType.INV_CRN.getLabel()) ) {
					libCRI=matchedAO.getReference().split("_")[0];
				}
			}
    		}
		}
        if (ao!=null && ao.getType().equalsIgnoreCase(ReportTransactionType.INV_STD.getLabel())) {
            libCRI=ao.getReference()+"/"+ao.getCustomerAccount().getCode();
        }
        if (ao.getType().equalsIgnoreCase(ReportTransactionType.COM_PSP.getLabel())) {
        	OtherCreditAndCharge occ=(OtherCreditAndCharge)ao;
        	libCRI="commission du "+format("%-8s", simpleDateFormat.format(occ.getOperationDate()));
        }
        return format("%-25s", libCRI);
    }

    private AccountOperation setAccountOperationCRE(AccountOperation ao, String fileName) {
        ao = accountOperationService.refreshOrRetrieve(ao);
        if(!"null".equals(fileName)){
          ao.setPaymentInfo1(fileName);
          ao= accountOperationService.update(ao);
        }
      return ao;
        
    }

    private String createHeader() {
        return join("  ", new String[]{"CROID", "AECORDRE", "TCRCODE", "ENTJURI", "ENTGEST", "CDCLIENT",
                "LCDCLIENT", "NCONTRAT", "REFOPE", "TYPOPE", "DTCMPTA", "DTEXIGIB", "DTDEBABO", "DTFINABO",
                "DTPIECE", "PIECENUM", "RIMSENS", "DEVISE", "MONTTTC", "MONTHT", "MONTTVA", "MONTCOM",
                "LIBECRI", "CRB", "SA", "FILIERE", "PROJET", "FILLER\n"});
    }

    enum ReportTransactionType {
        INV_STD("I"), PAY_DDT("P"), COM_PSP("OCC"),REF_DDT("RF"),INV_CRN("I");
        String label;

        ReportTransactionType(String label) {
            this.label = label;
        }

        public static ReportTransactionType fromValue(String value) {
            return Arrays.stream(ReportTransactionType.values())
                    .filter(reportTransactionType -> reportTransactionType.label.equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("no type for the given value " + value));
        }

        public String getLabel() {
            return label;
        }
    }

    private BigDecimal fromCfValues(CustomFieldValues customFieldValues, String cfLabel) {
        BigDecimal amount = BigDecimal.ZERO;
        if(customFieldValues.getValue(cfLabel) != null) {
            amount = new BigDecimal((Double) customFieldValues.getValue(cfLabel), DECIMAL32);
        }
        return amount;
    }
}