package org.meveo.admin.sepa;

import java.io.File;
import java.math.RoundingMode;
import java.util.Date;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.jaxb.Pain002;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlGrpInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts.TxInfAndSts;
import org.meveo.admin.sepa.jaxb.pain008.AccountIdentification4Choice;
import org.meveo.admin.sepa.jaxb.pain008.ActiveOrHistoricCurrencyAndAmount;
import org.meveo.admin.sepa.jaxb.pain008.BranchAndFinancialInstitutionIdentification4;
import org.meveo.admin.sepa.jaxb.pain008.CashAccount16;
import org.meveo.admin.sepa.jaxb.pain008.CustomerDirectDebitInitiationV02;
import org.meveo.admin.sepa.jaxb.pain008.DirectDebitTransaction6;
import org.meveo.admin.sepa.jaxb.pain008.DirectDebitTransactionInformation9;
import org.meveo.admin.sepa.jaxb.pain008.Document;
import org.meveo.admin.sepa.jaxb.pain008.FinancialInstitutionIdentification7;
import org.meveo.admin.sepa.jaxb.pain008.GenericPersonIdentification1;
import org.meveo.admin.sepa.jaxb.pain008.GroupHeader39;
import org.meveo.admin.sepa.jaxb.pain008.LocalInstrument2Choice;
import org.meveo.admin.sepa.jaxb.pain008.MandateRelatedInformation6;
import org.meveo.admin.sepa.jaxb.pain008.Party6Choice;
import org.meveo.admin.sepa.jaxb.pain008.PartyIdentification32;
import org.meveo.admin.sepa.jaxb.pain008.PaymentIdentification1;
import org.meveo.admin.sepa.jaxb.pain008.PaymentInstructionInformation4;
import org.meveo.admin.sepa.jaxb.pain008.PaymentMethod2Code;
import org.meveo.admin.sepa.jaxb.pain008.PaymentTypeInformation20;
import org.meveo.admin.sepa.jaxb.pain008.PersonIdentification5;
import org.meveo.admin.sepa.jaxb.pain008.PersonIdentificationSchemeName1Choice;
import org.meveo.admin.sepa.jaxb.pain008.SequenceType1Code;
import org.meveo.admin.sepa.jaxb.pain008.ServiceLevel8Choice;
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.payments.impl.DDRequestBuilderInterface;
import org.meveo.util.DDRequestBuilderClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author anasseh
 * @author Wassim Drira
 * @lastModifiedVersion 5.2
 *
 */
@DDRequestBuilderClass
public class SepaFile implements DDRequestBuilderInterface {
    Logger log = LoggerFactory.getLogger(SepaFile.class);

