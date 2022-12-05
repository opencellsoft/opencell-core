/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.CacheKeyStr;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.model.datawarehouse.DWHAccountOperation;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.reporting.impl.DWHAccountOperationService;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Named
public class AccountingDetail extends FileProducer implements Reporting {
    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private DWHAccountOperationService accountOperationTransformationService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    private HashMap<CacheKeyStr, BigDecimal> balances = new HashMap<CacheKeyStr, BigDecimal>();
    private HashMap<CacheKeyStr, String> customerNames = new HashMap<CacheKeyStr, String>();

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Inject
    private ParamBeanFactory paramBeanFactory;

    public void generateAccountingDetailFile(Date startDate, Date endDate, OutputFormatEnum outputFormat) {
        FileWriter writer = null;
        try {
            File file = null;
            if (outputFormat == OutputFormatEnum.PDF) {
                file = File.createTempFile("tempAccountingDetail", ".csv");
            } else if (outputFormat == OutputFormatEnum.CSV) {
                StringBuilder sb = new StringBuilder(getFilename());
                sb.append(".csv");
                file = new File(sb.toString());
            }
            writer = new FileWriter(file);
            writer.append("N° compte client;Nom du compte client;Code operation;Référence comptable;Date de l'opération;Date d'exigibilité;Débit;Credit;Solde client");
            writer.append('\n');
            List<DWHAccountOperation> list = accountOperationTransformationService.getAccountingDetailRecords(new Date());
            Iterator<DWHAccountOperation> itr = list.iterator();
            String previousAccountCode = null;
            BigDecimal solde = BigDecimal.ZERO;
            BigDecimal amount = BigDecimal.ZERO;
            while (itr.hasNext()) {
                DWHAccountOperation accountOperationTransformation = itr.next();
                if (previousAccountCode != null) {
                    if (!previousAccountCode.equals(accountOperationTransformation.getAccountCode())) {
                        writer.append(String.valueOf(solde).replace('.', ','));
                        solde = BigDecimal.ZERO;
                    }
                    writer.append('\n');
                }
                if (accountOperationTransformation.getStatus() == 2) {
                    AccountOperation accountOperation = accountOperationService.findById(accountOperationTransformation.getId());
                    amount = accountOperation.getUnMatchingAmount();
                } else {
                    amount = accountOperationTransformation.getAmount();
                }

                amount = accountOperationTransformation.getAmount();
                if (accountOperationTransformation.getCategory() == 1) {
                    solde = solde.subtract(amount);
                } else {
                    solde = solde.add(amount);
                }
                previousAccountCode = accountOperationTransformation.getAccountCode();
                writer.append(accountOperationTransformation.getAccountCode() + ";"); // Num
                                                                                      // compte
                                                                                      // client
                writer.append(accountOperationTransformation.getAccountDescription() + ";");
                writer.append(accountOperationTransformation.getOccCode() + ";");
                writer.append(accountOperationTransformation.getReference() + ";");
                writer.append(sdf.format(accountOperationTransformation.getTransactionDate()) + ";");
                writer.append(sdf.format(accountOperationTransformation.getDueDate()) + ";");
                if (accountOperationTransformation.getCategory() == 0) {
                    writer.append((amount + ";").replace('.', ','));
                } else {
                    writer.append("0;");
                }
                if (accountOperationTransformation.getCategory() == 1) {
                    writer.append((amount + ";").replace('.', ','));
                } else {
                    writer.append("0;");
                }
            }
            writer.append(String.valueOf(solde).replace('.', ','));
            writer.append('\n');

            writer.flush();
            if (outputFormat == OutputFormatEnum.PDF) {
                parameters.put("startDate", startDate);
                parameters.put("endDate", endDate);

                StringBuilder sb = new StringBuilder(getFilename());
                sb.append(".pdf");
                String jasperTemplatesFolder = paramBeanFactory.getInstance().getProperty("reports.jasperTemplatesFolder", "/opt/jboss/files/reports/JasperTemplates/");
                String templateFilename = jasperTemplatesFolder + "accountingDetail.jasper";
                generatePDFfile(file, sb.toString(), templateFilename, parameters);
            }
        } catch (IOException e) {
            log.error("failed to generate accounting detail File", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    log.error("failed to close writer.", ex);
                }
            }
        }

    }

    public String getCustomerName(String customerAccountCode) {
        String result = "";
        if (customerNames.containsKey(new CacheKeyStr(currentUser.getProviderCode(), customerAccountCode))) {
            result = customerNames.get(new CacheKeyStr(currentUser.getProviderCode(), customerAccountCode));
        } else {
            CustomerAccount account = customerAccountService.findByCode(customerAccountCode);
            if (account.getName() != null) {
                result = account.getName().getTitle().getCode();
                if (account.getName().getFirstName() != null) {
                    result += " " + account.getName().getFirstName();
                }
                if (account.getName().getLastName() != null) {
                    result += " " + account.getName().getLastName();
                }
            }
        }
        return result;
    }

    public BigDecimal getCustomerBalanceDue(String customerAccountCode, Date atDate) {
        BigDecimal result = BigDecimal.ZERO;
        if (balances.containsKey(new CacheKeyStr(currentUser.getProviderCode(), customerAccountCode))) {
            result = balances.get(new CacheKeyStr(currentUser.getProviderCode(), customerAccountCode));
        } else {
            try {
                result = customerAccountService.customerAccountBalanceDue(null, customerAccountCode, atDate);
                balances.put(new CacheKeyStr(currentUser.getProviderCode(), customerAccountCode), result);
            } catch (BusinessException e) {
                log.error("Error while getting balance dues", e);
            }
        }
        return result;
    }

    public String getFilename() {

        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        StringBuilder sb = new StringBuilder();
        ParamBean param = paramBeanFactory.getInstance();
        String reportsFolder = param.getProperty("reportsURL", "/opt/jboss/files/reports/");
        sb.append(reportsFolder);
        sb.append(appProvider.getCode() + "_");
        sb.append("INVENTAIRE_CCLIENT_");
        sb.append(sdf.format(new Date()).toString());
        return sb.toString();
    }

    public void export(Report report) {

        accountOperationTransformationService = null;
        customerAccountService = null;
        accountOperationService = null;

        generateAccountingDetailFile(report.getStartDate(), report.getEndDate(), report.getOutputFormat());

    }

}
