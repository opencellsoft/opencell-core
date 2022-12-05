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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.io.IOUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.model.datawarehouse.DWHAccountOperation;
import org.meveo.service.reporting.impl.DWHAccountOperationService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Named
public class AccountingJournal extends FileProducer implements Reporting {
    @Inject
    private DWHAccountOperationService accountOperationService;

    private String separator;

    private String templateFilename;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Inject
    private ParamBeanFactory paramBeanFactory;

    public void generateJournalFile(Date startDate, Date endDate, OutputFormatEnum outputFormat) {
        FileWriter writer = null;
        try {
            File file = null;
            if (outputFormat == OutputFormatEnum.PDF) {
                file = File.createTempFile("tempAccountingJournal", ".csv");
            } else if (outputFormat == OutputFormatEnum.CSV) {
                StringBuilder sb = new StringBuilder(getFilename(startDate, endDate));
                sb.append(".csv");
                file = new File(sb.toString());
            }
            writer = new FileWriter(file);
            writer.append("Date G.L.;Code operation;Libele operation;No de client;Ste;CG;CA;DA;CR;IC;GP;Debit;Credit");
            writer.append('\n');
            List<DWHAccountOperation> records = accountOperationService.getAccountingJournalRecords(startDate, endDate);
            for (DWHAccountOperation operation : records) {
                // first line
                writer.append(sdf.format(operation.getTransactionDate()) + ";");// operation
                // Date
                writer.append(operation.getOccCode() + ";");// operation Code
                writer.append(operation.getOccDescription() + ";");// operation
                // Description
                writer.append(operation.getAccountCode() + ";");// customerAccountCode
                if (operation.getAccountingCode() != null) {// accountingCode
                    // (debit)
                    writer.append(operation.getAccountingCode().toString().replace(separator.toCharArray()[0], ';') + ";");
                } else {
                    writer.append("00000;00000;0000;000;0000;00000000;00000;");
                }
                if (operation.getCategory() == 0) {// case debit
                    writer.append((operation.getAmount() + ";").replace('.', ','));// amount
                    // Debit
                    writer.append(";");// amount Credit
                } else {
                    writer.append(";");// amount Debit
                    writer.append((operation.getAmount() + ";").replace('.', ','));// amount
                    // Credit
                }
                writer.append('\n');

                // line client side
                writer.append(sdf.format(operation.getTransactionDate()) + ";");// operation
                // Date
                writer.append(operation.getOccCode() + ";");// operation Code
                writer.append(operation.getOccDescription() + ";");// operation
                // Description
                writer.append(operation.getAccountCode() + ";");// customerAccountCode
                if (operation.getAccountingCodeClientSide() != null) {// accountingCode
                    // (debit)
                    writer.append(operation.getAccountingCodeClientSide().toString().replace(separator.toCharArray()[0], ';') + ";");
                } else {
                    writer.append("00000;00000;0000;000;0000;00000000;00000;");
                }
                if (operation.getCategory() == 0) {// case debit
                    writer.append(";");// amount Debit
                    writer.append((operation.getAmount() + ";").replace('.', ','));// amount
                    // Credit
                } else {
                    writer.append((operation.getAmount() + ";").replace('.', ','));// amount
                    // Debit
                    writer.append(";");// amount Credit
                }
                writer.append('\n');
            }
            // then write invoices

            writer.flush();
            if (outputFormat == OutputFormatEnum.PDF) {
                parameters.put("startDate", startDate);
                parameters.put("endDate", endDate);
                StringBuilder sb = new StringBuilder(getFilename(startDate, endDate));
                sb.append(".pdf");
                generatePDFfile(file, sb.toString(), templateFilename, parameters);
            }
        } catch (IOException e) {
            log.error("failed to generate journal file ", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public String getFilename(Date startDate, Date endDate) {

        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        StringBuilder sb = new StringBuilder();
        ParamBean param = paramBeanFactory.getInstance();
        String reportsFolder = param.getProperty("reportsURL", "/opt/jboss/files/reports/");
        sb.append(reportsFolder);
        sb.append(appProvider.getCode());
        sb.append("_JOURNAL_TRESO_");
        sb.append(sdf.format(new Date()).toString());
        sb.append("_du_");
        sb.append(sdf.format(startDate).toString());
        sb.append("_au_");
        sb.append(sdf.format(endDate).toString());
        return sb.toString();
    }

    public void export(Report report) {
        ParamBean param = paramBeanFactory.getInstance();
        separator = param.getProperty("reporting.accountingCode.separator", ",");
        String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder", "/opt/jboss/files/reports/JasperTemplates/");
        templateFilename = jasperTemplatesFolder + "accountingJournal.jasper";
        generateJournalFile(report.getStartDate(), report.getEndDate(), report.getOutputFormat());

    }

}
