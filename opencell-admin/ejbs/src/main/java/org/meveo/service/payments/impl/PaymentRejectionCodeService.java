package org.meveo.service.payments.impl;

import static java.io.File.separator;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.meveo.apiv2.payments.RejectionCodeImportMode.REPLACE;
import static org.meveo.apiv2.payments.RejectionCodeImportMode.UPDATE;
import static org.meveo.commons.utils.StringUtils.EMPTY;
import static org.meveo.commons.utils.StringUtils.isNotBlank;
import static org.meveo.service.payments.impl.RejectionCodeImportResult.EMPTY_IMPORT_RESULT;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentRejectionCode;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.TradingLanguageService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
public class PaymentRejectionCodeService extends BusinessService<PaymentRejectionCode> {

    public static final String FILE_PATH_RESULT_LABEL = "FILE_PATH";
    public static final String EXPORT_SIZE_RESULT_LABEL = "EXPORT_SIZE";
    public static final String ENCODED_FILE_RESULT_LABEL = "ENCODED_FILE";

    private static final String EXPORT_DATE_FORMAT_PATTERN = "yyyyMMdd_hhmmss";
    private static final String DESCRIPTION_I18N_REGEX = "Description ([a-zA-Z]*$)";
    public static final String SEPARATOR = ";";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(EXPORT_DATE_FORMAT_PATTERN);

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private PaymentGatewayService paymentGatewayService;

    /**
     * Create payment rejection code
     *
     * @param rejectionCode payment rejection code
     */
    @Override
    public void create(PaymentRejectionCode rejectionCode) {
        if (findByCodeAndPaymentGateway(rejectionCode.getCode(),
                rejectionCode.getPaymentGateway().getId()).isPresent()) {
            throw new BusinessException(format("Rejection code with code %s already exists in gateway %s",
                    rejectionCode.getCode(), rejectionCode.getPaymentGateway().getCode()));
        }
        super.create(rejectionCode);

    }

    /**
     * Find a payment rejection code using code and payment gateway id
     *
     * @param code             payment rejection code
     * @param paymentGatewayId payment gateway id
     * @return Optional of PaymentRejectionCode
     */
    public Optional<PaymentRejectionCode> findByCodeAndPaymentGateway(String code, Long paymentGatewayId) {
        try {
            return of((PaymentRejectionCode) getEntityManager()
                    .createNamedQuery("PaymentRejectionCode.findByCodeAndPaymentGateway")
                    .setParameter("code", code)
                    .setParameter("paymentGatewayId", paymentGatewayId)
                    .getSingleResult());
        } catch (NoResultException exception) {
            return empty();
        }
    }

    /**
     * Update payment rejection code
     *
     * @param rejectionCode payment rejection code
     * @return RejectionCode updated entity
     */
    @Override
    public PaymentRejectionCode update(PaymentRejectionCode rejectionCode) {
        if (findByCodeAndPaymentGateway(rejectionCode.getCode(),
                rejectionCode.getPaymentGateway().getId()).isPresent()) {
            throw new BusinessException(format("Rejection code with code %s already exists in gateway %s",
                    rejectionCode.getCode(), rejectionCode.getPaymentGateway().getCode()));
        }
        return super.update(rejectionCode);
    }

    /**
     * Clear rejectionsCodes by gateway
     *
     * @param paymentGateway payment gateway
     */
    public int clearAll(PaymentGateway paymentGateway) {
        String namedQuery = paymentGateway != null
                ? "PaymentRejectionCode.clearAllByPaymentGateway" : "PaymentRejectionCode.clearAll";
        Query clearQuery = getEntityManager().createNamedQuery(namedQuery);
        if (paymentGateway != null) {
            clearQuery.setParameter("paymentGatewayId", paymentGateway.getId());
        }
        try {
            return clearQuery.executeUpdate();
        } catch (Exception exception) {
            throw new BusinessException("Error occurred during rejection codes clearing " + exception.getMessage());
        }
    }

