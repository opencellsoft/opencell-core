package org.meveo.admin.sepa;

import java.io.File;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.jaxb.Pain008;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.GrpHdr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.GrpHdr.InitgPty;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.Cdtr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAcct;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAgt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAcct.Id;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAgt.FinInstnId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId.Othr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId.Othr.SchmeNm;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.Dbtr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAgt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DrctDbtTx;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.InstdAmt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.PmtId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DrctDbtTx.MndtRltdInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf.LclInstrm;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf.SvcLvl;
import org.meveo.admin.util.ArConfig;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.payments.impl.GatewayPaymentInterface;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PaymentGatewayClass
public class SepaFile implements GatewayPaymentInterface {
    Logger log = LoggerFactory.getLogger(SepaFile.class);

    @Inject
    @ApplicationProvider
    private Provider appProvider;
    
    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    public String getDDFileName(DDRequestLOT ddRequestLot) {
        String fileName = ArConfig.getDDRequestFileNamePrefix() + ddRequestLot.getId();
        fileName = fileName + "_" + appProvider.getCode();
        fileName = fileName + "_" + DateUtils.formatDateWithPattern(new Date(), "yyyyMMdd") + ArConfig.getDDRequestFileNameExtension();

        String outputDir = ParamBean.getInstance().getChrootDir(currentUser.getProviderCode());

        outputDir = outputDir + File.separator + ArConfig.getDDRequestOutputDirectory();
        outputDir = outputDir.replaceAll("\\..", "");

        log.info("DDRequest output directory=" + outputDir);
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return outputDir + File.separator + fileName;
    }

    public void exportDDRequestLot(DDRequestLOT ddRequestLot) throws Exception {
        Pain008 document = new Pain008();
        CstmrDrctDbtInitn Message = new CstmrDrctDbtInitn();
        document.setCstmrDrctDbtInitn(Message);
        document.setXmlns("urn:iso:std:iso:20022:tech:xsd:pain.008.001.02");
        addHeader(Message, ddRequestLot);
        for (DDRequestItem ddrequestItem : ddRequestLot.getDdrequestItems()) {
            if (!ddrequestItem.hasError()) {
                addPaymentInformation(Message, ddrequestItem);
            }
        }
        String schemaLocation = ParamBean.getInstance().getProperty("sepa.schemaLocation.pain008",
            "https://github.com/w2c/sepa-sdd-xml-generator/blob/master/validation_schemes/pain.008.001.02.xsd");
        JAXBUtils.marshaller(document, new File(ddRequestLot.getFileName()), schemaLocation);

    }

    private void addHeader(CstmrDrctDbtInitn Message, DDRequestLOT ddRequestLOT) throws Exception {
        GrpHdr groupHeader = new GrpHdr();
        Message.setGrpHdr(groupHeader);
        groupHeader.setMsgId(ArConfig.getDDRequestHeaderReference() + "-" + ddRequestLOT.getId());
        groupHeader.setCreDtTm(DateUtils.dateToXMLGregorianCalendar(new Date()));
        groupHeader.setNbOfTxs(ddRequestLOT.getDdrequestItems().size());
        groupHeader.setCtrlSum(ddRequestLOT.getInvoicesAmount().setScale(2, RoundingMode.HALF_UP));
        InitgPty initgPty = new InitgPty();
        initgPty.setNm(appProvider.getDescription());
        groupHeader.setInitgPty(initgPty);

    }

