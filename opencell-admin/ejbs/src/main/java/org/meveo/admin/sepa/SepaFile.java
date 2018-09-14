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
import org.meveo.admin.sepa.jaxb.Pain008;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.GrpHdr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.GrpHdr.InitgPty;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.Cdtr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAcct;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAcct.Id;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAgt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrAgt.FinInstnId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId.Othr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.CdtrSchmeId.Id.PrvtId.Othr.SchmeNm;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.Dbtr;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAgt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DrctDbtTx;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DrctDbtTx.MndtRltdInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.InstdAmt;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.PmtId;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf.LclInstrm;
import org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.PmtTpInf.SvcLvl;
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
            Pain008 document = new Pain008();
            CstmrDrctDbtInitn message = new CstmrDrctDbtInitn();
            document.setCstmrDrctDbtInitn(message);
            document.setXmlns("urn:iso:std:iso:20022:tech:xsd:pain.008.001.02");
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
                    ddRejectFileInfos.getListInvoiceRefsRejected().put(txInfAndSts.getOrgnlEndToEndId(),"RJCT");                    
                }
            }
            
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return ddRejectFileInfos;
    }
    private void addHeader(CstmrDrctDbtInitn message, DDRequestLOT ddRequestLOT, Provider appProvider) throws Exception {
        GrpHdr groupHeader = new GrpHdr();
        message.setGrpHdr(groupHeader);
        groupHeader.setMsgId(ArConfig.getDDRequestHeaderReference() + "-" + ddRequestLOT.getId());
        groupHeader.setCreDtTm(DateUtils.dateToXMLGregorianCalendar(new Date()));
        groupHeader.setNbOfTxs(ddRequestLOT.getDdrequestItems().size());
        groupHeader.setCtrlSum(ddRequestLOT.getInvoicesAmount().setScale(2, RoundingMode.HALF_UP));
        InitgPty initgPty = new InitgPty();
        initgPty.setNm(appProvider.getDescription());
        groupHeader.setInitgPty(initgPty);

    }

    private void addPaymentInformation(CstmrDrctDbtInitn Message, DDRequestItem dDRequestItem, Provider appProvider) throws Exception {

        log.info("addPaymentInformation dDRequestItem id=" + dDRequestItem.getId());
        ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
        PmtInf PaymentInformation = new PmtInf();
        Message.getPmtInf().add(PaymentInformation);
        PaymentInformation.setPmtInfId(ArConfig.getDDRequestHeaderReference() + "-" + dDRequestItem.getId());
        PaymentInformation.setPmtMtd(paramBean.getProperty("sepa.PmtMtd", "TRF"));
        PaymentInformation.setNbOfTxs(1);
        PaymentInformation.setCtrlSum(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
        PmtTpInf PaymentTypeInformation = new PmtTpInf();
        PaymentInformation.setPmtTpInf(PaymentTypeInformation);
        SvcLvl ServiceLevel = new SvcLvl();
        PaymentTypeInformation.setSvcLvl(ServiceLevel);
        ServiceLevel.setCd("SEPA");
        LclInstrm LocalInstrument = new LclInstrm();
        PaymentTypeInformation.setLclInstrm(LocalInstrument);
        LocalInstrument.setCd(paramBean.getProperty("sepa.LclInstrm", "CORE"));
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
        addTransaction(dDRequestItem, PaymentInformation);
    }

    private void addTransaction(DDRequestItem dDRequestItem, PmtInf PaymentInformation) throws Exception {
        CustomerAccount ca = dDRequestItem.getAccountOperations().get(0).getCustomerAccount();

        DrctDbtTxInf DirectDebitTransactionInformation = new DrctDbtTxInf();
        PaymentInformation.getDrctDbtTxInf().add(DirectDebitTransactionInformation);
        PmtId PaymentIdentification = new PmtId();
        DirectDebitTransactionInformation.setPmtId(PaymentIdentification);
        PaymentIdentification.setInstrId(""+dDRequestItem.getId());
        PaymentIdentification.setEndToEndId(""+dDRequestItem.getId());
        InstdAmt InstructedAmount = new InstdAmt();
        DirectDebitTransactionInformation.setInstdAmt(InstructedAmount);
        InstructedAmount.setValue(dDRequestItem.getAmount().setScale(2, RoundingMode.HALF_UP));
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
        FinancialInstitutionIdentification.setBIC("bic");
        DebtorAgent.setFinInstnId(FinancialInstitutionIdentification);

        Dbtr Debtor = new Dbtr();
        DirectDebitTransactionInformation.setDbtr(Debtor);
        Debtor.setNm(ca.getDescription());

        DbtrAcct DebtorAccount = new DbtrAcct();
        DirectDebitTransactionInformation.setDbtrAcct(DebtorAccount);
        org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct.Id Identification = new org.meveo.admin.sepa.jaxb.Pain008.CstmrDrctDbtInitn.PmtInf.DrctDbtTxInf.DbtrAcct.Id();
        Identification.setIBAN("iban");
        DebtorAccount.setId(Identification);

    }
}