    /**
     * Export rejectionsCodes by gateway
     *
     * @param paymentGateway payment gateway
     */
    public Map<String, Object> export(PaymentGateway paymentGateway) {
        Map<String, Object> filters = null;
        if (paymentGateway != null) {
            filters = new HashMap<>();
            filters.put("paymentGateway", paymentGateway);
        }
        final String exportFileName = "PaymentRejectionCodes_"
                + (paymentGateway != null ? paymentGateway.getCode() : "AllGateways") + "_" + dateFormatter.format(new Date());
        List<Object[]> languagesDetails = getAvailableTradingLanguage();
        final List<String> dataToExport = prepareLines(list(new PaginationConfiguration(filters)), languagesDetails);
        try {
            final String exportFile = buildExportFilePath(exportFileName, "exports");
            final String header = "Payment gateway;Rejection code;Description;" + getAvailableTradingLanguages(languagesDetails);
            try (PrintWriter writer = new PrintWriter(exportFile)) {
                writer.println(header);
                dataToExport.forEach(writer::println);
            }
            Map<String, Object> exportResult = new HashMap<>();
            exportResult.put(FILE_PATH_RESULT_LABEL, exportFile);
            exportResult.put(EXPORT_SIZE_RESULT_LABEL, dataToExport.size());
            exportResult.put(ENCODED_FILE_RESULT_LABEL, Base64.getEncoder().encodeToString(readFileToByteArray(new File(exportFile))));
            log.debug("Rejection codes export file name : " + exportFileName);
            log.debug("Rejection codes export size : " + dataToExport.size());
            return exportResult;
        } catch (IOException exception) {
            throw new BusinessException(exception.getMessage());
        }
    }

    private List<Object[]> getAvailableTradingLanguage() {
        return (List<Object[]>) tradingLanguageService.getEntityManager()
                .createNamedQuery("TradingLanguage.findLanguageDetails")
                .getResultList();
    }

    private List<String> prepareLines(List<PaymentRejectionCode> rejectionCodes, List<Object[]> languagesDetails) {
        return rejectionCodes.stream()
                .map(rejectionCode -> convertToCSV(rejectionCode.getPaymentGateway().getCode(),
                        rejectionCode.getCode(),
                        rejectionCode.getDescription(),
                        buildI18nDescription(rejectionCode.getDescriptionI18n(), languagesDetails)))
                .collect(toList());
    }

    private String buildI18nDescription(Map<String, String> descriptionI18n, List<Object[]> languagesDetails) {
        if(languagesDetails == null) {
            return EMPTY;
        }
        return languagesDetails.stream()
                .map(language -> language[2])
                .map(descriptionI18n::get)
                .collect(joining(";"));
    }

    private String buildExportFilePath(String fileName, String directoryName) {
        String exportDirectoryPath = paramBeanFactory.getChrootDir() + separator + directoryName + separator;
        File exportDirectory = new File(exportDirectoryPath);
        if (!exportDirectory.exists()) {
            exportDirectory.mkdirs();
        }
        return exportDirectory.getPath() + separator + fileName + ".csv";
    }

    public String convertToCSV(String... data) {
        return join(";", data);
    }

    private String getAvailableTradingLanguages(List<Object[]> languagesDetails) {
        String tradingLanguages = "";
        if (languagesDetails != null) {
            tradingLanguages = languagesDetails.stream()
                    .map(language -> "Description " + language[2])
                    .collect(joining(";"));
        }
        return tradingLanguages;
    }

    public RejectionCodeImportResult importRejectionCodes(ImportRejectionCodeConfig config) {
        byte[] importStream = Base64.getDecoder().decode(config.getBase64Csv());
        String[] lines = new String(importStream).split("\\n");
        List<PaymentRejectionCode> rejectionCodes = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        RejectionCodeImportResult rejectionCodeImportResult = EMPTY_IMPORT_RESULT;
        if (lines.length > 0) {
            List<Object[]> languagesDetails = getAvailableTradingLanguage();
            String[] header = lines[0].trim().split(SEPARATOR);
            for (int index = 1; index < lines.length; index++) {
                String[] line = lines[index].split(SEPARATOR);
                PaymentRejectionCode rejectionCode = createFromImportData(line, header, errors, index, config);
                if (rejectionCode != null) {
                    String checkResult = checkDescriptionI18(rejectionCode.getDescriptionI18n(), languagesDetails);
                    if (isNotBlank(checkResult)) {
                        String error = format("Error occurred during importing rejection code [code = %s, gateway= %s]: %s",
                                rejectionCode.getCode(), rejectionCode.getPaymentGateway().getCode(), checkResult);
                        if (!config.isIgnoreLanguageErrors()) {
                            throw new BusinessException(error);
                        } else {
                            errors.add(error);
                        }
                    }
                    rejectionCodes.add(rejectionCode);
                }
            }
            rejectionCodeImportResult = saveImportData(config, rejectionCodes, lines.length - 1, errors);
        }
        return rejectionCodeImportResult;
    }

