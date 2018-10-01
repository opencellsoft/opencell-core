package org.meveo.admin.sepa;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.jaxb.Pain002;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlGrpInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts.TxInfAndSts;
import org.meveo.admin.sepa.jaxb.pain001.AmountType3Choice;
import org.meveo.admin.sepa.jaxb.pain001.CreditTransferTransactionInformation10;
import org.meveo.admin.sepa.jaxb.pain001.CustomerCreditTransferInitiationV03;
import org.meveo.admin.sepa.jaxb.pain001.GroupHeader32;
import org.meveo.admin.sepa.jaxb.pain001.OrganisationIdentification4;
import org.meveo.admin.sepa.jaxb.pain001.PaymentInstructionInformation3;
import org.meveo.admin.sepa.jaxb.pain001.PaymentMethod3Code;
import org.meveo.admin.sepa.jaxb.pain001.PaymentTypeInformation19;
import org.meveo.admin.sepa.jaxb.pain001.RemittanceInformation5;
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

            ddRejectFileInfos.setDdRequestLotId(new Long(dDRequestLOTrefSplited[1]));

            if (orgnlGrpInfAndSts.getGrpSts() != null && "RJCT".equals(orgnlGrpInfAndSts.getGrpSts())) {
                ddRejectFileInfos.setTheDDRequestFileWasRejected(true);
                ddRejectFileInfos.setReturnStatusCode(orgnlGrpInfAndSts.getStsRsnInf().getRsn().getCd());
                return ddRejectFileInfos;
            }
            OrgnlPmtInfAndSts orgnlPmtInfAndSts = cstmrPmtStsRpt.getOrgnlPmtInfAndSts();
            for (TxInfAndSts txInfAndSts : orgnlPmtInfAndSts.getTxInfAndSts()) {
                if ("RJCT".equals(txInfAndSts.getTxSts())) {
                    ddRejectFileInfos.getListInvoiceRefsRejected().put(new Long(txInfAndSts.getOrgnlEndToEndId()), "RJCT");
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

        paymentInformation.setReqdColltnDt(DateUtils.dateToXMLGregorianCalendarFieldUndefined(new Date())); // à revoir

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
        if (preferedPaymentMethod == null || !(preferedPaymentMethod instanceof DDPaymentMethod)) {
            throw new BusinessException("Payment method not valid!");
        }
        bankCoordiates = ((DDPaymentMethod) preferedPaymentMethod).getBankCoordinates();

        DirectDebitTransactionInformation9 directDebitTransactionInformation = new DirectDebitTransactionInformation9();
        paymentInformation.getDrctDbtTxInf().add(directDebitTransactionInformation);
        PaymentIdentification1 PaymentIdentification = new PaymentIdentification1();
        directDebitTransactionInformation.setPmtId(PaymentIdentification);
        PaymentIdentification.setInstrId(String.valueOf(dDRequestItem.getId()));
        PaymentIdentification.setEndToEndId(String.valueOf(dDRequestItem.getId()));
        ActiveOrHistoricCurrencyAndAmount instructedAmount = new ActiveOrHistoricCurrencyAndAmount();
        directDebitTransactionInformation.setInstdAmt(instructedAmount);
        instructedAmount.setValue(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
        instructedAmount.setCcy("EUR");
        DirectDebitTransaction6 directDebitTransaction = new DirectDebitTransaction6();
        directDebitTransactionInformation.setDrctDbtTx(directDebitTransaction);
        MandateRelatedInformation6 mandateRelatedInformation = new MandateRelatedInformation6();
        directDebitTransaction.setMndtRltdInf(mandateRelatedInformation);

        mandateRelatedInformation.setMndtId(((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification());
        mandateRelatedInformation.setDtOfSgntr(DateUtils.dateToXMLGregorianCalendarFieldUndefined(((DDPaymentMethod) preferedPaymentMethod).getMandateDate()));
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

    @Override
    public String getSCTFileName(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {

        ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
        String fileName = ArConfig.getSCTRequestFileNamePrefix() + ddRequestLot.getId();
        fileName = fileName + "_" + appProvider.getCode();
        fileName = fileName + "_" + DateUtils.formatDateWithPattern(new Date(), "yyyyMMddHHmmssSSS") + ArConfig.getSCTRequestFileNameExtension();

        String outputDir = paramBean.getChrootDir(appProvider.getCode());

        outputDir = outputDir + File.separator + ArConfig.getSCTRequestOutputDir();
        outputDir = outputDir.replaceAll("\\..", "");

        log.info("SCTRequest output directory=" + outputDir);
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        fileName = outputDir + File.separator + fileName;
        return fileName;
    }

    @Override
    public void generateSCTRequestLotFile(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
        ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());

        org.meveo.admin.sepa.jaxb.pain001.Document document;
        CustomerCreditTransferInitiationV03 message;
        List<DDRequestItem> ddrequestItems = ddRequestLot.getDdrequestItems();
        DDRequestItem ddrequestItem;
        Long operationsByFile = ddRequestLot.getDdRequestBuilder().getNbOperationPerFile();
        if (operationsByFile == null || operationsByFile <= 0) {
            operationsByFile = (long) ddrequestItems.size();
        }
        int filesToGenerate = ddrequestItems.size() == 0 ? 0 : (int) Math.ceil(ddrequestItems.size() / (double) operationsByFile);
        int opToGenerate = 0;
        int generatedOps = 0;
        BigDecimal totalAmount;
        List<String> generatedFilesNames = new ArrayList<>();
        String fileName;
        for (int fileNumber = 1; fileNumber <= filesToGenerate; fileNumber++) {
            try {
                fileName = getSCTFileName(ddRequestLot, appProvider);
                document = new org.meveo.admin.sepa.jaxb.pain001.Document();
                message = new CustomerCreditTransferInitiationV03();
                document.setCstmrCdtTrfInitn(message);
                addSctHeader(message, ddRequestLot, appProvider, fileNumber);
                totalAmount = BigDecimal.ZERO;
                opToGenerate = 0;
                while (generatedOps < ddrequestItems.size() && opToGenerate < operationsByFile) {
                    ddrequestItem = ddrequestItems.get(generatedOps);
                    totalAmount = totalAmount.add(ddrequestItem.getAmount());
                    if (!ddrequestItem.hasError()) {
                        addSctPaymentInformation(message, ddrequestItem, appProvider);
                    } else {
                        log.error("ddrequestItem with id = " + ddrequestItem.getId() + " has Errors :" + ddrequestItem.getErrorMsg() + ". The file " + fileName
                                + "will not contain all payment informations.");
                    }
                    opToGenerate++;
                    generatedOps++;
                }
                message.getGrpHdr().setCtrlSum(totalAmount.setScale(2, RoundingMode.HALF_UP));
                message.getGrpHdr().setNbOfTxs(String.valueOf(opToGenerate));
                String schemaLocation = paramBean.getProperty("sepa.schemaLocation.pain008",
                    "https://github.com/w2c/sepa-sdd-xml-generator/blob/master/validation_schemes/pain.008.001.02.xsd");

                JAXBUtils.marshaller(document, new File(fileName), schemaLocation);
                generatedFilesNames.add(fileName);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(e.getMessage());
            }

        }
        ddRequestLot.setFileName(String.join(",", generatedFilesNames));

    }

    private void addSctPaymentInformation(CustomerCreditTransferInitiationV03 message, DDRequestItem ddrequestItem, Provider appProvider) throws Exception {

        log.info("addPaymentInformation dDRequestItem id=" + ddrequestItem.getId());

        PaymentInstructionInformation3 paymentInformation = new PaymentInstructionInformation3();
        message.getPmtInf().add(paymentInformation);
        paymentInformation.setPmtInfId(ArConfig.getDDRequestHeaderReference() + "-" + ddrequestItem.getId());
        paymentInformation.setPmtMtd(PaymentMethod3Code.TRF);
        paymentInformation.setBtchBookg(true);
        paymentInformation.setNbOfTxs("1");
        paymentInformation.setCtrlSum(ddrequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
        PaymentTypeInformation19 paymentTypeInformation = new PaymentTypeInformation19();
        paymentInformation.setPmtTpInf(paymentTypeInformation);
        org.meveo.admin.sepa.jaxb.pain001.ServiceLevel8Choice serviceLevel = new org.meveo.admin.sepa.jaxb.pain001.ServiceLevel8Choice();
        paymentTypeInformation.setSvcLvl(serviceLevel);
        serviceLevel.setCd("SEPA");
        org.meveo.admin.sepa.jaxb.pain001.CategoryPurpose1Choice ctgyPurp = new org.meveo.admin.sepa.jaxb.pain001.CategoryPurpose1Choice();
        paymentTypeInformation.setCtgyPurp(ctgyPurp);
        ctgyPurp.setCd("SUPP");

        paymentInformation.setReqdExctnDt(DateUtils.dateToXMLGregorianCalendarFieldUndefined(new Date())); // TODO : define a configurable delay between payment and date of issue

        org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32 dbtr = new org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32();
        dbtr.setNm(appProvider.getDescription());
        paymentInformation.setDbtr(dbtr);

        org.meveo.admin.sepa.jaxb.pain001.CashAccount16 dbtrAccount = new org.meveo.admin.sepa.jaxb.pain001.CashAccount16();
        paymentInformation.setDbtrAcct(dbtrAccount);
        org.meveo.admin.sepa.jaxb.pain001.AccountIdentification4Choice identification = new org.meveo.admin.sepa.jaxb.pain001.AccountIdentification4Choice();
        dbtrAccount.setId(identification);

        BankCoordinates providerBC = appProvider.getBankCoordinates();
        if (providerBC == null) {
            throw new BusinessException("Missing bank information on provider");
        }
        // iban pattern
        if (!isMatched(providerBC.getIban(), "[A-Z]{2,2}[0-9]{2,2}[a-zA-Z0-9]{1,30}")) {
            throw new BusinessException("IBAN not valid!");
        }
        identification.setIBAN(providerBC.getIban());

        dbtrAccount.setCcy("EUR");
        org.meveo.admin.sepa.jaxb.pain001.BranchAndFinancialInstitutionIdentification4 dbtrAgent = new org.meveo.admin.sepa.jaxb.pain001.BranchAndFinancialInstitutionIdentification4();
        paymentInformation.setDbtrAgt(dbtrAgent);
        org.meveo.admin.sepa.jaxb.pain001.FinancialInstitutionIdentification7 financialInstitutionIdentification = new org.meveo.admin.sepa.jaxb.pain001.FinancialInstitutionIdentification7();
        dbtrAgent.setFinInstnId(financialInstitutionIdentification);
        if (StringUtils.isBlank(providerBC.getBic())) {
            org.meveo.admin.sepa.jaxb.pain001.GenericFinancialIdentification1 othr = new org.meveo.admin.sepa.jaxb.pain001.GenericFinancialIdentification1();
            othr.setId("NOTPROVIDED");
            financialInstitutionIdentification.setOthr(othr);
        } else if (isMatched(providerBC.getBic(), "[A-Z]{6,6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3,3}){0,1}")) {
            financialInstitutionIdentification.setBIC(providerBC.getBic());
        } else {
            throw new BusinessException("BIC not valid!");
        }

        paymentInformation.setChrgBr(org.meveo.admin.sepa.jaxb.pain001.ChargeBearerType1Code.SLEV);

        addSctTransaction(ddrequestItem, paymentInformation);
    }

    private void addSctHeader(CustomerCreditTransferInitiationV03 message, DDRequestLOT ddRequestLot, Provider appProvider, int fileNumber) throws Exception {
        GroupHeader32 groupHeader = new GroupHeader32();
        message.setGrpHdr(groupHeader);
        groupHeader.setMsgId(ArConfig.getSCTRequestHeaderRefrence() + "-" + ddRequestLot.getId() + "-" + fileNumber);
        groupHeader.setCreDtTm(DateUtils.dateToXMLGregorianCalendar(new Date()));
        org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32 initgPty = new org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32();
        initgPty.setNm(appProvider.getDescription());
        org.meveo.admin.sepa.jaxb.pain001.Party6Choice idProperty = new org.meveo.admin.sepa.jaxb.pain001.Party6Choice();
        initgPty.setId(idProperty);
        OrganisationIdentification4 orgId = new OrganisationIdentification4();
        idProperty.setOrgId(orgId);
        if (appProvider.getBankCoordinates() != null) {
            orgId.setBICOrBEI(appProvider.getBankCoordinates().getBic());
        }
        groupHeader.setInitgPty(initgPty);

    }

    private void addSctTransaction(DDRequestItem dDRequestItem, PaymentInstructionInformation3 paymentInformation) throws Exception {
        CustomerAccount ca = dDRequestItem.getAccountOperations().get(0).getCustomerAccount();
        PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
        BankCoordinates bankCoordiates = null;
        if (preferedPaymentMethod == null || !(preferedPaymentMethod instanceof DDPaymentMethod)) {
            throw new BusinessException("Payment method not valid!");
        }
        bankCoordiates = ((DDPaymentMethod) preferedPaymentMethod).getBankCoordinates();

        CreditTransferTransactionInformation10 cdtTrfTxInf = new CreditTransferTransactionInformation10();
        paymentInformation.getCdtTrfTxInf().add(cdtTrfTxInf);
        org.meveo.admin.sepa.jaxb.pain001.PaymentIdentification1 paymentIdentification = new org.meveo.admin.sepa.jaxb.pain001.PaymentIdentification1();
        cdtTrfTxInf.setPmtId(paymentIdentification);
        paymentIdentification.setInstrId(String.valueOf(dDRequestItem.getId()));
        paymentIdentification.setEndToEndId(String.valueOf(dDRequestItem.getId()));

        AmountType3Choice amt = new AmountType3Choice();
        cdtTrfTxInf.setAmt(amt);
        org.meveo.admin.sepa.jaxb.pain001.ActiveOrHistoricCurrencyAndAmount instdAmt = new org.meveo.admin.sepa.jaxb.pain001.ActiveOrHistoricCurrencyAndAmount();
        if (dDRequestItem.getAmount() == null || dDRequestItem.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Amount is not valid :" + dDRequestItem.getAmount());
        }
        instdAmt.setValue(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
        instdAmt.setCcy("EUR");
        amt.setInstdAmt(instdAmt);

        org.meveo.admin.sepa.jaxb.pain001.BranchAndFinancialInstitutionIdentification4 cdtrAgent = new org.meveo.admin.sepa.jaxb.pain001.BranchAndFinancialInstitutionIdentification4();
        cdtTrfTxInf.setCdtrAgt(cdtrAgent);
        org.meveo.admin.sepa.jaxb.pain001.FinancialInstitutionIdentification7 finInstnId = new org.meveo.admin.sepa.jaxb.pain001.FinancialInstitutionIdentification7();

        finInstnId.setBIC(bankCoordiates.getBic());
        cdtrAgent.setFinInstnId(finInstnId);
        org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32 cdtr = new org.meveo.admin.sepa.jaxb.pain001.PartyIdentification32();
        cdtTrfTxInf.setCdtr(cdtr);
        cdtr.setNm(ca.getDescription());
        cdtr.setCtryOfRes("FR");
        org.meveo.admin.sepa.jaxb.pain001.CashAccount16 cdtrAccount = new org.meveo.admin.sepa.jaxb.pain001.CashAccount16();
        cdtTrfTxInf.setCdtrAcct(cdtrAccount);
        org.meveo.admin.sepa.jaxb.pain001.AccountIdentification4Choice identification = new org.meveo.admin.sepa.jaxb.pain001.AccountIdentification4Choice();
        if (!isMatched(bankCoordiates.getIban(), "[A-Z]{2,2}[0-9]{2,2}[a-zA-Z0-9]{1,30}")) {
            throw new BusinessException("IBAN of the creditor account is not valid!");
        }
        identification.setIBAN(bankCoordiates.getIban());
        cdtrAccount.setId(identification);

        RemittanceInformation5 rmtInf = new RemittanceInformation5();
        cdtTrfTxInf.setRmtInf(rmtInf);
        rmtInf.getUstrd().add("Remboursement " + dDRequestItem.getReference());
    }

    /**
     * @param field the field to validate : character sequence to be matched
     * @param pattern The expression to be compiled for regEx pattern
     * @return true if, and only if, the field matches the matcher s pattern
     */
    private boolean isMatched(String field, String pattern) {
        if (field == null || pattern == null) {
            return false;
        }
        Pattern ibanPattern = Pattern.compile(pattern);
        Matcher matcher = ibanPattern.matcher(field);
        return matcher.matches();
    }
}