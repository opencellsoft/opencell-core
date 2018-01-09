package org.meveo.admin.sepa;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.service.payments.impl.GatewayPaymentInterface;
import org.meveo.util.PaymentGatewayClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PaymentGatewayClass
public class PaynumFile implements GatewayPaymentInterface {
    Logger log = LoggerFactory.getLogger(PaynumFile.class);

    public String getDDFileName(DDRequestLOT ddRequestLot) throws BusinessException {
        String fileName = null;
        return fileName;
        // TODO (PaymentRun) move it to the job

        // String codeCreancier_paramKey = "paynum.codeCreancier";
        // String codeCreancier = (String) customFieldInstanceService.getOrCreateCFValueFromParamValue(codeCreancier_paramKey, null, appProvider, true);
        // fileName = DateUtils.formatDateWithPattern(new Date(), "yyyyMMdd")
        // + "_" + (ddRequestLot.getInvoicesNumber() - ddRequestLot.getRejectedInvoices()) + "_" + (ddRequestLot.getInvoicesAmount()
        // .setScale((appProvider.getRounding() == null ? 2 : appProvider.getRounding()), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).longValue())
        // + "_ppf_factures_" + codeCreancier + ".csv";
        //
        // String outputDir = ParamBean.getInstance().getProperty("providers.rootDir", "./opencelldata");
        //
        // outputDir = outputDir + File.separator + appProvider.getCode() + File.separator + ArConfig.getDDRequestOutputDirectory();
        // outputDir = outputDir.replaceAll("\\..", "");
        //
        // log.info("DDRequest output directory=" + outputDir);
        // File dir = new File(outputDir);
        // if (!dir.exists()) {
        // dir.mkdirs();
        // }
        // return outputDir + File.separator + fileName;
    }

    private String[] ddRequestItemToRecord(DDRequestItem ddrequestItem) throws Exception {
        String[] lineAsArray = new String[14];
        // code débiteur (optionnel)
        lineAsArray[0] = getSecretCode(ddrequestItem.getRecordedInvoice().getCustomerAccount());
        // nom débiteur (optionnel)
        lineAsArray[1] = "";
        // prénom débiteur (optionnel)
        lineAsArray[2] = "";
        // tél débiteur (optionnel)
        lineAsArray[3] = "";
        // email débiteur (optionnel)
        lineAsArray[4] = "";
        // code facture
        lineAsArray[5] = ddrequestItem.getReference();
        // code facture secondaire (optionnel)
        lineAsArray[6] = ddrequestItem.getReference();
        // montant en centimes
        lineAsArray[7] = "" + (ddrequestItem.getAmount().setScale((/* appProvider.getRounding() == null ? 2 : appProvider.getRounding() */2), RoundingMode.HALF_UP)
            .multiply(new BigDecimal(100)).longValue());
        // devise (code ISO sur 3 caractères, exemples: "EUR", "USD")
        lineAsArray[8] = ddrequestItem.getRecordedInvoice().getCustomerAccount().getTradingCurrency().getCurrencyCode();
        // date émission (optionnel)
        lineAsArray[9] = "";
        // date échéance (optionnel)
        lineAsArray[10] = "";
        // libellé (optionnel)
        lineAsArray[11] = "";
        // commentaire (optionnel)
        lineAsArray[12] = "";
        // code émetteur (optionnel)
        lineAsArray[13] = "";

        return lineAsArray;
    }

    public void exportDDRequestLot(DDRequestLOT ddRequestLot) throws Exception {
        CsvBuilder csvBuilder = new CsvBuilder(";", false);
        for (DDRequestItem ddrequestItem : ddRequestLot.getDdrequestItems()) {
            if (!ddrequestItem.hasError()) {
                csvBuilder.appendValues(ddRequestItemToRecord(ddrequestItem));
                csvBuilder.startNewLine();
            }
        }
        if (!csvBuilder.isEmpty()) {
            csvBuilder.toFile(ddRequestLot.getFileName());
        }
    }

    public void processRejectFile(File currentFile, String fileName) throws Exception {
        // TODO (PaymentRun) this class should just handle the file format and not the opencell entities

        // CsvReader csvReader = new CsvReader(currentFile.getAbsolutePath(), ';');
        //
        // while (csvReader.readRecord()) {
        // String fields[] = csvReader.getValues();
        //
        // String codeFacture = fields[3];
        // String causeRejet = fields[12];
        // RecordedInvoice recordedInvoice = recordedInvoiceService.getRecordedInvoice(codeFacture);
        // if (recordedInvoice.getPayedDDRequestItem() != null) {
        // ddRequestItemService.rejectPayment(recordedInvoice, causeRejet);
        // DDRequestLOT dDRequestLOT = recordedInvoice.getPayedDDRequestItem().getDdRequestLOT();
        // dDRequestLOT.setReturnFileName(fileName);
        // dDRequestLOTService.updateNoCheck(dDRequestLOT);
        // }
        // }

    }

    public static String getSecretCode(CustomerAccount customerAccount) throws Exception {
        String code = customerAccount.getContactInformation().getEmail();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(code.getBytes("UTF-8"));
        return Base64.encodeBase64URLSafeString(hash);
    }

    @Override
    public PayByCardResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PayByCardResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
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
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType, String countryCode) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PayByCardResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PayByCardResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException {
        throw new UnsupportedOperationException();
    }

}