    private PaymentRejectionCode createFromImportData(String[] line, String[] header,
                                                      List<String> errors, int lineNumber, ImportRejectionCodeConfig config) {
        PaymentRejectionCode rejectionCode = new PaymentRejectionCode();
        String paymentGatewayCode = null;
        PaymentGateway paymentGateway = null;
        Map<String, String> inputLanguages = new HashMap<>();
        for (int i = 0; i < line.length; i++) {
            if ("payment gateway".equalsIgnoreCase(header[i])) {
                paymentGatewayCode = line[i];
                paymentGateway = paymentGatewayService.findByCode(line[i]);
            }
            if ("rejection code".equalsIgnoreCase(header[i])) {
                if (config.getMode() == UPDATE) {
                    rejectionCode = ofNullable(findByCode(line[i])).orElse(new PaymentRejectionCode());
                } else {
                    rejectionCode = new PaymentRejectionCode();
                }
                rejectionCode.setCode(line[i]);
            }
            if ("description".equalsIgnoreCase(header[i])) {
                rejectionCode.setDescription(line[i]);
            }
            if (header[i] != null && header[i].matches(DESCRIPTION_I18N_REGEX)) {
                inputLanguages.put(header[i].split(" ")[1], line[i].trim());
            }
        }
        if (paymentGateway == null) {
            errors.add(format("Line %d import ignored, payment gateway %s not found", lineNumber, paymentGatewayCode));
            return null;
        }
        rejectionCode.setPaymentGateway(paymentGateway);
        rejectionCode.setDescriptionI18n(inputLanguages);
        return rejectionCode;
    }

    private String checkDescriptionI18(Map<String, String> inputLanguages, List<Object[]> languagesDetails) {
        if (languagesDetails == null || languagesDetails.isEmpty()) {
            return EMPTY;
        }
        return languagesDetails.stream()
                .filter(language -> !inputLanguages.containsKey(language[2]) && inputLanguages.get(language[2]) == null)
                .map(language -> "Trading language " + language[2] + " not provided")
                .collect(joining("\\n"));
    }

    private RejectionCodeImportResult saveImportData(ImportRejectionCodeConfig config,
                                                     List<PaymentRejectionCode> rejectionCodes,
                                                     int importSize, List<String> errors) {
        RejectionCodeImportResult rejectionCodeImportResult = EMPTY_IMPORT_RESULT;
        if (rejectionCodes != null && !rejectionCodes.isEmpty()) {
            Map<PaymentGateway, List<PaymentRejectionCode>> rejectionCodesByGateway = rejectionCodes.stream()
                    .collect(groupingBy(PaymentRejectionCode::getPaymentGateway));
            for (Map.Entry<PaymentGateway, List<PaymentRejectionCode>> entry : rejectionCodesByGateway.entrySet()) {
                if (REPLACE.equals(config.getMode())) {
                    remove(findBYPaymentGateway(entry.getKey().getId()));
                    entry.getValue().forEach(this::create);
                } else {
                    entry.getValue().forEach(rejectionCode -> {
                        if (rejectionCode.getId() != null) {
                            super.update(rejectionCode);
                        } else {
                            create(rejectionCode);
                        }
                    });
                }
            }
            rejectionCodeImportResult =
                    new RejectionCodeImportResult(importSize, rejectionCodes.size(), errors.size(), errors);
        }
        return rejectionCodeImportResult;
    }

    /**
     * Remove rejection codes
     *
     * @param rejectionCodes List of rejection codes
     */
    public void remove(List<PaymentRejectionCode> rejectionCodes) {
        if (rejectionCodes == null || rejectionCodes.isEmpty()) {
            return;
        }
        rejectionCodes.forEach(this::remove);
        getEntityManager().flush();
    }

    /**
     * Find payment rejection codes by payment gateway id
     *
     * @param paymentGatewayId payment gateway id
     * @return List of PaymentRejectionCode
     */
    public List<PaymentRejectionCode> findBYPaymentGateway(Long paymentGatewayId) {
        if (paymentGatewayId == null) {
            return emptyList();
        }
        return (List<PaymentRejectionCode>) getEntityManager()
                .createNamedQuery("PaymentRejectionCode.findByPaymentGateway")
                .setParameter("paymentGatewayId", paymentGatewayId)
                .getResultList();
    }
}
