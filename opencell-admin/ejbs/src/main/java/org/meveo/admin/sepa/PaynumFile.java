package org.meveo.admin.sepa;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.payments.impl.AbstractDDRequestBuilder;
import org.meveo.util.DDRequestBuilderClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PaynumFile.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */
@DDRequestBuilderClass
public class PaynumFile extends AbstractDDRequestBuilder {
    
    /** The log. */
    Logger log = LoggerFactory.getLogger(PaynumFile.class);


    @Override
    public String getDDFileName(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
        if(ddRequestLot.getOperationCategoryToProcess() == OperationCategoryEnum.CREDIT) {
            throw new UnsupportedOperationException("Refund Sepa not implimented for Paynum");
        }
        ParamBean paramBean = ParamBean.getInstanceByProvider(appProvider.getCode());
        String fileName = null;
        String codeCreancier_paramKey = "paynum.codeCreancier";
        String codeCreancier = paramBean.getProperty(codeCreancier_paramKey, null);
        fileName = DateUtils.formatDateWithPattern(new Date(), "yyyyMMdd") + "_" + (ddRequestLot.getNbItemsOk() - ddRequestLot.getNbItemsKo()) + "_"
                + (ddRequestLot.getTotalAmount().setScale(appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()).multiply(new BigDecimal(100)).longValue())
                + "_ppf_factures_" + codeCreancier + ".csv";

        String outputDir = ArConfig.getDDRequestOutputDirectory();
        outputDir = outputDir.replaceAll("\\..", "");

        log.info("DDRequest output directory=" + outputDir);
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return outputDir + File.separator + fileName;
    }


    @Override
    public void generateDDRequestLotFile(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
        if(ddRequestLot.getOperationCategoryToProcess() == OperationCategoryEnum.CREDIT) {
            throw new UnsupportedOperationException("Refund Sepa not implimented for Paynum");
        }
        try {
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
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }


    @Override
    public DDRejectFileInfos processSDDRejectedFile(File file) throws BusinessException {
        DDRejectFileInfos ddRejectFileInfos = new DDRejectFileInfos();
        try {
            ddRejectFileInfos.setFileName(file.getName());
            CsvReader csvReader = new CsvReader(file.getAbsolutePath(), ';');
            while (csvReader.readRecord()) {
                String fields[] = csvReader.getValues();
                String ddRequestLotId = fields[1];
                String codeFacture = fields[3];
                String causeRejet = fields[12];

                ddRejectFileInfos.setDdRequestLotId(new Long(ddRequestLotId));
                ddRejectFileInfos.getListInvoiceRefsRejected().put(new Long(codeFacture), causeRejet);
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return ddRejectFileInfos;
    }

    /**
     * Dd request item to record.
     *
     * @param ddrequestItem the ddrequest item
     * @return the string[]
     * @throws Exception the exception
     */
    private String[] ddRequestItemToRecord(DDRequestItem ddrequestItem) throws Exception {
        String[] lineAsArray = new String[14];
        // code débiteur (optionnel)
        lineAsArray[0] = getSecretCode(ddrequestItem.getAccountOperations().get(0).getCustomerAccount());
        // nom débiteur (optionnel)
        lineAsArray[1] = "";
        // prénom débiteur (optionnel)
        lineAsArray[2] = "";
        // tél débiteur (optionnel)
        lineAsArray[3] = "";
        // email débiteur (optionnel)
        lineAsArray[4] = "";
        // code facture
        lineAsArray[5] = "" + ddrequestItem.getId();
        // code facture secondaire (optionnel)
        lineAsArray[6] = ddrequestItem.getReference();
        // montant en centimes
        lineAsArray[7] = "" + (ddrequestItem.getAmount().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).longValue());
        // devise (code ISO sur 3 caractères, exemples: "EUR", "USD")
        lineAsArray[8] = ddrequestItem.getAccountOperations().get(0).getCustomerAccount().getTradingCurrency().getCurrencyCode();
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

    /**
     * Gets the secret code.
     *
     * @param customerAccount the customer account
     * @return the secret code
     * @throws Exception the exception
     */
    private static String getSecretCode(CustomerAccount customerAccount) throws Exception {
        String code = customerAccount.getContactInformationNullSafe().getEmail();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(code.getBytes("UTF-8"));
        return Base64.encodeBase64URLSafeString(hash);
    }


    @Override
    public DDRejectFileInfos processSCTRejectedFile(File file) throws BusinessException {
            throw new UnsupportedOperationException("Refund Sepa not implimented for Paynum");        
    }
}