    private void addPaymentInformation(CstmrDrctDbtInitn Message, DDRequestItem dDRequestItem) throws Exception {

        log.info("addPaymentInformation dDRequestItem id=" + dDRequestItem.getId());

        PmtInf PaymentInformation = new PmtInf();
        Message.getPmtInf().add(PaymentInformation);
        PaymentInformation.setPmtInfId(ArConfig.getDDRequestHeaderReference() + "-" + dDRequestItem.getId());
        PaymentInformation.setPmtMtd(ParamBean.getInstance().getProperty("sepa.PmtMtd", "TRF"));
        PaymentInformation.setNbOfTxs(1);
        PaymentInformation.setCtrlSum(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
        PmtTpInf PaymentTypeInformation = new PmtTpInf();
        PaymentInformation.setPmtTpInf(PaymentTypeInformation);
        SvcLvl ServiceLevel = new SvcLvl();
        PaymentTypeInformation.setSvcLvl(ServiceLevel);
        ServiceLevel.setCd("SEPA");
        LclInstrm LocalInstrument = new LclInstrm();
        PaymentTypeInformation.setLclInstrm(LocalInstrument);
        LocalInstrument.setCd(ParamBean.getInstance().getProperty("sepa.LclInstrm", "CORE"));
        PaymentTypeInformation.setSeqTp("FRST");

        PaymentInformation.setReqdColltnDt(DateUtils.dateToXMLGregorianCalendar(new Date())); // à revoir

        BankCoordinates providerBC = appProvider.getBankCoordinates();
        if (providerBC == null) {
            throw new BusinessException("Missing bank information on provider");
        }
        Cdtr Creditor = new Cdtr();
        Creditor.setNm(appProvider.getDescription());
        PaymentInformation.setCdtr(Creditor);

        CdtrAcct creditorAccount = new CdtrAcct();
        PaymentInformation.setCdtrAcct(creditorAccount);
        Id identification = new Id();
        creditorAccount.setId(identification);
        identification.setIBAN(providerBC.getIban());

        CdtrAgt CreditorAgent = new CdtrAgt();
        PaymentInformation.setCdtrAgt(CreditorAgent);
        FinInstnId FinancialInstitutionIdentification = new FinInstnId();
        CreditorAgent.setFinInstnId(FinancialInstitutionIdentification);
        FinancialInstitutionIdentification.setBIC(providerBC.getBic());
        CdtrSchmeId CreditorSchemeIdentification = new CdtrSchmeId();
        PaymentInformation.setCdtrSchmeId(CreditorSchemeIdentification);
        org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id CdtrSchmeId = new org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id();
        CreditorSchemeIdentification.setId(CdtrSchmeId);
        PrvtId privateidentifier = new PrvtId();
        CdtrSchmeId.setPrvtId(privateidentifier);
        Othr other = new Othr();
        privateidentifier.setOthr(other);
        other.setId(providerBC.getIcs());
        SchmeNm SchemeName = new SchmeNm();
        other.setSchmeNm(SchemeName);
        SchemeName.setPrtry("SEPA");
        addTransaction(dDRequestItem.getRecordedInvoice(), PaymentInformation);
    }

    private void addTransaction(RecordedInvoice invoice, PmtInf PaymentInformation) throws Exception {
        CustomerAccount ca = invoice.getCustomerAccount();

        DrctDbtTxInf DirectDebitTransactionInformation = new DrctDbtTxInf();
        PaymentInformation.getDrctDbtTxInf().add(DirectDebitTransactionInformation);
        PmtId PaymentIdentification = new PmtId();
        DirectDebitTransactionInformation.setPmtId(PaymentIdentification);
        PaymentIdentification.setInstrId(invoice.getReference());
        PaymentIdentification.setEndToEndId(invoice.getReference());
        InstdAmt InstructedAmount = new InstdAmt();
        DirectDebitTransactionInformation.setInstdAmt(InstructedAmount);
        InstructedAmount.setValue(invoice.getAmount().setScale(2, RoundingMode.HALF_UP));
        InstructedAmount.setCcy("EUR");
        DrctDbtTx DirectDebitTransaction = new DrctDbtTx();
        DirectDebitTransactionInformation.setDrctDbtTx(DirectDebitTransaction);
        MndtRltdInf MandateRelatedInformation = new MndtRltdInf();
        DirectDebitTransaction.setMndtRltdInf(MandateRelatedInformation);
        PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
        if (preferedPaymentMethod != null && preferedPaymentMethod instanceof DDPaymentMethod) {
            MandateRelatedInformation.setMndtId(((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification());
            MandateRelatedInformation.setDtOfSgntr(DateUtils.dateToXMLGregorianCalendar(((DDPaymentMethod) preferedPaymentMethod).getMandateDate()));
        }
        DbtrAgt DebtorAgent = new DbtrAgt();
        DirectDebitTransactionInformation.setDbtrAgt(DebtorAgent);
        org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAgt.FinInstnId FinancialInstitutionIdentification = new org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAgt.FinInstnId();
        FinancialInstitutionIdentification.setBIC(invoice.getPaymentInfo6());
        DebtorAgent.setFinInstnId(FinancialInstitutionIdentification);

        Dbtr Debtor = new Dbtr();
        DirectDebitTransactionInformation.setDbtr(Debtor);
        Debtor.setNm(ca.getDescription());

        DbtrAcct DebtorAccount = new DbtrAcct();
        DirectDebitTransactionInformation.setDbtrAcct(DebtorAccount);
        org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct.Id Identification = new org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct.Id();
        Identification.setIBAN(invoice.getPaymentInfo());
        DebtorAccount.setId(Identification);

    }

    @Override
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelPayment(String paymentID) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doBulkPaymentAsFile(DDRequestLOT ddRequestLot) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doPaymentSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MandatInfoDto checkMandat(String mandatReference,String mandateId) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }


    @Override
    public PaymentResponseDto checkPayment(String paymentID, PaymentMethodEnum paymentMethodType) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    /*
     * private String enleverAccent(String value) { if (StringUtils.isBlank(value)) { return value; } return Normalizer.normalize(value,
     * Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""); }
     */

}
