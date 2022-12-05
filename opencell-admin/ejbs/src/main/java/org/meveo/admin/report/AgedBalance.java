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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.io.IOUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.bi.OutputFormatEnum;
import org.meveo.model.bi.Report;
import org.meveo.service.reporting.impl.DWHAccountOperationService;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@Named
public class AgedBalance extends FileProducer implements Reporting {
    @Inject
    private DWHAccountOperationService accountOperationService;

    final static String DEBIT = "0";
    final static String CREDIT = "1";

    @Inject
    private ParamBeanFactory paramBeanFactory;

    public void generateAgedBalanceFile(Date date, OutputFormatEnum outputFormat) {
        FileWriter writer = null;
        try {
            date = new Date();
            File file = null;
            if (outputFormat == OutputFormatEnum.PDF) {
                file = File.createTempFile("tempAgedBalance", ".csv");
            } else if (outputFormat == OutputFormatEnum.CSV) {
                StringBuilder sb = new StringBuilder(getFilename(date));
                sb.append(".csv");
                file = new File(sb.toString());
            }
            writer = new FileWriter(file);
            int endMonth = calculateEndMonth(date);
            writer.append("Type;Dont non echue;Moins de 3 mois;De 3 a 6 mois;De 6 mois a 1 an;De 1 an a 2 ans;De 2 ans a 3 ans;Plus de 3 ans;Total");
            writer.append('\n');
            writer.append("DEBIT;");
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth, null, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 3, endMonth, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 6, endMonth - 3, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 12, endMonth - 6, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 24, endMonth - 12, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 36, endMonth - 24, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(null, endMonth - 36, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.totalAmount(DEBIT).toString()).replace('.', ','));
            writer.append('\n');
            writer.append("NB DEBIT;");
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth, null, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 3, endMonth, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 6, endMonth - 3, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 12, endMonth - 6, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 24, endMonth - 12, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 36, endMonth - 24, DEBIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(null, endMonth - 36, DEBIT) + ";").replace('.', ','));
            writer.append(Double.toString(accountOperationService.totalCount(DEBIT)).replace('.', ','));
            writer.append('\n');

            writer.append('\n');
            writer.append("CREDIT;");
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth, null, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 3, endMonth, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 6, endMonth - 3, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 12, endMonth - 6, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 24, endMonth - 12, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(endMonth - 36, endMonth - 24, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.calculateRecordsBetweenDueMonth(null, endMonth - 36, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.totalAmount(CREDIT).toString()).replace('.', ','));
            writer.append('\n');
            writer.append("NB CREDIT;");
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth, null, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 3, endMonth, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 6, endMonth - 3, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 12, endMonth - 6, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 24, endMonth - 12, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(endMonth - 36, endMonth - 24, CREDIT) + ";").replace('.', ','));
            writer.append((accountOperationService.countRecordsBetweenDueMonth(null, endMonth - 36, CREDIT) + ";").replace('.', ','));
            writer.append(Double.toString(accountOperationService.totalCount(CREDIT)).replace('.', ','));
            writer.append('\n');

            writer.flush();
            if (outputFormat == OutputFormatEnum.PDF) {
                parameters.put("startDate", date);
                StringBuilder sb = new StringBuilder(getFilename(date));
                sb.append(".pdf");
                String jasperTemplatesFolder = paramBeanFactory.getInstance().getProperty("reports.jasperTemplatesFolder", "/opt/jboss/files/reports/JasperTemplates/");
                String templateFilename = jasperTemplatesFolder + "agedBalance.jasper";
                generatePDFfile(file, sb.toString(), templateFilename, parameters);
            }
        } catch (IOException e) {
            log.error("failed to generate aged balance file", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public int calculateEndMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int endYear = calendar.get(Calendar.YEAR);
        int monthInYear = calendar.get(Calendar.MONTH);
        return endYear * 12 + monthInYear;
    }

    public String getFilename(Date date) {
        ParamBean param = paramBeanFactory.getInstance();
        String reportsFolder = param.getProperty("reportsURL", "/opt/jboss/files/reports/");
        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        StringBuilder sb = new StringBuilder();
        sb.append(reportsFolder);
        sb.append(appProvider.getCode());
        sb.append("_BALANCE_AGEE_");
        sb.append(sdf.format(date).toString());
        return sb.toString();
    }

    public void export(Report report) {
        ParamBean param = paramBeanFactory.getInstance();
        String reportsFolder = param.getProperty("reportsURL", "/opt/jboss/files/reports/");
        String jasperTemplatesFolder = param.getProperty("reports.jasperTemplatesFolder", "/opt/jboss/files/reports/JasperTemplates/");
        String templateFilename = jasperTemplatesFolder + "agedBalance.jasper";
        generateAgedBalanceFile(report.getSchedule(), report.getOutputFormat());
    }
}