    @Override
    public String getDDFileName(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
        try {
            ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
            String fileName = ArConfig.getDDRequestFileNamePrefix() + ddRequestLot.getId();
            fileName = fileName + "_" + appProvider.getCode();
            fileName = fileName + "_" + DateUtils.formatDateWithPattern(new Date(), "yyyyMMdd") + ArConfig.getDDRequestFileNameExtension();

            String outputDir = paramBean.getChrootDir(appProvider.getCode());

            outputDir = outputDir + File.separator + ArConfig.getDDRequestOutputDirectory();
            outputDir = outputDir.replaceAll("\\..", "");

            log.info("DDRequest output directory=" + outputDir);
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return outputDir + File.separator + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void generateDDRequestLotFile(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
        try {
            ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
            Document document = new Document();
            CustomerDirectDebitInitiationV02 message = new CustomerDirectDebitInitiationV02();
            document.setCstmrDrctDbtInitn(message);
            addHeader(message, ddRequestLot, appProvider);
            for (DDRequestItem ddrequestItem : ddRequestLot.getDdrequestItems()) {
                if (!ddrequestItem.hasError()) {
                    addPaymentInformation(message, ddrequestItem, appProvider);
                }
            }
            String schemaLocation = paramBean.getProperty("sepa.schemaLocation.pain008",
                "https://github.com/w2c/sepa-sdd-xml-generator/blob/master/validation_schemes/pain.008.001.02.xsd");
            JAXBUtils.marshaller(document, new File(ddRequestLot.getFileName()), schemaLocation);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }

    @Override
    public String getDDRejectFilePrefix() throws BusinessException {
        return "Pain002_";
    }

    @Override
    public String getDDRejectFileExtension() throws BusinessException {
        return "xml";
    }

    @Override
    public DDRejectFileInfos processDDRejectedFile(File file) throws BusinessException {
        DDRejectFileInfos ddRejectFileInfos = new DDRejectFileInfos();
        try {
            ddRejectFileInfos.setFileName(file.getName());
            Pain002 pain002 = (Pain002) JAXBUtils.unmarshaller(Pain002.class, file);

            CstmrPmtStsRpt cstmrPmtStsRpt = pain002.getCstmrPmtStsRpt();

            OrgnlGrpInfAndSts orgnlGrpInfAndSts = cstmrPmtStsRpt.getOrgnlGrpInfAndSts();

            if (orgnlGrpInfAndSts == null) {
                throw new BusinessException("OriginalGroupInformationAndStatus tag doesn't exist");
            }

            String dDRequestLOTref = orgnlGrpInfAndSts.getOrgnlMsgId();
            if (dDRequestLOTref == null || dDRequestLOTref.indexOf("-") < 0) {
                throw new BusinessException("Unknown dDRequestLOTref:" + dDRequestLOTref);
            }
            String[] dDRequestLOTrefSplited = dDRequestLOTref.split("-");

            ddRejectFileInfos.setDdRequestLotId(dDRequestLOTrefSplited[1]);

            if (orgnlGrpInfAndSts.getGrpSts() != null && "RJCT".equals(orgnlGrpInfAndSts.getGrpSts())) {
                ddRejectFileInfos.setTheDDRequestFileWasRejected(true);
                ddRejectFileInfos.setReturnStatusCode(orgnlGrpInfAndSts.getStsRsnInf().getRsn().getCd());
                return ddRejectFileInfos;
            }
            OrgnlPmtInfAndSts orgnlPmtInfAndSts = cstmrPmtStsRpt.getOrgnlPmtInfAndSts();
            for (TxInfAndSts txInfAndSts : orgnlPmtInfAndSts.getTxInfAndSts()) {
                if ("RJCT".equals(txInfAndSts.getTxSts())) {
                    ddRejectFileInfos.getListInvoiceRefsRejected().put(txInfAndSts.getOrgnlEndToEndId(), "RJCT");
                }
            }

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return ddRejectFileInfos;
    }

    private void addHeader(CustomerDirectDebitInitiationV02 message, DDRequestLOT ddRequestLOT, Provider appProvider) throws Exception {

        GroupHeader39 groupHeader = new GroupHeader39();
        message.setGrpHdr(groupHeader);
        groupHeader.setMsgId(ArConfig.getDDRequestHeaderReference() + "-" + ddRequestLOT.getId());
        groupHeader.setCreDtTm(DateUtils.dateToXMLGregorianCalendar(new Date()));
        groupHeader.setNbOfTxs(String.valueOf(ddRequestLOT.getDdrequestItems().size()));
        groupHeader.setCtrlSum(ddRequestLOT.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
        PartyIdentification32 initgPty = new PartyIdentification32();
        initgPty.setNm(appProvider.getDescription());
        groupHeader.setInitgPty(initgPty);

    }

    private void addPaymentInformation(CustomerDirectDebitInitiationV02 Message, DDRequestItem dDRequestItem, Provider appProvider) throws Exception {

        log.info("addPaymentInformation dDRequestItem id=" + dDRequestItem.getId());
        ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
        PaymentInstructionInformation4 paymentInformation = new PaymentInstructionInformation4();
        Message.getPmtInf().add(paymentInformation);
        paymentInformation.setPmtInfId(ArConfig.getDDRequestHeaderReference() + "-" + dDRequestItem.getId());
        paymentInformation.setPmtMtd(PaymentMethod2Code.DD);
        paymentInformation.setNbOfTxs("1");
        paymentInformation.setCtrlSum(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
        PaymentTypeInformation20 paymentTypeInformation = new PaymentTypeInformation20();
        paymentInformation.setPmtTpInf(paymentTypeInformation);
        ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
        paymentTypeInformation.setSvcLvl(serviceLevel);
        serviceLevel.setCd("SEPA");
        LocalInstrument2Choice localInstrument = new LocalInstrument2Choice();
        paymentTypeInformation.setLclInstrm(localInstrument);
        localInstrument.setCd(paramBean.getProperty("sepa.LclInstrm", "CORE"));
        paymentTypeInformation.setSeqTp(SequenceType1Code.FRST);

        paymentInformation.setReqdColltnDt(DateUtils.dateToXMLGregorianCalendar(new Date())); // à revoir

        BankCoordinates providerBC = appProvider.getBankCoordinates();
        if (providerBC == null) {
            throw new BusinessException("Missing bank information on provider");
        }
        PartyIdentification32 creditor = new PartyIdentification32();
        creditor.setNm(appProvider.getDescription());
        paymentInformation.setCdtr(creditor);

        CashAccount16 creditorAccount = new CashAccount16();
        paymentInformation.setCdtrAcct(creditorAccount);
        AccountIdentification4Choice identification = new AccountIdentification4Choice();
        creditorAccount.setId(identification);
        identification.setIBAN(providerBC.getIban());

        BranchAndFinancialInstitutionIdentification4 creditorAgent = new BranchAndFinancialInstitutionIdentification4();
        paymentInformation.setCdtrAgt(creditorAgent);
        FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
        creditorAgent.setFinInstnId(financialInstitutionIdentification);
        financialInstitutionIdentification.setBIC(providerBC.getBic());
        PartyIdentification32 creditorSchemeIdentification = new PartyIdentification32();
        paymentInformation.setCdtrSchmeId(creditorSchemeIdentification);
        Party6Choice cdtrSchmeId = new Party6Choice();
        creditorSchemeIdentification.setId(cdtrSchmeId);
        PersonIdentification5 privateidentifier = new PersonIdentification5();
        cdtrSchmeId.setPrvtId(privateidentifier);
        GenericPersonIdentification1 other = new GenericPersonIdentification1();
        privateidentifier.getOthr().add(other);
        other.setId(providerBC.getIcs());
        PersonIdentificationSchemeName1Choice schemeName = new PersonIdentificationSchemeName1Choice();
        other.setSchmeNm(schemeName);
        schemeName.setPrtry("SEPA");
        addTransaction(dDRequestItem, paymentInformation);
    }

    private void addTransaction(DDRequestItem dDRequestItem, PaymentInstructionInformation4 paymentInformation) throws Exception {
        CustomerAccount ca = dDRequestItem.getAccountOperations().get(0).getCustomerAccount();
        PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
        BankCoordinates bankCoordiates = null;
        if (preferedPaymentMethod instanceof DDPaymentMethod) {
            bankCoordiates = ((DDPaymentMethod) preferedPaymentMethod).getBankCoordinates();
        }

        DirectDebitTransactionInformation9 directDebitTransactionInformation = new DirectDebitTransactionInformation9();
        paymentInformation.getDrctDbtTxInf().add(directDebitTransactionInformation);
        PaymentIdentification1 PaymentIdentification = new PaymentIdentification1();
        directDebitTransactionInformation.setPmtId(PaymentIdentification);
        PaymentIdentification.setInstrId(""+dDRequestItem.getId());
        PaymentIdentification.setEndToEndId(""+dDRequestItem.getId());
        ActiveOrHistoricCurrencyAndAmount instructedAmount = new ActiveOrHistoricCurrencyAndAmount();
        directDebitTransactionInformation.setInstdAmt(instructedAmount);
        instructedAmount.setValue(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
        instructedAmount.setCcy("EUR");
        DirectDebitTransaction6 directDebitTransaction = new DirectDebitTransaction6();
        directDebitTransactionInformation.setDrctDbtTx(directDebitTransaction);
        MandateRelatedInformation6 mandateRelatedInformation = new MandateRelatedInformation6();
        directDebitTransaction.setMndtRltdInf(mandateRelatedInformation);
       
        if (preferedPaymentMethod != null && preferedPaymentMethod instanceof DDPaymentMethod) {
            mandateRelatedInformation.setMndtId(((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification());
            mandateRelatedInformation.setDtOfSgntr(DateUtils.dateToXMLGregorianCalendar(((DDPaymentMethod) preferedPaymentMethod).getMandateDate()));
        }
        BranchAndFinancialInstitutionIdentification4 debtorAgent = new BranchAndFinancialInstitutionIdentification4();
        directDebitTransactionInformation.setDbtrAgt(debtorAgent);
        FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
        financialInstitutionIdentification.setBIC(bankCoordiates.getBic());
        debtorAgent.setFinInstnId(financialInstitutionIdentification);

        PartyIdentification32 debtor = new PartyIdentification32();
        directDebitTransactionInformation.setDbtr(debtor);
        debtor.setNm(ca.getDescription());

        CashAccount16 debtorAccount = new CashAccount16();
        directDebitTransactionInformation.setDbtrAcct(debtorAccount);
        AccountIdentification4Choice identification = new AccountIdentification4Choice();
        identification.setIBAN(bankCoordiates.getIban());
        debtorAccount.setId(identification);

    }
}